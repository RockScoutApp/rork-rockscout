import { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  ArrowLeft,
  Heart,
  MessageCircle,
  Trash2,
  Send,
  Loader2,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { toast } from "sonner";
import { filterProfanity } from "@/lib/profanity-filter";
import { useProfanityLevel } from "@/hooks/useProfanityLevel";
import NotFound from "@/pages/NotFound";
import { UserAvatar } from "@/components/app/UserAvatar";

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
  owner_avatar_path: string | null;
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
  author_avatar_path: string | null;
}

const formatTime = (iso: string): string => {
  const d = new Date(iso);
  const now = new Date();
  const diff = now.getTime() - d.getTime();
  const hours = diff / (1000 * 60 * 60);
  if (hours < 1) return "just now";
  if (hours < 24) return `${Math.floor(hours)}h ago`;
  const days = Math.floor(hours / 24);
  if (days < 7) return `${days}d ago`;
  return d.toLocaleDateString("en-US", { month: "short", day: "numeric" });
};

export default function CommunityPostDetail() {
  const profanityLevel = useProfanityLevel();
  const { postId } = useParams<{ postId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [commentBody, setCommentBody] = useState("");

  const { data: post, isLoading } = useQuery<PostWithMeta | null>({
    queryKey: ["community-post", postId],
    queryFn: async () => {
      if (!postId) return null;
      const { data: postData, error: postErr } = await supabase
        .from("rockscout_posts")
        .select("*")
        .eq("id", postId)
        .maybeSingle();
      if (postErr) throw postErr;
      if (!postData) return null;

      const rawPost = postData as Post;

      // Fetch owner profile
      const { data: ownerProfile } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, avatar_image_path")
        .eq("id", rawPost.user_id)
        .maybeSingle();

      // Fetch likes
      const { data: likes } = await supabase
        .from("rockscout_post_likes")
        .select("user_id")
        .eq("post_id", postId);

      // Fetch comment count
      const { count: commentCount } = await supabase
        .from("rockscout_post_comments")
        .select("id", { count: "exact", head: true })
        .eq("post_id", postId);

      const likeList = likes ?? [];
      const likedByMe = user ? likeList.some((l) => l.user_id === user.id) : false;

      return {
        ...rawPost,
        owner_emoji: ownerProfile?.avatar_emoji ?? "💎",
        owner_name: ownerProfile?.display_name ?? "Rockhound",
        owner_avatar_path: (ownerProfile as { avatar_image_path?: string | null })?.avatar_image_path ?? null,
        like_count: likeList.length,
        liked_by_me: likedByMe,
        comment_count: commentCount ?? 0,
      } as PostWithMeta;
    },
    enabled: !!postId,
  });

  const { data: comments } = useQuery<Comment[]>({
    queryKey: ["post-comments-detail", postId],
    queryFn: async () => {
      if (!postId) return [];
      const { data: commentRows, error } = await supabase
        .from("rockscout_post_comments")
        .select("id, post_id, user_id, body, created_at")
        .eq("post_id", postId)
        .order("created_at", { ascending: true });
      if (error) throw error;
      const rows = commentRows ?? [];
      if (rows.length === 0) return [];

      const userIds = [...new Set(rows.map((r) => r.user_id))];
      const { data: profiles } = await supabase
        .from("rockscout_profiles")
        .select("id, display_name, avatar_emoji, avatar_image_path")
        .in("id", userIds);
      const profileMap = new Map(
        (profiles ?? []).map((p) => [p.id as string, p]),
      );

      return rows.map((r) => {
        const p = profileMap.get(r.user_id);
        return {
          id: r.id,
          post_id: r.post_id,
          user_id: r.user_id,
          body: r.body,
          created_at: r.created_at,
          author_emoji: p?.avatar_emoji ?? "💎",
          author_name: p?.display_name ?? "Rockhound",
          author_avatar_path: (p as { avatar_image_path?: string | null })?.avatar_image_path ?? null,
        } as Comment;
      });
    },
    enabled: !!postId,
  });

  const addComment = useMutation({
    mutationFn: async () => {
      if (!user || !postId || !commentBody.trim()) return;
      const { filteredText } = filterProfanity(commentBody.trim(), profanityLevel);
      const { error } = await supabase.from("rockscout_post_comments").insert({
        post_id: postId,
        user_id: user.id,
        body: filteredText,
      });
      if (error) throw error;
    },
    onSuccess: () => {
      setCommentBody("");
      queryClient.invalidateQueries({ queryKey: ["post-comments-detail", postId] });
      queryClient.invalidateQueries({ queryKey: ["community-post", postId] });
      queryClient.invalidateQueries({ queryKey: ["community-posts"] });
      toast.success("Comment added");
    },
    onError: () => toast.error("Failed to add comment"),
  });

  const toggleLike = useMutation({
    mutationFn: async () => {
      if (!user || !postId) return;
      if (post?.liked_by_me) {
        const { error } = await supabase
          .from("rockscout_post_likes")
          .delete()
          .eq("post_id", postId)
          .eq("user_id", user.id);
        if (error) throw error;
      } else {
        const { error } = await supabase
          .from("rockscout_post_likes")
          .insert({ post_id: postId, user_id: user.id });
        if (error) throw error;
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["community-post", postId] });
      queryClient.invalidateQueries({ queryKey: ["community-posts"] });
    },
    onError: (err) =>
      toast.error(err instanceof Error ? err.message : "Failed to toggle like"),
  });

  const deletePost = useMutation({
    mutationFn: async () => {
      if (!postId) return;
      const { error } = await supabase
        .from("rockscout_posts")
        .delete()
        .eq("id", postId);
      if (error) throw error;
    },
    onSuccess: () => {
      toast.success("Post deleted");
      navigate("/app/community");
    },
    onError: () => toast.error("Failed to delete post"),
  });

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <Loader2 className="h-6 w-6 animate-spin text-primary" />
      </div>
    );
  }

  if (!post) return <NotFound />;

  return (
    <div className="space-y-5">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => navigate("/app/community")}
        className="gap-2"
      >
        <ArrowLeft className="h-4 w-4" />
        Back to Community
      </Button>

      {/* Post */}
      <div className="dark-card sculpted-raised rounded-xl overflow-hidden">
        <div className="flex items-center gap-3 p-4">
          <UserAvatar
            imagePath={post.owner_avatar_path}
            displayName={post.owner_name ?? "Anonymous"}
            size="sm"
            showName={false}
          />
          <div className="min-w-0 flex-1">
            <button
              className="text-left text-sm font-semibold text-foreground hover:text-primary hover:underline"
              onClick={() => navigate(`/app/profile/${post.user_id}`)}
            >
              {post.owner_name ?? "Anonymous"}
            </button>
            <p className="text-xs text-muted-foreground">
              {formatTime(post.created_at)}
            </p>
          </div>
          {user?.id === post.user_id && (
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8 text-muted-foreground hover:text-destructive"
              onClick={() => deletePost.mutate()}
              disabled={deletePost.isPending}
            >
              <Trash2 className="h-4 w-4" />
            </Button>
          )}
        </div>

        {post.image_uri && (
          <div className="w-full overflow-hidden bg-muted/20">
            <img
              src={post.image_uri}
              alt={post.title}
              className="max-h-[400px] w-full object-contain"
              onError={(e) => {
                (e.target as HTMLImageElement).src = "/placeholder.svg";
              }}
            />
          </div>
        )}

        <div className="p-4">
          <h2 className="font-display text-lg font-bold text-foreground">
            {post.title}
          </h2>
          {post.tagline && (
            <p className="mt-0.5 text-sm text-primary">{post.tagline}</p>
          )}
          {post.caption && (
            <p className="mt-2 text-sm leading-relaxed text-muted-foreground">
              {post.caption}
            </p>
          )}
          {post.location_text && (
            <p className="mt-2 text-xs text-muted-foreground">
              📍 {post.location_text}
            </p>
          )}

          <div className="mt-3 flex items-center gap-4 border-t border-border pt-3">
            <button
              className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-primary"
              onClick={() => toggleLike.mutate()}
              disabled={!user || toggleLike.isPending}
            >
              <Heart
                className={`h-4 w-4 ${post.liked_by_me ? "fill-primary text-primary" : ""}`}
              />
              {post.like_count ?? 0}
            </button>
            <span className="flex items-center gap-1.5 text-sm text-muted-foreground">
              <MessageCircle className="h-4 w-4" />
              {post.comment_count ?? 0}
            </span>
          </div>
        </div>
      </div>

      {/* Comments */}
      <div className="space-y-3">
        <h3 className="font-display text-base font-semibold text-foreground">
          Comments
        </h3>

        {comments && comments.length > 0 ? (
          <div className="space-y-2">
            {comments.map((comment) => (
              <div
                key={comment.id}
                className="dark-card sculpted-raised rounded-lg p-3"
              >
                <div className="flex items-center gap-2">
                  <UserAvatar
                    imagePath={comment.author_avatar_path}
                    displayName={comment.author_name ?? "Anonymous"}
                    size="xs"
                    showName={false}
                  />
                  <button
                    className="text-left text-sm font-medium text-foreground hover:text-primary hover:underline"
                    onClick={() => navigate(`/app/profile/${comment.user_id}`)}
                  >
                    {comment.author_name ?? "Anonymous"}
                  </button>
                  <span className="ml-auto text-xs text-muted-foreground">
                    {formatTime(comment.created_at)}
                  </span>
                </div>
                <p className="mt-1.5 text-sm text-muted-foreground">
                  {comment.body}
                </p>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-sm text-muted-foreground">No comments yet.</p>
        )}

        {/* Add comment */}
        {user && (
          <div className="flex gap-2">
            <Input
              value={commentBody}
              onChange={(e) => setCommentBody(e.target.value)}
              placeholder="Write a comment..."
              onKeyDown={(e) => {
                if (e.key === "Enter" && commentBody.trim()) {
                  addComment.mutate();
                }
              }}
            />
            <Button
              size="icon"
              onClick={() => addComment.mutate()}
              disabled={!commentBody.trim() || addComment.isPending}
            >
              {addComment.isPending ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Send className="h-4 w-4" />
              )}
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}
