import { useState, useMemo } from "react";
import {
  Search,
  ChevronDown,
  ChevronUp,
  PawPrint,
  Ruler,
  Weight,
  MapPin,
  Lightbulb,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import {
  DINO_ENTRIES,
  ERA_LABELS,
  DIET_LABELS,
  type DinoEra,
  type DinoDiet,
  type DinoEntry,
} from "@/data/dinosaurData";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

const ERA_ORDER: DinoEra[] = [
  "TRIASSIC",
  "JURASSIC",
  "CRETACEOUS",
  "PALEOGENE",
  "NEOGENE",
  "QUATERNARY",
  "OTHER",
];

const DIET_COLORS: Record<DinoDiet, string> = {
  CARNIVORE: "4 70% 55%",
  HERBIVORE: "147 49% 55%",
  OMNIVORE: "36 80% 58%",
  PISCIVORE: "200 67% 57%",
  FILTER_FEEDER: "174 100% 45%",
  INSECTIVORE: "265 47% 67%",
  SCAVENGER: "30 10% 50%",
};

export default function DinosaurDictionary() {
  const [search, setSearch] = useState("");
  const [filterEra, setFilterEra] = useState<DinoEra | "all">("all");
  const [filterDiet, setFilterDiet] = useState<DinoDiet | "all">("all");
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const filtered = useMemo(() => {
    return DINO_ENTRIES.filter((entry) => {
      if (filterEra !== "all" && entry.era !== filterEra) return false;
      if (filterDiet !== "all" && entry.diet !== filterDiet) return false;
      if (search) {
        const q = search.toLowerCase();
        return (
          entry.name.toLowerCase().includes(q) ||
          entry.description.toLowerCase().includes(q) ||
          entry.period.toLowerCase().includes(q)
        );
      }
      return true;
    });
  }, [search, filterEra, filterDiet]);

  return (
    <ScreenScaffold title="Dinosaur Dictionary">
      <div className="space-y-5 px-4 pb-8">
        {/* Search — compact, at the top */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by name, period, description…"
            className="pl-9"
          />
        </div>

        {/* Stats */}
        <p className="text-sm text-muted-foreground">
          {DINO_ENTRIES.length} dinosaurs & prehistoric animals from all eras
        </p>

        {/* Era filter */}
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setFilterEra("all")}
            className={`rounded-full px-3 py-1.5 text-xs font-bold transition-all ${
              filterEra === "all"
                ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                : "border border-border text-muted-foreground hover:text-foreground"
            }`}
          >
            All Eras
          </button>
          {ERA_ORDER.map((era) => (
            <button
              key={era}
              onClick={() => setFilterEra(era)}
              className={`rounded-full px-3 py-1.5 text-xs font-bold transition-all ${
                filterEra === era
                  ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                  : "border border-border text-muted-foreground hover:text-foreground"
              }`}
            >
              {ERA_LABELS[era].label}
            </button>
          ))}
        </div>

        {/* Diet filter */}
        <div className="flex flex-wrap gap-2">
          <button
            onClick={() => setFilterDiet("all")}
            className={`rounded-full px-3 py-1.5 text-xs font-medium transition-all ${
              filterDiet === "all"
                ? "bg-primary/10 text-primary"
                : "border border-border text-muted-foreground hover:text-foreground"
            }`}
          >
            All Diets
          </button>
          {(Object.keys(DIET_LABELS) as DinoDiet[]).map((diet) => (
            <button
              key={diet}
              onClick={() => setFilterDiet(diet)}
              className="rounded-full px-3 py-1.5 text-xs font-medium transition-all"
              style={{
                backgroundColor:
                  filterDiet === diet
                    ? `hsl(${DIET_COLORS[diet]} / 0.15)`
                    : undefined,
                color:
                  filterDiet === diet
                    ? `hsl(${DIET_COLORS[diet]})`
                    : undefined,
                border: `1px solid hsl(${filterDiet === diet ? DIET_COLORS[diet] : "var(--border)"} / ${filterDiet === diet ? 0.4 : 1})`,
              }}
            >
              {DIET_LABELS[diet]}
            </button>
          ))}
        </div>

        {/* Results count */}
        <p className="text-xs text-muted-foreground">
          Showing {filtered.length} {filtered.length === 1 ? "entry" : "entries"}
        </p>

        {/* Entry cards */}
        <div className="space-y-3">
          {filtered.map((entry) => (
            <DinoCard
              key={entry.id}
              entry={entry}
              expanded={expandedId === entry.id}
              onToggle={() =>
                setExpandedId(expandedId === entry.id ? null : entry.id)
              }
            />
          ))}
        </div>

        {filtered.length === 0 && (
          <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <PawPrint className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">
              No entries match your search
            </p>
          </SculptedCard>
        )}
      </div>
    </ScreenScaffold>
  );
}

function DinoCard({
  entry,
  expanded,
  onToggle,
}: {
  entry: DinoEntry;
  expanded: boolean;
  onToggle: () => void;
}) {
  const eraInfo = ERA_LABELS[entry.era];
  const dietColor = DIET_COLORS[entry.diet];
  const imageUrl = `/dino_images/${entry.id}.jpg`;
  const lifeImageUrl = `/dino_life/${entry.id}.jpg`;

  return (
    <SculptedCard
      accent="aqua"
      className="overflow-hidden"
      interactive
      onClick={onToggle}
    >
      {/* Image + name header */}
      <div className="relative h-40 overflow-hidden">
        <img
          src={imageUrl}
          alt={entry.name}
          className="h-full w-full object-cover"
          loading="lazy"
          onError={(e) => {
            (e.target as HTMLImageElement).style.display = "none";
          }}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-[hsl(30_10%_7%)] via-[hsl(30_10%_7%/0.3)] to-transparent" />
        {/* Era badge */}
        <div
          className="absolute left-3 top-3 rounded-full px-2.5 py-1 text-xs font-bold"
          style={{
            backgroundColor: `${entry.color}cc`,
            color: "white",
          }}
        >
          {eraInfo.label}
        </div>
        {/* Diet badge */}
        <div
          className="absolute right-3 top-3 rounded-full px-2.5 py-1 text-xs font-bold"
          style={{
            backgroundColor: `hsl(${dietColor} / 0.85)`,
            color: "hsl(30 30% 9%)",
          }}
        >
          {DIET_LABELS[entry.diet]}
        </div>
        {/* Name */}
        <div className="absolute bottom-3 left-4 right-4">
          <h3 className="font-display text-lg font-bold text-foreground">
            {entry.name}
          </h3>
          <p className="text-xs text-[hsl(var(--text-mid))]">
            {entry.period} · {entry.age}
          </p>
        </div>
      </div>

      {/* Quick stats */}
      <div className="flex items-center gap-4 px-4 py-3">
        <div className="flex items-center gap-1.5">
          <Ruler className="h-3.5 w-3.5 text-muted-foreground" />
          <span className="text-xs font-medium text-foreground">{entry.length}</span>
        </div>
        <div className="flex items-center gap-1.5">
          <Weight className="h-3.5 w-3.5 text-muted-foreground" />
          <span className="text-xs font-medium text-foreground">{entry.weight}</span>
        </div>
        <div className="flex-1" />
        {expanded ? (
          <ChevronUp className="h-4 w-4 text-muted-foreground" />
        ) : (
          <ChevronDown className="h-4 w-4 text-muted-foreground" />
        )}
      </div>

      {/* Expanded detail */}
      {expanded && (
        <div className="space-y-4 px-4 pb-5">
          {/* Life reconstruction */}
          <div className="relative overflow-hidden rounded-xl">
            <img
              src={lifeImageUrl}
              alt={`${entry.name} life reconstruction`}
              className="w-full object-cover"
              loading="lazy"
              onError={(e) => {
                (e.target as HTMLImageElement).style.display = "none";
              }}
            />
          </div>

          {/* Description */}
          <div>
            <p className="text-sm text-[hsl(var(--text-mid))]">{entry.description}</p>
          </div>

          {/* Habitat */}
          <div className="flex items-start gap-2">
            <MapPin className="h-4 w-4 shrink-0" style={{ color: `hsl(${CITRINE_HEX})` }} />
            <div>
              <p className="text-xs font-semibold text-muted-foreground">Habitat</p>
              <p className="text-sm text-foreground">{entry.habitat}</p>
            </div>
          </div>

          {/* Fun facts */}
          <div className="space-y-2">
            <p className="flex items-center gap-1.5 text-xs font-semibold" style={{ color: `hsl(${CITRINE_HEX})` }}>
              <Lightbulb className="h-3.5 w-3.5" />
              Fun Facts
            </p>
            <ul className="space-y-1.5">
              {entry.funFacts.map((fact, i) => (
                <li key={i} className="flex items-start gap-2 text-xs text-[hsl(var(--text-mid))]">
                  <span style={{ color: entry.color }}>•</span>
                  {fact}
                </li>
              ))}
            </ul>
          </div>

          {/* Found in */}
          <div className="space-y-2">
            <p className="text-xs font-semibold text-muted-foreground">Found In</p>
            <div className="flex flex-wrap gap-1.5">
              {entry.foundIn.map((loc, i) => (
                <TagChip key={i} accent={`hsl(${AQUA_HEX})`}>
                  {loc}
                </TagChip>
              ))}
            </div>
          </div>
        </div>
      )}
    </SculptedCard>
  );
}
