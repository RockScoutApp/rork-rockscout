/**
 * Syncs a user's RevenueCat Premium entitlement to the Supabase profile.
 *
 * Calls the backend `/entitlement` endpoint, which asks RevenueCat whether
 * the user has an active Premium or legacy Pro subscription and writes
 * `is_pro = true/false` back to the `rockscout_profiles` row.
 *
 * Called on sign-in and when the Paywall opens, so a user who buys Premium
 * on Android and then signs into the web sees their Premium status reflected
 * immediately.
 */

interface EntitlementResponse {
  ok: boolean;
  isPremium: boolean;
  supabaseUpdated: boolean;
  error?: string;
}

/**
 * Check and sync the user's Premium entitlement via the backend.
 * Returns `true` if the user has an active Premium/Pro entitlement.
 * Best-effort — returns `false` on network errors (fail open = don't block).
 */
export async function syncEntitlement(userId: string): Promise<boolean> {
  try {
    const functionsUrl = import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL as
      | string
      | undefined;
    const appKey = import.meta.env.EXPO_PUBLIC_RORK_APP_KEY as
      | string
      | undefined;
    if (!functionsUrl || !appKey) {
      console.warn("Entitlement sync not configured — env vars missing");
      return false;
    }

    const res = await fetch(`${functionsUrl}/entitlement`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-App-Key": appKey,
      },
      body: JSON.stringify({ userId }),
    });

    if (!res.ok) {
      console.warn("Entitlement sync failed:", res.status);
      return false;
    }

    const data = (await res.json()) as EntitlementResponse;
    return data.isPremium;
  } catch (err) {
    console.warn("Entitlement sync error:", err);
    return false;
  }
}
