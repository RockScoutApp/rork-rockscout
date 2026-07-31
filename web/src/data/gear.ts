import gearData from "@/data/gear.json";

/** Decode unicode escapes in emoji strings. */
const decodeEmoji = (s: string): string => {
  try {
    return s.replace(/\\u([0-9a-fA-F]{4})/g, (_, hex) =>
      String.fromCodePoint(parseInt(hex, 16)),
    );
  } catch {
    return s;
  }
};

export interface GearItem {
  id: string;
  name: string;
  description: string;
  emoji: string;
  url: string;
  priceBand: string;
  topPick?: boolean;
}

export const gearItems: GearItem[] = (gearData as GearItem[]).map((item) => ({
  ...item,
  emoji: decodeEmoji(item.emoji),
}));

/** Gear categories for filtering. */
export const GEAR_CATEGORIES = [
  { id: "all", label: "All Gear" },
  { id: "essentials", label: "Essentials", itemIds: ["loupe_10x", "rock_hammer", "field_bag", "safety_glasses", "hardness_kit", "streak_plate"] },
  { id: "tools", label: "Tools", itemIds: ["loupe_30x", "chisel_set", "sledge_3lb", "pick_hook_set", "engraving_pen", "stiff_brush"] },
  { id: "field", label: "Field Gear", itemIds: ["field_notebook", "spray_bottle", "gps_handheld", "rain_gear", "hiking_boots", "sun_hat", "kneepads", "kneeling_pad"] },
  { id: "beach", label: "Beach & Creek", itemIds: ["scoop_tele", "sifting_sieve", "collapsible_strainer", "viewing_bucket", "waders", "bug_spray"] },
  { id: "uv", label: "UV & Fluorescence", itemIds: ["uv_light_365", "uv_light_sw"] },
  { id: "camping", label: "Camping", itemIds: ["tent", "sleeping_bag", "lantern", "noaa_radio", "hand_warmers"] },
  { id: "gold", label: "Gold Panning", itemIds: ["gold_pan", "portable_sluice", "fishing_magnets"] },
  { id: "display", label: "Display & Storage", itemIds: ["display_cases", "lapidary", "rock_starter_kit"] },
] as const;
