package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rork.rockscout.data.CustomGemShowStore
import java.util.Calendar

/**
 * Monthly worker that refreshes the gem & mineral show list.
 *
 * Runs once near the end of each month. Because the show list is currently a
 * curated static dataset (recurring annual shows), "refreshing" means:
 *  1. Re-sorting shows so the nearest upcoming month appears first.
 *  2. Recording a refresh timestamp so the UI can display "Updated <date>".
 *  3. Notifying the user that the show list has been refreshed for the new month.
 *
 * When a remote show feed is added in the future, this worker is the natural
 * place to fetch it and merge new entries into [GemShowData].
 */
class GemShowRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "rockscout_gem_show_refresh"
        private const val TAG = "GemShowRefreshWorker"
    }

    override suspend fun doWork(): Result {
        val context = applicationContext
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }
        val currentMonth1 = calendar.get(Calendar.MONTH) + 1

        // Ensure user-submitted custom shows are loaded so they merge with the static list.
        CustomGemShowStore.initialize()

        // Recompute the upcoming order for the current month.
        val upcoming = GemShowData.upcomingShows(currentMonth1)
        val totalCount = GemShowData.totalShowCount()
        Log.d(TAG, "Gem show refresh — $totalCount total shows, current month=$currentMonth1")

        // Persist refresh metadata so the UI can show "Updated <date>".
        val prefs = context.getSharedPreferences(GemShowData.PREFS_NAME, Context.MODE_PRIVATE)
        val refreshCount = prefs.getInt(GemShowData.KEY_REFRESH_COUNT, 0) + 1
        prefs.edit()
            .putLong(GemShowData.KEY_LAST_REFRESH_MS, now)
            .putInt(GemShowData.KEY_REFRESH_COUNT, refreshCount)
            .apply()

        // Notify the user that the show list has been refreshed.
        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, java.util.Locale.US) ?: "this month"
        NotificationHelper.showProximityNotification(
            context = context,
            locationId = "gem-show-refresh",
            title = "Gem show list updated",
            message = "Your gem & mineral show list has been refreshed for $monthName with $totalCount shows. " +
                "Tap to browse upcoming shows near you.",
        )

        return Result.success()
    }
}
