package com.rork.rockscout.data

import kotlinx.serialization.Serializable

/**
 * A museum or cultural center that handles artifacts — natural history,
 * anthropology, archaeology, tribal/indigenous cultural centers, etc.
 *
 * Returned by the /museums backend endpoint (which queries OpenStreetMap
 * Overpass API) and displayed in the [MuseumFinderSheet] bottom sheet.
 *
 * @param id OSM element ID (e.g. "node_12345")
 * @param name museum display name
 * @param type classified category (e.g. "Natural History", "Archaeology")
 * @param lat latitude
 * @param lon longitude
 * @param phone phone number if available, null otherwise
 * @param website website URL if available, null otherwise
 * @param email email address if available, null otherwise
 * @param address assembled full address from addr:* tags
 * @param distanceMiles haversine distance from the user's location
 */
@Serializable
data class Museum(
    val id: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double,
    val phone: String? = null,
    val website: String? = null,
    val email: String? = null,
    val address: String = "",
    val distanceMiles: Double = 0.0,
)

@Serializable
data class MuseumResponse(
    val museums: List<Museum> = emptyList(),
    val expandedRadius: Boolean = false,
    val searchRadiusMiles: Int = 50,
)
