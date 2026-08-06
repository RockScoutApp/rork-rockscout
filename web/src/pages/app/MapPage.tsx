import { useRef, useEffect, useState, useMemo, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import L from "leaflet";
import {
  Search,
  Loader2,
  LocateFixed,
  X,
  Filter,
  Layers,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { toast } from "sonner";
import { isValidCoordinate, runMapSafe, safeMarker, safeRemoveLayer, safeRemoveMap, safeSetView } from "@/lib/mapSafe";
import {
  allMapMarkers,
  getTypeMeta,
  getTypeLabel,
  distanceMiles,
  getCurrentPosition,
  type MapMarker,
} from "@/data/locations";

// Fix Leaflet's default icon path issue with bundlers
delete (L.Icon.Default.prototype as unknown as { _getIconUrl?: unknown })._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl: "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

/** All location types that can be toggled on the map. */
const TYPE_FILTERS = [
  { value: "PUBLIC_DIG", label: "Public Dig" },
  { value: "MINE", label: "Mines" },
  { value: "QUARRY", label: "Quarries" },
  { value: "BEACH", label: "Beaches" },
  { value: "RIVER", label: "Rivers" },
  { value: "DESERT", label: "Desert" },
  { value: "ROCK_SHOP", label: "Rock Shops" },
  { value: "METAPHYSICAL", label: "Metaphysical" },
  { value: "LAPIDARY_CLUB", label: "Clubs" },
  { value: "BLM_DIG_SITE", label: "BLM Sites" },
  { value: "TRAILHEAD", label: "Trailheads" },
  { value: "CAMPGROUND", label: "Campgrounds" },
  { value: "STATE_PARK", label: "State Parks" },
] as const;

/** Create a colored emoji marker icon for Leaflet. */
const createEmojiIcon = (emoji: string, color: string) =>
  L.divIcon({
    className: "rockscout-map-marker",
    html: `<div style="
      display:flex;align-items:center;justify-content:center;
      width:30px;height:30px;border-radius:50% 50% 50% 0;
      background:${color};transform:rotate(-45deg);
      border:2px solid white;box-shadow:0 2px 6px rgba(0,0,0,0.4);
      font-size:14px;
    "><span style="transform:rotate(45deg);">${emoji}</span></div>`,
    iconSize: [30, 30],
    iconAnchor: [15, 30],
    popupAnchor: [0, -28],
  });

/** Create a "you are here" marker icon. */
const createUserIcon = () =>
  L.divIcon({
    className: "rockscout-user-marker",
    html: `<div style="
      width:16px;height:16px;border-radius:50%;
      background:#4A90D9;border:3px solid white;
      box-shadow:0 0 0 8px rgba(74,144,217,0.25),0 2px 6px rgba(0,0,0,0.3);
    "></div>`,
    iconSize: [16, 16],
    iconAnchor: [8, 8],
  });

interface UserLocation {
  lat: number;
  lng: number;
}

export default function MapPage() {
  const navigate = useNavigate();
  const mapRef = useRef<HTMLDivElement>(null);
  const mapInstance = useRef<L.Map | null>(null);
  const markersLayer = useRef<L.LayerGroup | null>(null);
  const userMarker = useRef<L.Marker | null>(null);

  const [search, setSearch] = useState("");
  const [showFilters, setShowFilters] = useState(false);
  const [activeTypes, setActiveTypes] = useState<Set<string>>(
    new Set(TYPE_FILTERS.map((f) => f.value)),
  );
  const [userLocation, setUserLocation] = useState<UserLocation | null>(null);
  const [locating, setLocating] = useState(false);
  const [nearbyOnly, setNearbyOnly] = useState(false);
  const [selectedMarker, setSelectedMarker] = useState<MapMarker | null>(null);

  // ── Initialize the Leaflet map once ──
  useEffect(() => {
    if (!mapRef.current || mapInstance.current) return;

    const map = L.map(mapRef.current, {
      center: [39.5, -98.35], // USA center
      zoom: 4,
      zoomControl: true,
      scrollWheelZoom: true,
    });

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
      maxZoom: 19,
    }).addTo(map);

    markersLayer.current = L.layerGroup().addTo(map);
    mapInstance.current = map;

    // Invalidate size after mount to handle flex layout
    setTimeout(() => map.invalidateSize(), 100);

    // Handle container resizes so the map stays correctly sized after tab changes or soft refresh.
    const resizeObserver = new ResizeObserver(() => {
      runMapSafe("resize", () => map.invalidateSize());
    });
    if (mapRef.current) resizeObserver.observe(mapRef.current);

    return () => {
      resizeObserver.disconnect();
      safeRemoveMap(map);
      mapInstance.current = null;
    };
  }, []);

  // ── Filtered markers ──
  const visibleMarkers = useMemo(() => {
    let markers = allMapMarkers.filter((m) => activeTypes.has(m.type));

    if (search.trim()) {
      const q = search.toLowerCase();
      markers = markers.filter(
        (m) =>
          m.name.toLowerCase().includes(q) ||
          m.region.toLowerCase().includes(q),
      );
    }

    if (nearbyOnly && userLocation) {
      markers = markers
        .map((m) => ({
          ...m,
          _dist: distanceMiles(
            userLocation.lat,
            userLocation.lng,
            m.latitude,
            m.longitude,
          ),
        }))
        .filter((m) => m._dist <= 250)
        .sort((a, b) => a._dist - b._dist) as (MapMarker & { _dist: number })[];
    }

    return markers;
  }, [activeTypes, search, nearbyOnly, userLocation]);

  // ── Render markers whenever the filtered set changes ──
  useEffect(() => {
    if (!markersLayer.current || !mapInstance.current) return;
    markersLayer.current.clearLayers();

    // Limit to 500 markers for performance, and only render markers with valid coordinates.
    const toRender = visibleMarkers.slice(0, 500).filter((m) => isValidCoordinate(m.latitude, m.longitude));

    for (const marker of toRender) {
      const meta = getTypeMeta(marker.type);
      const icon = createEmojiIcon(meta.emoji, meta.color);
      const lm = safeMarker(mapInstance.current, marker.latitude, marker.longitude, { icon });
      if (!lm) continue;
      lm.bindPopup(
        `<div style="min-width:180px;">
          <div style="font-weight:600;font-size:14px;margin-bottom:2px;">${marker.name}</div>
          <div style="font-size:12px;color:#666;margin-bottom:4px;">${marker.region}</div>
          <div style="font-size:11px;color:#888;">${getTypeLabel(marker.type)}</div>
        </div>`,
      );
      lm.on("click", () => setSelectedMarker(marker));
      runMapSafe("addLayer", () => markersLayer.current?.addLayer(lm));
    }
  }, [visibleMarkers]);

  // ── Locate me ──
  const handleLocate = useCallback(async () => {
    setLocating(true);
    try {
      const pos = await getCurrentPosition();
      setUserLocation(pos);

      if (mapInstance.current) {
        if (userMarker.current) {
          userMarker.current.setLatLng([pos.lat, pos.lng]);
        } else {
          userMarker.current = safeMarker(mapInstance.current, pos.lat, pos.lng, {
            icon: createUserIcon(),
          });
        }
        safeSetView(mapInstance.current, pos.lat, pos.lng, 10, { animate: true });
      }
      toast.success("Found your location");
    } catch (err) {
      toast.error(
        err instanceof Error ? err.message : "Could not get your location",
      );
    } finally {
      setLocating(false);
    }
  }, []);

  const toggleType = (type: string) => {
    setActiveTypes((prev) => {
      const next = new Set(prev);
      if (next.has(type)) {
        next.delete(type);
      } else {
        next.add(type);
      }
      return next;
    });
  };

  const clearTypeFilters = () => {
    setActiveTypes(new Set(TYPE_FILTERS.map((f) => f.value)));
    setSearch("");
    setNearbyOnly(false);
  };

  const openInMaps = (marker: MapMarker) => {
    if (!isValidCoordinate(marker.latitude, marker.longitude)) return;
    const url = `https://www.google.com/maps/search/?api=1&query=${marker.latitude},${marker.longitude}`;
    window.open(url, "_blank", "noopener,noreferrer");
  };

  // The nearby list is always renderable when we have markers; on desktop it
  // sits in a side column next to the map, on mobile it stacks below.
  const showList = visibleMarkers.length > 0;

  return (
    <div className="space-y-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Maps & Dig Sites
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            {visibleMarkers.length} of {allMapMarkers.length} locations
            {nearbyOnly && userLocation && " within 250 miles"}
          </p>
        </div>
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowFilters((v) => !v)}
            className="gap-2"
          >
            <Filter className="h-4 w-4" />
            <span className="hidden sm:inline">Filters</span>
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={handleLocate}
            disabled={locating}
            className="gap-2"
          >
            {locating ? (
              <Loader2 className="h-4 w-4 animate-spin" />
            ) : (
              <LocateFixed className="h-4 w-4" />
            )}
            <span className="hidden sm:inline">Near me</span>
          </Button>
        </div>
      </div>

      {/* Search bar */}
      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search dig sites, parks, shops by name or area..."
          className="pl-10"
        />
        {search && (
          <button
            onClick={() => setSearch("")}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
          >
            <X className="h-4 w-4" />
          </button>
        )}
      </div>

      {/* Filter panel */}
      {showFilters && (
        <div className="space-y-3 dark-card sculpted-raised rounded-xl p-4">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium text-foreground">
              Location types
            </span>
            <button
              onClick={clearTypeFilters}
              className="text-xs text-primary hover:underline"
            >
              Reset all
            </button>
          </div>
          <div className="flex flex-wrap gap-2">
            {TYPE_FILTERS.map((f) => {
              const meta = getTypeMeta(f.value);
              const active = activeTypes.has(f.value);
              return (
                <button
                  key={f.value}
                  onClick={() => toggleType(f.value)}
                  className={cn(
                    "flex items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-medium transition-all",
                    active
                      ? "bg-primary/15 text-primary ring-1 ring-primary/30"
                      : "bg-muted text-muted-foreground hover:bg-muted/70",
                  )}
                >
                  <span>{meta.emoji}</span>
                  {f.label}
                </button>
              );
            })}
          </div>
          {userLocation && (
            <label className="flex items-center gap-2 pt-1 text-sm text-muted-foreground">
              <input
                type="checkbox"
                checked={nearbyOnly}
                onChange={(e) => setNearbyOnly(e.target.checked)}
                className="h-4 w-4 rounded border-border accent-primary"
              />
              Within 250 miles of my location
            </label>
          )}
        </div>
      )}

      {/* Desktop split: map on the left, scrollable location list on the right.
          On mobile the map stacks on top and the list flows below. */}
      <div className="grid gap-4 lg:grid-cols-[1fr_360px] xl:grid-cols-[1fr_400px]">
        {/* Map column */}
        <div className="space-y-4">
          <div className="relative overflow-hidden rounded-xl border border-border">
            <div
              ref={mapRef}
              className="h-[400px] w-full md:h-[550px] lg:h-[640px]"
            />
            {visibleMarkers.length > 500 && (
              <div className="absolute bottom-2 left-1/2 -translate-x-1/2 rounded-full bg-black/70 px-3 py-1 text-xs text-white backdrop-blur">
                Showing 500 of {visibleMarkers.length} — zoom in or filter to
                see more
              </div>
            )}
          </div>

          {/* Selected marker info card */}
          {selectedMarker && (
            <div className="space-y-3 dark-card sculpted-raised rounded-xl p-4">
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-start gap-3">
                  <div
                    className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg text-lg"
                    style={{
                      backgroundColor: `${getTypeMeta(selectedMarker.type).color}20`,
                    }}
                  >
                    {getTypeMeta(selectedMarker.type).emoji}
                  </div>
                  <div>
                    <h3 className="font-display font-semibold text-foreground">
                      {selectedMarker.name}
                    </h3>
                    <p className="text-sm text-muted-foreground">
                      {selectedMarker.region}
                    </p>
                    <p className="mt-0.5 text-xs text-muted-foreground">
                      {getTypeLabel(selectedMarker.type)}
                    </p>
                  </div>
                </div>
                <button
                  onClick={() => setSelectedMarker(null)}
                  className="text-muted-foreground hover:text-foreground"
                >
                  <X className="h-4 w-4" />
                </button>
              </div>
              <div className="flex gap-2">
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => openInMaps(selectedMarker)}
                  className="gap-2"
                >
                  <Layers className="h-4 w-4" />
                  Directions
                </Button>
                <Button
                  size="sm"
                  onClick={() =>
                    navigate(`/app/locations/${selectedMarker.id}`)
                  }
                >
                  View details
                </Button>
              </div>
            </div>
          )}
        </div>

        {/* Location list column — visible on desktop alongside the map,
            and stacked below the map on mobile. */}
        {showList && (
          <div className="flex flex-col dark-card sculpted-raised rounded-xl">
            <div className="sticky top-0 z-10 flex items-center justify-between border-b border-border bg-card/95 px-4 py-3 backdrop-blur">
              <h3 className="font-display text-sm font-semibold text-foreground">
                {nearbyOnly && userLocation
                  ? "Nearest locations"
                  : "All locations"}
              </h3>
              <span className="text-xs text-muted-foreground">
                {Math.min(visibleMarkers.length, 100)} shown
              </span>
            </div>
            <div className="max-h-[640px] space-y-2 overflow-y-auto p-3 lg:max-h-[640px]">
              {(visibleMarkers as (MapMarker & { _dist?: number })[])
                .slice(0, 100)
                .map((marker) => {
                  const meta = getTypeMeta(marker.type);
                  const isSelected = selectedMarker?.id === marker.id;
                  return (
                    <button
                      key={marker.id}
                      onClick={() => {
                        setSelectedMarker(marker);
                        mapInstance.current?.setView(
                          [marker.latitude, marker.longitude],
                          12,
                          { animate: true },
                        );
                      }}
                      className={cn(
                        "flex w-full items-center gap-3 rounded-lg border p-3 text-left transition-colors",
                        isSelected
                          ? "border-primary/50 bg-primary/5"
                          : "border-border bg-card hover:border-primary/40",
                      )}
                    >
                      <div
                        className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg text-base"
                        style={{ backgroundColor: `${meta.color}20` }}
                      >
                        {meta.emoji}
                      </div>
                      <div className="min-w-0 flex-1">
                        <p className="truncate text-sm font-medium text-foreground">
                          {marker.name}
                        </p>
                        <p className="truncate text-xs text-muted-foreground">
                          {marker.region}
                        </p>
                      </div>
                      {marker._dist != null && (
                        <span className="shrink-0 text-xs font-medium text-primary">
                          {marker._dist < 1
                            ? `${Math.round(marker._dist * 5280)} ft`
                            : `${Math.round(marker._dist)} mi`}
                        </span>
                      )}
                    </button>
                  );
                })}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
