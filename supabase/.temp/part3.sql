alter table public.rockscout_connections       enable row level security;
alter table public.rockscout_message_requests  enable row level security;
alter table public.rockscout_friend_requests   enable row level security;
alter table public.rockscout_blocks            enable row level security;
alter table public.rockscout_threads           enable row level security;
alter table public.rockscout_messages          enable row level security;
alter table public.rockscout_pings             enable row level security;
alter table public.rockscout_posts             enable row level security;
alter table public.rockscout_post_likes        enable row level security;
alter table public.rockscout_post_comments     enable row level security;
alter table public.rockscout_notifications     enable row level security;
alter table public.rockscout_trade_interests   enable row level security;
drop policy if exists connections_select on public.rockscout_connections;
create policy connections_select on public.rockscout_connections
    for select using (user_a = auth.uid() or user_b = auth.uid());
drop policy if exists connections_insert on public.rockscout_connections;
create policy connections_insert on public.rockscout_connections
    for insert with check (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
drop policy if exists connections_delete on public.rockscout_connections;
create policy connections_delete on public.rockscout_connections
    for delete using (user_a = auth.uid() or user_b = auth.uid());
grant select, insert, delete on public.rockscout_connections to anon, authenticated;
drop policy if exists msg_requests_select on public.rockscout_message_requests;
create policy msg_requests_select on public.rockscout_message_requests
    for select using (sender_id = auth.uid() or recipient_id = auth.uid());
drop policy if exists msg_requests_insert on public.rockscout_message_requests;
create policy msg_requests_insert on public.rockscout_message_requests
    for insert with check (
        sender_id = auth.uid()
        and not public.rockscout_is_blocked(sender_id, recipient_id)
    );
drop policy if exists msg_requests_update on public.rockscout_message_requests;
create policy msg_requests_update on public.rockscout_message_requests
    for update using (recipient_id = auth.uid()) with check (recipient_id = auth.uid());
grant select, insert, update on public.rockscout_message_requests to anon, authenticated;
drop policy if exists friend_requests_select on public.rockscout_friend_requests;
create policy friend_requests_select on public.rockscout_friend_requests
    for select using (sender_id = auth.uid() or recipient_id = auth.uid());
drop policy if exists friend_requests_insert on public.rockscout_friend_requests;
create policy friend_requests_insert on public.rockscout_friend_requests
    for insert with check (
        sender_id = auth.uid()
        and not public.rockscout_is_blocked(sender_id, recipient_id)
    );
drop policy if exists friend_requests_update on public.rockscout_friend_requests;
create policy friend_requests_update on public.rockscout_friend_requests
    for update using (recipient_id = auth.uid()) with check (recipient_id = auth.uid());
grant select, insert, update on public.rockscout_friend_requests to anon, authenticated;
drop policy if exists blocks_select on public.rockscout_blocks;
create policy blocks_select on public.rockscout_blocks
    for select using (blocker_id = auth.uid());
drop policy if exists blocks_insert on public.rockscout_blocks;
create policy blocks_insert on public.rockscout_blocks
    for insert with check (blocker_id = auth.uid());
drop policy if exists blocks_delete on public.rockscout_blocks;
create policy blocks_delete on public.rockscout_blocks
    for delete using (blocker_id = auth.uid());
grant select, insert, delete on public.rockscout_blocks to anon, authenticated;
drop policy if exists threads_select on public.rockscout_threads;
create policy threads_select on public.rockscout_threads
    for select using (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
drop policy if exists threads_insert on public.rockscout_threads;
create policy threads_insert on public.rockscout_threads
    for insert with check (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
drop policy if exists threads_update on public.rockscout_threads;
create policy threads_update on public.rockscout_threads
    for update using (user_a = auth.uid() or user_b = auth.uid());
grant select, insert, update on public.rockscout_threads to anon, authenticated;
drop policy if exists messages_select on public.rockscout_messages;
create policy messages_select on public.rockscout_messages
    for select using (
        exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
              and not public.rockscout_is_blocked(t.user_a, t.user_b)
        )
    );
drop policy if exists messages_insert on public.rockscout_messages;
create policy messages_insert on public.rockscout_messages
    for insert with check (
        sender_id = auth.uid()
        and exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
              and not public.rockscout_is_blocked(t.user_a, t.user_b)
        )
    );
drop policy if exists messages_update on public.rockscout_messages;
create policy messages_update on public.rockscout_messages
    for update using (
        exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
        )
    );
grant select, insert, update on public.rockscout_messages to anon, authenticated;
drop policy if exists pings_select on public.rockscout_pings;
create policy pings_select on public.rockscout_pings
    for select using (
        user_id = auth.uid()
        or (
            public.rockscout_are_connected(auth.uid(), user_id)
            and not public.rockscout_is_blocked(auth.uid(), user_id)
        )
    );
drop policy if exists pings_insert on public.rockscout_pings;
create policy pings_insert on public.rockscout_pings
    for insert with check (user_id = auth.uid());
drop policy if exists pings_delete on public.rockscout_pings;
create policy pings_delete on public.rockscout_pings
    for delete using (user_id = auth.uid());
grant select, insert, delete on public.rockscout_pings to anon, authenticated;
drop policy if exists posts_select on public.rockscout_posts;
create policy posts_select on public.rockscout_posts
    for select using (
        user_id = auth.uid()
        or not public.rockscout_is_blocked(auth.uid(), user_id)
    );
drop policy if exists posts_insert on public.rockscout_posts;
create policy posts_insert on public.rockscout_posts
    for insert with check (user_id = auth.uid());
drop policy if exists posts_delete on public.rockscout_posts;
create policy posts_delete on public.rockscout_posts
    for delete using (user_id = auth.uid());
grant select, insert, delete on public.rockscout_posts to anon, authenticated;
drop policy if exists likes_select on public.rockscout_post_likes;
create policy likes_select on public.rockscout_post_likes
    for select using (
        exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and (p.user_id = auth.uid() or not public.rockscout_is_blocked(auth.uid(), p.user_id))
        )
    );
drop policy if exists likes_insert on public.rockscout_post_likes;
create policy likes_insert on public.rockscout_post_likes
    for insert with check (
        user_id = auth.uid()
        and exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and not public.rockscout_is_blocked(auth.uid(), p.user_id)
        )
    );
drop policy if exists likes_delete on public.rockscout_post_likes;
create policy likes_delete on public.rockscout_post_likes
    for delete using (user_id = auth.uid());
grant select, insert, delete on public.rockscout_post_likes to anon, authenticated;
drop policy if exists comments_select on public.rockscout_post_comments;
create policy comments_select on public.rockscout_post_comments
    for select using (
        exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and (p.user_id = auth.uid() or not public.rockscout_is_blocked(auth.uid(), p.user_id))
        )
    );
drop policy if exists comments_insert on public.rockscout_post_comments;
create policy comments_insert on public.rockscout_post_comments
    for insert with check (
        user_id = auth.uid()
        and exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and public.rockscout_are_connected(auth.uid(), p.user_id)
              and not public.rockscout_is_blocked(auth.uid(), p.user_id)
        )
    );
drop policy if exists comments_delete on public.rockscout_post_comments;
create policy comments_delete on public.rockscout_post_comments
    for delete using (user_id = auth.uid());
grant select, insert, delete on public.rockscout_post_comments to anon, authenticated;
drop policy if exists notifs_select on public.rockscout_notifications;
create policy notifs_select on public.rockscout_notifications
    for select using (user_id = auth.uid());
drop policy if exists notifs_insert on public.rockscout_notifications;
create policy notifs_insert on public.rockscout_notifications
    for insert with check (
        actor_id = auth.uid()
        and user_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), user_id)
    );
drop policy if exists notifs_update on public.rockscout_notifications;
create policy notifs_update on public.rockscout_notifications
    for update using (user_id = auth.uid());
grant select, insert, update on public.rockscout_notifications to anon, authenticated;
drop policy if exists trade_interests_select on public.rockscout_trade_interests;
create policy trade_interests_select on public.rockscout_trade_interests
    for select using (
        interested_user_id = auth.uid()
        or listing_owner_id = auth.uid()
    );
drop policy if exists trade_interests_insert on public.rockscout_trade_interests;
create policy trade_interests_insert on public.rockscout_trade_interests
    for insert with check (
        interested_user_id = auth.uid()
        and listing_owner_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), listing_owner_id)
    );
drop policy if exists trade_interests_delete on public.rockscout_trade_interests;
create policy trade_interests_delete on public.rockscout_trade_interests
    for delete using (listing_owner_id = auth.uid());
grant select, insert, delete on public.rockscout_trade_interests to anon, authenticated;
alter table public.rockscout_collection
    add column if not exists note text not null default '';
alter table public.rockscout_collection
    add column if not exists found_at text not null default '';
alter table public.rockscout_collection
    add column if not exists added_at bigint not null default 0;
do $$
declare
    constraint_name text;
begin
    select c.conname into constraint_name
    from pg_constraint c
    join pg_class t on c.conrelid = t.oid
    join pg_namespace n on t.relnamespace = n.oid
    where n.nspname = 'public' and t.relname = 'rockscout_collection'
      and c.contype = 'f';
    if constraint_name is not null then
        execute format('alter table public.rockscout_collection drop constraint %s', constraint_name);
    end if;
end $$;
grant select, insert, update, delete on public.rockscout_collection to authenticated;
grant select, insert, delete on public.rockscout_wishlist to authenticated;
create index if not exists idx_friend_requests_recipient
    on public.rockscout_friend_requests (recipient_id)
    where status = 'pending';
create index if not exists idx_message_requests_recipient
    on public.rockscout_message_requests (recipient_id)
    where status = 'pending';
create index if not exists idx_threads_user_a
    on public.rockscout_threads (user_a);
create index if not exists idx_threads_user_b
    on public.rockscout_threads (user_b);
create index if not exists idx_pings_user_expires
    on public.rockscout_pings (user_id, expires_at);
create index if not exists idx_posts_user_created
    on public.rockscout_posts (user_id, created_at desc);
create index if not exists idx_notifications_user_unread
    on public.rockscout_notifications (user_id)
    where read_at is null;
create index if not exists idx_trade_interests_owner
    on public.rockscout_trade_interests (listing_owner_id);
do $$
begin
  perform cron.unschedule('cleanup-old-error-logs');
exception when others then null;
end $$;
select cron.schedule(
  'cleanup-old-error-logs',
  '0 3 * * *',
  $$delete from public.rockscout_error_logs where created_at < now() - interval '90 days'$$
);
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
select
    'user-photos',
    'user-photos',
    true,   -- public read (URLs include user_id + UUID, effectively unguessable)
    10_485_760,  -- 10 MB per file
    array['image/jpeg', 'image/png', 'image/webp', 'image/heic', 'image/heif']
where not exists (
    select 1 from storage.buckets where id = 'user-photos'
);
alter table storage.objects enable row level security;
drop policy if exists "user-photos-read-all" on storage.objects;
create policy "user-photos-read-all" on storage.objects
    for select using (bucket_id = 'user-photos');
drop policy if exists "user-photos-write-self" on storage.objects;
create policy "user-photos-write-self" on storage.objects
    for insert with check (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );
drop policy if exists "user-photos-update-self" on storage.objects;
create policy "user-photos-update-self" on storage.objects
    for update using (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    ) with check (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );
drop policy if exists "user-photos-delete-self" on storage.objects;
create policy "user-photos-delete-self" on storage.objects
    for delete using (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );
alter table public.rockscout_captures
    add column if not exists updated_at timestamptz not null default now();
alter table public.rockscout_saved_images
    add column if not exists updated_at timestamptz not null default now();
alter table public.rockscout_captures
    add column if not exists sync_status text not null default 'synced';
alter table public.rockscout_saved_images
    add column if not exists sync_status text not null default 'synced';
alter table public.rockscout_field_journal
    add column if not exists sync_status text not null default 'synced';
alter table public.rockscout_trips
    add column if not exists sync_status text not null default 'synced';
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
CREATE EXTENSION IF NOT EXISTS pg_net;
GRANT USAGE ON SCHEMA net TO postgres;
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
  SELECT value INTO worker_url FROM rockscout_config WHERE key = 'worker_url';
  SELECT value INTO app_key FROM rockscout_config WHERE key = 'app_key';
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
DROP TRIGGER IF EXISTS trigger_fatal_error_email ON rockscout_error_logs;
CREATE TRIGGER trigger_fatal_error_email
  AFTER INSERT ON rockscout_error_logs
  FOR EACH ROW
  WHEN (NEW.is_fatal = true)
  EXECUTE FUNCTION notify_fatal_error();
GRANT EXECUTE ON FUNCTION notify_fatal_error() TO postgres;
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
  reply_to_message_id text,  -- self-reference for reply threading
  tagged_user_ids uuid[] DEFAULT '{}',  -- users tagged in the message
  read_at timestamptz,  -- first time any non-sender member read it
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS user_warnings (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reason text DEFAULT '',
  source text DEFAULT 'chat',  -- 'chat' or 'group_chat'
  source_id text,  -- thread or group chat id
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
  read_at timestamptz,  -- per-recipient read tracking
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS chat_message_reads (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  message_id text NOT NULL REFERENCES chat_messages(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  read_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (message_id, user_id)
);
CREATE INDEX IF NOT EXISTS idx_group_chat_members_user ON group_chat_members(user_id);
CREATE INDEX IF NOT EXISTS idx_group_chat_members_group ON group_chat_members(group_chat_id);
CREATE INDEX IF NOT EXISTS idx_group_messages_chat ON group_messages(group_chat_id, created_at);
CREATE INDEX IF NOT EXISTS idx_group_chat_invites_invitee ON group_chat_invites(invitee_id, status);
CREATE INDEX IF NOT EXISTS idx_user_warnings_user ON user_warnings(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_chat_thread_participants_user ON chat_thread_participants(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_thread ON chat_messages(thread_id, created_at);
CREATE INDEX IF NOT EXISTS idx_chat_message_reads_message ON chat_message_reads(message_id);
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
ALTER TABLE user_warnings ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "uw_select" ON user_warnings;
CREATE POLICY "uw_select" ON user_warnings FOR SELECT
  USING (user_id = auth.uid());
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
CREATE OR REPLACE FUNCTION check_warning_thresholds()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  warn_count int;
BEGIN
  SELECT count(*) INTO warn_count FROM user_warnings WHERE user_id = NEW.user_id;
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
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chats TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chat_members TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chat_invites TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_messages TO anon, authenticated;
GRANT SELECT ON user_warnings TO anon, authenticated;
GRANT INSERT ON user_warnings TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_threads TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_thread_participants TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_messages TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_message_reads TO anon, authenticated;
ALTER TABLE rockscout_profiles ADD COLUMN IF NOT EXISTS banned_at timestamptz;
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
    UPDATE rockscout_profiles SET banned_at = now() WHERE id = NEW.user_id;
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
DROP POLICY IF EXISTS "uw_insert" ON user_warnings;
CREATE POLICY "uw_insert" ON user_warnings FOR INSERT
  WITH CHECK (user_id = auth.uid());
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
CREATE TABLE IF NOT EXISTS chat_typing_status (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  chat_id TEXT NOT NULL,
  user_id TEXT NOT NULL REFERENCES rockscout_profiles(id) ON DELETE CASCADE,
  is_typing BOOLEAN DEFAULT false,
  updated_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE(chat_id, user_id)
);
ALTER TABLE chat_typing_status ENABLE ROW LEVEL SECURITY;
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
CREATE POLICY "users_can_upsert_own_typing" ON chat_typing_status
  FOR INSERT WITH CHECK (user_id = auth.uid());
CREATE POLICY "users_can_update_own_typing" ON chat_typing_status
  FOR UPDATE USING (user_id = auth.uid());
CREATE POLICY "users_can_delete_own_typing" ON chat_typing_status
  FOR DELETE USING (user_id = auth.uid());
CREATE INDEX IF NOT EXISTS idx_typing_status_chat_id ON chat_typing_status(chat_id);
CREATE INDEX IF NOT EXISTS idx_typing_status_updated_at ON chat_typing_status(updated_at DESC);
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO anon;
ALTER TABLE rockscout_profiles
  ADD COLUMN IF NOT EXISTS highlight_color TEXT DEFAULT NULL;
ALTER TABLE rockscout_profiles ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Profiles are readable by all" ON rockscout_profiles;
CREATE POLICY "Profiles are readable by all"
  ON rockscout_profiles FOR SELECT
  USING (true);
DROP POLICY IF EXISTS "Users can update own profile" ON rockscout_profiles;
CREATE POLICY "Users can update own profile"
  ON rockscout_profiles FOR UPDATE
  USING (auth.uid() = id);
alter table public.rockscout_profiles
    add column if not exists premium_source text
    check (premium_source in ('apk', 'revenuecat', null));
ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS profanity_filter_level text NOT NULL DEFAULT 'low'
  CHECK (profanity_filter_level IN ('off', 'low', 'strict'));
GRANT UPDATE (profanity_filter_level) ON public.rockscout_profiles TO authenticated;
alter table public.rockscout_installed_devices
    add column if not exists device_platform text not null default 'unknown';
update public.rockscout_installed_devices
    set device_platform = 'web'
    where device_platform = 'unknown';
