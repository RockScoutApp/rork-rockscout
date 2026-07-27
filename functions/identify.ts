import { SPECIMEN_DB, type SpecimenEntry } from "./specimens";
import { ARTIFACT_DB, ARTIFACT_MAP, type ArtifactEntry } from "./artifacts";
import {
  embedText,
  matchSpecimenEmbeddings,
  matchArtifactEmbeddings,
  type EmbeddingMatch,
  type ArtifactEmbeddingMatch,
} from "./embeddings";

export async function handleIdentify(
  request: Request,
  env: Env,
  cors: Record<string, string>,
): Promise<Response> {
  try {
    const body = await request.json() as {
      imageBase64: string;
      mimeType?: string;
      /** Caller's entitlement tier — drives the accuracy ladder.
       *  "free" = Haiku only, "premium" = Haiku + Sonnet re-rank on ambiguous,
       *  "pro" = Haiku + Sonnet + Gemini third opinion on the hardest cases.
       *  Defaults to "free" so missing/legacy callers stay on the safe path. */
      entitlement?: string;
      /** Search mode: "rocks" (default — current behavior, artifacts excluded)
       *  or "artifacts" (artifacts prioritized in the candidate set). */
      searchMode?: string;
    };

    if (!body.imageBase64) {
      return Response.json(
        { error: "imageBase64 is required" },
        { status: 400, headers: cors },
      );
    }

    const mimeType = body.mimeType ?? "image/jpeg";
    const imageData = stripDataUriPrefix(body.imageBase64);
    const tier = ((body.entitlement ?? "free") as string).toLowerCase();
    const searchMode = (body.searchMode ?? "rocks").toLowerCase();

    // ── Artifact identification branch ───────────────────────────────────
    // When the user confirms (or suspects) they're scanning an artifact, we
    // route to a dedicated artifact-first pipeline that uses ARTIFACT_DB as
    // the candidate set instead of the rock/mineral/fossil specimen database.
    // The rock-ID flow below is completely untouched.
    if (searchMode === "artifacts") {
      return await identifyArtifact(request, env, cors, imageData, mimeType, tier);
    }
    // Premium and legacy Pro both get all 3 models (Haiku + Sonnet + Gemini).
    // Free stays Haiku-only.
    const useSonnet = tier === "premium" || tier === "pro";
    const useGemini = tier === "premium" || tier === "pro";

    const toolkitUrl = env.EXPO_PUBLIC_TOOLKIT_URL ?? "https://toolkit.rork.com";
    const toolkitSecret = env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY;

    if (!toolkitSecret) {
      return Response.json(
        { error: "Toolkit secret not configured" },
        { status: 500, headers: cors },
      );
    }

    const responseHeaders = { ...cors, "Content-Type": "application/json" };

    // ── Embedding-first identification pipeline ──────────────────────────
    // New flow: describe photo → embed description → pgvector match →
    // narrowed candidates → visual comparison. Falls back to the old
    // text-first Haiku pass (full 58K-token DB in system prompt) if Supabase
    // or the embedding pipeline is unavailable.
    const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
    const supabaseAnonKey = env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
    const embeddingEnabled = !!(supabaseUrl && supabaseAnonKey);

    let parsed: IdentificationResult;
    let usedEmbeddingFlow = false;

    if (embeddingEnabled) {
      try {
        // Step 1: Lightweight Haiku pass — describe the photo in specimen
        // vocabulary (no DB in context, just visual observation).
        const photoDescription = await callDescribePhoto(
          toolkitUrl, toolkitSecret, imageData, mimeType,
        );
        if (photoDescription) {
          // Step 2: Embed the description and match against pgvector.
          const queryEmbedding = await embedText(toolkitUrl, toolkitSecret, photoDescription);
          const matchCount = useSonnet ? 20 : 25;
          const embeddingMatches = await matchSpecimenEmbeddings(
            supabaseUrl!, supabaseAnonKey!, queryEmbedding, matchCount,
          );
          if (embeddingMatches.length > 0) {
            parsed = {
              matches: embeddingMatchesToMatchResults(embeddingMatches),
              summary: "Narrowed by embedding index — pending visual comparison.",
            };
            usedEmbeddingFlow = true;
          }
        }
      } catch (err) {
        console.error("Embedding flow failed, falling back to text-first:", String(err));
      }
    }

    if (!usedEmbeddingFlow) {
      // Fallback: old text-first flow (full 58K-token DB in system prompt).
      const result = await callVisionModel(toolkitUrl, toolkitSecret, imageData, mimeType);
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
    }

    // Determine if clarification is needed (top match below 85%)
    const topConfidence = parsed.matches.length > 0 ? parsed.matches[0].confidence : 0;
    const isAmbiguous = topConfidence < 85 && parsed.matches.length > 0;

    // ── Visual reference comparison ───────────────────────────────────────
    // In the embedding-first flow, this is the primary identification step —
    // the model compares the user's photo against the reference images of the
    // embedding-narrowed candidates (25 for free, 20 for premium).
    // In the fallback flow, it refines the top 5 from the text-first pass.
    //   • Free tier → Haiku visual comparison
    //   • Premium/Pro → Sonnet visual comparison
    // If the visual match confidence is >= 92%, short-circuit the entire
    // remaining pipeline (Sonnet re-rank, Gemini, web search, clarification).
    const visualMaxCandidates = usedEmbeddingFlow ? (useSonnet ? 20 : 25) : 5;
    const visualResult = await callVisualReferenceComparison(
      toolkitUrl, toolkitSecret, imageData, mimeType, parsed.matches, useSonnet, visualMaxCandidates,
    );

    let finalMatches = parsed.matches;
    let finalSummary = parsed.summary;
    const modelsUsed: string[] = usedEmbeddingFlow ? ["embedding", "haiku-describe"] : ["haiku"];
    let sonnetDisagreed = false;
    let visualReferenceUsed = false;
    let visualShortCircuit = false;

    if (visualResult) {
      modelsUsed.push(useSonnet ? "sonnet-visual" : "haiku-visual");
      visualReferenceUsed = true;
      finalMatches = visualResult.matches;
      finalSummary = visualResult.summary;

      const visualTopConf = visualResult.matches.length > 0 ? visualResult.matches[0].confidence : 0;
      if (visualTopConf >= 92) {
        visualShortCircuit = true;
      }
    }

    // Recalculate ambiguity after the visual step — the visual comparison may
    // have resolved what was previously ambiguous, or vice versa.
    const visualTopConfidence = finalMatches.length > 0 ? finalMatches[0].confidence : 0;
    const isAmbiguousAfterVisual = visualTopConfidence < 85 && finalMatches.length > 0;

    if (!visualShortCircuit && useSonnet && isAmbiguousAfterVisual) {
      const sonnetResult = await callSonnetRerank(
        toolkitUrl, toolkitSecret, imageData, mimeType, finalMatches, finalSummary,
      );
      if (sonnetResult) {
        modelsUsed.push("sonnet");
        const merged = mergeRankings(finalMatches, sonnetResult.matches);
        finalMatches = merged.matches;
        finalSummary = merged.summary;
        // Compare Sonnet's top against the ORIGINAL Haiku top (before merge),
        // not the merged result — otherwise we'd always see "agreement" after
        // the merge overwrites the ranking.
        const haikuTopId = visualResult?.matches?.[0]?.id ?? finalMatches[0]?.id;
        sonnetDisagreed = sonnetResult.matches.length > 0 &&
          sonnetResult.matches[0].id !== haikuTopId;

        // Pro: Gemini third opinion when both Haiku and Sonnet land < 85% or disagree
        const sonnetTopConf = sonnetResult.matches.length > 0 ? sonnetResult.matches[0].confidence : 0;
        if (useGemini && (sonnetTopConf < 85 || sonnetDisagreed)) {
          const geminiResult = await callGeminiThirdOpinion(
            toolkitUrl, toolkitSecret, imageData, mimeType, finalMatches, sonnetResult.matches, finalSummary,
          );
          if (geminiResult) {
            modelsUsed.push("gemini");
            const resolved = mergeRankingsThreeWay(finalMatches, sonnetResult.matches, geminiResult.matches);
            finalMatches = resolved.matches;
            finalSummary = resolved.summary;
          }
        }
      }
    }

    const needsClarification = !visualShortCircuit && isAmbiguousAfterVisual;

    let clarificationQuestions: ClarificationQuestion[] = [];
    let webReferences: WebReference[] = [];

    if (needsClarification && finalMatches.length > 0) {
      // Generate questions tailored to the ambiguous matches
      clarificationQuestions = await generateClarificationQuestions(
        toolkitUrl,
        toolkitSecret,
        finalMatches,
        finalSummary,
      );

      // Cross-reference with web search for additional accuracy
      webReferences = await searchWebReferences(
        toolkitUrl,
        toolkitSecret,
        finalMatches,
      );
    }

    // ── Assemblage auto-detection (Phase 8) ───────────────────────────────
    // After the tiered accuracy ladder, check if the top match looks like a
    // multi-mineral assemblage. If the summary mentions multiple minerals or
    // the top match is a rock type, run a Gemini assemblage analysis pass.
    let assemblageResult: AssemblageResult | null = null;
    const topMatchName = finalMatches.length > 0 ? finalMatches[0].name : "";
    const assemblageKeywords = ["assemblage", "with", "in ", "hosting", "containing", "bearing", "veins", "included", "association"];
    const looksLikeAssemblage = assemblageKeywords.some(kw => 
      finalSummary.toLowerCase().includes(kw) || topMatchName.toLowerCase().includes(kw)
    ) || finalMatches.some(m => 
      m.reasoning.toLowerCase().includes("assemblage") || 
      m.reasoning.toLowerCase().includes("multiple minerals") ||
      m.reasoning.toLowerCase().includes("host rock")
    );
    if (looksLikeAssemblage && finalMatches.length > 0) {
      // Sonnet is the primary assemblage model. If its top component confidence
      // is below 88%, run Gemini as a second pass and keep whichever result has
      // the higher top component percentage. If Sonnet fails entirely, fall
      // back to Gemini.
      const sonnetResult = await callSonnetAssemblageAnalysis(
        toolkitUrl, toolkitSecret, imageData, mimeType, topMatchName, finalSummary,
      );
      if (sonnetResult) {
        modelsUsed.push("sonnet-assemblage");
        const sonnetTopPct = Math.max(0, ...sonnetResult.components.map(c => c.percentage));
        if (sonnetTopPct < 88) {
          // Low confidence — run Gemini as a second pass.
          const geminiResult = await callGeminiAssemblageAnalysis(
            toolkitUrl, toolkitSecret, imageData, mimeType, topMatchName, finalSummary,
          );
          if (geminiResult) {
            modelsUsed.push("gemini-assemblage");
            const geminiTopPct = Math.max(0, ...geminiResult.components.map(c => c.percentage));
            assemblageResult = geminiTopPct > sonnetTopPct ? geminiResult : sonnetResult;
          } else {
            assemblageResult = sonnetResult;
          }
        } else {
          assemblageResult = sonnetResult;
        }
      } else {
        // Sonnet failed — fall back to Gemini.
        assemblageResult = await callGeminiAssemblageAnalysis(
          toolkitUrl, toolkitSecret, imageData, mimeType, topMatchName, finalSummary,
        );
        if (assemblageResult) {
          modelsUsed.push("gemini-assemblage");
        }
      }
    }

    const response: IdentifyResponse = {
      matches: finalMatches,
      summary: finalSummary,
      needsClarification,
      clarificationQuestions,
      webReferences,
      modelsUsed,
      visualReferenceUsed,
      assemblage: assemblageResult ?? undefined,
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

/**
 * Artifact-first identification pipeline. Runs when the user confirms (or
 * suspects) they're scanning an artifact — uses ARTIFACT_DB as the candidate
 * set instead of the rock/mineral/fossil specimen database.
 *
 * Follows the SAME embedding-first pipeline as the rock-ID flow:
 *   1. Haiku describes the photo in artifact vocabulary (no DB in context)
 *   2. The description is embedded and matched against the artifact_embeddings
 *      pgvector index to narrow candidates
 *   3. Visual reference comparison against the narrowed candidate images
 *   4. Optional Sonnet re-rank on ambiguous matches (top < 85%)
 * Falls back to the old text-first flow (full DB in system prompt) if the
 * embedding pipeline is unavailable.
 *
 * The rock-ID path (handleIdentify above) is completely untouched — this is
 * a separate, self-contained branch.
 */
async function identifyArtifact(
  request: Request,
  env: Env,
  cors: Record<string, string>,
  imageData: string,
  mimeType: string,
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

  const useSonnet = tier === "premium" || tier === "pro";
  const useGemini = tier === "premium" || tier === "pro";
  const modelsUsed: string[] = ["artifact-mode"];

  // ── Embedding-first pipeline (mirrors the rock-ID flow) ──────────────
  const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
  const supabaseAnonKey = env.EXPO_PUBLIC_SUPABASE_ANON_KEY;
  const embeddingEnabled = !!(supabaseUrl && supabaseAnonKey);

  try {
    let parsed: IdentificationResult;
    let usedEmbeddingFlow = false;

    if (embeddingEnabled) {
      try {
        // Step 1: Lightweight Haiku pass — describe the photo in artifact
        // vocabulary (no DB in context, just visual observation).
        const photoDescription = await callDescribeArtifactPhoto(
          toolkitUrl, toolkitSecret, imageData, mimeType,
        );
        if (photoDescription) {
          // Step 2: Embed the description and match against pgvector.
          const queryEmbedding = await embedText(toolkitUrl, toolkitSecret, photoDescription);
          const matchCount = useSonnet ? 20 : 25;
          const embeddingMatches = await matchArtifactEmbeddings(
            supabaseUrl!, supabaseAnonKey!, queryEmbedding, matchCount,
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
      // Fallback: old text-first flow (full 106-artifact DB in system prompt).
      const result = await callArtifactVisionModel(
        toolkitUrl, toolkitSecret, imageData, mimeType,
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
      parsed = parseIdentificationResponse(content);
      modelsUsed.push("haiku-artifact");
    }

    // Step 3: Visual reference comparison — send the user's photo alongside
    // the top artifact reference images for a direct visual match.
    // In the embedding-first flow, this is the primary identification step.
    const visualMaxCandidates = usedEmbeddingFlow
      ? (useSonnet ? 20 : 25)
      : (useSonnet ? 12 : 15);
    const visualResult = await callArtifactVisualComparison(
      toolkitUrl, toolkitSecret, imageData, mimeType, parsed.matches, useSonnet, visualMaxCandidates,
    );

    let finalMatches = parsed.matches;
    let finalSummary = parsed.summary;
    let visualReferenceUsed = false;
    let visualShortCircuit = false;

    if (usedEmbeddingFlow) {
      modelsUsed.push("haiku-artifact-describe");
    }

    if (visualResult) {
      modelsUsed.push(useSonnet ? "sonnet-artifact-visual" : "haiku-artifact-visual");
      visualReferenceUsed = true;
      finalMatches = visualResult.matches;
      finalSummary = visualResult.summary;

      // Short-circuit if visual confidence is very high (same as rock flow).
      const visualTopConf = visualResult.matches.length > 0
        ? visualResult.matches[0].confidence : 0;
      if (visualTopConf >= 92) {
        visualShortCircuit = true;
      }
    }

    // Step 4: Optional Sonnet re-rank on ambiguous artifact matches (top < 85%).
    // Mirrors the rock-ID tiered accuracy ladder exactly.
    let sonnetDisagreed = false;
    const topConf = finalMatches.length > 0 ? finalMatches[0].confidence : 0;
    if (!visualShortCircuit && useSonnet && topConf < 85 && finalMatches.length > 0) {
      const sonnetResult = await callSonnetArtifactRerank(
        toolkitUrl, toolkitSecret, imageData, mimeType, finalMatches, finalSummary,
      );
      if (sonnetResult) {
        modelsUsed.push("sonnet-artifact");
        const haikuTopId = finalMatches.length > 0 ? finalMatches[0].id : undefined;
        const merged = mergeRankings(finalMatches, sonnetResult.matches);
        finalMatches = merged.matches;
        finalSummary = merged.summary;
        sonnetDisagreed = sonnetResult.matches.length > 0 &&
          sonnetResult.matches[0].id !== haikuTopId;

        // Gemini third opinion when both Sonnet and Haiku land < 85% or disagree.
        // Mirrors the rock-ID callGeminiThirdOpinion gate.
        const sonnetTopConf = sonnetResult.matches.length > 0
          ? sonnetResult.matches[0].confidence : 0;
        if (useGemini && (sonnetTopConf < 85 || sonnetDisagreed)) {
          const geminiResult = await callGeminiArtifactThirdOpinion(
            toolkitUrl, toolkitSecret, imageData, mimeType,
            finalMatches, sonnetResult.matches, finalSummary,
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

    // Step 5: Web search + clarification when still ambiguous after the full
    // accuracy ladder. Mirrors the rock-ID flow exactly.
    const finalTopConfidence = finalMatches.length > 0
      ? finalMatches[0].confidence : 0;
    const needsClarification = !visualShortCircuit && finalTopConfidence < 85 && finalMatches.length > 0;

    let clarificationQuestions: ClarificationQuestion[] = [];
    let webReferences: WebReference[] = [];

    if (needsClarification && finalMatches.length > 0) {
      clarificationQuestions = await generateArtifactClarificationQuestions(
        toolkitUrl, toolkitSecret, finalMatches, finalSummary,
      );
      webReferences = await searchWebReferences(toolkitUrl, toolkitSecret, finalMatches);
    }

    // Uncertainty flag — only fires when the ENTIRE pipeline (database,
    // Haiku, Sonnet, Gemini, and web search) still can't produce a
    // reasonably confident match. The app shows a notification that the
    // object could not be fully distinguished between an actual artifact
    // and a similar-shaped natural rock.
    const uncertainArtifact = !visualShortCircuit && finalTopConfidence < 55;

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

/** Convert artifact embedding match RPC results into MatchResult objects
 *  for the visual comparison step. Mirrors embeddingMatchesToMatchResults
 *  for specimens — confidence is mapped from cosine similarity (0–1) to
 *  the 0–100 scale. Reasoning is a placeholder; the real reasoning comes
 *  from the LLM visual pass. */
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

/** Lightweight Haiku pass that describes the user's photo in artifact
 *  vocabulary WITHOUT the full database in context — mirrors
 *  callDescribePhoto for specimens. The returned text is embedded and
 *  matched against the pgvector artifact index to narrow candidates.
 *  Returns null on any failure — the caller falls back to the text-first
 *  flow (full DB in system prompt). */
async function callDescribeArtifactPhoto(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
): Promise<string | null> {
  const describePrompt = `Observe this prehistoric artifact photograph carefully and describe what you see using the vocabulary an archaeologist would use.

Describe in 3-5 sentences:
- Overall shape (lanceolate, stemmed, corner-notched, bifacial, disc, tubular, oval, triangular, pick, etc.)
- Flaking pattern (collateral, parallel, oblique, random, pressure-flaked, bifacial, unifacial)
- Base style (concave, convex, straight, notched, bifurcated, ground, grooved, shouldered)
- Material hints (chert, flint, obsidian, slate, shell, ceramic, stone, bone, wood)
- Surface treatment (incised, cord-marked, polished, serrated, cortex, retouched)
- Size and proportions if discernible
- Any hafting features (notches, stem, tang, groove)
- Cultural or temporal markers if visible

Return ONLY the description prose — no JSON, no markdown, no preamble.`;

  const messages = [
    {
      role: "user",
      content: [
        { type: "image_url", image_url: { url: `data:${mimeType};base64,${imageBase64}` } },
        { type: "text", text: describePrompt },
      ],
    },
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

/** Build the artifact system prompt — the full ARTIFACT_DB as a compact
 *  reference list, grouped by family. */
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

/** Haiku vision call for artifact identification. */
async function callArtifactVisionModel(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
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
        { type: "image_url", image_url: { url: `data:${mimeType};base64,${imageBase64}` } },
        { type: "text", text: userPrompt },
      ],
    },
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

/** Visual reference comparison for artifacts — sends the user's photo
 *  alongside the top artifact reference images. */
async function callArtifactVisualComparison(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  preliminaryMatches: MatchResult[],
  useSonnet: boolean,
  maxCandidates: number = 12,
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

  const userContent: Array<Record<string, unknown>> = [
    { type: "image_url", image_url: { url: `data:${mimeType};base64,${imageBase64}` } },
    { type: "text", text: "This is the user's photo. Below are reference images for the top artifact candidates. Compare the user's photo against each reference image visually." },
  ];

  for (let i = 0; i < refs.length; i++) {
    userContent.push({ type: "image_url", image_url: { url: refs[i].imageUrl } });
    userContent.push({ type: "text", text: `Reference ${i + 1}: ${refs[i].name} (id: ${refs[i].id})` });
  }

  const refList = refs.map((r, i) => `${i + 1}. ${r.name} (id: ${r.id})`).join("\n");

  const prompt = `You are comparing a user's artifact photo against database reference images.

The first image is the user's unknown artifact. The following images are reference photos for these candidates:
${refList}

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
        model: useSonnet ? "anthropic/claude-sonnet-4" : "anthropic/claude-haiku-4.5",
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

/** Sonnet re-rank for ambiguous artifact matches. */
async function callSonnetArtifactRerank(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  preliminaryMatches: MatchResult[],
  summary: string,
): Promise<IdentificationResult | null> {
  const systemPrompt = buildArtifactSystemPrompt();
  const matchNames = preliminaryMatches.slice(0, 5).map((m) => `${m.name} (${m.confidence}%)`).join(", ");
  const userPrompt = `Re-evaluate this artifact photo independently. A first-pass model returned these candidates: ${matchNames}

Initial summary: ${summary}

Look at the photo again with fresh eyes and return your own ranked matches. You may agree, reorder, or introduce new candidates from the artifact database — but every id must exist in the reference database.

Return ONLY valid JSON — no markdown:
{
  "matches": [
    { "id": "artifact-id", "name": "Name", "confidence": 90, "reasoning": "Reasoning." }
  ],
  "summary": "Updated analysis."
}`;

  const messages = [
    { role: "system", content: [{ type: "text", text: systemPrompt, cache_control: { type: "ephemeral" } }] },
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
      headers: {
        Authorization: `Bearer ${secret}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "anthropic/claude-sonnet-4",
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

/** Gemini third-opinion for the artifact pipeline — mirrors
 *  callGeminiThirdOpinion for specimens. Casts a tie-breaking vote when the
 *  Haiku and Sonnet passes disagree on the top pick or both land < 85%. */
async function callGeminiArtifactThirdOpinion(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  haikuMatches: MatchResult[],
  sonnetMatches: MatchResult[],
  summary: string,
): Promise<IdentificationResult | null> {
  const allCandidates = [...haikuMatches, ...sonnetMatches];
  const systemPrompt = buildArtifactCandidateSystemPrompt(allCandidates);
  const haikuList = haikuMatches.slice(0, 5).map(m => `- ${m.name} (${m.confidence}%)`).join("\n");
  const sonnetList = sonnetMatches.slice(0, 5).map(m => `- ${m.name} (${m.confidence}%)`).join("\n");
  const userPrompt = `Two prior models disagreed on this artifact photo.

Model A (first pass) candidates:
${haikuList}

Model B (re-rank) candidates:
${sonnetList}

Summary: ${summary}

You are the tie-breaker. Look at the photo yourself and return your own ranked matches. Every id must exist in the reference database. Return the same JSON shape.`;

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

/** Candidate-only system prompt for the artifact pipeline — mirrors
 *  buildCandidateSystemPrompt for specimens. Includes ONLY the narrowed
 *  candidate set's metadata so the rerank / tie-breaker models can reason
 *  about the candidates without the full 106-artifact DB in context. */
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

/** Generate clarification questions tailored to ambiguous artifact matches.
 *  Mirrors generateClarificationQuestions for specimens but uses artifact
 *  vocabulary (shape, flaking, base style, material, hafting). */
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

/** Fallback clarification questions for artifacts when the AI generation
 *  fails. Mirrors getDefaultQuestions for specimens but uses artifact
 *  vocabulary. */
function getDefaultArtifactQuestions(matches: MatchResult[]): ClarificationQuestion[] {
  const arts = matches.slice(0, 4)
    .map(m => ARTIFACT_MAP[m.id])
    .filter(Boolean) as ArtifactEntry[];

  const questions: ClarificationQuestion[] = [];

  // Shape question — based on subFamily hints
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

  // Material question
  questions.push({
    id: "material",
    question: "What material is it made from?",
    options: ["Gray/tan chert or flint", "Black obsidian", "Quartz or quartzite", "Slate or other stone", "Shell or bone"],
  });

  // Size question
  questions.push({
    id: "size",
    question: "How large is the object?",
    options: ["Under 1 inch", "1-2 inches", "2-4 inches", "Over 4 inches", "Not sure"],
  });

  // Context question
  questions.push({
    id: "context",
    question: "Where did you find it?",
    options: ["Field or plowed ground", "Creek or riverbed", "Construction site", "Desert or open land", "Bought / unknown"],
  });

  return questions.slice(0, 4);
}

/** Parse the artifact identification response — validates IDs against
 *  ARTIFACT_DB instead of SPECIMEN_DB. Without this, all artifact IDs
 *  ("art-*") would be silently filtered out by parseIdentificationResponse. */
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

/**
 * Lightweight artifact-detection pre-pass (Haiku-only, fast, cheap).
 * Returns { isArtifact, confidence } — no credit consumed on the client.
 * Used by the IdentifyScreen confirmation gate before the full identify runs.
 */
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

    // Single Haiku call — cheap and fast (~5-10s). No DB needed in context.
    const detectPrompt = `You are an expert archaeologist. Look at this photo and determine: is this a prehistoric artifact (knapped stone tool, arrowhead, spear point, hand axe, scraper, drill, bead, effigy, pipe, game disc, pottery sherd, or other human-made object of stone, shell, wood, or ceramic)?

Respond with ONLY a JSON object:
{"isArtifact": true/false, "confidence": 0-100}

- isArtifact: true if the object appears to be a human-made artifact
- confidence: your confidence level (0-100)

If it's clearly a natural rock, mineral, crystal, or fossil, return isArtifact: false with high confidence.
If it's clearly an artifact, return isArtifact: true with high confidence.
If you're unsure, return your best guess with appropriate confidence.`;

    const result = await callVisionModel(
      toolkitUrl,
      toolkitSecret,
      imageData,
      mimeType,
      detectPrompt,
      "claude-haiku-4-5",
    );

    // Parse the JSON response from the model
    let isArtifact = false;
    let confidence = 0;
    try {
      const cleaned = result.trim()
        .replace(/^```json\s*/i, "")
        .replace(/```$/, "")
        .trim();
      const parsed = JSON.parse(cleaned);
      isArtifact = !!parsed.isArtifact;
      confidence = Math.max(0, Math.min(100, Number(parsed.confidence) || 0));
    } catch {
      // If parsing fails, fail open — don't block the user
      isArtifact = false;
      confidence = 0;
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

export async function handleClarify(
  request: Request,
  env: Env,
  cors: Record<string, string>,
): Promise<Response> {
  try {
    const body = await request.json() as {
      imageBase64: string;
      mimeType?: string;
      answers: Record<string, string>;
      preliminaryMatches: MatchResult[];
      summary: string;
    };

    if (!body.imageBase64 || !body.answers || !body.preliminaryMatches) {
      return Response.json(
        { error: "imageBase64, answers, and preliminaryMatches are required" },
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

    const result = await callClarifyModel(
      toolkitUrl,
      toolkitSecret,
      imageData,
      mimeType,
      body.answers,
      body.preliminaryMatches,
      body.summary,
    );

    const responseHeaders = { ...cors, "Content-Type": "application/json" };

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

    // Cross-reference the refined matches with web search
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

function stripDataUriPrefix(b64: string): string {
  if (!b64.startsWith("data:")) return b64;
  const comma = b64.indexOf(",");
  return comma === -1 ? b64 : b64.slice(comma + 1);
}

// --- Embedding-first pipeline helpers ---

/** Convert pgvector match RPC results into MatchResult objects for the
 *  visual comparison step. Confidence is mapped from cosine similarity
 *  (0–1) to the 0–100 scale the rest of the pipeline uses. Reasoning is
 *  a placeholder — the real reasoning comes from the LLM visual pass. */
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

/** Lightweight Haiku pass that describes the user's photo in specimen
 *  vocabulary WITHOUT the full database in context. The returned text is
 *  embedded and matched against the pgvector index to narrow candidates.
 *  Returns null on any failure — the caller falls back to the text-first
 *  flow. */
async function callDescribePhoto(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
): Promise<string | null> {
  const describePrompt = `Observe this rock, mineral, crystal, gem, or fossil photograph carefully and describe what you see using the vocabulary a field geologist would use.

Describe in 3-5 sentences:
- Primary color(s), color zoning, banding patterns
- Crystal habit or form (cubes, hexagonal prisms, blades, needles, botryoidal, massive, microcrystalline, etc.)
- Luster (metallic, vitreous, waxy, pearly, dull, adamantine, silky)
- Texture (crystalline, grainy, smooth, fibrous, bladed, banded)
- Transparency (transparent, translucent, opaque)
- Any visible matrix or associated minerals
- Any special optical effects (chatoyancy, asterism, play of color, adularescence, iridescence)
- For fossils: shape, symmetry, surface texture, skeletal features

Return ONLY the description prose — no JSON, no markdown, no preamble.`;

  const messages = [
    {
      role: "user",
      content: [
        { type: "image_url", image_url: { url: `data:${mimeType};base64,${imageBase64}` } },
        { type: "text", text: describePrompt },
      ],
    },
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
    console.error("callDescribePhoto error:", String(err));
    return null;
  }
}

async function callVisionModel(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
): Promise<Response> {
  const systemPrompt = buildSystemPrompt();
  const userPrompt = buildUserPrompt();

  // Prompt caching: mark the system prompt (specimen database, ~58K tokens) as
  // cacheable. Within the 5-minute cache window, repeat calls reuse the cached
  // tokens at ~88% discount. The user's photo and question are never cached.
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

async function callClarifyModel(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  answers: Record<string, string>,
  preliminaryMatches: MatchResult[],
  summary: string,
): Promise<Response> {
  const systemPrompt = buildSystemPrompt();
  const userPrompt = buildClarifyUserPrompt(answers, preliminaryMatches, summary);

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

// --- Visual reference comparison (Phase 9) ---

/** Visual reference comparison: sends the user's photo alongside the database
 *  reference images for the top 5 candidates, asking the vision model to
 *  directly compare visual similarity rather than working from text alone.
 *  Reference images are passed as public R2 URL strings (not base64), so they
 *  don't count against the 4.5 MB Vercel body limit.
 *
 *  Model tiering: Haiku for free, Sonnet for premium/pro. */
async function callVisualReferenceComparison(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  preliminaryMatches: MatchResult[],
  useSonnet: boolean,
  maxCandidates: number = 5,
): Promise<IdentificationResult | null> {
  if (preliminaryMatches.length === 0) return null;

  // Look up reference image URLs for the top candidates (up to maxCandidates).
  // In the embedding-first flow this is 25 (free) / 20 (premium); in the
  // fallback text-first flow it's 5.
  const topCandidates = preliminaryMatches.slice(0, maxCandidates);
  const refs: Array<{ id: string; name: string; imageUrl: string }> = [];
  for (const m of topCandidates) {
    const spec = SPECIMEN_DB.find(s => s.id === m.id);
    if (spec?.imageUrl) {
      refs.push({ id: spec.id, name: spec.name, imageUrl: spec.imageUrl });
    }
  }
  if (refs.length === 0) return null;

  // Build the user content: user's photo first, then each reference image with a label
  const userContent: Array<Record<string, unknown>> = [
    {
      type: "image_url",
      image_url: { url: `data:${mimeType};base64,${imageBase64}` },
    },
    {
      type: "text",
      text: "This is the user's photo that needs identification. Below are database reference images for the top candidates. Compare the user's photo against each reference image visually.",
    },
  ];

  for (const ref of refs) {
    userContent.push({
      type: "image_url",
      image_url: { url: ref.imageUrl },
    });
    userContent.push({
      type: "text",
      text: `Reference image ${refs.indexOf(ref) + 1}: ${ref.name} (id: ${ref.id})`,
    });
  }

  const refList = refs.map((r, i) => `${i + 1}. ${r.name} (id: ${r.id})`).join("\n");

  const prompt = `You are comparing a user's specimen photo against database reference images.

The first image is the user's unknown specimen. The following images are reference photos for these candidates:
${refList}

Visually compare the user's photo against each reference image. Focus on:
- Color match (hue, saturation, zoning, banding)
- Crystal habit / form match (cubes, prisms, botryoidal, massive, etc.)
- Luster and surface texture match
- Overall visual similarity

Rank all candidates by how well the user's photo visually matches the reference image. You may reorder from the initial ranking if visual comparison suggests a different top match.

Return ONLY valid JSON — no markdown, no extra text:
{
  "matches": [
    {
      "id": "specimen-id from the list above",
      "name": "Specimen Name",
      "confidence": 90,
      "reasoning": "2-3 sentences explaining the visual similarity or differences between the user's photo and the reference image."
    }
  ],
  "summary": "3-4 sentence visual comparison summary noting which features matched and which differed."
}`;

  userContent.push({ type: "text", text: prompt });

  const model = useSonnet ? "anthropic/claude-sonnet-4.5" : "anthropic/claude-haiku-4.5";

  try {
    const response = await fetch(`${toolkitUrl}/v2/vercel/v1/chat/completions`, {
      method: "POST",
      headers: { Authorization: `Bearer ${secret}`, "Content-Type": "application/json" },
      body: JSON.stringify({
        model,
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

// --- Tiered accuracy ladder helpers (Phase 8) ---

/** Premium / high-donation tier: Sonnet independently re-scores the same image
 *  and candidate set. Used on ambiguous calls (top confidence < 85%). */
async function callSonnetRerank(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  preliminaryMatches: MatchResult[],
  summary: string,
): Promise<IdentificationResult | null> {
  // Embedding-first flow: use the trimmed candidate-only system prompt
  // instead of the full 58K-token database.
  const systemPrompt = buildCandidateSystemPrompt(preliminaryMatches);
  const matchList = preliminaryMatches.slice(0, 5)
    .map(m => `- ${m.name} (${m.confidence}%): ${m.reasoning}`)
    .join("\n");
  const userPrompt = `Re-evaluate this specimen photo independently. A first-pass model returned these candidates:

${matchList}

Summary: ${summary}

Look at the photo again with fresh eyes and return your own ranked matches. You may agree, reorder, or introduce new candidates from the database — but every id must exist in the reference database. Return the same JSON shape as the first pass.`;

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

/** Pro tier: Gemini casts a third vote only when Haiku AND Sonnet both land
 *  < 85% or disagree on the top pick. Resolves the final ranking by majority. */
async function callGeminiThirdOpinion(
  toolkitUrl: string,
  secret: string,
  imageBase64: string,
  mimeType: string,
  haikuMatches: MatchResult[],
  sonnetMatches: MatchResult[],
  summary: string,
): Promise<IdentificationResult | null> {
  // Embedding-first flow: use the trimmed candidate-only system prompt.
  // Combine both match sets for the candidate list.
  const allCandidates = [...haikuMatches, ...sonnetMatches];
  const systemPrompt = buildCandidateSystemPrompt(allCandidates);
  const haikuList = haikuMatches.slice(0, 5).map(m => `- ${m.name} (${m.confidence}%)`).join("\n");
  const sonnetList = sonnetMatches.slice(0, 5).map(m => `- ${m.name} (${m.confidence}%)`).join("\n");
  const userPrompt = `Two prior models disagreed on this specimen photo.

Model A (first pass) candidates:
${haikuList}

Model B (re-rank) candidates:
${sonnetList}

Summary: ${summary}

You are the tie-breaker. Look at the photo yourself and return your own ranked matches. Every id must exist in the reference database. Return the same JSON shape.`;

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

/** Gemini assemblage analysis — detects and analyzes multi-mineral assemblages.
 *  Called when the initial identification suggests the specimen contains multiple
 *  distinct minerals (e.g. garnets in granite, epidote in basalt, druzy quartz on petrified wood). */
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

Examples of assemblages:
- Granite with garnet crystals
- Basalt with epidote veins
- Petrified wood with druzy quartz
- Amazonite with smoky quartz
- Quartz with chalcopyrite

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

/** Sonnet assemblage analysis — primary model for detecting and analyzing
 *  multi-mineral assemblages. Uses claude-sonnet-4.5 with the same prompt as
 *  the Gemini pass. If the top component confidence is below 88%, the caller
 *  runs [callGeminiAssemblageAnalysis] as a second pass. */
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

Examples of assemblages:
- Granite with garnet crystals
- Basalt with epidote veins
- Petrified wood with druzy quartz
- Amazonite with smoky quartz
- Quartz with chalcopyrite

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

/** Parse the assemblage analysis response from Gemini or Sonnet. */
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

/** Merge two ranked match lists into a consensus ranking. Picks are weighted by
 *  confidence; a specimen that both models agree on rises to the top. */
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

/** Merge three ranked match lists (Haiku, Sonnet, Gemini) by majority vote. */
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

The top match confidence is below 85%, meaning there's ambiguity. Generate 3-4 short questions that will help disambiguate between these candidates. Each question should have 3-5 multiple-choice options.

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

    // Validate each question
    return parsed.questions
      .filter(q => q.id && q.question && Array.isArray(q.options) && q.options.length >= 2)
      .slice(0, 4);
  } catch (err) {
    console.error("Clarification question generation error:", String(err));
    return getDefaultQuestions(matches);
  }
}

function getDefaultQuestions(matches: MatchResult[]): ClarificationQuestion[] {
  // Build questions based on the actual candidates' properties
  const specs = matches.slice(0, 4).map(m => SPECIMEN_DB.find(s => s.id === m.id)).filter(Boolean) as SpecimenEntry[];

  const questions: ClarificationQuestion[] = [];

  // Color question
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

  // Luster question
  const lusters = new Set(specs.map(s => s.luster).filter(Boolean));
  if (lusters.size > 1) {
    questions.push({
      id: "luster",
      question: "How would you describe the surface shine?",
      options: Array.from(lusters).slice(0, 5),
    });
  }

  // Hardness question
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

  // Context question
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

async function searchWebReferences(
  toolkitUrl: string,
  secret: string,
  matches: MatchResult[],
): Promise<WebReference[]> {
  if (matches.length === 0) return [];

  const topMatch = matches[0];
  const secondMatch = matches[1];

  const query = secondMatch
    ? `${topMatch.name} vs ${secondMatch.name} identification mineralogy distinguishing features`
    : `${topMatch.name} mineral identification properties`;

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

function extractDomain(url: string): string {
  try {
    const u = new URL(url);
    return u.hostname.replace("www.", "");
  } catch {
    return "";
  }
}

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
  // Group specimens by category for more organized reference
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

  // Build compact specimen lists with all key visual properties
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

/** Candidate-only system prompt for the embedding-first flow. Instead of
 *  dumping the full 794-specimen database (58K tokens) into context, this
 *  includes ONLY the narrowed candidate set's metadata — enough for the
 *  rerank / tie-breaker models to reason about the candidates without the
 *  massive prompt. The diagnostic-features guide and confidence calibration
 *  are retained; only the specimen list is trimmed. */
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

// --- Types ---

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
  /** Which AI models contributed to this result (Phase 8 accuracy ladder). */
  modelsUsed?: string[];
  /** Whether the visual reference comparison step was used. */
  visualReferenceUsed?: boolean;
  /** Assemblage analysis result — present when the specimen is a multi-mineral assemblage. */
  assemblage?: AssemblageResult;
  /** Artifact-only: true when the full pipeline (database + Haiku + Sonnet +
   *  Gemini + web search) still can't produce a reasonably confident match
   *  (top < 55%). The app shows a notification that the object could not be
   *  fully distinguished between an actual artifact and a similar-shaped rock. */
  uncertainArtifact?: boolean;
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

// --- Parsing ---

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
