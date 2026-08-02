import { useState, useMemo } from "react";
import { Search, X, FlaskConical } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface MohsEntry {
  number: number;
  mineral: string;
  emoji: string;
  description: string;
  examples: string[];
  canScratch: string;
  scratchedBy: string;
}

const MOHS_SCALE: MohsEntry[] = [
  {
    number: 1,
    mineral: "Talc",
    emoji: "🧴",
    description: "The softest mineral — can be scratched with a fingernail. Used in baby powder and cosmetics.",
    examples: ["Soapstone", "Steatite", "Talc schist"],
    canScratch: "Nothing — it's the softest",
    scratchedBy: "Fingernail (2.5), copper penny (3), everything harder",
  },
  {
    number: 2,
    mineral: "Gypsum",
    emoji: "🏔️",
    description: "Scratched by a fingernail. Forms beautiful 'desert rose' crystals and is used in plaster and drywall.",
    examples: ["Selenite", "Alabaster", "Desert rose", "Satin spar"],
    canScratch: "Talc (1)",
    scratchedBy: "Fingernail (2.5), copper penny (3)",
  },
  {
    number: 3,
    mineral: "Calcite",
    emoji: "🔷",
    description: "Scratched by a copper penny. Fizzes in acid — the classic field test. Double refraction makes text appear doubled through clear crystals.",
    examples: ["Iceland spar", "Limestone", "Marble", "Travertine"],
    canScratch: "Talc (1), Gypsum (2)",
    scratchedBy: "Steel knife (5.5), glass (5.5)",
  },
  {
    number: 4,
    mineral: "Fluorite",
    emoji: "🌈",
    description: "Scratched by a steel knife easily. Famous for fluorescence under UV — the property that gives the word its name.",
    examples: ["Blue John", "Fluorescent fluorite", "Antozonite"],
    canScratch: "Talc, Gypsum, Calcite",
    scratchedBy: "Steel knife (5.5), glass (5.5)",
  },
  {
    number: 5,
    mineral: "Apatite",
    emoji: "🦷",
    description: "Scratched by a steel knife. The mineral in your teeth and bones! A key phosphate mineral and hardness standard.",
    examples: ["Tooth enamel", "Bone", "Morseite", "Francolite"],
    canScratch: "Talc through Fluorite",
    scratchedBy: "Steel knife (5.5), glass (5.5)",
  },
  {
    number: 6,
    mineral: "Orthoclase (Feldspar)",
    emoji: "🪨",
    description: "Scratches glass. A major rock-forming mineral — granite is mostly feldspar. Can be scratched by a steel file with difficulty.",
    examples: ["Moonstone", "Amazonite", "Granite feldspar", "Adularia"],
    canScratch: "Glass (5.5), Talc through Apatite",
    scratchedBy: "Quartz (7), steel file with pressure",
  },
  {
    number: 7,
    mineral: "Quartz",
    emoji: "💎",
    description: "Scratches glass easily and can't be scratched by a steel knife. The most common mineral in the Earth's crust after feldspar.",
    examples: ["Amethyst", "Citrine", "Rose quartz", "Smoky quartz", "Agate", "Jasper"],
    canScratch: "Glass (5.5), steel knife (5.5), Talc through Orthoclase",
    scratchedBy: "Topaz (8), corundum (9), diamond (10)",
  },
  {
    number: 8,
    mineral: "Topaz",
    emoji: "💛",
    description: "Scratches quartz. A hard gemstone that comes in golden, blue, pink, and colorless varieties.",
    examples: ["Imperial topaz", "Blue topaz", "Mystic topaz", "Precious topaz"],
    canScratch: "Quartz (7), glass, steel knife",
    scratchedBy: "Corundum (9), diamond (10)",
  },
  {
    number: 9,
    mineral: "Corundum",
    emoji: "❤️",
    description: "Scratches topaz. Second only to diamond in hardness. Ruby and sapphire are both corundum, colored by trace impurities.",
    examples: ["Ruby", "Sapphire", "Star ruby", "Padparadscha sapphire", "Emery"],
    canScratch: "Topaz (8), quartz (7), everything softer",
    scratchedBy: "Diamond (10) only",
  },
  {
    number: 10,
    mineral: "Diamond",
    emoji: "💍",
    description: "The hardest known natural mineral — scratches everything, scratched by nothing. Forms under extreme pressure deep in the Earth.",
    examples: ["Natural diamond", "Industrial diamond", "Bort", "Carbonado"],
    canScratch: "Everything",
    scratchedBy: "Nothing — it's the hardest",
  },
];

const SCRATCH_TOOLS = [
  { hardness: 2.5, tool: "Fingernail" },
  { hardness: 3.0, tool: "Copper penny" },
  { hardness: 5.5, tool: "Glass / steel knife" },
  { hardness: 6.5, tool: "Steel file" },
  { hardness: 7.0, tool: "Quartz crystal" },
  { hardness: 8.5, tool: "Masonry drill bit" },
];

export default function MohsScale() {
  const [search, setSearch] = useState("");

  const filtered = useMemo(() => {
    if (!search.trim()) return MOHS_SCALE;
    const q = search.toLowerCase();
    return MOHS_SCALE.filter(
      (m) =>
        m.mineral.toLowerCase().includes(q) ||
        m.description.toLowerCase().includes(q) ||
        m.examples.some((e) => e.toLowerCase().includes(q)),
    );
  }, [search]);

  return (
    <ScreenScaffold title="Mohs Hardness Scale" onBack={() => window.history.back()}>
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        The 10-step mineral hardness reference for field identification
      </p>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search minerals..."
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

      {/* Quick reference: scratch tools */}
      <SculptedCard accent="citrine" className="p-4">
        <h3 className="mb-2 font-display text-sm font-bold text-foreground">Field scratch tools</h3>
        <div className="grid grid-cols-2 gap-2 sm:grid-cols-3">
          {SCRATCH_TOOLS.map((tool) => (
            <div key={tool.tool} className="flex items-center justify-between rounded-lg bg-muted/30 px-3 py-1.5 text-xs">
              <span className="text-muted-foreground">{tool.tool}</span>
              <span className="font-mono font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>{tool.hardness}</span>
            </div>
          ))}
        </div>
      </SculptedCard>

      {/* Mohs scale list */}
      <div className="space-y-3">
        {filtered.map((entry) => {
          const accent = entry.number <= 3 ? "4 70% 55%" : entry.number <= 6 ? "36 80% 58%" : "147 49% 55%";
          return (
          <SculptedCard key={entry.number} accent="citrine" className="flex gap-4 p-4">
            <div className="flex flex-col items-center gap-1">
              <div className="glowing-border flex h-12 w-12 items-center justify-center rounded-full font-display text-xl font-bold"
                style={{ ["--glow-color" as string]: accent, backgroundColor: `hsl(${accent} / 0.15)`, color: `hsl(${accent})` }}>
                {entry.number}
              </div>
              <span className="text-xl">{entry.emoji}</span>
            </div>
            <div className="min-w-0 flex-1 space-y-2">
              <h3 className="font-display text-sm font-bold text-foreground">{entry.mineral}</h3>
              <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{entry.description}</p>
              <div className="flex flex-wrap gap-1.5">
                {entry.examples.map((ex) => (
                  <TagChip key={ex} accent={`hsl(${AQUA_HEX})`}>{ex}</TagChip>
                ))}
              </div>
              <div className="grid grid-cols-1 gap-1 text-xs sm:grid-cols-2">
                <p className="text-[hsl(var(--text-mid))]">
                  <span className="font-bold text-foreground">Can scratch:</span> {entry.canScratch}
                </p>
                <p className="text-[hsl(var(--text-mid))]">
                  <span className="font-bold text-foreground">Scratched by:</span> {entry.scratchedBy}
                </p>
              </div>
            </div>
          </SculptedCard>
          );
        })}
      </div>

      {filtered.length === 0 && (
        <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
          <FlaskConical className="h-10 w-10 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No minerals found. Try a different search.</p>
        </SculptedCard>
      )}
     </div>
    </ScreenScaffold>
  );
}
