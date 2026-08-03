import { useState } from "react";
import { Search, X, Sun, AlertTriangle } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface FluorescentMineral {
  name: string;
  color: string;
  uvType: "LW" | "SW" | "Both";
  emoji: string;
  description: string;
  bestLocations: string[];
}

const MINERALS: FluorescentMineral[] = [
  {
    name: "Fluorite",
    color: "Blue, green, purple, white",
    uvType: "Both",
    emoji: "🌈",
    description: "The mineral that gave fluorescence its name. Different localities glow different colors — blue-green from England, purple from Illinois, white from New Mexico.",
    bestLocations: ["Weardale, England", "Cave-in-Rock, IL", "Rogerley Mine, UK", "Mexico"],
  },
  {
    name: "Calcite",
    color: "Red, pink, orange, white",
    uvType: "Both",
    emoji: "🔶",
    description: "One of the most common fluorescent minerals. Red/pink calcite from Franklin, NJ is legendary. Some calcite phosphoresces (keeps glowing after the UV is removed).",
    bestLocations: ["Franklin, NJ", "Balmat, NY", "Mexico", "Iceland"],
  },
  {
    name: "Willemite",
    color: "Bright green",
    uvType: "Both",
    emoji: "🟢",
    description: "The signature fluorescent mineral of Franklin, NJ — the 'fluorescent mineral capital of the world.' Glows intense bright green under both LW and SW. Often found with red calcite, creating a classic green-and-red combo.",
    bestLocations: ["Franklin, NJ", "Sterling Hill, NJ", "Belgium", "Zaire"],
  },
  {
    name: "Yooperlite (Sodalite)",
    color: "Orange, pink, red",
    uvType: "LW",
    emoji: "🪨",
    description: "A syenite rock containing fluorescent sodalite that glows orange-pink under longwave UV. Famous for night-hunting on Lake Superior beaches in Michigan's Upper Peninsula.",
    bestLocations: ["Lake Superior beaches, MI", "Keweenaw Peninsula", "Marquette, MI"],
  },
  {
    name: "Autunite",
    color: "Bright green-yellow",
    uvType: "Both",
    emoji: "☢️",
    description: "A uranium mineral that fluoresces bright green-yellow under UV. Radioactive — handle with care and wash hands. Beautiful but requires safety precautions.",
    bestLocations: ["Spare, France", "Mt. Spokane, WA", "Portugal", "Germany"],
  },
  {
    name: "Scapolite",
    color: "Yellow, orange",
    uvType: "SW",
    emoji: "💛",
    description: "Glow yellow-orange under shortwave UV. The 'cat's eye' variety (fibrous) is prized by collectors. Found in metamorphic rocks.",
    bestLocations: ["Bolivia", "Tanzania", "Ontario, Canada", "Grenville, QC"],
  },
  {
    name: "Scheelite",
    color: "Bright blue-white",
    uvType: "SW",
    emoji: "🔵",
    description: "An important tungsten ore that fluoresces bright blue-white under shortwave UV. Used historically by prospectors with UV lights to locate tungsten deposits at night.",
    bestLocations: ["Korea", "Bolivia", "Mill City, NV", "Cornwall, UK"],
  },
  {
    name: "Hackmanite (Sodalite variety)",
    color: "Pink-orange",
    uvType: "Both",
    emoji: "💗",
    description: "A tenebrescent sodalite — it changes color in sunlight and fluoresces under UV. The pink glow fades in daylight and returns under UV, a phenomenon called tenebrescence.",
    bestLocations: ["Greenland", "Afghanistan", "Bancroft, ON", "Mt. St. Hilaire, QC"],
  },
  {
    name: "Adamite",
    color: "Bright green",
    uvType: "Both",
    emoji: "💚",
    description: "A zinc arsenate mineral that fluoresces bright green under both LW and SW UV. Often found with limonite matrix. Beautiful green glow against orange-brown matrix.",
    bestLocations: ["Mapimi, Mexico", "Ojuela Mine, Mexico", "Laurion, Greece"],
  },
  {
    name: "Hydrozincite",
    color: "Sky blue",
    uvType: "Both",
    emoji: "🔹",
    description: "A zinc carbonate mineral that fluoresces sky blue-white under UV. Often forms as coatings and crusts on other zinc minerals.",
    bestLocations: ["Tsumeb, Namibia", "Leadville, CO", "Zinc mines worldwide"],
  },
  {
    name: "Diamond",
    color: "Blue, green, yellow, red",
    uvType: "Both",
    emoji: "💎",
    description: "About 30% of diamonds fluoresce under UV, most commonly blue. The fluorescence can help identify real diamonds — and affects their appearance in daylight.",
    bestLocations: ["Worldwide (all diamond localities)"],
  },
  {
    name: "Opal",
    color: "Green, white",
    uvType: "Both",
    emoji: "🔮",
    description: "Some opals fluoresce greenish-white under UV. The fluorescence is often weaker than their play-of-color, but visible in dark conditions.",
    bestLocations: ["Coober Pedy, Australia", "Lightning Ridge, Australia", "Virgin Valley, NV"],
  },
];

const UV_GUIDE = [
  {
    type: "Longwave (LW) — 365nm",
    desc: "The most common UV light for rockhounding. Cheaper, safer, and reveals yooperlites, fluorite, calcite, and many common fluorescent minerals. Most LEDs sold for hobbyists are 365nm.",
    pros: ["Cheaper and safer", "Reveals yooperlites and common minerals", "Good for night hunting", "LEDs are affordable ($15-40)"],
  },
  {
    type: "Shortwave (SW) — 254nm",
    desc: "Reveals a different set of fluorescent minerals — willemite, scheelite, scapolite, and many uranium minerals. More expensive and requires eye protection (SW UV damages eyes). Essential for Franklin, NJ collecting.",
    pros: ["Reveals willemite, scheelite", "Essential for serious collecting", "Different color set than LW", "Requires UV safety glasses"],
  },
];

export default function Fluorescence() {
  const [search, setSearch] = useState("");

  const filtered = MINERALS.filter(
    (m) =>
      !search.trim() ||
      m.name.toLowerCase().includes(search.toLowerCase()) ||
      m.description.toLowerCase().includes(search.toLowerCase()) ||
      m.color.toLowerCase().includes(search.toLowerCase()),
  );

  return (
    <ScreenScaffold title="Fluorescence & UV">
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        {MINERALS.length} fluorescent minerals and UV light guide
      </p>

      {/* UV light guide */}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {UV_GUIDE.map((guide) => (
          <SculptedCard key={guide.type} accent="citrine" className="space-y-2 p-4">
            <div className="flex items-center gap-2">
              <Sun className="h-5 w-5" style={{ color: `hsl(${CITRINE_HEX})` }} />
              <h3 className="font-display text-sm font-bold text-foreground">{guide.type}</h3>
            </div>
            <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{guide.desc}</p>
            <ul className="space-y-0.5">
              {guide.pros.map((pro) => (
                <li key={pro} className="flex items-start gap-1.5 text-xs text-[hsl(var(--text-mid))]">
                  <span className="mt-0.5" style={{ color: `hsl(${CITRINE_HEX})` }}>•</span>
                  {pro}
                </li>
              ))}
            </ul>
          </SculptedCard>
        ))}
      </div>

      <SculptedCard accent="danger" className="flex items-start gap-3 p-4">
        <AlertTriangle className="h-5 w-5 shrink-0" style={{ color: "hsl(4 70% 55%)" }} />
        <div>
          <h3 className="text-sm font-bold text-foreground">⚠ Safety note</h3>
          <p className="mt-1 text-xs text-[hsl(var(--text-mid))]">
            Never look directly into a UV light — especially shortwave UV, which
            can damage eyes. Wear UV-blocking safety glasses when using SW lights.
            Some fluorescent minerals (autunite, torbernite) are radioactive —
            handle with care and wash hands after.
          </p>
        </div>
      </SculptedCard>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search fluorescent minerals..."
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
        {filtered.map((mineral) => (
          <SculptedCard key={mineral.name} accent="amethyst" className="space-y-2 p-4">
            <div className="flex items-start justify-between gap-2">
              <div className="flex items-center gap-2">
                <div className="glowing-border flex h-10 w-10 items-center justify-center rounded-xl text-xl" style={{ ["--glow-color" as string]: "265 47% 67%" }}>{mineral.emoji}</div>
                <h3 className="font-display text-sm font-bold text-foreground">{mineral.name}</h3>
              </div>
              <TagChip accent={mineral.uvType === "LW" ? "hsl(210 70% 55%)" : mineral.uvType === "SW" ? "hsl(265 47% 67%)" : `hsl(${CITRINE_HEX})`}>{mineral.uvType}</TagChip>
            </div>
            <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{mineral.description}</p>
            <div>
              <p className="text-xs font-bold text-foreground">Glow color</p>
              <p className="text-xs text-[hsl(var(--text-mid))]">{mineral.color}</p>
            </div>
            <div>
              <p className="mb-1 text-xs font-bold text-foreground">Best locations</p>
              <div className="flex flex-wrap gap-1.5">
                {mineral.bestLocations.map((loc) => (
                  <TagChip key={loc} accent={`hsl(${AQUA_HEX})`}>{loc}</TagChip>
                ))}
              </div>
            </div>
          </SculptedCard>
        ))}
      </div>
     </div>
    </ScreenScaffold>
  );
}
