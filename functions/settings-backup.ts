/**
 * Settings backup/restore endpoints — Cloudflare Worker.
 *
 * When the app detects a fresh install (empty SharedPreferences) and the user
 * signs in, it calls these endpoints to fetch the user's previously backed-up
 * settings from Supabase. The backup is also pushed periodically (every 12h)
 * and before the signing-conflict uninstall flow.
 *
 * Routes:
 *   PUT  /settings/backup   — { userId, settingsJson } → upserts to Supabase
 *   GET  /settings/restore  — ?userId=... → returns { settingsJson } or 404
 *
 * Storage: Supabase `rockscout_settings_backup` table (service-role key bypasses RLS).
 * Previously used Cloudflare KV, but the Rork platform does not bind KV namespaces.
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

interface SettingsBackupEnv {
  SUPABASE_SERVICE_ROLE_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
}

/**
 * Upsert the settings blob into Supabase using the service-role key.
 * Uses Prefer: resolution=merge-duplicates for upsert behavior.
 */
async function upsertSettings(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
  settingsJson: string,
): Promise<boolean> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/rest/v1/rockscout_settings_backup`,
      {
        method: "POST",
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
          "Content-Type": "application/json",
          Prefer: "resolution=merge-duplicates,return=minimal",
        },
        body: JSON.stringify({
          user_id: userId,
          settings_json: settingsJson,
          updated_at: new Date().toISOString(),
        }),
      },
    );

    if (!resp.ok) {
      console.error(
        `Supabase settings upsert failed: ${resp.status}`,
        await resp.text(),
      );
      return false;
    }
    return true;
  } catch (err) {
    console.error("Supabase settings upsert error:", err);
    return false;
  }
}

/**
 * Fetch the settings blob from Supabase using the service-role key.
 * Returns the JSON string or null if no backup exists.
 */
async function fetchSettings(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
): Promise<string | null> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/rest/v1/rockscout_settings_backup?select=settings_json&user_id=eq.${encodeURIComponent(userId)}`,
      {
        method: "GET",
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
          "Content-Type": "application/json",
        },
      },
    );

    if (!resp.ok) {
      console.error(
        `Supabase settings fetch failed: ${resp.status}`,
        await resp.text(),
      );
      return null;
    }

    const rows = (await resp.json()) as Array<{ settings_json: string }>;
    if (!rows || rows.length === 0) return null;
    return rows[0].settings_json;
  } catch (err) {
    console.error("Supabase settings fetch error:", err);
    return null;
  }
}

/** Handle /settings/backup (PUT) and /settings/restore (GET). */
export async function handleSettingsBackup(
  request: Request,
  env: SettingsBackupEnv,
  cors: Record<string, string>,
): Promise<Response> {
  const url = new URL(request.url);

  // Validate required env vars.
  if (!env.SUPABASE_SERVICE_ROLE_KEY || !env.EXPO_PUBLIC_SUPABASE_URL) {
    return Response.json(
      { error: "Backup storage not configured — missing Supabase credentials" },
      { status: 503, headers: cors },
    );
  }

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

    const success = await upsertSettings(
      env.EXPO_PUBLIC_SUPABASE_URL,
      env.SUPABASE_SERVICE_ROLE_KEY,
      body.userId,
      body.settingsJson,
    );

    if (!success) {
      return Response.json(
        { error: "Failed to store settings backup" },
        { status: 500, headers: cors },
      );
    }

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

    const settingsJson = await fetchSettings(
      env.EXPO_PUBLIC_SUPABASE_URL,
      env.SUPABASE_SERVICE_ROLE_KEY,
      userId,
    );

    const response: RestoreResponse = { settingsJson };
    return Response.json(response, { headers: cors });
  }

  return Response.json(
    { error: "Method not allowed" },
    { status: 405, headers: cors },
  );
}
