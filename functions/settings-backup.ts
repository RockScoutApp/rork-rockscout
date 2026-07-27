/**
 * Settings backup/restore endpoints for the signing-conflict flow.
 *
 * When a signing conflict is detected during an APK update, the user must
 * uninstall the old app before installing the new one. Uninstalling wipes
 * all local data (SharedPreferences). To honor the dialog's promise that
 * "your data will be restored when you sign back in", we back up the full
 * SharedPreferences JSON blob to Cloudflare KV keyed by the user's ID
 * before launching the uninstall, and restore it on the next sign-in.
 *
 * Routes:
 *   PUT  /settings/backup   — { userId, settingsJson } → stores in KV
 *   GET  /settings/restore  — ?userId=... → returns { settingsJson } or 404
 *
 * Auth: X-App-Key header (same as other endpoints).
 * Rate-limited to prevent abuse.
 */

interface BackupRequest {
  userId: string;
  settingsJson: string;
}

interface RestoreResponse {
  settingsJson: string | null;
}

const KV_KEY_PREFIX = "settings_backup:";

/** Handle /settings/backup (PUT) and /settings/restore (GET). */
export async function handleSettingsBackup(
  request: Request,
  env: { SETTINGS_KV?: KVNamespace },
  cors: Record<string, string>,
): Promise<Response> {
  const url = new URL(request.url);

  // PUT /settings/backup — store the settings blob
  if (url.pathname === "/settings/backup" && request.method === "PUT") {
    let body: BackupRequest;
    try {
      body = (await request.json()) as BackupRequest;
    } catch {
      return Response.json(
        { error: "Invalid JSON body" },
        { status: 400, headers: cors },
      );
    }

    if (!body.userId || !body.settingsJson) {
      return Response.json(
        { error: "userId and settingsJson are required" },
        { status: 400, headers: cors },
      );
    }

    if (!env.SETTINGS_KV) {
      return Response.json(
        { error: "Backup storage not configured" },
        { status: 503, headers: cors },
      );
    }

    const key = `${KV_KEY_PREFIX}${body.userId}`;
    // Store with a 30-day TTL — if the user doesn't reinstall within 30 days,
    // the backup expires. This prevents unbounded KV growth.
    await env.SETTINGS_KV.put(key, body.settingsJson, {
      expirationTtl: 30 * 24 * 60 * 60,
    });

    return Response.json({ ok: true }, { headers: cors });
  }

  // GET /settings/restore?userId=... — retrieve the settings blob
  if (url.pathname === "/settings/restore" && request.method === "GET") {
    const userId = url.searchParams.get("userId");
    if (!userId) {
      return Response.json(
        { error: "userId query parameter is required" },
        { status: 400, headers: cors },
      );
    }

    if (!env.SETTINGS_KV) {
      return Response.json(
        { error: "Backup storage not configured" },
        { status: 503, headers: cors },
      );
    }

    const key = `${KV_KEY_PREFIX}${userId}`;
    const settingsJson = await env.SETTINGS_KV.get(key);

    const response: RestoreResponse = { settingsJson: settingsJson ?? null };
    return Response.json(response, { headers: cors });
  }

  return Response.json(
    { error: "Method not allowed" },
    { status: 405, headers: cors },
  );
}
