/**
 * Central error reporting endpoint for all platforms.
 *
 * POST /error-report
 *   {
 *     platform: 'android' | 'ios' | 'web',
 *     appVersion?: string,
 *     osVersion?: string,
 *     deviceModel?: string,
 *     userId?: string,
 *     errorType: string,
 *     errorMessage: string,
 *     stackTrace?: string,
 *     isFatal?: boolean,
 *     screen?: string,
 *     breadcrumb?: string,
 *     autoHealed?: boolean,
 *     healAction?: string,
 *     fingerprint: string,
 *   }
 *   → { ok: boolean, deduplicated?: boolean }
 *
 * Auth: app-key header (X-App-Key). No rate limit — errors should always
 * be accepted. The Supabase insert uses the service-role key to bypass RLS.
 *
 * Dedup: if the same fingerprint was logged within the last 5 minutes,
 * the new entry is skipped (deduplicated: true) to prevent a flood of
 * identical errors from a crash loop.
 */

import { buildCorsHeaders, resolveSupabaseUrl } from "./auth";

interface ErrorReport {
  platform: string;
  appVersion?: string;
  osVersion?: string;
  deviceModel?: string;
  userId?: string;
  errorType: string;
  errorMessage: string;
  stackTrace?: string;
  isFatal?: boolean;
  screen?: string;
  breadcrumb?: string;
  autoHealed?: boolean;
  healAction?: string;
  fingerprint: string;
}

interface ErrorReportEnv {
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
}

/** Dedup window — skip if same fingerprint logged within this many seconds. */
const DEDUP_WINDOW_SECONDS = 300; // 5 minutes

export async function handleErrorReport(
  request: Request,
  env: ErrorReportEnv,
  cors: Record<string, string>,
): Promise<Response> {
  const headers = { ...cors, "Content-Type": "application/json" };

  // Validate app key
  const expectedKey = env.EXPO_PUBLIC_RORK_APP_KEY;
  if (!expectedKey) {
    return Response.json({ ok: false, error: "Server not configured." }, { status: 503, headers });
  }
  const providedKey = request.headers.get("x-app-key");
  if (providedKey !== expectedKey) {
    return Response.json({ ok: false, error: "Unauthorized." }, { status: 401, headers });
  }

  // Parse body
  let body: ErrorReport;
  try {
    body = (await request.json()) as ErrorReport;
  } catch {
    return Response.json({ ok: false, error: "Invalid JSON." }, { status: 400, headers });
  }

  // Validate required fields
  if (!body.platform || !body.errorType || !body.errorMessage || !body.fingerprint) {
    return Response.json({ ok: false, error: "Missing required fields." }, { status: 400, headers });
  }

  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY);
  const serviceKey = env.SUPABASE_SERVICE_ROLE_KEY;
  if (!supabaseUrl || !serviceKey) {
    // Still return ok so the client doesn't retry — just log to console
    console.error("[error-report] Supabase not configured, cannot store error:", body.errorType, body.errorMessage);
    return Response.json({ ok: false, error: "Storage not configured." }, { status: 503, headers });
  }

  // Truncate stack trace to prevent oversized payloads
  const stackTrace = body.stackTrace ? body.stackTrace.slice(0, 8000) : null;
  const screen = body.screen ? body.screen.slice(0, 200) : null;
  const breadcrumb = body.breadcrumb ? body.breadcrumb.slice(0, 500) : null;
  const deviceModel = body.deviceModel ? body.deviceModel.slice(0, 300) : null;

  // Check for recent duplicate (dedup)
  const dedupCutoff = new Date(Date.now() - DEDUP_WINDOW_SECONDS * 1000).toISOString();
  try {
    const dedupResp = await fetch(
      `${supabaseUrl}/rest/v1/rockscout_error_logs?select=id&error_fingerprint=eq.${encodeURIComponent(body.fingerprint)}&created_at=gte.${dedupCutoff}&limit=1`,
      {
        headers: {
          "apikey": serviceKey,
          "Authorization": `Bearer ${serviceKey}`,
        },
      },
    );
    if (dedupResp.ok) {
      const existing = (await dedupResp.json()) as unknown[];
      if (existing.length > 0) {
        return Response.json({ ok: true, deduplicated: true }, { headers });
      }
    }
  } catch {
    // Dedup check is best-effort — proceed to insert on failure
  }

  // Insert the error
  try {
    const insertResp = await fetch(`${supabaseUrl}/rest/v1/rockscout_error_logs`, {
      method: "POST",
      headers: {
        "apikey": serviceKey,
        "Authorization": `Bearer ${serviceKey}`,
        "Content-Type": "application/json",
        "Prefer": "return=minimal",
      },
      body: JSON.stringify({
        platform: body.platform,
        app_version: body.appVersion ?? null,
        os_version: body.osVersion ?? null,
        device_model: deviceModel,
        user_id: body.userId ?? null,
        error_type: body.errorType.slice(0, 300),
        error_message: body.errorMessage.slice(0, 2000),
        stack_trace: stackTrace,
        is_fatal: body.isFatal ?? false,
        screen: screen,
        breadcrumb: breadcrumb,
        auto_healed: body.autoHealed ?? false,
        heal_action: body.healAction ?? null,
        error_fingerprint: body.fingerprint.slice(0, 64),
      }),
    });

    if (!insertResp.ok) {
      const text = await insertResp.text().catch(() => "");
      console.error("[error-report] Supabase insert failed:", insertResp.status, text);
      return Response.json({ ok: false, error: "Could not store error." }, { status: 502, headers });
    }

    return Response.json({ ok: true }, { headers });
  } catch (err) {
    console.error("[error-report] Network error during insert:", err);
    return Response.json({ ok: false, error: "Network error." }, { status: 502, headers });
  }
}
