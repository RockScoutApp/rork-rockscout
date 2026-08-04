package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Stores in-progress chat reply text per thread or group chat ID.
 *
 * When a user starts typing in a chat but navigates away, the text is saved
 * so they can pick up where they left off. Drafts are shown in the messages
 * notification popup alongside email drafts.
 *
 * Stored in [Context.filesDir] as a JSON file. Drafts auto-expire after 7 days.
 */
@Serializable
data class ChatDraftEntry(
    val id: String,           // thread ID or group chat ID
    val recipientName: String, // display name of the other user or group name
    val body: String,
    val isGroup: Boolean = false,
    val savedAtMs: Long = 0L,
)

@Serializable
private data class ChatDraftStoreData(
    val drafts: List<ChatDraftEntry> = emptyList(),
)

object ChatDraftStore {

    private const val DRAFT_FILE = "chat_drafts.json"
    private const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000

    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private lateinit var appContext: Context

    private val _drafts = MutableStateFlow<List<ChatDraftEntry>>(emptyList())
    val drafts: StateFlow<List<ChatDraftEntry>> = _drafts.asStateFlow()

    fun initialize(context: Context) {
        appContext = context.applicationContext
        load()
    }

    /** Save or update a draft for a chat. */
    fun saveDraft(id: String, recipientName: String, body: String, isGroup: Boolean = false) {
        if (!::appContext.isInitialized) return
        if (body.isBlank()) {
            deleteDraft(id)
            return
        }
        val now = System.currentTimeMillis()
        val current = _drafts.value.toMutableList()
        val idx = current.indexOfFirst { it.id == id }
        val entry = ChatDraftEntry(
            id = id,
            recipientName = recipientName,
            body = body,
            isGroup = isGroup,
            savedAtMs = now,
        )
        if (idx >= 0) current[idx] = entry else current.add(entry)
        _drafts.value = current
        persist(current)
    }

    /** Get the draft body for a chat, or empty string if none. */
    fun getDraft(id: String): String {
        return _drafts.value.firstOrNull { it.id == id }?.body ?: ""
    }

    /** Delete a draft for a chat. */
    fun deleteDraft(id: String) {
        val current = _drafts.value.filterNot { it.id == id }
        _drafts.value = current
        persist(current)
    }

    /** Total number of drafts (chat + email). */
    fun chatDraftCount(): Int = _drafts.value.size

    /** Load drafts from disk, pruning expired ones. */
    fun load() {
        if (!::appContext.isInitialized) return
        try {
            val file = File(appContext.filesDir, DRAFT_FILE)
            if (!file.exists()) return
            val data = json.decodeFromString(ChatDraftStoreData.serializer(), file.readText())
            val now = System.currentTimeMillis()
            val active = data.drafts.filter { now - it.savedAtMs < SEVEN_DAYS_MS }
            if (active.size != data.drafts.size) {
                persist(active)
            }
            _drafts.value = active
        } catch (e: Exception) {
            Log.w("ChatDraftStore", "load failed", e)
        }
    }

    private fun persist(drafts: List<ChatDraftEntry>) {
        if (!::appContext.isInitialized) return
        try {
            val file = File(appContext.filesDir, DRAFT_FILE)
            val data = ChatDraftStoreData(drafts = drafts)
            file.writeText(json.encodeToString(ChatDraftStoreData.serializer(), data))
        } catch (e: Exception) {
            Log.w("ChatDraftStore", "persist failed", e)
        }
    }

    /** Total drafts including email drafts, for the notification count. */
    fun totalDraftCount(): Int {
        val emailCount = if (EmailComposerDraftStore.exists()) 1 else 0
        return chatDraftCount() + emailCount
    }
}
