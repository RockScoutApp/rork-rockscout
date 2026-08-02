/**
 * Email verification endpoint — sends a verification email via Resend with a
 * click-to-verify button (primary) and a 6-digit code (fallback), verifies
 * either method, and confirms the Supabase email so the user can sign in.
 *
 * POST /email-verification { action: "send", email }
 *   → { ok: true, expiresInSeconds } | { ok: false, error, reason }
 *
 * POST /email-verification { action: "verify", email, code, supabaseUserId? }
 *   → { ok: true, verified: true, emailConfirmed } | { ok: false, error }
 *
 * GET /verify-email?email=…&token=…
 *   → Validates the token, confirms the Supabase email, then redirects to
 *     rockscout://verify_email?email=…&verified=true (or false on failure).
 *     Returns an HTML interstitial page so the browser handles the custom-scheme
 *     redirect reliably (302s to custom schemes are dropped by some browsers).
 *
 * ── Why codes/tokens are STATELESS ──────────────────────────────────────
 * Cloudflare Workers run many isolates. The previous implementation stored
 * codes in a module-level Map with an optional KV fallback, so "send" and
 * "verify" frequently landed on different isolates and the user got a random
 * "Code expired. Please request a new one." even seconds after receiving it.
 *
 * Codes and tokens are now derived: HMAC-SHA256 over `email-verify:${email}:${bucket}`
 * keyed by the server-side app key. The code is truncated to 6 digits (RFC 4226
 * style); the token is the full HMAC hex string. Any isolate can validate either
 * instantly with zero storage and zero round trips, so verification works every
 * time. Both rotate every 30s and stay valid for CODE_WINDOW_BUCKETS * 30s
 * (10 minutes), which also makes "Resend" safe.
 *
 * Env vars:
 *   RESEND_API_KEY            (required — email delivery)
 *   EXPO_PUBLIC_RORK_APP_KEY  (HMAC secret; already present for app auth)
 *   SUPABASE_SERVICE_ROLE_KEY (required — marks the Supabase email confirmed)
 *   EXPO_PUBLIC_SUPABASE_URL
 */

import { resolveSupabaseUrl } from "./auth";

const FROM = "RockScout <welcome@rockscout.net>";
const TAGLINE = "Made by a rockhounder, for rockhounders";

/** Seconds per code bucket. */
const BUCKET_SECONDS = 30;
/** How many past buckets stay valid (20 * 30s = 10 minutes). */
const CODE_WINDOW_BUCKETS = 20;
/** Human-facing validity window. */
const CODE_TTL_SECONDS = BUCKET_SECONDS * CODE_WINDOW_BUCKETS;

/** Last-resort HMAC secret when no server key is configured. */
const FALLBACK_SECRET = "rockscout-email-verify";

interface EmailVerificationEnv {
  RESEND_API_KEY?: string;
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
  /** Legacy binding — no longer used, kept so existing bindings don't break typing. */
  VERIFICATION_KV?: KVNamespace;
}

/** Detect mobile devices from the User-Agent so we can route the verification
 *  redirect to the native app (mobile) or the PWA install page (desktop). */
function isMobileDevice(userAgent: string): boolean {
  const ua = userAgent.toLowerCase();
  return /android|iphone|ipad|ipod/.test(ua);
}

function currentBucket(): number {
  return Math.floor(Date.now() / 1000 / BUCKET_SECONDS);
}

function normalizeEmail(raw: string): string {
  return raw.trim().toLowerCase();
}

/** Derive the 6-digit code for a given email + time bucket. */
async function deriveCode(
  secret: string,
  email: string,
  bucket: number,
): Promise<string> {
  const key = await crypto.subtle.importKey(
    "raw",
    new TextEncoder().encode(secret),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const mac = new Uint8Array(
    await crypto.subtle.sign(
      "HMAC",
      key,
      new TextEncoder().encode(`email-verify:${email}:${bucket}`),
    ),
  );
  // Dynamic truncation (RFC 4226 style) → 6 digits.
  const offset = mac[mac.length - 1] & 0x0f;
  const binary =
    ((mac[offset] & 0x7f) << 24) |
    ((mac[offset + 1] & 0xff) << 16) |
    ((mac[offset + 2] & 0xff) << 8) |
    (mac[offset + 3] & 0xff);
  return (binary % 1_000_000).toString().padStart(6, "0");
}

/** Derive the full HMAC hex token for a given email + time bucket.
 *  This is the same HMAC as the code, but the full hex string — used for
 *  the click-to-verify link so the worker can validate it statelessly. */
async function deriveToken(
  secret: string,
  email: string,
  bucket: number,
): Promise<string> {
  const key = await crypto.subtle.importKey(
    "raw",
    new TextEncoder().encode(secret),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const mac = await crypto.subtle.sign(
    "HMAC",
    key,
    new TextEncoder().encode(`email-verify:${email}:${bucket}`),
  );
  return Array.from(new Uint8Array(mac))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
}

/** Constant-time-ish string compare. */
function safeEqual(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i++) diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return diff === 0;
}

function buildEmail(code: string, verifyUrl: string): { html: string; text: string } {
  const html = `<!DOCTYPE html>
<html><body style="margin:0;padding:0;background:#F3EFE7;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:560px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:24px;">
      <div style="font-size:40px;">&#9935;</div>
      <h1 style="color:#1C1A14;margin:8px 0 0;">Verify your email</h1>
    </div>
    <div style="background:#FAF8F4;border:1px solid #D3CAB4;border-radius:16px;padding:24px;">
      <p style="color:#1C1A14;font-size:16px;line-height:1.5;margin-top:0;">
        Tap the button below to verify your email address and activate your
        RockScout account:
      </p>
      <div style="text-align:center;margin:24px 0;">
        <a href="${verifyUrl}"
           style="display:inline-block;background:#C3D31A;color:#1C1A14;
                  font-size:18px;font-weight:700;text-decoration:none;
                  padding:16px 40px;border-radius:12px;">
          Verify My Email
        </a>
      </div>
      <p style="color:#514C42;font-size:14px;line-height:1.5;text-align:center;margin:16px 0 8px;">
        Or enter this code in the app:
      </p>
      <div style="text-align:center;margin:8px 0 16px;">
        <span style="font-size:28px;font-weight:800;letter-spacing:8px;color:#1C1A14;
                     background:#F3EFE7;border-radius:12px;padding:10px 20px;display:inline-block;">
          ${code}
        </span>
      </div>
      <p style="color:#514C42;font-size:14px;line-height:1.5;margin-bottom:0;">
        This link and code expire in 10 minutes. If you didn't create a RockScout
        account, you can safely ignore this email.
      </p>
    </div>
    <p style="text-align:center;color:#8A8475;font-size:13px;margin-top:24px;">
      ${TAGLINE}
    </p>
  </div>
</body></html>`;

  const text = `Verify your email

Open this link to verify your email address and activate your RockScout account:

${verifyUrl}

Or enter this code in the app:

${code}

This link and code expire in 10 minutes. If you didn't create a RockScout account, you can safely ignore this email.

${TAGLINE}`;

  return { html, text };
}

export async function handleEmailVerification(
  request: Request,
  env: EmailVerificationEnv,
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  const headers = { ...corsHeaders, "Content-Type": "application/json" };

  let body: {
    action?: string;
    email?: string;
    code?: string;
    supabaseUserId?: string;
  };
  try {
    body = await request.json();
  } catch {
    return Response.json({ ok: false, error: "invalid_json" }, { status: 400, headers });
  }

  const action = body.action;
  const email = normalizeEmail(body.email ?? "");

  if (!email || !email.includes("@")) {
    return Response.json(
      { ok: false, error: "Please enter a valid email address." },
      { status: 400, headers },
    );
  }

  const secret =
    env.EXPO_PUBLIC_RORK_APP_KEY ??
    env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY ??
    FALLBACK_SECRET;

  if (action === "send") {
    const apiKey = env.RESEND_API_KEY;
    if (!apiKey) {
      console.error("email-verification: RESEND_API_KEY is not configured");
      return Response.json(
        {
          ok: false,
          reason: "email_verification_not_configured",
          error:
            "Verification email service is temporarily unavailable. Please try again shortly.",
        },
        { status: 503, headers },
      );
    }

    const bucket = currentBucket();
    const code = await deriveCode(secret, email, bucket);
    const token = await deriveToken(secret, email, bucket);
    const origin = new URL(request.url).origin;
    const verifyUrl = `${origin}/verify-email?email=${encodeURIComponent(email)}&token=${token}`;
    const { html, text } = buildEmail(code, verifyUrl);

    try {
      const res = await fetch("https://api.resend.com/emails", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${apiKey}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          from: FROM,
          to: [email],
          subject: `Verify your email for RockScout`,
          html,
          text,
        }),
      });
      if (!res.ok) {
        const errText = await res.text();
        console.error("email-verification: Resend send failed", res.status, errText);
        return Response.json(
          {
            ok: false,
            reason: "resend_failed",
            status: res.status,
            error: "We couldn't send the code right now. Please try again.",
          },
          { status: 502, headers },
        );
      }
      return Response.json(
        { ok: true, expiresInSeconds: CODE_TTL_SECONDS },
        { status: 200, headers },
      );
    } catch (err) {
      console.error("email-verification: Resend request threw", err);
      return Response.json(
        {
          ok: false,
          reason: "resend_exception",
          error: "We couldn't send the code right now. Please try again.",
        },
        { status: 502, headers },
      );
    }
  }

  if (action === "verify") {
    const submitted = (body.code ?? "").trim();
    if (!/^[0-9]{6}$/.test(submitted)) {
      return Response.json(
        { ok: false, error: "Enter the 6-digit code." },
        { status: 200, headers },
      );
    }

    const now = currentBucket();
    let matched = false;
    for (let i = 0; i <= CODE_WINDOW_BUCKETS; i++) {
      const candidate = await deriveCode(secret, email, now - i);
      if (safeEqual(candidate, submitted)) {
        matched = true;
        break;
      }
    }

    if (!matched) {
      return Response.json(
        { ok: false, error: "Invalid or expired code. Request a new one." },
        { status: 200, headers },
      );
    }

    // Mark the Supabase email confirmed so the user can sign in right away —
    // our 6-digit code IS the verification, there's no separate link to click.
    // If the admin API fails (e.g. service-role key mismatch), we still return
    // verified=true because the code was correct — the client can then attempt
    // a fallback sign-in or surface a clear error instead of a generic failure.
    const confirm = await confirmSupabaseEmail(
      resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY),
      env.SUPABASE_SERVICE_ROLE_KEY,
      email,
      body.supabaseUserId,
    );

    return Response.json(
      {
        ok: true,
        verified: true,
        emailConfirmed: confirm.confirmed,
        ...(confirm.reason ? { confirmReason: confirm.reason } : {}),
        // When the admin confirm fails, include a user-facing hint so the
        // client can show an actionable message instead of a generic error.
        ...(confirm.confirmed
          ? {}
          : {
              confirmHint:
                "Your code is correct, but we couldn't fully activate your account. Please try signing in, or contact support if it persists.",
            }),
      },
      { status: 200, headers },
    );
  }

  return Response.json(
    { ok: false, error: "Unknown action. Use 'send' or 'verify'." },
    { status: 400, headers },
  );
}

interface ConfirmResult {
  confirmed: boolean;
  reason?: string;
}

/**
 * Confirm a Supabase user's email via the admin API (service-role key, bypasses
 * RLS). Resolves the user id from the email when the client didn't supply one,
 * so web and iOS don't have to track it. Best-effort — never throws.
 */
async function confirmSupabaseEmail(
  supabaseUrl: string | undefined,
  serviceKey: string | undefined,
  email: string,
  supabaseUserId?: string,
): Promise<ConfirmResult> {
  if (!supabaseUrl || !serviceKey) {
    console.error(
      "email-verification: SUPABASE_SERVICE_ROLE_KEY / SUPABASE_URL missing — cannot confirm email",
    );
    return { confirmed: false, reason: "supabase_admin_not_configured" };
  }

  // Detect key/project mismatch by decoding the JWT ref claim.
  // This produces a clear log entry instead of a cryptic admin_401.
  try {
    const payload = JSON.parse(
      atob(serviceKey.split(".")[1] ?? ""),
    ) as { ref?: string; role?: string };
    const projectRef = supabaseUrl.match(/https?:\/\/([a-z0-9]+)\.supabase\.co/)?.[1];
    if (payload.ref && projectRef && payload.ref !== projectRef) {
      console.error(
        `email-verification: SERVICE ROLE KEY MISMATCH — key is for project '${payload.ref}' but SUPABASE_URL is '${projectRef}'. ` +
          `Update SUPABASE_SERVICE_ROLE_KEY to the key for '${projectRef}'.`,
      );
      return {
        confirmed: false,
        reason: `service_key_project_mismatch:${payload.ref}_vs_${projectRef}`,
      };
    }
    if (payload.role && payload.role !== "service_role") {
      console.error(
        `email-verification: SUPABASE_SERVICE_ROLE_KEY has role '${payload.role}', expected 'service_role'`,
      );
    }
  } catch {
    // Not a JWT or decode failed — proceed with the API call; Supabase will reject if invalid.
  }

  const userId = supabaseUserId?.trim()
    ? supabaseUserId.trim()
    : await lookupUserIdByEmail(supabaseUrl, serviceKey, email);

  if (!userId) return { confirmed: false, reason: "user_not_found" };

  try {
    const resp = await fetch(
      `${supabaseUrl}/auth/v1/admin/users/${encodeURIComponent(userId)}`,
      {
        method: "PUT",
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email_confirm: true }),
      },
    );
    if (!resp.ok) {
      console.error(
        "email-verification: admin confirm failed",
        resp.status,
        await resp.text(),
      );
      return { confirmed: false, reason: `admin_${resp.status}` };
    }
    return { confirmed: true };
  } catch (err) {
    console.error("email-verification: admin confirm threw", err);
    return { confirmed: false, reason: "admin_exception" };
  }
}

/**
 * GET /verify-email?email=…&token=… — click-to-verify callback.
 *
 * Validates the HMAC token against the current and recent buckets, confirms
 * the Supabase email via admin API, then serves an HTML interstitial page
 * that attempts to open the app via `rockscout://verify_email` deep link.
 *
 * We use an HTML page with a meta-refresh + JS redirect rather than a 302
 * because many email clients and browsers silently drop redirects to custom
 * schemes. The page gives the user a manual fallback link too.
 */
export async function handleVerifyEmailGet(
  request: Request,
  env: EmailVerificationEnv,
): Promise<Response> {
  const url = new URL(request.url);
  const email = normalizeEmail(url.searchParams.get("email") ?? "");
  const token = (url.searchParams.get("token") ?? "").trim();

  const htmlBase = `<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1"><style>
body{margin:0;padding:0;background:#F3EFE7;font-family:-apple-system,Segoe UI,Roboto,sans-serif;}
.card{max-width:420px;margin:40px auto;padding:32px 24px;text-align:center;}
.btn{display:inline-block;background:#C3D31A;color:#1C1A14;font-size:18px;font-weight:700;text-decoration:none;padding:16px 40px;border-radius:12px;margin:16px 0;}
.sub{color:#514C42;font-size:14px;line-height:1.5;margin-top:12px;}
</style></head><body><div class="card">`;
  const htmlEnd = `</div></body></html>`;

  if (!email || !email.includes("@")) {
    return new Response(
      `${htmlBase}<h1>Invalid link</h1><p class="sub">This verification link is malformed. Please use the link from your RockScout email.</p>${htmlEnd}`,
      { status: 400, headers: { "Content-Type": "text/html; charset=utf-8" } },
    );
  }

  if (!token) {
    return new Response(
      `${htmlBase}<h1>Invalid link</h1><p class="sub">This verification link is missing a security token. Please use the link from your RockScout email.</p>${htmlEnd}`,
      { status: 400, headers: { "Content-Type": "text/html; charset=utf-8" } },
    );
  }

  const secret =
    env.EXPO_PUBLIC_RORK_APP_KEY ??
    env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY ??
    FALLBACK_SECRET;

  const now = currentBucket();
  let matched = false;
  for (let i = 0; i <= CODE_WINDOW_BUCKETS; i++) {
    const candidate = await deriveToken(secret, email, now - i);
    if (safeEqual(candidate, token)) {
      matched = true;
      break;
    }
  }

  if (!matched) {
    const deepLink = `rockscout://verify_email?email=${encodeURIComponent(email)}&verified=false&reason=expired`;
    const userAgent = request.headers.get("User-Agent") ?? "";
    const isMobile = isMobileDevice(userAgent);
    const origin = new URL(request.url).origin;
    const pwaInstallUrl = `${origin}/install?verified=false&email=${encodeURIComponent(email)}&reason=expired`;
    if (isMobile) {
      return new Response(
        `${htmlBase}<h1>Link expired</h1><p class="sub">This verification link has expired. Please open the RockScout app and tap "Resend code" to get a new email.</p><a href="${deepLink}" class="btn">Open RockScout</a><p class="sub" style="margin-top:16px;"><a href="${pwaInstallUrl}" style="color:#5C8C1A;text-decoration:underline;">Or open the web version</a></p>${htmlEnd}`,
        { status: 200, headers: { "Content-Type": "text/html; charset=utf-8" } },
      );
    } else {
      return new Response(
        `${htmlBase}<h1>Link expired</h1><p class="sub">This verification link has expired. Please open RockScout and request a new verification code.</p><a href="${pwaInstallUrl}" class="btn">Open RockScout Web</a>${htmlEnd}`,
        { status: 200, headers: { "Content-Type": "text/html; charset=utf-8" } },
      );
    }
  }

  // Token is valid — confirm the Supabase email via admin API.
  const confirm = await confirmSupabaseEmail(
    resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY),
    env.SUPABASE_SERVICE_ROLE_KEY,
    email,
  );

  const verified = confirm.confirmed ? "true" : "false";
  const reasonParam = confirm.reason ? `&reason=${encodeURIComponent(confirm.reason)}` : "";
  const deepLink = `rockscout://verify_email?email=${encodeURIComponent(email)}&verified=${verified}${reasonParam}`;

  // Detect mobile vs desktop to route the redirect correctly.
  // Mobile -> attempt the rockscout:// deep link to open the native app.
  // Desktop -> redirect to the PWA install page with a success banner.
  const userAgent = request.headers.get("User-Agent") ?? "";
  const isMobile = isMobileDevice(userAgent);
  const origin = new URL(request.url).origin;
  const pwaInstallUrl = `${origin}/install?verified=${verified}&email=${encodeURIComponent(email)}${reasonParam}`;

  if (confirm.confirmed) {
    if (isMobile) {
      // Mobile: attempt to open the native app via deep link, with a
      // timed fallback to the PWA install page if the app isn't installed.
      return new Response(
        `${htmlBase}<h1 style="color:#1C1A14;">Email verified!</h1><p class="sub">Your RockScout account is now active. Opening the app…</p><a href="${deepLink}" class="btn">Open RockScout App</a><p class="sub" style="margin-top:20px;">App not opening? <a href="${pwaInstallUrl}" style="color:#5C8C1A;text-decoration:underline;">Open the web version instead</a></p><script>try{window.location.href='${deepLink}';}catch(e){}setTimeout(function(){if(!document.hidden){window.location.href='${pwaInstallUrl}';}},2500);</script>${htmlEnd}`,
        { status: 200, headers: { "Content-Type": "text/html; charset=utf-8" } },
      );
    } else {
      // Desktop: redirect to the PWA install page with a success banner.
      return new Response(
        `${htmlBase}<h1 style="color:#1C1A14;">Email verified!</h1><p class="sub">Your RockScout account is now active. Redirecting to the web app…</p><a href="${pwaInstallUrl}" class="btn">Open RockScout</a><script>window.location.href='${pwaInstallUrl}';</script>${htmlEnd}`,
        { status: 200, headers: { "Content-Type": "text/html; charset=utf-8" } },
      );
    }
  } else {
    if (isMobile) {
      return new Response(
        `${htmlBase}<h1>Almost there!</h1><p class="sub">Your verification link was valid, but we couldn't fully activate your account (${confirm.reason ?? "unknown"}). Try opening the app and entering the code from your email.</p><a href="${deepLink}" class="btn">Open RockScout</a>${htmlEnd}`,
        { status: 200, headers: { "Content-Type": "text/html; charset=utf-8" } },
      );
    } else {
      return new Response(
        `${htmlBase}<h1>Almost there!</h1><p class="sub">Your verification link was valid, but we couldn't fully activate your account (${confirm.reason ?? "unknown"}). Try opening the web app and entering the code from your email.</p><a href="${pwaInstallUrl}" class="btn">Open RockScout Web</a>${htmlEnd}`,
        { status: 200, headers: { "Content-Type": "text/html; charset=utf-8" } },
      );
    }
  }
}

/** Look up a Supabase auth user id by email using the admin list endpoint. */
async function lookupUserIdByEmail(
  supabaseUrl: string,
  serviceKey: string,
  email: string,
): Promise<string | null> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/auth/v1/admin/users?page=1&per_page=200&filter=${encodeURIComponent(email)}`,
      {
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
        },
      },
    );
    if (!resp.ok) {
      console.error("email-verification: admin lookup failed", resp.status);
      return null;
    }
    const data = (await resp.json()) as { users?: { id: string; email?: string }[] };
    const match = (data.users ?? []).find(
      (u) => normalizeEmail(u.email ?? "") === email,
    );
    return match?.id ?? null;
  } catch (err) {
    console.error("email-verification: admin lookup threw", err);
    return null;
  }
}
