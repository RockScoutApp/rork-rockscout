package com.rork.rockscout.data

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Shared geocoding utilities for reverse-geocoding lat/lng into human-readable
 * location strings. Used by the Ask-an-Expert email flow, nearby-places search,
 * and field-capture location tagging.
 */
object GeoUtils {

    /**
     * Reverse-geocodes [lat]/[lng] to a "City, State, Country" string.
     *
     * Falls back to [homeRegion] if geocoding fails, then to a raw coordinate
     * string. Runs on [Dispatchers.IO] — call from a coroutine.
     *
     * @param context any [Context] for the Geocoder
     * @param lat latitude
     * @param lng longitude
     * @param homeRegion fallback region label (e.g. "Arkansas, USA")
     * @return human-readable location string, never blank
     */
    suspend fun reverseGeocode(
        context: Context,
        lat: Double,
        lng: Double,
        homeRegion: String = "",
    ): String = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val geocoder = Geocoder(context)
                var resolved = ""
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    val addr = addresses.firstOrNull()
                    if (addr != null) {
                        resolved = formatAddress(addr)
                    }
                }
                // Geocoder async may not complete synchronously — wait briefly
                Thread.sleep(500)
                if (resolved.isNotBlank()) resolved else fallback(homeRegion, lat, lng)
            } else {
                @Suppress("DEPRECATION")
                val addresses = Geocoder(context).getFromLocation(lat, lng, 1)
                val addr = addresses?.firstOrNull()
                if (addr != null) {
                    formatAddress(addr).ifBlank { fallback(homeRegion, lat, lng) }
                } else {
                    fallback(homeRegion, lat, lng)
                }
            }
        } catch (_: Throwable) {
            fallback(homeRegion, lat, lng)
        }
    }

    /**
     * Builds the full location string for the Ask-an-Expert email — combines
     * the reverse-geocoded readable location with raw GPS coordinates so the
     * expert has both a general area and precise coordinates.
     *
     * Format: "City, State, Country\nGPS coordinates: 34.5037, -93.6321"
     */
    suspend fun emailLocationString(
        context: Context,
        lat: Double,
        lng: Double,
        homeRegion: String = "",
    ): String = withContext(Dispatchers.IO) {
        val readable = reverseGeocode(context, lat, lng, homeRegion)
        val coords = "%.4f, %.4f".format(lat, lng)
        "$readable\nGPS coordinates: $coords"
    }

    private fun formatAddress(addr: android.location.Address): String {
        val parts = mutableListOf<String>()
        addr.locality?.let { parts.add(it) }
        addr.adminArea?.let { parts.add(it) }
        addr.countryName?.let { parts.add(it) }
        return parts.joinToString(", ")
    }

    private fun fallback(homeRegion: String, lat: Double, lng: Double): String {
        return homeRegion.ifBlank { "%.4f, %.4f".format(lat, lng) }
    }
}
