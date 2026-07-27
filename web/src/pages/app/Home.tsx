import { useNavigate } from "react-router-dom";
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
} from "lucide-react";

interface Tile {
  to: string;
  icon: typeof Camera;
  label: string;
  description: string;
  featured?: boolean;
  /** When set, clicking the tile opens this callback instead of navigating. */
  onClick?: () => void;
}

const TILES: Tile[] = [
  {
    to: "/app/identify",
    icon: Camera,
    label: "Identify a Rock",
    description:
      "Snap a photo and let AI identify your specimen from 900+ known rocks, minerals, gems, and fossils.",
    featured: true,
  },
  {
    to: "/app/specimens",
    icon: BookOpen,
    label: "Specimen Database",
    description: "Browse 900+ rocks, minerals, crystals, gems, and fossils.",
  },
  {
    to: "/app/map",
    icon: Map,
    label: "Maps & Dig Sites",
    description: "Find nearby collecting sites, mines, parks, and shops.",
  },
  {
    to: "/app/collection",
    icon: Gem,
    label: "My Collection",
    description: "Your identified and saved specimens.",
  },
  {
    to: "/app/wishlist",
    icon: Heart,
    label: "Wishlist",
    description: "Specimens you're hunting for next.",
  },
  {
    to: "",
    icon: Camera,
    label: "Field Camera",
    description: "Capture and save field photos to any destination.",
    onClick: () =>
      window.dispatchEvent(new CustomEvent("open-field-camera")),
  },
  {
    to: "/app/captures",
    icon: MapPin,
    label: "Field Captures",
    description: "Your field camera photos and capture cards.",
  },
  {
    to: "/app/saved-images",
    icon: ImageIcon,
    label: "My Saved Images",
    description: "Your saved photo gallery.",
  },
  {
    to: "/app/artifacts",
    icon: Bone,
    label: "Artifacts",
    description: "Arrowheads, hand axes, beads, and stone tools.",
  },
  {
    to: "/app/favorites",
    icon: MapPin,
    label: "Favorite Spots",
    description: "Bookmark dig sites, parks, and campgrounds.",
  },
  {
    to: "/app/journal",
    icon: ClipboardList,
    label: "Field Journal",
    description: "Log your field trips and finds.",
  },
  {
    to: "/app/trips",
    icon: Calendar,
    label: "Trip Planner",
    description: "Plan your next rockhounding adventure.",
  },
  {
    to: "/app/trade",
    icon: ArrowRightLeft,
    label: "Trade Board",
    description: "Swap, sell, and trade specimens.",
  },
  {
    to: "/app/community",
    icon: Users,
    label: "Community",
    description: "Share finds and connect with rockhounds.",
  },
  {
    to: "/app/friends",
    icon: Users,
    label: "Friends",
    description: "Connect and message fellow rockhounds.",
  },
  {
    to: "/app/aurora",
    icon: Sparkles,
    label: "Aurora Tracker",
    description: "Aurora forecasts and visibility maps.",
  },
  {
    to: "/app/stars",
    icon: Telescope,
    label: "Stars & Constellations",
    description: "Night sky guide for field trips.",
  },
  {
    to: "/app/gear",
    icon: Compass,
    label: "Gear Guide",
    description: "47 recommended tools and equipment.",
  },
  {
    to: "/app/gem-shows",
    icon: Calendar,
    label: "Gem & Mineral Shows",
    description: "281 upcoming shows across the US.",
  },
  {
    to: "/app/reference",
    icon: BookMarked,
    label: "Reference Library",
    description: "Guides, geology, paleontology, and more.",
  },
  {
    to: "/app/notifications",
    icon: Bell,
    label: "Notifications",
    description: "Your in-app notification feed.",
  },
  {
    to: "/app/referral",
    icon: Gift,
    label: "Refer a Friend",
    description: "Earn XP for every rockhound you bring in.",
  },
  {
    to: "/app/paywall",
    icon: Zap,
    label: "Go Premium",
    description: "Unlimited IDs, ad-free, pro features.",
  },
  {
    to: "/app/achievements",
    icon: Award,
    label: "Achievements",
    description: "Level up and earn badges.",
  },
];

export default function Home() {
  const navigate = useNavigate();

  return (
    <div className="space-y-6">
      <div className="fade-rise">
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Welcome back
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          What are you hunting for today?
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5">
        {TILES.map((tile, i) => (
          <button
            key={tile.label}
            onClick={() => {
              if (tile.onClick) tile.onClick();
              else navigate(tile.to);
            }}
            className={`group flex flex-col items-start gap-3 rounded-xl border border-border bg-card p-5 text-left transition-all hover:border-primary/40 hover:bg-card/80 ${
              tile.featured
                ? "citrine-glow sm:col-span-2 lg:col-span-3"
                : ""
            }`}
            style={{ animationDelay: `${i * 30}ms` }}
          >
            <div
              className={`flex h-11 w-11 items-center justify-center rounded-lg transition-colors ${
                tile.featured
                  ? "bg-primary/20 text-primary"
                  : "bg-muted text-muted-foreground group-hover:bg-primary/15 group-hover:text-primary"
              }`}
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
            {tile.featured && (
              <div className="mt-1 flex items-center gap-1.5 text-sm font-medium text-primary">
                <Star className="h-4 w-4" />
                Start here
              </div>
            )}
          </button>
        ))}
      </div>
    </div>
  );
}
