/**
 * Welcome email endpoint — sends a branded RockScout welcome email to a newly
 * signed-up user via Resend. The email includes a "Confirm Email" button (the
 * Supabase confirmation link), a short app description, and the tagline
 * "Made by a rockhounder, for rockhounders" at the end.
 *
 * Expects POST { email, confirmationUrl? } with a Bearer token from the
 * signed-in user. The confirmation URL is the Supabase email-confirmation link;
 * if omitted, the email body tells the user to check for the separate Supabase
 * confirmation email.
 *
 * Requires a RESEND_API_KEY env var on the Worker. If the key is absent the
 * endpoint returns 503 so the app can fall back to Supabase's built-in
 * confirmation email (which still works).
 */

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
  "Content-Type": "application/json",
};

const FROM = "RockScout <welcome@rockscout.app>";
const TAGLINE = "Made by a rockhounder, for rockhounders";

export async function handleWelcomeEmail(
  request: Request,
  env: { RESEND_API_KEY?: string; EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  const apiKey = env.RESEND_API_KEY;
  if (!apiKey) {
    return Response.json(
      { ok: false, error: "welcome_email_not_configured" },
      { status: 503, headers: CORS },
    );
  }

  let body: { email?: string; confirmationUrl?: string; displayName?: string };
  try {
    body = await request.json();
  } catch {
    return Response.json(
      { ok: false, error: "invalid_json" },
      { status: 400, headers: CORS },
    );
  }

  const email = body.email?.trim();
  if (!email || !email.includes("@")) {
    return Response.json(
      { ok: false, error: "missing_email" },
      { status: 400, headers: CORS },
    );
  }

  const confirmationUrl = body.confirmationUrl;
  const displayName = body.displayName?.trim() || "Rock Scout";

  const confirmButton = confirmationUrl
    ? `<div style="text-align:center;margin:28px 0;">
         <a href="${confirmationUrl}"
            style="background:#E8A33D;color:#1C1A14;font-weight:700;text-decoration:none;
                   padding:14px 28px;border-radius:12px;display:inline-block;font-size:16px;">
           Confirm Email
         </a>
       </div>`
    : `<p style="color:#514C42;">We've also sent a separate confirmation email — click the link inside to confirm your address.</p>`;

  const html = `<!DOCTYPE html>
<html><body style="margin:0;padding:0;background:#F3EFE7;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:560px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:24px;">
      <div style="font-size:40px;">⛏️</div>
      <h1 style="color:#1C1A14;margin:8px 0 0;">Welcome to RockScout, ${displayName}!</h1>
    </div>
    <div style="background:#FAF8F4;border:1px solid #D3CAB4;border-radius:16px;padding:24px;">
      <p style="color:#1C1A14;font-size:16px;line-height:1.5;">
        You're officially a RockScout. 🎉 RockScout is your field companion for
        identifying rocks, minerals, and fossils — point your camera, get an
        instant ID, and build your collection as you hunt.
      </p>
      <p style="color:#4A453C;font-size:15px;line-height:1.5;">
        Here's how to get started:
      </p>
      <ul style="color:#4A453C;font-size:15px;line-height:1.7;">
        <li><b>Identify</b> — snap a photo of any rock, mineral, or fossil for an instant AI identification.</li>
        <li><b>Explore</b> — browse 800+ specimens and dig sites across the country.</li>
        <li><b>Collect</b> — log your field captures, build your collection, and trade with other RockScouts.</li>
        <li><b>Connect</b> — turn on RockScout Club to scan for nearby hunters and coordinate meet-ups.</li>
      </ul>
      ${confirmButton}
      <p style="color:#514C42;font-size:14px;line-height:1.5;">
        Once confirmed, you can sign in on any device and your collection stays with you.
      </p>
    </div>
    <p style="text-align:center;color:#8A8475;font-size:13px;margin-top:24px;">
      ${TAGLINE}
    </p>
  </div>
</body></html>`;

  const text = `Welcome to RockScout, ${displayName}!

You're officially a RockScout. RockScout is your field companion for identifying rocks, minerals, and fossils — point your camera, get an instant ID, and build your collection as you hunt.

Here's how to get started:
- Identify — snap a photo of any rock, mineral, or fossil for an instant AI identification.
- Explore — browse 800+ specimens and dig sites across the country.
- Collect — log your field captures, build your collection, and trade with other RockScouts.
- Connect — turn on RockScout Club to scan for nearby hunters and coordinate meet-ups.

${confirmationUrl ? `Confirm your email: ${confirmationUrl}` : "We've also sent a separate confirmation email — click the link inside to confirm your address."}

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
        subject: "Welcome to RockScout — confirm your email",
        html,
        text,
      }),
    });
    if (!res.ok) {
      const errText = await res.text();
      console.error("Resend welcome email failed:", res.status, errText);
      return Response.json(
        { ok: false, error: "resend_failed", status: res.status },
        { status: 502, headers: CORS },
      );
    }
    return Response.json({ ok: true }, { status: 200, headers: CORS });
  } catch (err) {
    console.error("Resend welcome email exception:", err);
    return Response.json(
      { ok: false, error: "resend_exception" },
      { status: 502, headers: CORS },
    );
  }
}
