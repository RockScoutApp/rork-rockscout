package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Drains the [SyncQueueManager] pending queue — uploads local photos to
 * Supabase Storage and upserts/deletes records in the captures, saved images,
 * field journal, and trips tables.
 *
 * Triggered by:
 *  - [WorkScheduler.scheduleOfflineSync] — periodic (every 6 hours, network required)
 *  - [WorkScheduler.runOfflineSyncNow] — one-shot when connectivity is restored
 *  - Nightly sync at ~4 AM in the user's timezone (piggybacks on the existing
 *    NightlySyncWorker schedule, or runs as its own periodic with a 4 AM target)
 *
 * The worker is lightweight: it checks if sync is enabled (user is signed in
 * with Supabase tokens) and if there are pending items before doing any work.
 * On failure, returns [Result.retry] so WorkManager reschedules with backoff.
 */
class OfflineSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Offline sync worker starting")

        if (!SupabaseDataSync.isEnabled) {
            Log.d(TAG, "Sync not enabled — user not authenticated. Skipping.")
            return Result.success()
        }

        val pendingCount = SyncQueueManager.queue.value.size
        if (pendingCount == 0) {
            Log.d(TAG, "No pending sync items — nothing to do")
            return Result.success()
        }

        Log.i(TAG, "Draining $pendingCount pending sync items")
        return try {
            val synced = SyncQueueManager.drain()
            val remaining = SyncQueueManager.queue.value.size

            if (remaining > 0) {
                Log.w(TAG, "Sync partial: $synced succeeded, $remaining remaining — will retry")
                Result.retry()
            } else {
                Log.i(TAG, "Sync complete: all $synced items synced successfully")
                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Offline sync failed: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "rockscout_offline_sync"
        const val WORK_NAME_NOW = "rockscout_offline_sync_now"
        private const val TAG = "OfflineSyncWorker"
    }
}
