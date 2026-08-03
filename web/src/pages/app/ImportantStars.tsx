import { useState, useMemo } from "react";
import { Search, X, Star, Navigation, Thermometer, Sun, Globe } from "lucide-react";
import { Input } from "@/components/ui/input";
import { STARS, type StarEntry } from "@/data/starData";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const CYAN_HEX = "174 100% 45%";

const HEMISPHERE_COLORS: Record<string, string> = {
  "Northern": "200 67% 57%",
  "Southern": "4 70% 55%",
  "Both": "147 49% 55%",
};

export default function ImportantStars() {
  const [search, setSearch] = useState("");
  const [filterHemi, setFilterHemi] = useState<string>("all");
  const [selected, setSelected] = useState<StarEntry | null>(null);

  const filtered = useMemo(() => {
    return STARS.filter((star) => {
      if (filterHemi !== "all" && star.hemisphere !== filterHemi) return false;
      if (search) {
        const q = search.toLowerCase();
        return (
          star.name.toLowerCase().includes(q) ||
          star.constellation.toLowerCase().includes(q) ||
          star.spectralClass.toLowerCase().includes(q)
        );
      }
      return true;
    }).sort((a, b) => a.apparentMagnitude - b.apparentMagnitude);
  }, [search, filterHemi]);

  return (
    <ScreenScaffold title="Important Stars">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          {STARS.length} notable stars — the brightest and most famous in the night sky
        </p>

        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by name, constellation, or spectral class…"
            className="pl-9"
          />
        </div>

        {/* Hemisphere filters */}
        <div className="flex gap-2">
          {["all", "Northern", "Southern", "Both"].map((h) => (
            <button
              key={h}
              onClick={() => setFilterHemi(h)}
              className={`rounded-full px-3 py-1.5 text-xs font-bold transition-all ${
                filterHemi === h
                  ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                  : "border border-border text-muted-foreground hover:text-foreground"
              }`}
            >
              {h === "all" ? "All" : h}
            </button>
          ))}
        </div>

        {/* Star list — sorted by brightness */}
        <div className="space-y-3">
          {filtered.map((star, index) => {
            const hemiColor = HEMISPHERE_COLORS[star.hemisphere] ?? AQUA_HEX;
            return (
              <SculptedCard
                key={star.name}
                accent="cyan"
                interactive
                className="overflow-hidden"
                onClick={() => setSelected(star)}
              >
                <div className="flex items-center gap-4 p-3.5">
                  {/* Star image */}
                  <div className="relative h-14 w-14 shrink-0 overflow-hidden rounded-full">
                    <img
                      src={star.imageUrl}
                      alt={star.name}
                      className="h-full w-full object-cover"
                      loading="lazy"
                    />
                    <div
                      className="absolute inset-0 rounded-full"
                      style={{ boxShadow: `inset 0 0 8px ${star.color}80` }}
                    />
                  </div>

                  {/* Star info */}
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <h3 className="truncate text-sm font-bold text-foreground">{star.name}</h3>
                      <span className="text-[10px] text-muted-foreground">#{index + 1}</span>
                    </div>
                    <p className="truncate text-xs text-muted-foreground">
                      {star.constellation} · {star.spectralClass}
                    </p>
                    <div className="mt-1 flex items-center gap-2">
                      <span
                        className="flex items-center gap-1 rounded-full px-1.5 py-0.5 text-[9px] font-bold"
                        style={{
                          backgroundColor: `hsl(${hemiColor} / 0.15)`,
                          color: `hsl(${hemiColor})`,
                        }}
                      >
                        {star.hemisphere}
                      </span>
                      <span className="text-[10px] text-muted-foreground">
                        Mag {star.apparentMagnitude}
                      </span>
                    </div>
                  </div>

                  {/* Color dot */}
                  <div
                    className="h-3 w-3 shrink-0 rounded-full"
                    style={{
                      backgroundColor: star.color,
                      boxShadow: `0 0 6px ${star.color}`,
                    }}
                  />
                </div>
              </SculptedCard>
            );
          })}
        </div>

        {filtered.length === 0 && (
          <SculptedCard accent="cyan" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <Star className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">No stars match your search</p>
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
              style={{ ["--sculpted-accent" as string]: CITRINE_HEX }}
              onClick={(e) => e.stopPropagation()}
            >
              {/* Hero image */}
              <div className="relative h-48 overflow-hidden">
                <img src={selected.imageUrl} alt={selected.name} className="h-full w-full object-cover" />
                <div className="absolute inset-0 bg-gradient-to-t from-[hsl(30_10%_7%)] to-transparent" />
                <button
                  onClick={() => setSelected(null)}
                  className="absolute right-3 top-3 flex h-8 w-8 items-center justify-center rounded-full bg-black/50 text-white"
                >
                  <X className="h-4 w-4" />
                </button>
                <div className="absolute bottom-3 left-4">
                  <h2 className="font-display text-2xl font-bold text-foreground">{selected.name}</h2>
                  <p className="text-sm" style={{ color: `hsl(${AQUA_HEX})` }}>{selected.constellation}</p>
                </div>
              </div>

              <div className="max-h-[50vh] space-y-4 overflow-y-auto p-5">
                <p className="text-sm text-[hsl(var(--text-mid))]">{selected.description}</p>

                {/* Key stats */}
                <div className="grid grid-cols-2 gap-3">
                  <StatItem icon={Star} label="Apparent Magnitude" value={String(selected.apparentMagnitude)} />
                  <StatItem icon={Sun} label="Absolute Magnitude" value={String(selected.absoluteMagnitude)} />
                  <StatItem icon={Navigation} label="Distance" value={`${selected.distanceLy} ly`} />
                  <StatItem icon={Thermometer} label="Surface Temp" value={`${selected.temperatureK} K`} />
                  <StatItem icon={Sun} label="Luminosity" value={`${selected.luminositySolar}× Sun`} />
                  <StatItem icon={Globe} label="Spectral Class" value={selected.spectralClass} />
                </div>

                {/* Tags */}
                <div className="flex flex-wrap gap-2 border-t border-border pt-3">
                  <TagChip accent={`hsl(${HEMISPHERE_COLORS[selected.hemisphere] ?? AQUA_HEX})`}>
                    {selected.hemisphere}
                  </TagChip>
                  <TagChip accent={`hsl(${CITRINE_HEX})`}>
                    Best viewed: {selected.bestViewingMonth}
                  </TagChip>
                  <TagChip accent={selected.color}>
                    <span className="h-2 w-2 rounded-full" style={{ backgroundColor: selected.color }} />
                    {selected.spectralClass}
                  </TagChip>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </ScreenScaffold>
  );
}

function StatItem({ icon: Icon, label, value }: { icon: typeof Star; label: string; value: string }) {
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
