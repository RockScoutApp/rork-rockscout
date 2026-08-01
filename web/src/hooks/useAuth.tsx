import createContextHook from "@nkzw/create-context-hook";
import { useEffect, useState, useCallback } from "react";
import type { Session, User } from "@supabase/supabase-js";
import { supabase } from "@/lib/supabase";
import { syncEntitlement } from "@/lib/entitlement";

/**
 * Local capture mode. Enabled ONLY by running the dev server with
 * `VITE_SCREENSHOT_MODE=1` for automated screenshot capture. It is never
 * set in production builds, where the flag inlines as `undefined`.
 */
export const SCREENSHOT_MODE: boolean =
  import.meta.env.VITE_SCREENSHOT_MODE === "1";

/** Stand-in user used only while capturing tutorial screenshots. */
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

function useAuthState() {
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
      },
    );

    return () => {
      listener.subscription.unsubscribe();
    };
  }, []);

  const signUp = useCallback(
    async (email: string, password: string) => {
      setError(null);
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
    },
    [],
  );

  const signIn = useCallback(async (email: string, password: string) => {
    setError(null);
    const { data, error: err } = await supabase.auth.signInWithPassword({
      email,
      password,
    });
    if (err) {
      setError(err.message);
      throw err;
    }
    // Sync RevenueCat entitlement to Supabase is_pro so the web sees
    // Premium status immediately if the user bought on Android/iOS.
    if (data.user?.id) {
      void syncEntitlement(data.user.id);
    }
  }, []);

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

export const [AuthProvider, useAuth] = createContextHook(useAuthState);
