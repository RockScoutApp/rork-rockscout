package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Schedules and manages the three periodic background workers:
 * - [UpdateCheckWorker] — checks for app updates every 6 hours (requires network)
 * - [ProximityCheckWorker] — checks for nearby dig sites every 10 minutes (requires location)
 * - [GemShowRefreshWorker] — refreshes the gem show list near the end of each month
 * - [NotificationSummaryWorker] — checks for engagement + new-posts summaries every 1 hour
 * - [WeatherAlertWorker] — self-rescheduling chain that polls NWS API every 3 minutes
 * - [AuroraAlertWorker] — self-rescheduling chain that polls NOAA SWPC Kp index every 3 minutes
 *
 * Call [schedule] from Application.onCreate().
 */
object WorkScheduler {

    private const val TAG = "WorkScheduler"

    /** Schedule all periodic workers. Safe to call multiple times — uses KEEP policy. */
    fun schedule(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Update check — every 6 hours, requires network
        val updateRequest = PeriodicWorkRequestBuilder<UpdateCheckWorker>(
            6, TimeUnit.HOURS,
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .setInitialDelay(30, TimeUnit.SECONDS)  // let app finish starting
            .build()

        workManager.enqueueUniquePeriodicWork(
            UpdateCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            updateRequest,
        )

        // Proximity check — every 10 minutes for faster nearby-spot alerts
        val proximityRequest = PeriodicWorkRequestBuilder<ProximityCheckWorker>(
            10, TimeUnit.MINUTES,
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build(),
            )
            .setInitialDelay(5, TimeUnit.SECONDS)  // fetch location almost immediately
            .build()

        workManager.enqueueUniquePeriodicWork(
            ProximityCheckWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            proximityRequest,
        )

        // Gem show refresh — runs roughly every 30 days, scheduled to fire near
        // the end of each month so the show list updates automatically.
        val daysUntilMonthEnd = computeDaysUntilMonthEnd()
        val gemShowRequest = PeriodicWorkRequestBuilder<GemShowRefreshWorker>(
            30, TimeUnit.DAYS,
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .setInitialDelay(daysUntilMonthEnd, TimeUnit.DAYS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            GemShowRefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            gemShowRequest,
        )

        // Notification summary — every 1 hour, fires engagement summary
        // (likes/comments/replies) + new-posts summary, and runs the 30-day
        // auto-deletion sweep. Instant push types are fired by
        // NotificationRepository at creation time, so the worker only
        // handles the hourly summaries.
        val notificationRequest = PeriodicWorkRequestBuilder<NotificationSummaryWorker>(
            1, TimeUnit.HOURS,
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .setInitialDelay(60, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            NotificationSummaryWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            notificationRequest,
        )

        // Weather alert check — self-rescheduling chain that polls the NWS API
        // every 3 minutes for near-instant severe-weather alerts. Independent of
        // the general location monitoring toggle — the worker fetches the device
        // location on its own for weather alerts. The chain starts here and each
        // run schedules the next; it stops when the weather alerts toggle is off.
        // Only (re)start the chain if the toggle is on, and use KEEP so a cold
        // start doesn't reset the 3-minute timer of an already-pending check.
        if (PersistenceManager.isWeatherAlertsEnabled()) {
            ensureWeatherChain(context)
        }

        // Aurora alert check — self-rescheduling chain that polls the NOAA SWPC
        // Kp index every 3 minutes. Only fires at night when aurora is visible.
        // Independent of NWS weather alerts. Starts here and each run schedules
        // the next; it stops when the aurora alerts toggle is off.
        // Only (re)start the chain if the toggle is on, and use KEEP so a cold
        // start doesn't reset the 3-minute timer of an already-pending check.
        if (PersistenceManager.isAuroraAlertsEnabled()) {
            ensureAuroraChain(context)
        }

        Log.d(TAG, "Scheduled update check (6h), proximity check (10min), notification summary (1h), gem show refresh (monthly, in ${daysUntilMonthEnd}d), weather alerts (${WeatherAlertWorker.CHECK_INTERVAL_MINUTES}min chain), aurora alerts (${AuroraAlertWorker.CHECK_INTERVAL_MINUTES}min chain)")
    }

    /**
     * Immediately runs a single proximity check so nearby-spot alerts reflect
     * the user's latest location right after they save profile changes (or on
     * cold start), instead of waiting up to the next 10-minute periodic tick.
     * Uses REPLACE policy so rapid successive saves collapse into one run.
     */
    fun runProximityCheckNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<ProximityCheckWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build(),
            )
            .setInitialDelay(2, TimeUnit.SECONDS)  // let prefs settle
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "rockscout_proximity_check_now",
            ExistingWorkPolicy.REPLACE,
            request,
        )
        Log.d(TAG, "Scheduled immediate proximity check")
    }

    /**
     * Immediately runs a single weather alert check so the user gets any active
     * severe-weather warnings right after they enable the toggle, instead of
     * waiting up to the next 3-minute chain tick.
     */
    fun runWeatherCheckNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .setInitialDelay(2, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WeatherAlertWorker.WORK_NAME_NOW,
            ExistingWorkPolicy.REPLACE,
            request,
        )
        Log.d(TAG, "Scheduled immediate weather alert check")
    }

    /**
     * Starts the self-rescheduling weather alert chain from scratch (REPLACE).
     * Use this when the user explicitly enables the toggle — it should start
     * fresh regardless of any stale pending work.
     * Called from the Social Settings / Severe Weather toggle handlers.
     */
    fun scheduleWeatherChain(context: Context) {
        WeatherAlertWorker.scheduleNext(context, WeatherAlertWorker.CHECK_INTERVAL_MINUTES)
        Log.d(TAG, "Started weather alert chain (${WeatherAlertWorker.CHECK_INTERVAL_MINUTES}min cycle)")
    }

    /**
     * Ensures the weather alert chain is running without resetting an already-
     * pending check. Uses KEEP policy so a cold start doesn't cancel and restart
     * the 3-minute timer. Called from [schedule] on app start.
     */
    fun ensureWeatherChain(context: Context) {
        WeatherAlertWorker.ensureChainRunning(context)
        Log.d(TAG, "Ensured weather alert chain is running (${WeatherAlertWorker.CHECK_INTERVAL_MINUTES}min cycle)")
    }

    /**
     * Cancels the self-rescheduling weather alert chain when the user turns off
     * the weather alerts toggle.
     */
    fun cancelWeatherChain(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WeatherAlertWorker.WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(WeatherAlertWorker.WORK_NAME_NOW)
        Log.d(TAG, "Cancelled weather alert chain")
    }

    /**
     * Starts the self-rescheduling aurora alert chain from scratch (REPLACE).
     * Use this when the user explicitly enables the toggle — it should start
     * fresh regardless of any stale pending work.
     * Called from the AuroraScreen toggle handler.
     */
    fun scheduleAuroraChain(context: Context) {
        AuroraAlertWorker.scheduleNext(context, AuroraAlertWorker.CHECK_INTERVAL_MINUTES)
        Log.d(TAG, "Started aurora alert chain (${AuroraAlertWorker.CHECK_INTERVAL_MINUTES}min cycle)")
    }

    /**
     * Ensures the aurora alert chain is running without resetting an already-
     * pending check. Uses KEEP policy so a cold start doesn't cancel and restart
     * the 3-minute timer. Called from [schedule] on app start.
     */
    fun ensureAuroraChain(context: Context) {
        AuroraAlertWorker.ensureChainRunning(context)
        Log.d(TAG, "Ensured aurora alert chain is running (${AuroraAlertWorker.CHECK_INTERVAL_MINUTES}min cycle)")
    }

    /**
     * Cancels the self-rescheduling aurora alert chain when the user turns off
     * the aurora alerts toggle.
     */
    fun cancelAuroraChain(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(AuroraAlertWorker.WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(AuroraAlertWorker.WORK_NAME_NOW)
        Log.d(TAG, "Cancelled aurora alert chain")
    }

    /**
     * Immediately runs a single aurora alert check so the user gets an
     * immediate alert if aurora is visible right now.
     */
    fun runAuroraCheckNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<AuroraAlertWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .setInitialDelay(2, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            AuroraAlertWorker.WORK_NAME_NOW,
            ExistingWorkPolicy.REPLACE,
            request,
        )
        Log.d(TAG, "Scheduled immediate aurora alert check")
    }

    /**
     * Schedules the nightly offline-database sync worker. Runs once a day with
     * an initial delay calibrated to the next 2:30 AM local time so it lands in
     * the small hours when the device is most likely idle and on the charger.
     *
     * Constraints:
     *  - **Charging** — never burns battery on a device that isn't plugged in.
     *  - **Device idle** — Android's "idle" window (typically 2–5 AM) so the
     *    sync doesn't compete with foreground use.
     *  - **Unmetered network (WiFi)** — never burns the user's cellular data in
     *    the background. If WiFi isn't available overnight the run is simply
     *    skipped and retried the next night.
     *
     * Safe to call multiple times — uses KEEP policy so the schedule isn't
     * reset on every app start. Call from [com.rork.rockscout.RockScoutApplication.onCreate]
     * only when [PersistenceManager.isNightlySyncEnabled] is true, and from the
     * Storage settings toggle when the user turns it on.
     */
    fun scheduleNightlySync(context: Context) {
        val initialDelayMinutes = computeMinutesUntilNextSync()

        val request = PeriodicWorkRequestBuilder<NightlySyncWorker>(
            1, TimeUnit.DAYS,
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .setRequiresCharging(true)
                    .setRequiresDeviceIdle(true)
                    .build(),
            )
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            NightlySyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
        Log.d(TAG, "Scheduled nightly sync (in $initialDelayMinutes min, then daily @ ~2:30 AM, charging+idle+WiFi only)")
    }

    /**
     * Cancels the nightly offline-database sync when the user turns the toggle
     * off in Storage settings.
     */
    fun cancelNightlySync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(NightlySyncWorker.WORK_NAME)
        Log.d(TAG, "Cancelled nightly sync")
    }

    /**
     * Minutes from now until the next 2:30 AM local time. Used as the initial
     * delay for the nightly sync so the first run lands in the small hours.
     */
    private fun computeMinutesUntilNextSync(): Long {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 2)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_MONTH, 1)
        }
        val diffMs = next.timeInMillis - now.timeInMillis
        return (diffMs / (60_000L)).coerceAtLeast(1L)
    }

    /** Days from now until the last day of the current month (minimum 1). */
    private fun computeDaysUntilMonthEnd(): Long {
        val now = Calendar.getInstance()
        val end = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (end.before(now)) {
            // Shouldn't happen, but guard anyway
            end.add(Calendar.MONTH, 1)
            end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        val diffMs = end.timeInMillis - now.timeInMillis
        val days = (diffMs / (24L * 60 * 60 * 1000)).coerceAtLeast(1L)
        return days
    }
}
