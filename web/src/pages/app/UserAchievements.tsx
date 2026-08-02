import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { ArrowLeft, Loader2, X } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { SculptedCard, ScreenScaffold, TagChip } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";

interface UserProfile {
  id: string;
  display_name: string;
  avatar_emoji: string;
  xp: number;
  level: number;
}

interface Badge {
  id: string;
  name: string;
  emoji: string;
  description: string;
}

const BADGE_CATALOG: Badge[] = [
  { id: "first-find", name: "First Find", emoji: "🎯", description: "Identify your first rock or mineral" },
  { id: "collector", name: "Collector", emoji: "📦", description: "Add 10 specimens to your collection" },
  { id: "field-hunter", name: "Field Hunter", emoji: "📸", description: "Capture 25 field photos" },
  { id: "rock-hound", name: "Rock Hound", emoji: "🐕", description: "Identify 50 rocks total" },
  { id: "crystal-master", name: "Crystal Master", emoji: "💎", description: "Identify 10 different crystal specimens" },
  { id: "fossil-finder", name: "Fossil Finder", emoji: "🦴", description: "Find and identify your first fossil" },
  { id: "meteorite-man", name: "Meteorite Man", emoji: "☄️", description: "Identify a meteorite" },
  { id: "trade-master", name: "Trade Master", emoji: "🤝", description: "Complete 5 trades on the Trading Floor" },
  { id: "social-butterfly", name: "Social Butterfly", emoji: "🦋", description: "Make 10 RockScout friends" },
  { id: "trip-planner", name: "Trip Planner", emoji: "🗺️", description: "Plan and complete your first trip" },
  { id: "gem-expert", name: "Gem Expert", emoji: "✨", description: "Identify 25 different gem-quality specimens" },
  { id: "level-10", name: "Seasoned Hunter", emoji: "⭐", description: "Reach Level 10" },
  { id: "level-25", name: "Expert Hunter", emoji: "🌟", description: "Reach Level 25" },
  { id: "level-50", name: "Master Hunter", emoji: "🏆", description: "Reach Level 50" },
  { id: "100-captures", name: "Century Club", emoji: "💯", description: "Take 100 field captures" },
];

const TIER_INFO: { minLevel: number; name: string; emoji: string }[] = [
  { minLevel: 1, name: "Stone Seeker", emoji: "🪨" },
  { minLevel: 5, name: "Pebble Pathfinder", emoji: "🟤" },
  { minLevel: 10, name: "Crystal Hunter", emoji: "🔮" },
  { minLevel: 20, name: "Gem Explorer", emoji: "💎" },
  { minLevel: 35, name: "Rock Sage", emoji: "⛰️" },
  { minLevel: 50, name: "Mineral Master", emoji: "👑" },
];

function getTier(level: number) {
  let tier = TIER_INFO[0];
  for (const t of TIER_INFO) {
    if (level >= t.minLevel) tier = t;
  }
  return tier;
}

function xpForLevel(level: number): number {
  return level * 100;
}

function levelForXp(xp: number): number {
  return Math.floor(Math.sqrt(xp / 10)) + 1;
}

export default function UserAchievements() {
  const { id: userId } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [selectedBadge, setSelectedBadge] = useState<Badge | null>(null);

  const { data: profile, isLoading } = useQuery<UserProfile>({
    queryKey: ["user-profile", userId],
    queryFn: async () => {
      if (!userId) throw new Error("No user ID");
      const { data, error } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, xp, level")
        .eq("id", userId)
        .maybeSingle();
      if (error) throw error;
      if (!data) throw new Error("User not found");
      return data as UserProfile;
    },
    enabled: !!userId,
  });

  if (isLoading) {
    return (
      <ScreenScaffold title="Achievements" onBack={() => window.history.back()}>
        <div className="flex justify-center py-16">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      </ScreenScaffold>
    );
  }

  if (!profile) {
    return (
      <ScreenScaffold title="Achievements" onBack={() => window.history.back()}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <p className="text-muted-foreground">User not found</p>
        </div>
      </ScreenScaffold>
    );
  }

  const level = profile.level || levelForXp(profile.xp || 0);
  const tier = getTier(level);
  const earnedBadgeIds = new Set(
    BADGE_CATALOG.filter((b) => {
      if (b.id === "level-10") return level >= 10;
      if (b.id === "level-25") return level >= 25;
      if (b.id === "level-50") return level >= 50;
      return false;
    }).map((b) => b.id),
  );

  const progress = ((profile.xp || 0) - xpForLevel(level)) / (xpForLevel(level + 1) - xpForLevel(level));

  return (
    <ScreenScaffold title={`${profile.display_name}'s Achievements`} onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        {/* Gamer header */}
        <SculptedCard accent="citrine" className="overflow-hidden p-5">
          <div className="flex items-center gap-4">
            <div
              className="glowing-border flex h-16 w-16 shrink-0 items-center justify-center rounded-full text-3xl"
              style={{ ["--glow-color" as string]: CITRINE_HEX }}
            >
              {profile.avatar_emoji || "🪨"}
            </div>
            <div className="min-w-0 flex-1">
              <h3 className="truncate font-display text-lg font-bold text-foreground">
                {profile.display_name}
              </h3>
              <div className="mt-1 flex items-center gap-2">
                <TagChip accent={`hsl(${CITRINE_HEX})`}>
                  {tier.emoji} {tier.name}
                </TagChip>
              </div>
            </div>
            <div className="text-right">
              <p className="text-xs font-bold text-muted-foreground">LV</p>
              <p className="font-display text-3xl font-extrabold" style={{ color: `hsl(${CITRINE_HEX})` }}>
                {level}
              </p>
            </div>
          </div>

          {/* XP bar */}
          <div className="mt-4">
            <div className="flex items-center justify-between text-xs">
              <span className="text-muted-foreground">{profile.xp || 0} XP</span>
              <span className="text-muted-foreground">Level {level + 1}</span>
            </div>
            <div className="mt-1.5 h-2.5 overflow-hidden rounded-full bg-muted/30">
              <div
                className="h-full rounded-full transition-all"
                style={{
                  width: `${Math.min(progress * 100, 100)}%`,
                  background: `linear-gradient(to right, hsl(${CITRINE_HEX}), hsl(${AQUA_HEX}))`,
                }}
              />
            </div>
          </div>
        </SculptedCard>

        {/* Stat pills */}
        <div className="grid grid-cols-4 gap-2">
          <StatPill label="Identified" value="—" accent={CITRINE_HEX} />
          <StatPill label="Captured" value="—" accent={AQUA_HEX} />
          <StatPill label="Badges" value={String(earnedBadgeIds.size)} accent="147 49% 55%" />
          <StatPill label="XP" value={String(profile.xp || 0)} accent="265 47% 67%" />
        </div>

        {/* Badge catalog header */}
        <div className="flex items-center gap-2">
          <div className="rounded-lg bg-primary/15 px-3 py-1 ring-1 ring-primary/30">
            <span className="text-sm font-extrabold" style={{ color: `hsl(${CITRINE_HEX})` }}>
              BADGE CATALOG
            </span>
          </div>
        </div>
        <p className="text-xs text-muted-foreground">
          Badge progress is approximate — shown from public level data.
        </p>

        {/* Badge grid */}
        <div className="grid grid-cols-3 gap-3">
          {BADGE_CATALOG.map((badge) => {
            const earned = earnedBadgeIds.has(badge.id);
            return (
              <button
                key={badge.id}
                onClick={() => setSelectedBadge(badge)}
                className={`flex flex-col items-center gap-2 rounded-xl border p-3 transition-all hover:scale-105 ${
                  earned
                    ? "border-primary/40 bg-primary/10"
                    : "border-border bg-muted/20 opacity-60"
                }`}
              >
                <span className="text-3xl" style={{ filter: earned ? "none" : "grayscale(1)" }}>
                  {badge.emoji}
                </span>
                <span className="text-center text-[10px] font-bold leading-tight text-foreground">
                  {badge.name}
                </span>
                <span
                  className="rounded-full px-2 py-0.5 text-[8px] font-bold"
                  style={{
                    backgroundColor: earned ? `hsl(${CITRINE_HEX} / 0.15)` : "hsl(var(--muted))",
                    color: earned ? `hsl(${CITRINE_HEX})` : "hsl(var(--muted-foreground))",
                  }}
                >
                  {earned ? "EARNED" : "LOCKED"}
                </span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Badge detail overlay */}
      {selectedBadge && (
        <div
          className="fixed inset-0 z-[80] flex items-center justify-center bg-black/70 p-4 backdrop-blur-sm"
          onClick={() => setSelectedBadge(null)}
        >
          <div
            className="dark-card sculpted-raised w-full max-w-sm rounded-2xl p-6 text-center"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={() => setSelectedBadge(null)}
              className="absolute right-4 top-4 text-muted-foreground"
            >
              <X className="h-5 w-5" />
            </button>
            <span className="text-5xl">{selectedBadge.emoji}</span>
            <h3 className="mt-3 font-display text-xl font-bold text-foreground">
              {selectedBadge.name}
            </h3>
            <div
              className="mt-2 inline-block rounded-lg px-3 py-1 text-xs font-bold"
              style={{
                backgroundColor: earnedBadgeIds.has(selectedBadge.id)
                  ? `hsl(${CITRINE_HEX} / 0.15)`
                  : "hsl(var(--muted) / 0.3)",
                color: earnedBadgeIds.has(selectedBadge.id)
                  ? `hsl(${CITRINE_HEX})`
                  : "hsl(var(--muted-foreground))",
              }}
            >
              {earnedBadgeIds.has(selectedBadge.id) ? "EARNED" : "LOCKED"}
            </div>
            <p className="mt-3 text-sm text-[hsl(var(--text-mid))]">
              {selectedBadge.description}
            </p>
            <p className="mt-4 text-xs text-muted-foreground">Tap anywhere to close</p>
          </div>
        </div>
      )}
    </ScreenScaffold>
  );
}

function StatPill({ label, value, accent }: { label: string; value: string; accent: string }) {
  return (
    <div
      className="flex flex-col items-center gap-0.5 rounded-xl border p-2 text-center"
      style={{
        borderColor: `hsl(${accent} / 0.3)`,
        backgroundColor: `hsl(${accent} / 0.08)`,
      }}
    >
      <span className="font-display text-lg font-bold" style={{ color: `hsl(${accent})` }}>
        {value}
      </span>
      <span className="text-[10px] font-medium text-muted-foreground">{label}</span>
    </div>
  );
}
