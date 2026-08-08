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

type PremiumSource = "apk" | "revenuecat" | null;

interface SupabaseProfile {
  is_pro: boolean;
  premium_source: PremiumSource;
}

/** Fetch the current is_pro + premium_source values from Supabase. */
async function fetchSupabaseProfile(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
): Promise<SupabaseProfile | null> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/rest/v1/rockscout_profiles?select=is_pro,premium_source&id=eq.${encodeURIComponent(userId)}`,
      {
        method: "GET",
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
        },
      },
    );
    if (!resp.ok) return null;
    const rows = (await resp.json()) as SupabaseProfile[];
    return rows[0] ?? null;
  } catch {
    return null;
  }
}

/** Update the Supabase profile's is_pro + premium_source columns via the service-role key. */
async function updateSupabaseProfile(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
  isPro: boolean,
  premiumSource: PremiumSource,
): Promise<boolean> {
  try {
    const payload: Record<string, unknown> = { is_pro: isPro };
    if (premiumSource !== null) {
      payload.premium_source = premiumSource;
    } else {
      // Explicitly clear the column when source is null.
      payload.premium_source = null;
    }

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
        body: JSON.stringify(payload),
      },
    );

    if (!resp.ok) {
      console.error(
        `Supabase profile update failed: ${resp.status}`,
        await resp.text(),
      );
      return false;
    }
    return true;
  } catch (err) {
    console.error("Supabase profile update error:", err);
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

  let body: { userId?: string; forcePremium?: boolean; premiumSource?: string } = {};
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
  let requestedSource: PremiumSource = null;
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
    requestedSource = "apk";
  }

  // 1. Check RevenueCat for active entitlements (unless the APK forces it).
  let revenueCatPremium = false;
  if (!forcePremium) {
    revenueCatPremium = await checkRevenueCatEntitlements(
      env.REVENUECAT_SECRET_API_KEY,
      userId,
    );
  }

  // 2. Write the result back to Supabase so the web PWA sees it.
  //
  // premium_source tells us how the profile became premium:
  // - "apk": premium APK (no RevenueCat record). Keep is_pro=true regardless of RC.
  // - "revenuecat": subscription managed by RevenueCat. Follow RC active state.
  // - null: never premium or unknown.
  //
  // This prevents lapsed RevenueCat subscribers from staying premium while still
  // preserving premium APK users who bypass RevenueCat entirely.
  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY);
  let isPremium = forcePremium || revenueCatPremium;
  let supabaseUpdated = false;
  let premiumSource: PremiumSource = requestedSource;

  if (env.SUPABASE_SERVICE_ROLE_KEY && supabaseUrl) {
    const currentProfile = await fetchSupabaseProfile(
      supabaseUrl,
      env.SUPABASE_SERVICE_ROLE_KEY,
      userId,
    );
    const currentSource = currentProfile?.premium_source ?? null;
    const currentIsPro = currentProfile?.is_pro ?? false;

    if (forcePremium) {
      // Premium APK: check email allowlist before granting premium.
      // Look up the user's email via Supabase admin API.
      const userEmail = await fetchUserEmail(
        supabaseUrl,
        env.SUPABASE_SERVICE_ROLE_KEY,
        userId,
      );

      if (!userEmail) {
        // Email lookup failed — fail closed, never grant premium on error.
        return Response.json(
          {
            ok: true,
            isPremium: false,
            premiumSource: null,
            supabaseUpdated: false,
            allowed: false,
            message: "lookup_failed",
          },
          { status: 200, headers },
        );
      }

      // Check the allowlist
      const isAllowed = await checkPremiumAllowlist(
        supabaseUrl,
        env.SUPABASE_SERVICE_ROLE_KEY,
        userEmail,
      );

      if (!isAllowed) {
        // Not on the allowlist — stay on free tier.
        premiumSource = null;
        isPremium = false;
        supabaseUpdated = await updateSupabaseProfile(
          supabaseUrl,
          env.SUPABASE_SERVICE_ROLE_KEY,
          userId,
          false,
          null,
        );
        return Response.json(
          {
            ok: true,
            isPremium: false,
            premiumSource: null,
            supabaseUpdated,
            allowed: false,
            message: "not_on_allowlist",
          },
          { status: 200, headers },
        );
      }

      // On the allowlist — grant premium.
      premiumSource = "apk";
      isPremium = true;
      supabaseUpdated = await updateSupabaseProfile(
        supabaseUrl,
        env.SUPABASE_SERVICE_ROLE_KEY,
        userId,
        true,
        "apk",
      );
    } else if (revenueCatPremium) {
      // Active RevenueCat subscription: mark premium and source=revenuecat.
      premiumSource = "revenuecat";
      isPremium = true;
      supabaseUpdated = await updateSupabaseProfile(
        supabaseUrl,
        env.SUPABASE_SERVICE_ROLE_KEY,
        userId,
        true,
        "revenuecat",
      );
    } else if (currentSource === "apk") {
      // Premium APK user with no RevenueCat record: preserve premium.
      isPremium = true;
      supabaseUpdated = true;
    } else if (currentIsPro && currentSource === null) {
      // Profile is already premium but the source is unknown (e.g., an APK user
      // whose Android sync hasn't completed yet, or a legacy premium user).
      // Don't downgrade — wait for the APK or RevenueCat sync to tell us the
      // source. This prevents the web PWA's sync from accidentally revoking
      // premium while the true source is still being established.
      isPremium = true;
      supabaseUpdated = false;
    } else {
      // Lapsed RevenueCat subscriber or never-premium user: write false.
      // Keep premium_source as revenuecat so we know it was once a subscription.
      premiumSource = currentSource === "revenuecat" ? "revenuecat" : null;
      isPremium = false;
      supabaseUpdated = await updateSupabaseProfile(
        supabaseUrl,
        env.SUPABASE_SERVICE_ROLE_KEY,
        userId,
        false,
        premiumSource,
      );
    }
  }

  return Response.json(
    {
      ok: true,
      isPremium,
      premiumSource,
      supabaseUpdated,
      allowed: true,
      message: null,
    },
    { status: 200, headers },
  );
}

/** Look up a user's email via the Supabase admin API. */
async function fetchUserEmail(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
): Promise<string | null> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/auth/v1/admin/users/${encodeURIComponent(userId)}`,
      {
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
        },
      },
    );
    if (!resp.ok) {
      console.error("fetchUserEmail failed:", resp.status);
      return null;
    }
    const data = await resp.json() as { email?: string };
    return data.email?.toLowerCase() ?? null;
  } catch (err) {
    console.error("fetchUserEmail error:", String(err));
    return null;
  }
}

/** Check if an email is on the premium APK allowlist. */
async function checkPremiumAllowlist(
  supabaseUrl: string,
  serviceKey: string,
  email: string,
): Promise<boolean> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/rest/v1/rockscout_premium_apk_allowlist?email=eq.${encodeURIComponent(email.toLowerCase())}&select=email&limit=1`,
      {
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
        },
      },
    );
    if (!resp.ok) {
      console.error("checkPremiumAllowlist failed:", resp.status);
      return false;
    }
    const rows = await resp.json() as Array<{ email: string }>;
    return rows.length > 0;
  } catch (err) {
    console.error("checkPremiumAllowlist error:", String(err));
    return false;
  }
}
