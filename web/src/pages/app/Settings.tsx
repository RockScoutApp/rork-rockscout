import { useNavigate } from "react-router-dom";
import { Bell, Download, LogOut, ChevronRight, Smartphone } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/hooks/useAuth";
import { usePushNotifications } from "@/hooks/usePushNotifications";
import { toast } from "sonner";

/**
 * Settings page — push notification opt-in, offline downloads entry, sign out.
 */
export default function Settings() {
  const { user, signOut } = useAuth();
  const navigate = useNavigate();
  const push = usePushNotifications();

  const handleSignOut = async () => {
    await signOut();
    navigate("/app");
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-display text-2xl font-bold text-foreground md:text-3xl">
          Settings
        </h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Notifications, offline storage, and account.
        </p>
      </div>

      {/* Push notifications */}
      <div className="rounded-xl border border-border bg-card p-5">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/15">
            <Bell className="h-5 w-5 text-primary" />
          </div>
          <div className="flex-1">
            <p className="font-display text-sm font-semibold text-foreground">
              Push Notifications
            </p>
            <p className="text-xs text-muted-foreground">
              {push.supported
                ? push.subscribed
                  ? "Enabled — you'll receive notifications on this device."
                  : "Get alerts on your lock screen even when the app is closed."
                : "Not supported on this browser."}
            </p>
          </div>
          {push.supported && (
            <Switch
              checked={push.subscribed}
              disabled={push.loading || !push.supported}
              onCheckedChange={(checked) => {
                if (checked) void push.subscribe();
                else void push.unsubscribe();
              }}
            />
          )}
        </div>

        {push.supported && push.permission === "denied" && !push.subscribed && (
          <p className="mt-3 rounded-lg bg-amber-500/10 px-3 py-2 text-xs text-amber-600 dark:text-amber-400">
            Notification permission was blocked. Enable it in your browser/site
            settings to allow push.
          </p>
        )}

        {push.supported && push.subscribed && (
          <div className="mt-4 space-y-2 border-t border-border pt-4">
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
          </div>
        )}
      </div>

      {/* Offline downloads */}
      <button
        onClick={() => navigate("/app/offline")}
        className="flex w-full items-center gap-3 rounded-xl border border-border bg-card p-5 text-left transition-colors hover:bg-muted/40"
      >
        <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/15">
          <Download className="h-5 w-5 text-primary" />
        </div>
        <div className="flex-1">
          <p className="font-display text-sm font-semibold text-foreground">
            Offline Downloads
          </p>
          <p className="text-xs text-muted-foreground">
            Cache specimen photos, guides, and maps for field trips.
          </p>
        </div>
        <ChevronRight className="h-5 w-5 text-muted-foreground" />
      </button>

      {/* Device info */}
      <div className="rounded-xl border border-border bg-card p-5">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-muted">
            <Smartphone className="h-5 w-5 text-muted-foreground" />
          </div>
          <div>
            <p className="font-display text-sm font-semibold text-foreground">
              Account
            </p>
            <p className="text-xs text-muted-foreground">{user?.email}</p>
          </div>
        </div>
        <Button
          variant="outline"
          className="mt-4 w-full gap-2"
          onClick={handleSignOut}
        >
          <LogOut className="h-4 w-4" />
          Sign out
        </Button>
      </div>
    </div>
  );
}
