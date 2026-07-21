/**
 * One free trial per device — Cloudflare Worker endpoints.
 *
 * POST /trial/check  { deviceId } → { trialUsed: boolean }
 * POST /trial/claim  { deviceId } → { ok: boolean }
 *
 * Uses TRIAL_KV if bound, falls back to an in-memory Set (ephemeral).
 */

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
  "Content-Type": "application/json",
};

// In-memory fallback when TRIAL_KV is not bound.
const claimedDevices = new Set<string>();

export async function handleTrial(
  request: Request,
  env: { TRIAL_KV?: KVNamespace },
  corsHeaders: Record<string, string>,
): Promise<Response> {
  const url = new URL(request.url);
  const headers = { ...corsHeaders, ...CORS };

  if (request.method === "OPTIONS") {
    return new Response(null, { status: 204, headers: corsHeaders });
  }

  // POST /trial/check
  if (url.pathname === "/trial/check" && request.method === "POST") {
    try {
      const body = await request.json() as { deviceId?: string };
      const deviceId = body.deviceId?.trim();
      if (!deviceId) {
        return Response.json({ trialUsed: false, error: "missing deviceId" }, { status: 400, headers });
      }

      let trialUsed = false;
      if (env.TRIAL_KV) {
        const existing = await env.TRIAL_KV.get(`trial:${deviceId}`);
        trialUsed = existing === "1";
      } else {
        trialUsed = claimedDevices.has(deviceId);
      }

      return Response.json({ trialUsed }, { headers });
    } catch {
      return Response.json({ trialUsed: false, error: "invalid request" }, { status: 400, headers });
    }
  }

  // POST /trial/claim
  if (url.pathname === "/trial/claim" && request.method === "POST") {
    try {
      const body = await request.json() as { deviceId?: string };
      const deviceId = body.deviceId?.trim();
      if (!deviceId) {
        return Response.json({ ok: false, error: "missing deviceId" }, { status: 400, headers });
      }

      if (env.TRIAL_KV) {
        const existing = await env.TRIAL_KV.get(`trial:${deviceId}`);
        if (existing === "1") {
          return Response.json({ ok: true, alreadyClaimed: true }, { headers });
        }
        await env.TRIAL_KV.put(`trial:${deviceId}`, "1");
      } else {
        claimedDevices.add(deviceId);
      }

      return Response.json({ ok: true }, { headers });
    } catch {
      return Response.json({ ok: false, error: "invalid request" }, { status: 400, headers });
    }
  }

  return new Response("not found", { status: 404, headers });
}
