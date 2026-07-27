import { useQuery } from "@tanstack/react-query";
import { MessageCircle, Loader2, Send } from "lucide-react";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";

interface Thread {
  id: string;
  other_user_name: string;
  other_user_emoji: string;
  last_message: string;
  last_at: string;
}

export default function Messenger() {
  const { user } = useAuth();

  const { data: threads, isLoading } = useQuery<Thread[]>({
    queryKey: ["messenger-threads", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_threads")
        .select("*")
        .or(`user_a.eq.${user.id},user_b.eq.${user.id}`)
        .order("last_at", { ascending: false });
      if (error) throw error;
      return (data ?? []) as Thread[];
    },
    enabled: !!user,
  });

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <MessageCircle className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to view messages</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">Messages</h1>
        <p className="mt-0.5 text-sm text-muted-foreground">Your conversation threads</p>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : threads && threads.length > 0 ? (
        <div className="space-y-2">
          {threads.map((thread) => (
            <div key={thread.id} className="flex items-center gap-3 rounded-xl border border-border bg-card p-3">
              <span className="text-2xl">{thread.other_user_emoji ?? "🧗"}</span>
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-semibold text-foreground">{thread.other_user_name}</p>
                <p className="truncate text-xs text-muted-foreground">{thread.last_message}</p>
              </div>
              <Send className="h-4 w-4 text-muted-foreground" />
            </div>
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
          <MessageCircle className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">No conversations yet. Add friends to start messaging!</p>
        </div>
      )}
    </div>
  );
}
