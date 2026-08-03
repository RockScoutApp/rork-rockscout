import { useState, useMemo } from "react";
import { Search, X, Telescope, Eye, MapPin, Ruler, Calendar } from "lucide-react";
import { Input } from "@/components/ui/input";
import { DEEP_SKY_OBJECTS, type DeepSkyObject } from "@/data/deepSkyData";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const CYAN_HEX = "174 100% 45%";
const AMETHYST_HEX = "265 47% 67%";

const TYPE_COLORS: Record<string, string> = {
  "Galaxy": "265 47% 67%",
  "Nebula": "174 100% 45%",
  "Planetary Nebula": "174 100% 45%",
  "Globular Cluster": "36 80% 58%",
  "Open Cluster": "147 49% 55%",
};

const EQUIPMENT_COLORS: Record<string, string> = {
  "Naked eye": "147 49% 55%",
  "Binoculars": "36 80% 58%",
  "Telescope": "265 47% 67%",
};

export default function DeepSkyObjects() {
  const [search, setSearch] = useState("");
  const [filterType, setFilterType] = useState<string>("all");
  const [selected, setSelected] = useState<DeepSkyObject | null>(null);

  const types = ["all", ...Array.from(new Set(DEEP_SKY_OBJECTS.map((d) => d.type)))];

  const filtered = useMemo(() => {
    return DEEP_SKY_OBJECTS.filter((dso) => {
      if (filterType !== "all" && dso.type !== filterType) return false;
      if (search) {
        const q = search.toLowerCase();
        return (
          dso.commonName.toLowerCase().includes(q) ||
          dso.catalog.toLowerCase().includes(q) ||
          dso.constellation.toLowerCase().includes(q)
        );
      }
      return true;
    });
  }, [search, filterType]);

  return (
    <ScreenScaffold title="Deep Sky Objects">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          {DEEP_SKY_OBJECTS.length} galaxies, nebulae, and star clusters
        </p>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by name, catalog, or constellation…"
            className="pl-9"
          />
        </div>

        {/* Type filters */}
        <div className="flex flex-wrap gap-2">
          {types.map((t) => (
            <button
              key={t}
              onClick={() => setFilterType(t)}
              className={`rounded-full px-3 py-1.5 text-xs font-bold transition-all ${
                filterType === t
                  ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                  : "border border-border text-muted-foreground hover:text-foreground"
              }`}
            >
              {t === "all" ? "All Types" : t}
            </button>
          ))}
        </div>

        {/* Grid */}
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((dso) => {
            const typeColor = TYPE_COLORS[dso.type] ?? AQUA_HEX;
            return (
              <SculptedCard
                key={dso.catalog}
                accent="amethyst"
                interactive
                className="overflow-hidden"
                onClick={() => setSelected(dso)}
              >
                <div className="relative h-32 overflow-hidden">
                  <img
                    src={dso.imageUrl}
                    alt={dso.commonName}
                    className="h-full w-full object-cover"
                    loading="lazy"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-[hsl(30_10%_7%)] to-transparent" />
                  <div
                    className="absolute right-2 top-2 rounded-full px-2 py-0.5 text-[10px] font-bold"
                    style={{
                      backgroundColor: `hsl(${typeColor} / 0.85)`,
                      color: "hsl(30 30% 9%)",
                    }}
                  >
                    {dso.type}
                  </div>
                  <div className="absolute bottom-2 left-3 right-3">
                    <p className="text-[10px] font-bold text-muted-foreground">{dso.catalog}</p>
                    <h3 className="truncate text-sm font-bold text-foreground">{dso.commonName}</h3>
                  </div>
                </div>
                <div className="flex items-center gap-2 p-2.5">
                  <MapPin className="h-3 w-3 text-muted-foreground" />
                  <span className="flex-1 truncate text-xs text-muted-foreground">{dso.constellation}</span>
                  <span
                    className="rounded-full px-1.5 py-0.5 text-[9px] font-bold"
                    style={{
                      backgroundColor: `hsl(${EQUIPMENT_COLORS[dso.equipment] ?? AQUA_HEX} / 0.15)`,
                      color: `hsl(${EQUIPMENT_COLORS[dso.equipment] ?? AQUA_HEX})`,
                    }}
                  >
                    {dso.equipment}
                  </span>
                </div>
              </SculptedCard>
            );
          })}
        </div>

        {filtered.length === 0 && (
          <SculptedCard accent="amethyst" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <Telescope className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">No objects match your search</p>
          </SculptedCard>
        )}

        {/* Detail modal */}
        {selected && (
          <div
            className="fixed inset-0 z-[80] flex items-end justify-center bg-black/70 backdrop-blur-sm md:items-center"
            onClick={() => setSelected(null)}
          >
            <div
              className="dark-card sculpted-raised w-full max-w-lg overflow-hidden rounded-t-2xl md:rounded-2xl"
              style={{ ["--sculpted-accent" as string]: AMETHYST_HEX }}
              onClick={(e) => e.stopPropagation()}
            >
              <div className="relative h-56 overflow-hidden">
                <img src={selected.imageUrl} alt={selected.commonName} className="h-full w-full object-cover" />
                <div className="absolute inset-0 bg-gradient-to-t from-[hsl(30_10%_7%)] to-transparent" />
                <button
                  onClick={() => setSelected(null)}
                  className="absolute right-3 top-3 flex h-8 w-8 items-center justify-center rounded-full bg-black/50 text-white"
                >
                  <X className="h-4 w-4" />
                </button>
                <div className="absolute bottom-3 left-4 right-4">
                  <p className="text-xs font-bold text-muted-foreground">{selected.catalog}</p>
                  <h2 className="font-display text-2xl font-bold text-foreground">{selected.commonName}</h2>
                  <div className="mt-1 flex gap-1.5">
                    <TagChip accent={`hsl(${TYPE_COLORS[selected.type] ?? AMETHYST_HEX})`}>{selected.type}</TagChip>
                    <TagChip accent={`hsl(${AQUA_HEX})`}>{selected.constellation}</TagChip>
                  </div>
                </div>
              </div>

              <div className="max-h-[50vh] space-y-4 overflow-y-auto p-5">
                <p className="text-sm text-[hsl(var(--text-mid))]">{selected.description}</p>

                <div className="grid grid-cols-2 gap-3">
                  <StatItem icon={MapPin} label="Distance" value={selected.distance} />
                  <StatItem icon={Eye} label="Magnitude" value={selected.magnitude} />
                  <StatItem icon={Ruler} label="Angular Size" value={selected.angularSize} />
                  <StatItem icon={Calendar} label="Best Season" value={selected.bestSeason} />
                </div>

                <div className="space-y-2 border-t border-border pt-3">
                  <div className="flex items-center gap-2">
                    <Telescope className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
                    <div>
                      <p className="text-xs font-semibold text-muted-foreground">Equipment Needed</p>
                      <p className="text-sm text-foreground">{selected.equipment}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar className="h-4 w-4" style={{ color: `hsl(${AQUA_HEX})` }} />
                    <div>
                      <p className="text-xs font-semibold text-muted-foreground">Hemisphere</p>
                      <p className="text-sm text-foreground">{selected.hemisphere}</p>
                    </div>
                  </div>
                </div>

                <div className="border-t border-border pt-3">
                  <p className="text-xs font-semibold text-muted-foreground">Discovery</p>
                  <p className="text-sm text-foreground">
                    {selected.discoveryYear} by {selected.discoverer}
                  </p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </ScreenScaffold>
  );
}

function StatItem({ icon: Icon, label, value }: { icon: typeof Eye; label: string; value: string }) {
  return (
    <div className="flex items-start gap-2">
      <Icon className="h-4 w-4 shrink-0 text-muted-foreground mt-0.5" />
      <div className="min-w-0">
        <p className="text-xs font-semibold text-muted-foreground">{label}</p>
        <p className="text-sm text-foreground truncate">{value}</p>
      </div>
    </div>
  );
}
