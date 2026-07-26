-- RockScout specimen embedding index for visual-embedding-first identification.
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.
--
-- Text-only embedding approach: the Vercel AI Gateway /v1/embeddings endpoint
-- accepts text input only (vision-capable embedding models exist in the catalog
-- but their gateway endpoint exposes text-only input_modalities). So we embed a
-- rich text description of each specimen and do text-to-text cosine similarity
-- at query time. The LLM vision pass over the narrowed candidate set still runs
-- exactly as specified in the plan.
--
-- Model: openai/text-embedding-3-small (1536 dimensions)

-- ============================================================================
-- 1. Enable pgvector extension
-- ============================================================================
create extension if not exists vector with schema extensions;

-- ============================================================================
-- 2. specimen_embeddings table — one row per (specimen, image, model)
-- ============================================================================
create table if not exists public.specimen_embeddings (
    id              bigint primary key generated always as identity,
    specimen_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (specimen_id, image_url, embedding_model)
);

-- ============================================================================
-- 3. Indexes — ivfflat for fast cosine similarity search
-- ============================================================================
-- lists = 100 is a reasonable starting point for ~800-3600 rows.
-- For production scale, rebuild after the full backfill with tuned lists.
create index if not exists idx_specimen_embeddings_text_vec
    on public.specimen_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);

-- Index for the upsert dedup lookup.
create index if not exists idx_specimen_embeddings_dedup
    on public.specimen_embeddings (specimen_id, image_url, embedding_model);

-- ============================================================================
-- 4. RLS — this is reference data (not user-specific), so we disable RLS.
--    The backfill route is guarded at the Cloudflare layer by the toolkit
--    secret. The match RPC is called from the identify function with the
--    Supabase anon key.
-- ============================================================================
alter table public.specimen_embeddings disable row level security;

-- ============================================================================
-- 5. match_specimen_embeddings RPC — returns top-N specimen_ids by cosine
--    similarity to the query embedding. Groups by specimen_id and keeps the
--    highest similarity score per specimen (a specimen may have multiple
--    image rows, all with the same text embedding in the text-only approach).
-- ============================================================================
create or replace function public.match_specimen_embeddings(
    query_embedding vector(1536),
    match_count     int default 25
)
returns table (
    specimen_id     text,
    max_similarity  float
)
language sql
stable
as $$
    select
        e.specimen_id,
        max(1 - (e.text_embedding <=> query_embedding))::float as max_similarity
    from public.specimen_embeddings e
    where e.text_embedding is not null
    group by e.specimen_id
    order by max(1 - (e.text_embedding <=> query_embedding)) desc
    limit match_count;
$$;

-- Grant access to the anon role (used by the Cloudflare identify function).
grant select on public.specimen_embeddings to anon;
grant execute on function public.match_specimen_embeddings(vector(1536), int) to anon;

-- ============================================================================
-- DONE
-- ============================================================================
