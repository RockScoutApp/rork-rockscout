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
