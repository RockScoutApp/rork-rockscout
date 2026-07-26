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
 * POST /push/send
 *   { userId, category, title, body, url? }
 *   → { sent: number, failed: number }
 *   Admin-only (toolkit-secret guarded). Sends a push to every subscription
 *   for the user that has `category` in its enabled list.
 *
 * Web Push encryption uses the `web-push` HMAC + AES-GCM scheme hand-rolled
 * against Web Crypto (no SDK — Workers don't bundle Node-only modules).
 * VAPID keys come from VAPID_PUBLIC_KEY / VAPID_PRIVATE_KEY env vars.
 */

const CORS_JSON = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization, X-App-Key",
  "Content-Type": "application/json",
  "Vary": "Origin",
};

export async function handlePush(
  request: Request,
  env: {
    EXPO_PUBLIC_RORK_APP_KEY?: string;
    EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
    EXPO_PUBLIC_SUPABASE_URL?: string;
    EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
    SUPABASE_SERVICE_ROLE_KEY?: string;
    VAPID_PUBLIC_KEY?: string;
    VAPID_PRIVATE_KEY?: string;
  },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }
  const headers = { ...corsHeaders, ...CORS_JSON };
  const url = new URL(request.url);

  // /push/subscribe and /push/unsubscribe are user-auth (app-key + Supabase JWT).
  if (url.pathname === "/push/subscribe" && request.method === "POST") {
    return handleSubscribe(request, env, headers);
  }
  if (url.pathname === "/push/unsubscribe" && request.method === "POST") {
    return handleUnsubscribe(request, env, headers);
  }
  if (url.pathname === "/push/send" && request.method === "POST") {
    return handleSend(request, env, headers);
  }

  return new Response("not found", { status: 404, headers });
}

// ─── Subscribe / unsubscribe ─────────────────────────────────────────────────

async function handleSubscribe(
  request: Request,
  env: {
    EXPO_PUBLIC_RORK_APP_KEY?: string;
    EXPO_PUBLIC_SUPABASE_URL?: string;
    EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
  },
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

  const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
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
  env: {
    EXPO_PUBLIC_RORK_APP_KEY?: string;
    EXPO_PUBLIC_SUPABASE_URL?: string;
    EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
  },
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

  const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
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

// ─── Send (admin-triggered) ──────────────────────────────────────────────────

async function handleSend(
  request: Request,
  env: {
    EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
    EXPO_PUBLIC_SUPABASE_URL?: string;
    SUPABASE_SERVICE_ROLE_KEY?: string;
    EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
    VAPID_PUBLIC_KEY?: string;
    VAPID_PRIVATE_KEY?: string;
  },
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

  if (!env.VAPID_PUBLIC_KEY || !env.VAPID_PRIVATE_KEY) {
    return Response.json({ error: "VAPID keys not configured." }, { status: 503, headers });
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

  const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
  const serviceKey = env.SUPABASE_SERVICE_ROLE_KEY ?? env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
  if (!supabaseUrl || !serviceKey) {
    return Response.json({ error: "Supabase not configured." }, { status: 503, headers });
  }

  // Fetch the user's subscriptions (service-role bypasses RLS).
  const resp = await fetch(
    `${supabaseUrl}/rest/v1/rockscout_push_subscriptions?user_id=eq.${encodeURIComponent(body.userId)}&select=endpoint,p256dh_key,auth_key,categories`,
    {
      headers: {
        "apikey": serviceKey,
        "Authorization": `Bearer ${serviceKey}`,
      },
    },
  );
  if (!resp.ok) {
    return Response.json({ error: "Could not fetch subscriptions." }, { status: 502, headers });
  }
  const subs = (await resp.json()) as Array<{
    endpoint: string;
    p256dh_key: string;
    auth_key: string;
    categories: string[];
  }>;

  // Filter to subscriptions that have the target category enabled (empty list = all).
  const eligible = subs.filter((s) => s.categories.length === 0 || s.categories.includes(body.category!));

  let sent = 0;
  let failed = 0;
  for (const sub of eligible) {
    try {
      const ok = await sendWebPush(
        sub.endpoint,
        sub.p256dh_key,
        sub.auth_key,
        body.title!,
        body.body!,
        body.url ?? "/app/notifications",
        env.VAPID_PUBLIC_KEY!,
        env.VAPID_PRIVATE_KEY!,
      );
      if (ok) sent++;
      else failed++;
    } catch {
      failed++;
    }
  }

  return Response.json({ sent, failed }, { headers });
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
): Promise<boolean> {
  // 1. Generate the payload (JSON Notification).
  const payload = JSON.stringify({ title, body, url, icon: "/pwa-192.png", badge: "/pwa-192.png" });
  const encoder = new TextEncoder();
  const payloadBytes = encoder.encode(payload);

  // 2. Generate local ECDH P-256 key pair (server ephemeral).
  const serverKeys = await crypto.subtle.generateKey(
    { name: "ECDH", namedCurve: "P-256" },
    true,
    ["deriveBits"],
  );
  const serverPubRaw = new Uint8Array(
    await crypto.subtle.exportKey("raw", serverKeys.publicKey as CryptoKey),
  );

  // 3. Import the subscriber's public key (p256dh, base64url → raw → P-256).
  const userPubRaw = base64UrlToBytes(p256dh);
  const userPubKey = await crypto.subtle.importKey(
    "raw",
    userPubRaw,
    { name: "ECDH", namedCurve: "P-256" },
    false,
    [],
  );

  // 4. ECDH shared secret.
  const sharedSecret = await crypto.subtle.deriveBits(
    { name: "ECDH", public: userPubKey },
    serverKeys.privateKey as CryptoKey,
    256,
  );

  // 5. IKM = concat(sharedSecret, authSecret).
  const authSecret = base64UrlToBytes(auth);
  const ikm = new Uint8Array(sharedSecret.byteLength + authSecret.byteLength);
  ikm.set(new Uint8Array(sharedSecret), 0);
  ikm.set(authSecret, sharedSecret.byteLength);

  // 6. HKDF to derive content encryption key (16 bytes) + nonce (12 bytes).
  const info = new Uint8Array([...encoder.encode("Content-Encoding: aes128gcm"), 0]);
  const cek = await hkdf(ikm, 16, info);
  const nonceInfo = new Uint8Array([...encoder.encode("Content-Encoding: nonce"), 0]);
  const nonce = await hkdf(ikm, 12, nonceInfo);

  // 7. Encrypt the payload with AES-GCM (RFC 8188 content coding).
  // Build the aes128gcm encoded body: header + ciphertext.
  const encrypted = await crypto.subtle.encrypt(
    { name: "AES-GCM", iv: nonce },
    await crypto.subtle.importKey("raw", cek, { name: "AES-GCM" }, false, ["encrypt"]),
    payloadBytes,
  );

  // RFC 8188 record header.
  const maxPad = 0;
  const header = new Uint8Array(21 + 65 + 1 + 4 + 1);
  let offset = 0;
  header.set(serverPubRaw, offset + 1); // skip the salt (16 bytes) — we use zeros for simplicity
  // Actually build it properly: salt(16) + rs(4) + idlen(1) + keyid(65) + pad(1)
  const salt = crypto.getRandomValues(new Uint8Array(16));
  const rs = 4096;
  const fullHeader = new Uint8Array(16 + 4 + 1 + 65 + 1);
  fullHeader.set(salt, 0);
  fullHeader.set(new Uint8Array([0, 0, 0, 0]), 16); // rs as big-endian uint32 — filled below
  new DataView(fullHeader.buffer).setUint32(16, rs);
  fullHeader[20] = 65; // idlen
  fullHeader.set(serverPubRaw, 21);
  fullHeader[fullHeader.length - 1] = maxPad | 0x80; // last record marker

  const record = new Uint8Array(fullHeader.length + encrypted.byteLength);
  record.set(fullHeader, 0);
  record.set(new Uint8Array(encrypted), fullHeader.length);

  // 8. Build the VAPID JWT (ES256).
  const jwt = await buildVapidJwt(endpoint, vapidPrivateKey);
  // VAPID Authorization header: "vapid t=JWT,k=PUBLICKEY".
  const vapidAuth = `vapid t=${jwt},k=${vapidPublicKey}`;

  // 9. POST to the push endpoint.
  const pushResp = await fetch(endpoint, {
    method: "POST",
    headers: {
      "Content-Type": "application/octet-stream",
      "Content-Encoding": "aes128gcm",
      "TTL": "2419200",
      "Authorization": vapidAuth,
    },
    body: record,
  });

  return pushResp.ok || pushResp.status === 201;
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

async function hkdf(ikm: Uint8Array, length: number, info: Uint8Array): Promise<Uint8Array> {
  // HKDF-Extract: PRK = HMAC-SHA256(salt=0, IKM)
  const salt = new Uint8Array(32);
  const prkKey = await crypto.subtle.importKey(
    "raw",
    salt,
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
