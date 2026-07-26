import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import {
  Award,
  Search,
  X,
  Loader2,
  Trophy,
  Star,
  Zap,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import achievementData from "@/data/achievements.json";

interface Achievement {
  id: string;
  name: string;
  description: string;
  emoji: string;
  threshold: number;
  rewardXp: number;
  metric: string;
  familyTag: string;
}

interface AchievementState {
  identify: number;
  collection: number;
  wishlist: number;
  favorite_spots: number;
  trips: number;
  journal: number;
}

const METRIC_LABELS: Record<string, string> = {
  IDENTIFY: "Identifications",
  CAPTURE: "Field Captures",
  WISHLIST_HIT: "Wishlist Hits",
  DIG_SITE_CHECKIN: "Dig Site Check-ins",
  JOURNAL_ENTRY: "Journal Entries",
  TRADE: "Trades",
  REFERRAL: "Referrals",
  PREMIUM_REFERRAL: "Premium Referrals",
  STREAK: "Daily Streak",
  COLLECTION_SIZE: "Collection Size",
  WISHLIST_SIZE: "Wishlist Size",
  FAVORITE_SPOTS: "Favorite Spots",
  LEVEL: "Player Level",
  FAMILY_COMPLETE: "Families Completed",
  TRIPS: "Trips Planned",
};

const allAchievements = achievementData as Achievement[];

/** Decode unicode escapes in emoji strings. */
const decodeEmoji = (s: string): string => {
  try {
    return s.replace(/\\u([0-9a-fA-F]{4})/g, (_, hex) =>
      String.fromCodePoint(parseInt(hex, 16)),
    );
  } catch {
    return s;
  }
};

const levelProgress = (xp: number, level: number) => {
  const current = xp - (level - 1) * 100;
  return Math.min(100, (current / 100) * 100);
};

export default function Achievements() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState<string>("ALL");

  // Fetch profile for level/XP
  const { data: profile } = useQuery<{
    level: number;
    xp: number;
  }>({
    queryKey: ["my-profile", user?.id],
    queryFn: async () => {
      if (!user) return { level: 1, xp: 0 };
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("level, xp")
        .eq("id", user.id)
        .maybeSingle();
      return (data as { level: number; xp: number }) ?? { level: 1, xp: 0 };
    },
    enabled: !!user,
  });

  // Fetch counts for progress calculation
  const { data: counts } = useQuery<AchievementState>({
    queryKey: ["achievement-counts", user?.id],
    queryFn: async () => {
      if (!user)
        return {
          identify: 0,
          collection: 0,
          wishlist: 0,
          favorite_spots: 0,
          trips: 0,
          journal: 0,
        };
      const [col, wish, fav, trips, journal] = await Promise.all([
        supabase
          .from("rockscout_collection")
          .select("id", { count: "exact", head: true })
          .eq("user_id", user.id),
        supabase
          .from("rockscout_wishlist")
          .select("id", { count: "exact", head: true })
          .eq("user_id", user.id),
        supabase
          .from("rockscout_favorite_spots")
          .select("id", { count: "exact", head: true })
          .eq("user_id", user.id),
        supabase
          .from("rockscout_trips")
          .select("id", { count: "exact", head: true })
          .eq("user_id", user.id),
        supabase
          .from("rockscout_field_journal")
          .select("id", { count: "exact", head: true })
          .eq("user_id", user.id),
      ]);
      return {
        identify: 0, // not tracked yet in PWA
        collection: col.count ?? 0,
        wishlist: wish.count ?? 0,
        favorite_spots: fav.count ?? 0,
        trips: trips.count ?? 0,
        journal: journal.count ?? 0,
      };
    },
    enabled: !!user,
  });

  /** Calculate progress for a given achievement metric. */
  const progressFor = (metric: string): number => {
    if (!counts) return 0;
    const map: Record<string, number> = {
      IDENTIFY: counts.identify,
      CAPTURE: 0, // not tracked in PWA yet
      WISHLIST_SIZE: counts.wishlist,
      COLLECTION_SIZE: counts.collection,
      FAVORITE_SPOTS: counts.favorite_spots,
      TRIPS: counts.trips,
      JOURNAL_ENTRY: counts.journal,
      LEVEL: profile?.level ?? 1,
    };
    return map[metric] ?? 0;
  };

  const filtered = useMemo(() => {
    let result = allAchievements;
    if (filter !== "ALL") {
      result = result.filter((a) => a.metric === filter);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(
        (a) =>
          a.name.toLowerCase().includes(q) ||
          a.description.toLowerCase().includes(q),
      );
    }
    return result;
  }, [search, filter]);

  const earnedCount = allAchievements.filter(
    (a) => progressFor(a.metric) >= a.threshold,
  ).length;

  const uniqueMetrics = [...new Set(allAchievements.map((a) => a.metric))];

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Award className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view achievements</p>
      </div>
    );
  }

  const level = profile?.level ?? 1;
  const xp = profile?.xp ?? 0;
  const progress = levelProgress(xp, level);

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Achievements
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {earnedCount} of {allAchievements.length} badges earned
        </p>
      </div>

      {/* Level card */}
      <div className="rounded-xl border border-border bg-gradient-to-br from-primary/10 to-card p-5">
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <div className="flex h-14 w-14 items-center justify-center rounded-full bg-primary/20 ring-2 ring-primary/30">
              <Trophy className="h-7 w-7 text-primary" />
            </div>
            <div>
              <p className="font-display text-2xl font-bold text-foreground">
                Level {level}
              </p>
              <p className="text-sm text-muted-foreground">{xp} XP earned</p>
            </div>
          </div>
          <div className="text-right">
            <p className="text-sm font-medium text-muted-foreground">
              {earnedCount}/{allAchievements.length}
            </p>
            <p className="text-xs text-muted-foreground">badges</p>
          </div>
        </div>
        {/* Level progress bar */}
        <div className="mt-4">
          <div className="mb-1 flex items-center justify-between text-xs text-muted-foreground">
            <span>Level {level}</span>
            <span>{100 - (xp - (level - 1) * 100)} XP to Level {level + 1}</span>
          </div>
          <div className="h-2.5 w-full overflow-hidden rounded-full bg-muted">
            <div
              className="h-full rounded-full bg-gradient-to-r from-primary to-amber-500 transition-all"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>
      </div>

      {/* Search & filter */}
      <div className="relative">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search achievements..."
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

      {/* Metric filter chips */}
      <div className="flex flex-wrap gap-2">
        <button
          onClick={() => setFilter("ALL")}
          className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
            filter === "ALL"
              ? "bg-primary text-primary-foreground"
              : "bg-muted text-muted-foreground hover:bg-muted/70"
          }`}
        >
          All
        </button>
        {uniqueMetrics.map((m) => (
          <button
            key={m}
            onClick={() => setFilter(m)}
            className={`rounded-full px-3 py-1 text-xs font-medium transition-colors ${
              filter === m
                ? "bg-primary text-primary-foreground"
                : "bg-muted text-muted-foreground hover:bg-muted/70"
            }`}
          >
            {METRIC_LABELS[m] ?? m}
          </button>
        ))}
      </div>

      {/* Achievement grid */}
      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
        {filtered.map((achievement) => {
          const current = progressFor(achievement.metric);
          const earned = current >= achievement.threshold;
          const pct = Math.min(100, (current / achievement.threshold) * 100);
          const emoji = decodeEmoji(achievement.emoji);
          return (
            <div
              key={achievement.id}
              className={`rounded-xl border p-4 transition-all ${
                earned
                  ? "border-primary/40 bg-primary/5"
                  : "border-border bg-card"
              }`}
            >
              <div className="flex items-start gap-3">
                <div
                  className={`flex h-12 w-12 shrink-0 items-center justify-center rounded-xl text-2xl ${
                    earned ? "bg-primary/15" : "bg-muted/50 grayscale"
                  }`}
                >
                  {emoji}
                </div>
                <div className="min-w-0 flex-1">
                  <h3 className="font-display text-sm font-semibold text-foreground">
                    {achievement.name}
                  </h3>
                  <p className="mt-0.5 text-xs text-muted-foreground">
                    {achievement.description}
                  </p>
                </div>
                {earned && (
                  <Star className="h-4 w-4 shrink-0 fill-primary text-primary" />
                )}
              </div>

              {/* Progress bar */}
              <div className="mt-3">
                <div className="mb-1 flex items-center justify-between text-xs">
                  <span className="text-muted-foreground">
                    {Math.min(current, achievement.threshold)}/
                    {achievement.threshold}
                  </span>
                  <span className="flex items-center gap-0.5 font-medium text-amber-500">
                    <Zap className="h-3 w-3" />
                    {achievement.rewardXp} XP
                  </span>
                </div>
                <div className="h-1.5 w-full overflow-hidden rounded-full bg-muted">
                  <div
                    className={`h-full rounded-full transition-all ${
                      earned
                        ? "bg-gradient-to-r from-primary to-amber-500"
                        : "bg-primary/60"
                    }`}
                    style={{ width: `${pct}%` }}
                  />
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
