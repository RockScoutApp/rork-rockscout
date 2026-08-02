import { useState } from "react";
import { Search, X, Bone } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface FossilPeriod {
  id: string;
  name: string;
  era: string;
  emoji: string;
  timeRange: string;
  duration: string;
  climate: string;
  majorEvents: string[];
  keyOrganisms: { name: string; type: string; emoji: string; description: string }[];
  funFact: string;
}

const PERIODS: FossilPeriod[] = [
  {
    id: "cambrian",
    name: "Cambrian",
    era: "Paleozoic",
    emoji: "🦠",
    timeRange: "541 – 485 million years ago",
    duration: "~56 million years",
    climate: "Warm global climate with high sea levels. The supercontinent Gondwana dominated the southern hemisphere.",
    majorEvents: [
      "The Cambrian Explosion — almost all major animal body plans appear",
      "First animals with hard shells and skeletons",
      "First trilobites, brachiopods, and mollusks appear",
    ],
    keyOrganisms: [
      { name: "Trilobite", type: "Arthropod", emoji: "🦠", description: "The iconic Paleozoic arthropod with complex compound eyes." },
      { name: "Anomalocaris", type: "Apex predator", emoji: "🦐", description: "A 3-foot swimming predator with grasping claws and a circular mouth." },
      { name: "Hallucigenia", type: "Lobopodian", emoji: "🐛", description: "A bizarre spiked worm-like creature named for its hallucination-like appearance." },
    ],
    funFact: "Before the Cambrian Explosion, almost all life was microscopic. In just ~20 million years, nearly every modern animal body plan appeared.",
  },
  {
    id: "ordovician",
    name: "Ordovician",
    era: "Paleozoic",
    emoji: "🐚",
    timeRange: "485 – 444 million years ago",
    duration: "~41 million years",
    climate: "Warm at first, but ended with a severe ice age and mass extinction.",
    majorEvents: [
      "Great Ordovician Biodiversification Event — marine diversity tripled",
      "First land plants appear",
      "End-Ordovician mass extinction wiped out ~85% of marine species",
    ],
    keyOrganisms: [
      { name: "Orthoceras", type: "Cephalopod", emoji: "📐", description: "A straight-shelled nautiloid that grew up to 14 feet long." },
      { name: "Graptolite", type: "Colonial plankton", emoji: "📊", description: "Tiny saw-tooth colonies that are vital index fossils." },
      { name: "Eurypterid", type: "Sea scorpion", emoji: "🦂", description: "The earliest eurypterids appeared — they would grow to 8 feet." },
    ],
    funFact: "The Ordovician ended with the second-largest mass extinction in Earth history — triggered by a short, intense ice age.",
  },
  {
    id: "silurian",
    name: "Silurian",
    era: "Paleozoic",
    emoji: "🌿",
    timeRange: "444 – 419 million years ago",
    duration: "~25 million years",
    climate: "Warm and stable after the ice age. High sea levels created extensive shallow seas.",
    majorEvents: [
      "First vascular land plants with stems appear",
      "First jawed fish (placoderms and acanthodians)",
      "Coral reefs recover and expand dramatically",
    ],
    keyOrganisms: [
      { name: "Eurypterid", type: "Sea scorpion", emoji: "🦂", description: "Silurian eurypterids dominated shallow seas and brackish lagoons." },
      { name: "Cooksonia", type: "Early land plant", emoji: "🌱", description: "One of the first vascular land plants — simple, leafless stems." },
      { name: "Tabulate coral", type: "Colonial coral", emoji: "🪸", description: "Tabulate corals built vast reefs in Silurian tropical seas." },
    ],
    funFact: "New York's state fossil — the eurypterid — comes from Silurian rocks near Buffalo.",
  },
  {
    id: "devonian",
    name: "Devonian",
    era: "Paleozoic",
    emoji: "🐟",
    timeRange: "419 – 359 million years ago",
    duration: "~60 million years",
    climate: "Warm and arid. The first extensive forests appeared, pulling CO₂ from the atmosphere.",
    majorEvents: [
      "Age of Fishes — fish diversity explodes",
      "First forests and seed plants appear",
      "First tetrapods walk on land",
    ],
    keyOrganisms: [
      { name: "Dunkleosteus", type: "Placoderm fish", emoji: "🐠", description: "A 30-foot armored fish with the strongest bite of any fish." },
      { name: "Tiktaalik", type: "Transitional fossil", emoji: "🐟", description: "The 'fishapod' — halfway between fish and land animals." },
      { name: "Rugose coral", type: "Horn coral", emoji: "🪸", description: "Solitary cone-shaped corals that studded Devonian seafloors." },
    ],
    funFact: "Devonian rocks produce some of the finest horn coral and brachiopod fossils in the world.",
  },
  {
    id: "carboniferous",
    name: "Carboniferous",
    era: "Paleozoic",
    emoji: "🌳",
    timeRange: "359 – 299 million years ago",
    duration: "~60 million years",
    climate: "Hot and humid with vast swampy forests. Oxygen reached 35% — the highest in Earth history.",
    majorEvents: [
      "Massive coal deposits form from ancient swamp forests",
      "Oxygen levels peak — insects grow to enormous sizes",
      "First reptiles appear and lay shelled eggs on land",
    ],
    keyOrganisms: [
      { name: "Meganeura", type: "Giant dragonfly", emoji: "🛸", description: "A dragonfly with a 2.5-foot wingspan — possible only with high oxygen." },
      { name: "Fossil fern", type: "Tree fern", emoji: "🌿", description: "Giant tree ferns formed the canopy of Carboniferous coal forests." },
      { name: "Arthropleura", type: "Giant millipede", emoji: "🐛", description: "An 8-foot millipede — the largest land arthropod ever." },
    ],
    funFact: "The Carboniferous is named for its massive coal beds — from 60-million-year-old swamp forests.",
  },
  {
    id: "permian",
    name: "Permian",
    era: "Paleozoic",
    emoji: "🏜️",
    timeRange: "299 – 252 million years ago",
    duration: "~47 million years",
    climate: "Pangaea assembled into one supercontinent. The interior was a vast desert. Ended with Earth's worst extinction.",
    majorEvents: [
      "The Great Dying — ~96% of marine species go extinct",
      "Synapsids (mammal ancestors) dominate land",
      "Conifer forests spread across Pangaea",
    ],
    keyOrganisms: [
      { name: "Dimetrodon", type: "Synapsid", emoji: "🦖", description: "A sail-backed predator more closely related to mammals than dinosaurs." },
      { name: "Trilobite", type: "Arthropod", emoji: "🦠", description: "Trilobites survived 270 million years but perished in this extinction." },
      { name: "Glossopteris", type: "Seed fern", emoji: "🌿", description: "A seed fern whose fossils on multiple continents proved continental drift." },
    ],
    funFact: "The Siberian Traps — a gigantic volcanic eruption lasting a million years — caused the Permian extinction.",
  },
  {
    id: "jurassic",
    name: "Jurassic",
    era: "Mesozoic",
    emoji: "🦕",
    timeRange: "201 – 145 million years ago",
    duration: "~56 million years",
    climate: "Warm and humid. Pangaea continued breaking apart. High sea levels flooded continents.",
    majorEvents: [
      "Age of giant sauropods — the largest land animals ever",
      "First birds (Archaeopteryx) appear",
      "Lush forests and fern prairies cover continents",
    ],
    keyOrganisms: [
      { name: "Brachiosaurus", type: "Sauropod", emoji: "🦕", description: "A colossal sauropod reaching 85 feet long that browsed treetops." },
      { name: "Ammonite", type: "Cephalopod", emoji: "🐚", description: "Jurassic ammonites evolved spectacular coiled forms — some exceeding 6 feet." },
      { name: "Archaeopteryx", type: "First bird", emoji: "🦅", description: "The famous 'dino-bird' with feathers and wings — but also teeth and a tail." },
    ],
    funFact: "The Morrison Formation in the western US has produced more dinosaur fossils than almost anywhere.",
  },
  {
    id: "cretaceous",
    name: "Cretaceous",
    era: "Mesozoic",
    emoji: "🦖",
    timeRange: "145 – 66 million years ago",
    duration: "~79 million years",
    climate: "Warmest period of the last 300 million years. High sea levels created vast inland seas.",
    majorEvents: [
      "First flowering plants appear and diversify",
      "T. rex and Triceratops dominate the land",
      "Asteroid impact ends the Mesozoic — dinosaurs go extinct",
    ],
    keyOrganisms: [
      { name: "Tyrannosaurus rex", type: "Theropod", emoji: "🦖", description: "The 40-foot apex predator with 8-inch teeth that could crush bone." },
      { name: "Mosasaur", type: "Marine reptile", emoji: "🐊", description: "A 50-foot marine reptile that ruled Cretaceous seas." },
      { name: "Ammonite (giant)", type: "Cephalopod", emoji: "🐚", description: "Giant heteromorph ammonites with bizarre unwound shells." },
    ],
    funFact: "The Chicxulub asteroid struck 66 million years ago with the force of 10 billion Hiroshima bombs — ending the dinosaurs in a single day.",
  },
];

export default function Paleontology() {
  const [search, setSearch] = useState("");
  const [era, setEra] = useState<string>("ALL");

  const eras = ["ALL", "Paleozoic", "Mesozoic", "Cenozoic"];

  const filtered = PERIODS.filter((p) => {
    if (era !== "ALL" && p.era !== era) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        p.name.toLowerCase().includes(q) ||
        p.keyOrganisms.some((o) => o.name.toLowerCase().includes(q)) ||
        p.majorEvents.some((e) => e.toLowerCase().includes(q))
      );
    }
    return true;
  });

  return (
    <ScreenScaffold title="Paleontology" onBack={() => window.history.back()}>
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        {PERIODS.length} geological periods, key organisms, and mass extinctions
      </p>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search periods or organisms..."
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
        {eras.map((e) => (
          <button
            key={e}
            onClick={() => setEra(e)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              era === e
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {e === "ALL" ? "All Eras" : e}
          </button>
        ))}
      </div>

      <div className="space-y-3">
        {filtered.map((period) => (
          <SculptedCard key={period.id} accent="citrine" className="space-y-3 p-4">
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-center gap-3">
                <div className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl" style={{ ["--glow-color" as string]: CITRINE_HEX }}>{period.emoji}</div>
                <div>
                  <h3 className="font-display text-base font-bold text-foreground">{period.name} Period</h3>
                  <p className="text-xs text-muted-foreground">{period.era} · {period.timeRange}</p>
                </div>
              </div>
              <TagChip accent={`hsl(${CITRINE_HEX})`}>{period.duration}</TagChip>
            </div>
            <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{period.climate}</p>
            <div>
              <p className="mb-1 text-xs font-bold text-foreground">Major events</p>
              <ul className="space-y-1">
                {period.majorEvents.map((event, i) => (
                  <li key={i} className="flex items-start gap-1.5 text-xs text-[hsl(var(--text-mid))]">
                    <span className="mt-0.5" style={{ color: `hsl(${CITRINE_HEX})` }}>•</span>
                    {event}
                  </li>
                ))}
              </ul>
            </div>
            <div>
              <p className="mb-2 text-xs font-bold text-foreground">Key organisms</p>
              <div className="grid grid-cols-1 gap-2 sm:grid-cols-3">
                {period.keyOrganisms.map((org) => (
                  <div key={org.name} className="rounded-lg bg-muted/30 p-2.5">
                    <div className="flex items-center gap-1.5">
                      <span className="text-lg">{org.emoji}</span>
                      <p className="text-xs font-bold text-foreground">{org.name}</p>
                    </div>
                    <p className="mt-0.5 text-[10px]" style={{ color: `hsl(${AQUA_HEX})` }}>{org.type}</p>
                    <p className="mt-1 text-[11px] leading-relaxed text-[hsl(var(--text-mid))]">{org.description}</p>
                  </div>
                ))}
              </div>
            </div>
            <SculptedCard accent="citrine" className="p-2.5">
              <p className="text-xs text-foreground/80">
                <span className="font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>Fun fact:</span> {period.funFact}
              </p>
            </SculptedCard>
          </SculptedCard>
        ))}
      </div>

      {filtered.length === 0 && (
        <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
          <Bone className="h-10 w-10 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No periods found. Try a different search.</p>
        </SculptedCard>
      )}
     </div>
    </ScreenScaffold>
  );
}
