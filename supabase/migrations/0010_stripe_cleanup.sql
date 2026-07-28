-- 0010_stripe_cleanup.sql
-- Removes all Stripe-related database artifacts now that payments are handled
-- exclusively via native in-app purchases (RevenueCat) on Android and iOS.
-- The web PWA Paywall now redirects users to subscribe in the mobile app.

-- ============================================================================
-- 1. Drop the rockscout_payment_events table (idempotent webhook event log)
-- ============================================================================
drop table if exists public.rockscout_payment_events cascade;

-- Drop the index if it still exists (drop table cascade handles it, but be safe).
drop index if exists idx_payment_events_type;

-- ============================================================================
-- 2. Remove stripe_customer_id column from rockscout_profiles
-- ============================================================================
alter table public.rockscout_profiles
    drop column if exists stripe_customer_id;

-- Drop the index that was used for Stripe customer lookups.
drop index if exists idx_profiles_stripe_customer;

-- ============================================================================
-- DONE
-- ============================================================================
