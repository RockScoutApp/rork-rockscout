/**
 * Expert verification endpoint — Cloudflare Worker.
 *
 * POST /verify-expert { userId, name, field, qualifications }
 *
 * Performs a web search (Exa) for the user's name + field + qualifications,
 * then uses Haiku to evaluate whether the search results confirm the user's
 * credentials. Sets expert_verified + expert_verification_status on the
 * profile via the service-role key.
 *
 * Auth: X-App-Key header (same as other endpoints).
 */

import { resolveSupabaseUrl } from "./auth";

interface VerifyExpertEnv {
  EXPO_PUBLIC_TOOLKIT_URL?: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY?: string;
  EXPO_PUBLIC_RORK_APP_KEY?: string;
  SUPABASE_SERVICE_ROLE_KEY?: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
}

interface ExaSearchResult {
  title?: string;
  url?: string;
  text?: string;
  highlight?: string[];
}

interface ExaSearchResponse {
  results?: ExaSearchResult[];
}

export async function handleVerifyExpert(
  request: Request,
  env: VerifyExpertEnv,
  cors: Record<string, string>,
): Promise<Response> {
  if (request.method !== "POST") {
    return new Response("method not allowed", { status: 405, headers: cors });
  }

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

  let body: { userId?: string; name?: string; field?: string; qualifications?: string } = {};
  try {
    body = await request.json() as typeof body;
  } catch {
    return Response.json(
      { ok: false, error: "invalid_json" },
      { status: 400, headers },
    );
  }

  const userId = (body.userId ?? "").trim();
  const name = (body.name ?? "").trim();
  const field = (body.field ?? "").trim();
  const qualifications = (body.qualifications ?? "").trim();

  if (!userId || !name || !field) {
    return Response.json(
      { ok: false, error: "missing_fields" },
      { status: 400, headers },
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

  // Set status to pending_auto while we search
  await updateExpertStatus(supabaseUrl, serviceKey, userId, "pending_auto", false);

  const toolkitUrl = env.EXPO_PUBLIC_TOOLKIT_URL;
  const secret = env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY;

  if (!toolkitUrl || !secret) {
    // No toolkit — can't do web search, send to manual review
    await updateExpertStatus(supabaseUrl, serviceKey, userId, "pending_manual", false);
    return Response.json(
      { ok: true, status: "pending_manual", evidence: "Web search unavailable — sent for manual review." },
      { status: 200, headers },
    );
  }

  // Step 1: Exa web search for name + field + qualifications
  const searchQuery = `${name} ${field} ${qualifications.slice(0, 200)}`;
  let searchResults: ExaSearchResult[] = [];

  try {
    const exaResp = await fetch(`${toolkitUrl}/v2/exa/search`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        query: searchQuery,
        numResults: 8,
        useAutoprompt: true,
        contents: {
          text: { maxCharacters: 600 },
          highlights: { numSentences: 3, highlightsPerUrl: 2 },
        },
        type: "neural",
      }),
    });

    if (exaResp.ok) {
      const data = await exaResp.json() as ExaSearchResponse;
      searchResults = data.results ?? [];
    }
  } catch (err) {
    console.error("Expert verification Exa search error:", String(err));
  }

  if (searchResults.length === 0) {
    // No results — send to manual review
    await updateExpertStatus(supabaseUrl, serviceKey, userId, "pending_manual", false);
    return Response.json(
      { ok: true, status: "pending_manual", evidence: "No web results found — sent for manual review." },
      { status: 200, headers },
    );
  }

  // Step 2: Haiku evaluates the search results
  const searchContext = searchResults.slice(0, 8).map((r, i) => {
    const title = r.title ?? "";
    const text = r.text ?? "";
    const highlights = r.highlight?.join(" ") ?? "";
    return `[${i + 1}] ${title}\nURL: ${r.url ?? ""}\nContent: ${text.slice(0, 400)}\nHighlights: ${highlights.slice(0, 300)}`;
  }).join("\n\n");

  let verdict: "auto_verified" | "pending_manual" | "denied" = "pending_manual";
  let evidence = "";

  try {
    const haikuResp = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "anthropic/claude-haiku-4.5",
        messages: [{
          role: "user",
          content: `You are verifying a user's claim of professional expertise for a rock/mineral/fossil identification app.

User claims:
- Name: ${name}
- Field of expertise: ${field}
- Qualifications: ${qualifications}

Web search results for "${name} ${field} ${qualifications}":
${searchContext}

Evaluate whether these search results CONFIRM this person's credentials. Look for:
- The person's name appearing on university faculty pages, research publications, professional registries
- Business listings for rock shops, metaphysical shops, gem shops matching the name and field
- Published papers, conference talks, or professional profiles matching the qualifications
- Any evidence that this specific person (not just someone with a similar name) has credentials in the claimed field

Return ONLY a JSON object:
{
  "verdict": "auto_verified" | "pending_manual" | "denied",
  "evidence": "brief explanation of what was found or why it's inconclusive"
}

- "auto_verified": Clear evidence found — name matches on authoritative pages with relevant credentials
- "pending_manual": Some results but inconclusive — needs human review
- "denied": Results actively contradict the claim (e.g., person doesn't exist, credentials are fabricated)

Be conservative: when in doubt, choose "pending_manual" rather than "auto_verified".`,
        }],
        max_tokens: 256,
        temperature: 0.1,
      }),
    });

    if (haikuResp.ok) {
      const haikuData = await haikuResp.json() as {
        choices?: Array<{ message?: { content?: string } }>;
      };
      const content = haikuData.choices?.[0]?.message?.content ?? "";
      const jsonMatch = content.match(/\{[\s\S]*\}/);
      if (jsonMatch) {
        const parsed = JSON.parse(jsonMatch[0]) as {
          verdict?: string;
          evidence?: string;
        };
        if (parsed.verdict === "auto_verified" || parsed.verdict === "pending_manual" || parsed.verdict === "denied") {
          verdict = parsed.verdict;
        }
        evidence = parsed.evidence ?? "";
      }
    }
  } catch (err) {
    console.error("Expert verification Haiku eval error:", String(err));
  }

  // Step 3: Update profile based on verdict
  const isVerified = verdict === "auto_verified";
  await updateExpertStatus(supabaseUrl, serviceKey, userId, verdict, isVerified);

  return Response.json(
    { ok: true, status: verdict, evidence },
    { status: 200, headers },
  );
}

/** Update expert verification status on the profile via service role key. */
async function updateExpertStatus(
  supabaseUrl: string,
  serviceKey: string,
  userId: string,
  status: string,
  verified: boolean,
): Promise<void> {
  try {
    await fetch(
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
          expert_verification_status: status,
          expert_verified: verified,
        }),
      },
    );
  } catch (err) {
    console.error("updateExpertStatus error:", String(err));
  }
}
