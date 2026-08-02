-- 0014_consolidated_rls_and_missing_tables.sql
--
-- Applies all missing tables from migrations 0001-0013 that were never
-- pushed to the live kblsiyyelyokhxaxefhy database, plus a full RLS policy
-- pass ensuring users can view/edit only their own specimen captures,
-- collection, wishlist, journal, trips, etc.
--
-- The service-role key (used by Cloudflare Workers and the Developer Console
-- for review and database upload) bypasses RLS entirely, so admin access is
-- automatic once the tables exist.
--
-- Idempotent: safe to re-run. Uses IF NOT EXISTS / DROP IF EXISTS throughout.

-- ============================================================================
-- 0. EXTENSIONS
-- ============================================================================
create extension if not exists vector with schema extensions;
create extension if not exists pg_cron;

-- ============================================================================
-- 1. HELPER FUNCTIONS (from 0001)
-- ============================================================================
create or replace function public.rockscout_is_blocked(a uuid, b uuid)
returns boolean language sql stable security definer set search_path = public as $$
    select exists (
        select 1 from public.rockscout_blocks
        where (blocker_id = a and blocked_id = b)
           or (blocker_id = b and blocked_id = a)
    );
$$;

create or replace function public.rockscout_are_connected(a uuid, b uuid)
returns boolean language sql stable security definer set search_path = public as $$
    select exists (
        select 1 from public.rockscout_connections
        where (user_a = a and user_b = b)
           or (user_a = b and user_b = a)
    );
$$;

-- ============================================================================
-- 2. ROCKSCOUT_PROFILES (from 0001 + 0006 column extensions)
-- ============================================================================
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
    tokens              int not null default 0,
    unlock_until        timestamptz,
    created_at          timestamptz not null default now()
);

alter table public.rockscout_profiles enable row level security;

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

grant select, insert, update on public.rockscout_profiles to anon, authenticated;
grant update (tokens, unlock_until) on public.rockscout_profiles to authenticated;
grant select (tokens, unlock_until) on public.rockscout_profiles to authenticated;

-- ============================================================================
-- 3. SOCIAL TABLES (from 0001)
-- ============================================================================
create table if not exists public.rockscout_connections (
    id          uuid primary key default gen_random_uuid(),
    user_a      uuid not null references auth.users(id) on delete cascade,
    user_b      uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (user_a, user_b),
    check (user_a <> user_b)
);

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

create table if not exists public.rockscout_friend_requests (
    id           uuid primary key default gen_random_uuid(),
    sender_id    uuid not null references auth.users(id) on delete cascade,
    recipient_id uuid not null references auth.users(id) on delete cascade,
    status       text not null default 'pending',
    created_at   timestamptz not null default now(),
    responded_at timestamptz,
    check (status in ('pending','accepted','denied','blocked'))
);

create table if not exists public.rockscout_blocks (
    id          uuid primary key default gen_random_uuid(),
    blocker_id  uuid not null references auth.users(id) on delete cascade,
    blocked_id  uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (blocker_id, blocked_id),
    check (blocker_id <> blocked_id)
);

create table if not exists public.rockscout_threads (
    id              uuid primary key default gen_random_uuid(),
    user_a          uuid not null references auth.users(id) on delete cascade,
    user_b          uuid not null references auth.users(id) on delete cascade,
    last_message_at timestamptz not null default now(),
    created_at      timestamptz not null default now(),
    unique (user_a, user_b),
    check (user_a <> user_b)
);

create table if not exists public.rockscout_messages (
    id          uuid primary key default gen_random_uuid(),
    thread_id   uuid not null references public.rockscout_threads(id) on delete cascade,
    sender_id   uuid not null references auth.users(id) on delete cascade,
    body        text not null default '',
    read_at     timestamptz,
    created_at  timestamptz not null default now()
);

create table if not exists public.rockscout_pings (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    lat         double precision not null,
    lng         double precision not null,
    label       text not null default '',
    expires_at  timestamptz not null,
    created_at  timestamptz not null default now()
);

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

create table if not exists public.rockscout_post_likes (
    post_id     uuid not null references public.rockscout_posts(id) on delete cascade,
    user_id     uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    primary key (post_id, user_id)
);

create table if not exists public.rockscout_post_comments (
    id                uuid primary key default gen_random_uuid(),
    post_id           uuid not null references public.rockscout_posts(id) on delete cascade,
    user_id           uuid not null references auth.users(id) on delete cascade,
    body              text not null default '',
    parent_comment_id uuid references public.rockscout_post_comments(id) on delete cascade,
    created_at        timestamptz not null default now()
);

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
-- 4. 5-POST LIMIT TRIGGER (from 0001)
-- ============================================================================
create or replace function public.rockscout_enforce_five_posts()
returns trigger language plpgsql security definer set search_path = public as $$
begin
    if (select count(*) from public.rockscout_posts where user_id = new.user_id) >= 5 then
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
-- 5. FIELD CAPTURES + SAVED IMAGES (from 0008)
-- ============================================================================
create table if not exists public.rockscout_captures (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null default 'field-camera',
    specimen_emoji  text not null default '📷',
    custom_name     text not null default '',
    custom_location text not null default '',
    general_info    text not null default '',
    image_urls      jsonb not null default '[]'::jsonb,
    latitude        double precision,
    longitude       double precision,
    in_collection   boolean not null default false,
    in_wishlist     boolean not null default false,
    created_at      timestamptz not null default now()
);

alter table public.rockscout_captures enable row level security;

drop policy if exists captures_select on public.rockscout_captures;
create policy captures_select on public.rockscout_captures
    for select using (auth.uid() = user_id);

drop policy if exists captures_insert on public.rockscout_captures;
create policy captures_insert on public.rockscout_captures
    for insert with check (auth.uid() = user_id);

drop policy if exists captures_update on public.rockscout_captures;
create policy captures_update on public.rockscout_captures
    for update using (auth.uid() = user_id);

drop policy if exists captures_delete on public.rockscout_captures;
create policy captures_delete on public.rockscout_captures
    for delete using (auth.uid() = user_id);

grant select, insert, update, delete on public.rockscout_captures to anon, authenticated;

create index if not exists idx_captures_user
    on public.rockscout_captures (user_id);
create index if not exists idx_captures_created
    on public.rockscout_captures (user_id, created_at desc);

create table if not exists public.rockscout_saved_images (
    id            uuid primary key default gen_random_uuid(),
    user_id       uuid not null references auth.users(id) on delete cascade,
    image_url     text not null,
    thumbnail_url text not null default '',
    source        text not null default 'field-camera',
    created_at    timestamptz not null default now()
);

alter table public.rockscout_saved_images enable row level security;

drop policy if exists saved_images_select on public.rockscout_saved_images;
create policy saved_images_select on public.rockscout_saved_images
    for select using (auth.uid() = user_id);

drop policy if exists saved_images_insert on public.rockscout_saved_images;
create policy saved_images_insert on public.rockscout_saved_images
    for insert with check (auth.uid() = user_id);

drop policy if exists saved_images_delete on public.rockscout_saved_images;
create policy saved_images_delete on public.rockscout_saved_images
    for delete using (auth.uid() = user_id);

grant select, insert, delete on public.rockscout_saved_images to anon, authenticated;

create index if not exists idx_saved_images_user
    on public.rockscout_saved_images (user_id);
create index if not exists idx_saved_images_created
    on public.rockscout_saved_images (user_id, created_at desc);

-- ============================================================================
-- 6. FIELD JOURNAL + TRIPS + FAVORITE SPOTS (from 0005)
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

grant select, insert, update, delete on public.rockscout_field_journal to anon, authenticated;

create index if not exists idx_field_journal_user
    on public.rockscout_field_journal (user_id);
create index if not exists idx_field_journal_date
    on public.rockscout_field_journal (user_id, entry_date desc);

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

grant select, insert, update, delete on public.rockscout_trips to anon, authenticated;

create index if not exists idx_trips_user
    on public.rockscout_trips (user_id);
create index if not exists idx_trips_archived
    on public.rockscout_trips (user_id, is_archived, trip_date desc);

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

grant select, insert, delete on public.rockscout_favorite_spots to anon, authenticated;

create index if not exists idx_favorite_spots_user
    on public.rockscout_favorite_spots (user_id);

-- ============================================================================
-- 7. LIKED SPECIMENS + AURORA SAVED SPOTS (from 0011)
-- ============================================================================
create table if not exists public.rockscout_liked_specimens (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null,
    created_at      timestamptz not null default now(),
    unique (user_id, specimen_id)
);

alter table public.rockscout_liked_specimens enable row level security;

drop policy if exists liked_specimens_select on public.rockscout_liked_specimens;
create policy liked_specimens_select on public.rockscout_liked_specimens
    for select using (auth.uid() = user_id);
drop policy if exists liked_specimens_insert on public.rockscout_liked_specimens;
create policy liked_specimens_insert on public.rockscout_liked_specimens
    for insert with check (auth.uid() = user_id);
drop policy if exists liked_specimens_delete on public.rockscout_liked_specimens;
create policy liked_specimens_delete on public.rockscout_liked_specimens
    for delete using (auth.uid() = user_id);

grant select, insert, delete on public.rockscout_liked_specimens to anon, authenticated;

create index if not exists idx_liked_specimens_user
    on public.rockscout_liked_specimens (user_id);

create table if not exists public.rockscout_aurora_saved_spots (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    spot_id         text not null,
    name            text not null,
    latitude        double precision not null,
    longitude       double precision not null,
    created_at      bigint not null default 0,
    unique (user_id, spot_id)
);

alter table public.rockscout_aurora_saved_spots enable row level security;

drop policy if exists aurora_spots_select on public.rockscout_aurora_saved_spots;
create policy aurora_spots_select on public.rockscout_aurora_saved_spots
    for select using (auth.uid() = user_id);
drop policy if exists aurora_spots_insert on public.rockscout_aurora_saved_spots;
create policy aurora_spots_insert on public.rockscout_aurora_saved_spots
    for insert with check (auth.uid() = user_id);
drop policy if exists aurora_spots_delete on public.rockscout_aurora_saved_spots;
create policy aurora_spots_delete on public.rockscout_aurora_saved_spots
    for delete using (auth.uid() = user_id);

grant select, insert, delete on public.rockscout_aurora_saved_spots to anon, authenticated;

create index if not exists idx_aurora_spots_user
    on public.rockscout_aurora_saved_spots (user_id);

-- ============================================================================
-- 8. PUSH SUBSCRIPTIONS (from 0006, minus Stripe)
-- ============================================================================
create table if not exists public.rockscout_push_subscriptions (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    endpoint        text not null,
    p256dh_key      text not null,
    auth_key        text not null,
    categories      jsonb not null default '[]'::jsonb,
    platform        text not null default '',
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now(),
    unique (user_id, endpoint)
);

alter table public.rockscout_push_subscriptions enable row level security;

drop policy if exists push_sub_select on public.rockscout_push_subscriptions;
create policy push_sub_select on public.rockscout_push_subscriptions
    for select using (auth.uid() = user_id);
drop policy if exists push_sub_insert on public.rockscout_push_subscriptions;
create policy push_sub_insert on public.rockscout_push_subscriptions
    for insert with check (auth.uid() = user_id);
drop policy if exists push_sub_delete on public.rockscout_push_subscriptions;
create policy push_sub_delete on public.rockscout_push_subscriptions
    for delete using (auth.uid() = user_id);

grant select, insert, delete on public.rockscout_push_subscriptions to authenticated;

create index if not exists idx_push_sub_user
    on public.rockscout_push_subscriptions (user_id);

-- ============================================================================
-- 9. INSTALLED DEVICES (from 0009)
-- ============================================================================
create table if not exists public.rockscout_installed_devices (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id) on delete cascade,
    device_label text not null,
    device_fingerprint text not null,
    user_agent text,
    installed_at timestamptz not null default now(),
    last_seen_at timestamptz not null default now()
);

create unique index if not exists rockscout_installed_devices_user_fp_key
    on public.rockscout_installed_devices (user_id, device_fingerprint);

create index if not exists rockscout_installed_devices_user_idx
    on public.rockscout_installed_devices (user_id);

alter table public.rockscout_installed_devices enable row level security;

drop policy if exists "Users can read own devices" on public.rockscout_installed_devices;
create policy "Users can read own devices"
    on public.rockscout_installed_devices for select to authenticated
    using (auth.uid() = user_id);

drop policy if exists "Users can insert own devices" on public.rockscout_installed_devices;
create policy "Users can insert own devices"
    on public.rockscout_installed_devices for insert to authenticated
    with check (auth.uid() = user_id);

drop policy if exists "Users can update own devices" on public.rockscout_installed_devices;
create policy "Users can update own devices"
    on public.rockscout_installed_devices for update to authenticated
    using (auth.uid() = user_id) with check (auth.uid() = user_id);

drop policy if exists "Users can delete own devices" on public.rockscout_installed_devices;
create policy "Users can delete own devices"
    on public.rockscout_installed_devices for delete to authenticated
    using (auth.uid() = user_id);

-- ============================================================================
-- 10. SETTINGS BACKUP (from 0012)
-- ============================================================================
create table if not exists public.rockscout_settings_backup (
    user_id        uuid primary key references auth.users(id) on delete cascade,
    settings_json  text not null,
    updated_at     timestamptz not null default now()
);

alter table public.rockscout_settings_backup enable row level security;

drop policy if exists settings_backup_select on public.rockscout_settings_backup;
create policy settings_backup_select on public.rockscout_settings_backup
    for select to authenticated using (auth.uid() = user_id);

grant select on public.rockscout_settings_backup to authenticated;

-- ============================================================================
-- 11. ARTIFACT EMBEDDINGS (from 0007)
-- ============================================================================
create table if not exists public.artifact_embeddings (
    id              bigint primary key generated always as identity,
    artifact_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (artifact_id, image_url, embedding_model)
);

create index if not exists idx_artifact_embeddings_text_vec
    on public.artifact_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);

create index if not exists idx_artifact_embeddings_dedup
    on public.artifact_embeddings (artifact_id, image_url, embedding_model);

alter table public.artifact_embeddings disable row level security;

grant select on public.artifact_embeddings to anon;

create or replace function public.match_artifact_embeddings(
    query_embedding vector(1536),
    match_count     int default 25
)
returns table (artifact_id text, max_similarity float)
language sql stable as $$
    select
        e.artifact_id,
        max(1 - (e.text_embedding <=> query_embedding))::float as max_similarity
    from public.artifact_embeddings e
    where e.text_embedding is not null
    group by e.artifact_id
    order by max(1 - (e.text_embedding <=> query_embedding)) desc
    limit match_count;
$$;

grant execute on function public.match_artifact_embeddings(vector(1536), int) to anon;

-- ============================================================================
-- 12. TRADE LISTINGS — merged schema (0001 social + 0011 Android sync)
-- ============================================================================
-- The table uses user_id as the primary owner column (matching the Android
-- sync code). owner_user_id is kept for the social trade board RLS policies
-- from 0001; a trigger copies user_id → owner_user_id on insert so both
-- the sync code and the social features work seamlessly.

create table if not exists public.rockscout_trade_listings (
    id                            uuid primary key default gen_random_uuid(),
    user_id                       uuid not null references auth.users(id) on delete cascade,
    owner_user_id                 uuid references auth.users(id) on delete cascade,
    listing_id                    text not null default '',
    type                          text not null default 'HAVE',
    listing_mode                  text not null default 'SWAP',
    price                         text not null default '',
    specimen_name                 text not null default '',
    condition                     text not null default '',
    description                   text not null default '',
    want_in_return                text not null default '',
    photo_uri                     text,
    tags                          jsonb not null default '[]'::jsonb,
    source_capture_id             text,
    source_collection_specimen_id text,
    source_wishlist_specimen_id   text,
    status                        text not null default 'active',
    owner_username                text,
    created_at                    bigint not null default 0,
    expires_at                    bigint not null default 0,
    synced_at                     timestamptz not null default now(),
    unique (user_id, listing_id),
    check (type in ('HAVE','WANT')),
    check (status in ('active','traded','expired'))
);

-- Trigger: copy user_id → owner_user_id so social RLS policies work
create or replace function public.rockscout_set_owner_user_id()
returns trigger language plpgsql as $$
begin
    if new.owner_user_id is null then
        new.owner_user_id := new.user_id;
    end if;
    return new;
end;
$$;

drop trigger if exists trg_set_owner_user_id on public.rockscout_trade_listings;
create trigger trg_set_owner_user_id
    before insert on public.rockscout_trade_listings
    for each row execute function public.rockscout_set_owner_user_id();

alter table public.rockscout_trade_listings enable row level security;

-- Social RLS: owner can see own listings; others can see non-blocked listings
drop policy if exists trade_listings_select on public.rockscout_trade_listings;
create policy trade_listings_select on public.rockscout_trade_listings
    for select using (
        user_id = auth.uid()
        or (
            owner_user_id is not null
            and not public.rockscout_is_blocked(auth.uid(), owner_user_id)
        )
    );

drop policy if exists trade_listings_insert on public.rockscout_trade_listings;
create policy trade_listings_insert on public.rockscout_trade_listings
    for insert with check (user_id = auth.uid());

drop policy if exists trade_listings_update on public.rockscout_trade_listings;
create policy trade_listings_update on public.rockscout_trade_listings
    for update using (user_id = auth.uid());

drop policy if exists trade_listings_delete on public.rockscout_trade_listings;
create policy trade_listings_delete on public.rockscout_trade_listings
    for delete using (user_id = auth.uid());

grant select, insert, update, delete on public.rockscout_trade_listings to anon, authenticated;

create index if not exists idx_trade_listings_user
    on public.rockscout_trade_listings (user_id);

-- ============================================================================
-- 13. SOCIAL RLS POLICIES (from 0001 + 0002 tightening)
-- ============================================================================
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

-- Connections
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

-- Message requests
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

-- Friend requests
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

-- Blocks (tightened in 0002: only blocker can see their blocks)
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

-- Threads
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

-- Messages
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

-- Pings
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

-- Posts
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

-- Post likes
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

-- Post comments
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

-- Notifications
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

-- Trade interests
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

-- ============================================================================
-- 14. EXISTING TABLE FIXES
-- ============================================================================

-- rockscout_collection: add columns from 0011 that the Android sync code expects
alter table public.rockscout_collection
    add column if not exists note text not null default '';
alter table public.rockscout_collection
    add column if not exists found_at text not null default '';
alter table public.rockscout_collection
    add column if not exists added_at bigint not null default 0;

-- Drop the FK on specimen_id so non-catalog specimen IDs don't cause insert failures
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

-- Ensure the existing collection RLS policies are correct (already exists from 0004)
-- The policies already grant select/insert/update/delete to anon — also grant to authenticated
grant select, insert, update, delete on public.rockscout_collection to authenticated;

-- Ensure wishlist grants include authenticated
grant select, insert, delete on public.rockscout_wishlist to authenticated;

-- ============================================================================
-- 15. INDEXES (from 0002)
-- ============================================================================
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

-- ============================================================================
-- 16. ERROR LOG RETENTION (from 0013, table already exists)
-- ============================================================================
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

-- ============================================================================
-- DONE
-- ============================================================================
