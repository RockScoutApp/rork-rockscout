package com.rork.rockscout.data

/**
 * Affiliate gear-link configuration (Phase 9 — passive revenue).
 *
 * Rockhounds buy gear constantly — rock hammers, field bags, 10x loupes,
 * hardness kits, streak plates, books. Amazon Associates pays 1–4% on most
 * categories, ~4.5% on outdoor/tools. Links are stored here so they can be
 * updated without an app release.
 *
 * All links open the system browser / Amazon app via an intent; the app
 * never handles transactions. Links are useful content, not ads — they stay
 * visible to every user (Premium and Pro subscribers see them too).
 */

/** A single affiliate gear item. */
data class GearItem(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    /** Amazon affiliate URL (or direct merchant URL). */
    val url: String,
    /** Approximate price band for display. */
    val priceBand: String,
    /** Whether this item is a "Top Pick" — drives a highlight badge to boost engagement. */
    val topPick: Boolean = false,
)

/** A themed gear kit — beginner / intermediate / advanced or per-specimen-type. */
data class GearKit(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val items: List<GearItem>,
)

object GearGuide {

    // ── Individual gear items (declared first so the kits/lists below resolve) ──

    val GEAR_LOUPE_10X = GearItem(
        id = "loupe_10x",
        name = "10x Jeweler's Loupe",
        description = "The single most important tool a rockhound owns. Lets you see crystal structure, inclusions, and surface detail the naked eye misses.",
        emoji = "\uD83D\uDD0D",
        url = "https://www.amazon.com/s?k=10x+jewelers+loupe&tag=rockscout-20",
        priceBand = "$8 – $15",
        topPick = true,
    )

    val GEAR_LOUPE_30X = GearItem(
        id = "loupe_30x",
        name = "30x Triplet Loupe",
        description = "Higher magnification for serious mineral ID — triplet lens corrects distortion at the edges. The step up from a basic 10x.",
        emoji = "\uD83D\uDD0D",
        url = "https://www.amazon.com/s?k=30x+triplet+loupe&tag=rockscout-20",
        priceBand = "$20 – $40",
    )

    val GEAR_HEADBAND_MAGNIFIER = GearItem(
        id = "headband_magnifier",
        name = "Headband Magnifier",
        description = "A hands-free head-mounted magnifier for examining specimens in the field without juggling a loupe. Great for long sorting sessions on the gravel bar.",
        emoji = "\uD83D\uDD2C",
        url = "https://www.amazon.com/s?k=headband+magnifier+hands+free&tag=rockscout-20",
        priceBand = "$15 – $50",
    )

    val GEAR_HARDNESS_KIT = GearItem(
        id = "hardness_kit",
        name = "Mohs Hardness Kit",
        description = "Reference specimens for the Mohs scale (talc through diamond substitutes). The classic way to narrow down a mineral ID in the field.",
        emoji = "\uD83D\uDD73\uFE0F",
        url = "https://www.amazon.com/s?k=mohs+hardness+kit&tag=rockscout-20",
        priceBand = "$25 – $60",
        topPick = true,
    )

    val GEAR_STREAK_PLATE = GearItem(
        id = "streak_plate",
        name = "Streak Plate (Unglazed Porcelain)",
        description = "A small unglazed porcelain tile for streak tests — the powder color left behind is a key diagnostic for many minerals.",
        emoji = "\uD83D\uDDA8\uFE0F",
        url = "https://www.amazon.com/s?k=streak+plate+unglazed+porcelain&tag=rockscout-20",
        priceBand = "$5 – $10",
        topPick = true,
    )

    val GEAR_ROCK_HAMMER = GearItem(
        id = "rock_hammer",
        name = "Rock Pick Hammer",
        description = "The standard field hammer — flat face for breaking, pick end for prying and splitting layers. Essential for any collecting trip.",
        emoji = "\uD83D\uDD28",
        url = "https://www.amazon.com/s?k=rock+pick+hammer+geology&tag=rockscout-20",
        priceBand = "$25 – $50",
        topPick = true,
    )

    val GEAR_ROCK_HAMMER_PRO = GearItem(
        id = "rock_hammer_pro",
        name = "Forged Geology Hammer (Estwing)",
        description = "Single-piece forged steel with a shock-reduction grip — the gold standard. Lasts a lifetime, transfers more force than a cast hammer.",
        emoji = "\uD83D\uDD28",
        url = "https://www.amazon.com/s?k=estwing+rock+pick&tag=rockscout-20",
        priceBand = "$45 – $80",
        topPick = true,
    )

    val GEAR_CHISEL_SET = GearItem(
        id = "chisel_set",
        name = "Rock Chisel Set",
        description = "Cold chisels for splitting nodules, geodes, and matrix rock. Pair with the sledge for harder material.",
        emoji = "\uD83D\uDEBF",
        url = "https://www.amazon.com/s?k=rock+chisel+set+geology&tag=rockscout-20",
        priceBand = "$15 – $35",
    )

    val GEAR_SLEDGE_3LB = GearItem(
        id = "sledge_3lb",
        name = "3 lb Crack Hammer",
        description = "For breaking open geodes, nodules, and large matrix blocks. Use with chisels — not your rock pick.",
        emoji = "\uD83D\uDD28",
        url = "https://www.amazon.com/s?k=3lb+crack+hammer&tag=rockscout-20",
        priceBand = "$25 – $45",
    )

    val GEAR_PICK_HOOK_SET = GearItem(
        id = "pick_hook_set",
        name = "Pick and Hook Tool Set",
        description = "Small picks and hooks for extracting delicate crystals from vugs and cleaning out pockets without damage. The fine-work companion to a hammer and chisel.",
        emoji = "\uD83E\uDE9D",
        url = "https://www.amazon.com/s?k=pick+and+hook+tool+set&tag=rockscout-20",
        priceBand = "$12 – $25",
    )

    val GEAR_ENGRAVING_PEN = GearItem(
        id = "engraving_pen",
        name = "Pneumatic Air Scribe / Prep Pen",
        description = "A hand-held pneumatic pen for fossil preparation, matrix removal, and deep cleaning of rock and crystal specimens — the step between a hand chisel and a full air scribe. Ideal for careful, detailed prep work without damaging delicate material.",
        emoji = "\u270F\uFE0F",
        url = "https://www.amazon.com/s?k=pneumatic+air+scribe+prep+pen&tag=rockscout-20",
        priceBand = "$40 – $120",
    )

    val GEAR_FIELD_BAG = GearItem(
        id = "field_bag",
        name = "Field Collection Bag",
        description = "A rugged canvas or Cordura bag with pockets for specimens, tools, and a water bottle. Padded dividers keep finds from scratching each other.",
        emoji = "\uD83C\uDF92",
        url = "https://www.amazon.com/s?k=rockhound+field+bag&tag=rockscout-20",
        priceBand = "$20 – $45",
        topPick = true,
    )

    val GEAR_WATERPROOF_FIELD_BAG = GearItem(
        id = "waterproof_field_bag",
        name = "Waterproof Field Bag",
        description = "A waterproof roll-top bag that keeps your specimens, notebook, and phone dry through creek crossings and rain. No more soggy labels.",
        emoji = "\uD83C\uDF92",
        url = "https://www.amazon.com/s?k=waterproof+dry+bag+field&tag=rockscout-20",
        priceBand = "$25 – $60",
    )

    val GEAR_UV_LIGHT_365 = GearItem(
        id = "uv_light_365",
        name = "365nm UV Flashlight (Longwave)",
        description = "Reveals fluorescent minerals (yooperlite, fluorite, calcite, willemite) and is essential for night-hunting yooperlites on Lake Superior beaches.",
        emoji = "\uD83D\uDD26",
        url = "https://www.amazon.com/s?k=365nm+uv+flashlight&tag=rockscout-20",
        priceBand = "$15 – $40",
        topPick = true,
    )

    val GEAR_UV_LIGHT_SW = GearItem(
        id = "uv_light_sw",
        name = "Shortwave UV Light",
        description = "Shortwave (254nm) UV reveals a different set of fluorescent minerals than longwave — calcite, willemite, scheelite, and many uranium minerals. More specialized than a 365nm light.",
        emoji = "\uD83D\uDF76",
        url = "https://www.amazon.com/s?k=shortwave+uv+light+254nm&tag=rockscout-20",
        priceBand = "$80 – $300",
    )

    val GEAR_FIELD_NOTEBOOK = GearItem(
        id = "field_notebook",
        name = "Rite-in-the-Rain Field Notebook",
        description = "Waterproof all-weather notebook for recording finds, locations, and notes in any condition. The field standard for working geologists.",
        emoji = "\uD83D\uDCD3",
        url = "https://www.amazon.com/s?k=rite+in+the+rain+notebook&tag=rockscout-20",
        priceBand = "$10 – $25",
        topPick = true,
    )

    val GEAR_SPRAY_BOTTLE = GearItem(
        id = "spray_bottle",
        name = "Small Spray Bottle",
        description = "Wetting rocks reveals colors and patterns hidden when dry — Petoskey stones, agates, and fossils pop with a quick spray. Bring water.",
        emoji = "\uD83D\uDCA7",
        url = "https://www.amazon.com/s?k=small+spray+bottle&tag=rockscout-20",
        priceBand = "$3 – $8",
    )

    val GEAR_GPS_HANDHELD = GearItem(
        id = "gps_handheld",
        name = "Handheld GPS Unit",
        description = "Marks find locations when cell service drops — essential for remote desert and mountain hunts. Backup to your phone's GPS.",
        emoji = "\uD83D\uDDFA\uFE0F",
        url = "https://www.amazon.com/s?k=handheld+gps+geocaching&tag=rockscout-20",
        priceBand = "$100 – $300",
    )

    val GEAR_RAIN_GEAR = GearItem(
        id = "rain_gear",
        name = "Rain Gear / Shell",
        description = "A waterproof breathable jacket and pants keep you dry on wet creek beds and in surprise storms — where some of the best material washes out after rain.",
        emoji = "\uD83C\uDF26\uFE0F",
        url = "https://www.amazon.com/s?k=rain+gear+waterproof+jacket+pants&tag=rockscout-20",
        priceBand = "$30 – $150",
    )

    val GEAR_WADERS = GearItem(
        id = "waders",
        name = "Chest Waders",
        description = "Wade creeks and riverbeds without soaking your boots — essential for gravel-bar collecting, fluorite creeks, and agate hunting in current.",
        emoji = "\uD83E\uDD73",
        url = "https://www.amazon.com/s?k=fishing+waders+chest&tag=rockscout-20",
        priceBand = "$50 – $180",
    )

    val GEAR_HIKING_BOOTS = GearItem(
        id = "hiking_boots",
        name = "Waterproof Hiking Boots",
        description = "Waterproof, ankle-supporting hiking boots for rough terrain and creek crossings — the foundation of every dig kit. Worth spending on.",
        emoji = "\uD83E\uDD7E",
        url = "https://www.amazon.com/s?k=waterproof+hiking+boots+ankle&tag=rockscout-20",
        priceBand = "$80 – $200",
        topPick = true,
    )

    val GEAR_SCOOP_TELE = GearItem(
        id = "scoop_tele",
        name = "Telescoping Rock Scoop",
        description = "Extending-handle scoop for sifting gravel in creeks and beach surf — saves your back on long hunts and lets you work deeper water without wading in.",
        emoji = "\uD83E\uDEA3",
        url = "https://www.amazon.com/s?k=telescoping+rock+scoop&tag=rockscout-20",
        priceBand = "$30 – $90",
    )

    val GEAR_SIFTING_SIEVE = GearItem(
        id = "sifting_sieve",
        name = "Mesh Sifting Sieve",
        description = "A framed mesh sieve for sifting gravel and loose sediment for small fossils, shark teeth, and gem gravel. Stackable with other mesh sizes for grading finds.",
        emoji = "\uD83E\uDEEC",
        url = "https://www.amazon.com/s?k=mesh+sifting+sieve+geology&tag=rockscout-20",
        priceBand = "$20 – $60",
    )

    val GEAR_HIKING_BACKPACK = GearItem(
        id = "hiking_backpack",
        name = "Hiking Daypack",
        description = "A proper day-hike pack with hip belt and hydration sleeve for longer treks to remote dig sites — carries water, layers, lunch, plus your finds on the way out.",
        emoji = "\uD83C\uDF92",
        url = "https://www.amazon.com/s?k=hiking+backpack+daypack&tag=rockscout-20",
        priceBand = "$40 – $130",
    )

    val GEAR_COLLAPSIBLE_STRAINER = GearItem(
        id = "collapsible_strainer",
        name = "Collapsible Strainer",
        description = "A cheap, wide collapsible colander/strainer is a rockhound secret weapon for sifting small fossils, shark teeth, and gem gravel out of loose sediment and creek beds — and it packs flat.",
        emoji = "\uD83E\uDDC4",
        url = "https://www.amazon.com/s?k=collapsible+colander+strainer&tag=rockscout-20",
        priceBand = "$8 – $20",
    )

    val GEAR_SUN_HAT = GearItem(
        id = "sun_hat",
        name = "Sun Hat / Beach Hat",
        description = "Wide-brim UPF hat for desert, beach, and open-quarry digs where shade is nonexistent. Keeps sun off your face, neck, and ears during long exposure.",
        emoji = "\uD83C\uDFA3",
        url = "https://www.amazon.com/s?k=sun+hat+upf+wide+brim&tag=rockscout-20",
        priceBand = "$15 – $40",
    )

    val GEAR_SAFETY_GLASSES = GearItem(
        id = "safety_glasses",
        name = "Safety Glasses",
        description = "Impact-resistant eye protection — essential when swinging a hammer or splitting rock. Rock splinters fly fast and a chip in the eye ends a trip.",
        emoji = "\uD83E\uDD7D",
        url = "https://www.amazon.com/s?k=safety+glasses+impact&tag=rockscout-20",
        priceBand = "$5 – $20",
        topPick = true,
    )

    val GEAR_GLOVES_WATERPROOF = GearItem(
        id = "gloves_waterproof",
        name = "Waterproof Gloves",
        description = "Waterproof gloves for cold creek collecting and wet matrix work — keep hands dry and protected from sharp rock edges.",
        emoji = "\uD83E\uDD7C",
        url = "https://www.amazon.com/s?k=waterproof+work+gloves&tag=rockscout-20",
        priceBand = "$15 – $40",
    )

    val GEAR_KNEEPADS = GearItem(
        id = "kneepads",
        name = "Kneepads",
        description = "Padded knee protection for long hours kneeling on sharp rock and gravel — save your knees on creek-bed and quarry digs.",
        emoji = "\uD83E\uDD7B",
        url = "https://www.amazon.com/s?k=kneepads+work+padded&tag=rockscout-20",
        priceBand = "$15 – $40",
    )

    val GEAR_KNEELING_PAD = GearItem(
        id = "kneeling_pad",
        name = "Kneeling Pad",
        description = "A foam kneeling pad for comfortable ground work — lighter than kneepads and easy to strap to a pack. Pair with (or instead of) kneepads.",
        emoji = "\uD83E\uDDDE",
        url = "https://www.amazon.com/s?k=foam+kneeling+pad&tag=rockscout-20",
        priceBand = "$10 – $25",
    )

    val GEAR_STIFF_BRUSH = GearItem(
        id = "stiff_brush",
        name = "Stiff Plastic-Bristle Brush",
        description = "A stiff brush for cleaning dirt and loose matrix off specimens in the field — gentler than a chisel but tougher than a toothbrush.",
        emoji = "\uD83E\uDDF9",
        url = "https://www.amazon.com/s?k=stiff+bristle+brush+cleaning&tag=rockscout-20",
        priceBand = "$5 – $15",
    )

    val GEAR_FISHING_MAGNETS = GearItem(
        id = "fishing_magnets",
        name = "Fishing Magnets",
        description = "Neodymium magnet on a rope for magnet fishing in creeks and rivers — a fun side-quest while rockhounding, and it can even find meteorites.",
        emoji = "\uD83E\uDDF2",
        url = "https://www.amazon.com/s?k=fishing+magnet+neodymium+rope&tag=rockscout-20",
        priceBand = "$15 – $60",
    )

    val GEAR_FOLDABLE_SHOVEL = GearItem(
        id = "foldable_shovel",
        name = "Foldable Shovel",
        description = "A compact folding shovel for digging into gravel banks and loose sediment — packs small and weighs nothing. Easier than prying with a rock pick.",
        emoji = "\u26CF\uFE0F",
        url = "https://www.amazon.com/s?k=foldable+shovel+compact&tag=rockscout-20",
        priceBand = "$20 – $50",
    )

    val GEAR_GORILLA_CART = GearItem(
        id = "gorilla_cart",
        name = "Gorilla Cart",
        description = "A heavy-duty garden cart for hauling heavy specimens and tools from the dig site back to the car — saves your back on big loads of matrix or nodules.",
        emoji = "\uD83D\uDED9",
        url = "https://www.amazon.com/s?k=gorilla+cart+garden&tag=rockscout-20",
        priceBand = "$80 – $150",
    )

    val GEAR_BUG_SPRAY = GearItem(
        id = "bug_spray",
        name = "Bug Spray",
        description = "DEET or picaridin repellent — ticks, mosquitoes, and gnats are constant companions on summer creek and woods digs. Don't skip it.",
        emoji = "\uD83E\uDD9F",
        url = "https://www.amazon.com/s?k=bug+spray+deet+repellent&tag=rockscout-20",
        priceBand = "$5 – $15",
    )

    val GEAR_HAND_WARMERS = GearItem(
        id = "hand_warmers",
        name = "Hand Warmers",
        description = "Disposable or rechargeable hand warmers for winter digs and cold morning starts — frozen fingers drop specimens.",
        emoji = "\uD83D\uDD25",
        url = "https://www.amazon.com/s?k=hand+warmers+disposable+rechargeable&tag=rockscout-20",
        priceBand = "$5 – $20",
    )

    val GEAR_GOLD_PAN = GearItem(
        id = "gold_pan",
        name = "Gold Pan",
        description = "The classic gold pan for working concentrate by hand — essential for any prospecting trip and cheap to start.",
        emoji = "\uD83E\uDEE3",
        url = "https://www.amazon.com/s?k=gold+pan+prospecting&tag=rockscout-20",
        priceBand = "$10 – $40",
    )

    val GEAR_PORTABLE_SLUICE = GearItem(
        id = "portable_sluice",
        name = "Portable Gold Sluice",
        description = "A compact folding sluice box for panning gold-bearing creeks — sets up in shallow water and processes material far faster than panning alone.",
        emoji = "\uD83E\uDD11",
        url = "https://www.amazon.com/s?k=portable+gold+sluice+folding&tag=rockscout-20",
        priceBand = "$60 – $200",
    )

    val GEAR_ROCK_STARTER_KIT = GearItem(
        id = "rock_starter_kit",
        name = "Rock Collection Starter Kit",
        description = "A boxed set of 15–30 labeled mineral specimens for new collectors — a great reference set for learning ID and a head start on a display collection.",
        emoji = "\uD83E\uDEA8",
        url = "https://www.amazon.com/s?k=rock+collection+starter+kit+minerals&tag=rockscout-20",
        priceBand = "$20 – $60",
    )

    val GEAR_DISPLAY_CASES = GearItem(
        id = "display_cases",
        name = "Mineral & Crystal Display Cases",
        description = "Clear display cases and riker mounts to show off and protect your collection — keep dust off and specimens safe from handling.",
        emoji = "\uD83D\uDCE6",
        url = "https://www.amazon.com/s?k=mineral+display+case+riker+mount&tag=rockscout-20",
        priceBand = "$15 – $80",
    )

    val GEAR_LAPIDARY = GearItem(
        id = "lapidary",
        name = "Lapidary Equipment",
        description = "Cabochon machines, slab saws, and polishers for turning rough finds into finished stones — the next step after collecting.",
        emoji = "\uD83D\uDC8E",
        url = "https://www.amazon.com/s?k=lapidary+equipment+cabbing+machine&tag=rockscout-20",
        priceBand = "$100 – $500",
    )

    val GEAR_CAMPING_BUNDLE = GearItem(
        id = "camping_bundle",
        name = "Camping Equipment",
        description = "Shelter, stove, cookware, and camp essentials for multi-day remote digs — bundle the basics for trips to far-flung sites.",
        emoji = "\u26FA\uFE0F",
        url = "https://www.amazon.com/s?k=camping+gear+bundle+essentials&tag=rockscout-20",
        priceBand = "$50 – $300",
    )

    val GEAR_TENT = GearItem(
        id = "tent",
        name = "Camping Tent",
        description = "A weatherproof 2–4 person tent for multi-day digs at remote sites — many of the best collecting spots are hours from any hotel, and mornings on-site beat pre-dawn drives.",
        emoji = "\u26FA\uFE0F",
        url = "https://www.amazon.com/s?k=camping+tent+3+person&tag=rockscout-20",
        priceBand = "$80 – $250",
    )

    val GEAR_SLEEPING_BAG = GearItem(
        id = "sleeping_bag",
        name = "Sleeping Bag",
        description = "A 3-season bag rated to ~30°F for desert nights (which get surprisingly cold) and shoulder-season digs. Compresses small for a field pack.",
        emoji = "\uD83D\uDECF\uFE0F",
        url = "https://www.amazon.com/s?k=sleeping+bag+3+season+30+degree&tag=rockscout-20",
        priceBand = "$50 – $180",
    )

    val GEAR_LANTERN = GearItem(
        id = "lantern",
        name = "Solar / Battery Lantern",
        description = "Rechargeable camp lantern for night UV hunts, after-dark specimen sorting, and camp setup. Solar top-up means you don't burn through batteries on long trips.",
        emoji = "\uD83D\uDDA1",
        url = "https://www.amazon.com/s?k=solar+battery+camping+lantern&tag=rockscout-20",
        priceBand = "$20 – $60",
    )

    val GEAR_NOAA_RADIO = GearItem(
        id = "noaa_radio",
        name = "Midland NOAA Weather Radio",
        description = "Hand-crank/solar NOAA weather radio for remote trips — flash floods, lightning, and heat advisories can roll in fast on desert and creek-bed digs with no cell service. A safety essential.",
        emoji = "\uD83D\uDCF4",
        url = "https://www.amazon.com/s?k=midland+noaa+weather+radio+hand+crank&tag=rockscout-20",
        priceBand = "$30 – $70",
    )

    val GEAR_VIEWING_BUCKET = GearItem(
        id = "viewing_bucket",
        name = "Underwater Viewing Bucket",
        description = "A clear-bottom bucket for spotting specimens through creek and river water without getting in — surprisingly effective for agates, fossils, and fluorite in shallow current.",
        emoji = "\uD83E\uDEA3",
        url = "https://www.amazon.com/s?k=underwater+viewing+bucket&tag=rockscout-20",
        priceBand = "$25 – $60",
    )

    // ── Curated kits + lists (reference the items above) ────────────────────────

    /** Curated kits shown on the dedicated Gear Guide section under "Rocks Are Amazing". */
    val kits: List<GearKit> = listOf(
        GearKit(
            id = "beginner",
            title = "Beginner Kit",
            subtitle = "Everything a new rockhound needs for their first hunt",
            emoji = "\uD83C\uDF92",
            items = listOf(
                GEAR_LOUPE_10X,
                GEAR_HARDNESS_KIT,
                GEAR_STREAK_PLATE,
                GEAR_FIELD_BAG,
                GEAR_ROCK_HAMMER,
                GEAR_SAFETY_GLASSES,
                GEAR_ROCK_STARTER_KIT,
            ),
        ),
        GearKit(
            id = "intermediate",
            title = "Intermediate Kit",
            subtitle = "Step up your ID game and start field-collecting seriously",
            emoji = "⛏\uFE0F",
            items = listOf(
                GEAR_LOUPE_30X,
                GEAR_ROCK_HAMMER,
                GEAR_CHISEL_SET,
                GEAR_FIELD_BAG,
                GEAR_HARDNESS_KIT,
                GEAR_STREAK_PLATE,
                GEAR_UV_LIGHT_365,
                GEAR_FIELD_NOTEBOOK,
                GEAR_SAFETY_GLASSES,
                GEAR_COLLAPSIBLE_STRAINER,
                GEAR_PICK_HOOK_SET,
            ),
        ),
        GearKit(
            id = "advanced",
            title = "Advanced Kit",
            subtitle = "For serious collectors, prospectors, and lapidary work",
            emoji = "\uD83D\uDD2C",
            items = listOf(
                GEAR_LOUPE_30X,
                GEAR_HEADBAND_MAGNIFIER,
                GEAR_ROCK_HAMMER_PRO,
                GEAR_CHISEL_SET,
                GEAR_SLEDGE_3LB,
                GEAR_PICK_HOOK_SET,
                GEAR_ENGRAVING_PEN,
                GEAR_WATERPROOF_FIELD_BAG,
                GEAR_HARDNESS_KIT,
                GEAR_STREAK_PLATE,
                GEAR_UV_LIGHT_365,
                GEAR_UV_LIGHT_SW,
                GEAR_FIELD_NOTEBOOK,
                GEAR_RAIN_GEAR,
                GEAR_SIFTING_SIEVE,
                GEAR_SUN_HAT,
                GEAR_SAFETY_GLASSES,
                GEAR_GLOVES_WATERPROOF,
                GEAR_KNEEPADS,
                GEAR_STIFF_BRUSH,
                GEAR_TENT,
                GEAR_SLEEPING_BAG,
                GEAR_LANTERN,
                GEAR_NOAA_RADIO,
                GEAR_LAPIDARY,
            ),
        ),
    )

    /** All individual gear items — the flat catalog. */
    val allItems: List<GearItem> = listOf(
        GEAR_LOUPE_10X,
        GEAR_LOUPE_30X,
        GEAR_HEADBAND_MAGNIFIER,
        GEAR_HARDNESS_KIT,
        GEAR_STREAK_PLATE,
        GEAR_ROCK_HAMMER,
        GEAR_ROCK_HAMMER_PRO,
        GEAR_CHISEL_SET,
        GEAR_SLEDGE_3LB,
        GEAR_PICK_HOOK_SET,
        GEAR_ENGRAVING_PEN,
        GEAR_FIELD_BAG,
        GEAR_WATERPROOF_FIELD_BAG,
        GEAR_UV_LIGHT_365,
        GEAR_UV_LIGHT_SW,
        GEAR_FIELD_NOTEBOOK,
        GEAR_SPRAY_BOTTLE,
        GEAR_GPS_HANDHELD,
        GEAR_RAIN_GEAR,
        GEAR_WADERS,
        GEAR_HIKING_BOOTS,
        GEAR_SCOOP_TELE,
        GEAR_SIFTING_SIEVE,
        GEAR_HIKING_BACKPACK,
        GEAR_COLLAPSIBLE_STRAINER,
        GEAR_SUN_HAT,
        GEAR_SAFETY_GLASSES,
        GEAR_GLOVES_WATERPROOF,
        GEAR_KNEEPADS,
        GEAR_KNEELING_PAD,
        GEAR_STIFF_BRUSH,
        GEAR_FISHING_MAGNETS,
        GEAR_FOLDABLE_SHOVEL,
        GEAR_GORILLA_CART,
        GEAR_BUG_SPRAY,
        GEAR_HAND_WARMERS,
        GEAR_GOLD_PAN,
        GEAR_PORTABLE_SLUICE,
        GEAR_ROCK_STARTER_KIT,
        GEAR_DISPLAY_CASES,
        GEAR_LAPIDARY,
        GEAR_CAMPING_BUNDLE,
        GEAR_TENT,
        GEAR_SLEEPING_BAG,
        GEAR_LANTERN,
        GEAR_NOAA_RADIO,
        GEAR_VIEWING_BUCKET,
    )

    /**
     * Contextual gear recommendations for a given specimen's rock class.
     * Drives the "Gear to find this" section on Specimen Detail.
     */
    fun gearForRockClass(rockClass: RockClass): List<GearItem> = when (rockClass) {
        RockClass.IGNEOUS -> listOf(GEAR_ROCK_HAMMER, GEAR_LOUPE_10X, GEAR_HARDNESS_KIT, GEAR_FIELD_BAG, GEAR_SAFETY_GLASSES)
        RockClass.SEDIMENTARY -> listOf(GEAR_LOUPE_10X, GEAR_HARDNESS_KIT, GEAR_SPRAY_BOTTLE, GEAR_COLLAPSIBLE_STRAINER, GEAR_FIELD_NOTEBOOK, GEAR_STIFF_BRUSH)
        RockClass.METAMORPHIC -> listOf(GEAR_ROCK_HAMMER, GEAR_LOUPE_10X, GEAR_HARDNESS_KIT, GEAR_CHISEL_SET, GEAR_SAFETY_GLASSES)
        RockClass.MINERAL -> listOf(GEAR_LOUPE_30X, GEAR_HARDNESS_KIT, GEAR_STREAK_PLATE, GEAR_UV_LIGHT_365, GEAR_UV_LIGHT_SW, GEAR_PICK_HOOK_SET, GEAR_ENGRAVING_PEN)
        RockClass.CRYSTAL -> listOf(GEAR_LOUPE_30X, GEAR_HARDNESS_KIT, GEAR_STREAK_PLATE, GEAR_FIELD_BAG, GEAR_PICK_HOOK_SET, GEAR_ENGRAVING_PEN, GEAR_DISPLAY_CASES)
        RockClass.FOSSIL -> listOf(GEAR_LOUPE_10X, GEAR_PICK_HOOK_SET, GEAR_STIFF_BRUSH, GEAR_COLLAPSIBLE_STRAINER, GEAR_SPRAY_BOTTLE, GEAR_FIELD_NOTEBOOK, GEAR_ENGRAVING_PEN)
    }

    /**
     * Contextual gear recommendations for a dig-site location type.
     * Drives the "Recommended gear for this trip" section on dig-site cards.
     */
    fun gearForLocationType(type: LocationType): List<GearItem> = when (type) {
        LocationType.PUBLIC_DIG -> listOf(GEAR_ROCK_HAMMER, GEAR_LOUPE_10X, GEAR_FIELD_BAG, GEAR_HARDNESS_KIT, GEAR_SPRAY_BOTTLE, GEAR_PICK_HOOK_SET, GEAR_STIFF_BRUSH, GEAR_SAFETY_GLASSES, GEAR_KNEELING_PAD)
        LocationType.MINE -> listOf(GEAR_ROCK_HAMMER_PRO, GEAR_CHISEL_SET, GEAR_SLEDGE_3LB, GEAR_LOUPE_10X, GEAR_WATERPROOF_FIELD_BAG, GEAR_HARDNESS_KIT, GEAR_ENGRAVING_PEN, GEAR_PICK_HOOK_SET, GEAR_SAFETY_GLASSES, GEAR_NOAA_RADIO)
        LocationType.QUARRY -> listOf(GEAR_ROCK_HAMMER_PRO, GEAR_CHISEL_SET, GEAR_SLEDGE_3LB, GEAR_LOUPE_10X, GEAR_WATERPROOF_FIELD_BAG, GEAR_HARDNESS_KIT, GEAR_ENGRAVING_PEN, GEAR_PICK_HOOK_SET, GEAR_SAFETY_GLASSES, GEAR_NOAA_RADIO, GEAR_GORILLA_CART)
        LocationType.BEACH -> listOf(GEAR_LOUPE_10X, GEAR_SPRAY_BOTTLE, GEAR_FIELD_BAG, GEAR_UV_LIGHT_365, GEAR_SCOOP_TELE, GEAR_SIFTING_SIEVE, GEAR_SUN_HAT, GEAR_COLLAPSIBLE_STRAINER, GEAR_VIEWING_BUCKET, GEAR_LANTERN, GEAR_BUG_SPRAY)
        LocationType.RIVER -> listOf(GEAR_LOUPE_10X, GEAR_FIELD_BAG, GEAR_SPRAY_BOTTLE, GEAR_GPS_HANDHELD, GEAR_WADERS, GEAR_SCOOP_TELE, GEAR_SIFTING_SIEVE, GEAR_COLLAPSIBLE_STRAINER, GEAR_VIEWING_BUCKET, GEAR_NOAA_RADIO, GEAR_GOLD_PAN, GEAR_PORTABLE_SLUICE)
        LocationType.DESERT -> listOf(GEAR_LOUPE_10X, GEAR_FIELD_BAG, GEAR_SPRAY_BOTTLE, GEAR_GPS_HANDHELD, GEAR_UV_LIGHT_365, GEAR_HIKING_BACKPACK, GEAR_SUN_HAT, GEAR_TENT, GEAR_SLEEPING_BAG, GEAR_LANTERN, GEAR_NOAA_RADIO, GEAR_HAND_WARMERS)
        LocationType.ROCK_SHOP -> listOf(GEAR_LOUPE_10X)
        LocationType.METAPHYSICAL -> listOf(GEAR_LOUPE_10X)
        LocationType.LAPIDARY_CLUB -> listOf(GEAR_LOUPE_10X, GEAR_HARDNESS_KIT, GEAR_LAPIDARY, GEAR_DISPLAY_CASES)
    }

    /** "Tools to confirm this ID at home" — shown on Identify results. */
    val confirmIdGear: List<GearItem> = listOf(
        GEAR_LOUPE_10X,
        GEAR_HARDNESS_KIT,
        GEAR_STREAK_PLATE,
    )
}
