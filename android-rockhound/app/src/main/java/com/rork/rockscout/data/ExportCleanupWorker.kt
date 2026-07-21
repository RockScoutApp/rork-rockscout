package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Periodic worker that deletes generated export files (ZIP, PDF, CSV) left in
 * the app's cache directory once they are older than [MAX_AGE_MILLIS] (24h).
 *
 * Export files are written to per-type subfolders of `cacheDir`:
 *  - `zip_exports/`     — `RockScout_Collection_<stamp>.zip` from
 *    [CollectionZipExporter] (also wipes `zip_staging/` leftovers).
 *  - `csv_exports/`     — `RockScout_Collection_<stamp>.csv` from
 *    [CollectionCsvExporter].
 *  - `pdf_exports/`     — PDFs saved to cache by the PDF exporter's share path.
 *
 * These files are only needed for the few seconds between generation and the
 * user picking a share/save target in the system sheet. Anything still lying
 * around after 24h is almost certainly abandoned, so we sweep it to keep the
 * cache from growing unbounded on devices that rarely reboot (Android already
 * trims `cacheDir` under disk pressure, but this gives us a deterministic,
 * app-controlled cadence).
 *
 * Runs every 6 hours via WorkManager. A one-shot sweep is also enqueued on app
 * startup via [runNow] so a cold launch cleans up anything left from a prior
 * session within a few seconds.
 */
class ExportCleanupWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val deleted = sweep(applicationContext)
            Log.d(TAG, "Swept export cache: deleted $deleted file(s) older than ${MAX_AGE_HOURS}h")
            Result.success()
        } catch (e: Throwable) {
            Log.w(TAG, "Export cache sweep failed", e)
            Result.success() // non-fatal — never block other workers with a retry loop
        }
    }

    companion object {
        private const val TAG = "ExportCleanupWorker"
        const val WORK_NAME = "rockscout_export_cleanup"
        const val WORK_NAME_NOW = "rockscout_export_cleanup_now"

        /** Export files older than this are deleted. */
        const val MAX_AGE_HOURS = 24L
        const val MAX_AGE_MILLIS = MAX_AGE_HOURS * 60L * 60L * 1000L

        /** Subfolders of `cacheDir` that hold generated export files. */
        private val EXPORT_DIRS = listOf(
            "zip_exports",
            "zip_staging",
            "csv_exports",
            "pdf_exports",
        )

        /**
         * Delete every file under the known export cache subfolders whose
         * `lastModified()` is older than [MAX_AGE_MILLIS]. Empty subfolders are
         * removed too. Returns the number of files deleted.
         *
         * Best-effort: each file deletion is wrapped in its own try/catch so a
         * single locked/missing file can't abort the whole sweep.
         */
        fun sweep(context: Context): Int {
            val cutoff = System.currentTimeMillis() - MAX_AGE_MILLIS
            var deleted = 0
            for (name in EXPORT_DIRS) {
                val dir = File(context.cacheDir, name)
                if (!dir.exists()) continue
                dir.walkBottomUp().forEach { file ->
                    if (file == dir) return@forEach
                    try {
                        if (file.isFile && file.lastModified() < cutoff) {
                            if (file.delete()) deleted++
                        } else if (file.isDirectory && file.listFiles()?.isEmpty() == true) {
                            // Drop empty staging subfolders even if young — they're
                            // transient by design (staging is wiped at end of export).
                            file.delete()
                        }
                    } catch (_: Throwable) {
                        // ignore this file and move on
                    }
                }
            }
            return deleted
        }

        /** Schedule the recurring 6-hourly sweep. Idempotent (KEEP policy). */
        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<ExportCleanupWorker>(
                6, TimeUnit.HOURS,
            ).setInitialDelay(5, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        /** Enqueue a one-shot sweep to run shortly after app start. */
        fun runNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<ExportCleanupWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_NOW,
                androidx.work.ExistingWorkPolicy.KEEP,
                request,
            )
        }
    }
}
