import { useNavigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { Users, Loader2 } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface Connection {
  id: string;
  user_a: string;
  user_b: string;
}

interface Profile {
  id: string;
  display_name: string;
  avatar_emoji: string;
  level: number;
  is_pro: boolean;
}

export default function ProfileFriends() {
  const { user } = useAuth();
  const navigate = useNavigate();

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

  const friendIds = connections.flatMap((c) => [c.user_a, c.user_b]).filter((id) => id !== user?.id);

  const { data: friends, isLoading } = useQuery<Profile[]>({
    queryKey: ["friends-profiles", friendIds],
    queryFn: async () => {
      if (friendIds.length === 0) return [];
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, level, is_pro")
        .in("id", friendIds)
        .order("level", { ascending: false });
      return (data ?? []) as Profile[];
    },
    enabled: friendIds.length > 0,
  });

  if (!user) {
    return (
      <ScreenScaffold title="Friends" onBack={() => window.history.back()}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <Users className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to view friends</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="Friends" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          {friends ? `${friends.length} connections` : "Loading..."}
        </p>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        ) : friends && friends.length > 0 ? (
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {friends.map((friend) => (
              <SculptedCard
                key={friend.id}
                accent="aqua"
                interactive
                className="overflow-hidden"
                onClick={() => navigate(`/app/profile/${friend.id}`)}
              >
                <div className="flex items-center gap-3 p-3.5">
                  <div
                    className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-full text-xl"
                    style={{ ["--glow-color" as string]: AQUA_HEX }}
                  >
                    {friend.avatar_emoji ?? "🧗"}
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-1.5">
                      <p className="truncate text-sm font-bold text-foreground">{friend.display_name}</p>
                      {friend.is_pro && (
                        <span className="text-xs" style={{ color: `hsl(${CITRINE_HEX})` }}>★</span>
                      )}
                    </div>
                    <p className="text-xs text-muted-foreground">Level {friend.level ?? 1}</p>
                  </div>
                </div>
              </SculptedCard>
            ))}
          </div>
        ) : (
          <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <Users className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">No friends yet. Discover hunters to connect!</p>
            <SculptedButton accent="citrine" size="sm" onClick={() => navigate("/app/discover-hunters")}>
              <Users className="h-4 w-4" />
              Discover Hunters
            </SculptedButton>
          </SculptedCard>
        )}
      </div>
    </ScreenScaffold>
  );
}
