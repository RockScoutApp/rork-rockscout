import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { MapPin, Search, X, Loader2 } from "lucide-react";
import { Input } from "@/components/ui/input";
import {
  digSites,
  blmDigSites,
  stateParks,
  getTypeLabel,
  getTypeMeta,
  type DigSite,
} from "@/data/locations";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";

interface LocationEntry {
  id: string;
  name: string;
  region: string;
  summary: string;
  markerType: string;
}

const ALL_LOCATIONS: LocationEntry[] = [
  ...digSites.map((s) => ({
    id: s.id,
    name: s.name,
    region: s.region,
    summary: s.summary,
    markerType: s.type,
  })),
  ...blmDigSites.map((s) => ({
    id: s.id,
    name: s.name,
    region: s.region,
    summary: s.whatToFind,
    markerType: "BLM_DIG_SITE",
  })),
  ...stateParks.map((s) => ({
    id: s.id,
    name: s.name,
    region: s.region,
    summary: s.description,
    markerType: "STATE_PARK",
  })),
];

const FILTER_TYPES = [
  { value: "ALL", label: "All" },
  { value: "PUBLIC_DIG", label: "Public Digs" },
  { value: "MINE", label: "Mines" },
  { value: "QUARRY", label: "Quarries" },
  { value: "BEACH", label: "Beaches" },
  { value: "RIVER", label: "Rivers" },
  { value: "DESERT", label: "Deserts" },
  { value: "ROCK_SHOP", label: "Rock Shops" },
  { value: "METAPHYSICAL", label: "Metaphysical" },
  { value: "BLM_DIG_SITE", label: "BLM Sites" },
  { value: "STATE_PARK", label: "State Parks" },
];

export default function Locations() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [typeFilter, setTypeFilter] = useState<string>("ALL");

  const filtered = useMemo(() => {
    let result = ALL_LOCATIONS;
    if (typeFilter !== "ALL") {
      result = result.filter((l) => l.markerType === typeFilter);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(
        (l) =>
          l.name.toLowerCase().includes(q) ||
          l.region.toLowerCase().includes(q) ||
          l.summary.toLowerCase().includes(q),
      );
    }
    return result;
  }, [search, typeFilter]);

  return (
    <ScreenScaffold title="Dig Sites & Locations">
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        {ALL_LOCATIONS.length} collecting sites, mines, parks, and shops
      </p>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by name, region, or description..."
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

      <div className="flex flex-wrap gap-2">
        {FILTER_TYPES.map((ft) => (
          <button
            key={ft.value}
            onClick={() => setTypeFilter(ft.value)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              typeFilter === ft.value
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {ft.label}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {filtered.map((loc) => {
          const meta = getTypeMeta(loc.markerType);
          return (
            <SculptedCard key={loc.id} accent="aqua" interactive className="overflow-hidden"
              onClick={() => navigate(`/app/locations/${loc.id}`)}>
              <div className="flex flex-col gap-2 p-4">
                <div className="flex items-start justify-between gap-2">
                  <span className="text-2xl">{meta.emoji}</span>
                  <TagChip accent={meta.color}>{getTypeLabel(loc.markerType)}</TagChip>
                </div>
                <h3 className="font-display text-sm font-bold text-foreground">{loc.name}</h3>
                <p className="flex items-center gap-1 text-xs" style={{ color: `hsl(${AQUA_HEX})` }}>
                  <MapPin className="h-3 w-3" />{loc.region}
                </p>
                <p className="line-clamp-2 text-xs text-[hsl(var(--text-mid))]">{loc.summary}</p>
              </div>
            </SculptedCard>
          );
        })}
      </div>

      {filtered.length === 0 && (
        <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
          <MapPin className="h-10 w-10 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No locations found. Try a different search or filter.</p>
        </SculptedCard>
      )}
     </div>
    </ScreenScaffold>
  );
}
