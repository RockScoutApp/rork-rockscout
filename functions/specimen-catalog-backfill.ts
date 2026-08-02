// Idempotent backfill route for the specimen_catalog table.
//
// POST /specimen-catalog-backfill
// Body (optional): { "limit"?: number }
// Auth: toolkit-secret guarded (admin-triggered only).
//
// Iterates every specimen in SPECIMEN_DB and upserts into the Supabase
// specimen_catalog table. Idempotent on the primary key (specimen_id).
// Re-runs are safe — existing rows are merged, not duplicated.
//
// This populates the reference data the PWA specimen database, specimen
// detail pages, and the identify-to-detail link depend on.

import { SPECIMEN_DB } from "./specimens";
import { resolveSupabaseUrl } from "./auth";

interface CatalogBackfillEnv {
  EXPO_PUBLIC_SUPABASE_URL: string;
  EXPO_PUBLIC_SUPABASE_ANON_KEY: string;
}

interface CatalogBackfillRequestBody {
  /** Max specimens to upsert this run (default: all). */
  limit?: number;
}

interface CatalogBackfillSummary {
  upserted: number;
  failed: number;
  total: number;
  durationMs: number;
  errors: string[];
}

interface CatalogRow {
  id: string;
  name: string;
  category: string;
  tagline: string;
  colors: string;
  hardness: string;
  luster: string;
  crystal_system: string;
  streak: string;
  rarity: string;
  image_url: string;
}

export async function handleSpecimenCatalogBackfill(
  request: Request,
  env: CatalogBackfillEnv,
  cors: Record<string, string>,
): Promise<Response> {
  const startedAt = Date.now();
  const responseHeaders = { ...cors, "Content-Type": "application/json" };

  let body: CatalogBackfillRequestBody = {};
  try {
    const text = await request.text();
    if (text && text.trim().length > 0) {
      body = JSON.parse(text) as CatalogBackfillRequestBody;
    }
  } catch {
    return Response.json(
      { error: "Invalid JSON body" },
      { status: 400, headers: responseHeaders },
    );
  }

  const supabaseUrl = resolveSupabaseUrl(env.EXPO_PUBLIC_SUPABASE_URL, undefined);
  const supabaseAnonKey = env.EXPO_PUBLIC_SUPABASE_ANON_KEY;

  if (!supabaseUrl || !supabaseAnonKey) {
    return Response.json(
      { error: "Supabase URL / anon key not configured" },
      { status: 500, headers: responseHeaders },
    );
  }

  const limit = body.limit && body.limit > 0 ? body.limit : SPECIMEN_DB.length;
  const specimens = SPECIMEN_DB.slice(0, Math.min(limit, SPECIMEN_DB.length));

  const summary: CatalogBackfillSummary = {
    upserted: 0,
    failed: 0,
    total: specimens.length,
    durationMs: 0,
    errors: [],
  };

  // Upsert in batches of 50 via the PostgREST bulk insert with merge-duplicates.
  const BATCH = 50;
  const endpoint = `${supabaseUrl}/rest/v1/specimen_catalog`;

  for (let i = 0; i < specimens.length; i += BATCH) {
    const batch = specimens.slice(i, i + BATCH);
    const rows: CatalogRow[] = batch.map((s) => ({
      id: s.id,
      name: s.name,
      category: s.category,
      tagline: s.tagline,
      colors: s.colors,
      hardness: s.hardness,
      luster: s.luster,
      crystal_system: s.crystal,
      streak: s.streak,
      rarity: s.rarity,
      image_url: s.imageUrl,
    }));

    try {
      const response = await fetch(endpoint, {
        method: "POST",
        headers: {
          apikey: supabaseAnonKey,
          Authorization: `Bearer ${supabaseAnonKey}`,
          "Content-Type": "application/json",
          // Upsert on the primary key (id).
          Prefer: "resolution=merge-duplicates",
        },
        body: JSON.stringify(rows),
      });

      if (!response.ok) {
        const errBody = await response.text().catch(() => "unknown error");
        const msg = `Batch ${Math.floor(i / BATCH) + 1} failed (${response.status}): ${errBody.slice(0, 300)}`;
        console.error(msg);
        summary.errors.push(msg);
        summary.failed += batch.length;
        continue;
      }

      summary.upserted += batch.length;
    } catch (err) {
      const msg = `Batch ${Math.floor(i / BATCH) + 1} exception: ${String(err)}`;
      console.error(msg);
      summary.errors.push(msg);
      summary.failed += batch.length;
    }
  }

  summary.durationMs = Date.now() - startedAt;

  console.log(
    `specimen-catalog-backfill complete: upserted=${summary.upserted} failed=${summary.failed} total=${summary.total} durationMs=${summary.durationMs}`,
  );

  return Response.json(summary, { headers: responseHeaders });
}
