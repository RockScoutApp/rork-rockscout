/**
 * Email verification endpoint — sends a 6-digit verification code via Resend
 * email to a newly signed-up user, and verifies the code when the user enters
 * it in the app.
 *
 * POST /email-verification { action: "send", email: "user@example.com" }
 *   Generates a 6-digit code, stores it in-memory keyed by email with a
 *   5-minute TTL, and sends it via Resend. Returns { ok: true }.
 *
 * POST /email-verification { action: "verify", email: "user@example.com", code: "123456" }
 *   Checks the code against the stored code for that email. Returns
 *   { ok: true, verified: true } or { ok: false, error: "..." }.
 *
 * Requires a RESEND_API_KEY env var on the Worker.
 */

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
  "Content-Type": "application/json",
};

const FROM = "RockScout <welcome@rockscout.app>";
const TAGLINE = "Made by a rockhounder, for rockhounders";
const CODE_TTL_MS = 5 * 60 * 1000; // 5 minutes

// In-memory code store keyed by email. Each entry has the code and expiry.
const codeStore = new Map<string, { code: string; expiresAt: number }>();

function generateCode(): string {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

function cleanupExpired() {
  const now = Date.now();
  for (const [key, entry] of codeStore) {
    if (now > entry.expiresAt) {
      codeStore.delete(key);
    }
  }
}

export async function handleEmailVerification(
  request: Request,
  env: { RESEND_API_KEY?: string },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  const headers = { ...corsHeaders, "Content-Type": "application/json" };

  const apiKey = env.RESEND_API_KEY;
  if (!apiKey) {
    return Response.json(
      { ok: false, error: "email_verification_not_configured" },
      { status: 503, headers },
    );
  }

  let body: { action?: string; email?: string; code?: string };
  try {
    body = await request.json();
  } catch {
    return Response.json(
      { ok: false, error: "invalid_json" },
      { status: 400, headers },
    );
  }

  const action = body.action;
  const email = body.email?.trim().toLowerCase();

  if (!email || !email.includes("@")) {
    return Response.json(
      { ok: false, error: "missing_email" },
      { status: 400, headers },
    );
  }

  cleanupExpired();

  if (action === "send") {
    const code = generateCode();
    codeStore.set(email, { code, expiresAt: Date.now() + CODE_TTL_MS });

    const html = `<!DOCTYPE html>
<html><body style="margin:0;padding:0;background:#F3EFE7;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:560px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:24px;">
      <div style="font-size:40px;">⛏️</div>
      <h1 style="color:#1C1A14;margin:8px 0 0;">Verify your email</h1>
    </div>
    <div style="background:#FAF8F4;border:1px solid #D3CAB4;border-radius:16px;padding:24px;">
      <p style="color:#1C1A14;font-size:16px;line-height:1.5;">
        Enter this code in the RockScout app to verify your email address and
        activate your account:
      </p>
      <div style="text-align:center;margin:24px 0;">
        <span style="font-size:36px;font-weight:800;letter-spacing:10px;color:#1C1A14;
                     background:#F3EFE7;border-radius:12px;padding:12px 24px;display:inline-block;">
          ${code}
        </span>
      </div>
      <p style="color:#514C42;font-size:14px;line-height:1.5;">
        This code expires in 5 minutes. If you didn't create a RockScout account,
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

This code expires in 5 minutes. If you didn't create a RockScout account, you can safely ignore this email.

${TAGLINE}`;

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
          subject: "Your RockScout verification code",
          html,
          text,
        }),
      });
      if (!res.ok) {
        const errText = await res.text();
        console.error("Resend verification email failed:", res.status, errText);
        return Response.json(
          { ok: false, error: "resend_failed", status: res.status },
          { status: 502, headers },
        );
      }
      return Response.json({ ok: true }, { status: 200, headers });
    } catch (err) {
      console.error("Resend verification email exception:", err);
      return Response.json(
        { ok: false, error: "resend_exception" },
        { status: 502, headers },
      );
    }
  }

  if (action === "verify") {
    const submittedCode = body.code?.trim();
    const entry = codeStore.get(email);

    if (!entry || Date.now() > entry.expiresAt) {
      codeStore.delete(email);
      return Response.json(
        { ok: false, error: "Code expired. Please request a new one." },
        { status: 200, headers },
      );
    }

    if (submittedCode === entry.code) {
      codeStore.delete(email);
      return Response.json({ ok: true, verified: true }, { status: 200, headers });
    }

    return Response.json(
      { ok: false, error: "Invalid code. Try again." },
      { status: 200, headers },
    );
  }

  return Response.json(
    { ok: false, error: "Unknown action. Use 'send' or 'verify'." },
    { status: 400, headers },
  );
}
