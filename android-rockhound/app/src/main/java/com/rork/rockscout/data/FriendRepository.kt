package com.rork.rockscout.data

import android.util.Log
import com.rork.rockscout.data.MockDataSeeder.LocalFriendRequest
import com.rork.rockscout.data.MockDataSeeder.LocalBlock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * RockScout Friends — friend requests, blocking, and the friends list.
 *
 * Fully self-contained: all data stored in [LocalDataStore]. No Supabase.
 *
 * Friend requests are separate from message requests: accepting a friend
 * request creates a connection (both users become Friends). Accepting a
 * message request only opens a thread — no connection.
 *
 * Blocking is symmetric and immediate: both users are hidden from each other.
 */
class FriendRepository private constructor() {

    @Serializable
    data class FriendRequestRow(
        val id: String,
        val sender_id: String,
        val recipient_id: String,
        val status: String,
        val created_at: String,
        val responded_at: String? = null,
    )

    private val _incomingFriendRequests = MutableStateFlow<List<FriendRequestRow>>(emptyList())
    val incomingFriendRequests: StateFlow<List<FriendRequestRow>> = _incomingFriendRequests.asStateFlow()

    private val _outgoingFriendRequests = MutableStateFlow<List<FriendRequestRow>>(emptyList())
    val outgoingFriendRequests: StateFlow<List<FriendRequestRow>> = _outgoingFriendRequests.asStateFlow()

    @Serializable
    data class BlockRow(
        val id: String,
        val blocker_id: String,
        val blocked_id: String,
        val created_at: String,
    )

    private val _blockedUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    private val _blockedByUserIds = MutableStateFlow<Set<String>>(emptySet())
    val blockedByUserIds: StateFlow<Set<String>> = _blockedByUserIds.asStateFlow()

    private val _friends = MutableStateFlow<List<SocialRepository.HunterProfile>>(emptyList())
    val friends: StateFlow<List<SocialRepository.HunterProfile>> = _friends.asStateFlow()

    private fun currentUserId(): String? = AuthRepository.instance.currentUserId

    /** Send a friend request to [recipientId]. Returns ok / error message. */
    suspend fun sendFriendRequest(recipientId: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        if (isBlocked(recipientId)) {
            return Result.failure(IllegalStateException("You are blocked from sending requests to this user."))
        }
        return runCatching {
            if (me == recipientId) return@runCatching
            val social = SocialRepository.instance
            if (social.isConnected(recipientId)) return@runCatching
            val rows = LocalDataStore.getTable<LocalFriendRequest>(LocalDataStore.KEY_FRIEND_REQUESTS).toMutableList()
            if (rows.any { it.sender_id == me && it.recipient_id == recipientId && it.status == "pending" }) {
                return@runCatching
            }
            rows.add(LocalFriendRequest(
                id = "fr-" + UUID.randomUUID(),
                sender_id = me,
                recipient_id = recipientId,
                status = "pending",
                created_at = java.time.OffsetDateTime.now().toString(),
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_FRIEND_REQUESTS, rows)
            // Also create a notification for the recipient.
            NotificationRepository.instance.createNotification(
                userId = recipientId,
                type = NotificationRepository.TYPE_FRIEND_REQUEST,
                actorId = me,
                refId = null,
                body = "You have a new RockScout Friends request.",
            )
            loadOutgoingFriendRequests()
        }.onFailure { Log.w("FriendRepository", "sendFriendRequest failed", it) }
    }

    /** Load incoming + outgoing friend requests. */
    suspend fun loadFriendRequests() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalFriendRequest>(LocalDataStore.KEY_FRIEND_REQUESTS)
            _incomingFriendRequests.value = rows
                .filter { it.recipient_id == me && it.status == "pending" }
                .map { FriendRequestRow(it.id, it.sender_id, it.recipient_id, it.status, it.created_at, it.responded_at) }
            _outgoingFriendRequests.value = rows
                .filter { it.sender_id == me }
                .map { FriendRequestRow(it.id, it.sender_id, it.recipient_id, it.status, it.created_at, it.responded_at) }
        }.onFailure { Log.w("FriendRepository", "loadFriendRequests failed", it) }
    }

    private suspend fun loadOutgoingFriendRequests() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalFriendRequest>(LocalDataStore.KEY_FRIEND_REQUESTS)
            _outgoingFriendRequests.value = rows
                .filter { it.sender_id == me }
                .map { FriendRequestRow(it.id, it.sender_id, it.recipient_id, it.status, it.created_at, it.responded_at) }
        }.onFailure { Log.w("FriendRepository", "loadOutgoingFriendRequests failed", it) }
    }

    /** Accept a friend request: mark accepted + create the reciprocal connection. */
    suspend fun acceptFriendRequest(request: FriendRequestRow): Result<Unit> {
        return runCatching {
            updateFriendRequestStatus(request.id, "accepted")
            SocialRepository.instance.createConnection(request.sender_id)
            loadFriendRequests()
            loadFriends()
            Unit
        }.onFailure { Log.w("FriendRepository", "acceptFriendRequest failed", it) }
    }

    /** Deny a friend request — just mark denied. */
    suspend fun denyFriendRequest(request: FriendRequestRow): Result<Unit> {
        return runCatching {
            updateFriendRequestStatus(request.id, "denied")
            loadFriendRequests()
            Unit
        }.onFailure { Log.w("FriendRepository", "denyFriendRequest failed", it) }
    }

    /** Deny and block: mark blocked + write a block row. */
    suspend fun denyAndBlockFriendRequest(request: FriendRequestRow): Result<Unit> {
        return runCatching {
            updateFriendRequestStatus(request.id, "blocked")
            blockUser(request.sender_id)
            loadFriendRequests()
            Unit
        }.onFailure { Log.w("FriendRepository", "denyAndBlockFriendRequest failed", it) }
    }

    private fun updateFriendRequestStatus(requestId: String, status: String) {
        val rows = LocalDataStore.getTable<LocalFriendRequest>(LocalDataStore.KEY_FRIEND_REQUESTS).toMutableList()
        val idx = rows.indexOfFirst { it.id == requestId }
        if (idx >= 0) {
            rows[idx] = rows[idx].copy(status = status, responded_at = java.time.OffsetDateTime.now().toString())
            LocalDataStore.setTable(LocalDataStore.KEY_FRIEND_REQUESTS, rows)
        }
    }

    /** Block a user — symmetric hide. */
    suspend fun blockUser(otherId: String): Result<Unit> {
        val me = currentUserId() ?: return Result.failure(IllegalStateException("Not signed in"))
        return runCatching {
            val rows = LocalDataStore.getTable<LocalBlock>(LocalDataStore.KEY_BLOCKS).toMutableList()
            if (rows.none { it.blocker_id == me && it.blocked_id == otherId }) {
                rows.add(LocalBlock(
                    id = "block-" + UUID.randomUUID(),
                    blocker_id = me,
                    blocked_id = otherId,
                    created_at = java.time.OffsetDateTime.now().toString(),
                ))
                LocalDataStore.setTable(LocalDataStore.KEY_BLOCKS, rows)
            }
            loadBlocks()
            Unit
        }.onFailure { Log.w("FriendRepository", "blockUser failed", it) }
    }

    /** Load all blocks I've made + blocks against me. */
    suspend fun loadBlocks() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalBlock>(LocalDataStore.KEY_BLOCKS)
            _blockedUserIds.value = rows.filter { it.blocker_id == me }.map { it.blocked_id }.toSet()
            _blockedByUserIds.value = rows.filter { it.blocked_id == me }.map { it.blocker_id }.toSet()
        }.onFailure { Log.w("FriendRepository", "loadBlocks failed", it) }
    }

    /** Is [otherId] blocked (by me or blocking me)? */
    fun isBlocked(otherId: String): Boolean {
        return otherId in _blockedUserIds.value || otherId in _blockedByUserIds.value
    }

    /** Unfriend a user — removes the connection and reloads the friends list. */
    suspend fun unfriend(userId: String) {
        runCatching {
            SocialRepository.instance.removeConnection(userId)
            loadFriends()
            Unit
        }.onFailure { Log.w("FriendRepository", "unfriend failed", it) }
    }

    /** Load the friends list (connected users' profiles). */
    suspend fun loadFriends() {
        val social = SocialRepository.instance
        social.loadConnections()
        val connectionIds = social.connections.value
        if (connectionIds.isEmpty()) {
            _friends.value = emptyList()
            return
        }
        runCatching {
            val fetched = mutableMapOf<String, SocialRepository.HunterProfile>()
            social.fetchProfiles(connectionIds).forEach { fetched[it.id] = it }
            _friends.value = connectionIds.mapNotNull { fetched[it] }
        }.onFailure { Log.w("FriendRepository", "loadFriends failed", it) }
    }

    companion object {
        val instance: FriendRepository by lazy { FriendRepository() }
    }
}
