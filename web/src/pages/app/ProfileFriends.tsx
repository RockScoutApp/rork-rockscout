import { useQuery } from "@tanstack/react-query";
import { Users, Loader2, MapPin } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";

interface Friend {
  id: string;
  display_name: string;
  avatar_emoji: string;
  status: string;
}

export default function ProfileFriends() {
  const { user } = useAuth();

  const { data: friends, isLoading } = useQuery<Friend[]>({
    queryKey: ["profile-friends", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_connections")
        .select("id, display_name, avatar_emoji, status")
        .eq("user_id", user.id)
        .eq("status", "accepted")
        .order("display_name");
      if (error) throw error;
      return (data ?? []) as Friend[];
    },
    enabled: !!user,
  });

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Users className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view friends</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">Friends</h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          {friends ? `${friends.length} connections` : "Loading..."}
        </p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : friends && friends.length > 0 ? (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {friends.map((friend) => (
            <div key={friend.id} className="flex items-center gap-3 rounded-xl border border-border bg-card p-3">
              <span className="text-2xl">{friend.avatar_emoji ?? "🧗"}</span>
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-semibold text-foreground">{friend.display_name}</p>
                <p className="text-xs text-muted-foreground">{friend.status}</p>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <Users className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No friends yet. Discover hunters to connect!</p>
        </div>
      )}
    </div>
  );
}
