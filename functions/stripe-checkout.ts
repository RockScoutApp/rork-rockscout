/**
 * Stripe Checkout session creation — Cloudflare Worker endpoint.
 *
 * POST /stripe/checkout
 *   { type: "subscription" | "donation" | "tokens", priceId: string, userId: string, email: string }
 *   → { url: string }  (redirect the browser to this Stripe Checkout URL)
 *
 * Uses the official Stripe REST API (no SDK — Workers can't bundle Node-only
 * modules). The STRIPE_SECRET_KEY env var holds the `sk_...` key.
 *
 * Price IDs are resolved from the STRIPE_PRICE_* env vars so the client never
 * sends a raw Stripe price id — it sends a stable key like "premium-monthly".
 */

const CORS_JSON = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization, X-App-Key",
  "Content-Type": "application/json",
  "Vary": "Origin",
};

/** Stable client-side keys → Stripe price ids (env-configured). */
const PRICE_MAP: Record<string, string> = {
  "premium-monthly": "",
  "donation-2": "",
  "donation-4": "",
  "tokens-1": "",
  "tokens-4": "",
  "tokens-10": "",
};

/** One-time vs recurring mode per product type. */
const ONE_TIME_KEYS = new Set(["donation-2", "donation-4", "tokens-1", "tokens-4", "tokens-10"]);

const APP_URL = "https://jvns5dfy7fpytx79a2tb3-web.rork.live";

export async function handleStripeCheckout(
  request: Request,
  env: {
    STRIPE_SECRET_KEY?: string;
    EXPO_PUBLIC_RORK_APP_KEY?: string;
    EXPO_PUBLIC_SUPABASE_URL?: string;
    EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
  },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }
  const headers = { ...corsHeaders, ...CORS_JSON };

  // App-key auth (same gate as other protected endpoints).
  const expectedKey = env.EXPO_PUBLIC_RORK_APP_KEY;
  if (!expectedKey) {
    return Response.json({ error: "Server not configured for payments." }, { status: 503, headers });
  }
  const providedKey = request.headers.get("x-app-key");
  if (providedKey !== expectedKey) {
    return Response.json({ error: "Unauthorized." }, { status: 401, headers });
  }

  if (!env.STRIPE_SECRET_KEY) {
    return Response.json(
      { error: "Stripe is not yet configured. Payments coming soon — enjoy the free tier!" },
      { status: 503, headers },
    );
  }

  let body: { type?: string; priceId?: string; userId?: string; email?: string };
  try {
    body = (await request.json()) as typeof body;
  } catch {
    return Response.json({ error: "Invalid JSON body." }, { status: 400, headers });
  }

  const { type, priceId, userId, email } = body;
  if (!type || !priceId || !userId) {
    return Response.json({ error: "Missing type, priceId, or userId." }, { status: 400, headers });
  }

  // Resolve the stable key → Stripe price id from env.
  const stripePriceId = resolvePriceId(priceId, env);
  if (!stripePriceId) {
    return Response.json(
      { error: `Unknown price key: ${priceId}. Product not configured.` },
      { status: 400, headers },
    );
  }

  const isOneTime = ONE_TIME_KEYS.has(priceId);
  const mode = isOneTime ? "payment" : "subscription";

  // Build the Checkout Session via Stripe REST API.
  const params = new URLSearchParams();
  params.append("mode", mode);
  params.append("line_items[0][price]", stripePriceId);
  params.append("line_items[0][quantity]", "1");
  params.append("success_url", `${APP_URL}/app/paywall?status=success`);
  params.append("cancel_url", `${APP_URL}/app/paywall?status=cancelled`);
  params.append("client_reference_id", userId);
  if (email) params.append("customer_email", email);
  // Let Stripe build the customer so we can link it back via webhook.
  params.append("customer_creation", isOneTime ? "always" : "always");
  // We need payment_method_types for one-time (cards).
  if (isOneTime) {
    params.append("payment_method_types[0]", "card");
  }

  try {
    const resp = await fetch("https://api.stripe.com/v1/checkout/sessions", {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${env.STRIPE_SECRET_KEY}`,
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: params.toString(),
    });

    if (!resp.ok) {
      const errText = await resp.text();
      console.error("Stripe checkout create failed", resp.status, errText);
      return Response.json(
        { error: "Stripe could not start checkout. Please try again." },
        { status: 502, headers },
      );
    }

    const session = (await resp.json()) as { url?: string; id?: string };
    if (!session.url) {
      return Response.json({ error: "Stripe returned no checkout URL." }, { status: 502, headers });
    }

    return Response.json({ url: session.url, sessionId: session.id }, { headers });
  } catch (err) {
    console.error("Stripe checkout exception", err);
    return Response.json({ error: "Payment service unavailable." }, { status: 502, headers });
  }
}

/**
 * Resolve a stable client-side price key to a Stripe price id from env vars.
 * Env naming: STRIPE_PRICE_PREMIUM_MONTHLY, STRIPE_PRICE_DONATION_2, etc.
 */
function resolvePriceId(key: string, env: Record<string, string | undefined>): string | null {
  const envName =
    "STRIPE_PRICE_" +
    key
      .toUpperCase()
      .replace(/-/g, "_");
  const val = env[envName];
  return val && val.length > 0 ? val : null;
}
