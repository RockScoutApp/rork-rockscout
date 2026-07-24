package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Self-rescheduling worker that checks the planetary Kp index every ~15 minutes
 * and posts a local notification when aurora is likely visible from the user's
 * latitude. Only fires at night (between sunset and sunrise) and uses a per-Kp-level
 * cooldown so the same storm doesn't trigger repeated notifications.
 *
 * Mirrors the [WeatherAlertWorker] self-rescheduling chain pattern.
 */
class AuroraAlertWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "rockscout_aurora_alerts_chain"
        const val WORK_NAME_NOW = "rockscout_aurora_alerts_now"

        private const val TAG = "AuroraAlertWorker"
        private const val PREFS_NAME = "aurora_alert_prefs"
        private const val KEY_LAST_NOTIFIED_KP = "last_notified_kp_level"
        private const val KEY_LAST_NOTIFIED_MS = "last_notified_ms"
        private const val COOLDOWN_MS = 3 * 60 * 60 * 1000L // 3 hours per Kp level

        /** Check interval — 15 minutes (Kp changes slower than NWS alerts). */
        const val CHECK_INTERVAL_MINUTES = 15L

        /**
         * Schedule the next aurora check in the self-rescheduling chain.
         */
        fun scheduleNext(context: Context, delayMinutes: Long = CHECK_INTERVAL_MINUTES) {
            val request = OneTimeWorkRequestBuilder<AuroraAlertWorker>()
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                )
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request,
            )
        }
    }

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun doWork(): Result {
        val context = applicationContext

        if (!PersistenceManager.isAuroraAlertsEnabled()) {
            Log.d(TAG, "Aurora alerts are off — stopping chain")
            return Result.success()
        }

        if (!LocationFetcher.hasPermission(context)) {
            Log.d(TAG, "Location permission not granted — skipping")
            scheduleNext(context)
            return Result.success()
        }

        // Only fire notifications at night (aurora is only visible in darkness)
        if (!isNighttime(context)) {
            Log.d(TAG, "Daytime — aurora not visible, skipping notification check")
            scheduleNext(context)
            return Result.success()
        }

        // Get current location
        val location = try {
            LocationFetcher.fetch(context)
        } catch (e: Exception) {
            Log.w(TAG, "Location fetch failed: ${e.message}")
            null
        }

        if (location == null) {
            Log.d(TAG, "Could not get location — will retry next cycle")
            scheduleNext(context)
            return Result.success()
        }

        // Fetch current Kp
        val kp = fetchCurrentKp() ?: run {
            Log.d(TAG, "Failed to fetch Kp — will retry next cycle")
            scheduleNext(context)
            return Result.retry()
        }

        val threshold = AuroraRepository.kpThresholdForLatitude(location.latitude)
        if (kp < threshold) {
            Log.d(TAG, "Kp $kp below threshold $threshold for lat ${location.latitude} — no alert")
            scheduleNext(context)
            return Result.success()
        }

        // Check cooldown
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val lastNotifiedMs = prefs.getLong(KEY_LAST_NOTIFIED_MS, 0L)
        val lastNotifiedKp = prefs.getLong(KEY_LAST_NOTIFIED_KP, 0L).toInt()

        // If the Kp level hasn't changed significantly and we're within cooldown, skip
        if (now - lastNotifiedMs < COOLDOWN_MS && kp.toInt() == lastNotifiedKp) {
            Log.d(TAG, "Aurora alert on cooldown (Kp $kp, last notified ${lastNotifiedKp}) — skipping")
            scheduleNext(context)
            return Result.success()
        }

        // Fire notification
        val stormScale = AuroraRepository.stormScaleLabel(kp)
        val title = "\uD83C\uDF0C Aurora alert: Kp $kp ($stormScale)"
        val message = buildString {
            append("Aurora may be visible from your location tonight. ")
            append("Current Kp is $kp — threshold for your latitude is ${String.format("%.1f", threshold)}. ")
            append("Look north toward the horizon!")
        }

        NotificationHelper.showAuroraAlertNotification(
            context = context,
            title = title,
            message = message,
        )

        prefs.edit()
            .putLong(KEY_LAST_NOTIFIED_MS, now)
            .putLong(KEY_LAST_NOTIFIED_KP, kp.toLong())
            .apply()

        Log.d(TAG, "Aurora alert sent: Kp $kp, threshold $threshold")
        scheduleNext(context)
        return Result.success()
    }

    /**
     * Fetch the latest Kp value from NOAA SWPC.
     */
    private suspend fun fetchCurrentKp(): Double? {
        return try {
            val response: HttpResponse = NetworkClient.client.get(
                "https://services.swpc.noaa.gov/json/planetary_k_index_1m.json",
            ) {
                headers.append("User-Agent", "RockScout aurora alerts (contact@rork.app)")
            }
            if (!response.status.isSuccess()) return null
            val raw: String = response.body()
            val root = json.parseToJsonElement(raw).jsonObject
            val arr: JsonArray = root["KpIndexList"] as? JsonArray ?: return null
            arr.lastOrNull()?.jsonObject?.get("Kp")?.jsonPrimitive?.doubleOrNull
        } catch (e: Exception) {
            Log.w(TAG, "Kp fetch failed: ${e.message}")
            null
        }
    }

    /**
     * Check if it's currently nighttime at the user's location.
     * Uses a simple approximation: after 6 PM or before 6 AM local time.
     * A more precise check would use sunrise/sunset, but this is sufficient
     * for notification gating — the user can see aurora roughly between
     * civil dusk and dawn.
     */
    private fun isNighttime(context: Context): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour >= 18 || hour < 6
    }
}
