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

-- RockScout Friends social schema + RLS policies.
-- Run this in the Supabase SQL editor (or via `supabase db push`).
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.

-- ============================================================================
-- TABLES
-- ============================================================================

-- User profiles (one row per signed-in user, keyed by auth.uid())
create table if not exists public.rockscout_profiles (
    id                  uuid primary key references auth.users(id) on delete cascade,
    display_name        text not null default '',
    avatar_emoji        text not null default '💎',
    status              text not null default 'off',
    club_enabled        boolean not null default false,
    scan_radius_miles   int not null default 25,
    coarse_lat          double precision,
    coarse_lng          double precision,
    coarse_updated_at   timestamptz,
    last_status_change_at timestamptz default now(),
    level               int not null default 1,
    xp                  int not null default 0,
    is_pro              boolean not null default false,
    pro_badge           boolean not null default false,
    created_at          timestamptz not null default now()
);

-- Connections (reciprocal RockScout Friends). (user_a, user_b) is unique with a<b ordering.
create table if not exists public.rockscout_connections (
    id          uuid primary key default gen_random_uuid(),
    user_a      uuid not null references auth.users(id) on delete cascade,
    user_b      uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (user_a, user_b),
    check (user_a <> user_b)
);

-- Message requests (separate from friend requests). Accepting only opens a thread.
create table if not exists public.rockscout_message_requests (
    id           uuid primary key default gen_random_uuid(),
    sender_id    uuid not null references auth.users(id) on delete cascade,
    recipient_id uuid not null references auth.users(id) on delete cascade,
    body         text not null default '',
    status       text not null default 'pending',
    created_at   timestamptz not null default now(),
    responded_at timestamptz,
    check (status in ('pending','accepted','denied','blocked'))
);

-- Friend requests (separate from message requests). Accepting creates a connection.
create table if not exists public.rockscout_friend_requests (
    id           uuid primary key default gen_random_uuid(),
    sender_id    uuid not null references auth.users(id) on delete cascade,
    recipient_id uuid not null references auth.users(id) on delete cascade,
    status       text not null default 'pending',
    created_at   timestamptz not null default now(),
    responded_at timestamptz,
    check (status in ('pending','accepted','denied','blocked'))
);

-- Blocks (symmetric hide). A block row means blocker_id blocked blocked_id.
create table if not exists public.rockscout_blocks (
    id          uuid primary key default gen_random_uuid(),
    blocker_id  uuid not null references auth.users(id) on delete cascade,
    blocked_id  uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (blocker_id, blocked_id),
    check (blocker_id <> blocked_id)
);

-- Message threads between two users. (user_a, user_b) unique with a<b ordering.
create table if not exists public.rockscout_threads (
    id              uuid primary key default gen_random_uuid(),
    user_a          uuid not null references auth.users(id) on delete cascade,
    user_b          uuid not null references auth.users(id) on delete cascade,
    last_message_at timestamptz not null default now(),
    created_at      timestamptz not null default now(),
    unique (user_a, user_b),
    check (user_a <> user_b)
);

-- Messages within a thread.
create table if not exists public.rockscout_messages (
    id          uuid primary key default gen_random_uuid(),
    thread_id   uuid not null references public.rockscout_threads(id) on delete cascade,
    sender_id   uuid not null references auth.users(id) on delete cascade,
    body        text not null default '',
    read_at     timestamptz,
    created_at  timestamptz not null default now()
);

-- Live pings (drop-a-pin on the map).
create table if not exists public.rockscout_pings (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    lat         double precision not null,
    lng         double precision not null,
    label       text not null default '',
    expires_at  timestamptz not null,
    created_at  timestamptz not null default now()
);

-- Profile posts (max 5 per user, enforced by trigger).
create table if not exists public.rockscout_posts (
    id             uuid primary key default gen_random_uuid(),
    user_id        uuid not null references auth.users(id) on delete cascade,
    source_type    text not null,
    source_ref_id  text,
    title          text not null default '',
    tagline        text not null default '',
    image_uri      text,
    caption        text not null default '',
    location_text  text not null default '',
    created_at     timestamptz not null default now(),
    check (source_type in ('capture','collection','wishlist','digsite','raa','favoritespot','trip','journal'))
);

-- Post loves (yooperlite heart). Composite PK = one love per user per post.
create table if not exists public.rockscout_post_likes (
    post_id     uuid not null references public.rockscout_posts(id) on delete cascade,
    user_id     uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    primary key (post_id, user_id)
);

-- Post comments (friends only; one level of threading via parent_comment_id).
create table if not exists public.rockscout_post_comments (
    id                uuid primary key default gen_random_uuid(),
    post_id           uuid not null references public.rockscout_posts(id) on delete cascade,
    user_id           uuid not null references auth.users(id) on delete cascade,
    body              text not null default '',
    parent_comment_id uuid references public.rockscout_post_comments(id) on delete cascade,
    created_at        timestamptz not null default now()
);

-- Notifications (in-app + push source).
create table if not exists public.rockscout_notifications (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    type        text not null,
    actor_id    uuid,
    ref_id      text,
    body        text not null default '',
    read_at     timestamptz,
    created_at  timestamptz not null default now(),
    check (type in ('new_post','friend_request','message','trade_interest'))
);

-- Trade listings (synced from client; owner_user_id links to the listing owner).
create table if not exists public.rockscout_trade_listings (
    id                            uuid primary key default gen_random_uuid(),
    owner_user_id                 uuid not null references auth.users(id) on delete cascade,
    type                          text not null,
    specimen_name                 text not null default '',
    condition                     text not null default '',
    description                   text not null default '',
    want_in_return                text not null default '',
    photo_uri                     text,
    tags                          jsonb not null default '[]',
    source_capture_id             text,
    source_collection_specimen_id text,
    source_wishlist_specimen_id   text,
    status                        text not null default 'active',
    created_at                    bigint not null default extract(epoch from now())::bigint,
    expires_at                    bigint not null default (extract(epoch from now())::bigint + 2592000),
    check (type in ('HAVE','WANT')),
    check (status in ('active','traded','expired'))
);

-- Trade interests: one "I'm interested!" tap per user per listing.
create table if not exists public.rockscout_trade_interests (
    id                  uuid primary key default gen_random_uuid(),
    listing_id          uuid not null references public.rockscout_trade_listings(id) on delete cascade,
    listing_owner_id    uuid not null references auth.users(id) on delete cascade,
    interested_user_id  uuid not null references auth.users(id) on delete cascade,
    message             text not null default '',
    created_at          timestamptz not null default now(),
    unique (listing_id, interested_user_id),
    check (listing_owner_id <> interested_user_id)
);

-- ============================================================================
-- HELPER: is either user blocked relative to the other?
-- Returns true if a blocks b OR b blocks a.
-- ============================================================================
create or replace function public.rockscout_is_blocked(a uuid, b uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
    select exists (
        select 1 from public.rockscout_blocks
        where (blocker_id = a and blocked_id = b)
           or (blocker_id = b and blocked_id = a)
    );
$$;

-- ============================================================================
-- HELPER: are two users connected (friends)?
-- ============================================================================
create or replace function public.rockscout_are_connected(a uuid, b uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
    select exists (
        select 1 from public.rockscout_connections
        where (user_a = a and user_b = b)
           or (user_a = b and user_b = a)
    );
$$;

-- ============================================================================
-- 5-POST LIMIT TRIGGER
-- ============================================================================
create or replace function public.rockscout_enforce_five_posts()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
    -- Count the user's existing posts (excluding the one being inserted).
    if (select count(*) from public.rockscout_posts where user_id = new.user_id) >= 5 then
        -- Delete the oldest post to make room.
        delete from public.rockscout_posts
        where id in (
            select id from public.rockscout_posts
            where user_id = new.user_id
            order by created_at asc
            limit 1
        );
    end if;
    return new;
end;
$$;

drop trigger if exists trg_enforce_five_posts on public.rockscout_posts;
create trigger trg_enforce_five_posts
    before insert on public.rockscout_posts
    for each row execute function public.rockscout_enforce_five_posts();

-- ============================================================================
-- ROW LEVEL SECURITY
-- ============================================================================
alter table public.rockscout_profiles          enable row level security;
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
alter table public.rockscout_trade_listings    enable row level security;
alter table public.rockscout_trade_interests   enable row level security;

-- Helper: current user id
-- (auth.uid() is the Supabase built-in.)

-- ---- rockscout_profiles -----------------------------------------------------
-- Everyone can read profiles (so users can view other profiles), but the
-- NOT EXISTS block check hides blocked users. Location columns are
-- redacted client-side for non-friends; RLS just controls row visibility.
drop policy if exists profiles_select on public.rockscout_profiles;
create policy profiles_select on public.rockscout_profiles
    for select using (
        id = auth.uid()
        or not public.rockscout_is_blocked(auth.uid(), id)
    );

drop policy if exists profiles_insert_self on public.rockscout_profiles;
create policy profiles_insert_self on public.rockscout_profiles
    for insert with check (id = auth.uid());

drop policy if exists profiles_update_self on public.rockscout_profiles;
create policy profiles_update_self on public.rockscout_profiles
    for update using (id = auth.uid()) with check (id = auth.uid());

-- ---- rockscout_connections --------------------------------------------------
drop policy if exists connections_select on public.rockscout_connections;
create policy connections_select on public.rockscout_connections
    for select using (
        user_a = auth.uid() or user_b = auth.uid()
    );

drop policy if exists connections_insert on public.rockscout_connections;
create policy connections_insert on public.rockscout_connections
    for insert with check (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );

drop policy if exists connections_delete on public.rockscout_connections;
create policy connections_delete on public.rockscout_connections
    for delete using (user_a = auth.uid() or user_b = auth.uid());

-- ---- rockscout_message_requests ---------------------------------------------
drop policy if exists msg_requests_select on public.rockscout_message_requests;
create policy msg_requests_select on public.rockscout_message_requests
    for select using (
        sender_id = auth.uid() or recipient_id = auth.uid()
    );

drop policy if exists msg_requests_insert on public.rockscout_message_requests;
create policy msg_requests_insert on public.rockscout_message_requests
    for insert with check (
        sender_id = auth.uid()
        and not public.rockscout_is_blocked(sender_id, recipient_id)
    );

drop policy if exists msg_requests_update on public.rockscout_message_requests;
create policy msg_requests_update on public.rockscout_message_requests
    for update using (recipient_id = auth.uid()) with check (recipient_id = auth.uid());

-- ---- rockscout_friend_requests ----------------------------------------------
drop policy if exists friend_requests_select on public.rockscout_friend_requests;
create policy friend_requests_select on public.rockscout_friend_requests
    for select using (
        sender_id = auth.uid() or recipient_id = auth.uid()
    );

drop policy if exists friend_requests_insert on public.rockscout_friend_requests;
create policy friend_requests_insert on public.rockscout_friend_requests
    for insert with check (
        sender_id = auth.uid()
        and not public.rockscout_is_blocked(sender_id, recipient_id)
    );

drop policy if exists friend_requests_update on public.rockscout_friend_requests;
create policy friend_requests_update on public.rockscout_friend_requests
    for update using (recipient_id = auth.uid()) with check (recipient_id = auth.uid());

-- ---- rockscout_blocks -------------------------------------------------------
drop policy if exists blocks_select on public.rockscout_blocks;
create policy blocks_select on public.rockscout_blocks
    for select using (blocker_id = auth.uid() or blocked_id = auth.uid());

drop policy if exists blocks_insert on public.rockscout_blocks;
create policy blocks_insert on public.rockscout_blocks
    for insert with check (blocker_id = auth.uid());

drop policy if exists blocks_delete on public.rockscout_blocks;
create policy blocks_delete on public.rockscout_blocks
    for delete using (blocker_id = auth.uid());

-- ---- rockscout_threads ------------------------------------------------------
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

-- ---- rockscout_messages -----------------------------------------------------
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

-- ---- rockscout_pings --------------------------------------------------------
-- Visible to owner + connections, blocked users hidden.
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

-- ---- rockscout_posts --------------------------------------------------------
-- Everyone can read posts (so non-friends can view profiles), but blocked
-- users are hidden. Location redaction is client-side for non-friends.
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

-- ---- rockscout_post_likes ---------------------------------------------------
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

-- ---- rockscout_post_comments ------------------------------------------------
-- Friends only can comment; reads are open (minus blocked users).
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

-- ---- rockscout_notifications ------------------------------------------------
drop policy if exists notifs_select on public.rockscout_notifications;
create policy notifs_select on public.rockscout_notifications
    for select using (user_id = auth.uid());

drop policy if exists notifs_insert on public.rockscout_notifications;
create policy notifs_insert on public.rockscout_notifications
    for insert with check (
        user_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), user_id)
    );

drop policy if exists notifs_update on public.rockscout_notifications;
create policy notifs_update on public.rockscout_notifications
    for update using (user_id = auth.uid());

-- ---- rockscout_trade_listings -----------------------------------------------
drop policy if exists trade_listings_select on public.rockscout_trade_listings;
create policy trade_listings_select on public.rockscout_trade_listings
    for select using (
        owner_user_id = auth.uid()
        or not public.rockscout_is_blocked(auth.uid(), owner_user_id)
    );

drop policy if exists trade_listings_insert on public.rockscout_trade_listings;
create policy trade_listings_insert on public.rockscout_trade_listings
    for insert with check (owner_user_id = auth.uid());

drop policy if exists trade_listings_update on public.rockscout_trade_listings;
create policy trade_listings_update on public.rockscout_trade_listings
    for update using (owner_user_id = auth.uid());

drop policy if exists trade_listings_delete on public.rockscout_trade_listings;
create policy trade_listings_delete on public.rockscout_trade_listings
    for delete using (owner_user_id = auth.uid());

-- ---- rockscout_trade_interests ----------------------------------------------
-- The interested user can read own interests; the listing owner can read
-- interests on their listings. Blocked users cannot express interest.
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

-- ============================================================================
-- DONE
-- ============================================================================
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
-- RockScout specimen embedding index for visual-embedding-first identification.
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.
--
-- Text-only embedding approach: the Vercel AI Gateway /v1/embeddings endpoint
-- accepts text input only (vision-capable embedding models exist in the catalog
-- but their gateway endpoint exposes text-only input_modalities). So we embed a
-- rich text description of each specimen and do text-to-text cosine similarity
-- at query time. The LLM vision pass over the narrowed candidate set still runs
-- exactly as specified in the plan.
--
-- Model: openai/text-embedding-3-small (1536 dimensions)

-- ============================================================================
-- 1. Enable pgvector extension
-- ============================================================================
create extension if not exists vector with schema extensions;

-- ============================================================================
-- 2. specimen_embeddings table — one row per (specimen, image, model)
-- ============================================================================
create table if not exists public.specimen_embeddings (
    id              bigint primary key generated always as identity,
    specimen_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (specimen_id, image_url, embedding_model)
);

-- ============================================================================
-- 3. Indexes — ivfflat for fast cosine similarity search
-- ============================================================================
-- lists = 100 is a reasonable starting point for ~800-3600 rows.
-- For production scale, rebuild after the full backfill with tuned lists.
create index if not exists idx_specimen_embeddings_text_vec
    on public.specimen_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);

-- Index for the upsert dedup lookup.
create index if not exists idx_specimen_embeddings_dedup
    on public.specimen_embeddings (specimen_id, image_url, embedding_model);

-- ============================================================================
-- 4. RLS — this is reference data (not user-specific), so we disable RLS.
--    The backfill route is guarded at the Cloudflare layer by the toolkit
--    secret. The match RPC is called from the identify function with the
--    Supabase anon key.
-- ============================================================================
alter table public.specimen_embeddings disable row level security;

-- ============================================================================
-- 5. match_specimen_embeddings RPC — returns top-N specimen_ids by cosine
--    similarity to the query embedding. Groups by specimen_id and keeps the
--    highest similarity score per specimen (a specimen may have multiple
--    image rows, all with the same text embedding in the text-only approach).
-- ============================================================================
create or replace function public.match_specimen_embeddings(
    query_embedding vector(1536),
    match_count     int default 25
)
returns table (
    specimen_id     text,
    max_similarity  float
)
language sql
stable
as $$
    select
        e.specimen_id,
        max(1 - (e.text_embedding <=> query_embedding))::float as max_similarity
    from public.specimen_embeddings e
    where e.text_embedding is not null
    group by e.specimen_id
    order by max(1 - (e.text_embedding <=> query_embedding)) desc
    limit match_count;
$$;

-- Grant access to the anon role (used by the Cloudflare identify function).
grant select on public.specimen_embeddings to anon;
grant execute on function public.match_specimen_embeddings(vector(1536), int) to anon;

-- ============================================================================
-- DONE
-- ============================================================================
-- PWA specimen catalog + collection/wishlist tables.
-- The specimen_catalog table holds the same 900+ specimens as the Android
-- app's Kotlin data files and functions/specimens.ts. The backfill route
-- populates specimen_embeddings; a separate route (or manual SQL) populates
-- this catalog from the Kotlin specimen data.
-- Idempotent: uses IF NOT EXISTS so it can be re-run safely.

-- ============================================================================
-- 1. specimen_catalog — reference data, RLS disabled (readable by anon)
-- ============================================================================
create table if not exists public.specimen_catalog (
    id              text primary key,
    name            text not null,
    category        text not null default '',
    tagline         text not null default '',
    colors          text not null default '',
    hardness        text not null default '',
    luster          text not null default '',
    crystal_system  text not null default '',
    streak          text not null default '',
    rarity          text not null default '',
    image_url       text not null default '',
    description     text,
    formation       text,
    where_found     text,
    created_at      timestamptz not null default now()
);

alter table public.specimen_catalog disable row level security;
grant select on public.specimen_catalog to anon;

-- Search indexes
create index if not exists idx_specimen_catalog_name
    on public.specimen_catalog (name);
create index if not exists idx_specimen_catalog_category
    on public.specimen_catalog (category);
create index if not exists idx_specimen_catalog_rarity
    on public.specimen_catalog (rarity);

-- ============================================================================
-- 2. rockscout_collection — user's saved specimens (RLS via auth.uid())
-- ============================================================================
create table if not exists public.rockscout_collection (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    specimen_id text not null references public.specimen_catalog(id) on delete cascade,
    notes       text not null default '',
    created_at  timestamptz not null default now(),
    unique (user_id, specimen_id)
);

alter table public.rockscout_collection enable row level security;

drop policy if exists collection_select on public.rockscout_collection;
create policy collection_select on public.rockscout_collection
    for select using (auth.uid() = user_id);

drop policy if exists collection_insert on public.rockscout_collection;
create policy collection_insert on public.rockscout_collection
    for insert with check (auth.uid() = user_id);

drop policy if exists collection_delete on public.rockscout_collection;
create policy collection_delete on public.rockscout_collection
    for delete using (auth.uid() = user_id);

drop policy if exists collection_update on public.rockscout_collection;
create policy collection_update on public.rockscout_collection
    for update using (auth.uid() = user_id);

grant select, insert, update, delete on public.rockscout_collection to anon;

create index if not exists idx_collection_user
    on public.rockscout_collection (user_id);

-- ============================================================================
-- 3. rockscout_wishlist — user's wanted specimens (RLS via auth.uid())
-- ============================================================================
create table if not exists public.rockscout_wishlist (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    specimen_id text not null references public.specimen_catalog(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (user_id, specimen_id)
);

alter table public.rockscout_wishlist enable row level security;

drop policy if exists wishlist_select on public.rockscout_wishlist;
create policy wishlist_select on public.rockscout_wishlist
    for select using (auth.uid() = user_id);

drop policy if exists wishlist_insert on public.rockscout_wishlist;
create policy wishlist_insert on public.rockscout_wishlist
    for insert with check (auth.uid() = user_id);

drop policy if exists wishlist_delete on public.rockscout_wishlist;
create policy wishlist_delete on public.rockscout_wishlist
    for delete using (auth.uid() = user_id);

grant select, insert, delete on public.rockscout_wishlist to anon;

create index if not exists idx_wishlist_user
    on public.rockscout_wishlist (user_id);

-- ============================================================================
-- DONE
-- ============================================================================
-- PWA Phase 2: field journal, trip planner, favorite spots.
-- All three tables are per-user with RLS on auth.uid().
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.

-- ============================================================================
-- 1. rockscout_field_journal — per-user journal entries (synced across devices)
-- ============================================================================
create table if not exists public.rockscout_field_journal (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    entry_date      date not null default current_date,
    location        text not null default '',
    dig_site_id     text,
    weather_summary text not null default '',
    notes           text not null default '',
    photo_urls      jsonb not null default '[]'::jsonb,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);

alter table public.rockscout_field_journal enable row level security;

drop policy if exists field_journal_select on public.rockscout_field_journal;
create policy field_journal_select on public.rockscout_field_journal
    for select using (auth.uid() = user_id);

drop policy if exists field_journal_insert on public.rockscout_field_journal;
create policy field_journal_insert on public.rockscout_field_journal
    for insert with check (auth.uid() = user_id);

drop policy if exists field_journal_update on public.rockscout_field_journal;
create policy field_journal_update on public.rockscout_field_journal
    for update using (auth.uid() = user_id);

drop policy if exists field_journal_delete on public.rockscout_field_journal;
create policy field_journal_delete on public.rockscout_field_journal
    for delete using (auth.uid() = user_id);

grant select, insert, update, delete on public.rockscout_field_journal to anon;

create index if not exists idx_field_journal_user
    on public.rockscout_field_journal (user_id);
create index if not exists idx_field_journal_date
    on public.rockscout_field_journal (user_id, entry_date desc);

-- ============================================================================
-- 2. rockscout_trips — multi-stop rockhounding trip plans
-- ============================================================================
create table if not exists public.rockscout_trips (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    name            text not null,
    trip_date       date not null default current_date,
    stops           jsonb not null default '[]'::jsonb,
    target_specimens jsonb not null default '[]'::jsonb,
    gear_checklist  jsonb not null default '[]'::jsonb,
    notes           text not null default '',
    is_archived     boolean not null default false,
    completed_at    timestamptz,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);

alter table public.rockscout_trips enable row level security;

drop policy if exists trips_select on public.rockscout_trips;
create policy trips_select on public.rockscout_trips
    for select using (auth.uid() = user_id);

drop policy if exists trips_insert on public.rockscout_trips;
create policy trips_insert on public.rockscout_trips
    for insert with check (auth.uid() = user_id);

drop policy if exists trips_update on public.rockscout_trips;
create policy trips_update on public.rockscout_trips
    for update using (auth.uid() = user_id);

drop policy if exists trips_delete on public.rockscout_trips;
create policy trips_delete on public.rockscout_trips
    for delete using (auth.uid() = user_id);

grant select, insert, update, delete on public.rockscout_trips to anon;

create index if not exists idx_trips_user
    on public.rockscout_trips (user_id);
create index if not exists idx_trips_archived
    on public.rockscout_trips (user_id, is_archived, trip_date desc);

-- ============================================================================
-- 3. rockscout_favorite_spots — bookmarked locations (dig sites, parks, pins)
-- ============================================================================
create table if not exists public.rockscout_favorite_spots (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    spot_id         text not null,
    spot_type       text not null default 'dig_site',
    name            text not null,
    region          text not null default '',
    latitude        double precision not null default 0.0,
    longitude       double precision not null default 0.0,
    created_at      timestamptz not null default now(),
    unique (user_id, spot_id)
);

alter table public.rockscout_favorite_spots enable row level security;

drop policy if exists favorite_spots_select on public.rockscout_favorite_spots;
create policy favorite_spots_select on public.rockscout_favorite_spots
    for select using (auth.uid() = user_id);

drop policy if exists favorite_spots_insert on public.rockscout_favorite_spots;
create policy favorite_spots_insert on public.rockscout_favorite_spots
    for insert with check (auth.uid() = user_id);

drop policy if exists favorite_spots_delete on public.rockscout_favorite_spots;
create policy favorite_spots_delete on public.rockscout_favorite_spots
    for delete using (auth.uid() = user_id);

grant select, insert, delete on public.rockscout_favorite_spots to anon;

create index if not exists idx_favorite_spots_user
    on public.rockscout_favorite_spots (user_id);

-- ============================================================================
-- DONE
-- ============================================================================
