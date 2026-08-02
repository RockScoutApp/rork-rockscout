/**
 * Shared authentication + rate-limiting middleware for RockScout Cloudflare Workers.
 *
 * Auth pattern: shared-secret app key. The Android app sends the
 * EXPO_PUBLIC_RORK_APP_KEY env var in the `X-App-Key` header on every
 * protected request. The worker compares it against the server-side
 * EXPO_PUBLIC_RORK_APP_KEY secret. If they don't match, the request
 * is rejected with 401.
 *
 * Rate limiting: per-IP token-bucket using Cloudflare KV (if available)
 * or an in-memory fallback (per-isolate, best-effort). Limits are
 * per-endpoint to prevent abuse of expensive AI calls or email sends.
 */

/** Expected app key header name. */
const APP_KEY_HEADER = "x-app-key";

/** Allowed origins for CORS — restricted to the app domains. */
const ALLOWED_ORIGINS = new Set([
  "https://rockscout.app",
  "https://rockscout.net",
  "http://localhost:8080",
  "http://localhost:3000",
]);

/**
 * Checks if an origin matches the *.rork.live pattern (Rork preview/deploy domains).
 * These are dynamically generated per project, so we can't enumerate them.
 */
function isRorkLiveOrigin(origin: string): boolean {
  return /^https:\/\/[a-z0-9-]+\.rork\.live$/i.test(origin);
}

/** Per-endpoint rate-limit configs (requests per minute). */
interface RateLimitConfig {
  rpm: number;
  burst: number;
}

const RATE_LIMITS: Record<string, RateLimitConfig> = {
  "/identify": { rpm: 20, burst: 5 },
  "/identify/clarify": { rpm: 20, burst: 5 },
  "/welcome-email": { rpm: 5, burst: 2 },
  "/image-rejection-email": { rpm: 10, burst: 3 },
  "/referral/send": { rpm: 10, burst: 3 },
  "/referral/register": { rpm: 10, burst: 3 },
  // Verification endpoints are split per action. Sending an email is the
  // expensive/abusable half; checking a code is cheap and MUST never be
  // throttled, otherwise the user gets a 429 on the very next tap after the
  // code arrives (the old shared bucket had burst 1-2, so "send" drained it
  // and "verify" was rejected).
  "/email-verification:send": { rpm: 6, burst: 3 },
  "/email-verification:verify": { rpm: 60, burst: 15 },
  "/dev-sms-verify:send": { rpm: 6, burst: 3 },
  "/dev-sms-verify:verify": { rpm: 60, burst: 15 },
  // Legacy keys (kept so an un-actioned request still has a sane limit).
  "/email-verification": { rpm: 30, burst: 10 },
  "/dev-sms-verify": { rpm: 30, burst: 10 },
  "/trial": { rpm: 5, burst: 2 },
  "/delete-account": { rpm: 3, burst: 1 },
  // Admin-triggered embedding backfill — low rpm, large payload.
  "/embeddings-backfill": { rpm: 2, burst: 1 },
  // Admin-triggered specimen catalog backfill — low rpm, large payload.
  "/specimen-catalog-backfill": { rpm: 2, burst: 1 },
  // Admin-triggered artifact embeddings backfill — low rpm, large payload.
  "/artifacts-backfill": { rpm: 2, burst: 1 },
  "/app-version": { rpm: 60, burst: 10 },
  "/push/subscribe": { rpm: 10, burst: 3 },
  "/push/send": { rpm: 30, burst: 10 },
  "/museums": { rpm: 10, burst: 3 },
  "/settings/backup": { rpm: 5, burst: 2 },
  "/settings/restore": { rpm: 10, burst: 3 },
  // Entitlement bridge — called on sign-in + Paywall open (web) and after purchase (Android).
  "/entitlement": { rpm: 10, burst: 3 },
};

/** In-memory rate-limit state (per-isolate fallback when KV is absent). */
const memoryBuckets = new Map<string, { tokens: number; lastRefill: number }>();

/**
 * Resolves the CORS origin header — returns the requesting origin if
 * it's in the allow-list, otherwise a wildcard fallback for the app
 * (which sends no Origin header since it's a native HTTP client).
 */
export function resolveCorsOrigin(request: Request): string {
  const origin = request.headers.get("origin") ?? "";
  if (origin && (ALLOWED_ORIGINS.has(origin) || isRorkLiveOrigin(origin))) return origin;
  // Native app requests have no Origin header — allow them.
  return ALLOWED_ORIGINS.values().next().value ?? "*";
}

/** Build CORS headers for a given request. */
export function buildCorsHeaders(request: Request): Record<string, string> {
  const origin = resolveCorsOrigin(request);
  return {
    "Access-Control-Allow-Origin": origin,
    "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type, Authorization, X-App-Key",
    "Vary": "Origin",
  };
}

/**
 * Validates the app-key header against the server-side secret.
 * Returns true if the request is authorized, false otherwise.
 */
export function validateAppKey(request: Request, env: Env): boolean {
  const expectedKey = env.EXPO_PUBLIC_RORK_APP_KEY;
  if (!expectedKey) {
    console.error("[auth] EXPO_PUBLIC_RORK_APP_KEY is not set on the worker — all requests will 401");
    return false;
  }
  const providedKey = request.headers.get(APP_KEY_HEADER);
  if (!providedKey) {
    console.warn("[auth] Request missing X-App-Key header");
    return false;
  }
  if (providedKey !== expectedKey) {
    console.warn(
      `[auth] App key mismatch — expected ${expectedKey.slice(0, 8)}… got ${providedKey.slice(0, 8)}…`,
    );
    return false;
  }
  return true;
}

/**
 * Checks the rate limit for a given path + client IP.
 * Uses in-memory token bucket (per-isolate). Returns true if the
 * request is allowed, false if rate-limited.
 */
export function checkRateLimit(
  pathname: string,
  clientIp: string,
  rateLimitKv?: KVNamespace,
): boolean {
  const config = RATE_LIMITS[pathname];
  if (!config) return true; // No limit configured — allow.

  const key = `${pathname}:${clientIp}`;
  const now = Date.now();
  const intervalMs = 60_000; // 1 minute
  const refillRate = config.rpm / intervalMs; // tokens per ms

  let bucket = memoryBuckets.get(key);
  if (!bucket) {
    bucket = { tokens: config.burst, lastRefill: now };
    memoryBuckets.set(key, bucket);
  }

  // Refill tokens based on elapsed time.
  const elapsed = now - bucket.lastRefill;
  bucket.tokens = Math.min(config.burst, bucket.tokens + elapsed * refillRate);
  bucket.lastRefill = now;

  if (bucket.tokens < 1) {
    return false; // Rate limited.
  }

  bucket.tokens -= 1;
  return true;
}

/** Extracts the client IP from the request (Cloudflare provides CF-Connecting-IP). */
export function getClientIp(request: Request): string {
  return request.headers.get("cf-connecting-ip") ??
    request.headers.get("x-forwarded-for")?.split(",")[0]?.trim() ??
    "unknown";
}

/**
 * Auth + rate-limit guard for protected endpoints.
 * Returns an error Response if the request should be blocked, or null
 * if the request is allowed to proceed.
 */
export function guardEndpoint(
  request: Request,
  env: Env,
  pathname: string,
  cors: Record<string, string>,
  rateLimitKv?: KVNamespace,
): Response | null {
  // Auth check
  if (!validateAppKey(request, env)) {
    return Response.json(
      { error: "Unauthorized — invalid or missing app key." },
      { status: 401, headers: cors },
    );
  }

  // Rate-limit check
  const clientIp = getClientIp(request);
  if (!checkRateLimit(pathname, clientIp, rateLimitKv)) {
    return Response.json(
      { error: "Rate limit exceeded. Please try again in a moment." },
      { status: 429, headers: { ...cors, "Retry-After": "60" } },
    );
  }

  return null;
}

type Env = {
  EXPO_PUBLIC_TOOLKIT_URL: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY: string;
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  REFERRAL_KV?: KVNamespace;
  TRIAL_KV?: KVNamespace;
  RATE_LIMIT_KV?: KVNamespace;
  RESEND_API_KEY?: string;
  DEV_2FA_EMAIL_TO?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  REVENUECAT_SECRET_API_KEY?: string;
  VAPID_PUBLIC_KEY?: string;
  VAPID_PRIVATE_KEY?: string;
};

/**
 * Resolves the correct Supabase URL, preferring the project ref embedded in the
 * service-role key's JWT payload over the EXPO_PUBLIC_SUPABASE_URL env var.
 *
 * The env var can go stale when a Supabase project is migrated but the platform
 * env widget hasn't been updated yet. The service-role key is always correct for
 * its own project, so deriving the URL from its `ref` claim guarantees we hit
 * the right project regardless of a stale env var.
 *
 * Falls back to the env var if the key can't be decoded.
 */
export function resolveSupabaseUrl(
  envUrl: string | undefined,
  serviceKey: string | undefined,
): string | undefined {
  if (serviceKey) {
    try {
      const payload = JSON.parse(
        atob(serviceKey.split(".")[1] ?? ""),
      ) as { ref?: string };
      if (payload.ref) {
        return `https://${payload.ref}.supabase.co`;
      }
    } catch {
      // Not a JWT or decode failed — fall through to env var.
    }
  }
  return envUrl;
}
