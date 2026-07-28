import { useState } from "react";
import { Download, Crown, CheckCircle2 } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { usePwaInstall } from "@/hooks/usePwaInstall";
import { useQuery } from "@tanstack/react-query";
import { supabase } from "@/lib/supabase";

interface DeviceRow {
  id: string;
  device_label: string;
  device_fingerprint: string;
}

const MAX_DEVICES = 2;

/**
 * Tier-aware PWA install button.
 *
 * - Signed out: renders nothing (the navbar AuthPill handles sign-in).
 * - Free user: "Install free PWA" — unlimited installs, no device tracking.
 * - Premium user: "Install Premium PWA" — up to 2 additional devices, tracked.
 *   When the limit is reached, shows a "Device limit reached" badge.
 * - Already installed: shows a "PWA installed" check.
 */
export const InstallAppButton = () => {
  const { user } = useAuth();
  const { isPremium, isFree } = useTier();
  const { canInstall, install, installed, platform } = usePwaInstall();
  const [installing, setInstalling] = useState(false);

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

  const handleInstall = async () => {
    setInstalling(true);
    try {
      await install();
    } finally {
      setInstalling(false);
    }
  };

  if (isFree) {
    return (
      <button
        onClick={handleInstall}
        disabled={installing}
        className="inline-flex items-center gap-2 rounded-full border border-primary/50 bg-primary/10 px-3.5 py-1.5 text-xs font-semibold text-primary transition-all hover:border-primary hover:bg-primary/15 hover:shadow-sm disabled:opacity-50"
      >
        <Download className="h-3.5 w-3.5" aria-hidden="true" />
        {installing ? "Installing…" : "Install free PWA"}
      </button>
    );
  }

  if (isPremium) {
    const atLimit = deviceCount >= MAX_DEVICES;
    if (atLimit) {
      return (
        <a
          href="/app/manage-devices"
          className="inline-flex items-center gap-2 rounded-full border border-border bg-card/60 px-3.5 py-1.5 text-xs font-medium text-muted-foreground transition-colors hover:bg-muted/50"
        >
          <Crown className="h-3.5 w-3.5" />
          Device limit reached ({deviceCount}/{MAX_DEVICES}) — Manage
        </a>
      );
    }
    return (
      <button
        onClick={handleInstall}
        disabled={installing}
        className="inline-flex items-center gap-2 rounded-full border border-amber-500/50 bg-gradient-to-br from-amber-500/15 to-primary/15 px-3.5 py-1.5 text-xs font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm disabled:opacity-50 dark:text-amber-400"
      >
        <Crown className="h-3.5 w-3.5" aria-hidden="true" />
        {installing
          ? "Installing…"
          : `Install Premium PWA (${deviceCount}/${MAX_DEVICES})`}
      </button>
    );
  }

  return null;
};
