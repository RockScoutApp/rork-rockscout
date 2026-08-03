import { useState } from "react";
import { Search, X, Hexagon } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface CrystalSystem {
  name: string;
  axes: string;
  angles: string;
  emoji: string;
  description: string;
  examples: string[];
  keyFeature: string;
}

const CRYSTAL_SYSTEMS: CrystalSystem[] = [
  {
    name: "Cubic (Isometric)",
    axes: "3 equal axes (a1 = a2 = a3)",
    angles: "All 90°",
    emoji: "🎲",
    description: "The most symmetrical crystal system. All three axes are equal length and at right angles. Crystals often form cubes, octahedrons, or dodecahedrons.",
    examples: ["Pyrite", "Galena", "Halite", "Fluorite", "Diamond", "Garnet", "Magnetite"],
    keyFeature: "Highest symmetry — looks the same from all sides",
  },
  {
    name: "Tetragonal",
    axes: "3 axes (a1 = a2 ≠ c)",
    angles: "All 90°",
    emoji: "📦",
    description: "Like cubic but stretched or squashed along one axis. Two horizontal axes are equal, the vertical axis is different. Forms prisms and pyramids.",
    examples: ["Zircon", "Rutile", "Scapolite", "Vesuvianite", "Chalcopyrite"],
    keyFeature: "Square cross-section, taller or shorter than a cube",
  },
  {
    name: "Orthorhombic",
    axes: "3 unequal axes (a ≠ b ≠ c)",
    angles: "All 90°",
    emoji: "📏",
    description: "Three axes at right angles, all different lengths. The most common crystal system. Forms rhombic prisms, dipyramids, and tabular crystals.",
    examples: ["Olivine", "Topaz", "Sulfur", "Aragonite", "Barite", "Celestite", "Danburite"],
    keyFeature: "Three different lengths, all at right angles",
  },
  {
    name: "Monoclinic",
    axes: "3 unequal axes (a ≠ b ≠ c)",
    angles: "Two at 90°, one oblique",
    emoji: "📐",
    description: "Three unequal axes where two intersect at 90° but the third is tilted. Common for prismatic and bladed crystals. Second most common system.",
    examples: ["Gypsum", "Orthoclase", "Augite", "Horneblende", "Azurite", "Malachite", "Beryl"],
    keyFeature: "One axis is tilted — like a box pushed sideways",
  },
  {
    name: "Triclinic",
    axes: "3 unequal axes (a ≠ b ≠ c)",
    angles: "All oblique (none at 90°)",
    emoji: "🔀",
    description: "The least symmetrical system. Three unequal axes, none at right angles. Crystals are often tabular or bladed with no symmetry beyond a center.",
    examples: ["Plagioclase", "Kyanite", "Albite", "Microcline", "Rhodonite", "Axinite"],
    keyFeature: "Lowest symmetry — nothing is at a right angle",
  },
  {
    name: "Hexagonal",
    axes: "4 axes (3 equal at 120°, 1 vertical)",
    angles: "120° between horizontal, 90° to vertical",
    emoji: "⬡",
    description: "Three equal horizontal axes at 120° to each other, plus a vertical axis. Forms hexagonal prisms and pyramids. Think quartz and beryl.",
    examples: ["Quartz", "Beryl", "Apatite", "Corundum", "Calcite", "Nepheline"],
    keyFeature: "Six-sided cross-section — the classic crystal shape",
  },
  {
    name: "Trigonal (Rhombohedral)",
    axes: "4 axes (3 equal at 120°, 1 vertical)",
    angles: "120° between horizontal, not 90° to vertical",
    emoji: "🔷",
    description: "Often grouped with hexagonal but has only 3-fold symmetry instead of 6-fold. Forms rhombohedrons and scalenohedrons. Calcite is the classic example.",
    examples: ["Calcite", "Dolomite", "Magnesite", "Siderite", "Rhodochrosite", "Quartz (some)"],
    keyFeature: "Three-fold symmetry — rhombohedron shape",
  },
];

export default function CrystalSystems() {
  const [search, setSearch] = useState("");

  const filtered = CRYSTAL_SYSTEMS.filter(
    (s) =>
      !search.trim() ||
      s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.description.toLowerCase().includes(search.toLowerCase()) ||
      s.examples.some((e) => e.toLowerCase().includes(search.toLowerCase())),
  );

  return (
    <ScreenScaffold title="Crystal Systems">
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        The 7 crystal systems — how minerals are classified by their atomic structure
      </p>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search systems or minerals..."
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

      <div className="space-y-3">
        {filtered.map((system) => (
          <SculptedCard key={system.name} accent="amethyst" className="space-y-3 p-4">
            <div className="flex items-start gap-3">
              <div className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl" style={{ ["--glow-color" as string]: "265 47% 67%" }}>{system.emoji}</div>
              <div className="min-w-0 flex-1">
                <h3 className="font-display text-sm font-bold text-foreground">{system.name}</h3>
                <p className="mt-0.5 text-xs text-[hsl(var(--text-mid))]">{system.keyFeature}</p>
              </div>
            </div>
            <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{system.description}</p>
            <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
              <div className="rounded-lg bg-muted/30 px-3 py-2">
                <p className="text-xs font-bold text-foreground">Axes</p>
                <p className="text-xs text-[hsl(var(--text-mid))]">{system.axes}</p>
              </div>
              <div className="rounded-lg bg-muted/30 px-3 py-2">
                <p className="text-xs font-bold text-foreground">Angles</p>
                <p className="text-xs text-[hsl(var(--text-mid))]">{system.angles}</p>
              </div>
            </div>
            <div>
              <p className="mb-1 text-xs font-bold text-foreground">Common minerals</p>
              <div className="flex flex-wrap gap-1.5">
                {system.examples.map((ex) => (
                  <TagChip key={ex} accent={`hsl(${AQUA_HEX})`}>{ex}</TagChip>
                ))}
              </div>
            </div>
          </SculptedCard>
        ))}
      </div>

      {filtered.length === 0 && (
        <SculptedCard accent="amethyst" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
          <Hexagon className="h-10 w-10 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No crystal systems found. Try a different search.</p>
        </SculptedCard>
      )}
     </div>
    </ScreenScaffold>
  );
}
