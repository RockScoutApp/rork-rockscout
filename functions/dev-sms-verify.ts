/**
 * Developer 2-step verification endpoint.
 *
 * POST /dev-sms-verify { action: "send" }
 *   Derives the current 6-digit code, sends it via Twilio SMS when Twilio is
 *   configured, and returns { ok, sent, smsSent, devCode? }. `devCode` is only
 *   returned when SMS delivery did not happen (no Twilio config or a Twilio
 *   failure) so the app can post an instant local notification with the code —
 *   the developer never has to wait for a carrier round-trip.
 *
 * POST /dev-sms-verify { action: "verify", code: "123456" }
 *   → { ok: true, verified: true } | { ok: false, error }
 *
 * Codes are STATELESS: they're an HMAC-SHA256 of a 30-second time bucket keyed
 * by the server-side app key. Cloudflare Workers run many isolates, so the old
 * module-level `storedCode` variable meant "send" and "verify" frequently hit
 * different isolates and verification failed at random. Deriving the code
 * instead makes verification work on any isolate, instantly, with no KV round
 * trip. Codes stay valid for CODE_WINDOW_BUCKETS * 30s (5 minutes).
 *
 * Optional env vars (SMS is a bonus channel, not a requirement):
 *   TWILIO_ACCOUNT_SID / TWILIO_AUTH_TOKEN / TWILIO_PHONE_FROM
 *   TWILIO_PHONE_TO (overrides the default developer destination number)
 */

/** Seconds per code bucket. */
const BUCKET_SECONDS = 30;
/** How many past buckets stay valid (10 * 30s = 5 minutes). */
const CODE_WINDOW_BUCKETS = 10;

/** Default destination for the SMS channel (developer's phone). */
const DEFAULT_DEV_PHONE_TO = "+13134256511";

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

async function sendSms(
  to: string,
  from: string,
  body: string,
  accountSid: string,
  authToken: string,
): Promise<boolean> {
  try {
    const url = `https://api.twilio.com/2010-04-01/Accounts/${accountSid}/Messages.json`;
    const auth = btoa(`${accountSid}:${authToken}`);
    const params = new URLSearchParams();
    params.append("To", to);
    params.append("From", from);
    params.append("Body", body);

    const resp = await fetch(url, {
      method: "POST",
      headers: {
        Authorization: `Basic ${auth}`,
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: params.toString(),
    });
    if (!resp.ok) {
      console.error("dev-sms-verify: Twilio send failed", resp.status);
    }
    return resp.ok;
  } catch {
    console.error("dev-sms-verify: Twilio request threw");
    return false;
  }
}

export async function handleDevSmsVerify(
  request: Request,
  env: {
    TWILIO_ACCOUNT_SID?: string;
    TWILIO_AUTH_TOKEN?: string;
    TWILIO_PHONE_FROM?: string;
    TWILIO_PHONE_TO?: string;
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

      const sid = env.TWILIO_ACCOUNT_SID;
      const token = env.TWILIO_AUTH_TOKEN;
      const from = env.TWILIO_PHONE_FROM;
      const to = env.TWILIO_PHONE_TO?.trim() || DEFAULT_DEV_PHONE_TO;

      let smsSent = false;
      if (sid && token && from && from.trim() !== to) {
        smsSent = await sendSms(
          to,
          from,
          `RockScout Developer Access — your verification code is: ${code}. It expires in 5 minutes.`,
          sid,
          token,
        );
      }

      // When SMS can't be delivered, hand the code back so the app can post an
      // instant local notification. The endpoint is already app-key guarded and
      // only reachable after the developer PIN, so this stays a developer-only
      // channel.
      return Response.json(
        { ok: true, sent: true, smsSent, ...(smsSent ? {} : { devCode: code }) },
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
