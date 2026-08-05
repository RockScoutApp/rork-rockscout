package com.rork.rockscout.data

import android.util.Log
import com.rork.rockscout.data.ProfanityFilter
import com.rork.rockscout.data.MockDataSeeder.LocalConnection
import com.rork.rockscout.data.MockDataSeeder.LocalThread
import com.rork.rockscout.data.MockDataSeeder.LocalMessage
import com.rork.rockscout.data.MockDataSeeder.LocalPing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Client-side RockScout Friends social layer — scan, connections, message
 * requests, threads, messages, pings.
 *
 * Fully self-contained: all data is stored in [LocalDataStore] (SharedPreferences).
 * No Supabase backend required. Mock users seeded by [MockDataSeeder] provide
 * the "other hunters" the signed-in user can discover and interact with.
 *
 * Coarse distance is computed client-side from the other user's coarse_lat /
 * coarse_lng and the caller's current GPS position.
 */
class SocialRepository private constructor() {

    // ---- Nearby hunters (scan results) -------------------------------------
    @Serializable
    data class HunterProfile(
        val id: String,
        val display_name: String,
        val avatar_emoji: String,
        val status: String,
        val level: Int = 1,
        val is_premium: Boolean = false,
        val premium_badge: Boolean = false,
        val coarse_lat: Double? = null,
        val coarse_lng: Double? = null,
        val collection_count: Int = 0,
        val wishlist_count: Int = 0,
        val favorite_spots_count: Int = 0,
        val last_location_update: Long = 0L,
        val gender: String = "rather_not_say",
        val birthday_millis: Long? = null,
        val birthday_public: Boolean = false,
        val favorite_rock: String = "",
        val highlight_color: String? = null,
    )

    /** Result of a scan: the hunter + coarse distance bucket (mi). */
    data class ScanResult(
        val hunter: HunterProfile,
        val distanceMiles: Double,
    )

    private val _scanResults = MutableStateFlow<List<ScanResult>>(emptyList())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanError = MutableStateFlow<String?>(null)
    val scanError: StateFlow<String?> = _scanError.asStateFlow()

/** Run a scan for nearby hunters within [radiusMiles].
     *  Returns all users who have location monitoring ON and are searchable
     *  (club_enabled), within the caller's radius. Users are automatically
     *  included in each other's search radius when their toggles are ON —
     *  no prior connection required.
     *
     *  The scan populates a card list of RockScouts within range.
     *  It does NOT show anyone on the ping map (pings are private).
     *  Returns the results list (also pushed into [scanResults]). */
    suspend fun scan(
        myLat: Double,
        myLng: Double,
        radiusMiles: Int,
    ): Result<List<ScanResult>> {
        _isScanning.value = true
        _scanError.value = null
        return runCatching {
            val me = currentUserId() ?: error("Not signed in")
            val blocks = FriendRepository.instance
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val results = users.mapNotNull { u ->
                if (u.id == me) return@mapNotNull null
                if (!u.club_enabled) return@mapNotNull null
                if (u.status == "off" || u.status == "off-grid" || u.status == "invisible") return@mapNotNull null
                if (blocks.isBlocked(u.id)) return@mapNotNull null
                val lat = u.coarse_lat ?: return@mapNotNull null
                val lng = u.coarse_lng ?: return@mapNotNull null
                val dist = haversineMiles(myLat, myLng, lat, lng)
                if (dist <= radiusMiles) ScanResult(u.toHunterProfile(), dist) else null
            }.sortedBy { it.distanceMiles }
            _scanResults.value = results
            results
        }.onFailure {
            _scanError.value = it.message ?: "Scan failed"
            Log.e("SocialRepository", "scan failed", it)
        }.also {
            _isScanning.value = false
        }
    }

    /** Coarse distance bucket label for a scan result, sized to the active radius. */
    fun distanceBucket(miles: Double, radius: Int): String = when {
        miles <= radius * 0.1 -> "within ${radius * 0.1} mi"
        miles <= radius * 0.5 -> "within ${radius * 0.5} mi"
        else -> "within $radius mi"
    }.replace(".0", "")

    /** Format distance as a rounded-mile string with compass direction.
     *  e.g. "about 2 miles south of you", "about 0 miles from you". */
    fun distanceDirectionText(miles: Double, myLat: Double, myLng: Double, theirLat: Double, theirLng: Double): String {
        val rounded = miles.roundToInt().coerceAtLeast(0)
        val dir = compassDirection(myLat, myLng, theirLat, theirLng)
        return if (dir == null) {
            "about $rounded ${if (rounded == 1) "mile" else "miles"} from you"
        } else {
            "about $rounded ${if (rounded == 1) "mile" else "miles"} $dir of you"
        }
    }

    /** 8-point compass direction from (myLat,myLng) → (theirLat,theirLng), or null if ~same spot. */
    private fun compassDirection(myLat: Double, myLng: Double, theirLat: Double, theirLng: Double): String? {
        val dLat = theirLat - myLat
        val dLng = theirLng - myLng
        if (kotlin.math.abs(dLat) < 0.0001 && kotlin.math.abs(dLng) < 0.0001) return null
        val angle = Math.toDegrees(atan2(dLng, dLat))
        val normalized = ((angle + 360.0) % 360.0)
        val dirs = arrayOf("north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest")
        val idx = ((normalized + 22.5) / 45.0).toInt() % 8
        return dirs[idx]
    }

    // ---- My profile sync ---------------------------------------------------
    /** Upsert the signed-in user's profile row in the local users table. */
    suspend fun syncMyProfile(
        profile: UserProfile,
        level: Int,
        totalXp: Int,
        isPremium: Boolean,
        coarseLat: Double? = null,
        coarseLng: Double? = null,
    ) {
        val me = currentUserId() ?: return
        runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS).toMutableList()
            val idx = users.indexOfFirst { it.id == me }
            val updated = LocalUser(
                id = me,
                email = if (idx >= 0) users[idx].email else "",
                password = if (idx >= 0) users[idx].password else "",
                display_name = profile.name,
                avatar_emoji = profile.avatarEmoji,
                status = profile.hunterStatus.name.lowercase().replace("_", "-"),
                level = level,
                xp = totalXp,
                is_premium = isPremium,
                premium_badge = isPremium,
                coarse_lat = coarseLat ?: users.getOrNull(idx)?.coarse_lat,
                coarse_lng = coarseLng ?: users.getOrNull(idx)?.coarse_lng,
                bio = profile.bio,
                home_region = profile.homeRegion,
                club_enabled = profile.clubEnabled,
                scan_radius_miles = profile.scanRadiusMiles,
                last_location_update = users.getOrNull(idx)?.last_location_update ?: 0L,
                gender = profile.gender,
                birthday_millis = profile.birthdayMillis,
                birthday_public = profile.birthdayPublic,
                favorite_rock = profile.favoriteRock,
            )
            if (idx >= 0) users[idx] = updated else users.add(updated)
            LocalDataStore.setTable(LocalDataStore.KEY_USERS, users)
        }.onFailure { Log.w("SocialRepository", "syncMyProfile failed", it) }
    }

    /** Update just the status field. */
    suspend fun updateStatus(status: HunterStatus) {
        val me = currentUserId() ?: return
        runCatching {
            updateMyUser { it.copy(status = status.name.lowercase().replace("_", "-")) }
        }.onFailure { Log.w("SocialRepository", "updateStatus failed", it) }
    }

    /** Update just the club_enabled flag. */
    suspend fun updateClubEnabled(enabled: Boolean) {
        val me = currentUserId() ?: return
        runCatching {
            updateMyUser { it.copy(club_enabled = enabled) }
        }.onFailure { Log.w("SocialRepository", "updateClubEnabled failed", it) }
    }

    /** Update the coarse scan location (snap to ~1 mile grid before storing).
     *  Also stamps [last_location_update] so the Most Recent sort works. */
    suspend fun updateCoarseLocation(lat: Double, lng: Double) {
        val me = currentUserId() ?: return
        val snappedLat = (lat * 100.0).roundToInt() / 100.0
        val snappedLng = (lng * 100.0).roundToInt() / 100.0
        runCatching {
            updateMyUser {
                it.copy(
                    coarse_lat = snappedLat,
                    coarse_lng = snappedLng,
                    last_location_update = System.currentTimeMillis(),
                )
            }
        }.onFailure { Log.w("SocialRepository", "updateCoarseLocation failed", it) }
    }

    /** Helper: update the current user's row in the users table. */
    private fun updateMyUser(transform: (LocalUser) -> LocalUser) {
        val me = currentUserId() ?: return
        val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS).toMutableList()
        val idx = users.indexOfFirst { it.id == me }
        if (idx >= 0) {
            users[idx] = transform(users[idx])
            LocalDataStore.setTable(LocalDataStore.KEY_USERS, users)
        }
    }

    // ---- Connections -------------------------------------------------------
    private val _connections = MutableStateFlow<List<String>>(emptyList())
    val connections: StateFlow<List<String>> = _connections.asStateFlow()

    /** Load the user ids I'm connected with. */
    suspend fun loadConnections() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalConnection>(LocalDataStore.KEY_CONNECTIONS)
            _connections.value = rows
                .filter { it.user_a == me || it.user_b == me }
                .map { if (it.user_a == me) it.user_b else it.user_a }
        }.onFailure { Log.w("SocialRepository", "loadConnections failed", it) }
    }

    /** Are [me] and [otherId] connected? */
    suspend fun isConnected(otherId: String): Boolean {
        return _connections.value.contains(otherId)
    }

    /** Create a reciprocal connection between [me] and [otherId]. Idempotent. */
    suspend fun createConnection(otherId: String) {
        val me = currentUserId() ?: return
        runCatching {
            if (me == otherId) return@runCatching
            if (_connections.value.contains(otherId)) return@runCatching
            val rows = LocalDataStore.getTable<LocalConnection>(LocalDataStore.KEY_CONNECTIONS).toMutableList()
            val (a, b) = if (me < otherId) me to otherId else otherId to me
            if (rows.none { (it.user_a == a && it.user_b == b) }) {
                rows.add(LocalConnection(
                    id = "conn-" + UUID.randomUUID(),
                    user_a = a,
                    user_b = b,
                    created_at = java.time.OffsetDateTime.now().toString(),
                ))
                LocalDataStore.setTable(LocalDataStore.KEY_CONNECTIONS, rows)
            }
            _connections.value = _connections.value + otherId
        }.onFailure { Log.w("SocialRepository", "createConnection failed", it) }
    }

    /**
     * Create a RockScout Friends connection between the current user and the
     * referrer identified by [referrerEmail]. If no local user with that email
     * exists yet, a placeholder user row is created so the connection can be
     * stored. Used when a new user signs up with a referral code.
     */
    suspend fun createConnectionFromReferral(referrerEmail: String) {
        val me = currentUserId() ?: return
        runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS).toMutableList()
            val existing = users.firstOrNull { it.email.equals(referrerEmail, ignoreCase = true) }
            val referrerId = existing?.id ?: run {
                val placeholder = LocalUser(
                    id = "ref-" + java.util.UUID.randomUUID().toString(),
                    email = referrerEmail,
                    password = "",
                    display_name = referrerEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                    avatar_emoji = "\uD83E\uDD1D",
                    status = "off",
                    club_enabled = true,
                )
                users.add(placeholder)
                LocalDataStore.setTable(LocalDataStore.KEY_USERS, users)
                placeholder.id
            }
            createConnection(referrerId)
        }.onFailure { Log.w("SocialRepository", "createConnectionFromReferral failed", it) }
    }

    /** Remove a connection (reciprocal — deletes the single row).
     *  Does not hide the user from nearby scans; only removes any shared
     *  pings that came from this user so they no longer appear on the map. */
    suspend fun removeConnection(otherId: String) {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalConnection>(LocalDataStore.KEY_CONNECTIONS)
            val filtered = rows.filterNot {
                (it.user_a == me && it.user_b == otherId) ||
                (it.user_a == otherId && it.user_b == me)
            }
            LocalDataStore.setTable(LocalDataStore.KEY_CONNECTIONS, filtered)
            _connections.value = _connections.value - otherId
            removeSharedPingsFromSender(otherId)
        }.onFailure { Log.w("SocialRepository", "removeConnection failed", it) }
    }

    // ---- Message requests --------------------------------------------------
    @Serializable
    data class MessageRequestRow(
        val id: String,
        val sender_id: String,
        val recipient_id: String,
        val body: String,
        val status: String,
        val created_at: String,
        val responded_at: String? = null,
    )

    private val _incomingRequests = MutableStateFlow<List<MessageRequestRow>>(emptyList())
    val incomingRequests: StateFlow<List<MessageRequestRow>> = _incomingRequests.asStateFlow()

    private val _outgoingRequests = MutableStateFlow<List<MessageRequestRow>>(emptyList())
    val outgoingRequests: StateFlow<List<MessageRequestRow>> = _outgoingRequests.asStateFlow()

    suspend fun loadRequests() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<MessageRequestRow>(LocalDataStore.KEY_MESSAGE_REQUESTS)
            _incomingRequests.value = rows.filter { it.recipient_id == me && it.status == "pending" }
            _outgoingRequests.value = rows.filter { it.sender_id == me }
            updateMessageCounts()
        }.onFailure { Log.w("SocialRepository", "loadRequests failed", it) }
    }

    /** Send a message request to [recipientId]. Returns ok / error message.
     *  Blocked users cannot send requests — returns failure immediately. */
    suspend fun sendRequest(recipientId: String, body: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        val blocks = FriendRepository.instance
        if (blocks.isBlocked(recipientId)) {
            return Result.failure(IllegalStateException("You are blocked from messaging this user."))
        }
        val filtered = ProfanityFilter.filter(body)
        return runCatching {
            if (isConnected(recipientId)) return@runCatching
            val rows = LocalDataStore.getTable<MessageRequestRow>(LocalDataStore.KEY_MESSAGE_REQUESTS).toMutableList()
            if (rows.any { it.sender_id == me && it.recipient_id == recipientId && it.status == "pending" }) {
                return@runCatching
            }
            rows.add(MessageRequestRow(
                id = "mr-" + UUID.randomUUID(),
                sender_id = me,
                recipient_id = recipientId,
                body = filtered,
                status = "pending",
                created_at = java.time.OffsetDateTime.now().toString(),
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_MESSAGE_REQUESTS, rows)
            // Notify the recipient they have a message request.
            NotificationRepository.instance.createNotification(
                userId = recipientId,
                type = NotificationRepository.TYPE_MESSAGE,
                actorId = me,
                refId = null,
                body = "You have a new message request.",
            )
            loadRequests()
            updateMessageCounts()
        }.onFailure { Log.w("SocialRepository", "sendRequest failed", it) }
    }

    /** Accept a request: mark accepted and create the message thread only. */
    suspend fun acceptRequest(request: MessageRequestRow): Result<Unit> {
        return runCatching {
            updateRequestStatus(request.id, "accepted")
            ensureThread(request.sender_id)
            loadRequests()
            updateMessageCounts()
            Unit
        }.onFailure { Log.w("SocialRepository", "acceptRequest failed", it) }
    }

    /** Deny a request — just mark denied. */
    suspend fun denyRequest(request: MessageRequestRow): Result<Unit> {
        return runCatching {
            updateRequestStatus(request.id, "denied")
            loadRequests()
            updateMessageCounts()
            Unit
        }.onFailure { Log.w("SocialRepository", "denyRequest failed", it) }
    }

    /** Deny and block: mark blocked + write a block row. */
    suspend fun denyAndBlockRequest(request: MessageRequestRow): Result<Unit> {
        return runCatching {
            updateRequestStatus(request.id, "blocked")
            FriendRepository.instance.blockUser(request.sender_id)
            loadRequests()
            updateMessageCounts()
            Unit
        }.onFailure { Log.w("SocialRepository", "denyAndBlockRequest failed", it) }
    }

    private fun updateRequestStatus(requestId: String, status: String) {
        val rows = LocalDataStore.getTable<MessageRequestRow>(LocalDataStore.KEY_MESSAGE_REQUESTS).toMutableList()
        val idx = rows.indexOfFirst { it.id == requestId }
        if (idx >= 0) {
            rows[idx] = rows[idx].copy(status = status, responded_at = java.time.OffsetDateTime.now().toString())
            LocalDataStore.setTable(LocalDataStore.KEY_MESSAGE_REQUESTS, rows)
        }
    }

    // ---- Threads + messages ------------------------------------------------
    @Serializable
    data class ThreadRow(
        val id: String,
        val user_a: String,
        val user_b: String,
        val last_message_at: String,
        val created_at: String,
    )

    @Serializable
    data class MessageRow(
        val id: String,
        val thread_id: String,
        val sender_id: String,
        val body: String,
        val image_uri: String? = null,
        val read_at: String? = null,
        val created_at: String,
        val reply_to_message_id: String? = null,
        val tagged_user_ids: List<String> = emptyList(),
    )

    private val _threads = MutableStateFlow<List<ThreadRow>>(emptyList())
    val threads: StateFlow<List<ThreadRow>> = _threads.asStateFlow()

    private val _messages = MutableStateFlow<List<MessageRow>>(emptyList())
    val messages: StateFlow<List<MessageRow>> = _messages.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _unreadMessageCount = MutableStateFlow(0)
    /** Unread messages in existing threads (not message requests). */
    val unreadMessageCount: StateFlow<Int> = _unreadMessageCount.asStateFlow()

    private val _unreadMessageRequestCount = MutableStateFlow(0)
    /** Pending incoming message requests. */
    val unreadMessageRequestCount: StateFlow<Int> = _unreadMessageRequestCount.asStateFlow()

    private val _totalMessagingCount = MutableStateFlow(0)
    /** Combined unread messages + pending message requests. */
    val totalMessagingCount: StateFlow<Int> = _totalMessagingCount.asStateFlow()

    suspend fun loadThreads() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalThread>(LocalDataStore.KEY_THREADS)
            _threads.value = rows
                .filter { it.user_a == me || it.user_b == me }
                .map { ThreadRow(it.id, it.user_a, it.user_b, it.last_message_at, it.created_at) }
                .sortedByDescending { it.last_message_at }
            updateMessageCounts()
        }.onFailure { Log.w("SocialRepository", "loadThreads failed", it) }
    }

    /** Find or create the thread between [me] and [otherId]. Returns thread id. */
    suspend fun ensureThread(otherId: String): String? {
        val me = currentUserId() ?: return null
        val existing = _threads.value.firstOrNull {
            (it.user_a == me && it.user_b == otherId) ||
            (it.user_a == otherId && it.user_b == me)
        }
        if (existing != null) return existing.id
        return runCatching {
            val rows = LocalDataStore.getTable<LocalThread>(LocalDataStore.KEY_THREADS).toMutableList()
            val (a, b) = if (me < otherId) me to otherId else otherId to me
            val now = java.time.OffsetDateTime.now().toString()
            val thread = LocalThread(
                id = "thread-" + UUID.randomUUID(),
                user_a = a,
                user_b = b,
                last_message_at = now,
                created_at = now,
            )
            rows.add(thread)
            LocalDataStore.setTable(LocalDataStore.KEY_THREADS, rows)
            loadThreads()
            thread.id
        }.onFailure { Log.w("SocialRepository", "ensureThread failed", it) }.getOrNull()
    }

    /** Load messages for [threadId]. */
    suspend fun loadMessages(threadId: String) {
        runCatching {
            val rows = LocalDataStore.getTable<LocalMessage>(LocalDataStore.KEY_MESSAGES)
            _messages.value = rows
                .filter { it.thread_id == threadId }
                .sortedBy { it.created_at }
                .map { MessageRow(it.id, it.thread_id, it.sender_id, it.body, it.image_uri, it.read_at, it.created_at, it.reply_to_message_id, it.tagged_user_ids) }
            updateMessageCounts()
        }.onFailure { Log.w("SocialRepository", "loadMessages failed", it) }
    }

    /** Get the most recent message in a thread, or null if empty. */
    fun getLastMessageForThread(threadId: String): MessageRow? {
        val me = currentUserId() ?: return null
        return LocalDataStore.getTable<LocalMessage>(LocalDataStore.KEY_MESSAGES)
            .filter { it.thread_id == threadId }
            .sortedByDescending { it.created_at }
            .firstOrNull()
            ?.let { MessageRow(it.id, it.thread_id, it.sender_id, it.body, it.image_uri, it.read_at, it.created_at, it.reply_to_message_id, it.tagged_user_ids) }
    }

    /** Count unread messages in a thread for the current user. */
    fun unreadCountForThread(threadId: String): Int {
        val me = currentUserId() ?: return 0
        return LocalDataStore.getTable<LocalMessage>(LocalDataStore.KEY_MESSAGES)
            .count { it.thread_id == threadId && it.sender_id != me && it.read_at == null }
    }

    /** Send a message in [threadId]. Also creates a notification for the
     *  other participant so they know there's a new message. */
    suspend fun sendMessage(
        threadId: String,
        body: String,
        imageUri: String? = null,
        replyToMessageId: String? = null,
        taggedUserIds: List<String> = emptyList(),
    ): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            if (body.isBlank() && imageUri.isNullOrBlank()) return@runCatching
            val now = java.time.OffsetDateTime.now().toString()
            val rows = LocalDataStore.getTable<LocalMessage>(LocalDataStore.KEY_MESSAGES).toMutableList()
            rows.add(LocalMessage(
                id = "msg-" + UUID.randomUUID(),
                thread_id = threadId,
                sender_id = me,
                body = body.trim(),
                image_uri = imageUri,
                created_at = now,
                reply_to_message_id = replyToMessageId,
                tagged_user_ids = taggedUserIds,
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_MESSAGES, rows)
            // Bump thread last_message_at.
            val threads = LocalDataStore.getTable<LocalThread>(LocalDataStore.KEY_THREADS).toMutableList()
            val tIdx = threads.indexOfFirst { it.id == threadId }
            if (tIdx >= 0) {
                threads[tIdx] = threads[tIdx].copy(last_message_at = now)
                LocalDataStore.setTable(LocalDataStore.KEY_THREADS, threads)
            }
            // Notify the other participant.
            val thread = _threads.value.firstOrNull { it.id == threadId }
            if (thread != null) {
                val otherId = if (thread.user_a == me) thread.user_b else thread.user_a
                NotificationRepository.instance.createNotification(
                    userId = otherId,
                    type = NotificationRepository.TYPE_MESSAGE,
                    actorId = me,
                    refId = threadId,
                    body = "You have a new message.",
                )
            }
            loadMessages(threadId)
            loadThreads()
            updateMessageCounts()
        }.onFailure { Log.w("SocialRepository", "sendMessage failed", it) }
    }

    /** Mark all messages in [threadId] where I'm the receiver as read. */
    suspend fun markThreadRead(threadId: String) {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalMessage>(LocalDataStore.KEY_MESSAGES).toMutableList()
            val now = java.time.OffsetDateTime.now().toString()
            for (i in rows.indices) {
                if (rows[i].thread_id == threadId && rows[i].sender_id != me && rows[i].read_at == null) {
                    rows[i] = rows[i].copy(read_at = now)
                }
            }
            LocalDataStore.setTable(LocalDataStore.KEY_MESSAGES, rows)
            loadMessages(threadId)
            updateMessageCounts()
        }.onFailure { Log.w("SocialRepository", "markThreadRead failed", it) }
    }

    private fun updateMessageCounts() {
        val me = currentUserId() ?: return
        runCatching {
            // Only count messages from threads the user is actually part of.
            // This prevents orphaned message rows from inflating the counter.
            val myThreadIds = LocalDataStore.getTable<LocalThread>(LocalDataStore.KEY_THREADS)
                .filter { it.user_a == me || it.user_b == me }
                .map { it.id }
                .toSet()
            val unreadMessages = LocalDataStore.getTable<LocalMessage>(LocalDataStore.KEY_MESSAGES)
                .count { it.sender_id != me && it.read_at == null && it.thread_id in myThreadIds }
            val unreadRequests = LocalDataStore.getTable<MessageRequestRow>(LocalDataStore.KEY_MESSAGE_REQUESTS)
                .count { it.recipient_id == me && it.status == "pending" }
            _unreadMessageCount.value = unreadMessages
            _unreadMessageRequestCount.value = unreadRequests
            _totalMessagingCount.value = unreadMessages + unreadRequests
            updateUnreadCount()
        }.onFailure { Log.w("SocialRepository", "updateMessageCounts failed", it) }
    }

    private fun updateUnreadCount() {
        val me = currentUserId() ?: return
        runCatching {
            // Only count messages from threads the user is part of.
            val myThreadIds = LocalDataStore.getTable<LocalThread>(LocalDataStore.KEY_THREADS)
                .filter { it.user_a == me || it.user_b == me }
                .map { it.id }
                .toSet()
            val rows = LocalDataStore.getTable<LocalMessage>(LocalDataStore.KEY_MESSAGES)
            _unreadCount.value = rows.count { it.sender_id != me && it.read_at == null && it.thread_id in myThreadIds }
        }.onFailure { Log.w("SocialRepository", "updateUnreadCount failed", it) }
    }

    /** Delete a thread and all its messages. */
    suspend fun deleteThread(threadId: String) {
        runCatching {
            val threads = LocalDataStore.getTable<LocalThread>(LocalDataStore.KEY_THREADS)
                .filterNot { it.id == threadId }
            LocalDataStore.setTable(LocalDataStore.KEY_THREADS, threads)
            val msgs = LocalDataStore.getTable<LocalMessage>(LocalDataStore.KEY_MESSAGES)
                .filterNot { it.thread_id == threadId }
            LocalDataStore.setTable(LocalDataStore.KEY_MESSAGES, msgs)
            loadThreads()
            updateMessageCounts()
        }.onFailure { Log.w("SocialRepository", "deleteThread failed", it) }
    }

    /** The other user id in a thread (not me). */
    fun otherUserId(thread: ThreadRow): String {
        val me = currentUserId() ?: return thread.user_a
        return if (thread.user_a == me) thread.user_b else thread.user_a
    }

    // ---- Pings -------------------------------------------------------------
    @Serializable
    data class PingRow(
        val id: String,
        val user_id: String,
        val lat: Double,
        val lng: Double,
        val label: String,
        val expires_at: String,
        val created_at: String,
    )

    private val _pings = MutableStateFlow<List<PingRow>>(emptyList())
    val pings: StateFlow<List<PingRow>> = _pings.asStateFlow()

    // ---- Shared pings (received from others via Messenger) ---------------
    @Serializable
    data class SharedPingRow(
        val id: String,
        val lat: Double,
        val lng: Double,
        val label: String,
        val senderId: String?,
        val senderName: String,
        val receivedAt: String,
        val expiresAt: String,
    )

    private val _sharedPings = MutableStateFlow<List<SharedPingRow>>(emptyList())
    val sharedPings: StateFlow<List<SharedPingRow>> = _sharedPings.asStateFlow()

    /** Add a shared ping received via a rockscout://ping deep link.
     *  Deduplicates by lat+lng+senderId — tapping the same link twice
     *  doesn't create duplicate markers. Expires after 24 hours. */
    suspend fun addSharedPing(
        lat: Double,
        lng: Double,
        label: String,
        senderId: String?,
        senderName: String,
    ): Result<Unit> {
        return runCatching {
            val now = java.time.OffsetDateTime.now()
            val expires = now.plusHours(24)
            val rows = LocalDataStore.getTable<MockDataSeeder.LocalSharedPing>(LocalDataStore.KEY_SHARED_PINGS).toMutableList()
            // Deduplicate — skip if same coords + sender already exist and not expired.
            val exists = rows.any {
                it.lat == lat && it.lng == lng && it.sender_id == senderId &&
                runCatching { java.time.OffsetDateTime.parse(it.expires_at).isAfter(now) }.getOrDefault(false)
            }
            if (!exists) {
                rows.add(MockDataSeeder.LocalSharedPing(
                    id = "shared-ping-" + UUID.randomUUID(),
                    lat = lat,
                    lng = lng,
                    label = label,
                    sender_id = senderId,
                    sender_name = senderName,
                    received_at = now.toString(),
                    expires_at = expires.toString(),
                ))
                LocalDataStore.setTable(LocalDataStore.KEY_SHARED_PINGS, rows)
            }
            loadSharedPings()
            Unit
        }.onFailure { Log.w("SocialRepository", "addSharedPing failed", it) }
    }

    /** Load all non-expired shared pings received from others. */
    suspend fun loadSharedPings(): Result<List<SharedPingRow>> {
        return runCatching {
            val now = java.time.OffsetDateTime.now()
            val all = LocalDataStore.getTable<MockDataSeeder.LocalSharedPing>(LocalDataStore.KEY_SHARED_PINGS)
            // Prune expired entries.
            val active = all.filter {
                runCatching { java.time.OffsetDateTime.parse(it.expires_at).isAfter(now) }.getOrDefault(false)
            }
            if (active.size != all.size) {
                LocalDataStore.setTable(LocalDataStore.KEY_SHARED_PINGS, active.map {
                    MockDataSeeder.LocalSharedPing(it.id, it.lat, it.lng, it.label, it.sender_id, it.sender_name, it.received_at, it.expires_at)
                })
            }
            _sharedPings.value = active.map {
                SharedPingRow(it.id, it.lat, it.lng, it.label, it.sender_id, it.sender_name, it.received_at, it.expires_at)
            }
            _sharedPings.value
        }.onFailure { Log.w("SocialRepository", "loadSharedPings failed", it) }
    }

    /** Remove a specific shared ping by id. */
    suspend fun removeSharedPing(id: String) {
        runCatching {
            val rows = LocalDataStore.getTable<MockDataSeeder.LocalSharedPing>(LocalDataStore.KEY_SHARED_PINGS)
                .filterNot { it.id == id }
            LocalDataStore.setTable(LocalDataStore.KEY_SHARED_PINGS, rows)
            loadSharedPings()
        }.onFailure { Log.w("SocialRepository", "removeSharedPing failed", it) }
    }

    /** Remove all shared pings received from a specific user. Used when a
     *  RockScout Friends connection is removed so the ex-connection's pings
     *  disappear from the current user's map. */
    suspend fun removeSharedPingsFromSender(senderId: String) {
        runCatching {
            val rows = LocalDataStore.getTable<MockDataSeeder.LocalSharedPing>(LocalDataStore.KEY_SHARED_PINGS)
                .filterNot { it.sender_id == senderId }
            LocalDataStore.setTable(LocalDataStore.KEY_SHARED_PINGS, rows)
            loadSharedPings()
        }.onFailure { Log.w("SocialRepository", "removeSharedPingsFromSender failed", it) }
    }

    /** Drop a new ping at [lat]/[lng]. Replaces any existing live ping of mine. */
    suspend fun setPing(lat: Double, lng: Double, label: String, ttlHours: Int = 12): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val rows = LocalDataStore.getTable<LocalPing>(LocalDataStore.KEY_PINGS)
                .filterNot { it.user_id == me }.toMutableList()
            val now = java.time.OffsetDateTime.now()
            rows.add(LocalPing(
                id = "ping-" + UUID.randomUUID(),
                user_id = me,
                lat = lat,
                lng = lng,
                label = ProfanityFilter.filter(label),
                expires_at = now.plusHours(ttlHours.toLong()).toString(),
                created_at = now.toString(),
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_PINGS, rows)
            // Update the _pings StateFlow immediately so any observing UI
            // reflects the new ping without requiring a separate load call.
            _pings.value = rows.filter {
                it.user_id == me &&
                runCatching { java.time.OffsetDateTime.parse(it.expires_at).isAfter(java.time.OffsetDateTime.now()) }.getOrDefault(false)
            }.map { PingRow(it.id, it.user_id, it.lat, it.lng, it.label, it.expires_at, it.created_at) }
            Unit
        }.onFailure { Log.w("SocialRepository", "setPing failed", it) }
    }

    /** Clear my live ping. */
    suspend fun clearMyPing() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalPing>(LocalDataStore.KEY_PINGS)
                .filterNot { it.user_id == me }
            LocalDataStore.setTable(LocalDataStore.KEY_PINGS, rows)
            // Update _pings StateFlow immediately so the UI reflects the removal.
            _pings.value = rows.filter {
                it.user_id == me &&
                runCatching { java.time.OffsetDateTime.parse(it.expires_at).isAfter(java.time.OffsetDateTime.now()) }.getOrDefault(false)
            }.map { PingRow(it.id, it.user_id, it.lat, it.lng, it.label, it.expires_at, it.created_at) }
        }.onFailure { Log.w("SocialRepository", "clearMyPing failed", it) }
    }

    /**
     * Load the current user's own live pings only. Pings are private — they
     * never appear on anyone else's map. The user shares their ping location
     * manually through the Android share sheet (Messenger, SMS, etc.).
     *
     * The [myLat], [myLng], and [radiusMiles] parameters are accepted for
     * backward compatibility but no longer affect visibility.
     */
    suspend fun loadVisiblePings(
        myLat: Double? = null,
        myLng: Double? = null,
        radiusMiles: Int = 0,
    ): Result<List<PingRow>> {
        return runCatching {
            val me = currentUserId() ?: error("Not signed in")
            val now = java.time.OffsetDateTime.now()
            val all = LocalDataStore.getTable<LocalPing>(LocalDataStore.KEY_PINGS)
            val visible = all.filter {
                it.user_id == me &&
                runCatching { java.time.OffsetDateTime.parse(it.expires_at).isAfter(now) }.getOrDefault(false)
            }.map { PingRow(it.id, it.user_id, it.lat, it.lng, it.label, it.expires_at, it.created_at) }
            _pings.value = visible
            visible
        }.onFailure { Log.w("SocialRepository", "loadVisiblePings failed", it) }
    }

    /** Search all discoverable hunters by display name **or sign-in email**.
     *
     *  - Blank query: lists every discoverable hunter (club_enabled, not
     *    blocked, not self) — sorted alphabetically by display name.
     *  - Non-blank query that looks like an email (contains '@'): matches
     *    any user whose email contains the query (case-insensitive),
     *    bypassing the club_enabled gate so users can be found by email
     *    even if they haven't opted into discovery scans.
     *  - Otherwise: matches display_name (case-insensitive) among
     *    discoverable hunters.
     *
     *  Blocked users and the current user are always excluded. */
    suspend fun searchHunters(query: String): List<HunterProfile> {
        val me = currentUserId() ?: return emptyList()
        return runCatching {
            val blocks = FriendRepository.instance
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val isEmailSearch = query.trim().contains('@')
            users.asSequence()
                .filter { it.id != me }
                .filter { !blocks.isBlocked(it.id) }
                .filter { if (isEmailSearch) true else it.club_enabled }
                .filter {
                    query.isBlank() ||
                    it.display_name.contains(query, ignoreCase = true) ||
                    (isEmailSearch && it.email.contains(query, ignoreCase = true))
                }
                .sortedBy { it.display_name.lowercase() }
                .map { it.toHunterProfile() }
                .toList()
        }.onFailure { Log.w("SocialRepository", "searchHunters failed", it) }.getOrDefault(emptyList())
    }

    /** Check whether a display name is already taken by another user.
     *  Comparison is case-insensitive and ignores leading/trailing whitespace.
     *  Pass [excludeUserId] to skip the caller's own row (e.g. when editing
     *  an existing profile so the user can keep their current name). */
    fun isDisplayNameTaken(name: String, excludeUserId: String? = null): Boolean {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return false
        val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
        return users.any {
            it.id != excludeUserId &&
            it.display_name.trim().equals(trimmed, ignoreCase = true)
        }
    }

    /** Fetch a batch of hunter profiles by id. */
    suspend fun fetchProfiles(ids: List<String>): List<HunterProfile> {
        if (ids.isEmpty()) return emptyList()
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val map = users.associateBy { it.id }
            ids.mapNotNull { map[it]?.toHunterProfile() }
        }.onFailure { Log.w("SocialRepository", "fetchProfiles failed", it) }.getOrDefault(emptyList())
    }

    /** Fetch a single user's full profile (for viewing other user's profiles). */
    suspend fun fetchUserProfile(userId: String): LocalUser? {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            users.firstOrNull { it.id == userId }
        }.onFailure { Log.w("SocialRepository", "fetchUserProfile failed", it) }.getOrNull()
    }

    /** Fetch another user's collection specimen IDs. Empty if the user isn't found. */
    suspend fun fetchUserCollection(userId: String): List<String> {
        return runCatching {
            LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
                .firstOrNull { it.id == userId }
                ?.collection_ids
                ?: emptyList()
        }.onFailure { Log.w("SocialRepository", "fetchUserCollection failed", it) }.getOrDefault(emptyList())
    }

    /** Fetch another user's wishlist specimen IDs. Empty if the user isn't found. */
    suspend fun fetchUserWishlist(userId: String): List<String> {
        return runCatching {
            LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
                .firstOrNull { it.id == userId }
                ?.wishlist_ids
                ?: emptyList()
        }.onFailure { Log.w("SocialRepository", "fetchUserWishlist failed", it) }.getOrDefault(emptyList())
    }

    /** Fetch another user's favorite spot IDs. Empty if the user isn't found. */
    suspend fun fetchUserFavoriteSpots(userId: String): List<String> {
        return runCatching {
            LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
                .firstOrNull { it.id == userId }
                ?.favorite_spot_ids
                ?: emptyList()
        }.onFailure { Log.w("SocialRepository", "fetchUserFavoriteSpots failed", it) }.getOrDefault(emptyList())
    }

    /** Copy the signed-in user's collection, wishlist, and favorite spots from
     *  [AppRepository] into the local users table so other hunters can see them. */
    suspend fun syncMyPublicLists() {
        val me = currentUserId() ?: return
        val repo = AppRepository.instance
        val collection = repo.collection.value.map { it.specimenId }
        val wishlist = repo.wishlist.value
        val favoriteSpots = repo.favoriteSpots.value
        runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS).toMutableList()
            val idx = users.indexOfFirst { it.id == me }
            if (idx >= 0) {
                users[idx] = users[idx].copy(
                    collection_ids = collection,
                    wishlist_ids = wishlist,
                    favorite_spot_ids = favoriteSpots,
                )
                LocalDataStore.setTable(LocalDataStore.KEY_USERS, users)
            }
        }.onFailure { Log.w("SocialRepository", "syncMyPublicLists failed", it) }
    }

    // ---- Helpers -----------------------------------------------------------
    /** Current signed-in user id or null. */
    fun currentUserId(): String? = AuthRepository.instance.currentUserId

    /** Great-circle distance in miles between two lat/lng points. */
    private fun haversineMiles(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 3958.8
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).let { it * it } +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLng / 2).let { it * it }
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    // ---- List likes (liking other users' collection/wishlist entries) -------

    private val _listLikes = MutableStateFlow<List<MockDataSeeder.LocalListLike>>(emptyList())
    val listLikes: StateFlow<List<MockDataSeeder.LocalListLike>> = _listLikes.asStateFlow()
    private val _likedListEntries = MutableStateFlow<Set<String>>(emptySet())
    val likedListEntries: StateFlow<Set<String>> = _likedListEntries.asStateFlow()

    /** Load list likes into memory. Call on screen entry. */
    fun loadListLikes() {
        val me = currentUserId() ?: return
        val all = LocalDataStore.getTable<MockDataSeeder.LocalListLike>(LocalDataStore.KEY_LIST_LIKES)
        _listLikes.value = all
        _likedListEntries.value = all.filter { it.liker_id == me }
            .map { "${it.target_user_id}:${it.specimen_id}:${it.list_type}" }
            .toSet()
    }

    /** Toggle like on another user's collection or wishlist entry. */
    suspend fun toggleListLike(
        targetUserId: String,
        specimenId: String,
        listType: String,
    ): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val likes = LocalDataStore.getTable<MockDataSeeder.LocalListLike>(LocalDataStore.KEY_LIST_LIKES).toMutableList()
            val key = "$targetUserId:$specimenId:$listType"
            val existing = likes.indexOfFirst {
                it.target_user_id == targetUserId &&
                it.specimen_id == specimenId &&
                it.list_type == listType &&
                it.liker_id == me
            }
            if (existing >= 0) {
                likes.removeAt(existing)
                _likedListEntries.value = _likedListEntries.value - key
            } else {
                likes.add(MockDataSeeder.LocalListLike(
                    target_user_id = targetUserId,
                    specimen_id = specimenId,
                    list_type = listType,
                    liker_id = me,
                    created_at = java.time.OffsetDateTime.now().toString(),
                ))
                _likedListEntries.value = _likedListEntries.value + key
            }
            LocalDataStore.setTable(LocalDataStore.KEY_LIST_LIKES, likes)
            _listLikes.value = likes
            Unit
        }.onFailure { Log.w("SocialRepository", "toggleListLike failed", it) }
    }

    /** Get like count for a specific entry (target user + specimen + list type). */
    fun listLikeCount(targetUserId: String, specimenId: String, listType: String): Int {
        return _listLikes.value.count {
            it.target_user_id == targetUserId &&
            it.specimen_id == specimenId &&
            it.list_type == listType
        }
    }

    /** Check if the current user has liked a specific entry. */
    fun isListEntryLiked(targetUserId: String, specimenId: String, listType: String): Boolean {
        val key = "$targetUserId:$specimenId:$listType"
        return _likedListEntries.value.contains(key)
    }

    private fun Double.roundToInt(): Int = Math.round(this).toInt()

    companion object {
        val instance: SocialRepository by lazy { SocialRepository() }
    }
}
