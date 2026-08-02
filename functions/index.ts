import { handleIdentify, handleClarify, handleArtifactDetect } from "./identify";
// RockScout backend — Cloudflare Worker entry (auth + rate-limit enabled).
// Routes: /ping, /identify, /identify/clarify, /identify/artifact-detect, /app-version, /welcome-email, /image-rejection-email, /referral/*, /dev-sms-verify, /push/*.
import { handleAppVersion } from "./app-version";
import { handleWelcomeEmail } from "./welcome-email";
import { handleImageRejectionEmail } from "./image-rejection-email";
import { handleReferral } from "./referral";
import { handleTrial } from "./trial";
import { handleDevSmsVerify } from "./dev-sms-verify";
import { handleDeleteAccount } from "./delete-account";
import { handleEmailVerification, handleVerifyEmailGet } from "./email-verification";
import { handleEmbeddingsBackfill } from "./embeddings-backfill";
import { handleArtifactsBackfill } from "./artifacts-backfill";
import { handleSpecimenCatalogBackfill } from "./specimen-catalog-backfill";
import { handlePush } from "./push";
import { handleMuseums } from "./museums";
import { handleEntitlement } from "./entitlement";
import { handleSettingsBackup } from "./settings-backup";
import { handleImageProxy, buildProxyCors } from "./image-proxy";
import { handleErrorReport } from "./error-report";
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

    // GET /verify-email — public click-to-verify callback from the email link.
    // No app-key required: the HMAC token in the URL IS the authentication.
    // The handler validates the token statelessly and redirects to the app.
    if (url.pathname === "/verify-email" && request.method === "GET") {
      return handleVerifyEmailGet(
        request,
        env as unknown as {
          RESEND_API_KEY?: string;
          EXPO_PUBLIC_RORK_APP_KEY?: string;
          EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
          SUPABASE_SERVICE_ROLE_KEY?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
        },
      );
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

    if (url.pathname === "/identify/artifact-detect" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/identify/artifact-detect", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleArtifactDetect(request, env, cors);
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
      const { action, replay } = await readActionAndReplay(request);
      const guard = guardEndpoint(
        request,
        env,
        `/dev-sms-verify:${action}`,
        cors,
        env.RATE_LIMIT_KV,
      );
      if (guard) return guard;
      return handleDevSmsVerify(
        replay,
        env as unknown as {
          RESEND_API_KEY?: string;
          DEV_2FA_EMAIL_TO?: string;
          EXPO_PUBLIC_RORK_APP_KEY?: string;
          EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
        },
        cors,
      );
    }

    if (url.pathname === "/email-verification" && request.method === "POST") {
      const { action, replay } = await readActionAndReplay(request);
      const guard = guardEndpoint(
        request,
        env,
        `/email-verification:${action}`,
        cors,
        env.RATE_LIMIT_KV,
      );
      if (guard) return guard;
      return handleEmailVerification(
        replay,
        env as unknown as {
          RESEND_API_KEY?: string;
          EXPO_PUBLIC_RORK_APP_KEY?: string;
          EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
          SUPABASE_SERVICE_ROLE_KEY?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
        },
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
          EXPO_PUBLIC_SUPABASE_URL?: string;
          SUPABASE_SERVICE_ROLE_KEY?: string;
        },
        cors,
      );
    }

    // Web Push subscribe / unsubscribe / test / notify / send.
    // GET /push/key is public — clients need the application-server key before
    // they can subscribe, and it is not a secret.
    if (url.pathname.startsWith("/push/") && (request.method === "POST" || request.method === "GET")) {
      if (!(url.pathname === "/push/key" && request.method === "GET")) {
        const rateLimitPath = url.pathname === "/push/send" ? "/push/send" : "/push/subscribe";
        const guard = guardEndpoint(request, env, rateLimitPath, cors, env.RATE_LIMIT_KV);
        if (guard) return guard;
      }
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

    // Artifact embeddings backfill — admin-triggered, toolkit-secret guarded.
    // Populates the artifact_embeddings table from ARTIFACT_DB for the
    // embedding-first artifact identification pipeline. Idempotent upsert.
    if (url.pathname === "/artifacts-backfill" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/artifacts-backfill", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleArtifactsBackfill(
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

    // Museum finder — queries OpenStreetMap for artifact-relevant museums.
    // Used by the "Ask an Expert" feature on the artifact uncertainty card.
    if (url.pathname === "/museums" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/museums", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleMuseums(request, env, cors);
    }

    // Settings backup/restore — used by the signing-conflict flow to preserve
    // user data across uninstall. Backup is stored in Cloudflare KV with a 30-day TTL.
    if (
      (url.pathname === "/settings/backup" && request.method === "PUT") ||
      (url.pathname === "/settings/restore" && request.method === "GET")
    ) {
      const rateLimitPath = url.pathname === "/settings/backup" ? "/settings/backup" : "/settings/restore";
      const guard = guardEndpoint(request, env, rateLimitPath, cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleSettingsBackup(
        request,
        env as unknown as {
          SUPABASE_SERVICE_ROLE_KEY?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
        },
        cors,
      );
    }

    // Entitlement bridge — checks RevenueCat for active Premium, writes
    // is_pro back to the Supabase profile. Called by the web app on sign-in
    // and Paywall open, and by Android after a purchase.
    if (url.pathname === "/entitlement" && request.method === "POST") {
      const guard = guardEndpoint(request, env, "/entitlement", cors, env.RATE_LIMIT_KV);
      if (guard) return guard;
      return handleEntitlement(
        request,
        env as unknown as {
          REVENUECAT_SECRET_API_KEY?: string;
          SUPABASE_SERVICE_ROLE_KEY?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
        },
        cors,
      );
    }

    // Central error reporting — app-key auth, no rate limit (errors should
    // always be accepted). Deduplicated on the server side to prevent floods.
    if (url.pathname === "/error-report" && request.method === "POST") {
      return handleErrorReport(
        request,
        env as unknown as {
          EXPO_PUBLIC_RORK_APP_KEY?: string;
          EXPO_PUBLIC_SUPABASE_URL?: string;
          SUPABASE_SERVICE_ROLE_KEY?: string;
        },
        cors,
      );
    }

    // Image caching proxy — public GET, no auth required.
    // Proxies r2-pub.rork.com images with immutable cache headers so
    // Cloudflare's edge CDN caches them (r2-pub returns no cache-control,
    // so without this every image request hits origin).
    if (url.pathname === "/img" && request.method === "GET") {
      return handleImageProxy(request, buildProxyCors(request));
    }

    // CORS preflight for /img
    if (url.pathname === "/img" && request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: buildProxyCors(request) });
    }

    return new Response("not found", { status: 404, headers: cors });
  },
};

/**
 * Reads the JSON body once to learn which `action` a verification request is
 * performing, and returns a replayable Request carrying the same body.
 *
 * Rate limits are applied per action: sending an email is throttled, checking
 * a code is not. Without this, "send" drained the shared per-endpoint bucket
 * and the immediately-following "verify" was rejected with 429.
 */
async function readActionAndReplay(
  request: Request,
): Promise<{ action: "send" | "verify" | "unknown"; replay: Request }> {
  const bodyText = await request.text();
  let action: "send" | "verify" | "unknown" = "unknown";
  try {
    const parsed = JSON.parse(bodyText) as { action?: string };
    if (parsed.action === "send" || parsed.action === "verify") {
      action = parsed.action;
    }
  } catch {
    // Malformed body — the handler will return the proper 400.
  }
  const replay = new Request(request.url, {
    method: request.method,
    headers: request.headers,
    body: bodyText,
  });
  return { action, replay };
}

type Env = {
  EXPO_PUBLIC_TOOLKIT_URL: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY: string;
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
  REFERRAL_KV?: KVNamespace;
  TRIAL_KV?: KVNamespace;
  RATE_LIMIT_KV?: KVNamespace;
  RESEND_API_KEY?: string;
  DEV_2FA_EMAIL_TO?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  REVENUECAT_SECRET_API_KEY?: string;
  VAPID_PUBLIC_KEY?: string;
  VAPID_PRIVATE_KEY?: string;
};

// Re-export for auth module type compatibility.
export { getClientIp };
