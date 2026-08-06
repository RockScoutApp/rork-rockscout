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

create table if not exists public.specimen_embeddings (
    id              bigint primary key generated always as identity,
    specimen_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (specimen_id, image_url, embedding_model)
);

create index if not exists idx_specimen_embeddings_text_vec
    on public.specimen_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);

create index if not exists idx_specimen_embeddings_dedup
    on public.specimen_embeddings (specimen_id, image_url, embedding_model);

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

create index if not exists idx_profiles_stripe_customer
    on public.rockscout_profiles (stripe_customer_id)
    where stripe_customer_id is not null;

create index if not exists idx_push_sub_user
    on public.rockscout_push_subscriptions (user_id);

create index if not exists idx_payment_events_type
    on public.rockscout_payment_events (event_type, processed_at desc);

create table if not exists public.artifact_embeddings (
    id              bigint primary key generated always as identity,
    artifact_id     text        not null,
    image_url       text        not null,
    text_embedding  vector(1536),
    embedding_model text        not null default 'openai/text-embedding-3-small',
    created_at      timestamptz not null default now(),
    unique (artifact_id, image_url, embedding_model)
);

create index if not exists idx_artifact_embeddings_text_vec
    on public.artifact_embeddings
    using ivfflat (text_embedding vector_cosine_ops)
    with (lists = 100);

create index if not exists idx_artifact_embeddings_dedup
    on public.artifact_embeddings (artifact_id, image_url, embedding_model);

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

CREATE TABLE IF NOT EXISTS public.rockscout_settings_backup (
    user_id        uuid PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    settings_json  text NOT NULL,
    updated_at     timestamptz NOT NULL DEFAULT now()
);

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
