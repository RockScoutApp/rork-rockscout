-- 0026_premium_apk_allowlist.sql
-- Server-side email allowlist for the premium APK.
-- The entitlement backend checks this table when forcePremium=true.
-- Only approved emails get Premium — all others stay on the free tier.
-- Managed via Dev Console > Premium tab (through backend, not direct Supabase).

CREATE TABLE IF NOT EXISTS public.rockscout_premium_apk_allowlist (
  email text PRIMARY KEY,
  added_at timestamptz NOT NULL DEFAULT now(),
  added_by text,
  notes text
);

-- RLS: service role only — no client read/write.
-- The Dev Console goes through the backend functions which use the service role key.
ALTER TABLE public.rockscout_premium_apk_allowlist ENABLE ROW LEVEL SECURITY;

-- No policies = no client access (service role bypasses RLS).

-- Index for sorting by added date
CREATE INDEX IF NOT EXISTS idx_premium_allowlist_added_at
  ON public.rockscout_premium_apk_allowlist (added_at DESC);
