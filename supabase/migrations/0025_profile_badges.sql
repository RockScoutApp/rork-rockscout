-- 0025_profile_badges.sql
-- Adds profile badge columns and expert verification fields to rockscout_profiles.
-- Badge booleans are computed client-side when thresholds are met.
-- Expert verification goes through a backend endpoint (web search + Haiku eval).

-- ─── Badge flag columns (computed from existing tables) ──────────────────
ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS badge_top_contributor boolean NOT NULL DEFAULT false;

ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS badge_avid_trader boolean NOT NULL DEFAULT false;

ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS badge_specimen_contributor boolean NOT NULL DEFAULT false;

-- ─── Expert verification columns ─────────────────────────────────────────
ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS expert_qualifications text;

ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS expert_field text;

ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS expert_verified boolean NOT NULL DEFAULT false;

-- Tracks state: 'none', 'pending_auto', 'auto_verified', 'pending_manual', 'approved', 'denied'
ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS expert_verification_status text NOT NULL DEFAULT 'none';

-- ─── Grants ──────────────────────────────────────────────────────────────
-- Self can update own badge flags and expert fields; all can read (existing RLS).
GRANT UPDATE (
  badge_top_contributor,
  badge_avid_trader,
  badge_specimen_contributor,
  expert_qualifications,
  expert_field
) ON public.rockscout_profiles TO authenticated;

GRANT SELECT (
  badge_top_contributor,
  badge_avid_trader,
  badge_specimen_contributor,
  expert_qualifications,
  expert_field,
  expert_verified,
  expert_verification_status
) ON public.rockscout_profiles TO anon, authenticated;

-- expert_verified and expert_verification_status are updated server-side
-- via the service role key (backend endpoint), NOT by the client directly.
