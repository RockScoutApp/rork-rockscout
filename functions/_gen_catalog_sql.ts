// Temporary script — generates a bulk INSERT SQL file for specimen_catalog
// from SPECIMEN_DB, to run in the Supabase SQL Editor (bypasses PostgREST).
import { SPECIMEN_DB } from "./specimens";

function esc(s: string): string {
  return "'" + s.replace(/'/g, "''") + "'";
}

const lines: string[] = [];
lines.push("-- RockScout specimen_catalog bulk upsert (794 specimens)");
lines.push("-- Generated from functions/specimens.ts. Idempotent on id (PK).");
lines.push("-- Run in the Supabase SQL Editor — bypasses PostgREST entirely.");
lines.push("");
lines.push("insert into public.specimen_catalog");
lines.push("  (id, name, category, tagline, colors, hardness, luster, crystal_system, streak, rarity, image_url)");
lines.push("values");

const rows = SPECIMEN_DB.map((s) => {
  return `  (${esc(s.id)}, ${esc(s.name)}, ${esc(s.category)}, ${esc(s.tagline)}, ${esc(s.colors)}, ${esc(s.hardness)}, ${esc(s.luster)}, ${esc(s.crystal)}, ${esc(s.streak)}, ${esc(s.rarity)}, ${esc(s.imageUrl)})`;
});
lines.push(rows.join(",\n") + ";");
lines.push("");
lines.push("-- Verify count");
lines.push("select count(*) as total from public.specimen_catalog;");

const out = lines.join("\n");
await Bun.write("/tmp/specimen_catalog_upsert.sql", out);
console.log(`Wrote /tmp/specimen_catalog_upsert.sql — ${SPECIMEN_DB.length} rows, ${out.length} chars`);
