package com.rork.rockscout.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rork.rockscout.data.SeedData
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Periodic worker that checks the user's current location against all
 * dig sites, rock shops, and favorite spots. When the user is within
 * the proximity radius of a location they haven't been notified about
 * recently, a notification is posted.
 *
 * Only runs when the user has location monitoring enabled in their profile.
 * Skipped for premium users (proximity alerts are a free feature — NOT skipped).
 *
 * Uses a per-location cooldown (stored in SharedPreferences) so the same
 * spot doesn't trigger repeated notifications within the cooldown window.
 */
class ProximityCheckWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "rockscout_proximity_check"

        private const val PREFS_NAME = "proximity_prefs"
        private const val COOLDOWN_MS = 6 * 60 * 60 * 1000L  // 6 hours per location
        private const val PROXIMITY_RADIUS_METERS = 80_467.0  // ~50 miles for dig sites/shops
        private const val GEM_SHOW_RADIUS_METERS = 160_934.0  // ~100 miles for upcoming gem shows
        private const val GEM_SHOW_COOLDOWN_MS = 24 * 60 * 60 * 1000L  // 24 hours per show
        private const val NEARBY_FRIENDS_COOLDOWN_MS = 6 * 60 * 60 * 1000L  // 6 hours per friend batch
        private const val KEY_LAST_FRIENDS_NOTIFIED = "last_notified_nearby_friends"
        private const val TAG = "ProximityCheckWorker"
    }

    override suspend fun doWork(): Result {
        val context = applicationContext

        // Check location permissions
        if (!hasLocationPermission(context)) {
            Log.d(TAG, "Location permission not granted — skipping")
            return Result.success()
        }

        // Check if location monitoring is enabled.
        // Read from PersistenceManager (background-safe) rather than the
        // in-memory AppRepository, which may not be alive when the app is closed.
        if (!PersistenceManager.isLocationMonitoringEnabled()) {
            Log.d(TAG, "Location monitoring is off — skipping")
            return Result.success()
        }

        if (!PersistenceManager.isNearbyAlertsEnabled()) {
            Log.d(TAG, "Nearby alerts are off — skipping")
            return Result.success()
        }

        // Get current location
        val currentLocation = getCurrentLocation(context) ?: run {
            Log.d(TAG, "Could not get current location — skipping")
            return Result.success()
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        var notificationsSent = 0

        // --- Aggregate nearby rock spots notification (within 50 miles) ---
        // Count all dig sites, rock shops, beaches, etc. within the 50-mile radius
        // so the notification shows an accurate total rather than one spot at a time.
        val nearbyCount = SeedData.allLocations.count { location ->
            distanceMeters(
                currentLocation.latitude, currentLocation.longitude,
                location.latitude, location.longitude,
            ) <= PROXIMITY_RADIUS_METERS
        }

        val aggregateKey = "last_notified_nearby_aggregate"
        val lastAggregateNotified = prefs.getLong(aggregateKey, 0L)
        if (nearbyCount > 0 && now - lastAggregateNotified >= COOLDOWN_MS) {
            NotificationHelper.showNearbySpotsNotification(
                context = context,
                count = nearbyCount,
            )
            prefs.edit().putLong(aggregateKey, now).apply()
            notificationsSent++
            Log.d(TAG, "Aggregate nearby notification sent: $nearbyCount spots within 50 miles")
        } else {
            Log.d(TAG, "Nearby aggregate: $nearbyCount spots, on cooldown or empty")
        }

        // --- Upcoming gem & mineral shows within the 100-mile radius ---
        // Gem shows use a wider radius (100 mi) and a longer cooldown (24h) so users
        // get a heads-up about nearby shows without being spammed.
        val calendar = java.util.Calendar.getInstance().apply { timeInMillis = now }
        val currentMonth1 = calendar.get(java.util.Calendar.MONTH) + 1
        val gemShows = GemShowData.upcomingShows(currentMonth1)
        for (show in gemShows) {
            if (show.latitude == 0.0 && show.longitude == 0.0) continue
            val distanceMeters = distanceMeters(
                currentLocation.latitude, currentLocation.longitude,
                show.latitude, show.longitude,
            )
            if (distanceMeters > GEM_SHOW_RADIUS_METERS) continue

            // Only notify about shows in the current or next 2 months
            if (show.monthIndex != 0 && ((show.monthIndex - currentMonth1 + 12) % 12) > 2) continue

            val key = "last_notified_show_${show.id}"
            val lastNotified = prefs.getLong(key, 0L)
            if (now - lastNotified < GEM_SHOW_COOLDOWN_MS) {
                Log.d(TAG, "Show ${show.id} on cooldown — skipping")
                continue
            }

            val milesAway = (distanceMeters / 1609.34).toInt()
            NotificationHelper.showProximityNotification(
                context = context,
                locationId = "show_${show.id}",
                title = "Gem show nearby!",
                message = "${show.name} is ${milesAway}mi away in ${show.city}, ${show.state}. " +
                    "Typically held in ${show.monthLabel} (${show.dateRange}).",
            )
            prefs.edit().putLong(key, now).apply()
            notificationsSent++
        }

        // --- Nearby friends notification (within 50 miles) ---
        // When the user has the Nearby Friends alert toggle on and is signed in,
        // count connected friends whose coarse location is within the 50-mile
        // radius and send a single aggregate notification.
        val friendsSent = checkNearbyFriends(context, currentLocation, prefs, now)
        notificationsSent += friendsSent

        // Update the in-memory current location for the UI
        AppRepository.instance.setCurrentLocation(
            currentLocation.latitude,
            currentLocation.longitude,
        )

        Log.d(TAG, "Proximity check complete — $notificationsSent notification(s) sent")
        return Result.success()
    }

    /**
     * Check whether any connected friends are within the 50-mile radius and
     * post an aggregate notification if the Nearby Friends alerts toggle is
     * on. Reads everything from [LocalDataStore] / [PersistenceManager] so it
     * works from a background process without touching the in-memory repos.
     *
     * Returns the number of notifications sent (0 or 1).
     */
    private suspend fun checkNearbyFriends(
        context: Context,
        currentLocation: Location,
        prefs: android.content.SharedPreferences,
        now: Long,
    ): Int {
        // Only run if the Nearby Friends alerts toggle is on.
        if (!PersistenceManager.isNearbyFriendsAlertsEnabled()) {
            Log.d(TAG, "Nearby friends alerts are off — skipping")
            return 0
        }

        // Require the user to be signed in.
        val me = LocalDataStore.getString(LocalDataStore.KEY_AUTH_USER_ID)
        if (me.isNullOrBlank()) {
            Log.d(TAG, "Not signed in — skipping nearby friends check")
            return 0
        }

        // Cooldown so we don't spam the user every 10 minutes.
        val lastNotified = prefs.getLong(KEY_LAST_FRIENDS_NOTIFIED, 0L)
        if (now - lastNotified < NEARBY_FRIENDS_COOLDOWN_MS) {
            Log.d(TAG, "Nearby friends notification on cooldown — skipping")
            return 0
        }

        return runCatching {
            // Load connections (friend relationships) from local storage.
            val connections = LocalDataStore.getTable<MockDataSeeder.LocalConnection>(
                LocalDataStore.KEY_CONNECTIONS,
            )
            // Extract the friend ids that involve me.
            val friendIds = connections
                .filter { it.user_a == me || it.user_b == me }
                .map { if (it.user_a == me) it.user_b else it.user_a }
                .toSet()
            if (friendIds.isEmpty()) {
                Log.d(TAG, "No friends — skipping nearby friends check")
                return@runCatching 0
            }

            // Load the user rows and filter to my friends who have a coarse
            // location and are within 50 miles.
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val nearbyFriends = users.filter { u ->
                u.id in friendIds &&
                u.coarse_lat != null &&
                u.coarse_lng != null &&
                distanceMeters(
                    currentLocation.latitude, currentLocation.longitude,
                    u.coarse_lat, u.coarse_lng,
                ) <= PROXIMITY_RADIUS_METERS
            }

            if (nearbyFriends.isEmpty()) {
                Log.d(TAG, "No friends within 50 miles — skipping notification")
                return@runCatching 0
            }

            NotificationHelper.showNearbyFriendsNotification(
                context = context,
                friendCount = nearbyFriends.size,
            )
            prefs.edit().putLong(KEY_LAST_FRIENDS_NOTIFIED, now).apply()
            Log.d(TAG, "Nearby friends notification sent: ${nearbyFriends.size} within 50 miles")
            1
        }.onFailure {
            Log.w(TAG, "Nearby friends check failed: ${it.message}")
        }.getOrDefault(0)
    }

    /** Check fine + coarse location permissions. */
    private fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get the most recent device location. Delegates to [LocationFetcher]
     * so the worker benefits from the same cached-then-fresh logic used by
     * the foreground UI — giving notifications the most current position
     * instead of a potentially stale last-known reading.
     */
    private suspend fun getCurrentLocation(context: Context): Location? {
        return try {
            LocationFetcher.fetch(context)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get location: ${e.message}")
            null
        }
    }

    /** Haversine distance in meters. */
    private fun distanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
    ): Double {
        val r = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
