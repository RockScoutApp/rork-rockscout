/**
 * Region-to-coordinate lookup for war relic discovery locations.
 * Maps the `whereFound` strings from WarRelicSpecimens to approximate
 * lat/lng coordinates for map display. Regions are intentionally
 * coarse — these are areas, not exact sites.
 */

export interface RegionCoord {
  name: string;
  lat: number;
  lng: number;
  /** Short label shown in the map popup */
  label: string;
}

/**
 * Every distinct `whereFound` value from the 64 war relic specimens,
 * mapped to a representative coordinate. When a relic has multiple
 * regions, all of them get a marker on the map.
 */
const REGION_COORDS: Record<string, RegionCoord> = {
  // ── Broad theater / region terms ──────────────────────────────
  "Eastern Theater": { name: "Eastern Theater", lat: 38.9, lng: -77.35, label: "Eastern Theater (Virginia / Maryland)" },
  "Western Theater": { name: "Western Theater", lat: 35.15, lng: -88.65, label: "Western Theater (Tennessee / Mississippi)" },
  "Eastern battlefields": { name: "Eastern battlefields", lat: 38.5, lng: -77.5, label: "Eastern Battlefields (Virginia)" },
  "Southern battlefields": { name: "Southern battlefields", lat: 33.0, lng: -87.0, label: "Southern Battlefields" },
  "Deep South sites": { name: "Deep South sites", lat: 31.0, lng: -88.0, label: "Deep South Sites" },
  "Early war sites": { name: "Early war sites", lat: 38.5, lng: -77.5, label: "Early War Sites (Virginia)" },
  "Late-war sites": { name: "Late-war sites", lat: 35.0, lng: -81.0, label: "Late-War Sites (Carolinas)" },
  "Both armies": { name: "Both armies", lat: 38.9, lng: -77.35, label: "Both Armies (Eastern Theater)" },

  // ── State-specific ────────────────────────────────────────────
  "Virginia": { name: "Virginia", lat: 38.0, lng: -79.5, label: "Virginia" },
  "Tennessee": { name: "Tennessee", lat: 35.5, lng: -86.0, label: "Tennessee" },

  // ── Camp / position types (default to Eastern Theater if no region) ──
  "Union camps": { name: "Union camps", lat: 38.9, lng: -77.35, label: "Union Camps" },
  "Union cavalry camps": { name: "Union cavalry camps", lat: 38.9, lng: -77.35, label: "Union Cavalry Camps" },
  "Union artillery camps": { name: "Union artillery camps", lat: 38.9, lng: -77.35, label: "Union Artillery Camps" },
  "Union artillery positions": { name: "Union artillery positions", lat: 38.9, lng: -77.35, label: "Union Artillery Positions" },
  "Union positions": { name: "Union positions", lat: 38.9, lng: -77.35, label: "Union Positions" },
  "New York regiment camps": { name: "New York regiment camps", lat: 43.0, lng: -75.0, label: "New York Regiment Camps" },
  "Confederate camps": { name: "Confederate camps", lat: 37.5, lng: -78.0, label: "Confederate Camps (Virginia)" },
  "Confederate cavalry camps": { name: "Confederate cavalry camps", lat: 37.5, lng: -78.0, label: "Confederate Cavalry Camps" },
  "Confederate artillery camps": { name: "Confederate artillery camps", lat: 37.5, lng: -78.0, label: "Confederate Artillery Camps" },
  "Confederate artillery positions": { name: "Confederate artillery positions", lat: 37.5, lng: -78.0, label: "Confederate Artillery Positions" },
  "Confederate positions": { name: "Confederate positions", lat: 37.5, lng: -78.0, label: "Confederate Positions" },
  "Georgia troop camps": { name: "Georgia troop camps", lat: 32.5, lng: -83.0, label: "Georgia Troop Camps" },
  "British camps": { name: "British camps", lat: 40.7, lng: -74.0, label: "British Camps (New York / Boston)" },
  "British & American camps": { name: "British & American camps", lat: 40.7, lng: -74.0, label: "British & American Camps" },
  "American camps": { name: "American camps", lat: 40.7, lng: -74.0, label: "American Camps" },
  "Continental Army camps": { name: "Continental Army camps", lat: 40.0, lng: -75.0, label: "Continental Army Camps" },
  "Officer camps": { name: "Officer camps", lat: 38.9, lng: -77.35, label: "Officer Camps" },
  "NCO camps": { name: "NCO camps", lat: 38.9, lng: -77.35, label: "NCO Camps" },
  "Camps": { name: "Camps", lat: 38.9, lng: -77.35, label: "Camp Sites" },
  "Cavalry camps": { name: "Cavalry camps", lat: 38.9, lng: -77.35, label: "Cavalry Camps" },
  "Cavalry sites": { name: "Cavalry sites", lat: 38.9, lng: -77.35, label: "Cavalry Sites" },
  "Winter quarters": { name: "Winter quarters", lat: 38.7, lng: -77.15, label: "Winter Quarters" },
  "March routes": { name: "March routes", lat: 38.9, lng: -77.35, label: "March Routes" },
  "Rifle pits": { name: "Rifle pits", lat: 38.9, lng: -77.35, label: "Rifle Pits" },
  "Riflemen camps": { name: "Riflemen camps", lat: 38.9, lng: -77.35, label: "Riflemen Camps" },
  "Trench lines": { name: "Trench lines", lat: 37.5, lng: -77.4, label: "Trench Lines (Petersburg)" },
  "Firing lines": { name: "Firing lines", lat: 38.9, lng: -77.35, label: "Firing Lines" },
  "Defensive lines": { name: "Defensive lines", lat: 38.9, lng: -77.35, label: "Defensive Lines" },
  "Fortifications": { name: "Fortifications", lat: 38.9, lng: -77.35, label: "Fortifications" },
  "Siege positions": { name: "Siege positions", lat: 37.23, lng: -77.40, label: "Siege Positions (Petersburg)" },
  "Siege sites": { name: "Siege sites", lat: 37.23, lng: -77.40, label: "Siege Sites (Petersburg)" },
  "Artillery positions": { name: "Artillery positions", lat: 38.9, lng: -77.35, label: "Artillery Positions" },
  "Battery positions": { name: "Battery positions", lat: 38.9, lng: -77.35, label: "Battery Positions" },
  "Battery sites": { name: "Battery sites", lat: 38.9, lng: -77.35, label: "Battery Sites" },
  "Battlefields": { name: "Battlefields", lat: 38.9, lng: -77.35, label: "Battlefields" },
  "Naval sites": { name: "Naval sites", lat: 36.9, lng: -76.0, label: "Naval Sites (Hampton Roads)" },
  "Frontier sites": { name: "Frontier sites", lat: 40.0, lng: -80.0, label: "Frontier Sites" },
  "Ambush sites": { name: "Ambush sites", lat: 38.9, lng: -77.35, label: "Ambush Sites" },
  "Close-quarters engagements": { name: "Close-quarters engagements", lat: 38.9, lng: -77.35, label: "Close-Quarters Engagements" },
  "Southern fortifications": { name: "Southern fortifications", lat: 33.0, lng: -87.0, label: "Southern Fortifications" },

  // ── Source / origin terms ─────────────────────────────────────
  "Imported from Britain": { name: "Imported from Britain", lat: 53.0, lng: -1.5, label: "Imported from Britain" },
};

/**
 * Default fallback coordinate when a `whereFound` string is not in the lookup.
 * Centered on the Eastern Theater (most common CW relic region).
 */
const DEFAULT_COORD: RegionCoord = {
  name: "Unknown",
  lat: 38.9,
  lng: -77.35,
  label: "Discovery Area",
};

/**
 * Given a list of `whereFound` strings from an artifact, return
 * the matching coordinates for each one. Strings that don't match
 * the lookup are skipped (no default fallback — we only show
 * regions we can confidently place on the map).
 */
export function getRegionCoords(whereFound: string[]): RegionCoord[] {
  const results: RegionCoord[] = [];
  const seen = new Set<string>();
  for (const region of whereFound) {
    const coord = REGION_COORDS[region];
    if (coord && !seen.has(coord.label)) {
      results.push(coord);
      seen.add(coord.label);
    }
  }
  return results;
}
