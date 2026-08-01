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
