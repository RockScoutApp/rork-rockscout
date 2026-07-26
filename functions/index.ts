import { handleIdentify, handleClarify } from "./identify";
// RockScout backend — Cloudflare Worker entry (auth + rate-limit enabled).
// Routes: /ping, /identify, /identify/clarify, /app-version, /welcome-email, /image-rejection-email, /referral/*, /dev-sms-verify, /stripe/*, /push/*.
import { handleAppVersion } from "./app-version";
import { handleWelcomeEmail } from "./welcome-email";
import { handleImageRejectionEmail } from "./image-rejection-email";
import { handleReferral } from "./referral";
import { handleTrial } from "./trial";
import { handleDevSmsVerify } from "./dev-sms-verify";
import { handleDeleteAccount } from "./delete-account";
import { handleEmailVerification } from "./email-verification";
import { handleEmbeddingsBackfill } from "./embeddings-backfill";
import { handleSpecimenCatalogBackfill } from "./specimen-catalog-backfill";
import { handleStripeCheckout } from "./stripe-checkout";
import { handleStripeWebhook } from "./stripe-webhook";
import { handlePush } from "./push";
import {
  buildCorsHeaders,
  guardEndpoint,
  getClientIp,
} from "./auth";

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);
    const cors = buildCorsHeaders(request);

    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: cors });
    }

    if (url.pathname === "/ping") {
      return Response.json({ ok: true, now: new Date().toISOString() });
    }

    // /app-version is a lightweight public endpoint — auth but no rate limit issues.
    if (url.pathname === "/app-version" && request.method === "GET") {
      const guard = guardEndpoint(request, env, "/app-version", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleAppVersion(request);
    }

    // Protected AI endpoints — auth + rate limit.
    if (url.pathname === "/identify" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/identify", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleIdentify(request, env, cors);
    }

    if (url.pathname === "/identify/clarify" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/identify/clarify", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleClarify(request, env, cors);
    }

    // Email endpoints — auth + rate limit.
    if (url.pathname === "/welcome-email" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/welcome-email", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleWelcomeEmail(
        request,
        env as unknown as { RESEND_API_KEY?: string; EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string },
        cors,
      );
    }

    if (url.pathname === "/image-rejection-email" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/image-rejection-email", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleImageRejectionEmail(request, env as unknown as { RESEND_API_KEY?: string }, cors);
    }

    if (url.pathname.startsWith("/referral") && request.method === "POST") {
      const rateLimitPath = url.pathname.includes("/register") ? "/referral/register" : "/referral/send";
      const guard = guardEndpoint(request, env, rateLimitPath, cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleReferral(
        request,
        env as unknown as { RESEND_API_KEY?: string; REFERRAL_KV?: KVNamespace },
        cors,
      );
    }

    if (url.pathname.startsWith("/trial") && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/trial", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleTrial(
        request,
        env as unknown as { TRIAL_KV?: KVNamespace },
        cors,
      );
    }

    if (url.pathname === "/dev-sms-verify" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/dev-sms-verify", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleDevSmsVerify(
        request,
        env as unknown as {
          TWILIO_ACCOUNT_SID?: string;
          TWILIO_AUTH_TOKEN?: string;
          TWILIO_PHONE_FROM?: string;
        },
        cors,
      );
    }

    if (url.pathname === "/email-verification" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/email-verification", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleEmailVerification(
        request,
        env as unknown as { RESEND_API_KEY?: string; VERIFICATION_KV?: KVNamespace },
        cors,
      );
    }

    if (url.pathname === "/delete-account" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/delete-account", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleDeleteAccount(
        request,
        env as unknown as {
          RESEND_API_KEY?: string;
          EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
        },
        cors,
      );
    }

    // Stripe Checkout session creation — app-key guarded, rate limited.
    if (url.pathname === "/stripe/checkout" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/stripe/checkout", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleStripeCheckout(
        request,
        env as unknown as {
          STRIPE_SECRET_KEY?: string;
          EXPO_PUBLIC_RORK_APP_KEY?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
          EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
        },
        cors,
      );
    }

    // Stripe webhook — raw body, Stripe-Signature header. NOT app-key guarded
    // (Stripe sends its own signature). Auth is via HMAC signature verification.
    if (url.pathname === "/stripe/webhook" && request.method === "POST") {
      return handleStripeWebhook(
        request,
        env as unknown as {
          STRIPE_SECRET_KEY?: string;
          STRIPE_WEBHOOK_SECRET?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
          EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
          SUPABASE_SERVICE_ROLE_KEY?: string;
        },
        cors,
      );
    }

    // Web Push subscribe / unsubscribe / send.
    if (url.pathname.startsWith("/push/") && request.method === "POST") {
      const rateLimitPath = url.pathname === "/push/send" ? "/push/send" : "/push/subscribe";
      const guard = guardEndpoint(request, env, rateLimitPath, cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handlePush(
        request,
        env as unknown as {
          EXPO_PUBLIC_RORK_APP_KEY?: string;
          EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
          EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
          SUPABASE_SERVICE_ROLE_KEY?: string;
          VAPID_PUBLIC_KEY?: string;
          VAPID_PRIVATE_KEY?: string;
        },
        cors,
      );
    }

    // Embedding backfill — admin-triggered, toolkit-secret guarded, low rpm.
    // No app-key required (the toolkit secret gates this). Idempotent upsert,
    // safe to re-run.
    if (url.pathname === "/embeddings-backfill" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/embeddings-backfill", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleEmbeddingsBackfill(
        request,
        env as unknown as {
          EXPO_PUBLIC_TOOLKIT_URL?: string;
          EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
          EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
        },
        cors,
      );
    }

    // Specimen catalog backfill — admin-triggered, toolkit-secret guarded.
    // Populates the specimen_catalog reference table from SPECIMEN_DB.
    if (url.pathname === "/specimen-catalog-backfill" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/specimen-catalog-backfill", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleSpecimenCatalogBackfill(
        request,
        env as unknown as {
          EXPO_PUBLIC_SUPABASE_URL?: string;
          EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
        },
        cors,
      );
    }

    return new Response("not found", { status: 404, headers: cors });
  },
};

type Env = {
  EXPO_PUBLIC_TOOLKIT_URL: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY: string;
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  REFERRAL_KV?: KVNamespace;
  TRIAL_KV?: KVNamespace;
  RATE_LIMIT_KV?: KVNamespace;
  RESEND_API_KEY?: string;
  TWILIO_ACCOUNT_SID?: string;
  TWILIO_AUTH_TOKEN?: string;
  TWILIO_PHONE_FROM?: string;
  STRIPE_SECRET_KEY?: string;
  STRIPE_WEBHOOK_SECRET?: string;
  STRIPE_PRICE_PREMIUM_MONTHLY?: string;
  STRIPE_PRICE_DONATION_2?: string;
  STRIPE_PRICE_DONATION_4?: string;
  STRIPE_PRICE_TOKENS_1?: string;
  STRIPE_PRICE_TOKENS_4?: string;
  STRIPE_PRICE_TOKENS_10?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  VAPID_PUBLIC_KEY?: string;
  VAPID_PRIVATE_KEY?: string;
};

// Re-export for auth module type compatibility.
export { getClientIp };
