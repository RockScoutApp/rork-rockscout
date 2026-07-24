package com.rork.rockscout.data

import androidx.compose.ui.graphics.Color
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Success

/**
 * Unified model for any location that can be saved to the user's favorite spots.
 * Resolves a prefixed ID string back to display data for the favorites list.
 */
data class FavoriteSpotEntry(
    val id: String,
    val name: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val typeLabel: String,
    val emoji: String,
    val accent: Color,
    /** Route path to navigate to this spot's detail screen. */
    val route: String,
)

/**
 * Resolves favorite spot IDs (which may belong to different data sources) into
 * a unified [FavoriteSpotEntry] for display and navigation.
 *
 * ID prefix scheme (avoids collisions with existing SeedData dig site IDs):
 *  - Dig sites (SeedData):           bare ID (e.g. "crater-of-diamonds")
 *  - State parks:                    "park:{parkId}"
 *  - BLM trailheads:                 "trailhead:{name}"
 *  - BLM campgrounds:                "campground:{name}"
 *  - BLM dig sites:                  "blmsite:{stateCode}:{siteName}"
 *  - BLM states:                     "blmstate:{stateCode}"
 */
object FavoriteSpotResolver {

    private const val PARK_PREFIX = "park:"
    private const val TRAILHEAD_PREFIX = "trailhead:"
    private const val CAMPGROUND_PREFIX = "campground:"
    private const val BLM_SITE_PREFIX = "blmsite:"
    private const val BLM_STATE_PREFIX = "blmstate:"

    // ── ID generators (used by detail screens when toggling favorites) ──

    fun parkId(parkId: String): String = "$PARK_PREFIX$parkId"
    fun trailheadId(name: String): String = "$TRAILHEAD_PREFIX$name"
    fun campgroundId(name: String): String = "$CAMPGROUND_PREFIX$name"
    fun blmSiteId(stateCode: String, siteName: String): String = "$BLM_SITE_PREFIX$stateCode:$siteName"
    fun blmStateId(stateCode: String): String = "$BLM_STATE_PREFIX$stateCode"

    // ── Resolution ──

    /**
     * Resolves a favorite spot ID into a [FavoriteSpotEntry], or null if the
     * ID is unrecognized or the underlying data was removed.
     */
    fun resolve(id: String): FavoriteSpotEntry? {
        return when {
            id.startsWith(PARK_PREFIX) -> resolvePark(id.removePrefix(PARK_PREFIX))
            id.startsWith(TRAILHEAD_PREFIX) -> resolveTrailhead(id.removePrefix(TRAILHEAD_PREFIX))
            id.startsWith(CAMPGROUND_PREFIX) -> resolveCampground(id.removePrefix(CAMPGROUND_PREFIX))
            id.startsWith(BLM_SITE_PREFIX) -> resolveBlmSite(id.removePrefix(BLM_SITE_PREFIX))
            id.startsWith(BLM_STATE_PREFIX) -> resolveBlmState(id.removePrefix(BLM_STATE_PREFIX))
            else -> resolveDigLocation(id)
        }
    }

    private fun resolvePark(parkId: String): FavoriteSpotEntry? {
        val park = StateParkData.allParks.firstOrNull { it.id == parkId } ?: return null
        return FavoriteSpotEntry(
            id = parkId(park.id),
            name = park.name,
            region = park.region,
            latitude = park.latitude,
            longitude = park.longitude,
            typeLabel = "State Park",
            emoji = "🏞️",
            accent = Success,
            route = "state_park/${park.id}",
        )
    }

    private fun resolveTrailhead(name: String): FavoriteSpotEntry? {
        val trailhead = BlmData.trailheads.firstOrNull { it.name == name } ?: return null
        return FavoriteSpotEntry(
            id = trailheadId(trailhead.name),
            name = trailhead.name,
            region = trailhead.region,
            latitude = trailhead.latitude,
            longitude = trailhead.longitude,
            typeLabel = "Trailhead",
            emoji = "🥾",
            accent = Success,
            route = "blm_trailhead/${android.net.Uri.encode(trailhead.name)}",
        )
    }

    private fun resolveCampground(name: String): FavoriteSpotEntry? {
        val campground = BlmData.campgrounds.firstOrNull { it.name == name } ?: return null
        return FavoriteSpotEntry(
            id = campgroundId(campground.name),
            name = campground.name,
            region = campground.region,
            latitude = campground.latitude,
            longitude = campground.longitude,
            typeLabel = "Campground",
            emoji = "⛺",
            accent = Citrine,
            route = "blm_campground/${android.net.Uri.encode(campground.name)}",
        )
    }

    private fun resolveBlmSite(combined: String): FavoriteSpotEntry? {
        val colonIdx = combined.indexOf(':')
        if (colonIdx < 0) return null
        val stateCode = combined.substring(0, colonIdx)
        val siteName = combined.substring(colonIdx + 1)
        val site = BlmData.allBlmDigSites.firstOrNull { it.name == siteName } ?: return null
        val state = BlmData.allStates.firstOrNull { it.code == stateCode }
        return FavoriteSpotEntry(
            id = blmSiteId(stateCode, site.name),
            name = site.name,
            region = site.region,
            latitude = site.latitude,
            longitude = site.longitude,
            typeLabel = "BLM Collecting Site",
            emoji = "⛏️",
            accent = state?.let { Color(it.accentHex) } ?: Citrine,
            route = "blm_state/$stateCode",
        )
    }

    private fun resolveBlmState(stateCode: String): FavoriteSpotEntry? {
        val state = BlmData.allStates.firstOrNull { it.code == stateCode } ?: return null
        return FavoriteSpotEntry(
            id = blmStateId(state.code),
            name = "${state.name} BLM Guide",
            region = "BLM: ${state.blmAcreage}",
            latitude = 0.0,
            longitude = 0.0,
            typeLabel = "BLM State",
            emoji = state.silhouetteEmoji,
            accent = Color(state.accentHex),
            route = "blm_state/${state.code}",
        )
    }

    private fun resolveDigLocation(locationId: String): FavoriteSpotEntry? {
        val loc = SeedData.locationById(locationId) ?: return null
        return FavoriteSpotEntry(
            id = loc.id,
            name = loc.name,
            region = loc.region,
            latitude = loc.latitude,
            longitude = loc.longitude,
            typeLabel = loc.type.label,
            emoji = loc.type.emoji,
            accent = Aqua,
            route = "location/${loc.id}",
        )
    }
}
