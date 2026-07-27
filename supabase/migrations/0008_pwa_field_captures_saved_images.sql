-- PWA Phase 1: field captures + saved images.
-- Mirrors Android's CapturedPhoto and SavedImage models so the web PWA
-- has full parity with the field camera flow.
-- Both tables are per-user with RLS on auth.uid().

-- ============================================================================
-- 1. rockscout_captures — field camera photos (always created, shown on map)
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

grant select, insert, update, delete on public.rockscout_captures to anon;

create index if not exists idx_captures_user
    on public.rockscout_captures (user_id);
create index if not exists idx_captures_created
    on public.rockscout_captures (user_id, created_at desc);

-- ============================================================================
-- 2. rockscout_saved_images — user's saved image gallery
-- ============================================================================
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

grant select, insert, delete on public.rockscout_saved_images to anon;

create index if not exists idx_saved_images_user
    on public.rockscout_saved_images (user_id);
create index if not exists idx_saved_images_created
    on public.rockscout_saved_images (user_id, created_at desc);

-- ============================================================================
-- DONE
-- ============================================================================
