import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Bell,
  Check,
  Trash2,
  Loader2,
  UserPlus,
  MessageSquare,
  Heart,
  ArrowRightLeft,
  Sparkles,
} from "lucide-react";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import { usePushNotifications } from "@/hooks/usePushNotifications";
import { toast } from "sonner";
import { SculptedCard, SculptedButton, SculptedIconButton, ScreenScaffold } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";

interface Notification {
  id: string;
  type: string;
  actor_id: string | null;
  ref_id: string | null;
  body: string;
  read_at: string | null;
  created_at: string;
}

const NOTIFICATION_ICONS: Record<string, typeof Bell> = {
  new_post: Heart,
  friend_request: UserPlus,
  message: MessageSquare,
  trade_interest: ArrowRightLeft,
  engagement: Sparkles,
};

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

export default function Notifications() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [showSettings, setShowSettings] = useState(false);
  const push = usePushNotifications();

  const { data: notifications, isLoading } = useQuery<Notification[]>({
    queryKey: ["notifications", user?.id],
    queryFn: async () => {
      if (!user) return [];
      const { data, error } = await supabase
        .from("rockscout_notifications")
        .select("*")
        .eq("user_id", user.id)
        .order("created_at", { ascending: false })
        .limit(50);
      if (error) throw error;
      return (data ?? []) as unknown as Notification[];
    },
    enabled: !!user,
  });

  const markAllRead = useMutation({
    mutationFn: async () => {
      if (!user) return;
      const { error } = await supabase
        .from("rockscout_notifications")
        .update({ read_at: new Date().toISOString() })
        .eq("user_id", user.id)
        .is("read_at", null);
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
    onError: () => toast.error("Failed to mark as read"),
  });

  const deleteNotification = useMutation({
    mutationFn: async (id: string) => {
      const { error } = await supabase
        .from("rockscout_notifications")
        .delete()
        .eq("id", id);
      if (error) throw error;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notifications"] });
    },
  });

  const unreadCount = (notifications ?? []).filter((n) => !n.read_at).length;

  if (!user) {
    return (
      <ScreenScaffold title="Notifications">
        <div className="flex flex-col items-center justify-center gap-3 px-4 py-16 text-center">
          <Bell className="h-10 w-10 text-muted-foreground" />
          <p className="text-muted-foreground">Sign in to view notifications</p>
        </div>
      </ScreenScaffold>
    );
  }

  return (
    <ScreenScaffold title="Notifications">
     <div className="space-y-5 px-4 pb-8">
      <div className="flex items-center justify-between gap-3">
        <p className="text-sm text-muted-foreground">
          {unreadCount > 0 ? `${unreadCount} unread` : "All caught up"}
        </p>
        <div className="flex gap-2">
          {unreadCount > 0 && (
            <SculptedButton accent="aqua" size="sm"
              onClick={() => markAllRead.mutate()}
              disabled={markAllRead.isPending}>
              <Check className="h-4 w-4" />
              Mark all read
            </SculptedButton>
          )}
          <SculptedIconButton accent="citrine" size="sm"
            onClick={() => setShowSettings((v) => !v)}>
            <Bell className="h-4 w-4" style={{ color: `hsl(${CITRINE_HEX})` }} />
          </SculptedIconButton>
        </div>
      </div>

      {/* Push opt-in + settings panel */}
      {showSettings && (
        <SculptedCard accent="citrine" className="space-y-4 p-4">
          <div className="flex items-center justify-between gap-3">
            <div className="flex-1">
              <h3 className="font-display text-sm font-semibold text-foreground">
                Push notifications
              </h3>
              <p className="mt-0.5 text-xs text-muted-foreground">
                {push.supported
                  ? push.subscribed
                    ? "Enabled on this device."
                    : "Get lock-screen alerts even when the app is closed."
                  : "Not supported on this browser."}
              </p>
            </div>
            {push.supported && (
              <Switch
                checked={push.subscribed}
                disabled={push.loading}
                onCheckedChange={(checked) => {
                  if (checked) void push.subscribe();
                  else void push.unsubscribe();
                }}
              />
            )}
          </div>

          {push.supported && push.permission === "denied" && !push.subscribed && (
            <p className="rounded-lg bg-amber-500/10 px-3 py-2 text-xs text-amber-600 dark:text-amber-400">
              Notification permission was blocked. Enable it in your browser site
              settings to allow push.
            </p>
          )}

          {push.supported && push.subscribed && (
            <div className="space-y-2 border-t border-border pt-3">
              <p className="text-xs font-medium text-muted-foreground">
                Categories
              </p>
              {push.categories.map((cat) => (
                <div
                  key={cat.id}
                  className="flex items-center justify-between gap-3"
                >
                  <Label htmlFor={`push-${cat.id}`} className="text-sm">
                    {cat.label}
                  </Label>
                  <Switch
                    id={`push-${cat.id}`}
                    checked={push.selectedCategories.has(cat.id)}
                    onCheckedChange={() => push.toggleCategory(cat.id)}
                  />
                </div>
              ))}
              <p className="pt-1 text-xs text-muted-foreground">
                Category changes apply to the next notification. To update this
                device's categories now, disable and re-enable push.
              </p>
              <SculptedButton accent="aqua" size="sm" className="mt-2 w-full"
                disabled={push.loading}
                onClick={() => void push.sendTest()}>
                Send a test notification
              </SculptedButton>
            </div>
          )}
        </SculptedCard>
      )}

      {isLoading ? (
        <div className="flex justify-center py-12">
          <Loader2 className="h-6 w-6 animate-spin text-primary" />
        </div>
      ) : notifications && notifications.length > 0 ? (
        <div className="space-y-2">
          {notifications.map((notif) => {
            const Icon = NOTIFICATION_ICONS[notif.type] ?? Bell;
            const isUnread = !notif.read_at;
            return (
              <SculptedCard key={notif.id} accent={isUnread ? "citrine" : "aqua"} className={`flex items-start gap-3 p-3 ${isUnread ? "" : "opacity-70"}`}>
                <div
                  className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg"
                  style={{ backgroundColor: isUnread ? `hsl(${CITRINE_HEX} / 0.15)` : "hsl(var(--muted))", color: isUnread ? `hsl(${CITRINE_HEX})` : "hsl(var(--muted-foreground))" }}
                >
                  <Icon className="h-4 w-4" />
                </div>
                <div className="min-w-0 flex-1">
                  <p className="text-sm text-foreground">{notif.body}</p>
                  <p className="mt-0.5 text-xs text-muted-foreground">{formatTime(notif.created_at)}</p>
                </div>
                <button
                  onClick={() => deleteNotification.mutate(notif.id)}
                  className="shrink-0 rounded-lg p-1.5 text-muted-foreground hover:bg-destructive/10 hover:text-destructive"
                  aria-label="Dismiss"
                >
                  <Trash2 className="h-3.5 w-3.5" />
                </button>
              </SculptedCard>
            );
          })}
        </div>
      ) : (
        <SculptedCard accent="aqua" className="flex flex-col items-center justify-center gap-3 py-16 text-center">
          <Bell className="h-10 w-10 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            No notifications yet. You'll see friend requests, messages, trade
            interests, and community activity here.
          </p>
        </SculptedCard>
      )}
     </div>
    </ScreenScaffold>
  );
}
