package com.rork.rockscout.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.TimeUnit

/**
 * Self-rescheduling worker that polls the National Weather Service (NWS) API for
 * active severe-weather alerts at the user's current location and posts a local
 * notification for each new alert matching the monitored event types.
 *
 * Uses the free, no-auth NWS API: https://api.weather.gov/alerts/active?point={lat},{lon}
 *
 * **Independent of location monitoring** — runs whenever the user has the weather
 * alerts toggle on and has granted location permission. The worker fetches the
 * device's current location on its own specifically for weather alerts, so users
 * don't need to enable the general location monitoring toggle.
 *
 * Self-reschedules every 3 minutes (instead of using a 15-minute periodic worker)
 * so alerts are delivered near-instantly — closer to NOAA weather radio speed.
 * The chain is self-perpetuating: each run schedules the next, and stops naturally
 * when the weather alerts toggle is turned off (the worker returns without
 * rescheduling).
 *
 * Uses a per-alert-ID cooldown so the same alert doesn't trigger repeated
 * notifications during its lifetime.
 */
class WeatherAlertWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "rockscout_weather_alerts_chain"
        const val WORK_NAME_NOW = "rockscout_weather_alerts_now"

        private const val TAG = "WeatherAlertWorker"
        private const val PREFS_NAME = "weather_alert_prefs"
        private const val KEY_LAST_NOTIFIED_PREFIX = "alert_notified_"
        private const val KEY_LAST_CLEANUP = "last_cleanup"
        private const val COOLDOWN_MS = 4 * 60 * 60 * 1000L  // 4 hours per alert
        private const val CLEANUP_INTERVAL_MS = 24 * 60 * 60 * 1000L  // 24 hours

        /** Interval between weather alert checks — 3 minutes for near-instant alerts. */
        const val CHECK_INTERVAL_MINUTES = 3L

        /**
         * The NWS alert event names that trigger a notification.
         *
         * IMPORTANT: These must exactly match the `event` field in the NWS CAP/GeoJSON
         * alert properties. Two previously-listed names were invalid and would never match:
         *   - "Marine Warning"  → correct name is "Special Marine Warning"
         *   - "Thunderstorm Warning" → not a real NWS event; "Severe Thunderstorm Warning" covers it
         *
         * The full set now covers all severe-weather hazards relevant to outdoor rockhounders:
         * convective storms, floods, winter weather, tropical systems, fire/weather, and
         * visibility hazards — so every individual alert type fires a notification instantly.
         */
        private val MONITORED_EVENTS = setOf(
            // Convective storms
            "Severe Thunderstorm Warning",
            "Severe Thunderstorm Watch",
            "Tornado Warning",
            "Tornado Watch",
            "Special Marine Warning",
            // Tropical systems
            "Hurricane Warning",
            "Hurricane Watch",
            "Typhoon Warning",
            "Typhoon Watch",
            "Tropical Storm Warning",
            "Tropical Storm Watch",
            // Tsunami
            "Tsunami Warning",
            "Tsunami Watch",
            // Floods
            "Flash Flood Warning",
            "Flash Flood Watch",
            "Flood Warning",
            "Flood Watch",
            // Wind
            "High Wind Warning",
            "High Wind Watch",
            "Extreme Wind Warning",
            "Gale Warning",
            "Gale Watch",
            // Heat & cold
            "Extreme Heat Warning",
            "Extreme Heat Watch",
            "Extreme Cold Warning",
            "Extreme Cold Watch",
            "Freeze Warning",
            "Freeze Watch",
            // Winter storms
            "Blizzard Warning",
            "Blizzard Watch",
            "Snow Squall Warning",
            "Winter Storm Warning",
            "Winter Storm Watch",
            "Ice Storm Warning",
            // Fire & smoke
            "Red Flag Warning",
            "Fire Weather Watch",
            "Fire Warning",
            "Air Quality Alert",
            "Dense Smoke Advisory",
            // Dust & visibility
            "Dust Storm Warning",
            "Blowing Dust Advisory",
            "Dense Fog Advisory",
        )

        /** Emoji icon for each alert type, used in the notification title. */
        private val EVENT_EMOJI = mapOf(
            // Convective
            "Severe Thunderstorm Warning" to "\u26A1\uFE0F",
            "Severe Thunderstorm Watch" to "\u26A1\uFE0F",
            "Tornado Warning" to "\uD83C\uDF2A\uFE0F",
            "Tornado Watch" to "\uD83C\uDF2A\uFE0F",
            "Special Marine Warning" to "\u26A1\uFE0F",
            // Tropical
            "Hurricane Warning" to "\uD83C\uDF00\uFE0F",
            "Hurricane Watch" to "\uD83C\uDF00\uFE0F",
            "Typhoon Warning" to "\uD83C\uDF00\uFE0F",
            "Typhoon Watch" to "\uD83C\uDF00\uFE0F",
            "Tropical Storm Warning" to "\uD83C\uDF00\uFE0F",
            "Tropical Storm Watch" to "\uD83C\uDF00\uFE0F",
            // Tsunami
            "Tsunami Warning" to "\uD83C\uDF30\uFE0F",
            "Tsunami Watch" to "\uD83C\uDF30\uFE0F",
            // Flood
            "Flash Flood Warning" to "\uD83C\uDF26\uFE0F",
            "Flash Flood Watch" to "\uD83C\uDF26\uFE0F",
            "Flood Warning" to "\uD83C\uDF27\uFE0F",
            "Flood Watch" to "\uD83C\uDF27\uFE0F",
            // Wind
            "High Wind Warning" to "\uD83C\uDF2C\uFE0F",
            "High Wind Watch" to "\uD83C\uDF2C\uFE0F",
            "Extreme Wind Warning" to "\uD83C\uDF2C\uFE0F",
            "Gale Warning" to "\uD83C\uDF2C\uFE0F",
            "Gale Watch" to "\uD83C\uDF2C\uFE0F",
            // Heat & cold
            "Extreme Heat Warning" to "\uD83C\uDF21\uFE0F",
            "Extreme Heat Watch" to "\uD83C\uDF21\uFE0F",
            "Extreme Cold Warning" to "\u2744\uFE0F",
            "Extreme Cold Watch" to "\u2744\uFE0F",
            "Freeze Warning" to "\u2744\uFE0F",
            "Freeze Watch" to "\u2744\uFE0F",
            // Winter
            "Blizzard Warning" to "\uD83C\uDF28\uFE0F",
            "Blizzard Watch" to "\uD83C\uDF28\uFE0F",
            "Snow Squall Warning" to "\uD83C\uDF28\uFE0F",
            "Winter Storm Warning" to "\uD83C\uDF28\uFE0F",
            "Winter Storm Watch" to "\uD83C\uDF28\uFE0F",
            "Ice Storm Warning" to "\u2744\uFE0F",
            // Fire & smoke
            "Red Flag Warning" to "\uD83D\uDD25",
            "Fire Weather Watch" to "\uD83D\uDD25",
            "Fire Warning" to "\uD83D\uDD25",
            "Air Quality Alert" to "\uD83C\uDF2B\uFE0F",
            "Dense Smoke Advisory" to "\uD83C\uDF6A",
            // Dust & visibility
            "Dust Storm Warning" to "\uD83C\uDF2B\uFE0F",
            "Blowing Dust Advisory" to "\uD83C\uDF2B\uFE0F",
            "Dense Fog Advisory" to "\uD83C\uDF2B\uFE0F",
        )

        /**
         * Schedule the next weather alert check in the self-rescheduling chain.
         * Called at the end of every worker run to keep the 3-minute cycle going.
         *
         * Requires network connectivity — if the phone has no signal, WorkManager
         * defers the check until connectivity returns, at which point it fires
         * immediately. This ensures alerts are delivered as close to instantaneous
         * as possible when signal is available, without wasting battery on
         * doomed HTTP attempts during offline periods.
         */
        fun scheduleNext(context: Context, delayMinutes: Long = CHECK_INTERVAL_MINUTES) {
            val request = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
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

        /**
         * Ensure the chain is running without resetting an already-pending check.
         * Uses KEEP policy so a cold start doesn't cancel and restart the
         * 3-minute timer of a scheduled check. If no work is queued, this
         * enqueues one; if one is already queued, it's left untouched.
         * Called from [WorkScheduler.ensureWeatherChain] on app start.
         */
        fun ensureChainRunning(context: Context) {
            val request = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
                .setInitialDelay(CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                )
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.KEEP,
                request,
            )
        }
    }

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun doWork(): Result {
        val context = applicationContext

        // Require location permission (needed to fetch the user's area for alerts)
        if (!hasLocationPermission(context)) {
            Log.d(TAG, "Location permission not granted — skipping")
            return Result.success()
        }

        // Require weather alerts toggle on.
        // NOTE: This is independent of the general location monitoring toggle.
        // The worker fetches the device's location on its own for weather alerts.
        if (!PersistenceManager.isWeatherAlertsEnabled()) {
            Log.d(TAG, "Weather alerts are off — stopping chain")
            return Result.success()
        }

        // Get current location (fetched independently, not tied to location monitoring)
        val currentLocation = getCurrentLocation(context) ?: run {
            Log.d(TAG, "Could not get current location — will retry next cycle")
            scheduleNext(context)
            return Result.success()
        }

        // Fetch active alerts from NWS
        val alerts = fetchActiveAlerts(
            currentLocation.latitude,
            currentLocation.longitude,
        ) ?: run {
            Log.d(TAG, "Failed to fetch NWS alerts — will retry next cycle")
            scheduleNext(context)
            return Result.retry()
        }

        if (alerts.isEmpty()) {
            Log.d(TAG, "No active severe-weather alerts at current location")
            scheduleNext(context)
            return Result.success()
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        var notificationsSent = 0

        // Periodically clean up stale cooldown entries
        maybeCleanupPrefs(prefs, now)

        for (alert in alerts) {
            val cooldownKey = KEY_LAST_NOTIFIED_PREFIX + alert.id
            val lastNotified = prefs.getLong(cooldownKey, 0L)
            if (now - lastNotified < COOLDOWN_MS) {
                Log.d(TAG, "Alert ${alert.id} (${alert.event}) on cooldown — skipping")
                continue
            }

            val emoji = EVENT_EMOJI[alert.event] ?: "\u26A0\uFE0F"
            val title = "$emoji ${alert.event}"
            val message = buildString {
                append(alert.headline)
                if (alert.areaDesc.isNotBlank()) {
                    append("\n\nArea: ")
                    append(alert.areaDesc)
                }
            }

            NotificationHelper.showWeatherAlertNotification(
                context = context,
                alertId = alert.id,
                title = title,
                message = message,
            )
            prefs.edit().putLong(cooldownKey, now).apply()
            notificationsSent++
            Log.d(TAG, "Weather alert notification sent: ${alert.event} — ${alert.headline}")
        }

        Log.d(TAG, "Weather alert check complete — $notificationsSent notification(s) sent out of ${alerts.size} active alert(s)")

        // Self-reschedule for near-instant alert delivery (3-minute cycle)
        scheduleNext(context)
        return Result.success()
    }

    /**
     * Fetch active NWS alerts for the given coordinates and filter to only
     * the monitored severe-weather event types.
     *
     * NWS API: GET https://api.weather.gov/alerts/active?point={lat},{lon}
     * Returns a GeoJSON FeatureCollection where each feature's `properties`
     * contains the alert metadata (event, headline, description, areaDesc, id).
     *
     * Returns null on network/parse failure (caller should retry), or an
     * empty list if no monitored alerts are active.
     */
    private suspend fun fetchActiveAlerts(
        latitude: Double,
        longitude: Double,
    ): List<NwsAlert>? {
        // NWS API expects point as lat,lon (up to 4 decimal places is sufficient)
        val lat = String.format("%.4f", latitude)
        val lon = String.format("%.4f", longitude)
        val url = "https://api.weather.gov/alerts/active?point=$lat,$lon"

        return try {
            val response: HttpResponse = NetworkClient.client.get(url) {
                headers.append("User-Agent", "RockScout weather alerts (contact@rork.app)")
                headers.append("Accept", "application/geo+json")
            }

            if (!response.status.isSuccess()) {
                Log.w(TAG, "NWS API returned ${response.status.value}")
                null
            } else {
                val body: String = response.body()
                parseAlerts(body)
            }
        } catch (e: Exception) {
            Log.w(TAG, "NWS API request failed: ${e.message}")
            null
        }
    }

    /**
     * Parse the NWS GeoJSON alert response and extract monitored alert types.
     */
    private fun parseAlerts(jsonString: String): List<NwsAlert> {
        val root = runCatching { json.parseToJsonElement(jsonString).jsonObject }
            .getOrElse { return emptyList() }

        val features: JsonArray = root["features"]?.jsonArray ?: return emptyList()
        val result = mutableListOf<NwsAlert>()

        for (feature in features) {
            val props = feature.jsonObject["properties"]?.jsonObject ?: continue
            val event = props["event"]?.jsonPrimitive?.contentOrNull ?: continue
            if (event !in MONITORED_EVENTS) continue

            val id = props["id"]?.jsonPrimitive?.contentOrNull
                ?: feature.jsonObject["id"]?.jsonPrimitive?.contentOrNull
                ?: continue
            val headline = props["headline"]?.jsonPrimitive?.contentOrNull ?: event
            val areaDesc = props["areaDesc"]?.jsonPrimitive?.contentOrNull ?: ""

            result.add(NwsAlert(id = id, event = event, headline = headline, areaDesc = areaDesc))
        }

        return result
    }

    /**
     * Remove stale cooldown entries older than 24 hours so the prefs file
     * doesn't grow unbounded. Runs at most once per 24-hour period.
     */
    private fun maybeCleanupPrefs(
        prefs: android.content.SharedPreferences,
        now: Long,
    ) {
        val lastCleanup = prefs.getLong(KEY_LAST_CLEANUP, 0L)
        if (now - lastCleanup < CLEANUP_INTERVAL_MS) return

        val editor = prefs.edit()
        val all = prefs.all
        for ((key, value) in all) {
            if (key.startsWith(KEY_LAST_NOTIFIED_PREFIX) && value is Long) {
                if (now - value > CLEANUP_INTERVAL_MS) {
                    editor.remove(key)
                }
            }
        }
        editor.putLong(KEY_LAST_CLEANUP, now)
        editor.apply()
        Log.d(TAG, "Cleaned up stale weather alert cooldown entries")
    }

    /** Check fine + coarse location permissions. */
    private fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get the most recent device location. Delegates to [LocationFetcher]
     * so the worker benefits from the same cached-then-fresh logic.
     * This is independent of the general location monitoring toggle.
     */
    private suspend fun getCurrentLocation(context: Context): Location? {
        return try {
            LocationFetcher.fetch(context)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get location: ${e.message}")
            null
        }
    }
}

/** Simplified NWS alert data extracted from the GeoJSON response. */
@Serializable
private data class NwsAlert(
    val id: String,
    val event: String,
    val headline: String,
    val areaDesc: String,
)
