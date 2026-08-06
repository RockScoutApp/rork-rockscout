-- 0015_user_photos_storage_and_sync.sql
--
-- Creates a private Supabase Storage bucket for user-uploaded photos
-- (field captures, journal photos, trip photos, saved images) so they
-- can be synced cross-device when connectivity is restored.
--
-- RLS on the bucket ensures users can only read/write their own folder:
--   user-photos/{user_id}/{filename}
--
-- The service-role key (used by Cloudflare Workers and the Developer
-- Console for review) bypasses RLS entirely.

-- ============================================================================
-- 1. STORAGE BUCKET
-- ============================================================================
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
select
    'user-photos',
    'user-photos',
    true,   -- public read (URLs include user_id + UUID, effectively unguessable)
            -- write still controlled via RLS policies below
    10_485_760,  -- 10 MB per file
    array['image/jpeg', 'image/png', 'image/webp', 'image/heic', 'image/heif']
where not exists (
    select 1 from storage.buckets where id = 'user-photos'
);

-- ============================================================================
-- 2. STORAGE RLS POLICIES
-- ============================================================================
-- Users can upload to their own folder: user-photos/{auth.uid()}/...
-- Public reads are allowed (bucket is public) but writes are RLS-controlled.
alter table storage.objects enable row level security;

-- Allow anyone to SELECT (read) — bucket is public, filenames include UUIDs
drop policy if exists "user-photos-read-all" on storage.objects;
create policy "user-photos-read-all" on storage.objects
    for select using (bucket_id = 'user-photos');

-- Allow users to INSERT (upload) into their own folder only
drop policy if exists "user-photos-write-self" on storage.objects;
create policy "user-photos-write-self" on storage.objects
    for insert with check (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );

-- Allow users to UPDATE (overwrite) their own files
drop policy if exists "user-photos-update-self" on storage.objects;
create policy "user-photos-update-self" on storage.objects
    for update using (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    ) with check (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );

-- Allow users to DELETE their own files
drop policy if exists "user-photos-delete-self" on storage.objects;
create policy "user-photos-delete-self" on storage.objects
    for delete using (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );

-- ============================================================================
-- 3. ADD updated_at COLUMNS FOR SYNC CONFLICT DETECTION
-- ============================================================================
-- These columns let the sync logic compare local vs remote timestamps
-- to implement last-write-wins conflict resolution.

alter table public.rockscout_captures
    add column if not exists updated_at timestamptz not null default now();

alter table public.rockscout_saved_images
    add column if not exists updated_at timestamptz not null default now();

-- rockscout_field_journal already has updated_at from migration 0005
-- rockscout_trips already has updated_at from migration 0005

-- ============================================================================
-- 4. ADD sync_status COLUMN FOR TRACKING DIRTY RECORDS
-- ============================================================================
-- Tracks whether a row has been synced to the client. Values:
--   'synced'  — the row is current on both client and server
--   'dirty'   — the row was modified locally and needs to be pushed
--   'pending' — the row is queued for upload (photo upload in progress)

alter table public.rockscout_captures
    add column if not exists sync_status text not null default 'synced';

alter table public.rockscout_saved_images
    add column if not exists sync_status text not null default 'synced';

alter table public.rockscout_field_journal
    add column if not exists sync_status text not null default 'synced';

alter table public.rockscout_trips
    add column if not exists sync_status text not null default 'synced';

-- ============================================================================
-- 5. INDEXES FOR SYNC QUERIES
-- ============================================================================
create index if not exists idx_captures_user_sync
    on public.rockscout_captures (user_id, sync_status)
    where sync_status != 'synced';

create index if not exists idx_saved_images_user_sync
    on public.rockscout_saved_images (user_id, sync_status)
    where sync_status != 'synced';

create index if not exists idx_field_journal_user_sync
    on public.rockscout_field_journal (user_id, sync_status)
    where sync_status != 'synced';

create index if not exists idx_trips_user_sync
    on public.rockscout_trips (user_id, sync_status)
    where sync_status != 'synced';

-- ============================================================================
-- DONE
-- ============================================================================
-- 0016_fatal_error_email_trigger.sql
-- PostgreSQL trigger on rockscout_error_logs that calls the /send-error-email
-- Cloudflare Worker endpoint whenever a new row with is_fatal = true is inserted.
-- The Worker sends an email alert to aaron_james_martin@yahoo.com via Resend.

-- Enable pg_http if available (Supabase ships pg_net for outbound HTTP).
-- We use pg_net's net.http_post function to call the Edge Function.
CREATE EXTENSION IF NOT EXISTS pg_net;

-- Grant access to the net schema for the trigger function's owner.
GRANT USAGE ON SCHEMA net TO postgres;

-- Function that fires AFTER INSERT on rockscout_error_logs when is_fatal = true.
-- Sends an HTTP POST to the Cloudflare Worker /send-error-email endpoint with
-- the error details so an email alert is dispatched via Resend.
CREATE OR REPLACE FUNCTION notify_fatal_error()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  worker_url text;
  app_key text;
BEGIN
  -- Only fire for fatal errors
  IF NEW.is_fatal = false THEN
    RETURN NEW;
  END IF;

  -- Read config from the rockscout_config table (set once via SQL Editor).
  SELECT value INTO worker_url FROM rockscout_config WHERE key = 'worker_url';
  SELECT value INTO app_key FROM rockscout_config WHERE key = 'app_key';

  IF worker_url IS NULL OR app_key IS NULL THEN
    -- Configuration not set yet — skip silently (error is still stored in the table)
    RETURN NEW;
  END IF;

  -- Fire-and-forget HTTP POST to the Worker endpoint.
  -- pg_net's http_post is async — the trigger doesn't block on the response.
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

-- Drop existing trigger if it exists (idempotent re-run safe)
DROP TRIGGER IF EXISTS trigger_fatal_error_email ON rockscout_error_logs;

-- Create the trigger — fires AFTER INSERT, only for fatal errors.
-- Using a WHEN clause so the trigger only fires for is_fatal = true rows,
-- avoiding overhead on non-fatal error inserts.
CREATE TRIGGER trigger_fatal_error_email
  AFTER INSERT ON rockscout_error_logs
  FOR EACH ROW
  WHEN (NEW.is_fatal = true)
  EXECUTE FUNCTION notify_fatal_error();

-- Grant execute on the trigger function to the postgres role.
GRANT EXECUTE ON FUNCTION notify_fatal_error() TO postgres;
-- 0017_group_chats_and_warnings.sql
-- Group chat tables, user warnings table, and RLS policies.
-- Supports the community board group chat feature and the profanity
-- warning system (3 warnings = auto-report, 5 = 2nd report, 6 = ban).

-- ─── Group Chats ───────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS group_chats (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  name text NOT NULL,
  subject text DEFAULT '',
  creator_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  max_members int,  -- NULL = no cap
  profanity_filter_level text NOT NULL DEFAULT 'normal' CHECK (profanity_filter_level IN ('normal','strict')),
  header_image_url text,
  scroll_speed_setting text NOT NULL DEFAULT 'normal' CHECK (scroll_speed_setting IN ('normal','half','stop')),
  created_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz  -- NULL = active; set when creator deletes
);

-- ─── Group Chat Members ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS group_chat_members (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  joined_at timestamptz NOT NULL DEFAULT now(),
  role text NOT NULL DEFAULT 'member' CHECK (role IN ('creator','member')),
  UNIQUE (group_chat_id, user_id)
);

-- ─── Group Chat Invites ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS group_chat_invites (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  inviter_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  invitee_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  status text NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','accepted','declined')),
  created_at timestamptz NOT NULL DEFAULT now(),
  responded_at timestamptz
);

-- ─── Group Messages ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS group_messages (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  sender_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  body text DEFAULT '',
  image_url text,
  reply_to_message_id text,  -- self-reference for reply threading
  tagged_user_ids uuid[] DEFAULT '{}',  -- users tagged in the message
  read_at timestamptz,  -- first time any non-sender member read it
  created_at timestamptz NOT NULL DEFAULT now()
);

-- ─── User Warnings ─────────────────────────────────────────────────────
-- Tracks profanity warnings per user. The warning counter logic:
--   3 warnings → auto-report #1
--   5 warnings → auto-report #2
--   6 warnings → account ban
CREATE TABLE IF NOT EXISTS user_warnings (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reason text DEFAULT '',
  source text DEFAULT 'chat',  -- 'chat' or 'group_chat'
  source_id text,  -- thread or group chat id
  auto_reported boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);

-- ─── Private chat thread participants ──────────────────────────────────
-- Extends the existing messages table to support multi-user private chats.
-- A thread can have up to 5 participants (private chat max).
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

-- ─── Private chat messages (separate from group_messages) ──────────────
CREATE TABLE IF NOT EXISTS chat_messages (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  thread_id text NOT NULL REFERENCES chat_threads(id) ON DELETE CASCADE,
  sender_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  body text DEFAULT '',
  image_url text,
  reply_to_message_id text,
  tagged_user_ids uuid[] DEFAULT '{}',
  read_at timestamptz,  -- per-recipient read tracking
  created_at timestamptz NOT NULL DEFAULT now()
);

-- ─── Per-recipient read receipts for private chats ─────────────────────
CREATE TABLE IF NOT EXISTS chat_message_reads (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  message_id text NOT NULL REFERENCES chat_messages(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  read_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (message_id, user_id)
);

-- ─── Indexes ───────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_group_chat_members_user ON group_chat_members(user_id);
CREATE INDEX IF NOT EXISTS idx_group_chat_members_group ON group_chat_members(group_chat_id);
CREATE INDEX IF NOT EXISTS idx_group_messages_chat ON group_messages(group_chat_id, created_at);
CREATE INDEX IF NOT EXISTS idx_group_chat_invites_invitee ON group_chat_invites(invitee_id, status);
CREATE INDEX IF NOT EXISTS idx_user_warnings_user ON user_warnings(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_chat_thread_participants_user ON chat_thread_participants(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_thread ON chat_messages(thread_id, created_at);
CREATE INDEX IF NOT EXISTS idx_chat_message_reads_message ON chat_message_reads(message_id);

-- ─── RLS Policies ──────────────────────────────────────────────────────

-- Group chats: visible to members only
ALTER TABLE group_chats ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "group_chats_select" ON group_chats;
CREATE POLICY "group_chats_select" ON group_chats FOR SELECT
  USING (
    deleted_at IS NULL AND EXISTS (
      SELECT 1 FROM group_chat_members m
      WHERE m.group_chat_id = group_chats.id AND m.user_id = auth.uid()
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

-- Group chat members: members can see who's in their chats; anyone can join via invite
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

-- Group chat invites: invitee can see their invites; inviter can see sent invites
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

-- Group messages: only members can read/write
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

-- User warnings: users can see their own warnings; only service role can insert
ALTER TABLE user_warnings ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "uw_select" ON user_warnings;
CREATE POLICY "uw_select" ON user_warnings FOR SELECT
  USING (user_id = auth.uid());

-- Chat threads: participants can see their threads
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

-- Chat thread participants: members can see who's in their threads
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

-- Chat messages: only thread participants can read/write
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

-- Chat message reads: users can mark their own reads
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

-- ─── Auto-report trigger on user_warnings ──────────────────────────────
-- When a user's warning count hits 3, 5, or 6, auto-generate a report.
CREATE OR REPLACE FUNCTION check_warning_thresholds()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  warn_count int;
BEGIN
  SELECT count(*) INTO warn_count FROM user_warnings WHERE user_id = NEW.user_id;

  -- At 3 warnings: auto-report #1
  IF warn_count = 3 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 3 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;

  -- At 5 warnings: auto-report #2
  IF warn_count = 5 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 5 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;

  -- At 6 warnings: account ban — set banned_at on profile
  IF warn_count >= 6 THEN
    UPDATE profiles SET banned_at = now() WHERE id = NEW.user_id;
  END IF;

  RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trigger_warning_thresholds ON user_warnings;
CREATE TRIGGER trigger_warning_thresholds
  AFTER INSERT ON user_warnings
  FOR EACH ROW
  EXECUTE FUNCTION check_warning_thresholds();

GRANT EXECUTE ON FUNCTION check_warning_thresholds() TO postgres;
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
-- Chat typing status table for real-time typing indicators
-- Supports both private threads (chat_id = thread ID) and group chats (chat_id = group chat ID)
CREATE TABLE IF NOT EXISTS chat_typing_status (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  chat_id TEXT NOT NULL,
  user_id TEXT NOT NULL REFERENCES rockscout_profiles(id) ON DELETE CASCADE,
  is_typing BOOLEAN DEFAULT false,
  updated_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE(chat_id, user_id)
);

-- RLS: users can only see typing status for chats they're a member of
ALTER TABLE chat_typing_status ENABLE ROW LEVEL SECURITY;

-- SELECT: anyone who is a participant in the chat can see typing statuses
-- For private threads: check chat_thread_participants
-- For group chats: check group_chat_members
CREATE POLICY "users_can_read_typing_status" ON chat_typing_status
  FOR SELECT USING (
    -- Private thread: user is a participant
    EXISTS (
      SELECT 1 FROM chat_thread_participants p
      WHERE p.thread_id = chat_typing_status.chat_id
        AND p.user_id = auth.uid()
    )
    OR
    -- Group chat: user is a member
    EXISTS (
      SELECT 1 FROM group_chat_members m
      WHERE m.group_chat_id = chat_typing_status.chat_id
        AND m.user_id = auth.uid()
    )
  );

-- INSERT/UPDATE: users can only upsert their own typing status
CREATE POLICY "users_can_upsert_own_typing" ON chat_typing_status
  FOR INSERT WITH CHECK (user_id = auth.uid());

CREATE POLICY "users_can_update_own_typing" ON chat_typing_status
  FOR UPDATE USING (user_id = auth.uid());

CREATE POLICY "users_can_delete_own_typing" ON chat_typing_status
  FOR DELETE USING (user_id = auth.uid());

-- Index for fast lookups
CREATE INDEX IF NOT EXISTS idx_typing_status_chat_id ON chat_typing_status(chat_id);
CREATE INDEX IF NOT EXISTS idx_typing_status_updated_at ON chat_typing_status(updated_at DESC);

-- Grant access
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO anon;
-- 0020_profile_highlight_color.sql
-- Adds a custom highlight color column to rockscout_profiles so users can
-- personalize their profile page with a color others can see.

ALTER TABLE rockscout_profiles
  ADD COLUMN IF NOT EXISTS highlight_color TEXT DEFAULT NULL;

-- Allow users to read everyone's highlight_color (it's public display data)
ALTER TABLE rockscout_profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Profiles are readable by all" ON rockscout_profiles;
CREATE POLICY "Profiles are readable by all"
  ON rockscout_profiles FOR SELECT
  USING (true);

-- Allow users to update their own highlight_color
DROP POLICY IF EXISTS "Users can update own profile" ON rockscout_profiles;
CREATE POLICY "Users can update own profile"
  ON rockscout_profiles FOR UPDATE
  USING (auth.uid() = id);
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
-- Add device_platform column to rockscout_installed_devices so the
-- Manage Devices screen can show whether each device is Android, iOS, or web.
-- The 3-device limit is derived client-side from installed_at ordering —
-- no is_over_limit column, no triggers, no Postgres functions needed.

alter table public.rockscout_installed_devices
    add column if not exists device_platform text not null default 'unknown';

-- Backfill existing rows to 'web' (all existing entries are from the PWA).
update public.rockscout_installed_devices
    set device_platform = 'web'
    where device_platform = 'unknown';

-- RLS already allows authenticated users to select/insert/update/delete their
-- own rows (auth.uid() = user_id). The new column is covered by the existing
-- update policy, so no additional grant is needed.
