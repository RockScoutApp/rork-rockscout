/**
 * Top Pick Manager — randomizes which gear items get the "Top Pick" badge
 * once every 24 hours for each user. The selection is persisted in
 * localStorage so the same set stays stable throughout the day and only
 * re-rolls at midnight (or on the first access of a new calendar day).
 *
 * Roughly 20% of the catalog (min 8, max 15) is marked as a top pick
 * each cycle, drawn from all available item IDs.
 */

import { gearItems } from "@/data/gear";

const STORAGE_KEY = "rockscout_top_picks";
const MIN_PICKS = 8;
const MAX_PICKS = 15;
const PICK_RATIO = 0.2;

function todayString(): string {
  return new Date().toISOString().slice(0, 10);
}

interface PersistedTopPicks {
  date: string;
  ids: string[];
}

function loadPersisted(): PersistedTopPicks | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as PersistedTopPicks;
    if (!parsed.date || !Array.isArray(parsed.ids)) return null;
    return parsed;
  } catch {
    return null;
  }
}

function savePersisted(data: PersistedTopPicks): void {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  } catch {
    // localStorage might be full or unavailable — silently ignore
  }
}

function roll(date: string): Set<string> {
  const allIds = gearItems.map((i) => i.id);
  const count = Math.min(
    MAX_PICKS,
    Math.max(MIN_PICKS, Math.round(allIds.length * PICK_RATIO)),
  );
  // Fisher-Yates shuffle
  const shuffled = [...allIds];
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
  }
  const picked = new Set(shuffled.slice(0, count));
  savePersisted({ date, ids: [...picked] });
  return picked;
}

let cachedPicks: Set<string> | null = null;
let cachedDate: string | null = null;

/**
 * Returns the set of top-pick item IDs for today.
 * Re-rolls automatically when the calendar date changes.
 * Results are cached in memory for the lifetime of the page.
 */
export function getTopPickIds(): Set<string> {
  const today = todayString();
  if (cachedPicks && cachedDate === today) {
    return cachedPicks;
  }
  const persisted = loadPersisted();
  if (persisted && persisted.date === today) {
    cachedPicks = new Set(persisted.ids);
    cachedDate = today;
    return cachedPicks;
  }
  cachedPicks = roll(today);
  cachedDate = today;
  return cachedPicks;
}

/** Check if a single item is a top pick today. */
export function isTopPick(itemId: string): boolean {
  return getTopPickIds().has(itemId);
}

/** Returns the set of top-pick item *names* for today.
 *  Useful for surfaces that identify gear by name rather than ID. */
export function getTopPickNames(): Set<string> {
  const ids = getTopPickIds();
  return new Set(
    gearItems.filter((i) => ids.has(i.id)).map((i) => i.name),
  );
}

/** Force a re-roll now (useful for testing / dev console). */
export function forceRerollTopPicks(): void {
  cachedPicks = null;
  cachedDate = null;
  roll(todayString());
}

/** Check if a gear item is a top pick by its display name. */
export function isTopPickByName(name: string): boolean {
  return getTopPickNames().has(name);
}
