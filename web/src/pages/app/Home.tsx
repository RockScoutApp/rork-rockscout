import { useNavigate, Link } from "react-router-dom";
import {
  Camera,
  BookOpen,
  Map,
  Gem,
  Heart,
  ClipboardList,
  Calendar,
  MapPin,
  ArrowRightLeft,
  Users,
  Sparkles,
  Award,
  Compass,
  Star,
  Telescope,
  BookMarked,
  Bell,
  Gift,
  Zap,
  Image as ImageIcon,
  Bone,
  Crown,
  Search,
  User,
  Flame,
  TrendingUp,
  Cloud,
  Sun,
  AlertTriangle,
  Package,
  Download,
  Moon,
  Diamond,
  Construction,
  Brush,
  Lightbulb,
  FlaskConical,
  Flame as FlameIcon,
  Trees,
  PawPrint,
  Globe,
  Mountain,
  TreePine,
  Tent,
  BookOpen as BookOpenIcon,
  ChevronRight,
} from "lucide-react";
import { useTier } from "@/hooks/useTier";
import { useAuth } from "@/hooks/useAuth";
import { DashboardTile, SculptedCard, ProfileStatBar } from "@/components/sculpted";

type Accent = "citrine" | "aqua" | "cyan" | "amethyst" | "danger" | "success";

interface HomeTileData {
  label: string;
  subtitle: string;
  icon: React.ReactNode;
  accent: Accent;
  to: string;
  imageUrl?: string;
  onClick?: () => void;
}

const AMETHYST_HEX = "265 47% 67%";
const SUCCESS_HEX = "147 49% 55%";
const DANGER_HEX = "4 70% 55%";
const CYAN_HEX = "174 100% 45%";
const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

const iconColor = (accent: Accent): string => {
  const map: Record<Accent, string> = {
    citrine: `hsl(${CITRINE_HEX})`,
    aqua: `hsl(${AQUA_HEX})`,
    cyan: `hsl(${CYAN_HEX})`,
    amethyst: `hsl(${AMETHYST_HEX})`,
    danger: `hsl(${DANGER_HEX})`,
    success: `hsl(${SUCCESS_HEX})`,
  };
  return map[accent];
};

const iconForAccent = (accent: Accent, Icon: typeof Camera) => (
  <Icon className="h-5 w-5" style={{ color: iconColor(accent) }} />
);

/* ── Tile data matching Android HomeScreen.kt ── */

const FIELD_KIT_TILES: HomeTileData[] = [
  {
    label: "My Rocks",
    subtitle: "Your collected specimens",
    icon: iconForAccent("aqua", Package),
    accent: "aqua",
    to: "/app/collection",
  },
  {
    label: "Wishlist",
    subtitle: "Specimens you're hunting for",
    icon: iconForAccent("amethyst", Heart),
    accent: "amethyst",
    to: "/app/wishlist",
  },
  {
    label: "Field Captures",
    subtitle: "Rock photos logged in the field",
    icon: iconForAccent("success", ImageIcon),
    accent: "success",
    to: "/app/captures",
  },
  {
    label: "Field Camera",
    subtitle: "Snap a photo · Save anywhere",
    icon: iconForAccent("citrine", Camera),
    accent: "citrine",
    to: "",
    onClick: () =>
      window.dispatchEvent(new CustomEvent("open-field-camera")),
  },
  {
    label: "My Favorite Spots",
    subtitle: "Dig sites you've saved",
    icon: iconForAccent("danger", Heart),
    accent: "danger",
    to: "/app/favorites",
  },
  {
    label: "Dig Sites & Rock Shops",
    subtitle: "Mines, shops & digs to visit",
    icon: iconForAccent("success", MapPin),
    accent: "success",
    to: "/app/map",
  },
  {
    label: "Specimen Database",
    subtitle: "Browse every specimen in the app",
    icon: iconForAccent("cyan", BookOpen),
    accent: "cyan",
    to: "/app/specimens",
  },
  {
    label: "My Saved Images",
    subtitle: "Photos you've saved from the app",
    icon: <Download className="h-5 w-5" style={{ color: "hsl(200 67% 57%)" }} />,
    accent: "cyan",
    to: "/app/saved-images",
  },
  {
    label: "Aurora Forecaster",
    subtitle: "Northern lights forecast & maps",
    icon: iconForAccent("cyan", Moon),
    accent: "cyan",
    to: "/app/aurora",
  },
  {
    label: "Severe Weather",
    subtitle: "NWS alerts + storm chaser streams",
    icon: <AlertTriangle className="h-5 w-5" style={{ color: "hsl(20 100% 60%)" }} />,
    accent: "danger",
    to: "/app/weather",
  },
];

const FIELD_GUIDE_TILES: HomeTileData[] = [
  {
    label: "BLM Public Lands",
    subtitle: "State rules, dig sites & info",
    icon: <Mountain className="h-5 w-5" style={{ color: "hsl(22 55% 55%)" }} />,
    accent: "aqua",
    to: "/app/blm",
  },
  {
    label: "National / State Parks",
    subtitle: "Parks with geological interest",
    icon: <TreePine className="h-5 w-5" style={{ color: "hsl(110 30% 54%)" }} />,
    accent: "success",
    to: "/app/state-parks",
  },
  {
    label: "Campgrounds & Trailheads",
    subtitle: "Camp & hike near dig sites",
    icon: iconForAccent("citrine", Tent),
    accent: "citrine",
    to: "/app/campgrounds",
  },
  {
    label: "Finding Meteorites",
    subtitle: "How to hunt and identify space rocks",
    icon: <Globe className="h-5 w-5" style={{ color: "hsl(0 0% 75%)" }} />,
    accent: "cyan",
    to: "/app/meteorites",
  },
  {
    label: "Rock & Gem Resources",
    subtitle: "Trusted geology, gem & fossil sites",
    icon: <Globe className="h-5 w-5" style={{ color: "hsl(210 70% 62%)" }} />,
    accent: "cyan",
    to: "/app/resources",
  },
  {
    label: "Glossary",
    subtitle: "Every rock & mineral term explained",
    icon: iconForAccent("cyan", BookMarked),
    accent: "cyan",
    to: "/app/glossary",
  },
];

const EXPLORE_TILES: HomeTileData[] = [
  {
    label: "Rocks Are Amazing",
    subtitle: "Earth's most stunning formations",
    icon: <BookOpen className="h-5 w-5" style={{ color: "hsl(200 67% 57%)" }} />,
    accent: "cyan",
    to: "/app/rocks-are-amazing",
  },
  {
    label: "Exploring Geology",
    subtitle: "How rocks, minerals & gems form",
    icon: <BookOpenIcon className="h-5 w-5" style={{ color: "hsl(41 53% 64%)" }} />,
    accent: "aqua",
    to: "/app/geology",
  },
  {
    label: "Exploring Paleontology",
    subtitle: "Fossils, eras & deep-time history",
    icon: <Bone className="h-5 w-5" style={{ color: "hsl(33 38% 64%)" }} />,
    accent: "aqua",
    to: "/app/paleontology",
  },
  {
    label: "Prehistoric Organisms",
    subtitle: "Dinosaurs, birds, ancient flora",
    icon: <TreePine className="h-5 w-5" style={{ color: "hsl(110 33% 58%)" }} />,
    accent: "success",
    to: "/app/prehistoric",
  },
  {
    label: "Dinosaur Dictionary",
    subtitle: "200+ dinosaurs & Ice Age animals",
    icon: <PawPrint className="h-5 w-5" style={{ color: "hsl(140 33% 58%)" }} />,
    accent: "success",
    to: "/app/dinosaurs",
  },
  {
    label: "Tectonics & Volcanoes",
    subtitle: "Plate movement & where rocks form",
    icon: <FlameIcon className="h-5 w-5" style={{ color: iconColor("danger") }} />,
    accent: "danger",
    to: "/app/tectonics",
  },
  {
    label: "Periodic Table",
    subtitle: "118 elements in rocks & gems",
    icon: <FlaskConical className="h-5 w-5" style={{ color: "hsl(210 70% 62%)" }} />,
    accent: "cyan",
    to: "/app/periodic-table",
  },
  {
    label: "Mineral Care",
    subtitle: "Safe cleaning for every mineral",
    icon: iconForAccent("success", Brush),
    accent: "success",
    to: "/app/mineral-care",
  },
  {
    label: "Fluorescence & UV",
    subtitle: "Which minerals glow under UV",
    icon: iconForAccent("amethyst", Lightbulb),
    accent: "amethyst",
    to: "/app/fluorescence",
  },
  {
    label: "Crystal Systems",
    subtitle: "The 7 crystal shapes",
    icon: iconForAccent("cyan", Diamond),
    accent: "cyan",
    to: "/app/crystal-systems",
  },
  {
    label: "Lapidary Basics",
    subtitle: "Cut, polish & cab your finds",
    icon: iconForAccent("citrine", Construction),
    accent: "citrine",
    to: "/app/lapidary",
  },
  {
    label: "Artifacts",
    subtitle: "Arrowheads & stone tools",
    icon: <Bone className="h-5 w-5" style={{ color: "hsl(22 55% 42%)" }} />,
    accent: "aqua",
    to: "/app/artifacts",
  },
  {
    label: "Natural Wonders",
    subtitle: "World-famous geological sites",
    icon: <Globe className="h-5 w-5" style={{ color: "hsl(200 50% 30%)" }} />,
    accent: "cyan",
    to: "/app/natural-wonders",
  },
  {
    label: "Explore the Stars",
    subtitle: "88 constellations & planets",
    icon: iconForAccent("cyan", Star),
    accent: "cyan",
    to: "/app/stars",
  },
];

/* Routes hidden for free users */
const PREMIUM_ONLY_ROUTES = new Set([
  "/app/identify",
  "/app/journal",
  "/app/trips",
  "/app/trade",
  "/app/community",
  "/app/friends",
  "/app/notifications",
  "/app/achievements",
  "/app/referral",
]);

export default function Home() {
  const navigate = useNavigate();
  const { isFree, isPremium } = useTier();
  const { user } = useAuth();

  const handleTileClick = (tile: HomeTileData) => {
    if (tile.onClick) {
      tile.onClick();
    } else if (tile.to) {
      navigate(tile.to);
    }
  };

  return (
    <div className="space-y-8">
      {/* ── Header bar ── */}
      <div className="fade-rise flex items-center gap-3">
        <button
          onClick={() => navigate("/app/search")}
          className="sculpted-button sculpted-raised dark-card flex h-11 w-11 items-center justify-center rounded-xl"
          style={{ ["--sculpted-accent" as string]: CITRINE_HEX }}
          aria-label="Search"
        >
          <Search className="h-5 w-5" style={{ color: `hsl(${CITRINE_HEX})` }} />
        </button>
        <div className="flex-1">
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            {user ? `Hello, ${user.email?.split("@")[0] ?? "Rockhound"}` : "Welcome to RockScout"}
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            What are you hunting for today?
          </p>
        </div>
        <button
          onClick={() => navigate(user ? "/app/profile" : "/app/signin")}
          className="sculpted-button sculpted-raised dark-card flex h-11 w-11 items-center justify-center rounded-xl"
          style={{ ["--sculpted-accent" as string]: AQUA_HEX }}
          aria-label="Profile"
        >
          <User className="h-5 w-5" style={{ color: `hsl(${AQUA_HEX})` }} />
        </button>
      </div>

      {/* ── Identify hero card (premium only) ── */}
      {isPremium && (
        <SculptedCard
          accent="citrine"
          glowing
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/identify")}
        >
          <div className="relative flex items-center gap-4 p-5 md:p-6">
            <div
              className="absolute inset-0 opacity-20"
              style={{
                background: `radial-gradient(ellipse at 20% 50%, hsl(${CITRINE_HEX}), transparent 60%)`,
              }}
            />
            <div className="relative flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl"
              style={{
                background: `radial-gradient(circle, hsl(${CITRINE_HEX} / 0.3), hsl(${CITRINE_HEX} / 0.05))`,
                boxShadow: `0 0 12px hsl(${CITRINE_HEX} / 0.3)`,
              }}
            >
              <Camera className="h-8 w-8" style={{ color: `hsl(${CITRINE_HEX})` }} />
            </div>
            <div className="relative flex-1">
              <div className="flex items-center gap-2">
                <h2 className="font-display text-xl font-bold text-foreground">
                  Identify a Rock
                </h2>
                <Star className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
              </div>
              <p className="mt-1 text-sm text-muted-foreground">
                Snap a photo and let AI identify your specimen from 900+ known rocks, minerals, gems, and fossils.
              </p>
            </div>
            <ChevronRight className="relative h-6 w-6 text-muted-foreground" />
          </div>
        </SculptedCard>
      )}

      {/* ── Go Premium banner for free users ── */}
      {isFree && (
        <Link
          to="/app/paywall"
          className="sculpted-raised dark-card flex items-center gap-4 p-4 transition-all hover:scale-[1.01]"
          style={{ ["--sculpted-accent" as string]: CITRINE_HEX }}
        >
          <div
            className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl"
            style={{
              background: `hsl(${CITRINE_HEX} / 0.15)`,
              boxShadow: `inset 0 0 8px hsl(${CITRINE_HEX} / 0.2)`,
            }}
          >
            <Crown className="h-6 w-6" style={{ color: `hsl(${CITRINE_HEX})` }} />
          </div>
          <div className="flex-1">
            <h3 className="font-display text-base font-semibold text-foreground">
              Go Premium
            </h3>
            <p className="text-sm text-muted-foreground">
              Unlock AI identification, social, trade, and more.
            </p>
          </div>
          <Zap className="h-5 w-5" style={{ color: `hsl(${CITRINE_HEX})` }} />
        </Link>
      )}

      {/* ── Full-width banner tiles ── */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
        {/* Gear Guide */}
        <SculptedCard
          accent="citrine"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/gear")}
        >
          <div className="flex items-center gap-3 p-4">
            <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <Compass className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
                Gear Guide
              </h3>
              <p className="text-xs text-muted-foreground">47 recommended tools & equipment</p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        {/* Gem & Mineral Shows */}
        <SculptedCard
          accent="citrine"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/gem-shows")}
        >
          <div className="flex items-center gap-3 p-4">
            <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <Calendar className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
                Gem & Mineral Shows
              </h3>
              <p className="text-xs text-muted-foreground">281 upcoming shows across the US</p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        {/* Reference Library */}
        <SculptedCard
          accent="citrine"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/reference")}
        >
          <div className="flex items-center gap-3 p-4">
            <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <BookMarked className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
                Reference Library
              </h3>
              <p className="text-xs text-muted-foreground">Guides, geology, paleontology & more</p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        {/* Premium-only banner tiles */}
        {isPremium && (
          <>
            <SculptedCard
              accent="citrine"
              interactive
              className="overflow-hidden"
              onClick={() => navigate("/app/trade")}
            >
              <div className="flex items-center gap-3 p-4">
                <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
                  style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
                >
                  <ArrowRightLeft className="h-5 w-5" />
                </div>
                <div className="flex-1">
                  <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
                    Trade Board
                  </h3>
                  <p className="text-xs text-muted-foreground">Swap, sell & trade specimens</p>
                </div>
                <ChevronRight className="h-5 w-5 text-muted-foreground" />
              </div>
            </SculptedCard>

            <SculptedCard
              accent="citrine"
              interactive
              className="overflow-hidden"
              onClick={() => navigate("/app/community")}
            >
              <div className="flex items-center gap-3 p-4">
                <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
                  style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
                >
                  <Users className="h-5 w-5" />
                </div>
                <div className="flex-1">
                  <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
                    Community
                  </h3>
                  <p className="text-xs text-muted-foreground">Share finds & connect</p>
                </div>
                <ChevronRight className="h-5 w-5 text-muted-foreground" />
              </div>
            </SculptedCard>

            <SculptedCard
              accent="citrine"
              interactive
              className="overflow-hidden"
              onClick={() => navigate("/app/journal")}
            >
              <div className="flex items-center gap-3 p-4">
                <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
                  style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
                >
                  <ClipboardList className="h-5 w-5" />
                </div>
                <div className="flex-1">
                  <h3 className="font-display text-sm font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
                    Trip Planner & Journal
                  </h3>
                  <p className="text-xs text-muted-foreground">Plan hunts & log field days</p>
                </div>
                <ChevronRight className="h-5 w-5 text-muted-foreground" />
              </div>
            </SculptedCard>
          </>
        )}
      </div>

      {/* ── "Your field kit" section ── */}
      <div className="space-y-3">
        <h2 className="font-display text-lg font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
          Your field kit
        </h2>
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
          {FIELD_KIT_TILES.map((tile) => (
            <DashboardTile
              key={tile.label}
              label={tile.label}
              subtitle={tile.subtitle}
              icon={tile.icon}
              accent={tile.accent}
              imageUrl={tile.imageUrl}
              onClick={() => handleTileClick(tile)}
            />
          ))}
        </div>
      </div>

      {/* ── Field guides section ── */}
      <div className="space-y-3">
        <h2 className="font-display text-lg font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
          Field guides
        </h2>
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6">
          {FIELD_GUIDE_TILES.map((tile) => (
            <DashboardTile
              key={tile.label}
              label={tile.label}
              subtitle={tile.subtitle}
              icon={tile.icon}
              accent={tile.accent}
              imageUrl={tile.imageUrl}
              onClick={() => handleTileClick(tile)}
            />
          ))}
        </div>
      </div>

      {/* ── "Explore & learn" section ── */}
      <div className="space-y-3">
        <h2 className="font-display text-lg font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
          Explore & learn
        </h2>
        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-7">
          {EXPLORE_TILES.map((tile) => (
            <DashboardTile
              key={tile.label}
              label={tile.label}
              subtitle={tile.subtitle}
              icon={tile.icon}
              accent={tile.accent}
              imageUrl={tile.imageUrl}
              onClick={() => handleTileClick(tile)}
            />
          ))}
        </div>
      </div>

      {/* ── Mohs hardness scale widget ── */}
      <SculptedCard accent="cyan" className="p-5">
        <div className="mb-4 flex items-center gap-3">
          <div className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
            style={{ ["--badge-accent" as string]: CYAN_HEX, color: `hsl(${CYAN_HEX})` }}
          >
            <Diamond className="h-5 w-5" />
          </div>
          <div>
            <h3 className="font-display text-base font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
              Mohs Hardness Scale
            </h3>
            <p className="text-xs text-muted-foreground">Reference for field testing</p>
          </div>
        </div>
        <div className="flex flex-wrap gap-2">
          {[
            { n: 1, name: "Talc", c: "150 20% 80%" },
            { n: 2, name: "Gypsum", c: "150 25% 72%" },
            { n: 3, name: "Calcite", c: "120 30% 65%" },
            { n: 4, name: "Fluorite", c: "200 40% 62%" },
            { n: 5, name: "Apatite", c: "190 45% 55%" },
            { n: 6, name: "Orthoclase", c: "170 40% 50%" },
            { n: 7, name: "Quartz", c: "140 35% 45%" },
            { n: 8, name: "Topaz", c: "120 35% 40%" },
            { n: 9, name: "Corundum", c: "100 30% 35%" },
            { n: 10, name: "Diamond", c: "80 25% 30%" },
          ].map((m) => (
            <div
              key={m.n}
              className="flex items-center gap-2 rounded-lg border border-border px-3 py-1.5"
              style={{ backgroundColor: `hsl(${m.c} / 0.12)` }}
            >
              <span
                className="flex h-6 w-6 items-center justify-center rounded-md text-xs font-bold"
                style={{ backgroundColor: `hsl(${m.c} / 0.3)`, color: `hsl(${m.c})` }}
              >
                {m.n}
              </span>
              <span className="text-xs font-medium text-foreground">{m.name}</span>
            </div>
          ))}
        </div>
      </SculptedCard>

      {/* ── Version footer ── */}
      <div className="pt-4 text-center">
        <p className="text-xs text-muted-foreground">
          RockScout v1.1.6 · Built with passion for rockhounds
        </p>
      </div>
    </div>
  );
}
