-- ═══════════════════════════════════════════════════════════════════════════
-- ALTER trade_listings to add columns from 0014's merged schema
-- (0001 created it with owner_user_id only; 0014's RLS policies need user_id)
-- ═══════════════════════════════════════════════════════════════════════════
ALTER TABLE public.rockscout_trade_listings
    ADD COLUMN IF NOT EXISTS user_id uuid REFERENCES auth.users(id) ON DELETE CASCADE,
    ADD COLUMN IF NOT EXISTS listing_id text NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS listing_mode text NOT NULL DEFAULT 'SWAP',
    ADD COLUMN IF NOT EXISTS price text NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS owner_username text,
    ADD COLUMN IF NOT EXISTS synced_at timestamptz NOT NULL DEFAULT now();

CREATE UNIQUE INDEX IF NOT EXISTS rockscout_trade_listings_user_listing_id_key
    ON public.rockscout_trade_listings (user_id, listing_id);

CREATE OR REPLACE FUNCTION public.rockscout_set_owner_user_id()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.owner_user_id IS NULL THEN
        NEW.owner_user_id := NEW.user_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_set_owner_user_id ON public.rockscout_trade_listings;
CREATE TRIGGER trg_set_owner_user_id
    BEFORE INSERT ON public.rockscout_trade_listings
    FOR EACH ROW EXECUTE FUNCTION public.rockscout_set_owner_user_id();

