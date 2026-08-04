import { useState, useEffect, useRef, useCallback } from "react";
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
  Calendar,
  Camera,
  ImageIcon,
  Loader2,
  X,
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
import { filterProfanity } from "@/lib/profanity-filter";
import { isUsernameTaken } from "@/lib/username-resolver";

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

/** 30 easily-distinguishable colors for the profile highlight picker. */
const PROFILE_HIGHLIGHT_COLORS = [
  "#FF3B30", "#FF9500", "#FFCC00", "#FF2D55", "#E8A33D", "#D9B26A",
  "#34C759", "#5CC98C", "#00C7BE", "#30B0C7", "#32ADE6", "#007AFF",
  "#5856D6", "#9B7BD8", "#AF52DE", "#B08BFF", "#FF6B3D", "#FF5E3A",
  "#00E5C9", "#4FC3F7", "#6FA8C7", "#7CB5EC", "#8BBF6A", "#6FBF8A",
  "#B87333", "#C97B4A", "#E2574C", "#1B3A4B", "#44AACC", "#C0C0C0",
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
  avatar_image_path?: string | null;
  status: string;
  level: number;
  xp: number;
  is_pro: boolean;
  pro_badge: boolean;
  tokens: number;
  bio?: string;
  home_region?: string;
  gender?: string;
  birthday?: string;
  birthday_private?: boolean;
  favorite_rock?: string;
  highlight_color?: string | null;
}

const GENDER_OPTIONS = [
  { value: "rather_not_say", label: "Rather not say" },
  { value: "male", label: "Male" },
  { value: "female", label: "Female" },
];

const MONTHS = [
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

function formatBirthday(dateStr: string): string {
  if (!dateStr) return "";
  const parts = dateStr.split("-");
  if (parts.length !== 3) return dateStr;
  return `${parts[1]}/${parts[2]}/${parts[0]}`;
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
  const [editGender, setEditGender] = useState("rather_not_say");
  const [editBirthday, setEditBirthday] = useState("");
  const [editBirthdayPrivate, setEditBirthdayPrivate] = useState(true);
  const [editFavoriteRock, setEditFavoriteRock] = useState("");
  const [editHighlightColor, setEditHighlightColor] = useState<string | null>(null);
  const [showBirthdayPicker, setShowBirthdayPicker] = useState(false);
  const [pickerMonth, setPickerMonth] = useState(0);
  const [pickerYear, setPickerYear] = useState(2000);
  const [showMonthScroll, setShowMonthScroll] = useState(false);
  const [showYearScroll, setShowYearScroll] = useState(false);
  const [editAvatarImage, setEditAvatarImage] = useState<string | null>(null);
  const [avatarUploading, setAvatarUploading] = useState(false);
  const [avatarScale, setAvatarScale] = useState(1);
  const [avatarOffset, setAvatarOffset] = useState({ x: 0, y: 0 });
  const avatarPreviewRef = useRef<HTMLDivElement>(null);
  const avatarImgRef = useRef<HTMLImageElement>(null);
  const avatarDragStart = useRef<{ x: number; y: number } | null>(null);
  const avatarPinchDist = useRef<number | null>(null);
  const avatarPinchScale = useRef<number>(1);
  const [showFullScreenAvatarEditor, setShowFullScreenAvatarEditor] = useState(false);
  const fullScreenAvatarRef = useRef<HTMLDivElement>(null);
  const fullScreenAvatarImgRef = useRef<HTMLImageElement>(null);
  const fullScreenDragStart = useRef<{ x: number; y: number } | null>(null);
  const fullScreenPinchDist = useRef<number | null>(null);
  const fullScreenPinchScale = useRef<number>(1);
  const [nameError, setNameError] = useState<string | null>(null);
  const [nameChecking, setNameChecking] = useState(false);
  const [originalName, setOriginalName] = useState("");
  const nameCheckTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const avatarFileRef = useRef<HTMLInputElement>(null);
  const avatarCameraRef = useRef<HTMLInputElement>(null);

  // ── Profile data ──
  const { data: profile } = useQuery<ProfileData>({
    queryKey: ["profile", user?.id],
    queryFn: async () => {
      if (!user) return null;
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, avatar_image_path, status, level, xp, is_pro, pro_badge, tokens, bio, home_region, gender, birthday, birthday_private, favorite_rock, highlight_color")
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
      setOriginalName(profile.display_name || "");
      setEditEmoji(profile.avatar_emoji || "💎");
      setEditAvatarImage(profile.avatar_image_path ?? null);
      setEditStatus(profile.status || "off");
      setEditGender(profile.gender || "rather_not_say");
      setEditBirthday(profile.birthday || "");
      setEditBirthdayPrivate(profile.birthday_private ?? true);
      setEditFavoriteRock(profile.favorite_rock || "");
      setEditHighlightColor(profile.highlight_color ?? null);
      if (profile.birthday) {
        const parts = profile.birthday.split("-");
        if (parts.length === 3) {
          setPickerMonth(parseInt(parts[1]) - 1);
          setPickerYear(parseInt(parts[0]));
        }
      }
    }
  }, [profile]);

  // ── Debounced real-time name validation ──
  const checkNameAvailability = useCallback(
    async (name: string) => {
      const trimmed = name.trim();
      if (!trimmed) {
        setNameError("Display name cannot be empty.");
        setNameChecking(false);
        return;
      }
      if (trimmed.toLowerCase() === originalName.trim().toLowerCase()) {
        setNameError(null);
        setNameChecking(false);
        return;
      }
      setNameChecking(true);
      try {
        const taken = await isUsernameTaken(trimmed, user?.id);
        setNameError(taken ? "That username is already taken. Add numbers to make it unique." : null);
      } catch {
        setNameError(null);
      }
      setNameChecking(false);
    },
    [originalName, user?.id],
  );

  useEffect(() => {
    if (!showEditSheet) return;
    if (nameCheckTimer.current) clearTimeout(nameCheckTimer.current);
    setNameChecking(true);
    nameCheckTimer.current = setTimeout(() => {
      checkNameAvailability(editName);
    }, 500);
    return () => {
      if (nameCheckTimer.current) clearTimeout(nameCheckTimer.current);
    };
  }, [editName, showEditSheet, checkNameAvailability]);

  // ── Avatar upload handler ──
  const handleAvatarFile = useCallback(
    async (file: File) => {
      if (!user) return;
      if (file.size > 5 * 1024 * 1024) {
        toast.error("Image must be under 5 MB.");
        return;
      }
      setAvatarUploading(true);
      try {
        const reader = new FileReader();
        reader.onload = async () => {
          const dataUrl = reader.result as string;
          // Upload to Supabase Storage
          const ext = file.name.split(".").pop()?.toLowerCase() || "jpg";
          const path = `avatars/${user.id}/avatar_${Date.now()}.${ext}`;
          const { error: uploadErr } = await supabase.storage
            .from("rockscout-avatars")
            .upload(path, file, { upsert: true });
          if (uploadErr) {
            // Fallback: store the data URL directly in the profile
            setEditAvatarImage(dataUrl);
          } else {
            const { data: urlData } = supabase.storage
              .from("rockscout-avatars")
              .getPublicUrl(path);
            setEditAvatarImage(urlData.publicUrl || dataUrl);
          }
          setAvatarUploading(false);
        };
        reader.onerror = () => {
          setAvatarUploading(false);
          toast.error("Failed to read image file.");
        };
        reader.readAsDataURL(file);
      } catch {
        setAvatarUploading(false);
        toast.error("Failed to upload avatar.");
      }
    },
    [user],
  );

  // ── Crop avatar image based on pan/zoom state ──
  const cropAvatarFromPreview = useCallback(async (): Promise<string | null> => {
    if (!editAvatarImage) return null;
    if (avatarScale === 1 && avatarOffset.x === 0 && avatarOffset.y === 0) {
      return editAvatarImage;
    }
    const img = avatarImgRef.current;
    const previewEl = avatarPreviewRef.current;
    if (!img || !previewEl) return editAvatarImage;
    try {
      const naturalW = img.naturalWidth;
      const naturalH = img.naturalHeight;
      const boxPx = previewEl.clientWidth;
      const cropScale = Math.max(boxPx / naturalW, boxPx / naturalH);
      const imgLeft = (boxPx - naturalW * cropScale) / 2;
      const imgTop = (boxPx - naturalH * cropScale) / 2;
      const us = Math.max(1, avatarScale);
      const visSize = boxPx / (cropScale * us);
      const cx = (boxPx / 2 - imgLeft - avatarOffset.x / us) / cropScale;
      const cy = (boxPx / 2 - imgTop - avatarOffset.y / us) / cropScale;
      const half = visSize / 2;
      const left = Math.max(0, Math.min(cx - half, naturalW - visSize));
      const top = Math.max(0, Math.min(cy - half, naturalH - visSize));
      const size = Math.min(naturalW, naturalH, visSize);
      const canvas = document.createElement("canvas");
      const outSize = 300;
      canvas.width = outSize;
      canvas.height = outSize;
      const ctx = canvas.getContext("2d");
      if (!ctx) return editAvatarImage;
      ctx.drawImage(img, left, top, size, size, 0, 0, outSize, outSize);
      return canvas.toDataURL("image/jpeg", 0.9);
    } catch {
      return editAvatarImage;
    }
  }, [editAvatarImage, avatarScale, avatarOffset]);

  // ── Save profile edits ──
  const saveProfile = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Not signed in");
      const baseName = filterProfanity((editName.trim() || user.email?.split("@")[0]) ?? "Rockhound").filteredText;
      const taken = await isUsernameTaken(baseName, user.id);
      if (taken) {
        throw new Error("That username is already in use. Try adding a couple numbers to make it unique.");
      }
      // Crop avatar if user adjusted pan/zoom
      let finalAvatarImage = editAvatarImage;
      if (editAvatarImage && (avatarScale !== 1 || avatarOffset.x !== 0 || avatarOffset.y !== 0)) {
        finalAvatarImage = await cropAvatarFromPreview();
      }
      const { error } = await supabase
        .from("rockscout_profiles")
        .update({
          display_name: baseName,
          avatar_emoji: editEmoji,
          avatar_image_path: finalAvatarImage,
          status: editStatus,
          gender: editGender,
          birthday: editBirthday || null,
          birthday_private: editBirthdayPrivate,
          favorite_rock: filterProfanity(editFavoriteRock.trim()).filteredText || null,
          highlight_color: editHighlightColor || null,
        })
        .eq("id", user.id);
      if (error) throw error;
      // Force invalidate all profile-dependent queries so other devices
      // and tabs pick up the changes immediately via React Query refetch.
      queryClient.invalidateQueries({ queryKey: ["profile"] });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["profile", user?.id] });
      queryClient.invalidateQueries({ queryKey: ["friends"] });
      setShowEditSheet(false);
      toast.success("Profile updated & synced");
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
        accent={profile?.highlight_color ? undefined : "citrine"}
        glowing
        className="overflow-hidden"
        style={profile?.highlight_color ? {
          ["--sculpted-accent" as string]: profile.highlight_color,
          ["--glow-color" as string]: profile.highlight_color,
        } : undefined}
      >
        {/* Background gradient band */}
        <div
          className="relative flex h-32 items-center justify-center overflow-hidden"
          style={{
            background: profile?.highlight_color
              ? `linear-gradient(180deg, ${profile.highlight_color}40, hsl(${AQUA_HEX} / 0.15), hsl(30 10% 9%))`
              : `linear-gradient(180deg, hsl(${CITRINE_HEX} / 0.25), hsl(${AQUA_HEX} / 0.15), hsl(30 10% 9%))`,
          }}
        >
          <Sparkles
            className="absolute left-4 top-4 h-5 w-5"
            style={{ color: profile?.highlight_color ? `${profile.highlight_color}66` : `hsl(${CITRINE_HEX} / 0.4)` }}
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
              className="glowing-border flex h-[75px] w-[75px] shrink-0 items-center justify-center rounded-2xl text-3xl"
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

          {/* Gender */}
          {profile?.gender && profile.gender !== "rather_not_say" && (
            <div className="mt-2 flex items-center gap-1.5">
              <span className="text-sm font-medium text-[hsl(var(--text-mid))]">
                {GENDER_OPTIONS.find((g) => g.value === profile.gender)?.label ?? "Rather not say"}
              </span>
            </div>
          )}

          {/* Birthday */}
          {profile?.birthday && (
            <div className="mt-1 flex items-center gap-1.5">
              <span className="text-xs font-medium text-[hsl(var(--text-mid))]">
                {profile.birthday_private ? "Birthday: Private" : `Birthday: ${formatBirthday(profile.birthday)}`}
              </span>
            </div>
          )}
        </div>

        {/* Favorite Rock — bottom of profile card */}
        {profile?.favorite_rock && (
          <div
            className="flex items-center gap-2 px-5 py-3"
            style={{
              background: `linear-gradient(90deg, hsl(${CITRINE_HEX} / 0.12), hsl(${AQUA_HEX} / 0.08))`,
              borderTop: `1px solid hsl(${CITRINE_HEX} / 0.2)`,
            }}
          >
            <Gem className="h-4 w-4 shrink-0" style={{ color: `hsl(${CITRINE_HEX})` }} />
            <span className="text-sm font-bold text-foreground">
              Favorite Rock: <span style={{ color: `hsl(${CITRINE_HEX})` }}>{profile.favorite_rock}</span>
            </span>
          </div>
        )}
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
          RockScout v1.1.9 · {isPremium ? "Premium" : "Free"} account
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

            {/* Avatar picker — photo + emoji */}
            <div className="mb-4">
              <label className="mb-2 block text-xs font-semibold text-muted-foreground">
                Avatar <span className="text-red-500">*</span>
              </label>
              <p className="mb-2 text-xs text-muted-foreground/70">
                Drag to pan and pinch to zoom. The square preview shows what others will see.
              </p>
              {/* Camera + Gallery buttons (side-by-side) */}
              <div className="mb-3 flex gap-2">
                <button
                  onClick={() => avatarCameraRef.current?.click()}
                  className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-2 rounded-lg px-3 py-2.5 text-xs font-bold"
                  style={{ ["--sculpted-accent" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
                >
                  <Camera className="h-4 w-4" />
                  Take Photo
                </button>
                <button
                  onClick={() => avatarFileRef.current?.click()}
                  className="sculpted-button sculpted-raised dark-card flex flex-1 items-center justify-center gap-2 rounded-lg px-3 py-2.5 text-xs font-bold"
                  style={{ ["--sculpted-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
                >
                  <ImageIcon className="h-4 w-4" />
                  Choose from Gallery
                </button>
              </div>
              {/* Pan/zoom preview or fallback */}
              {avatarUploading ? (
                <div
                  className="glowing-border mb-3 flex h-[200px] items-center justify-center rounded-2xl"
                  style={{ ["--glow-color" as string]: CITRINE_HEX }}
                >
                  <Loader2 className="h-8 w-8 animate-spin text-primary" />
                </div>
              ) : editAvatarImage ? (
                <div className="mb-3">
                  <div
                    ref={avatarPreviewRef}
                    className="glowing-border relative flex h-[200px] w-full items-center justify-center overflow-hidden rounded-2xl"
                    style={{
                      ["--glow-color" as string]: CITRINE_HEX,
                      touchAction: "none",
                      cursor: avatarScale > 1 ? "grab" : "default",
                    }}
                    onPointerDown={(e) => {
                      if (avatarScale <= 1) return;
                      (e.target as HTMLElement).setPointerCapture(e.pointerId);
                      avatarDragStart.current = { x: e.clientX - avatarOffset.x, y: e.clientY - avatarOffset.y };
                    }}
                    onPointerMove={(e) => {
                      if (!avatarDragStart.current) return;
                      const previewEl = avatarPreviewRef.current;
                      if (!previewEl) return;
                      const boxPx = previewEl.clientWidth;
                      const maxOff = boxPx * (avatarScale - 1) / 2;
                      const newX = e.clientX - avatarDragStart.current.x;
                      const newY = e.clientY - avatarDragStart.current.y;
                      setAvatarOffset({
                        x: Math.max(-maxOff, Math.min(maxOff, newX)),
                        y: Math.max(-maxOff, Math.min(maxOff, newY)),
                      });
                    }}
                    onPointerUp={() => { avatarDragStart.current = null; }}
                    onPointerCancel={() => { avatarDragStart.current = null; }}
                    onWheel={(e) => {
                      e.preventDefault();
                      const delta = -e.deltaY * 0.002;
                      setAvatarScale((s) => Math.max(1, Math.min(5, s + delta)));
                    }}
                  >
                    <img
                      ref={avatarImgRef}
                      src={editAvatarImage}
                      alt="Avatar preview"
                      className="h-full w-full object-cover"
                      style={{
                        transform: `scale(${avatarScale}) translate(${avatarOffset.x / avatarScale}px, ${avatarOffset.y / avatarScale}px)`,
                        transformOrigin: "center",
                        userSelect: "none",
                        pointerEvents: "none",
                      }}
                      onError={(e) => {
                        (e.target as HTMLImageElement).style.display = "none";
                      }}
                      draggable={false}
                    />
                    {/* 75px crop frame with grid overlay + corner brackets */}
                    <div
                      className="pointer-events-none absolute left-1/2 top-1/2"
                      style={{
                        width: "75px",
                        height: "75px",
                        transform: "translate(-50%, -50%)",
                        boxShadow: `0 0 0 9999px rgba(0,0,0,0.35)`,
                      }}
                    >
                      <div className="absolute inset-0 rounded-xl" style={{ border: `1px solid hsl(${CITRINE_HEX} / 0.7)` }} />
                      <div className="absolute left-1/3 top-0 h-full w-px" style={{ background: "rgba(255,255,255,0.25)" }} />
                      <div className="absolute left-2/3 top-0 h-full w-px" style={{ background: "rgba(255,255,255,0.25)" }} />
                      <div className="absolute top-1/3 left-0 w-full h-px" style={{ background: "rgba(255,255,255,0.25)" }} />
                      <div className="absolute top-2/3 left-0 w-full h-px" style={{ background: "rgba(255,255,255,0.25)" }} />
                      <div className="absolute left-0 top-0 h-3 w-3 rounded-tl-lg" style={{ borderLeft: `2px solid hsl(${CITRINE_HEX})`, borderTop: `2px solid hsl(${CITRINE_HEX})` }} />
                      <div className="absolute right-0 top-0 h-3 w-3 rounded-tr-lg" style={{ borderRight: `2px solid hsl(${CITRINE_HEX})`, borderTop: `2px solid hsl(${CITRINE_HEX})` }} />
                      <div className="absolute left-0 bottom-0 h-3 w-3 rounded-bl-lg" style={{ borderLeft: `2px solid hsl(${CITRINE_HEX})`, borderBottom: `2px solid hsl(${CITRINE_HEX})` }} />
                      <div className="absolute right-0 bottom-0 h-3 w-3 rounded-br-lg" style={{ borderRight: `2px solid hsl(${CITRINE_HEX})`, borderBottom: `2px solid hsl(${CITRINE_HEX})` }} />
                    </div>
                    {/* Tap-to-expand badge */}
                    <button
                      onClick={() => setShowFullScreenAvatarEditor(true)}
                      className="absolute bottom-2 right-2 flex items-center gap-1 rounded-lg bg-black/70 px-2.5 py-1 text-xs font-semibold transition-all hover:bg-black/85"
                      style={{ color: `hsl(${CITRINE_HEX})` }}
                    >
                      <Edit3 className="h-3 w-3" />
                      Tap to expand
                    </button>
                  </div>
                  <div className="mt-1.5 flex items-center justify-between">
                    <span className="text-xs font-semibold" style={{ color: `hsl(${AQUA_HEX})` }}>
                      Zoom: {avatarScale.toFixed(1)}x
                    </span>
                    <button
                      onClick={() => { setAvatarScale(1); setAvatarOffset({ x: 0, y: 0 }); }}
                      className="rounded-lg px-3 py-1 text-xs font-bold transition-all"
                      style={{
                        background: `hsl(${CITRINE_HEX} / 0.12)`,
                        color: `hsl(${CITRINE_HEX})`,
                      }}
                    >
                      Reset
                    </button>
                  </div>
                  <button
                    onClick={() => { setEditAvatarImage(null); setAvatarScale(1); setAvatarOffset({ x: 0, y: 0 }); }}
                    className="mt-2 flex items-center justify-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-bold text-red-400 hover:bg-red-500/10"
                  >
                    <X className="h-3 w-3" />
                    Remove Photo
                  </button>
                </div>
              ) : (
                <div
                  className="glowing-border mb-3 flex h-[120px] w-full items-center justify-center rounded-2xl text-5xl"
                  style={{
                    background: `radial-gradient(circle, hsl(${CITRINE_HEX} / 0.15), hsl(${AQUA_HEX} / 0.08))`,
                    ["--glow-color" as string]: CITRINE_HEX,
                  }}
                >
                  {editEmoji}
                </div>
              )}
              {/* Hidden file inputs */}
              <input
                ref={avatarFileRef}
                type="file"
                accept="image/*"
                className="hidden"
                onChange={(e) => {
                  const file = e.target.files?.[0];
                  if (file) {
                    setAvatarScale(1);
                    setAvatarOffset({ x: 0, y: 0 });
                    handleAvatarFile(file);
                  }
                  e.target.value = "";
                }}
              />
              <input
                ref={avatarCameraRef}
                type="file"
                accept="image/*"
                capture="user"
                className="hidden"
                onChange={(e) => {
                  const file = e.target.files?.[0];
                  if (file) {
                    setAvatarScale(1);
                    setAvatarOffset({ x: 0, y: 0 });
                    handleAvatarFile(file);
                  }
                  e.target.value = "";
                }}
              />
              <p className="mb-2 text-xs text-muted-foreground">Or pick an emoji:</p>
              <div className="grid grid-cols-8 gap-2 sm:grid-cols-10">
                {AVATAR_OPTIONS.map((emoji) => (
                  <button
                    key={emoji}
                    onClick={() => {
                      setEditEmoji(emoji);
                      setEditAvatarImage(null);
                    }}
                    className={`flex h-9 w-9 items-center justify-center rounded-lg text-lg transition-all ${
                      editEmoji === emoji && !editAvatarImage
                        ? "ring-2 ring-primary"
                        : "hover:bg-muted/50"
                    }`}
                  >
                    {emoji}
                  </button>
                ))}
              </div>
            </div>

            {/* Display name — mandatory with real-time validation */}
            <div className="mb-4">
              <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                Display Name <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={editName}
                onChange={(e) => setEditName(e.target.value)}
                maxLength={30}
                placeholder="Your hunter name"
                className={`w-full dark-card sculpted-raised rounded-xl px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:outline-none ${
                  nameError ? "border-2 border-red-500" : "focus:border-primary"
                }`}
              />
              {/* Real-time validation indicator */}
              {nameChecking && (
                <div className="mt-1.5 flex items-center gap-1.5">
                  <Loader2 className="h-3 w-3 animate-spin text-muted-foreground" />
                  <span className="text-xs text-muted-foreground">Checking availability…</span>
                </div>
              )}
              {!nameChecking && !nameError && editName.trim() && editName.trim().toLowerCase() !== originalName.trim().toLowerCase() && (
                <div className="mt-1.5 flex items-center gap-1.5">
                  <Check className="h-3 w-3 text-green-500" />
                  <span className="text-xs font-semibold text-green-500">Name is available</span>
                </div>
              )}
              {nameError && (
                <div className="mt-1.5 text-xs font-semibold text-red-500">
                  {nameError}
                </div>
              )}
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

            {/* Gender dropdown */}
            <div className="mb-4">
              <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                Gender
              </label>
              <select
                value={editGender}
                onChange={(e) => setEditGender(e.target.value)}
                className="w-full dark-card sculpted-raised rounded-xl px-4 py-2.5 text-sm text-foreground focus:border-primary focus:outline-none"
              >
                {GENDER_OPTIONS.map((opt) => (
                  <option key={opt.value} value={opt.value} className="bg-background text-foreground">
                    {opt.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Birthday picker */}
            <div className="mb-4">
              <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                Birthday
              </label>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => setShowBirthdayPicker(!showBirthdayPicker)}
                  className="dark-card sculpted-raised flex items-center gap-2 rounded-xl px-4 py-2.5 text-sm text-foreground"
                >
                  <Calendar className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
                  {editBirthday ? formatBirthday(editBirthday) : "Set birthday"}
                </button>
                {editBirthday && (
                  <button
                    onClick={() => { setEditBirthday(""); setShowBirthdayPicker(false); }}
                    className="text-xs text-muted-foreground hover:text-foreground"
                  >
                    Clear
                  </button>
                )}
              </div>

              {/* Public/Private toggle */}
              <div className="mt-2 flex items-center gap-2">
                <button
                  onClick={() => setEditBirthdayPrivate(!editBirthdayPrivate)}
                  onContextMenu={(e) => { e.preventDefault(); setEditBirthdayPrivate(!editBirthdayPrivate); }}
                  className={`rounded-full px-3 py-1.5 text-xs font-bold transition-all ${
                    editBirthdayPrivate
                      ? "border border-border text-muted-foreground"
                      : "ring-2 ring-primary bg-primary/10 text-primary"
                  }`}
                  title="Long press to toggle"
                >
                  {editBirthdayPrivate ? "🔒 Private" : "👁 Public"}
                </button>
                <span className="text-xs text-muted-foreground">Long press to change</span>
              </div>

              {/* Birthday calendar popup */}
              {showBirthdayPicker && (
                <div className="mt-3 dark-card sculpted-raised rounded-xl p-4">
                  {/* Rather not say */}
                  <button
                    onClick={() => { setEditBirthday(""); setShowBirthdayPicker(false); }}
                    className="mb-3 w-full rounded-lg px-3 py-2 text-xs font-semibold text-muted-foreground hover:bg-muted/50"
                  >
                    Rather not say
                  </button>

                  {/* Month/Year selectors */}
                  <div className="mb-3 flex items-center gap-2">
                    <button
                      onClick={() => { setShowMonthScroll(!showMonthScroll); setShowYearScroll(false); }}
                      className="dark-card sculpted-raised rounded-lg px-3 py-2 text-sm text-foreground"
                    >
                      {MONTHS[pickerMonth]}
                    </button>
                    <button
                      onClick={() => { setShowYearScroll(!showYearScroll); setShowMonthScroll(false); }}
                      className="dark-card sculpted-raised rounded-lg px-3 py-2 text-sm text-foreground"
                    >
                      {pickerYear}
                    </button>
                  </div>

                  {/* Month scroll dropdown */}
                  {showMonthScroll && (
                    <div className="mb-3 max-h-32 overflow-y-auto rounded-lg border border-border">
                      {MONTHS.map((m, idx) => (
                        <button
                          key={m}
                          onClick={() => { setPickerMonth(idx); setShowMonthScroll(false); }}
                          className={`block w-full px-3 py-1.5 text-left text-sm ${
                            idx === pickerMonth ? "bg-primary/20 text-primary" : "text-foreground hover:bg-muted/50"
                          }`}
                        >
                          {m}
                        </button>
                      ))}
                    </div>
                  )}

                  {/* Year scroll dropdown */}
                  {showYearScroll && (
                    <div className="mb-3 max-h-32 overflow-y-auto rounded-lg border border-border">
                      {Array.from({ length: 100 }, (_, i) => 2025 - i).map((yr) => (
                        <button
                          key={yr}
                          onClick={() => { setPickerYear(yr); setShowYearScroll(false); }}
                          className={`block w-full px-3 py-1.5 text-left text-sm ${
                            yr === pickerYear ? "bg-primary/20 text-primary" : "text-foreground hover:bg-muted/50"
                          }`}
                        >
                          {yr}
                        </button>
                      ))}
                    </div>
                  )}

                  {/* Day grid */}
                  {!showMonthScroll && !showYearScroll && (
                    <>
                      <div className="mb-1 grid grid-cols-7 gap-1 text-center text-xs text-muted-foreground">
                        {['S','M','T','W','T','F','S'].map((d, i) => <div key={i}>{d}</div>)}
                      </div>
                      <div className="grid grid-cols-7 gap-1">
                        {Array.from({ length: new Date(pickerYear, pickerMonth + 1, 0).getDate() }, (_, i) => i + 1).map((day) => {
                          const dateStr = `${pickerYear}-${String(pickerMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
                          const isSelected = editBirthday === dateStr;
                          return (
                            <button
                              key={day}
                              onClick={() => { setEditBirthday(dateStr); setShowBirthdayPicker(false); }}
                              className={`flex h-8 items-center justify-center rounded text-sm ${
                                isSelected ? "bg-primary text-primary-foreground" : "text-foreground hover:bg-muted/50"
                              }`}
                            >
                              {day}
                            </button>
                          );
                        })}
                      </div>
                    </>
                  )}
                </div>
              )}
            </div>

            {/* Favorite Rock */}
            <div className="mb-4">
              <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                Favorite Rock
              </label>
              <input
                type="text"
                value={editFavoriteRock}
                onChange={(e) => setEditFavoriteRock(e.target.value)}
                maxLength={40}
                placeholder="e.g. Quartz, Amethyst, Fluorite..."
                className="w-full dark-card sculpted-raised rounded-xl px-4 py-2.5 text-sm text-foreground placeholder:text-muted-foreground/60 focus:border-primary focus:outline-none"
              />
            </div>

            {/* Profile Highlight Color */}
            <div className="mb-4">
              <label className="mb-1.5 block text-xs font-semibold text-muted-foreground">
                Profile Highlight Color
              </label>
              <p className="mb-2 text-xs text-muted-foreground/70">
                Pick a color others will see on your profile page.
              </p>
              <div className="grid grid-cols-6 gap-2 sm:grid-cols-10">
                {PROFILE_HIGHLIGHT_COLORS.map((color) => (
                  <button
                    key={color}
                    onClick={() => setEditHighlightColor(color === editHighlightColor ? null : color)}
                    className={`flex h-8 w-8 items-center justify-center rounded-full transition-all ${
                      editHighlightColor === color
                        ? "ring-2 ring-white ring-offset-2 ring-offset-background"
                        : "hover:scale-110"
                    }`}
                    style={{ backgroundColor: color }}
                    aria-label={`Select ${color}`}
                  >
                    {editHighlightColor === color && (
                      <Check className="h-4 w-4 text-white" />
                    )}
                  </button>
                ))}
              </div>
              {editHighlightColor && (
                <button
                  onClick={() => setEditHighlightColor(null)}
                  className="mt-2 text-xs font-medium text-muted-foreground hover:text-foreground"
                >
                  Reset to default
                </button>
              )}
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

      {/* ── Full-screen avatar editor overlay ── */}
      {showFullScreenAvatarEditor && editAvatarImage && (
        <div
          className="fixed inset-0 z-[90] flex flex-col bg-[#0D0C0A]"
          style={{ touchAction: "none" }}
        >
          {/* Top bar: title + close */}
          <div className="flex items-center justify-between px-4 py-3 pt-[env(safe-area-inset-top)]">
            <h3 className="font-display text-lg font-bold text-foreground">
              Adjust Avatar
            </h3>
            <button
              onClick={() => setShowFullScreenAvatarEditor(false)}
              className="flex h-10 w-10 items-center justify-center rounded-full"
              style={{
                background: "#2A2820",
                border: `2px solid hsl(${CITRINE_HEX} / 0.5)`,
                color: `hsl(${CITRINE_HEX})`,
              }}
              aria-label="Close"
            >
              <X className="h-5 w-5" />
            </button>
          </div>

          {/* Full-screen image area with pan/zoom */}
          <div className="relative flex flex-1 items-center justify-center overflow-hidden">
            <div
              ref={fullScreenAvatarRef}
              className="absolute inset-0 flex items-center justify-center"
              style={{ touchAction: "none", cursor: avatarScale > 1 ? "grab" : "default" }}
              onPointerDown={(e) => {
                if (avatarScale <= 1) return;
                (e.target as HTMLElement).setPointerCapture(e.pointerId);
                fullScreenDragStart.current = { x: e.clientX - avatarOffset.x, y: e.clientY - avatarOffset.y };
              }}
              onPointerMove={(e) => {
                if (!fullScreenDragStart.current) return;
                const el = fullScreenAvatarRef.current;
                if (!el) return;
                const boxPx = Math.max(el.clientWidth, el.clientHeight);
                const maxOff = boxPx * (avatarScale - 1) / 2;
                const newX = e.clientX - fullScreenDragStart.current.x;
                const newY = e.clientY - fullScreenDragStart.current.y;
                setAvatarOffset({
                  x: Math.max(-maxOff, Math.min(maxOff, newX)),
                  y: Math.max(-maxOff, Math.min(maxOff, newY)),
                });
              }}
              onPointerUp={() => { fullScreenDragStart.current = null; }}
              onPointerCancel={() => { fullScreenDragStart.current = null; }}
              onWheel={(e) => {
                e.preventDefault();
                const delta = -e.deltaY * 0.002;
                setAvatarScale((s) => Math.max(1, Math.min(8, s + delta)));
              }}
              onTouchStart={(e) => {
                if (e.touches.length === 2) {
                  const dx = e.touches[0].clientX - e.touches[1].clientX;
                  const dy = e.touches[0].clientY - e.touches[1].clientY;
                  fullScreenPinchDist.current = Math.hypot(dx, dy);
                  fullScreenPinchScale.current = avatarScale;
                }
              }}
              onTouchMove={(e) => {
                if (e.touches.length === 2 && fullScreenPinchDist.current !== null) {
                  e.preventDefault();
                  const dx = e.touches[0].clientX - e.touches[1].clientX;
                  const dy = e.touches[0].clientY - e.touches[1].clientY;
                  const dist = Math.hypot(dx, dy);
                  const ratio = dist / fullScreenPinchDist.current;
                  setAvatarScale(Math.max(1, Math.min(8, fullScreenPinchScale.current * ratio)));
                }
              }}
              onTouchEnd={() => { fullScreenPinchDist.current = null; }}
            >
              <img
                ref={fullScreenAvatarImgRef}
                src={editAvatarImage}
                alt="Full-screen avatar editor"
                className="h-full w-full object-cover"
                style={{
                  transform: `scale(${avatarScale}) translate(${avatarOffset.x / avatarScale}px, ${avatarOffset.y / avatarScale}px)`,
                  transformOrigin: "center",
                  userSelect: "none",
                  pointerEvents: "none",
                }}
                draggable={false}
              />
            </div>

            {/* Crop frame: dark outside + grid overlay + corner brackets */}
            <div
              className="pointer-events-none absolute left-1/2 top-1/2"
              style={{
                width: "75px",
                height: "75px",
                transform: "translate(-50%, -50%)",
                boxShadow: "0 0 0 9999px rgba(0,0,0,0.6)",
              }}
            >
              <div className="absolute inset-0 rounded-xl" style={{ border: `1px solid hsl(${CITRINE_HEX} / 0.7)` }} />
              <div className="absolute left-1/3 top-0 h-full w-px" style={{ background: "rgba(255,255,255,0.3)" }} />
              <div className="absolute left-2/3 top-0 h-full w-px" style={{ background: "rgba(255,255,255,0.3)" }} />
              <div className="absolute top-1/3 left-0 w-full h-px" style={{ background: "rgba(255,255,255,0.3)" }} />
              <div className="absolute top-2/3 left-0 w-full h-px" style={{ background: "rgba(255,255,255,0.3)" }} />
              <div className="absolute left-0 top-0 h-3.5 w-3.5 rounded-tl-lg" style={{ borderLeft: `3px solid hsl(${CITRINE_HEX})`, borderTop: `3px solid hsl(${CITRINE_HEX})` }} />
              <div className="absolute right-0 top-0 h-3.5 w-3.5 rounded-tr-lg" style={{ borderRight: `3px solid hsl(${CITRINE_HEX})`, borderTop: `3px solid hsl(${CITRINE_HEX})` }} />
              <div className="absolute left-0 bottom-0 h-3.5 w-3.5 rounded-bl-lg" style={{ borderLeft: `3px solid hsl(${CITRINE_HEX})`, borderBottom: `3px solid hsl(${CITRINE_HEX})` }} />
              <div className="absolute right-0 bottom-0 h-3.5 w-3.5 rounded-br-lg" style={{ borderRight: `3px solid hsl(${CITRINE_HEX})`, borderBottom: `3px solid hsl(${CITRINE_HEX})` }} />
            </div>
          </div>

          {/* Bottom bar: instructions + zoom + reset + done */}
          <div className="space-y-2.5 px-5 py-4 pb-[env(safe-area-inset-bottom)]">
            <p className="text-center text-xs text-muted-foreground/70">
              Drag to pan · Pinch to zoom · The square shows your avatar
            </p>
            <div className="flex items-center gap-2.5">
              <span
                className="flex-1 text-xs font-semibold"
                style={{ color: `hsl(${AQUA_HEX})` }}
              >
                Zoom: {avatarScale.toFixed(1)}x
              </span>
              <button
                onClick={() => { setAvatarScale(1); setAvatarOffset({ x: 0, y: 0 }); }}
                className="rounded-lg px-4 py-2 text-xs font-bold transition-all"
                style={{
                  background: `hsl(${CITRINE_HEX} / 0.12)`,
                  color: `hsl(${CITRINE_HEX})`,
                }}
              >
                Reset
              </button>
              <SculptedButton
                accent="citrine"
                size="sm"
                glowing
                onClick={() => setShowFullScreenAvatarEditor(false)}
              >
                <Check className="h-4 w-4" />
                Done
              </SculptedButton>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
