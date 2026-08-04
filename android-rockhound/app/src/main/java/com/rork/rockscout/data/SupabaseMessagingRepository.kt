package com.rork.rockscout.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.util.UUID

/**
 * Supabase-backed messaging repository for private chats and group chats.
 *
 * Uses the Supabase REST API (PostgREST) via [NetworkClient] to read/write
 * the chat_threads, chat_thread_participants, chat_messages, group_chats,
 * group_chat_members, group_chat_invites, and group_messages tables.
 *
 * All operations require a valid Supabase access token from [AuthRepository].
 * RLS policies enforce that users can only access chats they're members of.
 *
 * Falls back to [SocialRepository] (local) when Supabase is not available
 * (offline or not signed in with Supabase tokens).
 */
object SupabaseMessagingRepository {

    private const val TAG = "SupabaseMessaging"
    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }
    private val client = NetworkClient.client

    private fun baseUrl() = BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_URL", BuildSecrets.SUPABASE_URL)
    private fun anonKey() = BuildSecrets.resolve("EXPO_PUBLIC_SUPABASE_ANON_KEY", BuildSecrets.SUPABASE_ANON_KEY)
    private fun accessToken(): String? = LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)
    private fun userId(): String? = AuthRepository.instance.currentUserId

    val isEnabled: Boolean
        get() = !accessToken().isNullOrBlank() && !userId().isNullOrBlank()

    // ─── Data Models ───────────────────────────────────────────────────

    @Serializable
    data class ChatThreadDto(
        val id: String = "",
        val created_at: String = "",
        val last_message_at: String = "",
    )

    @Serializable
    data class ChatThreadParticipantDto(
        val id: String = "",
        val thread_id: String = "",
        val user_id: String = "",
        val joined_at: String = "",
    )

    @Serializable
    data class ChatMessageDto(
        val id: String = "",
        val thread_id: String = "",
        val sender_id: String = "",
        val body: String = "",
        val image_url: String? = null,
        val reply_to_message_id: String? = null,
        val tagged_user_ids: List<String> = emptyList(),
        val created_at: String = "",
    )

    @Serializable
    data class GroupChatDto(
        val id: String = "",
        val name: String = "",
        val subject: String = "",
        val creator_id: String = "",
        val max_members: Int? = null,
        val profanity_filter_level: String = "normal",
        val header_image_url: String? = null,
        val scroll_speed_setting: String = "normal",
        val created_at: String = "",
        val deleted_at: String? = null,
    )

    @Serializable
    data class GroupChatMemberDto(
        val id: String = "",
        val group_chat_id: String = "",
        val user_id: String = "",
        val joined_at: String = "",
        val role: String = "member",
    )

    @Serializable
    data class GroupChatInviteDto(
        val id: String = "",
        val group_chat_id: String = "",
        val inviter_id: String = "",
        val invitee_id: String = "",
        val status: String = "pending",
        val created_at: String = "",
    )

    @Serializable
    data class GroupMessageDto(
        val id: String = "",
        val group_chat_id: String = "",
        val sender_id: String = "",
        val body: String = "",
        val image_url: String? = null,
        val reply_to_message_id: String? = null,
        val tagged_user_ids: List<String> = emptyList(),
        val created_at: String = "",
    )

    @Serializable
    data class UserWarningDto(
        val id: String = "",
        val user_id: String = "",
        val reason: String = "",
        val source: String = "chat",
        val source_id: String? = null,
        val auto_reported: Boolean = false,
        val created_at: String = "",
    )

    // ─── State Flows ───────────────────────────────────────────────────

    private val _groupChats = MutableStateFlow<List<GroupChatDto>>(emptyList())
    val groupChats: StateFlow<List<GroupChatDto>> = _groupChats.asStateFlow()

    private val _groupChatMembers = MutableStateFlow<List<GroupChatMemberDto>>(emptyList())
    val groupChatMembers: StateFlow<List<GroupChatMemberDto>> = _groupChatMembers.asStateFlow()

    private val _pendingGroupInvites = MutableStateFlow<List<GroupChatInviteDto>>(emptyList())
    val pendingGroupInvites: StateFlow<List<GroupChatInviteDto>> = _pendingGroupInvites.asStateFlow()

    private val _groupMessages = MutableStateFlow<List<GroupMessageDto>>(emptyList())
    val groupMessages: StateFlow<List<GroupMessageDto>> = _groupMessages.asStateFlow()

    private val _privateThreads = MutableStateFlow<List<ChatThreadDto>>(emptyList())
    val privateThreads: StateFlow<List<ChatThreadDto>> = _privateThreads.asStateFlow()

    private val _privateMessages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val privateMessages: StateFlow<List<ChatMessageDto>> = _privateMessages.asStateFlow()

    private val _threadParticipants = MutableStateFlow<List<ChatThreadParticipantDto>>(emptyList())
    val threadParticipants: StateFlow<List<ChatThreadParticipantDto>> = _threadParticipants.asStateFlow()

    private val _warningCount = MutableStateFlow(0)
    val warningCount: StateFlow<Int> = _warningCount.asStateFlow()

    // ─── Private Chats ─────────────────────────────────────────────────

    /** Create a private chat thread between the current user and [otherUserId]. */
    suspend fun createPrivateThread(otherUserId: String): Result<String> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            val threadId = "thread-" + UUID.randomUUID()
            val body = buildJsonObject {
                put("id", threadId)
            }.toString()
            val resp = client.post("${baseUrl()}/rest/v1/chat_threads") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(body)
            }
            if (!resp.status.isSuccess()) {
                return Result.failure(Exception("Failed to create thread: ${resp.status}"))
            }
            // Add both participants
            addThreadParticipant(threadId, uid)
            addThreadParticipant(threadId, otherUserId)
            Result.success(threadId)
        } catch (e: Exception) {
            Log.e(TAG, "createPrivateThread failed", e)
            Result.failure(e)
        }
    }

    private suspend fun addThreadParticipant(threadId: String, userId: String): Boolean {
        val token = accessToken() ?: return false
        return try {
            val body = buildJsonObject {
                put("thread_id", threadId)
                put("user_id", userId)
            }.toString()
            val resp = client.post("${baseUrl()}/rest/v1/chat_thread_participants") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(body)
            }
            resp.status.isSuccess()
        } catch (e: Exception) {
            Log.e(TAG, "addThreadParticipant failed", e)
            false
        }
    }

    /** Load all private chat threads the current user is a participant in. */
    suspend fun loadPrivateThreads(): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            // Get participant rows for my user id
            val resp = client.get("${baseUrl()}/rest/v1/chat_thread_participants?user_id=eq.$uid&select=thread_id") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to load participants"))
            val raw = resp.body<String>()
            val arr = json.parseToJsonElement(raw).jsonArray
            val threadIds = arr.mapNotNull { it.jsonObject["thread_id"]?.jsonPrimitive?.contentOrNull }
            if (threadIds.isEmpty()) {
                _privateThreads.value = emptyList()
                _threadParticipants.value = emptyList()
                return Result.success(Unit)
            }
            // Fetch thread data
            val idsParam = threadIds.joinToString(",") { "\"$it\"" }
            val threadsResp = client.get("${baseUrl()}/rest/v1/chat_threads?id=in.($idsParam)&select=*&order=last_message_at.desc") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (threadsResp.status.isSuccess()) {
                val threadsRaw = threadsResp.body<String>()
                _privateThreads.value = json.decodeFromString(threadsRaw)
            }
            // Fetch all participants for these threads
            val partsResp = client.get("${baseUrl()}/rest/v1/chat_thread_participants?thread_id=in.($idsParam)&select=*") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (partsResp.status.isSuccess()) {
                val partsRaw = partsResp.body<String>()
                _threadParticipants.value = json.decodeFromString(partsRaw)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "loadPrivateThreads failed", e)
            Result.failure(e)
        }
    }

    /** Load messages for a private chat thread. */
    suspend fun loadPrivateMessages(threadId: String): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val resp = client.get("${baseUrl()}/rest/v1/chat_messages?thread_id=eq.$threadId&select=*&order=created_at.asc") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (resp.status.isSuccess()) {
                val raw = resp.body<String>()
                _privateMessages.value = json.decodeFromString(raw)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "loadPrivateMessages failed", e)
            Result.failure(e)
        }
    }

    /** Send a message in a private chat thread. */
    suspend fun sendPrivateMessage(
        threadId: String,
        body: String,
        imageUrl: String? = null,
        replyToMessageId: String? = null,
        taggedUserIds: List<String> = emptyList(),
    ): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            val payload = buildJsonObject {
                put("thread_id", threadId)
                put("sender_id", uid)
                put("body", body)
                if (imageUrl != null) put("image_url", imageUrl)
                if (replyToMessageId != null) put("reply_to_message_id", replyToMessageId)
                if (taggedUserIds.isNotEmpty()) {
                    put("tagged_user_ids", JsonArray(taggedUserIds.map { kotlinx.serialization.json.JsonPrimitive(it) }))
                }
            }.toString()
            val resp = client.post("${baseUrl()}/rest/v1/chat_messages") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(payload)
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to send message: ${resp.status}"))
            // Bump thread last_message_at
            val updatePayload = buildJsonObject { put("last_message_at", java.time.OffsetDateTime.now().toString()) }.toString()
            client.patch("${baseUrl()}/rest/v1/chat_threads?id=eq.$threadId") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(updatePayload)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "sendPrivateMessage failed", e)
            Result.failure(e)
        }
    }

    /** Mark messages in a thread as read by the current user. */
    suspend fun markPrivateMessagesRead(threadId: String): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            // Get unread messages not sent by me
            val resp = client.get("${baseUrl()}/rest/v1/chat_messages?thread_id=eq.$threadId&sender_id=neq.$uid&select=id") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (!resp.status.isSuccess()) return Result.success(Unit)
            val raw = resp.body<String>()
            val msgIds = json.parseToJsonElement(raw).jsonArray.mapNotNull { it.jsonObject["id"]?.jsonPrimitive?.contentOrNull }
            for (msgId in msgIds) {
                val payload = buildJsonObject {
                    put("message_id", msgId)
                    put("user_id", uid)
                }.toString()
                client.post("${baseUrl()}/rest/v1/chat_message_reads") {
                    header("apikey", anonKey())
                    header("Authorization", "Bearer $token")
                    header("Content-Type", "application/json")
                    header("Prefer", "return=minimal")
                    setBody(payload)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "markPrivateMessagesRead failed", e)
            Result.failure(e)
        }
    }

    /** Get participant user IDs for a thread. */
    fun threadParticipantIds(threadId: String): List<String> =
        _threadParticipants.value.filter { it.thread_id == threadId }.map { it.user_id }

    // ─── Group Chats ───────────────────────────────────────────────────

    /** Create a new group chat. Returns the chat ID. */
    suspend fun createGroupChat(
        name: String,
        subject: String,
        maxMembers: Int?,
        profanityFilterLevel: String,
        headerImageUrl: String?,
        scrollSpeedSetting: String,
    ): Result<String> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            val chatId = "gc-" + UUID.randomUUID()
            val payload = buildJsonObject {
                put("id", chatId)
                put("name", name)
                put("subject", subject)
                put("creator_id", uid)
                if (maxMembers != null) put("max_members", maxMembers)
                put("profanity_filter_level", profanityFilterLevel)
                if (headerImageUrl != null) put("header_image_url", headerImageUrl)
                put("scroll_speed_setting", scrollSpeedSetting)
            }.toString()
            val resp = client.post("${baseUrl()}/rest/v1/group_chats") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(payload)
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to create group chat: ${resp.status}"))
            // Add creator as first member
            val memberPayload = buildJsonObject {
                put("group_chat_id", chatId)
                put("user_id", uid)
                put("role", "creator")
            }.toString()
            client.post("${baseUrl()}/rest/v1/group_chat_members") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(memberPayload)
            }
            Result.success(chatId)
        } catch (e: Exception) {
            Log.e(TAG, "createGroupChat failed", e)
            Result.failure(e)
        }
    }

    /** Load all group chats the current user is a member of. */
    suspend fun loadGroupChats(): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            // Get my membership rows
            val resp = client.get("${baseUrl()}/rest/v1/group_chat_members?user_id=eq.$uid&select=group_chat_id") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to load memberships"))
            val raw = resp.body<String>()
            val arr = json.parseToJsonElement(raw).jsonArray
            val chatIds = arr.mapNotNull { it.jsonObject["group_chat_id"]?.jsonPrimitive?.contentOrNull }
            if (chatIds.isEmpty()) {
                _groupChats.value = emptyList()
                _groupChatMembers.value = emptyList()
                return Result.success(Unit)
            }
            val idsParam = chatIds.joinToString(",") { "\"$it\"" }
            // Fetch chat data (only non-deleted)
            val chatsResp = client.get("${baseUrl()}/rest/v1/group_chats?id=in.($idsParam)&deleted_at=is.null&select=*&order=created_at.desc") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (chatsResp.status.isSuccess()) {
                val chatsRaw = chatsResp.body<String>()
                _groupChats.value = json.decodeFromString(chatsRaw)
            }
            // Fetch all members for these chats
            val membersResp = client.get("${baseUrl()}/rest/v1/group_chat_members?group_chat_id=in.($idsParam)&select=*") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (membersResp.status.isSuccess()) {
                val membersRaw = membersResp.body<String>()
                _groupChatMembers.value = json.decodeFromString(membersRaw)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "loadGroupChats failed", e)
            Result.failure(e)
        }
    }

    /** Load messages for a group chat. */
    suspend fun loadGroupMessages(groupChatId: String): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val resp = client.get("${baseUrl()}/rest/v1/group_messages?group_chat_id=eq.$groupChatId&select=*&order=created_at.asc") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (resp.status.isSuccess()) {
                val raw = resp.body<String>()
                _groupMessages.value = json.decodeFromString(raw)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "loadGroupMessages failed", e)
            Result.failure(e)
        }
    }

    /** Send a message in a group chat. */
    suspend fun sendGroupMessage(
        groupChatId: String,
        body: String,
        imageUrl: String? = null,
        replyToMessageId: String? = null,
        taggedUserIds: List<String> = emptyList(),
    ): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            val payload = buildJsonObject {
                put("group_chat_id", groupChatId)
                put("sender_id", uid)
                put("body", body)
                if (imageUrl != null) put("image_url", imageUrl)
                if (replyToMessageId != null) put("reply_to_message_id", replyToMessageId)
                if (taggedUserIds.isNotEmpty()) {
                    put("tagged_user_ids", JsonArray(taggedUserIds.map { kotlinx.serialization.json.JsonPrimitive(it) }))
                }
            }.toString()
            val resp = client.post("${baseUrl()}/rest/v1/group_messages") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(payload)
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to send group message: ${resp.status}"))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "sendGroupMessage failed", e)
            Result.failure(e)
        }
    }

    /** Invite a user to a group chat. */
    suspend fun inviteToGroupChat(groupChatId: String, inviteeId: String): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            val payload = buildJsonObject {
                put("group_chat_id", groupChatId)
                put("inviter_id", uid)
                put("invitee_id", inviteeId)
            }.toString()
            val resp = client.post("${baseUrl()}/rest/v1/group_chat_invites") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(payload)
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to invite: ${resp.status}"))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "inviteToGroupChat failed", e)
            Result.failure(e)
        }
    }

    /** Accept a group chat invite — adds the user as a member. */
    suspend fun acceptGroupInvite(inviteId: String, groupChatId: String): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            // Update invite status
            val updatePayload = buildJsonObject {
                put("status", "accepted")
                put("responded_at", java.time.OffsetDateTime.now().toString())
            }.toString()
            client.patch("${baseUrl()}/rest/v1/group_chat_invites?id=eq.$inviteId") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(updatePayload)
            }
            // Add as member
            val memberPayload = buildJsonObject {
                put("group_chat_id", groupChatId)
                put("user_id", uid)
                put("role", "member")
            }.toString()
            client.post("${baseUrl()}/rest/v1/group_chat_members") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(memberPayload)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "acceptGroupInvite failed", e)
            Result.failure(e)
        }
    }

    /** Decline a group chat invite. */
    suspend fun declineGroupInvite(inviteId: String): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val payload = buildJsonObject {
                put("status", "declined")
                put("responded_at", java.time.OffsetDateTime.now().toString())
            }.toString()
            client.patch("${baseUrl()}/rest/v1/group_chat_invites?id=eq.$inviteId") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(payload)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "declineGroupInvite failed", e)
            Result.failure(e)
        }
    }

    /** Load pending group chat invites for the current user. */
    suspend fun loadPendingInvites(): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            val resp = client.get("${baseUrl()}/rest/v1/group_chat_invites?invitee_id=eq.$uid&status=eq.pending&select=*") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (resp.status.isSuccess()) {
                val raw = resp.body<String>()
                _pendingGroupInvites.value = json.decodeFromString(raw)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "loadPendingInvites failed", e)
            Result.failure(e)
        }
    }

    /** Leave a group chat (remove own membership). */
    suspend fun leaveGroupChat(groupChatId: String): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            val resp = client.delete("${baseUrl()}/rest/v1/group_chat_members?group_chat_id=eq.$groupChatId&user_id=eq.$uid") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to leave: ${resp.status}"))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "leaveGroupChat failed", e)
            Result.failure(e)
        }
    }

    /** Delete a group chat (creator only). Sets deleted_at. */
    suspend fun deleteGroupChat(groupChatId: String): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val payload = buildJsonObject {
                put("deleted_at", java.time.OffsetDateTime.now().toString())
            }.toString()
            val resp = client.patch("${baseUrl()}/rest/v1/group_chats?id=eq.$groupChatId") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(payload)
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to delete: ${resp.status}"))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "deleteGroupChat failed", e)
            Result.failure(e)
        }
    }

    /** Get member user IDs for a group chat. */
    fun groupChatMemberIds(groupChatId: String): List<String> =
        _groupChatMembers.value.filter { it.group_chat_id == groupChatId }.map { it.user_id }

    /** Get member count for a group chat. */
    fun groupChatMemberCount(groupChatId: String): Int =
        _groupChatMembers.value.count { it.group_chat_id == groupChatId }

    // ─── Warnings ──────────────────────────────────────────────────────

    /** Load the current user's warning count. */
    suspend fun loadWarningCount(): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        val uid = userId() ?: return Result.failure(Exception("No user ID"))
        return try {
            val resp = client.get("${baseUrl()}/rest/v1/user_warnings?user_id=eq.$uid&select=id") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
            }
            if (resp.status.isSuccess()) {
                val raw = resp.body<String>()
                _warningCount.value = json.parseToJsonElement(raw).jsonArray.size
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "loadWarningCount failed", e)
            Result.failure(e)
        }
    }

    /** Record a warning for a user (called via Cloudflare Worker edge function for server-side enforcement). */
    suspend fun recordWarning(userId: String, reason: String, source: String, sourceId: String?): Result<Unit> {
        val token = accessToken() ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val payload = buildJsonObject {
                put("user_id", userId)
                put("reason", reason)
                put("source", source)
                if (sourceId != null) put("source_id", sourceId)
            }.toString()
            val resp = client.post("${baseUrl()}/rest/v1/user_warnings") {
                header("apikey", anonKey())
                header("Authorization", "Bearer $token")
                header("Content-Type", "application/json")
                header("Prefer", "return=minimal")
                setBody(payload)
            }
            if (!resp.status.isSuccess()) return Result.failure(Exception("Failed to record warning: ${resp.status}"))
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "recordWarning failed", e)
            Result.failure(e)
        }
    }
}
