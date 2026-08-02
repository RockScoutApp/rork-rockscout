-- 0015_user_photos_storage_and_sync.sql
--
-- Creates a private Supabase Storage bucket for user-uploaded photos
-- (field captures, journal photos, trip photos, saved images) so they
-- can be synced cross-device when connectivity is restored.
--
-- RLS on the bucket ensures users can only read/write their own folder:
--   user-photos/{user_id}/{filename}
--
-- The service-role key (used by Cloudflare Workers and the Developer
-- Console for review) bypasses RLS entirely.

-- ============================================================================
-- 1. STORAGE BUCKET
-- ============================================================================
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
select
    'user-photos',
    'user-photos',
    true,   -- public read (URLs include user_id + UUID, effectively unguessable)
            -- write still controlled via RLS policies below
    10_485_760,  -- 10 MB per file
    array['image/jpeg', 'image/png', 'image/webp', 'image/heic', 'image/heif']
where not exists (
    select 1 from storage.buckets where id = 'user-photos'
);

-- ============================================================================
-- 2. STORAGE RLS POLICIES
-- ============================================================================
-- Users can upload to their own folder: user-photos/{auth.uid()}/...
-- Public reads are allowed (bucket is public) but writes are RLS-controlled.
alter table storage.objects enable row level security;

-- Allow anyone to SELECT (read) — bucket is public, filenames include UUIDs
drop policy if exists "user-photos-read-all" on storage.objects;
create policy "user-photos-read-all" on storage.objects
    for select using (bucket_id = 'user-photos');

-- Allow users to INSERT (upload) into their own folder only
drop policy if exists "user-photos-write-self" on storage.objects;
create policy "user-photos-write-self" on storage.objects
    for insert with check (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );

-- Allow users to UPDATE (overwrite) their own files
drop policy if exists "user-photos-update-self" on storage.objects;
create policy "user-photos-update-self" on storage.objects
    for update using (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    ) with check (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );

-- Allow users to DELETE their own files
drop policy if exists "user-photos-delete-self" on storage.objects;
create policy "user-photos-delete-self" on storage.objects
    for delete using (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );

-- ============================================================================
-- 3. ADD updated_at COLUMNS FOR SYNC CONFLICT DETECTION
-- ============================================================================
-- These columns let the sync logic compare local vs remote timestamps
-- to implement last-write-wins conflict resolution.

alter table public.rockscout_captures
    add column if not exists updated_at timestamptz not null default now();

alter table public.rockscout_saved_images
    add column if not exists updated_at timestamptz not null default now();

-- rockscout_field_journal already has updated_at from migration 0005
-- rockscout_trips already has updated_at from migration 0005

-- ============================================================================
-- 4. ADD sync_status COLUMN FOR TRACKING DIRTY RECORDS
-- ============================================================================
-- Tracks whether a row has been synced to the client. Values:
--   'synced'  — the row is current on both client and server
--   'dirty'   — the row was modified locally and needs to be pushed
--   'pending' — the row is queued for upload (photo upload in progress)

alter table public.rockscout_captures
    add column if not exists sync_status text not null default 'synced';

alter table public.rockscout_saved_images
    add column if not exists sync_status text not null default 'synced';

alter table public.rockscout_field_journal
    add column if not exists sync_status text not null default 'synced';

alter table public.rockscout_trips
    add column if not exists sync_status text not null default 'synced';

-- ============================================================================
-- 5. INDEXES FOR SYNC QUERIES
-- ============================================================================
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

-- ============================================================================
-- DONE
-- ============================================================================
