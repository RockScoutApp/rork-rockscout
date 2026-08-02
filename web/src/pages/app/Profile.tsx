import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Gem,
  Award,
  Settings,
  LogOut,
  Edit3,
  Bell,
  Mail,
  MapPin,
  Package,
  Heart,
  Flame,
  Crown,
  Zap,
  Users,
  Smartphone,
  Download,
  Shield,
  ChevronRight,
  Star,
  Sparkles,
  Gift,
  Check,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import {
  SculptedCard,
  SculptedButton,
  SculptedIconButton,
  StatTile,
  ProfileStatBar,
} from "@/components/sculpted";
import { supabase } from "@/lib/supabase";
import { toast } from "sonner";

/* ── Constants ── */
const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const CYAN_HEX = "174 100% 45%";
const AMETHYST_HEX = "265 47% 67%";
const DANGER_HEX = "4 70% 55%";
const SUCCESS_HEX = "147 49% 55%";

const AVATAR_OPTIONS = [
  "🦠", "⛏️", "💎", "🪨", "🔮", "🌋", "🧗", "👨‍🔬", "🌍",
  "⛰️", "🏔️", "💠", "🔬", "⚒️", "🧭", "🗺️", "🏜️", "🦴",
  "🐚", "🦈", "🦕", "🦖", "🐌", "🦋", "🐟", "🦞", "🐉",
  "🦎", "☄️", "✨", "⭐", "🔥", "❄️", "🧊", "🗿",
];

const HUNTER_STATUS_OPTIONS = [
  { value: "off", label: "Invisible", icon: "👻" },
  { value: "hunting", label: "Out Hunting", icon: "⛏️" },
  { value: "field", label: "In the Field", icon: "🧭" },
  { value: "show", label: "At a Show", icon: "🎪" },
  { value: "home", label: "Sorting Specimens", icon: "🏠" },
];

interface ProfileData {
  id: string;
  display_name: string;
  avatar_emoji: string;
  status: string;
  level: number;
  xp: number;
  is_pro: boolean;
  pro_badge: boolean;
  tokens: number;
  bio?: string;
  home_region?: string;
}

/** XP required to reach the next level from a given level. */
function xpForLevel(level: number): number {
  return 100 + (level - 1) * 50;
}

/** Tier name for a given level — matching Android LevelTier. */
function tierName(level: number): string {
  if (level >= 50) return "Grandmaster Hunter";
  if (level >= 40) return "Master Hunter";
  if (level >= 30) return "Expert Hunter";
  if (level >= 20) return "Senior Hunter";
  if (level >= 10) return "Apprentice Hunter";
  return "Rookie Hunter";
}

export default function Profile() {
  const { user, signOut } = useAuth();
  const { isPremium } = useTier();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [showEditSheet, setShowEditSheet] = useState(false);
  const [editName, setEditName] = useState("");
  const [editEmoji, setEditEmoji] = useState("💎");
  const [editBio, setEditBio] = useState("");
  const [editRegion, setEditRegion] = useState("");
  const [editStatus, setEditStatus] = useState("off");

  // ── Profile data ──
  const { data: profile } = useQuery<ProfileData>({
    queryKey: ["profile", user?.id],
    queryFn: async () => {
      if (!user) return null;
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, status, level, xp, is_pro, pro_badge, tokens")
        .eq("id", user.id)
        .maybeSingle();
      return (data as ProfileData) ?? null;
    },
    enabled: !!user,
  });

  // ── Collection count ──
  const { data: collectionCount = 0 } = useQuery<number>({
    queryKey: ["collection-count", user?.id],
    queryFn: async () => {
      if (!user) return 0;
      const { count } = await supabase
        .from("rockscout_collection")
        .select("*", { count: "exact", head: true })
        .eq("user_id", user.id);
      return count ?? 0;
    },
    enabled: !!user,
  });

  // ── Wishlist count ──
  const { data: wishlistCount = 0 } = useQuery<number>({
    queryKey: ["wishlist-count", user?.id],
    queryFn: async () => {
      if (!user) return 0;
      const { count } = await supabase
        .from("rockscout_wishlist")
        .select("*", { count: "exact", head: true })
        .eq("user_id", user.id);
      return count ?? 0;
    },
    enabled: !!user,
  });

  // ── Favorite spots count ──
  const { data: spotsCount = 0 } = useQuery<number>({
    queryKey: ["spots-count", user?.id],
    queryFn: async () => {
      if (!user) return 0;
      const { count } = await supabase
        .from("rockscout_favorite_spots")
        .select("*", { count: "exact", head: true })
        .eq("user_id", user.id);
      return count ?? 0;
    },
    enabled: !!user,
  });

  // ── Connections count ──
  const { data: friendsCount = 0 } = useQuery<number>({
    queryKey: ["friends-count", user?.id],
    queryFn: async () => {
      if (!user) return 0;
      const { count } = await supabase
        .from("rockscout_connections")
        .select("*", { count: "exact", head: true })
        .or(`user_a.eq.${user.id},user_b.eq.${user.id}`);
      return count ?? 0;
    },
    enabled: !!user,
  });

  // ── Sync edit form when profile loads ──
  useEffect(() => {
    if (profile) {
      setEditName(profile.display_name || "");
      setEditEmoji(profile.avatar_emoji || "💎");
      setEditStatus(profile.status || "off");
    }
  }, [profile]);

  // ── Save profile edits ──
  const saveProfile = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Not signed in");
      const { error } = await supabase
        .from("rockscout_profiles")
        .update({
          display_name: (editName.trim() || user.email?.split("@")[0]) ?? "Rockhound",
          avatar_emoji: editEmoji,
          status: editStatus,
        })
        .eq("id", user.id);
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["profile", user?.id] });
      setShowEditSheet(false);
      toast.success("Profile updated");
    },
    onError: (err: Error) => toast.error(err.message || "Failed to save"),
  });

  if (!user) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center gap-4">
        <Gem className="h-12 w-12 text-primary" />
        <p className="text-muted-foreground">Sign in to view your profile</p>
        <SculptedButton accent="citrine" onClick={() => navigate("/app/signin")}>
          Sign In
        </SculptedButton>
      </div>
    );
  }

  const level = profile?.level ?? 1;
  const xp = profile?.xp ?? 0;
  const currentLevelXp = xpForLevel(level);
  const tier = tierName(level);
  const statusLabel = HUNTER_STATUS_OPTIONS.find((s) => s.value === (profile?.status ?? "off"));

  const handleSignOut = async () => {
    await signOut();
    navigate("/app");
  };

  return (
    <div className="space-y-6">
      {/* ── Header ── */}
      <div className="fade-rise flex items-center gap-3">
        <h1 className="flex-1 font-display text-2xl font-bold text-foreground md:text-3xl">
          Profile
        </h1>
        <SculptedIconButton
          accent="aqua"
          size="sm"
          onClick={() => setShowEditSheet(true)}
          aria-label="Edit profile"
        >
          <Edit3 className="h-4 w-4" />
        </SculptedIconButton>
      </div>

      {/* ── Profile header card ── */}
      <SculptedCard
        accent="citrine"
        glowing
        className="overflow-hidden"
      >
        {/* Background gradient band */}
        <div
          className="relative flex h-32 items-center justify-center overflow-hidden"
          style={{
            background: `linear-gradient(180deg, hsl(${CITRINE_HEX} / 0.25), hsl(${AQUA_HEX} / 0.15), hsl(30 10% 9%))`,
          }}
        >
          <Sparkles
            className="absolute left-4 top-4 h-5 w-5"
            style={{ color: `hsl(${CITRINE_HEX} / 0.4)` }}
          />
          <Star
            className="absolute bottom-4 right-4 h-4 w-4"
            style={{ color: `hsl(${AQUA_HEX} / 0.3)` }}
          />
          <span className="text-5xl" role="img" aria-label="avatar">
            {profile?.avatar_emoji ?? "⛏️"}
          </span>
        </div>

        {/* Bottom content zone */}
        <div className="p-5">
          {/* Top row: avatar circle, badges, social settings */}
          <div className="flex items-center gap-3">
            {/* Avatar circle */}
            <div
              className="glowing-border flex h-16 w-16 shrink-0 items-center justify-center rounded-full text-3xl"
              style={{
                background: `radial-gradient(circle, hsl(${CITRINE_HEX} / 0.3), hsl(${AQUA_HEX} / 0.1))`,
                ["--glow-color" as string]: CITRINE_HEX,
              }}
            >
              {profile?.avatar_emoji ?? "💎"}
            </div>

            {/* Notification & message badges */}
            <button
              onClick={() => navigate("/app/notifications")}
              className="sculpted-button sculpted-raised dark-card relative flex h-11 w-11 items-center justify-center rounded-xl"
              style={{ ["--sculpted-accent" as string]: AQUA_HEX }}
              aria-label="Notifications"
            >
              <Bell className="h-5 w-5" style={{ color: `hsl(${AQUA_HEX})` }} />
            </button>
            <button
              onClick={() => navigate("/app/friends")}
              className="sculpted-button sculpted-raised dark-card relative flex h-11 w-11 items-center justify-center rounded-xl"
              style={{ ["--sculpted-accent" as string]: AQUA_HEX }}
              aria-label="Messages"
            >
              <Mail className="h-5 w-5" style={{ color: `hsl(${AQUA_HEX})` }} />
            </button>

            <div className="flex-1" />

            {/* Social Settings pill */}
            <button
              onClick={() => navigate("/app/social-settings")}
              className="sculpted-button flex items-center gap-1.5 rounded-full px-3 py-2 text-xs font-bold"
              style={{
                background: `linear-gradient(90deg, hsl(${CYAN_HEX}), hsl(172 100% 36%))`,
                color: `hsl(30 30% 9%)`,
              }}
            >
              <Settings className="h-3.5 w-3.5" />
              <span className="hidden sm:inline">Social Settings</span>
              <span className="sm:hidden">Social</span>
            </button>
          </div>

          {/* Name + ProBadge */}
          <div className="mt-4 flex items-center gap-2">
            <h2 className="font-display text-xl font-bold text-foreground">
              {profile?.display_name || user.email?.split("@")[0] || "Rockhound"}
            </h2>
            {isPremium && (
              <span
                className="glowing-border inline-flex items-center gap-1 rounded-full px-2 py-0.5 text-xs font-bold"
                style={{
                  background: `hsl(${CITRINE_HEX} / 0.15)`,
                  color: `hsl(${CITRINE_HEX})`,
                  ["--glow-color" as string]: CITRINE_HEX,
                }}
              >
                <Crown className="h-3 w-3" />
                PRO
              </span>
            )}
          </div>

          {/* Status badge */}
          {statusLabel && (
            <div className="mt-2 flex items-center gap-1.5">
              <span className="text-sm">{statusLabel.icon}</span>
              <span className="text-sm font-medium text-[hsl(var(--text-mid))]">
                {statusLabel.label}
              </span>
            </div>
          )}

          {/* Location */}
          <div className="mt-2 flex items-center gap-1.5">
            <MapPin className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
            <span className="text-sm font-medium" style={{ color: `hsl(${CITRINE_HEX})` }}>
              {profile?.home_region || "Location not set"}
            </span>
          </div>

          {/* Email */}
          <p className="mt-1 flex items-center gap-1.5 text-xs text-muted-foreground">
            <Mail className="h-3.5 w-3.5" />
            {user.email}
          </p>
        </div>
      </SculptedCard>

      {/* ── Level / XP / streak card ── */}
      <SculptedCard accent="citrine" className="p-5">
        <div className="mb-4 flex items-center gap-3">
          <div
            className="icon-badge glowing-border flex h-12 w-12 items-center justify-center rounded-xl"
            style={{
              ["--badge-accent" as string]: CITRINE_HEX,
              ["--glow-color" as string]: CITRINE_HEX,
              color: `hsl(${CITRINE_HEX})`,
            }}
          >
            <span className="text-lg font-bold">{level}</span>
          </div>
          <div className="flex-1">
            <h3 className="font-display text-base font-bold text-foreground">
              Level {level} · {tier}
            </h3>
            <p className="text-xs text-muted-foreground">
              {xp} total XP · {currentLevelXp - (xp % currentLevelXp)} XP to next level
            </p>
          </div>
          <Flame className="h-5 w-5" style={{ color: `hsl(${DANGER_HEX})` }} />
        </div>
        <ProfileStatBar
          current={xp % currentLevelXp}
          max={currentLevelXp}
          accent="citrine"
          label="Level progress"
        />
        <div className="mt-4 flex gap-3">
          <button
            onClick={() => navigate("/app/achievements")}
            className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-2 rounded-xl px-3 py-2.5 text-sm font-semibold"
            style={{ ["--sculpted-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
          >
            <Award className="h-4 w-4" />
            Achievements
          </button>
          <button
            onClick={() => navigate("/app/all-achievements")}
            className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-2 rounded-xl px-3 py-2.5 text-sm font-semibold"
            style={{ ["--sculpted-accent" as string]: AMETHYST_HEX, color: `hsl(${AMETHYST_HEX})` }}
          >
            <Star className="h-4 w-4" />
            Badges
          </button>
        </div>
      </SculptedCard>

      {/* ── Stat tiles ── */}
      <div className="grid grid-cols-3 gap-3">
        <StatTile
          label="Collected"
          value={collectionCount}
          accent="aqua"
          icon={<Package className="h-4 w-4" />}
        />
        <StatTile
          label="Wishlist"
          value={wishlistCount}
          accent="amethyst"
          icon={<Heart className="h-4 w-4" />}
        />
        <StatTile
          label="Spots"
          value={spotsCount}
          accent="danger"
          icon={<MapPin className="h-4 w-4" />}
        />
      </div>

      {/* ── Tokens (if any) ── */}
      {(profile?.tokens ?? 0) > 0 && (
        <SculptedCard accent="cyan" className="flex items-center gap-4 p-4">
          <div
            className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
            style={{ ["--badge-accent" as string]: CYAN_HEX, color: `hsl(${CYAN_HEX})` }}
          >
            <Zap className="h-5 w-5" />
          </div>
          <div className="flex-1">
            <h3 className="font-display text-sm font-bold text-foreground">Token Bank</h3>
            <p className="text-xs text-muted-foreground">
              {profile?.tokens ?? 0} identification tokens available
            </p>
          </div>
          <button
            onClick={() => navigate("/app/token-info")}
            className="text-xs font-semibold"
            style={{ color: `hsl(${CYAN_HEX})` }}
          >
            Manage
          </button>
        </SculptedCard>
      )}

      {/* ── RockScout Friends section ── */}
      <div className="space-y-3">
        <h2 className="font-display text-lg font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
          RockScout Friends
        </h2>
        <SculptedCard accent="aqua" className="p-4">
          <div className="flex items-center gap-3">
            <div
              className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
            >
              <Users className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <p className="text-sm font-semibold text-foreground">
                {friendsCount} {friendsCount === 1 ? "Connection" : "Connections"}
              </p>
              <p className="text-xs text-muted-foreground">
                Find and connect with fellow rockhounds
              </p>
            </div>
          </div>
          <div className="mt-3 flex gap-2">
            <button
              onClick={() => navigate("/app/friends")}
              className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-1.5 rounded-lg px-3 py-2 text-xs font-semibold"
              style={{ ["--sculpted-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
            >
              <Users className="h-3.5 w-3.5" />
              Friends
            </button>
            <button
              onClick={() => navigate("/app/discover-hunters")}
              className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-1.5 rounded-lg px-3 py-2 text-xs font-semibold"
              style={{ ["--sculpted-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <Gem className="h-3.5 w-3.5" />
              Discover
            </button>
            <button
              onClick={() => navigate("/app/scan")}
              className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-1.5 rounded-lg px-3 py-2 text-xs font-semibold"
              style={{ ["--sculpted-accent" as string]: CYAN_HEX, color: `hsl(${CYAN_HEX})` }}
            >
              <Smartphone className="h-3.5 w-3.5" />
              Scan
            </button>
          </div>
        </SculptedCard>
      </div>

      {/* ── Settings sections ── */}
      <div className="space-y-3">
        <h2 className="font-display text-lg font-bold" style={{ color: `hsl(${AQUA_HEX})` }}>
          Settings
        </h2>

        <SculptedCard
          accent="citrine"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/settings")}
        >
          <div className="flex items-center gap-3 p-4">
            <div
              className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <Settings className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold text-foreground">
                App Settings
              </h3>
              <p className="text-xs text-muted-foreground">
                Notifications, offline cache, theme
              </p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        <SculptedCard
          accent="aqua"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/social-settings")}
        >
          <div className="flex items-center gap-3 p-4">
            <div
              className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
            >
              <Users className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold text-foreground">
                Social Settings
              </h3>
              <p className="text-xs text-muted-foreground">
                Friends, scan radius, location sharing
              </p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        <SculptedCard
          accent="cyan"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/offline")}
        >
          <div className="flex items-center gap-3 p-4">
            <div
              className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CYAN_HEX, color: `hsl(${CYAN_HEX})` }}
            >
              <Download className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold text-foreground">
                Offline Downloads
              </h3>
              <p className="text-xs text-muted-foreground">
                Cache specimens & guides for field trips
              </p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        <SculptedCard
          accent="amethyst"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/manage-devices")}
        >
          <div className="flex items-center gap-3 p-4">
            <div
              className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: AMETHYST_HEX, color: `hsl(${AMETHYST_HEX})` }}
            >
              <Smartphone className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold text-foreground">
                Manage Devices
              </h3>
              <p className="text-xs text-muted-foreground">
                Push notification subscriptions
              </p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        <SculptedCard
          accent="success"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/referral")}
        >
          <div className="flex items-center gap-3 p-4">
            <div
              className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: SUCCESS_HEX, color: `hsl(${SUCCESS_HEX})` }}
            >
              <Gift className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold text-foreground">
                Refer a Friend
              </h3>
              <p className="text-xs text-muted-foreground">
                Earn tokens for every referral
              </p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        <SculptedCard
          accent="danger"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/contact")}
        >
          <div className="flex items-center gap-3 p-4">
            <div
              className="icon-badge flex h-10 w-10 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: DANGER_HEX, color: `hsl(${DANGER_HEX})` }}
            >
              <Shield className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <h3 className="font-display text-sm font-bold text-foreground">
                Support & Privacy
              </h3>
              <p className="text-xs text-muted-foreground">
                Contact us, privacy policy, terms
              </p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>
      </div>

      {/* ── Sign out ── */}
      <SculptedButton
        accent="danger"
        size="lg"
        className="w-full"
        onClick={handleSignOut}
      >
        <LogOut className="h-4 w-4" />
        Sign Out
      </SculptedButton>

      {/* ── Version footer ── */}
      <div className="pt-2 text-center">
        <p className="text-xs text-muted-foreground">
          RockScout v1.1.6 · {isPremium ? "Premium" : "Free"} account
        </p>
      </div>

      {/* ── Edit Profile Sheet ── */}
      {showEditSheet && (
        <div
          className="fixed inset-0 z-[80] flex items-end justify-center bg-black/60 backdrop-blur-sm md:items-center"
          onClick={() => setShowEditSheet(false)}
        >
          <div
            className="dark-card sculpted-raised w-full max-w-md rounded-t-2xl p-6 md:rounded-2xl"
            style={{ ["--sculpted-accent" as string]: CITRINE_HEX }}
            onClick={(e) => e.stopPropagation()}
          >
            <div className="mb-4 flex items-center justify-between">
              <h3 className="font-display text-lg font-bold text-foreground">
                Edit Profile
              </h3>
              <button
                onClick={() => setShowEditSheet(false)}
                className="text-muted-foreground hover:text-foreground"
              >
                ✕
              </button>
            </div>

            {/* Avatar picker */}
            <div className="mb-4">
              <label className="mb-2 block text-xs font-semibold text-muted-foreground">
                Avatar
              </label>
              <div className="mb-2 flex items-center gap-3">
                <div
                  className="glowing-border flex h-14 w-14 items-center justify-center rounded-full text-2xl"
                  style={{ ["--glow-color" as string]: CITRINE_HEX }}
                >
                  {editEmoji}
                </div>
                <span className="text-sm text-muted-foreground">
                  Tap an emoji below
                </span>
              </div>
              <div className="grid grid-cols-8 gap-2 sm:grid-cols-10">
                {AVATAR_OPTIONS.map((emoji) => (
                  <button
                    key={emoji}
                    onClick={() => setEditEmoji(emoji)}
                    className={`flex h-9 w-9 items-center justify-center rounded-lg text-lg transition-all ${
                      editEmoji === emoji
                        ? "ring-2 ring-primary"
                        : "hover:bg-muted/50"
                    }`}
                  >
                    {emoji}
                  </button>
                ))}
              </div>
            </div>

            {/* Display name */}
            <div className="mb-4">
              <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                Display Name
              </label>
              <input
                type="text"
                value={editName}
                onChange={(e) => setEditName(e.target.value)}
                maxLength={30}
                placeholder="Your hunter name"
                className="w-full dark-card sculpted-raised rounded-xl px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
              />
            </div>

            {/* Status selector */}
            <div className="mb-4">
              <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                Hunter Status
              </label>
              <div className="flex flex-wrap gap-2">
                {HUNTER_STATUS_OPTIONS.map((opt) => (
                  <button
                    key={opt.value}
                    onClick={() => setEditStatus(opt.value)}
                    className={`flex items-center gap-1.5 rounded-full px-3 py-1.5 text-xs font-medium transition-all ${
                      editStatus === opt.value
                        ? "ring-2 ring-primary bg-primary/10"
                        : "border border-border hover:bg-muted/40"
                    }`}
                  >
                    <span>{opt.icon}</span>
                    {opt.label}
                  </button>
                ))}
              </div>
            </div>

            {/* Save / Cancel */}
            <div className="flex gap-3">
              <SculptedButton
                accent="aqua"
                size="md"
                className="flex-1"
                onClick={() => setShowEditSheet(false)}
              >
                Cancel
              </SculptedButton>
              <SculptedButton
                accent="citrine"
                size="md"
                glowing
                className="flex-1"
                disabled={saveProfile.isPending}
                onClick={() => saveProfile.mutate()}
              >
                <Check className="h-4 w-4" />
                {saveProfile.isPending ? "Saving…" : "Save"}
              </SculptedButton>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
