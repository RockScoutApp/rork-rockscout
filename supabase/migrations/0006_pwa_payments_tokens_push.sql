-- PWA Phase 4: Stripe payments (tokens + premium unlock) + Web Push subscriptions.
-- Idempotent: uses IF NOT EXISTS / OR REPLACE so it can be re-run safely.

-- ============================================================================
-- 1. Extend rockscout_profiles with token bank + unlock window + Stripe link
-- ============================================================================
alter table public.rockscout_profiles
    add column if not exists tokens          int not null default 0,
    add column if not exists unlock_until    timestamptz,
    add column if not exists stripe_customer_id text;

-- Index for looking up a profile by Stripe customer id (webhook resolution).
create index if not exists idx_profiles_stripe_customer
    on public.rockscout_profiles (stripe_customer_id)
    where stripe_customer_id is not null;

-- Allow users to read/update their own token + unlock fields (existing RLS
-- policies already cover the row; we just need column grants).
grant update (tokens, unlock_until, stripe_customer_id)
    on public.rockscout_profiles to authenticated;
grant select (tokens, unlock_until)
    on public.rockscout_profiles to authenticated;

-- ============================================================================
-- 2. rockscout_push_subscriptions — per-user Web Push endpoints
-- ============================================================================
create table if not exists public.rockscout_push_subscriptions (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    endpoint        text not null,
    p256dh_key      text not null,
    auth_key        text not null,
    -- JSON array of enabled category ids: ["social","trade","weather",...]
    categories      jsonb not null default '[]'::jsonb,
    -- User-agent / platform hint for debugging ("ios-safari","chrome-desktop",...)
    platform        text not null default '',
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now(),
    unique (user_id, endpoint)
);

alter table public.rockscout_push_subscriptions enable row level security;

drop policy if exists push_sub_select on public.rockscout_push_subscriptions;
create policy push_sub_select on public.rockscout_push_subscriptions
    for select using (auth.uid() = user_id);

drop policy if exists push_sub_insert on public.rockscout_push_subscriptions;
create policy push_sub_insert on public.rockscout_push_subscriptions
    for insert with check (auth.uid() = user_id);

drop policy if exists push_sub_delete on public.rockscout_push_subscriptions;
create policy push_sub_delete on public.rockscout_push_subscriptions
    for delete using (auth.uid() = user_id);

-- The webhook worker uses the service-role key (bypasses RLS) to send pushes,
-- so no anon/authenticated grants are needed beyond the owner policies above.
grant select, insert, delete on public.rockscout_push_subscriptions to authenticated;

create index if not exists idx_push_sub_user
    on public.rockscout_push_subscriptions (user_id);

-- ============================================================================
-- 3. rockscout_payment_events — idempotent webhook event log
-- ============================================================================
create table if not exists public.rockscout_payment_events (
    id              uuid primary key default gen_random_uuid(),
    stripe_event_id text unique not null,
    event_type      text not null,
    user_id         uuid references auth.users(id) on delete set null,
    payload         jsonb not null default '{}'::jsonb,
    processed_at    timestamptz not null default now()
);

-- No RLS — only the service-role worker writes/reads this table.
-- Prevents duplicate processing of the same Stripe event.
create index if not exists idx_payment_events_type
    on public.rockscout_payment_events (event_type, processed_at desc);

-- ============================================================================
-- DONE
-- ============================================================================
