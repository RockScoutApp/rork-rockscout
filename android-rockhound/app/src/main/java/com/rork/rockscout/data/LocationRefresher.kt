package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import com.rork.rockscout.data.AppRepository
import com.rork.rockscout.data.LocationFetcher
import com.rork.rockscout.data.WorkScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Unified one-shot location refresh used by every trigger point that needs
 * the user's current position to propagate instantly through the app:
 *
 * - [com.rork.rockscout.MainActivity.onResume] (app refreshed / foregrounded)
 * - HomeScreen pull-to-refresh
 * - LocationsScreen entry / monitoring toggle
 * - ProfileScreen location save
 *
 * It performs three things in order:
 *  1. Fetches the freshest available location via [LocationFetcher] (cached
 *     first for an instant result, then a single GPS/network update).
 *  2. Pushes the result into [AppRepository.setCurrentLocation] so every
 *     screen observing `currentLocation` (Home nearby list, Locations list,
 *     the OSM map) re-derives distances and re-sorts immediately.
 *  3. Schedules an immediate [ProximityCheckWorker] run via
 *     [WorkScheduler.runProximityCheckNow] so nearby-spot push notifications
 *     fire against the user's latest position without waiting for the
 *     10-minute periodic tick.
 *
 * The 10-minute periodic worker continues to run in the background on its
 * own schedule and also calls [AppRepository.setCurrentLocation], so the UI
 * stays current even when the app is in the foreground and untouched.
 */
object LocationRefresher {

    private const val TAG = "LocationRefresher"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Kick a fresh location fetch + UI update + proximity notification pass.
     * Safe to call from the main thread; runs on Dispatchers.IO.
     * No-ops if location permission is missing or monitoring is disabled.
     */
    fun refresh(context: Context) {
        if (!LocationFetcher.hasPermission(context)) {
            Log.d(TAG, "No location permission — skipping refresh")
            return
        }
        if (!PersistenceManager.isLocationMonitoringEnabled()) {
            Log.d(TAG, "Location monitoring off — skipping refresh")
            return
        }
        scope.launch {
            val loc = LocationFetcher.fetch(context) ?: run {
                Log.d(TAG, "Location fetch returned null — skipping update")
                return@launch
            }
            // 1. Push into the shared state so observing screens update instantly.
            AppRepository.instance.setCurrentLocation(loc.latitude, loc.longitude)
            Log.d(TAG, "Updated current location to (${loc.latitude}, ${loc.longitude})")
            // 2. Trigger proximity notifications against the fresh position.
            WorkScheduler.runProximityCheckNow(context)
        }
    }
}
