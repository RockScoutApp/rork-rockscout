import { createClient, type SupabaseClient } from "@supabase/supabase-js";

const supabaseUrl = import.meta.env.EXPO_PUBLIC_SUPABASE_URL as string;
const supabaseAnonKey = import.meta.env.EXPO_PUBLIC_SUPABASE_ANON_KEY as string;

const hasConfig = Boolean(supabaseUrl && supabaseAnonKey);

if (!hasConfig) {
  console.warn(
    "Supabase env vars missing — PWA auth and data features will not work. " +
      "Set EXPO_PUBLIC_SUPABASE_URL and EXPO_PUBLIC_SUPABASE_ANON_KEY in your build environment.",
  );
}

/**
 * Supabase client.
 *
 * When env vars are missing (e.g. a production build without secrets
 * configured), a placeholder client is created so module initialization does
 * NOT throw. This lets the marketing/landing pages render normally — only
 * auth-gated PWA routes are affected. Without this guard, `createClient`
 * throws "supabaseUrl is required" at module-eval time, killing the entire
 * bundle before React mounts (black screen on every route).
 */
export const supabase: SupabaseClient = createClient(
  supabaseUrl || "https://placeholder.supabase.co",
  supabaseAnonKey || "placeholder-anon-key",
  {
    auth: {
      persistSession: true,
      autoRefreshToken: true,
      detectSessionInUrl: true,
    },
  },
);
