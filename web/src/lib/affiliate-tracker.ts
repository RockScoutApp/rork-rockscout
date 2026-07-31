/**
 * Affiliate Click Tracker — localStorage-based click logging for Amazon
 * affiliate gear links. Records per-item and per-day click counts so the
 * dev tools dashboard can render graphs of click-through activity.
 */

export interface ItemClick {
  itemId: string;
  name: string;
  clicks: number;
}

export interface DailyClick {
  date: string; // YYYY-MM-DD
  clicks: number;
}

export interface AffiliateState {
  totalClicks: number;
  perItem: ItemClick[];
  perDay: DailyClick[];
}

const STORAGE_KEY = "rockscout_affiliate_clicks";
const MAX_DAYS = 30;

function todayString(): string {
  return new Date().toISOString().slice(0, 10);
}

function loadState(): AffiliateState {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return { totalClicks: 0, perItem: [], perDay: [] };
    const parsed = JSON.parse(raw) as AffiliateState;
    return {
      totalClicks: parsed.totalClicks ?? 0,
      perItem: parsed.perItem ?? [],
      perDay: parsed.perDay ?? [],
    };
  } catch {
    return { totalClicks: 0, perItem: [], perDay: [] };
  }
}

function saveState(state: AffiliateState): void {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  } catch {
    // localStorage might be full or unavailable — silently ignore
  }
}

/** Record a single affiliate link click. */
export function recordAffiliateClick(itemId: string, name: string): void {
  const state = loadState();
  const today = todayString();

  // Update per-item
  const itemIdx = state.perItem.findIndex((i) => i.itemId === itemId);
  if (itemIdx >= 0) {
    state.perItem[itemIdx] = {
      ...state.perItem[itemIdx],
      clicks: state.perItem[itemIdx].clicks + 1,
    };
  } else {
    state.perItem.push({ itemId, name, clicks: 1 });
  }
  state.perItem.sort((a, b) => b.clicks - a.clicks);

  // Update per-day
  const dayIdx = state.perDay.findIndex((d) => d.date === today);
  if (dayIdx >= 0) {
    state.perDay[dayIdx] = {
      ...state.perDay[dayIdx],
      clicks: state.perDay[dayIdx].clicks + 1,
    };
  } else {
    state.perDay.push({ date: today, clicks: 1 });
    while (state.perDay.length > MAX_DAYS) state.perDay.shift();
  }

  state.totalClicks += 1;
  saveState(state);
}

/** Get the current affiliate click state for dashboard rendering. */
export function getAffiliateState(): AffiliateState {
  return loadState();
}

/** Reset all affiliate click stats. */
export function resetAffiliateStats(): void {
  saveState({ totalClicks: 0, perItem: [], perDay: [] });
}

/** Estimated affiliate revenue (conservative: 4% commission, $50 avg order, 3% conversion). */
export function estimatedAffiliateRevenue(): string {
  const state = loadState();
  const est = state.totalClicks * 50 * 0.04 * 0.03;
  return `$${est.toFixed(2)}`;
}
