-- ═══════════════════════════════════════════════════════════════════════════
-- rockscout_config table — needed by 0016's notify_fatal_error() trigger
-- No migration creates this table; 0016's trigger reads from it.
-- ═══════════════════════════════════════════════════════════════════════════
CREATE TABLE IF NOT EXISTS public.rockscout_config (
    key         text PRIMARY KEY,
    value       text NOT NULL,
    updated_at  timestamptz NOT NULL DEFAULT now()
);

ALTER TABLE public.rockscout_config DISABLE ROW LEVEL SECURITY;
GRANT SELECT ON public.rockscout_config TO anon, authenticated;

INSERT INTO public.rockscout_config (key, value) VALUES
    ('worker_url', 'https://rockscout-finder-backend.rork.app'),
    ('app_key', 'rpk_munggtdkjtv3tbx5sw9ge3kebajzh39k')
ON CONFLICT (key) DO NOTHING;

