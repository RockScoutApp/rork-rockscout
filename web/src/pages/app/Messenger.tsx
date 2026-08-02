import { useState, useRef, useEffect, useCallback } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  MessageCircle,
  Loader2,
  Send,
  ArrowLeft,
  Search,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const BUBBLE_MINE = "168 57% 27%";
const BUBBLE_OTHER = "226 16% 30%";

interface Thread {
  id: string;
  user_a: string;
  user_b: string;
  last_message_at: string;
  created_at: string;
}

interface Message {
  id: string;
  thread_id: string;
  sender_id: string;
  body: string;
  read_at: string | null;
  created_at: string;
}

interface Profile {
  id: string;
  display_name: string;
  avatar_emoji: string;
}

interface ThreadWithMeta extends Thread {
  other_user_id: string;
  other_user?: Profile;
  last_message?: Message;
}

const formatTime = (iso: string): string => {
  const d = new Date(iso);
  const diff = Date.now() - d.getTime();
  const mins = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);
  if (mins < 1) return "Just now";
  if (mins < 60) return `${mins}m`;
  if (hours < 24) return `${hours}h`;
  if (days < 7) return `${days}d`;
  return d.toLocaleDateString("en-US", { month: "short", day: "numeric" });
};

export default function Messenger() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [selectedThread, setSelectedThread] = useState<ThreadWithMeta | null>(null);
  const [messageText, setMessageText] = useState("");
  const [search, setSearch] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // ── Load threads ──
  const { data: threads, isLoading } = useQuery<ThreadWithMeta[]>({
    queryKey: ["messenger-threads", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_threads")
        .select("*")
        .or(`user_a.eq.${user.id},user_b.eq.${user.id}`)
        .order("last_message_at", { ascending: false });
      if (error) throw error;
      const rows = (data ?? []) as Thread[];

      // Get other user IDs
      const otherIds = rows.map((t) => (t.user_a === user.id ? t.user_b : t.user_a));
      if (otherIds.length === 0) return [];

      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji")
        .in("id", otherIds);

      const profileMap = new Map((profiles ?? []).map((p) => [p.id, p]));

      // Get last messages for each thread
      const threadsWithMeta: ThreadWithMeta[] = [];
      for (const t of rows) {
        const otherId = t.user_a === user.id ? t.user_b : t.user_a;
        const { data: lastMsg } = await supabase
          .from("rockscout_messages")
          .select("*")
          .eq("thread_id", t.id)
          .order("created_at", { ascending: false })
          .limit(1)
          .maybeSingle();

        threadsWithMeta.push({
          ...t,
          other_user_id: otherId,
          other_user: profileMap.get(otherId) ?? undefined,
          last_message: (lastMsg as Message) ?? undefined,
        });
      }
      return threadsWithMeta;
    },
    enabled: !!user,
  });

  // ── Load messages for selected thread ──
  const { data: messages } = useQuery<Message[]>({
    queryKey: ["thread-messages", selectedThread?.id],
    queryFn: async () => {
      if (!selectedThread) return [];
      const { data, error } = await supabase
        .from("rockscout_messages")
        .select("*")
        .eq("thread_id", selectedThread.id)
        .order("created_at", { ascending: true });
      if (error) throw error;
      return (data ?? []) as Message[];
    },
    enabled: !!selectedThread,
  });

  // ── Mark messages as read ──
  useEffect(() => {
    if (!selectedThread || !user || !messages) return;
    const unread = messages.filter((m) => m.sender_id !== user.id && !m.read_at);
    if (unread.length === 0) return;
    supabase
      .from("rockscout_messages")
      .update({ read_at: new Date().toISOString() })
      .in("id", unread.map((m) => m.id))
      .then(() => queryClient.invalidateQueries({ queryKey: ["messenger-threads", user.id] }));
  }, [selectedThread, messages, user, queryClient]);

  // ── Auto-scroll to bottom ──
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // ── Realtime subscription ──
  useEffect(() => {
    if (!selectedThread) return;
    const channel = supabase
      .channel(`thread-${selectedThread.id}`)
      .on(
        "postgres_changes",
        { event: "INSERT", schema: "public", table: "rockscout_messages", filter: `thread_id=eq.${selectedThread.id}` },
        () => {
          queryClient.invalidateQueries({ queryKey: ["thread-messages", selectedThread.id] });
          queryClient.invalidateQueries({ queryKey: ["messenger-threads", user?.id] });
        },
      )
      .subscribe();
    return () => { supabase.removeChannel(channel); };
  }, [selectedThread, queryClient, user]);

  // ── Send message ──
  const sendMessage = useMutation({
    mutationFn: async () => {
      if (!user || !selectedThread || !messageText.trim()) return;
      const { error } = await supabase
        .from("rockscout_messages")
        .insert({
          thread_id: selectedThread.id,
          sender_id: user.id,
          body: messageText.trim(),
        });
      if (error) throw error;

      // Update thread's last_message_at
      await supabase
        .from("rockscout_threads")
        .update({ last_message_at: new Date().toISOString() })
        .eq("id", selectedThread.id);
    },
    onSuccess: () => {
      setMessageText("");
      queryClient.invalidateQueries({ queryKey: ["thread-messages", selectedThread?.id] });
      queryClient.invalidateQueries({ queryKey: ["messenger-threads", user?.id] });
    },
    onError: (err: Error) => toast.error(err.message),
  });

  const handleSend = useCallback(() => {
    if (messageText.trim()) sendMessage.mutate();
  }, [messageText, sendMessage]);

  if (!user) {
    return (
      <ScreenScaffold title="Messages" onBack={() => window.history.back()}>
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <MessageCircle className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to view messages</p>
        </div>
      </ScreenScaffold>
    );
  }

  // ── Chat detail view ──
  if (selectedThread) {
    return (
      <div className="flex h-[calc(100vh-8rem)] flex-col md:h-[calc(100vh-4rem)]">
        {/* Chat header */}
        <div className="flex items-center gap-3 border-b border-border px-4 py-3">
          <button
            onClick={() => setSelectedThread(null)}
            className="sculpted-button sculpted-raised dark-card flex h-9 w-9 items-center justify-center rounded-lg"
            style={{ ["--sculpted-accent" as string]: AQUA_HEX }}
          >
            <ArrowLeft className="h-4 w-4" style={{ color: `hsl(${AQUA_HEX})` }} />
          </button>
          <span className="text-2xl">{selectedThread.other_user?.avatar_emoji ?? "🧗"}</span>
          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-bold text-foreground">
              {selectedThread.other_user?.display_name ?? "Unknown hunter"}
            </p>
          </div>
        </div>

        {/* Messages */}
        <div className="flex-1 space-y-3 overflow-y-auto px-4 py-4">
          {messages?.map((msg) => {
            const isMine = msg.sender_id === user.id;
            return (
              <div
                key={msg.id}
                className={`flex ${isMine ? "justify-end" : "justify-start"}`}
              >
                <div
                  className="max-w-[75%] rounded-2xl px-4 py-2.5 text-sm"
                  style={{
                    backgroundColor: `hsl(${isMine ? BUBBLE_MINE : BUBBLE_OTHER})`,
                    color: "hsl(36 40% 95%)",
                    borderTopRightRadius: isMine ? "0.5rem" : undefined,
                    borderTopLeftRadius: !isMine ? "0.5rem" : undefined,
                  }}
                >
                  <p className="whitespace-pre-wrap break-words">{msg.body}</p>
                  <p className="mt-1 text-[10px] opacity-60">
                    {formatTime(msg.created_at)}
                  </p>
                </div>
              </div>
            );
          })}
          <div ref={messagesEndRef} />
        </div>

        {/* Input */}
        <div className="flex gap-2 border-t border-border px-4 py-3">
          <Input
            value={messageText}
            onChange={(e) => setMessageText(e.target.value)}
            onKeyDown={(e) => { if (e.key === "Enter") handleSend(); }}
            placeholder="Type a message…"
            maxLength={1000}
            className="flex-1"
          />
          <SculptedButton
            accent="citrine"
            glowing
            onClick={handleSend}
            disabled={!messageText.trim() || sendMessage.isPending}
          >
            <Send className="h-4 w-4" />
          </SculptedButton>
        </div>
      </div>
    );
  }

  // ── Thread list view ──
  const filteredThreads = (threads ?? []).filter((t) => {
    if (!search) return true;
    const name = t.other_user?.display_name?.toLowerCase() ?? "";
    const lastMsg = t.last_message?.body?.toLowerCase() ?? "";
    return name.includes(search.toLowerCase()) || lastMsg.includes(search.toLowerCase());
  });

  return (
    <ScreenScaffold title="Messages" onBack={() => window.history.back()}>
      <div className="space-y-4 px-4 pb-8">
        {/* Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search conversations…"
            className="pl-9"
          />
        </div>

        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        ) : filteredThreads.length > 0 ? (
          <div className="space-y-2">
            {filteredThreads.map((thread) => (
              <SculptedCard
                key={thread.id}
                accent="aqua"
                interactive
                className="overflow-hidden"
                onClick={() => setSelectedThread(thread)}
              >
                <div className="flex items-center gap-3 p-3.5">
                  <div
                    className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-full text-xl"
                    style={{ ["--glow-color" as string]: AQUA_HEX }}
                  >
                    {thread.other_user?.avatar_emoji ?? "🧗"}
                  </div>
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-bold text-foreground">
                      {thread.other_user?.display_name ?? "Unknown hunter"}
                    </p>
                    <p className="truncate text-xs text-muted-foreground">
                      {thread.last_message?.body ?? "No messages yet"}
                    </p>
                  </div>
                  <span className="shrink-0 text-[10px] text-muted-foreground">
                    {thread.last_message ? formatTime(thread.last_message.created_at) : formatTime(thread.last_message_at)}
                  </span>
                </div>
              </SculptedCard>
            ))}
          </div>
        ) : (
          <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <MessageCircle className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">
              No conversations yet. Add friends to start messaging!
            </p>
          </SculptedCard>
        )}
      </div>
    </ScreenScaffold>
  );
}
