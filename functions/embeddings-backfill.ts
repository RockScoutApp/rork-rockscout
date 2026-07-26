// Idempotent backfill route for the specimen embedding index.
//
// POST /embeddings-backfill
// Body (optional): { "limit"?: number, "batchSize"?: number }
// Auth: toolkit-secret guarded (admin-triggered only, same pattern as other
// protected routes).
//
// Iterates every specimen in SPECIMEN_DB, builds a rich text description for
// each, embeds it via the Vercel AI Gateway, and upserts into the Supabase
// specimen_embeddings table on (specimen_id, image_url, embedding_model).
// Re-runs are safe and cheap — existing rows are merged, not duplicated.
//
// IMPORTANT: the gateway /v1/embeddings endpoint accepts TEXT input only
// (vision-capable embedding models exist in the catalog but their gateway
// endpoint exposes input_modalities: ["text"]). So this route is text-only:
// one row per specimen, keyed on the specimen's primary imageUrl. The
// text_embedding captures the specimen's visually-discriminative vocabulary
// (colors, luster, crystal habit, category, tagline, hardness, streak).
// The LLM vision pass over the narrowed candidate set still runs at query
// time exactly as specified in the plan.

import { SPECIMEN_DB, type SpecimenEntry } from "./specimens";
import {
  TEXT_EMBEDDING_MODEL,
  buildSpecimenText,
  embedTextBatch,
  upsertEmbedding,
} from "./embeddings";

interface BackfillEnv {
  EXPO_PUBLIC_TOOLKIT_URL: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY: string;
  EXPO_PUBLIC_SUPABASE_URL: string;
  EXPO_PUBLIC_SUPABASE_ANON_KEY: string;
}

interface BackfillRequestBody {
  /** Max specimens to embed this run (default: all). */
  limit?: number;
  /** Embeddings per gateway batch call (default: 32). */
  batchSize?: number;
}

interface BackfillSummary {
  embedded: number;
  skipped: number;
  failed: number;
  total: number;
  textModelUsed: string;
  visionModelUsed: string | null;
  durationMs: number;
  errors: string[];
}

/**
 * Handle the /embeddings-backfill POST route.
 *
 * Cors headers are passed through from the index router. The route is
 * toolkit-secret guarded upstream via guardEndpoint.
 */
export async function handleEmbeddingsBackfill(
  request: Request,
  env: BackfillEnv,
  cors: Record<string, string>,
): Promise<Response> {
  const startedAt = Date.now();
  const responseHeaders = { ...cors, "Content-Type": "application/json" };

  // Parse optional body (may be empty for a POST).
  let body: BackfillRequestBody = {};
  try {
    const text = await request.text();
    if (text && text.trim().length > 0) {
      body = JSON.parse(text) as BackfillRequestBody;
    }
  } catch {
    return Response.json(
      { error: "Invalid JSON body" },
      { status: 400, headers: responseHeaders },
    );
  }

  const toolkitUrl = env.EXPO_PUBLIC_TOOLKIT_URL ?? "https://toolkit.rork.com";
  const toolkitSecret = env.EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY;
  const supabaseUrl = env.EXPO_PUBLIC_SUPABASE_URL;
  const supabaseAnonKey = env.EXPO_PUBLIC_SUPABASE_ANON_KEY;

  if (!toolkitSecret) {
    return Response.json(
      { error: "Toolkit secret not configured" },
      { status: 500, headers: responseHeaders },
    );
  }
  if (!supabaseUrl || !supabaseAnonKey) {
    return Response.json(
      { error: "Supabase URL / anon key not configured" },
      { status: 500, headers: responseHeaders },
    );
  }

  const limit = body.limit && body.limit > 0 ? body.limit : SPECIMEN_DB.length;
  const batchSize = body.batchSize && body.batchSize > 0 ? body.batchSize : 32;

  const specimens = SPECIMEN_DB.slice(0, Math.min(limit, SPECIMEN_DB.length));

  const summary: BackfillSummary = {
    embedded: 0,
    skipped: 0,
    failed: 0,
    total: specimens.length,
    textModelUsed: TEXT_EMBEDDING_MODEL,
    visionModelUsed: null, // text-only approach
    durationMs: 0,
    errors: [],
  };

  // Process in batches to stay within gateway request limits.
  for (let i = 0; i < specimens.length; i += batchSize) {
    const batch = specimens.slice(i, i + batchSize);
    const texts = batch.map((s) => buildSpecimenText(s));

    let embeddings: number[][];
    try {
      embeddings = await embedTextBatch(toolkitUrl, toolkitSecret, texts);
    } catch (err) {
      const msg = `Batch ${i / batchSize + 1} embed failed: ${String(err)}`;
      console.error(msg);
      summary.errors.push(msg);
      summary.failed += batch.length;
      continue;
    }

    // Upsert each embedding row. We key on the specimen's primary imageUrl
    // (text-only approach → one row per specimen).
    for (let j = 0; j < batch.length; j++) {
      const specimen = batch[j];
      const embedding = embeddings[j];
      if (!embedding) {
        summary.skipped += 1;
        continue;
      }

      try {
        await upsertEmbedding(supabaseUrl, supabaseAnonKey, {
          specimen_id: specimen.id,
          image_url: resolvePrimaryImage(specimen),
          text_embedding: embedding,
          embedding_model: TEXT_EMBEDDING_MODEL,
        });
        summary.embedded += 1;
      } catch (err) {
        const msg = `Upsert failed for ${specimen.id}: ${String(err)}`;
        console.error(msg);
        summary.errors.push(msg);
        summary.failed += 1;
      }
    }
  }

  summary.durationMs = Date.now() - startedAt;

  console.log(
    `embeddings-backfill complete: embedded=${summary.embedded} skipped=${summary.skipped} failed=${summary.failed} total=${summary.total} durationMs=${summary.durationMs}`,
  );

  return Response.json(summary, { headers: responseHeaders });
}

/**
 * Resolve the primary image URL for a specimen. Prefers the first entry of
 * `imageUrls` (the full Kotlin card image set) when present and non-empty,
 * otherwise falls back to the single `imageUrl` field.
 */
function resolvePrimaryImage(s: SpecimenEntry): string {
  if (s.imageUrls && s.imageUrls.length > 0) {
    return s.imageUrls[0];
  }
  return s.imageUrl;
}
