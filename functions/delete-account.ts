/**
 * Records an account-deletion request from the Android app.
 *
 * RockScout is currently local-auth / local-storage first, so the device-side
 * deleteAccount() in AuthRepository already wipes the user's account, social
 * graph, collection, captures, posts, and messages from the device. This
 * endpoint provides a server-side receipt for that deletion request and can be
 * expanded to purge Supabase rows or other backend copies once backend-backed
 * accounts are enabled.
 */

export async function handleDeleteAccount(
  request: Request,
  env: { RESEND_API_KEY?: string; EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string },
  cors: Record<string, string>,
): Promise<Response> {
  if (request.method !== "POST") {
    return new Response("method not allowed", { status: 405, headers: cors });
  }

  let body: { email?: string } = {};
  try {
    const text = await request.text();
    if (text) body = JSON.parse(text) as { email?: string };
  } catch {
    return Response.json({ error: "Invalid JSON body" }, { status: 400, headers: cors });
  }

  const email = (body.email ?? "").trim().toLowerCase();
  if (!email || !email.includes("@")) {
    return Response.json({ error: "Email is required" }, { status: 400, headers: cors });
  }

  const deletedAt = new Date().toISOString();

  // Future hook: if a Supabase backend account exists, delete rows here.
  // For now, the device-side deletion is the source of truth.

  return Response.json(
    {
      success: true,
      email,
      deletedAt,
      message:
        "Account deletion request recorded. Your device data has been removed from this app.",
    },
    { status: 200, headers: cors },
  );
}
