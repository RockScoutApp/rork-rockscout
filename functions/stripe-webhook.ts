/**
 * Stripe webhook handler — Cloudflare Worker endpoint.
 *
 * POST /stripe/webhook  (raw body, Stripe-Signature header)
 *
 * Handles:
 *   checkout.session.completed       → link stripe_customer_id to profile
 *   customer.subscription.created    → set is_pro = true (Premium active)
 *   customer.subscription.updated    → set is_pro based on subscription status
 *   customer.subscription.deleted    → set is_pro = false
 *   payment_intent.succeeded         → credit tokens + unlock window for one-time
 *
 * Idempotent: every event id is logged in rockscout_payment_events; duplicates
 * are rejected before any side effects run.
 *
 * Signature verification uses Stripe's HMAC-SHA256 scheme hand-rolled against
 * Web Crypto (no SDK — Workers don't bundle the Node Stripe SDK).
 */

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Stripe-Signature",
  "Vary": "Origin",
};

/** Donation tiers → token grants + unlock days (matches IapConfig.kt + Paywall.tsx). */
const DONATION_GRANTS: Record<string, { tokens: number; unlockDays: number }> = {
  "donation-2": { tokens: 5, unlockDays: 2 },
  "donation-4": { tokens: 10, unlockDays: 5 },
};

/** Token pack grants. */
const TOKEN_PACK_GRANTS: Record<string, number> = {
  "tokens-1": 1,
  "tokens-4": 4,
  "tokens-10": 10,
};

export async function handleStripeWebhook(
  request: Request,
  env: {
    STRIPE_SECRET_KEY?: string;
    STRIPE_WEBHOOK_SECRET?: string;
    EXPO_PUBLIC_SUPABASE_URL?: string;
    EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
    SUPABASE_SERVICE_ROLE_KEY?: string;
  },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  if (!env.STRIPE_WEBHOOK_SECRET || !env.STRIPE_SECRET_KEY) {
    return Response.json(
      { error: "Webhook not configured." },
      { status: 503, headers: CORS },
    );
  }

  const sigHeader = request.headers.get("stripe-signature") ?? "";
  const rawBody = await request.text();

  // Verify the Stripe signature.
  let event: StripeEvent;
  try {
    event = await verifyStripeSignature(rawBody, sigHeader, env.STRIPE_WEBHOOK_SECRET);
  } catch (err) {
    const msg = err instanceof Error ? err.message : "signature verification failed";
    console.error("Stripe webhook signature failed", msg);
    return Response.json({ error: `Invalid signature: ${msg}` }, { status: 400, headers: CORS });
  }

  // Idempotency: check if we've already processed this event id.
  const serviceKey = env.SUPABASE_SERVICE_ROLE_KEY ?? env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
  const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
  if (!supabaseUrl || !serviceKey) {
    return Response.json({ error: "Supabase not configured." }, { status: 503, headers: CORS });
  }

  const alreadyProcessed = await checkEventProcessed(supabaseUrl, serviceKey, event.id);
  if (alreadyProcessed) {
    return Response.json({ received: true, duplicate: true }, { headers: CORS });
  }

  // Dispatch the event.
  try {
    await dispatchEvent(event, supabaseUrl, serviceKey);
    await logEvent(supabaseUrl, serviceKey, event);
  } catch (err) {
    console.error("Stripe webhook dispatch failed", err);
    return Response.json({ error: "Webhook handler failed." }, { status: 500, headers: CORS });
  }

  return Response.json({ received: true }, { headers: CORS });
}

// ─── Event dispatch ──────────────────────────────────────────────────────────

async function dispatchEvent(event: StripeEvent, supabaseUrl: string, serviceKey: string): Promise<void> {
  const obj = event.data.object;

  if (event.type === "checkout.session.completed") {
    const session = obj as CheckoutSession;
    const userId = session.client_reference_id;
    if (userId && session.customer) {
      await updateProfile(supabaseUrl, serviceKey, userId, {
        stripe_customer_id: typeof session.customer === "string" ? session.customer : session.customer.id,
      });
    }
    return;
  }

  if (event.type === "customer.subscription.created" || event.type === "customer.subscription.updated") {
    const sub = obj as StripeSubscription;
    const isActive = sub.status === "active" || sub.status === "trialing";
    const userId = sub.metadata?.user_id;
    if (userId) {
      await updateProfile(supabaseUrl, serviceKey, userId, { is_pro: isActive });
    }
    return;
  }

  if (event.type === "customer.subscription.deleted") {
    const sub = obj as StripeSubscription;
    const userId = sub.metadata?.user_id;
    if (userId) {
      await updateProfile(supabaseUrl, serviceKey, userId, { is_pro: false });
    }
    return;
  }

  if (event.type === "payment_intent.succeeded") {
    const pi = obj as PaymentIntent;
    const userId = pi.metadata?.user_id;
    const productKey = pi.metadata?.product_key;
    if (!userId || !productKey) return;

    // Donation: credit tokens + unlock window.
    const donation = DONATION_GRANTS[productKey];
    if (donation) {
      await creditTokensAndUnlock(supabaseUrl, serviceKey, userId, donation.tokens, donation.unlockDays);
      return;
    }

    // Token pack: credit tokens only.
    const packTokens = TOKEN_PACK_GRANTS[productKey];
    if (packTokens) {
      await creditTokensAndUnlock(supabaseUrl, serviceKey, userId, packTokens, 0);
      return;
    }
  }
}

// ─── Supabase helpers (service-role, bypasses RLS) ───────────────────────────

async function updateProfile(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
  patch: Record<string, unknown>,
): Promise<void> {
  const resp = await fetch(`${supabaseUrl}/rest/v1/rockscout_profiles?id=eq.${encodeURIComponent(userId)}`, {
    method: "PATCH",
    headers: {
      "apikey": serviceKey,
      "Authorization": `Bearer ${serviceKey}`,
      "Content-Type": "application/json",
      "Prefer": "return=minimal",
    },
    body: JSON.stringify(patch),
  });
  if (!resp.ok) {
    const text = await resp.text();
    console.error("updateProfile failed", resp.status, text);
  }
}

async function creditTokensAndUnlock(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
  tokens: number,
  unlockDays: number,
): Promise<void> {
  // Read current values.
  const getResp = await fetch(
    `${supabaseUrl}/rest/v1/rockscout_profiles?id=eq.${encodeURIComponent(userId)}&select=tokens,unlock_until`,
    {
      headers: {
        "apikey": serviceKey,
        "Authorization": `Bearer ${serviceKey}`,
      },
    },
  );
  if (!getResp.ok) {
    console.error("creditTokens read failed", getResp.status);
    return;
  }
  const rows = (await getResp.json()) as Array<{ tokens: number; unlock_until: string | null }>;
  if (rows.length === 0) return;

  const row = rows[0];
  const newTokens = (row.tokens ?? 0) + tokens;
  const now = Date.now();
  const currentUnlockMs = row.unlock_until ? new Date(row.unlock_until).getTime() : 0;
  const base = Math.max(currentUnlockMs, now);
  const newUnlock = unlockDays > 0 ? new Date(base + unlockDays * 86_400_000).toISOString() : row.unlock_until;

  await updateProfile(supabaseUrl, serviceKey, userId, {
    tokens: newTokens,
    unlock_until: newUnlock,
  });
}

async function checkEventProcessed(supabaseUrl: string, serviceKey: string, eventId: string): Promise<boolean> {
  const resp = await fetch(
    `${supabaseUrl}/rest/v1/rockscout_payment_events?stripe_event_id=eq.${encodeURIComponent(eventId)}&select=id&limit=1`,
    {
      headers: {
        "apikey": serviceKey,
        "Authorization": `Bearer ${serviceKey}`,
      },
    },
  );
  if (!resp.ok) return false;
  const rows = (await resp.json()) as Array<{ id: string }>;
  return rows.length > 0;
}

async function logEvent(supabaseUrl: string, serviceKey: string, event: StripeEvent): Promise<void> {
  await fetch(`${supabaseUrl}/rest/v1/rockscout_payment_events`, {
    method: "POST",
    headers: {
      "apikey": serviceKey,
      "Authorization": `Bearer ${serviceKey}`,
      "Content-Type": "application/json",
      "Prefer": "return=minimal",
    },
    body: JSON.stringify({
      stripe_event_id: event.id,
      event_type: event.type,
      payload: event,
    }),
  });
}

// ─── Stripe signature verification (Web Crypto, no SDK) ──────────────────────

async function verifyStripeSignature(payload: string, sigHeader: string, secret: string): Promise<StripeEvent> {
  const parts = sigHeader.split(",").map((p) => p.trim());
  let timestamp = "";
  let signatures: string[] = [];
  for (const part of parts) {
    const [k, v] = part.split("=");
    if (k === "t") timestamp = v;
    else if (k === "v1") signatures.push(v);
  }
  if (!timestamp || signatures.length === 0) {
    throw new Error("missing t or v1 in signature header");
  }

  // Reject timestamps older than 5 minutes (replay protection).
  const tsNum = parseInt(timestamp, 10);
  if (isNaN(tsNum) || Math.abs(Date.now() / 1000 - tsNum) > 300) {
    throw new Error("timestamp outside tolerance");
  }

  const signedPayload = `${timestamp}.${payload}`;
  const encoder = new TextEncoder();
  const key = await crypto.subtle.importKey(
    "raw",
    encoder.encode(secret),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const sigBuf = await crypto.subtle.sign("HMAC", key, encoder.encode(signedPayload));
  const expected = Array.from(new Uint8Array(sigBuf))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");

  // Compare against any of the provided v1 signatures (Stripe may send multiple).
  let matched = false;
  for (const sig of signatures) {
    if (timingSafeEqual(sig, expected)) {
      matched = true;
      break;
    }
  }
  if (!matched) throw new Error("signature mismatch");

  return JSON.parse(payload) as StripeEvent;
}

function timingSafeEqual(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i++) {
    diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  }
  return diff === 0;
}

// ─── Types ───────────────────────────────────────────────────────────────────

interface StripeEvent {
  id: string;
  type: string;
  data: { object: unknown };
}

interface CheckoutSession {
  client_reference_id?: string;
  customer?: string | { id: string };
}

interface StripeSubscription {
  status: string;
  metadata?: { user_id?: string };
}

interface PaymentIntent {
  metadata?: { user_id?: string; product_key?: string };
}
