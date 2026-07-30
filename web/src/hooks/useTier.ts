import createContextHook from "@nkzw/create-context-hook";
import { useQuery } from "@tanstack/react-query";
import { useAuth, SCREENSHOT_MODE } from "@/hooks/useAuth";
import { supabase } from "@/lib/supabase";

interface ProfileRow {
  is_pro: boolean;
}

interface TierState {
  tier: "free" | "premium";
  isPremium: boolean;
  isFree: boolean;
  isLoading: boolean;
}

function useTierState(): TierState {
  const { user, session, isLoading: authLoading } = useAuth();

  const { data: profile, isLoading: profileLoading } = useQuery<ProfileRow>({
    queryKey: ["tier-profile", user?.id],
    queryFn: async () => {
      if (!user) return { is_pro: false };
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("is_pro")
        .eq("id", user.id)
        .maybeSingle();
      return (data as ProfileRow) ?? { is_pro: false };
    },
    enabled: !SCREENSHOT_MODE && !!user && !!session,
    staleTime: 60_000,
  });

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

export const [TierProvider, useTier] = createContextHook(useTierState);
