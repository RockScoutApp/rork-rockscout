import { resolveSupabaseUrl } from "./auth";

/**
 * Entitlement bridge endpoint — Cloudflare Worker.
 *
 * POST /entitlement { userId }
 *
 * Asks RevenueCat whether the given user (linked via the Android/iOS SDK's
 * `logIn(userId)`) has an active Premium or legacy Pro entitlement, then
 * writes the result back to the Supabase `rockscout_profiles.is_pro` column
 * using the service-role key (bypasses RLS).
 *
 * The web PWA calls this on sign-in and when the Paywall opens, so a user who
 * buys Premium on Android and then signs into the web sees their Premium
 * status reflected immediately. The Android app also calls this after a
 * successful purchase to keep the server-side profile in sync.
 *
 * Uses RevenueCat REST API v1 (GET /v1/subscribers/{appUserId}) which is
 * authenticated with the secret API key. v1 is used instead of v2 because
 * the v1 subscriber response includes the full entitlements map in a single
 * call, while v2 requires a separate `/active_entitlements` sub-resource
 * that has been observed to return empty in some caching states.
 *
 * Requires: REVENUECAT_SECRET_API_KEY, SUPABASE_SERVICE_ROLE_KEY,
 *           EXPO_PUBLIC_SUPABASE_URL
 */

/** Entitlement identifiers — must match IapConfig.kt on Android. */
const PREMIUM_ENTITLEMENT_ID = "premium";
const PRO_ENTITLEMENT_ID = "pro";

/** RevenueCat v1 subscriber endpoint base. */
const RC_API_BASE = "https://api.revenuecat.com/v1";

interface RCEntitlement {
  expires_date: string | null;
  purchase_date: string;
  product_identifier: string;
  is_active?: boolean;
}

interface RCSubscriberResponse {
  subscriber?: {
    entitlements?: Record<string, RCEntitlement>;
  };
}

interface EntitlementEnv {
  REVENUECAT_SECRET_API_KEY?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
  EXPO_PUBLIC_RORK_APP_KEY?: string;
}

/**
 * Check whether a given entitlement is currently active.
 * An entitlement is active if it has no expiry (lifetime) or the expiry
 * is in the future.
 */
function isEntitlementActive(ent: RCEntitlement | undefined): boolean {
  if (!ent) return false;
  // RevenueCat v1 doesn't include an is_active field; we check expiry.
  if (!ent.expires_date) return true; // Lifetime / no expiry.
  const expires = new Date(ent.expires_date).getTime();
  return expires > Date.now();
}

/** Query RevenueCat for the subscriber's entitlements. */
async function checkRevenueCatEntitlements(
  secretKey: string,
  userId: string,
): Promise<boolean> {
  try {
    const resp = await fetch(`${RC_API_BASE}/subscribers/${encodeURIComponent(userId)}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${secretKey}`,
        "Content-Type": "application/json",
      },
    });

    if (!resp.ok) {
      console.error(
        `RevenueCat subscriber fetch failed: ${resp.status}`,
        await resp.text(),
      );
      return false;
    }

    const data = (await resp.json()) as RCSubscriberResponse;
    const entitlements = data.subscriber?.entitlements ?? {};

    const premiumActive = isEntitlementActive(entitlements[PREMIUM_ENTITLEMENT_ID]);
    const proActive = isEntitlementActive(entitlements[PRO_ENTITLEMENT_ID]);

    return premiumActive || proActive;
  } catch (err) {
    console.error("RevenueCat subscriber fetch error:", err);
    return false;
  }
}

/** Update the Supabase profile's is_pro column via the service-role key. */
async function updateSupabaseIsPro(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
  isPro: boolean,
): Promise<boolean> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/rest/v1/rockscout_profiles?id=eq.${encodeURIComponent(userId)}`,
      {
        method: "PATCH",
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
          "Content-Type": "application/json",
          Prefer: "return=minimal",
        },
        body: JSON.stringify({ is_pro: isPro }),
      },
    );

    if (!resp.ok) {
      console.error(
        `Supabase is_pro update failed: ${resp.status}`,
        await resp.text(),
      );
      return false;
    }
    return true;
  } catch (err) {
    console.error("Supabase is_pro update error:", err);
    return false;
  }
}

export async function handleEntitlement(
  request: Request,
  env: EntitlementEnv,
  cors: Record<string, string>,
): Promise<Response> {
  if (request.method !== "POST") {
    return new Response("method not allowed", { status: 405, headers: cors });
  }

  const headers = { ...cors, "Content-Type": "application/json" };

  // Validate required env vars.
  if (!env.REVENUECAT_SECRET_API_KEY) {
    return Response.json(
      { ok: false, error: "entitlement_not_configured" },
      { status: 503, headers },
    );
  }

  let body: { userId?: string; forcePremium?: boolean } = {};
  try {
    const text = await request.text();
    if (text) body = JSON.parse(text) as typeof body;
  } catch {
    return Response.json(
      { ok: false, error: "invalid_json" },
      { status: 400, headers },
    );
  }

  const userId = (body.userId ?? "").trim();
  if (!userId) {
    return Response.json(
      { ok: false, error: "missing_userId" },
      { status: 400, headers },
    );
  }

  // FORCE_PREMIUM path: the Android premium APK bypasses RevenueCat entirely,
  // so it has no RevenueCat entitlement. When the app sends forcePremium=true
  // (authenticated via X-App-Key), skip RevenueCat and set is_pro=true directly.
  const forcePremium = body.forcePremium === true;

  let isPremium: boolean;
  if (forcePremium) {
    // Verify the request is app-key authenticated before honoring forcePremium.
    const expectedKey = env.EXPO_PUBLIC_RORK_APP_KEY;
    const providedKey = request.headers.get("x-app-key");
    if (!expectedKey || providedKey !== expectedKey) {
      return Response.json(
        { ok: false, error: "unauthorized" },
        { status: 401, headers },
      );
    }
    isPremium = true;
  } else {
    // 1. Check RevenueCat for active entitlements.
    isPremium = await checkRevenueCatEntitlements(
      env.REVENUECAT_SECRET_API_KEY,
      userId,
    );
  }

  // 2. Write the result back to Supabase so the web PWA sees it.
  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY);
  let supabaseUpdated = false;
  if (env.SUPABASE_SERVICE_ROLE_KEY && supabaseUrl) {
    supabaseUpdated = await updateSupabaseIsPro(
      supabaseUrl,
      env.SUPABASE_SERVICE_ROLE_KEY,
      userId,
      isPremium,
    );
  }

  return Response.json(
    {
      ok: true,
      isPremium,
      supabaseUpdated,
    },
    { status: 200, headers },
  );
}
