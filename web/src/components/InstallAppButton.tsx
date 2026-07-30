import { useState } from "react";
import { Download, Crown, CheckCircle2, Share, MoreVertical, MonitorDown } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { usePwaInstall, type Platform } from "@/hooks/usePwaInstall";
import { useQuery } from "@tanstack/react-query";
import { supabase } from "@/lib/supabase";

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

/**
 * Manual "Add to Home Screen" instructions, used when the browser never fires
 * `beforeinstallprompt` (iOS Safari always, Firefox, and Chrome before the
 * engagement heuristic fires). Without these the install button was a dead
 * control — tapping it called a prompt that did not exist.
 */
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

/**
 * Tier-aware PWA install button.
 *
 * - Free user: "Install free PWA" — unlimited installs, no device tracking.
 * - Premium user: "Install Premium PWA" — up to 2 additional devices, tracked.
 *   When the limit is reached, shows a "Device limit reached" badge.
 * - Already installed: shows a "PWA installed" check.
 *
 * When the browser exposes no native install prompt, the button expands
 * step-by-step instructions instead of doing nothing.
 */
export const InstallAppButton = () => {
  const { user } = useAuth();
  const { isPremium } = useTier();
  const { canInstall, install, installed, platform, hasNativePrompt } = usePwaInstall();
  const [installing, setInstalling] = useState<boolean>(false);
  const [showGuide, setShowGuide] = useState<boolean>(false);

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
    enabled: !!user && isPremium,
    staleTime: 60_000,
  });

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

  // Premium users past their device allowance get a manage link instead.
  if (isPremium && deviceCount >= MAX_DEVICES) {
    return (
      <a
        href="/app/manage-devices"
        className="inline-flex items-center gap-2 rounded-full border border-border bg-card/60 px-3.5 py-1.5 text-xs font-medium text-muted-foreground transition-colors hover:bg-muted/50"
      >
        <Crown className="h-3.5 w-3.5" aria-hidden="true" />
        Device limit reached ({deviceCount}/{MAX_DEVICES}) — Manage
      </a>
    );
  }

  const guide = INSTALL_GUIDES[platform];

  const handleClick = async () => {
    // No native prompt available (iOS Safari, Firefox, pre-heuristic Chrome):
    // surface the manual steps rather than silently doing nothing.
    if (!hasNativePrompt) {
      setShowGuide((v) => !v);
      return;
    }
    setInstalling(true);
    try {
      await install();
    } catch {
      // The prompt can be dismissed or rejected by the browser — fall back to
      // showing the manual steps so the user is never left stuck.
      setShowGuide(true);
    } finally {
      setInstalling(false);
    }
  };

  const label = installing
    ? "Installing…"
    : isPremium
      ? `Install Premium PWA (${deviceCount}/${MAX_DEVICES})`
      : "Install free PWA";

  const buttonClass = isPremium
    ? "inline-flex items-center gap-2 rounded-full border border-amber-500/50 bg-gradient-to-br from-amber-500/15 to-primary/15 px-3.5 py-1.5 text-xs font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm disabled:opacity-50 dark:text-amber-400"
    : "inline-flex items-center gap-2 rounded-full border border-primary/50 bg-primary/10 px-3.5 py-1.5 text-xs font-semibold text-primary transition-all hover:border-primary hover:bg-primary/15 hover:shadow-sm disabled:opacity-50";

  return (
    <div className="flex flex-col items-start gap-2">
      <button
        type="button"
        onClick={handleClick}
        disabled={installing}
        aria-expanded={hasNativePrompt ? undefined : showGuide}
        className={buttonClass}
      >
        {isPremium ? (
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
