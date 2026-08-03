import { Link, useSearchParams } from "react-router-dom";
import { useEffect, useState, useCallback } from "react";
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
  AlertCircle,
} from "lucide-react";
import { Layout } from "@/components/Layout";
import { InstallAppButton } from "@/components/InstallAppButton";
import { AuthPill } from "@/components/AuthPill";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { usePwaInstall, type Platform } from "@/hooks/usePwaInstall";
import { SITE } from "@/content/legal";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from "@/components/ui/dialog";
import { supabase } from "@/lib/supabase";
import { cn } from "@/lib/utils";

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
 */
export default function InstallPWA({ mode }: InstallPWAProps) {
  const { user, isLoading: authLoading } = useAuth();
  const { isPremium, isLoading: tierLoading } = useTier();
  const { platform } = usePwaInstall();
  const [searchParams] = useSearchParams();
  const [justVerified, setJustVerified] = useState<boolean>(false);
  const [authDialogOpen, setAuthDialogOpen] = useState<boolean>(false);

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

          {justVerified && (
            <div className="mx-auto mt-6 flex max-w-sm items-center gap-3 rounded-xl border border-emerald-500/40 bg-emerald-500/10 px-4 py-3 text-sm font-medium text-emerald-600 dark:text-emerald-400">
              <CheckCircle2 className="h-5 w-5 shrink-0" />
              <span>Your email is verified! You can now sign in.</span>
            </div>
          )}

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
                onSignIn={() => setAuthDialogOpen(true)}
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

      {/* Inline sign-in dialog for the Premium install page */}
      <SignInDialog
        open={authDialogOpen}
        onOpenChange={setAuthDialogOpen}
      />
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

  return (
    <div className="scale-125">
      <InstallAppButton mode="premium" />
    </div>
  );
}

/**
 * Sign-in dialog embedded on the Premium install page.
 *
 * After sign-in, the user's Premium status is checked directly from Supabase
 * (avoiding the stale-closure issue with useTier). If Premium and the email is
 * already confirmed in Supabase, the "Install Premium PWA" button appears
 * immediately. If the email is not confirmed, a 6-digit code is sent first.
 */
function SignInDialog({
  open,
  onOpenChange,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}) {
  const { signIn, signUp, error, clearError, setPremiumConfirmed } = useAuth();
  const { install, installed, hasNativePrompt, platform } = usePwaInstall();
  const [mode, setMode] = useState<"signin" | "signup">("signin");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [verificationMsg, setVerificationMsg] = useState<string | null>(null);
  const [awaitingCode, setAwaitingCode] = useState(false);
  const [codeInput, setCodeInput] = useState("");
  const [codeError, setCodeError] = useState<string | null>(null);
  const [codeSending, setCodeSending] = useState(false);
  const [confirmedEmail, setConfirmedEmail] = useState("");
  const [pendingPassword, setPendingPassword] = useState("");
  const [premiumVerified, setPremiumVerified] = useState(false);
  const [installing, setInstalling] = useState(false);
  const [freeTierMsg, setFreeTierMsg] = useState(false);

  const resetForm = useCallback(() => {
    setEmail("");
    setPassword("");
    setVerificationMsg(null);
    setAwaitingCode(false);
    setCodeInput("");
    setCodeError(null);
    setPendingPassword("");
    setPremiumVerified(false);
    setFreeTierMsg(false);
    clearError();
  }, [clearError]);

  /**
   * Checks the user's Premium status directly from Supabase to avoid the
   * stale-closure issue where isPremium from useTier is captured before sign-in.
   */
  const checkUserPremiumDirect = useCallback(async (): Promise<boolean> => {
    try {
      const { data: authData } = await supabase.auth.getUser();
      const userId = authData.user?.id;
      if (!userId) return false;
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("is_pro")
        .eq("id", userId)
        .maybeSingle();
      return (data as { is_pro: boolean } | null)?.is_pro ?? false;
    } catch {
      return false;
    }
  }, []);

  const requestCode = async (emailToSend: string, purpose: "signup" | "premium") => {
    setCodeSending(true);
    setCodeError(null);
    try {
      const { sendVerificationCode } = await import("@/lib/emailVerification");
      const outcome = await sendVerificationCode(emailToSend);
      if (!outcome.ok) {
        setCodeError(outcome.error ?? "Could not send verification code.");
        return false;
      }
      setAwaitingCode(true);
      setConfirmedEmail(emailToSend);
      setVerificationMsg(
        purpose === "signup"
          ? `We sent a 6-digit code to ${emailToSend}. Enter it to activate your account.`
          : "Premium account detected! Enter the 6-digit code sent to your email to confirm.",
      );
      return true;
    } finally {
      setCodeSending(false);
    }
  };

  const verifyCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setCodeError(null);
    try {
      const { verifyEmailCode } = await import("@/lib/emailVerification");
      const outcome = await verifyEmailCode(confirmedEmail, codeInput);
      if (!outcome.ok || !outcome.verified) {
        setCodeError(outcome.error ?? "Invalid code. Try again.");
        setSubmitting(false);
        return;
      }
      if (mode === "signup") {
        try {
          await signIn(confirmedEmail, pendingPassword);
        } catch {
          setCodeError("Email verified. Please sign in with your new password to continue.");
          setSubmitting(false);
          return;
        }
      }
      setPremiumConfirmed(Date.now());
      setAwaitingCode(false);
      setPremiumVerified(true);
      setVerificationMsg("Premium verified! You can now install the Premium PWA.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setVerificationMsg(null);
    setFreeTierMsg(false);
    clearError();
    try {
      if (mode === "signup") {
        await signUp(email, password);
        setPendingPassword(password);
        await requestCode(email, "signup");
      } else {
        await signIn(email, password);
        setConfirmedEmail(email);
        // Check premium status directly from Supabase to avoid stale closure
        const userPremium = await checkUserPremiumDirect();
        if (userPremium) {
          // If the email is already confirmed in Supabase, skip the 6-digit
          // code and show the install button immediately.
          const { data: authData } = await supabase.auth.getUser();
          const emailConfirmed = !!authData.user?.email_confirmed_at;
          if (emailConfirmed) {
            setPremiumConfirmed(Date.now());
            setPremiumVerified(true);
            setVerificationMsg("Premium verified! You can now install the Premium PWA.");
          } else {
            await requestCode(email, "premium");
          }
        } else {
          setFreeTierMsg(true);
          setVerificationMsg(
            "You're signed in on the Free tier. Upgrade to Premium to install the Premium PWA and unlock all features.",
          );
        }
      }
    } catch {
      // error is set in the hook
    } finally {
      setSubmitting(false);
    }
  };

  const handleInstallClick = async () => {
    if (!hasNativePrompt) {
      onOpenChange(false);
      return;
    }
    setInstalling(true);
    try {
      await install();
    } catch {
      // Fall back to instructions below
    } finally {
      setInstalling(false);
    }
  };

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
            <Crown className="h-5 w-5 text-primary" />
            {premiumVerified
              ? "Premium verified!"
              : mode === "signin"
                ? "Sign in to Premium"
                : "Create account"}
          </DialogTitle>
          <DialogDescription>
            {premiumVerified
              ? "Click the button below to install the Premium PWA."
              : "Verify your Premium account to install the RockScout Premium PWA on this device."}
          </DialogDescription>
        </DialogHeader>

        {verificationMsg && (
          <div
            className={cn(
              "rounded-lg border p-3 text-sm",
              premiumVerified
                ? "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
                : freeTierMsg
                  ? "border-amber-500/40 bg-amber-500/10 text-amber-600 dark:text-amber-400"
                  : "border-primary/30 bg-primary/10 text-primary",
            )}
          >
            {verificationMsg}
          </div>
        )}

        {error && (
          <div className="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
            <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {/* Premium verified — show install button at bottom of dialog */}
        {premiumVerified ? (
          <div className="space-y-3">
            <Button
              type="button"
              onClick={handleInstallClick}
              disabled={installing}
              size="lg"
              className="w-full inline-flex items-center gap-2 rounded-full bg-amber-500 text-stone-950 hover:bg-amber-400"
            >
              {installing ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Download className="h-4 w-4" />
              )}
              {installing ? "Installing…" : "Install Premium PWA"}
            </Button>
            {installed && (
              <div className="flex items-center justify-center gap-2 text-sm font-medium text-emerald-600 dark:text-emerald-400">
                <CheckCircle2 className="h-4 w-4" />
                Premium PWA installed!
              </div>
            )}
            {!hasNativePrompt && !installing && (
              <p className="text-center text-xs text-muted-foreground">
                {platform === "ios"
                  ? "Use Safari's Share → Add to Home Screen to install."
                  : "Follow the platform install steps below this dialog."}
              </p>
            )}
          </div>
        ) : awaitingCode ? (
          <form onSubmit={verifyCode} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="install-code">Verification code</Label>
              <Input
                id="install-code"
                type="text"
                required
                maxLength={6}
                value={codeInput}
                onChange={(e) => setCodeInput(e.target.value.replace(/\D/g, ""))}
                placeholder="6-digit code"
                autoComplete="one-time-code"
                className="text-center text-lg tracking-widest"
              />
            </div>
            {codeError && (
              <div className="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
                <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
                <span>{codeError}</span>
              </div>
            )}
            <Button type="submit" disabled={submitting || codeInput.length !== 6} className="w-full" size="lg">
              {submitting ? <Loader2 className="h-4 w-4 animate-spin" /> : "Verify code"}
            </Button>
            <button
              type="button"
              onClick={() => requestCode(confirmedEmail, mode === "signup" ? "signup" : "premium")}
              disabled={codeSending}
              className="w-full text-center text-xs text-muted-foreground hover:text-foreground"
            >
              {codeSending ? "Sending…" : "Resend code"}
            </button>
          </form>
        ) : freeTierMsg ? (
          /* Free-tier user — show upgrade link */
          <div className="space-y-3">
            <Link
              to="/app/paywall"
              onClick={() => onOpenChange(false)}
              className="block w-full rounded-full border border-amber-500/40 bg-amber-500/10 px-4 py-2.5 text-center text-sm font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm dark:text-amber-400"
            >
              <Crown className="mr-1.5 inline h-4 w-4" />
              Upgrade to Premium
            </Link>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="install-email">Email</Label>
              <Input
                id="install-email"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                autoComplete="email"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="install-password">Password</Label>
              <Input
                id="install-password"
                type="password"
                required
                minLength={6}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="At least 6 characters"
                autoComplete={mode === "signin" ? "current-password" : "new-password"}
              />
            </div>
            <Button type="submit" disabled={submitting} className="w-full" size="lg">
              {submitting ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : mode === "signin" ? (
                "Sign in"
              ) : (
                "Create account"
              )}
            </Button>
          </form>
        )}

        {/* Mode switch — hidden during code/verified/free-tier states */}
        {!premiumVerified && !awaitingCode && !freeTierMsg && (
          <div className="text-center text-sm text-muted-foreground">
            {mode === "signin" ? (
              <>
                Don&apos;t have an account?{" "}
                <button
                  onClick={() => {
                    setMode("signup");
                    clearError();
                    setVerificationMsg(null);
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
                    setMode("signin");
                    clearError();
                    setVerificationMsg(null);
                  }}
                  className="font-medium text-primary hover:underline"
                >
                  Sign in
                </button>
              </>
            )}
          </div>
        )}
      </DialogContent>
    </Dialog>
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
