/**
 * Route access classification for the RockScout PWA.
 *
 * Every `/app/*` route is classified into one of four tiers:
 * - "open" — read-only content, available to all users (free + premium).
 * - "bookmarks" — read + write personal data (collection, wishlist, spots, profile).
 * - "premium" — locked for free users; shows a PremiumGate lock screen.
 * - "social" — fully hidden for free users; redirects to Home silently.
 *
 * Used by `PremiumGate` to decide whether to render, redirect, or pass through.
 */

export type RouteTier = "open" | "bookmarks" | "premium" | "social";

/**
 * Map of route paths (relative to `/app`) to their access tier.
 * Routes not listed default to "open".
 */
export const ROUTE_ACCESS: Record<string, RouteTier> = {
  // ─── Open (read-only, all users) ───
  "": "open", // Home
  specimens: "open",
  "specimens/:id": "open",
  artifacts: "open",
  "artifacts/:id": "open",
  map: "open",
  locations: "open",
  "locations/:id": "open",
  reference: "open",
  gear: "open",
  "gem-shows": "open",
  "gem-shows/:id": "open",
  glossary: "open",
  "mohs-scale": "open",
  "crystal-systems": "open",
  geology: "open",
  fluorescence: "open",
  "mineral-care": "open",
  lapidary: "open",
  "meteorite-hunting": "open",
  paleontology: "open",
  aurora: "open",
  stars: "open",
  "severe-weather": "open",
  "natural-wonders": "open",
  "blm-guide": "open",
  "state-parks": "open",
  "state-park/:id": "open",
  "prehistoric-organisms": "open",
  "periodic-table": "open",
  "tectonic-volcanic": "open",
  "resource-links": "open",
  "rocks-are-amazing": "open",
  "rock-types": "open",
  "mineral-id": "open",
  "crystal-hardness": "open",
  "rock-cycle": "open",
  "geo-time-scale": "open",
  "mass-extinctions": "open",
  "fossil-types": "open",
  "geologic-periods": "open",
  "period/:id": "open",
  search: "open",
  disclaimer: "open",
  "how-to-use": "open",
  contact: "open",

  // ─── Bookmarks (read + write personal data, free + premium) ───
  collection: "bookmarks",
  wishlist: "bookmarks",
  favorites: "bookmarks",
  profile: "bookmarks",
  settings: "bookmarks",
  offline: "bookmarks",
  paywall: "bookmarks",
  "token-info": "bookmarks",
  referral: "bookmarks",
  "manage-devices": "bookmarks",
  captures: "bookmarks",
  "saved-images": "bookmarks",

  // ─── Premium (locked for free users — show PremiumGate) ───
  identify: "premium",
  scan: "premium",
  journal: "premium",
  trips: "premium",
  "archived-trips": "premium",
  "trip-calendar": "premium",
  "trip-journal": "premium",

  // ─── Social (fully hidden for free users — redirect to Home) ───
  community: "social",
  "community/:postId": "social",
  friends: "social",
  "profile/friends": "social",
  messenger: "social",
  trade: "social",
  "trading-floor": "social",
  "my-trades": "social",
  "profile/:id": "social",
  "discover-hunters": "social",
  "rockscouts-map": "social",
  "social-settings": "social",
  "shared-spot/:lat/:lng": "social",
  achievements: "social",
  "all-achievements": "social",
  notifications: "social",
  "user-achievements/:id": "social",
  "user-collection/:id/:mode": "social",
  "blm": "open",
  "campgrounds": "open",
  "dinosaurs": "open",
  "meteorites": "open",
  "prehistoric": "open",
  "resources": "open",
  "tectonics": "open",
  "weather": "open",
  "planets": "open",
  "deep-sky": "open",
  "important-stars": "open",
};

/**
 * Get the access tier for a route path.
 * Falls back to "open" for unlisted routes.
 */
export function getRouteTier(path: string): RouteTier {
  return ROUTE_ACCESS[path] ?? "open";
}
