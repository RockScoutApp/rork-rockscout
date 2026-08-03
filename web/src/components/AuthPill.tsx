import { useState, useCallback } from "react";
import { useNavigate, Link } from "react-router-dom";
import {
  Crown,
  LogOut,
  Loader2,
  AlertCircle,
  LayoutGrid,
  ChevronDown,
  Sparkles,
  Smartphone,
  Download,
  CheckCircle2,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { usePwaInstall } from "@/hooks/usePwaInstall";
import { sendVerificationCode, verifyEmailCode } from "@/lib/emailVerification";
import { supabase } from "@/lib/supabase";
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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { cn } from "@/lib/utils";

interface ProfileRow {
  is_pro: boolean;
}

/**
 * Navbar sign-in pill. Lets website visitors sign in to verify their Premium
 * status — which is required to install the RockScout Premium PWA and keep
 * data synced.
 *
 * States:
 *  - Signed out: "Premium PWA" pill → opens a sign-in/sign-up dialog. After
 *    sign-in, if the user is Premium, a 6-digit code is sent. Once verified,
 *    an "Install Premium PWA" button appears at the bottom of the dialog.
 *  - Signed in + Premium: amber "Premium" pill with a crown + dropdown that
 *    includes an "Install Premium PWA" action.
 *  - Signed in + Free: outline pill with email + dropdown (upgrade prompt).
 */
export function AuthPill() {
  const { user, isLoading, signIn, signUp, signOut, error, clearError, setPremiumConfirmed } =
    useAuth();
  const { isPremium: isPro, isLoading: tierLoading } = useTier();
  const { canInstall, install, installed, hasNativePrompt, platform } = usePwaInstall();
  const navigate = useNavigate();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [mode, setMode] = useState<"signin" | "signup">("signin");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [verificationMsg, setVerificationMsg] = useState<string | null>(null);

  // Email-code confirmation state for premium users
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
   * Checks the user's Premium status directly from Supabase. This avoids the
   * stale-closure issue where `isPro` from `useTier()` is captured from the
   * render before sign-in (always false at that point).
   */
  const checkUserPremiumDirect = useCallback(async (): Promise<boolean> => {
    try {
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("is_pro")
        .maybeSingle();
      return (data as ProfileRow)?.is_pro ?? false;
    } catch {
      return false;
    }
  }, []);

  /**
   * Sends a 6-digit code. `purpose` decides what happens once it's verified:
   * `signup` activates a brand-new account and signs it in, `premium` unlocks
   * the PWA device confirmation for an existing Premium account.
   */
  const requestCode = useCallback(
    async (emailToSend: string, purpose: "signup" | "premium") => {
      setCodeSending(true);
      setCodeError(null);
      const outcome = await sendVerificationCode(emailToSend);
      setCodeSending(false);

      if (!outcome.ok) {
        setCodeError(outcome.error ?? "Could not send verification code.");
        return false;
      }

      setConfirmedEmail(emailToSend);
      setAwaitingCode(true);
      setVerificationMsg(
        purpose === "signup"
          ? `We sent a 6-digit code to ${emailToSend}. Enter it to activate your account.`
          : "Premium account detected! Enter the 6-digit code sent to your email to confirm.",
      );
      return true;
    },
    [],
  );

  const verifyCode = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setCodeError(null);

    const outcome = await verifyEmailCode(confirmedEmail, codeInput);

    if (!outcome.ok || !outcome.verified) {
      setCodeError(outcome.error ?? "Invalid code. Try again.");
      setSubmitting(false);
      return;
    }

    setPremiumConfirmed(Date.now());
    setAwaitingCode(false);
    setPremiumVerified(true);
    setVerificationMsg("Premium verified! You can now install the Premium PWA.");
    setSubmitting(false);
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
        // After sign-in, check premium status directly from Supabase to
        // avoid the stale isPro closure value.
        const userPremium = await checkUserPremiumDirect();
        if (userPremium) {
          await requestCode(email, "premium");
        } else {
          // Free-tier user — show upgrade message, keep dialog open
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
      // No native prompt — close dialog and go to install page
      setDialogOpen(false);
      navigate("/install/premium");
      return;
    }
    setInstalling(true);
    try {
      await install();
    } catch {
      setDialogOpen(false);
      navigate("/install/premium");
    } finally {
      setInstalling(false);
    }
  };

  const handleSignOut = async () => {
    await signOut();
  };

  // Loading state while the session and tier are being restored.
  if (isLoading || tierLoading) {
    return (
      <span
        aria-disabled
        className="inline-flex items-center gap-1.5 rounded-full border border-border bg-card/60 px-3.5 py-1.5 text-xs font-medium text-muted-foreground"
      >
        <Loader2 className="h-3.5 w-3.5 animate-spin" />
        <span className="hidden sm:inline">Checking account…</span>
      </span>
    );
  }

  // Signed out — show the Premium PWA pill that opens a sign-in dialog.
  if (!user) {
    return (
      <>
        <button
          type="button"
          onClick={() => {
            setDialogOpen(true);
            setMode("signin");
          }}
          title="Sign in to install the Premium PWA"
          className="group inline-flex items-center gap-1.5 rounded-full border border-amber-500/50 bg-gradient-to-br from-amber-500/10 to-primary/10 px-3.5 py-1.5 text-xs font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm sm:px-4 sm:py-2 sm:text-sm dark:text-amber-400"
        >
          <Crown className="h-3.5 w-3.5 transition-transform group-hover:scale-110" />
          <span>Premium PWA</span>
        </button>

        <Dialog
          open={dialogOpen}
          onOpenChange={(open) => {
            setDialogOpen(open);
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
                    ? "Sign in to install"
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

            {/* Premium verified — show install button */}
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
                {freeTierMsg && (
                  <Link
                    to="/app/paywall"
                    onClick={() => setDialogOpen(false)}
                    className="block w-full rounded-full border border-amber-500/40 bg-amber-500/10 px-4 py-2.5 text-center text-sm font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm dark:text-amber-400"
                  >
                    <Crown className="mr-1.5 inline h-4 w-4" />
                    Upgrade to Premium
                  </Link>
                )}
                {!hasNativePrompt && !installing && (
                  <p className="text-center text-xs text-muted-foreground">
                    {platform === "ios"
                      ? "After clicking, use Safari's Share → Add to Home Screen to install."
                      : "Your browser will show install steps."}
                  </p>
                )}
              </div>
            ) : awaitingCode ? (
              /* Code verification form */
              <form onSubmit={verifyCode} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="authpill-code">Verification code</Label>
                  <Input
                    id="authpill-code"
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
                <Button
                  type="submit"
                  disabled={submitting || codeInput.length !== 6}
                  className="w-full"
                  size="lg"
                >
                  {submitting ? (
                    <Loader2 className="h-4 w-4 animate-spin" />
                  ) : (
                    "Verify code"
                  )}
                </Button>
                <button
                  type="button"
                  onClick={() => requestCode(confirmedEmail, "signup")}
                  disabled={codeSending}
                  className="w-full text-center text-xs text-muted-foreground hover:text-foreground"
                >
                  {codeSending ? "Sending…" : "Resend code"}
                </button>
              </form>
            ) : freeTierMsg ? (
              /* Free-tier user after sign-in — show upgrade link */
              <div className="space-y-3">
                <Link
                  to="/app/paywall"
                  onClick={() => setDialogOpen(false)}
                  className="block w-full rounded-full border border-amber-500/40 bg-amber-500/10 px-4 py-2.5 text-center text-sm font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm dark:text-amber-400"
                >
                  <Crown className="mr-1.5 inline h-4 w-4" />
                  Upgrade to Premium
                </Link>
                <button
                  type="button"
                  onClick={() => {
                    setDialogOpen(false);
                    navigate("/app");
                  }}
                  className="block w-full text-center text-xs text-muted-foreground hover:text-foreground"
                >
                  Continue with Free tier →
                </button>
              </div>
            ) : (
              /* Sign-in / sign-up form */
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="authpill-email">Email</Label>
                  <Input
                    id="authpill-email"
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="you@example.com"
                    autoComplete="email"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="authpill-password">Password</Label>
                  <Input
                    id="authpill-password"
                    type="password"
                    required
                    minLength={6}
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="At least 6 characters"
                    autoComplete={mode === "signin" ? "current-password" : "new-password"}
                  />
                </div>
                <Button
                  type="submit"
                  disabled={submitting}
                  className="w-full"
                  size="lg"
                >
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
      </>
    );
  }

  // Signed in — show premium-status pill with a dropdown.
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <button
          type="button"
          title={
            isPro
              ? "Premium verified — install the PWA on this device"
              : "Signed in — upgrade to Premium to install the PWA"
          }
          className={cn(
            "inline-flex items-center gap-1.5 rounded-full border px-3 py-1.5 text-xs font-semibold transition-all hover:shadow-sm sm:px-3.5 sm:py-2 sm:text-sm",
            isPro
              ? "border-amber-500/50 bg-gradient-to-br from-amber-500/15 to-primary/15 text-amber-600 dark:text-amber-400"
              : "border-border bg-card/60 text-foreground",
          )}
        >
          {isPro ? (
            <Crown className="h-3.5 w-3.5" />
          ) : (
            <Sparkles className="h-3.5 w-3.5 text-primary" />
          )}
          <span className="max-w-[8rem] truncate sm:max-w-none">
            Premium
          </span>
          <ChevronDown className="h-3 w-3 opacity-60" />
        </button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-60">
        <DropdownMenuLabel className="font-normal">
          <p className="text-xs text-muted-foreground">Signed in as</p>
          <p className="truncate text-sm font-medium text-foreground">
            {user.email}
          </p>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        {isPro ? (
          <>
            <DropdownMenuItem
              onClick={() => {
                if (installed) {
                  navigate("/app");
                } else if (hasNativePrompt) {
                  void install();
                } else {
                  navigate("/install/premium");
                }
              }}
              className="gap-2"
            >
              <Download className="h-4 w-4" />
              {installed ? "Premium PWA installed" : "Install Premium PWA"}
            </DropdownMenuItem>
            <DropdownMenuItem
              onClick={() => navigate("/app/manage-devices")}
              className="gap-2"
            >
              <Smartphone className="h-4 w-4" />
              Manage devices
            </DropdownMenuItem>
          </>
        ) : (
          <DropdownMenuItem
            onClick={() => navigate("/app/paywall")}
            className="gap-2"
          >
            <Crown className="h-4 w-4 text-amber-500" />
            Upgrade to Premium
          </DropdownMenuItem>
        )}
        <DropdownMenuItem
          onClick={() => navigate("/app")}
          className="gap-2"
        >
          <LayoutGrid className="h-4 w-4" />
          Open the app
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem
          onClick={handleSignOut}
          className="gap-2 text-destructive focus:text-destructive"
        >
          <LogOut className="h-4 w-4" />
          Sign out
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
