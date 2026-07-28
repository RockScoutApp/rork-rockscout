-- 0011_android_synced_data.sql
-- Creates the remaining per-user data tables needed for Android <-> Supabase sync.
-- The web PWA already uses rockscout_captures, rockscout_saved_images,
-- rockscout_field_journal, rockscout_trips, and rockscout_favorite_spots.
-- This migration adds: collection, wishlist, liked specimens, trade listings,
-- and aurora saved spots — all per-user with RLS on auth.uid().

-- ============================================================================
-- 1. rockscout_collection — user's collected specimens with personal notes
-- ============================================================================
create table if not exists public.rockscout_collection (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null,
    note            text not null default '',
    found_at        text not null default '',
    added_at        bigint not null default 0,
    created_at      timestamptz not null default now(),
    unique (user_id, specimen_id)
);

alter table public.rockscout_collection enable row level security;

drop policy if exists collection_select on public.rockscout_collection;
create policy collection_select on public.rockscout_collection
    for select using (auth.uid() = user_id);

drop policy if exists collection_insert on public.rockscout_collection;
create policy collection_insert on public.rockscout_collection
    for insert with check (auth.uid() = user_id);

drop policy if exists collection_update on public.rockscout_collection;
create policy collection_update on public.rockscout_collection
    for update using (auth.uid() = user_id);

drop policy if exists collection_delete on public.rockscout_collection;
create policy collection_delete on public.rockscout_collection
    for delete using (auth.uid() = user_id);

grant select, insert, update, delete on public.rockscout_collection to authenticated;

create index if not exists idx_collection_user
    on public.rockscout_collection (user_id);

-- ============================================================================
-- 2. rockscout_wishlist — user's wishlist specimen IDs
-- ============================================================================
create table if not exists public.rockscout_wishlist (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null,
    created_at      timestamptz not null default now(),
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

grant select, insert, delete on public.rockscout_wishlist to authenticated;

create index if not exists idx_wishlist_user
    on public.rockscout_wishlist (user_id);

-- ============================================================================
-- 3. rockscout_liked_specimens — user's liked specimen IDs
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

grant select, insert, delete on public.rockscout_liked_specimens to authenticated;

create index if not exists idx_liked_specimens_user
    on public.rockscout_liked_specimens (user_id);

-- ============================================================================
-- 4. rockscout_trade_listings — user's trade board listings
-- ============================================================================
create table if not exists public.rockscout_trade_listings (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    listing_id      text not null,
    type            text not null default 'HAVE',
    listing_mode    text not null default 'SWAP',
    price           text not null default '',
    specimen_name   text not null,
    condition       text not null default '',
    description     text not null default '',
    want_in_return  text not null default '',
    photo_uri       text,
    tags            jsonb not null default '[]'::jsonb,
    source_capture_id      text,
    source_collection_specimen_id text,
    source_wishlist_specimen_id   text,
    status          text not null default 'active',
    owner_username  text,
    created_at      bigint not null default 0,
    expires_at      bigint not null default 0,
    synced_at       timestamptz not null default now(),
    unique (user_id, listing_id)
);

alter table public.rockscout_trade_listings enable row level security;

drop policy if exists trade_listings_select on public.rockscout_trade_listings;
create policy trade_listings_select on public.rockscout_trade_listings
    for select using (auth.uid() = user_id);

drop policy if exists trade_listings_insert on public.rockscout_trade_listings;
create policy trade_listings_insert on public.rockscout_trade_listings
    for insert with check (auth.uid() = user_id);

drop policy if exists trade_listings_update on public.rockscout_trade_listings;
create policy trade_listings_update on public.rockscout_trade_listings
    for update using (auth.uid() = user_id);

drop policy if exists trade_listings_delete on public.rockscout_trade_listings;
create policy trade_listings_delete on public.rockscout_trade_listings
    for delete using (auth.uid() = user_id);

grant select, insert, update, delete on public.rockscout_trade_listings to authenticated;

create index if not exists idx_trade_listings_user
    on public.rockscout_trade_listings (user_id);

-- ============================================================================
-- 5. rockscout_aurora_saved_spots — user's aurora tracking bookmarks
-- ============================================================================
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

grant select, insert, delete on public.rockscout_aurora_saved_spots to authenticated;

create index if not exists idx_aurora_spots_user
    on public.rockscout_aurora_saved_spots (user_id);

-- ============================================================================
-- DONE
-- ============================================================================
