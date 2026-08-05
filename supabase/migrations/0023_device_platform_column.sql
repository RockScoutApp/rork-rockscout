-- Add device_platform column to rockscout_installed_devices so the
-- Manage Devices screen can show whether each device is Android, iOS, or web.
-- The 3-device limit is derived client-side from installed_at ordering —
-- no is_over_limit column, no triggers, no Postgres functions needed.

alter table public.rockscout_installed_devices
    add column if not exists device_platform text not null default 'unknown';

-- Backfill existing rows to 'web' (all existing entries are from the PWA).
update public.rockscout_installed_devices
    set device_platform = 'web'
    where device_platform = 'unknown';

-- RLS already allows authenticated users to select/insert/update/delete their
-- own rows (auth.uid() = user_id). The new column is covered by the existing
-- update policy, so no additional grant is needed.
