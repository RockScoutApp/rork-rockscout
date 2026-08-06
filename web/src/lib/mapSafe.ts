import L from "leaflet";

/** Maximum valid latitude and longitude bounds for WGS-84 coordinates. */
const MAX_LAT = 90;
const MAX_LNG = 180;

/**
 * Returns true if the coordinate is a finite number within the WGS-84 bounds.
 * Used to guard every point that is projected into a Leaflet marker or camera.
 */
export function isValidCoordinate(lat: unknown, lng: unknown): boolean {
  return (
    typeof lat === "number" &&
    typeof lng === "number" &&
    Number.isFinite(lat) &&
    Number.isFinite(lng) &&
    Math.abs(lat) <= MAX_LAT &&
    Math.abs(lng) <= MAX_LNG
  );
}

/** Safely run a Leaflet map operation, catching errors so one bad marker cannot crash the screen. */
export function runMapSafe<T>(label: string, fn: () => T): T | undefined {
  try {
    return fn();
  } catch (err) {
    console.error(`[MapSafe] ${label}:`, err);
    return undefined;
  }
}

/** Create a Leaflet marker only if the coordinates are valid and the map is still attached. */
export function safeMarker(
  map: L.Map | null,
  lat: number,
  lng: number,
  options?: L.MarkerOptions,
): L.Marker | null {
  if (!map || !isValidCoordinate(lat, lng)) return null;
  return runMapSafe("safeMarker", () => L.marker([lat, lng], options)) ?? null;
}

/** Remove a layer from a Leaflet map, catching errors if the map is already disposed. */
export function safeRemoveLayer(map: L.Map | null, layer: L.Layer | null): void {
  if (!map || !layer) return;
  runMapSafe("safeRemoveLayer", () => layer.removeFrom(map));
}

/** Add a layer to a Leaflet map, catching errors if the map is already disposed. */
export function safeAddLayer(map: L.Map | null, layer: L.Layer | null): void {
  if (!map || !layer) return;
  runMapSafe("safeAddLayer", () => layer.addTo(map));
}

/** Wrapper for map.remove() that guards against double-removal. */
export function safeRemoveMap(map: L.Map | null): void {
  if (!map) return;
  runMapSafe("safeRemoveMap", () => map.remove());
}

/** Wrapper for invalidateSize that guards against disposed maps. */
export function safeInvalidateSize(map: L.Map | null): void {
  if (!map) return;
  runMapSafe("safeInvalidateSize", () => map.invalidateSize());
}

/** Wrapper for setView that validates coordinates and guards against disposed maps. */
export function safeSetView(
  map: L.Map | null,
  lat: number,
  lng: number,
  zoom: number,
  options?: L.ZoomPanOptions,
): void {
  if (!map || !isValidCoordinate(lat, lng)) return;
  runMapSafe("safeSetView", () => map.setView([lat, lng], zoom, options));
}

/** Wrapper for fitBounds that guards against disposed maps. */
export function safeFitBounds(map: L.Map | null, bounds: L.LatLngBoundsExpression): void {
  if (!map) return;
  runMapSafe("safeFitBounds", () => map.fitBounds(bounds));
}
