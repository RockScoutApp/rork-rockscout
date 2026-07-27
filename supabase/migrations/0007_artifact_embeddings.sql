-- RockScout artifact embedding index for embedding-first artifact identification.
-- Mirrors the specimen_embeddings table (0003) but for the ARTIFACT_DB catalog.
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.
--
-- Model: openai/text-embedding-3-small (1536 dimensions) — same as specimens.

-- ============================================================================
-- 1. artifact_embeddings table — one row per (artifact, image, model)
-- ============================================================================
create table if not exists public.artifact_embeddings (
    id              bigint primary key generated always as identity,
    artifact_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (artifact_id, image_url, embedding_model)
);

-- ============================================================================
-- 2. Indexes — ivfflat for fast cosine similarity search
-- ============================================================================
-- lists = 100 is reasonable for ~106 artifacts (small, but consistent).
create index if not exists idx_artifact_embeddings_text_vec
    on public.artifact_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);

-- Index for the upsert dedup lookup.
create index if not exists idx_artifact_embeddings_dedup
    on public.artifact_embeddings (artifact_id, image_url, embedding_model);

-- ============================================================================
-- 3. RLS — reference data (not user-specific), so we disable RLS.
--    The backfill route is guarded at the Cloudflare layer by the toolkit
--    secret. The match RPC is called from the identify function with the
--    Supabase anon key.
-- ============================================================================
alter table public.artifact_embeddings disable row level security;

-- ============================================================================
-- 4. match_artifact_embeddings RPC — returns top-N artifact_ids by cosine
--    similarity to the query embedding.
-- ============================================================================
create or replace function public.match_artifact_embeddings(
    query_embedding vector(1536),
    match_count     int default 25
)
returns table (
    artifact_id     text,
    max_similarity  float
)
language sql
stable
as $$
    select
        e.artifact_id,
        max(1 - (e.text_embedding <=> query_embedding))::float as max_similarity
    from public.artifact_embeddings e
    where e.text_embedding is not null
    group by e.artifact_id
    order by max(1 - (e.text_embedding <=> query_embedding)) desc
    limit match_count;
$$;

-- Grant access to the anon role (used by the Cloudflare identify function).
grant select on public.artifact_embeddings to anon;
grant execute on function public.match_artifact_embeddings(vector(1536), int) to anon;

-- ============================================================================
-- DONE
-- ============================================================================
