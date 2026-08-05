import { useQuery } from "@tanstack/react-query";
import { supabase } from "@/lib/supabase";
import { useAuth } from "@/hooks/useAuth";
import type { ProfanityLevel } from "@/lib/profanity-filter";

/**
 * Fetches the current user's profanity filter level from Supabase.
 * Shared across all web pages via React Query cache (single query key).
 * Defaults to "low" if the user is not signed in or the column is unset.
 */
export function useProfanityLevel() {
  const { user } = useAuth();

  const { data } = useQuery<ProfanityLevel>({
    queryKey: ["profanity-level", user?.id],
    queryFn: async () => {
      if (!user) return "low";
      const { data } = await supabase
        .from("rockscout_profiles")
        .select("profanity_filter_level")
        .eq("id", user.id)
        .maybeSingle();
      const level = (data as { profanity_filter_level?: string } | null)?.profanity_filter_level;
      if (level === "off" || level === "low" || level === "strict") return level;
      return "low";
    },
    enabled: !!user,
    staleTime: 30_000,
  });

  return data ?? "low";
}
