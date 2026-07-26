import { useState, useMemo } from "react";
import { Search, X, Mountain, ExternalLink } from "lucide-react";
import { Input } from "@/components/ui/input";
import { blmDigSites, trailheads, campgrounds } from "@/data/locations";

interface BlmSite {
  id: string;
  name: string;
  region: string;
  latitude: number;
  longitude: number;
  whatToFind: string;
  directions: string;
  facilities: string;
  feeInfo: string;
  difficulty: string;
  website: string | null;
  type: "site" | "trailhead" | "campground";
}

const allBlmSites: BlmSite[] = [
  ...blmDigSites.map((s) => ({ ...s, type: "site" as const })),
  ...trailheads.map((s) => ({
    id: s.id,
    name: s.name,
    region: s.region,
    latitude: s.latitude,
    longitude: s.longitude,
    whatToFind: s.description,
    directions: "",
    facilities: "",
    feeInfo: "Free",
    difficulty: "",
    website: null as string | null,
    type: "trailhead" as const,
  })),
  ...campgrounds.map((s) => ({
    id: s.id,
    name: s.name,
    region: s.region,
    latitude: s.latitude,
    longitude: s.longitude,
    whatToFind: s.description,
    directions: "",
    facilities: "",
    feeInfo: s.feeInfo,
    difficulty: "",
    website: null as string | null,
    type: "campground" as const,
  })),
];

const TYPE_LABELS: Record<string, string> = {
  site: "BLM Collecting Site",
  trailhead: "Trailhead",
  campground: "Campground",
};

const TYPE_COLORS: Record<string, string> = {
  site: "#C97B4A",
  trailhead: "#6B9E7E",
  campground: "#E0A040",
};

const BLM_RULES = [
  {
    title: "What you can collect",
    desc: "Rocks, mineral specimens, semiprecious gemstones, petrified wood, and common invertebrate fossils for personal, non-commercial use. No vertebrate fossils, artifacts, or human remains.",
  },
  {
    title: "Quantity limits",
    desc: "Up to 25 lbs per day plus one piece. Annual cap: 250 lbs/year. Groups cannot pool allotments to exceed 250 lbs. Petrified wood: same limits.",
  },
  {
    title: "Tool restrictions",
    desc: "Hand tools only — no power equipment, explosives, or mechanized tools. Surface disturbance must be minimal and reclaimed. Metal detectors allowed (with exceptions in NCAs).",
  },
  {
    title: "Permits",
    desc: "No permit required for casual non-commercial collecting. Commercial collecting requires a permit or contract from the local BLM field office.",
  },
  {
    title: "What's prohibited",
    desc: "Collecting Indian/ancient artifacts, rock art, bottles, old equipment. Collecting in National Monuments or designated special areas without permission. Vertebrate fossils (these require a permit and go to a museum).",
  },
  {
    title: "Before you go",
    desc: "Check for active mining claims — you cannot collect on someone's claimed ground. Verify access roads are open. Bring water, sun protection, and a map. Cell service is usually nonexistent.",
  },
];

export default function BlmGuide() {
  const [search, setSearch] = useState("");
  const [type, setType] = useState<string>("ALL");

  const filtered = useMemo(() => {
    let sites = allBlmSites;
    if (type !== "ALL") sites = sites.filter((s) => s.type === type);
    if (search.trim()) {
      const q = search.toLowerCase();
      sites = sites.filter(
        (s) =>
          s.name.toLowerCase().includes(q) ||
          s.region.toLowerCase().includes(q) ||
          s.whatToFind.toLowerCase().includes(q),
      );
    }
    return sites;
  }, [search, type]);

  const openInMaps = (site: BlmSite) => {
    window.open(
      `https://www.google.com/maps/search/?api=1&query=${site.latitude},${site.longitude}`,
      "_blank",
      "noopener,noreferrer",
    );
  };

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          BLM Rockhounding Guide
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {blmDigSites.length} collecting sites, {trailheads.length} trailheads, {campgrounds.length} campgrounds on BLM public lands
        </p>
      </div>

      {/* BLM rules */}
      <div className="rounded-xl border border-border bg-gradient-to-br from-primary/10 to-card p-5">
        <h2 className="mb-3 flex items-center gap-2 font-display text-base font-bold text-foreground">
          <Mountain className="h-5 w-5 text-primary" />
          BLM Collecting Rules
        </h2>
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          {BLM_RULES.map((rule) => (
            <div key={rule.title} className="rounded-lg bg-muted/30 p-3">
              <p className="text-sm font-semibold text-foreground">{rule.title}</p>
              <p className="mt-1 text-xs leading-relaxed text-muted-foreground">
                {rule.desc}
              </p>
            </div>
          ))}
        </div>
        <a
          href="https://www.blm.gov/programs/recreation/rockhounding"
          target="_blank"
          rel="noopener noreferrer"
          className="mt-3 flex items-center gap-1 text-xs font-medium text-primary hover:underline"
        >
          Official BLM Rockhounding Page
          <ExternalLink className="h-3 w-3" />
        </a>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search BLM sites by name or region..."
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
        {["ALL", "site", "trailhead", "campground"].map((t) => (
          <button
            key={t}
            onClick={() => setType(t)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              type === t
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {t === "ALL" ? "All Sites" : TYPE_LABELS[t]}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {filtered.map((site) => {
          const color = TYPE_COLORS[site.type] ?? "#666";
          return (
            <div
              key={`${site.type}-${site.id}`}
              className="space-y-2 rounded-xl border border-border bg-card p-4"
            >
              <div className="flex items-start justify-between gap-2">
                <h3 className="font-display text-sm font-semibold text-foreground">
                  {site.name}
                </h3>
                <span
                  className="shrink-0 rounded-full px-2 py-0.5 text-[10px] font-medium"
                  style={{ backgroundColor: `${color}20`, color }}
                >
                  {TYPE_LABELS[site.type]}
                </span>
              </div>

              <p className="text-xs text-muted-foreground">{site.region}</p>

              {site.whatToFind && (
                <p className="text-sm leading-relaxed text-muted-foreground">
                  {site.whatToFind}
                </p>
              )}

              {site.directions && (
                <div>
                  <p className="text-xs font-medium text-foreground">Directions</p>
                  <p className="text-xs text-muted-foreground">{site.directions}</p>
                </div>
              )}

              {site.facilities && (
                <div>
                  <p className="text-xs font-medium text-foreground">Facilities</p>
                  <p className="text-xs text-muted-foreground">{site.facilities}</p>
                </div>
              )}

              <div className="flex items-center justify-between border-t border-border pt-2">
                <span className="text-xs text-muted-foreground">
                  {site.feeInfo}
                  {site.difficulty && ` · ${site.difficulty}`}
                </span>
                <button
                  onClick={() => openInMaps(site)}
                  className="flex items-center gap-1 text-xs font-medium text-primary hover:underline"
                >
                  Map
                  <ExternalLink className="h-3 w-3" />
                </button>
              </div>
            </div>
          );
        })}
      </div>

      {filtered.length === 0 && (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <Mountain className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No BLM sites found. Try a different search.
          </p>
        </div>
      )}
    </div>
  );
}
