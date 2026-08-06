-- 0024_rls_indexes_read_receipts_cron_trips.sql
-- Comprehensive hardening pass:
--   1. Additional indexes on group_chat_members and group_messages
--   2. group_message_reads table (per-recipient read receipts for group chats)
--   3. RLS policies on group_message_reads
--   4. Schema additions: trip_id, specimen_markers, attached_capture_ids on field_journal;
--      specimen_markers on trips
--   5. updated_at auto-trigger for field_journal and trips
--   6. rockscout_cron_logs table for server-side scheduled task logging
--   7. pg_cron job: trip-reminder-check (daily, queries trips for tomorrow)
--   8. pg_cron job: cleanup-old-temp-data (daily, cleans up stale data)
--   9. updated_at auto-trigger for chat_typing_status cleanup support

-- ============================================================================
-- 1. ADDITIONAL INDEXES FOR EFFICIENT QUERY PERFORMANCE
-- ============================================================================

-- Composite index for "get all groups a user is in, ordered by join date"
CREATE INDEX IF NOT EXISTS idx_group_chat_members_user_joined
  ON group_chat_members (user_id, joined_at DESC);

-- Cover the member-uniqueness lookup without hitting the heap
CREATE INDEX IF NOT EXISTS idx_group_chat_members_group_user
  ON group_chat_members (group_chat_id, user_id);

-- Index for fetching group messages by sender (for "my messages" queries)
CREATE INDEX IF NOT EXISTS idx_group_messages_sender
  ON group_messages (sender_id, created_at DESC);

-- Index for fetching group messages by chat + sender (for read-status queries)
CREATE INDEX IF NOT EXISTS idx_group_messages_chat_sender
  ON group_messages (group_chat_id, sender_id, created_at DESC);

-- Index for chat_messages by sender (for unread-count queries)
CREATE INDEX IF NOT EXISTS idx_chat_messages_thread_sender
  ON chat_messages (thread_id, sender_id, created_at DESC);

-- Index for chat_message_reads by user (for "what have I read" queries)
CREATE INDEX IF NOT EXISTS idx_chat_message_reads_user
  ON chat_message_reads (user_id, read_at DESC);

-- Index for chat_thread_participants by thread (covers participant-list queries)
CREATE INDEX IF NOT EXISTS idx_chat_thread_participants_thread
  ON chat_thread_participants (thread_id, user_id);

-- Index for group_chat_invites by group (for "who was invited to this group" queries)
CREATE INDEX IF NOT EXISTS idx_group_chat_invites_group
  ON group_chat_invites (group_chat_id, status);

-- ============================================================================
-- 2. GROUP MESSAGE READ RECEIPTS
--    Mirrors chat_message_reads but for group_messages, enabling per-recipient
--    read tracking in group chats (same pattern as private chat read receipts).
-- ============================================================================

CREATE TABLE IF NOT EXISTS group_message_reads (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  message_id text NOT NULL REFERENCES group_messages(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  read_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (message_id, user_id)
);

-- Index for "who has read this message" queries
CREATE INDEX IF NOT EXISTS idx_group_message_reads_message
  ON group_message_reads (message_id);

-- Index for "what messages have I read in this group" queries (join via group_messages)
CREATE INDEX IF NOT EXISTS idx_group_message_reads_user
  ON group_message_reads (user_id, read_at DESC);

-- ─── RLS Policies for group_message_reads ────────────────────────────────
ALTER TABLE group_message_reads ENABLE ROW LEVEL SECURITY;

-- SELECT: a user can see read receipts for messages in groups they are a member of
DROP POLICY IF EXISTS "gmr_select" ON group_message_reads;
CREATE POLICY "gmr_select" ON group_message_reads FOR SELECT
  USING (
    user_id = auth.uid() OR EXISTS (
      SELECT 1 FROM group_messages gm
      JOIN group_chat_members m ON m.group_chat_id = gm.group_chat_id
      WHERE gm.id = group_message_reads.message_id AND m.user_id = auth.uid()
    )
  );

-- INSERT: a user can only insert their own read receipts (for messages in their groups)
DROP POLICY IF EXISTS "gmr_insert" ON group_message_reads;
CREATE POLICY "gmr_insert" ON group_message_reads FOR INSERT
  WITH CHECK (
    user_id = auth.uid() AND EXISTS (
      SELECT 1 FROM group_messages gm
      JOIN group_chat_members m ON m.group_chat_id = gm.group_chat_id
      WHERE gm.id = group_message_reads.message_id AND m.user_id = auth.uid()
    )
  );

-- DELETE: a user can only delete their own read receipts
DROP POLICY IF EXISTS "gmr_delete" ON group_message_reads;
CREATE POLICY "gmr_delete" ON group_message_reads FOR DELETE
  USING (user_id = auth.uid());

GRANT SELECT, INSERT, DELETE ON group_message_reads TO anon, authenticated;

-- ============================================================================
-- 3. RLS POLICY HARDENING
--    Ensure users can only access messages and chat groups they are authorized
--    to view. The existing policies from 0017/0018 are already correct, but we
--    add explicit UPDATE and DELETE policies on group_messages and chat_messages
--    that were missing, and tighten chat_message_reads with a DELETE policy.
-- ============================================================================

-- group_messages: only the sender can update/delete their own messages
DROP POLICY IF EXISTS "gm_update" ON group_messages;
CREATE POLICY "gm_update" ON group_messages FOR UPDATE
  USING (sender_id = auth.uid());

DROP POLICY IF EXISTS "gm_delete" ON group_messages;
CREATE POLICY "gm_delete" ON group_messages FOR DELETE
  USING (sender_id = auth.uid());

GRANT UPDATE, DELETE ON group_messages TO anon, authenticated;

-- chat_messages: only the sender can update/delete their own messages
DROP POLICY IF EXISTS "cm_update" ON chat_messages;
CREATE POLICY "cm_update" ON chat_messages FOR UPDATE
  USING (sender_id = auth.uid());

DROP POLICY IF EXISTS "cm_delete" ON chat_messages;
CREATE POLICY "cm_delete" ON chat_messages FOR DELETE
  USING (sender_id = auth.uid());

GRANT UPDATE, DELETE ON chat_messages TO anon, authenticated;

-- chat_message_reads: add DELETE policy (user can retract their own read receipt)
DROP POLICY IF EXISTS "cmr_delete" ON chat_message_reads;
CREATE POLICY "cmr_delete" ON chat_message_reads FOR DELETE
  USING (user_id = auth.uid());

GRANT DELETE ON chat_message_reads TO anon, authenticated;

-- chat_thread_participants: add DELETE policy (user can leave a thread)
DROP POLICY IF EXISTS "ctp_delete" ON chat_thread_participants;
CREATE POLICY "ctp_delete" ON chat_thread_participants FOR DELETE
  USING (user_id = auth.uid());

GRANT DELETE ON chat_thread_participants TO anon, authenticated;

-- ============================================================================
-- 4. SCHEMA ADDITIONS: field_journal and trips
--    Add columns that the Android/iOS data models already track but the DB
--    schema was missing: trip_id, specimen_markers, attached_capture_ids.
-- ============================================================================

-- field_journal: link to a trip, specimen markers, attached capture IDs
ALTER TABLE public.rockscout_field_journal
  ADD COLUMN IF NOT EXISTS trip_id uuid;  -- optional link to rockscout_trips(id)

ALTER TABLE public.rockscout_field_journal
  ADD COLUMN IF NOT EXISTS specimen_markers jsonb NOT NULL DEFAULT '[]'::jsonb;

ALTER TABLE public.rockscout_field_journal
  ADD COLUMN IF NOT EXISTS attached_capture_ids jsonb NOT NULL DEFAULT '[]'::jsonb;

-- Add FK from field_journal.trip_id to trips(id) (self-referential within user)
-- We use a deferred approach: the FK is optional and only enforced when trip_id is set.
DO $$
BEGIN
  -- Add the FK if it doesn't already exist
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

-- Index for querying journal entries by trip
CREATE INDEX IF NOT EXISTS idx_field_journal_trip
  ON public.rockscout_field_journal (user_id, trip_id)
  WHERE trip_id IS NOT NULL;

-- trips: specimen markers (pins on the trip's specimen marker map)
ALTER TABLE public.rockscout_trips
  ADD COLUMN IF NOT EXISTS specimen_markers jsonb NOT NULL DEFAULT '[]'::jsonb;

-- Grant on new columns (RLS already covers the table-level access)
GRANT SELECT, INSERT, UPDATE, DELETE ON public.rockscout_field_journal TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON public.rockscout_trips TO anon, authenticated;

-- ============================================================================
-- 5. updated_at AUTO-TRIGGER for field_journal and trips
--    Automatically update updated_at on row modification.
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
-- 6. CRON LOG TABLE — tracks execution of all server-side scheduled tasks
-- ============================================================================

CREATE TABLE IF NOT EXISTS rockscout_cron_logs (
  id bigserial PRIMARY KEY,
  created_at timestamptz NOT NULL DEFAULT now(),
  job_name text NOT NULL,              -- e.g. 'trip-reminder-check', 'cleanup-old-temp-data'
  status text NOT NULL,                -- 'started', 'success', 'failure'
  details text,                        -- JSON string with execution details
  duration_ms integer,                 -- execution time in milliseconds
  rows_affected integer                -- number of rows processed (if applicable)
);

-- Index for querying recent cron log entries
CREATE INDEX IF NOT EXISTS idx_cron_logs_job_created
  ON rockscout_cron_logs (job_name, created_at DESC);

-- Index for querying failures
CREATE INDEX IF NOT EXISTS idx_cron_logs_status
  ON rockscout_cron_logs (status, created_at DESC)
  WHERE status = 'failure';

-- RLS disabled — cron logs are managed by the postgres role (pg_cron runs as postgres)
-- Anyone with the anon key can read for diagnostics dashboards.
GRANT SELECT ON rockscout_cron_logs TO anon, authenticated;

-- ============================================================================
-- 7. TRIP REMINDER CHECK — pg_cron job that runs daily at 8 AM UTC
--    Queries rockscout_trips for trips scheduled for tomorrow (not archived),
--    and for each trip, sends an HTTP POST to the Cloudflare Worker
--    /trips/reminder endpoint, which dispatches a push notification to the user.
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

  -- Log the start of this cron run
  INSERT INTO rockscout_cron_logs (job_name, status, details)
  VALUES ('trip-reminder-check', 'started',
    json_build_object('target_date', (current_date + 1)::text)::text);

  -- Read config from the rockscout_config table
  SELECT value INTO worker_url FROM rockscout_config WHERE key = 'worker_url';
  SELECT value INTO app_key FROM rockscout_config WHERE key = 'app_key';

  IF worker_url IS NULL OR app_key IS NULL THEN
    INSERT INTO rockscout_cron_logs (job_name, status, details, duration_ms)
    VALUES ('trip-reminder-check', 'failure',
      'Worker URL or app key not configured in rockscout_config',
      EXTRACT(EPOCH FROM (clock_timestamp() - start_time)) * 1000)::integer);
    RETURN;
  END IF;

  -- Query all non-archived trips scheduled for tomorrow
  FOR trip_record IN
    SELECT id, user_id, name, trip_date
    FROM rockscout_trips
    WHERE trip_date = current_date + 1
      AND is_archived = false
  LOOP
    BEGIN
      -- Fire-and-forget HTTP POST to the Worker endpoint
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

  -- Log the completion
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

-- Schedule the trip reminder check: daily at 8:00 AM UTC
-- (8 AM UTC = 3 AM EST / 12 AM PST — early morning for most US users)
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
-- 8. CLEANUP OLD TEMPORARY DATA — pg_cron job that runs daily at 3 AM UTC
--    Cleans up:
--    - rockscout_cron_logs older than 30 days
--    - chat_typing_status entries older than 1 hour (ephemeral)
--    - rockscout_error_logs older than 90 days (redundant with 0013 but safe)
--    - group_chat_invites with status 'declined' older than 30 days
--    - report_notifications older than 90 days
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

  -- Log the start
  INSERT INTO rockscout_cron_logs (job_name, status, details)
  VALUES ('cleanup-old-temp-data', 'started', '{}');

  -- Clean up old cron logs (30 days)
  DELETE FROM rockscout_cron_logs WHERE created_at < now() - interval '30 days';
  GET DIAGNOSTICS cron_deleted = ROW_COUNT;
  total_deleted := total_deleted + cron_deleted;

  -- Clean up stale typing status entries (1 hour — these are ephemeral)
  DELETE FROM chat_typing_status WHERE updated_at < now() - interval '1 hour';
  GET DIAGNOSTICS typing_deleted = ROW_COUNT;
  total_deleted := total_deleted + typing_deleted;

  -- Clean up old error logs (90 days — same as 0013's schedule)
  DELETE FROM rockscout_error_logs WHERE created_at < now() - interval '90 days';
  GET DIAGNOSTICS error_deleted = ROW_COUNT;
  total_deleted := total_deleted + error_deleted;

  -- Clean up declined group chat invites older than 30 days
  DELETE FROM group_chat_invites
  WHERE status = 'declined' AND created_at < now() - interval '30 days';
  GET DIAGNOSTICS invite_deleted = ROW_COUNT;
  total_deleted := total_deleted + invite_deleted;

  -- Clean up old report notifications (90 days)
  DELETE FROM report_notifications WHERE created_at < now() - interval '90 days';
  GET DIAGNOSTICS report_deleted = ROW_COUNT;
  total_deleted := total_deleted + report_deleted;

  -- Log the completion
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

-- Schedule the cleanup: daily at 3:00 AM UTC (before the trip reminder at 8 AM)
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
-- DONE
-- ============================================================================
