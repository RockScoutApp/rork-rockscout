/**
 * Referral program endpoint — cross-device code storage, email delivery,
 * completion tracking, and sender reward crediting.
 *
 * POST /referral/send
 *   { code, senderEmail, recipientEmail }
 *   Stores the referral code -> sender email mapping and sends the recipient an
 *   email with the code and instructions. Used when an existing user taps
 *   "Send referral" in the app.
 *
 * POST /referral/verify
 *   { code, recipientEmail }
 *   Returns { ok: true, senderEmail: string } if the code is known, otherwise
 *   { ok: false }. Used when a new user signs up and enters a referral code.
 *
 * POST /referral/complete
 *   { code, recipientEmail }
 *   Called by the new user's app AFTER they've signed in and verified the code.
 *   Records the completion so the sender can be credited. The referral is only
 *   considered complete after both code verification AND sign-in have happened.
 *
 * POST /referral/check-completions
 *   { senderEmail }
 *   Returns { ok: true, completions: [{ code, recipientEmail, completedAt }] }
 *   — all completions the sender hasn't acknowledged yet. The sender's app polls
 *   this on sign-in and credits rewards for each completion.
 *
 * POST /referral/acknowledge
 *   { senderEmail, code, recipientEmail }
 *   Marks a completion as acknowledged so it won't be returned again by
 *   check-completions. Called after the sender's app has credited the reward.
 *
 * The worker stores codes in a Cloudflare KV namespace if REFERRAL_KV is bound,
 * otherwise it falls back to in-memory maps (ephemeral, but fine for demo
 * deployments without KV configured).
 */

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
  "Content-Type": "application/json",
};

const FROM = "RockScout <referrals@rockscout.net>";
const TAGLINE = "Made by a rockhounder, for rockhounders";

// In-memory fallback when REFERRAL_KV is not bound.
// code -> senderEmail
const memoryStore = new Map<string, string>();
// "senderEmail|code|recipientEmail" -> { senderEmail, recipientEmail, completedAt }
const memoryCompletions = new Map<string, CompletionRecord>();

interface CompletionRecord {
  code: string;
  senderEmail: string;
  recipientEmail: string;
  completedAt: string;
}

async function getSenderEmail(
  env: { REFERRAL_KV?: KVNamespace },
  code: string,
): Promise<string | null> {
  if (env.REFERRAL_KV) {
    return await env.REFERRAL_KV.get(`code:${code}`);
  }
  return memoryStore.get(code) ?? null;
}

async function setSenderEmail(
  env: { REFERRAL_KV?: KVNamespace },
  code: string,
  senderEmail: string,
): Promise<void> {
  if (env.REFERRAL_KV) {
    await env.REFERRAL_KV.put(`code:${code}`, senderEmail);
  } else {
    memoryStore.set(code, senderEmail);
  }
}

async function recordCompletion(
  env: { REFERRAL_KV?: KVNamespace },
  record: CompletionRecord,
): Promise<void> {
  const key = `comp:${record.senderEmail}|${record.code}|${record.recipientEmail}`;
  if (env.REFERRAL_KV) {
    await env.REFERRAL_KV.put(key, JSON.stringify(record));
  } else {
    memoryCompletions.set(key, record);
  }
}

async function getCompletionsForSender(
  env: { REFERRAL_KV?: KVNamespace },
  senderEmail: string,
): Promise<CompletionRecord[]> {
  const prefix = `comp:${senderEmail}|`;
  if (env.REFERRAL_KV) {
    const list = await env.REFERRAL_KV.list({ prefix });
    const results: CompletionRecord[] = [];
    for (const key of list.keys) {
      const raw = await env.REFERRAL_KV.get(key.name);
      if (raw) {
        try {
          results.push(JSON.parse(raw) as CompletionRecord);
        } catch {
          // skip malformed entries
        }
      }
    }
    return results;
  }
  return Array.from(memoryCompletions.entries())
    .filter(([k]) => k.startsWith(prefix))
    .map(([, v]) => v);
}

async function acknowledgeCompletion(
  env: { REFERRAL_KV?: KVNamespace },
  senderEmail: string,
  code: string,
  recipientEmail: string,
): Promise<void> {
  const key = `comp:${senderEmail}|${code}|${recipientEmail}`;
  if (env.REFERRAL_KV) {
    await env.REFERRAL_KV.delete(key);
  } else {
    memoryCompletions.delete(key);
  }
}

export async function handleReferral(
  request: Request,
  env: { RESEND_API_KEY?: string; REFERRAL_KV?: KVNamespace },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  let body: Record<string, string> = {};
  try {
    body = await request.json();
  } catch {
    return Response.json({ ok: false, error: "invalid_json" }, { status: 400, headers: CORS });
  }

  const url = new URL(request.url);

  if (url.pathname === "/referral/send" && request.method === "POST") {
    return handleSend(body, env, corsHeaders);
  }

  // Register-only mode: stores the code→sender mapping without sending an email.
  // Android sends an empty recipientEmail to register — this route accepts that.
  if (url.pathname === "/referral/register" && request.method === "POST") {
    return handleRegister(body, env, corsHeaders);
  }

  if (url.pathname === "/referral/verify" && request.method === "POST") {
    return handleVerify(body, env, corsHeaders);
  }

  if (url.pathname === "/referral/complete" && request.method === "POST") {
    return handleComplete(body, env, corsHeaders);
  }

  if (url.pathname === "/referral/check-completions" && request.method === "POST") {
    return handleCheckCompletions(body, env, corsHeaders);
  }

  if (url.pathname === "/referral/acknowledge" && request.method === "POST") {
    return handleAcknowledge(body, env, corsHeaders);
  }

  return Response.json({ ok: false, error: "not_found" }, { status: 404, headers: CORS });
}

async function handleSend(
  body: Record<string, string>,
  env: { RESEND_API_KEY?: string; REFERRAL_KV?: KVNamespace },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const code = body.code?.trim().toUpperCase();
  const senderEmail = body.senderEmail?.trim().toLowerCase();
  const recipientEmail = body.recipientEmail?.trim().toLowerCase();
  const senderName = body.senderName?.trim() || "A RockScout friend";

  if (!code || !code.startsWith("ROCK-")) {
    return Response.json({ ok: false, error: "invalid_code" }, { status: 400, headers: CORS });
  }
  if (!senderEmail || !senderEmail.includes("@")) {
    return Response.json({ ok: false, error: "invalid_sender_email" }, { status: 400, headers: CORS });
  }
  if (!recipientEmail || !recipientEmail.includes("@")) {
    return Response.json({ ok: false, error: "invalid_recipient_email" }, { status: 400, headers: CORS });
  }

  // Collision guard: if this code was already issued to a different sender,
  // reject instead of overwriting the existing mapping — otherwise a new user
  // verifying the colliding code would credit the wrong sender.
  const existingSender = await getSenderEmail(env, code);
  if (existingSender && existingSender !== senderEmail) {
    return Response.json({ ok: false, error: "code_taken" }, { status: 409, headers: CORS });
  }

  await setSenderEmail(env, code, senderEmail);

  const apiKey = env.RESEND_API_KEY;
  if (!apiKey) {
    return Response.json({ ok: false, error: "email_not_configured" }, { status: 503, headers: CORS });
  }

  const html = `<!DOCTYPE html>
<html><body style="margin:0;padding:0;background:#F3EFE7;font-family:-apple-system,Segoe UI,Roboto,sans-serif;">
  <div style="max-width:560px;margin:0 auto;padding:32px 24px;">
    <div style="text-align:center;margin-bottom:24px;">
      <div style="font-size:40px;">⛏️</div>
      <h1 style="color:#1C1A14;margin:8px 0 0;">You've been invited to RockScout!</h1>
    </div>
    <div style="background:#FAF8F4;border:1px solid #D3CAB4;border-radius:16px;padding:24px;">
      <p style="color:#1C1A14;font-size:16px;line-height:1.5;">
        <strong>${senderName}</strong> wants you to join them on RockScout — the rockhounder's field companion.
      </p>
      <p style="color:#4A453C;font-size:15px;line-height:1.5;">
        Your personal referral code is:
      </p>
      <div style="text-align:center;margin:24px 0;">
        <span style="display:inline-block;background:#1C1A14;color:#E8A33D;font-family:monospace;font-size:28px;font-weight:700;padding:16px 28px;border-radius:12px;letter-spacing:1px;">${code}</span>
      </div>
      <p style="color:#4A453C;font-size:15px;line-height:1.5;">
        How to use it:
      </p>
      <ol style="color:#4A453C;font-size:15px;line-height:1.7;">
        <li>Download RockScout and create your account.</li>
        <li>During your first sign-in, enter the code above when asked.</li>
        <li>You and ${senderName} will automatically become RockScout Friends so you can find each other easily.</li>
      </ol>
      <p style="color:#1C1A14;font-size:15px;line-height:1.5;">
        <strong>Free gifts:</strong> as a referred new RockScout you get starter ID tokens and a full-feature unlock to begin identifying rocks, saving dig sites, and connecting with the community.
      </p>
    </div>
    <p style="text-align:center;color:#8A8475;font-size:13px;margin-top:24px;">
      ${TAGLINE}
    </p>
  </div>
</body></html>`;

  const text = `${senderName} wants you to join them on RockScout — the rockhounder's field companion.

Your personal referral code is: ${code}

How to use it:
1. Download RockScout and create your account.
2. During your first sign-in, enter the code above when asked.
3. You and ${senderName} will automatically become RockScout Friends so you can find each other easily.

Free gifts: as a referred new RockScout you get starter ID tokens and a full-feature unlock to begin identifying rocks, saving dig sites, and connecting with the community.

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
        to: [recipientEmail],
        subject: `${senderName} invited you to RockScout`,
        html,
        text,
      }),
    });
    if (!res.ok) {
      const errText = await res.text();
      console.error("Resend referral email failed:", res.status, errText);
      return Response.json({ ok: false, error: "resend_failed", status: res.status }, { status: 502, headers: CORS });
    }
    return Response.json({ ok: true }, { status: 200, headers: CORS });
  } catch (err) {
    console.error("Resend referral email exception:", err);
    return Response.json({ ok: false, error: "resend_exception" }, { status: 502, headers: CORS });
  }
}

/**
 * Register-only mode: stores the code→sender mapping without sending an email.
 * Used by the Android app when a user generates a referral code — it registers
 * the code immediately so it can be verified later, even before the user sends
 * it to a friend.
 */
async function handleRegister(
  body: Record<string, string>,
  env: { REFERRAL_KV?: KVNamespace },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const code = body.code?.trim().toUpperCase();
  const senderEmail = body.senderEmail?.trim().toLowerCase();

  if (!code || !code.startsWith("ROCK-")) {
    return Response.json({ ok: false, error: "invalid_code" }, { status: 400, headers: CORS });
  }
  if (!senderEmail || !senderEmail.includes("@")) {
    return Response.json({ ok: false, error: "invalid_sender_email" }, { status: 400, headers: CORS });
  }

  // Collision guard: same as handleSend.
  const existingSender = await getSenderEmail(env, code);
  if (existingSender && existingSender !== senderEmail) {
    return Response.json({ ok: false, error: "code_taken" }, { status: 409, headers: CORS });
  }

  await setSenderEmail(env, code, senderEmail);
  return Response.json({ ok: true }, { status: 200, headers: CORS });
}

async function handleVerify(
  body: Record<string, string>,
  env: { REFERRAL_KV?: KVNamespace },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const code = body.code?.trim().toUpperCase();
  if (!code) {
    return Response.json({ ok: false, error: "missing_code" }, { status: 400, headers: CORS });
  }

  const senderEmail = await getSenderEmail(env, code);
  if (!senderEmail) {
    return Response.json({ ok: false, error: "code_not_found" }, { status: 404, headers: CORS });
  }

  // Self-referral guard — reject if the verifying email matches the sender's email
  const recipientEmail = body.recipientEmail?.trim().toLowerCase();
  if (recipientEmail && senderEmail.toLowerCase() === recipientEmail) {
    return Response.json({ ok: false, error: "self_referral_not_allowed" }, { status: 400, headers: CORS });
  }

  return Response.json({ ok: true, senderEmail }, { status: 200, headers: CORS });
}

/**
 * Called by the new user's app after they've signed in AND verified the code.
 * This records the completion so the sender can be credited when they next
 * check for completions. The referral is only complete after both code
 * verification and sign-in — this endpoint is the final step.
 */
async function handleComplete(
  body: Record<string, string>,
  env: { REFERRAL_KV?: KVNamespace },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const code = body.code?.trim().toUpperCase();
  const recipientEmail = body.recipientEmail?.trim().toLowerCase();
  if (!code) {
    return Response.json({ ok: false, error: "missing_code" }, { status: 400, headers: CORS });
  }
  if (!recipientEmail || !recipientEmail.includes("@")) {
    return Response.json({ ok: false, error: "invalid_recipient_email" }, { status: 400, headers: CORS });
  }

  const senderEmail = await getSenderEmail(env, code);
  if (!senderEmail) {
    return Response.json({ ok: false, error: "code_not_found" }, { status: 404, headers: CORS });
  }

  // Self-referral guard
  if (senderEmail.toLowerCase() === recipientEmail) {
    return Response.json({ ok: false, error: "self_referral_not_allowed" }, { status: 400, headers: CORS });
  }

  // Record the completion so the sender can be credited later.
  // Use lowercased senderEmail for consistent completion key lookups.
  await recordCompletion(env, {
    code,
    senderEmail: senderEmail.toLowerCase(),
    recipientEmail,
    completedAt: new Date().toISOString(),
  });

  return Response.json({ ok: true, senderEmail }, { status: 200, headers: CORS });
}

/**
 * Called by the sender's app on sign-in to find completions they haven't been
 * credited for yet. Returns all pending completions for this sender.
 */
async function handleCheckCompletions(
  body: Record<string, string>,
  env: { REFERRAL_KV?: KVNamespace },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const senderEmail = body.senderEmail?.trim().toLowerCase();
  if (!senderEmail || !senderEmail.includes("@")) {
    return Response.json({ ok: false, error: "invalid_sender_email" }, { status: 400, headers: CORS });
  }

  const completions = await getCompletionsForSender(env, senderEmail);
  return Response.json(
    { ok: true, completions },
    { status: 200, headers: CORS },
  );
}

/**
 * Called by the sender's app after it has credited the reward for a completion.
 * Removes the completion so it won't be returned again.
 */
async function handleAcknowledge(
  body: Record<string, string>,
  env: { REFERRAL_KV?: KVNamespace },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const senderEmail = body.senderEmail?.trim().toLowerCase();
  const code = body.code?.trim().toUpperCase();
  const recipientEmail = body.recipientEmail?.trim().toLowerCase();
  if (!senderEmail || !code || !recipientEmail) {
    return Response.json({ ok: false, error: "missing_params" }, { status: 400, headers: CORS });
  }

  await acknowledgeCompletion(env, senderEmail, code, recipientEmail);
  return Response.json({ ok: true }, { status: 200, headers: CORS });
}
