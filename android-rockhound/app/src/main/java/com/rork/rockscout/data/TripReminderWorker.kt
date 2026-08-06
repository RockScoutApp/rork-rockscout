package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar

/**
 * Checks for trips scheduled for tomorrow and shows a local notification
 * reminding the user to prepare. Complements the server-side pg_cron
 * `check_tomorrow_trips()` function, which sends web push notifications
 * — this worker handles Android local notifications for users who may not
 * have web push subscriptions.
 *
 * Scheduled daily at ~8 AM local time by [WorkScheduler].
 * Runs only when the user is signed in and has non-archived trips.
 */
class TripReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = AuthRepository.instance.currentUserId ?: run {
            Log.d(TAG, "Not signed in — skipping trip reminder check")
            return Result.success()
        }

        return runCatching {
            val context = applicationContext
            val tomorrow = getTomorrowDateString()

            // Check local trips for tomorrow (non-archived)
            val trips = AppRepository.instance.trips.value
                .filter { !it.isArchived }
                .filter { dateMatchesTomorrow(it.date, tomorrow) }

            if (trips.isEmpty()) {
                Log.d(TAG, "No trips scheduled for tomorrow ($tomorrow) — skipping")
                return@runCatching Result.success()
            }

            NotificationHelper.createChannels(context)

            for (trip in trips) {
                val stopsText = if (trip.stops.isNotEmpty()) {
                    trip.stops.joinToString(", ") { it.locationName }
                } else {
                    "No stops planned"
                }

                NotificationHelper.showTripReminderNotification(
                    context = context,
                    tripId = trip.id,
                    tripName = trip.name,
                    stopsText = stopsText,
                    gearCount = trip.gearChecklist.size,
                )
            }

            Log.i(TAG, "Sent ${trips.size} trip reminder notification(s) for tomorrow ($tomorrow)")
            Result.success()
        }.onFailure {
            Log.w(TAG, "Trip reminder worker failed", it)
        }.getOrDefault(Result.success())
    }

    /** Format tomorrow's date as "yyyy-MM-dd" in the user's effective timezone. */
    private fun getTomorrowDateString(): String {
        val tz = UserTimezoneProvider.effectiveTimeZone.value
        val cal = Calendar.getInstance(tz)
        cal.add(Calendar.DAY_OF_MONTH, 1)
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).apply {
            timeZone = tz
        }.format(cal.time)
    }

    /** Check if a trip's epoch-millis date falls on the given date string. */
    private fun dateMatchesTomorrow(tripDateMillis: Long, tomorrowStr: String): Boolean {
        val tz = UserTimezoneProvider.effectiveTimeZone.value
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).apply {
            timeZone = tz
        }
        val tripDateStr = sdf.format(java.util.Date(tripDateMillis))
        return tripDateStr == tomorrowStr
    }

    companion object {
        const val WORK_NAME = "rockscout_trip_reminder"
        private const val TAG = "TripReminderWorker"
    }
}
