/**
 * Location reference data for the RockScout PWA.
 *
 * Ported from the Android app's Kotlin data files (SeedData, BlmData,
 * StateParkData, GemShowData, FamousBeaches, FamousShops, InternationalLocations)
 * into a bundled JSON file. This is static reference data — no Supabase needed.
 */

import locationData from "@/data/locations.json";

/** A dig site, mine, quarry, beach, river, desert, or shop. */
export interface DigSite {
  id: string;
  name: string;
  type: DigSiteType;
  region: string;
  latitude: number;
  longitude: number;
  summary: string;
  knownFor: string[];
  mineralTags: string[];
  feeInfo: string;
  hours: string;
  website: string | null;
  phone: string | null;
  difficulty: string;
  publicAccess: boolean;
  tips: string;
}

export type DigSiteType =
  | "PUBLIC_DIG"
  | "MINE"
  | "QUARRY"
  | "BEACH"
  | "RIVER"
  | "DESERT"
  | "ROCK_SHOP"
  | "METAPHYSICAL"
  | "LAPIDARY_CLUB";

/** A BLM-managed public collecting site. */
export interface BlmDigSite {
  id: string;
  name: string;
  region: string;
  latitude: number;
  longitude: number;
  whatToFind: string;
  directions: string;
  facilities: string;
  feeInfo: string;
  difficulty: string;
  website: string | null;
  type: "BLM_DIG_SITE";
}

/** A BLM trailhead useful for rockhounds. */
export interface Trailhead {
  id: string;
  name: string;
  state: string;
  region: string;
  description: string;
  latitude: number;
  longitude: number;
  type: "TRAILHEAD";
}

/** A BLM campground near rockhounding areas. */
export interface Campground {
  id: string;
  name: string;
  state: string;
  region: string;
  description: string;
  feeInfo: string;
  latitude: number;
  longitude: number;
  type: "CAMPGROUND";
}

/** A U.S. state park with rockhounding or geological relevance. */
export interface StatePark {
  id: string;
  name: string;
  state: string;
  region: string;
  description: string;
  latitude: number;
  longitude: number;
  hasCamping: boolean;
  feeInfo: string;
  website: string | null;
  type: "STATE_PARK";
}

/** An upcoming gem, mineral, or fossil show. */
export interface GemShow {
  id: string;
  name: string;
  city: string;
  state: string;
  venue: string;
  monthLabel: string;
  dateRange: string;
  description: string;
  entryFee: string;
  isAnnual: boolean;
  website: string;
  latitude: number;
  longitude: number;
  monthIndex: number;
  phone: string | null;
  email: string | null;
  type: "GEM_SHOW";
}

/** A unified map marker — any location type that can appear on the Leaflet map. */
export interface MapMarker {
  id: string;
  name: string;
  region: string;
  latitude: number;
  longitude: number;
  type: DigSiteType | "BLM_DIG_SITE" | "TRAILHEAD" | "CAMPGROUND" | "STATE_PARK";
  emoji: string;
  color: string;
}

/** Emoji + color per location type for map markers. */
const TYPE_META: Record<string, { emoji: string; color: string }> = {
  PUBLIC_DIG: { emoji: "⛏️", color: "#C97B4A" },
  MINE: { emoji: "🏔️", color: "#7BA868" },
  QUARRY: { emoji: "🪨", color: "#8B7D6B" },
  BEACH: { emoji: "🏖️", color: "#5BB8B8" },
  RIVER: { emoji: "🌊", color: "#4A90D9" },
  DESERT: { emoji: "🏜️", color: "#D9B26A" },
  ROCK_SHOP: { emoji: "💎", color: "#B87333" },
  METAPHYSICAL: { emoji: "🔮", color: "#9B59B6" },
  LAPIDARY_CLUB: { emoji: "🏛️", color: "#BDC3C7" },
  BLM_DIG_SITE: { emoji: "⛏️", color: "#C97B4A" },
  TRAILHEAD: { emoji: "🥾", color: "#6B9E7E" },
  CAMPGROUND: { emoji: "⛺", color: "#E0A040" },
  STATE_PARK: { emoji: "🏞️", color: "#5A8C5A" },
  GEM_SHOW: { emoji: "🎪", color: "#E74C3C" },
};

/** Human-readable label per location type. */
const TYPE_LABELS: Record<string, string> = {
  PUBLIC_DIG: "Public Dig Site",
  MINE: "Mine",
  QUARRY: "Quarry",
  BEACH: "Beach / Shore",
  RIVER: "River / Creek",
  DESERT: "Desert / Field",
  ROCK_SHOP: "Rock Shop",
  METAPHYSICAL: "Metaphysical Shop",
  LAPIDARY_CLUB: "Lapidary Club",
  BLM_DIG_SITE: "BLM Collecting Site",
  TRAILHEAD: "Trailhead",
  CAMPGROUND: "Campground",
  STATE_PARK: "State Park",
  GEM_SHOW: "Gem & Mineral Show",
};

export const getTypeMeta = (type: string) =>
  TYPE_META[type] ?? { emoji: "📍", color: "#666" };

export const getTypeLabel = (type: string) =>
  TYPE_LABELS[type] ?? "Location";

// ── Exported typed arrays ──

export const digSites: DigSite[] = locationData.digSites as DigSite[];
export const blmDigSites: BlmDigSite[] = locationData.blmDigSites as BlmDigSite[];
export const trailheads: Trailhead[] = locationData.trailheads as Trailhead[];
export const campgrounds: Campground[] = locationData.campgrounds as Campground[];
export const stateParks: StatePark[] = locationData.stateParks as StatePark[];
export const gemShows: GemShow[] = locationData.gemShows as GemShow[];

/** All mapable locations as a unified marker list (for Leaflet). */
export const allMapMarkers: MapMarker[] = [
  ...digSites.map((s) => ({
    id: s.id,
    name: s.name,
    region: s.region,
    latitude: s.latitude,
    longitude: s.longitude,
    type: s.type,
    emoji: getTypeMeta(s.type).emoji,
    color: getTypeMeta(s.type).color,
  })),
  ...blmDigSites.map((s) => ({
    id: `blm-${s.id}`,
    name: s.name,
    region: s.region,
    latitude: s.latitude,
    longitude: s.longitude,
    type: "BLM_DIG_SITE" as const,
    emoji: getTypeMeta("BLM_DIG_SITE").emoji,
    color: getTypeMeta("BLM_DIG_SITE").color,
  })),
  ...trailheads.map((s) => ({
    id: `th-${s.id}`,
    name: s.name,
    region: s.region,
    latitude: s.latitude,
    longitude: s.longitude,
    type: "TRAILHEAD" as const,
    emoji: getTypeMeta("TRAILHEAD").emoji,
    color: getTypeMeta("TRAILHEAD").color,
  })),
  ...campgrounds.map((s) => ({
    id: `cg-${s.id}`,
    name: s.name,
    region: s.region,
    latitude: s.latitude,
    longitude: s.longitude,
    type: "CAMPGROUND" as const,
    emoji: getTypeMeta("CAMPGROUND").emoji,
    color: getTypeMeta("CAMPGROUND").color,
  })),
  ...stateParks.map((s) => ({
    id: `park-${s.id}`,
    name: s.name,
    region: s.region,
    latitude: s.latitude,
    longitude: s.longitude,
    type: "STATE_PARK" as const,
    emoji: getTypeMeta("STATE_PARK").emoji,
    color: getTypeMeta("STATE_PARK").color,
  })),
];

/** Total count of all locations. */
export const totalLocations = allMapMarkers.length;

/** Look up a dig site by ID. */
export const findDigSiteById = (id: string): DigSite | undefined =>
  digSites.find((s) => s.id === id);

/** Look up any location by its marker ID (handles prefixed IDs). */
export const findLocationByMarkerId = (
  id: string,
):
  | DigSite
  | BlmDigSite
  | Trailhead
  | Campground
  | StatePark
  | undefined => {
  if (id.startsWith("blm-")) {
    return blmDigSites.find((s) => `blm-${s.id}` === id);
  }
  if (id.startsWith("th-")) {
    return trailheads.find((s) => `th-${s.id}` === id);
  }
  if (id.startsWith("cg-")) {
    return campgrounds.find((s) => `cg-${s.id}` === id);
  }
  if (id.startsWith("park-")) {
    return stateParks.find((s) => `park-${s.id}` === id);
  }
  return findDigSiteById(id);
};

/** Haversine distance in miles between two lat/lng points. */
export const distanceMiles = (
  lat1: number,
  lng1: number,
  lat2: number,
  lng2: number,
): number => {
  const R = 3958.8; // Earth radius in miles
  const dLat = ((lat2 - lat1) * Math.PI) / 180;
  const dLng = ((lng2 - lng1) * Math.PI) / 180;
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos((lat1 * Math.PI) / 180) *
      Math.cos((lat2 * Math.PI) / 180) *
      Math.sin(dLng / 2) *
      Math.sin(dLng / 2);
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
};

/** Get the user's current position via browser geolocation. */
export const getCurrentPosition = (): Promise<{ lat: number; lng: number }> =>
  new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("Geolocation not supported on this device"));
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => resolve({ lat: pos.coords.latitude, lng: pos.coords.longitude }),
      (err) => reject(new Error(err.message)),
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 60000 },
    );
  });
