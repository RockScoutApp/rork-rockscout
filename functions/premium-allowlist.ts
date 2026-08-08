/**
 * Premium APK allowlist CRUD endpoint — Cloudflare Worker.
 *
 * GET    /premium-allowlist         — returns all entries
 * POST   /premium-allowlist         — adds an email to the allowlist
 * DELETE /premium-allowlist         — removes an email from the allowlist
 *
 * Auth: X-App-Key header (Dev Console only).
 * All operations use the service role key (bypasses RLS).
 */

import { resolveSupabaseUrl } from "./auth";

interface PremiumAllowlistEnv {
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
}

interface AllowlistEntry {
  email: string;
  added_at: string;
  added_by: string | null;
  notes: string | null;
}

export async function handlePremiumAllowlist(
  request: Request,
  env: PremiumAllowlistEnv,
  cors: Record<string, string>,
): Promise<Response> {
  const headers = { ...cors, "Content-Type": "application/json" };

  // Validate app key
  const expectedKey = env.EXPO_PUBLIC_RORK_APP_KEY;
  const providedKey = request.headers.get("x-app-key");
  if (!expectedKey || providedKey !== expectedKey) {
    return Response.json(
      { ok: false, error: "unauthorized" },
      { status: 401, headers },
    );
  }

  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, env.SUPABASE_SERVICE_ROLE_KEY);
  const serviceKey = env.SUPABASE_SERVICE_ROLE_KEY;
  if (!supabaseUrl || !serviceKey) {
    return Response.json(
      { ok: false, error: "server_not_configured" },
      { status: 503, headers },
    );
  }

  // ── GET: return all entries ────────────────────────────────────────────
  if (request.method === "GET") {
    try {
      const resp = await fetch(
        `${supabaseUrl}/rest/v1/rockscout_premium_apk_allowlist?select=email,added_at,added_by,notes&order=added_at.desc`,
        {
          headers: {
            apikey: serviceKey,
            Authorization: `Bearer ${serviceKey}`,
          },
        },
      );

      if (!resp.ok) {
        return Response.json(
          { ok: false, error: "fetch_failed" },
          { status: 500, headers },
        );
      }

      const entries = await resp.json() as AllowlistEntry[];
      return Response.json({ ok: true, entries }, { status: 200, headers });
    } catch (err) {
      console.error("premium-allowlist GET error:", String(err));
      return Response.json(
        { ok: false, error: "fetch_failed" },
        { status: 500, headers },
      );
    }
  }

  // ── POST: add an email ──────────────────────────────────────────────────
  if (request.method === "POST") {
    let body: { email?: string; notes?: string; addedBy?: string } = {};
    try {
      body = await request.json() as typeof body;
    } catch {
      return Response.json(
        { ok: false, error: "invalid_json" },
        { status: 400, headers },
      );
    }

    const email = (body.email ?? "").trim().toLowerCase();
    if (!email || !email.includes("@")) {
      return Response.json(
        { ok: false, error: "invalid_email" },
        { status: 400, headers },
      );
    }

    const notes = (body.notes ?? "").trim() || null;
    const addedBy = (body.addedBy ?? "admin").trim();

    try {
      // Upsert (ignore duplicates)
      const resp = await fetch(
        `${supabaseUrl}/rest/v1/rockscout_premium_apk_allowlist`,
        {
          method: "POST",
          headers: {
            apikey: serviceKey,
            Authorization: `Bearer ${serviceKey}`,
            "Content-Type": "application/json",
            Prefer: "resolution=merge-duplicates,return=minimal",
          },
          body: JSON.stringify({
            email,
            notes,
            added_by: addedBy,
          }),
        },
      );

      if (!resp.ok) {
        const errText = await resp.text();
        console.error("premium-allowlist POST failed:", resp.status, errText);
        return Response.json(
          { ok: false, error: "insert_failed" },
          { status: 500, headers },
        );
      }

      return Response.json(
        { ok: true, email },
        { status: 200, headers },
      );
    } catch (err) {
      console.error("premium-allowlist POST error:", String(err));
      return Response.json(
        { ok: false, error: "insert_failed" },
        { status: 500, headers },
      );
    }
  }

  // ── DELETE: remove an email ─────────────────────────────────────────────
  if (request.method === "DELETE") {
    let body: { email?: string } = {};
    try {
      body = await request.json() as typeof body;
    } catch {
      return Response.json(
        { ok: false, error: "invalid_json" },
        { status: 400, headers },
      );
    }

    const email = (body.email ?? "").trim().toLowerCase();
    if (!email) {
      return Response.json(
        { ok: false, error: "missing_email" },
        { status: 400, headers },
      );
    }

    try {
      const resp = await fetch(
        `${supabaseUrl}/rest/v1/rockscout_premium_apk_allowlist?email=eq.${encodeURIComponent(email)}`,
        {
          method: "DELETE",
          headers: {
            apikey: serviceKey,
            Authorization: `Bearer ${serviceKey}`,
            Prefer: "return=minimal",
          },
        },
      );

      if (!resp.ok) {
        return Response.json(
          { ok: false, error: "delete_failed" },
          { status: 500, headers },
        );
      }

      return Response.json(
        { ok: true, email },
        { status: 200, headers },
      );
    } catch (err) {
      console.error("premium-allowlist DELETE error:", String(err));
      return Response.json(
        { ok: false, error: "delete_failed" },
        { status: 500, headers },
      );
    }
  }

  return new Response("method not allowed", { status: 405, headers: cors });
}
