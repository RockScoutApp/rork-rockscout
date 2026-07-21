package com.rork.rockscout.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Periodic worker that checks whether a newer app version is available.
 *
 * Delegates the actual version comparison, in-app state publication, and push
 * notification to [UpdateManager.checkForUpdate] so the UI "Update Now" button
 * and the push notification stay in sync from a single code path.
 *
 * Runs roughly every 6 hours via WorkManager.
 */
class UpdateCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            UpdateManager.checkForUpdate(applicationContext)
            // Give the async check a moment to complete before reporting success
            // so WorkManager doesn't immediately re-enqueue a retry.
            kotlinx.coroutines.delay(2000)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "rockscout_update_check"
    }
}
