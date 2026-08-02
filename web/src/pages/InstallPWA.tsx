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
  Sparkles,
  BookOpen,
  Camera,
  Map as MapIcon,
  Users,
} from "lucide-react";
import { Layout } from "@/components/Layout";
import { InstallAppButton } from "@/components/InstallAppButton";
import { AuthPill } from "@/components/AuthPill";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { SITE } from "@/content/legal";

/**
 * Dedicated PWA install page.
 *
 * This is the landing destination when a website visitor clicks "Free PWA" or
 * "Install Premium PWA". It shows platform-specific install instructions and
 * the native install button (when the browser supports it), without forcing
 * the user through a sign-in wall first.
 *
 * Free users can install the read-only PWA immediately — no account required.
 * Premium users sign in to verify their subscription, then install on up to 2
 * additional devices.
 */
export default function InstallPWA() {
  const { user } = useAuth();
  const { isPremium } = useTier();
  const [searchParams] = useSearchParams();
  const [justVerified, setJustVerified] = useState(false);

  // If the user arrived here from an email verification redirect, show a
  // success banner so they know their email was confirmed.
  useEffect(() => {
    const verified = searchParams.get("verified");
    const email = searchParams.get("email");
    if (verified === "true" && email) {
      setJustVerified(true);
      // Persist a flag so the app dashboard can show a verification banner
      // after the user signs in and navigates to /app.
      try {
        sessionStorage.setItem("rockscout_just_verified", email);
      } catch {
        // sessionStorage might be unavailable in private browsing
      }
      // Clean the URL so the banner doesn't persist on refresh
      const cleanUrl = window.location.pathname;
      window.history.replaceState({}, "", cleanUrl);
    }
  }, [searchParams]);

  return (
    <Layout
      title={`Install RockScout PWA · ${SITE.name}`}
      description="Install RockScout as a Progressive Web App on your desktop, laptop, or tablet. Free read-only PWA for learning, or Premium PWA with all features unlocked."
    >
      {/* Hero */}
      <section className="relative overflow-hidden border-b border-border/40">
        <div className="geode-gradient pointer-events-none absolute inset-0 opacity-30" />
        <div className="relative mx-auto max-w-4xl px-4 py-12 text-center sm:px-6 sm:py-16 md:py-20">
          <div className="mx-auto mb-6 flex h-16 w-16 items-center justify-center rounded-2xl bg-primary/15 ring-1 ring-primary/30">
            <Download className="h-8 w-8 text-primary" />
          </div>
          <h1 className="font-display text-3xl font-bold tracking-tight sm:text-4xl md:text-5xl">
            Install RockScout
          </h1>
          <p className="mx-auto mt-4 max-w-xl text-balance text-base text-muted-foreground sm:text-lg">
            Get the full RockScout experience as a Progressive Web App —
            installs on your desktop, laptop, or tablet in seconds.
          </p>

          {justVerified && (
            <div className="mx-auto mt-6 flex max-w-md items-center gap-3 rounded-xl border border-emerald-500/40 bg-emerald-500/10 px-4 py-3 text-sm font-medium text-emerald-600 dark:text-emerald-400">
              <CheckCircle2 className="h-5 w-5 shrink-0" />
              <span>Your email is verified! You can now sign in to your account.</span>
            </div>
          )}

          {/* Install button + auth pill */}
          <div className="mt-8 flex flex-col items-center gap-4">
            <div className="scale-110">
              <InstallAppButton />
            </div>
            {!user && (
              <div className="flex flex-col items-center gap-2">
                <p className="text-sm text-muted-foreground">
                  Want Premium features? Sign in to verify your subscription:
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

      {/* Tier comparison */}
      <section className="mx-auto max-w-5xl px-4 py-12 sm:px-6 sm:py-16">
        <div className="grid gap-6 md:grid-cols-2">
          {/* Free PWA card */}
          <div className="relative overflow-hidden rounded-2xl border border-border bg-card/50 p-6 sm:p-8">
            <div className="mb-4 flex items-center gap-3">
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/15 ring-1 ring-primary/25">
                <BookOpen className="h-6 w-6 text-primary" />
              </div>
              <div>
                <h2 className="font-display text-xl font-bold">Free PWA</h2>
                <p className="text-sm text-muted-foreground">Read-only · No account needed</p>
              </div>
            </div>
            <ul className="space-y-2.5 text-sm">
              {[
                "Full 900+ specimen database",
                "Interactive dig-site map",
                "Educational guides & reference library",
                "Dinosaur dictionary & paleontology",
                "Aurora forecaster & space weather",
                "Stars, constellations & planets",
                "Periodic table & geology reference",
                "Personal bookmarks & field captures",
              ].map((feature) => (
                <li key={feature} className="flex items-start gap-2.5">
                  <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-primary" />
                  <span className="text-foreground/90">{feature}</span>
                </li>
              ))}
              <li className="flex items-start gap-2.5 text-muted-foreground">
                <span className="mt-0.5 h-4 w-4 shrink-0 text-center text-xs leading-4">—</span>
                <span className="line-through">AI rock identification</span>
              </li>
              <li className="flex items-start gap-2.5 text-muted-foreground">
                <span className="mt-0.5 h-4 w-4 shrink-0 text-center text-xs leading-4">—</span>
                <span className="line-through">Community, trade & social features</span>
              </li>
            </ul>
            <div className="mt-6">
              <InstallAppButton />
            </div>
            <p className="mt-3 text-xs text-muted-foreground">
              No sign-in required — just install and start exploring.
            </p>
          </div>

          {/* Premium PWA card */}
          <div className="relative overflow-hidden rounded-2xl border border-amber-500/40 bg-gradient-to-br from-amber-500/5 to-primary/5 p-6 sm:p-8">
            <div className="absolute right-4 top-4 rounded-full bg-amber-500/15 px-3 py-1 text-xs font-bold uppercase tracking-wider text-amber-600 ring-1 ring-amber-500/30 dark:text-amber-400">
              Premium
            </div>
            <div className="mb-4 flex items-center gap-3">
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-amber-500/15 ring-1 ring-amber-500/25">
                <Crown className="h-6 w-6 text-amber-600 dark:text-amber-400" />
              </div>
              <div>
                <h2 className="font-display text-xl font-bold">Premium PWA</h2>
                <p className="text-sm text-muted-foreground">All features · Up to 2 extra devices</p>
              </div>
            </div>
            <ul className="space-y-2.5 text-sm">
              {[
                "Everything in Free, plus:",
                "AI rock & mineral identification (5-source pipeline)",
                "Field journal & trip planner",
                "Trade board & community feed",
                "Friends, messaging & QR scanner",
                "Achievements & collections",
                "Proximity alerts & push notifications",
                "Install on up to 2 additional devices",
              ].map((feature, i) => (
                <li key={feature} className="flex items-start gap-2.5">
                  <CheckCircle2 className={`mt-0.5 h-4 w-4 shrink-0 ${i === 0 ? "opacity-0" : "text-amber-500"}`} />
                  <span className={i === 0 ? "font-semibold text-foreground" : "text-foreground/90"}>{feature}</span>
                </li>
              ))}
            </ul>
            <div className="mt-6 flex flex-col gap-3">
              {user && isPremium ? (
                <InstallAppButton />
              ) : user && !isPremium ? (
                <Link
                  to="/app/paywall"
                  className="inline-flex items-center justify-center gap-2 rounded-full bg-gradient-to-r from-amber-500 to-primary px-6 py-3 text-sm font-bold text-white transition-transform hover:scale-[1.02] active:scale-[0.98]"
                >
                  <Crown className="h-4 w-4" />
                  Upgrade to Premium
                </Link>
              ) : (
                <div className="flex flex-col gap-2">
                  <AuthPill />
                  <p className="text-xs text-muted-foreground">
                    Sign in with your Premium account to install on additional devices.
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </section>

      {/* Platform instructions */}
      <section className="border-t border-border/40 bg-card/20 py-12 sm:py-16">
        <div className="mx-auto max-w-4xl px-4 sm:px-6">
          <h2 className="text-center font-display text-2xl font-bold sm:text-3xl">
            How to install on any device
          </h2>
          <div className="mt-8 grid gap-6 sm:grid-cols-3">
            <PlatformGuide
              icon={Monitor}
              title="Desktop"
              steps={[
                "Look for the install icon in the address bar",
                "Click it and select \"Install\"",
                "RockScout opens in its own app window",
              ]}
            />
            <PlatformGuide
              icon={Smartphone}
              title="Android"
              steps={[
                "Open the menu (⋮) in Chrome",
                "Tap \"Install app\" or \"Add to Home screen\"",
                "RockScout launches full-screen from your home screen",
              ]}
            />
            <PlatformGuide
              icon={Tablet}
              title="iOS / iPadOS"
              steps={[
                "Tap the Share button in Safari",
                "Scroll down and tap \"Add to Home Screen\"",
                "Tap \"Add\" — RockScout installs like a native app",
              ]}
            />
          </div>
        </div>
      </section>

      {/* Feature highlights */}
      <section className="mx-auto max-w-4xl px-4 py-12 sm:px-6 sm:py-16">
        <div className="grid gap-4 sm:grid-cols-2 md:grid-cols-4">
          {[
            { icon: BookOpen, label: "900+ specimens" },
            { icon: MapIcon, label: "Interactive maps" },
            { icon: Camera, label: "AI rock ID (Premium)" },
            { icon: Users, label: "Community (Premium)" },
          ].map((item) => (
            <div
              key={item.label}
              className="flex flex-col items-center gap-3 rounded-2xl border border-border/60 bg-card/40 p-5 text-center"
            >
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 ring-1 ring-primary/20">
                <item.icon className="h-6 w-6 text-primary" />
              </div>
              <p className="text-sm font-medium text-foreground">{item.label}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="mx-auto max-w-4xl px-4 pb-16 sm:px-6">
        <div className="rounded-2xl border border-border/60 bg-card/30 p-6 text-center sm:p-8">
          <Sparkles className="mx-auto mb-3 h-8 w-8 text-primary" />
          <h2 className="font-display text-xl font-bold sm:text-2xl">
            Already have the app?
          </h2>
          <p className="mt-2 text-sm text-muted-foreground">
            Open RockScout directly in your browser — no install needed.
          </p>
          <Link
            to="/app"
            className="mt-5 inline-flex items-center gap-2 rounded-full border border-border bg-card/60 px-6 py-3 text-sm font-medium text-foreground transition-colors hover:bg-card"
          >
            Open RockScout
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </section>
    </Layout>
  );
}

function PlatformGuide({
  icon: Icon,
  title,
  steps,
}: {
  icon: typeof Monitor;
  title: string;
  steps: string[];
}) {
  return (
    <div className="rounded-2xl border border-border/60 bg-card/40 p-5">
      <div className="mb-4 flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10 ring-1 ring-primary/20">
          <Icon className="h-5 w-5 text-primary" />
        </div>
        <h3 className="font-display text-lg font-semibold">{title}</h3>
      </div>
      <ol className="space-y-2">
        {steps.map((step, i) => (
          <li key={step} className="flex gap-2.5 text-sm text-muted-foreground">
            <span className="grid h-5 w-5 shrink-0 place-items-center rounded-full bg-primary/15 text-[10px] font-bold text-primary">
              {i + 1}
            </span>
            <span className="leading-relaxed">{step}</span>
          </li>
        ))}
      </ol>
    </div>
  );
}
