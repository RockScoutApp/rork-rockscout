import { useState, useMemo } from "react";
import { Search, X, Hammer, Gem, ExternalLink, Star } from "lucide-react";
import { Input } from "@/components/ui/input";
import { gearItems, LAPIDARY_GEAR_IDS } from "@/data/gear";
import { recordAffiliateClick } from "@/lib/affiliate-tracker";
import { getTopPickIds } from "@/lib/top-picks";

interface LapidaryTopic {
  id: string;
  title: string;
  category: "Cutting" | "Grinding" | "Polishing" | "Cabbing" | "Safety";
  emoji: string;
  description: string;
  tips: string[];
  tools: string[];
}

const TOPICS: LapidaryTopic[] = [
  {
    id: "trim-saw",
    title: "Trim Saw Cutting",
    category: "Cutting",
    emoji: "🪚",
    description: "The first step in lapidary — cutting rough rock into workable slabs or preforms. A diamond-blade trim saw slices through stone with water coolant. The blade is a continuous rim of diamonds on a steel disc.",
    tips: [
      "Always use water coolant — a dry blade will shatter the stone and the blade",
      "Let the blade do the work — don't force the stone through",
      "Mark your cut line with a pencil or aluminum scratch first",
      "Keep fingers clear of the blade — use a vise or push stick for small pieces",
    ],
    tools: ["Trim saw (6-10 inch)", "Diamond blade (continuous rim)", "Water reservoir", "Vise or hold-down clamp"],
  },
  {
    id: "slab-saw",
    title: "Slab Saw Cutting",
    category: "Cutting",
    emoji: "📐",
    description: "A larger saw for cutting big rough into slabs. Slab saws have an adjustable feed mechanism that advances the stone into the blade at a controlled rate. The result is flat, even slabs ready for cabbing.",
    tips: [
      "Adjust feed rate to the stone hardness — harder stone = slower feed",
      "Check water level frequently — the blade must stay submerged",
      "Slab thickness: 5-7mm for cabbing, 3-4mm for thin sections",
      "Replace blades when they slow down or start wandering off-line",
    ],
    tools: ["Slab saw (14-24 inch)", "Diamond blade (segmented or continuous)", "Adjustable feed mechanism", "Coolant (water + rust inhibitor)"],
  },
  {
    id: "grinding-wheels",
    title: "Grinding & Shaping",
    category: "Grinding",
    emoji: "⚙️",
    description: "Grinding wheels shape the slab into a cabochon preform. Start with a coarse wheel (80-100 grit) to rough out the shape, then progress through finer grits. Diamond or silicon carbide wheels are standard.",
    tips: [
      "Always progress from coarse to fine — never skip grits",
      "Keep the stone moving to avoid flat spots",
      "Wet grind only — dry grinding creates harmful silica dust",
      "80 grit → 220 grit → 400 grit → 600 grit is the standard progression",
    ],
    tools: ["Cabbing machine (6-8 inch wheels)", "Diamond wheels (80/220/400/600 grit)", "Water drip system", "Dop pot (wax)"],
  },
  {
    id: "sanding",
    title: "Sanding",
    category: "Grinding",
    emoji: "🧽",
    description: "After grinding, sanding removes the scratches left by the coarse wheels. Use silicon carbide sandpaper in progressive grits (220, 400, 600). Some lapidaries go to 1200 or 3000 for a mirror finish.",
    tips: [
      "Wet sanding is cleaner and produces a better finish",
      "Inspect under a strong light between grits — look for scratches",
      "Don't move to a finer grit until all scratches from the previous grit are gone",
      "Flexibile sanding pads work well for curved cabochon surfaces",
    ],
    tools: ["Wet/dry sandpaper (220-3000 grit)", "Sanding sticks", "Water spray bottle", "Strong light for inspection"],
  },
  {
    id: "polishing",
    title: "Polishing",
    category: "Polishing",
    emoji: "✨",
    description: "The final step — bringing the stone to a mirror polish. Polishing compounds like cerium oxide, tin oxide, or diamond paste on a soft pad (felt, leather, or polishing pad) produce the final shine.",
    tips: [
      "Use a very soft pad (felt or leather) — hard pads re-scratch",
      "Cerium oxide is the standard for quartz and agate",
      "Tin oxide works for softer stones like calcite and marble",
      "Diamond paste (50K-100K) for the ultimate mirror finish on hard stones",
    ],
    tools: ["Polishing pad (felt or leather)", "Cerium oxide or tin oxide powder", "Diamond paste (optional)", "Clean water"],
  },
  {
    id: "dopping",
    title: "Dopping (Wax Holding)",
    category: "Cabbing",
    emoji: "🕯️",
    description: "Dopping wax attaches the stone to a dop stick, giving you a handle for safer, more controlled grinding and polishing. The stone is warmed, the wax is melted, and the stick is pressed into the wax.",
    tips: [
      "Warm the stone gently — thermal shock can crack some stones",
      "Use dopping wax (green wax for hard stones, red wax for softer)",
      "Position the stick so the face you're working is accessible",
      "To remove: warm the stone and twist off, or freeze with ice",
    ],
    tools: ["Dopping wax (green or red)", "Dop sticks (wooden dowels)", "Alcohol lamp or wax pot", "Pliers for removal"],
  },
  {
    id: "cabbing-shape",
    title: "Cabochon Shapes",
    category: "Cabbing",
    emoji: "🟠",
    description: "A cabochon is a gem with a smooth, domed top and a flat back. Common shapes include oval, round, teardrop, and freeform. The dome is created by grinding the top into a gentle curve.",
    tips: [
      "Oval is the most common — fits standard jewelry settings",
      "Calibrate your shape to standard sizes (e.g. 18x13mm, 25x18mm)",
      "A higher dome shows more flash in star stones and tiger's eye",
      "Freeform cabs let the stone's natural pattern dictate the shape",
    ],
    tools: ["Cabochon templates", "Calipers", "Marking pencil"],
  },
  {
    id: "safety",
    title: "Lapidary Safety",
    category: "Safety",
    emoji: "🦺",
    description: "Lapidary work produces silica dust, flying debris, and involves spinning blades and wheels. Safety gear is non-negotiable — silicosis is a serious, irreversible lung disease caused by inhaling silica dust.",
    tips: [
      "ALWAYS wet grind — never dry grind silica-bearing stones",
      "Wear safety glasses at all times near spinning equipment",
      "Wear a dust mask (N95 or better) if any dry work is done",
      "Tie back long hair and avoid loose clothing near equipment",
      "Keep a first aid kit nearby — cuts are common",
      "Ventilate your workspace — even wet grinding produces fine mist",
    ],
    tools: ["Safety glasses (Z87+)", "Dust mask (N95 minimum)", "First aid kit", "Ventilation fan"],
  },
];

const CATEGORY_COLORS: Record<string, string> = {
  Cutting: "#C97B4A",
  Grinding: "#8B7D6B",
  Polishing: "#E0A040",
  Cabbing: "#9B59B6",
  Safety: "#E74C3C",
};

export default function LapidaryBasics() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<string>("ALL");

  const topPickIds = useMemo(() => getTopPickIds(), []);

  const lapidaryGear = useMemo(
    () => gearItems.filter((i) => LAPIDARY_GEAR_IDS.includes(i.id)),
    [],
  );

  const filtered = TOPICS.filter((t) => {
    if (category !== "ALL" && t.category !== category) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        t.title.toLowerCase().includes(q) ||
        t.description.toLowerCase().includes(q) ||
        t.tips.some((tip) => tip.toLowerCase().includes(q))
      );
    }
    return true;
  });

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Lapidary Basics
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {TOPICS.length} guides for cutting, grinding, and polishing stones
        </p>
      </div>

      <div className="rounded-xl border border-amber-500/30 bg-amber-500/5 p-4">
        <h3 className="mb-1 flex items-center gap-2 font-display text-sm font-semibold text-amber-600">
          <Hammer className="h-4 w-4" />
          Safety first
        </h3>
        <p className="text-sm text-foreground/80">
          Silicosis is an irreversible lung disease caused by inhaling silica
          dust. <strong>Always wet grind</strong> and wear a dust mask when
          working with stone. This is not optional.
        </p>
      </div>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search lapidary topics..."
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
        {["ALL", "Cutting", "Grinding", "Polishing", "Cabbing", "Safety"].map((cat) => (
          <button
            key={cat}
            onClick={() => setCategory(cat)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              category === cat
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {cat === "ALL" ? "All Topics" : cat}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {filtered.map((topic) => {
          const color = CATEGORY_COLORS[topic.category] ?? "#666";
          return (
            <div
              key={topic.id}
              className="space-y-3 rounded-xl border border-border bg-card p-4"
            >
              <div className="flex items-start gap-3">
                <div
                  className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg text-xl"
                  style={{ backgroundColor: `${color}20` }}
                >
                  {topic.emoji}
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="font-display text-sm font-semibold text-foreground">
                    {topic.title}
                  </h3>
                  <span
                    className="rounded-full px-2 py-0.5 text-[10px] font-medium"
                    style={{ backgroundColor: `${color}20`, color }}
                  >
                    {topic.category}
                  </span>
                </div>
              </div>
              <p className="text-sm leading-relaxed text-muted-foreground">
                {topic.description}
              </p>
              <div>
                <p className="mb-1 text-xs font-medium text-foreground">Tips</p>
                <ul className="space-y-1">
                  {topic.tips.map((tip, i) => (
                    <li
                      key={i}
                      className="flex items-start gap-1.5 text-xs text-muted-foreground"
                    >
                      <span className="mt-0.5 text-primary">•</span>
                      {tip}
                    </li>
                  ))}
                </ul>
              </div>
              <div>
                <p className="mb-1 text-xs font-medium text-foreground">
                  Tools needed
                </p>
                <div className="flex flex-wrap gap-1.5">
                  {topic.tools.map((tool) => (
                    <span
                      key={tool}
                      className="rounded-full bg-muted px-2.5 py-0.5 text-xs text-muted-foreground"
                    >
                      {tool}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Lapidary Equipment — Affiliate Links */}
      <div className="space-y-3">
        <div className="flex items-center gap-2">
          <Gem className="h-5 w-5 text-amber-500" />
          <h2 className="font-display text-lg font-bold text-foreground">
            Lapidary Gear & Equipment
          </h2>
        </div>
        <p className="text-sm text-muted-foreground">
          Tools and supplies for cutting, grinding, and polishing your finds into finished stones.
        </p>
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {lapidaryGear.map((item) => (
            <a
              key={item.id}
              href={item.url}
              target="_blank"
              rel="noopener noreferrer sponsored"
              onClick={() => recordAffiliateClick(item.id, item.name)}
              className={`group flex flex-col gap-2 rounded-xl border bg-card p-4 transition-all hover:border-amber-500/40 ${
                topPickIds.has(item.id) ? "border-amber-500/40 ring-1 ring-amber-500/20" : "border-border"
              }`}
            >
              <div className="flex items-start gap-3">
                <span className="text-2xl">{item.emoji}</span>
                <div className="min-w-0 flex-1">
                  <div className="flex items-center gap-1.5">
                    <h3 className="font-display text-sm font-semibold text-foreground">
                      {item.name}
                    </h3>
                    {topPickIds.has(item.id) && (
                      <span className="inline-flex shrink-0 items-center gap-0.5 rounded-full bg-amber-500/15 px-1.5 py-0.5 text-[10px] font-bold uppercase tracking-wide text-amber-500 ring-1 ring-amber-500/40">
                        <Star className="h-2.5 w-2.5 fill-amber-500 text-amber-500" />
                        Top Pick
                      </span>
                    )}
                  </div>
                  <p className="mt-0.5 text-xs text-primary">{item.priceBand}</p>
                </div>
                <ExternalLink className="h-4 w-4 shrink-0 text-muted-foreground transition-colors group-hover:text-primary" />
              </div>
              <p className="text-xs leading-relaxed text-muted-foreground">
                {item.description}
              </p>
            </a>
          ))}
        </div>
        <p className="text-xs text-muted-foreground">
          Links open separately in your browser. As an Amazon Associate, RockScout earns from qualifying purchases.
        </p>
      </div>
    </div>
  );
}
