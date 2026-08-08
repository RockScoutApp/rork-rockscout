import { useState, useRef, useEffect } from "react";
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
import { useQuery } from "@tanstack/react-query";
import { supabase } from "@/lib/supabase";
import { DashboardTile, SculptedCard, ProfileStatBar } from "@/components/sculpted";
import { UserAvatar } from "@/components/app/UserAvatar";

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

const R2_BASE = "https://r2-pub.rork.com/projects/jvns5dfy7fpytx79a2tb3/assets";

const FIELD_KIT_TILES: HomeTileData[] = [
  {
    label: "My Rocks",
    subtitle: "Your collected specimens",
    icon: iconForAccent("aqua", Package),
    accent: "aqua",
    to: "/app/collection",
    imageUrl: `${R2_BASE}/83b2bd7a-36bd-4a87-9872-e784d4a3872a.png`,
  },
  {
    label: "Wishlist",
    subtitle: "Specimens you're hunting for",
    icon: iconForAccent("amethyst", Heart),
    accent: "amethyst",
    to: "/app/wishlist",
    imageUrl: `${R2_BASE}/d3a5cdaf-cd76-4dfe-ab86-7e106532b9da.png`,
  },
  {
    label: "Field Captures",
    subtitle: "Rock photos logged in the field",
    icon: iconForAccent("success", ImageIcon),
    accent: "success",
    to: "/app/captures",
    imageUrl: `${R2_BASE}/f365ba1f-83ac-4e0c-b1ea-6933d51cf997.png`,
  },
  {
    label: "Field Camera",
    subtitle: "Snap a photo · Save anywhere",
    icon: iconForAccent("citrine", Camera),
    accent: "citrine",
    to: "",
    onClick: () =>
      window.dispatchEvent(new CustomEvent("open-field-camera")),
    imageUrl: `${R2_BASE}/3fcf5457-26b9-44ee-870b-ed47e10ed672.png`,
  },
  {
    label: "My Favorite Spots",
    subtitle: "Dig sites you've saved",
    icon: iconForAccent("danger", Heart),
    accent: "danger",
    to: "/app/favorites",
    imageUrl: `${R2_BASE}/6549b03b-f04f-4f66-a92c-1771c1cc3de3.png`,
  },
  {
    label: "Dig Sites & Rock Shops",
    subtitle: "Mines, shops & digs to visit",
    icon: iconForAccent("success", MapPin),
    accent: "success",
    to: "/app/map",
    imageUrl: `${R2_BASE}/95a9e35b-30d9-4522-82e2-1415c612dbc7.png`,
  },
  {
    label: "Specimen Database",
    subtitle: "Browse every specimen in the app",
    icon: iconForAccent("cyan", BookOpen),
    accent: "cyan",
    to: "/app/specimens",
    imageUrl: `${R2_BASE}/ce54d4e8-b66c-4431-a33a-9bcae71ff5a5.png`,
  },
  {
    label: "My Saved Images",
    subtitle: "Photos you've saved from the app",
    icon: <Download className="h-5 w-5" style={{ color: "hsl(200 67% 57%)" }} />,
    accent: "cyan",
    to: "/app/saved-images",
    imageUrl: `${R2_BASE}/6249a68c-94f4-4b90-82db-0434741d21af.png`,
  },
  {
    label: "Aurora Forecaster",
    subtitle: "Northern lights forecast & maps",
    icon: iconForAccent("cyan", Moon),
    accent: "cyan",
    to: "/app/aurora",
    imageUrl: `${R2_BASE}/030eff91-2010-41f3-bea6-3188baec1a38.png`,
  },
  {
    label: "Severe Weather",
    subtitle: "NWS alerts + storm chaser streams",
    icon: <AlertTriangle className="h-5 w-5" style={{ color: "hsl(20 100% 60%)" }} />,
    accent: "danger",
    to: "/app/weather",
    imageUrl: `${R2_BASE}/e2803cb6-56f0-4506-84cb-0a36be573f7e.png`,
  },
];

const FIELD_GUIDE_TILES: HomeTileData[] = [
  {
    label: "BLM Public Lands",
    subtitle: "State rules, dig sites & info",
    icon: <Mountain className="h-5 w-5" style={{ color: "hsl(22 55% 55%)" }} />,
    accent: "aqua",
    to: "/app/blm",
    imageUrl: `${R2_BASE}/7f95720c-5737-487c-975f-fa5b1dbedf0a.png`,
  },
  {
    label: "National / State Parks",
    subtitle: "Parks with geological interest",
    icon: <TreePine className="h-5 w-5" style={{ color: "hsl(110 30% 54%)" }} />,
    accent: "success",
    to: "/app/state-parks",
    imageUrl: `${R2_BASE}/6d05636d-7ae8-4886-9789-ae62aecd18a2.png`,
  },
  {
    label: "Campgrounds & Trailheads",
    subtitle: "Camp & hike near dig sites",
    icon: iconForAccent("citrine", Tent),
    accent: "citrine",
    to: "/app/campgrounds",
    imageUrl: `${R2_BASE}/3224f2e2-4cb7-463e-8ba6-f0563b917f8f.png`,
  },
  {
    label: "Finding Meteorites",
    subtitle: "How to hunt and identify space rocks",
    icon: <Globe className="h-5 w-5" style={{ color: "hsl(0 0% 75%)" }} />,
    accent: "cyan",
    to: "/app/meteorites",
    imageUrl: `${R2_BASE}/06428f02-9e5f-4953-9d47-45fa156b24cd.png`,
  },
  {
    label: "Rock & Gem Resources",
    subtitle: "Trusted geology, gem & fossil sites",
    icon: <Globe className="h-5 w-5" style={{ color: "hsl(210 70% 62%)" }} />,
    accent: "cyan",
    to: "/app/resources",
    imageUrl: `${R2_BASE}/e7703d3b-aee3-43a8-a6a9-378169a022d2.png`,
  },
  {
    label: "Glossary",
    subtitle: "Every rock & mineral term explained",
    icon: iconForAccent("cyan", BookMarked),
    accent: "cyan",
    to: "/app/glossary",
    imageUrl: `${R2_BASE}/ce15c050-ec63-4ef0-bfea-d66c63efd132.png`,
  },
];

const EXPLORE_TILES: HomeTileData[] = [
  {
    label: "Rocks Are Amazing",
    subtitle: "Earth's most stunning formations",
    icon: <BookOpen className="h-5 w-5" style={{ color: "hsl(200 67% 57%)" }} />,
    accent: "cyan",
    to: "/app/rocks-are-amazing",
    imageUrl: `${R2_BASE}/f876baa4-cb5e-4734-9a7e-ffdc9c356aa9.png`,
  },
  {
    label: "Exploring Geology",
    subtitle: "How rocks, minerals & gems form",
    icon: <BookOpenIcon className="h-5 w-5" style={{ color: "hsl(41 53% 64%)" }} />,
    accent: "aqua",
    to: "/app/geology",
    imageUrl: `${R2_BASE}/8bff65d0-0992-413a-adda-8580519d929b.png`,
  },
  {
    label: "Exploring Paleontology",
    subtitle: "Fossils, eras & deep-time history",
    icon: <Bone className="h-5 w-5" style={{ color: "hsl(33 38% 64%)" }} />,
    accent: "aqua",
    to: "/app/paleontology",
    imageUrl: `${R2_BASE}/43091c8c-862a-4f1d-b342-84ee86e12783.png`,
  },
  {
    label: "Prehistoric Organisms",
    subtitle: "Dinosaurs, birds, ancient flora",
    icon: <TreePine className="h-5 w-5" style={{ color: "hsl(110 33% 58%)" }} />,
    accent: "success",
    to: "/app/prehistoric",
    imageUrl: `${R2_BASE}/6bc8e37c-11c8-42ed-9250-dff8fbe2bfec.png`,
  },
  {
    label: "Dinosaur Dictionary",
    subtitle: "200+ dinosaurs & Ice Age animals",
    icon: <PawPrint className="h-5 w-5" style={{ color: "hsl(140 33% 58%)" }} />,
    accent: "success",
    to: "/app/dinosaurs",
    imageUrl: "/dino_images/tyrannosaurus.jpg",
  },
  {
    label: "Tectonics & Volcanoes",
    subtitle: "Plate movement & where rocks form",
    icon: <FlameIcon className="h-5 w-5" style={{ color: iconColor("danger") }} />,
    accent: "danger",
    to: "/app/tectonics",
    imageUrl: `${R2_BASE}/13d72c22-f574-47c4-a23c-a6a9ae6b65bb.png`,
  },
  {
    label: "Periodic Table",
    subtitle: "118 elements in rocks & gems",
    icon: <FlaskConical className="h-5 w-5" style={{ color: "hsl(210 70% 62%)" }} />,
    accent: "cyan",
    to: "/app/periodic-table",
    imageUrl: `${R2_BASE}/040be3bf-71ab-46d0-b6be-4598df22a18b.png`,
  },
  {
    label: "Mineral Care",
    subtitle: "Safe cleaning for every mineral",
    icon: iconForAccent("success", Brush),
    accent: "success",
    to: "/app/mineral-care",
    imageUrl: `${R2_BASE}/263c5833-c668-4d2a-a4b5-e45cb5148679.png`,
  },
  {
    label: "Fluorescence & UV",
    subtitle: "Which minerals glow under UV",
    icon: iconForAccent("amethyst", Lightbulb),
    accent: "amethyst",
    to: "/app/fluorescence",
    imageUrl: `${R2_BASE}/d28875c4-12ea-4ee1-8af2-fc1682be76d3.png`,
  },
  {
    label: "Crystal Systems",
    subtitle: "The 7 crystal shapes",
    icon: iconForAccent("cyan", Diamond),
    accent: "cyan",
    to: "/app/crystal-systems",
    imageUrl: `${R2_BASE}/ffe8b73c-b664-4533-945e-5fea690c0ecc.png`,
  },
  {
    label: "Lapidary Basics",
    subtitle: "Cut, polish & cab your finds",
    icon: iconForAccent("citrine", Construction),
    accent: "citrine",
    to: "/app/lapidary",
    imageUrl: `${R2_BASE}/4984334e-fd43-4402-907f-10d9b056b6d7.png`,
  },
  {
    label: "Artifacts",
    subtitle: "Arrowheads & stone tools",
    icon: <Bone className="h-5 w-5" style={{ color: "hsl(22 55% 42%)" }} />,
    accent: "aqua",
    to: "/app/artifacts",
    imageUrl: `${R2_BASE}/ca44cafb-2e4f-4d3b-9334-174ceedf713b.png`,
  },
  {
    label: "Natural Wonders",
    subtitle: "World-famous geological sites",
    icon: <Globe className="h-5 w-5" style={{ color: "hsl(200 50% 30%)" }} />,
    accent: "cyan",
    to: "/app/natural-wonders",
    imageUrl: `${R2_BASE}/5eafcf10-07b7-47ec-acd2-bd068858b712.png`,
  },
  {
    label: "Explore the Stars",
    subtitle: "88 constellations & planets",
    icon: iconForAccent("cyan", Star),
    accent: "cyan",
    to: "/app/stars",
    imageUrl: `${R2_BASE}/923312d6-4c0c-4855-8e00-827426991a2f.png`,
  },
];

export default function Home() {
  const navigate = useNavigate();
  const { isFree, isPremium, deviceOverLimit, rawIsPremium } = useTier();
  const { user } = useAuth();
  const [versionTaps, setVersionTaps] = useState(0);
  const versionTapTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const [justVerified, setJustVerified] = useState(false);

  // Fetch the user's display_name so the greeting shows their username, not email.
  const { data: profile } = useQuery<{ display_name: string; avatar_image_path?: string | null } | null>({
    queryKey: ["home-profile", user?.id],
    queryFn: async () => {
      if (!user) return null;
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("display_name, avatar_image_path")
        .eq("id", user.id)
        .maybeSingle();
      return (data as { display_name: string; avatar_image_path?: string | null } | null) ?? null;
    },
    enabled: !!user,
    staleTime: 60_000,
  });

  const displayName = profile?.display_name || user?.email?.split("@")[0] || "Rockhound";

  // Show a verification-success banner if the user arrived from a click-to-verify
  // email link. The InstallPWA page sets a sessionStorage flag before redirecting.
  useEffect(() => {
    try {
      const flag = sessionStorage.getItem("rockscout_just_verified");
      if (flag) {
        setJustVerified(true);
        sessionStorage.removeItem("rockscout_just_verified");
      }
    } catch {
      // sessionStorage unavailable
    }
  }, []);

  const handleTileClick = (tile: HomeTileData) => {
    if (tile.onClick) {
      tile.onClick();
    } else if (tile.to) {
      navigate(tile.to);
    }
  };

  // ── Version footer 5-tap easter egg → dev console ──
  const handleVersionTap = () => {
    const newCount = versionTaps + 1;
    setVersionTaps(newCount);
    if (versionTapTimer.current) clearTimeout(versionTapTimer.current);
    if (newCount >= 5) {
      setVersionTaps(0);
      navigate("/app/dev-console");
      return;
    }
    versionTapTimer.current = setTimeout(() => setVersionTaps(0), 1500);
  };

  return (
    <div className="space-y-8">
      {/* ── Email verification success banner ── */}
      {justVerified && (
        <div className="fade-rise flex items-center gap-3 rounded-xl border border-emerald-500/40 bg-emerald-500/10 px-4 py-3">
          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-emerald-500/15">
            <svg className="h-5 w-5 text-emerald-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M22 13.5L14 5.5L2 17.5" />
              <path d="M16 5.5H22V11.5" />
              <path d="M5 19L2 22" />
            </svg>
          </div>
          <div className="flex-1">
            <p className="font-display text-sm font-bold text-foreground">Email verified!</p>
            <p className="text-xs text-muted-foreground">Your RockScout account is now active. Welcome aboard!</p>
          </div>
          <button
            onClick={() => setJustVerified(false)}
            className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-muted/50 text-muted-foreground transition-colors hover:bg-muted"
            aria-label="Dismiss"
          >
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M18 6L6 18M6 6l12 12" />
            </svg>
          </button>
        </div>
      )}

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
        <div className="flex flex-1 items-center gap-2.5">
          {user && (
            <UserAvatar
              imagePath={profile?.avatar_image_path ?? null}
              displayName={displayName}
              size="sm"
              showName={false}
              onClick={() => navigate("/app/profile")}
            />
          )}
          <div>
            <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
              {user ? `Hello, ${displayName}` : "Welcome to RockScout"}
            </h1>
            <p className="mt-0.5 text-sm text-muted-foreground">
              What are you hunting for today?
            </p>
          </div>
        </div>
        <UserAvatar
          imagePath={profile?.avatar_image_path ?? null}
          displayName={displayName}
          size="sm"
          showName={false}
          onClick={() => navigate(user ? "/app/profile" : "/app/signin")}
        />
      </div>

      {/* ── Device limit banner ── */}
      {rawIsPremium && deviceOverLimit && (
        <SculptedCard accent="citrine" interactive className="p-4" onClick={() => navigate("/app/manage-devices")}>
          <div className="flex items-center gap-3">
            <AlertTriangle className="h-5 w-5 shrink-0 text-amber-500" />
            <div className="flex-1">
              <p className="text-sm font-semibold text-foreground">3-device limit reached</p>
              <p className="text-xs text-muted-foreground">Premium is paused on this device. Tap to manage your devices.</p>
            </div>
            <ChevronRight className="h-4 w-4 shrink-0 text-muted-foreground" />
          </div>
        </SculptedCard>
      )}

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

      {/* ── Version footer (5 taps → dev console easter egg) ── */}
      <div className="pt-4 text-center">
        <button
          onClick={handleVersionTap}
          className="text-xs text-muted-foreground transition-colors hover:text-foreground/70"
        >
          RockScout v1.1.6 · Built with passion for rockhounds
          {versionTaps > 0 && versionTaps < 5 && (
            <span className="ml-1 text-primary">{"·".repeat(versionTaps)}</span>
          )}
        </button>
      </div>
    </div>
  );
}
