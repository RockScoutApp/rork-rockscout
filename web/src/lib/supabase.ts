import { createClient, type SupabaseClient } from "@supabase/supabase-js";
import { SUPABASE_URL, SUPABASE_ANON_KEY } from "@/lib/config";

export const supabaseUrl = SUPABASE_URL;
export const supabaseAnonKey = SUPABASE_ANON_KEY;

export const hasSupabaseConfig = Boolean(supabaseUrl && supabaseAnonKey);

if (!hasSupabaseConfig) {
  console.warn(
    "Supabase env vars missing — PWA auth and data features will not work. " +
      "Set EXPO_PUBLIC_SUPABASE_URL and EXPO_PUBLIC_SUPABASE_ANON_KEY in your build environment.",
  );
}

// ---------------------------------------------------------------------------
// Baked fallback values — used when the env-resolved config is stale/wrong.
// These mirror config.ts but are duplicated here so that a configuration
// mismatch can be recovered without importing config.ts again (avoiding
// circular resolution if config.ts itself was the source of the bad value).
// ---------------------------------------------------------------------------
const BAKED_SUPABASE_URL = "https://kblsiyyelyokhxaxefhy.supabase.co";
const BAKED_SUPABASE_ANON_KEY =
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtibHNpeXllbHlva2h4YXhlZmh5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUwNjUxMjIsImV4cCI6MjEwMDY0MTEyMn0.qrJW7EH6HHcvYtprhU26dwHX0RBfZjQ9JDEObmrFDmE";

// ---------------------------------------------------------------------------
// Internal mutable state — the Proxy delegates to whatever client is current.
// ---------------------------------------------------------------------------
const MAX_RETRIES = 3;
const RETRY_DELAYS_MS = [1_000, 3_000, 7_000];

interface ClientState {
  client: SupabaseClient;
  usingBaked: boolean;
  recovering: boolean;
}

const _state: ClientState = {
  client: createClient(
    supabaseUrl || "https://placeholder.supabase.co",
    supabaseAnonKey || "placeholder-anon-key",
    {
      auth: {
        persistSession: true,
        autoRefreshToken: true,
        detectSessionInUrl: true,
      },
    },
  ),
  usingBaked: false,
  recovering: false,
};

/**
 * Build a fresh Supabase client with the given URL and anon key.
 */
function buildClient(url: string, key: string): SupabaseClient {
  return createClient(url, key, {
    auth: {
      persistSession: true,
      autoRefreshToken: true,
      detectSessionInUrl: true,
    },
  });
}

/**
 * Determine whether an error message looks like a configuration mismatch
 * rather than a normal RLS / not-found / network-blip error.
 *
 * Triggers on:
 *  - URL format issues ("Invalid URL", "supabaseUrl is required")
 *  - Auth/key mismatches ("Invalid API key", "401", "403", "JWT")
 *  - DNS / connection refused (stale project ref)
 *  - Fetch failures that persist across retries
 */
function isConfigMismatch(errorMessage: string): boolean {
  const lower = errorMessage.toLowerCase();
  return (
    lower.includes("invalid url") ||
    lower.includes("supabaseurl is required") ||
    lower.includes("invalid api key") ||
    lower.includes("jwt") ||
    lower.includes("401") ||
    lower.includes("403") ||
    lower.includes("fetch") ||
    lower.includes("failed to fetch") ||
    lower.includes("networkerror") ||
    lower.includes("econnrefused") ||
    lower.includes("enotfound") ||
    lower.includes("getaddrinfo") ||
    lower.includes("err_invalid_url") ||
    lower.includes("placeholder")
  );
}

/**
 * Lightweight health check — queries a single row from `rockscout_config`.
 * Returns true if the client can reach Supabase and the credentials are valid.
 */
async function isClientHealthy(client: SupabaseClient): Promise<boolean> {
  try {
    const { error } = await client
      .from("rockscout_config")
      .select("key")
      .limit(1)
      .maybeSingle();
    // No error OR a normal RLS/permission error means the connection works —
    // only config-mismatch errors indicate a broken client.
    if (!error) return true;
    // RLS permission errors (42501) or relation-not-found (42P01) still mean
    // the URL and key are valid — the connection itself is healthy.
    if (
      error.code === "42501" ||
      error.code === "42P01" ||
      error.code === "PGRST116" ||
      error.message?.toLowerCase().includes("permission denied") ||
      error.message?.toLowerCase().includes("does not exist")
    ) {
      return true;
    }
    return !isConfigMismatch(error.message ?? "");
  } catch (err) {
    return !isConfigMismatch((err as Error)?.message ?? "");
  }
}

/**
 * Attempt to recover the Supabase client by retrying with the baked config.
 *
 * Called automatically on module load and also exported for manual retry.
 * Uses exponential backoff (1s → 3s → 7s). After each failed attempt with
 * the env-resolved config, the next attempt switches to the baked fallback.
 */
export async function recoverSupabaseClient(): Promise<boolean> {
  if (_state.recovering) return _state.usingBaked;
  _state.recovering = true;

  try {
    for (let attempt = 0; attempt < MAX_RETRIES; attempt++) {
      // On the first attempt, try the current (env-resolved) client.
      // On subsequent attempts, swap to baked config before testing.
      if (attempt > 0 || _state.usingBaked) {
        const url = _state.usingBaked ? BAKED_SUPABASE_URL : SUPABASE_URL;
        const key = _state.usingBaked ? BAKED_SUPABASE_ANON_KEY : SUPABASE_ANON_KEY;
        _state.client = buildClient(url, key);
      }

      // Wait before retrying (skip delay on first attempt).
      if (attempt > 0) {
        await new Promise((r) => setTimeout(r, RETRY_DELAYS_MS[attempt - 1]));
      }

      const healthy = await isClientHealthy(_state.client);
      if (healthy) {
        console.info(
          `[supabase] Client healthy${_state.usingBaked ? " (using baked fallback config)" : ""} after ${attempt + 1} attempt(s).`,
        );
        _state.recovering = false;
        return true;
      }

      // If env config failed, switch to baked for the next attempt.
      if (!_state.usingBaked) {
        console.warn(
          `[supabase] Config mismatch detected on attempt ${attempt + 1}, switching to baked fallback.`,
        );
        _state.usingBaked = true;
        _state.client = buildClient(BAKED_SUPABASE_URL, BAKED_SUPABASE_ANON_KEY);
      }
    }

    // All retries exhausted — keep the baked client as the best fallback.
    _state.client = buildClient(BAKED_SUPABASE_URL, BAKED_SUPABASE_ANON_KEY);
    _state.usingBaked = true;
    console.warn("[supabase] All retry attempts exhausted — using baked fallback config.");
    _state.recovering = false;
    return false;
  } catch (err) {
    console.error("[supabase] Recovery failed:", err);
    _state.client = buildClient(BAKED_SUPABASE_URL, BAKED_SUPABASE_ANON_KEY);
    _state.usingBaked = true;
    _state.recovering = false;
    return false;
  }
}

/**
 * Check whether the client is currently using baked fallback config.
 */
export function isUsingBakedConfig(): boolean {
  return _state.usingBaked;
}

/**
 * Supabase client — exported as a Proxy so that all 54 import sites
 * automatically delegate to the current underlying client even after
 * a background recovery swaps it for the baked-fallback version.
 *
 * This is critical: without the Proxy, early importers would capture a
 * reference to the original (broken) client and never see the recovered one.
 */
export const supabase: SupabaseClient = new Proxy({} as SupabaseClient, {
  get(_target, prop, receiver) {
    const value = Reflect.get(_state.client, prop);
    if (typeof value === "function") {
      return value.bind(_state.client);
    }
    return value;
  },
  set(_target, prop, value) {
    return Reflect.set(_state.client as object, prop, value);
  },
  has(_target, prop) {
    return Reflect.has(_state.client as object, prop);
  },
  ownKeys(_target) {
    return Reflect.ownKeys(_state.client as object);
  },
  getOwnPropertyDescriptor(_target, prop) {
    return Reflect.getOwnPropertyDescriptor(_state.client as object, prop);
  },
});

// ---------------------------------------------------------------------------
// Auto-run health check on module load.
// If the env-resolved config is wrong, this silently recovers to baked.
// ---------------------------------------------------------------------------
void recoverSupabaseClient();
