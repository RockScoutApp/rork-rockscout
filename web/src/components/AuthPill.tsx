import { useState } from "react";
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
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { useTier } from "@/hooks/useTier";
import { sendVerificationCode, verifyEmailCode } from "@/lib/emailVerification";
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

/**
 * Navbar sign-in pill. Lets website visitors sign in to verify their Premium
 * status — which is required to install the RockScout Premium PWA on 2 more
 * devices and keep data synced. The pill uses the same tier source as the rest
 * of the app so its label is always consistent with the actual entitlement.
 *
 * States:
 *  - Signed out: "Premium PWA" pill → opens a sign-in/sign-up dialog, then
 *    leads to the Premium install page.
 *  - Signed in + Premium: amber "Premium" pill with a crown + dropdown.
 *  - Signed in + Free: outline pill with email + dropdown (upgrade prompt).
 */
export function AuthPill() {
  const { user, isLoading, signIn, signUp, signOut, error, clearError, setPremiumConfirmed } =
    useAuth();
  const { isPremium: isPro, isLoading: tierLoading } = useTier();
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
  const [codePurpose, setCodePurpose] = useState<"signup" | "premium">("premium");
  const [pendingPassword, setPendingPassword] = useState("");

  const resetForm = () => {
    setEmail("");
    setPassword("");
    setVerificationMsg(null);
    setAwaitingCode(false);
    setCodeInput("");
    setCodeError(null);
    setPendingPassword("");
    clearError();
  };

  /**
   * Sends a 6-digit code. `purpose` decides what happens once it's verified:
   * `signup` activates a brand-new account and signs it in, `premium` unlocks
   * the PWA device confirmation for an existing Premium account.
   */
  const requestCode = async (
    emailToSend: string,
    purpose: "signup" | "premium",
  ) => {
    setCodeSending(true);
    setCodeError(null);
    const outcome = await sendVerificationCode(emailToSend);
    setCodeSending(false);

    if (!outcome.ok) {
      setCodeError(outcome.error ?? "Could not send verification code.");
      return false;
    }

    setCodePurpose(purpose);
    setConfirmedEmail(emailToSend);
    setAwaitingCode(true);
    setVerificationMsg(
      purpose === "signup"
        ? `We sent a 6-digit code to ${emailToSend}. Enter it to activate your account.`
        : "Premium account detected! Enter the 6-digit code sent to your email to confirm.",
    );
    return true;
  };

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

    if (codePurpose === "signup") {
      // The backend just confirmed the Supabase email, so the account can
      // sign in immediately — no confirmation link to chase.
      try {
        await signIn(confirmedEmail, pendingPassword);
      } catch {
        setCodeError(
          "Email verified. Please sign in with your new password to continue.",
        );
        setSubmitting(false);
        return;
      }
    } else {
      setPremiumConfirmed(Date.now());
    }

    setAwaitingCode(false);
    setDialogOpen(false);
    resetForm();
    setSubmitting(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setVerificationMsg(null);
    clearError();
    try {
      if (mode === "signup") {
        await signUp(email, password);
        // Always run the 6-digit code flow — it's instant, works on every
        // platform, and activates the Supabase account server-side so there is
        // no confirmation link for the user to hunt down.
        setPendingPassword(password);
        await requestCode(email, "signup");
      } else {
        await signIn(email, password);
        // After sign-in, check if the user is premium — if so, require email-code
        // confirmation before closing the dialog. Free users get frictionless sign-in.
        // We wait a tick so the shared tier query can load.
        setConfirmedEmail(email);
        setTimeout(() => {
          if (isPro) {
            void requestCode(email, "premium");
          } else {
            setDialogOpen(false);
            resetForm();
          }
        }, 400);
      }
    } catch {
      // error is set in the hook
    } finally {
      setSubmitting(false);
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

  // Signed out — show the sign-in pill with clear PWA/premium labelling.
  if (!user) {
    return (
      <>
        <Link
          to="/install/premium"
          title="Install the Premium PWA with all features unlocked"
          className="group inline-flex items-center gap-1.5 rounded-full border border-amber-500/50 bg-gradient-to-br from-amber-500/10 to-primary/10 px-3.5 py-1.5 text-xs font-semibold text-amber-600 transition-all hover:border-amber-500 hover:shadow-sm sm:px-4 sm:py-2 sm:text-sm dark:text-amber-400"
        >
          <Crown className="h-3.5 w-3.5 transition-transform group-hover:scale-110" />
          <span>Premium PWA</span>
        </Link>

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
                {mode === "signin" ? "Sign in to install" : "Create account"}
              </DialogTitle>
              <DialogDescription>
                Verify your Premium account to install the RockScout PWA on 2
                more devices, including tablets, laptops, or PCs.
              </DialogDescription>
            </DialogHeader>

            {verificationMsg && (
              <div className="rounded-lg border border-primary/30 bg-primary/10 p-3 text-sm text-primary">
                {verificationMsg}
              </div>
            )}

            {error && (
              <div className="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
                <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
                <span>{error}</span>
              </div>
            )}

            {awaitingCode ? (
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
                  onClick={() => requestCode(confirmedEmail, codePurpose)}
                  disabled={codeSending}
                  className="w-full text-center text-xs text-muted-foreground hover:text-foreground"
                >
                  {codeSending ? "Sending…" : "Resend code"}
                </button>
              </form>
            ) : (
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
              ? "Premium verified — install the PWA on 2 more devices"
              : "Signed in — upgrade to Premium to install the PWA on 2 more devices"
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
            {isPro ? "Premium" : "Free"}
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
        <DropdownMenuItem
          onClick={() => navigate("/app")}
          className="gap-2"
        >
          <LayoutGrid className="h-4 w-4" />
          Open the app
        </DropdownMenuItem>
        {isPro && (
          <DropdownMenuItem
            onClick={() => navigate("/app/manage-devices")}
            className="gap-2"
          >
            <Smartphone className="h-4 w-4" />
            Manage devices
          </DropdownMenuItem>
        )}
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
