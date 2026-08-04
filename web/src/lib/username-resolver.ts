/**
 * Ensures display names are unique across the platform by querying
 * the Supabase `rockscout_profiles` table for existing names and
 * appending a number suffix if a collision is found.
 *
 * Mirrors the Android `UsernameResolver.kt` logic.
 */

import { supabase } from "@/lib/supabase";

/**
 * Query Supabase for all display names that case-insensitively match
 * `desiredName` (excluding the current user). Returns the set of
 * exact-match display names that already exist.
 */
async function findExistingNames(
  desiredName: string,
  excludeUserId?: string,
): Promise<Set<string>> {
  try {
    const { data, error } = await supabase
      .from("rockscout_profiles")
      .select("display_name")
      .ilike("display_name", desiredName);

    if (error || !data) return new Set();

    return new Set(
      data
        .map((r) => r.display_name as string)
        .filter((name) => name !== excludeUserId),
    );
  } catch {
    return new Set();
  }
}

/**
 * Given a desired name, return a unique version by appending a number
 * suffix if the name is already taken by another user.
 *
 * @param desiredName The name the user wants.
 * @param excludeUserId The current user's ID (so they can keep their own name).
 * @returns A unique display name.
 */
export async function ensureUniqueUsername(
  desiredName: string,
  excludeUserId?: string,
): Promise<string> {
  const trimmed = desiredName.trim();
  if (!trimmed) return "Rockhound";

  const existing = await findExistingNames(trimmed, excludeUserId);
  if (existing.size === 0) return trimmed;

  // Check if the exact name is taken (case-insensitive)
  const isExactTaken = [...existing].some((n) =>
    n.toLowerCase() === trimmed.toLowerCase(),
  );
  if (!isExactTaken) return trimmed;

  // Find the next available suffix number
  let suffix = 2;
  while (suffix <= 9999) {
    const candidate = `${trimmed}${suffix}`;
    const candidateTaken = [...existing].some(
      (n) => n.toLowerCase() === candidate.toLowerCase(),
    );
    if (!candidateTaken) {
      // Also do a fresh check for names like "RockScout2"
      const additional = await findExistingNames(candidate, excludeUserId);
      if (additional.size === 0) return candidate;
    }
    suffix++;
  }

  return `${trimmed}-${Date.now() % 10000}`;
}

/**
 * Check whether a display name is already taken by another user.
 */
export async function isUsernameTaken(
  name: string,
  excludeUserId?: string,
): Promise<boolean> {
  const trimmed = name.trim();
  if (!trimmed) return false;

  const existing = await findExistingNames(trimmed, excludeUserId);
  return [...existing].some((n) => n.toLowerCase() === trimmed.toLowerCase());
}
