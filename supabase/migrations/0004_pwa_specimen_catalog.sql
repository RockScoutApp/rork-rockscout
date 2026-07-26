-- PWA specimen catalog + collection/wishlist tables.
-- The specimen_catalog table holds the same 900+ specimens as the Android
-- app's Kotlin data files and functions/specimens.ts. The backfill route
-- populates specimen_embeddings; a separate route (or manual SQL) populates
-- this catalog from the Kotlin specimen data.
-- Idempotent: uses IF NOT EXISTS so it can be re-run safely.

-- ============================================================================
-- 1. specimen_catalog — reference data, RLS disabled (readable by anon)
-- ============================================================================
create table if not exists public.specimen_catalog (
    id              text primary key,
    name            text not null,
    category        text not null default '',
    tagline         text not null default '',
    colors          text not null default '',
    hardness        text not null default '',
    luster          text not null default '',
    crystal_system  text not null default '',
    streak          text not null default '',
    rarity          text not null default '',
    image_url       text not null default '',
    description     text,
    formation       text,
    where_found     text,
    created_at      timestamptz not null default now()
);

alter table public.specimen_catalog disable row level security;
grant select on public.specimen_catalog to anon;

-- Search indexes
create index if not exists idx_specimen_catalog_name
    on public.specimen_catalog (name);
create index if not exists idx_specimen_catalog_category
    on public.specimen_catalog (category);
create index if not exists idx_specimen_catalog_rarity
    on public.specimen_catalog (rarity);

-- ============================================================================
-- 2. rockscout_collection — user's saved specimens (RLS via auth.uid())
-- ============================================================================
create table if not exists public.rockscout_collection (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    specimen_id text not null references public.specimen_catalog(id) on delete cascade,
    notes       text not null default '',
    created_at  timestamptz not null default now(),
    unique (user_id, specimen_id)
);

alter table public.rockscout_collection enable row level security;

drop policy if exists collection_select on public.rockscout_collection;
create policy collection_select on public.rockscout_collection
    for select using (auth.uid() = user_id);

drop policy if exists collection_insert on public.rockscout_collection;
create policy collection_insert on public.rockscout_collection
    for insert with check (auth.uid() = user_id);

drop policy if exists collection_delete on public.rockscout_collection;
create policy collection_delete on public.rockscout_collection
    for delete using (auth.uid() = user_id);

drop policy if exists collection_update on public.rockscout_collection;
create policy collection_update on public.rockscout_collection
    for update using (auth.uid() = user_id);

grant select, insert, update, delete on public.rockscout_collection to anon;

create index if not exists idx_collection_user
    on public.rockscout_collection (user_id);

-- ============================================================================
-- 3. rockscout_wishlist — user's wanted specimens (RLS via auth.uid())
-- ============================================================================
create table if not exists public.rockscout_wishlist (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    specimen_id text not null references public.specimen_catalog(id) on delete cascade,
    created_at  timestamptz not null default now(),
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

grant select, insert, delete on public.rockscout_wishlist to anon;

create index if not exists idx_wishlist_user
    on public.rockscout_wishlist (user_id);

-- ============================================================================
-- DONE
-- ============================================================================
