import { useState, useCallback, useEffect, useRef } from "react";
import { useNavigate, Link } from "react-router-dom";
import {
  Crown,
  LogOut,
  Loader2,
  AlertCircle,
  Download,
  CheckCircle2,
  Share,
  MoreVertical,
  MonitorDown,
  Smartphone,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { usePwaInstall, type Platform } from "@/hooks/usePwaInstall";
import { supabase } from "@/lib/supabase";
import { getDeviceFingerprint, getDeviceLabel } from "@/lib/deviceFingerprint";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { cn } from "@/lib/utils";

const MAX_DEVICES = 3;

interface ProfileRow {
  is_pro: boolean;
}

interface DeviceRow {
  id: string;
  device_fingerprint: string;
  installed_at: string;
}

const INSTALL_GUIDES: Record<Exclude<Platform, "unsupported">, { heading: string; steps: string[] }> = {
  ios: {
    heading: "Add RockScout to your Home Screen",
    steps: [
      "Tap the Share button in Safari's toolbar",
      "Scroll down and tap \u201cAdd to Home Screen\u201d",
      "Tap \u201cAdd\u201d \u2014 RockScout installs like a native app",
    ],
  },
  "android-chrome": {
    heading: "Install RockScout from Chrome",
    steps: [
      "Tap the \u22EE menu, top right in Chrome",
      "Tap \u201cInstall app\u201d or \u201cAdd to Home screen\u201d",
      "Confirm \u2014 RockScout opens full screen from now on",
    ],
  },
  "desktop-chrome": {
    heading: "Install RockScout from Chrome",
    steps: [
      "Click the install icon at the right of the address bar",
      "Or open \u22ee \u2192 Cast, save and share \u2192 Install page as app",
      "Click \u201cInstall\u201d",
    ],
  },
  "desktop-edge": {
    heading: "Install RockScout from Edge",
    steps: [
      "Click the \u22ef menu, top right in Edge",
      "Choose Apps \u2192 Install this site as an app",
      "Click \u201cInstall\u201d",
    ],
  },
};

function GuideIcon({ platform }: { platform: Exclude<Platform, "unsupported"> }) {
  if (platform === "ios") return <Share className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
  if (platform === "android-chrome")
    return <MoreVertical className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
  return <MonitorDown className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
}

/**
 * Dialog states — the flow progresses linearly:
 *
 * 1. `form` — sign-in / sign-up form (user is not signed in)
 * 2. `checking` — verifying premium status with Supabase
 * 3. `premium` — user is premium, show the install button
 * 4. `free` — user signed in but is on the free tier, show upgrade link
 * 5. `device-limit` — premium user at 3 devices, show manage devices link
 * 6. `installed` — PWA was successfully installed
 */
type DialogState = "form" | "checking" | "premium" | "free" | "device-limit" | "installed";

export interface PremiumInstallDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

/**
 * Shared premium PWA install dialog.
 *
 * This is the single source of truth for the premium install flow:
 * 1. User clicks the Premium PWA button (navbar pill or landing CTA)
 * 2. This dialog opens with a sign-in form
 * 3. After sign-in, premium status is checked directly from Supabase
 * 4. If premium and under the device limit, the install button appears
 * 5. Clicking install triggers the native PWA install prompt and registers
 *    the device fingerprint in Supabase
 *
 * No email verification code is needed — these users sign in with the same
 * email they use in the app, so their Supabase accounts are already
 * confirmed. The device fingerprint (localStorage UUID + screen + timezone +
 * platform) tracks the 3-device limit, which is more reliable than IP.
 */
export function PremiumInstallDialog({ open, onOpenChange }: PremiumInstallDialogProps) {
  const { user, signIn, signUp, signOut, error, clearError } = useAuth();
  const { install, installed, hasNativePrompt, platform } = usePwaInstall();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [authMode, setAuthMode] = useState<"signin" | "signup">("signin");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [installing, setInstalling] = useState(false);
  const [statusMsg, setStatusMsg] = useState<string | null>(null);
  const [state, setState] = useState<DialogState>("form");
  const deviceRegisteredRef = useRef<boolean>(false);

  // Device count query — only runs when we know the user is premium
  const { data: deviceCount = 0 } = useQuery<number>({
    queryKey: ["device-count", user?.id],
    queryFn: async () => {
      if (!user) return 0;
      const { count, error } = await supabase
        .from("rockscout_installed_devices")
        .select("*", { count: "exact", head: true })
        .eq("user_id", user.id);
      if (error) return 0;
      return count ?? 0;
    },
    enabled: !!user && (state === "premium" || state === "device-limit" || state === "installed"),
    staleTime: 30_000,
  });

  // Reset everything when the dialog closes
  const resetForm = useCallback(() => {
    setEmail("");
    setPassword("");
    setStatusMsg(null);
    setAuthMode("signin");
    setInstalling(false);
    setSubmitting(false);
    setState("form");
    deviceRegisteredRef.current = false;
    clearError();
  }, [clearError]);

  // When the dialog opens and the user is already signed in, jump to the
  // appropriate state immediately instead of showing the sign-in form.
  useEffect(() => {
    if (!open) return;
    if (user) {
      void checkPremiumAndSetState();
    } else {
      setState("form");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, user]);

  /**
   * Checks premium status directly from Supabase and transitions to the
   * correct dialog state. This avoids the stale-closure issue where useTier's
   * isPremium is captured from the render before sign-in completes.
   */
  const checkPremiumAndSetState = useCallback(async () => {
    setState("checking");
    try {
      const { data: authData } = await supabase.auth.getUser();
      const userId = authData.user?.id;
      if (!userId) {
        setState("form");
        return;
      }
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("is_pro")
        .eq("id", userId)
        .maybeSingle();
      const isPro = (data as ProfileRow | null)?.is_pro ?? false;
      if (!isPro) {
        setState("free");
        setStatusMsg(
          "You're signed in on the Free tier. Upgrade to Premium to install the Premium PWA and unlock all features.",
        );
        return;
      }
      // Check device count
      const { count } = await supabase
        .from("rockscout_installed_devices")
        .select("*", { count: "exact", head: true })
        .eq("user_id", userId);
      const currentCount = count ?? 0;
      // If this device is already registered, it doesn't count against the limit
      const myFp = getDeviceFingerprint();
      const { data: existingDevice } = await supabase
        .from("rockscout_installed_devices")
        .select("id")
        .eq("user_id", userId)
        .eq("device_fingerprint", myFp)
        .maybeSingle();
      if (existingDevice) {
        // This device is already registered — install is always allowed
        setState("premium");
        queryClient.invalidateQueries({ queryKey: ["device-count", userId] });
        return;
      }
      if (currentCount >= MAX_DEVICES) {
        setState("device-limit");
        setStatusMsg(
          `You've reached the ${MAX_DEVICES}-device limit. Remove a device to install on this one.`,
        );
        return;
      }
      setState("premium");
      queryClient.invalidateQueries({ queryKey: ["device-count", userId] });
    } catch {
      setState("form");
    }
  }, [queryClient]);

  // Register the device in Supabase when the PWA is installed.
  useEffect(() => {
    if (!installed || !user || deviceRegisteredRef.current) return;
    // Only register if we're in the premium install flow
    if (state !== "premium" && state !== "installed") return;
    deviceRegisteredRef.current = true;
    const fp = getDeviceFingerprint();
    const label = getDeviceLabel();
    void (async () => {
      try {
        await supabase
          .from("rockscout_installed_devices")
          .upsert(
            {
              user_id: user.id,
              device_fingerprint: fp,
              device_label: label,
              device_platform: "web",
              last_seen_at: new Date().toISOString(),
            },
            { onConflict: "user_id,device_fingerprint" },
          );
        queryClient.invalidateQueries({ queryKey: ["device-count", user.id] });
        queryClient.invalidateQueries({ queryKey: ["installed-devices", user.id] });
        queryClient.invalidateQueries({ queryKey: ["device-limit", user.id] });
      } catch {
        // Best-effort — the install still succeeded even if tracking fails.
      }
    })();
  }, [installed, user, state, queryClient]);

  // Transition to "installed" state when the PWA is installed
  useEffect(() => {
    if (installed && state === "premium") {
      setState("installed");
    }
  }, [installed, state]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setStatusMsg(null);
    clearError();
    try {
      if (authMode === "signup") {
        await signUp(email, password);
        // After signup, the user is signed in (if email confirmation is disabled)
        // or needs to verify email (if enabled). We check premium status either way.
        await checkPremiumAndSetState();
      } else {
        await signIn(email, password);
        await checkPremiumAndSetState();
      }
    } catch {
      // error is set in the auth hook
    } finally {
      setSubmitting(false);
    }
  };

  const handleInstallClick = async () => {
    if (!hasNativePrompt) {
      // No native prompt — can't install programmatically, show instructions
      return;
    }
    setInstalling(true);
    try {
      await install();
    } catch {
      // Fall back to instructions (already shown below)
    } finally {
      setInstalling(false);
    }
  };

  const handleSignOut = async () => {
    await signOut();
    resetForm();
  };

  const dialogTitle = (() => {
    switch (state) {
      case "installed":
        return "Premium PWA installed!";
      case "premium":
        return "Premium verified!";
      case "free":
        return "Free tier account";
      case "device-limit":
        return "Device limit reached";
      case "checking":
        return "Checking account\u2026";
      default:
        return authMode === "signin" ? "Sign in to install" : "Create account";
    }
  })();

  const dialogDescription = (() => {
    switch (state) {
      case "installed":
        return "RockScout Premium is installed on this device. Open it to start using all premium features.";
      case "premium":
        return "Click the button below to install the Premium PWA on this device.";
      case "free":
        return "Upgrade to Premium to install the Premium PWA and unlock all features.";
      case "device-limit":
        return `You've reached the ${MAX_DEVICES}-device limit. Remove a device to install on this one.`;
      case "checking":
        return "Verifying your Premium status\u2026";
      default:
        return "Sign in with your Premium account to install the RockScout Premium PWA.";
    }
  })();

  return (
    <Dialog
      open={open}
      onOpenChange={(open) => {
        onOpenChange(open);
        if (!open) resetForm();
      }}
    >
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 font-display text-xl">
            <Crown className="h-5 w-5 text-amber-500" />
            {dialogTitle}
          </DialogTitle>
          <DialogDescription>{dialogDescription}</DialogDescription>
        </DialogHeader>

        {statusMsg && state !== "installed" && (
          <div
            className={cn(
              "rounded-lg border p-3 text-sm",
              state === "premium"
                ? "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
                : state === "free" || state === "device-limit"
                  ? "border-amber-500/40 bg-amber-500/10 text-amber-600 dark:text-amber-400"
                  : "border-primary/30 bg-primary/10 text-primary",
            )}
          >
            {statusMsg}
          </div>
        )}

        {error && (
          <div className="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
            <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {/* INSTALLED state — success confirmation */}
        {state === "installed" && (
          <div className="space-y-3">
            <div className="flex items-center justify-center gap-2 rounded-lg border border-emerald-500/40 bg-emerald-500/10 p-4 text-sm font-medium text-emerald-600 dark:text-emerald-400">
              <CheckCircle2 className="h-5 w-5" />
              Premium PWA installed!
            </div>
            <Button
              type="button"
              onClick={() => {
                onOpenChange(false);
                navigate("/app");
              }}
              size="lg"
              className="w-full inline-flex items-center gap-2 rounded-full bg-amber-500 text-stone-950 hover:bg-amber-400"
            >
              Open RockScout
            </Button>
            <button
              type="button"
              onClick={handleSignOut}
              className="w-full text-center text-xs text-muted-foreground hover:text-foreground"
            >
              Sign out
            </button>
          </div>
        )}

        {/* PREMIUM state — show install button + device count */}
        {state === "premium" && (
          <div className="space-y-3">
            <Button
              type="button"
              onClick={handleInstallClick}
              disabled={installing || !hasNativePrompt}
              size="lg"
              className="w-full inline-flex items-center gap-2 rounded-full bg-amber-500 text-stone-950 hover:bg-amber-400"
            >
              {installing ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Download className="h-4 w-4" />
              )}
              {installing ? "Installing\u2026" : "Install Premium PWA"}
            </Button>
            <p className="text-center text-xs text-muted-foreground">
              Device {deviceCount}/{MAX_DEVICES} registered
            </p>
            {!hasNativePrompt && platform !== "unsupported" && (
              <InstallGuide platform={platform} />
            )}
            {!hasNativePrompt && platform === "unsupported" && (
              <p className="text-center text-xs text-muted-foreground">
                Use your browser's menu to install this site as an app.
              </p>
            )}
            <button
              type="button"
              onClick={handleSignOut}
              className="w-full text-center text-xs text-muted-foreground hover:text-foreground"
            >
              Sign out
            </button>
          </div>
        )}

        {/* FREE tier state — show upgrade link */}
        {state === "free" && (
          <div className="space-y-3">
            <Link
              to="/app/paywall"
              onClick={() => onOpenChange(false)}
              className="block w-full rounded-full border border-amber-500/40 bg-amber-500/10 px-4 py-2.5 text-center text-sm font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm dark:text-amber-400"
            >
              <Crown className="mr-1.5 inline h-4 w-4" />
              Upgrade to Premium
            </Link>
            <button
              type="button"
              onClick={() => {
                onOpenChange(false);
                navigate("/app");
              }}
              className="block w-full text-center text-xs text-muted-foreground hover:text-foreground"
            >
              Continue with Free tier →
            </button>
            <button
              type="button"
              onClick={handleSignOut}
              className="block w-full text-center text-xs text-muted-foreground hover:text-foreground"
            >
              Sign out
            </button>
          </div>
        )}

        {/* DEVICE LIMIT state — show manage devices link */}
        {state === "device-limit" && (
          <div className="space-y-3">
            <Link
              to="/app/manage-devices"
              onClick={() => onOpenChange(false)}
              className="block w-full rounded-full border border-border bg-card/60 px-4 py-2.5 text-center text-sm font-medium text-foreground transition-colors hover:bg-muted/50"
            >
              <Smartphone className="mr-1.5 inline h-4 w-4" />
              Manage devices ({deviceCount}/{MAX_DEVICES})
            </Link>
            <p className="text-center text-xs text-muted-foreground">
              Remove an old device, then come back to install on this one.
            </p>
            <button
              type="button"
              onClick={handleSignOut}
              className="w-full text-center text-xs text-muted-foreground hover:text-foreground"
            >
              Sign out
            </button>
          </div>
        )}

        {/* CHECKING state — spinner */}
        {state === "checking" && (
          <div className="flex items-center justify-center py-6">
            <Loader2 className="h-6 w-6 animate-spin text-primary" />
          </div>
        )}

        {/* FORM state — sign-in / sign-up */}
        {state === "form" && (
          <>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="premium-install-email">Email</Label>
                <Input
                  id="premium-install-email"
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="you@example.com"
                  autoComplete="email"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="premium-install-password">Password</Label>
                <Input
                  id="premium-install-password"
                  type="password"
                  required
                  minLength={6}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="At least 6 characters"
                  autoComplete={authMode === "signin" ? "current-password" : "new-password"}
                />
              </div>
              <Button type="submit" disabled={submitting} className="w-full" size="lg">
                {submitting ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : authMode === "signin" ? (
                  "Sign in"
                ) : (
                  "Create account"
                )}
              </Button>
            </form>

            <div className="text-center text-sm text-muted-foreground">
              {authMode === "signin" ? (
                <>
                  Don&apos;t have an account?{" "}
                  <button
                    onClick={() => {
                      setAuthMode("signup");
                      clearError();
                      setStatusMsg(null);
                    }}
                    className="font-medium text-primary hover:underline"
                  >
                    Sign up
                  </button>
                </>
              ) : (
                <>
                  Already have an account?{" "}
                  <button
                    onClick={() => {
                      setAuthMode("signin");
                      clearError();
                      setStatusMsg(null);
                    }}
                    className="font-medium text-primary hover:underline"
                  >
                    Sign in
                  </button>
                </>
              )}
            </div>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}

/**
 * Platform-specific install instructions shown inside the dialog when the
 * browser doesn't fire `beforeinstallprompt` (iOS Safari, Firefox, etc.).
 */
function InstallGuide({ platform }: { platform: Exclude<Platform, "unsupported"> }) {
  const guide = INSTALL_GUIDES[platform];
  return (
    <div className="rounded-xl border border-border bg-card/90 p-3 text-left shadow-sm">
      <p className="mb-2 flex items-center gap-1.5 text-xs font-semibold text-foreground">
        <GuideIcon platform={platform} />
        {guide.heading}
      </p>
      <ol className="space-y-1.5">
        {guide.steps.map((step, i) => (
          <li key={step} className="flex gap-2 text-xs leading-relaxed text-muted-foreground">
            <span className="grid h-4 w-4 shrink-0 place-items-center rounded-full bg-primary/15 text-[10px] font-bold text-primary">
              {i + 1}
            </span>
            {step}
          </li>
        ))}
      </ol>
    </div>
  );
}
