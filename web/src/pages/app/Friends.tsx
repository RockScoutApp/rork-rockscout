import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import {
  Users,
  MessageSquare,
  UserPlus,
  Check,
  X,
  Send,
  Loader2,
  Search,
  Mail,
  Clock,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";

interface Profile {
  id: string;
  display_name: string;
  avatar_emoji: string;
  level: number;
  is_pro: boolean;
}

interface FriendRequest {
  id: string;
  sender_id: string;
  recipient_id: string;
  status: string;
  created_at: string;
  sender?: Profile;
}

interface Connection {
  id: string;
  user_a: string;
  user_b: string;
  created_at: string;
  friend?: Profile;
}

interface Thread {
  id: string;
  user_a: string;
  user_b: string;
  last_message_at: string;
}

interface Message {
  id: string;
  thread_id: string;
  sender_id: string;
  body: string;
  created_at: string;
}

interface ThreadWithMeta extends Thread {
  friend?: Profile;
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

export default function Friends() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const [tab, setTab] = useState<"friends" | "requests" | "messages">("friends");
  const [search, setSearch] = useState("");
  const [activeThread, setActiveThread] = useState<string | null>(null);
  const [messageText, setMessageText] = useState("");

  // ── Friend requests ──
  const { data: requests } = useQuery<FriendRequest[]>({
    queryKey: ["friend-requests", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_friend_requests")
        .select("*")
        .eq("recipient_id", user.id)
        .eq("status", "pending")
        .order("created_at", { ascending: false });
      if (error) throw error;
      const rows = (data ?? []) as unknown as FriendRequest[];
      if (rows.length === 0) return [];

      const senderIds = rows.map((r) => r.sender_id);
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, level, is_pro")
        .in("id", senderIds);
      const profileMap = new Map(
        (profiles ?? []).map((p) => [p.id as string, p as Profile]),
      );
      return rows.map((r) => ({
        ...r,
        sender: profileMap.get(r.sender_id),
      }));
    },
    enabled: !!user,
  });

  // ── Connections (friends) ──
  const { data: friends } = useQuery<Connection[]>({
    queryKey: ["connections", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_connections")
        .select("*")
        .order("created_at", { ascending: false });
      if (error) throw error;
      const rows = (data ?? []) as unknown as Connection[];
      if (rows.length === 0) return [];

      const friendIds = rows.map((r) =>
        r.user_a === user.id ? r.user_b : r.user_a,
      );
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, level, is_pro")
        .in("id", friendIds);
      const profileMap = new Map(
        (profiles ?? []).map((p) => [p.id as string, p as Profile]),
      );
      return rows.map((r) => ({
        ...r,
        friend: profileMap.get(
          r.user_a === user.id ? r.user_b : r.user_a,
        ),
      }));
    },
    enabled: !!user,
  });

  // ── Message threads ──
  const { data: threads } = useQuery<ThreadWithMeta[]>({
    queryKey: ["threads", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_threads")
        .select("*")
        .order("last_message_at", { ascending: false });
      if (error) throw error;
      const rows = (data ?? []) as unknown as Thread[];
      if (rows.length === 0) return [];

      const friendIds = rows.map((r) =>
        r.user_a === user.id ? r.user_b : r.user_a,
      );
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, level, is_pro")
        .in("id", friendIds);
      const profileMap = new Map(
        (profiles ?? []).map((p) => [p.id as string, p as Profile]),
      );
      return rows.map((r) => ({
        ...r,
        friend: profileMap.get(
          r.user_a === user.id ? r.user_b : r.user_a,
        ),
      }));
    },
    enabled: !!user && tab === "messages",
  });

  // ── Messages in active thread ──
  const { data: messages } = useQuery<Message[]>({
    queryKey: ["messages", activeThread],
    queryFn: async () => {
      if (!activeThread) return [];
      const { data, error } = await supabase
        .from("rockscout_messages")
        .select("*")
        .eq("thread_id", activeThread)
        .order("created_at", { ascending: true });
      if (error) throw error;
      return (data ?? []) as unknown as Message[];
    },
    enabled: !!activeThread,
  });

  // ── Search profiles ──
  const { data: searchResults } = useQuery<Profile[]>({
    queryKey: ["profile-search", search],
    queryFn: async () => {
      if (!search.trim() || search.trim().length < 2) return [];
      const { data, error } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, level, is_pro")
        .ilike("display_name", `%${search}%`)
        .neq("id", user?.id ?? "")
        .limit(10);
      if (error) throw error;
      return (data ?? []) as unknown as Profile[];
    },
    enabled: search.trim().length >= 2,
  });

  // ── Mutations ──
  const sendFriendRequest = useMutation({
    mutationFn: async (recipientId: string) => {
      if (!user) throw new Error("Sign in to add friends");
      const { error } = await supabase
        .from("rockscout_friend_requests")
        .insert({ sender_id: user.id, recipient_id: recipientId });
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Friend request sent");
      queryClient.invalidateQueries({ queryKey: ["friend-requests"] });
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to send request"),
  });

  const respondRequest = useMutation({
    mutationFn: async ({
      requestId,
      accept,
    }: {
      requestId: string;
      accept: boolean;
    }) => {
      const status = accept ? "accepted" : "denied";
      const { error: updateError } = await supabase
        .from("rockscout_friend_requests")
        .update({ status, responded_at: new Date().toISOString() })
        .eq("id", requestId);
      if (updateError) throw updateError;

      if (accept && user) {
        // Find the request to get sender_id and recipient_id
        const req = (requests ?? []).find((r) => r.id === requestId);
        if (req) {
          // Create connection with a < b ordering
          const userA = [user.id, req.sender_id].sort()[0];
          const userB = [user.id, req.sender_id].sort()[1];
          const { error: connError } = await supabase
            .from("rockscout_connections")
            .insert({ user_a: userA, user_b: userB });
          if (connError) throw connError;
        }
      }
    },
    onSuccess: () => {
      toast.success("Friend added");
      queryClient.invalidateQueries({ queryKey: ["friend-requests"] });
      queryClient.invalidateQueries({ queryKey: ["connections"] });
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to respond"),
  });

  const sendMessage = useMutation({
    mutationFn: async () => {
      if (!user || !activeThread || !messageText.trim()) return;
      const { error } = await supabase
        .from("rockscout_messages")
        .insert({
          thread_id: activeThread,
          sender_id: user.id,
          body: messageText.trim(),
        });
      if (error) throw error;

      await supabase
        .from("rockscout_threads")
        .update({ last_message_at: new Date().toISOString() })
        .eq("id", activeThread);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["messages", activeThread] });
      queryClient.invalidateQueries({ queryKey: ["threads"] });
      setMessageText("");
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to send message"),
  });

  const startThread = async (friendId: string): Promise<string> => {
    if (!user) throw new Error("Not signed in");
    const userA = [user.id, friendId].sort()[0];
    const userB = [user.id, friendId].sort()[1];
    const { data: existing } = await supabase
      .from("rockscout_threads")
      .select("id")
      .eq("user_a", userA)
      .eq("user_b", userB)
      .maybeSingle();
    if (existing) return existing.id as string;

    const { data: created, error } = await supabase
      .from("rockscout_threads")
      .insert({ user_a: userA, user_b: userB })
      .select("id")
      .single();
    if (error) throw error;
    return created.id as string;
  };

  const handleMessageFriend = async (friendId: string) => {
    try {
      const threadId = await startThread(friendId);
      setActiveThread(threadId);
      setTab("messages");
    } catch (err) {
      toast.error(
        err instanceof Error ? err.message : "Failed to open conversation",
      );
    }
  };

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Users className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to connect with rockhounds</p>
      </div>
    );
  }

  const pendingCount = requests?.length ?? 0;

  return (
    <div className="space-y-5">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          RockScout Friends
        </h1>
        <p className="mt-0.5 text-sm text-muted-foreground">
          Connect, message, and trade with fellow rockhounds
        </p>
      </div>

      <Tabs value={tab} onValueChange={(v) => setTab(v as typeof tab)}>
        <TabsList>
          <TabsTrigger value="friends" className="gap-2">
            <Users className="h-4 w-4" />
            Friends
          </TabsTrigger>
          <TabsTrigger value="requests" className="gap-2 relative">
            <UserPlus className="h-4 w-4" />
            Requests
            {pendingCount > 0 && (
              <span className="ml-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-primary px-1 text-xs font-bold text-primary-foreground">
                {pendingCount}
              </span>
            )}
          </TabsTrigger>
          <TabsTrigger value="messages" className="gap-2">
            <MessageSquare className="h-4 w-4" />
            Messages
          </TabsTrigger>
        </TabsList>
      </Tabs>

      {/* ── Friends tab ── */}
      {tab === "friends" && (
        <div className="space-y-4">
          <div className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search rockhounds by display name..."
              className="pl-10"
            />
          </div>

          {/* Search results */}
          {search.trim().length >= 2 && (
            <div className="space-y-2">
              <p className="text-xs font-medium text-muted-foreground">
                {searchResults && searchResults.length > 0
                  ? "Found rockhounds"
                  : "No rockhounds found"}
              </p>
              {(searchResults ?? []).map((profile) => {
                const isFriend = (friends ?? []).some(
                  (c) => c.friend?.id === profile.id,
                );
                return (
                  <div
                    key={profile.id}
                    className="flex items-center gap-3 rounded-lg border border-border bg-card p-3"
                  >
                    <span className="text-2xl">{profile.avatar_emoji}</span>
                    <button
                      onClick={() => navigate(`/app/profile/${profile.id}`)}
                      className="min-w-0 flex-1 text-left"
                    >
                      <p className="truncate text-sm font-medium text-foreground">
                        {profile.display_name || "Anonymous"}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        Level {profile.level}
                        {profile.is_pro && " · Pro"}
                      </p>
                    </button>
                    {isFriend ? (
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleMessageFriend(profile.id)}
                        className="gap-1.5"
                      >
                        <MessageSquare className="h-3.5 w-3.5" />
                        Message
                      </Button>
                    ) : (
                      <Button
                        size="sm"
                        onClick={() => sendFriendRequest.mutate(profile.id)}
                        disabled={sendFriendRequest.isPending}
                        className="gap-1.5"
                      >
                        <UserPlus className="h-3.5 w-3.5" />
                        Add
                      </Button>
                    )}
                  </div>
                );
              })}
            </div>
          )}

          {/* Friends list */}
          {!search.trim() && (
            <div className="space-y-2">
              {(friends ?? []).length === 0 ? (
                <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
                  <Users className="h-8 w-8 text-muted-foreground" />
                  <p className="max-w-sm text-sm text-muted-foreground">
                    No friends yet. Search for rockhounds by name to send a
                    friend request.
                  </p>
                </div>
              ) : (
                (friends ?? []).map((conn) => (
                  <div
                    key={conn.id}
                    className="flex items-center gap-3 rounded-lg border border-border bg-card p-3"
                  >
                    <span className="text-2xl">
                      {conn.friend?.avatar_emoji ?? "💎"}
                    </span>
                    <button
                      onClick={() =>
                        navigate(`/app/profile/${conn.friend?.id}`)
                      }
                      className="min-w-0 flex-1 text-left"
                    >
                      <p className="truncate text-sm font-medium text-foreground">
                        {conn.friend?.display_name ?? "Unknown"}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        Level {conn.friend?.level ?? 1}
                        {conn.friend?.is_pro && " · Pro"}
                      </p>
                    </button>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => handleMessageFriend(conn.friend!.id)}
                      className="gap-1.5"
                    >
                      <MessageSquare className="h-3.5 w-3.5" />
                      Message
                    </Button>
                  </div>
                ))
              )}
            </div>
          )}
        </div>
      )}

      {/* ── Requests tab ── */}
      {tab === "requests" && (
        <div className="space-y-2">
          {(requests ?? []).length === 0 ? (
            <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
              <UserPlus className="h-8 w-8 text-muted-foreground" />
              <p className="text-sm text-muted-foreground">
                No pending friend requests.
              </p>
            </div>
          ) : (
            (requests ?? []).map((req) => (
              <div
                key={req.id}
                className="flex items-center gap-3 rounded-lg border border-border bg-card p-3"
              >
                <span className="text-2xl">
                  {req.sender?.avatar_emoji ?? "💎"}
                </span>
                <button
                  onClick={() => navigate(`/app/profile/${req.sender_id}`)}
                  className="min-w-0 flex-1 text-left"
                >
                  <p className="truncate text-sm font-medium text-foreground">
                    {req.sender?.display_name ?? "Unknown"}
                  </p>
                  <p className="flex items-center gap-1 text-xs text-muted-foreground">
                    <Clock className="h-3 w-3" />
                    {formatTime(req.created_at)}
                  </p>
                </button>
                <Button
                  size="sm"
                  onClick={() =>
                    respondRequest.mutate({ requestId: req.id, accept: true })
                  }
                  disabled={respondRequest.isPending}
                  className="gap-1.5"
                >
                  <Check className="h-3.5 w-3.5" />
                  Accept
                </Button>
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() =>
                    respondRequest.mutate({ requestId: req.id, accept: false })
                  }
                  disabled={respondRequest.isPending}
                >
                  <X className="h-3.5 w-3.5" />
                </Button>
              </div>
            ))
          )}
        </div>
      )}

      {/* ── Messages tab ── */}
      {tab === "messages" && (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-[300px_1fr]">
          {/* Thread list */}
          <div className="space-y-2 md:max-h-[600px] md:overflow-y-auto">
            {(threads ?? []).length === 0 ? (
              <div className="flex flex-col items-center justify-center gap-3 rounded-lg border border-border bg-card py-12 text-center">
                <MessageSquare className="h-8 w-8 text-muted-foreground" />
                <p className="max-w-xs text-sm text-muted-foreground">
                  No conversations yet. Message a friend to start chatting.
                </p>
              </div>
            ) : (
              (threads ?? []).map((thread) => (
                <button
                  key={thread.id}
                  onClick={() => setActiveThread(thread.id)}
                  className={`flex w-full items-center gap-3 rounded-lg border p-3 text-left transition-colors ${
                    activeThread === thread.id
                      ? "border-primary bg-primary/10"
                      : "border-border bg-card hover:border-primary/40"
                  }`}
                >
                  <span className="text-2xl">
                    {thread.friend?.avatar_emoji ?? "💎"}
                  </span>
                  <div className="min-w-0 flex-1">
                    <p className="truncate text-sm font-medium text-foreground">
                      {thread.friend?.display_name ?? "Unknown"}
                    </p>
                    <p className="text-xs text-muted-foreground">
                      {formatTime(thread.last_message_at)}
                    </p>
                  </div>
                </button>
              ))
            )}
          </div>

          {/* Active conversation */}
          {activeThread ? (
            <div className="flex flex-col rounded-xl border border-border bg-card md:max-h-[600px]">
              <div className="flex-1 space-y-3 overflow-y-auto p-4">
                {(messages ?? []).length === 0 ? (
                  <p className="py-8 text-center text-sm text-muted-foreground">
                    No messages yet. Say hello!
                  </p>
                ) : (
                  (messages ?? []).map((msg) => {
                    const isMe = msg.sender_id === user.id;
                    return (
                      <div
                        key={msg.id}
                        className={`flex ${
                          isMe ? "justify-end" : "justify-start"
                        }`}
                      >
                        <div
                          className={`max-w-[75%] rounded-2xl px-3 py-2 text-sm ${
                            isMe
                              ? "bg-primary text-primary-foreground"
                              : "bg-muted text-foreground"
                          }`}
                        >
                          <p>{msg.body}</p>
                          <p
                            className={`mt-0.5 text-xs ${
                              isMe
                                ? "text-primary-foreground/70"
                                : "text-muted-foreground"
                            }`}
                          >
                            {formatTime(msg.created_at)}
                          </p>
                        </div>
                      </div>
                    );
                  })
                )}
              </div>
              <div className="flex gap-2 border-t border-border p-3">
                <Input
                  value={messageText}
                  onChange={(e) => setMessageText(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" && !e.shiftKey) {
                      e.preventDefault();
                      sendMessage.mutate();
                    }
                  }}
                  placeholder="Type a message..."
                  className="flex-1"
                />
                <Button
                  size="sm"
                  onClick={() => sendMessage.mutate()}
                  disabled={!messageText.trim() || sendMessage.isPending}
                >
                  <Send className="h-4 w-4" />
                </Button>
              </div>
            </div>
          ) : (
            <div className="hidden items-center justify-center rounded-xl border border-border bg-card/50 py-20 text-center md:flex">
              <p className="text-sm text-muted-foreground">
                Select a conversation to view messages
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
