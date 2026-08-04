-- 0020_profile_highlight_color.sql
-- Adds a custom highlight color column to rockscout_profiles so users can
-- personalize their profile page with a color others can see.

ALTER TABLE rockscout_profiles
  ADD COLUMN IF NOT EXISTS highlight_color TEXT DEFAULT NULL;

-- Allow users to read everyone's highlight_color (it's public display data)
ALTER TABLE rockscout_profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Profiles are readable by all" ON rockscout_profiles;
CREATE POLICY "Profiles are readable by all"
  ON rockscout_profiles FOR SELECT
  USING (true);

-- Allow users to update their own highlight_color
DROP POLICY IF EXISTS "Users can update own profile" ON rockscout_profiles;
CREATE POLICY "Users can update own profile"
  ON rockscout_profiles FOR UPDATE
  USING (auth.uid() = id);
