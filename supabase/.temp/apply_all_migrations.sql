-- RockScout consolidated migrations
-- Run this in Supabase SQL Editor (New query → Run)
-- Idempotent: safe to re-run if some tables already exist

-- Tables
create table if not exists public.rockscout_profiles (
    id                  uuid primary key references auth.users(id) on delete cascade,
    display_name        text not null default '',
    avatar_emoji        text not null default '💎',
    status              text not null default 'off',
    club_enabled        boolean not null default false,
    scan_radius_miles   int not null default 25,
    coarse_lat          double precision,
    coarse_lng          double precision,
    coarse_updated_at   timestamptz,
    last_status_change_at timestamptz default now(),
    level               int not null default 1,
    xp                  int not null default 0,
    is_pro              boolean not null default false,
    pro_badge           boolean not null default false,
    created_at          timestamptz not null default now()
);
create table if not exists public.rockscout_connections (
    id          uuid primary key default gen_random_uuid(),
    user_a      uuid not null references auth.users(id) on delete cascade,
    user_b      uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (user_a, user_b),
    check (user_a <> user_b)
);
create table if not exists public.rockscout_message_requests (
    id           uuid primary key default gen_random_uuid(),
    sender_id    uuid not null references auth.users(id) on delete cascade,
    recipient_id uuid not null references auth.users(id) on delete cascade,
    body         text not null default '',
    status       text not null default 'pending',
    created_at   timestamptz not null default now(),
    responded_at timestamptz,
    check (status in ('pending','accepted','denied','blocked'))
);
create table if not exists public.rockscout_friend_requests (
    id           uuid primary key default gen_random_uuid(),
    sender_id    uuid not null references auth.users(id) on delete cascade,
    recipient_id uuid not null references auth.users(id) on delete cascade,
    status       text not null default 'pending',
    created_at   timestamptz not null default now(),
    responded_at timestamptz,
    check (status in ('pending','accepted','denied','blocked'))
);
create table if not exists public.rockscout_blocks (
    id          uuid primary key default gen_random_uuid(),
    blocker_id  uuid not null references auth.users(id) on delete cascade,
    blocked_id  uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (blocker_id, blocked_id),
    check (blocker_id <> blocked_id)
);
create table if not exists public.rockscout_threads (
    id              uuid primary key default gen_random_uuid(),
    user_a          uuid not null references auth.users(id) on delete cascade,
    user_b          uuid not null references auth.users(id) on delete cascade,
    last_message_at timestamptz not null default now(),
    created_at      timestamptz not null default now(),
    unique (user_a, user_b),
    check (user_a <> user_b)
);
create table if not exists public.rockscout_messages (
    id          uuid primary key default gen_random_uuid(),
    thread_id   uuid not null references public.rockscout_threads(id) on delete cascade,
    sender_id   uuid not null references auth.users(id) on delete cascade,
    body        text not null default '',
    read_at     timestamptz,
    created_at  timestamptz not null default now()
);
create table if not exists public.rockscout_pings (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    lat         double precision not null,
    lng         double precision not null,
    label       text not null default '',
    expires_at  timestamptz not null,
    created_at  timestamptz not null default now()
);
create table if not exists public.rockscout_posts (
    id             uuid primary key default gen_random_uuid(),
    user_id        uuid not null references auth.users(id) on delete cascade,
    source_type    text not null,
    source_ref_id  text,
    title          text not null default '',
    tagline        text not null default '',
    image_uri      text,
    caption        text not null default '',
    location_text  text not null default '',
    created_at     timestamptz not null default now(),
    check (source_type in ('capture','collection','wishlist','digsite','raa','favoritespot','trip','journal'))
);
create table if not exists public.rockscout_post_likes (
    post_id     uuid not null references public.rockscout_posts(id) on delete cascade,
    user_id     uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    primary key (post_id, user_id)
);
create table if not exists public.rockscout_post_comments (
    id                uuid primary key default gen_random_uuid(),
    post_id           uuid not null references public.rockscout_posts(id) on delete cascade,
    user_id           uuid not null references auth.users(id) on delete cascade,
    body              text not null default '',
    parent_comment_id uuid references public.rockscout_post_comments(id) on delete cascade,
    created_at        timestamptz not null default now()
);
create table if not exists public.rockscout_notifications (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    type        text not null,
    actor_id    uuid,
    ref_id      text,
    body        text not null default '',
    read_at     timestamptz,
    created_at  timestamptz not null default now(),
    check (type in ('new_post','friend_request','message','trade_interest'))
);
create table if not exists public.rockscout_trade_listings (
    id                            uuid primary key default gen_random_uuid(),
    owner_user_id                 uuid not null references auth.users(id) on delete cascade,
    type                          text not null,
    specimen_name                 text not null default '',
    condition                     text not null default '',
    description                   text not null default '',
    want_in_return                text not null default '',
    photo_uri                     text,
    tags                          jsonb not null default '[]',
    source_capture_id             text,
    source_collection_specimen_id text,
    source_wishlist_specimen_id   text,
    status                        text not null default 'active',
    created_at                    bigint not null default extract(epoch from now())::bigint,
    expires_at                    bigint not null default (extract(epoch from now())::bigint + 2592000),
    check (type in ('HAVE','WANT')),
    check (status in ('active','traded','expired'))
);
create table if not exists public.rockscout_trade_interests (
    id                  uuid primary key default gen_random_uuid(),
    listing_id          uuid not null references public.rockscout_trade_listings(id) on delete cascade,
    listing_owner_id    uuid not null references auth.users(id) on delete cascade,
    interested_user_id  uuid not null references auth.users(id) on delete cascade,
    message             text not null default '',
    created_at          timestamptz not null default now(),
    unique (listing_id, interested_user_id),
    check (listing_owner_id <> interested_user_id)
);
create table if not exists public.specimen_embeddings (
    id              bigint primary key generated always as identity,
    specimen_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (specimen_id, image_url, embedding_model)
);
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
create table if not exists public.rockscout_collection (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    specimen_id text not null references public.specimen_catalog(id) on delete cascade,
    notes       text not null default '',
    created_at  timestamptz not null default now(),
    unique (user_id, specimen_id)
);
create table if not exists public.rockscout_wishlist (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    specimen_id text not null references public.specimen_catalog(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (user_id, specimen_id)
);
create table if not exists public.rockscout_field_journal (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    entry_date      date not null default current_date,
    location        text not null default '',
    dig_site_id     text,
    weather_summary text not null default '',
    notes           text not null default '',
    photo_urls      jsonb not null default '[]'::jsonb,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);
create table if not exists public.rockscout_trips (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    name            text not null,
    trip_date       date not null default current_date,
    stops           jsonb not null default '[]'::jsonb,
    target_specimens jsonb not null default '[]'::jsonb,
    gear_checklist  jsonb not null default '[]'::jsonb,
    notes           text not null default '',
    is_archived     boolean not null default false,
    completed_at    timestamptz,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);
create table if not exists public.rockscout_favorite_spots (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    spot_id         text not null,
    spot_type       text not null default 'dig_site',
    name            text not null,
    region          text not null default '',
    latitude        double precision not null default 0.0,
    longitude       double precision not null default 0.0,
    created_at      timestamptz not null default now(),
    unique (user_id, spot_id)
);
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
create table if not exists public.rockscout_payment_events (
    id              uuid primary key default gen_random_uuid(),
    stripe_event_id text unique not null,
    event_type      text not null,
    user_id         uuid references auth.users(id) on delete set null,
    payload         jsonb not null default '{}'::jsonb,
    processed_at    timestamptz not null default now()
);
create table if not exists public.artifact_embeddings (
    id              bigint primary key generated always as identity,
    artifact_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (artifact_id, image_url, embedding_model)
);
create table if not exists public.rockscout_captures (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null default 'field-camera',
    specimen_emoji  text not null default '📷',
    custom_name     text not null default '',
    custom_location text not null default '',
    general_info    text not null default '',
    image_urls      jsonb not null default '[]'::jsonb,
    latitude        double precision,
    longitude       double precision,
    in_collection   boolean not null default false,
    in_wishlist     boolean not null default false,
    created_at      timestamptz not null default now()
);
create table if not exists public.rockscout_saved_images (
    id            uuid primary key default gen_random_uuid(),
    user_id       uuid not null references auth.users(id) on delete cascade,
    image_url     text not null,
    thumbnail_url text not null default '',
    source        text not null default 'field-camera',
    created_at    timestamptz not null default now()
);
create table if not exists public.rockscout_collection (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null,
    note            text not null default '',
    found_at        text not null default '',
    added_at        bigint not null default 0,
    created_at      timestamptz not null default now(),
    unique (user_id, specimen_id)
);
create table if not exists public.rockscout_wishlist (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null,
    created_at      timestamptz not null default now(),
    unique (user_id, specimen_id)
);
create table if not exists public.rockscout_liked_specimens (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null,
    created_at      timestamptz not null default now(),
    unique (user_id, specimen_id)
);
create table if not exists public.rockscout_trade_listings (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    listing_id      text not null,
    type            text not null default 'HAVE',
    listing_mode    text not null default 'SWAP',
    price           text not null default '',
    specimen_name   text not null,
    condition       text not null default '',
    description     text not null default '',
    want_in_return  text not null default '',
    photo_uri       text,
    tags            jsonb not null default '[]'::jsonb,
    source_capture_id      text,
    source_collection_specimen_id text,
    source_wishlist_specimen_id   text,
    status          text not null default 'active',
    owner_username  text,
    created_at      bigint not null default 0,
    expires_at      bigint not null default 0,
    synced_at       timestamptz not null default now(),
    unique (user_id, listing_id)
);
create table if not exists public.rockscout_aurora_saved_spots (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    spot_id         text not null,
    name            text not null,
    latitude        double precision not null,
    longitude       double precision not null,
    created_at      bigint not null default 0,
    unique (user_id, spot_id)
);
CREATE TABLE IF NOT EXISTS public.rockscout_settings_backup (
    user_id        uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    settings_json  text NOT NULL,
    updated_at     timestamptz NOT NULL DEFAULT now()
);
create table if not exists public.rockscout_profiles (
    id                  uuid primary key references auth.users(id) on delete cascade,
    display_name        text not null default '',
    avatar_emoji        text not null default '💎',
    status              text not null default 'off',
    club_enabled        boolean not null default false,
    scan_radius_miles   int not null default 25,
    coarse_lat          double precision,
    coarse_lng          double precision,
    coarse_updated_at   timestamptz,
    last_status_change_at timestamptz default now(),
    level               int not null default 1,
    xp                  int not null default 0,
    is_pro              boolean not null default false,
    pro_badge           boolean not null default false,
    tokens              int not null default 0,
    unlock_until        timestamptz,
    created_at          timestamptz not null default now()
);
create table if not exists public.rockscout_connections (
    id          uuid primary key default gen_random_uuid(),
    user_a      uuid not null references auth.users(id) on delete cascade,
    user_b      uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (user_a, user_b),
    check (user_a <> user_b)
);
create table if not exists public.rockscout_message_requests (
    id           uuid primary key default gen_random_uuid(),
    sender_id    uuid not null references auth.users(id) on delete cascade,
    recipient_id uuid not null references auth.users(id) on delete cascade,
    body         text not null default '',
    status       text not null default 'pending',
    created_at   timestamptz not null default now(),
    responded_at timestamptz,
    check (status in ('pending','accepted','denied','blocked'))
);
create table if not exists public.rockscout_friend_requests (
    id           uuid primary key default gen_random_uuid(),
    sender_id    uuid not null references auth.users(id) on delete cascade,
    recipient_id uuid not null references auth.users(id) on delete cascade,
    status       text not null default 'pending',
    created_at   timestamptz not null default now(),
    responded_at timestamptz,
    check (status in ('pending','accepted','denied','blocked'))
);
create table if not exists public.rockscout_blocks (
    id          uuid primary key default gen_random_uuid(),
    blocker_id  uuid not null references auth.users(id) on delete cascade,
    blocked_id  uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    unique (blocker_id, blocked_id),
    check (blocker_id <> blocked_id)
);
create table if not exists public.rockscout_threads (
    id              uuid primary key default gen_random_uuid(),
    user_a          uuid not null references auth.users(id) on delete cascade,
    user_b          uuid not null references auth.users(id) on delete cascade,
    last_message_at timestamptz not null default now(),
    created_at      timestamptz not null default now(),
    unique (user_a, user_b),
    check (user_a <> user_b)
);
create table if not exists public.rockscout_messages (
    id          uuid primary key default gen_random_uuid(),
    thread_id   uuid not null references public.rockscout_threads(id) on delete cascade,
    sender_id   uuid not null references auth.users(id) on delete cascade,
    body        text not null default '',
    read_at     timestamptz,
    created_at  timestamptz not null default now()
);
create table if not exists public.rockscout_pings (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    lat         double precision not null,
    lng         double precision not null,
    label       text not null default '',
    expires_at  timestamptz not null,
    created_at  timestamptz not null default now()
);
create table if not exists public.rockscout_posts (
    id             uuid primary key default gen_random_uuid(),
    user_id        uuid not null references auth.users(id) on delete cascade,
    source_type    text not null,
    source_ref_id  text,
    title          text not null default '',
    tagline        text not null default '',
    image_uri      text,
    caption        text not null default '',
    location_text  text not null default '',
    created_at     timestamptz not null default now(),
    check (source_type in ('capture','collection','wishlist','digsite','raa','favoritespot','trip','journal'))
);
create table if not exists public.rockscout_post_likes (
    post_id     uuid not null references public.rockscout_posts(id) on delete cascade,
    user_id     uuid not null references auth.users(id) on delete cascade,
    created_at  timestamptz not null default now(),
    primary key (post_id, user_id)
);
create table if not exists public.rockscout_post_comments (
    id                uuid primary key default gen_random_uuid(),
    post_id           uuid not null references public.rockscout_posts(id) on delete cascade,
    user_id           uuid not null references auth.users(id) on delete cascade,
    body              text not null default '',
    parent_comment_id uuid references public.rockscout_post_comments(id) on delete cascade,
    created_at        timestamptz not null default now()
);
create table if not exists public.rockscout_notifications (
    id          uuid primary key default gen_random_uuid(),
    user_id     uuid not null references auth.users(id) on delete cascade,
    type        text not null,
    actor_id    uuid,
    ref_id      text,
    body        text not null default '',
    read_at     timestamptz,
    created_at  timestamptz not null default now(),
    check (type in ('new_post','friend_request','message','trade_interest'))
);
create table if not exists public.rockscout_trade_interests (
    id                  uuid primary key default gen_random_uuid(),
    listing_id          uuid not null references public.rockscout_trade_listings(id) on delete cascade,
    listing_owner_id    uuid not null references auth.users(id) on delete cascade,
    interested_user_id  uuid not null references auth.users(id) on delete cascade,
    message             text not null default '',
    created_at          timestamptz not null default now(),
    unique (listing_id, interested_user_id),
    check (listing_owner_id <> interested_user_id)
);
create table if not exists public.rockscout_captures (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null default 'field-camera',
    specimen_emoji  text not null default '📷',
    custom_name     text not null default '',
    custom_location text not null default '',
    general_info    text not null default '',
    image_urls      jsonb not null default '[]'::jsonb,
    latitude        double precision,
    longitude       double precision,
    in_collection   boolean not null default false,
    in_wishlist     boolean not null default false,
    created_at      timestamptz not null default now()
);
create table if not exists public.rockscout_saved_images (
    id            uuid primary key default gen_random_uuid(),
    user_id       uuid not null references auth.users(id) on delete cascade,
    image_url     text not null,
    thumbnail_url text not null default '',
    source        text not null default 'field-camera',
    created_at    timestamptz not null default now()
);
create table if not exists public.rockscout_field_journal (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    entry_date      date not null default current_date,
    location        text not null default '',
    dig_site_id     text,
    weather_summary text not null default '',
    notes           text not null default '',
    photo_urls      jsonb not null default '[]'::jsonb,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);
create table if not exists public.rockscout_trips (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    name            text not null,
    trip_date       date not null default current_date,
    stops           jsonb not null default '[]'::jsonb,
    target_specimens jsonb not null default '[]'::jsonb,
    gear_checklist  jsonb not null default '[]'::jsonb,
    notes           text not null default '',
    is_archived     boolean not null default false,
    completed_at    timestamptz,
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now()
);
create table if not exists public.rockscout_favorite_spots (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    spot_id         text not null,
    spot_type       text not null default 'dig_site',
    name            text not null,
    region          text not null default '',
    latitude        double precision not null default 0.0,
    longitude       double precision not null default 0.0,
    created_at      timestamptz not null default now(),
    unique (user_id, spot_id)
);
create table if not exists public.rockscout_liked_specimens (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    specimen_id     text not null,
    created_at      timestamptz not null default now(),
    unique (user_id, specimen_id)
);
create table if not exists public.rockscout_aurora_saved_spots (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    spot_id         text not null,
    name            text not null,
    latitude        double precision not null,
    longitude       double precision not null,
    created_at      bigint not null default 0,
    unique (user_id, spot_id)
);
create table if not exists public.rockscout_push_subscriptions (
    id              uuid primary key default gen_random_uuid(),
    user_id         uuid not null references auth.users(id) on delete cascade,
    endpoint        text not null,
    p256dh_key      text not null,
    auth_key        text not null,
    categories      jsonb not null default '[]'::jsonb,
    platform        text not null default '',
    created_at      timestamptz not null default now(),
    updated_at      timestamptz not null default now(),
    unique (user_id, endpoint)
);
create table if not exists public.rockscout_installed_devices (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id) on delete cascade,
    device_label text not null,
    device_fingerprint text not null,
    user_agent text,
    installed_at timestamptz not null default now(),
    last_seen_at timestamptz not null default now()
);
create table if not exists public.rockscout_settings_backup (
    user_id        uuid primary key references auth.users(id) on delete cascade,
    settings_json  text not null,
    updated_at     timestamptz not null default now()
);
create table if not exists public.artifact_embeddings (
    id              bigint primary key generated always as identity,
    artifact_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (artifact_id, image_url, embedding_model)
);
create table if not exists public.rockscout_trade_listings (
    id                            uuid primary key default gen_random_uuid(),
    user_id                       uuid not null references auth.users(id) on delete cascade,
    owner_user_id                 uuid references auth.users(id) on delete cascade,
    listing_id                    text not null default '',
    type                          text not null default 'HAVE',
    listing_mode                  text not null default 'SWAP',
    price                         text not null default '',
    specimen_name                 text not null default '',
    condition                     text not null default '',
    description                   text not null default '',
    want_in_return                text not null default '',
    photo_uri                     text,
    tags                          jsonb not null default '[]'::jsonb,
    source_capture_id             text,
    source_collection_specimen_id text,
    source_wishlist_specimen_id   text,
    status                        text not null default 'active',
    owner_username                text,
    created_at                    bigint not null default 0,
    expires_at                    bigint not null default 0,
    synced_at                     timestamptz not null default now(),
    unique (user_id, listing_id),
    check (type in ('HAVE','WANT')),
    check (status in ('active','traded','expired'))
);

-- Indexes
create index if not exists idx_friend_requests_recipient
    on public.rockscout_friend_requests (recipient_id)
    where status = 'pending';
create index if not exists idx_message_requests_recipient
    on public.rockscout_message_requests (recipient_id)
    where status = 'pending';
create index if not exists idx_threads_user_a
    on public.rockscout_threads (user_a);
create index if not exists idx_threads_user_b
    on public.rockscout_threads (user_b);
create index if not exists idx_pings_user_expires
    on public.rockscout_pings (user_id, expires_at);
create index if not exists idx_posts_user_created
    on public.rockscout_posts (user_id, created_at desc);
create index if not exists idx_notifications_user_unread
    on public.rockscout_notifications (user_id)
    where read_at is null;
create index if not exists idx_trade_interests_owner
    on public.rockscout_trade_interests (listing_owner_id);
create index if not exists idx_specimen_embeddings_text_vec
    on public.specimen_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);
create index if not exists idx_specimen_embeddings_dedup
    on public.specimen_embeddings (specimen_id, image_url, embedding_model);
create index if not exists idx_specimen_catalog_name
    on public.specimen_catalog (name);
create index if not exists idx_specimen_catalog_category
    on public.specimen_catalog (category);
create index if not exists idx_specimen_catalog_rarity
    on public.specimen_catalog (rarity);
create index if not exists idx_collection_user
    on public.rockscout_collection (user_id);
create index if not exists idx_wishlist_user
    on public.rockscout_wishlist (user_id);
create index if not exists idx_field_journal_user
    on public.rockscout_field_journal (user_id);
create index if not exists idx_field_journal_date
    on public.rockscout_field_journal (user_id, entry_date desc);
create index if not exists idx_trips_user
    on public.rockscout_trips (user_id);
create index if not exists idx_trips_archived
    on public.rockscout_trips (user_id, is_archived, trip_date desc);
create index if not exists idx_favorite_spots_user
    on public.rockscout_favorite_spots (user_id);
create index if not exists idx_profiles_stripe_customer
    on public.rockscout_profiles (stripe_customer_id)
    where stripe_customer_id is not null;
create index if not exists idx_push_sub_user
    on public.rockscout_push_subscriptions (user_id);
create index if not exists idx_payment_events_type
    on public.rockscout_payment_events (event_type, processed_at desc);
create index if not exists idx_artifact_embeddings_text_vec
    on public.artifact_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);
create index if not exists idx_artifact_embeddings_dedup
    on public.artifact_embeddings (artifact_id, image_url, embedding_model);
create index if not exists idx_captures_user
    on public.rockscout_captures (user_id);
create index if not exists idx_captures_created
    on public.rockscout_captures (user_id, created_at desc);
create index if not exists idx_saved_images_user
    on public.rockscout_saved_images (user_id);
create index if not exists idx_saved_images_created
    on public.rockscout_saved_images (user_id, created_at desc);
CREATE INDEX IF NOT EXISTS rockscout_installed_devices_user_idx
  ON rockscout_installed_devices (user_id);
create index if not exists idx_collection_user
    on public.rockscout_collection (user_id);
create index if not exists idx_wishlist_user
    on public.rockscout_wishlist (user_id);
create index if not exists idx_liked_specimens_user
    on public.rockscout_liked_specimens (user_id);
create index if not exists idx_trade_listings_user
    on public.rockscout_trade_listings (user_id);
create index if not exists idx_aurora_spots_user
    on public.rockscout_aurora_saved_spots (user_id);
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
create index if not exists idx_captures_user
    on public.rockscout_captures (user_id);
create index if not exists idx_captures_created
    on public.rockscout_captures (user_id, created_at desc);
create index if not exists idx_saved_images_user
    on public.rockscout_saved_images (user_id);
create index if not exists idx_saved_images_created
    on public.rockscout_saved_images (user_id, created_at desc);
create index if not exists idx_field_journal_user
    on public.rockscout_field_journal (user_id);
create index if not exists idx_field_journal_date
    on public.rockscout_field_journal (user_id, entry_date desc);
create index if not exists idx_trips_user
    on public.rockscout_trips (user_id);
create index if not exists idx_trips_archived
    on public.rockscout_trips (user_id, is_archived, trip_date desc);
create index if not exists idx_favorite_spots_user
    on public.rockscout_favorite_spots (user_id);
create index if not exists idx_liked_specimens_user
    on public.rockscout_liked_specimens (user_id);
create index if not exists idx_aurora_spots_user
    on public.rockscout_aurora_saved_spots (user_id);
create index if not exists idx_push_sub_user
    on public.rockscout_push_subscriptions (user_id);
create index if not exists rockscout_installed_devices_user_idx
    on public.rockscout_installed_devices (user_id);
create index if not exists idx_artifact_embeddings_text_vec
    on public.artifact_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);
create index if not exists idx_artifact_embeddings_dedup
    on public.artifact_embeddings (artifact_id, image_url, embedding_model);
create index if not exists idx_trade_listings_user
    on public.rockscout_trade_listings (user_id);
create index if not exists idx_friend_requests_recipient
    on public.rockscout_friend_requests (recipient_id)
    where status = 'pending';
create index if not exists idx_message_requests_recipient
    on public.rockscout_message_requests (recipient_id)
    where status = 'pending';
create index if not exists idx_threads_user_a
    on public.rockscout_threads (user_a);
create index if not exists idx_threads_user_b
    on public.rockscout_threads (user_b);
create index if not exists idx_pings_user_expires
    on public.rockscout_pings (user_id, expires_at);
create index if not exists idx_posts_user_created
    on public.rockscout_posts (user_id, created_at desc);
create index if not exists idx_notifications_user_unread
    on public.rockscout_notifications (user_id)
    where read_at is null;
create index if not exists idx_trade_interests_owner
    on public.rockscout_trade_interests (listing_owner_id);
create index if not exists idx_captures_user_sync
    on public.rockscout_captures (user_id, sync_status)
    where sync_status != 'synced';
create index if not exists idx_saved_images_user_sync
    on public.rockscout_saved_images (user_id, sync_status)
    where sync_status != 'synced';
create index if not exists idx_field_journal_user_sync
    on public.rockscout_field_journal (user_id, sync_status)
    where sync_status != 'synced';
create index if not exists idx_trips_user_sync
    on public.rockscout_trips (user_id, sync_status)
    where sync_status != 'synced';
CREATE INDEX IF NOT EXISTS idx_group_chat_members_user ON group_chat_members(user_id);
CREATE INDEX IF NOT EXISTS idx_group_chat_members_group ON group_chat_members(group_chat_id);
CREATE INDEX IF NOT EXISTS idx_group_messages_chat ON group_messages(group_chat_id, created_at);
CREATE INDEX IF NOT EXISTS idx_group_chat_invites_invitee ON group_chat_invites(invitee_id, status);
CREATE INDEX IF NOT EXISTS idx_user_warnings_user ON user_warnings(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_chat_thread_participants_user ON chat_thread_participants(user_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_thread ON chat_messages(thread_id, created_at);
CREATE INDEX IF NOT EXISTS idx_chat_message_reads_message ON chat_message_reads(message_id);
CREATE INDEX IF NOT EXISTS idx_self_harm_warnings_user ON self_harm_warnings(user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_report_notifications_user ON report_notifications(reported_user_id, created_at);
CREATE INDEX IF NOT EXISTS idx_typing_status_chat_id ON chat_typing_status(chat_id);
CREATE INDEX IF NOT EXISTS idx_typing_status_updated_at ON chat_typing_status(updated_at DESC);

-- Foreign keys / constraints

-- RLS policies
create policy profiles_select on public.rockscout_profiles
    for select using (
        id = auth.uid()
        or not public.rockscout_is_blocked(auth.uid(), id)
    );
create policy profiles_insert_self on public.rockscout_profiles
    for insert with check (id = auth.uid());
create policy profiles_update_self on public.rockscout_profiles
    for update using (id = auth.uid()) with check (id = auth.uid());
create policy connections_select on public.rockscout_connections
    for select using (
        user_a = auth.uid() or user_b = auth.uid()
    );
create policy connections_insert on public.rockscout_connections
    for insert with check (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
create policy connections_delete on public.rockscout_connections
    for delete using (user_a = auth.uid() or user_b = auth.uid());
create policy msg_requests_select on public.rockscout_message_requests
    for select using (
        sender_id = auth.uid() or recipient_id = auth.uid()
    );
create policy msg_requests_insert on public.rockscout_message_requests
    for insert with check (
        sender_id = auth.uid()
        and not public.rockscout_is_blocked(sender_id, recipient_id)
    );
create policy msg_requests_update on public.rockscout_message_requests
    for update using (recipient_id = auth.uid()) with check (recipient_id = auth.uid());
create policy friend_requests_select on public.rockscout_friend_requests
    for select using (
        sender_id = auth.uid() or recipient_id = auth.uid()
    );
create policy friend_requests_insert on public.rockscout_friend_requests
    for insert with check (
        sender_id = auth.uid()
        and not public.rockscout_is_blocked(sender_id, recipient_id)
    );
create policy friend_requests_update on public.rockscout_friend_requests
    for update using (recipient_id = auth.uid()) with check (recipient_id = auth.uid());
create policy blocks_select on public.rockscout_blocks
    for select using (blocker_id = auth.uid() or blocked_id = auth.uid());
create policy blocks_insert on public.rockscout_blocks
    for insert with check (blocker_id = auth.uid());
create policy blocks_delete on public.rockscout_blocks
    for delete using (blocker_id = auth.uid());
create policy threads_select on public.rockscout_threads
    for select using (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
create policy threads_insert on public.rockscout_threads
    for insert with check (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
create policy threads_update on public.rockscout_threads
    for update using (user_a = auth.uid() or user_b = auth.uid());
create policy messages_select on public.rockscout_messages
    for select using (
        exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
              and not public.rockscout_is_blocked(t.user_a, t.user_b)
        )
    );
create policy messages_insert on public.rockscout_messages
    for insert with check (
        sender_id = auth.uid()
        and exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
              and not public.rockscout_is_blocked(t.user_a, t.user_b)
        )
    );
create policy messages_update on public.rockscout_messages
    for update using (
        exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
        )
    );
create policy pings_select on public.rockscout_pings
    for select using (
        user_id = auth.uid()
        or (
            public.rockscout_are_connected(auth.uid(), user_id)
            and not public.rockscout_is_blocked(auth.uid(), user_id)
        )
    );
create policy pings_insert on public.rockscout_pings
    for insert with check (user_id = auth.uid());
create policy pings_delete on public.rockscout_pings
    for delete using (user_id = auth.uid());
create policy posts_select on public.rockscout_posts
    for select using (
        user_id = auth.uid()
        or not public.rockscout_is_blocked(auth.uid(), user_id)
    );
create policy posts_insert on public.rockscout_posts
    for insert with check (user_id = auth.uid());
create policy posts_delete on public.rockscout_posts
    for delete using (user_id = auth.uid());
create policy likes_select on public.rockscout_post_likes
    for select using (
        exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and (p.user_id = auth.uid() or not public.rockscout_is_blocked(auth.uid(), p.user_id))
        )
    );
create policy likes_insert on public.rockscout_post_likes
    for insert with check (
        user_id = auth.uid()
        and exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and not public.rockscout_is_blocked(auth.uid(), p.user_id)
        )
    );
create policy likes_delete on public.rockscout_post_likes
    for delete using (user_id = auth.uid());
create policy comments_select on public.rockscout_post_comments
    for select using (
        exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and (p.user_id = auth.uid() or not public.rockscout_is_blocked(auth.uid(), p.user_id))
        )
    );
create policy comments_insert on public.rockscout_post_comments
    for insert with check (
        user_id = auth.uid()
        and exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and public.rockscout_are_connected(auth.uid(), p.user_id)
              and not public.rockscout_is_blocked(auth.uid(), p.user_id)
        )
    );
create policy comments_delete on public.rockscout_post_comments
    for delete using (user_id = auth.uid());
create policy notifs_select on public.rockscout_notifications
    for select using (user_id = auth.uid());
create policy notifs_insert on public.rockscout_notifications
    for insert with check (
        user_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), user_id)
    );
create policy notifs_update on public.rockscout_notifications
    for update using (user_id = auth.uid());
create policy trade_listings_select on public.rockscout_trade_listings
    for select using (
        owner_user_id = auth.uid()
        or not public.rockscout_is_blocked(auth.uid(), owner_user_id)
    );
create policy trade_listings_insert on public.rockscout_trade_listings
    for insert with check (owner_user_id = auth.uid());
create policy trade_listings_update on public.rockscout_trade_listings
    for update using (owner_user_id = auth.uid());
create policy trade_listings_delete on public.rockscout_trade_listings
    for delete using (owner_user_id = auth.uid());
create policy trade_interests_select on public.rockscout_trade_interests
    for select using (
        interested_user_id = auth.uid()
        or listing_owner_id = auth.uid()
    );
create policy trade_interests_insert on public.rockscout_trade_interests
    for insert with check (
        interested_user_id = auth.uid()
        and listing_owner_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), listing_owner_id)
    );
create policy trade_interests_delete on public.rockscout_trade_interests
    for delete using (listing_owner_id = auth.uid());
create policy notifs_insert on public.rockscout_notifications
    for insert with check (
        actor_id = auth.uid()
        and user_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), user_id)
    );
create policy blocks_select on public.rockscout_blocks
    for select using (blocker_id = auth.uid());
create policy collection_select on public.rockscout_collection
    for select using (auth.uid() = user_id);
create policy collection_insert on public.rockscout_collection
    for insert with check (auth.uid() = user_id);
create policy collection_delete on public.rockscout_collection
    for delete using (auth.uid() = user_id);
create policy collection_update on public.rockscout_collection
    for update using (auth.uid() = user_id);
create policy wishlist_select on public.rockscout_wishlist
    for select using (auth.uid() = user_id);
create policy wishlist_insert on public.rockscout_wishlist
    for insert with check (auth.uid() = user_id);
create policy wishlist_delete on public.rockscout_wishlist
    for delete using (auth.uid() = user_id);
create policy field_journal_select on public.rockscout_field_journal
    for select using (auth.uid() = user_id);
create policy field_journal_insert on public.rockscout_field_journal
    for insert with check (auth.uid() = user_id);
create policy field_journal_update on public.rockscout_field_journal
    for update using (auth.uid() = user_id);
create policy field_journal_delete on public.rockscout_field_journal
    for delete using (auth.uid() = user_id);
create policy trips_select on public.rockscout_trips
    for select using (auth.uid() = user_id);
create policy trips_insert on public.rockscout_trips
    for insert with check (auth.uid() = user_id);
create policy trips_update on public.rockscout_trips
    for update using (auth.uid() = user_id);
create policy trips_delete on public.rockscout_trips
    for delete using (auth.uid() = user_id);
create policy favorite_spots_select on public.rockscout_favorite_spots
    for select using (auth.uid() = user_id);
create policy favorite_spots_insert on public.rockscout_favorite_spots
    for insert with check (auth.uid() = user_id);
create policy favorite_spots_delete on public.rockscout_favorite_spots
    for delete using (auth.uid() = user_id);
create policy push_sub_select on public.rockscout_push_subscriptions
    for select using (auth.uid() = user_id);
create policy push_sub_insert on public.rockscout_push_subscriptions
    for insert with check (auth.uid() = user_id);
create policy push_sub_delete on public.rockscout_push_subscriptions
    for delete using (auth.uid() = user_id);
create policy captures_select on public.rockscout_captures
    for select using (auth.uid() = user_id);
create policy captures_insert on public.rockscout_captures
    for insert with check (auth.uid() = user_id);
create policy captures_update on public.rockscout_captures
    for update using (auth.uid() = user_id);
create policy captures_delete on public.rockscout_captures
    for delete using (auth.uid() = user_id);
create policy saved_images_select on public.rockscout_saved_images
    for select using (auth.uid() = user_id);
create policy saved_images_insert on public.rockscout_saved_images
    for insert with check (auth.uid() = user_id);
create policy saved_images_delete on public.rockscout_saved_images
    for delete using (auth.uid() = user_id);
create policy collection_select on public.rockscout_collection
    for select using (auth.uid() = user_id);
create policy collection_insert on public.rockscout_collection
    for insert with check (auth.uid() = user_id);
create policy collection_update on public.rockscout_collection
    for update using (auth.uid() = user_id);
create policy collection_delete on public.rockscout_collection
    for delete using (auth.uid() = user_id);
create policy wishlist_select on public.rockscout_wishlist
    for select using (auth.uid() = user_id);
create policy wishlist_insert on public.rockscout_wishlist
    for insert with check (auth.uid() = user_id);
create policy wishlist_delete on public.rockscout_wishlist
    for delete using (auth.uid() = user_id);
create policy liked_specimens_select on public.rockscout_liked_specimens
    for select using (auth.uid() = user_id);
create policy liked_specimens_insert on public.rockscout_liked_specimens
    for insert with check (auth.uid() = user_id);
create policy liked_specimens_delete on public.rockscout_liked_specimens
    for delete using (auth.uid() = user_id);
create policy trade_listings_select on public.rockscout_trade_listings
    for select using (auth.uid() = user_id);
create policy trade_listings_insert on public.rockscout_trade_listings
    for insert with check (auth.uid() = user_id);
create policy trade_listings_update on public.rockscout_trade_listings
    for update using (auth.uid() = user_id);
create policy trade_listings_delete on public.rockscout_trade_listings
    for delete using (auth.uid() = user_id);
create policy aurora_spots_select on public.rockscout_aurora_saved_spots
    for select using (auth.uid() = user_id);
create policy aurora_spots_insert on public.rockscout_aurora_saved_spots
    for insert with check (auth.uid() = user_id);
create policy aurora_spots_delete on public.rockscout_aurora_saved_spots
    for delete using (auth.uid() = user_id);
CREATE POLICY settings_backup_select ON public.rockscout_settings_backup
    FOR SELECT TO authenticated USING (auth.uid() = user_id);
create policy profiles_select on public.rockscout_profiles
    for select using (
        id = auth.uid()
        or not public.rockscout_is_blocked(auth.uid(), id)
    );
create policy profiles_insert_self on public.rockscout_profiles
    for insert with check (id = auth.uid());
create policy profiles_update_self on public.rockscout_profiles
    for update using (id = auth.uid()) with check (id = auth.uid());
create policy captures_select on public.rockscout_captures
    for select using (auth.uid() = user_id);
create policy captures_insert on public.rockscout_captures
    for insert with check (auth.uid() = user_id);
create policy captures_update on public.rockscout_captures
    for update using (auth.uid() = user_id);
create policy captures_delete on public.rockscout_captures
    for delete using (auth.uid() = user_id);
create policy saved_images_select on public.rockscout_saved_images
    for select using (auth.uid() = user_id);
create policy saved_images_insert on public.rockscout_saved_images
    for insert with check (auth.uid() = user_id);
create policy saved_images_delete on public.rockscout_saved_images
    for delete using (auth.uid() = user_id);
create policy field_journal_select on public.rockscout_field_journal
    for select using (auth.uid() = user_id);
create policy field_journal_insert on public.rockscout_field_journal
    for insert with check (auth.uid() = user_id);
create policy field_journal_update on public.rockscout_field_journal
    for update using (auth.uid() = user_id);
create policy field_journal_delete on public.rockscout_field_journal
    for delete using (auth.uid() = user_id);
create policy trips_select on public.rockscout_trips
    for select using (auth.uid() = user_id);
create policy trips_insert on public.rockscout_trips
    for insert with check (auth.uid() = user_id);
create policy trips_update on public.rockscout_trips
    for update using (auth.uid() = user_id);
create policy trips_delete on public.rockscout_trips
    for delete using (auth.uid() = user_id);
create policy favorite_spots_select on public.rockscout_favorite_spots
    for select using (auth.uid() = user_id);
create policy favorite_spots_insert on public.rockscout_favorite_spots
    for insert with check (auth.uid() = user_id);
create policy favorite_spots_delete on public.rockscout_favorite_spots
    for delete using (auth.uid() = user_id);
create policy liked_specimens_select on public.rockscout_liked_specimens
    for select using (auth.uid() = user_id);
create policy liked_specimens_insert on public.rockscout_liked_specimens
    for insert with check (auth.uid() = user_id);
create policy liked_specimens_delete on public.rockscout_liked_specimens
    for delete using (auth.uid() = user_id);
create policy aurora_spots_select on public.rockscout_aurora_saved_spots
    for select using (auth.uid() = user_id);
create policy aurora_spots_insert on public.rockscout_aurora_saved_spots
    for insert with check (auth.uid() = user_id);
create policy aurora_spots_delete on public.rockscout_aurora_saved_spots
    for delete using (auth.uid() = user_id);
create policy push_sub_select on public.rockscout_push_subscriptions
    for select using (auth.uid() = user_id);
create policy push_sub_insert on public.rockscout_push_subscriptions
    for insert with check (auth.uid() = user_id);
create policy push_sub_delete on public.rockscout_push_subscriptions
    for delete using (auth.uid() = user_id);
create policy "Users can read own devices"
    on public.rockscout_installed_devices for select to authenticated
    using (auth.uid() = user_id);
create policy "Users can insert own devices"
    on public.rockscout_installed_devices for insert to authenticated
    with check (auth.uid() = user_id);
create policy "Users can update own devices"
    on public.rockscout_installed_devices for update to authenticated
    using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users can delete own devices"
    on public.rockscout_installed_devices for delete to authenticated
    using (auth.uid() = user_id);
create policy settings_backup_select on public.rockscout_settings_backup
    for select to authenticated using (auth.uid() = user_id);
create policy trade_listings_select on public.rockscout_trade_listings
    for select using (
        user_id = auth.uid()
        or (
            owner_user_id is not null
            and not public.rockscout_is_blocked(auth.uid(), owner_user_id)
        )
    );
create policy trade_listings_insert on public.rockscout_trade_listings
    for insert with check (user_id = auth.uid());
create policy trade_listings_update on public.rockscout_trade_listings
    for update using (user_id = auth.uid());
create policy trade_listings_delete on public.rockscout_trade_listings
    for delete using (user_id = auth.uid());
create policy connections_select on public.rockscout_connections
    for select using (user_a = auth.uid() or user_b = auth.uid());
create policy connections_insert on public.rockscout_connections
    for insert with check (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
create policy connections_delete on public.rockscout_connections
    for delete using (user_a = auth.uid() or user_b = auth.uid());
create policy msg_requests_select on public.rockscout_message_requests
    for select using (sender_id = auth.uid() or recipient_id = auth.uid());
create policy msg_requests_insert on public.rockscout_message_requests
    for insert with check (
        sender_id = auth.uid()
        and not public.rockscout_is_blocked(sender_id, recipient_id)
    );
create policy msg_requests_update on public.rockscout_message_requests
    for update using (recipient_id = auth.uid()) with check (recipient_id = auth.uid());
create policy friend_requests_select on public.rockscout_friend_requests
    for select using (sender_id = auth.uid() or recipient_id = auth.uid());
create policy friend_requests_insert on public.rockscout_friend_requests
    for insert with check (
        sender_id = auth.uid()
        and not public.rockscout_is_blocked(sender_id, recipient_id)
    );
create policy friend_requests_update on public.rockscout_friend_requests
    for update using (recipient_id = auth.uid()) with check (recipient_id = auth.uid());
create policy blocks_select on public.rockscout_blocks
    for select using (blocker_id = auth.uid());
create policy blocks_insert on public.rockscout_blocks
    for insert with check (blocker_id = auth.uid());
create policy blocks_delete on public.rockscout_blocks
    for delete using (blocker_id = auth.uid());
create policy threads_select on public.rockscout_threads
    for select using (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
create policy threads_insert on public.rockscout_threads
    for insert with check (
        (user_a = auth.uid() or user_b = auth.uid())
        and not public.rockscout_is_blocked(user_a, user_b)
    );
create policy threads_update on public.rockscout_threads
    for update using (user_a = auth.uid() or user_b = auth.uid());
create policy messages_select on public.rockscout_messages
    for select using (
        exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
              and not public.rockscout_is_blocked(t.user_a, t.user_b)
        )
    );
create policy messages_insert on public.rockscout_messages
    for insert with check (
        sender_id = auth.uid()
        and exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
              and not public.rockscout_is_blocked(t.user_a, t.user_b)
        )
    );
create policy messages_update on public.rockscout_messages
    for update using (
        exists (
            select 1 from public.rockscout_threads t
            where t.id = rockscout_messages.thread_id
              and (t.user_a = auth.uid() or t.user_b = auth.uid())
        )
    );
create policy pings_select on public.rockscout_pings
    for select using (
        user_id = auth.uid()
        or (
            public.rockscout_are_connected(auth.uid(), user_id)
            and not public.rockscout_is_blocked(auth.uid(), user_id)
        )
    );
create policy pings_insert on public.rockscout_pings
    for insert with check (user_id = auth.uid());
create policy pings_delete on public.rockscout_pings
    for delete using (user_id = auth.uid());
create policy posts_select on public.rockscout_posts
    for select using (
        user_id = auth.uid()
        or not public.rockscout_is_blocked(auth.uid(), user_id)
    );
create policy posts_insert on public.rockscout_posts
    for insert with check (user_id = auth.uid());
create policy posts_delete on public.rockscout_posts
    for delete using (user_id = auth.uid());
create policy likes_select on public.rockscout_post_likes
    for select using (
        exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and (p.user_id = auth.uid() or not public.rockscout_is_blocked(auth.uid(), p.user_id))
        )
    );
create policy likes_insert on public.rockscout_post_likes
    for insert with check (
        user_id = auth.uid()
        and exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and not public.rockscout_is_blocked(auth.uid(), p.user_id)
        )
    );
create policy likes_delete on public.rockscout_post_likes
    for delete using (user_id = auth.uid());
create policy comments_select on public.rockscout_post_comments
    for select using (
        exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and (p.user_id = auth.uid() or not public.rockscout_is_blocked(auth.uid(), p.user_id))
        )
    );
create policy comments_insert on public.rockscout_post_comments
    for insert with check (
        user_id = auth.uid()
        and exists (
            select 1 from public.rockscout_posts p
            where p.id = post_id
              and public.rockscout_are_connected(auth.uid(), p.user_id)
              and not public.rockscout_is_blocked(auth.uid(), p.user_id)
        )
    );
create policy comments_delete on public.rockscout_post_comments
    for delete using (user_id = auth.uid());
create policy notifs_select on public.rockscout_notifications
    for select using (user_id = auth.uid());
create policy notifs_insert on public.rockscout_notifications
    for insert with check (
        actor_id = auth.uid()
        and user_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), user_id)
    );
create policy notifs_update on public.rockscout_notifications
    for update using (user_id = auth.uid());
create policy trade_interests_select on public.rockscout_trade_interests
    for select using (
        interested_user_id = auth.uid()
        or listing_owner_id = auth.uid()
    );
create policy trade_interests_insert on public.rockscout_trade_interests
    for insert with check (
        interested_user_id = auth.uid()
        and listing_owner_id <> auth.uid()
        and not public.rockscout_is_blocked(auth.uid(), listing_owner_id)
    );
create policy trade_interests_delete on public.rockscout_trade_interests
    for delete using (listing_owner_id = auth.uid());

-- Functions, triggers, grants
create or replace function public.rockscout_is_blocked(a uuid, b uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
    select exists (
        select 1 from public.rockscout_blocks
        where (blocker_id = a and blocked_id = b)
           or (blocker_id = b and blocked_id = a)
    );
$$;
create or replace function public.rockscout_are_connected(a uuid, b uuid)
returns boolean
language sql
stable
security definer
set search_path = public
as $$
    select exists (
        select 1 from public.rockscout_connections
        where (user_a = a and user_b = b)
           or (user_a = b and user_b = a)
    );
$$;
create or replace function public.rockscout_enforce_five_posts()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
    if (select count(*) from public.rockscout_posts where user_id = new.user_id) >= 5 then
        delete from public.rockscout_posts
        where id in (
            select id from public.rockscout_posts
            where user_id = new.user_id
            order by created_at asc
            limit 1
        );
    end if;
    return new;
end;
$$;
drop trigger if exists trg_enforce_five_posts on public.rockscout_posts;
create trigger trg_enforce_five_posts
    before insert on public.rockscout_posts
    for each row execute function public.rockscout_enforce_five_posts();
alter table public.rockscout_profiles          enable row level security;
alter table public.rockscout_connections       enable row level security;
alter table public.rockscout_message_requests  enable row level security;
alter table public.rockscout_friend_requests   enable row level security;
alter table public.rockscout_blocks            enable row level security;
alter table public.rockscout_threads           enable row level security;
alter table public.rockscout_messages          enable row level security;
alter table public.rockscout_pings             enable row level security;
alter table public.rockscout_posts             enable row level security;
alter table public.rockscout_post_likes        enable row level security;
alter table public.rockscout_post_comments     enable row level security;
alter table public.rockscout_notifications     enable row level security;
alter table public.rockscout_trade_listings    enable row level security;
alter table public.rockscout_trade_interests   enable row level security;
drop policy if exists profiles_select on public.rockscout_profiles;
drop policy if exists profiles_insert_self on public.rockscout_profiles;
drop policy if exists profiles_update_self on public.rockscout_profiles;
drop policy if exists connections_select on public.rockscout_connections;
drop policy if exists connections_insert on public.rockscout_connections;
drop policy if exists connections_delete on public.rockscout_connections;
drop policy if exists msg_requests_select on public.rockscout_message_requests;
drop policy if exists msg_requests_insert on public.rockscout_message_requests;
drop policy if exists msg_requests_update on public.rockscout_message_requests;
drop policy if exists friend_requests_select on public.rockscout_friend_requests;
drop policy if exists friend_requests_insert on public.rockscout_friend_requests;
drop policy if exists friend_requests_update on public.rockscout_friend_requests;
drop policy if exists blocks_select on public.rockscout_blocks;
drop policy if exists blocks_insert on public.rockscout_blocks;
drop policy if exists blocks_delete on public.rockscout_blocks;
drop policy if exists threads_select on public.rockscout_threads;
drop policy if exists threads_insert on public.rockscout_threads;
drop policy if exists threads_update on public.rockscout_threads;
drop policy if exists messages_select on public.rockscout_messages;
drop policy if exists messages_insert on public.rockscout_messages;
drop policy if exists messages_update on public.rockscout_messages;
drop policy if exists pings_select on public.rockscout_pings;
drop policy if exists pings_insert on public.rockscout_pings;
drop policy if exists pings_delete on public.rockscout_pings;
drop policy if exists posts_select on public.rockscout_posts;
drop policy if exists posts_insert on public.rockscout_posts;
drop policy if exists posts_delete on public.rockscout_posts;
drop policy if exists likes_select on public.rockscout_post_likes;
drop policy if exists likes_insert on public.rockscout_post_likes;
drop policy if exists likes_delete on public.rockscout_post_likes;
drop policy if exists comments_select on public.rockscout_post_comments;
drop policy if exists comments_insert on public.rockscout_post_comments;
drop policy if exists comments_delete on public.rockscout_post_comments;
drop policy if exists notifs_select on public.rockscout_notifications;
drop policy if exists notifs_insert on public.rockscout_notifications;
drop policy if exists notifs_update on public.rockscout_notifications;
drop policy if exists trade_listings_select on public.rockscout_trade_listings;
drop policy if exists trade_listings_insert on public.rockscout_trade_listings;
drop policy if exists trade_listings_update on public.rockscout_trade_listings;
drop policy if exists trade_listings_delete on public.rockscout_trade_listings;
drop policy if exists trade_interests_select on public.rockscout_trade_interests;
drop policy if exists trade_interests_insert on public.rockscout_trade_interests;
drop policy if exists trade_interests_delete on public.rockscout_trade_interests;
drop policy if exists notifs_insert on public.rockscout_notifications;
drop policy if exists blocks_select on public.rockscout_blocks;
create extension if not exists vector with schema extensions;
alter table public.specimen_embeddings disable row level security;
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
grant select on public.specimen_embeddings to anon;
grant execute on function public.match_specimen_embeddings(vector(1536), int) to anon;
alter table public.specimen_catalog disable row level security;
grant select on public.specimen_catalog to anon;
alter table public.rockscout_collection enable row level security;
drop policy if exists collection_select on public.rockscout_collection;
drop policy if exists collection_insert on public.rockscout_collection;
drop policy if exists collection_delete on public.rockscout_collection;
drop policy if exists collection_update on public.rockscout_collection;
grant select, insert, update, delete on public.rockscout_collection to anon;
alter table public.rockscout_wishlist enable row level security;
drop policy if exists wishlist_select on public.rockscout_wishlist;
drop policy if exists wishlist_insert on public.rockscout_wishlist;
drop policy if exists wishlist_delete on public.rockscout_wishlist;
grant select, insert, delete on public.rockscout_wishlist to anon;
alter table public.rockscout_field_journal enable row level security;
drop policy if exists field_journal_select on public.rockscout_field_journal;
drop policy if exists field_journal_insert on public.rockscout_field_journal;
drop policy if exists field_journal_update on public.rockscout_field_journal;
drop policy if exists field_journal_delete on public.rockscout_field_journal;
grant select, insert, update, delete on public.rockscout_field_journal to anon;
alter table public.rockscout_trips enable row level security;
drop policy if exists trips_select on public.rockscout_trips;
drop policy if exists trips_insert on public.rockscout_trips;
drop policy if exists trips_update on public.rockscout_trips;
drop policy if exists trips_delete on public.rockscout_trips;
grant select, insert, update, delete on public.rockscout_trips to anon;
alter table public.rockscout_favorite_spots enable row level security;
drop policy if exists favorite_spots_select on public.rockscout_favorite_spots;
drop policy if exists favorite_spots_insert on public.rockscout_favorite_spots;
drop policy if exists favorite_spots_delete on public.rockscout_favorite_spots;
grant select, insert, delete on public.rockscout_favorite_spots to anon;
alter table public.rockscout_profiles
    add column if not exists tokens          int not null default 0,
    add column if not exists unlock_until    timestamptz,
    add column if not exists stripe_customer_id text;
grant update (tokens, unlock_until, stripe_customer_id)
    on public.rockscout_profiles to authenticated;
grant select (tokens, unlock_until)
    on public.rockscout_profiles to authenticated;
alter table public.rockscout_push_subscriptions enable row level security;
drop policy if exists push_sub_select on public.rockscout_push_subscriptions;
drop policy if exists push_sub_insert on public.rockscout_push_subscriptions;
drop policy if exists push_sub_delete on public.rockscout_push_subscriptions;
grant select, insert, delete on public.rockscout_push_subscriptions to authenticated;
alter table public.artifact_embeddings disable row level security;
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
grant select on public.artifact_embeddings to anon;
grant execute on function public.match_artifact_embeddings(vector(1536), int) to anon;
alter table public.rockscout_captures enable row level security;
drop policy if exists captures_select on public.rockscout_captures;
drop policy if exists captures_insert on public.rockscout_captures;
drop policy if exists captures_update on public.rockscout_captures;
drop policy if exists captures_delete on public.rockscout_captures;
grant select, insert, update, delete on public.rockscout_captures to anon;
alter table public.rockscout_saved_images enable row level security;
drop policy if exists saved_images_select on public.rockscout_saved_images;
drop policy if exists saved_images_insert on public.rockscout_saved_images;
drop policy if exists saved_images_delete on public.rockscout_saved_images;
grant select, insert, delete on public.rockscout_saved_images to anon;
CREATE TABLE IF NOT EXISTS rockscout_installed_devices (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  device_label text NOT NULL,
  device_fingerprint text NOT NULL,
  user_agent text,
  installed_at timestamptz NOT NULL DEFAULT now(),
  last_seen_at timestamptz NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX IF NOT EXISTS rockscout_installed_devices_user_fp_key
  ON rockscout_installed_devices (user_id, device_fingerprint);
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
drop table if exists public.rockscout_payment_events cascade;
drop index if exists idx_payment_events_type;
alter table public.rockscout_profiles
    drop column if exists stripe_customer_id;
drop index if exists idx_profiles_stripe_customer;
alter table public.rockscout_collection enable row level security;
drop policy if exists collection_select on public.rockscout_collection;
drop policy if exists collection_insert on public.rockscout_collection;
drop policy if exists collection_update on public.rockscout_collection;
drop policy if exists collection_delete on public.rockscout_collection;
grant select, insert, update, delete on public.rockscout_collection to authenticated;
alter table public.rockscout_wishlist enable row level security;
drop policy if exists wishlist_select on public.rockscout_wishlist;
drop policy if exists wishlist_insert on public.rockscout_wishlist;
drop policy if exists wishlist_delete on public.rockscout_wishlist;
grant select, insert, delete on public.rockscout_wishlist to authenticated;
alter table public.rockscout_liked_specimens enable row level security;
drop policy if exists liked_specimens_select on public.rockscout_liked_specimens;
drop policy if exists liked_specimens_insert on public.rockscout_liked_specimens;
drop policy if exists liked_specimens_delete on public.rockscout_liked_specimens;
grant select, insert, delete on public.rockscout_liked_specimens to authenticated;
alter table public.rockscout_trade_listings enable row level security;
drop policy if exists trade_listings_select on public.rockscout_trade_listings;
drop policy if exists trade_listings_insert on public.rockscout_trade_listings;
drop policy if exists trade_listings_update on public.rockscout_trade_listings;
drop policy if exists trade_listings_delete on public.rockscout_trade_listings;
grant select, insert, update, delete on public.rockscout_trade_listings to authenticated;
alter table public.rockscout_aurora_saved_spots enable row level security;
drop policy if exists aurora_spots_select on public.rockscout_aurora_saved_spots;
drop policy if exists aurora_spots_insert on public.rockscout_aurora_saved_spots;
drop policy if exists aurora_spots_delete on public.rockscout_aurora_saved_spots;
grant select, insert, delete on public.rockscout_aurora_saved_spots to authenticated;
ALTER TABLE public.rockscout_settings_backup ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS settings_backup_select ON public.rockscout_settings_backup;
GRANT SELECT ON public.rockscout_settings_backup TO authenticated;
CREATE EXTENSION IF NOT EXISTS pg_cron;
CREATE TABLE IF NOT EXISTS rockscout_error_logs (
  id bigserial PRIMARY KEY,
  created_at timestamptz NOT NULL DEFAULT now(),
  platform text NOT NULL,              -- 'android' | 'ios' | 'web'
  app_version text,
  os_version text,
  device_model text,                   -- model or userAgent
  user_id uuid,                        -- nullable — not all errors happen post-auth
  error_type text NOT NULL,            -- e.g. 'NullPointerException', 'TypeError'
  error_message text NOT NULL,
  stack_trace text,                    -- truncated to ~8000 chars on the client
  is_fatal boolean NOT NULL DEFAULT false,
  screen text,                         -- screen name / route
  breadcrumb text,                     -- last user action before the error
  auto_healed boolean NOT NULL DEFAULT false,
  heal_action text,                    -- what was done, e.g. 'cleared_cache', 'retried'
  error_fingerprint text NOT NULL      -- SHA-256 of type+message+screen (first 16 chars)
);
DO $$
BEGIN
  PERFORM cron.unschedule('cleanup-old-error-logs');
EXCEPTION WHEN OTHERS THEN NULL;
END $$;
SELECT cron.schedule(
  'cleanup-old-error-logs',
  '0 3 * * *',
  $$DELETE FROM rockscout_error_logs WHERE created_at < now() - interval '90 days'$$
);
create extension if not exists vector with schema extensions;
create extension if not exists pg_cron;
create or replace function public.rockscout_is_blocked(a uuid, b uuid)
returns boolean language sql stable security definer set search_path = public as $$
    select exists (
        select 1 from public.rockscout_blocks
        where (blocker_id = a and blocked_id = b)
           or (blocker_id = b and blocked_id = a)
    );
$$;
create or replace function public.rockscout_are_connected(a uuid, b uuid)
returns boolean language sql stable security definer set search_path = public as $$
    select exists (
        select 1 from public.rockscout_connections
        where (user_a = a and user_b = b)
           or (user_a = b and user_b = a)
    );
$$;
alter table public.rockscout_profiles enable row level security;
drop policy if exists profiles_select on public.rockscout_profiles;
drop policy if exists profiles_insert_self on public.rockscout_profiles;
drop policy if exists profiles_update_self on public.rockscout_profiles;
grant select, insert, update on public.rockscout_profiles to anon, authenticated;
grant update (tokens, unlock_until) on public.rockscout_profiles to authenticated;
grant select (tokens, unlock_until) on public.rockscout_profiles to authenticated;
create or replace function public.rockscout_enforce_five_posts()
returns trigger language plpgsql security definer set search_path = public as $$
begin
    if (select count(*) from public.rockscout_posts where user_id = new.user_id) >= 5 then
        delete from public.rockscout_posts
        where id in (
            select id from public.rockscout_posts
            where user_id = new.user_id
            order by created_at asc
            limit 1
        );
    end if;
    return new;
end;
$$;
drop trigger if exists trg_enforce_five_posts on public.rockscout_posts;
create trigger trg_enforce_five_posts
    before insert on public.rockscout_posts
    for each row execute function public.rockscout_enforce_five_posts();
alter table public.rockscout_captures enable row level security;
drop policy if exists captures_select on public.rockscout_captures;
drop policy if exists captures_insert on public.rockscout_captures;
drop policy if exists captures_update on public.rockscout_captures;
drop policy if exists captures_delete on public.rockscout_captures;
grant select, insert, update, delete on public.rockscout_captures to anon, authenticated;
alter table public.rockscout_saved_images enable row level security;
drop policy if exists saved_images_select on public.rockscout_saved_images;
drop policy if exists saved_images_insert on public.rockscout_saved_images;
drop policy if exists saved_images_delete on public.rockscout_saved_images;
grant select, insert, delete on public.rockscout_saved_images to anon, authenticated;
alter table public.rockscout_field_journal enable row level security;
drop policy if exists field_journal_select on public.rockscout_field_journal;
drop policy if exists field_journal_insert on public.rockscout_field_journal;
drop policy if exists field_journal_update on public.rockscout_field_journal;
drop policy if exists field_journal_delete on public.rockscout_field_journal;
grant select, insert, update, delete on public.rockscout_field_journal to anon, authenticated;
alter table public.rockscout_trips enable row level security;
drop policy if exists trips_select on public.rockscout_trips;
drop policy if exists trips_insert on public.rockscout_trips;
drop policy if exists trips_update on public.rockscout_trips;
drop policy if exists trips_delete on public.rockscout_trips;
grant select, insert, update, delete on public.rockscout_trips to anon, authenticated;
alter table public.rockscout_favorite_spots enable row level security;
drop policy if exists favorite_spots_select on public.rockscout_favorite_spots;
drop policy if exists favorite_spots_insert on public.rockscout_favorite_spots;
drop policy if exists favorite_spots_delete on public.rockscout_favorite_spots;
grant select, insert, delete on public.rockscout_favorite_spots to anon, authenticated;
alter table public.rockscout_liked_specimens enable row level security;
drop policy if exists liked_specimens_select on public.rockscout_liked_specimens;
drop policy if exists liked_specimens_insert on public.rockscout_liked_specimens;
drop policy if exists liked_specimens_delete on public.rockscout_liked_specimens;
grant select, insert, delete on public.rockscout_liked_specimens to anon, authenticated;
alter table public.rockscout_aurora_saved_spots enable row level security;
drop policy if exists aurora_spots_select on public.rockscout_aurora_saved_spots;
drop policy if exists aurora_spots_insert on public.rockscout_aurora_saved_spots;
drop policy if exists aurora_spots_delete on public.rockscout_aurora_saved_spots;
grant select, insert, delete on public.rockscout_aurora_saved_spots to anon, authenticated;
alter table public.rockscout_push_subscriptions enable row level security;
drop policy if exists push_sub_select on public.rockscout_push_subscriptions;
drop policy if exists push_sub_insert on public.rockscout_push_subscriptions;
drop policy if exists push_sub_delete on public.rockscout_push_subscriptions;
grant select, insert, delete on public.rockscout_push_subscriptions to authenticated;
create unique index if not exists rockscout_installed_devices_user_fp_key
    on public.rockscout_installed_devices (user_id, device_fingerprint);
alter table public.rockscout_installed_devices enable row level security;
drop policy if exists "Users can read own devices" on public.rockscout_installed_devices;
drop policy if exists "Users can insert own devices" on public.rockscout_installed_devices;
drop policy if exists "Users can update own devices" on public.rockscout_installed_devices;
drop policy if exists "Users can delete own devices" on public.rockscout_installed_devices;
alter table public.rockscout_settings_backup enable row level security;
drop policy if exists settings_backup_select on public.rockscout_settings_backup;
grant select on public.rockscout_settings_backup to authenticated;
alter table public.artifact_embeddings disable row level security;
grant select on public.artifact_embeddings to anon;
create or replace function public.match_artifact_embeddings(
    query_embedding vector(1536),
    match_count     int default 25
)
returns table (artifact_id text, max_similarity float)
language sql stable as $$
    select
        e.artifact_id,
        max(1 - (e.text_embedding <=> query_embedding))::float as max_similarity
    from public.artifact_embeddings e
    where e.text_embedding is not null
    group by e.artifact_id
    order by max(1 - (e.text_embedding <=> query_embedding)) desc
    limit match_count;
$$;
grant execute on function public.match_artifact_embeddings(vector(1536), int) to anon;
create or replace function public.rockscout_set_owner_user_id()
returns trigger language plpgsql as $$
begin
    if new.owner_user_id is null then
        new.owner_user_id := new.user_id;
    end if;
    return new;
end;
$$;
drop trigger if exists trg_set_owner_user_id on public.rockscout_trade_listings;
create trigger trg_set_owner_user_id
    before insert on public.rockscout_trade_listings
    for each row execute function public.rockscout_set_owner_user_id();
alter table public.rockscout_trade_listings enable row level security;
drop policy if exists trade_listings_select on public.rockscout_trade_listings;
drop policy if exists trade_listings_insert on public.rockscout_trade_listings;
drop policy if exists trade_listings_update on public.rockscout_trade_listings;
drop policy if exists trade_listings_delete on public.rockscout_trade_listings;
grant select, insert, update, delete on public.rockscout_trade_listings to anon, authenticated;
alter table public.rockscout_connections       enable row level security;
alter table public.rockscout_message_requests  enable row level security;
alter table public.rockscout_friend_requests   enable row level security;
alter table public.rockscout_blocks            enable row level security;
alter table public.rockscout_threads           enable row level security;
alter table public.rockscout_messages          enable row level security;
alter table public.rockscout_pings             enable row level security;
alter table public.rockscout_posts             enable row level security;
alter table public.rockscout_post_likes        enable row level security;
alter table public.rockscout_post_comments     enable row level security;
alter table public.rockscout_notifications     enable row level security;
alter table public.rockscout_trade_interests   enable row level security;
drop policy if exists connections_select on public.rockscout_connections;
drop policy if exists connections_insert on public.rockscout_connections;
drop policy if exists connections_delete on public.rockscout_connections;
grant select, insert, delete on public.rockscout_connections to anon, authenticated;
drop policy if exists msg_requests_select on public.rockscout_message_requests;
drop policy if exists msg_requests_insert on public.rockscout_message_requests;
drop policy if exists msg_requests_update on public.rockscout_message_requests;
grant select, insert, update on public.rockscout_message_requests to anon, authenticated;
drop policy if exists friend_requests_select on public.rockscout_friend_requests;
drop policy if exists friend_requests_insert on public.rockscout_friend_requests;
drop policy if exists friend_requests_update on public.rockscout_friend_requests;
grant select, insert, update on public.rockscout_friend_requests to anon, authenticated;
drop policy if exists blocks_select on public.rockscout_blocks;
drop policy if exists blocks_insert on public.rockscout_blocks;
drop policy if exists blocks_delete on public.rockscout_blocks;
grant select, insert, delete on public.rockscout_blocks to anon, authenticated;
drop policy if exists threads_select on public.rockscout_threads;
drop policy if exists threads_insert on public.rockscout_threads;
drop policy if exists threads_update on public.rockscout_threads;
grant select, insert, update on public.rockscout_threads to anon, authenticated;
drop policy if exists messages_select on public.rockscout_messages;
drop policy if exists messages_insert on public.rockscout_messages;
drop policy if exists messages_update on public.rockscout_messages;
grant select, insert, update on public.rockscout_messages to anon, authenticated;
drop policy if exists pings_select on public.rockscout_pings;
drop policy if exists pings_insert on public.rockscout_pings;
drop policy if exists pings_delete on public.rockscout_pings;
grant select, insert, delete on public.rockscout_pings to anon, authenticated;
drop policy if exists posts_select on public.rockscout_posts;
drop policy if exists posts_insert on public.rockscout_posts;
drop policy if exists posts_delete on public.rockscout_posts;
grant select, insert, delete on public.rockscout_posts to anon, authenticated;
drop policy if exists likes_select on public.rockscout_post_likes;
drop policy if exists likes_insert on public.rockscout_post_likes;
drop policy if exists likes_delete on public.rockscout_post_likes;
grant select, insert, delete on public.rockscout_post_likes to anon, authenticated;
drop policy if exists comments_select on public.rockscout_post_comments;
drop policy if exists comments_insert on public.rockscout_post_comments;
drop policy if exists comments_delete on public.rockscout_post_comments;
grant select, insert, delete on public.rockscout_post_comments to anon, authenticated;
drop policy if exists notifs_select on public.rockscout_notifications;
drop policy if exists notifs_insert on public.rockscout_notifications;
drop policy if exists notifs_update on public.rockscout_notifications;
grant select, insert, update on public.rockscout_notifications to anon, authenticated;
drop policy if exists trade_interests_select on public.rockscout_trade_interests;
drop policy if exists trade_interests_insert on public.rockscout_trade_interests;
drop policy if exists trade_interests_delete on public.rockscout_trade_interests;
grant select, insert, delete on public.rockscout_trade_interests to anon, authenticated;
alter table public.rockscout_collection
    add column if not exists note text not null default '';
alter table public.rockscout_collection
    add column if not exists found_at text not null default '';
alter table public.rockscout_collection
    add column if not exists added_at bigint not null default 0;
do $$
declare
    constraint_name text;
begin
    select c.conname into constraint_name
    from pg_constraint c
    join pg_class t on c.conrelid = t.oid
    join pg_namespace n on t.relnamespace = n.oid
    where n.nspname = 'public' and t.relname = 'rockscout_collection'
      and c.contype = 'f';
    if constraint_name is not null then
        execute format('alter table public.rockscout_collection drop constraint %s', constraint_name);
    end if;
end $$;
grant select, insert, update, delete on public.rockscout_collection to authenticated;
grant select, insert, delete on public.rockscout_wishlist to authenticated;
do $$
begin
  perform cron.unschedule('cleanup-old-error-logs');
exception when others then null;
end $$;
select cron.schedule(
  'cleanup-old-error-logs',
  '0 3 * * *',
  $$delete from public.rockscout_error_logs where created_at < now() - interval '90 days'$$
);
insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
select
    'user-photos',
    'user-photos',
    true,   -- public read (URLs include user_id + UUID, effectively unguessable)
    10_485_760,  -- 10 MB per file
    array['image/jpeg', 'image/png', 'image/webp', 'image/heic', 'image/heif']
where not exists (
    select 1 from storage.buckets where id = 'user-photos'
);
alter table storage.objects enable row level security;
drop policy if exists "user-photos-read-all" on storage.objects;
create policy "user-photos-read-all" on storage.objects
    for select using (bucket_id = 'user-photos');
drop policy if exists "user-photos-write-self" on storage.objects;
create policy "user-photos-write-self" on storage.objects
    for insert with check (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );
drop policy if exists "user-photos-update-self" on storage.objects;
create policy "user-photos-update-self" on storage.objects
    for update using (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    ) with check (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );
drop policy if exists "user-photos-delete-self" on storage.objects;
create policy "user-photos-delete-self" on storage.objects
    for delete using (
        bucket_id = 'user-photos'
        and (storage.foldername(name))[1] = auth.uid()::text
    );
alter table public.rockscout_captures
    add column if not exists updated_at timestamptz not null default now();
alter table public.rockscout_saved_images
    add column if not exists updated_at timestamptz not null default now();
alter table public.rockscout_captures
    add column if not exists sync_status text not null default 'synced';
alter table public.rockscout_saved_images
    add column if not exists sync_status text not null default 'synced';
alter table public.rockscout_field_journal
    add column if not exists sync_status text not null default 'synced';
alter table public.rockscout_trips
    add column if not exists sync_status text not null default 'synced';
CREATE EXTENSION IF NOT EXISTS pg_net;
GRANT USAGE ON SCHEMA net TO postgres;
CREATE OR REPLACE FUNCTION notify_fatal_error()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  worker_url text;
  app_key text;
BEGIN
  IF NEW.is_fatal = false THEN
    RETURN NEW;
  END IF;
  SELECT value INTO worker_url FROM rockscout_config WHERE key = 'worker_url';
  SELECT value INTO app_key FROM rockscout_config WHERE key = 'app_key';
  IF worker_url IS NULL OR app_key IS NULL THEN
    RETURN NEW;
  END IF;
  PERFORM net.http_post(
    url := worker_url || '/send-error-email',
    headers := jsonb_build_object(
      'Content-Type', 'application/json',
      'X-App-Key', app_key
    ),
    body := jsonb_build_object(
      'errorType', NEW.error_type,
      'errorMessage', NEW.error_message,
      'stackTrace', NEW.stack_trace,
      'platform', NEW.platform,
      'appVersion', NEW.app_version,
      'osVersion', NEW.os_version,
      'deviceModel', NEW.device_model,
      'userId', NEW.user_id::text,
      'screen', NEW.screen,
      'createdAt', NEW.created_at::text
    )
  );
  RETURN NEW;
END;
$$;
DROP TRIGGER IF EXISTS trigger_fatal_error_email ON rockscout_error_logs;
CREATE TRIGGER trigger_fatal_error_email
  AFTER INSERT ON rockscout_error_logs
  FOR EACH ROW
  WHEN (NEW.is_fatal = true)
  EXECUTE FUNCTION notify_fatal_error();
GRANT EXECUTE ON FUNCTION notify_fatal_error() TO postgres;
CREATE TABLE IF NOT EXISTS group_chats (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  name text NOT NULL,
  subject text DEFAULT '',
  creator_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  max_members int,  -- NULL = no cap
  profanity_filter_level text NOT NULL DEFAULT 'normal' CHECK (profanity_filter_level IN ('normal','strict')),
  header_image_url text,
  scroll_speed_setting text NOT NULL DEFAULT 'normal' CHECK (scroll_speed_setting IN ('normal','half','stop')),
  created_at timestamptz NOT NULL DEFAULT now(),
  deleted_at timestamptz  -- NULL = active; set when creator deletes
);
CREATE TABLE IF NOT EXISTS group_chat_members (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  joined_at timestamptz NOT NULL DEFAULT now(),
  role text NOT NULL DEFAULT 'member' CHECK (role IN ('creator','member')),
  UNIQUE (group_chat_id, user_id)
);
CREATE TABLE IF NOT EXISTS group_chat_invites (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  inviter_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  invitee_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  status text NOT NULL DEFAULT 'pending' CHECK (status IN ('pending','accepted','declined')),
  created_at timestamptz NOT NULL DEFAULT now(),
  responded_at timestamptz
);
CREATE TABLE IF NOT EXISTS group_messages (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  group_chat_id text NOT NULL REFERENCES group_chats(id) ON DELETE CASCADE,
  sender_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  body text DEFAULT '',
  image_url text,
  reply_to_message_id text,  -- self-reference for reply threading
  tagged_user_ids uuid[] DEFAULT '{}',  -- users tagged in the message
  read_at timestamptz,  -- first time any non-sender member read it
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS user_warnings (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reason text DEFAULT '',
  source text DEFAULT 'chat',  -- 'chat' or 'group_chat'
  source_id text,  -- thread or group chat id
  auto_reported boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS chat_threads (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  created_at timestamptz NOT NULL DEFAULT now(),
  last_message_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS chat_thread_participants (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  thread_id text NOT NULL REFERENCES chat_threads(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  joined_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (thread_id, user_id)
);
CREATE TABLE IF NOT EXISTS chat_messages (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  thread_id text NOT NULL REFERENCES chat_threads(id) ON DELETE CASCADE,
  sender_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  body text DEFAULT '',
  image_url text,
  reply_to_message_id text,
  tagged_user_ids uuid[] DEFAULT '{}',
  read_at timestamptz,  -- per-recipient read tracking
  created_at timestamptz NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS chat_message_reads (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  message_id text NOT NULL REFERENCES chat_messages(id) ON DELETE CASCADE,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  read_at timestamptz NOT NULL DEFAULT now(),
  UNIQUE (message_id, user_id)
);
ALTER TABLE group_chats ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "group_chats_select" ON group_chats;
CREATE POLICY "group_chats_select" ON group_chats FOR SELECT
  USING (
    deleted_at IS NULL AND EXISTS (
      SELECT 1 FROM group_chat_members m
      WHERE m.group_chat_id = group_chats.id AND m.user_id = auth.uid()
    )
  );
DROP POLICY IF EXISTS "group_chats_insert" ON group_chats;
CREATE POLICY "group_chats_insert" ON group_chats FOR INSERT
  WITH CHECK (creator_id = auth.uid());
DROP POLICY IF EXISTS "group_chats_update" ON group_chats;
CREATE POLICY "group_chats_update" ON group_chats FOR UPDATE
  USING (creator_id = auth.uid());
DROP POLICY IF EXISTS "group_chats_delete" ON group_chats;
CREATE POLICY "group_chats_delete" ON group_chats FOR DELETE
  USING (creator_id = auth.uid());
ALTER TABLE group_chat_members ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "gcm_select" ON group_chat_members;
CREATE POLICY "gcm_select" ON group_chat_members FOR SELECT
  USING (
    user_id = auth.uid() OR EXISTS (
      SELECT 1 FROM group_chat_members m2
      WHERE m2.group_chat_id = group_chat_members.group_chat_id AND m2.user_id = auth.uid()
    )
  );
DROP POLICY IF EXISTS "gcm_insert" ON group_chat_members;
CREATE POLICY "gcm_insert" ON group_chat_members FOR INSERT
  WITH CHECK (user_id = auth.uid());
DROP POLICY IF EXISTS "gcm_delete" ON group_chat_members;
CREATE POLICY "gcm_delete" ON group_chat_members FOR DELETE
  USING (user_id = auth.uid());
ALTER TABLE group_chat_invites ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "gci_select" ON group_chat_invites;
CREATE POLICY "gci_select" ON group_chat_invites FOR SELECT
  USING (invitee_id = auth.uid() OR inviter_id = auth.uid());
DROP POLICY IF EXISTS "gci_insert" ON group_chat_invites;
CREATE POLICY "gci_insert" ON group_chat_invites FOR INSERT
  WITH CHECK (inviter_id = auth.uid());
DROP POLICY IF EXISTS "gci_update" ON group_chat_invites;
CREATE POLICY "gci_update" ON group_chat_invites FOR UPDATE
  USING (invitee_id = auth.uid());
ALTER TABLE group_messages ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "gm_select" ON group_messages;
CREATE POLICY "gm_select" ON group_messages FOR SELECT
  USING (EXISTS (
    SELECT 1 FROM group_chat_members m
    WHERE m.group_chat_id = group_messages.group_chat_id AND m.user_id = auth.uid()
  ));
DROP POLICY IF EXISTS "gm_insert" ON group_messages;
CREATE POLICY "gm_insert" ON group_messages FOR INSERT
  WITH CHECK (
    sender_id = auth.uid() AND EXISTS (
      SELECT 1 FROM group_chat_members m
      WHERE m.group_chat_id = group_messages.group_chat_id AND m.user_id = auth.uid()
    )
  );
ALTER TABLE user_warnings ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "uw_select" ON user_warnings;
CREATE POLICY "uw_select" ON user_warnings FOR SELECT
  USING (user_id = auth.uid());
ALTER TABLE chat_threads ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "ct_select" ON chat_threads;
CREATE POLICY "ct_select" ON chat_threads FOR SELECT
  USING (EXISTS (
    SELECT 1 FROM chat_thread_participants p
    WHERE p.thread_id = chat_threads.id AND p.user_id = auth.uid()
  ));
DROP POLICY IF EXISTS "ct_insert" ON chat_threads;
CREATE POLICY "ct_insert" ON chat_threads FOR INSERT
  WITH CHECK (true);
DROP POLICY IF EXISTS "ct_update" ON chat_threads;
CREATE POLICY "ct_update" ON chat_threads FOR UPDATE
  USING (EXISTS (
    SELECT 1 FROM chat_thread_participants p
    WHERE p.thread_id = chat_threads.id AND p.user_id = auth.uid()
  ));
ALTER TABLE chat_thread_participants ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "ctp_select" ON chat_thread_participants;
CREATE POLICY "ctp_select" ON chat_thread_participants FOR SELECT
  USING (
    user_id = auth.uid() OR EXISTS (
      SELECT 1 FROM chat_thread_participants p2
      WHERE p2.thread_id = chat_thread_participants.thread_id AND p2.user_id = auth.uid()
    )
  );
DROP POLICY IF EXISTS "ctp_insert" ON chat_thread_participants;
CREATE POLICY "ctp_insert" ON chat_thread_participants FOR INSERT
  WITH CHECK (user_id = auth.uid() OR EXISTS (
    SELECT 1 FROM chat_thread_participants p2
    WHERE p2.thread_id = chat_thread_participants.thread_id AND p2.user_id = auth.uid()
  ));
ALTER TABLE chat_messages ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "cm_select" ON chat_messages;
CREATE POLICY "cm_select" ON chat_messages FOR SELECT
  USING (EXISTS (
    SELECT 1 FROM chat_thread_participants p
    WHERE p.thread_id = chat_messages.thread_id AND p.user_id = auth.uid()
  ));
DROP POLICY IF EXISTS "cm_insert" ON chat_messages;
CREATE POLICY "cm_insert" ON chat_messages FOR INSERT
  WITH CHECK (
    sender_id = auth.uid() AND EXISTS (
      SELECT 1 FROM chat_thread_participants p
      WHERE p.thread_id = chat_messages.thread_id AND p.user_id = auth.uid()
    )
  );
ALTER TABLE chat_message_reads ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "cmr_select" ON chat_message_reads;
CREATE POLICY "cmr_select" ON chat_message_reads FOR SELECT
  USING (
    user_id = auth.uid() OR EXISTS (
      SELECT 1 FROM chat_messages cm
      JOIN chat_thread_participants p ON p.thread_id = cm.thread_id
      WHERE cm.id = chat_message_reads.message_id AND p.user_id = auth.uid()
    )
  );
DROP POLICY IF EXISTS "cmr_insert" ON chat_message_reads;
CREATE POLICY "cmr_insert" ON chat_message_reads FOR INSERT
  WITH CHECK (user_id = auth.uid());
CREATE OR REPLACE FUNCTION check_warning_thresholds()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  warn_count int;
BEGIN
  SELECT count(*) INTO warn_count FROM user_warnings WHERE user_id = NEW.user_id;
  IF warn_count = 3 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 3 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;
  IF warn_count = 5 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 5 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;
  IF warn_count >= 6 THEN
    UPDATE profiles SET banned_at = now() WHERE id = NEW.user_id;
  END IF;
  RETURN NEW;
END;
$$;
DROP TRIGGER IF EXISTS trigger_warning_thresholds ON user_warnings;
CREATE TRIGGER trigger_warning_thresholds
  AFTER INSERT ON user_warnings
  FOR EACH ROW
  EXECUTE FUNCTION check_warning_thresholds();
GRANT EXECUTE ON FUNCTION check_warning_thresholds() TO postgres;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chats TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chat_members TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_chat_invites TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON group_messages TO anon, authenticated;
GRANT SELECT ON user_warnings TO anon, authenticated;
GRANT INSERT ON user_warnings TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_threads TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_thread_participants TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_messages TO anon, authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_message_reads TO anon, authenticated;
ALTER TABLE rockscout_profiles ADD COLUMN IF NOT EXISTS banned_at timestamptz;
CREATE OR REPLACE FUNCTION check_warning_thresholds()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  warn_count int;
BEGIN
  SELECT count(*) INTO warn_count
  FROM user_warnings
  WHERE user_id = NEW.user_id AND auto_reported = false;
  IF warn_count = 3 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 3 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;
  IF warn_count = 5 THEN
    INSERT INTO user_warnings (user_id, reason, source, auto_reported)
    VALUES (NEW.user_id, 'Auto-report: 5 warnings reached', 'system', true)
    ON CONFLICT DO NOTHING;
  END IF;
  IF warn_count >= 6 THEN
    UPDATE rockscout_profiles SET banned_at = now() WHERE id = NEW.user_id;
  END IF;
  RETURN NEW;
END;
$$;
DROP TRIGGER IF EXISTS trigger_warning_thresholds ON user_warnings;
CREATE TRIGGER trigger_warning_thresholds
  AFTER INSERT ON user_warnings
  FOR EACH ROW
  WHEN (NEW.auto_reported = false)
  EXECUTE FUNCTION check_warning_thresholds();
GRANT EXECUTE ON FUNCTION check_warning_thresholds() TO postgres;
DROP POLICY IF EXISTS "uw_insert" ON user_warnings;
CREATE POLICY "uw_insert" ON user_warnings FOR INSERT
  WITH CHECK (user_id = auth.uid());
DROP POLICY IF EXISTS "group_chats_select" ON group_chats;
CREATE POLICY "group_chats_select" ON group_chats FOR SELECT
  USING (
    deleted_at IS NULL AND (
      creator_id = auth.uid() OR
      EXISTS (
        SELECT 1 FROM group_chat_members m
        WHERE m.group_chat_id = group_chats.id AND m.user_id = auth.uid()
      ) OR
      true
    )
  );
CREATE OR REPLACE FUNCTION enforce_group_member_cap()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  max_members int;
  current_count int;
BEGIN
  SELECT gc.max_members INTO max_members
  FROM group_chats gc
  WHERE gc.id = NEW.group_chat_id AND gc.deleted_at IS NULL;
  IF max_members IS NOT NULL THEN
    SELECT count(*) INTO current_count
    FROM group_chat_members
    WHERE group_chat_id = NEW.group_chat_id;
    IF current_count >= max_members THEN
      RAISE EXCEPTION 'Group chat is full (max % members)', max_members;
    END IF;
  END IF;
  RETURN NEW;
END;
$$;
DROP TRIGGER IF EXISTS trigger_group_member_cap ON group_chat_members;
CREATE TRIGGER trigger_group_member_cap
  BEFORE INSERT ON group_chat_members
  FOR EACH ROW
  EXECUTE FUNCTION enforce_group_member_cap();
GRANT EXECUTE ON FUNCTION enforce_group_member_cap() TO postgres;
CREATE TABLE IF NOT EXISTS self_harm_warnings (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reason text DEFAULT '',
  source text DEFAULT 'chat',
  source_id text,
  auto_reported boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);
ALTER TABLE self_harm_warnings ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "shw_select" ON self_harm_warnings;
CREATE POLICY "shw_select" ON self_harm_warnings FOR SELECT
  USING (user_id = auth.uid());
DROP POLICY IF EXISTS "shw_insert" ON self_harm_warnings;
CREATE POLICY "shw_insert" ON self_harm_warnings FOR INSERT
  WITH CHECK (user_id = auth.uid());
GRANT SELECT, INSERT ON self_harm_warnings TO anon, authenticated;
CREATE TABLE IF NOT EXISTS report_notifications (
  id text PRIMARY KEY DEFAULT gen_random_uuid()::text,
  reported_user_id uuid NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  reporter_id uuid REFERENCES auth.users(id) ON DELETE SET NULL,
  reason text DEFAULT '',
  source text DEFAULT 'manual',  -- 'manual', 'auto_profanity', 'auto_self_harm'
  source_id text,
  email_sent boolean DEFAULT false,
  bell_notification_sent boolean DEFAULT false,
  created_at timestamptz NOT NULL DEFAULT now()
);
ALTER TABLE report_notifications ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "rn_select" ON report_notifications;
CREATE POLICY "rn_select" ON report_notifications FOR SELECT
  USING (reported_user_id = auth.uid() OR reporter_id = auth.uid());
DROP POLICY IF EXISTS "rn_insert" ON report_notifications;
CREATE POLICY "rn_insert" ON report_notifications FOR INSERT
  WITH CHECK (reporter_id = auth.uid() OR reporter_id IS NULL);
GRANT SELECT, INSERT ON report_notifications TO anon, authenticated;
CREATE TABLE IF NOT EXISTS chat_typing_status (
  id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  chat_id TEXT NOT NULL,
  user_id TEXT NOT NULL REFERENCES rockscout_profiles(id) ON DELETE CASCADE,
  is_typing BOOLEAN DEFAULT false,
  updated_at TIMESTAMPTZ DEFAULT now(),
  UNIQUE(chat_id, user_id)
);
ALTER TABLE chat_typing_status ENABLE ROW LEVEL SECURITY;
CREATE POLICY "users_can_read_typing_status" ON chat_typing_status
  FOR SELECT USING (
    EXISTS (
      SELECT 1 FROM chat_thread_participants p
      WHERE p.thread_id = chat_typing_status.chat_id
        AND p.user_id = auth.uid()
    )
    OR
    EXISTS (
      SELECT 1 FROM group_chat_members m
      WHERE m.group_chat_id = chat_typing_status.chat_id
        AND m.user_id = auth.uid()
    )
  );
CREATE POLICY "users_can_upsert_own_typing" ON chat_typing_status
  FOR INSERT WITH CHECK (user_id = auth.uid());
CREATE POLICY "users_can_update_own_typing" ON chat_typing_status
  FOR UPDATE USING (user_id = auth.uid());
CREATE POLICY "users_can_delete_own_typing" ON chat_typing_status
  FOR DELETE USING (user_id = auth.uid());
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO authenticated;
GRANT SELECT, INSERT, UPDATE, DELETE ON chat_typing_status TO anon;
ALTER TABLE rockscout_profiles
  ADD COLUMN IF NOT EXISTS highlight_color TEXT DEFAULT NULL;
ALTER TABLE rockscout_profiles ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Profiles are readable by all" ON rockscout_profiles;
CREATE POLICY "Profiles are readable by all"
  ON rockscout_profiles FOR SELECT
  USING (true);
DROP POLICY IF EXISTS "Users can update own profile" ON rockscout_profiles;
CREATE POLICY "Users can update own profile"
  ON rockscout_profiles FOR UPDATE
  USING (auth.uid() = id);
alter table public.rockscout_profiles
    add column if not exists premium_source text
    check (premium_source in ('apk', 'revenuecat', null));
ALTER TABLE public.rockscout_profiles
  ADD COLUMN IF NOT EXISTS profanity_filter_level text NOT NULL DEFAULT 'low'
  CHECK (profanity_filter_level IN ('off', 'low', 'strict'));
GRANT UPDATE (profanity_filter_level) ON public.rockscout_profiles TO authenticated;
alter table public.rockscout_installed_devices
    add column if not exists device_platform text not null default 'unknown';
update public.rockscout_installed_devices
    set device_platform = 'web'
    where device_platform = 'unknown';
