// RockScout identify pipeline — matches the approved 7-step plan (v2)
import { SPECIMEN_DB, type SpecimenEntry } from "./specimens";
import { ARTIFACT_DB, ARTIFACT_MAP, type ArtifactEntry } from "./artifacts";
import {
  embedText,
  matchSpecimenEmbeddings,
  matchArtifactEmbeddings,
  type EmbeddingMatch,
  type ArtifactEmbeddingMatch,
} from "./embeddings";

// ── Multi-angle image type ──────────────────────────────────────────────
// Each angle photo sent by the client. The client sends 1-3 photos
// (top, side, bottom) with optional per-angle descriptions (max 500 chars).
interface AngleImage {
  imageBase64: string;
  mimeType: string;
  angle: string; // "top", "side", "bottom"
  description: string; // optional, max 500 chars — empty string if not provided
}

/** Parse the request body into an AngleImage array, falling back to the
 *  legacy single `imageBase64` field for backward compatibility. */
function parseAngleImages(body: {
  imageBase64?: string;
  mimeType?: string;
  images?: Array<{
    imageBase64: string;
    mimeType?: string;
    angle?: string;
    description?: string;
  }>;
}): AngleImage[] {
  if (body.images && Array.isArray(body.images) && body.images.length > 0) {
    return body.images.slice(0, 3).map((img) => ({
      imageBase64: stripDataUriPrefix(img.imageBase64),
      mimeType: img.mimeType ?? "image/jpeg",
      angle: img.angle ?? "top",
      description: (img.description ?? "").slice(0, 500),
    }));
  }
  // Legacy fallback: single imageBase64 as a top-angle photo
  if (body.imageBase64) {
    return [{
      imageBase64: stripDataUriPrefix(body.imageBase64),
      mimeType: body.mimeType ?? "image/jpeg",
      angle: "top",
      description: "",
    }];
  }
  return [];
}

// ── Main identify handler ───────────────────────────────────────────────

export async function handleIdentify(
  request: Request,
  env: Env,
  cors: Record<string, string>,
): Promise<Response> {
  try {
    const body = await request.json() as {
      imageBase64?: string;
      mimeType?: string;
      images?: Array<{
        imageBase64: string;
        mimeType?: string;
        angle?: string;
        description?: string;
      }>;
      entitlement?: string;
      searchMode?: string;
      skipArtifactDetect?: boolean;
    };

    // Parse images array, falling back to legacy single imageBase64
    const images = parseAngleImages(body);
    if (images.length === 0) {
      return Response.json(
        { error: "At least one image is required" },
        { status: 400, headers: cors },
      );
    }

    const tier = ((body.entitlement ?? "free") as string).toLowerCase();
    const searchMode = (body.searchMode ?? "rocks").toLowerCase();
    const skipArtifactDetect = !!body.skipArtifactDetect;

    // ── Explicit artifact search mode ────────────────────────────────────
    if (searchMode === "artifacts") {
      return await identifyArtifact(env, cors, images, tier);
    }

    const isPremium = tier === "premium" || tier === "pro";
    const useGemini = isPremium;
    const CONFIDENCE_WEB_THRESHOLD = 85;
    const CONFIDENCE_CLARIFY_THRESHOLD = 60;

    const toolkitUrl = env.EXPO_PUBLIC_TOOLKIT_URL ?? "https://toolkit.rork.com";
    const toolkitSecret = env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY;

    if (!toolkitSecret) {
      return Response.json(
        { error: "Toolkit secret not configured" },
        { status: 500, headers: cors },
      );
    }

    const responseHeaders = { ...cors, "Content-Type": "application/json" };
    const modelsUsed: string[] = [];

    // ── Step 1: Combined describe + artifact detection (one Haiku call) ──
    // This single lightweight Haiku call produces (a) a specimen-vocabulary
    // description for the embedding index, and (b) an artifact-detection
    // verdict. It does NOT load the full specimen database into context.
    // Haiku examines ALL photos + descriptions and returns BOTH a combined
    // description AND an artifact-detection verdict in a single JSON response.
    // If skipArtifactDetect is true (user said "No" on the confirmation popup),
    // we still run the combined call for the description but ignore the artifact
    // verdict and proceed with the rock pipeline.
    const describeResult = await callDescribeAndDetect(
      toolkitUrl, toolkitSecret, images,
    );

    // If artifact detected (>= 70% confidence) and user hasn't explicitly
    // skipped detection, return the detection result so the client can show
    // the yes/maybe/no confirmation popup. The client will re-call with
    // searchMode=artifacts (yes/maybe) or skipArtifactDetect=true (no).
    if (
      describeResult?.isArtifact &&
      describeResult.artifactConfidence >= 70 &&
      !skipArtifactDetect
    ) {
      modelsUsed.push("haiku-describe-artifact-detect");
      const detectResponse: IdentifyResponse = {
        matches: [],
        summary: "The AI detected that this may be a prehistoric artifact rather than a natural rock or mineral.",
        needsClarification: false,
        clarificationQuestions: [],
        webReferences: [],
        modelsUsed,
        visualReferenceUsed: false,
        artifactDetected: true,
        artifactConfidence: describeResult.artifactConfidence,
      };
      return Response.json(detectResponse, { headers: responseHeaders });
    }

    const photoDescription = describeResult?.description ?? "";
    const isPolished = describeResult?.isPolished ?? false;
    if (photoDescription) {
      modelsUsed.push("haiku-describe");
    }

    // ── Step 2: Database embedding search (visual-embedding-first) ─────
    // Use the lightweight description to query the pgvector index. This
    // narrows all 810 specimens to a short candidate list before any vision
    // model is called, which is the core cost + accuracy win.
    const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, undefined);
    const supabaseAnonKey = env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
    const embeddingEnabled = !!(supabaseUrl && supabaseAnonKey);

    let parsed!: IdentificationResult;
    let usedEmbeddingFlow = false;

    if (embeddingEnabled && photoDescription) {
      try {
        const queryEmbedding = await embedText(toolkitUrl, toolkitSecret, photoDescription);
        const embeddingMatches = await matchSpecimenEmbeddings(
          supabaseUrl!, supabaseAnonKey!, queryEmbedding, 25,
        );
        if (embeddingMatches.length > 0) {
          parsed = {
            matches: embeddingMatchesToMatchResults(embeddingMatches),
            summary: "Narrowed by embedding index — pending visual comparison.",
          };
          usedEmbeddingFlow = true;
          modelsUsed.push("embedding");
        }
      } catch (err) {
        console.error("Embedding flow failed, falling back to text-first:", String(err));
      }
    }

    if (!usedEmbeddingFlow) {
      // Fallback: old text-first flow (full DB in system prompt).
      const firstImage = images[0];
      const result = await callVisionModel(toolkitUrl, toolkitSecret, firstImage.imageBase64, firstImage.mimeType);
      if (!result.ok) {
        const errorBody = await result.text().catch(() => "unknown error");
        console.error("AI Gateway error:", result.status, errorBody);
        return Response.json(
          { error: `AI identification failed (${result.status})` },
          { status: 502, headers: responseHeaders },
        );
      }
      const data = await result.json() as {
        choices?: Array<{ message?: { content?: string } }>;
      };
      const content = data.choices?.[0]?.message?.content;
      if (!content) {
        console.error("No content in AI response:", JSON.stringify(data));
        return Response.json(
          { error: "No identification results returned" },
          { status: 502, headers: responseHeaders },
        );
      }
      parsed = parseIdentificationResponse(content);
      modelsUsed.push("haiku");
    }

    // ── Step 3: Web search (BEFORE visual — feeds context into comparison) ──
    // Search authoritative mineralogy sites (mindat, webmineral, wikipedia,
    // etc) for reference data on the top embedding candidates. This context
    // is fed into the visual comparison prompt so the vision model can
    // cross-reference published properties (hardness, crystal system, luster)
    // alongside the reference images.
    const webContext = await fetchMineralogyContext(
      toolkitUrl, toolkitSecret, parsed.matches, photoDescription,
    );
    let webReferences: WebReference[] = webContext.references ?? [];
    if (webContext.text) {
      modelsUsed.push("web-search");
    }

    // ── Step 4: Haiku visual comparison (ALL tiers) ──
    // Haiku compares the user's photos against the narrowed candidate set's
    // reference images, with the web mineralogy context as additional input.
    const haikuVisual = await callVisualReferenceComparison(
      toolkitUrl, toolkitSecret, images, parsed.matches, webContext.text, 15, false, isPolished,
    );

    let finalMatches = parsed.matches;
    let finalSummary = parsed.summary;
    let visualReferenceUsed = false;

    if (haikuVisual) {
      modelsUsed.push("haiku-visual");
      visualReferenceUsed = true;
      finalMatches = haikuVisual.matches;
      finalSummary = haikuVisual.summary;
    }

    // ── Step 5: Sonnet visual (ALWAYS for premium — scans for assemblages) ──
    // Sonnet independently re-evaluates the specimen with fresh eyes. It
    // ALWAYS runs for premium regardless of Haiku's confidence, because
    // Sonnet catches multi-mineral assemblages (granite, schist, gneiss)
    // that Haiku may call as a single mineral at high confidence.
    if (isPremium) {
      const sonnetResult = await callSonnetRerank(
        toolkitUrl, toolkitSecret, images, finalMatches, finalSummary, webContext.text,
      );
      if (sonnetResult) {
        modelsUsed.push("sonnet-visual");
        const haikuTopId = finalMatches.length > 0 ? finalMatches[0].id : undefined;
        const sonnetDisagreed = sonnetResult.matches.length > 0 &&
          sonnetResult.matches[0].id !== haikuTopId;
        const merged = mergeRankings(finalMatches, sonnetResult.matches);
        finalMatches = merged.matches;
        finalSummary = merged.summary;

        // ── Step 6: Gemini if needed (disagreement or low confidence) ──
        const topConf = finalMatches.length > 0 ? finalMatches[0].confidence : 0;
        if (useGemini && (topConf < CONFIDENCE_WEB_THRESHOLD || sonnetDisagreed)) {
          const geminiResult = await callGeminiThirdOpinion(
            toolkitUrl, toolkitSecret, images, finalMatches, sonnetResult.matches,
            finalSummary, webContext.text,
          );
          if (geminiResult) {
            modelsUsed.push("gemini");
            const resolved = mergeRankingsThreeWay(
              finalMatches, sonnetResult.matches, geminiResult.matches,
            );
            finalMatches = resolved.matches;
            finalSummary = resolved.summary;
          }
        }
      }
    }

    // ── Step 7: Clarification (last resort, only if < 60%) ─────────────
    const finalTopConfidence = finalMatches.length > 0 ? finalMatches[0].confidence : 0;
    const needsClarification = finalTopConfidence < CONFIDENCE_CLARIFY_THRESHOLD && finalMatches.length > 0;

    let clarificationQuestions: ClarificationQuestion[] = [];
    if (needsClarification) {
      clarificationQuestions = await generateClarificationQuestions(
        toolkitUrl, toolkitSecret, finalMatches, finalSummary,
      );
    }

    const response: IdentifyResponse = {
      matches: finalMatches,
      summary: finalSummary,
      needsClarification,
      clarificationQuestions,
      webReferences,
      modelsUsed,
      visualReferenceUsed,
    };

    return Response.json(response, { headers: responseHeaders });
  } catch (err: unknown) {
    console.error("Identify error:", String(err));
    return Response.json(
      { error: "Internal server error" },
      { status: 500, headers: cors },
    );
  }
}

// ── Artifact identification pipeline ────────────────────────────────────

/**
 * Artifact-first identification pipeline. Runs when the combined describe+
 * detect step identifies an artifact, or when the user explicitly selects
 * artifact search mode. Uses ARTIFACT_DB as the candidate set.
 *
 * Accepts the multi-angle images array. Currently uses the first image for
 * the artifact-specific vision functions (which still take a single image).
 */
async function identifyArtifact(
  env: Env,
  cors: Record<string, string>,
  images: AngleImage[],
  tier: string,
): Promise<Response> {
  const responseHeaders = { ...cors, "Content-Type": "application/json" };
  const toolkitUrl = env.EXPO_PUBLIC_TOOLKIT_URL ?? "https://toolkit.rork.com";
  const toolkitSecret = env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY;

  if (!toolkitSecret) {
    return Response.json(
      { error: "Toolkit secret not configured" },
      { status: 500, headers: cors },
    );
  }

  const isPremium = tier === "premium" || tier === "pro";
  const useGemini = isPremium;
  const modelsUsed: string[] = ["artifact-mode"];

  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, undefined);
  const supabaseAnonKey = env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
  const embeddingEnabled = !!(supabaseUrl && supabaseAnonKey);

  try {
    let parsed!: IdentificationResult;
    let usedEmbeddingFlow = false;

    if (embeddingEnabled) {
      try {
        const photoDescription = await callDescribeArtifactPhoto(
          toolkitUrl, toolkitSecret, images,
        );
        if (photoDescription) {
          const queryEmbedding = await embedText(toolkitUrl, toolkitSecret, photoDescription);
          const embeddingMatches = await matchArtifactEmbeddings(
            supabaseUrl!, supabaseAnonKey!, queryEmbedding, 25,
          );
          if (embeddingMatches.length > 0) {
            parsed = {
              matches: artifactEmbeddingMatchesToMatchResults(embeddingMatches),
              summary: "Narrowed by embedding index — pending visual comparison.",
            };
            usedEmbeddingFlow = true;
          }
        }
      } catch (err) {
        console.error("Artifact embedding flow failed, falling back to text-first:", String(err));
      }
    }

    if (!usedEmbeddingFlow) {
      const result = await callArtifactVisionModel(
        toolkitUrl, toolkitSecret, images,
      );
      if (!result.ok) {
        const errorBody = await result.text().catch(() => "unknown error");
        console.error("Artifact AI Gateway error:", result.status, errorBody);
        return Response.json(
          { error: `Artifact identification failed (${result.status})` },
          { status: 502, headers: responseHeaders },
        );
      }
      const data = await result.json() as {
        choices?: Array<{ message?: { content?: string } }>;
      };
      const content = data.choices?.[0]?.message?.content;
      if (!content) {
        console.error("No content in artifact AI response:", JSON.stringify(data));
        return Response.json(
          { error: "No artifact identification results returned" },
          { status: 502, headers: responseHeaders },
        );
      }
      parsed = parseArtifactIdentificationResponse(content);
      modelsUsed.push("haiku-artifact");
    }

    // ── Artifact web context search ───────────────────────────────────
    // Search authoritative archaeological and museum sites for context on
    // the top artifact candidates. Feeds into the visual comparison prompt.
    const artifactWebContext = await fetchArtifactContext(
      toolkitUrl, toolkitSecret, parsed.matches,
    );
    if (artifactWebContext.text) {
      modelsUsed.push("web-search-artifact");
    }

    const visualMaxCandidates = 15;
    // Haiku visual for ALL tiers (Sonnet runs separately for premium)
    const visualResult = await callArtifactVisualComparison(
      toolkitUrl, toolkitSecret, images, parsed.matches, false, visualMaxCandidates,
      artifactWebContext.text,
    );

    let finalMatches = parsed.matches;
    let finalSummary = parsed.summary;
    let visualReferenceUsed = false;

    if (usedEmbeddingFlow) {
      modelsUsed.push("haiku-artifact-describe");
    }

    if (visualResult) {
      modelsUsed.push("haiku-artifact-visual");
      visualReferenceUsed = true;
      finalMatches = visualResult.matches;
      finalSummary = visualResult.summary;
    }

    // ── Sonnet artifact rerank (ALWAYS for premium) ──
    let sonnetDisagreed = false;
    if (isPremium && finalMatches.length > 0) {
      const sonnetResult = await callSonnetArtifactRerank(
        toolkitUrl, toolkitSecret, images, finalMatches, finalSummary,
        artifactWebContext.text,
      );
      if (sonnetResult) {
        modelsUsed.push("sonnet-artifact");
        const haikuTopId = finalMatches.length > 0 ? finalMatches[0].id : undefined;
        const merged = mergeRankings(finalMatches, sonnetResult.matches);
        finalMatches = merged.matches;
        finalSummary = merged.summary;
        sonnetDisagreed = sonnetResult.matches.length > 0 &&
          sonnetResult.matches[0].id !== haikuTopId;

        // ── Gemini if needed (disagreement or low confidence) ──
        const topConf = finalMatches.length > 0 ? finalMatches[0].confidence : 0;
        if (useGemini && (topConf < CONFIDENCE_WEB_THRESHOLD || sonnetDisagreed)) {
          const geminiResult = await callGeminiArtifactThirdOpinion(
            toolkitUrl, toolkitSecret, images,
            finalMatches, sonnetResult.matches, finalSummary,
            artifactWebContext.text,
          );
          if (geminiResult) {
            modelsUsed.push("gemini-artifact");
            const resolved = mergeRankingsThreeWay(
              finalMatches, sonnetResult.matches, geminiResult.matches,
            );
            finalMatches = resolved.matches;
            finalSummary = resolved.summary;
          }
        }
      }
    }

    const finalTopConfidence = finalMatches.length > 0
      ? finalMatches[0].confidence : 0;
    const needsClarification = finalTopConfidence < 60 && finalMatches.length > 0;

    let clarificationQuestions: ClarificationQuestion[] = [];
    let webReferences: WebReference[] = artifactWebContext.references ?? [];

    if (needsClarification && finalMatches.length > 0) {
      clarificationQuestions = await generateArtifactClarificationQuestions(
        toolkitUrl, toolkitSecret, finalMatches, finalSummary,
      );
    }

    const uncertainArtifact = finalTopConfidence < 55;

    const artifactResponse: IdentifyResponse = {
      matches: finalMatches,
      summary: finalSummary,
      needsClarification,
      clarificationQuestions,
      webReferences,
      modelsUsed,
      visualReferenceUsed,
      assemblage: undefined,
      uncertainArtifact,
    };

    return Response.json(artifactResponse, { headers: responseHeaders });
  } catch (err: unknown) {
    console.error("Artifact identify error:", String(err));
    return Response.json(
      { error: "Internal server error" },
      { status: 500, headers: cors },
    );
  }
}

// ── Artifact pipeline helpers ───────────────────────────────────────────

function artifactEmbeddingMatchesToMatchResults(
  matches: ArtifactEmbeddingMatch[],
): MatchResult[] {
  return matches.map((m) => {
    const art = ARTIFACT_MAP[m.artifact_id];
    const confidence = Math.max(1, Math.min(99, Math.round(m.max_similarity * 100)));
    return {
      id: m.artifact_id,
      name: art?.name ?? m.artifact_id,
      confidence,
      reasoning: `Embedding index match (similarity ${m.max_similarity.toFixed(3)}).`,
    };
  });
}

async function callDescribeArtifactPhoto(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
): Promise<string | null> {
  const describePrompt = `Observe this prehistoric artifact photograph carefully and describe what you see using the vocabulary an archaeologist would use.

${images.length > 1 ? `You are given ${images.length} photos of the same artifact from different angles. Examine all photos and produce a single combined description that integrates what you observe from each angle.\n\n` : ""}Describe in 3-5 sentences:
- Overall shape (lanceolate, stemmed, corner-notched, bifacial, disc, tubular, oval, triangular, pick, etc.)
- Flaking pattern (collateral, parallel, oblique, random, pressure-flaked, bifacial, unifacial)
- Base style (concave, convex, straight, notched, bifurcated, ground, grooved, shouldered)
- Material hints (chert, flint, obsidian, slate, shell, ceramic, stone, bone, wood)
- Surface treatment (incised, cord-marked, polished, serrated, cortex, retouched)
- Size and proportions if discernible
- Any hafting features (notches, stem, tang, groove)
- Cultural or temporal markers if visible

Return ONLY the description prose — no JSON, no markdown, no preamble.`;

  const userContent: Array<Record<string, unknown>> = [];
  for (let i = 0; i < images.length; i++) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${images[i].mimeType};base64,${images[i].imageBase64}` },
    });
  }
  userContent.push({ type: "text", text: describePrompt });

  const messages = [
    { role: "user", content: userContent },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "anthropic/claude-haiku-4.5",
        messages,
        max_tokens: 512,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as {
      choices?: Array<{ message?: { content?: string } }>;
    };
    const content = data.choices?.[0]?.message?.content;
    if (!content || content.trim().length === 0) return null;
    return content.trim();
  } catch (err) {
    console.error("callDescribeArtifactPhoto error:", String(err));
    return null;
  }
}

function buildArtifactSystemPrompt(): string {
  const families = [
    "Arrowheads", "Spear Points & Dart Tips", "Hand Axes & Axe Heads",
    "Drill Bits", "Flaked Stone Tools", "Stone Effigies", "Native Beads",
    "Shell Tools", "Shell Effigies", "Ornaments & Weights",
    "Pipes & Medicine Tubes", "Game Discs", "Pottery",
    "Wooden Artifacts", "Bone Tools",
  ];

  const sections = families.map((family) => {
    const items = ARTIFACT_DB.filter((a) => a.family === family);
    if (items.length === 0) return "";
    const lines = items.map((a) =>
      `- ${a.id}: "${a.name}" [${a.subFamily}] ${a.tagline} — ${a.tribe}, ${a.timePeriod}`
    );
    return `### ${family}\n${lines.join("\n")}`;
  }).filter((s) => s.length > 0);

  return `You are an expert archaeologist identifying prehistoric artifacts from photographs. The user has confirmed (or suspects) that their photo shows an artifact — a knapped stone tool, point, bead, effigy, pipe, game disc, pottery sherd, or other human-made object of stone, shell, wood, or ceramic.

Here is the complete artifact reference database (${ARTIFACT_DB.length} artifacts across ${families.length} families). Match the user's photo against these entries:

${sections.join("\n\n")}

For each match, explain which visual features (shape, flaking pattern, notching, base style, material, size hints) support the identification and what distinguishes it from similar types.`;
}

async function callArtifactVisionModel(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
): Promise<Response> {
  const systemPrompt = buildArtifactSystemPrompt();
  const userPrompt = `Analyze this artifact photograph carefully. Identify which artifact type from the database it matches.

STEP 1 — OBSERVE: Describe what you see:
- Overall shape (lanceolate, stemmed, corner-notched, bifacial, disc, tubular, etc.)
- Flaking pattern (collateral, parallel, oblique, random, pressure-flaked)
- Base style (concave, convex, straight, notched, bifurcated, ground)
- Material hints (chert, flint, obsidian, slate, shell, ceramic, stone)
- Size and proportions if discernible
- Any surface treatment (incised, cord-marked, polished, serrated)
- Cultural or temporal markers if visible

STEP 2 — COMPARE: Match against the artifact database. Consider:
- Which types share the observed shape and flaking pattern?
- Which match the base style and notching?
- Which are from the right material and region?

STEP 3 — RANK: Return exactly 5 matches with honest confidence scores:
- 90-98%: Near-certain match with multiple distinguishing features aligned
- 75-89%: Strong match with most features aligned
- 55-74%: Good match but significant lookalikes exist
- 30-54%: Possible match
- Below 30%: Unlikely but worth mentioning

Return ONLY valid JSON — no markdown, no extra text:
{
  "matches": [
    {
      "id": "artifact-id from database",
      "name": "Artifact Name",
      "confidence": 85,
      "reasoning": "2-3 sentences explaining which visual features support this match."
    }
  ],
  "summary": "3-4 sentence description of the artifact and your identification reasoning."
}`;

  const userContent: Array<Record<string, unknown>> = [];
  for (let i = 0; i < images.length; i++) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${images[i].mimeType};base64,${images[i].imageBase64}` },
    });
  }
  userContent.push({ type: "text", text: userPrompt });

  const messages = [
    {
      role: "system",
      content: [
        { type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } },
      ],
    },
    { role: "user", content: userContent },
  ];

  return fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${secret}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model: "anthropic/claude-haiku-4.5",
      messages,
      max_tokens: 4096,
      temperature: 0.2,
    }),
  });
}

async function callArtifactVisualComparison(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
  preliminaryMatches: MatchResult[],
  useSonnet: boolean,
  maxCandidates: number = 12,
  webContext: string = "",
): Promise<IdentificationResult | null> {
  if (preliminaryMatches.length === 0) return null;

  const topCandidates = preliminaryMatches.slice(0, maxCandidates);
  const refs: Array<{ id: string; name: string; imageUrl: string }> = [];
  for (const m of topCandidates) {
    const art = ARTIFACT_MAP[m.id];
    if (art?.imageUrl) {
      refs.push({ id: art.id, name: art.name, imageUrl: art.imageUrl });
    }
  }
  if (refs.length === 0) return null;

  const userContent: Array<Record<string, unknown>> = [];
  for (let i = 0; i < images.length; i++) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${images[i].mimeType};base64,${images[i].imageBase64}` },
    });
  }
  userContent.push({
    type: "text",
    text: images.length > 1
      ? `These are the user's photos of the same artifact from ${images.length} angles. Below are reference images for the top artifact candidates. Compare the user's photos against each reference image visually.`
      : "This is the user's photo. Below are reference images for the top artifact candidates. Compare the user's photo against each reference image visually.",
  });

  for (let i = 0; i < refs.length; i++) {
    userContent.push({ type: "image_url", image_url: { url: refs[i].imageUrl } });
    userContent.push({ type: "text", text: `Reference ${i + 1}: ${refs[i].name} (id: ${refs[i].id})` });
  }

  const refList = refs.map((r, i) => `${i + 1}. ${r.name} (id: ${r.id})`).join("\n");

  const webContextBlock = webContext
    ? `\n\nPublished archaeological data for top candidates:\n${webContext}\n\nUse BOTH the visual similarity AND the published archaeological data (typology, period, cultural affiliation, diagnostic features) to rank candidates. If the published data contradicts the visual appearance, note the discrepancy in your reasoning.`
    : "";

  const prompt = `You are comparing a user's artifact photo against database reference images.

The first image is the user's unknown artifact. The following images are reference photos for these candidates:
${refList}${webContextBlock}

Visually compare the user's photo against each reference image. Focus on:
- Overall shape and silhouette match
- Flaking pattern and surface treatment
- Base style, notching, and hafting features
- Material color and texture
- Proportions and size hints

Rank all candidates by how well the user's photo visually matches the reference image. You may reorder from the initial ranking if visual comparison suggests a different top match.

Return ONLY valid JSON — no markdown:
{
  "matches": [
    { "id": "artifact-id", "name": "Name", "confidence": 90, "reasoning": "Visual comparison reasoning." }
  ],
  "summary": "Updated analysis after visual comparison."
}`;

  const messages = [
    { role: "user", content: [...userContent, { type: "text", text: prompt }] },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: useSonnet ? "anthropic/claude-sonnet-4.5" : "anthropic/claude-haiku-4.5",
        messages,
        max_tokens: 4096,
        temperature: 0.15,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as {
      choices?: Array<{ message?: { content?: string } }>;
    };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseArtifactIdentificationResponse(content);
  } catch (err) {
    console.error("Artifact visual comparison error:", String(err));
    return null;
  }
}

async function callSonnetArtifactRerank(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
  preliminaryMatches: MatchResult[],
  summary: string,
  webContext: string = "",
): Promise<IdentificationResult | null> {
  const systemPrompt = buildArtifactSystemPrompt();
  const matchNames = preliminaryMatches.slice(0, 5).map((m) => `${m.name} (${m.confidence}%)`).join(", ");
  const webContextBlock = webContext
    ? `\n\nPublished archaeological data:\n${webContext}\n`
    : "";
  const userPrompt = `Re-evaluate this artifact photo independently. A first-pass model returned these candidates: ${matchNames}

Initial summary: ${summary}${webContextBlock}

Look at the ${images.length > 1 ? `${images.length} photos` : "photo"} again with fresh eyes and return your own ranked matches. You may agree, reorder, or introduce new candidates from the artifact database — but every id must exist in the reference database.

Return ONLY valid JSON — no markdown:
{
  "matches": [
    { "id": "artifact-id", "name": "Name", "confidence": 90, "reasoning": "Reasoning." }
  ],
  "summary": "Updated analysis."
}`;

  const userContent: Array<Record<string, unknown>> = [];
  for (let i = 0; i < images.length; i++) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${images[i].mimeType};base64,${images[i].imageBase64}` },
    });
  }
  userContent.push({ type: "text", text: userPrompt });

  const messages = [
    { role: "system", content: [{ type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } }] },
    { role: "user", content: userContent },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "anthropic/claude-sonnet-4.5",
        messages,
        max_tokens: 4096,
        temperature: 0.15,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as {
      choices?: Array<{ message?: { content?: string } }>;
    };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseArtifactIdentificationResponse(content);
  } catch (err) {
    console.error("Sonnet artifact re-rank error:", String(err));
    return null;
  }
}

async function callGeminiArtifactThirdOpinion(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
  haikuMatches: MatchResult[],
  sonnetMatches: MatchResult[],
  summary: string,
  webContext: string = "",
): Promise<IdentificationResult | null> {
  const allCandidates = [...haikuMatches, ...sonnetMatches];
  const systemPrompt = buildArtifactCandidateSystemPrompt(allCandidates);
  const haikuList = haikuMatches.slice(0, 5).map(m => `- ${m.name} (${m.confidence}%)`).join("\n");
  const sonnetList = sonnetMatches.slice(0, 5).map(m => `- ${m.name} (${m.confidence}%)`).join("\n");
  const webContextBlock = webContext
    ? `\n\nPublished archaeological data:\n${webContext}\n`
    : "";
  const userPrompt = `Two prior models disagreed on this artifact photo.

Model A (first pass) candidates:
${haikuList}

Model B (re-rank) candidates:
${sonnetList}

Summary: ${summary}${webContextBlock}

You are the tie-breaker. Look at the ${images.length > 1 ? `${images.length} photos` : "photo"} yourself and return your own ranked matches. Every id must exist in the reference database. Return the same JSON shape.`;

  const userContent: Array<Record<string, unknown>> = [];
  for (let i = 0; i < images.length; i++) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${images[i].mimeType};base64,${images[i].imageBase64}` },
    });
  }
  userContent.push({ type: "text", text: userPrompt });

  const messages = [
    {
      role: "system",
      content: [
        { type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } },
      ],
    },
    { role: "user", content: userContent },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model: "google/gemini-2.5-pro",
        messages,
        max_tokens: 4096,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as { choices?: Array<{ message?: { content?: string } }> };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseArtifactIdentificationResponse(content);
  } catch (err) {
    console.error("Gemini artifact third-opinion error:", String(err));
    return null;
  }
}

function buildArtifactCandidateSystemPrompt(candidates: MatchResult[]): string {
  const seen = new Set<string>();
  const rows: string[] = [];
  for (const m of candidates) {
    if (seen.has(m.id)) continue;
    seen.add(m.id);
    const art = ARTIFACT_MAP[m.id];
    if (!art) continue;
    const parts = [`- ${art.id}: "${art.name}" [${art.family} / ${art.subFamily}]`];
    if (art.tagline) parts.push(art.tagline);
    if (art.tribe) parts.push(`Culture: ${art.tribe}`);
    if (art.timePeriod) parts.push(`Period: ${art.timePeriod}`);
    rows.push(parts.join(" | "));
  }
  const candidateList = rows.join("\n");

  return `You are an expert archaeologist identifying prehistoric artifacts from photographs.

A pre-filter step has narrowed the database to these ${rows.length} candidates. Only consider these artifacts — never invent IDs.

## DIAGNOSTIC FEATURES (in priority order)
1. **Overall shape**: lanceolate, stemmed, corner-notched, bifacial, disc, tubular, oval, triangular, pick, cordiform, ovate, etc.
2. **Flaking pattern**: collateral, parallel, oblique, random, pressure-flaked, bifacial, unifacial
3. **Base style**: concave, convex, straight, notched, bifurcated, ground, grooved, shouldered
4. **Material**: chert, flint, obsidian, slate, shell, ceramic, stone, bone, wood
5. **Surface treatment**: incised, cord-marked, polished, serrated, cortex, retouched
6. **Hafting features**: notches, stem, tang, groove

## CONFIDENCE CALIBRATION
- **90-98%**: Multiple diagnostic features clearly visible and aligned
- **75-89%**: Strong match but one feature unclear or ambiguous
- **55-74%**: Good candidate but significant lookalikes exist
- **30-54%**: Possible but uncertain — photo quality or variability creates doubt
- **Below 30%**: Include only as a plausible alternative worth checking

## RULES
1. Only match against the candidates below — never invent IDs
2. Always provide exactly 5 matches, ranked by confidence highest to lowest
3. Each reasoning must mention specific visual evidence from the photo

## CANDIDATES (${rows.length})
${candidateList}`;
}

async function generateArtifactClarificationQuestions(
  toolkitUrl: string,
  secret: string,
  matches: MatchResult[],
  summary: string,
): Promise<ClarificationQuestion[]> {
  const topNames = matches.slice(0, 4).map(m => `${m.name} (${m.confidence}%)`).join(", ");
  const matchDetails = matches.slice(0, 4).map(m => {
    const art = ARTIFACT_MAP[m.id];
    return `- ${m.name}: ${m.reasoning}${art ? ` | Family: ${art.family}, Culture: ${art.tribe}, Period: ${art.timePeriod}` : ""}`;
  }).join("\n");

  const prompt = `You are helping a user identify a prehistoric artifact from a photo. The AI vision model returned these ambiguous matches:

${matchDetails}

Summary: ${summary}

The top match confidence is below 85%, meaning there's ambiguity. Generate 3-4 short questions that will help disambiguate between these candidates. Each question should have 3-5 multiple-choice options.

Focus on properties the user can easily observe:
- Overall shape (lanceolate, triangular, stemmed, corner-notched, etc.)
- Flaking pattern (parallel, oblique, random, pressure-flaked)
- Base style (concave, straight, notched, bifurcated, ground)
- Material color and type (gray chert, black obsidian, tan flint, etc.)
- Size and proportions
- Surface treatment (polished, serrated, cortex, incised)
- Context (where found — field, creek, construction site)

Return ONLY valid JSON — no markdown, no extra text:
{
  "questions": [
    {
      "id": "shape",
      "question": "What is the overall shape of the object?",
      "options": ["Lanceolate (leaf-shaped)", "Triangular", "Stemmed", "Corner-notched", "Round/disc"]
    }
  ]
}`;

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model: "anthropic/claude-haiku-4.5",
        messages: [
          { role: "system", content: "You are an archaeology expert assistant. Return only valid JSON." },
          { role: "user", content: prompt },
        ],
        max_tokens: 1500,
        temperature: 0.3,
      }),
    });
    if (!response.ok) return getDefaultArtifactQuestions(matches);
    const data = await response.json() as { choices?: Array<{ message?: { content?: string } }> };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return getDefaultArtifactQuestions(matches);

    let jsonStr = content.trim();
    const jsonMatch = jsonStr.match(/```(?:json)?\s*([\s\S]*?)```/);
    if (jsonMatch) jsonStr = jsonMatch[1].trim();
    if (!jsonStr.startsWith("{")) {
      const firstBrace = jsonStr.indexOf("{");
      const lastBrace = jsonStr.lastIndexOf("}");
      if (firstBrace !== -1 && lastBrace !== -1) {
        jsonStr = jsonStr.slice(firstBrace, lastBrace + 1);
      }
    }
    const parsed = JSON.parse(jsonStr) as { questions: ClarificationQuestion[] };
    if (!Array.isArray(parsed.questions) || parsed.questions.length === 0) {
      return getDefaultArtifactQuestions(matches);
    }
    return parsed.questions
      .filter(q => q.id && q.question && Array.isArray(q.options) && q.options.length >= 2)
      .slice(0, 4);
  } catch (err) {
    console.error("Artifact clarification question generation error:", String(err));
    return getDefaultArtifactQuestions(matches);
  }
}

function getDefaultArtifactQuestions(matches: MatchResult[]): ClarificationQuestion[] {
  const arts = matches.slice(0, 4)
    .map(m => ARTIFACT_MAP[m.id])
    .filter(Boolean) as ArtifactEntry[];

  const questions: ClarificationQuestion[] = [];

  const shapes = new Set<string>();
  arts.forEach(a => {
    const sf = a.subFamily.toLowerCase();
    if (sf.includes("lanceolate")) shapes.add("Lanceolate (leaf-shaped)");
    if (sf.includes("stemmed")) shapes.add("Stemmed");
    if (sf.includes("notched")) shapes.add("Notched");
    if (sf.includes("bifacial") || sf.includes("bifacial")) shapes.add("Bifacial (worked both sides)");
    if (sf.includes("disc") || sf.includes("oval") || sf.includes("cordiform")) shapes.add("Round / oval");
  });
  if (shapes.size > 1) {
    questions.push({
      id: "shape",
      question: "What is the overall shape of the object?",
      options: Array.from(shapes).slice(0, 5),
    });
  }

  questions.push({
    id: "material",
    question: "What material is it made from?",
    options: ["Gray/tan chert or flint", "Black obsidian", "Quartz or quartzite", "Slate or other stone", "Shell or bone"],
  });

  questions.push({
    id: "size",
    question: "How large is the object?",
    options: ["Under 1 inch", "1-2 inches", "2-4 inches", "Over 4 inches", "Not sure"],
  });

  questions.push({
    id: "context",
    question: "Where did you find it?",
    options: ["Field or plowed ground", "Creek or riverbed", "Construction site", "Desert or open land", "Bought / unknown"],
  });

  return questions.slice(0, 4);
}

function parseArtifactIdentificationResponse(content: string): IdentificationResult {
  let jsonStr = content.trim();
  const jsonMatch = jsonStr.match(/```(?:json)?\s*([\s\S]*?)```/);
  if (jsonMatch) {
    jsonStr = jsonMatch[1].trim();
  }
  if (!jsonStr.startsWith("{")) {
    const firstBrace = jsonStr.indexOf("{");
    const lastBrace = jsonStr.lastIndexOf("}");
    if (firstBrace !== -1 && lastBrace !== -1 && lastBrace > firstBrace) {
      jsonStr = jsonStr.slice(firstBrace, lastBrace + 1);
    }
  }
  try {
    const parsed = JSON.parse(jsonStr) as IdentificationResult;
    if (!Array.isArray(parsed.matches)) {
      throw new Error("matches is not an array");
    }
    const validIds = new Set(ARTIFACT_DB.map((a) => a.id));
    const idToName = new Map(ARTIFACT_DB.map((a) => [a.id, a.name]));
    parsed.matches = parsed.matches
      .filter((m) => validIds.has(m.id))
      .sort((a, b) => b.confidence - a.confidence)
      .slice(0, 5)
      .map((m) => ({
        id: m.id,
        name: idToName.get(m.id) ?? m.name ?? "Unknown",
        confidence: Math.min(100, Math.max(0, Math.round(m.confidence))),
        reasoning: m.reasoning ?? "",
      }));
    if (parsed.matches.length === 0) {
      return {
        matches: [],
        summary: parsed.summary ?? "The AI couldn't identify this as an artifact. Try a clearer photo showing the object's shape and flaking pattern.",
      };
    }
    return {
      matches: parsed.matches,
      summary: parsed.summary ?? "",
    };
  } catch {
    console.error("Failed to parse artifact AI JSON response:", jsonStr.slice(0, 500));
    return {
      matches: [],
      summary: "The AI response couldn't be parsed. Please try again with a clearer photo.",
    };
  }
}

// ── Artifact detection (legacy standalone endpoint) ─────────────────────
// Kept for backward compatibility — functions/index.ts still routes here.
// The main identify pipeline now handles artifact detection internally via
// callDescribeAndDetect, so the client no longer calls this endpoint.

export async function handleArtifactDetect(
  request: Request,
  env: Env,
  cors: Record<string, string>,
): Promise<Response> {
  try {
    const body = await request.json() as {
      imageBase64: string;
      mimeType?: string;
    };

    if (!body.imageBase64) {
      return Response.json(
        { error: "imageBase64 is required" },
        { status: 400, headers: cors },
      );
    }

    const mimeType = body.mimeType ?? "image/jpeg";
    const imageData = stripDataUriPrefix(body.imageBase64);
    const toolkitUrl = env.EXPO_PUBLIC_TOOLKIT_URL ?? "https://toolkit.rork.com";
    const toolkitSecret = env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY;

    if (!toolkitSecret) {
      return Response.json(
        { error: "Toolkit secret not configured" },
        { status: 500, headers: cors },
      );
    }

    const detectPrompt = `You are an expert archaeologist. Look at this photo and determine: is this a prehistoric artifact (knapped stone tool, arrowhead, spear point, hand axe, scraper, drill, bead, effigy, pipe, game disc, pottery sherd, or other human-made object of stone, shell, wood, or ceramic)?

Respond with ONLY a JSON object:
{"isArtifact": true/false, "confidence": 0-100}

- isArtifact: true if the object appears to be a human-made artifact
- confidence: your confidence level (0-100)

If it's clearly a natural rock, mineral, crystal, or fossil, return isArtifact: false with high confidence.
If it's clearly an artifact, return isArtifact: true with high confidence.
If you're unsure, return your best guess with appropriate confidence.`;

    const messages = [
    {
      role: "user",
      content: [
        { type: "image_url", image_url: { url: `data:${mimeType};base64,${imageData}` } },
        { type: "text", text: detectPrompt },
      ],
    },
  ];

    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${toolkitSecret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "anthropic/claude-haiku-4.5",
        messages,
        max_tokens: 256,
        temperature: 0.1,
      }),
    });

    let isArtifact = false;
    let confidence = 0;
    if (response.ok) {
      const data = await response.json() as {
        choices?: Array<{ message?: { content?: string } }>;
      };
      const content = data.choices?.[0]?.message?.content;
      if (content) {
        try {
          const cleaned = content.trim()
            .replace(/^```json\s*/i, "")
            .replace(/```$/, "")
            .trim();
          const parsed = JSON.parse(cleaned);
          isArtifact = !!parsed.isArtifact;
          confidence = Math.max(0, Math.min(100, Number(parsed.confidence) || 0));
        } catch {
          isArtifact = false;
          confidence = 0;
        }
      }
    }

    return Response.json(
      { isArtifact, confidence },
      { headers: { ...cors, "Content-Type": "application/json" } },
    );
  } catch (error) {
    return Response.json(
      { isArtifact: false, confidence: 0, error: "Detection failed" },
      { headers: { ...cors, "Content-Type": "application/json" } },
    );
  }
}

// ── Clarify handler ─────────────────────────────────────────────────────

export async function handleClarify(
  request: Request,
  env: Env,
  cors: Record<string, string>,
): Promise<Response> {
  try {
    const body = await request.json() as {
      imageBase64?: string;
      mimeType?: string;
      images?: Array<{
        imageBase64: string;
        mimeType?: string;
        angle?: string;
        description?: string;
      }>;
      answers: Record<string, string>;
      preliminaryMatches: MatchResult[];
      summary: string;
    };

    if (!body.answers || !body.preliminaryMatches) {
      return Response.json(
        { error: "answers and preliminaryMatches are required" },
        { status: 400, headers: cors },
      );
    }

    // Parse images array, falling back to legacy single imageBase64
    const images = parseAngleImages(body);
    if (images.length === 0) {
      return Response.json(
        { error: "At least one image is required" },
        { status: 400, headers: cors },
      );
    }

    const toolkitUrl = env.EXPO_PUBLIC_TOOLKIT_URL ?? "https://toolkit.rork.com";
    const toolkitSecret = env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY;

    if (!toolkitSecret) {
      return Response.json(
        { error: "Toolkit secret not configured" },
        { status: 500, headers: cors },
      );
    }

    const responseHeaders = { ...cors, "Content-Type": "application/json" };

    const result = await callClarifyModel(
      toolkitUrl,
      toolkitSecret,
      images,
      body.answers,
      body.preliminaryMatches,
      body.summary,
    );

    if (!result.ok) {
      const errorBody = await result.text().catch(() => "unknown error");
      console.error("AI Gateway error (clarify):", result.status, errorBody);
      return Response.json(
        { error: `Clarification failed (${result.status})` },
        { status: 502, headers: responseHeaders },
      );
    }

    const data = await result.json() as {
      choices?: Array<{ message?: { content?: string } }>;
    };

    const content = data.choices?.[0]?.message?.content;
    if (!content) {
      return Response.json(
        { error: "No clarification results returned" },
        { status: 502, headers: responseHeaders },
      );
    }

    const parsed = parseClarificationResponse(content);

    const webReferences = await searchWebReferences(
      toolkitUrl,
      toolkitSecret,
      parsed.matches,
    );

    const response: IdentifyResponse = {
      matches: parsed.matches,
      summary: parsed.summary,
      needsClarification: false,
      clarificationQuestions: [],
      webReferences,
    };

    return Response.json(response, { headers: responseHeaders });
  } catch (err: unknown) {
    console.error("Clarify error:", String(err));
    return Response.json(
      { error: "Internal server error" },
      { status: 500, headers: cors },
    );
  }
}

// ── Utility helpers ─────────────────────────────────────────────────────

function stripDataUriPrefix(b64: string): string {
  if (!b64.startsWith("data:")) return b64;
  const comma = b64.indexOf(",");
  return comma === -1 ? b64 : b64.slice(comma + 1);
}

function embeddingMatchesToMatchResults(matches: EmbeddingMatch[]): MatchResult[] {
  return matches.map((m) => {
    const spec = SPECIMEN_DB.find((s) => s.id === m.specimen_id);
    const confidence = Math.max(1, Math.min(99, Math.round(m.max_similarity * 100)));
    return {
      id: m.specimen_id,
      name: spec?.name ?? m.specimen_id,
      confidence,
      reasoning: `Embedding index match (similarity ${m.max_similarity.toFixed(3)}).`,
    };
  });
}

// ── Combined describe + artifact detection (NEW — replaces callDescribePhoto) ──

/** Combined Haiku call that examines ALL angle photos + descriptions and
 *  returns BOTH a synthesized description AND an artifact-detection verdict
 *  in a single JSON response. This replaces the old separate callDescribePhoto
 *  + client-side handleArtifactDetect flow, saving ~8-12s per identification. */
async function callDescribeAndDetect(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
): Promise<{ description: string; isArtifact: boolean; artifactConfidence: number; isPolished: boolean } | null> {
  // Build content with all images + per-angle descriptions
  const userContent: Array<Record<string, unknown>> = [];

  for (const img of images) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${img.mimeType};base64,${img.imageBase64}` },
    });
    if (img.description) {
      userContent.push({
        type: "text",
        text: `User's description of this ${img.angle} view: ${img.description}`,
      });
    }
  }

  const angleLabels = images.map((img) => img.angle).join(", ");
  userContent.push({
    type: "text",
    text: `You are given ${images.length} photo(s) of a specimen from different angles (${angleLabels}). Examine all photos carefully and produce a single combined description that integrates what you observe from each angle.${images.length > 1 ? " Different angles may reveal different features — synthesize them into one coherent description." : ""}

Describe in 3-6 sentences using field-geologist vocabulary:
- Primary color(s), color zoning, banding patterns
- Crystal habit or form (cubes, hexagonal prisms, blades, needles, botryoidal, massive, microcrystalline, etc.)
- Luster (metallic, vitreous, waxy, pearly, dull, adamantine, silky)
- Texture (crystalline, grainy, smooth, fibrous, bladed, banded)
- Transparency (transparent, translucent, opaque)
- Any visible matrix or associated minerals
- Any special optical effects (chatoyancy, asterism, play of color, adularescence, iridescence)
- For fossils: shape, symmetry, surface texture, skeletal features
- If the specimen appears to be a multi-mineral assemblage (e.g. granite, schist, gneiss), note the visible component minerals

ALSO determine: does this photo show a prehistoric artifact (knapped stone tool, arrowhead, spear point, hand axe, scraper, drill, bead, effigy, pipe, game disc, pottery sherd, or other human-made object)?

ALSO determine: does the specimen appear to be polished, cut, cabochon, tumbled, or otherwise lapidary-worked (as opposed to rough/natural)?

Return ONLY a JSON object — no markdown, no extra text:
{
  "description": "your combined description prose here",
  "isArtifact": false,
  "artifactConfidence": 0,
  "isPolished": false
}

- description: the combined description of the specimen
- isArtifact: true if the object appears to be a human-made artifact
- artifactConfidence: 0-100, your confidence in the artifact verdict
- isPolished: true if the specimen shows signs of lapidary work (polished cross-section, cut slab, cabochon, tumbled stone, faceted gem). false if it's rough/natural/raw.

If it's clearly a natural rock, mineral, crystal, or fossil, return isArtifact: false with high confidence.
If it's clearly an artifact, return isArtifact: true with high confidence.`,
  });

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "anthropic/claude-haiku-4.5",
        messages: [{ role: "user", content: userContent }],
        max_tokens: 768,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as {
      choices?: Array<{ message?: { content?: string } }>;
    };
    const content = data.choices?.[0]?.message?.content;
    if (!content || content.trim().length === 0) return null;

    // Parse JSON response
    let jsonStr = content.trim();
    const jsonMatch = jsonStr.match(/```(?:json)?\s*([\s\S]*?)```/);
    if (jsonMatch) jsonStr = jsonMatch[1].trim();
    if (!jsonStr.startsWith("{")) {
      const firstBrace = jsonStr.indexOf("{");
      const lastBrace = jsonStr.lastIndexOf("}");
      if (firstBrace !== -1 && lastBrace !== -1 && lastBrace > firstBrace) {
        jsonStr = jsonStr.slice(firstBrace, lastBrace + 1);
      }
    }
    const parsed = JSON.parse(jsonStr) as {
      description?: string;
      isArtifact?: boolean;
      artifactConfidence?: number;
      isPolished?: boolean;
    };
    return {
      description: (parsed.description ?? "").trim(),
      isArtifact: !!parsed.isArtifact,
      artifactConfidence: Math.max(0, Math.min(100, Math.round(parsed.artifactConfidence ?? 0))),
      isPolished: !!parsed.isPolished,
    };
  } catch (err) {
    console.error("callDescribeAndDetect error:", String(err));
    return null;
  }
}

// ── Fallback vision model (text-first flow) ─────────────────────────────

async function callVisionModel(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
): Promise<Response> {
  const systemPrompt = buildSystemPrompt();
  const userPrompt = buildUserPrompt();

  const messages = [
    {
      role: "system",
      content: [
        { type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } },
      ],
    },
    {
      role: "user",
      content: [
        {
          type: "image_url",
          image_url: { url: `data:${mimeType};base64,${imageBase64}` },
        },
        { type: "text", text: userPrompt },
      ],
    },
  ];

  const aiBody = {
    model: "anthropic/claude-haiku-4.5",
    messages,
    max_tokens: 4096,
    temperature: 0.2,
  };

  const proxyUrl = `${toolkitUrl}/v2/vercel/v1/chat/completions`;

  return fetch(proxyUrl, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${secret}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(aiBody),
  });
}

// ── Clarify model (updated for multi-angle) ─────────────────────────────

async function callClarifyModel(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
  answers: Record<string, string>,
  preliminaryMatches: MatchResult[],
  summary: string,
): Promise<Response> {
  const systemPrompt = buildSystemPrompt();
  const userPrompt = buildClarifyUserPrompt(answers, preliminaryMatches, summary);

  // Build user content with all angle images
  const userContent: Array<Record<string, unknown>> = [];
  for (const img of images) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${img.mimeType};base64,${img.imageBase64}` },
    });
  }
  userContent.push({ type: "text", text: userPrompt });

  const messages = [
    {
      role: "system",
      content: [
        { type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } },
      ],
    },
    {
      role: "user",
      content: userContent,
    },
  ];

  const aiBody = {
    model: "anthropic/claude-haiku-4.5",
    messages,
    max_tokens: 4096,
    temperature: 0.15,
  };

  const proxyUrl = `${toolkitUrl}/v2/vercel/v1/chat/completions`;

  return fetch(proxyUrl, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${secret}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(aiBody),
  });
}

// ── Visual reference comparison (updated for multi-angle + web context) ──

/** Visual reference comparison: sends ALL user photos (labeled by angle)
 *  alongside 15 database reference images, WITH web mineralogy context and
 *  per-angle descriptions fed in as additional context.
 *
 *  ALL tiers use Haiku for this pass. The prompt labels each user photo by
 *  angle so the model can cross-reference across viewpoints. */
async function callVisualReferenceComparison(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
  preliminaryMatches: MatchResult[],
  webContext: string,
  maxCandidates: number = 15,
  useSonnet: boolean = false,
  isPolished: boolean = false,
): Promise<IdentificationResult | null> {
  if (preliminaryMatches.length === 0) return null;

  // Sonnet can handle a larger reference-image budget than Haiku, but we
  // still cap it so the request stays reasonable. Each candidate shows up to
  // 3 reference images (face polished, cabochon, rough/natural).
  const REF_IMAGE_BUDGET = 15;
  const IMAGES_PER_SPECIMEN = 3;

  // Collect candidates with up to 3 reference images each.
  // When isPolished is true (Haiku detected lapidary work), prioritize
  // face-polished and cabochon reference views by selecting those first.
  const candidateRefs: Array<{
    id: string;
    name: string;
    imageUrls: string[];
    colors: string;
  }> = [];
  for (const m of preliminaryMatches) {
    const spec = SPECIMEN_DB.find(s => s.id === m.id);
    if (!spec?.imageUrl) continue;
    const allUrls = spec.imageUrls?.length ? spec.imageUrls : [spec.imageUrl];
    let selectedUrls: string[];
    if (isPolished && allUrls.length > IMAGES_PER_SPECIMEN) {
      // When the specimen is polished/cabochon, prioritize reference views
      // that show lapidary work (typically the last entries in the imageUrls
      // array — museum/cabochon shots) before rough/natural views.
      const polishedViews = allUrls.slice(-IMAGES_PER_SPECIMEN);
      const naturalViews = allUrls.slice(0, IMAGES_PER_SPECIMEN);
      selectedUrls = [...polishedViews, ...naturalViews].slice(0, IMAGES_PER_SPECIMEN);
    } else {
      selectedUrls = allUrls.slice(0, IMAGES_PER_SPECIMEN);
    }
    candidateRefs.push({
      id: spec.id,
      name: spec.name,
      imageUrls: selectedUrls,
      colors: spec.colors ?? "",
    });
  }
  if (candidateRefs.length === 0) return null;

  // Slice down to the requested maxCandidates, but also respect the per-model
  // reference-image budget. Always keep at least 6 candidates when possible.
  const budgetLimited = Math.floor(REF_IMAGE_BUDGET / IMAGES_PER_SPECIMEN);
  const refs = candidateRefs.slice(0, Math.min(maxCandidates, budgetLimited));

  // Build user content: all angle photos first (labeled), then reference images
  const userContent: Array<Record<string, unknown>> = [];

  // Add each user photo with angle label
  for (let i = 0; i < images.length; i++) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${images[i].mimeType};base64,${images[i].imageBase64}` },
    });
    userContent.push({
      type: "text",
      text: `Photo ${i + 1} — ${images[i].angle} view${images[i].description ? ` (user notes: ${images[i].description})` : ""}`,
    });
  }

  userContent.push({
    type: "text",
    text: `The above ${images.length} photo(s) show the user's unknown specimen from different angles. Below are database reference images for the top candidates. Each candidate may show up to ${IMAGES_PER_SPECIMEN} views (face polished, cabochon, rough/natural). Compare the user's photos against each reference image visually.`,
  });

  // Add reference images, grouped by candidate.
  const refListLines: string[] = [];
  for (let c = 0; c < refs.length; c++) {
    const candidate = refs[c];
    refListLines.push(`${c + 1}. ${candidate.name} (id: ${candidate.id})`);
    for (let u = 0; u < candidate.imageUrls.length; u++) {
      const label = u === 0 ? "face polished" : u === 1 ? "cabochon" : "rough/natural";
      userContent.push({
        type: "image_url",
        image_url: { url: candidate.imageUrls[u] },
      });
      userContent.push({
        type: "text",
        text: `Reference image ${c + 1}.${u + 1}: ${candidate.name} — ${label}${candidate.colors ? ` | typical colors: ${candidate.colors}` : ""}`,
      });
    }
  }
  const refList = refListLines.join("\n");

  // Build the prompt with web context
  const webContextBlock = webContext
    ? `\n\nPublished mineralogy data for top candidates:\n${webContext}\n\nUse BOTH the visual similarity across all angles AND the published properties (hardness, crystal system, luster, streak, associated minerals) to rank candidates. If the published data contradicts the visual appearance, note the discrepancy in your reasoning.`
    : "";

  const polishedNote = isPolished ? "\n\nIMPORTANT: The specimen appears to be polished, cut, or cabochon. Prioritize face-polished and cabochon reference views for comparison — the rough/natural view is provided for context only." : "";
  const agateGuidance = `If a candidate is an agate or chalcedony variety, pay special attention to the banding color sequence shown in the user's photo(s) versus the "typical colors" listed for each reference. Compare banding thickness, contrast between bands, fortification vs flat-band patterns, and translucency. A polished cross-section should match the face-polished/cabochon reference views; a rough nodule should match the rough/natural reference view.${polishedNote}`;

  const prompt = `You are comparing a user's specimen photos (from ${images.length} angle${images.length > 1 ? "s" : ""}) against database reference images.

The user's photos are labeled by angle above. The following images are reference photos for these candidates:
${refList}
${webContextBlock}

Visually compare the user's photos against each reference image. Focus on:
- Color match (hue, saturation, zoning, banding) — check across all angles
- Crystal habit / form match (cubes, prisms, botryoidal, massive, etc.)
- Luster and surface texture match — may vary by angle
- Overall visual similarity across all viewpoints
- If the specimen appears to be a multi-mineral assemblage (e.g. granite with quartz + feldspar + mica), identify the rock type AND note the visible component minerals
${agateGuidance}

Rank all candidates by how well the user's photos visually match the reference images. You may reorder from the initial ranking if visual comparison suggests a different top match.

Return ONLY valid JSON — no markdown, no extra text:
{
  "matches": [
    {
      "id": "specimen-id from the list above",
      "name": "Specimen Name",
      "confidence": 90,
      "reasoning": "2-3 sentences explaining the visual similarity or differences between the user's photos and the reference image."
    }
  ],
  "summary": "3-4 sentence visual comparison summary noting which features matched and which differed."
}`;

  userContent.push({ type: "text", text: prompt });

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model: useSonnet ? "anthropic/claude-sonnet-4.5" : "anthropic/claude-haiku-4.5",
        messages: [{ role: "user", content: userContent }],
        max_tokens: 4096,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as { choices?: Array<{ message?: { content?: string } }> };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseIdentificationResponse(content);
  } catch (err) {
    console.error("Visual reference comparison error:", String(err));
    return null;
  }
}

// ── Sonnet re-rank (updated for multi-angle + web context) ──────────────

/** Sonnet independently re-scores the specimen using all angle photos + web
 *  context. ALWAYS runs for premium — catches assemblages that Haiku might
 *  call as a single mineral at high confidence. */
async function callSonnetRerank(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
  preliminaryMatches: MatchResult[],
  summary: string,
  webContext: string,
): Promise<IdentificationResult | null> {
  const systemPrompt = buildCandidateSystemPrompt(preliminaryMatches);
  const matchList = preliminaryMatches.slice(0, 5)
    .map(m => `- ${m.name} (${m.confidence}%): ${m.reasoning}`)
    .join("\n");

  const webContextBlock = webContext
    ? `\n\nPublished mineralogy data:\n${webContext}\n`
    : "";

  const userPrompt = `Re-evaluate this specimen photo independently. A first-pass model returned these candidates:

${matchList}

Summary: ${summary}${webContextBlock}

Look at the photo(s) again with fresh eyes and return your own ranked matches. You may agree, reorder, or introduce new candidates from the database — but every id must exist in the reference database.

IMPORTANT: Check carefully if this specimen is a multi-mineral assemblage (e.g. granite = quartz + feldspar + mica, schist = mica + quartz, gneiss, basalt). If the first-pass model called a single mineral but the specimen is actually a rock with multiple visible minerals, identify the correct rock type and note the component minerals in your reasoning.

Return the same JSON shape as the first pass.`;

  // Build user content with all angle photos
  const userContent: Array<Record<string, unknown>> = [];
  for (const img of images) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${img.mimeType};base64,${img.imageBase64}` },
    });
  }
  userContent.push({ type: "text", text: userPrompt });

  const messages = [
    {
      role: "system",
      content: [
        { type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } },
      ],
    },
    {
      role: "user",
      content: userContent,
    },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model: "anthropic/claude-sonnet-4.5",
        messages,
        max_tokens: 4096,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as { choices?: Array<{ message?: { content?: string } }> };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseIdentificationResponse(content);
  } catch (err) {
    console.error("Sonnet rerank error:", String(err));
    return null;
  }
}

// ── Gemini third opinion (updated for multi-angle + web context) ────────

/** Gemini casts a tie-breaking vote when Haiku and Sonnet disagree or both
 *  land < 85%. Receives all angle photos + web context. */
async function callGeminiThirdOpinion(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
  haikuMatches: MatchResult[],
  sonnetMatches: MatchResult[],
  summary: string,
  webContext: string,
): Promise<IdentificationResult | null> {
  const allCandidates = [...haikuMatches, ...sonnetMatches];
  const systemPrompt = buildCandidateSystemPrompt(allCandidates);
  const haikuList = haikuMatches.slice(0, 5).map(m => `- ${m.name} (${m.confidence}%)`).join("\n");
  const sonnetList = sonnetMatches.slice(0, 5).map(m => `- ${m.name} (${m.confidence}%)`).join("\n");

  const webContextBlock = webContext
    ? `\n\nPublished mineralogy data:\n${webContext}\n`
    : "";

  const userPrompt = `Two prior models disagreed on this specimen photo.

Model A (first pass) candidates:
${haikuList}

Model B (re-rank) candidates:
${sonnetList}

Summary: ${summary}${webContextBlock}

You are the tie-breaker. Look at the photo(s) yourself and return your own ranked matches. Every id must exist in the reference database. Return the same JSON shape.`;

  // Build user content with all angle photos
  const userContent: Array<Record<string, unknown>> = [];
  for (const img of images) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${img.mimeType};base64,${img.imageBase64}` },
    });
  }
  userContent.push({ type: "text", text: userPrompt });

  const messages = [
    {
      role: "system",
      content: [
        { type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } },
      ],
    },
    {
      role: "user",
      content: userContent,
    },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model: "google/gemini-2.5-pro",
        messages,
        max_tokens: 4096,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as { choices?: Array<{ message?: { content?: string } }> };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseIdentificationResponse(content);
  } catch (err) {
    console.error("Gemini third-opinion error:", String(err));
    return null;
  }
}

// ── Gemini tie-breaker (new cheaper pipeline) ────────────────────────────

/** Gemini casts a tie-breaking vote when the primary visual model (Sonnet for
 *  premium, Haiku for free) returns ambiguous results. Receives all angle
 *  photos and the current top candidates. */
async function callGeminiTieBreaker(
  toolkitUrl: string,
  secret: string,
  images: AngleImage[],
  matches: MatchResult[],
  summary: string,
): Promise<IdentificationResult | null> {
  const systemPrompt = buildCandidateSystemPrompt(matches);
  const matchList = matches.slice(0, 5)
    .map(m => `- ${m.name} (${m.confidence}%): ${m.reasoning}`)
    .join("\n");

  const userPrompt = `Re-evaluate this specimen photo as a tie-breaker. The primary visual model returned these candidates:

${matchList}

Summary: ${summary}

Look at the photo(s) with fresh eyes and return your own ranked matches. Every id must exist in the reference database. Return the same JSON shape.`;

  const userContent: Array<Record<string, unknown>> = [];
  for (const img of images) {
    userContent.push({
      type: "image_url",
      image_url: { url: `data:${img.mimeType};base64,${img.imageBase64}` },
    });
  }
  userContent.push({ type: "text", text: userPrompt });

  const messages = [
    {
      role: "system",
      content: [{ type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } }],
    },
    { role: "user", content: userContent },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model: "google/gemini-2.5-pro",
        messages,
        max_tokens: 4096,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as { choices?: Array<{ message?: { content?: string } }> };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseIdentificationResponse(content);
  } catch (err) {
    console.error("Gemini tie-breaker error:", String(err));
    return null;
  }
}

// ── Assemblage analysis (kept for backward compat, not called from main flow) ──

async function callGeminiAssemblageAnalysis(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  topMatchName: string,
  summary: string,
): Promise<AssemblageResult | null> {
  const userPrompt = `Analyze this rock/mineral specimen photo for assemblage characteristics.

The initial identification suggests: ${topMatchName}
Summary: ${summary}

Look at the photo carefully. If this specimen is a multi-mineral assemblage (a host rock containing multiple distinct minerals visible together), analyze each component.

Return JSON in exactly this shape:
{
  "hostRock": "name of the main host rock",
  "components": [
    { "name": "mineral name", "percentage": 45, "evidence": "visual evidence from the photo" }
  ],
  "summary": "one-sentence description of the assemblage"
}

If the specimen is NOT an assemblage (single mineral/crystal/fossil), return:
{ "hostRock": "", "components": [], "summary": "" }`;

  const messages = [
    {
      role: "user",
      content: [
        { type: "image_url", image_url: { url: `data:${mimeType};base64,${imageBase64}` } },
        { type: "text", text: userPrompt },
      ],
    },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model: "google/gemini-2.5-pro",
        messages,
        max_tokens: 2048,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as { choices?: Array<{ message?: { content?: string } }> };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseAssemblageResponse(content);
  } catch (err) {
    console.error("Gemini assemblage analysis error:", String(err));
    return null;
  }
}

async function callSonnetAssemblageAnalysis(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  topMatchName: string,
  summary: string,
): Promise<AssemblageResult | null> {
  const userPrompt = `Analyze this rock/mineral specimen photo for assemblage characteristics.

The initial identification suggests: ${topMatchName}
Summary: ${summary}

Look at the photo carefully. If this specimen is a multi-mineral assemblage (a host rock containing multiple distinct minerals visible together), analyze each component.

Return JSON in exactly this shape:
{
  "hostRock": "name of the main host rock",
  "components": [
    { "name": "mineral name", "percentage": 45, "evidence": "visual evidence from the photo" }
  ],
  "summary": "one-sentence description of the assemblage"
}

If the specimen is NOT an assemblage (single mineral/crystal/fossil), return:
{ "hostRock": "", "components": [], "summary": "" }`;

  const messages = [
    {
      role: "user",
      content: [
        { type: "image_url", image_url: { url: `data:${mimeType};base64,${imageBase64}` } },
        { type: "text", text: userPrompt },
      ],
    },
  ];

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model: "anthropic/claude-sonnet-4.5",
        messages,
        max_tokens: 2048,
        temperature: 0.1,
      }),
    });
    if (!response.ok) return null;
    const data = await response.json() as { choices?: Array<{ message?: { content?: string } }> };
    const content = data.choices?.[0]?.message?.content;
    if (!content) return null;
    return parseAssemblageResponse(content);
  } catch (err) {
    console.error("Sonnet assemblage analysis error:", String(err));
    return null;
  }
}

function parseAssemblageResponse(content: string): AssemblageResult | null {
  let jsonStr = content.trim();
  const jsonMatch = jsonStr.match(/```(?:json)?\s*([\s\S]*?)```/);
  if (jsonMatch) {
    jsonStr = jsonMatch[1].trim();
  }
  if (!jsonStr.startsWith("{")) {
    const firstBrace = jsonStr.indexOf("{");
    const lastBrace = jsonStr.lastIndexOf("}");
    if (firstBrace !== -1 && lastBrace !== -1 && lastBrace > firstBrace) {
      jsonStr = jsonStr.slice(firstBrace, lastBrace + 1);
    }
  }
  try {
    const parsed = JSON.parse(jsonStr) as AssemblageResult;
    if (!parsed.hostRock || !parsed.components) return null;
    if (parsed.hostRock === "" && parsed.components.length === 0) return null;
    return {
      hostRock: parsed.hostRock,
      components: parsed.components.map((c) => ({
        name: c.name ?? "",
        percentage: Math.min(100, Math.max(0, Math.round(c.percentage ?? 0))),
        evidence: c.evidence ?? "",
      })).filter((c) => c.name !== ""),
      summary: parsed.summary ?? "",
    };
  } catch {
    return null;
  }
}

// ── Ranking merge helpers ───────────────────────────────────────────────

function mergeRankings(a: MatchResult[], b: MatchResult[]): { matches: MatchResult[]; summary: string } {
  const scores = new Map<string, { match: MatchResult; score: number }>();
  for (const m of a) {
    const existing = scores.get(m.id);
    const score = m.confidence + (a.indexOf(m) === 0 ? 5 : 0);
    if (!existing || score > existing.score) scores.set(m.id, { match: m, score });
    else existing.score += score * 0.5;
  }
  for (const m of b) {
    const existing = scores.get(m.id);
    const bonus = m.confidence + (b.indexOf(m) === 0 ? 5 : 0);
    if (existing) existing.score += bonus;
    else scores.set(m.id, { match: m, score: bonus });
  }
  const matches = Array.from(scores.values())
    .sort((x, y) => y.score - x.score)
    .slice(0, 5)
    .map(entry => ({
      ...entry.match,
      confidence: Math.min(98, Math.round(entry.score / 2)),
    }));
  return { matches, summary: b[0]?.reasoning ? `Consensus of two models. ${b[0].reasoning}` : "" };
}

function mergeRankingsThreeWay(
  a: MatchResult[],
  b: MatchResult[],
  c: MatchResult[],
): { matches: MatchResult[]; summary: string } {
  const scores = new Map<string, { match: MatchResult; score: number; votes: number }>();
  const lists = [a, b, c];
  for (const list of lists) {
    for (let i = 0; i < list.length; i++) {
      const m = list[i];
      const existing = scores.get(m.id);
      const score = m.confidence + (i === 0 ? 8 : 0);
      if (existing) {
        existing.score += score;
        existing.votes += 1;
      } else {
        scores.set(m.id, { match: m, score, votes: 1 });
      }
    }
  }
  const matches = Array.from(scores.values())
    .sort((x, y) => {
      if (y.votes !== x.votes) return y.votes - x.votes;
      return y.score - x.score;
    })
    .slice(0, 5)
    .map(entry => ({
      ...entry.match,
      confidence: Math.min(98, Math.round(entry.score / 3)),
    }));
  return { matches, summary: `Three-model consensus. ${c[0]?.reasoning ?? ""}` };
}

// ── Clarification questions ─────────────────────────────────────────────

async function generateClarificationQuestions(
  toolkitUrl: string,
  secret: string,
  matches: MatchResult[],
  summary: string,
): Promise<ClarificationQuestion[]> {
  const topNames = matches.slice(0, 4).map(m => `${m.name} (${m.confidence}%)`).join(", ");
  const matchDetails = matches.slice(0, 4).map(m => {
    const spec = SPECIMEN_DB.find(s => s.id === m.id);
    return `- ${m.name}: ${m.reasoning}${spec ? ` | Colors: ${spec.colors}, Hardness: ${spec.hardness}, Luster: ${spec.luster}` : ""}`;
  }).join("\n");

  const prompt = `You are helping a user identify a rock, mineral, or fossil from a photo. The AI vision model returned these ambiguous matches:

${matchDetails}

Summary: ${summary}

The top match confidence is below 60%, meaning there's significant ambiguity. Generate 3-4 short questions that will help disambiguate between these candidates. Each question should have 3-5 multiple-choice options.

Focus on properties the user can easily observe or test:
- Visual properties (color under different light, banding patterns, crystal shapes)
- Simple physical tests (hardness relative to fingernail/copper/steel, streak color)
- Context (where found, associated minerals, matrix rock)
- Tactile properties (weight/heft, texture, temperature feel)

Return ONLY valid JSON — no markdown, no extra text:
{
  "questions": [
    {
      "id": "color",
      "question": "What is the dominant color you see?",
      "options": ["Red/brown", "Green", "Blue", "Black/metallic", "Yellow/orange"]
    }
  ]
}`;

  try {
    const proxyUrl = `${toolkitUrl}/v2/vercel/v1/chat/completions`;
    const response = await fetch(proxyUrl, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "anthropic/claude-haiku-4.5",
        messages: [
          { role: "system", content: "You are a geology expert assistant. Return only valid JSON." },
          { role: "user", content: prompt },
        ],
        max_tokens: 1500,
        temperature: 0.3,
      }),
    });

    if (!response.ok) return getDefaultQuestions(matches);

    const data = await response.json() as {
      choices?: Array<{ message?: { content?: string } }>;
    };

    const content = data.choices?.[0]?.message?.content;
    if (!content) return getDefaultQuestions(matches);

    let jsonStr = content.trim();
    const jsonMatch = jsonStr.match(/```(?:json)?\s*([\s\S]*?)```/);
    if (jsonMatch) jsonStr = jsonMatch[1].trim();
    if (!jsonStr.startsWith("{")) {
      const firstBrace = jsonStr.indexOf("{");
      const lastBrace = jsonStr.lastIndexOf("}");
      if (firstBrace !== -1 && lastBrace !== -1) {
        jsonStr = jsonStr.slice(firstBrace, lastBrace + 1);
      }
    }

    const parsed = JSON.parse(jsonStr) as { questions: ClarificationQuestion[] };
    if (!Array.isArray(parsed.questions) || parsed.questions.length === 0) {
      return getDefaultQuestions(matches);
    }

    return parsed.questions
      .filter(q => q.id && q.question && Array.isArray(q.options) && q.options.length >= 2)
      .slice(0, 4);
  } catch (err) {
    console.error("Clarification question generation error:", String(err));
    return getDefaultQuestions(matches);
  }
}

function getDefaultQuestions(matches: MatchResult[]): ClarificationQuestion[] {
  const specs = matches.slice(0, 4).map(m => SPECIMEN_DB.find(s => s.id === m.id)).filter(Boolean) as SpecimenEntry[];

  const questions: ClarificationQuestion[] = [];

  const allColors = new Set<string>();
  specs.forEach(s => {
    if (s.colors) s.colors.split(",").forEach(c => allColors.add(c.trim().toLowerCase()));
  });
  if (allColors.size > 1) {
    questions.push({
      id: "color",
      question: "What is the primary color you see in the specimen?",
      options: Array.from(allColors).slice(0, 5),
    });
  }

  const lusters = new Set(specs.map(s => s.luster).filter(Boolean));
  if (lusters.size > 1) {
    questions.push({
      id: "luster",
      question: "How would you describe the surface shine?",
      options: Array.from(lusters).slice(0, 5),
    });
  }

  questions.push({
    id: "hardness",
    question: "Can you scratch it with your fingernail, a copper penny, or a steel knife?",
    options: [
      "Scratched by fingernail (very soft)",
      "Scratched by copper penny (soft)",
      "Scratched by steel knife (medium)",
      "Cannot scratch with steel knife (hard)",
      "I haven't tested hardness",
    ],
  });

  questions.push({
    id: "context",
    question: "Where did you find this specimen?",
    options: [
      "Beach or riverbed (water-worn)",
      "Desert or dry area",
      "Mountain or rocky outcrop",
      "Mine or quarry",
      "Bought from a shop / unknown",
    ],
  });

  return questions.slice(0, 4);
}

// ── Web search ──────────────────────────────────────────────────────────

/** Search authoritative mineralogy sites for reference data. Used both for
 *  feeding context into the visual comparison (via fetchMineralogyContext)
 *  and for returning web references to the user. Now constrained to
 *  authoritative domains. */
async function searchWebReferences(
  toolkitUrl: string,
  secret: string,
  matches: MatchResult[],
  isArtifact: boolean = false,
): Promise<WebReference[]> {
  if (matches.length === 0) return [];

  const topMatch = matches[0];
  const secondMatch = matches[1];

  const query = isArtifact
    ? (secondMatch
        ? `${topMatch.name} vs ${secondMatch.name} artifact identification archaeology distinguishing features`
        : `${topMatch.name} artifact identification archaeology type`)
    : (secondMatch
        ? `${topMatch.name} vs ${secondMatch.name} identification mineralogy distinguishing features`
        : `${topMatch.name} mineral identification properties`);

  const domains = isArtifact
    ? [
        "wikipedia.org",
        "archaeology.org",
        "sha.org",
        "saa.org",
        "antiquity.ac.uk",
        "britishmuseum.org",
        "metmuseum.org",
        "amnh.org",
        "si.edu",
        "nps.gov",
        "txsh-09.org",
        "lithiccastinglab.com",
        "projectilepoints.net",
        "indiana.edu",
        "umass.edu",
      ]
    : [
        "mindat.org",
        "webmineral.com",
        "minerals.net",
        "geology.com",
        "handbookofmineralogy.org",
        "rruff.info",
        "minsocam.org",
        "wikipedia.org",
      ];

  try {
    const exaUrl = `${toolkitUrl}/v2/exa/search`;
    const response = await fetch(exaUrl, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        query,
        numResults: 4,
        useAutoprompt: true,
        includeDomains: domains,
        contents: {
          text: { maxCharacters: 500 },
          highlights: { numSentences: 2, highlightsPerUrl: 1 },
        },
        type: "neural",
      }),
    });

    if (!response.ok) {
      console.error("Exa search error:", response.status);
      return [];
    }

    const data = await response.json() as ExaSearchResponse;
    if (!data.results || !Array.isArray(data.results)) return [];

    return data.results.slice(0, 4).map((r, i): WebReference => ({
      title: r.title ?? `Reference ${i + 1}`,
      url: r.url ?? "",
      snippet: r.text?.slice(0, 300) ?? (r.highlight ? r.highlight[0] : ""),
      source: extractDomain(r.url ?? ""),
    }));
  } catch (err) {
    console.error("Web search error:", String(err));
    return [];
  }
}

/** Fetch mineralogy context from authoritative sites to feed into the visual
 *  comparison prompt. Returns a formatted text block (~500-800 tokens) with
 *  key properties for the top 3 candidates, plus the web references for the
 *  response. Runs BEFORE the visual comparison so the data can be used as
 *  additional context. */
async function fetchMineralogyContext(
  toolkitUrl: string,
  secret: string,
  matches: MatchResult[],
  combinedDescription: string,
): Promise<{ text: string; references: WebReference[] }> {
  if (matches.length === 0) return { text: "", references: [] };

  // Build search queries for the top 2-3 candidates, incorporating the
  // combined multi-angle description for more targeted results.
  const topCandidates = matches.slice(0, 3);
  const candidateNames = topCandidates.map(m => m.name).join(" vs ");

  // Extract key keywords from the combined description for the search query
  const descKeywords = combinedDescription
    .split(/\s+/)
    .filter(w => w.length > 4)
    .slice(0, 8)
    .join(" ");

  const query = descKeywords
    ? `${candidateNames} ${descKeywords} mineralogy hardness crystal system luster distinguishing features`
    : `${candidateNames} mineral identification properties hardness luster crystal system`;

  try {
    const exaUrl = `${toolkitUrl}/v2/exa/search`;
    const response = await fetch(exaUrl, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        query,
        numResults: 6,
        useAutoprompt: true,
        includeDomains: [
          "mindat.org",
          "webmineral.com",
          "minerals.net",
          "geology.com",
          "handbookofmineralogy.org",
          "rruff.info",
          "minsocam.org",
          "wikipedia.org",
        ],
        contents: {
          text: { maxCharacters: 500 },
          highlights: { numSentences: 2, highlightsPerUrl: 1 },
        },
        type: "neural",
      }),
    });

    if (!response.ok) {
      console.error("Mineralogy context search error:", response.status);
      return { text: "", references: [] };
    }

    const data = await response.json() as ExaSearchResponse;
    if (!data.results || !Array.isArray(data.results)) return { text: "", references: [] };

    // Build formatted text block for the visual comparison prompt
    // Format: "Candidate: [name] — [key properties from web sources]"
    // Keep to ~500-800 tokens total (top 3 candidates × ~200-250 tokens each)
    const contextLines: string[] = [];
    for (let i = 0; i < Math.min(data.results.length, 6); i++) {
      const r = data.results[i];
      const title = r.title ?? "";
      const text = r.text ?? "";
      const source = extractDomain(r.url ?? "");
      // Truncate each entry to keep the total context manageable
      const snippet = text.slice(0, 300);
      contextLines.push(`[${title}] (${source}): ${snippet}`);
    }

    const text = contextLines.join("\n");
    const references = data.results.slice(0, 4).map((r, i): WebReference => ({
      title: r.title ?? `Reference ${i + 1}`,
      url: r.url ?? "",
      snippet: r.text?.slice(0, 300) ?? (r.highlight ? r.highlight[0] : ""),
      source: extractDomain(r.url ?? ""),
    }));

    return { text, references };
  } catch (err) {
    console.error("Mineralogy context fetch error:", String(err));
    return { text: "", references: [] };
  }
}

/** Fetch archaeological context from authoritative museum and archaeological
 *  sites to feed into the artifact visual comparison prompt. Searches for
 *  typology, period, cultural affiliation, and diagnostic features for the
 *  top artifact candidates. Returns a formatted text block for the prompt
 *  plus web references for the response. */
async function fetchArtifactContext(
  toolkitUrl: string,
  secret: string,
  matches: MatchResult[],
): Promise<{ text: string; references: WebReference[] }> {
  if (matches.length === 0) return { text: "", references: [] };

  const topCandidates = matches.slice(0, 3);
  const candidateNames = topCandidates.map(m => m.name).join(" vs ");
  const query = `${candidateNames} artifact typology period culture diagnostic features archaeology`;

  try {
    const exaUrl = `${toolkitUrl}/v2/exa/search`;
    const response = await fetch(exaUrl, {
      method: "POST",
      headers: {
        "Authorization": `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        query,
        numResults: 6,
        useAutoprompt: true,
        includeDomains: [
          "wikipedia.org",
          "archaeology.org",
          "sha.org",
          "saa.org",
          "antiquity.ac.uk",
          "britishmuseum.org",
          "metmuseum.org",
          "amnh.org",
          "si.edu",
          "nps.gov",
          "projectilepoints.net",
          "lithiccastinglab.com",
          "indiana.edu",
          "umass.edu",
        ],
        contents: {
          text: { maxCharacters: 500 },
          highlights: { numSentences: 2, highlightsPerUrl: 1 },
        },
        type: "neural",
      }),
    });

    if (!response.ok) {
      console.error("Artifact context search error:", response.status);
      return { text: "", references: [] };
    }

    const data = await response.json() as ExaSearchResponse;
    if (!data.results || !Array.isArray(data.results)) return { text: "", references: [] };

    const contextLines: string[] = [];
    for (let i = 0; i < Math.min(data.results.length, 6); i++) {
      const r = data.results[i];
      const title = r.title ?? "";
      const text = r.text ?? "";
      const source = extractDomain(r.url ?? "");
      const snippet = text.slice(0, 300);
      contextLines.push(`[${title}] (${source}): ${snippet}`);
    }

    const text = contextLines.join("\n");
    const references = data.results.slice(0, 4).map((r, i): WebReference => ({
      title: r.title ?? `Reference ${i + 1}`,
      url: r.url ?? "",
      snippet: r.text?.slice(0, 300) ?? (r.highlight ? r.highlight[0] : ""),
      source: extractDomain(r.url ?? ""),
    }));

    return { text, references };
  } catch (err) {
    console.error("Artifact context fetch error:", String(err));
    return { text: "", references: [] };
  }
}

function extractDomain(url: string): string {
  try {
    const u = new URL(url);
    return u.hostname.replace("www.", "");
  } catch {
    return "";
  }
}

// ── Prompt builders ─────────────────────────────────────────────────────

function buildUserPrompt(): string {
  return `Analyze this specimen photograph carefully. I need you to identify what rock, mineral, crystal, gem, or fossil is shown.

STEP 1 — OBSERVE: Describe what you see in detail:
- Primary color(s) and any color zoning or banding patterns
- Crystal shape/habit (if visible): cubes, hexagonal prisms, blades, needles, botryoidal, massive, etc.
- Luster: metallic, vitreous (glassy), waxy, pearly, dull, adamantine, silky
- Texture: crystalline, microcrystalline, grainy, smooth, rough, fibrous, bladed
- Any visible matrix rock or associated minerals
- Transparency: transparent, translucent, opaque
- Fracture or cleavage surfaces visible
- Any special optical effects: chatoyancy, asterism, play of color, adularescence, iridescence, fluorescence hints
- For fossils: shape, symmetry, surface texture, any visible skeletal features

STEP 2 — COMPARE: Match your observations against the database. Consider:
- Which specimens share the observed color range?
- Which match the crystal habit or texture?
- Which have compatible luster and hardness?
- For similar-looking specimens, use subtle distinguishing features (streak color, specific gravity hints from appearance, associated minerals, typical matrix)

STEP 3 — RANK: Return exactly 5 matches with honest confidence scores:
- 90-98%: Near-certain match with multiple distinguishing features aligned
- 75-89%: Strong match with most features aligned, some ambiguity
- 55-74%: Good match but significant lookalikes exist
- 30-54%: Possible match — could be this or a similar specimen
- Below 30%: Unlikely but worth mentioning as an alternative

Return ONLY valid JSON — no markdown, no extra text before or after:
{
  "matches": [
    {
      "id": "specimen-id from database",
      "name": "Specimen Name",
      "confidence": 85,
      "reasoning": "2-3 sentences explaining which visual features support this match and what distinguishes it from similar specimens."
    }
  ],
  "summary": "3-4 sentence description of what you observe in the photograph and your overall identification reasoning."
}`;
}

function buildClarifyUserPrompt(
  answers: Record<string, string>,
  preliminaryMatches: MatchResult[],
  summary: string,
): string {
  const answersText = Object.entries(answers)
    .map(([id, answer]) => `- ${id}: ${answer}`)
    .join("\n");

  const matchNames = preliminaryMatches.slice(0, 5).map(m => `${m.name} (${m.confidence}%)`).join(", ");

  return `You previously analyzed this specimen photo and returned these preliminary matches: ${matchNames}

Your initial summary: ${summary}

The user has now provided additional information to help refine the identification:

USER ANSWERS:
${answersText}

Using BOTH the visual evidence from the photo AND the user's answers above, re-evaluate and re-rank the matches. The user's answers provide real-world data (hardness tests, color observations, location context, etc.) that should help disambiguate between the candidates.

Adjust confidence scores based on how well each candidate aligns with the user's answers. If a candidate's properties match the user's description, increase its confidence. If they contradict, decrease it.

You may also consider specimens beyond the preliminary list if the user's answers strongly point to a different specimen — but only use IDs from the reference database.

Return exactly 5 matches, ranked by confidence from highest to lowest.

Return ONLY valid JSON — no markdown, no extra text:
{
  "matches": [
    {
      "id": "specimen-id from database",
      "name": "Specimen Name",
      "confidence": 90,
      "reasoning": "2-3 sentences explaining which visual features AND user-provided information support this match."
    }
  ],
  "summary": "3-4 sentence updated analysis incorporating the user's answers and your refined reasoning."
}`;
}

function buildSystemPrompt(): string {
  const minerals = SPECIMEN_DB.filter(s => 
    s.category.toLowerCase().includes("mineral") || 
    s.category.toLowerCase().includes("silicate") ||
    s.category.toLowerCase().includes("oxide") ||
    s.category.toLowerCase().includes("sulfide") ||
    s.category.toLowerCase().includes("carbonate") ||
    s.category.toLowerCase().includes("halide") ||
    s.category.toLowerCase().includes("sulfate") ||
    s.category.toLowerCase().includes("phosphate") ||
    s.category.toLowerCase().includes("native") ||
    s.category.toLowerCase().includes("borate") ||
    s.category.toLowerCase().includes("chromate") ||
    s.category.toLowerCase().includes("tungstate") ||
    s.category.toLowerCase().includes("molybdate") ||
    s.category.toLowerCase().includes("vanadate") ||
    s.category.toLowerCase().includes("arsenate")
  );
  
  const crystals = SPECIMEN_DB.filter(s => 
    s.category.toLowerCase().includes("crystal") || 
    s.category.toLowerCase().includes("gem") ||
    s.category.toLowerCase().includes("variety")
  );

  const rocks = SPECIMEN_DB.filter(s => 
    s.category.toLowerCase().includes("igneous") ||
    s.category.toLowerCase().includes("sedimentary") ||
    s.category.toLowerCase().includes("metamorphic") ||
    s.category.toLowerCase().includes("volcanic") ||
    s.category.toLowerCase().includes("plutonic") ||
    s.category.toLowerCase().includes("rock") ||
    s.category.toLowerCase().includes("clastic") ||
    s.category.toLowerCase().includes("foliated")
  );

  const fossils = SPECIMEN_DB.filter(s => 
    s.category.toLowerCase().includes("fossil") ||
    s.category.toLowerCase().includes("paleo") ||
    s.category.toLowerCase().includes("fossilized") ||
    s.category.toLowerCase().includes("coprolite") ||
    s.category.toLowerCase().includes("dinosaur") ||
    s.category.toLowerCase().includes("shark") ||
    s.category.toLowerCase().includes("ammonite") ||
    s.category.toLowerCase().includes("trilobite") ||
    s.category.toLowerCase().includes("petrified") ||
    s.category.toLowerCase().includes("organism") ||
    s.category.toLowerCase().includes("era") ||
    s.category.toLowerCase().includes("period")
  );

  const formatSpecimen = (s: SpecimenEntry): string => {
    const parts = [`- ${s.id}: "${s.name}" [${s.category}]`];
    if (s.tagline) parts.push(s.tagline);
    if (s.colors) parts.push(`Colors: ${s.colors}`);
    if (s.hardness && s.hardness !== "—") parts.push(`H=${s.hardness}`);
    if (s.luster) parts.push(`Luster: ${s.luster}`);
    if (s.crystal && s.crystal !== "N/A") parts.push(`Crystal: ${s.crystal}`);
    if (s.streak && s.streak !== "—" && s.streak !== "N/A") parts.push(`Streak: ${s.streak}`);
    return parts.join(" | ");
  };

  const mineralList = minerals.map(formatSpecimen).join("\n");
  const crystalList = crystals.map(formatSpecimen).join("\n");
  const rockList = rocks.map(formatSpecimen).join("\n");
  const fossilList = fossils.map(formatSpecimen).join("\n");

  return `You are an expert field geologist and mineralogist with 30+ years of experience identifying rocks, minerals, crystals, gems, and fossils from photographs.

Reference database: ${SPECIMEN_DB.length} known specimens, organized by category below.

## DIAGNOSTIC FEATURES (in priority order)
1. **Crystal habit**: cubic (fluorite, galena, pyrite), hexagonal prisms (quartz, beryl, tourmaline, apatite), rhombohedral (calcite, dolomite), octahedral (diamond, fluorite, magnetite), bladed (kyanite, barite, feldspar), needles/fibrous (millerite, actinolite, rutile), botryoidal (malachite, hematite, smithsonite, chalcedony), dendritic (native copper, manganese), massive/granular (granite, basalt), microcrystalline (agate, jasper, chert).
2. **Color**: brass-yellow metallic→pyrite/chalcopyrite/gold; lead-gray→galena/stibnite; purple→amethyst/fluorite/lepidolite/charoite; emerald green→malachite/dioptase/emerald/epidote; sky blue→turquoise/aquamarine/chrysocolla/larimar; pink→rhodochrosite/rose quartz/rhodonite; red→ruby/cinnabar/realgar/jasper; orange→carnelian/fire opal/crocoite/spessartine; black→obsidian/basalt/hematite/schorl/magnetite; banded→agate/malachite/rhodochrosite/banded iron.
3. **Luster**: metallic (pyrite, galena, hematite, chalcopyrite), vitreous (quartz, fluorite, calcite, topaz, beryl), waxy (chalcedony, turquoise, opal, jade), pearly (talc, muscovite, stilbite), adamantine (diamond, zircon, cerussite), silky (gypsum satin spar, fibrous malachite), dull/earthy (kaolinite, limonite, bauxite).
4. **Texture**: banded concentric→agate/malachite/onyx; fibrous→asbestos/actinolite; granular interlocking→granite/gabbro/pegmatite; conchoidal fracture→obsidian/opal/quartz/flint; foliated→slate/schist/gneiss; vesicular→pumice/scoria; oolitic→oolitic limestone/hematite.
5. **Optical effects**: play of color→precious opal; chatoyancy→cat's eye/tiger's eye; asterism→star sapphire/ruby; adularescence→moonstone/larvikite; labradorescence→labradorite/spectrolite; iridescence→bornite/ammolite; color change→alexandrite/zultanite.
6. **Fossils**: spiral shells→ammonite/nautiloid/gastropod; segmented stems→crinoid/calamites; leaf imprints→fossil fern/ginkgo; teeth→shark/megalodon/dinosaur; bone cross-sections→dinosaur bone/petrified wood; enrolled→trilobite.

## CONFIDENCE CALIBRATION
- **90-98%**: Multiple diagnostic features clearly visible and aligned
- **75-89%**: Strong match but one feature unclear or ambiguous
- **55-74%**: Good candidate but significant lookalikes exist
- **30-54%**: Possible but uncertain — photo quality or variability creates doubt
- **Below 30%**: Include only as a plausible alternative worth checking

## RULES
1. Only match against specimens in the database below — never invent IDs
2. If not a rock/mineral/fossil, say so in summary and return empty matches
3. If too blurry/dark, say so and return low-confidence matches
4. Distinguish similar specimens using subtle features (streak, luster, associated minerals, crystal system)
5. For polished/cut stones, consider both rough mineral identity and cut form
6. For rocks, identify the rock type AND any prominent minerals visible
7. Always provide exactly 5 matches, ranked by confidence highest to lowest
8. Each reasoning must mention specific visual evidence from the photo

## REFERENCE DATABASE (${SPECIMEN_DB.length} specimens)

### MINERALS & ORES (${minerals.length} specimens)
${mineralList}

### CRYSTALS & GEMSTONES (${crystals.length} specimens)
${crystalList}

### ROCKS — IGNEOUS, SEDIMENTARY, METAMORPHIC (${rocks.length} specimens)
${rockList}

### FOSSILS & PALEONTOLOGY (${fossils.length} specimens)
${fossilList}`;
}

function buildCandidateSystemPrompt(candidates: MatchResult[]): string {
  const seen = new Set<string>();
  const rows: string[] = [];
  for (const m of candidates) {
    if (seen.has(m.id)) continue;
    seen.add(m.id);
    const spec = SPECIMEN_DB.find((s) => s.id === m.id);
    if (!spec) continue;
    const parts = [`- ${spec.id}: "${spec.name}" [${spec.category}]`];
    if (spec.tagline) parts.push(spec.tagline);
    if (spec.colors) parts.push(`Colors: ${spec.colors}`);
    if (spec.hardness && spec.hardness !== "—") parts.push(`H=${spec.hardness}`);
    if (spec.luster) parts.push(`Luster: ${spec.luster}`);
    if (spec.crystal && spec.crystal !== "N/A") parts.push(`Crystal: ${spec.crystal}`);
    if (spec.streak && spec.streak !== "—" && spec.streak !== "N/A") parts.push(`Streak: ${spec.streak}`);
    rows.push(parts.join(" | "));
  }
  const candidateList = rows.join("\n");

  return `You are an expert field geologist and mineralogist with 30+ years of experience identifying rocks, minerals, crystals, gems, and fossils from photographs.

A pre-filter step has narrowed the database to these ${rows.length} candidates. Only consider these specimens — never invent IDs.

## DIAGNOSTIC FEATURES (in priority order)
1. **Crystal habit**: cubic (fluorite, galena, pyrite), hexagonal prisms (quartz, beryl, tourmaline, apatite), rhombohedral (calcite, dolomite), octahedral (diamond, fluorite, magnetite), bladed (kyanite, barite, feldspar), needles/fibrous (millerite, actinolite, rutile), botryoidal (malachite, hematite, smithsonite, chalcedony), dendritic (native copper, manganese), massive/granular (granite, basalt), microcrystalline (agate, jasper, chert).
2. **Color**: brass-yellow metallic→pyrite/chalcopyrite/gold; lead-gray→galena/stibnite; purple→amethyst/fluorite/lepidolite/charoite; emerald green→malachite/dioptase/emerald/epidote; sky blue→turquoise/aquamarine/chrysocolla/larimar; pink→rhodochrosite/rose quartz/rhodonite; red→ruby/cinnabar/realgar/jasper; orange→carnelian/fire opal/crocoite/spessartine; black→obsidian/basalt/hematite/schorl/magnetite; banded→agate/malachite/rhodochrosite/banded iron.
3. **Luster**: metallic (pyrite, galena, hematite, chalcopyrite), vitreous (quartz, fluorite, calcite, topaz, beryl), waxy (chalcedony, turquoise, opal, jade), pearly (talc, muscovite, stilbite), adamantine (diamond, zircon, cerussite), silky (gypsum satin spar, fibrous malachite), dull/earthy (kaolinite, limonite, bauxite).
4. **Texture**: banded concentric→agate/malachite/onyx; fibrous→asbestos/actinolite; granular interlocking→granite/gabbro/pegmatite; conchoidal fracture→obsidian/opal/quartz/flint; foliated→slate/schist/gneiss; vesicular→pumice/scoria; oolitic→oolitic limestone/hematite.
5. **Optical effects**: play of color→precious opal; chatoyancy→cat's eye/tiger's eye; asterism→star sapphire/ruby; adularescence→moonstone/larvikite; labradorescence→labradorite/spectrolite; iridescence→bornite/ammolite; color change→alexandrite/zultanite.
6. **Fossils**: spiral shells→ammonite/nautiloid/gastropod; segmented stems→crinoid/calamites; leaf imprints→fossil fern/ginkgo; teeth→shark/megalodon/dinosaur; bone cross-sections→dinosaur bone/petrified wood; enrolled→trilobite.

## CONFIDENCE CALIBRATION
- **90-98%**: Multiple diagnostic features clearly visible and aligned
- **75-89%**: Strong match but one feature unclear or ambiguous
- **55-74%**: Good candidate but significant lookalikes exist
- **30-54%**: Possible but uncertain — photo quality or variability creates doubt
- **Below 30%**: Include only as a plausible alternative worth checking

## RULES
1. Only match against the candidates below — never invent IDs
2. If not a rock/mineral/fossil, say so in summary and return empty matches
3. If too blurry/dark, say so and return low-confidence matches
4. Distinguish similar candidates using subtle features (streak, luster, associated minerals, crystal system)
5. For polished/cut stones, consider both rough mineral identity and cut form
6. For rocks, identify the rock type AND any prominent minerals visible
7. Always provide exactly 5 matches, ranked by confidence highest to lowest
8. Each reasoning must mention specific visual evidence from the photo

## CANDIDATES (${rows.length})
${candidateList}`;
}

// ── Types ───────────────────────────────────────────────────────────────

interface MatchResult {
  id: string;
  name: string;
  confidence: number;
  reasoning: string;
}

interface IdentificationResult {
  matches: MatchResult[];
  summary: string;
}

interface ClarificationQuestion {
  id: string;
  question: string;
  options: string[];
}

interface WebReference {
  title: string;
  url: string;
  snippet: string;
  source: string;
}

interface AssemblageComponent {
  name: string;
  percentage: number;
  evidence: string;
}

interface AssemblageResult {
  hostRock: string;
  components: AssemblageComponent[];
  summary: string;
}

interface IdentifyResponse {
  matches: MatchResult[];
  summary: string;
  needsClarification: boolean;
  clarificationQuestions: ClarificationQuestion[];
  webReferences: WebReference[];
  error?: string;
  modelsUsed?: string[];
  visualReferenceUsed?: boolean;
  assemblage?: AssemblageResult;
  uncertainArtifact?: boolean;
  artifactDetected?: boolean;
  artifactConfidence?: number;
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

// ── Parsing ─────────────────────────────────────────────────────────────

function parseIdentificationResponse(content: string): IdentificationResult {
  let jsonStr = content.trim();

  const jsonMatch = jsonStr.match(/```(?:json)?\s*([\s\S]*?)```/);
  if (jsonMatch) {
    jsonStr = jsonMatch[1].trim();
  }

  if (!jsonStr.startsWith("{")) {
    const firstBrace = jsonStr.indexOf("{");
    const lastBrace = jsonStr.lastIndexOf("}");
    if (firstBrace !== -1 && lastBrace !== -1 && lastBrace > firstBrace) {
      jsonStr = jsonStr.slice(firstBrace, lastBrace + 1);
    }
  }

  try {
    const parsed = JSON.parse(jsonStr) as IdentificationResult;

    if (!Array.isArray(parsed.matches)) {
      throw new Error("matches is not an array");
    }

    const validIds = new Set(SPECIMEN_DB.map((s) => s.id));
    const idToName = new Map(SPECIMEN_DB.map((s) => [s.id, s.name]));
    
    parsed.matches = parsed.matches
      .filter((m) => validIds.has(m.id))
      .sort((a, b) => b.confidence - a.confidence)
      .slice(0, 5)
      .map((m) => ({
        id: m.id,
        name: idToName.get(m.id) ?? m.name ?? "Unknown",
        confidence: Math.min(100, Math.max(0, Math.round(m.confidence))),
        reasoning: m.reasoning ?? "",
      }));

    if (parsed.matches.length === 0) {
      return {
        matches: [],
        summary: parsed.summary ?? "The AI couldn't identify this as a rock or mineral. Try a clearer photo with good lighting.",
      };
    }

    return {
      matches: parsed.matches,
      summary: parsed.summary ?? "",
    };
  } catch {
    console.error("Failed to parse AI JSON response:", jsonStr.slice(0, 500));
    return {
      matches: [],
      summary: "The AI response couldn't be parsed. Please try again with a clearer photo.",
    };
  }
}

function parseClarificationResponse(content: string): IdentificationResult {
  let jsonStr = content.trim();

  const jsonMatch = jsonStr.match(/```(?:json)?\s*([\s\S]*?)```/);
  if (jsonMatch) {
    jsonStr = jsonMatch[1].trim();
  }

  if (!jsonStr.startsWith("{")) {
    const firstBrace = jsonStr.indexOf("{");
    const lastBrace = jsonStr.lastIndexOf("}");
    if (firstBrace !== -1 && lastBrace !== -1 && lastBrace > firstBrace) {
      jsonStr = jsonStr.slice(firstBrace, lastBrace + 1);
    }
  }

  try {
    const parsed = JSON.parse(jsonStr) as IdentificationResult;

    if (!Array.isArray(parsed.matches)) {
      throw new Error("matches is not an array");
    }

    const validIds = new Set(SPECIMEN_DB.map((s) => s.id));
    const idToName = new Map(SPECIMEN_DB.map((s) => [s.id, s.name]));
    
    parsed.matches = parsed.matches
      .filter((m) => validIds.has(m.id))
      .sort((a, b) => b.confidence - a.confidence)
      .slice(0, 5)
      .map((m) => ({
        id: m.id,
        name: idToName.get(m.id) ?? m.name ?? "Unknown",
        confidence: Math.min(100, Math.max(0, Math.round(m.confidence))),
        reasoning: m.reasoning ?? "",
      }));

    if (parsed.matches.length === 0) {
      throw new Error("No valid matches after clarification");
    }

    return {
      matches: parsed.matches,
      summary: parsed.summary ?? "",
    };
  } catch {
    console.error("Failed to parse clarification JSON:", jsonStr.slice(0, 500));
    throw new Error("Failed to parse clarification results");
  }
}

type Env = {
  EXPO_PUBLIC_TOOLKIT_URL: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY: string;
  EXPO_PUBLIC_SUPABASE_URL?: string;
  EXPO_PUBLIC_SUPABASE_ANON_KEY?: string;
};

// Resolved import for Supabase URL correction.
import { resolveSupabaseUrl } from "./auth";
