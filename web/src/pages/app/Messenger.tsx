import { useState, useRef, useEffect, useCallback } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import {
  MessageCircle,
  Loader2,
  Send,
  X,
  Search,
  Image as ImageIcon,
  FastForward,
  AlertTriangle,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";
import { CompactSearchPill } from "@/components/CompactSearchPill";
import { filterProfanity, filterSelfHarm, parseTaggedUserIds } from "@/lib/profanity-filter";
import {
  enqueueMessage,
  getPendingForChat,
  drainMessageQueue,
  type PendingWebMessage,
} from "@/lib/offline-message-queue";

const CITRINE_HEX = "36 80% 58%";
const AQUA_HEX = "20 62% 65%";
const BUBBLE_MINE = "168 57% 27%";
const BUBBLE_OTHER = "226 16% 30%";

interface Thread {
  id: string;
  last_message_at: string;
  created_at: string;
}

interface ThreadParticipant {
  id: string;
  thread_id: string;
  user_id: string;
  joined_at: string;
}

interface Message {
  id: string;
  thread_id: string;
  sender_id: string;
  body: string;
  image_url: string | null;
  read_at: string | null;
  created_at: string;
  reply_to_message_id: string | null;
  tagged_user_ids: string[] | null;
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
  unread_count: number;
}

interface GroupChat {
  id: string;
  name: string;
  subject: string;
  creator_id: string;
  max_members: number | null;
  profanity_filter_level: string;
  scroll_speed_setting: string;
  created_at: string;
}

interface GroupMessage {
  id: string;
  group_chat_id: string;
  sender_id: string;
  body: string;
  image_url: string | null;
  reply_to_message_id: string | null;
  tagged_user_ids: string[] | null;
  created_at: string;
}

type ChatView =
  | { type: "list" }
  | { type: "thread"; thread: ThreadWithMeta }
  | { type: "group"; group: GroupChat };

type ScrollSpeed = "normal" | "half" | "stop";

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

/** Renders chat text with tagged usernames as bright Citrine pills. */
function TaggedText({ text, taggedNames }: { text: string; taggedNames: string[] }) {
  if (!taggedNames.length) {
    return <p className="whitespace-pre-wrap break-words">{text}</p>;
  }

  const segments: { text: string; isTagged: boolean }[] = [];
  let remaining = text;
  while (remaining.length > 0) {
    let earliestMatch: { name: string; idx: number } | null = null;
    for (const name of taggedNames) {
      const idx = remaining.toLowerCase().indexOf(name.toLowerCase());
      if (idx >= 0 && (earliestMatch === null || idx < earliestMatch.idx)) {
        earliestMatch = { name, idx };
      }
    }
    if (!earliestMatch) {
      segments.push({ text: remaining, isTagged: false });
      break;
    }
    const { name, idx } = earliestMatch;
    if (idx > 0) segments.push({ text: remaining.substring(0, idx), isTagged: false });
    segments.push({ text: remaining.substring(idx, idx + name.length), isTagged: true });
    remaining = remaining.substring(idx + name.length);
  }

  return (
    <p className="whitespace-pre-wrap break-words">
      {segments.map((seg, i) =>
        seg.isTagged ? (
          <span
            key={i}
            className="inline rounded-md bg-primary px-1.5 py-0.5 text-xs font-bold text-primary-foreground"
            style={{
              backgroundColor: `hsl(${CITRINE_HEX})`,
              color: "hsl(30 30% 12%)",
            }}
          >
            {seg.text}
          </span>
        ) : (
          <span key={i}>{seg.text}</span>
        ),
      )}
    </p>
  );
}

export default function Messenger() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [chatView, setChatView] = useState<ChatView>({ type: "list" });
  const [messageText, setMessageText] = useState("");
  const [search, setSearch] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Typing indicator state
  const [typingUsers, setTypingUsers] = useState<Record<string, string>>({});
  const lastTypingSentRef = useRef(0);
  const wasTypingRef = useRef(false);
  const typingPollRef = useRef<ReturnType<typeof setInterval> | null>(null);

  // Reply state
  const [replyToMessageId, setReplyToMessageId] = useState<string | null>(null);
  const [replyToSenderName, setReplyToSenderName] = useState<string | null>(null);
  const [replyToBody, setReplyToBody] = useState<string | null>(null);

  // Scroll speed
  const [scrollSpeed, setScrollSpeed] = useState<ScrollSpeed>("normal");
  const [isUserScrolling, setIsUserScrolling] = useState(false);
  const lastAutoScrollRef = useRef(0);

  // Profanity + self-harm warning
  const [showProfanityWarning, setShowProfanityWarning] = useState(false);
  const [pendingFilteredText, setPendingFilteredText] = useState("");
  const [showSelfHarmWarning, setShowSelfHarmWarning] = useState(false);
  const [selfHarmFilteredText, setSelfHarmFilteredText] = useState("");
  const [selfHarmOffenseCount, setSelfHarmOffenseCount] = useState(0);

  // Pending offline messages for the current chat (shown as greyed-out bubbles)
  const [pendingOfflineMsgs, setPendingOfflineMsgs] = useState<PendingWebMessage[]>([]);

  // ── Load threads ──
  const { data: threads, isLoading } = useQuery<ThreadWithMeta[]>({
    queryKey: ["messenger-threads", user?.id],
    queryFn: async () => {
      if (!user) return [];
      // Get thread IDs where the current user is a participant
      const { data: myParts, error: partsErr } = await supabase
        .from("chat_thread_participants")
        .select("thread_id")
        .eq("user_id", user.id);
      if (partsErr) throw partsErr;
      const threadIds = (myParts ?? []).map((p) => p.thread_id);
      if (threadIds.length === 0) return [];

      // Fetch thread data
      const { data: threadRows, error: threadErr } = await supabase
        .from("chat_threads")
        .select("*")
        .in("id", threadIds)
        .order("last_message_at", { ascending: false });
      if (threadErr) throw threadErr;
      const rows = (threadRows ?? []) as Thread[];
      if (rows.length === 0) return [];

      // Fetch ALL participants for these threads to find the other user
      const { data: allParts } = await supabase
        .from("chat_thread_participants")
        .select("*")
        .in("thread_id", threadIds);
      const partsMap = new Map<string, ThreadParticipant[]>();
      (allParts ?? []).forEach((p: ThreadParticipant) => {
        const list = partsMap.get(p.thread_id) ?? [];
        list.push(p);
        partsMap.set(p.thread_id, list);
      });

      // Collect other user IDs (participants who aren't the current user)
      const otherIds: string[] = [];
      rows.forEach((t) => {
        const parts = partsMap.get(t.id) ?? [];
        const other = parts.find((p) => p.user_id !== user.id);
        if (other) otherIds.push(other.user_id);
      });

      let profileMap = new Map<string, Profile>();
      if (otherIds.length > 0) {
        const { data: profiles } = await supabase
          .from("rockscout_profiles")
          .select("id, display_name, avatar_emoji")
          .in("id", otherIds);
        profileMap = new Map((profiles ?? []).map((p) => [p.id, p]));
      }

      const threadsWithMeta: ThreadWithMeta[] = [];
      for (const t of rows) {
        const parts = partsMap.get(t.id) ?? [];
        const otherPart = parts.find((p) => p.user_id !== user.id);
        const otherId = otherPart?.user_id ?? "";
        const { data: lastMsg } = await supabase
          .from("chat_messages")
          .select("*")
          .eq("thread_id", t.id)
          .order("created_at", { ascending: false })
          .limit(1)
          .maybeSingle();

        // Count unread messages from the other user
        const { count: unreadCount } = await supabase
          .from("chat_messages")
          .select("id", { count: "exact", head: true })
          .eq("thread_id", t.id)
          .neq("sender_id", user.id)
          .is("read_at", null);

        threadsWithMeta.push({
          ...t,
          other_user_id: otherId,
          other_user: profileMap.get(otherId) ?? undefined,
          last_message: (lastMsg as Message) ?? undefined,
          unread_count: unreadCount ?? 0,
        });
      }
      return threadsWithMeta;
    },
    enabled: !!user,
  });

  // ── Load group chats ──
  const { data: groupChats } = useQuery<GroupChat[]>({
    queryKey: ["messenger-group-chats", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data: memberships } = await supabase
        .from("group_chat_members")
        .select("group_chat_id")
        .eq("user_id", user.id);
      const chatIds = (memberships ?? []).map((m) => m.group_chat_id);
      if (chatIds.length === 0) return [];
      const { data: chats } = await supabase
        .from("group_chats")
        .select("*")
        .in("id", chatIds)
        .is("deleted_at", null)
        .order("created_at", { ascending: false });
      return (chats ?? []) as GroupChat[];
    },
    enabled: !!user,
  });

  // ── Load group chat unread counts (localStorage last-read tracking) ──
  const { data: groupUnreadCounts } = useQuery<Record<string, number>>({
    queryKey: ["group-unread-counts", user?.id, groupChats?.map((g) => g.id).join(",")],
    queryFn: async () => {
      if (!user || !groupChats || groupChats.length === 0) return {};
      const result: Record<string, number> = {};
      for (const gc of groupChats) {
        const lastRead = localStorage.getItem(`group_last_read_${gc.id}`);
        // Fetch messages newer than lastRead (or all if never read), not sent by me
        let query = supabase
          .from("group_messages")
          .select("id", { count: "exact", head: true })
          .eq("group_chat_id", gc.id)
          .neq("sender_id", user.id);
        if (lastRead) {
          query = query.gt("created_at", lastRead);
        }
        const { count } = await query;
        if (count && count > 0) result[gc.id] = count;
      }
      return result;
    },
    enabled: !!user && !!groupChats && groupChats.length > 0,
  });

  // ── Load messages for selected thread ──
  const activeThreadId = chatView.type === "thread" ? chatView.thread.id : null;
  const { data: messages } = useQuery<Message[]>({
    queryKey: ["thread-messages", activeThreadId],
    queryFn: async () => {
      if (!activeThreadId) return [];
      const { data, error } = await supabase
        .from("chat_messages")
        .select("*")
        .eq("thread_id", activeThreadId)
        .order("created_at", { ascending: true });
      if (error) throw error;
      return (data ?? []) as Message[];
    },
    enabled: !!activeThreadId,
  });

  // ── Load group messages ──
  const activeGroupId = chatView.type === "group" ? chatView.group.id : null;
  const { data: groupMessages } = useQuery<GroupMessage[]>({
    queryKey: ["group-messages", activeGroupId],
    queryFn: async () => {
      if (!activeGroupId) return [];
      const { data, error } = await supabase
        .from("group_messages")
        .select("*")
        .eq("group_chat_id", activeGroupId)
        .order("created_at", { ascending: true });
      if (error) throw error;
      return (data ?? []) as GroupMessage[];
    },
    enabled: !!activeGroupId,
  });

  // ── Load group members ──
  const { data: groupMembers } = useQuery<{ user_id: string }[]>({
    queryKey: ["group-members", activeGroupId],
    queryFn: async () => {
      if (!activeGroupId) return [];
      const { data, error } = await supabase
        .from("group_chat_members")
        .select("user_id")
        .eq("group_chat_id", activeGroupId);
      if (error) throw error;
      return (data ?? []) as { user_id: string }[];
    },
    enabled: !!activeGroupId,
  });

  // ── Load profiles for group members ──
  const memberIds = (groupMembers ?? []).map((m) => m.user_id);
  const { data: memberProfiles } = useQuery<Profile[]>({
    queryKey: ["group-member-profiles", memberIds.join(",")],
    queryFn: async () => {
      if (memberIds.length === 0) return [];
      const { data, error } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji")
        .in("id", memberIds);
      if (error) throw error;
      return (data ?? []) as Profile[];
    },
    enabled: memberIds.length > 0,
  });

  // ── Mark messages as read ──
  useEffect(() => {
    if (!activeThreadId || !user || !messages) return;
    const unread = messages.filter((m) => m.sender_id !== user.id && !m.read_at);
    if (unread.length === 0) return;
    supabase
      .from("chat_messages")
      .update({ read_at: new Date().toISOString() })
      .in("id", unread.map((m) => m.id))
      .then(() =>
        queryClient.invalidateQueries({ queryKey: ["messenger-threads", user.id] }),
      );
  }, [activeThreadId, messages, user, queryClient]);

  // ── Mark group chat as read on open ──
  useEffect(() => {
    if (chatView.type === "group" && chatView.group) {
      localStorage.setItem(
        `group_last_read_${chatView.group.id}`,
        new Date().toISOString(),
      );
      queryClient.invalidateQueries({ queryKey: ["group-unread-counts"] });
    }
  }, [chatView, queryClient]);

  // ── Auto-scroll with speed control ──
  const allMessages = chatView.type === "thread" ? (messages ?? []) : (groupMessages ?? []);
  useEffect(() => {
    if (allMessages.length === 0 || scrollSpeed === "stop" || isUserScrolling) return;
    const now = Date.now();
    const delayMs = scrollSpeed === "half" ? 4000 : 0;
    if (now - lastAutoScrollRef.current >= delayMs) {
      messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
      lastAutoScrollRef.current = now;
    }
  }, [allMessages.length, scrollSpeed, isUserScrolling]);

  // ── Realtime subscription ──
  useEffect(() => {
    if (chatView.type === "thread" && chatView.thread) {
      const channel = supabase
        .channel(`thread-${chatView.thread.id}`)
        .on(
          "postgres_changes",
          { event: "INSERT", schema: "public", table: "chat_messages", filter: `thread_id=eq.${chatView.thread.id}` },
          () => {
            queryClient.invalidateQueries({ queryKey: ["thread-messages", chatView.thread.id] });
            queryClient.invalidateQueries({ queryKey: ["messenger-threads", user?.id] });
          },
        )
        .subscribe();
      return () => { supabase.removeChannel(channel); };
    }
    if (chatView.type === "group" && chatView.group) {
      const channel = supabase
        .channel(`group-${chatView.group.id}`)
        .on(
          "postgres_changes",
          { event: "INSERT", schema: "public", table: "group_messages", filter: `group_chat_id=eq.${chatView.group.id}` },
          () => {
            queryClient.invalidateQueries({ queryKey: ["group-messages", chatView.group.id] });
          },
        )
        .subscribe();
      return () => { supabase.removeChannel(channel); };
    }
  }, [chatView, queryClient, user]);

  // ── Typing indicator: poll typing status for active chat ──
  const activeChatId = chatView.type === "thread" ? chatView.thread.id : chatView.type === "group" ? chatView.group.id : null;
  useEffect(() => {
    if (!activeChatId || !user) {
      setTypingUsers({});
      return;
    }
    const poll = async () => {
      const cutoff = new Date(Date.now() - 5000).toISOString();
      const { data } = await supabase
        .from("chat_typing_status")
        .select("user_id")
        .eq("chat_id", activeChatId)
        .neq("user_id", user.id)
        .eq("is_typing", true)
        .gt("updated_at", cutoff);
      const typingIds = (data ?? []).map((r: { user_id: string }) => r.user_id);
      if (typingIds.length > 0) {
        const { data: profiles } = await supabase
          .from("rockscout_profiles")
          .select("id, display_name")
          .in("id", typingIds);
        const map: Record<string, string> = {};
        (profiles ?? []).forEach((p: { id: string; display_name: string }) => {
          map[p.id] = p.display_name;
        });
        setTypingUsers(map);
      } else {
        setTypingUsers({});
      }
    };
    poll();
    typingPollRef.current = setInterval(poll, 2500);
    return () => {
      if (typingPollRef.current) {
        clearInterval(typingPollRef.current);
        typingPollRef.current = null;
      }
      setTypingUsers({});
    };
  }, [activeChatId, user]);

  // ── Send typing status when messageText changes ──
  useEffect(() => {
    if (!activeChatId || !user) return;
    const now = Date.now();
    if (messageText.trim()) {
      if (!wasTypingRef.current || now - lastTypingSentRef.current > 3000) {
        wasTypingRef.current = true;
        lastTypingSentRef.current = now;
        supabase
          .from("chat_typing_status")
          .upsert({
            chat_id: activeChatId,
            user_id: user.id,
            is_typing: true,
            updated_at: new Date().toISOString(),
          }, { onConflict: "chat_id,user_id" })
          .then(() => {});
      }
    } else if (wasTypingRef.current) {
      wasTypingRef.current = false;
      supabase
        .from("chat_typing_status")
        .delete()
        .eq("chat_id", activeChatId)
        .eq("user_id", user.id)
        .then(() => {});
    }
  }, [messageText, activeChatId, user]);

  // ── Clear typing status when leaving chat ──
  useEffect(() => {
    return () => {
      if (activeChatId && user && wasTypingRef.current) {
        supabase
          .from("chat_typing_status")
          .delete()
          .eq("chat_id", activeChatId)
          .eq("user_id", user.id)
          .then(() => {});
        wasTypingRef.current = false;
      }
    };
  }, [activeChatId, user]);

  // ── Save draft on unmount/navigation ──
  useEffect(() => {
    return () => {
      if (messageText.trim()) {
        const key =
          chatView.type === "thread"
            ? `draft-thread-${chatView.thread.id}`
            : chatView.type === "group"
              ? `draft-group-${chatView.group.id}`
              : "";
        if (key) localStorage.setItem(key, messageText);
      }
    };
  }, [messageText, chatView]);

  // ── Restore draft on open ──
  useEffect(() => {
    if (chatView.type === "thread") {
      const draft = localStorage.getItem(`draft-thread-${chatView.thread.id}`);
      if (draft) setMessageText(draft);
      else setMessageText("");
    } else if (chatView.type === "group") {
      const draft = localStorage.getItem(`draft-group-${chatView.group.id}`);
      if (draft) setMessageText(draft);
      else setMessageText("");
    }
    setReplyToMessageId(null);
    setReplyToSenderName(null);
    setReplyToBody(null);
  }, [chatView]);

  // ── Load pending offline messages for the current chat ──
  useEffect(() => {
    const chatId =
      chatView.type === "thread" ? chatView.thread.id :
      chatView.type === "group" ? chatView.group.id : null;
    if (!chatId) {
      setPendingOfflineMsgs([]);
      return;
    }
    getPendingForChat(chatId).then(setPendingOfflineMsgs).catch(() => setPendingOfflineMsgs([]));
  }, [chatView]);

  // ── Send message ──
  const sendMessage = useMutation({
    mutationFn: async (rawText: string) => {
      if (!user) return;
      if (chatView.type === "thread" && chatView.thread) {
        const otherName = chatView.thread.other_user?.display_name ?? "";
        const taggedIds = otherName && rawText.toLowerCase().includes(`@${otherName.toLowerCase()}`)
          ? [chatView.thread.other_user_id]
          : [];
        const { error } = await supabase.from("chat_messages").insert({
          thread_id: chatView.thread.id,
          sender_id: user.id,
          body: rawText.trim(),
          reply_to_message_id: replyToMessageId,
          tagged_user_ids: taggedIds.length > 0 ? taggedIds : null,
        });
        if (error) throw error;
        await supabase
          .from("chat_threads")
          .update({ last_message_at: new Date().toISOString() })
          .eq("id", chatView.thread.id);
      } else if (chatView.type === "group" && chatView.group) {
        const members = (memberProfiles ?? []).map((p) => ({ id: p.id, display_name: p.display_name }));
        const taggedIds = parseTaggedUserIds(rawText, members);
        const { error } = await supabase.from("group_messages").insert({
          group_chat_id: chatView.group.id,
          sender_id: user.id,
          body: rawText.trim(),
          reply_to_message_id: replyToMessageId,
          tagged_user_ids: taggedIds.length > 0 ? taggedIds : null,
        });
        if (error) throw error;
      }
    },
    onSuccess: () => {
      setMessageText("");
      setReplyToMessageId(null);
      setReplyToSenderName(null);
      setReplyToBody(null);
      const draftKey =
        chatView.type === "thread"
          ? `draft-thread-${chatView.thread.id}`
          : chatView.type === "group"
            ? `draft-group-${chatView.group.id}`
            : "";
      if (draftKey) localStorage.removeItem(draftKey);
      if (chatView.type === "thread") {
        queryClient.invalidateQueries({ queryKey: ["thread-messages", chatView.thread.id] });
        queryClient.invalidateQueries({ queryKey: ["messenger-threads", user?.id] });
      } else if (chatView.type === "group") {
        queryClient.invalidateQueries({ queryKey: ["group-messages", chatView.group.id] });
      }
    },
    onError: (err: Error) => {
      // Queue the message for offline retry instead of just showing a toast
      const chatId = chatView.type === "thread" ? chatView.thread.id : chatView.type === "group" ? chatView.group.id : "";
      const isGroup = chatView.type === "group";
      if (chatId && user) {
        const pendingMsg: PendingWebMessage = {
          id: `pending-${crypto.randomUUID()}`,
          chatId,
          body: messageText.trim(),
          imageUrl: null,
          replyToMessageId,
          taggedUserIds: null,
          isGroup,
          senderId: user.id,
          queuedAt: Date.now(),
          attempts: 0,
        };
        enqueueMessage(pendingMsg).catch(() => {});
        setPendingOfflineMsgs((prev) => [...prev, pendingMsg]);
      }
      toast.error(err.message || "Failed to send \u2014 message queued for retry");
    },
  });

  // ── Send image ──
  const sendImage = useMutation({
    mutationFn: async (file: File) => {
      if (!user) return;
      const ext = file.name.split(".").pop() || "jpg";
      const path = `${user.id}/message_images/${crypto.randomUUID()}.${ext}`;
      const { error: uploadErr } = await supabase.storage
        .from("rockscout-captures")
        .upload(path, file);
      if (uploadErr) throw uploadErr;
      const { data: urlData } = supabase.storage
        .from("rockscout-captures")
        .getPublicUrl(path);
      const imageUrl = urlData.publicUrl;

      if (chatView.type === "thread" && chatView.thread) {
        const { error } = await supabase.from("chat_messages").insert({
          thread_id: chatView.thread.id,
          sender_id: user.id,
          body: "",
          image_url: imageUrl,
          reply_to_message_id: replyToMessageId,
        });
        if (error) throw error;
        await supabase
          .from("chat_threads")
          .update({ last_message_at: new Date().toISOString() })
          .eq("id", chatView.thread.id);
      } else if (chatView.type === "group" && chatView.group) {
        const { error } = await supabase.from("group_messages").insert({
          group_chat_id: chatView.group.id,
          sender_id: user.id,
          body: "",
          image_url: imageUrl,
          reply_to_message_id: replyToMessageId,
        });
        if (error) throw error;
      }
    },
    onSuccess: () => {
      if (chatView.type === "thread") {
        queryClient.invalidateQueries({ queryKey: ["thread-messages", chatView.thread.id] });
      } else if (chatView.type === "group") {
        queryClient.invalidateQueries({ queryKey: ["group-messages", chatView.group.id] });
      }
      toast.success("Image sent");
    },
    onError: (err: Error) => toast.error(err.message),
  });

  const handleSend = useCallback(() => {
    if (!messageText.trim()) return;
    const isGroup = chatView.type === "group";
    const strict = isGroup && chatView.type === "group" && chatView.group?.profanity_filter_level === "strict";
    const chatId = chatView.type === "group" ? chatView.group?.id : chatView.type === "thread" ? chatView.thread?.id : "";

    // Step 1: Check for self-harm phrases FIRST (before regular profanity)
    const selfHarmResult = filterSelfHarm(messageText);
    const textToFilter = selfHarmResult.filteredText;

    if (selfHarmResult.hasSelfHarm) {
      const newCount = selfHarmOffenseCount + 1;
      setSelfHarmOffenseCount(newCount);
      setSelfHarmFilteredText(selfHarmResult.filteredText);
      // 1st offense = warning popup, 2nd offense = auto-report
      if (newCount >= 2) {
        // Auto-file a report for 2nd self-harm offense
        if (user) {
          fetch(`${import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL}/report-notification-email`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              reportedUserId: user.id,
              reportedEmail: user.email,
              reportReason: `Self-harm language detected: ${selfHarmResult.matchedPhrases.join(", ")}`,
              reportCount: newCount,
              source: "auto_self_harm",
            }),
          }).catch(() => {});
        }
      }
      setShowSelfHarmWarning(true);
      return; // Block send — user must acknowledge warning first
    }

    // Step 2: Regular profanity filter on the (possibly self-harm-asterisked) text
    const { filteredText, hasExplicitContent } = filterProfanity(textToFilter, strict);
    if (hasExplicitContent) {
      setPendingFilteredText(filteredText);
      setShowProfanityWarning(true);
      // Record warning server-side
      if (user) {
        fetch(`${import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL}/profanity-warning`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            userId: user.id,
            reason: "Explicit language in message",
            source: isGroup ? "group_chat" : "chat",
            sourceId: chatId,
          }),
        }).catch(() => {});
      }
    } else {
      sendMessage.mutate(filteredText);
    }
  }, [messageText, sendMessage, chatView, user, selfHarmOffenseCount]);

  const handleLongPressReply = (msgId: string, msgBody: string, senderId: string, senderName: string) => {
    setReplyToMessageId(msgId);
    setReplyToSenderName(senderName);
    setReplyToBody(msgBody || "[image]");
    if (!messageText.includes(`@${senderName}`)) {
      setMessageText(`@${senderName} ${messageText}`);
    }
  };

  if (!user) {
    return (
      <ScreenScaffold title="Messages">
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <MessageCircle className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to view messages</p>
        </div>
      </ScreenScaffold>
    );
  }

  // ── Chat detail view (private or group) ──
  if (chatView.type === "thread" || chatView.type === "group") {
    const isGroup = chatView.type === "group";
    const headerName = isGroup
      ? chatView.group.name
      : chatView.thread.other_user?.display_name ?? "Unknown hunter";
    const headerEmoji = isGroup ? "👥" : (chatView.thread.other_user?.avatar_emoji ?? "🧗");
    const memberCount = isGroup ? (groupMembers ?? []).length : 0;
    const currentMessages = isGroup ? (groupMessages ?? []) : (messages ?? []);
    const profileMap = new Map((memberProfiles ?? []).map((p) => [p.id, p]));

    return (
      <div className="flex h-[calc(100vh-8rem)] flex-col md:h-[calc(100vh-4rem)]">
        {/* Chat header with close button */}
        <div className="flex items-center gap-3 border-b border-border px-4 py-3">
          <button
            onClick={() => {
              if (messageText.trim()) {
                const key = isGroup
                  ? `draft-group-${chatView.group.id}`
                  : `draft-thread-${chatView.thread.id}`;
                localStorage.setItem(key, messageText);
              }
              setChatView({ type: "list" });
              setMessageText("");
              setReplyToMessageId(null);
              setReplyToSenderName(null);
              setReplyToBody(null);
            }}
            className="sculpted-button sculpted-raised dark-card flex h-9 w-9 items-center justify-center rounded-lg"
            style={{ ["--sculpted-accent" as string]: AQUA_HEX }}
            aria-label="Close chat"
          >
            <X className="h-4 w-4" style={{ color: `hsl(${AQUA_HEX})` }} />
          </button>
          <span className="text-2xl">{headerEmoji}</span>
          <div className="min-w-0 flex-1">
            <button
              className="block truncate text-left text-sm font-bold text-foreground hover:text-primary hover:underline"
              onClick={() => {
                const profileId = isGroup
                  ? chatView.group?.creator_id
                  : chatView.thread?.other_user_id;
                if (profileId && profileId !== user.id) {
                  navigate(`/app/profile/${profileId}`);
                }
              }}
            >
              {headerName}
            </button>
            {isGroup && (
              <p className="truncate text-xs text-muted-foreground">
                {memberCount} member{memberCount !== 1 ? "s" : ""}
                {chatView.group.subject && ` · ${chatView.group.subject}`}
              </p>
            )}
          </div>
        </div>

        {/* Messages */}
        <div
          className="flex-1 space-y-3 overflow-y-auto px-4 py-4"
          onScroll={() => setIsUserScrolling(true)}
        >
          {currentMessages.length === 0 && (
            <div className="flex items-center justify-center py-10 text-center">
              <p className="text-sm text-muted-foreground">
                Say hi! This is the start of your conversation.
              </p>
            </div>
          )}
          {Object.keys(typingUsers).length > 0 && (
            <div className="flex items-center gap-2 px-2 py-1">
              <span className="flex gap-1">
                {[0, 1, 2].map((i) => (
                  <span
                    key={i}
                    className="typing-dot"
                    style={{ animationDelay: `${i * 0.2}s` }}
                  />
                ))}
              </span>
              <span className="text-xs font-medium" style={{ color: `hsl(${AQUA_HEX})` }}>
                {Object.values(typingUsers).length === 1
                  ? `${Object.values(typingUsers)[0]} is typing…`
                  : Object.values(typingUsers).length === 2
                    ? `${Object.values(typingUsers)[0]} and ${Object.values(typingUsers)[1]} are typing…`
                    : `${Object.values(typingUsers)[0]} and ${Object.values(typingUsers).length - 1} others are typing…`}
              </span>
            </div>
          )}
          {currentMessages.map((msg) => {
            const isMine = msg.sender_id === user.id;
            const senderName = isMine
              ? "You"
              : (profileMap.get(msg.sender_id)?.display_name ?? headerName);
            const replySender = msg.reply_to_message_id
              ? (() => {
                  const replied = currentMessages.find((m) => m.id === msg.reply_to_message_id);
                  if (!replied) return null;
                  return replied.sender_id === user.id
                    ? "You"
                    : (profileMap.get(replied.sender_id)?.display_name ?? headerName);
                })()
              : null;
            const replyBodyText = msg.reply_to_message_id
              ? currentMessages.find((m) => m.id === msg.reply_to_message_id)?.body || "[image]"
              : null;
            const taggedNames = (msg.tagged_user_ids ?? [])
              .map((tid) => {
                if (tid === user.id) return "You";
                return profileMap.get(tid)?.display_name ?? "";
              })
              .filter(Boolean);

            return (
              <div
                key={msg.id}
                className={`flex ${isMine ? "justify-end" : "justify-start"}`}
              >
                <div
                  className="max-w-[75%] cursor-pointer rounded-2xl px-4 py-2.5 text-sm select-none"
                  style={{
                    backgroundColor: `hsl(${isMine ? BUBBLE_MINE : BUBBLE_OTHER})`,
                    color: "hsl(36 40% 95%)",
                    borderTopRightRadius: isMine ? "0.5rem" : undefined,
                    borderTopLeftRadius: !isMine ? "0.5rem" : undefined,
                  }}
                  onContextMenu={(e) => {
                    e.preventDefault();
                    handleLongPressReply(msg.id, msg.body, msg.sender_id, senderName);
                  }}
                  onDoubleClick={() =>
                    handleLongPressReply(msg.id, msg.body, msg.sender_id, senderName)
                  }
                  title="Right-click or double-click to reply. Long-press sender name to view profile."
                >
                  {isGroup && !isMine && (
                    <button
                      className="mb-1 block text-xs font-bold hover:underline"
                      style={{ color: `hsl(${CITRINE_HEX})` }}
                      onClick={(e) => {
                        e.stopPropagation();
                        if (msg.sender_id !== user.id) {
                          navigate(`/app/profile/${msg.sender_id}`);
                        }
                      }}
                      title="Click to view profile"
                    >
                      {profileMap.get(msg.sender_id)?.avatar_emoji ?? "💎"} {senderName}
                    </button>
                  )}
                  {/* Private chat: click sender name to open profile */}
                  {!isGroup && !isMine && (
                    <button
                      className="mb-1 block text-[10px] font-bold opacity-70 hover:underline"
                      style={{ color: `hsl(${AQUA_HEX})` }}
                      onClick={(e) => {
                        e.stopPropagation();
                        if (msg.sender_id !== user.id) {
                          navigate(`/app/profile/${msg.sender_id}`);
                        }
                      }}
                      title="Click to view profile"
                    >
                      {senderName}
                    </button>
                  )}
                  {/* Reply threading preview */}
                  {replySender && replyBodyText && (
                    <div
                      className="mb-2 rounded-lg p-2"
                      style={{ backgroundColor: "hsl(30 20% 10%)" }}
                    >
                      <p className="text-xs font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>
                        {replySender} replied to a comment
                      </p>
                      <p className="mt-0.5 text-xs opacity-70">
                        {replyBodyText.length > 80 ? replyBodyText.substring(0, 80) + "…" : replyBodyText}
                      </p>
                    </div>
                  )}
                  {/* Image */}
                  {msg.image_url && (
                    <img
                      src={msg.image_url}
                      alt="Shared image"
                      className="mb-2 max-h-60 w-full rounded-lg object-cover"
                      onError={(e) => {
                        (e.target as HTMLImageElement).src = "/placeholder.svg";
                      }}
                    />
                  )}
                  {/* Text with tagged pills */}
                  {msg.body && <TaggedText text={msg.body} taggedNames={taggedNames} />}
                  <p className="mt-1 text-[10px] opacity-60">{formatTime(msg.created_at)}</p>
                </div>
              </div>
            );
          })}
          <div ref={messagesEndRef} />
        </div>

        {/* Reply preview bar */}
        {replyToSenderName && replyToBody && (
          <div className="flex items-center gap-3 border-t border-border bg-card/40 px-4 py-2">
            <div className="min-w-0 flex-1">
              <p className="text-xs font-bold" style={{ color: `hsl(${CITRINE_HEX})` }}>
                Replying to {replyToSenderName}
              </p>
              <p className="truncate text-xs text-muted-foreground">
                {replyToBody.length > 60 ? replyToBody.substring(0, 60) + "…" : replyToBody}
              </p>
            </div>
            <button
              onClick={() => {
                setReplyToMessageId(null);
                setReplyToSenderName(null);
                setReplyToBody(null);
              }}
              className="shrink-0 rounded-lg p-1 text-muted-foreground hover:text-foreground"
              aria-label="Cancel reply"
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        )}

        {/* Scroll speed controls */}
        <div className="flex items-center gap-2 border-t border-border px-4 py-1.5">
          {(["normal", "half", "stop"] as ScrollSpeed[]).map((speed) => (
            <button
              key={speed}
              onClick={() => {
                setScrollSpeed(speed);
                setIsUserScrolling(false);
              }}
              className={`rounded-lg px-2.5 py-1 text-xs font-semibold capitalize transition-all ${
                scrollSpeed === speed
                  ? "bg-primary/15 text-primary ring-1 ring-primary/30"
                  : "text-muted-foreground hover:text-foreground"
              }`}
            >
              {speed}
            </button>
          ))}
          <div className="flex-1" />
          {(isUserScrolling || scrollSpeed === "stop") && (
            <button
              onClick={() => {
                setIsUserScrolling(false);
                messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
              }}
              className="inline-flex items-center gap-1 rounded-lg bg-primary/15 px-2.5 py-1 text-xs font-semibold text-primary ring-1 ring-primary/30"
            >
              <FastForward className="h-3 w-3" />
              Current
            </button>
          )}
        </div>

        {/* Input */}
        <div className="flex items-center gap-2 border-t border-border px-4 py-3">
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            className="hidden"
            onChange={(e) => {
              const file = e.target.files?.[0];
              if (file) sendImage.mutate(file);
              e.target.value = "";
            }}
          />
          <button
            onClick={() => fileInputRef.current?.click()}
            disabled={sendImage.isPending}
            className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-card text-primary ring-1 ring-border transition-colors hover:bg-primary/10"
            aria-label="Send image"
          >
            {sendImage.isPending ? (
              <Loader2 className="h-5 w-5 animate-spin" />
            ) : (
              <ImageIcon className="h-5 w-5" />
            )}
          </button>
          <Input
            value={messageText}
            onChange={(e) => setMessageText(e.target.value)}
            onKeyDown={(e) => { if (e.key === "Enter" && !e.shiftKey) { e.preventDefault(); handleSend(); } }}
            placeholder="Type a message… (right-click a message to reply)"
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

        {/* Self-harm warning dialog */}
        {showSelfHarmWarning && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
            <div className="dark-card sculpted-raised mx-4 max-w-sm rounded-xl p-5">
              <div className="flex items-center gap-2 text-destructive">
                <AlertTriangle className="h-5 w-5" />
                <h3 className="font-bold text-destructive">Self-Harm Language Detected</h3>
              </div>
              <p className="mt-3 text-sm text-muted-foreground">
                Your message contains language related to self-harm. This is a
                serious violation of our community guidelines. The phrase has been
                censored.
              </p>
              {selfHarmOffenseCount >= 2 && (
                <p className="mt-2 text-sm font-semibold text-destructive">
                  This is your {selfHarmOffenseCount}rd offense. An automatic report
                  has been filed and you will be notified via email and notifications.
                </p>
              )}
              <p className="mt-2 text-xs text-muted-foreground">
                If you or someone you know is struggling, please contact the
                988 Suicide & Crisis Lifeline by dialing 988.
              </p>
              <div className="mt-4 flex justify-end">
                <SculptedButton
                  accent="citrine"
                  onClick={() => {
                    setShowSelfHarmWarning(false);
                    sendMessage.mutate(selfHarmFilteredText);
                    setSelfHarmFilteredText("");
                  }}
                >
                  I Understand
                </SculptedButton>
              </div>
            </div>
          </div>
        )}

        {/* Profanity warning dialog */}
        {showProfanityWarning && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
            <div className="dark-card sculpted-raised mx-4 max-w-sm rounded-xl p-5">
              <div className="flex items-center gap-2 text-destructive">
                <AlertTriangle className="h-5 w-5" />
                <h3 className="font-bold text-destructive">Content Warning</h3>
              </div>
              <p className="mt-3 text-sm text-muted-foreground">
                Your message contains language that was censored. If you believe
                we're censoring a word by mistake, email{" "}
                <a
                  href="mailto:support@rockscout.net"
                  className="text-primary underline"
                >
                  support@rockscout.net
                </a>{" "}
                to get it cleared up.
              </p>
              <div className="mt-4 flex justify-end">
                <SculptedButton
                  accent="citrine"
                  onClick={() => {
                    setShowProfanityWarning(false);
                    sendMessage.mutate(pendingFilteredText);
                    setPendingFilteredText("");
                  }}
                >
                  OK
                </SculptedButton>
              </div>
            </div>
          </div>
        )}
      </div>
    );
  }

  // ── Thread + group list view ──
  const filteredThreads = (threads ?? []).filter((t) => {
    if (!search) return true;
    const name = t.other_user?.display_name?.toLowerCase() ?? "";
    const lastMsg = t.last_message?.body?.toLowerCase() ?? "";
    return name.includes(search.toLowerCase()) || lastMsg.includes(search.toLowerCase());
  });
  const filteredGroups = (groupChats ?? []).filter((gc) => {
    if (!search) return true;
    return gc.name.toLowerCase().includes(search.toLowerCase()) || gc.subject.toLowerCase().includes(search.toLowerCase());
  });

  return (
    <ScreenScaffold title="Messages">
      <div className="space-y-4 px-4 pb-8">
        <CompactSearchPill
          value={search}
          onChange={setSearch}
          placeholder="Search conversations…"
        />

        {/* Group chats */}
        {filteredGroups.length > 0 && (
          <div className="space-y-2">
            <p className="text-xs font-bold uppercase tracking-wide text-muted-foreground">
              Group Chats
            </p>
            {filteredGroups.map((gc) => {
              const gUnread = groupUnreadCounts?.[gc.id] ?? 0;
              return (
              <SculptedCard
                key={gc.id}
                accent="aqua"
                interactive
                className="overflow-hidden"
                onClick={() => setChatView({ type: "group", group: gc })}
              >
                <div className="flex items-center gap-3 p-3.5">
                  <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xl ring-1 ring-primary/25">
                    👥
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <p className="truncate text-sm font-bold text-foreground">
                        {gc.name}
                      </p>
                      {gUnread > 0 && (
                        <span
                          className="inline-flex h-5 min-w-5 shrink-0 items-center justify-center rounded-full px-1.5 text-[10px] font-bold"
                          style={{
                            backgroundColor: `hsl(${CITRINE_HEX})`,
                            color: "hsl(30 30% 12%)",
                          }}
                        >
                          {gUnread > 99 ? "99+" : gUnread}
                        </span>
                      )}
                    </div>
                    <p className="truncate text-xs text-muted-foreground">
                      {gc.subject || "No subject"}
                    </p>
                  </div>
                </div>
              </SculptedCard>
              );
            })}
          </div>
        )}

        {/* Private threads */}
        {isLoading ? (
          <div className="flex justify-center py-12">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        ) : filteredThreads.length > 0 ? (
          <div className="space-y-2">
            <p className="text-xs font-bold uppercase tracking-wide text-muted-foreground">
              Private Chats
            </p>
            {filteredThreads.map((thread) => {
              const tUnread = thread.unread_count ?? 0;
              return (
              <SculptedCard
                key={thread.id}
                accent="aqua"
                interactive
                className="overflow-hidden"
                onClick={() => setChatView({ type: "thread", thread })}
              >
                <div className="flex items-center gap-3 p-3.5">
                  <div
                    className="glowing-border flex h-12 w-12 shrink-0 items-center justify-center rounded-full text-xl"
                    style={{ ["--glow-color" as string]: AQUA_HEX }}
                  >
                    {thread.other_user?.avatar_emoji ?? "🧗"}
                  </div>
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <p className="truncate text-sm font-bold text-foreground">
                        {thread.other_user?.display_name ?? "Unknown hunter"}
                      </p>
                      {tUnread > 0 && (
                        <span
                          className="inline-flex h-5 min-w-5 shrink-0 items-center justify-center rounded-full px-1.5 text-[10px] font-bold"
                          style={{
                            backgroundColor: `hsl(${CITRINE_HEX})`,
                            color: "hsl(30 30% 12%)",
                          }}
                        >
                          {tUnread > 99 ? "99+" : tUnread}
                        </span>
                      )}
                    </div>
                    <p className="truncate text-xs text-muted-foreground">
                      {thread.last_message?.body ?? "No messages yet"}
                    </p>
                  </div>
                  <span className="shrink-0 text-[10px] text-muted-foreground">
                    {thread.last_message ? formatTime(thread.last_message.created_at) : formatTime(thread.last_message_at)}
                  </span>
                </div>
              </SculptedCard>
              );
            })}
          </div>
        ) : filteredGroups.length === 0 ? (
          <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
            <MessageCircle className="h-10 w-10 text-muted-foreground" />
            <p className="text-sm text-muted-foreground">
              No conversations yet. Add friends to start messaging!
            </p>
          </SculptedCard>
        ) : null}
      </div>
    </ScreenScaffold>
  );
}
