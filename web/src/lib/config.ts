/**
 * Build-time configuration with baked fallbacks.
 *
 * The auto-generated Rork env vars can go stale or revert when the platform's
 * env sync overwrites `.env` files. These baked fallbacks — mirroring Android's
 * `BuildSecrets.kt` and iOS's `AppSecrets` — guarantee the app always points to
 * the correct Supabase project and backend, even when `import.meta.env` is empty
 * or wrong.
 *
 * If the env var is populated and correct, it takes precedence. Otherwise the
 * baked value is used.
 */

const FALLBACK_SUPABASE_URL = "https://kblsiyyelyokhxaxefhy.supabase.co";
const FALLBACK_SUPABASE_ANON_KEY =
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtibHNpeXllbHlva2h4YXhlZmh5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUwNjUxMjIsImV4cCI6MjEwMDY0MTEyMn0.qrJW7EH6HHcvYtprhU26dwHX0RBfZjQ9JDEObmrFDmE";
const FALLBACK_FUNCTIONS_URL = "https://rockscout-finder-backend.rork.app";
const FALLBACK_APP_KEY = "rpk_munggtdkjtv3tbx5sw9ge3kebajzh39k";
const FALLBACK_VAPID_PUBLIC_KEY =
  "BJAnJrBLBpZ_M7egaVtyFCTm_zEI37qba3AzKMIXD3zOSAKkyA0_eh1A2IG0qrYXBPosz3q2WQ3ITVNFk5aE6P4";

/** Returns the env var if non-empty, otherwise the baked fallback. */
function resolve(envVar: string | undefined, fallback: string): string {
  return envVar && envVar.length > 0 ? envVar : fallback;
}

export const SUPABASE_URL = resolve(
  import.meta.env.EXPO_PUBLIC_SUPABASE_URL as string | undefined,
  FALLBACK_SUPABASE_URL,
);

export const SUPABASE_ANON_KEY = resolve(
  import.meta.env.EXPO_PUBLIC_SUPABASE_ANON_KEY as string | undefined,
  FALLBACK_SUPABASE_ANON_KEY,
);

export const FUNCTIONS_URL = resolve(
  import.meta.env.EXPO_PUBLIC_RORK_FUNCTIONS_URL as string | undefined,
  FALLBACK_FUNCTIONS_URL,
);

export const APP_KEY = resolve(
  import.meta.env.EXPO_PUBLIC_RORK_APP_KEY as string | undefined,
  FALLBACK_APP_KEY,
);

export const VAPID_PUBLIC_KEY = resolve(
  import.meta.env.EXPO_PUBLIC_VAPID_PUBLIC_KEY as string | undefined,
  FALLBACK_VAPID_PUBLIC_KEY,
);
