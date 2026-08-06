import createContextHook from "@nkzw/create-context-hook";
import { useEffect, useRef } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuth, SCREENSHOT_MODE } from "@/hooks/useAuth";
import { supabase } from "@/lib/supabase";
import { syncEntitlement } from "@/lib/entitlement";
import { getDeviceFingerprint } from "@/lib/deviceFingerprint";

interface ProfileRow {
  is_pro: boolean;
}

interface DeviceRow {
  id: string;
  device_fingerprint: string;
  installed_at: string;
}

interface TierState {
  tier: "free" | "premium";
  isPremium: boolean;
  isFree: boolean;
  isLoading: boolean;
  deviceOverLimit: boolean;
  /** Raw premium status from profile — true even when device is over limit. */
  rawIsPremium: boolean;
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
  deviceOverLimit: false,
  rawIsPremium: false,
};

const MAX_DEVICES = 3;

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

  const rawIsPremium = SCREENSHOT_MODE ? true : (profile?.is_pro ?? false);

  // Only check device limit when the user is actually premium.
  const { data: deviceOverLimit = false } = useQuery<boolean>({
    queryKey: ["device-limit", user?.id],
    queryFn: async () => {
      if (!user) return false;
      const { data, error } = await supabase
        .from("rockscout_installed_devices")
        .select("id,device_fingerprint,installed_at")
        .eq("user_id", user.id)
        .order("installed_at", { ascending: true });
      if (error) throw error;
      const devices = (data as DeviceRow[]) ?? [];
      const myFp = getDeviceFingerprint();
      const myIndex = devices.findIndex((d) => d.device_fingerprint === myFp);
      return myIndex >= MAX_DEVICES;
    },
    enabled: !SCREENSHOT_MODE && !!user && !!session && rawIsPremium,
    staleTime: 60_000,
    retry: 2,
  });

  // Sync the RevenueCat entitlement on every fresh sign-in. This catches users
  // who bought Premium on Android/iOS (or use a Premium APK) and then open the
  // web PWA without a fresh sign-in. The sync also refreshes an existing
  // premiumSource="apk" row so the PWA immediately sees Premium.
  useEffect(() => {
    if (SCREENSHOT_MODE || !user || !session || syncedOnce.current) return;
    syncedOnce.current = true;
    syncEntitlement(user.id).then((isPremium) => {
      if (isPremium || profile?.is_pro === false) {
        // Refresh the profile whether the sync turned premium on or the profile
        // was stale/off. This ensures the UI re-evaluates with the latest data.
        queryClient.invalidateQueries({ queryKey: ["tier-profile", user.id] });
      }
      // eslint-disable-next-line no-console
      console.log("[useTier] entitlement sync completed", { isPremium, userId: user.id });
    }).catch((err) => {
      // Best-effort sync; failures are logged so they can be diagnosed in the field.
      // eslint-disable-next-line no-console
      console.warn("[useTier] entitlement sync failed", err);
    });
  }, [user, session, profile?.is_pro, queryClient]);

  // Effective premium — false when device is over the 3-device limit.
  const isPremium = SCREENSHOT_MODE ? true : rawIsPremium && !deviceOverLimit;
  const isLoading = SCREENSHOT_MODE
    ? false
    : authLoading || (!!user && profileLoading);

  return {
    tier: isPremium ? "premium" : "free",
    isPremium,
    isFree: !isPremium,
    isLoading,
    deviceOverLimit,
    rawIsPremium,
  };
}

export const [TierProvider, useTierRaw] = createContextHook(useTierState, DEFAULT_TIER_STATE);

/**
 * Safe wrapper that guarantees a defined TierState even if the underlying
 * context hook momentarily returns undefined during provider remounts.
 */
export const useTier = (): TierState => useTierRaw() ?? DEFAULT_TIER_STATE;
