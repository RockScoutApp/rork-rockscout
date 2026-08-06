#!/bin/bash
set -e

DIR="supabase/migrations"
OUT="supabase/.temp/fresh"

# ─── ALL DROPS ───────────────────────────────────────────────────────────────
cat > "$OUT/drops.sql" << 'DROPS'
-- ═══════════════════════════════════════════════════════════════════════════
-- DROP ALL ROCKSCOUT TABLES, FUNCTIONS, AND TRIGGERS
-- ═══════════════════════════════════════════════════════════════════════════

-- Drop functions
DROP FUNCTION IF EXISTS public.rockscout_is_blocked(uuid, uuid) CASCADE;
DROP FUNCTION IF EXISTS public.rockscout_are_connected(uuid, uuid) CASCADE;
DROP FUNCTION IF EXISTS public.rockscout_enforce_five_posts() CASCADE;
DROP FUNCTION IF EXISTS public.rockscout_set_owner_user_id() CASCADE;
DROP FUNCTION IF EXISTS public.match_specimen_embeddings(vector(1536), int) CASCADE;
DROP FUNCTION IF EXISTS public.match_artifact_embeddings(vector(1536), int) CASCADE;
DROP FUNCTION IF EXISTS public.check_warning_thresholds() CASCADE;
DROP FUNCTION IF EXISTS public.enforce_group_member_cap() CASCADE;
DROP FUNCTION IF EXISTS public.notify_fatal_error() CASCADE;

-- Drop all tables (including fake ones from previous bad SQL)
DROP TABLE IF EXISTS public.rockscout_connections CASCADE;
DROP TABLE IF EXISTS public.rockscout_message_requests CASCADE;
DROP TABLE IF EXISTS public.rockscout_friend_requests CASCADE;
DROP TABLE IF EXISTS public.rockscout_blocks CASCADE;
DROP TABLE IF EXISTS public.rockscout_threads CASCADE;
DROP TABLE IF EXISTS public.rockscout_messages CASCADE;
DROP TABLE IF EXISTS public.rockscout_pings CASCADE;
DROP TABLE IF EXISTS public.rockscout_posts CASCADE;
DROP TABLE IF EXISTS public.rockscout_post_likes CASCADE;
DROP TABLE IF EXISTS public.rockscout_post_comments CASCADE;
DROP TABLE IF EXISTS public.rockscout_notifications CASCADE;
DROP TABLE IF EXISTS public.rockscout_trade_listings CASCADE;
DROP TABLE IF EXISTS public.rockscout_trade_interests CASCADE;
DROP TABLE IF EXISTS public.specimen_embeddings CASCADE;
DROP TABLE IF EXISTS public.specimen_catalog CASCADE;
DROP TABLE IF EXISTS public.rockscout_collection CASCADE;
DROP TABLE IF EXISTS public.rockscout_wishlist CASCADE;
DROP TABLE IF EXISTS public.rockscout_field_journal CASCADE;
DROP TABLE IF EXISTS public.rockscout_captures CASCADE;
DROP TABLE IF EXISTS public.rockscout_saved_images CASCADE;
DROP TABLE IF EXISTS public.rockscout_favorite_spots CASCADE;
DROP TABLE IF EXISTS public.rockscout_trips CASCADE;
DROP TABLE IF EXISTS public.rockscout_aurora_saved_spots CASCADE;
DROP TABLE IF EXISTS public.rockscout_liked_specimens CASCADE;
DROP TABLE IF EXISTS public.rockscout_installed_devices CASCADE;
DROP TABLE IF EXISTS public.rockscout_settings_backup CASCADE;
DROP TABLE IF EXISTS public.rockscout_push_subscriptions CASCADE;
DROP TABLE IF EXISTS public.rockscout_payment_events CASCADE;
DROP TABLE IF EXISTS public.artifact_embeddings CASCADE;
DROP TABLE IF EXISTS public.error_logs CASCADE;
DROP TABLE IF EXISTS public.rockscout_error_logs CASCADE;
DROP TABLE IF EXISTS public.rockscout_profiles CASCADE;
DROP TABLE IF EXISTS public.rockscout_config CASCADE;
-- Fake tables from previous bad SQL
DROP TABLE IF EXISTS public.rockscout_pwa_payments CASCADE;
DROP TABLE IF EXISTS public.rockscout_profile_highlight_color CASCADE;
DROP TABLE IF EXISTS public.rockscout_reports CASCADE;
DROP TABLE IF EXISTS public.rockscout_moderation_actions CASCADE;
DROP TABLE IF EXISTS public.rockscout_app_reviews CASCADE;
DROP TABLE IF EXISTS public.rockscout_daily_bonus CASCADE;
DROP TABLE IF EXISTS public.rockscout_artifact_submissions CASCADE;
DROP TABLE IF EXISTS public.rockscout_park_checkins CASCADE;
DROP TABLE IF EXISTS public.rockscout_digsite_checkins CASCADE;
DROP TABLE IF EXISTS public.rockscout_museum_checkins CASCADE;
DROP TABLE IF EXISTS public.rockscout_digsite_reviews CASCADE;
DROP TABLE IF EXISTS public.rockscout_museum_reviews CASCADE;
DROP TABLE IF EXISTS public.rockscout_crash_reports CASCADE;
DROP TABLE IF EXISTS public.rockscout_user_activity CASCADE;
DROP TABLE IF EXISTS public.rockscout_achievements CASCADE;
DROP TABLE IF EXISTS public.rockscout_achievement_definitions CASCADE;
DROP TABLE IF EXISTS public.rockscout_badge_progress CASCADE;
DROP TABLE IF EXISTS public.rockscout_badge_definitions CASCADE;
DROP TABLE IF EXISTS public.rockscout_park_favorites CASCADE;
DROP TABLE IF EXISTS public.rockscout_digsite_favorites CASCADE;
DROP TABLE IF EXISTS public.rockscout_museum_favorites CASCADE;
DROP TABLE IF EXISTS public.rockscout_park_photos CASCADE;
DROP TABLE IF EXISTS public.rockscout_digsite_photos CASCADE;
DROP TABLE IF EXISTS public.rockscout_museum_photos CASCADE;
DROP TABLE IF EXISTS public.rockscout_invite_codes CASCADE;
DROP TABLE IF EXISTS public.rockscout_invite_redemptions CASCADE;
DROP TABLE IF EXISTS public.rockscout_global_leaderboard CASCADE;
DROP TABLE IF EXISTS public.user_photos CASCADE;
DROP TABLE IF EXISTS public.group_chats CASCADE;
DROP TABLE IF EXISTS public.group_chat_members CASCADE;
DROP TABLE IF EXISTS public.group_chat_invites CASCADE;
DROP TABLE IF EXISTS public.group_messages CASCADE;
DROP TABLE IF EXISTS public.user_warnings CASCADE;
DROP TABLE IF EXISTS public.chat_threads CASCADE;
DROP TABLE IF EXISTS public.chat_thread_participants CASCADE;
DROP TABLE IF EXISTS public.chat_messages CASCADE;
DROP TABLE IF EXISTS public.chat_message_reads CASCADE;
DROP TABLE IF EXISTS public.self_harm_warnings CASCADE;
DROP TABLE IF EXISTS public.report_notifications CASCADE;
DROP TABLE IF EXISTS public.chat_typing_status CASCADE;

-- Drop wrong storage policies from previous bad SQL
DROP POLICY IF EXISTS "User can upload own photos" ON storage.objects;
DROP POLICY IF EXISTS "User can read own photos" ON storage.objects;
DROP POLICY IF EXISTS "User can delete own photos" ON storage.objects;

-- Drop cron jobs
DO $$
BEGIN
  PERFORM cron.unschedule('cleanup-old-error-logs');
EXCEPTION WHEN OTHERS THEN NULL;
END $$;
DO $$
BEGIN
  PERFORM cron.unschedule('fatal-error-check');
EXCEPTION WHEN OTHERS THEN NULL;
END $$;

DROPS

# ─── TRADE LISTINGS ALTER (insert between 0013 and 0014) ─────────────────────
cat > "$OUT/alter_trade.sql" << 'ALTER'
-- ═══════════════════════════════════════════════════════════════════════════
-- ALTER trade_listings to add columns from 0014's merged schema
-- (0001 created it with owner_user_id only; 0014's RLS policies need user_id)
-- ═══════════════════════════════════════════════════════════════════════════
ALTER TABLE public.rockscout_trade_listings
    ADD COLUMN IF NOT EXISTS user_id uuid REFERENCES auth.users(id) ON DELETE CASCADE,
    ADD COLUMN IF NOT EXISTS listing_id text NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS listing_mode text NOT NULL DEFAULT 'SWAP',
    ADD COLUMN IF NOT EXISTS price text NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS owner_username text,
    ADD COLUMN IF NOT EXISTS synced_at timestamptz NOT NULL DEFAULT now();

CREATE UNIQUE INDEX IF NOT EXISTS rockscout_trade_listings_user_listing_id_key
    ON public.rockscout_trade_listings (user_id, listing_id);

CREATE OR REPLACE FUNCTION public.rockscout_set_owner_user_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.owner_user_id IS NULL THEN
        NEW.owner_user_id := NEW.user_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_set_owner_user_id ON public.rockscout_trade_listings;
CREATE TRIGGER trg_set_owner_user_id
    BEFORE INSERT ON public.rockscout_trade_listings
    FOR EACH ROW EXECUTE FUNCTION public.rockscout_set_owner_user_id();

ALTER

# ─── ROCKSCOUT_CONFIG (insert between 0014 and 0015) ────────────────────────
cat > "$OUT/config.sql" << 'CONFIG'
-- ═══════════════════════════════════════════════════════════════════════════
-- rockscout_config table — needed by 0016's notify_fatal_error() trigger
-- No migration creates this table; 0016's trigger reads from it.
-- ═══════════════════════════════════════════════════════════════════════════
CREATE TABLE IF NOT EXISTS public.rockscout_config (
    key         text PRIMARY KEY,
    value       text NOT NULL,
    updated_at  timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE public.rockscout_config DISABLE ROW LEVEL SECURITY;
GRANT SELECT ON public.rockscout_config TO anon, authenticated;

INSERT INTO public.rockscout_config (key, value) VALUES
    ('worker_url', 'https://rockscout-finder-backend.rork.app'),
    ('app_key', 'rpk_munggtdkjtv3tbx5sw9ge3kebajzh39k')
ON CONFLICT (key) DO NOTHING;

CONFIG

# ─── BUILD PARTS ─────────────────────────────────────────────────────────────

# Part A: Drops + 0001-0005
cat "$OUT/drops.sql" \
    "$DIR/0001_rockscout_social_schema.sql" \
    "$DIR/0002_rls_tightening_and_indexes.sql" \
    "$DIR/0003_specimen_embeddings.sql" \
    "$DIR/0004_pwa_specimen_catalog.sql" \
    "$DIR/0005_pwa_field_journal_trips_favorites.sql" \
    > "$OUT/partA.sql"

# Part B: 0006-0013
cat "$DIR/0006_pwa_payments_tokens_push.sql" \
    "$DIR/0007_artifact_embeddings.sql" \
    "$DIR/0008_pwa_field_captures_saved_images.sql" \
    "$DIR/0009_pwa_installed_devices.sql" \
    "$DIR/0010_stripe_cleanup.sql" \
    "$DIR/0011_android_synced_data.sql" \
    "$DIR/0012_settings_backup.sql" \
    "$DIR/0013_error_logs.sql" \
    > "$OUT/partB.sql"

# Part C: ALTER trade + 0014 + config
cat "$OUT/alter_trade.sql" \
    "$DIR/0014_consolidated_rls_and_missing_tables.sql" \
    "$OUT/config.sql" \
    > "$OUT/partC.sql"

# Part D: 0015-0023
cat "$DIR/0015_user_photos_storage_and_sync.sql" \
    "$DIR/0016_fatal_error_email_trigger.sql" \
    "$DIR/0017_group_chats_and_warnings.sql" \
    "$DIR/0018_fix_grants_public_chats_self_harm.sql" \
    "$DIR/0019_chat_typing_status.sql" \
    "$DIR/0020_profile_highlight_color.sql" \
    "$DIR/0021_premium_source.sql" \
    "$DIR/0022_profanity_filter_level.sql" \
    "$DIR/0023_device_platform_column.sql" \
    > "$OUT/partD.sql"

echo "Part sizes:"
wc -l "$OUT"/part{A,B,C,D}.sql
echo ""
echo "Total:"
cat "$OUT"/part{A,B,C,D}.sql | wc -l
