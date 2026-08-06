-- consolidated_chat_and_hardening.sql
-- Self-contained, idempotent merge of migrations 0017–0024.
-- Creates all chat tables, config table, profile columns, cron jobs,
-- read receipts, RLS hardening, schema additions, and cleanup tasks.
-- Safe to re-run — uses IF NOT EXISTS / DROP IF EXISTS throughout.

-- ============================================================================
-- 0. EXTENSIONS
-- ============================================================================
CREATE EXTENSION IF NOT EXISTS pg_cron;
CREATE EXTENSION IF NOT EXISTS pg_net;
GRANT USAGE ON SCHEMA cron TO postgres;
GRANT USAGE ON SCHEMA net TO postgres;

-- ============================================================================
-- 0a. rockscout_config — key/value config table used by DB triggers
--     (0016 and 0024 both read worker_url and app_key from here)
-- ============================================================================
CREATE TABLE IF NOT EXISTS public.rockscout_config (
  key   text PRIMARY KEY,
  value text NOT NULL
);

ALTER TABLE public.rockscout_config DISABLE ROW LEVEL SECURITY;
GRANT SELECT ON public.rockscout_config TO anon, authenticated;

-- Seed the config values if not already set
INSERT INTO public.rockscout_config (key, value)
VALUES ('worker_url', 'https://rockscout-finder-backend.rork.app')
ON CONFLICT (key) DO NOTHING;

INSERT INTO public.rockscout_config (key, value)
VALUES ('app_key', 'rpk_munggtdkjtv3tbx5sw9ge3kebajzh39k')
ON CONFLICT (key) DO NOTHING;

-- ============================================================================
-- 1. PROFILE COLUMN ADDITIONS (0020, 0021, 0022)
-- ============================================================================
ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS highlight_color text DEFAULT NULL;

ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS premium_source text
  CHECK (premium_source IN ('apk', 'revenuecat', null));

ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS profanity_filter_level text NOT NULL DEFAULT 'low'
  CHECK (profanity_filter_level IN ('off', 'low', 'strict'));

ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS banned_at timestamptz;

-- Profile RLS: ensure all can read, self can update
DROP POLICY IF EXISTS "Profiles are readable by all" ON public.rockscout_profiles;
CREATE POLICY "Profiles are readable by all"
  ON public.rockscout_profiles FOR SELECT
  USING (true);

DROP POLICY IF EXISTS "Users can update own profile" ON public.rockscout_profiles;
CREATE POLICY "Users can update own profile"
  ON public.rockscout_profiles FOR UPDATE
  USING (auth.uid() = id);

GRANT UPDATE (profanity_filter_level) ON public.rockscout_profiles TO authenticated;
GRANT UPDATE (highlight_color) ON public.rockscout_profiles TO authenticated;

-- ============================================================================
-- 2. DEVICE PLATFORM COLUMN (0023)
-- ============================================================================
ALTER TABLE public.rockscout_installed_devices
  ADD COLUMN IF NOT EXISTS device_platform text NOT NULL DEFAULT 'unknown';

UPDATE public.rockscout_installed_devices
  SET device_platform = 'web'
  WHERE device_platform = 'unknown';

-- ============================================================================
-- 3. GROUP CHAT TABLES (0017)
-- ============================================================================
CREATE TABLE IF NOT EXISTS group_chats (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  name text NOT NULL,
  subject text DEFAULT '',
  creator_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  max_members int,
  profanity_filter_level text NOT NULL DEFAULT 'normal' CHECK (profanity_filter_level IN ('normal','strict')),
  header_image_url text,
  scroll_speed_setting text NOT NULL DEFAULT 'normal' CHECK (scroll_speed_setting IN ('normal','half','stop')),
  created_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz
);

CREATE TABLE IF NOT EXISTS group_chat_members (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  joined_at timestamptz NOT NULL DEFAULT now(),
  role text NOT NULL DEFAULT 'member' CHECK (role IN ('creator','member')),
  UNIQUE (group_chat_id, user_id)
);

CREATE TABLE IF NOT EXISTS group_chat_invites (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  inviter_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  invitee_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  status text NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','accepted','declined')),
  created_at timestamptz NOT NULL DEFAULT now(),
  responded_at timestamptz
);

CREATE TABLE IF NOT EXISTS group_messages (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  sender_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  body text DEFAULT '',
  image_url text,
  reply_to_message_id text,
  tagged_user_ids uuid[] DEFAULT '{}',
  read_at timestamptz,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS user_warnings (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reason text DEFAULT '',
  source text DEFAULT 'chat',
  source_id text,
  auto_reported boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS chat_threads (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  created_at timestamptz NOT NULL DEFAULT now(),
  last_message_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS chat_thread_participants (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  thread_id text NOT NULL REFERENCES chat_threads(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  joined_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (thread_id, user_id)
);

CREATE TABLE IF NOT EXISTS chat_messages (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  thread_id text NOT NULL REFERENCES chat_threads(id) ON DELETE CASCADE,
  sender_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  body text DEFAULT '',
  image_url text,
  reply_to_message_id text,
  tagged_user_ids uuid[] DEFAULT '{}',
  read_at timestamptz,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS chat_message_reads (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  message_id text NOT NULL REFERENCES chat_messages(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  read_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (message_id, user_id)
);

-- ============================================================================
-- 3a. GROUP MESSAGE READ RECEIPTS (0024)
-- ============================================================================
CREATE TABLE IF NOT EXISTS group_message_reads (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  message_id text NOT NULL REFERENCES group_messages(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  read_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (message_id, user_id)
);

-- ============================================================================
-- 3b. SELF-HARM WARNINGS + REPORT NOTIFICATIONS (0018)
-- ============================================================================
CREATE TABLE IF NOT EXISTS self_harm_warnings (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reason text DEFAULT '',
  source text DEFAULT 'chat',
  source_id text,
  auto_reported boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS report_notifications (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  reported_user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reporter_id uuid REFERENCES auth.users(id) ON DELETE SET NULL,
  reason text DEFAULT '',
  source text DEFAULT 'manual',
  source_id text,
  email_sent boolean DEFAULT false,
  bell_notification_sent boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);

-- ============================================================================
-- 3c. CHAT TYPING STATUS (0019)
-- ============================================================================
CREATE TABLE IF NOT EXISTS chat_typing_status (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  chat_id TEXT NOT NULL,
  user_id TEXT NOT NULL REFERENCES public.rockscout_profiles(id) ON DELETE CASCADE,
  is_typing BOOLEAN DEFAULT false,
  updated_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE(chat_id, user_id)
);

-- ============================================================================
-- 4. INDEXES — original (0017) + additional (0024)
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_group_chat_members_user ON group_chat_members(user_id);
CREATE INDEX IF NOT EXISTS idx_group_chat_members_group ON group_chat_members(group_chat_id);
CREATE INDEX IF NOT EXISTS idx_group_chat_members_user_joined
  ON group_chat_members (user_id, joined_at DESC);
CREATE INDEX IF NOT EXISTS idx_group_chat_members_group_user
  ON group_chat_members (group_chat_id, user_id);

CREATE INDEX IF NOT EXISTS idx_group_messages_chat ON group_messages(group_chat_id, created_at);
CREATE INDEX IF NOT EXISTS idx_group_messages_sender
  ON group_messages (sender_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_group_messages_chat_sender
  ON group_messages (group_chat_id, sender_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_group_chat_invites_invitee ON group_chat_invites(invitee_id, status);
CREATE INDEX IF NOT EXISTS idx_group_chat_invites_group
  ON group_chat_invites (group_chat_id, status);

CREATE INDEX IF NOT EXISTS idx_user_warnings_user ON user_warnings(user_id, created_at);

CREATE INDEX IF NOT EXISTS idx_chat_thread_participants_user ON chat_thread_participants(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_thread_participants_thread
  ON chat_thread_participants (thread_id, user_id);

CREATE INDEX IF NOT EXISTS idx_chat_messages_thread ON chat_messages(thread_id, created_at);
CREATE INDEX IF NOT EXISTS idx_chat_messages_thread_sender
  ON chat_messages (thread_id, sender_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_chat_message_reads_message ON chat_message_reads(message_id);
CREATE INDEX IF NOT EXISTS idx_chat_message_reads_user
  ON chat_message_reads (user_id, read_at DESC);

CREATE INDEX IF NOT EXISTS idx_group_message_reads_message
  ON group_message_reads (message_id);
CREATE INDEX IF NOT EXISTS idx_group_message_reads_user
  ON group_message_reads (user_id, read_at DESC);

CREATE INDEX IF NOT EXISTS idx_self_harm_warnings_user ON self_harm_warnings(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_report_notifications_user ON report_notifications(reported_user_id, created_at);

CREATE INDEX IF NOT EXISTS idx_typing_status_chat_id ON chat_typing_status(chat_id);
CREATE INDEX IF NOT EXISTS idx_typing_status_updated_at ON chat_typing_status(updated_at DESC);

-- ============================================================================
-- 5. RLS POLICIES
-- ============================================================================

-- ─── group_chats ──────────────────────────────────────────────────────────
ALTER TABLE group_chats ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "group_chats_select" ON group_chats;
CREATE POLICY "group_chats_select" ON group_chats FOR SELECT
  USING (
    deleted_at IS NULL AND (
      creator_id = auth.uid() OR
      EXISTS (
        SELECT 1 FROM group_chat_members m
        WHERE m.group_chat_id = group_chats.id AND m.user_id = auth.uid()
      ) OR
      true
    )
  );

DROP POLICY IF EXISTS "group_chats_insert" ON group_chats;
CREATE POLICY "group_chats_insert" ON group_chats FOR INSERT
  WITH CHECK (creator_id = auth.uid());

DROP POLICY IF EXISTS "group_chats_update" ON group_chats;
CREATE POLICY "group_chats_update" ON group_chats FOR UPDATE
  USING (creator_id = auth.uid());

DROP POLICY IF EXISTS "group_chats_delete" ON group_chats;
CREATE POLICY "group_chats_delete" ON group_chats FOR DELETE
  USING (creator_id = auth.uid());

-- ─── group_chat_members ───────────────────────────────────────────────────
ALTER TABLE group_chat_members ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "gcm_select" ON group_chat_members;
CREATE POLICY "gcm_select" ON group_chat_members FOR SELECT
  USING (
    user_id = auth.uid() OR EXISTS (
      SELECT 1 FROM group_chat_members m2
      WHERE m2.group_chat_id = group_chat_members.group_chat_id AND m2.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "gcm_insert" ON group_chat_members;
CREATE POLICY "gcm_insert" ON group_chat_members FOR INSERT
  WITH CHECK (user_id = auth.uid());

DROP POLICY IF EXISTS "gcm_delete" ON group_chat_members;
CREATE POLICY "gcm_delete" ON group_chat_members FOR DELETE
  USING (user_id = auth.uid());

-- ─── group_chat_invites ───────────────────────────────────────────────────
ALTER TABLE group_chat_invites ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "gci_select" ON group_chat_invites;
CREATE POLICY "gci_select" ON group_chat_invites FOR SELECT
  USING (invitee_id = auth.uid() OR inviter_id = auth.uid());

DROP POLICY IF EXISTS "gci_insert" ON group_chat_invites;
CREATE POLICY "gci_insert" ON group_chat_invites FOR INSERT
  WITH CHECK (inviter_id = auth.uid());

DROP POLICY IF EXISTS "gci_update" ON group_chat_invites;
CREATE POLICY "gci_update" ON group_chat_invites FOR UPDATE
  USING (invitee_id = auth.uid());

-- ─── group_messages ───────────────────────────────────────────────────────
ALTER TABLE group_messages ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "gm_select" ON group_messages;
CREATE POLICY "gm_select" ON group_messages FOR SELECT
  USING (EXISTS (
    SELECT 1 FROM group_chat_members m
    WHERE m.group_chat_id = group_messages.group_chat_id AND m.user_id = auth.uid()
  ));

DROP POLICY IF EXISTS "gm_insert" ON group_messages;
CREATE POLICY "gm_insert" ON group_messages FOR INSERT
  WITH CHECK (
    sender_id = auth.uid() AND EXISTS (
      SELECT 1 FROM group_chat_members m
      WHERE m.group_chat_id = group_messages.group_chat_id AND m.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "gm_update" ON group_messages;
CREATE POLICY "gm_update" ON group_messages FOR UPDATE
  USING (sender_id = auth.uid());

DROP POLICY IF EXISTS "gm_delete" ON group_messages;
CREATE POLICY "gm_delete" ON group_messages FOR DELETE
  USING (sender_id = auth.uid());

-- ─── group_message_reads ──────────────────────────────────────────────────
ALTER TABLE group_message_reads ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "gmr_select" ON group_message_reads;
CREATE POLICY "gmr_select" ON group_message_reads FOR SELECT
  USING (
    user_id = auth.uid() OR EXISTS (
      SELECT 1 FROM group_messages gm
      JOIN group_chat_members m ON m.group_chat_id = gm.group_chat_id
      WHERE gm.id = group_message_reads.message_id AND m.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "gmr_insert" ON group_message_reads;
CREATE POLICY "gmr_insert" ON group_message_reads FOR INSERT
  WITH CHECK (
    user_id = auth.uid() AND EXISTS (
      SELECT 1 FROM group_messages gm
      JOIN group_chat_members m ON m.group_chat_id = gm.group_chat_id
      WHERE gm.id = group_message_reads.message_id AND m.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "gmr_delete" ON group_message_reads;
CREATE POLICY "gmr_delete" ON group_message_reads FOR DELETE
  USING (user_id = auth.uid());

-- ─── user_warnings ────────────────────────────────────────────────────────
ALTER TABLE user_warnings ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "uw_select" ON user_warnings;
CREATE POLICY "uw_select" ON user_warnings FOR SELECT
  USING (user_id = auth.uid());

DROP POLICY IF EXISTS "uw_insert" ON user_warnings;
CREATE POLICY "uw_insert" ON user_warnings FOR INSERT
  WITH CHECK (user_id = auth.uid());

-- ─── self_harm_warnings ───────────────────────────────────────────────────
ALTER TABLE self_harm_warnings ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "shw_select" ON self_harm_warnings;
CREATE POLICY "shw_select" ON self_harm_warnings FOR SELECT
  USING (user_id = auth.uid());

DROP POLICY IF EXISTS "shw_insert" ON self_harm_warnings;
CREATE POLICY "shw_insert" ON self_harm_warnings FOR INSERT
  WITH CHECK (user_id = auth.uid());

-- ─── report_notifications ─────────────────────────────────────────────────
ALTER TABLE report_notifications ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "rn_select" ON report_notifications;
CREATE POLICY "rn_select" ON report_notifications FOR SELECT
  USING (reported_user_id = auth.uid() OR reporter_id = auth.uid());

DROP POLICY IF EXISTS "rn_insert" ON report_notifications;
CREATE POLICY "rn_insert" ON report_notifications FOR INSERT
  WITH CHECK (reporter_id = auth.uid() OR reporter_id IS NULL);

-- ─── chat_threads ─────────────────────────────────────────────────────────
ALTER TABLE chat_threads ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "ct_select" ON chat_threads;
CREATE POLICY "ct_select" ON chat_threads FOR SELECT
  USING (EXISTS (
    SELECT 1 FROM chat_thread_participants p
    WHERE p.thread_id = chat_threads.id AND p.user_id = auth.uid()
  ));

DROP POLICY IF EXISTS "ct_insert" ON chat_threads;
CREATE POLICY "ct_insert" ON chat_threads FOR INSERT
  WITH CHECK (true);

DROP POLICY IF EXISTS "ct_update" ON chat_threads;
CREATE POLICY "ct_update" ON chat_threads FOR UPDATE
  USING (EXISTS (
    SELECT 1 FROM chat_thread_participants p
    WHERE p.thread_id = chat_threads.id AND p.user_id = auth.uid()
  ));

-- ─── chat_thread_participants ─────────────────────────────────────────────
ALTER TABLE chat_thread_participants ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "ctp_select" ON chat_thread_participants;
CREATE POLICY "ctp_select" ON chat_thread_participants FOR SELECT
  USING (
    user_id = auth.uid() OR EXISTS (
      SELECT 1 FROM chat_thread_participants p2
      WHERE p2.thread_id = chat_thread_participants.thread_id AND p2.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "ctp_insert" ON chat_thread_participants;
CREATE POLICY "ctp_insert" ON chat_thread_participants FOR INSERT
  WITH CHECK (user_id = auth.uid() OR EXISTS (
    SELECT 1 FROM chat_thread_participants p2
    WHERE p2.thread_id = chat_thread_participants.thread_id AND p2.user_id = auth.uid()
  ));

DROP POLICY IF EXISTS "ctp_delete" ON chat_thread_participants;
CREATE POLICY "ctp_delete" ON chat_thread_participants FOR DELETE
  USING (user_id = auth.uid());

-- ─── chat_messages ────────────────────────────────────────────────────────
ALTER TABLE chat_messages ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "cm_select" ON chat_messages;
CREATE POLICY "cm_select" ON chat_messages FOR SELECT
  USING (EXISTS (
    SELECT 1 FROM chat_thread_participants p
    WHERE p.thread_id = chat_messages.thread_id AND p.user_id = auth.uid()
  ));

DROP POLICY IF EXISTS "cm_insert" ON chat_messages;
CREATE POLICY "cm_insert" ON chat_messages FOR INSERT
  WITH CHECK (
    sender_id = auth.uid() AND EXISTS (
      SELECT 1 FROM chat_thread_participants p
      WHERE p.thread_id = chat_messages.thread_id AND p.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "cm_update" ON chat_messages;
CREATE POLICY "cm_update" ON chat_messages FOR UPDATE
  USING (sender_id = auth.uid());

DROP POLICY IF EXISTS "cm_delete" ON chat_messages;
CREATE POLICY "cm_delete" ON chat_messages FOR DELETE
  USING (sender_id = auth.uid());

-- ─── chat_message_reads ───────────────────────────────────────────────────
ALTER TABLE chat_message_reads ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "cmr_select" ON chat_message_reads;
CREATE POLICY "cmr_select" ON chat_message_reads FOR SELECT
  USING (
    user_id = auth.uid() OR EXISTS (
      SELECT 1 FROM chat_messages cm
      JOIN chat_thread_participants p ON p.thread_id = cm.thread_id
      WHERE cm.id = chat_message_reads.message_id AND p.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "cmr_insert" ON chat_message_reads;
CREATE POLICY "cmr_insert" ON chat_message_reads FOR INSERT
  WITH CHECK (user_id = auth.uid());

DROP POLICY IF EXISTS "cmr_delete" ON chat_message_reads;
CREATE POLICY "cmr_delete" ON chat_message_reads FOR DELETE
  USING (user_id = auth.uid());

-- ─── chat_typing_status ───────────────────────────────────────────────────
ALTER TABLE chat_typing_status ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "users_can_read_typing_status" ON chat_typing_status;
CREATE POLICY "users_can_read_typing_status" ON chat_typing_status
  FOR SELECT USING (
    EXISTS (
      SELECT 1 FROM chat_thread_participants p
      WHERE p.thread_id = chat_typing_status.chat_id
        AND p.user_id = auth.uid()
    )
    OR
    EXISTS (
      SELECT 1 FROM group_chat_members m
      WHERE m.group_chat_id = chat_typing_status.chat_id
        AND m.user_id = auth.uid()
    )
  );

DROP POLICY IF EXISTS "users_can_upsert_own_typing" ON chat_typing_status;
CREATE POLICY "users_can_upsert_own_typing" ON chat_typing_status
  FOR INSERT WITH CHECK (user_id = auth.uid());

DROP POLICY IF EXISTS "users_can_update_own_typing" ON chat_typing_status;
CREATE POLICY "users_can_update_own_typing" ON chat_typing_status
  FOR UPDATE USING (user_id = auth.uid());

DROP POLICY IF EXISTS "users_can_delete_own_typing" ON chat_typing_status;
CREATE POLICY "users_can_delete_own_typing" ON chat_typing_status
  FOR DELETE USING (user_id = auth.uid());

-- ============================================================================
-- 6. GRANTS
-- ============================================================================
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chats TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chat_members TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chat_invites TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_messages TO anon, authenticated;
GRANT SELECT ON user_warnings TO anon, authenticated;
GRANT INSERT ON user_warnings TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_threads TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_thread_participants TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_messages TO anon, authenticated;
GRANT SELECT, INSERT, DELETE ON chat_message_reads TO anon, authenticated;
GRANT SELECT, INSERT, DELETE ON group_message_reads TO anon, authenticated;
GRANT SELECT, INSERT ON self_harm_warnings TO anon, authenticated;
GRANT SELECT, INSERT ON report_notifications TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO anon, authenticated;

-- ============================================================================
-- 7. TRIGGERS (0017 warning thresholds + 0018 fixes)
-- ============================================================================
CREATE OR REPLACE FUNCTION check_warning_thresholds()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  warn_count int;
BEGIN
  SELECT count(*) INTO warn_count
  FROM user_warnings
  WHERE user_id = NEW.user_id AND auto_reported = false;

  IF warn_count = 3 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 3 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;

  IF warn_count = 5 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 5 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;

  IF warn_count >= 6 THEN
    UPDATE public.rockscout_profiles SET banned_at = now() WHERE id = NEW.user_id;
  END IF;

  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trigger_warning_thresholds ON user_warnings;
CREATE TRIGGER trigger_warning_thresholds
  AFTER INSERT ON user_warnings
  FOR EACH ROW
  WHEN (NEW.auto_reported = false)
  EXECUTE FUNCTION check_warning_thresholds();

GRANT EXECUTE ON FUNCTION check_warning_thresholds() TO postgres;

-- ─── Member cap trigger (0018) ────────────────────────────────────────────
CREATE OR REPLACE FUNCTION enforce_group_member_cap()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  max_members int;
  current_count int;
BEGIN
  SELECT gc.max_members INTO max_members
  FROM group_chats gc
  WHERE gc.id = NEW.group_chat_id AND gc.deleted_at IS NULL;

  IF max_members IS NOT NULL THEN
    SELECT count(*) INTO current_count
    FROM group_chat_members
    WHERE group_chat_id = NEW.group_chat_id;

    IF current_count >= max_members THEN
      RAISE EXCEPTION 'Group chat is full (max % members)', max_members;
    END IF;
  END IF;

  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trigger_group_member_cap ON group_chat_members;
CREATE TRIGGER trigger_group_member_cap
  BEFORE INSERT ON group_chat_members
  FOR EACH ROW
  EXECUTE FUNCTION enforce_group_member_cap();

GRANT EXECUTE ON FUNCTION enforce_group_member_cap() TO postgres;

-- ============================================================================
-- 8. SCHEMA ADDITIONS: field_journal and trips (0024)
-- ============================================================================
ALTER TABLE public.rockscout_field_journal
  ADD COLUMN IF NOT EXISTS trip_id uuid;

ALTER TABLE public.rockscout_field_journal
  ADD COLUMN IF NOT EXISTS specimen_markers jsonb NOT NULL DEFAULT '[]'::jsonb;

ALTER TABLE public.rockscout_field_journal
  ADD COLUMN IF NOT EXISTS attached_capture_ids jsonb NOT NULL DEFAULT '[]'::jsonb;

ALTER TABLE public.rockscout_trips
  ADD COLUMN IF NOT EXISTS specimen_markers jsonb NOT NULL DEFAULT '[]'::jsonb;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_constraint
    WHERE conname = 'field_journal_trip_id_fkey'
  ) THEN
    ALTER TABLE public.rockscout_field_journal
      ADD CONSTRAINT field_journal_trip_id_fkey
      FOREIGN KEY (trip_id) REFERENCES public.rockscout_trips(id) ON DELETE SET NULL;
  END IF;
EXCEPTION WHEN OTHERS THEN NULL;
END $$;

CREATE INDEX IF NOT EXISTS idx_field_journal_trip
  ON public.rockscout_field_journal (user_id, trip_id)
  WHERE trip_id IS NOT NULL;

GRANT SELECT, INSERT, UPDATE, DELETE ON public.rockscout_field_journal TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.rockscout_trips TO anon, authenticated;

-- ============================================================================
-- 9. updated_at AUTO-TRIGGER (0024)
-- ============================================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trigger_field_journal_updated_at ON public.rockscout_field_journal;
CREATE TRIGGER trigger_field_journal_updated_at
  BEFORE UPDATE ON public.rockscout_field_journal
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS trigger_trips_updated_at ON public.rockscout_trips;
CREATE TRIGGER trigger_trips_updated_at
  BEFORE UPDATE ON public.rockscout_trips
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at_column();

GRANT EXECUTE ON FUNCTION update_updated_at_column() TO postgres;

-- ============================================================================
-- 10. FATAL ERROR EMAIL TRIGGER (0016 — included here in case it was never applied)
-- ============================================================================
CREATE OR REPLACE FUNCTION notify_fatal_error()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  worker_url text;
  app_key text;
BEGIN
  IF NEW.is_fatal = false THEN
    RETURN NEW;
  END IF;

  SELECT value INTO worker_url FROM public.rockscout_config WHERE key = 'worker_url';
  SELECT value INTO app_key FROM public.rockscout_config WHERE key = 'app_key';

  IF worker_url IS NULL OR app_key IS NULL THEN
    RETURN NEW;
  END IF;

  PERFORM net.http_post(
    url := worker_url || '/send-error-email',
    headers := jsonb_build_object(
      'Content-Type', 'application/json',
      'X-App-Key', app_key
    ),
    body := jsonb_build_object(
      'errorType', NEW.error_type,
      'errorMessage', NEW.error_message,
      'stackTrace', NEW.stack_trace,
      'platform', NEW.platform,
      'appVersion', NEW.app_version,
      'osVersion', NEW.os_version,
      'deviceModel', NEW.device_model,
      'userId', NEW.user_id::text,
      'screen', NEW.screen,
      'createdAt', NEW.created_at::text
    )
  );

  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trigger_fatal_error_email ON public.rockscout_error_logs;
CREATE TRIGGER trigger_fatal_error_email
  AFTER INSERT ON public.rockscout_error_logs
  FOR EACH ROW
  WHEN (NEW.is_fatal = true)
  EXECUTE FUNCTION notify_fatal_error();

GRANT EXECUTE ON FUNCTION notify_fatal_error() TO postgres;

-- ============================================================================
-- 11. CRON LOG TABLE (0024)
-- ============================================================================
CREATE TABLE IF NOT EXISTS rockscout_cron_logs (
  id bigserial PRIMARY KEY,
  created_at timestamptz NOT NULL DEFAULT now(),
  job_name text NOT NULL,
  status text NOT NULL,
  details text,
  duration_ms integer,
  rows_affected integer
);

CREATE INDEX IF NOT EXISTS idx_cron_logs_job_created
  ON rockscout_cron_logs (job_name, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_cron_logs_status
  ON rockscout_cron_logs (status, created_at DESC)
  WHERE status = 'failure';

GRANT SELECT ON rockscout_cron_logs TO anon, authenticated;

-- ============================================================================
-- 12. TRIP REMINDER CHECK — daily at 8 AM UTC (0024)
-- ============================================================================
CREATE OR REPLACE FUNCTION check_tomorrow_trips()
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  worker_url text;
  app_key text;
  trip_record RECORD;
  start_time timestamptz;
  success_count int := 0;
  fail_count int := 0;
BEGIN
  start_time := clock_timestamp();

  INSERT INTO rockscout_cron_logs (job_name, status, details)
  VALUES ('trip-reminder-check', 'started',
    json_build_object('target_date', (current_date + 1)::text)::text);

  SELECT value INTO worker_url FROM public.rockscout_config WHERE key = 'worker_url';
  SELECT value INTO app_key FROM public.rockscout_config WHERE key = 'app_key';

  IF worker_url IS NULL OR app_key IS NULL THEN
    INSERT INTO rockscout_cron_logs (job_name, status, details, duration_ms)
    VALUES ('trip-reminder-check', 'failure',
      'Worker URL or app key not configured in rockscout_config',
      (EXTRACT(EPOCH FROM (clock_timestamp() - start_time)) * 1000)::integer);
    RETURN;
  END IF;

  FOR trip_record IN
    SELECT id, user_id, name, trip_date
    FROM public.rockscout_trips
    WHERE trip_date = current_date + 1
      AND is_archived = false
  LOOP
    BEGIN
      PERFORM net.http_post(
        url := worker_url || '/trips/reminder',
        headers := jsonb_build_object(
          'Content-Type', 'application/json',
          'X-App-Key', app_key
        ),
        body := jsonb_build_object(
          'tripId', trip_record.id,
          'userId', trip_record.user_id::text,
          'tripName', trip_record.name,
          'tripDate', trip_record.trip_date::text
        )
      );
      success_count := success_count + 1;
    EXCEPTION WHEN OTHERS THEN
      fail_count := fail_count + 1;
    END;
  END LOOP;

  INSERT INTO rockscout_cron_logs (job_name, status, details, duration_ms, rows_affected)
  VALUES ('trip-reminder-check', 'success',
    json_build_object(
      'target_date', (current_date + 1)::text,
      'notifications_sent', success_count,
      'failures', fail_count
    )::text,
    (EXTRACT(EPOCH FROM (clock_timestamp() - start_time)) * 1000)::integer,
    success_count
  );
END;
$$;

GRANT EXECUTE ON FUNCTION check_tomorrow_trips() TO postgres;

DO $$
BEGIN
  PERFORM cron.unschedule('trip-reminder-check');
EXCEPTION WHEN OTHERS THEN NULL;
END $$;

SELECT cron.schedule(
  'trip-reminder-check',
  '0 8 * * *',
  $$SELECT check_tomorrow_trips()$$
);

-- ============================================================================
-- 13. CLEANUP OLD TEMPORARY DATA — daily at 3 AM UTC (0024)
-- ============================================================================
CREATE OR REPLACE FUNCTION cleanup_old_temp_data()
RETURNS void
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  start_time timestamptz;
  total_deleted int := 0;
  cron_deleted int;
  typing_deleted int;
  error_deleted int;
  invite_deleted int;
  report_deleted int;
BEGIN
  start_time := clock_timestamp();

  INSERT INTO rockscout_cron_logs (job_name, status, details)
  VALUES ('cleanup-old-temp-data', 'started', '{}');

  DELETE FROM rockscout_cron_logs WHERE created_at < now() - interval '30 days';
  GET DIAGNOSTICS cron_deleted = ROW_COUNT;
  total_deleted := total_deleted + cron_deleted;

  DELETE FROM chat_typing_status WHERE updated_at < now() - interval '1 hour';
  GET DIAGNOSTICS typing_deleted = ROW_COUNT;
  total_deleted := total_deleted + typing_deleted;

  DELETE FROM public.rockscout_error_logs WHERE created_at < now() - interval '90 days';
  GET DIAGNOSTICS error_deleted = ROW_COUNT;
  total_deleted := total_deleted + error_deleted;

  DELETE FROM group_chat_invites
  WHERE status = 'declined' AND created_at < now() - interval '30 days';
  GET DIAGNOSTICS invite_deleted = ROW_COUNT;
  total_deleted := total_deleted + invite_deleted;

  DELETE FROM report_notifications WHERE created_at < now() - interval '90 days';
  GET DIAGNOSTICS report_deleted = ROW_COUNT;
  total_deleted := total_deleted + report_deleted;

  INSERT INTO rockscout_cron_logs (job_name, status, details, duration_ms, rows_affected)
  VALUES ('cleanup-old-temp-data', 'success',
    json_build_object(
      'cron_logs_deleted', cron_deleted,
      'typing_status_deleted', typing_deleted,
      'error_logs_deleted', error_deleted,
      'invites_deleted', invite_deleted,
      'reports_deleted', report_deleted,
      'total_deleted', total_deleted
    )::text,
    (EXTRACT(EPOCH FROM (clock_timestamp() - start_time)) * 1000)::integer,
    total_deleted
  );
END;
$$;

GRANT EXECUTE ON FUNCTION cleanup_old_temp_data() TO postgres;

DO $$
BEGIN
  PERFORM cron.unschedule('cleanup-old-temp-data');
EXCEPTION WHEN OTHERS THEN NULL;
END $$;

SELECT cron.schedule(
  'cleanup-old-temp-data',
  '0 3 * * *',
  $$SELECT cleanup_old_temp_data()$$
);

-- ============================================================================
-- DONE — all chat tables, RLS, indexes, read receipts, cron jobs, schema
-- additions, and config table are now created and ready.
-- ============================================================================
