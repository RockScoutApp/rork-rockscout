import { useNavigate } from "react-router-dom";
import {
  BookOpen,
  Globe,
  Mountain,
  Bone,
  Sparkles,
  Telescope,
  Compass,
  Zap,
  Calendar,
  MapPin,
  Award,
  Bell,
  Gift,
  Hexagon,
  FlaskConical,
  Layers,
  Sun,
} from "lucide-react";

interface RefTile {
  to: string;
  icon: typeof BookOpen;
  label: string;
  description: string;
  color: string;
}

const TILES: RefTile[] = [
  {
    to: "/app/gear",
    icon: Compass,
    label: "Gear Guide",
    description: "47 recommended tools — loupes, hammers, UV lights, and more.",
    color: "#C97B4A",
  },
  {
    to: "/app/gem-shows",
    icon: Calendar,
    label: "Gem & Mineral Shows",
    description: "281 upcoming shows across the US with dates and venues.",
    color: "#E74C3C",
  },
  {
    to: "/app/map",
    icon: MapPin,
    label: "Dig Sites & Maps",
    description: "583 dig sites, mines, parks, and shops on an interactive map.",
    color: "#5BB8B8",
  },
  {
    to: "/app/glossary",
    icon: BookOpen,
    label: "Glossary",
    description: "146 terms — mineralogy, geology, paleontology, lapidary & more.",
    color: "#5CC9E6",
  },
  {
    to: "/app/mohs-scale",
    icon: FlaskConical,
    label: "Mohs Hardness Scale",
    description: "The 10-step mineral hardness reference with scratch tools.",
    color: "#E8A33D",
  },
  {
    to: "/app/crystal-systems",
    icon: Hexagon,
    label: "Crystal Systems",
    description: "The 7 crystal systems — how minerals are classified by structure.",
    color: "#9B59B6",
  },
  {
    to: "/app/geology",
    icon: Layers,
    label: "Geology Reference",
    description: "Rock types, the rock cycle, and how the Earth makes stone.",
    color: "#6FA8C7",
  },
  {
    to: "/app/fluorescence",
    icon: Sun,
    label: "Fluorescence & UV",
    description: "12 fluorescent minerals, UV light guide, and safety notes.",
    color: "#9B59B6",
  },
  {
    to: "/app/mineral-care",
    icon: BookOpen,
    label: "Mineral Care",
    description: "12 tips for cleaning, storing, and displaying specimens.",
    color: "#5A8C5A",
  },
  {
    to: "/app/paleontology",
    icon: Bone,
    label: "Paleontology",
    description: "8 geological periods, key organisms, and mass extinctions.",
    color: "#E8C44A",
  },
  {
    to: "/app/lapidary",
    icon: Compass,
    label: "Lapidary Basics",
    description: "8 guides for cutting, grinding, and polishing stones.",
    color: "#E8A33D",
  },
  {
    to: "/app/meteorite-hunting",
    icon: Globe,
    label: "Meteorite Hunting",
    description: "5 meteorite types, field ID tips, and hunting guide.",
    color: "#5CC98C",
  },
  {
    to: "/app/aurora",
    icon: Sparkles,
    label: "Aurora Tracker",
    description: "KP index guide, viewing tips, and forecast links.",
    color: "#5CC9E6",
  },
  {
    to: "/app/stars",
    icon: Telescope,
    label: "Stars & Constellations",
    description: "10 constellations & 10 bright stars with mythology.",
    color: "#34495E",
  },
  {
    to: "/app/severe-weather",
    icon: Zap,
    label: "Severe Weather Guide",
    description: "6 weather hazards, safety tips, and NOAA links.",
    color: "#E74C3C",
  },
  {
    to: "/app/achievements",
    icon: Award,
    label: "Achievements",
    description: "101 badges to earn as you identify, collect, and explore.",
    color: "#F39C12",
  },
  {
    to: "/app/paywall",
    icon: Zap,
    label: "Go Premium",
    description: "Unlimited AI IDs, ad-free, and pro features for $5.99/mo.",
    color: "#9B59B6",
  },
  {
    to: "/app/notifications",
    icon: Bell,
    label: "Notifications",
    description: "Your in-app notification feed and push settings.",
    color: "#3498DB",
  },
  {
    to: "/app/referral",
    icon: Gift,
    label: "Refer a Friend",
    description: "Earn XP and rewards for every rockhound you bring in.",
    color: "#2ECC71",
  },
  {
    to: "/app/natural-wonders",
    icon: Globe,
    label: "Natural Wonders",
    description: "10 world-famous geological landmarks and their stories.",
    color: "#6FA8C7",
  },
  {
    to: "/app/blm-guide",
    icon: MapPin,
    label: "BLM Guide",
    description: "Collecting rules, 10 sites, 61 trailheads, 55 campgrounds.",
    color: "#C97B4A",
  },
];

export default function ReferenceLibrary() {
  const navigate = useNavigate();

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Reference Library
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Guides, tools, and resources for rockhounds
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {TILES.map((tile, i) => (
          <button
            key={tile.label}
            onClick={() => navigate(tile.to)}
            className="group flex flex-col items-start gap-3 rounded-xl border border-border bg-card p-5 text-left transition-all hover:border-primary/40 hover:bg-card/80"
            style={{ animationDelay: `${i * 30}ms` }}
          >
            <div
              className="flex h-11 w-11 items-center justify-center rounded-lg transition-colors"
              style={{ backgroundColor: `${tile.color}20`, color: tile.color }}
            >
              <tile.icon className="h-5 w-5" />
            </div>
            <div>
              <h3 className="font-display text-base font-semibold text-foreground">
                {tile.label}
              </h3>
              <p className="mt-1 text-sm text-muted-foreground">
                {tile.description}
              </p>
            </div>
          </button>
        ))}
      </div>


    </div>
  );
}
