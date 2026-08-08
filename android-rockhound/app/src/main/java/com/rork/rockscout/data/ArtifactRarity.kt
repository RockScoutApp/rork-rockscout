package com.rork.rockscout.data

/**
 * Derives a rarity level for an [Artifact] based on its family and ID.
 *
 * Rarity tiers (matching the specimen rarity vocabulary):
 * - **Common** — frequently found by collectors and archaeologists
 * - **Uncommon** — found regularly but not at every site
 * - **Rare** — unusual finds that excite collectors
 * - **Exceptional** — rarely seen, museum-quality specimens
 * - **Museum Grade** — once-in-a-lifetime finds
 *
 * The mapping is based on real-world relic hunting and archaeological
 * frequency data. Bullets and percussion caps are the most common CW
 * relics; plates, effigy pipes, and Clovis points are exceptional finds.
 */
object ArtifactRarity {

    /** Rarity levels in ascending order of scarcity. */
    val LEVELS = listOf("Common", "Uncommon", "Rare", "Exceptional", "Museum Grade")

    private val RARITY_MAP: Map<String, String> = mapOf(

        // ── Prehistoric: Arrowheads ───────────────────────────────
        "art-clovis-point" to "Exceptional",
        "art-folsom-point" to "Exceptional",
        "art-dalton-point" to "Rare",
        "art-scottsbluff-point" to "Rare",
        "art-agate-basin-point" to "Rare",
        "art-plano-point" to "Uncommon",
        "art-adena-point" to "Uncommon",
        "art-corner-notched-point" to "Common",
        "art-hell-gap-point" to "Rare",
        "art-plainview-point" to "Rare",
        "art-meserve-point" to "Rare",
        "art-calf-creek-point" to "Uncommon",
        "art-kirk-point" to "Common",
        "art-palmer-point" to "Uncommon",
        "art-lecroy-point" to "Uncommon",
        "art-stanly-point" to "Uncommon",
        "art-morrow-mountain-point" to "Common",
        "art-guilford-point" to "Rare",
        "art-savannah-river-point" to "Common",
        "art-corner-tang-knife" to "Exceptional",

        // ── Prehistoric: Spear Points & Dart Tips ────────────────
        "art-atlatl-dart-point" to "Uncommon",
        "art-eden-point" to "Rare",
        "art-midland-point" to "Rare",
        "art-goshen-point" to "Exceptional",
        "art-milnesand-point" to "Rare",
        "art-angostura-point" to "Uncommon",
        "art-hardaway-point" to "Uncommon",
        "art-kirk-stemmed-point" to "Common",
        "art-big-sandy-point" to "Uncommon",
        "art-geneva-point" to "Uncommon",
        "art-robbins-nodule-point" to "Rare",
        "art-resharpened-dart-point" to "Common",

        // ── Prehistoric: Hand Axes & Axe Heads ───────────────────
        "art-acheulean-hand-axe" to "Uncommon",
        "art-cordiform-hand-axe" to "Rare",
        "art-ovate-hand-axe" to "Uncommon",
        "art-micoquian-hand-axe" to "Rare",
        "art-limande-hand-axe" to "Rare",
        "art-triangular-hand-axe" to "Uncommon",
        "art-biface-cleaver" to "Rare",
        "art-pick-hand-axe" to "Uncommon",
        "art-discoidal-hand-axe" to "Uncommon",
        "art-uniface-hand-axe" to "Common",
        "art-shouldered-hand-axe" to "Rare",
        "art-lens-hand-axe" to "Exceptional",
        "art-three-quarter-grooved-axe" to "Uncommon",
        "art-full-grooved-axe" to "Uncommon",
        "art-celt-ungrooved" to "Common",
        "art-notched-axe" to "Uncommon",
        "art-ground-stone-adze" to "Uncommon",
        "art-stone-gouge" to "Rare",
        "art-core-axe" to "Common",
        "art-chopper" to "Common",

        // ── Prehistoric: Drill Bits ───────────────────────────────
        "art-flint-drill" to "Uncommon",
        "art-micro-drill" to "Rare",
        "art-t-drill" to "Rare",
        "art-reamer" to "Uncommon",
        "art-strike-a-light" to "Uncommon",
        "art-gunflint" to "Common",

        // ── Prehistoric: Flaked Stone Tools ──────────────────────
        "art-end-scraper" to "Common",
        "art-side-scraper" to "Common",
        "art-burin" to "Uncommon",
        "art-graver-spall" to "Rare",
        "art-unifacial-tool" to "Common",
        "art-hammerstone" to "Common",
        "art-bolas-stone" to "Rare",
        "art-polyhedron-core" to "Common",
        "art-manuport" to "Exceptional",
        "art-backed-knife" to "Uncommon",
        "art-flint-sickle" to "Rare",
        "art-biface-knife" to "Uncommon",

        // ── Prehistoric: Stone Effigies ──────────────────────────
        "art-stone-effigy" to "Exceptional",
        "art-adena-effigy-pipe" to "Museum Grade",
        "art-hopewell-bird-effigy" to "Exceptional",
        "art-mississippian-birdman" to "Museum Grade",
        "art-stone-bear-effigy" to "Exceptional",
        "art-stone-serpent-effigy" to "Exceptional",
        "art-stone-turtle-effigy" to "Rare",

        // ── Prehistoric: Native Beads ─────────────────────────────
        "art-shell-beads" to "Common",
        "art-shell-disc-bead" to "Common",
        "art-tubular-shell-bead" to "Uncommon",
        "art-wampum-bead" to "Rare",
        "art-bone-bead" to "Common",
        "art-stone-bead" to "Uncommon",
        "art-steatite-bead" to "Common",
        "art-barrel-bead" to "Common",
        "art-shell-bead-column" to "Uncommon",
        "art-shell-gorget" to "Rare",
        "art-shell-pendant" to "Common",
        "art-stone-pendant" to "Uncommon",
        "art-shell-pin" to "Rare",

        // ── Prehistoric: Shell / Bone / Effigy / Ornaments ───────
        "art-shell-gouge" to "Uncommon",
        "art-shell-fishhook" to "Rare",
        "art-bone-needle" to "Uncommon",
        "art-bone-awl" to "Common",
        "art-shell-effigy" to "Exceptional",
        "art-atlatl-weight" to "Uncommon",
        "art-bannerstone" to "Exceptional",
        "art-gorget" to "Rare",
        "art-plummet" to "Rare",

        // ── Prehistoric: Pipes / Game Discs / Pottery / Wood ─────
        "art-stone-pipe" to "Rare",
        "art-ceramic-pipe" to "Uncommon",
        "art-effigy-pipe" to "Exceptional",
        "art-medicine-tube" to "Rare",
        "art-game-disc" to "Rare",
        "art-pottery-sherd" to "Common",
        "art-atlatl-handle" to "Exceptional",

        // ── War Relics: Civil War Bullets ────────────────────────
        "wr-cw-minie-ball-58" to "Common",
        "wr-cw-enfield-minie-577" to "Common",
        "wr-cw-sharps-54" to "Common",
        "wr-cw-burnside-54" to "Uncommon",
        "wr-cw-spencer-52" to "Uncommon",
        "wr-cw-colt-army-44" to "Common",
        "wr-cw-colt-navy-36" to "Uncommon",
        "wr-cw-williams-cleaner-58" to "Uncommon",
        "wr-cw-gardner-54" to "Uncommon",
        "wr-cw-round-ball-69" to "Common",
        "wr-cw-buck-and-ball" to "Uncommon",
        "wr-cw-teat-base-58" to "Uncommon",

        // ── War Relics: Revolutionary War Bullets ────────────────
        "wr-rw-brown-bess-ball" to "Uncommon",
        "wr-rw-charleville-ball" to "Uncommon",
        "wr-rw-kentucky-rifle-ball" to "Rare",
        "wr-rw-pistol-ball-60" to "Rare",
        "wr-rw-buckshot-swan-shot" to "Rare",

        // ── War Relics: Artillery Projectiles ────────────────────
        "wr-cw-12lb-solid-shot" to "Uncommon",
        "wr-cw-6lb-solid-shot" to "Uncommon",
        "wr-cw-12lb-case-shot" to "Rare",
        "wr-cw-10lb-parrott" to "Rare",
        "wr-cw-20lb-parrott" to "Rare",
        "wr-cw-whitworth-bolt" to "Exceptional",
        "wr-cw-schenkl-shell" to "Rare",
        "wr-cw-canister-shot" to "Uncommon",

        // ── War Relics: Uniform Buttons ──────────────────────────
        "wr-cw-union-eagle-I" to "Common",
        "wr-cw-union-eagle-A" to "Common",
        "wr-cw-union-eagle-C" to "Common",
        "wr-cw-confederate-block-I" to "Uncommon",
        "wr-cw-confederate-block-A" to "Uncommon",
        "wr-cw-confederate-block-C" to "Uncommon",
        "wr-cw-confederate-plain" to "Common",
        "wr-cw-union-ny-seal" to "Rare",
        "wr-cw-union-coat-button" to "Common",
        "wr-cw-union-cuff-button" to "Common",
        "wr-rw-continental-button" to "Rare",
        "wr-rw-british-regimental-button" to "Rare",

        // ── War Relics: Belt Buckles & Plates ────────────────────
        "wr-cw-us-oval-plate" to "Uncommon",
        "wr-cw-cs-oval-plate" to "Rare",
        "wr-cw-us-cartridge-box-plate" to "Uncommon",
        "wr-cw-cs-cartridge-box-plate" to "Exceptional",
        "wr-cw-us-breastplate" to "Rare",
        "wr-cw-officer-sword-belt-plate" to "Rare",
        "wr-cw-nco-waistbelt-plate" to "Uncommon",
        "wr-cw-georgia-state-plate" to "Exceptional",
        "wr-rw-british-waistbelt-buckle" to "Rare",

        // ── War Relics: Edged Weapons & Bayonets ─────────────────
        "wr-cw-socket-bayonet" to "Uncommon",
        "wr-cw-officer-sword-hilt" to "Rare",
        "wr-cw-enlisted-sword-hilt" to "Uncommon",
        "wr-rw-socket-bayonet" to "Rare",
        "wr-cw-bayonet-scabbard-chape" to "Uncommon",

        // ── War Relics: Military Accoutrements ───────────────────
        "wr-cw-percussion-cap" to "Common",
        "wr-cw-cartridge-box-tin" to "Uncommon",
        "wr-cw-cap-pouch" to "Uncommon",
        "wr-cw-gun-tool-bullet-mold" to "Rare",
        "wr-cw-friction-primer" to "Uncommon",
        "wr-cw-canteen-spout-ring" to "Uncommon",
        "wr-cw-knapsack-hardware" to "Uncommon",

        // ── War Relics: Camp Life & Personal Items ───────────────
        "wr-cw-bone-toothbrush" to "Uncommon",
        "wr-cw-clay-pipe-bowl" to "Common",
        "wr-cw-hardtack-fragment" to "Rare",
        "wr-cw-tin-cup-fragment" to "Common",
        "wr-cw-bone-dice" to "Rare",
        "wr-cw-brass-id-tag" to "Rare",
    )

    /** Returns the rarity string for the given artifact ID, defaulting to "Common". */
    fun forId(id: String): String = RARITY_MAP[id] ?: "Common"
}
