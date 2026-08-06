/**
 * Trip Reminder — Cloudflare Worker endpoint.
 *
 * POST /trips/reminder
 *   { tripId, userId, tripName, tripDate } → { ok, sent, failed }
 *   App-key authenticated. Called by the Supabase pg_cron job
 *   `check_tomorrow_trips()` (daily at 8 AM UTC) via pg_net for each
 *   non-archived trip scheduled for tomorrow.
 *
 * The handler dispatches a web push notification to the trip owner
 * reminding them about their upcoming trip, and logs the result.
 */
import { deliver } from "./push";
import { resolveSupabaseUrl } from "./auth";

interface TripReminderEnv {
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  VAPID_PUBLIC_KEY?: string;
  VAPID_PRIVATE_KEY?: string;
}

const CORS_JSON = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "POST, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, X-App-Key",
  "Content-Type": "application/json",
  Vary: "Origin",
};

/**
 * Verify the X-App-Key header matches the expected app key.
 * Returns an error Response if mismatched, null if authorized.
 */
function checkAppKey(
  request: Request,
  env: TripReminderEnv,
  headers: Record<string, string>,
): Response | null {
  const expected = env.EXPO_PUBLIC_RORK_APP_KEY;
  if (!expected) {
    return Response.json(
      { error: "Server not configured." },
      { status: 503, headers },
    );
  }
  const provided = request.headers.get("x-app-key");
  if (provided !== expected) {
    return Response.json(
      { error: "Unauthorized." },
      { status: 401, headers },
    );
  }
  return null;
}

/**
 * Insert a cron log entry into rockscout_cron_logs so the server-side
 * task execution is auditable from the diagnostics dashboard.
 */
async function logCronExecution(
  env: TripReminderEnv,
  jobName: string,
  status: string,
  details: Record<string, unknown>,
): Promise<void> {
  const supabaseUrl = resolveSupabaseUrl(
    env.EXPO_PUBLIC_SUPABASE_URL,
    env.SUPABASE_SERVICE_ROLE_KEY,
  );
  const serviceKey = env.SUPABASE_SERVICE_ROLE_KEY;
  if (!supabaseUrl || !serviceKey) return;

  try {
    await fetch(`${supabaseUrl}/rest/v1/rockscout_cron_logs`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        apikey: serviceKey,
        Authorization: `Bearer ${serviceKey}`,
        Prefer: "return=minimal",
      },
      body: JSON.stringify({
        job_name: jobName,
        status,
        details: JSON.stringify(details),
      }),
    });
  } catch {
    // Non-critical — the push still went out
  }
}

export async function handleTripReminder(
  request: Request,
  env: TripReminderEnv,
  headers: Record<string, string>,
): Promise<Response> {
  // Authenticate via X-App-Key (the pg_cron function sends the app key)
  const authError = checkAppKey(request, env, headers);
  if (authError) return authError;

  let body: {
    tripId?: string;
    userId?: string;
    tripName?: string;
    tripDate?: string;
  };

  try {
    body = (await request.json()) as typeof body;
  } catch {
    return Response.json(
      { error: "Invalid JSON body." },
      { status: 400, headers },
    );
  }

  if (!body.tripId || !body.userId || !body.tripName) {
    return Response.json(
      { error: "Missing tripId, userId, or tripName." },
      { status: 400, headers },
    );
  }

  const tripDate = body.tripDate ?? "tomorrow";
  const title = "Trip Tomorrow!";
  const pushBody = `Your trip "${body.tripName}" is scheduled for ${tripDate}. Don't forget to pack your gear!`;
  const pushUrl = `/app/trips/${body.tripId}`;

  // Deliver the push notification via the shared push infrastructure
  const pushResponse = await deliver(env, headers, {
    userId: body.userId,
    category: "trips",
    title,
    body: pushBody,
    url: pushUrl,
  });

  const pushResult = pushResponse.ok
    ? await pushResponse.json().catch(() => ({ sent: 0, failed: 0 }))
    : { sent: 0, failed: 1, error: pushResponse.status };

  // Log the execution to rockscout_cron_logs for auditability
  await logCronExecution(env, "trip-reminder-push", "success", {
    tripId: body.tripId,
    userId: body.userId,
    tripName: body.tripName,
    tripDate,
    pushResult,
  });

  return Response.json(
    {
      ok: true,
      tripId: body.tripId,
      ...pushResult,
    },
    { headers },
  );
}
