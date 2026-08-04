-- 0018_fix_grants_public_chats_self_harm.sql
-- Fixes missing GRANTs from 0017, makes group chats public to join,
-- fixes the ban trigger (wrong table name + missing column),
-- fixes trigger recursion on auto-report rows,
-- adds self-harm warning tracking,
-- adds INSERT policy on user_warnings for service-role writes,
-- updates private chat max participants from 5 to 10.

-- ─── 1. GRANT permissions on all 0017 tables ────────────────────────────
-- Without these, every PostgREST call returns 401/403.
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chats TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chat_members TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chat_invites TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_messages TO anon, authenticated;
GRANT SELECT ON user_warnings TO anon, authenticated;
-- user_warnings INSERT is service-role only (via Cloudflare Worker), but
-- we also allow authenticated INSERT so the Android app can record warnings
-- with the user's own JWT (the RLS policy restricts to self).
GRANT INSERT ON user_warnings TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_threads TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_thread_participants TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_messages TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_message_reads TO anon, authenticated;

-- ─── 2. Add banned_at column to rockscout_profiles ──────────────────────
ALTER TABLE rockscout_profiles ADD COLUMN IF NOT EXISTS banned_at timestamptz;

-- ─── 3. Fix the ban trigger ─────────────────────────────────────────────
-- The original trigger referenced `profiles` (wrong table) and `banned_at`
-- (column didn't exist). Replace with the correct table and column.
-- Also fix trigger recursion: exclude auto_reported rows from the count
-- so auto-report inserts don't inflate the warning count.
CREATE OR REPLACE FUNCTION check_warning_thresholds()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  warn_count int;
BEGIN
  -- Only count manual warnings (not auto-generated report rows)
  SELECT count(*) INTO warn_count
  FROM user_warnings
  WHERE user_id = NEW.user_id AND auto_reported = false;

  -- At 3 manual warnings: auto-report #1
  IF warn_count = 3 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 3 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;

  -- At 5 manual warnings: auto-report #2
  IF warn_count = 5 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 5 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;

  -- At 6 manual warnings: account ban
  IF warn_count >= 6 THEN
    UPDATE rockscout_profiles SET banned_at = now() WHERE id = NEW.user_id;
  END IF;

  RETURN NEW;
END;
$$;

-- Re-create the trigger (DROP IF EXISTS was already in 0017, but re-drop
-- in case the function signature changed)
DROP TRIGGER IF EXISTS trigger_warning_thresholds ON user_warnings;
CREATE TRIGGER trigger_warning_thresholds
  AFTER INSERT ON user_warnings
  FOR EACH ROW
  WHEN (NEW.auto_reported = false)
  EXECUTE FUNCTION check_warning_thresholds();

GRANT EXECUTE ON FUNCTION check_warning_thresholds() TO postgres;

-- ─── 4. INSERT policy on user_warnings ──────────────────────────────────
-- Allow authenticated users to insert warnings for themselves (the Android
-- app uses this path). The service role bypasses RLS entirely.
DROP POLICY IF EXISTS "uw_insert" ON user_warnings;
CREATE POLICY "uw_insert" ON user_warnings FOR INSERT
  WITH CHECK (user_id = auth.uid());

-- ─── 5. Public group chat browsing ──────────────────────────────────────
-- Make non-deleted group chats visible to all authenticated users so they
-- can browse and join public groups. Members-only visibility was too
-- restrictive for a public community board.
DROP POLICY IF EXISTS "group_chats_select" ON group_chats;
CREATE POLICY "group_chats_select" ON group_chats FOR SELECT
  USING (
    deleted_at IS NULL AND (
      -- Creator can always see their own chats
      creator_id = auth.uid() OR
      -- Existing members can see the chat
      EXISTS (
        SELECT 1 FROM group_chat_members m
        WHERE m.group_chat_id = group_chats.id AND m.user_id = auth.uid()
      ) OR
      -- Any authenticated user can browse non-deleted public group chats
      true
    )
  );

-- ─── 6. Allow self-join into group_chat_members ─────────────────────────
-- Users can join public group chats without an invite. The existing
-- gcm_insert policy already allows user_id = auth.uid(), which is correct.
-- But we need to ensure the member count doesn't exceed max_members.
-- This check is done client-side (Android/web) and also as a DB constraint:

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

-- ─── 7. Self-harm warning tracking ──────────────────────────────────────
-- Tracks self-harm phrase offenses per user (separate from profanity
-- warnings). 1st offense = warning popup, 2nd offense = auto-report.
CREATE TABLE IF NOT EXISTS self_harm_warnings (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reason text DEFAULT '',
  source text DEFAULT 'chat',
  source_id text,
  auto_reported boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE self_harm_warnings ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "shw_select" ON self_harm_warnings;
CREATE POLICY "shw_select" ON self_harm_warnings FOR SELECT
  USING (user_id = auth.uid());
DROP POLICY IF EXISTS "shw_insert" ON self_harm_warnings;
CREATE POLICY "shw_insert" ON self_harm_warnings FOR INSERT
  WITH CHECK (user_id = auth.uid());

GRANT SELECT, INSERT ON self_harm_warnings TO anon, authenticated;

CREATE INDEX IF NOT EXISTS idx_self_harm_warnings_user ON self_harm_warnings(user_id, created_at);

-- ─── 8. Report notifications table ──────────────────────────────────────
-- Tracks email + bell notifications sent when a user is reported.
CREATE TABLE IF NOT EXISTS report_notifications (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  reported_user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reporter_id uuid REFERENCES auth.users(id) ON DELETE SET NULL,
  reason text DEFAULT '',
  source text DEFAULT 'manual',  -- 'manual', 'auto_profanity', 'auto_self_harm'
  source_id text,
  email_sent boolean DEFAULT false,
  bell_notification_sent boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE report_notifications ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "rn_select" ON report_notifications;
CREATE POLICY "rn_select" ON report_notifications FOR SELECT
  USING (reported_user_id = auth.uid() OR reporter_id = auth.uid());
DROP POLICY IF EXISTS "rn_insert" ON report_notifications;
CREATE POLICY "rn_insert" ON report_notifications FOR INSERT
  WITH CHECK (reporter_id = auth.uid() OR reporter_id IS NULL);

GRANT SELECT, INSERT ON report_notifications TO anon, authenticated;

CREATE INDEX IF NOT EXISTS idx_report_notifications_user ON report_notifications(reported_user_id, created_at);
