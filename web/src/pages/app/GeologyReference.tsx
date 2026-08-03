import { useState } from "react";
import { Search, X, Mountain, ArrowRight, ArrowLeft } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface RockType {
  id: string;
  name: string;
  category: "Igneous" | "Sedimentary" | "Metamorphic";
  emoji: string;
  description: string;
  formation: string;
  examples: string[];
  keyTraits: string[];
}

const ROCK_TYPES: RockType[] = [
  // Igneous
  {
    id: "granite",
    name: "Granite",
    category: "Igneous",
    emoji: "🪨",
    description: "A coarse-grained intrusive igneous rock — the most common basement rock of continents. Light-colored with visible crystals of quartz, feldspar, and mica.",
    formation: "Cools slowly deep underground from silica-rich magma. The slow cooling allows large crystals to grow — you can see them with the naked eye.",
    examples: ["Mount Rushmore", "Yosemite Half Dome", "Stone Mountain GA", "Barre VT granite quarries"],
    keyTraits: ["Coarse-grained (visible crystals)", "Light-colored (quartz + feldspar)", "Hard and durable", "No layering or banding"],
  },
  {
    id: "basalt",
    name: "Basalt",
    category: "Igneous",
    emoji: "🌋",
    description: "A fine-grained extrusive igneous rock — dark and dense. The most common rock in the Earth's oceanic crust. Forms hexagonal columns when it cools slowly.",
    formation: "Erupts as runny lava at the surface and cools rapidly. Crystals are too small to see. Sometimes forms striking hexagonal columns like the Giant's Causeway.",
    examples: ["Giant's Causeway, Ireland", "Devils Postpile, CA", "Columbia Plateau, WA/OR", "Decommission Road, MI"],
    keyTraits: ["Fine-grained (crystals too small to see)", "Dark gray to black", "Very hard and dense", "May show columnar jointing"],
  },
  {
    id: "obsidian",
    name: "Obsidian",
    category: "Igneous",
    emoji: "🖤",
    description: "Volcanic glass — cools so fast that no crystals form at all. Usually black but can be brown, red, or have snowflake patterns. Conchoidal fracture makes razor-sharp edges.",
    formation: "Felsic lava cools instantly at the surface — too fast for any crystals to nucleate. The result is a natural glass.",
    examples: ["Yellowstone", "Glass Buttes, OR", "Obsidian Cliff, WY", "Hidalgo, Mexico"],
    keyTraits: ["Glassy (no crystals)", "Conchoidal fracture (curved breaks)", "Sharp edges — used for arrowheads", "Usually black or dark"],
  },
  {
    id: "pumice",
    name: "Pumice",
    category: "Igneous",
    emoji: "🫧",
    description: "A frothy volcanic rock so full of gas bubbles that it floats on water. Used as an abrasive and in lightweight concrete.",
    formation: "Gas-rich felsic lava erupts and cools so rapidly that the trapped gas bubbles create a porous, frothy texture.",
    examples: ["Pumice raft from Hunga Tonga eruption", "Craters of the Moon, ID", "Mt. St. Helens", "Lipari Islands, Italy"],
    keyTraits: ["Very light — floats on water", "Highly porous and abrasive", "Usually light gray or white", "Frothy, bubbly texture"],
  },
  // Sedimentary
  {
    id: "sandstone",
    name: "Sandstone",
    category: "Sedimentary",
    emoji: "🏜️",
    description: "Sand grains cemented together — the most common sedimentary rock. The grains are usually quartz. Forms spectacular cliff landscapes.",
    formation: "Sand accumulates in rivers, deserts, beaches, or oceans. Over time, minerals (silica, calcite, or iron oxide) cement the grains into solid rock.",
    examples: ["Grand Canyon walls", "Zion National Park", "Antelope Canyon, AZ", "Arches National Park, UT"],
    keyTraits: ["Grainy — feels like sandpaper", "Often layered (bedding)", "May show cross-bedding (dune patterns)", "Varies in color (tan, red, white, gray)"],
  },
  {
    id: "limestone",
    name: "Limestone",
    category: "Sedimentary",
    emoji: "🦴",
    description: "A sedimentary rock made of calcium carbonate — often from the shells and skeletons of marine organisms. Fizzes in acid (the classic field test).",
    formation: "Accumulates in warm, shallow seas from shell fragments, coral, and chemical precipitation. Compacts and cements into solid rock over millions of years.",
    examples: ["White Cliffs of Dover", "Carlsbad Caverns, NM", "Mammoth Cave, KY", "Yucatan Peninsula"],
    keyTraits: ["Fizzes in dilute acid (HCl)", "Often light gray or white", "May contain visible fossils", "Forms caves and karst landscapes"],
  },
  {
    id: "shale",
    name: "Shale",
    category: "Sedimentary",
    emoji: "📖",
    description: "A fine-grained sedimentary rock made of compacted mud and clay. Splits into thin layers. The most abundant sedimentary rock.",
    formation: "Mud and clay settle in calm water (lakes, deep ocean). The fine particles compact into thin, fissile layers.",
    examples: ["Burgess Shale, BC (fossils)", "Marcellus Shale (gas)", "Green River Formation", "Mancos Shale, UT"],
    keyTraits: ["Very fine-grained (smooth)", "Splits into thin layers (fissile)", "Often dark gray or black", "May contain fossils"],
  },
  {
    id: "conglomerate",
    name: "Conglomerate",
    category: "Sedimentary",
    emoji: "🥚",
    description: "A sedimentary rock made of rounded pebbles and cobbles cemented together. Each pebble tells a story of an ancient river.",
    formation: "Fast-moving water deposits gravel and cobbles. Mineral cement binds them into solid rock. The rounded shapes mean they traveled far.",
    examples: ["Witwatersrand, South Africa (gold)", "Torrance, CA", "Fountain Formation, CO", "Cheviot Hill, UK"],
    keyTraits: ["Visible rounded pebbles", "Cemented by silica, calcite, or iron", "Each pebble is a different rock", "Indicates ancient river systems"],
  },
  // Metamorphic
  {
    id: "marble",
    name: "Marble",
    category: "Metamorphic",
    emoji: "⚪",
    description: "Metamorphosed limestone — recrystallized calcite that sparkles in light. Prized for sculpture and architecture. Still fizzes in acid.",
    formation: "Limestone is buried and heated under pressure during mountain-building. The calcite recrystallizes into interlocking grains — no fossils survive.",
    examples: ["Carrara, Italy (Michelangelo's marble)", "Yule Marble, CO (Lincoln Memorial)", "Sylacauga, AL", "Paros, Greece"],
    keyTraits: ["Sugar-grain texture (crystalline)", "Fizzes in acid (it's still calcite)", "Often white but can be colored", "Takes a high polish"],
  },
  {
    id: "slate",
    name: "Slate",
    category: "Metamorphic",
    emoji: "🦅",
    description: "Metamorphosed shale — splits into perfectly flat sheets. Used for roofing, chalkboards, and billiard tables. The first step in the shale-to-schist progression.",
    formation: "Shale is compressed at low temperature. The clay minerals realign perpendicular to pressure, creating a perfect cleavage — slate splits into flat sheets.",
    examples: ["Wales (historic slate quarries)", "Granville, NY", "Poultney, VT", "Levant, PA"],
    keyTraits: ["Very fine-grained (smooth)", "Perfect flat cleavage", "Usually dark gray or black", "Harder and denser than shale"],
  },
  {
    id: "schist",
    name: "Schist",
    category: "Metamorphic",
    emoji: "✨",
    description: "A medium-grade metamorphic rock with visible mica flakes that glitter. Forms from shale or mudstone under higher pressure and temperature than slate.",
    formation: "Shale is buried deeper and heated more. Mica crystals grow large enough to see, giving the rock a sparkly, foliated texture. Often contains garnet, staurolite, or kyanite.",
    examples: ["Manhattan bedrock (schist)", "Garnet schist, Gore Mountain NY", "Mica schist, Blue Ridge", "Glacial erratic schists"],
    keyTraits: ["Visible mica flakes (sparkly)", "Foliated (layered/banded)", "May contain garnet or kyanite", "Intermediate metamorphic grade"],
  },
  {
    id: "gneiss",
    name: "Gneiss",
    category: "Metamorphic",
    emoji: "🎨",
    description: "The highest-grade metamorphic rock — alternating bands of light and dark minerals. Forms deep under mountain ranges under extreme pressure.",
    formation: "Rock is buried deep and heated intensely during continental collision. Minerals separate into light (quartz, feldspar) and dark (biotite, hornblende) bands.",
    examples: ["Canadian Shield", "Piedmont gneiss, NC", "Lewisian Gneiss, Scotland (3 billion yrs)", "Acadian gneiss, New England"],
    keyTraits: ["Banded (light and dark layers)", "Coarse-grained", "High-grade metamorphism", "Does NOT split easily (unlike slate/schist)"],
  },
];

const ROCK_CYCLE = [
  { from: "Magma", to: "Igneous Rock", process: "Cooling & crystallization", emoji: "🌋→🪨" },
  { from: "Igneous Rock", to: "Sediment", process: "Weathering & erosion", emoji: "🪨→🏜️" },
  { from: "Sediment", to: "Sedimentary Rock", process: "Lithification (compaction & cementation)", emoji: "🏜️→📖" },
  { from: "Sedimentary Rock", to: "Metamorphic Rock", process: "Heat & pressure", emoji: "📖→🎨" },
  { from: "Metamorphic Rock", to: "Magma", process: "Melting", emoji: "🎨→🌋" },
  { from: "Any Rock", to: "Sediment", process: "Weathering & erosion", emoji: "🪨→🏜️" },
  { from: "Any Rock", to: "Metamorphic Rock", process: "Heat & pressure", emoji: "🪨→🎨" },
];

export default function GeologyReference() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<string>("ALL");

  const filtered = ROCK_TYPES.filter((r) => {
    if (category !== "ALL" && r.category !== category) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        r.name.toLowerCase().includes(q) ||
        r.description.toLowerCase().includes(q) ||
        r.examples.some((e) => e.toLowerCase().includes(q))
      );
    }
    return true;
  });

  return (
    <ScreenScaffold title="Geology Reference">
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        Rock types, the rock cycle, and how the Earth makes stone
      </p>

      {/* Rock cycle diagram */}
      <SculptedCard accent="citrine" glowing className="p-5">
        <h2 className="mb-3 font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
          The Rock Cycle
        </h2>
        <p className="mb-4 text-sm text-muted-foreground">
          Rocks are constantly being recycled — melted, eroded, buried, and
          transformed. Any rock can become any other rock given enough time.
        </p>
        <div className="space-y-2">
          {ROCK_CYCLE.map((step, i) => (
            <div key={i} className="flex items-center gap-3 rounded-lg bg-muted/30 px-3 py-2">
              <span className="text-lg">{step.emoji}</span>
              <div className="flex min-w-0 flex-1 items-center gap-2 text-sm">
                <span className="font-bold text-foreground">{step.from}</span>
                <ArrowRight className="h-3.5 w-3.5 shrink-0 text-muted-foreground" />
                <span className="font-bold text-foreground">{step.to}</span>
              </div>
              <span className="shrink-0 text-xs text-[hsl(var(--text-mid))]">{step.process}</span>
            </div>
          ))}
        </div>
      </SculptedCard>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search rock types..."
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
        {["ALL", "Igneous", "Sedimentary", "Metamorphic"].map((cat) => (
          <button
            key={cat}
            onClick={() => setCategory(cat)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              category === cat
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {cat === "ALL" ? "All Types" : cat}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {filtered.map((rock) => {
          const accent = rock.category === "Igneous" ? "14 75% 57%" : rock.category === "Sedimentary" ? "41 53% 64%" : "200 41% 61%";
          return (
          <SculptedCard key={rock.id} accent="aqua" className="space-y-3 p-4">
            <div className="flex items-start gap-3">
              <div className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl" style={{ ["--glow-color" as string]: accent }}>{rock.emoji}</div>
              <div className="min-w-0 flex-1">
                <h3 className="font-display text-sm font-bold text-foreground">{rock.name}</h3>
                <TagChip accent={`hsl(${accent})`}>{rock.category}</TagChip>
              </div>
            </div>
            <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{rock.description}</p>
            <div>
              <p className="mb-1 text-xs font-bold text-foreground">Formation</p>
              <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{rock.formation}</p>
            </div>
            <div>
              <p className="mb-1 text-xs font-bold text-foreground">Key traits</p>
              <ul className="space-y-0.5">
                {rock.keyTraits.map((trait) => (
                  <li key={trait} className="flex items-start gap-1.5 text-xs text-[hsl(var(--text-mid))]">
                    <span className="mt-0.5" style={{ color: `hsl(${CITRINE_HEX})` }}>•</span>
                    {trait}
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <p className="mb-1 text-xs font-bold text-foreground">Famous examples</p>
              <div className="flex flex-wrap gap-1.5">
                {rock.examples.map((ex) => (
                  <TagChip key={ex} accent={`hsl(${AQUA_HEX})`}>{ex}</TagChip>
                ))}
              </div>
            </div>
          </SculptedCard>
          );
        })}
      </div>
     </div>
    </ScreenScaffold>
  );
}
