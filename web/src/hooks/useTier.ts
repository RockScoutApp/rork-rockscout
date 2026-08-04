import createContextHook from "@nkzw/create-context-hook";
import { useEffect, useRef } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuth, SCREENSHOT_MODE } from "@/hooks/useAuth";
import { supabase } from "@/lib/supabase";
import { syncEntitlement } from "@/lib/entitlement";

interface ProfileRow {
  is_pro: boolean;
}

interface TierState {
  tier: "free" | "premium";
  isPremium: boolean;
  isFree: boolean;
  isLoading: boolean;
}

/**
 * Safe fallback returned when useTier() is called outside TierProvider.
 * Without this, useContext returns undefined and any destructure like
 * `const { isPremium } = useTier()` crashes the entire React tree.
 */
const DEFAULT_TIER_STATE: TierState = {
  tier: "free",
  isPremium: false,
  isFree: true,
  isLoading: false,
};

function useTierState(): TierState {
  const { user, session, isLoading: authLoading } = useAuth();
  const queryClient = useQueryClient();
  const syncedOnce = useRef(false);

  const { data: profile, isLoading: profileLoading } = useQuery<ProfileRow>({
    queryKey: ["tier-profile", user?.id],
    queryFn: async () => {
      if (!user) return { is_pro: false };
      const { data, error } = await supabase
        .from("rockscout_profiles")
        .select("is_pro")
        .eq("id", user.id)
        .maybeSingle();
      if (error) throw error;
      return (data as ProfileRow) ?? { is_pro: false };
    },
    enabled: !SCREENSHOT_MODE && !!user && !!session,
    staleTime: 30_000,
    retry: 3,
  });

  // If the profile says the user is not Premium, try syncing the RevenueCat
  // entitlement once. This catches users who bought Premium on Android/iOS and
  // then opened the web PWA without a fresh sign-in.
  useEffect(() => {
    if (SCREENSHOT_MODE || !user || !session || syncedOnce.current) return;
    if (profile?.is_pro === false) {
      syncedOnce.current = true;
      syncEntitlement(user.id).then((isPremium) => {
        if (isPremium) {
          queryClient.invalidateQueries({ queryKey: ["tier-profile", user.id] });
        }
      });
    }
  }, [user, session, profile?.is_pro, queryClient]);

  const isPremium = SCREENSHOT_MODE ? true : (profile?.is_pro ?? false);
  const isLoading = SCREENSHOT_MODE
    ? false
    : authLoading || (!!user && profileLoading);

  return {
    tier: isPremium ? "premium" : "free",
    isPremium,
    isFree: !isPremium,
    isLoading,
  };
}

export const [TierProvider, useTier] = createContextHook(useTierState, DEFAULT_TIER_STATE);
