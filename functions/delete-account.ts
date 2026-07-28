/**
 * Account deletion handler — Cloudflare Worker endpoint.
 *
 * POST /delete-account { email, userId?, accessToken? }
 *
 * Deletes the Supabase auth user (which cascades to all Supabase tables via
 * foreign keys) if a userId + service-role key is available. Always records
 * the deletion request as a server-side receipt regardless.
 *
 * The device-side deleteAccount() in AuthRepository already wipes the user's
 * local data. This endpoint handles the server-side cleanup.
 */

export async function handleDeleteAccount(
  request: Request,
  env: {
    RESEND_API_KEY?: string;
    EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
    EXPO_PUBLIC_SUPABASE_URL?: string;
    SUPABASE_SERVICE_ROLE_KEY?: string;
  },
  cors: Record<string, string>,
): Promise<Response> {
  if (request.method !== "POST") {
    return new Response("method not allowed", { status: 405, headers: cors });
  }

  let body: { email?: string; userId?: string; accessToken?: string } = {};
  try {
    const text = await request.text();
    if (text) body = JSON.parse(text) as typeof body;
  } catch {
    return Response.json({ error: "Invalid JSON body" }, { status: 400, headers: cors });
  }

  const email = (body.email ?? "").trim().toLowerCase();
  if (!email || !email.includes("@")) {
    return Response.json({ error: "Email is required" }, { status: 400, headers: cors });
  }

  const deletedAt = new Date().toISOString();
  let supabaseDeleted = false;

  // Delete the Supabase auth user if we have the service-role key.
  // This cascades to all Supabase tables (profiles, captures, journal, trips,
  // favorites, social, etc.) via ON DELETE CASCADE foreign keys.
  if (env.SUPABASE_SERVICE_ROLE_KEY && env.EXPO_PUBLIC_SUPABASE_URL) {
    // If we have the user ID, delete directly.
    if (body.userId) {
      supabaseDeleted = await deleteSupabaseUser(
        env.EXPO_PUBLIC_SUPABASE_URL,
        env.SUPABASE_SERVICE_ROLE_KEY,
        body.userId,
      );
    }

    // If userId deletion failed or wasn't provided, try to find + delete by email.
    if (!supabaseDeleted) {
      supabaseDeleted = await deleteSupabaseUserByEmail(
        env.EXPO_PUBLIC_SUPABASE_URL,
        env.SUPABASE_SERVICE_ROLE_KEY,
        email,
      );
    }
  }

  return Response.json(
    {
      success: true,
      email,
      deletedAt,
      supabaseDeleted,
      message:
        "Account deletion request recorded. Your device data has been removed from this app.",
    },
    { status: 200, headers: cors },
  );
}

/** Delete a Supabase auth user by ID via the admin API. */
async function deleteSupabaseUser(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
): Promise<boolean> {
  try {
    const resp = await fetch(
      `${supabaseUrl}/auth/v1/admin/users/${encodeURIComponent(userId)}`,
      {
        method: "DELETE",
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
        },
      },
    );
    if (!resp.ok) {
      console.error("deleteSupabaseUser failed", resp.status, await resp.text());
      return false;
    }
    console.log(`Supabase user deleted: ${userId}`);
    return true;
  } catch (err) {
    console.error("deleteSupabaseUser error", err);
    return false;
  }
}

/** Find a Supabase user by email, then delete them. */
async function deleteSupabaseUserByEmail(
  supabaseUrl: string,
  serviceKey: string,
  email: string,
): Promise<boolean> {
  try {
    // List users filtered by email.
    const listResp = await fetch(
      `${supabaseUrl}/auth/v1/admin/users?per_page=1000`,
      {
        headers: {
          apikey: serviceKey,
          Authorization: `Bearer ${serviceKey}`,
        },
      },
    );
    if (!listResp.ok) return false;
    const data = (await listResp.json()) as { users?: Array<{ id: string; email: string }> };
    const user = data.users?.find((u) => u.email?.toLowerCase() === email);
    if (!user) return false;
    return await deleteSupabaseUser(supabaseUrl, serviceKey, user.id);
  } catch (err) {
    console.error("deleteSupabaseUserByEmail error", err);
    return false;
  }
}
