/**
 * Developer SMS verification endpoint — sends a 6-digit code via Twilio SMS
 * to a hardcoded developer phone number after the dev PIN is entered correctly.
 *
 * POST /dev-sms-verify { action: "send" }
 *   Generates a 6-digit code, sends it via Twilio, stores it in-memory with a
 *   5-minute expiry. Also returns the code in the response so the app can show
 *   an instant push notification with the same PIN. Returns:
 *     { ok: true, sent: true, devCode: "123456" }
 *
 * POST /dev-sms-verify { action: "verify", code: "123456" }
 *   Checks the code against the stored code. Returns { ok: true, verified: true }
 *   or { ok: false, error: "Invalid or expired code" }.
 *
 * Requires env vars:
 *   TWILIO_ACCOUNT_SID  — Twilio Account SID
 *   TWILIO_AUTH_TOKEN   — Twilio Auth Token
 *   TWILIO_PHONE_FROM   — Twilio phone number to send from
 *
 * The destination phone number is hardcoded to +13134256511.
 * Verification codes are always randomly generated 6-digit numbers.
 * The devCode is returned on every send request regardless of Twilio config
 * so the app can post an instant local notification as a reliable fallback.
 */

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
  "Content-Type": "application/json",
};

// In-memory code store with expiry. Keyed by a simple session counter.
let storedCode: string | null = null;
let codeExpiry = 0;
const CODE_TTL_MS = 5 * 60 * 1000; // 5 minutes

function generateCode(): string {
  return Math.floor(100000 + Math.random() * 900000).toString();
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

    return resp.ok;
  } catch {
    return false;
  }
}

const DEV_PHONE_TO = "+13134256511";

export async function handleDevSmsVerify(
  request: Request,
  env: {
    TWILIO_ACCOUNT_SID?: string;
    TWILIO_AUTH_TOKEN?: string;
    TWILIO_PHONE_FROM?: string;
  },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const headers = { ...corsHeaders, "Content-Type": "application/json" };

  try {
    const body = await request.json() as { action?: string; code?: string };
    const action = body.action;

    if (action === "send") {
      const code = generateCode();
      storedCode = code;
      codeExpiry = Date.now() + CODE_TTL_MS;

      const sid = env.TWILIO_ACCOUNT_SID;
      const token = env.TWILIO_AUTH_TOKEN;
      const from = env.TWILIO_PHONE_FROM;

      let smsSent = false;
      if (sid && token && from) {
        smsSent = await sendSms(
          DEV_PHONE_TO,
          from,
          `RockScout Developer Access — your verification code is: ${code}. It expires in 5 minutes.`,
          sid,
          token,
        );
      }

      // Always return the code so the app can post an instant local push
      // notification as a reliable fallback if SMS is delayed or fails.
      return Response.json(
        { ok: true, sent: true, smsSent, devCode: code },
        { headers },
      );
    }

    if (action === "verify") {
      const code = body.code;
      if (!storedCode || Date.now() > codeExpiry) {
        return Response.json(
          { ok: false, error: "Code expired. Please request a new one." },
          { headers },
        );
      }
      if (code === storedCode) {
        storedCode = null;
        codeExpiry = 0;
        return Response.json({ ok: true, verified: true }, { headers });
      }
      return Response.json(
        { ok: false, error: "Invalid code. Try again." },
        { headers },
      );
    }

    return Response.json(
      { ok: false, error: "Unknown action. Use 'send' or 'verify'." },
      { status: 400, headers },
    );
  } catch {
    return Response.json(
      { ok: false, error: "Invalid request body." },
      { status: 400, headers },
    );
  }
}
