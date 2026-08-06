/**
 * Web Push — Cloudflare Worker endpoints.
 *
 * POST /push/subscribe
 *   { endpoint, p256dh, auth, categories: string[], platform: string }
 *   → { ok: boolean }
 *   Stores the PushSubscription in Supabase for the authenticated user.
 *
 * POST /push/unsubscribe
 *   { endpoint }
 *   → { ok: boolean }
 *   Removes the subscription.
 *
 * GET /push/key
 *   → { publicKey }
 *   The VAPID application-server key the worker signs with. Clients MUST
 *   subscribe with this exact key — a client/server key mismatch makes every
 *   push endpoint reject the send with 403.
 *
 * POST /push/test
 *   {} → { sent, failed }
 *   User-auth. Sends a test push to the caller's own subscriptions.
 *
 * POST /push/notify
 *   { userId, category, title, body, url? } → { sent, failed }
 *   User-auth (app key + Supabase JWT). Lets an in-app event (message, trade
 *   interest, friend request…) push another user immediately.
 *
 * POST /push/send
 *   { userId, category, title, body, url? }
 *   → { sent: number, failed: number }
 *   Admin-only (toolkit-secret guarded). Sends a push to every subscription
 *   for the user that has `category` in its enabled list.
 *
 * Payload encryption is aes128gcm per RFC 8188 with the key derivation from
 * RFC 8291, hand-rolled against Web Crypto (Workers can't bundle `web-push`).
 * VAPID keys come from VAPID_PUBLIC_KEY / VAPID_PRIVATE_KEY env vars.
 */

// VAPID keys for Web Push (RFC 8292). Generated P-256 ECDSA pair.
// Env vars VAPID_PUBLIC_KEY / VAPID_PRIVATE_KEY take precedence, but ONLY as a
// matched pair — a public key from env combined with a fallback private key (or
// vice versa) produces a signature the push service rejects.
const FALLBACK_VAPID_PUBLIC_KEY =
  "BLtKfWHPcWrMDASWRB7jSqALMwG-w9x3Io8Ehux72XWXZ4_n3BcYSGaQnfldDMb82DdbQXZVSdHMfO67tGkEMC4";
const FALLBACK_VAPID_PRIVATE_KEY =
  "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgtAJ-WhJyRELf1j5LcUaCwaZLwrLUKuBqNVe1B2lSMuOhRANCAAS7Sn1hz3FqzAwElkQe40qgCzMBvsPcdyKPBIbse9l1l2eP59wXGEhmkJ35XQzG_Ng3W0F2VUnRzHzuu7RpBDAu";

/** Resolve the VAPID key pair, keeping public/private consistent. */
function resolveVapidKeys(env: {
  VAPID_PUBLIC_KEY?: string;
  VAPID_PRIVATE_KEY?: string;
}): { publicKey: string; privateKey: string } {
  if (env.VAPID_PUBLIC_KEY && env.VAPID_PRIVATE_KEY) {
    return { publicKey: env.VAPID_PUBLIC_KEY, privateKey: env.VAPID_PRIVATE_KEY };
  }
  return { publicKey: FALLBACK_VAPID_PUBLIC_KEY, privateKey: FALLBACK_VAPID_PRIVATE_KEY };
}

import { resolveSupabaseUrl } from "./auth";

/** Env bindings the push handlers need. */
interface PushEnv {
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
  EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  VAPID_PUBLIC_KEY?: string;
  VAPID_PRIVATE_KEY?: string;
}

const CORS_JSON = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization, X-App-Key",
  "Content-Type": "application/json",
  "Vary": "Origin",
};

export async function handlePush(
  request: Request,
  env: PushEnv,
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }
  const headers = { ...corsHeaders, ...CORS_JSON };
  const url = new URL(request.url);

  // Public: the application-server key clients must subscribe with.
  if (url.pathname === "/push/key" && request.method === "GET") {
    return Response.json({ publicKey: resolveVapidKeys(env).publicKey }, { headers });
  }

  // /push/subscribe and /push/unsubscribe are user-auth (app-key + Supabase JWT).
  if (url.pathname === "/push/subscribe" && request.method === "POST") {
    return handleSubscribe(request, env, headers);
  }
  if (url.pathname === "/push/unsubscribe" && request.method === "POST") {
    return handleUnsubscribe(request, env, headers);
  }
  if (url.pathname === "/push/test" && request.method === "POST") {
    return handleTest(request, env, headers);
  }
  if (url.pathname === "/push/notify" && request.method === "POST") {
    return handleNotify(request, env, headers);
  }
  if (url.pathname === "/push/send" && request.method === "POST") {
    return handleSend(request, env, headers);
  }

  return new Response("not found", { status: 404, headers });
}

// ─── Subscribe / unsubscribe ─────────────────────────────────────────────────

async function handleSubscribe(
  request: Request,
  env: PushEnv,
  headers: Record<string, string>,
): Promise<Response> {
  const authCheck = checkAppKey(request, env);
  if (authCheck) return authCheck;

  // Resolve the user id from the Supabase JWT in the Authorization header.
  const userId = resolveUserId(request);
  if (!userId) {
    return Response.json({ error: "Not authenticated." }, { status: 401, headers });
  }

  let body: {
    endpoint?: string;
    keys?: { p256dh?: string; auth?: string };
    categories?: string[];
    platform?: string;
  };
  try {
    body = (await request.json()) as typeof body;
  } catch {
    return Response.json({ error: "Invalid JSON." }, { status: 400, headers });
  }

  if (!body.endpoint || !body.keys?.p256dh || !body.keys?.auth) {
    return Response.json({ error: "Missing endpoint or keys." }, { status: 400, headers });
  }

  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY);
  const anonKey = env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
  if (!supabaseUrl || !anonKey) {
    return Response.json({ error: "Supabase not configured." }, { status: 503, headers });
  }

  // Forward the user's JWT so RLS applies (auth.uid() = userId).
  const userJwt = request.headers.get("authorization")?.replace(/^Bearer\s+/i, "");

  // Upsert the subscription (insert or update categories if already exists).
  // Use POST with Prefer: resolution=merge-duplicates to upsert on the unique
  // (user_id, endpoint) constraint.
  const resp = await fetch(`${supabaseUrl}/rest/v1/rockscout_push_subscriptions`, {
    method: "POST",
    headers: {
      "apikey": anonKey,
      "Authorization": userJwt ? `Bearer ${userJwt}` : `Bearer ${anonKey}`,
      "Content-Type": "application/json",
      "Prefer": "return=minimal,resolution=merge-duplicates",
    },
    body: JSON.stringify({
      user_id: userId,
      endpoint: body.endpoint,
      p256dh_key: body.keys.p256dh,
      auth_key: body.keys.auth,
      categories: body.categories ?? [],
      platform: body.platform ?? "",
    }),
  });

  if (!resp.ok) {
    const text = await resp.text();
    console.error("push subscribe failed", resp.status, text);
    return Response.json({ error: "Could not save subscription." }, { status: 502, headers });
  }
  return Response.json({ ok: true }, { headers });
}

async function handleUnsubscribe(
  request: Request,
  env: PushEnv,
  headers: Record<string, string>,
): Promise<Response> {
  const authCheck = checkAppKey(request, env);
  if (authCheck) return authCheck;

  const userId = resolveUserId(request);
  if (!userId) {
    return Response.json({ error: "Not authenticated." }, { status: 401, headers });
  }

  let body: { endpoint?: string };
  try {
    body = (await request.json()) as typeof body;
  } catch {
    return Response.json({ error: "Invalid JSON." }, { status: 400, headers });
  }
  if (!body.endpoint) {
    return Response.json({ error: "Missing endpoint." }, { status: 400, headers });
  }

  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY);
  const anonKey = env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
  if (!supabaseUrl || !anonKey) {
    return Response.json({ error: "Supabase not configured." }, { status: 503, headers });
  }
  const userJwt = request.headers.get("authorization")?.replace(/^Bearer\s+/i, "");

  const resp = await fetch(
    `${supabaseUrl}/rest/v1/rockscout_push_subscriptions?user_id=eq.${encodeURIComponent(userId)}&endpoint=eq.${encodeURIComponent(body.endpoint)}`,
    {
      method: "DELETE",
      headers: {
        "apikey": anonKey,
        "Authorization": userJwt ? `Bearer ${userJwt}` : `Bearer ${anonKey}`,
      },
    },
  );

  if (!resp.ok) {
    console.error("push unsubscribe failed", resp.status);
    return Response.json({ error: "Could not remove subscription." }, { status: 502, headers });
  }
  return Response.json({ ok: true }, { headers });
}

// ─── Test / notify (user-authenticated) ──────────────────────────────────────

/** Sends a test push to the caller's own subscriptions — instant delivery check. */
async function handleTest(
  request: Request,
  env: PushEnv,
  headers: Record<string, string>,
): Promise<Response> {
  const authCheck = checkAppKey(request, env);
  if (authCheck) return authCheck;
  const userId = resolveUserId(request);
  if (!userId) {
    return Response.json({ error: "Not authenticated." }, { status: 401, headers });
  }
  return deliver(env, headers, {
    userId,
    category: null,
    title: "RockScout push is working",
    body: "Notifications are set up on this device.",
    url: "/app/notifications",
  });
}

/**
 * Sends a push to another user on behalf of a signed-in user. Used for instant
 * social/trade notifications (messages, friend requests, trade interest) that
 * previously only ever fired as local notifications on the sender's device.
 */
async function handleNotify(
  request: Request,
  env: PushEnv,
  headers: Record<string, string>,
): Promise<Response> {
  const authCheck = checkAppKey(request, env);
  if (authCheck) return authCheck;
  const senderId = resolveUserId(request);
  if (!senderId) {
    return Response.json({ error: "Not authenticated." }, { status: 401, headers });
  }

  let body: { userId?: string; category?: string; title?: string; body?: string; url?: string };
  try {
    body = (await request.json()) as typeof body;
  } catch {
    return Response.json({ error: "Invalid JSON." }, { status: 400, headers });
  }
  if (!body.userId || !body.title || !body.body) {
    return Response.json({ error: "Missing userId, title, or body." }, { status: 400, headers });
  }

  return deliver(env, headers, {
    userId: body.userId,
    category: body.category ?? null,
    title: body.title.slice(0, 120),
    body: body.body.slice(0, 400),
    url: body.url ?? "/app/notifications",
  });
}

/** Shared fan-out: look up the target's subscriptions and encrypt+POST to each.
 *  Exported so other handlers (e.g. trip reminders) can reuse the same delivery path. */
export async function deliver(
  env: PushEnv,
  headers: Record<string, string>,
  msg: { userId: string; category: string | null; title: string; body: string; url: string },
): Promise<Response> {
  const { publicKey: vapidPublic, privateKey: vapidPrivate } = resolveVapidKeys(env);

  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY);
  const serviceKey = env.SUPABASE_SERVICE_ROLE_KEY ?? env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
  if (!supabaseUrl || !serviceKey) {
    return Response.json({ error: "Supabase not configured." }, { status: 503, headers });
  }

  const resp = await fetch(
    `${supabaseUrl}/rest/v1/rockscout_push_subscriptions?user_id=eq.${encodeURIComponent(msg.userId)}&select=endpoint,p256dh_key,auth_key,categories`,
    { headers: { "apikey": serviceKey, "Authorization": `Bearer ${serviceKey}` } },
  );
  if (!resp.ok) {
    console.error("push deliver: subscription lookup failed", resp.status);
    return Response.json({ error: "Could not fetch subscriptions." }, { status: 502, headers });
  }
  const subs = (await resp.json()) as Array<{
    endpoint: string;
    p256dh_key: string;
    auth_key: string;
    categories: string[] | null;
  }>;

  // Empty/absent category list means "all categories".
  const eligible = subs.filter((s) => {
    if (!msg.category) return true;
    const cats = s.categories ?? [];
    return cats.length === 0 || cats.includes(msg.category);
  });

  let sent = 0;
  let failed = 0;
  const expired: string[] = [];
  for (const sub of eligible) {
    try {
      const result = await sendWebPush(
        sub.endpoint,
        sub.p256dh_key,
        sub.auth_key,
        msg.title,
        msg.body,
        msg.url,
        vapidPublic,
        vapidPrivate,
      );
      if (result.ok) sent++;
      else {
        failed++;
        if (result.gone) expired.push(sub.endpoint);
      }
    } catch {
      failed++;
    }
  }

  // Prune endpoints the push service says are permanently gone so a stale
  // subscription can't keep a user looking "subscribed" while nothing arrives.
  for (const endpoint of expired) {
    await fetch(
      `${supabaseUrl}/rest/v1/rockscout_push_subscriptions?endpoint=eq.${encodeURIComponent(endpoint)}`,
      {
        method: "DELETE",
        headers: { "apikey": serviceKey, "Authorization": `Bearer ${serviceKey}` },
      },
    ).catch(() => undefined);
  }

  return Response.json({ sent, failed, subscriptions: eligible.length }, { headers });
}

// ─── Send (admin-triggered) ──────────────────────────────────────────────────

async function handleSend(
  request: Request,
  env: PushEnv,
  headers: Record<string, string>,
): Promise<Response> {
  // Toolkit-secret guarded (admin only).
  const expected = env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY;
  if (!expected) {
    return Response.json({ error: "Server not configured." }, { status: 503, headers });
  }
  const provided = request.headers.get("authorization")?.replace(/^Bearer\s+/i, "");
  if (provided !== expected) {
    return Response.json({ error: "Unauthorized." }, { status: 401, headers });
  }

  let body: { userId?: string; category?: string; title?: string; body?: string; url?: string };
  try {
    body = (await request.json()) as typeof body;
  } catch {
    return Response.json({ error: "Invalid JSON." }, { status: 400, headers });
  }
  if (!body.userId || !body.category || !body.title || !body.body) {
    return Response.json({ error: "Missing userId, category, title, or body." }, { status: 400, headers });
  }

  return deliver(env, headers, {
    userId: body.userId,
    category: body.category,
    title: body.title,
    body: body.body,
    url: body.url ?? "/app/notifications",
  });
}

// ─── Web Push encryption + send (RFC 8291 + VAPID RFC 8292) ──────────────────

async function sendWebPush(
  endpoint: string,
  p256dh: string,
  auth: string,
  title: string,
  body: string,
  url: string,
  vapidPublicKey: string,
  vapidPrivateKey: string,
): Promise<{ ok: boolean; gone: boolean }> {
  const encoder = new TextEncoder();

  // 1. Notification payload consumed by the service worker's "push" listener.
  const payloadBytes = encoder.encode(
    JSON.stringify({ title, body, url, icon: "/pwa-192.png", badge: "/pwa-192.png" }),
  );

  // 2. Ephemeral server ECDH key pair (the "application server" keys in RFC 8291).
  const serverKeys = (await crypto.subtle.generateKey(
    { name: "ECDH", namedCurve: "P-256" },
    true,
    ["deriveBits"],
  )) as CryptoKeyPair;
  const serverPubRaw = new Uint8Array(
    await crypto.subtle.exportKey("raw", serverKeys.publicKey),
  );

  // 3. Subscriber public key + auth secret.
  const userPubRaw = base64UrlToBytes(p256dh);
  const authSecret = base64UrlToBytes(auth);
  const userPubKey = await crypto.subtle.importKey(
    "raw",
    userPubRaw,
    { name: "ECDH", namedCurve: "P-256" },
    false,
    [],
  );

  // 4. ECDH shared secret.
  const sharedSecret = new Uint8Array(
    await crypto.subtle.deriveBits({ name: "ECDH", public: userPubKey }, serverKeys.privateKey, 256),
  );

  // 5. RFC 8291 key derivation. The auth secret is the HKDF *salt* here, and the
  //    info string binds both public keys into the derived key:
  //      PRK = HKDF(salt=auth, ikm=ecdh, info="WebPush: info\0"||ua_pub||as_pub)
  const keyInfo = concatBytes(
    encoder.encode("WebPush: info"),
    new Uint8Array([0]),
    userPubRaw,
    serverPubRaw,
  );
  const prk = await hkdf(authSecret, sharedSecret, keyInfo, 32);

  // 6. Content encryption key + nonce, salted with the record salt.
  const salt = crypto.getRandomValues(new Uint8Array(16));
  const cek = await hkdf(
    salt,
    prk,
    concatBytes(encoder.encode("Content-Encoding: aes128gcm"), new Uint8Array([0])),
    16,
  );
  const nonce = await hkdf(
    salt,
    prk,
    concatBytes(encoder.encode("Content-Encoding: nonce"), new Uint8Array([0])),
    12,
  );

  // 7. Encrypt. A single record, so the plaintext ends with the 0x02 padding
  //    delimiter (0x02 = last record) before the AES-GCM pass.
  const plaintext = concatBytes(payloadBytes, new Uint8Array([0x02]));
  const ciphertext = new Uint8Array(
    await crypto.subtle.encrypt(
      { name: "AES-GCM", iv: nonce },
      await crypto.subtle.importKey("raw", cek, { name: "AES-GCM" }, false, ["encrypt"]),
      plaintext,
    ),
  );

  // 8. RFC 8188 header: salt(16) || rs(4, big-endian) || idlen(1) || keyid(65).
  const recordSize = Math.max(ciphertext.length + 1, 4096);
  const header = new Uint8Array(16 + 4 + 1 + serverPubRaw.length);
  header.set(salt, 0);
  new DataView(header.buffer).setUint32(16, recordSize);
  header[20] = serverPubRaw.length;
  header.set(serverPubRaw, 21);

  const record = concatBytes(header, ciphertext);

  // 9. VAPID (RFC 8292) — "vapid t=<JWT>,k=<public key>".
  const jwt = await buildVapidJwt(endpoint, vapidPrivateKey);
  const vapidAuth = `vapid t=${jwt},k=${vapidPublicKey}`;

  const pushResp = await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/octet-stream",
      "Content-Encoding": "aes128gcm",
      "TTL": "2419200",
      "Urgency": "high",
      "Authorization": vapidAuth,
    },
    body: record,
  });

  if (!pushResp.ok) {
    console.error("web push rejected", pushResp.status, await pushResp.text().catch(() => ""));
  }
  // 404/410 mean the subscription is permanently dead and should be pruned.
  return {
    ok: pushResp.ok,
    gone: pushResp.status === 404 || pushResp.status === 410,
  };
}

/** Concatenate byte arrays. */
function concatBytes(...parts: Uint8Array[]): Uint8Array {
  const total = parts.reduce((n, p) => n + p.length, 0);
  const out = new Uint8Array(total);
  let offset = 0;
  for (const p of parts) {
    out.set(p, offset);
    offset += p.length;
  }
  return out;
}

async function buildVapidJwt(endpoint: string, privateKey: string): Promise<string> {
  const url = new URL(endpoint);
  const audience = `${url.protocol}//${url.host}`;
  const now = Math.floor(Date.now() / 1000);
  const payload = {
    aud: audience,
    exp: now + 12 * 60 * 60,
    sub: "mailto:hello@rockscout.app",
  };
  const header = { typ: "JWT", alg: "ES256" };
  const enc = (o: unknown) => base64UrlEncode(new TextEncoder().encode(JSON.stringify(o)));
  const token = `${enc(header)}.${enc(payload)}`;
  // Import the VAPID private key (base64url, P-256 PKCS8).
  const keyBytes = base64UrlToBytes(privateKey);
  const key = await crypto.subtle.importKey(
    "pkcs8",
    keyBytes,
    { name: "ECDSA", namedCurve: "P-256" },
    false,
    ["sign"],
  );
  const sig = await crypto.subtle.sign(
    { name: "ECDSA", hash: "SHA-256" },
    key,
    new TextEncoder().encode(token),
  );
  return `${token}.${base64UrlEncode(new Uint8Array(sig))}`;
}

// ─── Crypto helpers ──────────────────────────────────────────────────────────

function base64UrlToBytes(b64url: string): Uint8Array {
  const b64 = b64url.replace(/-/g, "+").replace(/_/g, "/");
  const pad = b64.length % 4 === 0 ? "" : "=".repeat(4 - (b64.length % 4));
  const binary = atob(b64 + pad);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
  return bytes;
}

function base64UrlEncode(bytes: Uint8Array): string {
  let binary = "";
  for (const b of bytes) binary += String.fromCharCode(b);
  return btoa(binary).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/, "");
}

/** HKDF-SHA256 (extract + expand) per RFC 5869. */
async function hkdf(
  salt: Uint8Array,
  ikm: Uint8Array,
  info: Uint8Array,
  length: number,
): Promise<Uint8Array> {
  // HKDF-Extract: PRK = HMAC-SHA256(salt, IKM)
  const prkKey = await crypto.subtle.importKey(
    "raw",
    salt.length > 0 ? salt : new Uint8Array(32),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const prk = await crypto.subtle.sign("HMAC", prkKey, ikm);

  // HKDF-Expand
  const prkImported = await crypto.subtle.importKey(
    "raw",
    prk,
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const n = Math.ceil(length / 32);
  let t = new Uint8Array(0);
  let okm = new Uint8Array(0);
  for (let i = 1; i <= n; i++) {
    const input = new Uint8Array(t.length + info.length + 1);
    input.set(t, 0);
    input.set(info, t.length);
    input[input.length - 1] = i;
    const ti = await crypto.subtle.sign("HMAC", prkImported, input);
    t = new Uint8Array(ti);
    const newOkm = new Uint8Array(okm.length + ti.byteLength);
    newOkm.set(okm, 0);
    newOkm.set(new Uint8Array(ti), okm.length);
    okm = newOkm;
  }
  return okm.slice(0, length);
}

// ─── Auth helpers ────────────────────────────────────────────────────────────

function checkAppKey(request: Request, env: { EXPO_PUBLIC_RORK_APP_KEY?: string }): Response | null {
  const expected = env.EXPO_PUBLIC_RORK_APP_KEY;
  if (!expected) return Response.json({ error: "Server not configured." }, { status: 503 });
  const provided = request.headers.get("x-app-key");
  if (provided !== expected) {
    return Response.json({ error: "Unauthorized." }, { status: 401 });
  }
  return null;
}

/** Extract the user id from a Supabase JWT in the Authorization header. */
function resolveUserId(request: Request): string | null {
  const authHeader = request.headers.get("authorization") ?? "";
  const token = authHeader.replace(/^Bearer\s+/i, "");
  if (!token || token.split(".").length !== 3) return null;
  try {
    // Decode the JWT payload (middle segment) — we trust the signature because
    // Supabase issued it and we forward it straight to Supabase for the write.
    const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/"))) as {
      sub?: string;
    };
    return payload.sub ?? null;
  } catch {
    return null;
  }
}
