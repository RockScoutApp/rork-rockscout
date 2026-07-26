import { useState } from "react";
import { useAuth } from "@/hooks/useAuth";
import { useStandaloneMode } from "@/hooks/usePlatform";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Mountain, Loader2, AlertCircle } from "lucide-react";

export default function SignIn() {
  const { signIn, signUp, isLoading, error, clearError } = useAuth();
  const [mode, setMode] = useState<"signin" | "signup">("signin");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [verificationMsg, setVerificationMsg] = useState<string | null>(null);
  const isStandalone = useStandaloneMode();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    setVerificationMsg(null);
    clearError();
    try {
      if (mode === "signup") {
        const result = await signUp(email, password);
        if (result.needsVerification) {
          setVerificationMsg(
            "Check your email for a confirmation link to finish creating your account.",
          );
        }
      } else {
        await signIn(email, password);
      }
    } catch {
      // error is set in the hook
    } finally {
      setSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-background px-4 py-8">
      <div className="geode-gradient pointer-events-none fixed inset-0 opacity-40" />

      <div className="relative z-10 w-full max-w-sm space-y-6">
        <div className="flex flex-col items-center gap-3 text-center">
          <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-primary/15 ring-1 ring-primary/30">
            <Mountain className="h-8 w-8 text-primary" />
          </div>
          <div>
            <h1 className="font-display text-3xl font-bold text-foreground">
              RockScout
            </h1>
            <p className="mt-1 text-sm text-muted-foreground">
              {mode === "signin"
                ? "Welcome back, rockhound."
                : "Start identifying rocks today."}
            </p>
          </div>
        </div>

        {verificationMsg && (
          <div className="rounded-lg border border-primary/30 bg-primary/10 p-4 text-sm text-primary">
            {verificationMsg}
          </div>
        )}

        {error && (
          <div className="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
            <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="you@example.com"
              autoComplete="email"
            />
          </div>
          <div className="space-y-2">
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
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

        {isStandalone && (
          <p className="text-center text-xs text-muted-foreground/60">
            Running as installed app
          </p>
        )}
      </div>
    </div>
  );
}
