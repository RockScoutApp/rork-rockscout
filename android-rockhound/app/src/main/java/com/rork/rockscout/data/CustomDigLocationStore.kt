package com.rork.rockscout.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Persistently stores user-accepted custom [DigLocation] entries that were
 * discovered via web search and approved in the Developer Console.
 *
 * Accepted sites are merged into [SeedData.allLocations] so they appear
 * permanently in the Dig Sites tab, on maps, and in trip planner searches.
 */
object CustomDigLocationStore {

    private const val PREFS_KEY = "custom_dig_locations"

    private val _locations = MutableStateFlow<List<DigLocation>>(emptyList())
    val locations: StateFlow<List<DigLocation>> = _locations.asStateFlow()

    private var initialized = false

    fun initialize() {
        if (initialized) return
        initialized = true
        loadFromDisk()
    }

    fun addAll(newLocations: List<DigLocation>) {
        val existingIds = _locations.value.map { it.id }.toSet()
        val unique = newLocations.filter { it.id !in existingIds }
        if (unique.isEmpty()) return
        _locations.value = (_locations.value + unique)
        persist()
    }

    fun remove(id: String) {
        _locations.value = _locations.value.filterNot { it.id == id }
        persist()
    }

    fun clear() {
        _locations.value = emptyList()
        persist()
    }

    private fun loadFromDisk() {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_KEY) ?: return
            val list = LocalDataStore.json.decodeFromString<List<DigLocation>>(raw)
            _locations.value = list
        }
    }

    private fun persist() {
        runCatching {
            LocalDataStore.setString(
                PREFS_KEY,
                LocalDataStore.json.encodeToString(_locations.value),
            )
        }
    }

    /**
     * Convert a [DigSiteDiscoveryStore.DiscoveredSite] (from web search) into
     * a permanent [DigLocation] that can be merged into [SeedData.allLocations].
     *
     * Coordinates default to 0,0 since the web search doesn't provide them —
     * the site will still appear in list/search results; a developer can edit
     * coordinates manually in [SeedData] later if map placement is needed.
     */
    /**
     * Convert an approved [LocationSubmissionStore.LocationSubmission] into a
     * permanent [DigLocation] that can be merged into [SeedData.allLocations].
     */
    fun fromSubmission(sub: LocationSubmissionStore.LocationSubmission): DigLocation {
        val locType = when (sub.type) {
            "Rock Shop" -> LocationType.ROCK_SHOP
            "Metaphysical Shop" -> LocationType.METAPHYSICAL
            "Lapidary Club" -> LocationType.LAPIDARY_CLUB
            "Mine" -> LocationType.MINE
            "Quarry" -> LocationType.QUARRY
            "Beach" -> LocationType.BEACH
            "River" -> LocationType.RIVER
            "Desert" -> LocationType.DESERT
            else -> LocationType.PUBLIC_DIG
        }
        return DigLocation(
            id = "user-sub-${sub.id}",
            name = sub.name,
            type = locType,
            region = sub.address.ifBlank { "User submitted" },
            latitude = sub.latitude,
            longitude = sub.longitude,
            summary = sub.comments.ifBlank { if (sub.webVerified) "Web-verified dig site submitted by ${sub.submitterName}." else "User-submitted location." },
            knownFor = emptyList(),
            mineralTags = emptyList(),
            feeInfo = "See website for details",
            hours = "See website for details",
            website = sub.webUrl.ifBlank { null },
            phone = null,
            difficulty = "Unknown",
            publicAccess = true,
            tips = if (sub.webSnippet.isNotBlank()) sub.webSnippet else "Submitted by ${sub.submitterName}. Verify access and hours before visiting.",
            submitterName = sub.submitterName,
            submittedPhotoUris = sub.photoUris,
        )
    }

    /**
     * Add an auto-verified or admin-approved location submission as a permanent
     * [DigLocation] so it appears in the Dig Sites tab, on maps, and in trip
     * planner searches.
     */
    fun addApprovedLocation(sub: LocationSubmissionStore.LocationSubmission) {
        val loc = fromSubmission(sub)
        addAll(listOf(loc))
    }

    fun fromDiscovered(site: DigSiteDiscoveryStore.DiscoveredSite): DigLocation {
        val locType = when (site.type) {
            "Rock Shop" -> LocationType.ROCK_SHOP
            "Metaphysical Shop" -> LocationType.METAPHYSICAL
            "Lapidary Club" -> LocationType.LAPIDARY_CLUB
            "Pay Dig Site", "Mine" -> LocationType.MINE
            "Quarry" -> LocationType.QUARRY
            "Beach" -> LocationType.BEACH
            "River" -> LocationType.RIVER
            "Desert" -> LocationType.DESERT
            else -> LocationType.PUBLIC_DIG
        }
        return DigLocation(
            id = "custom-${site.id}",
            name = site.name,
            type = locType,
            region = site.region,
            latitude = 0.0,
            longitude = 0.0,
            summary = site.description.ifBlank { "Discovered via web search in ${site.searchArea}." },
            knownFor = emptyList(),
            mineralTags = emptyList(),
            feeInfo = "See website for details",
            hours = "See website for details",
            website = site.url,
            phone = null,
            difficulty = "Unknown",
            publicAccess = true,
            tips = "Added from web search result. Verify access and hours before visiting.",
        )
    }
}
