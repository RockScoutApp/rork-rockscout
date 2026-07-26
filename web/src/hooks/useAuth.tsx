import createContextHook from "@nkzw/create-context-hook";
import { useEffect, useState, useCallback } from "react";
import type { Session, User } from "@supabase/supabase-js";
import { supabase } from "@/lib/supabase";

interface AuthState {
  session: Session | null;
  user: User | null;
  isLoading: boolean;
  error: string | null;
  signUp: (email: string, password: string) => Promise<{ needsVerification: boolean }>;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  clearError: () => void;
}

function useAuthState() {
  const [session, setSession] = useState<Session | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    supabase.auth.getSession().then(({ data }) => {
      setSession(data.session);
      setIsLoading(false);
    });

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
  }, []);

  const clearError = useCallback(() => setError(null), []);

  return {
    session,
    user: session?.user ?? null,
    isLoading,
    error,
    signUp,
    signIn,
    signOut,
    clearError,
  };
}

export const [AuthProvider, useAuth] = createContextHook(useAuthState);
