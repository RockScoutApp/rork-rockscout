import { Link, useSearchParams } from "react-router-dom";
import { useEffect, useState } from "react";
import {
  Download,
  Crown,
  CheckCircle2,
  Smartphone,
  Monitor,
  Tablet,
  ArrowRight,
  Share,
  MoreVertical,
  MonitorDown,
} from "lucide-react";
import { Layout } from "@/components/Layout";
import { InstallAppButton } from "@/components/InstallAppButton";
import { AuthPill } from "@/components/AuthPill";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { usePwaInstall, type Platform } from "@/hooks/usePwaInstall";
import { SITE } from "@/content/legal";

/**
 * Minimal PWA install page.
 *
 * Both the "Free PWA" and "Premium PWA" navbar buttons land here. The page
 * shows a single install button — no marketing content, no tier comparison
 * cards, no feature lists.
 *
 * - Not signed in: "Install Free PWA" button + a small "Premium? Sign in" link
 * - Signed in + Premium: "Install Premium PWA" button
 * - Signed in + Free: "Install Free PWA" button + "Upgrade to Premium" link
 *
 * Platform-specific install instructions are shown in a collapsible section
 * below the button — needed for iOS Safari and manual install flows.
 */
export default function InstallPWA() {
  const { user } = useAuth();
  const { isPremium } = useTier();
  const { platform } = usePwaInstall();
  const [searchParams] = useSearchParams();
  const [justVerified, setJustVerified] = useState<boolean>(false);

  useEffect(() => {
    const verified = searchParams.get("verified");
    const email = searchParams.get("email");
    if (verified === "true" && email) {
      setJustVerified(true);
      try {
        sessionStorage.setItem("rockscout_just_verified", email);
      } catch {
        /* sessionStorage might be unavailable in private browsing */
      }
      const cleanUrl = window.location.pathname;
      window.history.replaceState({}, "", cleanUrl);
    }
  }, [searchParams]);

  return (
    <Layout
      title={`Install RockScout PWA · ${SITE.name}`}
      description="Install RockScout as a Progressive Web App on your desktop, laptop, or tablet."
    >
      <section className="relative overflow-hidden border-b border-border/40">
        <div className="geode-gradient pointer-events-none absolute inset-0 opacity-30" />
        <div className="relative mx-auto max-w-xl px-4 py-16 text-center sm:px-6 sm:py-24 md:py-28">
          <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-2xl bg-primary/15 ring-1 ring-primary/30">
            <Download className="h-8 w-8 text-primary" />
          </div>
          <h1 className="font-display text-3xl font-bold tracking-tight sm:text-4xl">
            Install RockScout
          </h1>
          <p className="mx-auto mt-3 max-w-sm text-balance text-sm text-muted-foreground sm:text-base">
            {isPremium
              ? "Install the full Premium PWA with all features unlocked."
              : "Install RockScout on your desktop or tablet — it takes seconds."}
          </p>

          {justVerified && (
            <div className="mx-auto mt-6 flex max-w-sm items-center gap-3 rounded-xl border border-emerald-500/40 bg-emerald-500/10 px-4 py-3 text-sm font-medium text-emerald-600 dark:text-emerald-400">
              <CheckCircle2 className="h-5 w-5 shrink-0" />
              <span>Your email is verified! You can now sign in.</span>
            </div>
          )}

          {/* Install button */}
          <div className="mt-8 flex flex-col items-center gap-4">
            <div className="scale-125">
              <InstallAppButton />
            </div>

            {/* Tier-aware secondary action */}
            {!user && (
              <div className="flex flex-col items-center gap-1.5">
                <p className="text-xs text-muted-foreground">
                  Have a Premium account?
                </p>
                <div className="scale-110">
                  <AuthPill />
                </div>
              </div>
            )}
            {user && !isPremium && (
              <Link
                to="/app/paywall"
                className="inline-flex items-center gap-2 rounded-full bg-gradient-to-r from-amber-500/20 to-primary/20 border border-amber-500/40 px-5 py-2.5 text-sm font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm dark:text-amber-400"
              >
                <Crown className="h-4 w-4" />
                Upgrade to Premium
                <ArrowRight className="h-4 w-4" />
              </Link>
            )}
          </div>
        </div>
      </section>

      {/* Collapsible platform instructions */}
      <section className="mx-auto max-w-2xl px-4 py-12 sm:px-6">
        <PlatformInstructions platform={platform} />
      </section>
    </Layout>
  );
}

/**
 * Platform-specific install instructions. Shown as a simple, collapsible
 * section — needed for iOS Safari (no native prompt) and manual install flows.
 */
function PlatformInstructions({ platform }: { platform: Platform }) {
  const [open, setOpen] = useState<boolean>(false);

  const guides: Record<
    Exclude<Platform, "unsupported">,
    { icon: typeof Monitor; title: string; steps: string[] }
  > = {
    ios: {
      icon: Share,
      title: "iOS / iPadOS",
      steps: [
        "Tap the Share button in Safari's toolbar",
        "Scroll down and tap \u201CAdd to Home Screen\u201D",
        "Tap \u201CAdd\u201D \u2014 RockScout installs like a native app",
      ],
    },
    "android-chrome": {
      icon: MoreVertical,
      title: "Android",
      steps: [
        "Tap the \u22EE menu, top right in Chrome",
        "Tap \u201CInstall app\u201D or \u201CAdd to Home screen\u201D",
        "Confirm \u2014 RockScout opens full screen from now on",
      ],
    },
    "desktop-chrome": {
      icon: MonitorDown,
      title: "Desktop (Chrome)",
      steps: [
        "Click the install icon at the right of the address bar",
        "Or open \u22EE \u2192 Cast, save and share \u2192 Install page as app",
        "Click \u201CInstall\u201D",
      ],
    },
    "desktop-edge": {
      icon: MonitorDown,
      title: "Desktop (Edge)",
      steps: [
        "Click the \u22EF menu, top right in Edge",
        "Choose Apps \u2192 Install this site as an app",
        "Click \u201CInstall\u201D",
      ],
    },
  };

  if (platform === "unsupported") return null;
  const guide = guides[platform];

  return (
    <div className="rounded-2xl border border-border/60 bg-card/40 p-5 sm:p-6">
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className="flex w-full items-center justify-between gap-3 text-left"
        aria-expanded={open}
      >
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10 ring-1 ring-primary/20">
            <guide.icon className="h-5 w-5 text-primary" />
          </div>
          <div>
            <h2 className="font-display text-base font-semibold sm:text-lg">
              {guide.title} install steps
            </h2>
            <p className="text-xs text-muted-foreground">
              {open ? "Tap to hide" : "Tap to see how to install"}
            </p>
          </div>
        </div>
        <ArrowRight
          className={`h-4 w-4 shrink-0 text-muted-foreground transition-transform ${
            open ? "rotate-90" : ""
          }`}
        />
      </button>
      {open && (
        <ol className="mt-4 space-y-2.5 border-t border-border/40 pt-4">
          {guide.steps.map((step, i) => (
            <li key={step} className="flex gap-3 text-sm text-muted-foreground">
              <span className="grid h-5 w-5 shrink-0 place-items-center rounded-full bg-primary/15 text-[10px] font-bold text-primary">
                {i + 1}
              </span>
              <span className="leading-relaxed">{step}</span>
            </li>
          ))}
        </ol>
      )}
    </div>
  );
}
