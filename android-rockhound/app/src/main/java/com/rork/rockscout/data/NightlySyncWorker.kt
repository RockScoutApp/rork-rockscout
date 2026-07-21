package com.rork.rockscout.data

import android.content.Context
import android.os.BatteryManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import kotlinx.coroutines.Job

/**
 * Periodic worker that keeps the offline specimen + guide image cache current
 * by re-running the bulk prefetch while the device is **charging and idle**,
 * typically overnight.
 *
 * Scheduled by [WorkScheduler.scheduleNightlySync] when the user enables the
 * "Auto-sync offline database while charging at night" toggle in Storage
 * settings. The WorkManager constraints guarantee:
 *  - `setRequiresCharging(true)` — only runs on charger
 *  - `setRequiresDeviceIdle(true)` — only runs when the device is idle (night)
 *  - `setRequiredNetworkType(NetworkType.UNMETERED)` — only on WiFi / free WiFi
 *
 * Because the Coil disk cache already skips URLs it has, a nightly re-run only
 * downloads images that are new or were evicted since the last sync — typically
 * a small fraction of the full ~4 GB set. If the cache is already current, the
 * run completes in seconds and posts a quiet "up to date" notification.
 *
 * On completion it:
 *  - Updates [PersistenceManager.saveNightlySyncLastRun] so the settings UI can
 *    show "Last synced N days ago".
 *  - Refreshes the persisted [PersistenceManager.BulkDownloadState] so the
 *    BulkDownloadCard reflects the freshly topped-up cache.
 *  - Posts a low-priority notification via [NotificationHelper.showOfflineSyncCompleteNotification].
 */
class NightlySyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        Log.d(TAG, "Nightly sync starting")

        // Belt-and-suspenders: even though WorkManager constraints require
        // charging, re-check the battery state here so a mis-scheduled run on
        // a flaky OEM doesn't burn cellular battery.
        if (!isCharging(context)) {
            Log.d(TAG, "Device not charging — skipping nightly sync")
            return Result.success()
        }

        return runCatching {
            NotificationHelper.createChannels(context)

            // Snapshot the disk cache size before the run so we can report how
            // many new/refreshed images were written.
            val loader = SingletonImageLoader.get(context)
            val disk: DiskCache? = loader.diskCache
            val bytesBefore = disk?.size ?: 0L

            // Collect the full URL set once so we can compute the total count
            // for the progress/state record, matching bulkPrefetchAll's logic.
            val allUrls = runCatching {
                (SpecimenImages.urls.values.flatten() + EducationalImages.all)
                    .distinct()
                    .filter { it.isNotBlank() && it.startsWith("http") }
            }.getOrDefault(emptyList())

            if (allUrls.isEmpty()) {
                Log.d(TAG, "No URLs to sync — skipping")
                return@runCatching Result.success()
            }

            // Drive the existing bulk prefetch. shouldCancel never returns
            // true here (the worker is already background); WorkManager will
            // signal cancellation through coroutine cancellation if needed.
            var lastFinished = 0
            var lastTotal = allUrls.size
            val job: Job = ImagePrefetcher.bulkPrefetchAll(
                context = context,
                onProgress = { finished, total, _ ->
                    lastFinished = finished
                    lastTotal = total
                },
                shouldCancel = { false },
            )
            job.join()

            val bytesAfter = disk?.size ?: 0L
            val totalCached = lastFinished.coerceAtLeast(0)
            // Heuristic for "new images this run": compare disk size delta to
            // an average image size. Only used for the notification copy; never
            // shown as an exact count.
            val newBytes = (bytesAfter - bytesBefore).coerceAtLeast(0L)
            val approxNewImages = if (newBytes > 0) {
                (newBytes / (1_100_000L)).toInt().coerceAtLeast(0)
            } else 0

            // Record the successful run + refresh the bulk-download state so the
            // BulkDownloadCard shows the freshly topped-up cache the next time
            // the user opens it.
            PersistenceManager.saveNightlySyncLastRun(System.currentTimeMillis())
            val refreshed = PersistenceManager.BulkDownloadState(
                total = lastTotal,
                finished = totalCached,
                bytes = bytesAfter,
                done = totalCached >= lastTotal && lastTotal > 0,
                running = false,
                cancelled = false,
                startedAt = 0L,
                finishedAt = System.currentTimeMillis(),
            )
            PersistenceManager.saveBulkDownloadState(refreshed)

            NotificationHelper.showOfflineSyncCompleteNotification(
                context = context,
                newImages = approxNewImages,
                totalCached = totalCached,
            )

            Log.d(TAG, "Nightly sync complete — $totalCached/$lastTotal cached, ~$approxNewImages new, ${bytesAfter} bytes total")
            Result.success()
        }.onFailure { e ->
            Log.w(TAG, "Nightly sync failed", e)
        }.getOrDefault(Result.retry())
    }

    /** True if the device is currently on AC power (charging or full). */
    private fun isCharging(context: Context): Boolean {
        return runCatching {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            bm?.isCharging ?: true
        }.getOrDefault(true)
    }

    companion object {
        const val WORK_NAME = "rockscout_nightly_sync"
        private const val TAG = "NightlySyncWorker"
    }
}
