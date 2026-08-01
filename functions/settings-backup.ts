/**
 * Settings backup/restore endpoints — Cloudflare Worker.
 *
 * When the app detects a fresh install (empty SharedPreferences) and the user
 * signs in, it calls these endpoints to fetch the user's previously backed-up
 * settings from Supabase. The backup is also pushed periodically (every 12h)
 * and before the signing-conflict uninstall flow.
 *
 * Routes:
 *   PUT  /settings/backup   — { userId, settingsJson } + Bearer token → upserts to Supabase
 *   GET  /settings/restore  — ?userId=... + Bearer token → returns { settingsJson } or null
 *
 * Storage: Supabase `rockscout_settings_backup` table.
 *
 * Auth: The caller MUST include `Authorization: Bearer <supabase_access_token>`.
 * The token is forwarded to Supabase, and RLS ensures users can only read/write
 * their own row (user_id = auth.uid()). No service-role key needed — this is
 * more secure and doesn't break when the service key rotates.
 *
 * Also requires X-App-Key header (same as other endpoints).
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
  EXPO_PUBLIC_SUPABASE_URL?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
}

/**
 * Upsert the settings blob into Supabase using the user's own JWT.
 * RLS policy allows INSERT/UPDATE where user_id = auth.uid().
 * Uses Prefer: resolution=merge-duplicates for upsert behavior.
 */
async function upsertSettings(
  supabaseUrl: string,
  accessToken: string,
  userId: string,
  settingsJson: string,
): Promise<boolean> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/rest/v1/rockscout_settings_backup`,
      {
        method: "POST",
        headers: {
          apikey: accessToken,
          Authorization: `Bearer ${accessToken}`,
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
 * Fetch the settings blob from Supabase using the user's own JWT.
 * RLS policy allows SELECT where user_id = auth.uid().
 * Returns the JSON string or null if no backup exists.
 */
async function fetchSettings(
  supabaseUrl: string,
  accessToken: string,
  userId: string,
): Promise<string | null> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/rest/v1/rockscout_settings_backup?select=settings_json&user_id=eq.${encodeURIComponent(userId)}`,
      {
        method: "GET",
        headers: {
          apikey: accessToken,
          Authorization: `Bearer ${accessToken}`,
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

/** Extract the Supabase access token from the Authorization header. */
function getAccessToken(request: Request): string | null {
  const auth = request.headers.get("Authorization") ?? "";
  if (auth.startsWith("Bearer ")) {
    const token = auth.slice(7).trim();
    return token.length > 0 ? token : null;
  }
  return null;
}

/** Handle /settings/backup (PUT) and /settings/restore (GET). */
export async function handleSettingsBackup(
  request: Request,
  env: SettingsBackupEnv,
  cors: Record<string, string>,
): Promise<Response> {
  const url = new URL(request.url);

  if (!env.EXPO_PUBLIC_SUPABASE_URL) {
    return Response.json(
      { error: "Backup storage not configured — missing Supabase URL" },
      { status: 503, headers: cors },
    );
  }

  // The caller must include their Supabase access token. RLS enforces
  // that they can only read/write their own row.
  const accessToken = getAccessToken(request);
  if (!accessToken) {
    return Response.json(
      { error: "Authorization required — include Bearer access token" },
      { status: 401, headers: cors },
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
      accessToken,
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
      accessToken,
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
