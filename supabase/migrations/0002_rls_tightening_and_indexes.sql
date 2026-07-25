-- RockScout RLS tightening + missing indexes + block policy privacy fix.
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.

-- ============================================================================
-- 1. TIGHTEN notifs_insert — require actor_id = auth.uid() so a user can
--    only create notifications where they are the actor (not spoof others).
-- ============================================================================
drop policy if exists notifs_insert on public.rockscout_notifications;
create policy notifs_insert on public.rockscout_notifications
    for insert with check (
        actor_id = auth.uid()
        and user_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), user_id)
    );

-- ============================================================================
-- 2. FIX block policy privacy leak — blocked users should NOT be able to see
--    who blocked them. Only the blocker can see their own block rows.
-- ============================================================================
drop policy if exists blocks_select on public.rockscout_blocks;
create policy blocks_select on public.rockscout_blocks
    for select using (blocker_id = auth.uid());

-- ============================================================================
-- 3. MISSING INDEXES — critical for query performance at scale.
--    These speed up the most common filtered lookups in the app.
-- ============================================================================

-- Friend requests: lookup by recipient (the inbox query).
create index if not exists idx_friend_requests_recipient
    on public.rockscout_friend_requests (recipient_id)
    where status = 'pending';

-- Message requests: lookup by recipient (the requests inbox).
create index if not exists idx_message_requests_recipient
    on public.rockscout_message_requests (recipient_id)
    where status = 'pending';

-- Threads: lookup by either participant (the conversation list query).
create index if not exists idx_threads_user_a
    on public.rockscout_threads (user_a);
create index if not exists idx_threads_user_b
    on public.rockscout_threads (user_b);

-- Pings: filter by user + expiry (the "active pings" query).
create index if not exists idx_pings_user_expires
    on public.rockscout_pings (user_id, expires_at);

-- Posts: filter by user + created_at (the profile feed query).
create index if not exists idx_posts_user_created
    on public.rockscout_posts (user_id, created_at desc);

-- Notifications: filter by user + read status (the unread count query).
create index if not exists idx_notifications_user_unread
    on public.rockscout_notifications (user_id)
    where read_at is null;

-- Trade interests: lookup by listing owner (the "who's interested?" query).
create index if not exists idx_trade_interests_owner
    on public.rockscout_trade_interests (listing_owner_id);

-- ============================================================================
-- DONE
-- ============================================================================
