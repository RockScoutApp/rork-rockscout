import { useNavigate } from "react-router-dom";
import {
  BookOpen,
  Globe,
  Bone,
  Star,
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
import { SculptedCard, ScreenScaffold } from "@/components/sculpted";

interface RefTile {
  to: string;
  icon: typeof BookOpen;
  label: string;
  description: string;
  accent: string;
}

const TILES: RefTile[] = [
  { to: "/app/gear", icon: Compass, label: "Gear Guide", description: "77 recommended tools — loupes, hammers, UV lights, and more.", accent: "20 62% 55%" },
  { to: "/app/gem-shows", icon: Calendar, label: "Gem & Mineral Shows", description: "281 upcoming shows across the US with dates and venues.", accent: "4 70% 55%" },
  { to: "/app/map", icon: MapPin, label: "Dig Sites & Maps", description: "583 dig sites, mines, parks, and shops on an interactive map.", accent: "174 60% 45%" },
  { to: "/app/glossary", icon: BookOpen, label: "Glossary", description: "146 terms — mineralogy, geology, paleontology, lapidary & more.", accent: "190 60% 50%" },
  { to: "/app/mohs-scale", icon: FlaskConical, label: "Mohs Hardness Scale", description: "The 10-step mineral hardness reference with scratch tools.", accent: "36 80% 58%" },
  { to: "/app/crystal-systems", icon: Hexagon, label: "Crystal Systems", description: "The 7 crystal systems — how minerals are classified by structure.", accent: "265 47% 67%" },
  { to: "/app/geology", icon: Layers, label: "Geology Reference", description: "Rock types, the rock cycle, and how the Earth makes stone.", accent: "200 41% 61%" },
  { to: "/app/fluorescence", icon: Sun, label: "Fluorescence & UV", description: "12 fluorescent minerals, UV light guide, and safety notes.", accent: "265 47% 67%" },
  { to: "/app/mineral-care", icon: BookOpen, label: "Mineral Care", description: "12 tips for cleaning, storing, and displaying specimens.", accent: "147 49% 45%" },
  { to: "/app/paleontology", icon: Bone, label: "Paleontology", description: "8 geological periods, key organisms, and mass extinctions.", accent: "41 70% 58%" },
  { to: "/app/lapidary", icon: Compass, label: "Lapidary Basics", description: "8 guides for cutting, grinding, and polishing stones.", accent: "36 80% 58%" },
  { to: "/app/meteorite-hunting", icon: Globe, label: "Meteorite Hunting", description: "5 meteorite types, field ID tips, and hunting guide.", accent: "147 49% 55%" },
  { to: "/app/aurora", icon: Star, label: "Aurora Tracker", description: "KP index guide, viewing tips, and forecast links.", accent: "174 100% 45%" },
  { to: "/app/stars", icon: Telescope, label: "Stars & Constellations", description: "10 constellations & 10 bright stars with mythology.", accent: "220 30% 50%" },
  { to: "/app/severe-weather", icon: Zap, label: "Severe Weather Guide", description: "6 weather hazards, safety tips, and NOAA links.", accent: "4 70% 55%" },
  { to: "/app/achievements", icon: Award, label: "Achievements", description: "101 achievements to earn as you identify, collect, and explore.", accent: "36 80% 58%" },
  { to: "/app/paywall", icon: Zap, label: "Go Premium", description: "Unlimited AI IDs, ad-free, and pro features for $5.99/mo.", accent: "265 47% 67%" },
  { to: "/app/notifications", icon: Bell, label: "Notifications", description: "Your in-app notification feed and push settings.", accent: "210 70% 55%" },
  { to: "/app/referral", icon: Gift, label: "Refer a Friend", description: "Earn XP and rewards for every rockhound you bring in.", accent: "147 49% 55%" },
  { to: "/app/natural-wonders", icon: Globe, label: "Natural Wonders", description: "72 world-famous geological landmarks and their stories.", accent: "200 41% 61%" },
  { to: "/app/blm-guide", icon: MapPin, label: "BLM Guide", description: "Collecting rules, 10 sites, 61 trailheads, 55 campgrounds.", accent: "20 62% 55%" },
];

export default function ReferenceLibrary() {
  const navigate = useNavigate();

  return (
    <ScreenScaffold title="Reference Library">
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Guides, tools, and resources for rockhounds
        </p>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5">
          {TILES.map((tile, i) => (
            <SculptedCard
              key={tile.label}
              accent="aqua"
              interactive
              className="overflow-hidden"
              onClick={() => navigate(tile.to)}
            >
              <div
                className="flex flex-col items-start gap-3 p-5 fade-rise"
                style={{ animationDelay: `${i * 30}ms` }}
              >
                <div
                  className="icon-badge glowing-border flex h-11 w-11 items-center justify-center rounded-xl"
                  style={{
                    ["--badge-accent" as string]: tile.accent,
                    ["--glow-color" as string]: tile.accent,
                    color: `hsl(${tile.accent})`,
                  }}
                >
                  <tile.icon className="h-5 w-5" />
                </div>
                <div>
                  <h3 className="font-display text-sm font-bold text-foreground">
                    {tile.label}
                  </h3>
                  <p className="mt-1 text-xs text-[hsl(var(--text-mid))]">
                    {tile.description}
                  </p>
                </div>
              </div>
            </SculptedCard>
          ))}
        </div>
      </div>
    </ScreenScaffold>
  );
}
