package com.rork.rockscout.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Tracks locally-created/modified records that need to be synced to Supabase
 * when connectivity is restored or during the nightly 4 AM sync window.
 *
 * Each pending operation records the table name, the local record ID, and the
 * type of operation (INSERT/UPDATE/DELETE). When the sync worker runs, it
 * drains the queue by:
 *  1. Uploading any local photos to Supabase Storage via [PhotoUploader]
 *  2. Upserting the record to the corresponding Supabase table
 *  3. Removing the queue entry on success
 *
 * The queue is persisted to SharedPreferences so it survives app kills and
 * device restarts. On failure (network error), the entry stays in the queue
 * and is retried on the next sync trigger.
 *
 * Triggers:
 *  - [OfflineSyncWorker] — WorkManager periodic + connectivity-restored
 *  - Nightly sync at 4 AM in the user's timezone (via WorkScheduler)
 *  - Immediate drain when the app detects connectivity restoration
 */
object SyncQueueManager {

    private const val TAG = "SyncQueueManager"
    private const val PREFS_NAME = "rockscout_sync_queue"
    private const val KEY_QUEUE = "pending_sync_queue"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Table names supported by the sync queue. */
    enum class SyncTable {
        CAPTURES,
        SAVED_IMAGES,
        FIELD_JOURNAL,
        TRIPS,
    }

    /** Type of operation to perform during sync. */
    enum class SyncOp {
        UPSERT,
        DELETE,
    }

    /**
     * A single pending sync operation.
     *
     * @property id        Unique ID for this queue entry.
     * @property table     The target Supabase table.
     * @property recordId  The local record ID (used to fetch the current record
     *                     state from [AppRepository] at sync time).
     * @property op        UPSERT or DELETE.
     * @property queuedAt  Epoch millis when this entry was added to the queue.
     * @property attempts  Number of sync attempts (for backoff/retry logic).
     */
    @Serializable
    data class PendingSync(
        val id: String,
        val table: SyncTable,
        val recordId: String,
        val op: SyncOp,
        val queuedAt: Long = System.currentTimeMillis(),
        val attempts: Int = 0,
    )

    private val _queue = MutableStateFlow<List<PendingSync>>(emptyList())
    val queue: StateFlow<List<PendingSync>> = _queue.asStateFlow()

    /** Number of pending items — used by UI to show a sync badge. */
    val pendingCount: StateFlow<Int> = MutableStateFlow(0)

    private var prefs: android.content.SharedPreferences? = null

    /** Must be called once from Application.onCreate. */
    fun initialize(context: android.content.Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE)
        loadQueue()
        Log.d(TAG, "Initialized with ${_queue.value.size} pending sync items")
    }

    private fun loadQueue() {
        val p = prefs ?: return
        runCatching {
            p.getString(KEY_QUEUE, null)?.let { raw ->
                _queue.value = json.decodeFromString<List<PendingSync>>(raw)
            }
        }.onFailure { Log.w(TAG, "Failed to load queue: ${it.message}") }
        updatePendingCount()
    }

    private fun saveQueue() {
        val p = prefs ?: return
        runCatching {
            p.edit().putString(KEY_QUEUE, json.encodeToString(_queue.value)).apply()
        }.onFailure { Log.w(TAG, "Failed to save queue: ${it.message}") }
        updatePendingCount()
    }

    private fun updatePendingCount() {
        (pendingCount as MutableStateFlow).value = _queue.value.size
    }

    /**
     * Enqueues a pending sync operation. If an entry for the same table +
     * recordId already exists, it is replaced (keeping the latest op).
     */
    fun enqueue(table: SyncTable, recordId: String, op: SyncOp = SyncOp.UPSERT) {
        val entry = PendingSync(
            id = "${table.name}_${recordId}_${System.currentTimeMillis()}",
            table = table,
            recordId = recordId,
            op = op,
        )
        // Remove any existing entry for the same table + recordId, then add
        _queue.value = _queue.value.filterNot {
            it.table == table && it.recordId == recordId
        } + entry
        saveQueue()
        Log.d(TAG, "Enqueued $op for $table/$recordId (${_queue.value.size} total pending)")
    }

    /**
     * Enqueues a DELETE operation and immediately removes any pending UPSERT
     * for the same record (no point uploading something we're about to delete).
     */
    fun enqueueDelete(table: SyncTable, recordId: String) {
        _queue.value = _queue.value.filterNot {
            it.table == table && it.recordId == recordId && it.op == SyncOp.UPSERT
        }
        // Only add a DELETE if the record was previously synced (has a remote row).
        // For local-only records that were never synced, just drop the entry.
        // We can't know for sure here, so we add the DELETE and let the sync
        // logic handle "delete if exists" gracefully.
        val entry = PendingSync(
            id = "${table.name}_${recordId}_del_${System.currentTimeMillis()}",
            table = table,
            recordId = recordId,
            op = SyncOp.DELETE,
        )
        _queue.value = _queue.value + entry
        saveQueue()
        Log.d(TAG, "Enqueued DELETE for $table/$recordId (${_queue.value.size} total pending)")
    }

    /** Removes a queue entry after successful sync. */
    fun remove(entryId: String) {
        _queue.value = _queue.value.filterNot { it.id == entryId }
        saveQueue()
    }

    /** Increments the attempt counter for a queue entry (on failure). */
    fun incrementAttempts(entryId: String) {
        _queue.value = _queue.value.map {
            if (it.id == entryId) it.copy(attempts = it.attempts + 1) else it
        }
        saveQueue()
    }

    /** Clears all entries (used after a full pull-sync replaces local state). */
    fun clearAll() {
        _queue.value = emptyList()
        saveQueue()
    }

    /**
     * Drains the queue by syncing each pending operation to Supabase.
     * Called by [OfflineSyncWorker] and the immediate connectivity-restored
     * trigger. Returns the number of successfully synced items.
     */
    suspend fun drain(): Int {
        if (!SupabaseDataSync.isEnabled) {
            Log.d(TAG, "Sync not enabled — skipping drain")
            return 0
        }

        val pending = _queue.value.toList()
        if (pending.isEmpty()) return 0

        Log.i(TAG, "Draining ${pending.size} pending sync items")
        var successCount = 0
        val uid = AuthRepository.instance.currentUserId ?: return 0

        for (entry in pending) {
            val result = when (entry.table) {
                SyncTable.CAPTURES -> syncCapture(entry, uid)
                SyncTable.SAVED_IMAGES -> syncSavedImage(entry, uid)
                SyncTable.FIELD_JOURNAL -> syncJournalEntry(entry, uid)
                SyncTable.TRIPS -> syncTrip(entry, uid)
            }

            if (result) {
                remove(entry.id)
                successCount++
            } else {
                incrementAttempts(entry.id)
                // Give up after 10 attempts — drop the entry to avoid infinite retries
                val updated = _queue.value.firstOrNull { it.id == entry.id }
                if (updated != null && updated.attempts >= 10) {
                    Log.w(TAG, "Dropping ${entry.table}/${entry.recordId} after 10 failed attempts")
                    remove(entry.id)
                }
            }
        }

        Log.i(TAG, "Drain complete: $successCount/${pending.size} synced successfully")
        return successCount
    }

    // ─── Per-table sync logic ──────────────────────────────────────────────

    private suspend fun syncCapture(entry: PendingSync, uid: String): Boolean {
        if (entry.op == SyncOp.DELETE) {
            return SupabaseDataSync.deleteCapture(entry.recordId)
        }
        val capture = AppRepository.instance.captures.value.firstOrNull { it.id == entry.recordId }
            ?: return true // Already deleted locally — nothing to sync

        // Upload any local file:// URIs to Supabase Storage
        val uploadedUrls = PhotoUploader.uploadAll(capture.imageUris, uid)
        val remoteImageUrls = capture.imageUris.map { uploadedUrls[it] ?: it }

        // Update local capture with remote URLs so future loads use the CDN
        if (remoteImageUrls != capture.imageUris) {
            AppRepository.instance.updateCaptureImageUrls(capture.id, remoteImageUrls)
        }

        return SupabaseDataSync.pushCapture(capture.copy(imageUris = remoteImageUrls), uid)
    }

    private suspend fun syncSavedImage(entry: PendingSync, uid: String): Boolean {
        if (entry.op == SyncOp.DELETE) {
            return SupabaseDataSync.deleteSavedImage(entry.recordId)
        }
        val image = AppRepository.instance.savedImages.value.firstOrNull { it.id == entry.recordId }
            ?: return true

        // Upload local URI if present
        val remoteUrl = if (image.url.startsWith("file://") || image.url.startsWith("/")) {
            PhotoUploader.upload(image.url, uid) ?: image.url
        } else {
            image.url
        }

        // Update local with remote URL
        if (remoteUrl != image.url) {
            AppRepository.instance.updateSavedImageUrl(image.id, remoteUrl)
        }

        return SupabaseDataSync.pushSavedImage(image.copy(url = remoteUrl), uid)
    }

    private suspend fun syncJournalEntry(entry: PendingSync, uid: String): Boolean {
        if (entry.op == SyncOp.DELETE) {
            return SupabaseDataSync.deleteJournalEntry(entry.recordId)
        }
        val journal = AppRepository.instance.journalEntries.value.firstOrNull { it.id == entry.recordId }
            ?: return true

        // Upload journal photos
        val uploadedUrls = PhotoUploader.uploadAll(journal.photoUris, uid)
        val remotePhotoUrls = journal.photoUris.map { uploadedUrls[it] ?: it }

        if (remotePhotoUrls != journal.photoUris) {
            AppRepository.instance.updateJournalPhotoUrls(journal.id, remotePhotoUrls)
        }

        return SupabaseDataSync.pushJournalEntry(journal.copy(photoUris = remotePhotoUrls), uid)
    }

    private suspend fun syncTrip(entry: PendingSync, uid: String): Boolean {
        if (entry.op == SyncOp.DELETE) {
            return SupabaseDataSync.deleteTrip(entry.recordId)
        }
        // Trips don't have direct photo URIs, but specimen markers don't have photos either
        val trip = AppRepository.instance.trips.value.firstOrNull { it.id == entry.recordId }
            ?: AppRepository.instance.archivedTrips.value.firstOrNull { it.id == entry.recordId }
            ?: return true

        return SupabaseDataSync.pushTrip(trip, uid)
    }

    // ─── Immediate drain trigger ───────────────────────────────────────────

    /**
     * Attempts to drain the queue immediately (e.g. when connectivity is
     * restored while the app is foreground). Safe to call repeatedly.
     */
    fun drainInBackground() {
        if (_queue.value.isEmpty()) return
        if (!SupabaseDataSync.isEnabled) return
        scope.launch { drain() }
    }
}
