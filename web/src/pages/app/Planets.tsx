import { useState } from "react";
import { Globe, X, Ruler, Weight, Navigation, Thermometer, Wind, Eye, Moon, Calendar } from "lucide-react";
import { PLANETS, type PlanetEntry } from "@/data/planetData";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const CYAN_HEX = "174 100% 45%";

const TYPE_COLORS: Record<string, string> = {
  "Terrestrial": "174 100% 45%",
  "Gas Giant": "36 80% 58%",
  "Ice Giant": "200 67% 57%",
  "Dwarf Planet": "265 47% 67%",
};

export default function Planets() {
  const [selected, setSelected] = useState<PlanetEntry | null>(null);

  return (
    <ScreenScaffold title="Planets" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          The 8 planets + 2 dwarf planets of our solar system
        </p>

        {/* Planet grid */}
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-5">
          {PLANETS.map((planet) => {
            const typeColor = TYPE_COLORS[planet.type] ?? CITRINE_HEX;
            return (
              <SculptedCard
                key={planet.name}
                accent="cyan"
                interactive
                glowing
                className="overflow-hidden"
                onClick={() => setSelected(planet)}
              >
                {/* Planet image */}
                <div className="relative aspect-square overflow-hidden">
                  <img
                    src={planet.imageUrl}
                    alt={planet.name}
                    className="h-full w-full object-cover"
                    loading="lazy"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-[hsl(30_10%_7%)] to-transparent" />
                  {/* Name */}
                  <div className="absolute bottom-2 left-3 right-3">
                    <h3 className="font-display text-base font-bold text-foreground">
                      {planet.name}
                    </h3>
                    <span
                      className="text-[10px] font-semibold"
                      style={{ color: `hsl(${typeColor})` }}
                    >
                      {planet.type}
                    </span>
                  </div>
                </div>
              </SculptedCard>
            );
          })}
        </div>

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
                  <TagChip accent={`hsl(${TYPE_COLORS[selected.type] ?? CITRINE_HEX})`}>
                    {selected.type}
                  </TagChip>
                </div>
              </div>

              {/* Content */}
              <div className="max-h-[50vh] space-y-4 overflow-y-auto p-5">
                <p className="text-sm text-[hsl(var(--text-mid))]">{selected.description}</p>

                {/* Key stats grid */}
                <div className="grid grid-cols-2 gap-3">
                  <StatItem icon={Ruler} label="Diameter" value={`${selected.diameterKm.toLocaleString()} km`} />
                  <StatItem icon={Navigation} label="Distance from Sun" value={`${selected.distanceFromSunAu} AU`} />
                  <StatItem icon={Calendar} label="Orbital Period" value={
                    selected.orbitalPeriodDays > 365
                      ? `${(selected.orbitalPeriodDays / 365.25).toFixed(1)} years`
                      : `${selected.orbitalPeriodDays} days`
                  } />
                  <StatItem icon={Moon} label="Moons" value={String(selected.moons)} />
                  <StatItem icon={Weight} label="Mass (Earth = 1)" value={String(selected.massEarth)} />
                  <StatItem icon={Globe} label="Gravity" value={`${selected.gravityMs2} m/s²`} />
                  <StatItem icon={Thermometer} label="Temperature" value={selected.tempRangeC} />
                  <StatItem icon={Wind} label="Atmosphere" value={selected.atmosphere} />
                </div>

                {/* Viewing info */}
                <div className="space-y-2 border-t border-border pt-3">
                  <div className="flex items-center gap-2">
                    <Eye className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
                    <div>
                      <p className="text-xs font-semibold text-muted-foreground">Best Viewing</p>
                      <p className="text-sm text-foreground">{selected.bestViewing}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Eye className="h-4 w-4" style={{ color: `hsl(${AQUA_HEX})` }} />
                    <div>
                      <p className="text-xs font-semibold text-muted-foreground">Apparent Magnitude</p>
                      <p className="text-sm text-foreground">{selected.apparentMagnitude}</p>
                    </div>
                  </div>
                </div>

                {/* Notable features */}
                <div className="border-t border-border pt-3">
                  <p className="mb-1 text-xs font-semibold" style={{ color: `hsl(${CITRINE_HEX})` }}>
                    Notable Features
                  </p>
                  <p className="text-sm text-[hsl(var(--text-mid))]">{selected.notableFeatures}</p>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </ScreenScaffold>
  );
}

function StatItem({ icon: Icon, label, value }: { icon: typeof Globe; label: string; value: string }) {
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
