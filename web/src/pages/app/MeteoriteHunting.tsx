import { useState } from "react";
import { Search, X, Globe } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface MeteoriteType {
  id: string;
  name: string;
  type: "Stone" | "Iron" | "Stony-Iron";
  emoji: string;
  description: string;
  howToId: string[];
  famousExamples: string[];
  rarity: string;
}

const METEORITE_TYPES: MeteoriteType[] = [
  {
    id: "chondrite",
    name: "Chondrite (Ordinary)",
    type: "Stone",
    emoji: "🪨",
    description: "The most common meteorite type — 85% of all falls. Contains chondrules: tiny spherical grains that formed in the solar nebula 4.5 billion years ago. Usually brown to black fusion crust.",
    howToId: [
      "Look for a dark fusion crust (melted surface from atmospheric entry)",
      "Chondrules look like tiny BB-sized spheres embedded in the matrix",
      "Magnetic — most chondrites attract a magnet due to iron content",
      "Heavier than a normal Earth rock of the same size",
    ],
    famousExamples: ["Allende, Mexico (1969)", "Murchison, Australia (1969)", "Sikhote-Alin, Russia (1947)"],
    rarity: "Most common meteorite type",
  },
  {
    id: "carbonaceous",
    name: "Carbonaceous Chondrite",
    type: "Stone",
    emoji: "⚫",
    description: "Rare, primitive meteorites rich in carbon, water, and organic compounds. Some contain amino acids — the building blocks of life. The most scientifically valuable meteorites, pristine from the early solar system.",
    howToId: [
      "Dark matrix (black or dark gray) — carbon-rich",
      "Often fragile and crumbly — handle with extreme care",
      "May contain white CAI (calcium-aluminum inclusions)",
      "Low density for a meteorite — can be lighter than expected",
    ],
    famousExamples: ["Allende, Mexico", "Murchison, Australia", "Orgueil, France", "Tagish Lake, Canada"],
    rarity: "Rare — less than 5% of falls",
  },
  {
    id: "iron",
    name: "Iron Meteorite",
    type: "Iron",
    emoji: "🔩",
    description: "The most easily recognized meteorites — almost pure iron-nickel alloy. Very dense, very magnetic, and show the Widmanstätten pattern when cut and etched. ~5% of falls but the most found because they survive weathering.",
    howToId: [
      "VERY heavy — iron meteorites are 2-3x denser than normal rock",
      "Strongly magnetic — a magnet will snap to it",
      "Look for regmaglypts (thumbprint-like depressions on the surface)",
      "Cut and etched: shows geometric Widmanstätten lines (impossible to fake)",
      "No vesicles/bubbles — if it has bubbles, it's slag, not a meteorite",
    ],
    famousExamples: ["Canyon Diablo (Meteor Crater, AZ)", "Gibeon, Namibia", "Sikhote-Alin, Russia", "Campo del Cielo, Argentina"],
    rarity: "~5% of falls, most commonly found",
  },
  {
    id: "stony-iron-pallasite",
    name: "Pallasite (Stony-Iron)",
    type: "Stony-Iron",
    emoji: "💎",
    description: "The most beautiful meteorites — a metal matrix with transparent olivine crystals. When sliced and polished, pallasites look like stained glass windows. Formed at the core-mantle boundary of a destroyed planet.",
    howToId: [
      "Metal frame (iron-nickel) with embedded crystals (olivine/peridot)",
      "Slice thin enough and the crystals are translucent to transparent",
      "Crystals are typically amber-green (peridot)",
      "Extremely rare — fewer than 100 known pallasites",
    ],
    famousExamples: ["Fukang, China", "Brenham, Kansas", "Esquel, Argentina", "Imilac, Chile"],
    rarity: "Extremely rare — less than 1% of falls",
  },
  {
    id: "mesosiderite",
    name: "Mesosiderite (Stony-Iron)",
    type: "Stony-Iron",
    emoji: "🪨",
    description: "A breccia of metal and silicate minerals — looks like a rock with metal flecks. Less visually striking than pallasites but equally rare. The origin is still debated by scientists.",
    howToId: [
      "Metal flecks visible in a rocky matrix",
      "Magnetic but not as strongly as iron meteorites",
      "Uneven texture — a mix of metal and silicate fragments",
      "Heavier than normal stone, lighter than pure iron",
    ],
    famousExamples: ["Vaca Muerta, Chile", "Bench Crater, Hawaii", "Estherville, Iowa"],
    rarity: "Rare — about 1% of falls",
  },
];

const HUNTING_TIPS = [
  {
    title: "Where to hunt",
    tips: [
      "Dry lake beds and playas — dark rocks stand out on light surfaces",
      "Desert pavement — meteorites weather slowly in arid climates",
      "Glacial moraines — ice concentrates meteorites (ANSMET finds in Antarctica)",
      "Fields after plowing — freshly turned soil exposes dark fusion crusts",
      "Avoid forests and riverbeds — vegetation and water hide or bury meteorites",
    ],
  },
  {
    title: "Equipment you need",
    tips: [
      "Strong rare-earth magnet on a string — iron meteorites will swing it",
      "Metal detector — for buried iron meteorites in soil",
      "GPS — mark every find location precisely",
      "Camera — document the specimen in situ before moving it",
      "Permit and landowner permission — meteorite ownership laws vary by country",
    ],
  },
  {
    title: "Red flags (NOT a meteorite)",
    tips: [
      "Has bubbles or vesicles → it's slag or volcanic rock, not a meteorite",
      "Lightweight and porous → pumice, scoria, or industrial slag",
      "Quartz visible → meteorites don't contain quartz (except rare lunar samples)",
      "Layered or striped → sedimentary rock, not a meteorite",
      "Rust-colored surface → could be terrestrial ironstone or hematite",
      "Round and smooth → river rock; meteorites have angular, irregular shapes",
    ],
  },
];

export default function MeteoriteHunting() {
  const [search, setSearch] = useState("");

  const filtered = METEORITE_TYPES.filter(
    (m) =>
      !search.trim() ||
      m.name.toLowerCase().includes(search.toLowerCase()) ||
      m.description.toLowerCase().includes(search.toLowerCase()),
  );

  return (
    <ScreenScaffold title="Meteorite Hunting" onBack={() => window.history.back()}>
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        {METEORITE_TYPES.length} meteorite types, field ID tips, and hunting guide
      </p>

      {/* Hunting tips */}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-3">
        {HUNTING_TIPS.map((section) => (
          <SculptedCard key={section.title} accent="aqua" className="space-y-2 p-4">
            <h3 className="font-display text-sm font-bold text-foreground">{section.title}</h3>
            <ul className="space-y-1">
              {section.tips.map((tip, i) => (
                <li key={i} className="flex items-start gap-1.5 text-xs text-[hsl(var(--text-mid))]">
                  <span className="mt-0.5" style={{ color: `hsl(${AQUA_HEX})` }}>•</span>
                  {tip}
                </li>
              ))}
            </ul>
          </SculptedCard>
        ))}
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search meteorite types..."
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

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {filtered.map((meteorite) => (
          <SculptedCard key={meteorite.id} accent="citrine" className="space-y-3 p-4">
            <div className="flex items-start gap-3">
              <div className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl" style={{ ["--glow-color" as string]: CITRINE_HEX }}>{meteorite.emoji}</div>
              <div className="min-w-0 flex-1">
                <h3 className="font-display text-sm font-bold text-foreground">{meteorite.name}</h3>
                <TagChip accent={`hsl(${AQUA_HEX})`}>{meteorite.type}</TagChip>
              </div>
            </div>
            <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{meteorite.description}</p>
            <div>
              <p className="mb-1 text-xs font-bold text-foreground">How to identify</p>
              <ul className="space-y-1">
                {meteorite.howToId.map((tip, i) => (
                  <li key={i} className="flex items-start gap-1.5 text-xs text-[hsl(var(--text-mid))]">
                    <span className="mt-0.5" style={{ color: `hsl(${CITRINE_HEX})` }}>•</span>
                    {tip}
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <p className="mb-1 text-xs font-bold text-foreground">Famous examples</p>
              <div className="flex flex-wrap gap-1.5">
                {meteorite.famousExamples.map((ex) => (
                  <TagChip key={ex} accent={`hsl(${AQUA_HEX})`}>{ex}</TagChip>
                ))}
              </div>
            </div>
            <p className="text-xs font-bold" style={{ color: "hsl(36 80% 58%)" }}>{meteorite.rarity}</p>
          </SculptedCard>
        ))}
      </div>
     </div>
    </ScreenScaffold>
  );
}
