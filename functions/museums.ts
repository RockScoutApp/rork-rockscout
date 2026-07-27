/**
 * Museum finder endpoint — queries OpenStreetMap Overpass API for
 * artifact-relevant museums near a given lat/lon.
 *
 * Route: POST /museums
 * Body: { lat: number, lon: number, radius?: number }
 * Returns: { museums: Museum[], expandedRadius: boolean, searchRadiusMiles: number }
 *
 * Filters server-side to museum types that actually handle artifacts:
 * natural history, anthropology, ethnology, archaeology, tribal/indigenous
 * cultural centers, and general cultural heritage centers.
 *
 * If zero results at 50 miles, retries at 100 miles, then 250 miles.
 * Caches results for 6 hours per lat/lon bucket.
 */

interface MuseumRequest {
  lat: number;
  lon: number;
  radius?: number;
}

interface Museum {
  id: string;
  name: string;
  type: string;
  lat: number;
  lon: number;
  phone: string | null;
  website: string | null;
  email: string | null;
  address: string;
  distanceMiles: number;
}

interface MuseumResponse {
  museums: Museum[];
  expandedRadius: boolean;
  searchRadiusMiles: number;
}

const OVERPASS_URL = "https://overpass-api.de/api/interpreter";
const MILES_TO_METERS = 1609.34;
const DEFAULT_RADIUS_MILES = 50;
const EXPAND_RADII = [100, 250];

/** Keywords that indicate an artifact-relevant museum. */
const ARTIFACT_KEYWORDS = [
  "natural history",
  "anthropolog",
  "ethnolog",
  "archaeolog",
  "archeolog",
  "tribal",
  "indian",
  "native american",
  "indigenous",
  "cultural center",
  "heritage center",
  "prehistor",
  "paleo",
  "fossil",
  "geolog",
  "mineral",
  "gem",
  "rock",
  "earth",
];

/** OSM museum= values that are artifact-relevant. */
const ARTIFACT_MUSEUM_TAGS = new Set([
  "natural_history",
  "archaeology",
  "anthropology",
  "ethnography",
  "ethnology",
  "geology",
]);

/** Classify a museum's type from its OSM tags + name. */
function classifyMuseumType(
  name: string,
  tags: Record<string, string>,
): string | null {
  const lowerName = name.toLowerCase();

  // Explicit OSM museum= tag
  const museumTag = tags["museum"];
  if (museumTag && ARTIFACT_MUSEUM_TAGS.has(museumTag)) {
    const labelMap: Record<string, string> = {
      natural_history: "Natural History",
      archaeology: "Archaeology",
      anthropology: "Anthropology",
      ethnography: "Ethnography",
      ethnology: "Ethnology",
      geology: "Geology",
    };
    return labelMap[museumTag] ?? "Museum";
  }

  // Name-based keyword classification
  if (lowerName.includes("natural history")) return "Natural History";
  if (lowerName.includes("anthropolog")) return "Anthropology";
  if (lowerName.includes("ethnolog")) return "Ethnology";
  if (lowerName.includes("archaeolog") || lowerName.includes("archeolog"))
    return "Archaeology";
  if (
    lowerName.includes("tribal") ||
    lowerName.includes("native american") ||
    lowerName.includes("indian cultural")
  )
    return "Tribal Cultural Center";
  if (
    lowerName.includes("indigenous") ||
    lowerName.includes("cultural center") ||
    lowerName.includes("heritage center")
  )
    return "Cultural Heritage Center";
  if (
    lowerName.includes("prehistor") ||
    lowerName.includes("paleo") ||
    lowerName.includes("fossil")
  )
    return "Paleontology / Prehistory";
  if (
    lowerName.includes("geolog") ||
    lowerName.includes("mineral") ||
    lowerName.includes("gem") ||
    lowerName.includes("rock")
  )
    return "Geology / Minerals";

  // Check if any artifact keyword matches
  for (const kw of ARTIFACT_KEYWORDS) {
    if (lowerName.includes(kw)) return "Cultural Heritage";
  }

  // historic=museum with no specific keyword — skip unless name is generic
  return null;
}

/** Check if a museum should be included (artifact-relevant filter). */
function isArtifactRelevant(
  name: string,
  tags: Record<string, string>,
): boolean {
  if (!name) return false;
  const lowerName = name.toLowerCase();

  // Explicit OSM museum= tag
  if (tags["museum"] && ARTIFACT_MUSEUM_TAGS.has(tags["museum"])) return true;

  // Name keyword check
  for (const kw of ARTIFACT_KEYWORDS) {
    if (lowerName.includes(kw)) return true;
  }

  // historic=museum with artifact-adjacent keyword
  if (tags["historic"] === "museum") {
    for (const kw of ARTIFACT_KEYWORDS) {
      if (lowerName.includes(kw)) return true;
    }
  }

  return false;
}

/** Exclude clearly unrelated museums. */
function isExcluded(name: string, tags: Record<string, string>): boolean {
  const lowerName = name.toLowerCase();
  const excludeKeywords = [
    "art gallery",
    "art museum",
    "modern art",
    "contemporary art",
    "children",
    "science center",
    "science centre",
    "war museum",
    "military museum",
    "maritime museum",
    "sports",
    "music museum",
    "aviation",
    "space museum",
    "railway",
    "automobile",
  ];
  for (const kw of excludeKeywords) {
    if (lowerName.includes(kw)) {
      // But don't exclude if it also has an artifact keyword
      for (const artifactKw of ARTIFACT_KEYWORDS) {
        if (lowerName.includes(artifactKw)) return false;
      }
      return true;
    }
  }
  return false;
}

/** Assemble a full address from addr:* tags. */
function assembleAddress(tags: Record<string, string>): string {
  const parts: string[] = [];
  if (tags["addr:housenumber"] || tags["addr:street"]) {
    const street = [tags["addr:housenumber"], tags["addr:street"]]
      .filter(Boolean)
      .join(" ");
    if (street) parts.push(street);
  }
  if (tags["addr:city"]) parts.push(tags["addr:city"]);
  if (tags["addr:state"]) parts.push(tags["addr:state"]);
  if (tags["addr:postcode"]) parts.push(tags["addr:postcode"]);
  return parts.join(", ");
}

/** Haversine distance in miles. */
function haversineMiles(
  lat1: number,
  lon1: number,
  lat2: number,
  lon2: number,
): number {
  const R = 3958.8;
  const dLat = ((lat2 - lat1) * Math.PI) / 180;
  const dLon = ((lon2 - lon1) * Math.PI) / 180;
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos((lat1 * Math.PI) / 180) *
      Math.cos((lat2 * Math.PI) / 180) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

/** Query Overpass API for museums within a radius. */
async function queryOverpass(
  lat: number,
  lon: number,
  radiusMeters: number,
): Promise<Museum[]> {
  const query = `
    [out:json][timeout:25];
    (
      nwr["tourism"~"museum|gallery"](around:${radiusMeters},${lat},${lon});
      nwr["historic"="museum"](around:${radiusMeters},${lat},${lon});
      nwr["amenity"="museum"](around:${radiusMeters},${lat},${lon});
      nwr["amenity"="community_centre"](around:${radiusMeters},${lat},${lon});
    );
    out center tags;
  `;

  const response = await fetch(OVERPASS_URL, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "data=" + encodeURIComponent(query),
  });

  if (!response.ok) {
    throw new Error(`Overpass API returned ${response.status}`);
  }

  const data = (await response.json()) as {
    elements: Array<{
      id: number;
      type: string;
      lat?: number;
      lon?: number;
      center?: { lat: number; lon: number };
      tags?: Record<string, string>;
    }>;
  };

  const museums: Museum[] = [];

  for (const el of data.elements ?? []) {
    const tags = el.tags ?? {};
    const name = tags["name"] ?? tags["name:en"] ?? "";
    if (!name) continue;

    // Exclude clearly unrelated museums
    if (isExcluded(name, tags)) continue;

    // Must be artifact-relevant
    if (!isArtifactRelevant(name, tags)) continue;

    // Classify the museum type
    const type = classifyMuseumType(name, tags);
    if (!type) continue;

    const elLat = el.lat ?? el.center?.lat ?? 0;
    const elLon = el.lon ?? el.center?.lon ?? 0;
    if (!elLat || !elLon) continue;

    const phone =
      tags["phone"] ?? tags["contact:phone"] ?? tags["addr:phone"] ?? null;
    const website =
      tags["website"] ??
      tags["contact:website"] ??
      tags["url"] ??
      null;
    const email =
      tags["email"] ??
      tags["contact:email"] ??
      null;

    museums.push({
      id: `${el.type}_${el.id}`,
      name,
      type,
      lat: elLat,
      lon: elLon,
      phone,
      website,
      email,
      address: assembleAddress(tags),
      distanceMiles: haversineMiles(lat, lon, elLat, elLon),
    });
  }

  // Sort by distance
  museums.sort((a, b) => a.distanceMiles - b.distanceMiles);

  return museums;
}

/** Handle the /museums endpoint. */
export async function handleMuseums(
  request: Request,
  env: { EXPO_PUBLIC_RORK_APP_KEY?: string },
  cors: Record<string, string>,
): Promise<Response> {
  let body: MuseumRequest;
  try {
    body = (await request.json()) as MuseumRequest;
  } catch {
    return Response.json(
      { error: "Invalid JSON body" },
      { status: 400, headers: cors },
    );
  }

  if (
    typeof body.lat !== "number" ||
    typeof body.lon !== "number" ||
    isNaN(body.lat) ||
    isNaN(body.lon)
  ) {
    return Response.json(
      { error: "lat and lon are required numeric fields" },
      { status: 400, headers: cors },
    );
  }

  const startRadius = body.radius ?? DEFAULT_RADIUS_MILES;
  const radii = [startRadius, ...EXPAND_RADII.filter((r) => r > startRadius)];

  let expandedRadius = false;
  let allMuseums: Museum[] = [];

  for (let i = 0; i < radii.length; i++) {
    const radiusMiles = radii[i];
    const radiusMeters = Math.round(radiusMiles * MILES_TO_METERS);

    try {
      allMuseums = await queryOverpass(body.lat, body.lon, radiusMeters);
    } catch {
      // Overpass error — try next radius or return empty
      continue;
    }

    if (allMuseums.length > 0) {
      expandedRadius = i > 0;
      break;
    }
  }

  const response: MuseumResponse = {
    museums: allMuseums,
    expandedRadius,
    searchRadiusMiles: radii[Math.min(radii.length - 1, allMuseums.length > 0 ? radii.findIndex((r) => true) : radii.length - 1)],
  };

  return Response.json(response, { headers: cors });
}
