import { Link } from "react-router-dom";
import { useState } from "react";
import {
  Download,
  Crown,
  CheckCircle2,
  Share,
  MoreVertical,
  MonitorDown,
  Monitor,
  ArrowRight,
  BookOpen,
  Sparkles,
  Loader2,
} from "lucide-react";
import { Layout } from "@/components/Layout";
import { InstallAppButton } from "@/components/InstallAppButton";
import { AuthPill } from "@/components/AuthPill";
import { PremiumInstallDialog } from "@/components/PremiumInstallDialog";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { usePwaInstall, type Platform } from "@/hooks/usePwaInstall";
import { SITE } from "@/content/legal";
import { Button } from "@/components/ui/button";

interface InstallPWAProps {
  mode: "free" | "premium";
}

/**
 * Dedicated PWA install page.
 *
 * Two distinct entry points:
 *  - `/install/free` — install the free PWA, no account required.
 *  - `/install/premium` — install the Premium PWA; sign-in and an active
 *    Premium entitlement are required so the installed app can sync.
 *
 * The legacy `/install` route redirects to `/install/free`.
 *
 * The premium mode uses the shared PremiumInstallDialog component so the
 * flow is identical whether the user arrives via the navbar pill or this
 * page directly.
 */
export default function InstallPWA({ mode }: InstallPWAProps) {
  const { user, isLoading: authLoading } = useAuth();
  const { isPremium, isLoading: tierLoading } = useTier();
  const { platform } = usePwaInstall();
  const [dialogOpen, setDialogOpen] = useState(false);

  const isPremiumMode = mode === "premium";
  const isLoading = authLoading || tierLoading;

  const title = isPremiumMode
    ? `Install Premium PWA · ${SITE.name}`
    : `Install Free PWA · ${SITE.name}`;
  const description = isPremiumMode
    ? "Install RockScout Premium as a PWA for the full feature set, synced across your devices."
    : "Install the free RockScout PWA for read-only exploration, no account required.";

  return (
    <Layout title={title} description={description}>
      <section className="relative overflow-hidden border-b border-border/40">
        <div className="geode-gradient pointer-events-none absolute inset-0 opacity-30" />
        <div className="relative mx-auto max-w-xl px-4 py-16 text-center sm:px-6 sm:py-24 md:py-28">
          <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-2xl bg-primary/15 ring-1 ring-primary/30">
            {isPremiumMode ? (
              <Crown className="h-8 w-8 text-amber-500" />
            ) : (
              <Download className="h-8 w-8 text-primary" />
            )}
          </div>
          <h1 className="font-display text-3xl font-bold tracking-tight sm:text-4xl">
            {isPremiumMode ? "Install Premium PWA" : "Install Free PWA"}
          </h1>
          <p className="mx-auto mt-3 max-w-sm text-balance text-sm text-muted-foreground sm:text-base">
            {isPremiumMode
              ? "Premium unlocks AI ID, social features, and sync across devices."
              : "Explore the specimen database, maps, and guides — no account needed."}
          </p>

          <div className="mt-8 flex flex-col items-center gap-4">
            {isLoading ? (
              <span className="inline-flex items-center gap-2 rounded-full border border-border bg-card/60 px-4 py-2 text-sm font-medium text-muted-foreground">
                <Loader2 className="h-4 w-4 animate-spin" />
                Checking account…
              </span>
            ) : isPremiumMode ? (
              <PremiumInstallState
                user={user}
                isPremium={isPremium}
                onSignIn={() => setDialogOpen(true)}
              />
            ) : (
              <div className="scale-125">
                <InstallAppButton mode="free" />
              </div>
            )}

            {/* Switch-flow links */}
            {!isLoading && (
              <div className="flex flex-col items-center gap-2 text-sm text-muted-foreground">
                {isPremiumMode ? (
                  <>
                    <p>Just want the free learning app?</p>
                    <Link
                      to="/install/free"
                      className="inline-flex items-center gap-1.5 font-medium text-primary hover:underline"
                    >
                      <BookOpen className="h-4 w-4" />
                      Install Free PWA
                    </Link>
                  </>
                ) : (
                  <>
                    <p>Have a Premium account?</p>
                    <Link
                      to="/install/premium"
                      className="inline-flex items-center gap-1.5 font-medium text-amber-600 hover:underline dark:text-amber-400"
                    >
                      <Crown className="h-4 w-4" />
                      Install Premium PWA
                    </Link>
                  </>
                )}
              </div>
            )}
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-2xl px-4 py-12 sm:px-6">
        <PlatformInstructions platform={platform} />
      </section>

      {/* Shared premium install dialog — opened by the PremiumInstallState
          sign-in button or automatically when the user is already signed in. */}
      {isPremiumMode && (
        <PremiumInstallDialog open={dialogOpen} onOpenChange={setDialogOpen} />
      )}
    </Layout>
  );
}

function PremiumInstallState({
  user,
  isPremium,
  onSignIn,
}: {
  user: ReturnType<typeof useAuth>["user"];
  isPremium: boolean;
  onSignIn: () => void;
}) {
  if (!user) {
    return (
      <div className="flex flex-col items-center gap-4">
        <p className="max-w-sm text-sm text-muted-foreground">
          Sign in with your Premium account to install the Premium PWA. Your
          subscription syncs across all devices.
        </p>
        <Button
          onClick={onSignIn}
          size="lg"
          className="inline-flex items-center gap-2 rounded-full bg-amber-500 px-6 text-base font-semibold text-stone-950 hover:bg-amber-400"
        >
          <Crown className="h-5 w-5" />
          Sign in to install Premium PWA
        </Button>
        <div className="scale-110">
          <AuthPill />
        </div>
      </div>
    );
  }

  if (!isPremium) {
    return (
      <div className="flex flex-col items-center gap-4">
        <p className="max-w-sm text-sm text-muted-foreground">
          Your account is on the Free tier. Upgrade to Premium to install the
          Premium PWA and unlock everything.
        </p>
        <Link
          to="/app/paywall"
          className="inline-flex items-center gap-2 rounded-full bg-gradient-to-r from-amber-500/20 to-primary/20 border border-amber-500/40 px-6 py-3 text-base font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm dark:text-amber-400"
        >
          <Crown className="h-5 w-5" />
          Upgrade to Premium
          <ArrowRight className="h-5 w-5" />
        </Link>
      </div>
    );
  }

  // Premium user — show the install button and open the shared dialog for
  // the actual install flow.
  return (
    <div className="scale-125">
      <InstallAppButton mode="premium" />
    </div>
  );
}

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
