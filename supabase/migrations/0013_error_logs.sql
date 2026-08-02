-- 0013_error_logs.sql
-- Central runtime error log for all platforms (Android, iOS, web PWA).
-- Populated by the /error-report Cloudflare Worker endpoint.
-- RLS disabled — inserts are gated by the app-key on the worker side.
-- Anyone with the anon key can read for diagnostics dashboards.

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
SELECT cron.schedule(
  'cleanup-old-error-logs',
  '0 3 * * *',
  $$DELETE FROM rockscout_error_logs WHERE created_at < now() - interval '90 days'$$
);
