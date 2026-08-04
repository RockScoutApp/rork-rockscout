import { buildCorsHeaders, guardEndpoint } from "./auth";

/**
 * Server-side profanity warning enforcement.
 *
 * Called by the client when a message contains sexually explicit language
 * (not "fuck" variants, which are silently asterisked). Records a warning
 * in the user_warnings table. The database trigger on user_warnings
 * automatically checks thresholds:
 *   3 warnings → auto-report #1
 *   5 warnings → auto-report #2
 *   6 warnings → account ban
 *
 * POST /profanity-warning
 * Body: { userId: string, reason: string, source: "chat"|"group_chat", sourceId?: string }
 * Returns: { ok: true, warningCount: number, autoReported: boolean, banned: boolean }
 */
export async function handleProfanityWarning(
  request: Request,
  env: {
    EXPO_PUBLIC_SUPABASE_URL?: string;
    SUPABASE_SERVICE_ROLE_KEY?: string;
    EXPO_PUBLIC_RORK_APP_KEY?: string;
  },
  cors: Headers,
): Promise<Response> {
  try {
    const body = await request.json() as {
      userId?: string;
      reason?: string;
      source?: string;
      sourceId?: string;
    };

    if (!body.userId) {
      return Response.json({ error: "Missing userId" }, { status: 400, headers: cors });
    }

    const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
    const serviceKey = env.SUPABASE_SERVICE_ROLE_KEY;
    if (!supabaseUrl || !serviceKey) {
      return Response.json({ error: "Server not configured" }, { status: 500, headers: cors });
    }

    // Insert the warning using the service role key (bypasses RLS)
    const insertResp = await fetch(
      `${supabaseUrl}/rest/v1/user_warnings`,
      {
        method: "POST",
        headers: {
          "apikey": serviceKey,
          "Authorization": `Bearer ${serviceKey}`,
          "Content-Type": "application/json",
          "Prefer": "return=representation",
        },
        body: JSON.stringify({
          user_id: body.userId,
          reason: body.reason || "Explicit language in message",
          source: body.source || "chat",
          source_id: body.sourceId || null,
        }),
      },
    );

    if (!insertResp.ok) {
      return Response.json(
        { error: "Failed to record warning" },
        { status: 500, headers: cors },
      );
    }

    // Count total warnings for this user
    const countResp = await fetch(
      `${supabaseUrl}/rest/v1/user_warnings?user_id=eq.${body.userId}&select=id`,
      {
        headers: {
          "apikey": serviceKey,
          "Authorization": `Bearer ${serviceKey}`,
        },
      },
    );

    let warningCount = 0;
    if (countResp.ok) {
      const rows = await countResp.json() as unknown[];
      warningCount = rows.length;
    }

    const autoReported = warningCount === 3 || warningCount === 5;
    const banned = warningCount >= 6;

    return Response.json(
      { ok: true, warningCount, autoReported, banned },
      { headers: cors },
    );
  } catch (err) {
    return Response.json(
      { error: "Internal error" },
      { status: 500, headers: cors },
    );
  }
}
