/**
 * Report notification email endpoint — sends an email to a user who has been
 * reported (manually or automatically via the profanity/self-harm system).
 *
 * POST /report-notification-email
 * Body: {
 *   reportedUserId: string,
 *   reportedEmail: string,
 *   reportReason: string,
 *   reportCount: number,
 *   source: "manual" | "auto_profanity" | "auto_self_harm"
 * }
 *
 * The email informs the user they received a report, explains why, and
 * provides a link to appeal via the Contact Us page.
 *
 * Also inserts a row into the `report_notifications` Supabase table for
 * audit purposes.
 */

const FROM = "RockScout <noreply@rockscout.net>";

export async function handleReportNotificationEmail(
  request: Request,
  env: {
    RESEND_API_KEY?: string;
    EXPO_PUBLIC_SUPABASE_URL?: string;
    SUPABASE_SERVICE_ROLE_KEY?: string;
    EXPO_PUBLIC_RORK_APP_KEY?: string;
  },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  const apiKey = env.RESEND_API_KEY;
  if (!apiKey) {
    return Response.json(
      { ok: false, error: "email_not_configured" },
      { status: 503, headers: corsHeaders },
    );
  }

  let body: {
    reportedUserId?: string;
    reportedEmail?: string;
    reportReason?: string;
    reportCount?: number;
    source?: string;
    reporterId?: string;
  };
  try {
    body = await request.json();
  } catch {
    return Response.json(
      { ok: false, error: "invalid_json" },
      { status: 400, headers: corsHeaders },
    );
  }

  const email = body.reportedEmail?.trim();
  if (!email || !email.includes("@")) {
    return Response.json(
      { ok: false, error: "missing_email" },
      { status: 400, headers: corsHeaders },
    );
  }

  const reason = body.reportReason || "A report was filed against your account.";
  const count = body.reportCount ?? 1;
  const source = body.source || "manual";

  const subject = "You received a report on RockScout";

  const html = `<!DOCTYPE html>
<html><body style="margin:0;padding:0;background:#F3EFE7;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:560px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:24px;">
      <div style="font-size:36px;">⚠️</div>
      <h1 style="color:#1C1A14;margin:8px 0 0;font-size:24px;">RockScout Report Notice</h1>
    </div>
    <div style="background:#FAF8F4;border:1px solid #D3CAB4;border-radius:16px;padding:24px;">
      <p style="color:#1C1A14;font-size:16px;line-height:1.5;">
        A report has been filed against your RockScout account. You currently have
        <strong>${count} active report${count === 1 ? "" : "s"}</strong>.
      </p>
      <p style="color:#4A453C;font-size:15px;line-height:1.5;">
        <strong>Reason:</strong> ${reason}
      </p>
      <p style="color:#4A453C;font-size:15px;line-height:1.5;">
        RockScout is a family-friendly community. Repeated violations may result in
        warnings, temporary suspensions, or a permanent ban.
      </p>
      <div style="text-align:center;margin:24px 0;">
        <a href="https://rockscout.net/support"
           style="background:#E8A33D;color:#1C1A14;font-weight:700;text-decoration:none;
                  padding:12px 24px;border-radius:12px;display:inline-block;font-size:15px;">
          Appeal this report
        </a>
      </div>
      <p style="color:#514C42;font-size:14px;line-height:1.5;">
        If you believe this report was filed in error, you can appeal by contacting
        RockScout support. Include the details of what happened and why you think the
        report is incorrect. We review all appeals and respond within 36 hours.
      </p>
    </div>
    <p style="text-align:center;color:#8A8475;font-size:13px;margin-top:24px;">
      Made by a rockhounder, for rockhounders
    </p>
  </div>
</body></html>`;

  const text = `RockScout Report Notice

A report has been filed against your RockScout account. You currently have ${count} active report${count === 1 ? "" : "s"}.

Reason: ${reason}

RockScout is a family-friendly community. Repeated violations may result in warnings, temporary suspensions, or a permanent ban.

If you believe this report was filed in error, you can appeal by contacting RockScout support at support@rockscout.net. We review all appeals and respond within 36 hours.

Made by a rockhounder, for rockhounders`;

  // Send the email via Resend
  let emailSent = false;
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
        subject,
        html,
        text,
      }),
    });
    if (res.ok) {
      emailSent = true;
    } else {
      const errText = await res.text();
      console.error("Resend report email failed:", res.status, errText);
    }
  } catch (err) {
    console.error("Resend report email exception:", err);
  }

  // Record the notification in Supabase for audit
  const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
  const serviceKey = env.SUPABASE_SERVICE_ROLE_KEY;
  if (supabaseUrl && serviceKey && body.reportedUserId) {
    try {
      await fetch(`${supabaseUrl}/rest/v1/report_notifications`, {
        method: "POST",
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
          "Content-Type": "application/json",
          Prefer: "return=minimal",
        },
        body: JSON.stringify({
          reported_user_id: body.reportedUserId,
          reporter_id: body.reporterId || null,
          reason,
          source,
          email_sent: emailSent,
          bell_notification_sent: true,
        }),
      });
    } catch (err) {
      console.error("Failed to record report notification:", err);
    }
  }

  // Also insert a bell notification for the reported user via Supabase
  if (supabaseUrl && serviceKey && body.reportedUserId) {
    try {
      await fetch(`${supabaseUrl}/rest/v1/rockscout_notifications`, {
        method: "POST",
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
          "Content-Type": "application/json",
          Prefer: "return=minimal",
        },
        body: JSON.stringify({
          user_id: body.reportedUserId,
          type: "report",
          title: "You received a report",
          body: reason,
          url: "/app/notifications",
          created_at: new Date().toISOString(),
        }),
      });
    } catch (err) {
      console.error("Failed to insert bell notification:", err);
    }
  }

  return Response.json(
    { ok: true, emailSent },
    { status: 200, headers: corsHeaders },
  );
}
