package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import com.rork.rockscout.data.MockDataSeeder.LocalNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * In-app + push notifications for RockScout Friends social events:
 * new posts, friend requests, messages, and trade-interest hits.
 *
 * Fully self-contained: all data stored in [LocalDataStore]. No Supabase.
 *
 * The [NotificationSummaryWorker] polls every 1 hour for engagement and
 * new-posts summary notifications and posts them via [NotificationHelper].
 * Instant push types are sent immediately at creation time.
 */
class NotificationRepository private constructor() {

    private var appContext: Context? = null

    /** Must be called once from Application.onCreate so instant push
     *  notifications can be fired for friend requests and messages. */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    @Serializable
    data class NotificationRow(
        val id: String,
        val user_id: String,
        val type: String,
        val actor_id: String? = null,
        val ref_id: String? = null,
        val body: String,
        val read_at: String? = null,
        val created_at: String,
        val deep_link_target: String? = null,
    )

    private val _notifications = MutableStateFlow<List<NotificationRow>>(emptyList())
    val notifications: StateFlow<List<NotificationRow>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    /** Pending incoming friend-request count (for the bell summary tile + counter).
     *  Cross-referenced with the actual friend-request table so the count
     *  drops to zero immediately when requests are accepted/denied/blocked. */
    private val _friendRequestCount = MutableStateFlow(0)
    val friendRequestCount: StateFlow<Int> = _friendRequestCount.asStateFlow()

    /** Recently deleted notifications available for undo. */
    private val _undoStack = MutableStateFlow<List<LocalNotification>>(emptyList())
    val undoStack: StateFlow<List<LocalNotification>> = _undoStack.asStateFlow()

    private fun currentUserId(): String? = AuthRepository.instance.currentUserId

    /** Load all notifications for the current user. Message-related notifications
     *  (friend requests, messages, message requests) are kept out of the in-app
     *  bell feed so they only surface in their dedicated screens.
     */
    suspend fun loadNotifications() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS)
            _notifications.value = rows
                .filter { it.user_id == me && it.type !in SOCIAL_REQUEST_TYPES }
                .map { NotificationRow(it.id, it.user_id, it.type, it.actor_id, it.ref_id, it.body, it.read_at, it.created_at, it.deep_link_target) }
                .let { all ->
                    // Group likes/comments/replies on the same post by the same actor
                    // into a single row so the feed doesn't flood.
                    groupNotifications(all)
                }
                .sortedByDescending { it.created_at }
            // Bell counter = regular unread notifications + pending friend requests.
            // Base friendReqCount solely on the actual friend-request table so
            // it stays accurate even after markAllRead() clears notification
            // read_at flags. Accepting/denying a request drops the count to zero
            // because the request is no longer pending.
            val friendReqCount = if (me != null) {
                LocalDataStore.getTable<MockDataSeeder.LocalFriendRequest>(LocalDataStore.KEY_FRIEND_REQUESTS)
                    .count { it.recipient_id == me && it.status == "pending" }
            } else 0
            _friendRequestCount.value = friendReqCount
            _unreadCount.value = _notifications.value.count { it.read_at == null } + friendReqCount
        }.onFailure { Log.w("NotificationRepository", "loadNotifications failed", it) }
    }

    /** Create a notification for [userId]. */
    suspend fun createNotification(
        userId: String,
        type: String,
        actorId: String?,
        refId: String?,
        body: String,
        deepLinkTarget: String? = null,
    ): Result<Unit> {
        return runCatching {
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS).toMutableList()

            // For engagement types (likes/comments/replies), try to merge
            // into an existing notification for the same post+type+user.
            if (type in ENGAGEMENT_TYPES && refId != null) {
                val existing = rows.indexOfFirst {
                    it.user_id == userId && it.type == type && it.ref_id == refId && it.read_at == null
                }
                if (existing >= 0) {
                    val existingRow = rows[existing]
                    val updatedBody = buildGroupedBody(type, existingRow.body, body)
                    rows[existing] = existingRow.copy(
                        body = updatedBody,
                        created_at = java.time.OffsetDateTime.now().toString(),
                    )
                    LocalDataStore.setTable(LocalDataStore.KEY_NOTIFICATIONS, rows)
                    loadNotifications()
                    return@runCatching Unit
                }
            }

            rows.add(LocalNotification(
                id = "notif-" + UUID.randomUUID(),
                user_id = userId,
                type = type,
                actor_id = actorId,
                ref_id = refId,
                body = body,
                created_at = java.time.OffsetDateTime.now().toString(),
                deep_link_target = deepLinkTarget,
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_NOTIFICATIONS, rows)
            // Fire an instant push notification for high-priority types.
            fireInstantNotificationIfNeeded(type, userId)
            Unit
        }.onFailure { Log.w("NotificationRepository", "createNotification failed", it) }
    }

    /** Mark a notification as read. */
    suspend fun markRead(notificationId: String) {
        runCatching {
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS).toMutableList()
            val idx = rows.indexOfFirst { it.id == notificationId }
            if (idx >= 0) {
                rows[idx] = rows[idx].copy(read_at = java.time.OffsetDateTime.now().toString())
                LocalDataStore.setTable(LocalDataStore.KEY_NOTIFICATIONS, rows)
            }
            loadNotifications()
        }.onFailure { Log.w("NotificationRepository", "markRead failed", it) }
    }

    /** Mark all unread notifications as read. */
    suspend fun markAllRead() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS).toMutableList()
            val now = java.time.OffsetDateTime.now().toString()
            for (i in rows.indices) {
                if (rows[i].user_id == me && rows[i].read_at == null) {
                    rows[i] = rows[i].copy(read_at = now)
                }
            }
            LocalDataStore.setTable(LocalDataStore.KEY_NOTIFICATIONS, rows)
            loadNotifications()
        }.onFailure { Log.w("NotificationRepository", "markAllRead failed", it) }
    }

    /** Mark all unread notifications from a specific actor as read.
     *  Used when viewing a friend's profile — only clears that user's
     *  new-post notifications, not everyone's. */
    suspend fun markReadFromActor(actorId: String) {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS).toMutableList()
            val now = java.time.OffsetDateTime.now().toString()
            for (i in rows.indices) {
                if (rows[i].user_id == me && rows[i].actor_id == actorId && rows[i].read_at == null) {
                    rows[i] = rows[i].copy(read_at = now)
                }
            }
            LocalDataStore.setTable(LocalDataStore.KEY_NOTIFICATIONS, rows)
            loadNotifications()
        }.onFailure { Log.w("NotificationRepository", "markReadFromActor failed", it) }
    }

    /** Fire an immediate local push notification for high-priority types
     *  (friend requests, messages, trade interest, marked-traded, location
     *  approved, image rejection, report/ban) so the user sees them instantly
     *  instead of waiting for the periodic worker. */
    private fun fireInstantNotificationIfNeeded(type: String, userId: String) {
        val context = appContext ?: return
        // Only notify if the recipient is the current signed-in user.
        val me = AuthRepository.instance.currentUserId
        if (me == null || me != userId) return
        when (type) {
            TYPE_FRIEND_REQUEST -> {
                if (!PersistenceManager.isNotifFriendRequestsEnabled()) return
                val count = _notifications.value.count {
                    it.type == TYPE_FRIEND_REQUEST && it.read_at == null
                } + 1
                NotificationHelper.showFriendRequestNotification(context, count)
            }
            TYPE_MESSAGE -> {
                if (!PersistenceManager.isNotifMessagesEnabled()) return
                val count = _notifications.value.count {
                    it.type == TYPE_MESSAGE && it.read_at == null
                } + 1
                NotificationHelper.showPrivateMessageNotification(context, count)
            }
            TYPE_TRADE_INTEREST -> {
                if (!PersistenceManager.isNotifTradeInterestEnabled()) return
                val notifs = _notifications.value.filter {
                    it.type == TYPE_TRADE_INTEREST && it.read_at == null
                }
                val latest = notifs.lastOrNull() ?: return
                NotificationHelper.showTradeActivityNotification(
                    context,
                    title = "New interest in your listing!",
                    message = latest.body,
                )
            }
            TYPE_MARKED_TRADED -> {
                if (!PersistenceManager.isNotifMarkedTradedEnabled()) return
                val notifs = _notifications.value.filter {
                    it.type == TYPE_MARKED_TRADED && it.read_at == null
                }
                val latest = notifs.lastOrNull() ?: return
                NotificationHelper.showTradeActivityNotification(
                    context,
                    title = "Trade completed!",
                    message = latest.body,
                )
            }
            TYPE_LOCATION_APPROVED -> {
                if (!PersistenceManager.isNotifLocationApprovedEnabled()) return
                val notifs = _notifications.value.filter {
                    it.type == TYPE_LOCATION_APPROVED && it.read_at == null
                }
                val latest = notifs.lastOrNull() ?: return
                NotificationHelper.showLocationApprovedNotification(
                    context,
                    title = "Location approved!",
                    message = latest.body,
                )
            }
            TYPE_IMAGE_REJECTED -> {
                // No toggle — always on, instant.
                val notifs = _notifications.value.filter {
                    it.type == TYPE_IMAGE_REJECTED && it.read_at == null
                }
                val latest = notifs.lastOrNull() ?: return
                NotificationHelper.showModerationNotification(
                    context,
                    title = "Image rejected",
                    message = latest.body,
                )
            }
            TYPE_REPORT_BAN -> {
                // No toggle — always on, instant.
                val notifs = _notifications.value.filter {
                    it.type == TYPE_REPORT_BAN && it.read_at == null
                }
                val latest = notifs.lastOrNull() ?: return
                NotificationHelper.showModerationNotification(
                    context,
                    title = "Account alert",
                    message = latest.body,
                )
            }
            else -> { /* engagement + new posts — handled by periodic worker */ }
        }
    }

    /** Delete notifications by their IDs. Used by Delete Selected. */
    suspend fun deleteNotifications(ids: Set<String>) {
        if (ids.isEmpty()) return
        runCatching {
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS)
            LocalDataStore.setTable(
                LocalDataStore.KEY_NOTIFICATIONS,
                rows.filterNot { it.id in ids },
            )
            loadNotifications()
        }.onFailure { Log.w("NotificationRepository", "deleteNotifications failed", it) }
    }

    /** Delete a single notification and push it onto the undo stack.
     *  Call [undoDelete] within 5 seconds to restore it. */
    suspend fun swipeDelete(notificationId: String) {
        runCatching {
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS)
            val deleted = rows.firstOrNull { it.id == notificationId }
            if (deleted != null) {
                LocalDataStore.setTable(
                    LocalDataStore.KEY_NOTIFICATIONS,
                    rows.filterNot { it.id == notificationId },
                )
                _undoStack.value = _undoStack.value + deleted
            }
            loadNotifications()
        }.onFailure { Log.w("NotificationRepository", "swipeDelete failed", it) }
    }

    /** Restore the most recently swipe-deleted notification. */
    suspend fun undoDelete() {
        val stack = _undoStack.value
        if (stack.isEmpty()) return
        val toRestore = stack.last()
        runCatching {
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS).toMutableList()
            if (rows.none { it.id == toRestore.id }) {
                rows.add(toRestore)
                LocalDataStore.setTable(LocalDataStore.KEY_NOTIFICATIONS, rows)
            }
            _undoStack.value = stack.dropLast(1)
            loadNotifications()
        }.onFailure { Log.w("NotificationRepository", "undoDelete failed", it) }
    }

    /** Clear the undo stack (called after the undo pill auto-dismisses). */
    fun clearUndoStack() {
        _undoStack.value = emptyList()
    }

    /** Auto-delete notifications older than 30 days. */
    suspend fun autoDeleteOldNotifications() {
        runCatching {
            val cutoff = java.time.OffsetDateTime.now()
                .minusDays(30)
                .toString()
            val rows = LocalDataStore.getTable<LocalNotification>(LocalDataStore.KEY_NOTIFICATIONS)
            val filtered = rows.filter { it.created_at >= cutoff }
            if (filtered.size != rows.size) {
                LocalDataStore.setTable(LocalDataStore.KEY_NOTIFICATIONS, filtered)
                Log.d("NotificationRepository", "Auto-deleted ${rows.size - filtered.size} old notifications")
            }
        }.onFailure { Log.w("NotificationRepository", "autoDeleteOldNotifications failed", it) }
    }

    /** Group engagement notifications (likes/comments/replies) on the same
     *  post into a single feed row so the feed doesn't flood. */
    private fun groupNotifications(rows: List<NotificationRow>): List<NotificationRow> {
        val result = mutableListOf<NotificationRow>()
        val groupedIds = mutableSetOf<String>()

        for (row in rows) {
            if (row.id in groupedIds) continue
            if (row.type in ENGAGEMENT_TYPES && row.ref_id != null) {
                val sameGroup = rows.filter {
                    it.id !in groupedIds &&
                    it.type == row.type &&
                    it.ref_id == row.ref_id &&
                    it.user_id == row.user_id
                }
                if (sameGroup.size > 1) {
                    // Merge into one row with updated body.
                    val first = sameGroup.first()
                    val rest = sameGroup.drop(1)
                    val baseName = extractActorName(first.body)
                    val restCount = rest.size
                    val action = when (row.type) {
                        TYPE_POST_LIKE -> "liked"
                        TYPE_POST_COMMENT -> "commented on"
                        TYPE_POST_REPLY -> "replied to"
                        else -> "engaged with"
                    }
                    val mergedBody = if (restCount == 1) {
                        "$baseName and 1 other $action your post"
                    } else {
                        "$baseName and $restCount others $action your post"
                    }
                    val latest = sameGroup.maxByOrNull { it.created_at } ?: first
                    result.add(latest.copy(body = mergedBody))
                    sameGroup.forEach { groupedIds.add(it.id) }
                } else {
                    result.add(row)
                    groupedIds.add(row.id)
                }
            } else {
                result.add(row)
                groupedIds.add(row.id)
            }
        }
        return result
    }

    /** Extract the actor name from a notification body like "Sarah liked your post!" */
    private fun extractActorName(body: String): String {
        val verbs = listOf(" liked your post", " commented on your post", " replied to your post")
        for (v in verbs) {
            val idx = body.indexOf(v)
            if (idx > 0) return body.substring(0, idx).trim()
        }
        return body.substringBefore(" ").trim().ifEmpty { body }
    }

    /** Build a grouped body message when merging engagement notifications. */
    private fun buildGroupedBody(type: String, existingBody: String, newBody: String): String {
        val action = when (type) {
            TYPE_POST_LIKE -> "liked"
            TYPE_POST_COMMENT -> "commented on"
            TYPE_POST_REPLY -> "replied to"
            else -> "engaged with"
        }
        // Check if existing body is already a grouped message.
        if (existingBody.contains(" and ") && existingBody.contains(" others ")) {
            // Increment the count.
            val regex = Regex("and (\\d+) others")
            val match = regex.find(existingBody)
            if (match != null) {
                val count = match.groupValues[1].toIntOrNull() ?: 0
                return existingBody.replace(regex, "and ${count + 1} others")
            }
        }
        val existingName = extractActorName(existingBody)
        val newName = extractActorName(newBody)
        return if (existingName == newName) {
            newBody // same person, just update
        } else {
            "$existingName and 1 other $action your post"
        }
    }

    companion object {
        val instance: NotificationRepository by lazy { NotificationRepository() }

        const val TYPE_NEW_POST = "new_post"
        const val TYPE_FRIEND_REQUEST = "friend_request"
        const val TYPE_MESSAGE = "message"
        const val TYPE_TRADE_INTEREST = "trade_interest"
        const val TYPE_POST_LIKE = "post_like"
        const val TYPE_POST_COMMENT = "post_comment"
        const val TYPE_POST_REPLY = "post_reply"
        const val TYPE_MARKED_TRADED = "marked_traded"
        const val TYPE_LOCATION_APPROVED = "location_approved"
        const val TYPE_IMAGE_REJECTED = "image_rejected"
        const val TYPE_REPORT_BAN = "report_ban"

        val ENGAGEMENT_TYPES = setOf(TYPE_POST_LIKE, TYPE_POST_COMMENT, TYPE_POST_REPLY)
        val SOCIAL_REQUEST_TYPES = setOf(TYPE_FRIEND_REQUEST, TYPE_MESSAGE)
    }
}
