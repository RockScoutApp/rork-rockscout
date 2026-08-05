-- Add premium_source column to distinguish APK-verified premium from RevenueCat-verified premium.
-- This lets the backend correctly handle lapsed RevenueCat subscribers without
-- overwriting premium APK users who have no RevenueCat record.

alter table public.rockscout_profiles
    add column if not exists premium_source text
    check (premium_source in ('apk', 'revenuecat', null));

-- Backfill existing premium profiles: if is_pro is true but premium_source is
-- null, we cannot know the original source, so leave it null for the existing
-- row and let the next entitlement sync set the correct source based on the
-- caller's platform.

-- The entitlement worker uses the service-role key, so no RLS changes are needed
-- for this column beyond existing profiles_update_self / profiles_select policies.
