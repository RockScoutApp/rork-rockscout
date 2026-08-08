import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Users,
  UserPlus,
  Search,
  Loader2,
  Check,
  X,
  MapPin,
  Navigation,
  Gift,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold, StatTile } from "@/components/sculpted";
import { UserAvatar } from "@/components/app/UserAvatar";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const CYAN_HEX = "174 100% 45%";
const AMETHYST_HEX = "265 47% 67%";
const SUCCESS_HEX = "147 49% 55%";

interface Profile {
  id: string;
  display_name: string;
  avatar_emoji: string;
  avatar_image_path?: string | null;
  level: number;
  is_pro: boolean;
  coarse_lat?: number | null;
  coarse_lng?: number | null;
}

interface FriendRequest {
  id: string;
  sender_id: string;
  status: string;
  created_at: string;
  sender?: Profile;
}

interface Connection {
  id: string;
  user_a: string;
  user_b: string;
}

export default function DiscoverHunters() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [search, setSearch] = useState("");
  const [tab, setTab] = useState<"discover" | "requests" | "friends">("discover");

  // ── Connection IDs ──
  const { data: connections = [] } = useQuery<Connection[]>({
    queryKey: ["connections", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data } = await supabase
        .from("rockscout_connections")
        .select("*")
        .or(`user_a.eq.${user.id},user_b.eq.${user.id}`);
      return (data ?? []) as Connection[];
    },
    enabled: !!user,
  });

  const connectionIds = new Set(
    connections.flatMap((c) => [c.user_a, c.user_b]).filter((id) => id !== user?.id),
  );

  // ── Incoming friend requests ──
  const { data: requests = [] } = useQuery<FriendRequest[]>({
    queryKey: ["friend-requests", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data } = await supabase
        .from("rockscout_friend_requests")
        .select("*")
        .eq("recipient_id", user.id)
        .eq("status", "pending")
        .order("created_at", { ascending: false });
      const rows = (data ?? []) as FriendRequest[];
      if (rows.length === 0) return [];

      const senderIds = rows.map((r) => r.sender_id);
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, avatar_image_path, level, is_pro")
        .in("id", senderIds);
      const profileMap = new Map((profiles ?? []).map((p) => [p.id, p]));
      return rows.map((r) => ({ ...r, sender: profileMap.get(r.sender_id) }));
    },
    enabled: !!user,
  });

  // ── Discover hunters (all profiles except self & connections) ──
  const { data: hunters, isLoading } = useQuery<Profile[]>({
    queryKey: ["discover-hunters", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, avatar_image_path, level, is_pro, coarse_lat, coarse_lng")
        .neq("id", user.id)
        .order("level", { ascending: false })
        .limit(50);
      return (data ?? []) as Profile[];
    },
    enabled: !!user && tab === "discover",
  });

  // ── Friends list ──
  const { data: friends = [] } = useQuery<Profile[]>({
    queryKey: ["friends-list", user?.id, connectionIds.size],
    queryFn: async () => {
      const ids = [...connectionIds];
      if (ids.length === 0) return [];
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, avatar_image_path, level, is_pro, coarse_lat, coarse_lng")
        .in("id", ids)
        .order("level", { ascending: false });
      return (data ?? []) as Profile[];
    },
    enabled: connectionIds.size > 0 && tab === "friends",
  });

  // ── Send friend request ──
  const sendRequest = useMutation({
    mutationFn: async (targetId: string) => {
      if (!user) throw new Error("Sign in first");
      const { error } = await supabase
        .from("rockscout_friend_requests")
        .insert({
          sender_id: user.id,
          recipient_id: targetId,
          status: "pending",
        });
      if (error) {
        if (error.code === "23505") throw new Error("Request already sent");
        throw error;
      }
    },
    onSuccess: () => toast.success("Friend request sent!"),
    onError: (err: Error) => toast.error(err.message),
  });

  // ── Accept / decline request ──
  const respondRequest = useMutation({
    mutationFn: async ({ requestId, accept }: { requestId: string; accept: boolean }) => {
      if (!user) throw new Error("Sign in first");
      const { error: updateError } = await supabase
        .from("rockscout_friend_requests")
        .update({
          status: accept ? "accepted" : "denied",
          responded_at: new Date().toISOString(),
        })
        .eq("id", requestId);
      if (updateError) throw updateError;

      if (accept) {
        const req = requests.find((r) => r.id === requestId);
        if (req) {
          const [a, b] = [user.id, req.sender_id].sort();
          const { error: connError } = await supabase
            .from("rockscout_connections")
            .insert({ user_a: a, user_b: b });
          if (connError && connError.code !== "23505") throw connError;
        }
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["friend-requests"] });
      queryClient.invalidateQueries({ queryKey: ["connections"] });
      queryClient.invalidateQueries({ queryKey: ["friends-list"] });
      toast.success("Request handled");
    },
    onError: (err: Error) => toast.error(err.message),
  });

  if (!user) {
    return (
      <ScreenScaffold title="Discover Hunters">
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <Users className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to discover hunters</p>
        </div>
      </ScreenScaffold>
    );
  }

  const filteredHunters = (hunters ?? []).filter((h) => {
    if (!search) return true;
    return h.display_name?.toLowerCase().includes(search.toLowerCase());
  });

  const pendingRequestIds = new Set<string>();

  return (
    <ScreenScaffold title="Discover Hunters">
      <div className="space-y-5 px-4 pb-8">
        {/* Stats */}
        <div className="grid grid-cols-2 gap-3">
          <StatTile label="Connections" value={connectionIds.size} accent="aqua" icon={<Users className="h-4 w-4" />} />
          <StatTile label="Requests" value={requests.length} accent="citrine" icon={<UserPlus className="h-4 w-4" />} />
        </div>

        {/* Tab selector */}
        <div className="flex gap-2">
          {([
            { key: "discover", label: "Discover", icon: Gift },
            { key: "requests", label: `Requests${requests.length > 0 ? ` (${requests.length})` : ""}`, icon: UserPlus },
            { key: "friends", label: `Friends (${connectionIds.size})`, icon: Users },
          ] as const).map((t) => (
            <button
              key={t.key}
              onClick={() => setTab(t.key)}
              className={`flex items-center gap-1.5 rounded-full px-4 py-2 text-xs font-bold transition-all ${
                tab === t.key
                  ? "bg-primary/15 text-primary ring-1 ring-primary/40"
                  : "border border-border text-muted-foreground hover:text-foreground"
              }`}
            >
              <t.icon className="h-3.5 w-3.5" />
              {t.label}
            </button>
          ))}
        </div>

        {/* Search (discover tab only) */}
        {tab === "discover" && (
          <div className="relative">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by name…"
              className="pl-9"
            />
          </div>
        )}

        {/* Content */}
        {tab === "discover" && (
          isLoading ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin text-primary" />
            </div>
          ) : filteredHunters.length > 0 ? (
            <div className="grid gap-3 sm:grid-cols-2">
              {filteredHunters.map((hunter) => {
                const isFriend = connectionIds.has(hunter.id);
                const pending = pendingRequestIds.has(hunter.id);
                return (
                  <SculptedCard key={hunter.id} accent="aqua" className="p-3.5">
                    <div className="flex items-center gap-3">
                      <UserAvatar
                        imagePath={hunter.avatar_image_path ?? null}
                        displayName={hunter.display_name || "Unknown hunter"}
                        size="md"
                        showName={false}
                      />
                      <div className="min-w-0 flex-1">
                        <div className="flex items-center gap-1.5">
                          <p className="truncate text-sm font-bold text-foreground">
                            {hunter.display_name || "Unknown hunter"}
                          </p>
                          {hunter.is_pro && (
                            <span className="text-xs" style={{ color: `hsl(${CITRINE_HEX})` }}>★</span>
                          )}
                        </div>
                        <p className="text-xs text-muted-foreground">
                          Level {hunter.level ?? 1}
                        </p>
                        {hunter.coarse_lat != null && (
                          <p className="mt-0.5 flex items-center gap-1 text-[10px] text-muted-foreground">
                            <MapPin className="h-2.5 w-2.5" />
                            Location shared
                          </p>
                        )}
                      </div>
                      {isFriend ? (
                        <span
                          className="flex items-center gap-1 rounded-full px-2.5 py-1 text-xs font-bold"
                          style={{ backgroundColor: `hsl(${SUCCESS_HEX} / 0.15)`, color: `hsl(${SUCCESS_HEX})` }}
                        >
                          <Check className="h-3 w-3" />
                          Friend
                        </span>
                      ) : (
                        <SculptedButton
                          accent="citrine"
                          size="sm"
                          disabled={pending}
                          onClick={() => {
                            sendRequest.mutate(hunter.id);
                            pendingRequestIds.add(hunter.id);
                          }}
                        >
                          <UserPlus className="h-3 w-3" />
                          {pending ? "Sent" : "Add"}
                        </SculptedButton>
                      )}
                    </div>
                  </SculptedCard>
                );
              })}
            </div>
          ) : (
            <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
              <Users className="h-10 w-10 text-muted-foreground" />
              <p className="text-sm text-muted-foreground">
                {search ? "No hunters match your search" : "No other hunters found yet"}
              </p>
            </SculptedCard>
          )
        )}

        {/* Requests tab */}
        {tab === "requests" && (
          requests.length > 0 ? (
            <div className="space-y-3">
              {requests.map((req) => (
                <SculptedCard key={req.id} accent="citrine" className="p-3.5">
                  <div className="flex items-center gap-3">
                    <UserAvatar
                      imagePath={req.sender?.avatar_image_path ?? null}
                      displayName={req.sender?.display_name ?? "Unknown hunter"}
                      size="md"
                      showName={false}
                    />
                    <div className="min-w-0 flex-1">
                      <p className="truncate text-sm font-bold text-foreground">
                        {req.sender?.display_name ?? "Unknown hunter"}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        Level {req.sender?.level ?? 1} · Wants to connect
                      </p>
                    </div>
                    <button
                      onClick={() => respondRequest.mutate({ requestId: req.id, accept: true })}
                      disabled={respondRequest.isPending}
                      className="sculpted-button sculpted-raised dark-card flex h-9 w-9 items-center justify-center rounded-lg"
                      style={{ ["--sculpted-accent" as string]: SUCCESS_HEX, color: `hsl(${SUCCESS_HEX})` }}
                    >
                      <Check className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => respondRequest.mutate({ requestId: req.id, accept: false })}
                      disabled={respondRequest.isPending}
                      className="sculpted-button sculpted-raised dark-card flex h-9 w-9 items-center justify-center rounded-lg"
                      style={{ ["--sculpted-accent" as string]: "4 70% 55%", color: "hsl(4 70% 55%)" }}
                    >
                      <X className="h-4 w-4" />
                    </button>
                  </div>
                </SculptedCard>
              ))}
            </div>
          ) : (
            <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
              <UserPlus className="h-10 w-10 text-muted-foreground" />
              <p className="text-sm text-muted-foreground">No pending friend requests</p>
            </SculptedCard>
          )
        )}

        {/* Friends tab */}
        {tab === "friends" && (
          friends.length > 0 ? (
            <div className="grid gap-3 sm:grid-cols-2">
              {friends.map((friend) => (
                <SculptedCard key={friend.id} accent="success" className="p-3.5">
                  <div className="flex items-center gap-3">
                    <UserAvatar
                      imagePath={friend.avatar_image_path ?? null}
                      displayName={friend.display_name ?? "Unknown hunter"}
                      size="md"
                      showName={false}
                    />
                    <div className="min-w-0 flex-1">
                      <p className="truncate text-sm font-bold text-foreground">
                        {friend.display_name ?? "Unknown hunter"}
                      </p>
                      <p className="text-xs text-muted-foreground">Level {friend.level ?? 1}</p>
                    </div>
                  </div>
                </SculptedCard>
              ))}
            </div>
          ) : (
            <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
              <Users className="h-10 w-10 text-muted-foreground" />
              <p className="text-sm text-muted-foreground">
                No connections yet. Discover hunters and send friend requests!
              </p>
            </SculptedCard>
          )
        )}
      </div>
    </ScreenScaffold>
  );
}
