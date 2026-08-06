-- PWA Phase 4: Stripe payments (tokens + premium unlock) + Web Push subscriptions.
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.

-- ============================================================================
-- 1. Extend rockscout_profiles with token bank + unlock window + Stripe link
-- ============================================================================
alter table public.rockscout_profiles
    add column if not exists tokens          int not null default 0,
    add column if not exists unlock_until    timestamptz,
    add column if not exists stripe_customer_id text;

-- Index for looking up a profile by Stripe customer id (webhook resolution).
create index if not exists idx_profiles_stripe_customer
    on public.rockscout_profiles (stripe_customer_id)
    where stripe_customer_id is not null;

-- Allow users to read/update their own token + unlock fields (existing RLS
-- policies already cover the row; we just need column grants).
grant update (tokens, unlock_until, stripe_customer_id)
    on public.rockscout_profiles to authenticated;
grant select (tokens, unlock_until)
    on public.rockscout_profiles to authenticated;

-- ============================================================================
-- 2. rockscout_push_subscriptions — per-user Web Push endpoints
-- ============================================================================
create table if not exists public.rockscout_push_subscriptions (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    endpoint        text not null,
    p256dh_key      text not null,
    auth_key        text not null,
    -- JSON array of enabled category ids: ["social","trade","weather",...]
    categories      jsonb not null default '[]'::jsonb,
    -- User-agent / platform hint for debugging ("ios-safari","chrome-desktop",...)
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

-- The webhook worker uses the service-role key (bypasses RLS) to send pushes,
-- so no anon/authenticated grants are needed beyond the owner policies above.
grant select, insert, delete on public.rockscout_push_subscriptions to authenticated;

create index if not exists idx_push_sub_user
    on public.rockscout_push_subscriptions (user_id);

-- ============================================================================
-- 3. rockscout_payment_events — idempotent webhook event log
-- ============================================================================
create table if not exists public.rockscout_payment_events (
    id              uuid primary key default gen_random_uuid(),
    stripe_event_id text unique not null,
    event_type      text not null,
    user_id         uuid references auth.users(id) on delete set null,
    payload         jsonb not null default '{}'::jsonb,
    processed_at    timestamptz not null default now()
);

-- No RLS — only the service-role worker writes/reads this table.
-- Prevents duplicate processing of the same Stripe event.
create index if not exists idx_payment_events_type
    on public.rockscout_payment_events (event_type, processed_at desc);

-- ============================================================================
-- DONE
-- ============================================================================
-- RockScout artifact embedding index for embedding-first artifact identification.
-- Mirrors the specimen_embeddings table (0003) but for the ARTIFACT_DB catalog.
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.
--
-- Model: openai/text-embedding-3-small (1536 dimensions) — same as specimens.

-- ============================================================================
-- 1. artifact_embeddings table — one row per (artifact, image, model)
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

-- ============================================================================
-- 2. Indexes — ivfflat for fast cosine similarity search
-- ============================================================================
-- lists = 100 is reasonable for ~106 artifacts (small, but consistent).
create index if not exists idx_artifact_embeddings_text_vec
    on public.artifact_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);

-- Index for the upsert dedup lookup.
create index if not exists idx_artifact_embeddings_dedup
    on public.artifact_embeddings (artifact_id, image_url, embedding_model);

-- ============================================================================
-- 3. RLS — reference data (not user-specific), so we disable RLS.
--    The backfill route is guarded at the Cloudflare layer by the toolkit
--    secret. The match RPC is called from the identify function with the
--    Supabase anon key.
-- ============================================================================
alter table public.artifact_embeddings disable row level security;

-- ============================================================================
-- 4. match_artifact_embeddings RPC — returns top-N artifact_ids by cosine
--    similarity to the query embedding.
-- ============================================================================
create or replace function public.match_artifact_embeddings(
    query_embedding vector(1536),
    match_count     int default 25
)
returns table (
    artifact_id     text,
    max_similarity  float
)
language sql
stable
as $$
    select
        e.artifact_id,
        max(1 - (e.text_embedding <=> query_embedding))::float as max_similarity
    from public.artifact_embeddings e
    where e.text_embedding is not null
    group by e.artifact_id
    order by max(1 - (e.text_embedding <=> query_embedding)) desc
    limit match_count;
$$;

-- Grant access to the anon role (used by the Cloudflare identify function).
grant select on public.artifact_embeddings to anon;
grant execute on function public.match_artifact_embeddings(vector(1536), int) to anon;

-- ============================================================================
-- DONE
-- ============================================================================
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
-- 0009_pwa_installed_devices.sql
-- Tracks premium PWA installs for the 2-additional-device limit.
-- Free installs are unlimited and NOT tracked in this table.

CREATE TABLE IF NOT EXISTS rockscout_installed_devices (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  device_label text NOT NULL,
  device_fingerprint text NOT NULL,
  user_agent text,
  installed_at timestamptz NOT NULL DEFAULT now(),
  last_seen_at timestamptz NOT NULL DEFAULT now()
);

-- One row per (user, device) — upsert on conflict
CREATE UNIQUE INDEX IF NOT EXISTS rockscout_installed_devices_user_fp_key
  ON rockscout_installed_devices (user_id, device_fingerprint);

-- Fast lookup by user
CREATE INDEX IF NOT EXISTS rockscout_installed_devices_user_idx
  ON rockscout_installed_devices (user_id);

-- RLS: users can only read/insert/delete their own devices
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
-- 0010_stripe_cleanup.sql
-- Removes all Stripe-related database artifacts now that payments are handled
-- exclusively via native in-app purchases (RevenueCat) on Android and iOS.
-- The web PWA Paywall now redirects users to subscribe in the mobile app.

-- ============================================================================
-- 1. Drop the rockscout_payment_events table (idempotent webhook event log)
-- ============================================================================
drop table if exists public.rockscout_payment_events cascade;

-- Drop the index if it still exists (drop table cascade handles it, but be safe).
drop index if exists idx_payment_events_type;

-- ============================================================================
-- 2. Remove stripe_customer_id column from rockscout_profiles
-- ============================================================================
alter table public.rockscout_profiles
    drop column if exists stripe_customer_id;

-- Drop the index that was used for Stripe customer lookups.
drop index if exists idx_profiles_stripe_customer;

-- ============================================================================
-- DONE
-- ============================================================================
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
-- 0012_settings_backup.sql
-- Stores per-user settings JSON blobs for cross-device / re-install restore.
-- When the app detects a fresh install (empty SharedPreferences) and the user
-- signs in, it fetches the backup from this table via the /settings/restore endpoint.
-- The backend uses the service role key to bypass RLS, so no RLS policies are needed.

CREATE TABLE IF NOT EXISTS public.rockscout_settings_backup (
    user_id        uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    settings_json  text NOT NULL,
    updated_at     timestamptz NOT NULL DEFAULT now()
);

-- Allow the service role to read/write (it bypasses RLS by default, but be explicit).
ALTER TABLE public.rockscout_settings_backup ENABLE ROW LEVEL SECURITY;

-- RLS: users can read their own backup (service role bypasses RLS anyway).
DROP POLICY IF EXISTS settings_backup_select ON public.rockscout_settings_backup;
CREATE POLICY settings_backup_select ON public.rockscout_settings_backup
    FOR SELECT TO authenticated USING (auth.uid() = user_id);

GRANT SELECT ON public.rockscout_settings_backup TO authenticated;
-- 0013_error_logs.sql
-- Central runtime error log for all platforms (Android, iOS, web PWA).
-- Populated by the /error-report Cloudflare Worker endpoint.
-- RLS disabled — inserts are gated by the app-key on the worker side.
-- Anyone with the anon key can read for diagnostics dashboards.

-- Ensure pg_cron is available before scheduling the retention job.
CREATE EXTENSION IF NOT EXISTS pg_cron;

CREATE TABLE IF NOT EXISTS rockscout_error_logs (
  id bigserial PRIMARY KEY,
  created_at timestamptz NOT NULL DEFAULT now(),

  -- Identity / context
  platform text NOT NULL,              -- 'android' | 'ios' | 'web'
  app_version text,
  os_version text,
  device_model text,                   -- model or userAgent
  user_id uuid,                        -- nullable — not all errors happen post-auth

  -- Error details
  error_type text NOT NULL,            -- e.g. 'NullPointerException', 'TypeError'
  error_message text NOT NULL,
  stack_trace text,                    -- truncated to ~8000 chars on the client
  is_fatal boolean NOT NULL DEFAULT false,

  -- Where in the app
  screen text,                         -- screen name / route
  breadcrumb text,                     -- last user action before the error

  -- Self-healing
  auto_healed boolean NOT NULL DEFAULT false,
  heal_action text,                    -- what was done, e.g. 'cleared_cache', 'retried'

  -- Dedup / frequency
  error_fingerprint text NOT NULL      -- SHA-256 of type+message+screen (first 16 chars)
);

-- Index for dedup queries and recent-error lookups
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

-- Retention: auto-delete errors older than 90 days (Supabase pg_cron)
-- Keeps the table from growing unbounded in production.
-- Idempotent: unschedule any existing job first to avoid duplicates on re-run.
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
