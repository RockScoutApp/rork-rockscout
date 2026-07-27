// Embedding helpers for the RockScout visual-embedding-first identification pipeline.
//
// Model: openai/text-embedding-3-small (1536 dimensions, text-only).
//
// IMPORTANT: the Vercel AI Gateway /v1/embeddings endpoint accepts TEXT input
// only. Both multimodal candidates in the catalog (google/gemini-embedding-2,
// cohere/embed-v4.0) advertise multimodal in their descriptions but their
// gateway endpoint metadata exposes input_modalities: ["text"]. So we embed a
// rich text description of each specimen and do text-to-text cosine similarity
// at query time. The LLM vision pass over the narrowed candidate set still runs
// exactly as specified in the plan.

/** The embedding model of record. 1536 dimensions. */
export const TEXT_EMBEDDING_MODEL = "openai/text-embedding-3-small" as const;

/** Vector dimension for TEXT_EMBEDDING_MODEL. Must match the migration. */
export const TEXT_EMBEDDING_DIM = 1536;

/** Shape of a row in the specimen_embeddings table. */
export interface SpecimenEmbeddingRow {
  specimen_id: string;
  image_url: string;
  text_embedding: number[];
  embedding_model: string;
}

/** Result of querying the match RPC. */
export interface EmbeddingMatch {
  specimen_id: string;
  max_similarity: number;
}

/**
 * Build a rich text description of a specimen for embedding.
 * Combines the most visually-discriminative fields so the text embedding
 * captures what makes the specimen recognizable.
 */
export function buildSpecimenText(s: {
  name: string;
  category: string;
  tagline: string;
  colors: string;
  hardness: string;
  luster: string;
  crystal: string;
  streak: string;
  rarity: string;
}): string {
  return [
    s.name,
    s.category,
    s.tagline,
    `Colors: ${s.colors}`,
    `Hardness: ${s.hardness}`,
    `Luster: ${s.luster}`,
    `Crystal system: ${s.crystal}`,
    `Streak: ${s.streak}`,
    `Rarity: ${s.rarity}`,
  ]
    .filter((part) => part && part.trim().length > 0)
    .join(" | ");
}

/**
 * Build a rich text description of an artifact for embedding.
 * Combines the most visually-discriminative fields (family, subFamily,
 * tagline, tribe, time period) so the text embedding captures what makes
 * the artifact recognizable — mirroring buildSpecimenText for rocks.
 */
export function buildArtifactText(a: {
  name: string;
  family: string;
  subFamily: string;
  tagline: string;
  description: string;
  tribe: string;
  timePeriod: string;
}): string {
  return [
    a.name,
    a.family,
    a.subFamily,
    a.tagline,
    a.description,
    `Tribe/Culture: ${a.tribe}`,
    `Time period: ${a.timePeriod}`,
  ]
    .filter((part) => part && part.trim().length > 0)
    .join(" | ");
}

/** Result of querying the artifact match RPC. */
export interface ArtifactEmbeddingMatch {
  artifact_id: string;
  max_similarity: number;
}

/** Shape of a row in the artifact_embeddings table. */
export interface ArtifactEmbeddingRow {
  artifact_id: string;
  image_url: string;
  text_embedding: number[];
  embedding_model: string;
}

/**
 * Embed a single text string via the Vercel AI Gateway proxy.
 * Returns the embedding vector (length = TEXT_EMBEDDING_DIM).
 */
export async function embedText(
  toolkitUrl: string,
  secret: string,
  text: string,
  model: string = TEXT_EMBEDDING_MODEL,
): Promise<number[]> {
  const proxyUrl = `${toolkitUrl}/v2/vercel/v1/embeddings`;
  const body = { model, input: text };

  const response = await fetch(proxyUrl, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${secret}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    const errBody = await response.text().catch(() => "unknown error");
    throw new Error(
      `embedText failed (${response.status}): ${errBody.slice(0, 500)}`,
    );
  }

  const data = (await response.json()) as {
    data?: Array<{ embedding?: number[] }>;
  };

  const embedding = data.data?.[0]?.embedding;
  if (!embedding || !Array.isArray(embedding)) {
    throw new Error("embedText: no embedding in response");
  }

  if (embedding.length !== TEXT_EMBEDDING_DIM) {
    throw new Error(
      `embedText: expected ${TEXT_EMBEDDING_DIM} dims, got ${embedding.length}`,
    );
  }

  return embedding;
}

/**
 * Embed many text strings in a single batched call (the gateway accepts an
 * array of inputs). Returns an array of vectors in input order.
 */
export async function embedTextBatch(
  toolkitUrl: string,
  secret: string,
  texts: string[],
  model: string = TEXT_EMBEDDING_MODEL,
): Promise<number[][]> {
  if (texts.length === 0) return [];

  const proxyUrl = `${toolkitUrl}/v2/vercel/v1/embeddings`;
  const body = { model, input: texts };

  const response = await fetch(proxyUrl, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${secret}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify(body),
  });

  if (!response.ok) {
    const errBody = await response.text().catch(() => "unknown error");
    throw new Error(
      `embedTextBatch failed (${response.status}): ${errBody.slice(0, 500)}`,
    );
  }

  const data = (await response.json()) as {
    data?: Array<{ embedding?: number[] }>;
  };

  const rows = data.data ?? [];
  const out: number[][] = [];
  for (let i = 0; i < rows.length; i++) {
    const emb = rows[i]?.embedding;
    if (!emb || !Array.isArray(emb)) {
      throw new Error(`embedTextBatch: missing embedding at index ${i}`);
    }
    if (emb.length !== TEXT_EMBEDDING_DIM) {
      throw new Error(
        `embedTextBatch: expected ${TEXT_EMBEDDING_DIM} dims at index ${i}, got ${emb.length}`,
      );
    }
    out.push(emb);
  }
  return out;
}

/**
 * Upsert a specimen embedding row into the specimen_embeddings table via
 * the Supabase PostgREST API. Idempotent on (specimen_id, image_url,
 * embedding_model) thanks to the table's unique constraint.
 *
 * Uses the anon key — RLS is disabled on specimen_embeddings (it is reference
 * data, not user-specific) so anon can read and write.
 */
export async function upsertEmbedding(
  supabaseUrl: string,
  supabaseAnonKey: string,
  row: SpecimenEmbeddingRow,
): Promise<void> {
  const endpoint = `${supabaseUrl}/rest/v1/specimen_embeddings`;

  const response = await fetch(endpoint, {
    method: "POST",
    headers: {
      apikey: supabaseAnonKey,
      Authorization: `Bearer ${supabaseAnonKey}`,
      "Content-Type": "application/json",
      // Upsert on the unique (specimen_id, image_url, embedding_model).
      Prefer: "resolution=merge-duplicates",
    },
    body: JSON.stringify({
      specimen_id: row.specimen_id,
      image_url: row.image_url,
      text_embedding: row.text_embedding,
      embedding_model: row.embedding_model,
    }),
  });

  if (!response.ok) {
    const errBody = await response.text().catch(() => "unknown error");
    throw new Error(
      `upsertEmbedding failed (${response.status}): ${errBody.slice(0, 500)}`,
    );
  }
}

/**
 * Query the match_specimen_embeddings RPC for the top-N most similar
 * specimens to the given query embedding.
 */
export async function matchSpecimenEmbeddings(
  supabaseUrl: string,
  supabaseAnonKey: string,
  queryEmbedding: number[],
  matchCount: number = 25,
): Promise<EmbeddingMatch[]> {
  const endpoint = `${supabaseUrl}/rest/v1/rpc/match_specimen_embeddings`;

  const response = await fetch(endpoint, {
    method: "POST",
    headers: {
      apikey: supabaseAnonKey,
      Authorization: `Bearer ${supabaseAnonKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      query_embedding: queryEmbedding,
      match_count: matchCount,
    }),
  });

  if (!response.ok) {
    const errBody = await response.text().catch(() => "unknown error");
    throw new Error(
      `matchSpecimenEmbeddings failed (${response.status}): ${errBody.slice(0, 500)}`,
    );
  }

  const data = (await response.json()) as EmbeddingMatch[];
  if (!Array.isArray(data)) return [];
  return data;
}

/**
 * Upsert an artifact embedding row into the artifact_embeddings table via
 * the Supabase PostgREST API. Idempotent on (artifact_id, image_url,
 * embedding_model) thanks to the table's unique constraint.
 */
export async function upsertArtifactEmbedding(
  supabaseUrl: string,
  supabaseAnonKey: string,
  row: ArtifactEmbeddingRow,
): Promise<void> {
  const endpoint = `${supabaseUrl}/rest/v1/artifact_embeddings`;

  const response = await fetch(endpoint, {
    method: "POST",
    headers: {
      apikey: supabaseAnonKey,
      Authorization: `Bearer ${supabaseAnonKey}`,
      "Content-Type": "application/json",
      // Upsert on the unique (artifact_id, image_url, embedding_model).
      Prefer: "resolution=merge-duplicates",
    },
    body: JSON.stringify({
      artifact_id: row.artifact_id,
      image_url: row.image_url,
      text_embedding: row.text_embedding,
      embedding_model: row.embedding_model,
    }),
  });

  if (!response.ok) {
    const errBody = await response.text().catch(() => "unknown error");
    throw new Error(
      `upsertArtifactEmbedding failed (${response.status}): ${errBody.slice(0, 500)}`,
    );
  }
}

/**
 * Query the match_artifact_embeddings RPC for the top-N most similar
 * artifacts to the given query embedding.
 */
export async function matchArtifactEmbeddings(
  supabaseUrl: string,
  supabaseAnonKey: string,
  queryEmbedding: number[],
  matchCount: number = 25,
): Promise<ArtifactEmbeddingMatch[]> {
  const endpoint = `${supabaseUrl}/rest/v1/rpc/match_artifact_embeddings`;

  const response = await fetch(endpoint, {
    method: "POST",
    headers: {
      apikey: supabaseAnonKey,
      Authorization: `Bearer ${supabaseAnonKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      query_embedding: queryEmbedding,
      match_count: matchCount,
    }),
  });

  if (!response.ok) {
    const errBody = await response.text().catch(() => "unknown error");
    throw new Error(
      `matchArtifactEmbeddings failed (${response.status}): ${errBody.slice(0, 500)}`,
    );
  }

  const data = (await response.json()) as ArtifactEmbeddingMatch[];
  if (!Array.isArray(data)) return [];
  return data;
}
