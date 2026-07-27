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
import NotFound from "@/pages/NotFound";

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
  const { postId } = useParams<{ postId: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [commentBody, setCommentBody] = useState("");

  const { data: post, isLoading } = useQuery<PostWithMeta | null>({
    queryKey: ["community-post", postId],
    queryFn: async () => {
      if (!postId) return null;
      const { data, error } = await supabase
        .from("rockscout_posts")
        .select("*, like_count, liked_by_me, comment_count")
        .eq("id", postId)
        .maybeSingle();
      if (error) throw error;
      return data as PostWithMeta | null;
    },
    enabled: !!postId,
  });

  const { data: comments } = useQuery<Comment[]>({
    queryKey: ["post-comments", postId],
    queryFn: async () => {
      if (!postId) return [];
      const { data, error } = await supabase
        .from("rockscout_comments")
        .select("*, author_emoji, author_name")
        .eq("post_id", postId)
        .order("created_at", { ascending: true });
      if (error) throw error;
      return (data ?? []) as Comment[];
    },
    enabled: !!postId,
  });

  const addComment = useMutation({
    mutationFn: async () => {
      if (!user || !postId || !commentBody.trim()) return;
      const { error } = await supabase.from("rockscout_comments").insert({
        post_id: postId,
        user_id: user.id,
        body: commentBody.trim(),
      });
      if (error) throw error;
    },
    onSuccess: () => {
      setCommentBody("");
      queryClient.invalidateQueries({ queryKey: ["post-comments", postId] });
      queryClient.invalidateQueries({ queryKey: ["community-post", postId] });
      toast.success("Comment added");
    },
    onError: () => toast.error("Failed to add comment"),
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
      <div className="rounded-xl border border-border bg-card overflow-hidden">
        <div className="flex items-center gap-3 p-4">
          <span className="text-2xl">{post.owner_emoji ?? "🧗"}</span>
          <div className="min-w-0 flex-1">
            <p className="text-sm font-semibold text-foreground">
              {post.owner_name ?? "Anonymous"}
            </p>
            <p className="text-xs text-muted-foreground">
              {formatTime(post.created_at)}
            </p>
          </div>
        </div>

        {post.image_uri && (
          <div className="w-full overflow-hidden bg-muted/20">
            <img
              src={post.image_uri}
              alt={post.title}
              className="max-h-[400px] w-full object-contain"
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
            <button className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-primary">
              <Heart className="h-4 w-4" />
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
                className="rounded-lg border border-border bg-card p-3"
              >
                <div className="flex items-center gap-2">
                  <span className="text-lg">{comment.author_emoji ?? "🧗"}</span>
                  <span className="text-sm font-medium text-foreground">
                    {comment.author_name ?? "Anonymous"}
                  </span>
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
