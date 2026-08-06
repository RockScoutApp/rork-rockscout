/**
 * Free-text geocoding via Nominatim (OpenStreetMap).
 * Searches user-provided location strings and returns formatted address + lat/lng.
 * No API key required for low-volume use; we send a contact email in the User-Agent.
 */

export interface GeocodeResult {
  displayName: string;
  address: string;
  latitude: number;
  longitude: number;
}

interface NominatimResult {
  lat: string;
  lon: string;
  display_name: string;
  address?: Record<string, string>;
}

function formatAddress(result: NominatimResult): string {
  const addr = result.address;
  if (!addr) return result.display_name;
  const parts = [
    addr.house_number ? `${addr.house_number} ${addr.road || ""}` : addr.road,
    addr.city || addr.town || addr.village || addr.hamlet,
    addr.county,
    addr.state,
    addr.postcode,
    addr.country,
  ].filter(Boolean);
  // Remove duplicates while preserving order.
  return parts.filter((v, i, a) => a.indexOf(v) === i).join(", ");
}

export async function handleGeocode(
  request: Request,
  cors: Record<string, string>,
): Promise<Response> {
  try {
    const { query } = await request.json() as { query?: string };
    if (!query || query.trim().length < 2) {
      return Response.json(
        { error: "Query must be at least 2 characters" },
        { status: 400, headers: cors },
      );
    }

    const url = new URL("https://nominatim.openstreetmap.org/search");
    url.searchParams.set("q", query.trim());
    url.searchParams.set("format", "json");
    url.searchParams.set("addressdetails", "1");
    url.searchParams.set("limit", "5");

    const res = await fetch(url.toString(), {
      headers: {
        "User-Agent": "RockScout/1.0 (support@rockscout.app)",
        "Accept-Language": "en-US",
      },
    });

    if (!res.ok) {
      console.error("[geocode] Nominatim error", res.status, await res.text());
      return Response.json(
        { error: "Geocoding service unavailable" },
        { status: 503, headers: cors },
      );
    }

    const raw = await res.json() as NominatimResult[];
    const results: GeocodeResult[] = raw.map((r) => ({
      displayName: r.display_name,
      address: formatAddress(r),
      latitude: parseFloat(r.lat),
      longitude: parseFloat(r.lon),
    }));

    return Response.json({ results }, { headers: cors });
  } catch (err) {
    console.error("[geocode] error", err);
    return Response.json(
      { error: "Failed to geocode location" },
      { status: 500, headers: cors },
    );
  }
}
