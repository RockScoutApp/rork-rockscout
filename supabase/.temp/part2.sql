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
CREATE TABLE IF NOT EXISTS rockscout_installed_devices (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  device_label text NOT NULL,
  device_fingerprint text NOT NULL,
  user_agent text,
  installed_at timestamptz NOT NULL DEFAULT now(),
  last_seen_at timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS rockscout_installed_devices_user_fp_key
  ON rockscout_installed_devices (user_id, device_fingerprint);
CREATE INDEX IF NOT EXISTS rockscout_installed_devices_user_idx
  ON rockscout_installed_devices (user_id);
ALTER TABLE rockscout_installed_devices ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Users can read own devices"
  ON rockscout_installed_devices FOR SELECT
  TO authenticated
  USING (auth.uid() = user_id);
CREATE POLICY "Users can insert own devices"
  ON rockscout_installed_devices FOR INSERT
  TO authenticated
  WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can update own devices"
  ON rockscout_installed_devices FOR UPDATE
  TO authenticated
  USING (auth.uid() = user_id)
  WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can delete own devices"
  ON rockscout_installed_devices FOR DELETE
  TO authenticated
  USING (auth.uid() = user_id);
drop table if exists public.rockscout_payment_events cascade;
drop index if exists idx_payment_events_type;
alter table public.rockscout_profiles
    drop column if exists stripe_customer_id;
drop index if exists idx_profiles_stripe_customer;
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
CREATE TABLE IF NOT EXISTS public.rockscout_settings_backup (
    user_id        uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    settings_json  text NOT NULL,
    updated_at     timestamptz NOT NULL DEFAULT now()
);
ALTER TABLE public.rockscout_settings_backup ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS settings_backup_select ON public.rockscout_settings_backup;
CREATE POLICY settings_backup_select ON public.rockscout_settings_backup
    FOR SELECT TO authenticated USING (auth.uid() = user_id);
GRANT SELECT ON public.rockscout_settings_backup TO authenticated;
CREATE EXTENSION IF NOT EXISTS pg_cron;
CREATE TABLE IF NOT EXISTS rockscout_error_logs (
  id bigserial PRIMARY KEY,
  created_at timestamptz NOT NULL DEFAULT now(),
  platform text NOT NULL,              -- 'android' | 'ios' | 'web'
  app_version text,
  os_version text,
  device_model text,                   -- model or userAgent
  user_id uuid,                        -- nullable — not all errors happen post-auth
  error_type text NOT NULL,            -- e.g. 'NullPointerException', 'TypeError'
  error_message text NOT NULL,
  stack_trace text,                    -- truncated to ~8000 chars on the client
  is_fatal boolean NOT NULL DEFAULT false,
  screen text,                         -- screen name / route
  breadcrumb text,                     -- last user action before the error
  auto_healed boolean NOT NULL DEFAULT false,
  heal_action text,                    -- what was done, e.g. 'cleared_cache', 'retried'
  error_fingerprint text NOT NULL      -- SHA-256 of type+message+screen (first 16 chars)
);
CREATE INDEX IF NOT EXISTS rockscout_error_logs_fingerprint_idx
  ON rockscout_error_logs (error_fingerprint, created_at DESC);
CREATE INDEX IF NOT EXISTS rockscout_error_logs_platform_idx
  ON rockscout_error_logs (platform, created_at DESC);
CREATE INDEX IF NOT EXISTS rockscout_error_logs_user_idx
  ON rockscout_error_logs (user_id, created_at DESC)
  WHERE user_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS rockscout_error_logs_fatal_idx
  ON rockscout_error_logs (is_fatal, created_at DESC)
  WHERE is_fatal = true;
DO $$
BEGIN
  PERFORM cron.unschedule('cleanup-old-error-logs');
EXCEPTION WHEN OTHERS THEN NULL;
END $$;
SELECT cron.schedule(
  'cleanup-old-error-logs',
  '0 3 * * *',
  $$DELETE FROM rockscout_error_logs WHERE created_at < now() - interval '90 days'$$
);
create extension if not exists vector with schema extensions;
create extension if not exists pg_cron;
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
