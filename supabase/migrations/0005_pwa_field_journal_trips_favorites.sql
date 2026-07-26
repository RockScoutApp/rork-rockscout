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
