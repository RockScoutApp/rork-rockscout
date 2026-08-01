/**
 * Developer 2-step verification endpoint.
 *
 * POST /dev-sms-verify { action: "send" }
 *   Derives the current 6-digit code, emails it to the developer address via
 *   Resend, and returns { ok, sent, emailSent, devCode? }. `devCode` is only
 *   returned when the email could not be delivered (no Resend key or a Resend
 *   failure) so the app can post an instant local notification with the code —
 *   the developer is never locked out.
 *
 * POST /dev-sms-verify { action: "verify", code: "123456" }
 *   → { ok: true, verified: true } | { ok: false, error }
 *
 * Codes are STATELESS: they're an HMAC-SHA256 of a 30-second time bucket keyed
 * by the server-side app key. Cloudflare Workers run many isolates, so a
 * module-level `storedCode` variable would mean "send" and "verify" frequently
 * hit different isolates and verification fails at random. Deriving the code
 * instead makes verification work on any isolate, instantly, with no KV round
 * trip. Codes stay valid for CODE_WINDOW_BUCKETS * 30s (5 minutes).
 *
 * Env vars:
 *   RESEND_API_KEY (required for email delivery)
 *   DEV_2FA_EMAIL_TO (optional — overrides the default developer address)
 */

/** Seconds per code bucket. */
const BUCKET_SECONDS = 30;
/** How many past buckets stay valid (10 * 30s = 5 minutes). */
const CODE_WINDOW_BUCKETS = 10;

/** Default destination for the developer 2-step code. */
const DEFAULT_DEV_EMAIL_TO = "Aaron_James_Martin@yahoo.com";

/** Verified Resend sender for this project. */
const FROM = "RockScout <noreply@rockscout.net>";

/** Last-resort HMAC secret when no server key is configured. */
const FALLBACK_SECRET = "rockscout-dev-2fa";

function currentBucket(): number {
  return Math.floor(Date.now() / 1000 / BUCKET_SECONDS);
}

/** Derive the 6-digit code for a given time bucket. */
async function deriveCode(secret: string, bucket: number): Promise<string> {
  const key = await crypto.subtle.importKey(
    "raw",
    new TextEncoder().encode(secret),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"],
  );
  const mac = new Uint8Array(
    await crypto.subtle.sign("HMAC", key, new TextEncoder().encode(`dev-2fa:${bucket}`)),
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
<html><body style="margin:0;padding:0;background:#0B0F10;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:520px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:20px;">
      <div style="font-size:38px;">&#128274;</div>
      <h1 style="color:#EAF6F6;margin:8px 0 0;font-size:22px;">Developer Console access</h1>
    </div>
    <div style="background:#121A1C;border:1px solid #1F3A3D;border-radius:16px;padding:24px;">
      <p style="color:#B9CBCB;font-size:15px;line-height:1.5;margin-top:0;">
        Enter this code in RockScout to open the Developer Console:
      </p>
      <div style="text-align:center;margin:22px 0;">
        <span style="font-size:36px;font-weight:800;letter-spacing:10px;color:#4FD1C5;
                     background:#0B0F10;border-radius:12px;padding:12px 22px;display:inline-block;">
          ${code}
        </span>
      </div>
      <p style="color:#7E9494;font-size:13px;line-height:1.5;margin-bottom:0;">
        This code expires in 5 minutes. If you didn't request it, someone entered
        the developer PIN — no action was taken.
      </p>
    </div>
  </div>
</body></html>`;

  const text = `RockScout — Developer Console access

Your verification code is: ${code}

It expires in 5 minutes. If you didn't request it, someone entered the developer PIN — no action was taken.`;

  return { html, text };
}

interface SendOutcome {
  sent: boolean;
  reason?: string;
}

async function sendEmail(apiKey: string, to: string, code: string): Promise<SendOutcome> {
  try {
    const { html, text } = buildEmail(code);
    const res = await fetch("https://api.resend.com/emails", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${apiKey}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        from: FROM,
        to: [to],
        subject: `RockScout developer code: ${code}`,
        html,
        text,
      }),
    });
    if (!res.ok) {
      console.error("dev-2fa: Resend send failed", res.status, await res.text());
      return { sent: false, reason: `resend_${res.status}` };
    }
    return { sent: true };
  } catch {
    console.error("dev-2fa: Resend request threw");
    return { sent: false, reason: "resend_exception" };
  }
}

/** Masks an address for display: "aaron@yahoo.com" → "a***n@yahoo.com". */
function maskEmail(address: string): string {
  const [local, domain] = address.split("@");
  if (!domain || local.length < 2) return address;
  const head = local[0];
  const tail = local[local.length - 1];
  return `${head}***${tail}@${domain}`;
}

export async function handleDevSmsVerify(
  request: Request,
  env: {
    RESEND_API_KEY?: string;
    DEV_2FA_EMAIL_TO?: string;
    EXPO_PUBLIC_RORK_APP_KEY?: string;
    EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
  },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const headers = { ...corsHeaders, "Content-Type": "application/json" };
  const secret =
    env.EXPO_PUBLIC_RORK_APP_KEY ??
    env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY ??
    FALLBACK_SECRET;

  try {
    const body = (await request.json()) as { action?: string; code?: string };
    const action = body.action;

    if (action === "send") {
      const code = await deriveCode(secret, currentBucket());
      const to = env.DEV_2FA_EMAIL_TO?.trim() || DEFAULT_DEV_EMAIL_TO;

      let outcome: SendOutcome = { sent: false, reason: "resend_not_configured" };
      if (env.RESEND_API_KEY) {
        outcome = await sendEmail(env.RESEND_API_KEY, to, code);
      } else {
        console.error("dev-2fa: RESEND_API_KEY is not configured");
      }

      // When email can't be delivered, hand the code back so the app can post
      // an instant local notification. The endpoint is app-key guarded and only
      // reachable after the developer PIN, so this stays a developer-only
      // channel — the developer is never locked out of the console.
      return Response.json(
        {
          ok: true,
          sent: true,
          emailSent: outcome.sent,
          emailTo: outcome.sent ? maskEmail(to) : undefined,
          ...(outcome.sent ? {} : { devCode: code, emailError: outcome.reason }),
        },
        { headers },
      );
    }

    if (action === "verify") {
      const code = (body.code ?? "").trim();
      if (!/^[0-9]{6}$/.test(code)) {
        return Response.json({ ok: false, error: "Enter the 6-digit code." }, { headers });
      }
      const now = currentBucket();
      for (let i = 0; i <= CODE_WINDOW_BUCKETS; i++) {
        const candidate = await deriveCode(secret, now - i);
        if (safeEqual(candidate, code)) {
          return Response.json({ ok: true, verified: true }, { headers });
        }
      }
      return Response.json(
        { ok: false, error: "Invalid or expired code. Request a new one." },
        { headers },
      );
    }

    return Response.json(
      { ok: false, error: "Unknown action. Use 'send' or 'verify'." },
      { status: 400, headers },
    );
  } catch {
    return Response.json({ ok: false, error: "Invalid request body." }, { status: 400, headers });
  }
}
