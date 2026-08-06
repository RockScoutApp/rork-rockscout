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

