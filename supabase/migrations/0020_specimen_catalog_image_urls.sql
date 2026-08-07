-- Add image_urls jsonb column to specimen_catalog for multi-image galleries.
-- The Android app has 3-9 images per specimen; the web PWA was only showing
-- a single hero image. This column holds the full image set as a JSON array.
-- Idempotent: uses IF NOT EXISTS so it can be re-run safely.

alter table public.specimen_catalog
    add column if not exists image_urls jsonb not null default '[]'::jsonb;
