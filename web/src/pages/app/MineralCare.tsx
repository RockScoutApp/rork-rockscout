import { useState } from "react";
import { Search, X, Droplets, Sparkles, Shield, Box, AlertTriangle } from "lucide-react";
import { Input } from "@/components/ui/input";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface CareTip {
  id: string;
  title: string;
  category: "Cleaning" | "Storage" | "Display" | "Handling";
  emoji: string;
  description: string;
  appliesTo: string[];
  warning?: string;
}

const CARE_TIPS: CareTip[] = [
  {
    id: "clean-water",
    title: "Water Rinse",
    category: "Cleaning",
    emoji: "💧",
    description: "The safest first step for almost any mineral. Rinse with lukewarm water and gently brush with a soft toothbrush to remove dust and loose dirt. Safe for most hard minerals.",
    appliesTo: ["Quartz", "Feldspar", "Granite", "Basalt", "Most silicates"],
  },
  {
    id: "clean-soft-brush",
    title: "Soft Brush Cleaning",
    category: "Cleaning",
    emoji: "🪥",
    description: "A soft-bristled toothbrush or paintbrush removes dust from delicate crystal surfaces without scratching. Use dry for dust, damp for stuck-on dirt. Never use stiff brushes on soft minerals.",
    appliesTo: ["All specimens", "Especially delicate crystals"],
  },
  {
    id: "clean-ultrasonic",
    title: "Ultrasonic Cleaner",
    category: "Cleaning",
    emoji: "🔊",
    description: "Ultrasonic cleaners use high-frequency vibrations to remove grime. Effective but risky — can shatter fragile crystals, fracture stones with inclusions, or dissolve some minerals. Test on a cheap specimen first.",
    appliesTo: ["Hard, durable gems only"],
    warning: "Never use on opal, pearl, emerald, tanzanite, fluorite, or any included/fractured stone.",
  },
  {
    id: "clean-acid",
    title: "Acid Cleaning (Calcite Removal)",
    category: "Cleaning",
    emoji: "🧪",
    description: "Weak acid (vinegar or dilute HCl) dissolves calcite coatings from quartz and other acid-resistant minerals. The classic way to clean quartz from calcite matrix. Bubbles vigorously when calcite is present.",
    appliesTo: ["Quartz", "Pyrite", "Gold in quartz"],
    warning: "Never use acid on calcite, limestone, marble, azurite, malachite, or any carbonate — it will dissolve your specimen.",
  },
  {
    id: "clean-iron-stain",
    title: "Iron Stain Removal",
    category: "Cleaning",
    emoji: "🟤",
    description: "Iron oxide stains (rusty brown coating) can be removed with oxalic acid (Iron Out). Soak the specimen for hours to days. Works well on quartz but requires thorough rinsing afterward.",
    appliesTo: ["Quartz", "Hard silicates"],
    warning: "Toxic — wear gloves, eye protection, and work in a ventilated area. Rinse thoroughly.",
  },
  {
    id: "store-separate",
    title: "Store Specimens Separately",
    category: "Storage",
    emoji: "📦",
    description: "Hard minerals scratch softer ones. Store each specimen in its own compartment, box, or wrapped in tissue/bubble wrap. Never pile specimens on top of each other — even quartz-on-quartz contact causes chips.",
    appliesTo: ["All specimens"],
  },
  {
    id: "store-climate",
    title: "Climate Control",
    category: "Storage",
    emoji: "🌡️",
    description: "Avoid extreme temperature changes and high humidity. Some minerals (pyrite, marcasite) decompose in humidity ('pyrite disease'). Opal can craze or crack if it dries out. Ideal: 40-50% humidity, room temperature.",
    appliesTo: ["Pyrite", "Marcasite", "Opal", "Halite", "Sulfur"],
    warning: "Pyrite disease is irreversible — store pyrite specimens with silica gel packets.",
  },
  {
    id: "store-light",
    title: "Light Protection",
    category: "Storage",
    emoji: "🕶️",
    description: "Some minerals fade or change color in sunlight — amethyst can pale, fluorite can lose color, realgar turns to pararealgar (powdery orange). Store light-sensitive minerals in opaque containers or dark cabinets.",
    appliesTo: ["Amethyst", "Fluorite", "Realgar", "Topaz", "Kunzite"],
  },
  {
    id: "display-stand",
    title: "Display Stands",
    category: "Display",
    emoji: "🏗️",
    description: "Use acrylic risers, mineral tack, or custom stands to display specimens at their best angle. Mineral tack (a reusable putty) holds specimens securely without scratching. Avoid metal stands that can scratch soft stones.",
    appliesTo: ["All display specimens"],
  },
  {
    id: "display-labels",
    title: "Label Everything",
    category: "Display",
    emoji: "🏷️",
    description: "A specimen without a locality label loses most of its scientific and monetary value. Label each specimen with: name, locality, date found, and your catalog number. Keep labels with the specimen — never separate them.",
    appliesTo: ["All specimens"],
  },
  {
    id: "handle-soft",
    title: "Handle Soft Minerals Carefully",
    category: "Handling",
    emoji: "🧤",
    description: "Minerals below Mohs 4 (talc, gypsum, calcite, fluorite) scratch easily. Handle with clean, dry hands or cotton gloves. Oils from skin can dull luster on some minerals over time.",
    appliesTo: ["Talc", "Gypsum", "Calcite", "Fluorite", "Apatite"],
  },
  {
    id: "handle-toxic",
    title: "Toxic Minerals Safety",
    category: "Handling",
    emoji: "☣️",
    description: "Some minerals are toxic or radioactive. Always wash hands after handling. Keep away from children and pets. Display in sealed cases.",
    appliesTo: ["Cinnabar (mercury)", "Galena (lead)", "Autunite (uranium)", "Arsenopyrite (arsenic)", "Orpiment (arsenic)"],
    warning: "Wash hands thoroughly after handling. Never eat or drink near toxic minerals.",
  },
];

const CATEGORY_ICONS: Record<string, typeof Droplets> = {
  Cleaning: Droplets,
  Storage: Box,
  Display: Sparkles,
  Handling: Shield,
};

export default function MineralCare() {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState<string>("ALL");

  const filtered = CARE_TIPS.filter((tip) => {
    if (category !== "ALL" && tip.category !== category) return false;
    if (search.trim()) {
      const q = search.toLowerCase();
      return (
        tip.title.toLowerCase().includes(q) ||
        tip.description.toLowerCase().includes(q) ||
        tip.appliesTo.some((a) => a.toLowerCase().includes(q))
      );
    }
    return true;
  });

  return (
    <ScreenScaffold title="Mineral Care Guide">
     <div className="space-y-5 px-4 pb-8">
      <p className="text-sm text-muted-foreground">
        {CARE_TIPS.length} tips for cleaning, storing, and displaying your collection
      </p>

      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search care tips..."
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
        {["ALL", "Cleaning", "Storage", "Display", "Handling"].map((cat) => (
          <button
            key={cat}
            onClick={() => setCategory(cat)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              category === cat
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {cat === "ALL" ? "All Tips" : cat}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
        {filtered.map((tip) => {
          const Icon = CATEGORY_ICONS[tip.category] ?? Droplets;
          return (
            <SculptedCard key={tip.id} accent="aqua" className="space-y-2 p-4">
              <div className="flex items-start gap-3">
                <div className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
                  style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}>
                  <Icon className="h-5 w-5" />
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="font-display text-sm font-bold text-foreground">{tip.title}</h3>
                  <TagChip accent={`hsl(${CITRINE_HEX})`}>{tip.category}</TagChip>
                </div>
              </div>
              <p className="text-xs leading-relaxed text-[hsl(var(--text-mid))]">{tip.description}</p>
              {tip.warning && (
                <SculptedCard accent="danger" className="flex items-start gap-2 p-2.5">
                  <AlertTriangle className="h-3.5 w-3.5 shrink-0 mt-0.5" style={{ color: "hsl(4 70% 55%)" }} />
                  <p className="text-xs" style={{ color: "hsl(4 70% 55%)" }}>
                    <span className="font-bold">Warning:</span> {tip.warning}
                  </p>
                </SculptedCard>
              )}
              <div>
                <p className="mb-1 text-xs font-bold text-foreground">Applies to</p>
                <div className="flex flex-wrap gap-1.5">
                  {tip.appliesTo.map((item) => (
                    <TagChip key={item} accent={`hsl(${AQUA_HEX})`}>{item}</TagChip>
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
