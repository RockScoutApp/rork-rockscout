-- 0022_profanity_filter_level.sql
-- Adds a per-user profanity filter level column to rockscout_profiles.
-- Values: 'off' (only explicit/slurs/rape/retard censored), 'low' (default,
-- also censors fuck variants), 'strict' (censors everything except hell/damn).
-- Synced across devices via the existing profile RLS policies.

ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS profanity_filter_level text NOT NULL DEFAULT 'low'
  CHECK (profanity_filter_level IN ('off', 'low', 'strict'));

-- Allow users to update their own profanity_filter_level (covered by existing
-- profiles_update_self policy, but be explicit about the column grant).
GRANT UPDATE (profanity_filter_level) ON public.rockscout_profiles TO authenticated;
