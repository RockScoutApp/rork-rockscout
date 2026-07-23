import { handleIdentify, handleClarify } from "./identify";
// RockScout backend — Cloudflare Worker entry.
// Routes: /ping, /identify, /identify/clarify, /app-version, /welcome-email, /image-rejection-email, /referral/*, /dev-sms-verify.
import { handleAppVersion } from "./app-version";
import { handleWelcomeEmail } from "./welcome-email";
import { handleImageRejectionEmail } from "./image-rejection-email";
import { handleReferral } from "./referral";
import { handleTrial } from "./trial";
import { handleDevSmsVerify } from "./dev-sms-verify";
import { handleDeleteAccount } from "./delete-account";
import { handleEmailVerification } from "./email-verification";

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
};

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url);

    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: CORS });
    }

    if (url.pathname === "/ping") {
      return Response.json({ ok: true, now: new Date().toISOString() });
    }

    if (url.pathname === "/identify" && request.method === "POST") {
      return handleIdentify(request, env, CORS);
    }

    if (url.pathname === "/identify/clarify" && request.method === "POST") {
      return handleClarify(request, env, CORS);
    }

    if (url.pathname === "/app-version" && request.method === "GET") {
      return handleAppVersion(request);
    }

    if (url.pathname === "/welcome-email" && request.method === "POST") {
      return handleWelcomeEmail(request, env as unknown as { RESEND_API_KEY?: string; EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string }, CORS);
    }

    if (url.pathname === "/image-rejection-email" && request.method === "POST") {
      return handleImageRejectionEmail(request, env as unknown as { RESEND_API_KEY?: string }, CORS);
    }

    if (url.pathname.startsWith("/referral") && request.method === "POST") {
      return handleReferral(
        request,
        env as unknown as { RESEND_API_KEY?: string; REFERRAL_KV?: KVNamespace },
        CORS,
      );
    }

    if (url.pathname.startsWith("/trial") && request.method === "POST") {
      return handleTrial(
        request,
        env as unknown as { TRIAL_KV?: KVNamespace },
        CORS,
      );
    }

    if (url.pathname === "/dev-sms-verify" && request.method === "POST") {
      return handleDevSmsVerify(
        request,
        env as unknown as {
          TWILIO_ACCOUNT_SID?: string;
          TWILIO_AUTH_TOKEN?: string;
          TWILIO_PHONE_FROM?: string;
        },
        CORS,
      );
    }

    if (url.pathname === "/email-verification" && request.method === "POST") {
      return handleEmailVerification(
        request,
        env as unknown as { RESEND_API_KEY?: string },
        CORS,
      );
    }

    if (url.pathname === "/delete-account" && request.method === "POST") {
      return handleDeleteAccount(
        request,
        env as unknown as {
          RESEND_API_KEY?: string;
          EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
        },
        CORS,
      );
    }

    return new Response("not found", { status: 404 });
  },
};

type Env = {
  EXPO_PUBLIC_TOOLKIT_URL: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY: string;
  REFERRAL_KV?: KVNamespace;
  TRIAL_KV?: KVNamespace;
  RESEND_API_KEY?: string;
  TWILIO_ACCOUNT_SID?: string;
  TWILIO_AUTH_TOKEN?: string;
  TWILIO_PHONE_FROM?: string;
};
