package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Stores messages that failed to send due to network issues.
 * Messages are saved locally and automatically retried when
 * connectivity is restored.
 *
 * Used by [SupabaseMessagingRepository] for group chat messages
 * and private chat messages (Supabase-backed threads).
 */
@Serializable
data class PendingMessage(
    val id: String,
    val chatId: String,
    val body: String,
    val imageUrl: String? = null,
    val replyToMessageId: String? = null,
    val taggedUserIds: List<String> = emptyList(),
    val isGroup: Boolean,
    val queuedAtMs: Long = System.currentTimeMillis(),
    val attempts: Int = 0,
)

@Serializable
private data class OfflineMessageStoreData(
    val messages: List<PendingMessage> = emptyList(),
)

object OfflineMessageQueue {

    private const val FILE_NAME = "offline_message_queue.json"
    private const val MAX_ATTEMPTS = 10
    private const val TAG = "OfflineMsgQueue"

    private val json = Json { ignoreUnknownKeys = true; isLenient = true; encodeDefaults = true }

    private lateinit var appContext: Context

    private val _pendingMessages = MutableStateFlow<List<PendingMessage>>(emptyList())
    val pendingMessages: StateFlow<List<PendingMessage>> = _pendingMessages.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private var retryCallback: (suspend (PendingMessage) -> Result<Unit>)? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        load()
        registerNetworkCallback()
    }

    /** Set the callback that will be invoked to send a pending message. */
    fun setRetryCallback(callback: suspend (PendingMessage) -> Result<Unit>) {
        retryCallback = callback
    }

    /** Enqueue a message for later sending. */
    fun enqueue(message: PendingMessage) {
        val current = _pendingMessages.value.toMutableList()
        current.add(message)
        _pendingMessages.value = current
        persist(current)
        Log.d(TAG, "Enqueued message for chat ${message.chatId}, total pending: ${current.size}")
    }

    /** Remove a message from the queue after successful send. */
    fun remove(messageId: String) {
        val current = _pendingMessages.value.filterNot { it.id == messageId }
        _pendingMessages.value = current
        persist(current)
    }

    /** Get all pending messages for a specific chat (for UI display). */
    fun getPendingForChat(chatId: String): List<PendingMessage> =
        _pendingMessages.value.filter { it.chatId == chatId }

    /** Total pending count (for UI badge). */
    fun pendingCount(): Int = _pendingMessages.value.size

    /** Check if there are any pending messages. */
    fun hasPending(): Boolean = _pendingMessages.value.isNotEmpty()

    /**
     * Attempt to send all pending messages. Called when network is restored
     * or periodically. Returns the number of successfully sent messages.
     */
    suspend fun drainQueue(): Int {
        val callback = retryCallback ?: return 0
        if (_pendingMessages.value.isEmpty()) return 0

        Log.i(TAG, "Draining ${_pendingMessages.value.size} pending messages")
        val toSend = _pendingMessages.value.toList()
        var successCount = 0

        for (msg in toSend) {
            val result = try {
                callback(msg)
            } catch (e: Exception) {
                Log.w(TAG, "Retry send failed for ${msg.id}", e)
                Result.failure(e)
            }

            if (result.isSuccess) {
                remove(msg.id)
                successCount++
            } else {
                incrementAttempts(msg.id)
            }
        }

        // Drop messages that have failed too many times
        val overLimit = _pendingMessages.value.filter { it.attempts >= MAX_ATTEMPTS }
        for (msg in overLimit) {
            Log.w(TAG, "Dropping message ${msg.id} after $MAX_ATTEMPTS failed attempts")
            remove(msg.id)
        }

        Log.i(TAG, "Drain complete: $successCount sent, ${_pendingMessages.value.size} still pending")
        return successCount
    }

    private fun incrementAttempts(messageId: String) {
        val current = _pendingMessages.value.toMutableList()
        val idx = current.indexOfFirst { it.id == messageId }
        if (idx >= 0) {
            current[idx] = current[idx].copy(attempts = current[idx].attempts + 1)
            _pendingMessages.value = current
            persist(current)
        }
    }

    private fun registerNetworkCallback() {
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.i(TAG, "Network restored — draining queue")
                _isOnline.value = true
                CoroutineScope(Dispatchers.IO).launch {
                    drainQueue()
                }
            }

            override fun onLost(network: Network) {
                Log.d(TAG, "Network lost")
                _isOnline.value = false
            }
        })
    }

    private fun load() {
        if (!::appContext.isInitialized) return
        try {
            val file = File(appContext.filesDir, FILE_NAME)
            if (!file.exists()) return
            val data = json.decodeFromString(OfflineMessageStoreData.serializer(), file.readText())
            _pendingMessages.value = data.messages
            Log.d(TAG, "Loaded ${data.messages.size} pending messages")
        } catch (e: Exception) {
            Log.w(TAG, "load failed", e)
        }
    }

    private fun persist(messages: List<PendingMessage>) {
        if (!::appContext.isInitialized) return
        try {
            val file = File(appContext.filesDir, FILE_NAME)
            val data = OfflineMessageStoreData(messages = messages)
            file.writeText(json.encodeToString(OfflineMessageStoreData.serializer(), data))
        } catch (e: Exception) {
            Log.w(TAG, "persist failed", e)
        }
    }
}
