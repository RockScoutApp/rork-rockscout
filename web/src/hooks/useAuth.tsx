import createContextHook from "@nkzw/create-context-hook";
import { useEffect, useState, useCallback } from "react";
import type { Session, User } from "@supabase/supabase-js";
import { supabase } from "@/lib/supabase";

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
  const [isLoading, setIsLoading] = useState(true);
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
    const { error: err } = await supabase.auth.signInWithPassword({
      email,
      password,
    });
    if (err) {
      setError(err.message);
      throw err;
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
    user: session?.user ?? null,
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
