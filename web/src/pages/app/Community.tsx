import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Users,
  Plus,
  Heart,
  MessageCircle,
  Trash2,
  X,
  Loader2,
  Send,
  MessageSquare,
  Group as GroupIcon,
  Zap,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
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
import { CompactSearchPill } from "@/components/CompactSearchPill";
import { SculptedCard } from "@/components/sculpted";

interface Post {
  id: string;
  user_id: string;
  source_type: string;
  source_ref_id: string | null;
  title: string;
  tagline: string;
  image_uri: string | null;
  caption: string;
  location_text: string;
  created_at: string;
}

interface PostWithMeta extends Post {
  owner_emoji: string;
  owner_name: string;
  like_count: number;
  liked_by_me: boolean;
  comment_count: number;
}

interface Comment {
  id: string;
  post_id: string;
  user_id: string;
  body: string;
  created_at: string;
  author_emoji: string;
  author_name: string;
}

interface GroupChat {
  id: string;
  name: string;
  subject: string;
  creator_id: string;
  max_members: number | null;
  profanity_filter_level: string;
  header_image_url: string | null;
  scroll_speed_setting: string;
  created_at: string;
}

interface GroupChatMember {
  id: string;
  group_chat_id: string;
  user_id: string;
  joined_at: string;
  role: string;
}

type Tab = "posts" | "groups";

const formatTime = (iso: string): string => {
  const d = new Date(iso);
  const diff = Date.now() - d.getTime();
  const mins = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);
  if (mins < 1) return "Just now";
  if (mins < 60) return `${mins}m ago`;
  if (hours < 24) return `${hours}h ago`;
  if (days < 7) return `${days}d ago`;
  return d.toLocaleDateString("en-US", { month: "short", day: "numeric" });
};

export default function Community() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [activeTab, setActiveTab] = useState<Tab>("posts");
  const [showEditor, setShowEditor] = useState(false);
  const [form, setForm] = useState({ title: "", caption: "", location_text: "" });
  const [expandedPost, setExpandedPost] = useState<string | null>(null);
  const [commentText, setCommentText] = useState("");
  const [search, setSearch] = useState("");

  // Group chat state
  const [showGroupCreate, setShowGroupCreate] = useState(false);
  const [groupForm, setGroupForm] = useState({
    name: "",
    subject: "",
    max_members: 20,
    profanity_filter_level: "normal" as "normal" | "strict",
    scroll_speed_setting: "normal" as "normal" | "half" | "stop",
  });

  const { data: posts, isLoading } = useQuery<PostWithMeta[]>({
    queryKey: ["community-posts", user?.id],
    queryFn: async () => {
      const { data, error } = await supabase
        .from("rockscout_posts")
        .select("*")
        .order("created_at", { ascending: false })
        .limit(50);
      if (error) throw error;
      const rows = (data ?? []) as Post[];
      if (rows.length === 0) return [];

      const ownerIds = [...new Set(rows.map((r) => r.user_id))];
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji")
        .in("id", ownerIds);
      const profileMap = new Map(
        (profiles ?? []).map((p) => [p.id as string, p]),
      );

      const postIds = rows.map((r) => r.id);
      const { data: likes } = await supabase
        .from("rockscout_post_likes")
        .select("post_id, user_id")
        .in("post_id", postIds);
      const { data: commentCounts } = await supabase
        .from("rockscout_post_comments")
        .select("post_id")
        .in("post_id", postIds);

      const likeMap = new Map<string, number>();
      (likes ?? []).forEach((l) => {
        likeMap.set(l.post_id, (likeMap.get(l.post_id) ?? 0) + 1);
      });
      const commentMap = new Map<string, number>();
      (commentCounts ?? []).forEach((c) => {
        commentMap.set(c.post_id, (commentMap.get(c.post_id) ?? 0) + 1);
      });
      const likedByMe = new Set(
        (likes ?? []).filter((l) => l.user_id === user?.id).map((l) => l.post_id),
      );

      return rows.map((r) => {
        const p = profileMap.get(r.user_id);
        return {
          ...r,
          owner_emoji: p?.avatar_emoji ?? "💎",
          owner_name: p?.display_name ?? "Rockhound",
          like_count: likeMap.get(r.id) ?? 0,
          liked_by_me: likedByMe.has(r.id),
          comment_count: commentMap.get(r.id) ?? 0,
        };
      }) as PostWithMeta[];
    },
    enabled: !!user,
  });

  const { data: comments } = useQuery<Comment[]>({
    queryKey: ["post-comments", expandedPost],
    queryFn: async () => {
      if (!expandedPost) return [];
      const { data, error } = await supabase
        .from("rockscout_post_comments")
        .select("*")
        .eq("post_id", expandedPost)
        .order("created_at", { ascending: true });
      if (error) throw error;
      const rows = (data ?? []) as unknown as Array<{
        id: string;
        post_id: string;
        user_id: string;
        body: string;
        created_at: string;
      }>;
      if (rows.length === 0) return [];

      const userIds = [...new Set(rows.map((r) => r.user_id))];
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji")
        .in("id", userIds);
      const profileMap = new Map(
        (profiles ?? []).map((p) => [p.id as string, p]),
      );

      return rows.map((r) => {
        const p = profileMap.get(r.user_id);
        return {
          ...r,
          author_emoji: p?.avatar_emoji ?? "💎",
          author_name: p?.display_name ?? "Rockhound",
        };
      }) as Comment[];
    },
    enabled: !!expandedPost,
  });

  // ── Group chats query ──
  const { data: groupChats, isLoading: groupsLoading } = useQuery<
    (GroupChat & { member_count: number })[]
  >({
    queryKey: ["group-chats", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data: memberships, error: mErr } = await supabase
        .from("group_chat_members")
        .select("group_chat_id")
        .eq("user_id", user.id);
      if (mErr) throw mErr;
      const chatIds = (memberships ?? []).map((m) => m.group_chat_id);
      if (chatIds.length === 0) return [];

      const { data: chats, error: cErr } = await supabase
        .from("group_chats")
        .select("*")
        .in("id", chatIds)
        .is("deleted_at", null)
        .order("created_at", { ascending: false });
      if (cErr) throw cErr;

      const { data: members } = await supabase
        .from("group_chat_members")
        .select("group_chat_id")
        .in("group_chat_id", chatIds);

      const countMap = new Map<string, number>();
      (members ?? []).forEach((m) => {
        countMap.set(m.group_chat_id, (countMap.get(m.group_chat_id) ?? 0) + 1);
      });

      return (chats ?? []).map((c) => ({
        ...(c as GroupChat),
        member_count: countMap.get((c as GroupChat).id) ?? 0,
      }));
    },
    enabled: !!user && activeTab === "groups",
  });

  const createPost = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to post");
      const { error } = await supabase.from("rockscout_posts").insert({
        user_id: user.id,
        source_type: "journal",
        title: form.title,
        caption: form.caption,
        location_text: form.location_text,
      });
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Posted to the community feed");
      queryClient.invalidateQueries({ queryKey: ["community-posts"] });
      setShowEditor(false);
      setForm({ title: "", caption: "", location_text: "" });
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to post"),
  });

  const deletePost = useMutation({
    mutationFn: async (id: string) => {
      const { error } = await supabase
        .from("rockscout_posts")
        .delete()
        .eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Post deleted");
      queryClient.invalidateQueries({ queryKey: ["community-posts"] });
    },
    onError: () => toast.error("Failed to delete post"),
  });

  const toggleLike = useMutation({
    mutationFn: async (post: PostWithMeta) => {
      if (!user) throw new Error("Sign in to like posts");
      if (post.liked_by_me) {
        const { error } = await supabase
          .from("rockscout_post_likes")
          .delete()
          .eq("post_id", post.id)
          .eq("user_id", user.id);
        if (error) throw error;
      } else {
        const { error } = await supabase
          .from("rockscout_post_likes")
          .insert({ post_id: post.id, user_id: user.id });
        if (error) throw error;
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["community-posts"] });
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to toggle like"),
  });

  const addComment = useMutation({
    mutationFn: async () => {
      if (!user || !expandedPost || !commentText.trim()) return;
      const { error } = await supabase
        .from("rockscout_post_comments")
        .insert({
          post_id: expandedPost,
          user_id: user.id,
          body: commentText.trim(),
        });
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["post-comments", expandedPost] });
      queryClient.invalidateQueries({ queryKey: ["community-posts"] });
      setCommentText("");
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to comment"),
  });

  const createGroupChat = useMutation({
    mutationFn: async () => {
      if (!user) throw new Error("Sign in to create a group chat");
      if (!groupForm.name.trim()) throw new Error("Group name is required");
      const chatId = `gc-${crypto.randomUUID()}`;
      const { error: cErr } = await supabase.from("group_chats").insert({
        id: chatId,
        name: groupForm.name.trim(),
        subject: groupForm.subject.trim(),
        creator_id: user.id,
        max_members: groupForm.max_members || null,
        profanity_filter_level: groupForm.profanity_filter_level,
        scroll_speed_setting: groupForm.scroll_speed_setting,
      });
      if (cErr) throw cErr;
      const { error: mErr } = await supabase
        .from("group_chat_members")
        .insert({
          group_chat_id: chatId,
          user_id: user.id,
          role: "creator",
        });
      if (mErr) throw mErr;
    },
    onSuccess: () => {
      toast.success("Group chat created!");
      queryClient.invalidateQueries({ queryKey: ["group-chats"] });
      setShowGroupCreate(false);
      setGroupForm({
        name: "",
        subject: "",
        max_members: 20,
        profanity_filter_level: "normal",
        scroll_speed_setting: "normal",
      });
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to create group"),
  });

  if (!user) {
    return (
      <div className="flex flex-col items-center justify-center gap-3 py-16 text-center">
        <Users className="h-10 w-10 text-muted-foreground" />
        <p className="text-muted-foreground">Sign in to join the community</p>
      </div>
    );
  }

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
            Community
          </h1>
          <p className="mt-0.5 text-sm text-muted-foreground">
            Share your finds and connect with fellow rockhounds
          </p>
        </div>
        {activeTab === "posts" ? (
          <Button size="sm" onClick={() => setShowEditor(true)} className="gap-2">
            <Plus className="h-4 w-4" />
            Post
          </Button>
        ) : (
          <Button
            size="sm"
            onClick={() => setShowGroupCreate(true)}
            className="gap-2"
          >
            <Plus className="h-4 w-4" />
            New Group Chat
          </Button>
        )}
      </div>

      {/* Tab switcher */}
      <div className="flex items-center gap-2">
        <button
          onClick={() => setActiveTab("posts")}
          className={`inline-flex items-center gap-2 rounded-full px-4 py-2 text-sm font-semibold transition-all ${
            activeTab === "posts"
              ? "bg-primary/15 text-primary ring-1 ring-primary/30"
              : "text-muted-foreground hover:text-foreground"
          }`}
        >
          <MessageSquare className="h-4 w-4" />
          Posts
        </button>
        <button
          onClick={() => setActiveTab("groups")}
          className={`inline-flex items-center gap-2 rounded-full px-4 py-2 text-sm font-semibold transition-all ${
            activeTab === "groups"
              ? "bg-primary/15 text-primary ring-1 ring-primary/30"
              : "text-muted-foreground hover:text-foreground"
          }`}
        >
          <GroupIcon className="h-4 w-4" />
          Group Chats
        </button>
      </div>

      {/* Compact search */}
      <CompactSearchPill
        value={search}
        onChange={setSearch}
        placeholder={
          activeTab === "posts" ? "Search posts…" : "Search group chats…"
        }
      />

      {activeTab === "posts" ? (
        <>
          {isLoading ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin text-primary" />
            </div>
          ) : posts && posts.length > 0 ? (
            <div className="space-y-4">
              {posts
                .filter((p) => {
                  if (!search) return true;
                  const q = search.toLowerCase();
                  return (
                    p.title?.toLowerCase().includes(q) ||
                    p.caption?.toLowerCase().includes(q) ||
                    p.owner_name?.toLowerCase().includes(q)
                  );
                })
                .map((post) => (
                  <div
                    key={post.id}
                    className="space-y-3 dark-card sculpted-raised rounded-xl p-4"
                  >
                    <div className="flex items-center justify-between gap-2">
                      <div className="flex items-center gap-2">
                        <span className="text-2xl">{post.owner_emoji}</span>
                        <div>
                          <p className="text-sm font-semibold text-foreground">
                            {post.owner_name}
                          </p>
                          <p className="text-xs text-muted-foreground">
                            {formatTime(post.created_at)}
                            {post.location_text && ` · ${post.location_text}`}
                          </p>
                        </div>
                      </div>
                      {post.user_id === user.id && (
                        <button
                          onClick={() => deletePost.mutate(post.id)}
                          className="rounded-lg p-1.5 text-muted-foreground hover:bg-destructive/10 hover:text-destructive"
                          aria-label="Delete post"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      )}
                    </div>

                    {post.title && (
                      <h3 className="font-display text-base font-semibold text-foreground">
                        {post.title}
                      </h3>
                    )}

                    {post.caption && (
                      <p className="text-sm leading-relaxed text-foreground/80">
                        {post.caption}
                      </p>
                    )}

                    {post.image_uri && (
                      <img
                        src={post.image_uri}
                        alt={post.title || "Community post"}
                        className="max-h-80 w-full rounded-lg object-cover"
                        onError={(e) => {
                          (e.target as HTMLImageElement).src = "/placeholder.svg";
                        }}
                      />
                    )}

                    <div className="flex items-center gap-4 border-t border-border pt-2">
                      <button
                        onClick={() => toggleLike.mutate(post)}
                        className="flex items-center gap-1.5 text-sm text-muted-foreground transition-colors hover:text-primary"
                      >
                        <Heart
                          className={`h-4 w-4 ${
                            post.liked_by_me ? "fill-primary text-primary" : ""
                          }`}
                        />
                        {post.like_count > 0 && post.like_count}
                      </button>
                      <button
                        onClick={() =>
                          setExpandedPost(
                            expandedPost === post.id ? null : post.id,
                          )
                        }
                        className="flex items-center gap-1.5 text-sm text-muted-foreground transition-colors hover:text-foreground"
                      >
                        <MessageCircle className="h-4 w-4" />
                        {post.comment_count > 0 && post.comment_count}
                      </button>
                    </div>

                    {expandedPost === post.id && (
                      <div className="space-y-3 border-t border-border pt-3">
                        {comments && comments.length > 0 ? (
                          <div className="space-y-2">
                            {comments.map((c) => (
                              <div key={c.id} className="flex gap-2">
                                <span className="text-base">{c.author_emoji}</span>
                                <div className="min-w-0 flex-1">
                                  <p className="text-sm">
                                    <span className="font-medium text-foreground">
                                      {c.author_name}
                                    </span>{" "}
                                    <span className="text-muted-foreground">
                                      {c.body}
                                    </span>
                                  </p>
                                  <p className="text-xs text-muted-foreground">
                                    {formatTime(c.created_at)}
                                  </p>
                                </div>
                              </div>
                            ))}
                          </div>
                        ) : (
                          <p className="text-xs text-muted-foreground">
                            No comments yet.
                          </p>
                        )}
                        <div className="flex gap-2">
                          <Input
                            value={commentText}
                            onChange={(e) => setCommentText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key === "Enter") {
                                e.preventDefault();
                                addComment.mutate();
                              }
                            }}
                            placeholder="Write a comment..."
                            className="flex-1"
                          />
                          <Button
                            size="sm"
                            onClick={() => addComment.mutate()}
                            disabled={!commentText.trim() || addComment.isPending}
                          >
                            <Send className="h-4 w-4" />
                          </Button>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
              <Users className="h-8 w-8 text-muted-foreground" />
              <p className="max-w-sm text-sm text-muted-foreground">
                No posts yet. Share a find, a field trip story, or a specimen
                you're proud of with the community.
              </p>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowEditor(true)}
                className="gap-2"
              >
                <Plus className="h-4 w-4" />
                Share a post
              </Button>
            </div>
          )}
        </>
      ) : (
        /* ── Group Chats tab ── */
        <>
          {groupsLoading ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin text-primary" />
            </div>
          ) : groupChats && groupChats.length > 0 ? (
            <div className="space-y-2">
              {groupChats
                .filter((gc) => {
                  if (!search) return true;
                  const q = search.toLowerCase();
                  return (
                    gc.name?.toLowerCase().includes(q) ||
                    gc.subject?.toLowerCase().includes(q)
                  );
                })
                .map((gc) => (
                  <SculptedCard
                    key={gc.id}
                    accent="aqua"
                    interactive
                    className="overflow-hidden"
                  >
                    <div className="flex items-center gap-3 p-3.5">
                      <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-primary/15 text-xl ring-1 ring-primary/25">
                        {gc.header_image_url ? (
                          <img
                            src={gc.header_image_url}
                            alt={gc.name}
                            className="h-full w-full rounded-full object-cover"
                            onError={(e) => {
                              (e.target as HTMLImageElement).src =
                                "/placeholder.svg";
                            }}
                          />
                        ) : (
                          "👥"
                        )}
                      </div>
                      <div className="min-w-0 flex-1">
                        <p className="truncate text-sm font-bold text-foreground">
                          {gc.name}
                        </p>
                        <p className="truncate text-xs text-muted-foreground">
                          {gc.subject || "No subject"}
                          {gc.max_members && ` · ${gc.member_count}/${gc.max_members} members`}
                          {!gc.max_members && ` · ${gc.member_count} members`}
                        </p>
                      </div>
                      <span className="shrink-0 rounded-full bg-primary/10 px-2 py-0.5 text-[10px] font-medium text-primary">
                        {gc.profanity_filter_level === "strict" ? "Strict" : "Normal"}
                      </span>
                    </div>
                  </SculptedCard>
                ))}
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center gap-3 dark-card sculpted-raised rounded-lg py-12 text-center">
              <GroupIcon className="h-8 w-8 text-muted-foreground" />
              <p className="max-w-sm text-sm text-muted-foreground">
                No group chats yet. Create one to start a group conversation
                with fellow rockhounds.
              </p>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowGroupCreate(true)}
                className="gap-2"
              >
                <Plus className="h-4 w-4" />
                Start a Group Chat
              </Button>
            </div>
          )}
        </>
      )}

      {/* Post editor */}
      <Dialog open={showEditor} onOpenChange={setShowEditor}>
        <DialogContent aria-describedby={undefined} className="max-w-md">
          <DialogHeader>
            <DialogTitle>Share with the community</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="post-title">Title</Label>
              <Input
                id="post-title"
                value={form.title}
                onChange={(e) =>
                  setForm((f) => ({ ...f, title: e.target.value }))
                }
                placeholder="e.g. Found my first Herkimer Diamond!"
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="post-caption">Caption</Label>
              <Textarea
                id="post-caption"
                value={form.caption}
                onChange={(e) =>
                  setForm((f) => ({ ...f, caption: e.target.value }))
                }
                placeholder="Tell the story behind your find..."
                rows={4}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="post-location">Location (optional)</Label>
              <Input
                id="post-location"
                value={form.location_text}
                onChange={(e) =>
                  setForm((f) => ({ ...f, location_text: e.target.value }))
                }
                placeholder="e.g. Herkimer, NY"
              />
            </div>
          </div>
          <DialogFooter className="gap-2">
            <Button variant="outline" onClick={() => setShowEditor(false)}>
              Cancel
            </Button>
            <Button
              onClick={() => createPost.mutate()}
              disabled={
                createPost.isPending || (!form.title && !form.caption)
              }
            >
              {createPost.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                "Post"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Group chat creator */}
      <Dialog open={showGroupCreate} onOpenChange={setShowGroupCreate}>
        <DialogContent aria-describedby={undefined} className="max-w-md">
          <DialogHeader>
            <DialogTitle>Start a New Group Chat</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="gc-name">Group Name</Label>
              <Input
                id="gc-name"
                value={groupForm.name}
                onChange={(e) =>
                  setGroupForm((f) => ({ ...f, name: e.target.value }))
                }
                placeholder="e.g. Arizona Rockhounds"
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="gc-subject">Subject (optional)</Label>
              <Input
                id="gc-subject"
                value={groupForm.subject}
                onChange={(e) =>
                  setGroupForm((f) => ({ ...f, subject: e.target.value }))
                }
                placeholder="e.g. Quartz collecting trips"
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <Label htmlFor="gc-max">Max Members</Label>
                <Input
                  id="gc-max"
                  type="number"
                  value={groupForm.max_members}
                  onChange={(e) =>
                    setGroupForm((f) => ({
                      ...f,
                      max_members: parseInt(e.target.value) || 0,
                    }))
                  }
                  min={2}
                  max={100}
                />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="gc-filter">Profanity Filter</Label>
                <select
                  id="gc-filter"
                  value={groupForm.profanity_filter_level}
                  onChange={(e) =>
                    setGroupForm((f) => ({
                      ...f,
                      profanity_filter_level: e.target.value as "normal" | "strict",
                    }))
                  }
                  className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm text-foreground"
                >
                  <option value="normal">Normal</option>
                  <option value="strict">Strict</option>
                </select>
              </div>
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="gc-scroll">Default Scroll Speed</Label>
              <select
                id="gc-scroll"
                value={groupForm.scroll_speed_setting}
                onChange={(e) =>
                  setGroupForm((f) => ({
                    ...f,
                    scroll_speed_setting: e.target.value as "normal" | "half" | "stop",
                  }))
                }
                className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm text-foreground"
              >
                <option value="normal">Normal (instant)</option>
                <option value="half">Half (4s delay)</option>
                <option value="stop">Stop (no auto-scroll)</option>
              </select>
            </div>
          </div>
          <DialogFooter className="gap-2">
            <Button variant="outline" onClick={() => setShowGroupCreate(false)}>
              Cancel
            </Button>
            <Button
              onClick={() => createGroupChat.mutate()}
              disabled={createGroupChat.isPending || !groupForm.name.trim()}
            >
              {createGroupChat.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                "Create Group Chat"
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
