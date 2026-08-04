import { useParams, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  ArrowLeft,
  UserPlus,
  MessageSquare,
  Loader2,
  Check,
  X,
  Save,
  Gem,
  Heart,
  Award,
  MapPin,
  Crown,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { filterProfanity } from "@/lib/profanity-filter";
import { ensureUniqueUsername } from "@/lib/username-resolver";

interface Profile {
  id: string;
  display_name: string;
  avatar_emoji: string;
  status: string;
  level: number;
  xp: number;
  is_pro: boolean;
  home_region: string;
  bio: string;
}

interface CollectionCount {
  collection_count: number;
  wishlist_count: number;
  favorite_spots_count: number;
}

const AVATAR_OPTIONS = ["💎", "⛏️", "🏔️", "🪨", "🔮", "🌍", "🦴", "🦈", "🌋", "⭐"];

const LEVEL_XP = (level: number) => level * 100;
const levelProgress = (xp: number, level: number) => {
  const current = xp - (level - 1) * 100;
  const needed = 100;
  return Math.min(100, (current / needed) * 100);
};

export default function UserProfile() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [showEditor, setShowEditor] = useState(false);
  const [form, setForm] = useState({
    display_name: "",
    avatar_emoji: "💎",
    home_region: "",
    bio: "",
  });

  const isOwnProfile = !id || id === user?.id;
  const profileId = id ?? user?.id;

  const { data: profile, isLoading } = useQuery<Profile>({
    queryKey: ["profile", profileId],
    queryFn: async () => {
      const { data, error } = await supabase
        .from("rockscout_profiles")
        .select("*")
        .eq("id", profileId)
        .maybeSingle();
      if (error) throw error;
      if (!data) {
        // Auto-create own profile if missing
        if (isOwnProfile && user) {
          const { data: created, error: createErr } = await supabase
            .from("rockscout_profiles")
            .insert({
              id: user.id,
              display_name: filterProfanity(user.email?.split("@")[0] ?? "Rockhound").filteredText,
            })
            .select("*")
            .single();
          if (createErr) throw createErr;
          return created as Profile;
        }
        throw new Error("Profile not found");
      }
      return data as Profile;
    },
    enabled: !!profileId,
  });

  const { data: counts } = useQuery<CollectionCount>({
    queryKey: ["profile-counts", profileId],
    queryFn: async () => {
      if (!profileId) return { collection_count: 0, wishlist_count: 0, favorite_spots_count: 0 };
      const [col, wish, fav] = await Promise.all([
        supabase
          .from("rockscout_collection")
          .select("id", { count: "exact", head: true })
          .eq("user_id", profileId),
        supabase
          .from("rockscout_wishlist")
          .select("id", { count: "exact", head: true })
          .eq("user_id", profileId),
        supabase
          .from("rockscout_favorite_spots")
          .select("id", { count: "exact", head: true })
          .eq("user_id", profileId),
      ]);
      return {
        collection_count: col.count ?? 0,
        wishlist_count: wish.count ?? 0,
        favorite_spots_count: fav.count ?? 0,
      };
    },
    enabled: !!profileId,
  });

  const { data: isFriend } = useQuery<boolean>({
    queryKey: ["is-friend", user?.id, profileId],
    queryFn: async () => {
      if (!user || !profileId || isOwnProfile) return false;
      const { data } = await supabase
        .from("rockscout_connections")
        .select("id, user_a, user_b")
        .or(`user_a.eq.${user.id},user_b.eq.${user.id}`)
        .limit(5);
      const rows = (data ?? []) as unknown as Array<{
        id: string;
        user_a: string;
        user_b: string;
      }>;
      return rows.some(
        (r) =>
          (r.user_a === user.id && r.user_b === profileId) ||
          (r.user_b === user.id && r.user_a === profileId),
      );
    },
    enabled: !!user && !!profileId && !isOwnProfile,
  });

  const saveProfile = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to edit your profile");
      const uniqueName = await ensureUniqueUsername(filterProfanity(form.display_name).filteredText, user.id);
      const { error } = await supabase
        .from("rockscout_profiles")
        .update({
          display_name: uniqueName,
          avatar_emoji: form.avatar_emoji,
          home_region: form.home_region,
          bio: filterProfanity(form.bio).filteredText,
        })
        .eq("id", user.id);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Profile updated");
      queryClient.invalidateQueries({ queryKey: ["profile", user?.id] });
      setShowEditor(false);
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to update profile"),
  });

  const sendFriendRequest = useMutation({
    mutationFn: async () => {
      if (!user || !profileId) throw new Error("Not signed in");
      const { error } = await supabase
        .from("rockscout_friend_requests")
        .insert({ sender_id: user.id, recipient_id: profileId });
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Friend request sent");
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to send request"),
  });

  const handleEdit = () => {
    if (profile) {
      setForm({
        display_name: profile.display_name,
        avatar_emoji: profile.avatar_emoji,
        home_region: profile.home_region,
        bio: profile.bio,
      });
      setShowEditor(true);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <Loader2 className="h-6 w-6 animate-spin text-primary" />
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="space-y-4">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate(-1)}
          className="gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Back
        </Button>
        <p className="text-sm text-muted-foreground">Profile not found.</p>
      </div>
    );
  }

  const progress = levelProgress(profile.xp, profile.level);

  return (
    <div className="space-y-5">
      {!isOwnProfile && (
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate(-1)}
          className="gap-2"
        >
          <ArrowLeft className="h-4 w-4" />
          Back
        </Button>
      )}

      {/* Profile header */}
      <div className="flex flex-col items-center gap-4 dark-card sculpted-raised rounded-xl p-6 text-center sm:flex-row sm:text-left">
        <div className="flex h-20 w-20 shrink-0 items-center justify-center rounded-full bg-primary/10 text-4xl">
          {profile.avatar_emoji}
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex items-center justify-center gap-2 sm:justify-start">
            <h1 className="font-display text-xl font-bold text-foreground md:text-2xl">
              {profile.display_name || "Anonymous Rockhound"}
            </h1>
            {profile.is_pro && (
              <span className="pro-badge-gold inline-flex items-center gap-1 rounded-md bg-gradient-to-br from-amber-300 via-yellow-500 to-amber-600 px-2 py-0.5 text-xs font-black tracking-wide text-amber-950 shadow-[0_0_8px_rgba(251,191,36,0.5)]">
                <Crown className="h-3 w-3" />
                PRO
              </span>
            )}
          </div>
          {profile.home_region && (
            <p className="mt-1 flex items-center justify-center gap-1 text-sm text-muted-foreground sm:justify-start">
              <MapPin className="h-3.5 w-3.5" />
              {profile.home_region}
            </p>
          )}
          {profile.bio && (
            <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
              {profile.bio}
            </p>
          )}
          <div className="mt-3 flex items-center justify-center gap-2 sm:justify-start">
            <span className="rounded-full bg-primary/15 px-2.5 py-0.5 text-xs font-semibold text-primary">
              Level {profile.level}
            </span>
            <span className="text-xs text-muted-foreground">
              {profile.xp} XP
            </span>
          </div>
          {/* Level progress bar */}
          <div className="mt-2 h-2 w-full max-w-xs overflow-hidden rounded-full bg-muted">
            <div
              className="h-full rounded-full bg-primary transition-all"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>

        {/* Action buttons */}
        <div className="flex shrink-0 flex-col gap-2">
          {isOwnProfile ? (
            <Button size="sm" onClick={handleEdit} className="gap-2">
              <Save className="h-4 w-4" />
              Edit profile
            </Button>
          ) : isFriend ? (
            <Button
              size="sm"
              variant="outline"
              onClick={() => navigate("/app/friends")}
              className="gap-2"
            >
              <MessageSquare className="h-4 w-4" />
              Message
            </Button>
          ) : (
            <Button
              size="sm"
              onClick={() => sendFriendRequest.mutate()}
              disabled={sendFriendRequest.isPending}
              className="gap-2"
            >
              <UserPlus className="h-4 w-4" />
              Add friend
            </Button>
          )}
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-3">
        <div className="dark-card sculpted-raised rounded-xl p-4 text-center">
          <Gem className="mx-auto mb-1 h-5 w-5 text-primary" />
          <p className="font-display text-xl font-bold text-foreground">
            {counts?.collection_count ?? 0}
          </p>
          <p className="text-xs text-muted-foreground">Collection</p>
        </div>
        <div className="dark-card sculpted-raised rounded-xl p-4 text-center">
          <Heart className="mx-auto mb-1 h-5 w-5 text-primary" />
          <p className="font-display text-xl font-bold text-foreground">
            {counts?.wishlist_count ?? 0}
          </p>
          <p className="text-xs text-muted-foreground">Wishlist</p>
        </div>
        <div className="dark-card sculpted-raised rounded-xl p-4 text-center">
          <MapPin className="mx-auto mb-1 h-5 w-5 text-primary" />
          <p className="font-display text-xl font-bold text-foreground">
            {counts?.favorite_spots_count ?? 0}
          </p>
          <p className="text-xs text-muted-foreground">Spots</p>
        </div>
      </div>

      {/* Achievements quick link (own profile) */}
      {isOwnProfile && (
        <button
          onClick={() => navigate("/app/achievements")}
          className="flex w-full items-center gap-3 dark-card sculpted-raised rounded-xl p-4 text-left transition-colors hover:border-primary/40"
        >
          <div className="flex h-11 w-11 items-center justify-center rounded-lg bg-primary/15">
            <Award className="h-5 w-5 text-primary" />
          </div>
          <div className="flex-1">
            <h3 className="font-display text-sm font-semibold text-foreground">
              Achievements
            </h3>
            <p className="text-xs text-muted-foreground">
              View your badges and level progress
            </p>
          </div>
          <ArrowLeft className="h-4 w-4 rotate-180 text-muted-foreground" />
        </button>
      )}

      {/* Profile editor */}
      <Dialog open={showEditor} onOpenChange={setShowEditor}>
        <DialogContent aria-describedby={undefined} className="max-w-md">
          <DialogHeader>
            <DialogTitle>Edit your profile</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label>Avatar emoji</Label>
              <div className="flex flex-wrap gap-2">
                {AVATAR_OPTIONS.map((emoji) => (
                  <button
                    key={emoji}
                    onClick={() => setForm((f) => ({ ...f, avatar_emoji: emoji }))}
                    className={`flex h-10 w-10 items-center justify-center rounded-lg text-xl transition-all ${
                      form.avatar_emoji === emoji
                        ? "bg-primary/20 ring-2 ring-primary"
                        : "bg-muted hover:bg-muted/70"
                    }`}
                  >
                    {emoji}
                  </button>
                ))}
              </div>
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="display-name">Display name</Label>
              <Input
                id="display-name"
                value={form.display_name}
                onChange={(e) =>
                  setForm((f) => ({ ...f, display_name: e.target.value }))
                }
                placeholder="Your rockhound name"
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="home-region">Home region</Label>
              <Input
                id="home-region"
                value={form.home_region}
                onChange={(e) =>
                  setForm((f) => ({ ...f, home_region: e.target.value }))
                }
                placeholder="e.g. Tucson, Arizona"
              />
            </div>

            <div className="space-y-1.5">
              <Label htmlFor="bio">Bio</Label>
              <Textarea
                id="bio"
                value={form.bio}
                onChange={(e) => setForm((f) => ({ ...f, bio: e.target.value }))}
                placeholder="Tell other rockhounds about yourself..."
                rows={3}
              />
            </div>
          </div>
          <DialogFooter className="gap-2">
            <Button variant="outline" onClick={() => setShowEditor(false)}>
              Cancel
            </Button>
            <Button
              onClick={() => saveProfile.mutate()}
              disabled={saveProfile.isPending}
            >
              {saveProfile.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                "Save"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
