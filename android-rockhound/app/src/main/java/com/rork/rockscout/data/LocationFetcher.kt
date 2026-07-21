package com.rork.rockscout.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Fast one-shot location fetch using the platform LocationManager.
 * Tries GPS, then network, then passive — returns the most recent
 * accurate reading within a short timeout window.
 *
 * Unlike [ProximityCheckWorker.getCurrentLocation], this can request
 * a single fresh update instead of relying solely on stale cached
 * locations, making the "Near Me" list update quickly when the user
 * opens the Locations screen or enables monitoring.
 */
object LocationFetcher {

    private const val TAG = "LocationFetcher"
    private const val TIMEOUT_MS = 8_000L

    /** Check fine location permission. */
    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Fetch the most recent location. First checks cached locations
     * for an immediate result, then requests a fresh single update
     * if the cached reading is older than 2 minutes.
     */
    @SuppressLint("MissingPermission")
    suspend fun fetch(context: Context): Location? {
        if (!hasPermission(context)) return null

        // Use the application context so the LocationManager service handle is
        // never tied to an Activity that may be destroyed while the one-shot
        // update / timeout thread is still in flight. The system service
        // itself is process-scoped, so an Activity context here would keep
        // the destroyed Activity reachable until the listener is removed.
        val appContext = context.applicationContext
        val lm = appContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: run {
            Log.w(TAG, "LocationManager service unavailable")
            return null
        }

        // Step 1: Try cached locations for an instant result
        val cached = getCachedLocation(lm)
        if (cached != null && System.currentTimeMillis() - cached.time < 120_000L) {
            Log.d(TAG, "Using cached location (age=${System.currentTimeMillis() - cached.time}ms)")
            return cached
        }

        // Step 2: Request a fresh single update from the best available provider
        return try {
            requestSingleUpdate(lm) ?: cached
        } catch (e: Exception) {
            Log.w(TAG, "Fresh location request failed: ${e.message}")
            cached
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCachedLocation(lm: LocationManager): Location? {
        var best: Location? = null
        for (provider in lm.getProviders(true)) {
            val loc = lm.getLastKnownLocation(provider) ?: continue
            if (best == null || loc.accuracy < best.accuracy) {
                best = loc
            }
        }
        return best
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleUpdate(lm: LocationManager): Location? =
        suspendCancellableCoroutine { cont ->
            val providers = lm.getProviders(true)
                .filter { it == LocationManager.GPS_PROVIDER || it == LocationManager.NETWORK_PROVIDER }

            if (providers.isEmpty()) {
                cont.resume(null)
                return@suspendCancellableCoroutine
            }

            // Try GPS first (most accurate), fall back to network
            val provider = providers.firstOrNull { it == LocationManager.GPS_PROVIDER }
                ?: providers.first()

            var resumed = false

            val listener = object : android.location.LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (!resumed) {
                        resumed = true
                        try { lm.removeUpdates(this) } catch (_: Exception) {}
                        cont.resume(location)
                    }
                }

                override fun onProviderDisabled(provider: String) {}
                override fun onProviderEnabled(provider: String) {}
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
            }

            try {
                lm.requestSingleUpdate(provider, listener, Looper.getMainLooper())
            } catch (e: Exception) {
                if (!resumed) {
                    resumed = true
                    cont.resume(null)
                }
                return@suspendCancellableCoroutine
            }

            // Timeout fallback — resume with null after TIMEOUT_MS
            Thread {
                try {
                    Thread.sleep(TIMEOUT_MS)
                } catch (_: InterruptedException) {}
                if (!resumed) {
                    resumed = true
                    try { lm.removeUpdates(listener) } catch (_: Exception) {}
                    cont.resume(null)
                }
            }.also { thread ->
                cont.invokeOnCancellation {
                    thread.interrupt()
                    try { lm.removeUpdates(listener) } catch (_: Exception) {}
                }
            }
        }
}
