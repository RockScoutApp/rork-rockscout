import { useNavigate } from "react-router-dom";
import { Bell, Download, LogOut, ChevronRight, Smartphone } from "lucide-react";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { useAuth } from "@/hooks/useAuth";
import { usePushNotifications } from "@/hooks/usePushNotifications";
import { SculptedCard, SculptedButton, ScreenScaffold } from "@/components/sculpted";

const AQUA_HEX = "20 62% 65%";
const CITRINE_HEX = "36 80% 58%";
const CYAN_HEX = "174 100% 45%";

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
    <ScreenScaffold title="Settings" onBack={() => window.history.back()}>
      <div className="space-y-5 px-4 pb-8">
        <p className="text-sm text-muted-foreground">
          Notifications, offline storage, and account.
        </p>

        {/* Push notifications */}
        <SculptedCard accent="citrine" className="p-5">
          <div className="flex items-center gap-3">
            <div
              className="icon-badge glowing-border flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CITRINE_HEX, ["--glow-color" as string]: CITRINE_HEX, color: `hsl(${CITRINE_HEX})` }}
            >
              <Bell className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <p className="font-display text-sm font-bold text-foreground">
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
              <p className="text-xs font-bold text-muted-foreground">Categories</p>
              {push.categories.map((cat) => (
                <div key={cat.id} className="flex items-center justify-between gap-3">
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
              <SculptedButton
                accent="aqua"
                size="sm"
                className="mt-2 w-full"
                disabled={push.loading}
                onClick={() => void push.sendTest()}
              >
                Send a test notification
              </SculptedButton>
            </div>
          )}
        </SculptedCard>

        {/* Offline downloads */}
        <SculptedCard
          accent="cyan"
          interactive
          className="overflow-hidden"
          onClick={() => navigate("/app/offline")}
        >
          <div className="flex items-center gap-3 p-4">
            <div
              className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: CYAN_HEX, color: `hsl(${CYAN_HEX})` }}
            >
              <Download className="h-5 w-5" />
            </div>
            <div className="flex-1">
              <p className="font-display text-sm font-bold text-foreground">
                Offline Downloads
              </p>
              <p className="text-xs text-muted-foreground">
                Cache specimen photos, guides, and maps for field trips.
              </p>
            </div>
            <ChevronRight className="h-5 w-5 text-muted-foreground" />
          </div>
        </SculptedCard>

        {/* Account */}
        <SculptedCard accent="aqua" className="p-5">
          <div className="flex items-center gap-3">
            <div
              className="icon-badge flex h-10 w-10 shrink-0 items-center justify-center rounded-xl"
              style={{ ["--badge-accent" as string]: AQUA_HEX, color: `hsl(${AQUA_HEX})` }}
            >
              <Smartphone className="h-5 w-5" />
            </div>
            <div>
              <p className="font-display text-sm font-bold text-foreground">Account</p>
              <p className="text-xs text-muted-foreground">{user?.email}</p>
            </div>
          </div>
          <SculptedButton
            accent="danger"
            className="mt-4 w-full"
            onClick={handleSignOut}
          >
            <LogOut className="h-4 w-4" />
            Sign out
          </SculptedButton>
        </SculptedCard>
      </div>
    </ScreenScaffold>
  );
}
