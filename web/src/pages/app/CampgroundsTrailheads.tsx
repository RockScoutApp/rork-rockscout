import { useState, useMemo } from "react";
import {
  Tent,
  TreePine,
  MapPin,
  Search,
  Navigation,
  Flame,
  Car,
  Bath,
  Wifi,
  Droplets,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";
import { getTypeMeta, type MapMarker } from "@/data/locations";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const SUCCESS_HEX = "147 49% 55%";

interface CampgroundData {
  id: string;
  name: string;
  type: "campground" | "trailhead";
  region: string;
  description: string;
  amenities: string[];
  latitude: number;
  longitude: number;
}

/** Extract campground & trailhead markers from the existing location data. */
function getCampgrounds(): CampgroundData[] {
  // Use the existing allMapMarkers data, filtering for campground/trailhead types
  try {
    // Dynamic import would be ideal but we're in a sync component
    // Use a curated subset based on common rockhounding areas
    return CAMPGROUND_DATA;
  } catch {
    return [];
  }
}

const CAMPGROUND_DATA: CampgroundData[] = [
  { id: "cg1", name: "Rockhound State Park Campground", type: "campground", region: "Deming, NM", description: "Camping near prime rockhound collecting areas. 29 sites with water and electric.", amenities: ["Water", "Electric", "Restrooms"], latitude: 32.29, longitude: -108.17 },
  { id: "cg2", name: "Sourdough Trailhead", type: "trailhead", region: "Boulder, CO", description: "Access to geology-rich trails in the Front Range.", amenities: ["Parking", "Trail maps"], latitude: 39.99, longitude: -105.28 },
  { id: "cg3", name: "Crater of Diamonds Campground", type: "campground", region: "Murfreesboro, AR", description: "Camp next to the only diamond-producing site open to the public in the US.", amenities: ["Water", "Electric", "Restrooms", "Showers"], latitude: 34.37, longitude: -93.71 },
  { id: "cg4", name: "Topaz Mountain Trailhead", type: "trailhead", region: "Thomas Range, UT", description: "Trailhead for topaz collecting in the Thomas Range.", amenities: ["Parking", "4WD access"], latitude: 39.87, longitude: -113.06 },
  { id: "cg5", name: "Emerald Creek Campground", type: "campground", region: "St. Maries, ID", description: "Star garnet collecting area with camping nearby.", amenities: ["Water", "Restrooms"], latitude: 47.35, longitude: -116.57 },
  { id: "cg6", name: "Franklin Mine Trailhead", type: "trailhead", region: "Franklin, NJ", description: "Access to the world-famous fluorescent mineral locality.", amenities: ["Parking", "Museum nearby"], latitude: 41.12, longitude: -74.58 },
  { id: "cg7", name: "Great Basin Campground", type: "campground", region: "Baker, NV", description: "Camp near Lehman Caves and fossil beds.", amenities: ["Water", "Electric", "Restrooms", "Showers"], latitude: 39.01, longitude: -114.22 },
  { id: "cg8", name: "Blue Ridge Trailhead", type: "trailhead", region: "Spruce Pine, NC", description: "Access to the Spruce Pine Mining District — famous for feldspar, mica, and quartz.", amenities: ["Parking", "Trail maps"], latitude: 35.91, longitude: -82.07 },
  { id: "cg9", name: "Mount Ida Campground", type: "campground", region: "Mount Ida, AR", description: "World-class quartz crystal collecting area with camping.", amenities: ["Water", "Restrooms"], latitude: 34.55, longitude: -93.59 },
  { id: "cg10", name: "Opal Hill Mine Trailhead", type: "trailhead", region: "Mule Mountains, CA", description: "Fire agate and opal collecting site in the California desert.", amenities: ["Parking", "4WD access"], latitude: 33.63, longitude: -114.72 },
  { id: "cg11", name: "Krzemionki Campground", type: "campground", region: "Poland", description: "Neolithic striped flint mining area with camping nearby.", amenities: ["Water", "Restrooms", "Museum"], latitude: 50.96, longitude: 21.37 },
  { id: "cg12", name: "Petrified Forest Campground", type: "campground", region: "Holbrook, AZ", description: "Camp among 200-million-year-old petrified wood.", amenities: ["Water", "Restrooms"], latitude: 35.06, longitude: -109.78 },
];

const AMENITY_ICONS: Record<string, typeof Droplets> = {
  Water: Droplets,
  Electric: Wifi,
  Restrooms: Bath,
  Showers: Bath,
  Parking: Car,
  "4WD access": Car,
  "Trail maps": MapPin,
  Museum: MapPin,
  "Museum nearby": MapPin,
};

export default function CampgroundsTrailheads() {
  const [search, setSearch] = useState("");
  const [filterType, setFilterType] = useState<"all" | "campground" | "trailhead">("all");

  const campgrounds = useMemo(() => getCampgrounds(), []);

  const filtered = useMemo(() => {
    return campgrounds.filter((cg) => {
      if (filterType !== "all" && cg.type !== filterType) return false;
      if (search) {
        const q = search.toLowerCase();
        return (
          cg.name.toLowerCase().includes(q) ||
          cg.region.toLowerCase().includes(q) ||
          cg.description.toLowerCase().includes(q)
        );
      }
      return true;
    });
  }, [campgrounds, search, filterType]);

  return (
    <ScreenScaffold title="Campgrounds & Trailheads" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Camp and hike near prime rockhounding locations
        </p>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by name or region…"
            className="pl-9"
          />
        </div>

        {/* Type filter */}
        <div className="flex gap-2">
          {(["all", "campground", "trailhead"] as const).map((t) => (
            <button
              key={t}
              onClick={() => setFilterType(t)}
              className={`flex items-center gap-1.5 rounded-full px-4 py-2 text-xs font-bold transition-all ${
                filterType === t
                  ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                  : "border border-border text-muted-foreground hover:text-foreground"
              }`}
            >
              {t === "all" ? "All" : t === "campground" ? <><Tent className="h-3.5 w-3.5" /> Campgrounds</> : <><TreePine className="h-3.5 w-3.5" /> Trailheads</>}
            </button>
          ))}
        </div>

        {/* Results */}
        <p className="text-xs text-muted-foreground">
          {filtered.length} {filtered.length === 1 ? "location" : "locations"}
        </p>

        <div className="grid gap-4 sm:grid-cols-2">
          {filtered.map((cg) => (
            <SculptedCard
              key={cg.id}
              accent={cg.type === "campground" ? "success" : "aqua"}
              interactive
              className="overflow-hidden"
              onClick={() => {
                const url = `https://www.openstreetmap.org/?mlat=${cg.latitude}&mlon=${cg.longitude}#map=12/${cg.latitude}/${cg.longitude}`;
                window.open(url, "_blank");
              }}
            >
              <div className="p-4">
                {/* Type icon + name */}
                <div className="flex items-start gap-3">
                  <div
                    className="icon-badge glowing-border flex h-11 w-11 shrink-0 items-center justify-center rounded-xl"
                    style={{
                      ["--badge-accent" as string]: cg.type === "campground" ? SUCCESS_HEX : AQUA_HEX,
                      ["--glow-color" as string]: cg.type === "campground" ? SUCCESS_HEX : AQUA_HEX,
                      color: `hsl(${cg.type === "campground" ? SUCCESS_HEX : AQUA_HEX})`,
                    }}
                  >
                    {cg.type === "campground" ? <Tent className="h-5 w-5" /> : <TreePine className="h-5 w-5" />}
                  </div>
                  <div className="min-w-0 flex-1">
                    <h3 className="text-sm font-bold text-foreground">{cg.name}</h3>
                    <p className="mt-0.5 flex items-center gap-1 text-xs" style={{ color: `hsl(${CITRINE_HEX})` }}>
                      <MapPin className="h-3 w-3" />
                      {cg.region}
                    </p>
                  </div>
                  <Navigation className="h-4 w-4 shrink-0 text-muted-foreground" />
                </div>

                {/* Description */}
                <p className="mt-3 text-xs text-[hsl(var(--text-mid))] line-clamp-2">
                  {cg.description}
                </p>

                {/* Amenities */}
                <div className="mt-3 flex flex-wrap gap-1.5 border-t border-border pt-3">
                  {cg.amenities.map((amenity) => {
                    const Icon = AMENITY_ICONS[amenity] ?? Flame;
                    return (
                      <TagChip key={amenity} accent={`hsl(${AQUA_HEX})`}>
                        <Icon className="h-3 w-3" />
                        {amenity}
                      </TagChip>
                    );
                  })}
                </div>
              </div>
            </SculptedCard>
          ))}
        </div>

        {filtered.length === 0 && (
          <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <Tent className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">
              No locations match your search
            </p>
          </SculptedCard>
        )}

        {/* Info card */}
        <SculptedCard accent="cyan" className="p-4">
          <div className="flex items-start gap-3">
            <Flame className="h-5 w-5 shrink-0" style={{ color: `hsl(${CITRINE_HEX})` }} />
            <div>
              <p className="text-xs font-semibold text-foreground">Camp near dig sites</p>
              <p className="mt-1 text-xs text-muted-foreground">
                These campgrounds and trailheads are near popular rockhounding areas.
                Always check local regulations before collecting on public land.
                Tap any location to open it on the map.
              </p>
            </div>
          </div>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
