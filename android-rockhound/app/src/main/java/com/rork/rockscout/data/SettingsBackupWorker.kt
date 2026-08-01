package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Periodic worker that backs up the user's full SharedPreferences blob to
 * Supabase via [SettingsBackupApi]. Runs every 12 hours when the device
 * has network connectivity. Skips silently if the user is not signed in.
 *
 * A manual one-shot backup can also be enqueued via [enqueueNow] from the
 * Profile/Settings "Back Up Data Now" button.
 *
 * Debounce: both the periodic worker and the on-backgrounding trigger check
 * [KEY_LAST_BACKUP_AT] — if the last backup was less than 1 hour ago, the
 * run is a no-op. This prevents redundant uploads.
 */
class SettingsBackupWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME_PERIODIC = "rockscout_settings_backup_periodic"
        const val WORK_NAME_NOW = "rockscout_settings_backup_now"
        private const val TAG = "SettingsBackupWorker"
        private const val PREFS_NAME = "settings_backup_prefs"
        private const val KEY_LAST_BACKUP_AT = "last_settings_backup_at"
        private const val DEBOUNCE_MS = 60 * 60 * 1000L // 1 hour

        /** Schedule the 12-hour periodic backup. Uses KEEP policy. */
        fun schedulePeriodic(context: Context) {
            val request = PeriodicWorkRequestBuilder<SettingsBackupWorker>(
                12, TimeUnit.HOURS,
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                )
                .setInitialDelay(5, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_PERIODIC,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
            Log.d(TAG, "Scheduled periodic settings backup (every 12h)")
        }

        /** Enqueue an immediate one-shot backup (manual button or on-backgrounding). */
        fun enqueueNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<SettingsBackupWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                )
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_NOW,
                androidx.work.ExistingWorkPolicy.REPLACE,
                request,
            )
            Log.d(TAG, "Enqueued immediate settings backup")
        }

        /** Check if a backup should run (debounce check). Called from MainActivity.onStop. */
        fun shouldBackup(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val lastBackup = prefs.getLong(KEY_LAST_BACKUP_AT, 0L)
            return System.currentTimeMillis() - lastBackup > DEBOUNCE_MS
        }

        /** Timestamp of the last successful backup, or 0 if never. */
        fun lastBackupAt(context: Context): Long {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(KEY_LAST_BACKUP_AT, 0L)
        }
    }

    override suspend fun doWork(): Result {
        val userId = AuthRepository.instance.currentUserId
        if (userId.isNullOrBlank()) {
            Log.d(TAG, "Skipping backup — user not signed in")
            return Result.success()
        }

        // Need the user's Supabase access token for RLS-authenticated writes.
        val accessToken = LocalDataStore.getString(LocalDataStore.KEY_SUPABASE_ACCESS_TOKEN)
        if (accessToken.isNullOrBlank()) {
            Log.d(TAG, "Skipping backup — no access token available")
            return Result.success()
        }

        // Debounce check
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastBackup = prefs.getLong(KEY_LAST_BACKUP_AT, 0L)
        if (System.currentTimeMillis() - lastBackup < DEBOUNCE_MS) {
            Log.d(TAG, "Skipping backup — last backup was <1h ago")
            return Result.success()
        }

        return try {
            val settingsJson = PersistenceManager.exportAllSettingsAsJson()
            val result = SettingsBackupApi.backupSettings(userId, settingsJson, accessToken)
            if (result.isSuccess) {
                prefs.edit().putLong(KEY_LAST_BACKUP_AT, System.currentTimeMillis()).apply()
                Log.d(TAG, "Settings backup complete (${settingsJson.length} chars)")
                Result.success()
            } else {
                Log.w(TAG, "Settings backup failed: ${result.exceptionOrNull()?.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Settings backup exception", e)
            Result.retry()
        }
    }
}
