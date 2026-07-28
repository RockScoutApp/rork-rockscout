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
