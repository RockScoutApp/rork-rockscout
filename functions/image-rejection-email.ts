/**
 * Image rejection email endpoint — sends a branded RockScout email to a user
 * whose uploaded image (profile background or field capture) was rejected by
 * a developer in the moderation queue due to profanity or sexually explicit
 * content.
 *
 * Expects POST { email, type, displayName } with a Bearer token.
 * Requires RESEND_API_KEY env var on the Worker.
 */

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
  "Content-Type": "application/json",
};

const FROM = "RockScout <noreply@rockscout.app>";
const TAGLINE = "Made by a rockhounder, for rockhounders";

export async function handleImageRejectionEmail(
  request: Request,
  env: { RESEND_API_KEY?: string },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  const apiKey = env.RESEND_API_KEY;
  if (!apiKey) {
    return Response.json(
      { ok: false, error: "email_not_configured" },
      { status: 503, headers: CORS },
    );
  }

  let body: { email?: string; type?: string; displayName?: string };
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

  const type = body.type ?? "image";
  const displayName = body.displayName?.trim() || "Rock Scout";
  const typeLabel =
    type === "profile_background" ? "profile background image" : "field capture image";

  const html = `<!DOCTYPE html>
<html><body style="margin:0;padding:0;background:#F3EFE7;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:560px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:24px;">
      <div style="font-size:40px;">⛏️</div>
      <h1 style="color:#1C1A14;margin:8px 0 0;">Image Rejected, ${displayName}</h1>
    </div>
    <div style="background:#FAF8F4;border:1px solid #D3CAB4;border-radius:16px;padding:24px;">
      <p style="color:#1C1A14;font-size:16px;line-height:1.5;">
        Your ${typeLabel} was reviewed by our team and unfortunately it couldn't
        be approved. The image was flagged for profanity or sexually explicit
        content, which goes against RockScout's family-friendly policy.
      </p>
      <p style="color:#4A453C;font-size:15px;line-height:1.5;">
        Please upload another image and try again. We want RockScout to be a
        safe and welcoming space for rockhounds of all ages.
      </p>
      <div style="text-align:center;margin:24px 0;">
        <p style="color:#8A8475;font-size:14px;">Let's keep it clean, folks. 🪨</p>
      </div>
    </div>
    <p style="text-align:center;color:#8A8475;font-size:13px;margin-top:24px;">
      ${TAGLINE}
    </p>
  </div>
</body></html>`;

  const text = `Image Rejected, ${displayName}

Your ${typeLabel} was reviewed by our team and unfortunately it couldn't be approved. The image was flagged for profanity or sexually explicit content, which goes against RockScout's family-friendly policy.

Please upload another image and try again. We want RockScout to be a safe and welcoming space for rockhounds of all ages.

Let's keep it clean, folks.

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
        subject: "Your RockScout image was rejected",
        html,
        text,
      }),
    });
    if (!res.ok) {
      const errText = await res.text();
      console.error("Resend image rejection email failed:", res.status, errText);
      return Response.json(
        { ok: false, error: "resend_failed", status: res.status },
        { status: 502, headers: CORS },
      );
    }
    return Response.json({ ok: true }, { status: 200, headers: CORS });
  } catch (err) {
    console.error("Resend image rejection email exception:", err);
    return Response.json(
      { ok: false, error: "resend_exception" },
      { status: 502, headers: CORS },
    );
  }
}
