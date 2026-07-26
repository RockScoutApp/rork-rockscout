import { createClient } from "@supabase/supabase-js";

const supabaseUrl = import.meta.env.EXPO_PUBLIC_SUPABASE_URL as string;
const supabaseAnonKey = import.meta.env.EXPO_PUBLIC_SUPABASE_ANON_KEY as string;

if (!supabaseUrl || !supabaseAnonKey) {
  console.warn(
    "Supabase env vars missing — PWA auth and data features will not work.",
  );
}

export const supabase = createClient(supabaseUrl, supabaseAnonKey, {
  auth: {
    persistSession: true,
    autoRefreshToken: true,
    detectSessionInUrl: true,
  },
});
