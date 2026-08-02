import { useState } from "react";
import { Search, X, Globe, Navigation } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface NaturalWonder {
  id: string;
  name: string;
  location: string;
  latitude: number;
  longitude: number;
  type: string;
  description: string;
  formation: string;
  rocksToFind: string[];
  funFacts: string[];
}

const WONDERS: NaturalWonder[] = [
  {
    id: "grand-canyon",
    name: "Grand Canyon",
    location: "Arizona, USA",
    latitude: 36.1,
    longitude: -112.1,
    type: "Sedimentary",
    description: "A 277-mile chasm carved by the Colorado River over 5–6 million years, exposing nearly 2 billion years of Earth's geological history in its layered walls.",
    formation: "The Colorado River cut down through the Colorado Plateau as it was uplifted during the Laramide orogeny. The canyon's walls reveal sedimentary layers from the Paleozoic era.",
    rocksToFind: ["Kaibab Limestone", "Coconino Sandstone", "Hermit Shale", "Vishnu Schist", "Zoroaster Granite"],
    funFacts: [
      "The oldest rocks at the bottom are about 1.8 billion years old — nearly half the age of Earth.",
      "The canyon averages 4,000 feet deep and up to 18 miles wide.",
      "The Great Unconformity: a gap of up to 1.2 billion years between rock layers is visible here.",
    ],
  },
  {
    id: "giants-causeway",
    name: "Giant's Causeway",
    location: "County Antrim, Northern Ireland",
    latitude: 55.24,
    longitude: -6.51,
    type: "Volcanic",
    description: "An area of about 40,000 interlocking basalt columns, mostly hexagonal, formed by an ancient volcanic eruption.",
    formation: "Around 50–60 million years ago, lava cooled rapidly, contracting and fracturing into the characteristic hexagonal columns. The same formation appears at Fingal's Cave in Scotland.",
    rocksToFind: ["Basalt", "Columnar Basalt", "Tholeiitic Basalt"],
    funFacts: [
      "Legend says the giant Finn McCool built it as a bridge to fight a Scottish giant.",
      "The columns are mostly hexagonal, but some have 4, 5, 7, or 8 sides.",
      "The tallest columns reach 39 feet high.",
    ],
  },
  {
    id: "meteor-crater",
    name: "Meteor Crater",
    location: "Arizona, USA",
    latitude: 35.027,
    longitude: -111.023,
    type: "Impact",
    description: "A 4,000-foot-wide, 550-foot-deep crater created ~50,000 years ago by a nickel-iron meteorite about 150 feet across.",
    formation: "A meteorite traveling ~26,000 mph struck the Colorado Plateau, releasing energy equivalent to ~20 megatons of TNT. The crater is the best-preserved meteorite impact site on Earth.",
    rocksToFind: ["Meteorite fragments (Canyon Diablo)", "Shock-metamorphosed Coconino Sandstone", "Shatter cones"],
    funFacts: [
      "The meteorite that created it was mostly vaporized on impact — fragments are called 'Canyon Diablo' meteorites.",
      "It was the first crater proven to be of impact origin (by Daniel Barringer, 1903).",
      "NASA trained Apollo astronauts here for Moon geology.",
    ],
  },
  {
    id: "yellowstone",
    name: "Yellowstone Caldera",
    location: "Wyoming, USA",
    latitude: 44.428,
    longitude: -110.588,
    type: "Volcanic",
    description: "A 30x45-mile supervolcano caldera — one of Earth's largest active volcanic systems. Powers 10,000+ geothermal features including Old Faithful.",
    formation: "Three massive eruptions (2.1M, 1.3M, and 640K years ago) each erupted hundreds of cubic miles of magma, collapsing the ground into the caldera. The magma chamber is still active.",
    rocksToFind: ["Obsidian", "Rhyolite", "Pumice", "Tuff", "Sinter (geyserite)", "Petrified wood"],
    funFacts: [
      "The last supereruption (640K years ago) was 1,000x larger than Mt. St. Helens.",
      "Yellowstone hosts half of the world's geysers.",
      "Obsidian Cliff was a major source of toolstone for Native Americans.",
    ],
  },
  {
    id: "carlsbad",
    name: "Carlsbad Caverns",
    location: "New Mexico, USA",
    latitude: 32.174,
    longitude: -104.446,
    type: "Karst",
    description: "A limestone cave system with over 119 known caves, featuring the Big Room — one of the largest cave chambers in North America.",
    formation: "Sulfuric acid (not carbonic acid) dissolved the 250-million-year-old Capitan Reef limestone. This is a rare cave formation mechanism — most caves form from carbonic acid.",
    rocksToFind: ["Limestone", "Speleothems (stalactites, stalagmites, columns)", "Gypsum", "Selenite"],
    funFacts: [
      "The Big Room is 4,000 feet long and 625 feet wide.",
      "The caves formed when hydrogen sulfide from oil deposits rose and mixed with oxygenated water.",
      "Home to ~400,000 Brazilian free-tailed bats in summer.",
    ],
  },
  {
    id: "mt-rushmore",
    name: "Mount Rushmore",
    location: "South Dakota, USA",
    latitude: 43.879,
    longitude: -103.460,
    type: "Metamorphic",
    description: "A 60-foot granite sculpture of four U.S. presidents carved into the Black Hills — a geological as well as cultural landmark.",
    formation: "The Harney Peak Granite is a 1.7-billion-year-old intrusion — one of the oldest exposed rocks in North America. Gutzon Borglum chose it for its durability and fine grain.",
    rocksToFind: ["Harney Peak Granite", "Mica schist", "Pegmatite"],
    funFacts: [
      "The granite is about 1.7 billion years old — older than complex life.",
      "The carving was designed to erode at ~1 inch every 10,000 years.",
      "The Black Hills are a laccolith — magma that pushed up but never broke the surface.",
    ],
  },
  {
    id: "wave",
    name: "The Wave",
    location: "Arizona–Utah border, USA",
    latitude: 36.996,
    longitude: -112.005,
    type: "Sedimentary",
    description: "A swirling sandstone formation with undulating, wave-like layers of red, orange, and white Navajo Sandstone.",
    formation: "190-million-year-old Navajo Sandstone dunes were petrified into stone. Water erosion carved the smooth, flowing troughs over millions of years.",
    rocksToFind: ["Navajo Sandstone", "Cross-bedded sandstone", "Iron oxide concretions"],
    funFacts: [
      "Only 20 permits are issued per day — it's one of the most sought-after hikes in the US.",
      "The swirling pattern is from cross-bedding — ancient sand dune layers.",
      "The sandstone is Jurassic-age, ~190 million years old.",
    ],
  },
  {
    id: "chocolate-hills",
    name: "Chocolate Hills",
    location: "Bohol, Philippines",
    latitude: 9.815,
    longitude: 124.170,
    type: "Karst",
    description: "1,200+ symmetrical, cone-shaped hills that turn brown in the dry season — resembling chocolate drops scattered across the landscape.",
    formation: "Marine limestone was uplifted and then weathered by rainwater over millions of years. The hills are what remain of a once-continuous limestone layer.",
    rocksToFind: ["Limestone", "Karst formations"],
    funFacts: [
      "There are 1,268 hills spread across 20 square miles.",
      "Legend says they are the tears of a giant mourning his lost love.",
      "They turn green in the wet season and brown (chocolate) in the dry season.",
    ],
  },
  {
    id: "antelope-canyon",
    name: "Antelope Canyon",
    location: "Arizona, USA",
    latitude: 36.862,
    longitude: -111.374,
    type: "Sedimentary",
    description: "A slot canyon with flowing, sculpted Navajo Sandstone walls — one of the most photographed canyons in the world.",
    formation: "Flash flooding eroded the Navajo Sandstone over centuries. Water picks up sand and acts like a sandpaper, sculpting the smooth, flowing walls.",
    rocksToFind: ["Navajo Sandstone", "Cross-bedded sandstone"],
    funFacts: [
      "Light beams shine down into the canyon at midday — a famous photography moment.",
      "The canyon is on Navajo Nation land and requires a Navajo guide to visit.",
      "Upper and Lower Antelope Canyon have different shapes and difficulty.",
    ],
  },
  {
    id: "ireland-cliffs",
    name: "Cliffs of Moher",
    location: "County Clare, Ireland",
    latitude: 52.971,
    longitude: -9.431,
    type: "Sedimentary",
    description: "700-foot sea cliffs stretching 9 miles along Ireland's west coast — layers of sandstone, siltstone, and shale.",
    formation: "The cliffs expose 300+ million years of sedimentary rock — Carboniferous-age Namurian sandstone and shale. The Atlantic waves have been cutting them back for millennia.",
    rocksToFind: ["Namurian sandstone", "Shale", "Fossil brachiopods", "Ripple marks"],
    funFacts: [
      "The cliffs reach 702 feet at their highest point.",
      "They appear in 'The Princess Bride' as the Cliffs of Insanity.",
      "Over 30,000 seabirds nest on the cliffs each year.",
    ],
  },
];

const TYPE_COLORS: Record<string, string> = {
  Volcanic: "#E74C3C",
  Sedimentary: "#D9B26A",
  Metamorphic: "#6FA8C7",
  Impact: "#9B59B6",
  Karst: "#5A8C5A",
  Coastal: "#5BB8B8",
  Desert: "#C97B4A",
  Mountain: "#8B7D6B",
};

export default function NaturalWonders() {
  const [search, setSearch] = useState("");
  const [type, setType] = useState<string>("ALL");

  const types = ["ALL", ...new Set(WONDERS.map((w) => w.type))];

  const filtered = WONDERS.filter((w) => {
    if (type !== "ALL" && w.type !== type) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        w.name.toLowerCase().includes(q) ||
        w.location.toLowerCase().includes(q) ||
        w.description.toLowerCase().includes(q)
      );
    }
    return true;
  });

  const openInMaps = (w: NaturalWonder) => {
    window.open(
      `https://www.google.com/maps/search/?api=1&query=${w.latitude},${w.longitude}`,
      "_blank",
      "noopener,noreferrer",
    );
  };

  return (
    <ScreenScaffold title="Natural Wonders" onBack={() => window.history.back()}>
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        {WONDERS.length} world-famous geological landmarks and the stories behind them
      </p>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search wonders by name or location..."
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
        {types.map((t) => (
          <button
            key={t}
            onClick={() => setType(t)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              type === t
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {t === "ALL" ? "All Types" : t}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {filtered.map((w) => {
          const color = TYPE_COLORS[w.type] ?? "#666";
          return (
            <SculptedCard key={w.id} accent="aqua" className="space-y-3 p-4">
              <div className="flex items-start justify-between gap-2">
                <div className="flex items-center gap-2">
                  <Globe className="h-5 w-5" style={{ color: `hsl(${AQUA_HEX})` }} />
                  <h3 className="font-display text-sm font-bold text-foreground">{w.name}</h3>
                </div>
                <TagChip accent={color}>{w.type}</TagChip>
              </div>
              <p className="text-xs" style={{ color: `hsl(${CITRINE_HEX})` }}>{w.location}</p>
              <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{w.description}</p>
              <div>
                <p className="mb-1 text-xs font-bold text-foreground">How it formed</p>
                <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{w.formation}</p>
              </div>
              <div>
                <p className="mb-1 text-xs font-bold text-foreground">Rocks to find</p>
                <div className="flex flex-wrap gap-1.5">
                  {w.rocksToFind.map((rock) => (
                    <TagChip key={rock} accent={`hsl(${AQUA_HEX})`}>{rock}</TagChip>
                  ))}
                </div>
              </div>
              <div className="rounded-lg bg-muted/30 p-2.5">
                <ul className="space-y-1">
                  {w.funFacts.map((fact, i) => (
                    <li key={i} className="flex items-start gap-1.5 text-xs text-[hsl(var(--text-mid))]">
                      <span className="mt-0.5" style={{ color: `hsl(${CITRINE_HEX})` }}>•</span>
                      {fact}
                    </li>
                  ))}
                </ul>
              </div>
              <button onClick={() => openInMaps(w)}
                className="flex w-full items-center justify-center gap-1.5 rounded-lg border border-border py-2 text-xs font-medium transition-colors hover:border-primary/40"
                style={{ color: `hsl(${CITRINE_HEX})` }}>
                <Navigation className="h-3.5 w-3.5" />
                View on map
              </button>
            </SculptedCard>
          );
        })}
      </div>
     </div>
    </ScreenScaffold>
  );
}
