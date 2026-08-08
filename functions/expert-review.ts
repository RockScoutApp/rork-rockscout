/**
 * Expert review endpoint — Cloudflare Worker.
 *
 * GET  /expert-review — returns all pending manual review requests
 * POST /expert-review { userId, approve: boolean } — approve or deny a request
 *
 * Auth: X-App-Key header (Dev Console only).
 */

import { resolveSupabaseUrl } from "./auth";

interface ExpertReviewEnv {
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
}

interface PendingExpert {
  user_id: string;
  display_name: string;
  expert_field: string;
  expert_qualifications: string | null;
  expert_verification_status: string;
}

export async function handleExpertReview(
  request: Request,
  env: ExpertReviewEnv,
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

  if (request.method === "GET") {
    // Return all pending manual review requests
    try {
      const resp = await fetch(
        `${supabaseUrl}/rest/v1/rockscout_profiles?select=id,display_name,expert_field,expert_qualifications,expert_verification_status&expert_verification_status=eq.pending_manual&order=id.asc`,
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

      const rows = await resp.json() as PendingExpert[];
      const pending = rows.map((r) => ({
        userId: r.user_id ?? r.id,
        name: r.display_name,
        field: r.expert_field,
        qualifications: r.expert_qualifications,
        status: r.expert_verification_status,
      }));

      return Response.json({ ok: true, pending }, { status: 200, headers });
    } catch (err) {
      console.error("expert-review GET error:", String(err));
      return Response.json(
        { ok: false, error: "fetch_failed" },
        { status: 500, headers },
      );
    }
  }

  if (request.method === "POST") {
    // Approve or deny a request
    let body: { userId?: string; approve?: boolean } = {};
    try {
      body = await request.json() as typeof body;
    } catch {
      return Response.json(
        { ok: false, error: "invalid_json" },
        { status: 400, headers },
      );
    }

    const userId = (body.userId ?? "").trim();
    const approve = body.approve === true;

    if (!userId) {
      return Response.json(
        { ok: false, error: "missing_userId" },
        { status: 400, headers },
      );
    }

    const newStatus = approve ? "approved" : "denied";
    const isVerified = approve;

    try {
      const resp = await fetch(
        `${supabaseUrl}/rest/v1/rockscout_profiles?id=eq.${encodeURIComponent(userId)}`,
        {
          method: "PATCH",
          headers: {
            apikey: serviceKey,
            Authorization: `Bearer ${serviceKey}`,
            "Content-Type": "application/json",
            Prefer: "return=minimal",
          },
          body: JSON.stringify({
            expert_verification_status: newStatus,
            expert_verified: isVerified,
          }),
        },
      );

      if (!resp.ok) {
        return Response.json(
          { ok: false, error: "update_failed" },
          { status: 500, headers },
        );
      }

      return Response.json(
        { ok: true, status: newStatus, userId },
        { status: 200, headers },
      );
    } catch (err) {
      console.error("expert-review POST error:", String(err));
      return Response.json(
        { ok: false, error: "update_failed" },
        { status: 500, headers },
      );
    }
  }

  return new Response("method not allowed", { status: 405, headers: cors });
}
