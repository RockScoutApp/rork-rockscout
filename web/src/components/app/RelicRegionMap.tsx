import { useRef, useEffect, useMemo } from "react";
import L from "leaflet";
import { MapPin } from "lucide-react";
import { getRegionCoords, type RegionCoord } from "@/data/relicRegions";
import { isValidCoordinate, runMapSafe, safeRemoveMap, safeInvalidateSize, safeFitBounds } from "@/lib/mapSafe";

// Fix Leaflet's default icon path (same pattern as SharedSpot.tsx)
delete (L.Icon.Default.prototype as unknown as { _getIconUrl?: unknown })._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

const AQUA_HEX = "20 62% 65%";

interface RelicRegionMapProps {
  whereFound: string[];
  accentHex?: string;
}

/**
 * A compact Leaflet map that places markers for each region in the relic's
 * `whereFound` list. The map auto-fits to show all markers. Markers use
 * a military-themed divIcon (🎖️) and bind popups with the region label.
 *
 * Only rendered for war relics (domain === "war_relic"). Prehistoric
 * artifacts don't get a map — their `whereFound` values are broad
 * geographic regions (continents, culture areas) that don't benefit
 * from point markers.
 */
export default function RelicRegionMap({ whereFound, accentHex }: RelicRegionMapProps) {
  const mapRef = useRef<L.Map | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const regions = useMemo(() => getRegionCoords(whereFound), [whereFound]);

  // Don't render the map if no regions resolve to coordinates
  if (regions.length === 0) return null;

  const accent = accentHex ?? AQUA_HEX;

  return (
    <div className="dark-card sculpted-raised overflow-hidden rounded-lg">
      {/* Header */}
      <div className="flex items-center gap-2 border-b border-border/40 px-4 py-2.5">
        <MapPin className="h-4 w-4" style={{ color: `hsl(${accent})` }} />
        <span className="text-sm font-bold text-foreground">Discovery Regions</span>
        <span className="ml-auto text-xs text-muted-foreground">
          {regions.length} region{regions.length !== 1 ? "s" : ""}
        </span>
      </div>

      {/* Map container */}
      <div
        ref={(el) => {
          containerRef.current = el;
          // Initialize map when the container is mounted
          if (el && !mapRef.current) {
            const map = L.map(el, {
              center: [38.9, -77.35],
              zoom: 5,
              zoomControl: true,
              attributionControl: false,
              scrollWheelZoom: false,
            });

            L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
              attribution: '&copy; OpenStreetMap',
              maxZoom: 18,
            }).addTo(map);

            // Add markers for each region
            const bounds: L.LatLngExpression[] = [];
            for (const region of regions) {
              if (!isValidCoordinate(region.lat, region.lng)) continue;

              const icon = L.divIcon({
                className: "relic-region-marker",
                html: `<div style="font-size: 22px; filter: drop-shadow(0 0 6px hsl(${accent} / 0.5));">🎖️</div>`,
                iconSize: [26, 26],
                iconAnchor: [13, 22],
              });

              runMapSafe("relic marker", () => {
                L.marker([region.lat, region.lng], { icon })
                  .addTo(map)
                  .bindPopup(
                    `<div style="font-size:13px;font-weight:600;">${region.label}</div>`,
                  );
              });

              bounds.push([region.lat, region.lng]);
            }

            // Fit bounds to show all markers
            if (bounds.length > 1) {
              safeFitBounds(map, L.latLngBounds(bounds as L.LatLngTuple[]).pad(0.15));
            } else if (bounds.length === 1) {
              runMapSafe("set view single", () => map.setView(bounds[0] as L.LatLngTuple, 6));
            }

            mapRef.current = map;

            // Keep the map sized correctly
            const ro = new ResizeObserver(() => safeInvalidateSize(map));
            ro.observe(el);

            // Store cleanup on the element
            (el as unknown as { __cleanup?: () => void }).__cleanup = () => {
              ro.disconnect();
              safeRemoveMap(map);
              mapRef.current = null;
            };
          }
        }}
        style={{ height: "260px", width: "100%", backgroundColor: "#1a1812" }}
      />

      {/* Region list below the map */}
      <div className="flex flex-wrap gap-1.5 px-4 py-3">
        {regions.map((r) => (
          <span
            key={r.label}
            className="rounded-full border px-2.5 py-0.5 text-[11px] font-medium"
            style={{
              borderColor: `hsl(${accent} / 0.3)`,
              backgroundColor: `hsl(${accent} / 0.08)`,
              color: `hsl(${accent})`,
            }}
          >
            {r.label}
          </span>
        ))}
      </div>
    </div>
  );
}
