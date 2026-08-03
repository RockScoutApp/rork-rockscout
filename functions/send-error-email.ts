/**
 * Fatal error email notification endpoint.
 *
 * Called by the PostgreSQL trigger on `rockscout_error_logs` whenever a new
 * row with `is_fatal = true` is inserted. Sends an email alert to
 * aaron_james_martin@yahoo.com via Resend so the developer is immediately
 * notified of fatal crashes.
 *
 * POST /send-error-email
 *   {
 *     errorType: string,
 *     errorMessage: string,
 *     stackTrace?: string,
 *     platform?: string,
 *     appVersion?: string,
 *     osVersion?: string,
 *     deviceModel?: string,
 *     userId?: string,
 *     screen?: string,
 *     createdAt?: string,
 *   }
 *   → { ok: boolean }
 *
 * Auth: app-key header (X-App-Key) — same as /error-report.
 * Requires RESEND_API_KEY env var.
 */

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, X-App-Key",
  "Content-Type": "application/json",
};

const FROM = "RockScout Alerts <noreply@rockscout.net>";
const TO = "aaron_james_martin@yahoo.com";
const TAGLINE = "Made by a rockhounder, for rockhounds";

interface FatalErrorPayload {
  errorType?: string;
  errorMessage?: string;
  stackTrace?: string;
  platform?: string;
  appVersion?: string;
  osVersion?: string;
  deviceModel?: string;
  userId?: string;
  screen?: string;
  createdAt?: string;
}

interface SendErrorEmailEnv {
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  RESEND_API_KEY?: string;
}

export async function handleSendErrorEmail(
  request: Request,
  env: SendErrorEmailEnv,
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  const headers = { ...corsHeaders, "Content-Type": "application/json" };

  // Validate app key
  const expectedKey = env.EXPO_PUBLIC_RORK_APP_KEY;
  if (!expectedKey) {
    return Response.json({ ok: false, error: "Server not configured." }, { status: 503, headers });
  }
  const providedKey = request.headers.get("x-app-key");
  if (providedKey !== expectedKey) {
    return Response.json({ ok: false, error: "Unauthorized." }, { status: 401, headers });
  }

  const apiKey = env.RESEND_API_KEY;
  if (!apiKey) {
    return Response.json({ ok: false, error: "email_not_configured" }, { status: 503, headers });
  }

  let body: FatalErrorPayload;
  try {
    body = await request.json() as FatalErrorPayload;
  } catch {
    return Response.json({ ok: false, error: "invalid_json" }, { status: 400, headers });
  }

  const errorType = body.errorType ?? "Unknown";
  const errorMessage = body.errorMessage ?? "(no message)";
  const platform = body.platform ?? "unknown";
  const timestamp = body.createdAt ?? new Date().toISOString();

  // Build a readable plain-text and HTML email
  const text = `FATAL ERROR ALERT — RockScout

Time: ${timestamp}
Platform: ${platform}
Screen: ${body.screen ?? "N/A"}
App Version: ${body.appVersion ?? "N/A"}
OS Version: ${body.osVersion ?? "N/A"}
Device: ${body.deviceModel ?? "N/A"}
User ID: ${body.userId ?? "N/A"}

Error Type: ${errorType}
Error Message: ${errorMessage}

Stack Trace:
${body.stackTrace ?? "(no stack trace)"}

---
${TAGLINE}`;

  const html = `<!DOCTYPE html>
<html><body style="margin:0;padding:0;background:#1C1A14;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:620px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:24px;">
      <div style="font-size:36px;">⚠️</div>
      <h1 style="color:#FF3B30;margin:8px 0 0;font-size:22px;">Fatal Error Alert</h1>
      <p style="color:#8A8475;font-size:13px;margin:4px 0 0;">RockScout Crash Notification</p>
    </div>
    <div style="background:#2A2820;border:1px solid #3A3830;border-radius:12px;padding:20px;">
      <table style="width:100%;font-size:14px;color:#F5F0E6;border-collapse:collapse;">
        <tr><td style="padding:4px 0;color:#8A8475;width:120px;">Time</td><td style="padding:4px 0;">${timestamp}</td></tr>
        <tr><td style="padding:4px 0;color:#8A8475;">Platform</td><td style="padding:4px 0;">${platform}</td></tr>
        <tr><td style="padding:4px 0;color:#8A8475;">Screen</td><td style="padding:4px 0;">${body.screen ?? "N/A"}</td></tr>
        <tr><td style="padding:4px 0;color:#8A8475;">App Version</td><td style="padding:4px 0;">${body.appVersion ?? "N/A"}</td></tr>
        <tr><td style="padding:4px 0;color:#8A8475;">OS Version</td><td style="padding:4px 0;">${body.osVersion ?? "N/A"}</td></tr>
        <tr><td style="padding:4px 0;color:#8A8475;">Device</td><td style="padding:4px 0;">${body.deviceModel ?? "N/A"}</td></tr>
        <tr><td style="padding:4px 0;color:#8A8475;">User ID</td><td style="padding:4px 0;">${body.userId ?? "N/A"}</td></tr>
      </table>
      <div style="margin:16px 0 8px;color:#FF3B30;font-weight:bold;font-size:15px;">${errorType}</div>
      <div style="color:#F5F0E6;font-size:14px;line-height:1.5;margin-bottom:12px;">${errorMessage}</div>
      ${body.stackTrace ? `
      <div style="margin-top:12px;color:#8A8475;font-size:12px;font-weight:bold;">Stack Trace:</div>
      <pre style="background:#141210;border:1px solid #3A3830;border-radius:8px;padding:12px;color:#C0C0C0;font-size:11px;line-height:1.4;overflow-x:auto;white-space:pre-wrap;word-wrap:break-word;">${body.stackTrace.replace(/</g, "&lt;").replace(/>/g, "&gt;")}</pre>
      ` : ""}
    </div>
    <p style="text-align:center;color:#8A8475;font-size:12px;margin-top:20px;">${TAGLINE}</p>
  </div>
</body></html>`;

  try {
    const res = await fetch("https://api.resend.com/emails", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${apiKey}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        from: FROM,
        to: [TO],
        subject: `[FATAL] ${errorType} on ${platform} — RockScout`,
        html,
        text,
      }),
    });
    if (!res.ok) {
      const errText = await res.text();
      console.error("[send-error-email] Resend failed:", res.status, errText);
      return Response.json({ ok: false, error: "resend_failed", status: res.status }, { status: 502, headers });
    }
    return Response.json({ ok: true }, { status: 200, headers });
  } catch (err) {
    console.error("[send-error-email] Exception:", err);
    return Response.json({ ok: false, error: "resend_exception" }, { status: 502, headers });
  }
}
