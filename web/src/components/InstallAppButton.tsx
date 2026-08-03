import { useState, useEffect, useRef } from "react";
import { Download, Crown, CheckCircle2, Share, MoreVertical, MonitorDown, ArrowRight } from "lucide-react";
import { Link } from "react-router-dom";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { usePwaInstall, type Platform } from "@/hooks/usePwaInstall";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { supabase } from "@/lib/supabase";
import { getDeviceFingerprint, getDeviceLabel } from "@/lib/deviceFingerprint";

interface DeviceRow {
  id: string;
  device_label: string;
  device_fingerprint: string;
}

const MAX_DEVICES = 2;

interface InstallGuide {
  heading: string;
  steps: string[];
}

const INSTALL_GUIDES: Record<Exclude<Platform, "unsupported">, InstallGuide> = {
  ios: {
    heading: "Add RockScout to your Home Screen",
    steps: [
      "Tap the Share button in Safari's toolbar",
      "Scroll down and tap “Add to Home Screen”",
      "Tap “Add” — RockScout installs like a native app",
    ],
  },
  "android-chrome": {
    heading: "Install RockScout from Chrome",
    steps: [
      "Tap the ⋮ menu, top right in Chrome",
      "Tap “Install app” or “Add to Home screen”",
      "Confirm — RockScout opens full screen from now on",
    ],
  },
  "desktop-chrome": {
    heading: "Install RockScout from Chrome",
    steps: [
      "Click the install icon at the right of the address bar",
      "Or open ⋮ → Cast, save and share → Install page as app",
      "Click “Install”",
    ],
  },
  "desktop-edge": {
    heading: "Install RockScout from Edge",
    steps: [
      "Click the ⋯ menu, top right in Edge",
      "Choose Apps → Install this site as an app",
      "Click “Install”",
    ],
  },
};

function GuideIcon({ platform }: { platform: Exclude<Platform, "unsupported"> }) {
  if (platform === "ios") return <Share className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
  if (platform === "android-chrome")
    return <MoreVertical className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
  return <MonitorDown className="h-3.5 w-3.5 shrink-0 text-primary" aria-hidden="true" />;
}

export interface InstallAppButtonProps {
  /** Force the button into a specific tier mode. If omitted, it follows the signed-in user's tier. */
  mode?: "free" | "premium";
  /** Visual size variant. */
  size?: "sm" | "md" | "lg";
  /** Called when the inline install guide is toggled. */
  onGuideToggle?: (open: boolean) => void;
}

/**
 * Tier-aware PWA install button.
 *
 * - Free mode: always renders an “Install Free PWA” action. No account required.
 * - Premium mode: requires the user to be signed in and have an active Premium
 *   entitlement. If not signed in, it links to the Premium install page. If
 *   signed in but not Premium, it links to the paywall.
 * - Auto mode (no `mode` prop): follows the current user's tier.
 *
 * When the browser exposes no native install prompt, the button expands the
 * platform-specific “Add to Home Screen” instructions instead of silently doing
 * nothing.
 */
export const InstallAppButton = ({
  mode,
  size = "sm",
  onGuideToggle,
}: InstallAppButtonProps) => {
  const { user } = useAuth();
  const { isPremium } = useTier();
  const { canInstall, install, installed, platform, hasNativePrompt } = usePwaInstall();
  const [installing, setInstalling] = useState<boolean>(false);
  const [showGuide, setShowGuide] = useState<boolean>(false);
  const queryClient = useQueryClient();
  const deviceRegisteredRef = useRef<boolean>(false);

  // Resolve the effective tier mode. Explicit prop wins; otherwise use the
  // signed-in user's tier (free when signed out).
  const effectiveMode: "free" | "premium" = mode ?? (isPremium ? "premium" : "free");

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
    enabled: !!user && effectiveMode === "premium",
    staleTime: 60_000,
  });

  // Register the device in Supabase when a premium user installs the PWA.
  useEffect(() => {
    if (!installed || !user || effectiveMode !== "premium" || deviceRegisteredRef.current) return;
    deviceRegisteredRef.current = true;
    const fp = getDeviceFingerprint();
    const label = getDeviceLabel();
    void (async () => {
      try {
        await supabase
          .from("rockscout_installed_devices")
          .upsert(
            { user_id: user.id, device_fingerprint: fp, device_label: label },
            { onConflict: "user_id,device_fingerprint" },
          );
        queryClient.invalidateQueries({ queryKey: ["device-count", user.id] });
        queryClient.invalidateQueries({ queryKey: ["installed-devices", user.id] });
      } catch {
        // Best-effort — the install still succeeded even if tracking fails.
      }
    })();
  }, [installed, user, effectiveMode, queryClient]);

  if (installed) {
    return (
      <span className="inline-flex items-center gap-2 rounded-full border border-green-500/40 bg-green-500/10 px-3.5 py-1.5 text-xs font-medium text-green-600 dark:text-green-400">
        <CheckCircle2 className="h-3.5 w-3.5" aria-hidden="true" />
        PWA installed
      </span>
    );
  }

  if (!canInstall || platform === "unsupported") {
    return null;
  }

  // Premium mode guard: must be signed in with an active Premium entitlement.
  if (effectiveMode === "premium" && !user) {
    return (
      <Link
        to="/install/premium"
        className="inline-flex items-center gap-2 rounded-full border border-amber-500/50 bg-gradient-to-br from-amber-500/15 to-primary/15 px-3.5 py-1.5 text-xs font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm dark:text-amber-400"
      >
        <Crown className="h-3.5 w-3.5" aria-hidden="true" />
        Install Premium PWA
      </Link>
    );
  }

  if (effectiveMode === "premium" && user && !isPremium) {
    return (
      <Link
        to="/app/paywall"
        className="inline-flex items-center gap-2 rounded-full border border-amber-500/50 bg-gradient-to-br from-amber-500/15 to-primary/15 px-3.5 py-1.5 text-xs font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm dark:text-amber-400"
      >
        <Crown className="h-3.5 w-3.5" aria-hidden="true" />
        Upgrade to Premium
        <ArrowRight className="h-3 w-3" aria-hidden="true" />
      </Link>
    );
  }

  // Premium users past their device allowance get a manage link instead.
  if (effectiveMode === "premium" && deviceCount >= MAX_DEVICES) {
    return (
      <Link
        to="/app/manage-devices"
        className="inline-flex items-center gap-2 rounded-full border border-border bg-card/60 px-3.5 py-1.5 text-xs font-medium text-muted-foreground transition-colors hover:bg-muted/50"
      >
        <Crown className="h-3.5 w-3.5" aria-hidden="true" />
        Device limit reached ({deviceCount}/{MAX_DEVICES}) — Manage
      </Link>
    );
  }

  const guide = INSTALL_GUIDES[platform];

  const handleClick = async () => {
    if (!hasNativePrompt) {
      setShowGuide((v) => {
        const next = !v;
        onGuideToggle?.(next);
        return next;
      });
      return;
    }
    setInstalling(true);
    try {
      await install();
    } catch {
      setShowGuide(true);
      onGuideToggle?.(true);
    } finally {
      setInstalling(false);
    }
  };

  const isPremiumMode = effectiveMode === "premium";
  const label = installing
    ? "Installing…"
    : isPremiumMode
      ? `Install Premium PWA (${deviceCount}/${MAX_DEVICES})`
      : "Install Free PWA";

  const sizeClass =
    size === "lg"
      ? "h-14 w-56 items-center justify-center gap-2.5 px-5 text-base"
      : "gap-2 px-3.5 py-1.5 text-xs";

  const buttonClass = isPremiumMode
    ? `inline-flex rounded-full ${sizeClass} border border-amber-500/50 bg-gradient-to-br from-amber-500/15 to-primary/15 font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm disabled:opacity-50 dark:text-amber-400`
    : `inline-flex rounded-full ${sizeClass} border border-primary/50 bg-primary/10 font-semibold text-primary transition-all hover:border-primary hover:bg-primary/15 hover:shadow-sm disabled:opacity-50`;

  return (
    <div className="flex flex-col items-start gap-2">
      <button
        type="button"
        onClick={handleClick}
        disabled={installing}
        aria-expanded={hasNativePrompt ? undefined : showGuide}
        className={buttonClass}
      >
        {isPremiumMode ? (
          <Crown className="h-3.5 w-3.5" aria-hidden="true" />
        ) : (
          <Download className="h-3.5 w-3.5" aria-hidden="true" />
        )}
        {label}
      </button>

      {showGuide && (
        <div className="w-full max-w-xs animate-in fade-in slide-in-from-top-1 rounded-xl border border-border bg-card/90 p-3 text-left shadow-lg backdrop-blur-sm duration-200">
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
      )}
    </div>
  );
};
