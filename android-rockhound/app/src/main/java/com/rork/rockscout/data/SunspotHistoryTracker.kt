package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tracks daily snapshots of active sunspot regions to build a magnetic evolution
 * history. Snapshots are deduplicated by date + region number and capped at 90 days.
 *
 * Memory usage: ~500 bytes per region per day, ~15KB/month, ~180KB/year — negligible.
 */
object SunspotHistoryTracker {

    private const val TAG = "SunspotHistoryTracker"
    private const val PREFS_NAME = "sunspot_history_prefs"
    private const val KEY_SNAPSHOTS = "snapshots_json"
    private const val MAX_DAYS = 90

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    @Serializable
    data class SunspotSnapshot(
        @SerialName("date") val date: String,         // ISO date "2026-07-24"
        @SerialName("regionNumber") val regionNumber: Int,
        @SerialName("magneticClass") val magneticClass: String,
        @SerialName("spotCount") val spotCount: Int,
        @SerialName("location") val location: String,
        @SerialName("cProb") val cProb: Double = 0.0,
        @SerialName("mProb") val mProb: Double = 0.0,
        @SerialName("xProb") val xProb: Double = 0.0,
    )

    private lateinit var prefs: android.content.SharedPreferences

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun ensureInitialized() {
        check(::prefs.isInitialized) { "SunspotHistoryTracker not initialized" }
    }

    /** Record snapshots for all currently active regions. Deduplicates by date + region number. */
    fun recordSnapshots(regions: List<SolarRegion>) {
        ensureInitialized()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val current = loadAllSnapshots().toMutableList()

        for (region in regions) {
            val snapshot = SunspotSnapshot(
                date = today,
                regionNumber = region.number,
                magneticClass = region.magneticClass,
                spotCount = region.spotCount,
                location = region.location,
                cProb = region.cClassProb,
                mProb = region.mClassProb,
                xProb = region.xClassProb,
            )
            // Remove any existing snapshot for this date + region, then add the new one
            current.removeAll { it.date == today && it.regionNumber == region.number }
            current.add(snapshot)
        }

        // Prune entries older than MAX_DAYS
        val cutoffDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(
            Date(System.currentTimeMillis() - MAX_DAYS.toLong() * 24 * 60 * 60 * 1000)
        )
        val pruned = current.filter { it.date >= cutoffDate }

        saveSnapshots(pruned)
        Log.d(TAG, "Recorded ${regions.size} region snapshots for $today (total: ${pruned.size})")
    }

    /** Get the evolution history for a specific region, sorted by date ascending. */
    fun getRegionHistory(regionNumber: Int): List<SunspotSnapshot> {
        ensureInitialized()
        return loadAllSnapshots()
            .filter { it.regionNumber == regionNumber }
            .sortedBy { it.date }
    }

    private fun loadAllSnapshots(): List<SunspotSnapshot> {
        ensureInitialized()
        return runCatching {
            prefs.getString(KEY_SNAPSHOTS, null)
                ?.let { json.decodeFromString<List<SunspotSnapshot>>(it) }
        }.getOrNull() ?: emptyList()
    }

    private fun saveSnapshots(snapshots: List<SunspotSnapshot>) {
        ensureInitialized()
        runCatching {
            prefs.edit().putString(KEY_SNAPSHOTS, json.encodeToString(snapshots)).apply()
        }.onFailure { Log.w(TAG, "Failed to save snapshots: ${it.message}") }
    }

    /** Magnetic class color for timeline display. */
    fun magneticClassColor(magClass: String): Long {
        return when {
            magClass.contains("Delta", ignoreCase = true) && magClass.contains("Beta", ignoreCase = true) -> 0xFFFF3B30
            magClass.contains("Delta", ignoreCase = true) -> 0xFFFF3B30
            magClass.contains("Gamma", ignoreCase = true) && magClass.contains("Beta", ignoreCase = true) -> 0xFF9B7BD8
            magClass.contains("Gamma", ignoreCase = true) -> 0xFFFF9500
            magClass.equals("Beta", ignoreCase = true) -> 0xFFFFCC00
            magClass.equals("Alpha", ignoreCase = true) -> 0xFF30D158
            else -> 0xFF8E8E93
        }
    }

    /** Educational description for a magnetic class. */
    fun magneticClassDescription(magClass: String): String {
        return when {
            magClass.equals("Alpha", ignoreCase = true) ->
                "Single magnetic polarity. Simple, stable configuration with low flare risk."
            magClass.equals("Beta", ignoreCase = true) ->
                "Bipolar configuration with two distinct polarities. Moderate flare risk."
            magClass.contains("Gamma", ignoreCase = true) && magClass.contains("Beta", ignoreCase = true) ->
                "Complex multi-polarity region with mixed polarities. Higher flare risk than Beta alone."
            magClass.contains("Gamma", ignoreCase = true) ->
                "Complex multi-polarity region where no single bipolar pair dominates. Elevated flare risk."
            magClass.contains("Delta", ignoreCase = true) && magClass.contains("Beta", ignoreCase = true) ->
                "Contains umbrae of opposite polarity within a single penumbra. Very high flare risk — X-class flares possible."
            magClass.contains("Delta", ignoreCase = true) ->
                "Mixed polarities within the same penumbra. Highest flare risk — most likely to produce X-class flares."
            else -> "Magnetic classification for this region."
        }
    }
}
