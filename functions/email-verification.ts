/**
 * Email verification endpoint — sends a 6-digit verification code via Resend
 * to a newly signed-up user, verifies the code, and confirms the Supabase
 * email so the user can sign in immediately.
 *
 * POST /email-verification { action: "send", email }
 *   → { ok: true, expiresInSeconds } | { ok: false, error, reason }
 *
 * POST /email-verification { action: "verify", email, code, supabaseUserId? }
 *   → { ok: true, verified: true, emailConfirmed } | { ok: false, error }
 *
 * ── Why codes are STATELESS ──────────────────────────────────────────────
 * Cloudflare Workers run many isolates. The previous implementation stored
 * codes in a module-level Map with an optional KV fallback, so "send" and
 * "verify" frequently landed on different isolates and the user got a random
 * "Code expired. Please request a new one." even seconds after receiving it.
 *
 * Codes are now derived: HMAC-SHA256 over `${email}:${timeBucket}` keyed by
 * the server-side app key, truncated to 6 digits (RFC 4226 style). Any isolate
 * can validate any code instantly with zero storage and zero round trips, so
 * verification works every time. Codes rotate every 30s and stay valid for
 * CODE_WINDOW_BUCKETS * 30s (10 minutes), which also makes "Resend" safe — an
 * older code the user already typed still works.
 *
 * Env vars:
 *   RESEND_API_KEY            (required — email delivery)
 *   EXPO_PUBLIC_RORK_APP_KEY  (HMAC secret; already present for app auth)
 *   SUPABASE_SERVICE_ROLE_KEY (required — marks the Supabase email confirmed)
 *   EXPO_PUBLIC_SUPABASE_URL
 */

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

/** Constant-time-ish string compare. */
function safeEqual(a: string, b: string): boolean {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i++) diff |= a.charCodeAt(i) ^ b.charCodeAt(i);
  return diff === 0;
}

function buildEmail(code: string): { html: string; text: string } {
  const html = `<!DOCTYPE html>
<html><body style="margin:0;padding:0;background:#F3EFE7;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:560px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:24px;">
      <div style="font-size:40px;">&#9935;</div>
      <h1 style="color:#1C1A14;margin:8px 0 0;">Verify your email</h1>
    </div>
    <div style="background:#FAF8F4;border:1px solid #D3CAB4;border-radius:16px;padding:24px;">
      <p style="color:#1C1A14;font-size:16px;line-height:1.5;margin-top:0;">
        Enter this code in the RockScout app to verify your email address and
        activate your account:
      </p>
      <div style="text-align:center;margin:24px 0;">
        <span style="font-size:36px;font-weight:800;letter-spacing:10px;color:#1C1A14;
                     background:#F3EFE7;border-radius:12px;padding:12px 24px;display:inline-block;">
          ${code}
        </span>
      </div>
      <p style="color:#514C42;font-size:14px;line-height:1.5;margin-bottom:0;">
        This code expires in 10 minutes. If you didn't create a RockScout account,
        you can safely ignore this email.
      </p>
    </div>
    <p style="text-align:center;color:#8A8475;font-size:13px;margin-top:24px;">
      ${TAGLINE}
    </p>
  </div>
</body></html>`;

  const text = `Verify your email

Enter this code in the RockScout app to verify your email address and activate your account:

${code}

This code expires in 10 minutes. If you didn't create a RockScout account, you can safely ignore this email.

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

    const code = await deriveCode(secret, email, currentBucket());
    const { html, text } = buildEmail(code);

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
          subject: `Your RockScout verification code: ${code}`,
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
    const confirm = await confirmSupabaseEmail(
      env.EXPO_PUBLIC_SUPABASE_URL,
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
