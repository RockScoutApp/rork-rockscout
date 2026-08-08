package com.rork.rockscout.data

/**
 * Region-to-coordinate lookup for war relic discovery locations.
 * Maps the `whereFound` strings from [WarRelicSpecimens] to approximate
 * lat/lng coordinates for map display. Regions are intentionally coarse —
 * these are areas, not exact sites.
 */
object RelicRegions {

    data class RegionCoord(
        val name: String,
        val lat: Double,
        val lng: Double,
        val label: String,
    )

    private val REGION_COORDS: Map<String, RegionCoord> = mapOf(
        // ── Broad theater / region terms ──────────────────────────
        "Eastern Theater" to RegionCoord("Eastern Theater", 38.9, -77.35, "Eastern Theater (Virginia / Maryland)"),
        "Western Theater" to RegionCoord("Western Theater", 35.15, -88.65, "Western Theater (Tennessee / Mississippi)"),
        "Eastern battlefields" to RegionCoord("Eastern battlefields", 38.5, -77.5, "Eastern Battlefields (Virginia)"),
        "Southern battlefields" to RegionCoord("Southern battlefields", 33.0, -87.0, "Southern Battlefields"),
        "Deep South sites" to RegionCoord("Deep South sites", 31.0, -88.0, "Deep South Sites"),
        "Early war sites" to RegionCoord("Early war sites", 38.5, -77.5, "Early War Sites (Virginia)"),
        "Late-war sites" to RegionCoord("Late-war sites", 35.0, -81.0, "Late-War Sites (Carolinas)"),
        "Both armies" to RegionCoord("Both armies", 38.9, -77.35, "Both Armies (Eastern Theater)"),

        // ── State-specific ────────────────────────────────────────
        "Virginia" to RegionCoord("Virginia", 38.0, -79.5, "Virginia"),
        "Tennessee" to RegionCoord("Tennessee", 35.5, -86.0, "Tennessee"),

        // ── Camp / position types ─────────────────────────────────
        "Union camps" to RegionCoord("Union camps", 38.9, -77.35, "Union Camps"),
        "Union cavalry camps" to RegionCoord("Union cavalry camps", 38.9, -77.35, "Union Cavalry Camps"),
        "Union artillery camps" to RegionCoord("Union artillery camps", 38.9, -77.35, "Union Artillery Camps"),
        "Union artillery positions" to RegionCoord("Union artillery positions", 38.9, -77.35, "Union Artillery Positions"),
        "Union positions" to RegionCoord("Union positions", 38.9, -77.35, "Union Positions"),
        "New York regiment camps" to RegionCoord("New York regiment camps", 43.0, -75.0, "New York Regiment Camps"),
        "Confederate camps" to RegionCoord("Confederate camps", 37.5, -78.0, "Confederate Camps (Virginia)"),
        "Confederate cavalry camps" to RegionCoord("Confederate cavalry camps", 37.5, -78.0, "Confederate Cavalry Camps"),
        "Confederate artillery camps" to RegionCoord("Confederate artillery camps", 37.5, -78.0, "Confederate Artillery Camps"),
        "Confederate artillery positions" to RegionCoord("Confederate artillery positions", 37.5, -78.0, "Confederate Artillery Positions"),
        "Confederate positions" to RegionCoord("Confederate positions", 37.5, -78.0, "Confederate Positions"),
        "Georgia troop camps" to RegionCoord("Georgia troop camps", 32.5, -83.0, "Georgia Troop Camps"),
        "British camps" to RegionCoord("British camps", 40.7, -74.0, "British Camps (New York / Boston)"),
        "British & American camps" to RegionCoord("British & American camps", 40.7, -74.0, "British & American Camps"),
        "American camps" to RegionCoord("American camps", 40.7, -74.0, "American Camps"),
        "Continental Army camps" to RegionCoord("Continental Army camps", 40.0, -75.0, "Continental Army Camps"),
        "Officer camps" to RegionCoord("Officer camps", 38.9, -77.35, "Officer Camps"),
        "NCO camps" to RegionCoord("NCO camps", 38.9, -77.35, "NCO Camps"),
        "Camps" to RegionCoord("Camps", 38.9, -77.35, "Camp Sites"),
        "Cavalry camps" to RegionCoord("Cavalry camps", 38.9, -77.35, "Cavalry Camps"),
        "Cavalry sites" to RegionCoord("Cavalry sites", 38.9, -77.35, "Cavalry Sites"),
        "Winter quarters" to RegionCoord("Winter quarters", 38.7, -77.15, "Winter Quarters"),
        "March routes" to RegionCoord("March routes", 38.9, -77.35, "March Routes"),
        "Rifle pits" to RegionCoord("Rifle pits", 38.9, -77.35, "Rifle Pits"),
        "Riflemen camps" to RegionCoord("Riflemen camps", 38.9, -77.35, "Riflemen Camps"),
        "Trench lines" to RegionCoord("Trench lines", 37.5, -77.4, "Trench Lines (Petersburg)"),
        "Firing lines" to RegionCoord("Firing lines", 38.9, -77.35, "Firing Lines"),
        "Defensive lines" to RegionCoord("Defensive lines", 38.9, -77.35, "Defensive Lines"),
        "Fortifications" to RegionCoord("Fortifications", 38.9, -77.35, "Fortifications"),
        "Siege positions" to RegionCoord("Siege positions", 37.23, -77.40, "Siege Positions (Petersburg)"),
        "Siege sites" to RegionCoord("Siege sites", 37.23, -77.40, "Siege Sites (Petersburg)"),
        "Artillery positions" to RegionCoord("Artillery positions", 38.9, -77.35, "Artillery Positions"),
        "Battery positions" to RegionCoord("Battery positions", 38.9, -77.35, "Battery Positions"),
        "Battery sites" to RegionCoord("Battery sites", 38.9, -77.35, "Battery Sites"),
        "Battlefields" to RegionCoord("Battlefields", 38.9, -77.35, "Battlefields"),
        "Naval sites" to RegionCoord("Naval sites", 36.9, -76.0, "Naval Sites (Hampton Roads)"),
        "Frontier sites" to RegionCoord("Frontier sites", 40.0, -80.0, "Frontier Sites"),
        "Ambush sites" to RegionCoord("Ambush sites", 38.9, -77.35, "Ambush Sites"),
        "Close-quarters engagements" to RegionCoord("Close-quarters engagements", 38.9, -77.35, "Close-Quarters Engagements"),
        "Southern fortifications" to RegionCoord("Southern fortifications", 33.0, -87.0, "Southern Fortifications"),

        // ── Source / origin terms ─────────────────────────────────
        "Imported from Britain" to RegionCoord("Imported from Britain", 53.0, -1.5, "Imported from Britain"),
    )

    /**
     * Given a list of `whereFound` strings from a war relic, return
     * the matching coordinates for each one. Strings that don't match
     * the lookup are skipped — only confidently-placed regions are shown.
     */
    fun getRegionCoords(whereFound: List<String>): List<RegionCoord> {
        val results = mutableListOf<RegionCoord>()
        val seen = mutableSetOf<String>()
        for (region in whereFound) {
            val coord = REGION_COORDS[region] ?: continue
            if (seen.add(coord.label)) {
                results.add(coord)
            }
        }
        return results
    }
}
