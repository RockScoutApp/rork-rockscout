// Idempotent backfill route for the artifact embedding index.
//
// POST /artifacts-backfill
// Body (optional): { "limit"?: number, "batchSize"?: number }
// Auth: toolkit-secret guarded (admin-triggered only, same pattern as the
// specimen embeddings backfill).
//
// Iterates every artifact in ARTIFACT_DB, builds a rich text description for
// each, embeds it via the Vercel AI Gateway, and upserts into the Supabase
// artifact_embeddings table on (artifact_id, image_url, embedding_model).
// Re-runs are safe and cheap — existing rows are merged, not duplicated.
//
// Mirrors embeddings-backfill.ts but for artifacts instead of specimens.

import { ARTIFACT_DB, type ArtifactEntry } from "./artifacts";
import {
  TEXT_EMBEDDING_MODEL,
  buildArtifactText,
  embedTextBatch,
  upsertArtifactEmbedding,
} from "./embeddings";

interface BackfillEnv {
  EXPO_PUBLIC_TOOLKIT_URL: string;
  EXPO_PUBLIC_RORK_TOOLKIT_SECRET_KEY: string;
  EXPO_PUBLIC_SUPABASE_URL: string;
  EXPO_PUBLIC_SUPABASE_ANON_KEY: string;
}

interface BackfillRequestBody {
  /** Max artifacts to embed this run (default: all). */
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
  durationMs: number;
  errors: string[];
}

/**
 * Handle the /artifacts-backfill POST route.
 *
 * Cors headers are passed through from the index router. The route is
 * toolkit-secret guarded upstream via guardEndpoint.
 */
export async function handleArtifactsBackfill(
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

  const limit = body.limit && body.limit > 0 ? body.limit : ARTIFACT_DB.length;
  const batchSize = body.batchSize && body.batchSize > 0 ? body.batchSize : 32;

  const artifacts = ARTIFACT_DB.slice(0, Math.min(limit, ARTIFACT_DB.length));

  const summary: BackfillSummary = {
    embedded: 0,
    skipped: 0,
    failed: 0,
    total: artifacts.length,
    textModelUsed: TEXT_EMBEDDING_MODEL,
    durationMs: 0,
    errors: [],
  };

  // Process in batches to stay within gateway request limits.
  for (let i = 0; i < artifacts.length; i += batchSize) {
    const batch = artifacts.slice(i, i + batchSize);
    const texts = batch.map((a) => buildArtifactText(a));

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

    // Upsert each embedding row. One row per artifact, keyed on imageUrl.
    for (let j = 0; j < batch.length; j++) {
      const artifact = batch[j];
      const embedding = embeddings[j];
      if (!embedding) {
        summary.skipped += 1;
        continue;
      }

      try {
        await upsertArtifactEmbedding(supabaseUrl, supabaseAnonKey, {
          artifact_id: artifact.id,
          image_url: artifact.imageUrl,
          text_embedding: embedding,
          embedding_model: TEXT_EMBEDDING_MODEL,
        });
        summary.embedded += 1;
      } catch (err) {
        const msg = `Upsert failed for ${artifact.id}: ${String(err)}`;
        console.error(msg);
        summary.errors.push(msg);
        summary.failed += 1;
      }
    }
  }

  summary.durationMs = Date.now() - startedAt;

  console.log(
    `artifacts-backfill complete: embedded=${summary.embedded} skipped=${summary.skipped} failed=${summary.failed} total=${summary.total} durationMs=${summary.durationMs}`,
  );

  return Response.json(summary, { headers: responseHeaders });
}
