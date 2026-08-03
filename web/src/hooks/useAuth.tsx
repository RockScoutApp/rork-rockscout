import createContextHook from "@nkzw/create-context-hook";
import { useEffect, useState, useCallback } from "react";
import { useQueryClient } from "@tanstack/react-query";
import type { Session, User } from "@supabase/supabase-js";
import { supabase, supabaseUrl } from "@/lib/supabase";
import { syncEntitlement } from "@/lib/entitlement";
import { setErrorReporterUserId } from "@/lib/errorReporter";

/**
 * Local capture mode. Enabled ONLY by running the dev server with
 * `VITE_SCREENSHOT_MODE=1` for automated screenshot capture. It is never
 * set in production builds, where the flag inlines as `undefined`.
 */
export const SCREENSHOT_MODE: boolean =
  import.meta.env.VITE_SCREENSHOT_MODE === "1";

/** Stand-in user used only while capturing app screenshots. */
const CAPTURE_USER = {
  id: "00000000-0000-4000-8000-000000000001",
  aud: "authenticated",
  role: "authenticated",
  email: "field.demo@rockscout.app",
  app_metadata: {},
  user_metadata: { display_name: "Field Demo" },
  created_at: new Date(0).toISOString(),
} as unknown as User;

interface AuthState {
  session: Session | null;
  user: User | null;
  isLoading: boolean;
  error: string | null;
  premiumConfirmedAt: number | null;
  isPremiumConfirmed: boolean;
  setPremiumConfirmed: (ts: number) => void;
  clearPremiumConfirmation: () => void;
  signUp: (email: string, password: string) => Promise<{ needsVerification: boolean }>;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  clearError: () => void;
}

/**
 * Safe fallback returned when useAuth() is called outside AuthProvider.
 * Without this, useContext returns undefined and destructuring properties
 * from it crashes the React tree with a TypeError.
 */
const DEFAULT_AUTH_STATE: AuthState = {
  session: null,
  user: null,
  isLoading: true,
  error: null,
  premiumConfirmedAt: null,
  isPremiumConfirmed: false,
  setPremiumConfirmed: () => {},
  clearPremiumConfirmation: () => {},
  signUp: async () => { throw new Error("AuthProvider not mounted"); },
  signIn: async () => { throw new Error("AuthProvider not mounted"); },
  signOut: async () => {},
  clearError: () => {},
};

function useAuthState() {
  const queryClient = useQueryClient();
  const [session, setSession] = useState<Session | null>(null);
  const [isLoading, setIsLoading] = useState(!SCREENSHOT_MODE);
  const [error, setError] = useState<string | null>(null);
  const [premiumConfirmedAt, setPremiumConfirmedAt] = useState<number | null>(() => {
    try {
      const stored = localStorage.getItem("rockscout_premium_confirmed_at");
      if (!stored) return null;
      const ts = parseInt(stored, 10);
      // 7-day TTL
      if (Date.now() - ts > 7 * 24 * 60 * 60 * 1000) {
        localStorage.removeItem("rockscout_premium_confirmed_at");
        return null;
      }
      return ts;
    } catch {
      return null;
    }
  });

  const isPremiumConfirmed = premiumConfirmedAt !== null &&
    Date.now() - premiumConfirmedAt < 7 * 24 * 60 * 60 * 1000;

  const setPremiumConfirmedCb = useCallback((ts: number) => {
    setPremiumConfirmedAt(ts);
    try {
      localStorage.setItem("rockscout_premium_confirmed_at", String(ts));
    } catch {
      // Best-effort
    }
  }, []);

  const clearPremiumConfirmation = useCallback(() => {
    setPremiumConfirmedAt(null);
    try {
      localStorage.removeItem("rockscout_premium_confirmed_at");
    } catch {
      // Best-effort
    }
  }, []);

  useEffect(() => {
    if (SCREENSHOT_MODE) return;
    supabase.auth
      .getSession()
      .then(({ data }) => {
        setSession(data.session);
      })
      .catch(() => {
        // Env vars missing or network error — treat as no session.
      })
      .finally(() => setIsLoading(false));

    const { data: listener } = supabase.auth.onAuthStateChange(
      (_event, newSession) => {
        setSession(newSession);
        // Update error reporter with user ID for attribution
        setErrorReporterUserId(newSession?.user?.id ?? null);
      },
    );

    return () => {
      listener.subscription.unsubscribe();
    };
  }, []);

  const signUp = useCallback(
    async (email: string, password: string) => {
      setError(null);

      if (!supabaseUrl || supabaseUrl === "https://placeholder.supabase.co") {
        setError(
          "This PWA build is missing its backend configuration. Please reload the page or reinstall the app.",
        );
        throw new Error("Supabase URL not configured");
      }

      try {
        const { data, error: err } = await supabase.auth.signUp({
          email,
          password,
        });
        if (err) {
          setError(err.message);
          throw err;
        }
        // If no session returned, email verification is required.
        return { needsVerification: !data.session };
      } catch (err) {
        const message = err instanceof Error ? err.message : "";
        if (message === "Failed to fetch" || message.includes("NetworkError")) {
          setError(
            "Network error. Check your connection, disable any ad blockers, and try again. If this keeps happening, clear the app cache or reinstall the PWA.",
          );
        } else if (!error) {
          setError(message || "Sign up failed. Please try again.");
        }
        throw err;
      }
    },
    [error],
  );

  const signIn = useCallback(async (email: string, password: string) => {
    setError(null);

    // Guard against a build that was deployed without Supabase env vars.
    // Without this, the browser throws the generic "Failed to fetch" and the
    // user has no idea the app is misconfigured.
    if (!supabaseUrl || supabaseUrl === "https://placeholder.supabase.co") {
      setError(
        "This PWA build is missing its backend configuration. Please reload the page or reinstall the app.",
      );
      throw new Error("Supabase URL not configured");
    }

    try {
      const { data, error: err } = await supabase.auth.signInWithPassword({
        email,
        password,
      });
      if (err) {
        setError(err.message);
        throw err;
      }
      // Sync RevenueCat entitlement to Supabase is_pro so the web sees
      // Premium status immediately if the user bought on Android/iOS, then
      // refresh the shared tier queries so the UI updates right away.
      if (data.user?.id) {
        const ok = await syncEntitlement(data.user.id);
        if (ok) {
          queryClient.invalidateQueries({ queryKey: ["tier-profile", data.user.id] });
        }
      }
    } catch (err) {
      // Translate the browser's generic "Failed to fetch" into something
      // actionable, while preserving more specific Supabase errors.
      const message = err instanceof Error ? err.message : "";
      if (message === "Failed to fetch" || message.includes("NetworkError")) {
        setError(
          "Network error. Check your connection, disable any ad blockers, and try again. If this keeps happening, clear the app cache or reinstall the PWA.",
        );
      } else if (!error) {
        setError(message || "Sign in failed. Please try again.");
      }
      throw err;
    }
  }, [error]);

  const signOut = useCallback(async () => {
    await supabase.auth.signOut();
    setSession(null);
    clearPremiumConfirmation();
  }, [clearPremiumConfirmation]);

  const clearError = useCallback(() => setError(null), []);

  return {
    session,
    user: SCREENSHOT_MODE ? CAPTURE_USER : (session?.user ?? null),
    isLoading,
    error,
    premiumConfirmedAt,
    isPremiumConfirmed,
    setPremiumConfirmed: setPremiumConfirmedCb,
    clearPremiumConfirmation,
    signUp,
    signIn,
    signOut,
    clearError,
  };
}

export const [AuthProvider, useAuth] = createContextHook(useAuthState, DEFAULT_AUTH_STATE);
