/**
 * Rarity lookup for artifacts and war relics.
 * Maps artifact IDs to discovery rarity levels based on real-world
 * find frequency. Mirrors Android `ArtifactRarity.kt`.
 *
 * Levels: Common, Uncommon, Rare, Exceptional, Museum Grade
 */

const RARITY_MAP: Record<string, string> = {
  // ── Prehistoric: Arrowheads ─────────────────────────────────
  "art-clovis-point": "Exceptional",
  "art-folsom-point": "Exceptional",
  "art-dalton-point": "Rare",
  "art-scottsbluff-point": "Rare",
  "art-agate-basin-point": "Rare",
  "art-plano-point": "Uncommon",
  "art-adena-point": "Uncommon",
  "art-corner-notched-point": "Common",
  "art-hell-gap-point": "Rare",
  "art-plainview-point": "Rare",
  "art-meserve-point": "Rare",
  "art-calf-creek-point": "Uncommon",
  "art-kirk-point": "Common",
  "art-palmer-point": "Uncommon",
  "art-lecroy-point": "Uncommon",
  "art-stanly-point": "Uncommon",
  "art-morrow-mountain-point": "Common",
  "art-guilford-point": "Rare",
  "art-savannah-river-point": "Common",
  "art-corner-tang-knife": "Exceptional",

  // ── Prehistoric: Spear Points & Dart Tips ──────────────────
  "art-atlatl-dart-point": "Uncommon",
  "art-eden-point": "Rare",
  "art-midland-point": "Rare",
  "art-goshen-point": "Exceptional",
  "art-milnesand-point": "Rare",
  "art-angostura-point": "Uncommon",
  "art-hardaway-point": "Uncommon",
  "art-kirk-stemmed-point": "Common",
  "art-big-sandy-point": "Uncommon",
  "art-geneva-point": "Uncommon",
  "art-robbins-nodule-point": "Rare",
  "art-resharpened-dart-point": "Common",

  // ── Prehistoric: Hand Axes & Axe Heads ─────────────────────
  "art-acheulean-hand-axe": "Uncommon",
  "art-cordiform-hand-axe": "Rare",
  "art-ovate-hand-axe": "Uncommon",
  "art-micoquian-hand-axe": "Rare",
  "art-limande-hand-axe": "Rare",
  "art-triangular-hand-axe": "Uncommon",
  "art-biface-cleaver": "Rare",
  "art-pick-hand-axe": "Uncommon",
  "art-discoidal-hand-axe": "Uncommon",
  "art-uniface-hand-axe": "Common",
  "art-shouldered-hand-axe": "Rare",
  "art-lens-hand-axe": "Exceptional",
  "art-three-quarter-grooved-axe": "Uncommon",
  "art-full-grooved-axe": "Uncommon",
  "art-celt-ungrooved": "Common",
  "art-notched-axe": "Uncommon",
  "art-ground-stone-adze": "Uncommon",
  "art-stone-gouge": "Rare",
  "art-core-axe": "Common",
  "art-chopper": "Common",

  // ── Prehistoric: Drill Bits ────────────────────────────────
  "art-flint-drill": "Uncommon",
  "art-micro-drill": "Rare",
  "art-t-drill": "Rare",
  "art-reamer": "Uncommon",
  "art-strike-a-light": "Uncommon",
  "art-gunflint": "Common",

  // ── Prehistoric: Flaked Stone Tools ────────────────────────
  "art-end-scraper": "Common",
  "art-side-scraper": "Common",
  "art-burin": "Uncommon",
  "art-graver-spall": "Rare",
  "art-unifacial-tool": "Common",
  "art-hammerstone": "Common",
  "art-bolas-stone": "Rare",
  "art-polyhedron-core": "Common",
  "art-manuport": "Exceptional",
  "art-backed-knife": "Uncommon",
  "art-flint-sickle": "Rare",
  "art-biface-knife": "Uncommon",

  // ── Prehistoric: Stone Effigies ────────────────────────────
  "art-stone-effigy": "Exceptional",
  "art-adena-effigy-pipe": "Museum Grade",
  "art-hopewell-bird-effigy": "Exceptional",
  "art-mississippian-birdman": "Museum Grade",
  "art-stone-bear-effigy": "Exceptional",
  "art-stone-serpent-effigy": "Exceptional",
  "art-stone-turtle-effigy": "Rare",

  // ── Prehistoric: Native Beads ──────────────────────────────
  "art-shell-beads": "Common",
  "art-shell-disc-bead": "Common",
  "art-tubular-shell-bead": "Uncommon",
  "art-wampum-bead": "Rare",
  "art-bone-bead": "Common",
  "art-stone-bead": "Uncommon",
  "art-steatite-bead": "Common",
  "art-barrel-bead": "Common",
  "art-shell-bead-column": "Uncommon",
  "art-shell-gorget": "Rare",
  "art-shell-pendant": "Common",
  "art-stone-pendant": "Uncommon",
  "art-shell-pin": "Rare",

  // ── Prehistoric: Shell / Bone / Effigy / Ornaments ─────────
  "art-shell-gouge": "Uncommon",
  "art-shell-fishhook": "Rare",
  "art-bone-needle": "Uncommon",
  "art-bone-awl": "Common",
  "art-shell-effigy": "Exceptional",
  "art-atlatl-weight": "Uncommon",
  "art-bannerstone": "Exceptional",
  "art-gorget": "Rare",
  "art-plummet": "Rare",

  // ── Prehistoric: Pipes / Game Discs / Pottery / Wood ───────
  "art-stone-pipe": "Rare",
  "art-ceramic-pipe": "Uncommon",
  "art-effigy-pipe": "Exceptional",
  "art-medicine-tube": "Rare",
  "art-game-disc": "Rare",
  "art-pottery-sherd": "Common",
  "art-atlatl-handle": "Exceptional",

  // ── War Relics: Civil War Bullets ──────────────────────────
  "wr-cw-minie-ball-58": "Common",
  "wr-cw-enfield-minie-577": "Common",
  "wr-cw-sharps-54": "Common",
  "wr-cw-burnside-54": "Uncommon",
  "wr-cw-spencer-52": "Uncommon",
  "wr-cw-colt-army-44": "Common",
  "wr-cw-colt-navy-36": "Uncommon",
  "wr-cw-williams-cleaner-58": "Uncommon",
  "wr-cw-gardner-54": "Uncommon",
  "wr-cw-round-ball-69": "Common",
  "wr-cw-buck-and-ball": "Uncommon",
  "wr-cw-teat-base-58": "Uncommon",

  // ── War Relics: Revolutionary War Bullets ──────────────────
  "wr-rw-brown-bess-ball": "Uncommon",
  "wr-rw-charleville-ball": "Uncommon",
  "wr-rw-kentucky-rifle-ball": "Rare",
  "wr-rw-pistol-ball-60": "Rare",
  "wr-rw-buckshot-swan-shot": "Rare",

  // ── War Relics: Artillery Projectiles ──────────────────────
  "wr-cw-12lb-solid-shot": "Uncommon",
  "wr-cw-6lb-solid-shot": "Uncommon",
  "wr-cw-12lb-case-shot": "Rare",
  "wr-cw-10lb-parrott": "Rare",
  "wr-cw-20lb-parrott": "Rare",
  "wr-cw-whitworth-bolt": "Exceptional",
  "wr-cw-schenkl-shell": "Rare",
  "wr-cw-canister-shot": "Uncommon",

  // ── War Relics: Uniform Buttons ────────────────────────────
  "wr-cw-union-eagle-I": "Common",
  "wr-cw-union-eagle-A": "Common",
  "wr-cw-union-eagle-C": "Common",
  "wr-cw-confederate-block-I": "Uncommon",
  "wr-cw-confederate-block-A": "Uncommon",
  "wr-cw-confederate-block-C": "Uncommon",
  "wr-cw-confederate-plain": "Common",
  "wr-cw-union-ny-seal": "Rare",
  "wr-cw-union-coat-button": "Common",
  "wr-cw-union-cuff-button": "Common",
  "wr-rw-continental-button": "Rare",
  "wr-rw-british-regimental-button": "Rare",

  // ── War Relics: Belt Buckles & Plates ──────────────────────
  "wr-cw-us-oval-plate": "Uncommon",
  "wr-cw-cs-oval-plate": "Rare",
  "wr-cw-us-cartridge-box-plate": "Uncommon",
  "wr-cw-cs-cartridge-box-plate": "Exceptional",
  "wr-cw-us-breastplate": "Rare",
  "wr-cw-officer-sword-belt-plate": "Rare",
  "wr-cw-nco-waistbelt-plate": "Uncommon",
  "wr-cw-georgia-state-plate": "Exceptional",
  "wr-rw-british-waistbelt-buckle": "Rare",

  // ── War Relics: Edged Weapons & Bayonets ───────────────────
  "wr-cw-socket-bayonet": "Uncommon",
  "wr-cw-officer-sword-hilt": "Rare",
  "wr-cw-enlisted-sword-hilt": "Uncommon",
  "wr-rw-socket-bayonet": "Rare",
  "wr-cw-bayonet-scabbard-chape": "Uncommon",

  // ── War Relics: Military Accoutrements ─────────────────────
  "wr-cw-percussion-cap": "Common",
  "wr-cw-cartridge-box-tin": "Uncommon",
  "wr-cw-cap-pouch": "Uncommon",
  "wr-cw-gun-tool-bullet-mold": "Rare",
  "wr-cw-friction-primer": "Uncommon",
  "wr-cw-canteen-spout-ring": "Uncommon",
  "wr-cw-knapsack-hardware": "Uncommon",

  // ── War Relics: Camp Life & Personal Items ─────────────────
  "wr-cw-bone-toothbrush": "Uncommon",
  "wr-cw-clay-pipe-bowl": "Common",
  "wr-cw-hardtack-fragment": "Rare",
  "wr-cw-tin-cup-fragment": "Common",
  "wr-cw-bone-dice": "Rare",
  "wr-cw-brass-id-tag": "Rare",
};

/** Returns the rarity string for the given artifact ID, defaulting to "Common". */
export function getArtifactRarity(id: string): string {
  return RARITY_MAP[id] ?? "Common";
}

/** Rarity level to color (HSL triple string for CSS). */
export function rarityColor(rarity: string): string {
  switch (rarity) {
    case "Museum Grade": return "45 100% 55%";
    case "Exceptional": return "340 75% 55%";
    case "Rare": return "4 70% 55%";
    case "Uncommon": return "36 80% 58%";
    default: return "142 52% 54%";
  }
}
