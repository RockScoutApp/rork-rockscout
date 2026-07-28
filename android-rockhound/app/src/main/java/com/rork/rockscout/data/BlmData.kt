package com.rork.rockscout.data

/**
 * Bureau of Land Management (BLM) rockhounding guide data.
 *
 * All rules, limits, and location information are curated from BLM.gov
 * public-facing pages and state-specific BLM rockhounding brochures.
 * Rules can change — always verify with the local BLM field office before
 * collecting. See: https://www.blm.gov/programs/recreation/rockhounding
 */

/** A U.S. state with BLM-managed public lands and rockhounding rules. */
data class BlmState(
    val code: String,          // "AZ"
    val name: String,          // "Arizona"
    val blmAcreage: String,    // "12.1 million acres"
    val accentHex: Long,       // color for the state card
    val silhouetteEmoji: String, // representative emoji glyph
    val whatYouCanCollect: String,
    val quantityLimits: String,
    val toolRestrictions: String,
    val permitNotes: String,
    val specialNotes: String,
    /** Dig sites sourced from BLM.gov for this state. */
    val blmDigSites: List<BlmDigSite>,
)

/** A BLM.gov-sourced public collecting location. */
data class BlmDigSite(
    val name: String,
    val region: String,        // "Safford, Arizona"
    val latitude: Double,
    val longitude: Double,
    val whatToFind: String,
    val directions: String,
    val facilities: String,
    val feeInfo: String,
    val difficulty: String,
    val website: String?,
    /** True if this location already exists in the app's SeedData. */
    val alreadyInApp: Boolean,
)

/** A BLM trailhead useful for rockhounds. */
data class BlmTrailhead(
    val name: String,
    val state: String,         // state code
    val region: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val addedAtMs: Long = 0L,            // epoch ms when added; 0 = legacy (no badge)
) {
    /** True when this trailhead was added within the last 7 days. */
    fun isNew(): Boolean {
        if (addedAtMs == 0L) return false
        val ageMs = System.currentTimeMillis() - addedAtMs
        return ageMs in 0..(SEVEN_DAYS_MS)
    }

    private companion object {
        const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000
    }
}

/** A BLM campground near rockhounding areas. */
data class BlmCampground(
    val name: String,
    val state: String,
    val region: String,
    val description: String,
    val feeInfo: String,
    val latitude: Double,
    val longitude: Double,
    val addedAtMs: Long = 0L,            // epoch ms when added; 0 = legacy (no badge)
) {
    /** True when this campground was added within the last 7 days. */
    fun isNew(): Boolean {
        if (addedAtMs == 0L) return false
        val ageMs = System.currentTimeMillis() - addedAtMs
        return ageMs in 0..(SEVEN_DAYS_MS)
    }

    private companion object {
        const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000
    }
}

/** An important-info section for the BLM guide. */
data class BlmInfoSection(
    val title: String,
    val icon: String,
    val accentHex: Long,
    val points: List<String>,
)

object BlmData {

    // ── Colors for state cards ──────────────────────────────────────────
    private const val CLAY: Long = 0xFFC97B4A    // warm clay-orange
    private const val SAGE: Long = 0xFF7BA868    // sage-green
    private const val SAND: Long = 0xFFD9B26A    // sandstone-gold
    private const val COPPER: Long = 0xFFB87333  // copper
    private const val TURQUOISE: Long = 0xFF5BB8A8
    private const val RUST: Long = 0xFFC66B3D
    private const val JADE: Long = 0xFF6B9E7E
    private const val AMBER: Long = 0xFFE0A040
    private const val STONE: Long = 0xFF8B7D6B
    private const val MOSS: Long = 0xFF5A8C5A

    val states: List<BlmState> = listOf(
        BlmState(
            code = "AZ",
            name = "Arizona",
            blmAcreage = "12.1 million acres",
            accentHex = CLAY,
            silhouetteEmoji = "🏜️",
            whatYouCanCollect = "Rocks, mineral specimens, semiprecious gemstones (fire agate, turquoise, quartz, peridot), petrified wood, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable amounts for personal use. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year. Cannot pool quotas.",
            toolRestrictions = "Hand tools only — no power equipment, explosives, or mechanized tools. Surface disturbance must be minimal and reclaimed.",
            permitNotes = "No permit required for casual non-commercial collecting. Commercial collecting requires a permit or contract from the local BLM field office.",
            specialNotes = "Two designated BLM rockhound areas: Black Hills and Round Mountain. Summer temperatures can exceed 110°F — collect October through April. Always check for active mining claims before collecting.",
            blmDigSites = listOf(
                BlmDigSite(
                    name = "Black Hills Rockhound Area",
                    region = "Safford, Arizona",
                    latitude = 32.8740,
                    longitude = -109.3950,
                    whatToFind = "Fire agate — a brown agate with internal play of red, green, and gold fire",
                    directions = "On the north side of AZ Route 191, ~18 miles north of Safford, just east of milepost 141.",
                    facilities = "No facilities. Dispersed camping allowed up to 14 days. No fees or permits required.",
                    feeInfo = "Free",
                    difficulty = "Moderate",
                    website = "https://www.blm.gov/visit/black-hills-rockhound-area",
                    alreadyInApp = false,
                ),
                BlmDigSite(
                    name = "Round Mountain Rockhound Area",
                    region = "Eastern Arizona / Western New Mexico",
                    latitude = 33.0720,
                    longitude = -109.0850,
                    whatToFind = "Rocks and mineral specimens — various collectible minerals",
                    directions = "From Highway 70 east of Safford, travel ~50 miles into New Mexico to just beyond milepost 5. Turn right onto the dirt access road and follow signs ~12 miles.",
                    facilities = "No facilities. Remote desert location — bring everything you need.",
                    feeInfo = "Free",
                    difficulty = "Hard",
                    website = "https://www.blm.gov/visit/round-mountain-rockhound-area",
                    alreadyInApp = false,
                ),
            ),
        ),
        BlmState(
            code = "CA",
            name = "California",
            blmAcreage = "15.2 million acres",
            accentHex = AMBER,
            silhouetteEmoji = "⛰️",
            whatYouCanCollect = "Mineral specimens, rocks, semiprecious gemstones (geodes, agate, epidote, garnet), petrified wood, and common invertebrate fossils (shellfish, corals, trilobites, crinoids) for personal, non-commercial use.",
            quantityLimits = "Up to 25 lbs per day plus one piece. Annual cap: 250 lbs/year. Groups cannot pool allotments to exceed 250 lbs.",
            toolRestrictions = "No heavy equipment, explosives, or motorized/mechanical aid. Metal detectors are allowed (exception: San Pedro Riparian NCA).",
            permitNotes = "No permit for casual collecting. Contact the California State Office or local field office for areas with special designations (national monuments, research natural areas, NCAs).",
            specialNotes = "National Monuments and designated areas may have additional restrictions. Collecting Indian/ancient artifacts and historic artifacts (rock art, bottles, old equipment) is prohibited.",
            blmDigSites = listOf(
                BlmDigSite(
                    name = "Chambless Rock Collecting Area",
                    region = "Chambless, California",
                    latitude = 34.6720,
                    longitude = -115.3520,
                    whatToFind = "Hematite, magnetite, green epidote, limestone, marble, tiny dark-red garnets, fossils in gray limestone",
                    directions = "From Needles, take I-40 west ~25 miles to Mountain Springs Rd exit. Go south on National Trails Highway west ~35 miles to BLM Route NS328 (just west of Chambless). Turn right and drive ~2 miles.",
                    facilities = "No facilities. Hiking, horseback riding, and wildlife viewing in the area.",
                    feeInfo = "Free",
                    difficulty = "Moderate",
                    website = "https://www.blm.gov/visit/chambless-rock-collecting-area",
                    alreadyInApp = false,
                ),
                BlmDigSite(
                    name = "Savahia Peak Rock Collecting Area",
                    region = "Near Vidal Junction, California",
                    latitude = 34.2150,
                    longitude = -114.4680,
                    whatToFind = "Bright white chalcedony",
                    directions = "From Needles, take US-95 south ~35 miles to BLM Route NS660 (Pyramid Peak Rd). Turn left and drive ~2 miles. Collecting area is on both sides of the route. Alternative: large wash north of US-62, 2 miles east of Vidal Junction.",
                    facilities = "No facilities.",
                    feeInfo = "Free",
                    difficulty = "Moderate",
                    website = "https://www.blm.gov/visit/savahia-peak-rock-collecting-area",
                    alreadyInApp = false,
                ),
                BlmDigSite(
                    name = "Wiley Well Geode Beds",
                    region = "Blythe, California",
                    latitude = 33.4670,
                    longitude = -114.8730,
                    whatToFind = "Geodes with quartz, amethyst, and citrine crystals; nodules",
                    directions = "South of Blythe via Wiley's Well Road. Accessible via BLM Route NS-773. The collecting area includes Hauser Geode Beds and Potato Patch.",
                    facilities = "Wiley's Well Campground nearby with tables, fire rings, pit toilets. Dispersed camping allowed.",
                    feeInfo = "Free collecting; campground has minimal fees",
                    difficulty = "Moderate",
                    website = "https://www.blm.gov/visit/wiley-well-district-geode-beds",
                    alreadyInApp = true,
                ),
            ),
        ),
        BlmState(
            code = "CO",
            name = "Colorado",
            blmAcreage = "8.3 million acres",
            accentHex = COPPER,
            silhouetteEmoji = "🏔️",
            whatYouCanCollect = "Rocks, mineral specimens, semiprecious gemstones (aquamarine, topaz, amazonite, rhodochrosite, quartz varieties), petrified wood, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable amounts for personal use. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year.",
            toolRestrictions = "Hand tools only. No power equipment or explosives. Minimal surface disturbance; backfill all holes.",
            permitNotes = "No permit for casual collecting. Some areas require permits — check with the local field office. Commercial collecting requires a permit.",
            specialNotes = "Colorado is famous for Mount Antero aquamarine and Pikes Peak amazonite. Many high-altitude collecting areas are accessible only in summer. Check for active mining claims, especially in mineral-rich districts.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "ID",
            name = "Idaho",
            blmAcreage = "11.8 million acres",
            accentHex = JADE,
            silhouetteEmoji = "🌊",
            whatYouCanCollect = "Rocks, mineral specimens (aquamarine, smoky quartz, malachite, gold), semiprecious gemstones, petrified wood, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable quantities for personal use. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year.",
            toolRestrictions = "Hand tools and metal detectors only. No explosives or motorized/mechanical equipment. Reclaim all disturbances.",
            permitNotes = "No permit for casual non-commercial collecting. Suction dredges (<5 inches) require an Idaho Dept. of Water Resources Recreational Dredge Mining Permit and EPA NPDES permit. Decorative stone (river cobbles, lava rock) requires a permit.",
            specialNotes = "Check land ownership before collecting — BLM field offices provide maps showing public vs. private land. Abandoned mines are hazardous; do not enter. Known areas include Boise County (aquamarine, gold), Lemhi County (malachite), Elmore County (smoky quartz).",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "MT",
            name = "Montana",
            blmAcreage = "8.0 million acres",
            accentHex = SAGE,
            silhouetteEmoji = "🌾",
            whatYouCanCollect = "Rocks, mineral specimens (sapphire, agate, quartz, garnet, gold), semiprecious gemstones, petrified wood, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable amounts for personal use. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year.",
            toolRestrictions = "Hand tools only. No power equipment or explosives. Minimal surface disturbance.",
            permitNotes = "No permit for casual collecting. Commercial collecting requires a permit. Some areas are withdrawn from collecting.",
            specialNotes = "Montana is world-famous for Yogo sapphies and Missouri River agates. Many collecting areas are in remote backcountry — carry supplies and tell someone your plans. Grizzly country: carry bear spray.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "NV",
            name = "Nevada",
            blmAcreage = "48.0 million acres",
            accentHex = STONE,
            silhouetteEmoji = "🏜️",
            whatYouCanCollect = "Rocks, mineral specimens, semiprecious gemstones (garnet, opal, turquoise, chalcedony, jasper), petrified wood, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable amounts for personal use. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year.",
            toolRestrictions = "Hand tools only. No power equipment or explosives. Minimal surface disturbance.",
            permitNotes = "No permit for casual collecting. Opal mines in the Virgin Valley area are mostly fee-dig private operations — verify land status before collecting.",
            specialNotes = "Nevada has more BLM land than any other state. Garnet Hill near Ely is a designated BLM public collecting area. Virgin Valley opal mines are mostly fee-dig. Always check for active mining claims — Nevada has the most mining claims of any state.",
            blmDigSites = listOf(
                BlmDigSite(
                    name = "Garnet Hill",
                    region = "Ely, Nevada",
                    latitude = 39.2475,
                    longitude = -114.8891,
                    whatToFind = "Ruby-red almandine garnets in rhyolite rock",
                    directions = "From Ely, travel west on US-50. 1 mile north of the Ruth turnoff, turn right onto the graded access road. Drive 3.1 miles to the parking area.",
                    facilities = "Four picnic sites with grills, accessible restroom, group BBQ area, two informal tent/camper areas. No water. Elevation 7,000 ft.",
                    feeInfo = "Free",
                    difficulty = "Moderate",
                    website = "https://www.blm.gov/visit/garnet-hill",
                    alreadyInApp = true,
                ),
            ),
        ),
        BlmState(
            code = "NM",
            name = "New Mexico",
            blmAcreage = "13.4 million acres",
            accentHex = RUST,
            silhouetteEmoji = "🪨",
            whatYouCanCollect = "Rocks, mineral specimens (turquoise, quartz, beryl, fluorite), semiprecious gemstones, petrified wood, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable amounts for personal use. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year.",
            toolRestrictions = "Hand tools only. No power equipment or explosives. Minimal surface disturbance.",
            permitNotes = "No permit for casual collecting. Commercial collecting requires a permit. Rockhound State Park near Deming is a state park (not BLM) with its own rules.",
            specialNotes = "The Rockhound State Park near Deming allows collecting but is state-managed, not BLM. Many BLM areas in southern New Mexico have thundereggs and geodes. Check for active mining claims.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "OR",
            name = "Oregon",
            blmAcreage = "16.0 million acres",
            accentHex = TURQUOISE,
            silhouetteEmoji = "🌋",
            whatYouCanCollect = "Rocks, mineral specimens, semiprecious gemstones (sunstone, obsidian, thundereggs, agate, jasper), petrified wood, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable daily amount: fits in a car trunk, partial pickup load, under 250 lbs. Over 250 lbs or commercial use requires a permit.",
            toolRestrictions = "Hand tools only for casual use. No explosives or power equipment. Surface disturbance must be minimal and reclaimed.",
            permitNotes = "No fee or permit required for casual collecting. Commercial quantities or power equipment require a BLM permit, notice, or Plan of Operations.",
            specialNotes = "Oregon has two famous BLM collecting areas: Sunstone Collection Area (Lakeview District) and Glass Buttes obsidian area (Prineville District). Oregon is the only state with a designated state rock (thunderegg). Rock stockpiles in BLM quarries cannot be removed.",
            blmDigSites = listOf(
                BlmDigSite(
                    name = "Oregon Sunstone Public Collection Area",
                    region = "Plush, Oregon",
                    latitude = 42.7234,
                    longitude = -119.8607,
                    whatToFind = "Oregon sunstone — a translucent feldspar gemstone in shades of clear, yellow, red, green, and schiller (copper inclusion) varieties",
                    directions = "In the Rabbit Basin, south-central Oregon high desert. From Plush, follow signs via Hogback Road and BLM Roads 6155/6115/6195 to the entrance.",
                    facilities = "Pit toilet, picnic tables, shade structure. Camping allowed anywhere within the collection area.",
                    feeInfo = "Free for non-commercial collecting",
                    difficulty = "Moderate",
                    website = "https://www.blm.gov/visit/sunstone-collection-area",
                    alreadyInApp = true,
                ),
                BlmDigSite(
                    name = "Glass Buttes Obsidian Area",
                    region = "Lake County, Oregon",
                    latitude = 43.7460,
                    longitude = -120.0920,
                    whatToFind = "Obsidian — black, mahogany, fire, rainbow, and snowflake varieties",
                    directions = "In the Prineville District, off Highway 20 between Bend and Burns. Look for BLM signs marking the Glass Buttes area.",
                    facilities = "No facilities. Dispersed camping permitted on BLM land.",
                    feeInfo = "Free",
                    difficulty = "Easy",
                    website = "https://www.blm.gov/programs/recreation/rockhounding",
                    alreadyInApp = true,
                ),
            ),
        ),
        BlmState(
            code = "UT",
            name = "Utah",
            blmAcreage = "22.9 million acres",
            accentHex = SAND,
            silhouetteEmoji = "⛰️",
            whatYouCanCollect = "Rocks, mineral specimens (topaz, geodes, agate, variscite, bixbite/red beryl), semiprecious gemstones, petrified wood, and common invertebrate fossils (trilobites) for personal, non-commercial use.",
            quantityLimits = "Reasonable amounts for personal use. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year.",
            toolRestrictions = "Hand tools only. No power equipment or explosives. Tunneling is unsafe and not permitted at geode beds. Minimal surface disturbance.",
            permitNotes = "No permit for casual collecting. Some trilobite quarries near Antelope Springs are fee-dig private operations — verify land status. Commercial collecting requires a permit.",
            specialNotes = "Utah has two famous BLM rockhound areas: Topaz Mountain and Dugway Geode Beds. The Fillmore Field Office manages both. Trilobite collecting at Antelope Springs is nearby. Dugway geode beds require driving on unimproved roads — check conditions.",
            blmDigSites = listOf(
                BlmDigSite(
                    name = "Topaz Mountain Rockhound Recreation Area",
                    region = "Delta, Utah",
                    latitude = 39.7520,
                    longitude = -113.2840,
                    whatToFind = "Topaz crystals — Utah's state gemstone, plus other minerals like bixbyite, pseudobrookite, and garnet",
                    directions = "55 miles west of Delta via US Highway 6 and paved county road. In the Fillmore Field Office area.",
                    facilities = "No facilities. Dispersed camping permitted.",
                    feeInfo = "Free",
                    difficulty = "Moderate",
                    website = "https://www.blm.gov/visit/topaz-mountain-rockhound-recreation-area",
                    alreadyInApp = true,
                ),
                BlmDigSite(
                    name = "Dugway Geode Beds",
                    region = "Dugway, Utah",
                    latitude = 40.2910,
                    longitude = -113.1240,
                    whatToFind = "Geodes — often quartz varieties including clear quartz, amethyst, and rose quartz",
                    directions = "From Salt Lake City: I-80 west to exit 99 (SH-36). After ~33 miles, turn right onto the old Pony Express Rd at Faust. ~50 miles along Pony Express Trail/Simpson Springs Rd to a signed turnoff, then ~2 miles on unimproved road.",
                    facilities = "No on-site facilities. Contact Fillmore Field Office for info. Dispersed camping allowed.",
                    feeInfo = "Free",
                    difficulty = "Hard",
                    website = "https://www.blm.gov/visit/dugway-geode-beds",
                    alreadyInApp = true,
                ),
            ),
        ),
        BlmState(
            code = "WY",
            name = "Wyoming",
            blmAcreage = "18.0 million acres",
            accentHex = MOSS,
            silhouetteEmoji = "🦬",
            whatYouCanCollect = "Rocks, mineral specimens (jade, agate, quartz, pyrite, gold), semiprecious gemstones, petrified wood, and common invertebrate and plant fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable amounts — personal hobby collection or home display. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year. No combining quotas.",
            toolRestrictions = "Only non-powered hand tools. No explosives or powered equipment. All digging must be reclaimed. Off-road vehicle use may be restricted.",
            permitNotes = "No permit for casual non-commercial collecting. Permits required for more than 250 lbs/year of petrified wood or commercial use. Contact local BLM field office for area-specific restrictions.",
            specialNotes = "Wyoming is famous for nephrite jade — the state gemstone. Vertebrate fossils (bones, teeth, footprints) cannot be collected. Cave resources are federally protected. Abandoned Mine Lands should not be entered. Obtain BLM maps from field offices to identify open areas.",
            blmDigSites = emptyList(),
        ),
        // States with BLM land but less rockhounding-specific data
        BlmState(
            code = "AK",
            name = "Alaska",
            blmAcreage = "73.0 million acres",
            accentHex = JADE,
            silhouetteEmoji = "🏔️",
            whatYouCanCollect = "Rocks, mineral specimens (gold, jade, garnet), semiprecious gemstones, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable amounts for personal use. Petrified wood: up to 25 lbs/day plus one piece, max 250 lbs/year.",
            toolRestrictions = "Hand tools only. No power equipment or explosives.",
            permitNotes = "No permit for casual collecting. Recreational gold panning is allowed on most BLM land. Commercial operations require permits.",
            specialNotes = "Alaska has vast BLM holdings. Jade Mountain is famous for nephrite jade. Recreational gold panning is popular on many streams. Many areas are extremely remote — plan for wilderness conditions.",
            blmDigSites = emptyList(),
        ),
    )

    // ── States without significant BLM rockhounding data ─────────────────
    // These are states with minimal or no BLM-managed land relevant to rockhounding.
    // They're included for completeness with general federal collecting guidance.
    val otherStates: List<BlmState> = listOf(
        BlmState(
            code = "AL", name = "Alabama", blmAcreage = "Minimal acreage", accentHex = SAGE, silhouetteEmoji = "🪨",
            whatYouCanCollect = "Minimal BLM land. Rocks and minerals on other public lands — check state and county park rules.",
            quantityLimits = "Varies by land management agency.", toolRestrictions = "Hand tools only.",
            permitNotes = "Contact the local land management office.", specialNotes = "Alabama has very little BLM land. State parks may allow surface collecting with permits.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "AR", name = "Arkansas", blmAcreage = "Minimal acreage", accentHex = CLAY, silhouetteEmoji = "💎",
            whatYouCanCollect = "Minimal BLM land. Most collecting is on private fee-dig sites or state parks (Crater of Diamonds).",
            quantityLimits = "Varies by site.", toolRestrictions = "Hand tools only.",
            permitNotes = "Crater of Diamonds State Park has its own rules and entry fee.", specialNotes = "Arkansas is famous for quartz crystals, but most collecting is on private or state land.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "CT", name = "Connecticut", blmAcreage = "No acreage", accentHex = SAGE, silhouetteEmoji = "🌳",
            whatYouCanCollect = "No BLM land. State parks and private quarries are the main collecting venues.",
            quantityLimits = "N/A on BLM land.", toolRestrictions = "N/A",
            permitNotes = "Check state park regulations.", specialNotes = "No BLM-managed land in Connecticut.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "DE", name = "Delaware", blmAcreage = "No acreage", accentHex = STONE, silhouetteEmoji = "🏖️",
            whatYouCanCollect = "No BLM land. Beach collecting of fossils and minerals is possible on state lands.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check state regulations.", specialNotes = "No BLM-managed land in Delaware.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "FL", name = "Florida", blmAcreage = "Minimal acreage", accentHex = MOSS, silhouetteEmoji = "🐊",
            whatYouCanCollect = "Minimal BLM land. Fossil collecting on state lands requires permits.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Florida requires a fossil collecting permit on state lands.", specialNotes = "Florida is famous for fossilized shark teeth and manatee bones, mostly on state or private land.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "GA", name = "Georgia", blmAcreage = "Minimal acreage", accentHex = RUST, silhouetteEmoji = "🍑",
            whatYouCanCollect = "Minimal BLM land. State parks and private sites are the main venues.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state park regulations.", specialNotes = "Georgia is famous for staurolite (fairy crosses) — the state mineral. Most collecting is on private land.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "HI", name = "Hawaii", blmAcreage = "Minimal acreage", accentHex = JADE, silhouetteEmoji = "🌋",
            whatYouCanCollect = "Minimal BLM land. Collecting volcanic rocks is restricted in many areas to protect natural resources.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check with Hawaii Volcanoes National Park and state regulations.", specialNotes = "Taking lava rocks from Hawaii is considered bad luck (Pele's curse) and is prohibited in national parks.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "IA", name = "Iowa", blmAcreage = "Minimal acreage", accentHex = STONE, silhouetteEmoji = "🪨",
            whatYouCanCollect = "Minimal BLM land. Keokuk Geode Beds are the famous collecting area (mostly on private land).",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check private quarry rules.", specialNotes = "Iowa is famous for Keokuk geodes — most collecting is at fee-dig private quarries.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "IL", name = "Illinois", blmAcreage = "Minimal acreage", accentHex = SAGE, silhouetteEmoji = "🌾",
            whatYouCanCollect = "Minimal BLM land. Fluorite mining district in southern Illinois is the main draw.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check private mine rules.", specialNotes = "Southern Illinois was a major fluorite producer. Most collecting is at private fee-dig sites.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "IN", name = "Indiana", blmAcreage = "Minimal acreage", accentHex = AMBER, silhouetteEmoji = "🪨",
            whatYouCanCollect = "Minimal BLM land. Fossil collecting in state parks and on private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state park regulations.", specialNotes = "Indiana has fossil collecting at Falls of the Ohio State Park (fossil beds).",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "KS", name = "Kansas", blmAcreage = "Minimal acreage", accentHex = SAND, silhouetteEmoji = "🌻",
            whatYouCanCollect = "Minimal BLM land. Fossils and minerals on state and private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state regulations.", specialNotes = "Kansas is known for the Niobrara Chalk fossils and Post Rock limestone.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "KY", name = "Kentucky", blmAcreage = "Minimal acreage", accentHex = MOSS, silhouetteEmoji = "🪨",
            whatYouCanCollect = "Minimal BLM land. Geodes and fossils on private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check private land rules.", specialNotes = "Kentucky is known for geodes in the Knobs region and fluorite in the western part of the state.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "LA", name = "Louisiana", blmAcreage = "Minimal acreage", accentHex = CLAY, silhouetteEmoji = "🦐",
            whatYouCanCollect = "Minimal BLM land. Fossil collecting on state and private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state regulations.", specialNotes = "Louisiana is known for Vernon Parish opal and petrified palm wood — the state fossil.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "ME", name = "Maine", blmAcreage = "No acreage", accentHex = SAGE, silhouetteEmoji = "🌲",
            whatYouCanCollect = "No BLM land. Maine's famous pegmatite minerals are on private and state land.",
            quantityLimits = "N/A on BLM land.", toolRestrictions = "N/A",
            permitNotes = "Check with private quarries.", specialNotes = "Maine is world-famous for tourmaline, aquamarine, and other pegmatite minerals. Most collecting is at fee-dig quarries.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "MD", name = "Maryland", blmAcreage = "No acreage", accentHex = STONE, silhouetteEmoji = "🦀",
            whatYouCanCollect = "No BLM land. State parks and private sites only.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check state park rules.", specialNotes = "No BLM-managed land in Maryland.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "MA", name = "Massachusetts", blmAcreage = "No acreage", accentHex = JADE, silhouetteEmoji = "🍁",
            whatYouCanCollect = "No BLM land. State lands and private quarries.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check state regulations.", specialNotes = "No BLM-managed land in Massachusetts.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "MI", name = "Michigan", blmAcreage = "Minimal acreage", accentHex = COPPER, silhouetteEmoji = "⛵",
            whatYouCanCollect = "Minimal BLM land. Lake Superior agates and copper minerals on state and private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state park and private mine rules.", specialNotes = "Michigan's Keweenaw Peninsula is famous for native copper. Pictured Rocks and Lake Superior beaches are great for agates.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "MN", name = "Minnesota", blmAcreage = "Minimal acreage", accentHex = SAGE, silhouetteEmoji = "🪨",
            whatYouCanCollect = "Minimal BLM land. Lake Superior agates on state and private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state regulations.", specialNotes = "Minnesota's state gemstone is the Lake Superior agate. Beach collecting along the North Shore is popular.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "MS", name = "Mississippi", blmAcreage = "Minimal acreage", accentHex = CLAY, silhouetteEmoji = "🪨",
            whatYouCanCollect = "Minimal BLM land. Fossils and petrified wood on state and private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state regulations.", specialNotes = "Mississippi has petrified wood and marine fossils. The state fossil is the prehistoric whale Zygorhiza.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "MO", name = "Missouri", blmAcreage = "Minimal acreage", accentHex = STONE, silhouetteEmoji = "🪨",
            whatYouCanCollect = "Minimal BLM land. Missouri is famous for galena, calcite, and geodes.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check private mine and state park rules.", specialNotes = "Missouri was a major lead/zinc mining state. The Viburnum Trend and old Tri-State district are famous for collector minerals.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "NE", name = "Nebraska", blmAcreage = "Minimal acreage", accentHex = SAND, silhouetteEmoji = "🌾",
            whatYouCanCollect = "Minimal BLM land. Fossils and agates on state and private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state regulations.", specialNotes = "Nebraska is known for the Ashfall Fossil Beds and Blue River agates.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "NJ", name = "New Jersey", blmAcreage = "No acreage", accentHex = RUST, silhouetteEmoji = "🪨",
            whatYouCanCollect = "No BLM land. Famous mineral localities are on private land.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check with private sites.", specialNotes = "New Jersey's Franklin and Sterling Hill mines are world-famous for fluorescent minerals — now museums and fee-dig sites.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "NY", name = "New York", blmAcreage = "No acreage", accentHex = SAGE, silhouetteEmoji = "🗽",
            whatYouCanCollect = "No BLM land. State lands and private quarries.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check state park and private quarry rules.", specialNotes = "New York is famous for Herkimer diamonds (doubly-terminated quartz), garnets, and wollastonite.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "NC", name = "North Carolina", blmAcreage = "No acreage", accentHex = JADE, silhouetteEmoji = "⛰️",
            whatYouCanCollect = "No BLM land. Famous gem localities are on private fee-dig sites.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check with private mines.", specialNotes = "North Carolina is famous for emeralds (Hiddenite), sapphires, rubies, and mica. Most collecting is at fee-dig mines.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "ND", name = "North Dakota", blmAcreage = "Minimal acreage", accentHex = MOSS, silhouetteEmoji = "🌾",
            whatYouCanCollect = "Minimal BLM land. Fossils and petrified wood on state and private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state regulations.", specialNotes = "North Dakota is known for petrified wood and dinosaur fossils. The state fossil is Teredo-bored petrified wood.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "OH", name = "Ohio", blmAcreage = "No acreage", accentHex = CLAY, silhouetteEmoji = "🪨",
            whatYouCanCollect = "No BLM land. Fossils and minerals on state and private land.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check state park rules.", specialNotes = "Ohio is famous for Flint Ridge flint and Ordovician fossils. The state fossil is the trilobite Isotelus.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "OK", name = "Oklahoma", blmAcreage = "Minimal acreage", accentHex = SAND, silhouetteEmoji = "🪨",
            whatYouCanCollect = "Minimal BLM land. Barite roses and selenite crystals on state and private land.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check state and private land rules.", specialNotes = "Oklahoma is famous for barite rose rocks (the state rock) and Great Salt Plains selenite crystals.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "PA", name = "Pennsylvania", blmAcreage = "No acreage", accentHex = COPPER, silhouetteEmoji = "🪨",
            whatYouCanCollect = "No BLM land. Minerals and fossils on state and private land.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check state park rules.", specialNotes = "Pennsylvania has a rich mining history. Famous for pyrite, quartz, and historical mineral localities.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "RI", name = "Rhode Island", blmAcreage = "No acreage", accentHex = SAGE, silhouetteEmoji = "🪨",
            whatYouCanCollect = "No BLM land.", quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "N/A", specialNotes = "No BLM-managed land in Rhode Island.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "SC", name = "South Carolina", blmAcreage = "No acreage", accentHex = AMBER, silhouetteEmoji = "🌴",
            whatYouCanCollect = "No BLM land.", quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "N/A", specialNotes = "No BLM-managed land in South Carolina.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "SD", name = "South Dakota", blmAcreage = "271,000 acres", accentHex = STONE, silhouetteEmoji = "⛰️",
            whatYouCanCollect = "Rocks, mineral specimens (rose quartz, mica, beryl, gold), and common fossils for personal use.",
            quantityLimits = "Reasonable amounts.", toolRestrictions = "Hand tools only.",
            permitNotes = "Check with the South Dakota Field Office.", specialNotes = "South Dakota is famous for Black Hills minerals — rose quartz, tourmaline, and gold. The Black Hills have both BLM and Forest Service land.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "TN", name = "Tennessee", blmAcreage = "No acreage", accentHex = MOSS, silhouetteEmoji = "🪨",
            whatYouCanCollect = "No BLM land. Fluorite and fossils on private land.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check private mine rules.", specialNotes = "Tennessee is part of the old fluorite mining district. The state mineral is calcite.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "TX", name = "Texas", blmAcreage = "Minimal acreage", accentHex = RUST, silhouetteEmoji = "🤠",
            whatYouCanCollect = "Minimal BLM land. Most collecting is on private ranches (with permission) or state parks.",
            quantityLimits = "Varies.", toolRestrictions = "Hand tools only.",
            permitNotes = "Texas has very little BLM land. Always get permission for private land.", specialNotes = "Texas is known for topaz (Mason County), petrified wood, and agates. Most collecting requires private land access.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "VA", name = "Virginia", blmAcreage = "No acreage", accentHex = JADE, silhouetteEmoji = "🏔️",
            whatYouCanCollect = "No BLM land. Minerals and fossils on state and private land.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check state park rules.", specialNotes = "Virginia is famous for unakite, staurolite, and the Morefield Mine (amazonite, topaz). State mineral is gold, state rock is granite.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "VT", name = "Vermont", blmAcreage = "No acreage", accentHex = SAGE, silhouetteEmoji = "🍁",
            whatYouCanCollect = "No BLM land. Minerals on private land.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check private land rules.", specialNotes = "Vermont is known for asbestos, talc, and garnet (state mineral). No BLM-managed land.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "WA", name = "Washington", blmAcreage = "0.4 million acres", accentHex = TURQUOISE, silhouetteEmoji = "🌲",
            whatYouCanCollect = "Rocks, mineral specimens, semiprecious gemstones, and common invertebrate fossils for personal, non-commercial use.",
            quantityLimits = "Reasonable daily amount under 250 lbs. Over 250 lbs or commercial use requires a permit.",
            toolRestrictions = "Hand tools only. No explosives or power equipment.",
            permitNotes = "No permit for casual collecting. Check with the Spokane District office.", specialNotes = "Washington is famous for Mount St. Helens ash and obsidian-like materials, petrified wood (state gem), and thundereggs.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "WV", name = "West Virginia", blmAcreage = "No acreage", accentHex = MOSS, silhouetteEmoji = "⛰️",
            whatYouCanCollect = "No BLM land.", quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "N/A", specialNotes = "No BLM-managed land in West Virginia. State fossil is the giant ground sloth.",
            blmDigSites = emptyList(),
        ),
        BlmState(
            code = "WI", name = "Wisconsin", blmAcreage = "No acreage", accentHex = SAGE, silhouetteEmoji = "🪨",
            whatYouCanCollect = "No BLM land. Minerals and fossils on state and private land.",
            quantityLimits = "N/A", toolRestrictions = "N/A",
            permitNotes = "Check state park rules.", specialNotes = "Wisconsin is famous for lead/zinc minerals (galena, sphalerite) and Lake Superior agates. State mineral is galena.",
            blmDigSites = emptyList(),
        ),
    )

    /** All states combined, sorted alphabetically by name. */
    val allStates: List<BlmState> by lazy {
        (states + otherStates).sortedBy { it.name }
    }

    /** All BLM-sourced dig sites across all states. */
    val allBlmDigSites: List<BlmDigSite> by lazy {
        states.flatMap { it.blmDigSites }
    }

    /** New BLM dig sites not already in the app's SeedData. */
    val newDigSites: List<BlmDigSite> by lazy {
        allBlmDigSites.filter { !it.alreadyInApp }
    }

    // ── Trailheads ──────────────────────────────────────────────────────
    val trailheads: List<BlmTrailhead> = listOf(
        // Arizona
        BlmTrailhead("Black Hills Trailhead", "AZ", "Safford, Arizona", "Access point for the Black Hills Rockhound Area — fire agate collecting. Trailhead leads to the designated BLM digging zone.", 32.8740, -109.3950),
        // California
        BlmTrailhead("Chambless Trailhead", "CA", "Chambless, California", "Access to hematite, magnetite, epidote, and garnet collecting area along BLM Route NS328.", 34.6720, -115.3520),
        BlmTrailhead("Wiley Well Trailhead", "CA", "Blythe, California", "Access to geode beds including Hauser Geode Beds and Potato Patch. Near Wiley's Well Campground.", 33.4670, -114.8730),
        BlmTrailhead("Savahia Peak Trailhead", "CA", "Vidal Junction, California", "Access to bright white chalcedony collecting area along BLM Route NS660.", 34.2150, -114.4680),
        // Colorado
        BlmTrailhead("Mount Antero Trailhead", "CO", "Buena Vista, Colorado", "Access to high-altitude aquamarine and phenakite collecting on Mount Antero. Trailhead at Clear Creek Reservoir — 4WD road to 13,000 ft. Best July through September.", 38.6740, -106.2470),
        BlmTrailhead("Crystal Peak Trailhead", "CO", "Florissant, Colorado", "Access to amazonite, smoky quartz, and topaz collecting in the Pikes Peak granite. Popular BLM/Forest Service area near Florissant Fossil Beds.", 38.9140, -105.2820),
        // Idaho
        BlmTrailhead("Emerald Creek Trailhead", "ID", "Clarkia, Idaho", "Access to star garnet collecting area in the Emerald Creek drainage. One of only two places in the world where star garnets are found. Permit required from Potlatch (Panhandle) BLM.", 47.0120, -116.2480),
        BlmTrailhead("Mores Creek Trailhead", "ID", "Boise County, Idaho", "Access to smoky quartz and aquamarine collecting in the Boise Basin area. BLM land along Mores Creek.", 43.9280, -115.8820),
        // Montana
        BlmTrailhead("Rock Creek Trailhead", "MT", "Philipsburg, Montana", "Access to sapphire gravels along Rock Creek. BLM and USFS land — sapphire screening is popular. Look for the Gem Mountain sapphire area nearby.", 46.3280, -113.4520),
        BlmTrailhead("Missouri River Trailhead", "MT", "Helena, Montana", "Access to agate and sapphire gravels along the Missouri River. BLM land near Canyon Ferry and Hauser Lake.", 46.5960, -111.9890),
        // Nevada
        BlmTrailhead("Garnet Hill Trailhead", "NV", "Ely, Nevada", "Trailhead at 7,000 ft elevation with picnic sites. Access to garnet collecting on rhyolite ridge. Four picnic sites with grills and accessible restroom.", 39.2475, -114.8891),
        // New Mexico
        BlmTrailhead("Rockhound State Park Area Trailhead", "NM", "Deming, New Mexico", "While Rockhound State Park is state-managed, surrounding BLM land offers thunderegg and geode collecting. Access via BLM roads south of Deming.", 32.3180, -108.2520),
        // Oregon
        BlmTrailhead("Sunstone Area Trailhead", "OR", "Plush, Oregon", "Access to the Oregon Sunstone Public Collection Area in the Rabbit Basin. Pit toilet, picnic tables, and shade structure available.", 42.7234, -119.8607),
        BlmTrailhead("Glass Buttes Trailhead", "OR", "Lake County, Oregon", "Access to the Glass Buttes obsidian collecting area off Highway 20. Multiple obsidian varieties available at the surface.", 43.7460, -120.0920),
        // South Dakota
        BlmTrailhead("Black Hills Trailhead", "SD", "Custer, South Dakota", "Access to rose quartz and mica collecting areas in the Black Hills. BLM and Forest Service land near Custer. Look for the famous Fairburn agate gravels.", 43.7670, -103.5980),
        // Utah
        BlmTrailhead("Topaz Mountain Trailhead", "UT", "Delta, Utah", "55 miles west of Delta. Trailhead provides access to topaz collecting area in the Thomas Range. Dispersed camping permitted.", 39.7520, -113.2840),
        BlmTrailhead("Dugway Geode Beds Trailhead", "UT", "Dugway, Utah", "Remote trailhead via the Pony Express Trail. Access to geode collecting beds. Requires high-clearance vehicle for unimproved roads.", 40.2910, -113.1240),
        // Washington
        BlmTrailhead("Saddle Mountain Trailhead", "WA", "Mattawa, Washington", "Access to petrified wood and agate collecting on BLM land near Saddle Mountain. Popular spring and fall collecting.", 46.6920, -119.5420),
        // Wyoming
        BlmTrailhead("Jade Mountain Trailhead", "WY", "Lander, Wyoming", "Access to nephrite jade collecting areas in the Granite Mountains. BLM land along the Sweetwater River. Look for dark green to black jade boulders.", 42.8330, -107.5820),
        BlmTrailhead("Sweetwater River Trailhead", "WY", "Jeffrey City, Wyoming", "Access to jade and agate gravels along the Sweetwater River. BLM land — look for jade in alluvial deposits.", 42.4980, -107.8260),
        // Alaska
        BlmTrailhead("Jade Mountain Trailhead", "AK", "Kobuk River area, Alaska", "Remote access to nephrite jade deposits in the Jade Mountains. Extremely remote — access by bush plane or boat. Not for casual collectors.", 67.0830, -159.5830),
        BlmTrailhead("Resurrection Creek Trailhead", "AK", "Hope, Alaska", "Access to recreational gold panning on BLM land along Resurrection Creek. Popular day-use area on the Kenai Peninsula.", 60.9170, -149.5330),
        // Alabama
        BlmTrailhead("Walls of Jericho Trailhead", "AL", "Hytop, Alabama", "Popular hiking trail through Jackson County canyon with creek-bed rock and mineral collecting. Managed by Alabama State Lands, not BLM. Quartz, chert, and sandstone formations.", 34.9450, -86.0630),
        // Arkansas (AR)
        BlmTrailhead("Crater of Diamonds Trailhead", "AR", "Murfreesboro, Arkansas", "Access to the only public diamond mine in the world. State park fee-dig — keep what you find. Also find amethyst, garnet, jasper, and agate.", 34.6650, -93.7050),
        BlmTrailhead("Wegner Quartz Crystal Mines Trailhead", "AR", "Mount Ida, Arkansas", "Popular fee-dig quartz crystal collecting in the Ouachita Mountains. Private operation — access by appointment. World-class clear and smoky quartz.", 34.5530, -93.5940),
        // California Desert (moved from CA3 to CA)
        BlmTrailhead("Afton Canyon Trailhead", "CA", "Barstow, California", "BLM-managed canyon along the Mojave River. Access to travertine, calcite, and various minerals. Known as the 'Grand Canyon of the Mojave.' Popular year-round.", 35.1400, -116.3950),
        // Connecticut
        BlmTrailhead("Strickland Quarry Access", "CT", "Portland, Connecticut", "Historic mineral collecting site famous for tourmaline, beryl, and garnet. Private quarry — access by permission or organized mineral club field trips only.", 41.5650, -72.6390),
        // Delaware
        BlmTrailhead("Cape Henlopen Trailhead", "DE", "Lewes, Delaware", "Beach collecting for fossils and quartz pebbles along the Atlantic shore. State park, not BLM. Look for fossilized shell fragments in the sand.", 38.7800, -75.1070),
        // Florida
        BlmTrailhead("Peace River Trailhead", "FL", "Arcadia, Florida", "Popular fossil hunting area — megalodon teeth, ray plates, and mammal bones. Private and state lands — access via canoe outfitters. Florida fossil permit required.", 27.2950, -81.8640),
        // Georgia
        BlmTrailhead("Graves Mountain Access", "GA", "Lincolnton, Georgia", "Famous mineral locality — rutile, lazulite, pyrophyllite, and kyanite. Annual mineral society digs open to the public. Managed by Georgia DNR.", 33.8330, -82.4620),
        // Hawaii
        BlmTrailhead("Papakolea Green Sand Beach Trailhead", "HI", "South Point, Hawaii", "Hiking access to one of the world's only green sand beaches — olivine-rich sands. Collecting sand is prohibited; viewing only. Managed by Hawaii County.", 19.0000, -155.6450),
        // Iowa
        BlmTrailhead("Keokuk Geode Beds Trailhead", "IA", "Keokuk, Iowa", "Access to the famous Keokuk geode beds along the Des Moines River. Private quarries offer fee-dig access — geodes filled with quartz, chalcedony, and calcite.", 40.3960, -91.3850),
        // Illinois
        BlmTrailhead("Cave-in-Rock Fluorite District Trailhead", "IL", "Cave-in-Rock, Illinois", "Historic fluorite mining district. Private fee-dig mines offer fluorite, calcite, barite, and galena. Southern Illinois was once the largest fluorite producer in the U.S.", 37.4660, -88.1670),
        // Indiana
        BlmTrailhead("Falls of the Ohio Trailhead", "IN", "Clarksville, Indiana", "Exposed Devonian fossil beds on the Ohio River — corals, brachiopods, and crinoids. State park interpretive center; collecting is restricted to certain areas.", 38.2810, -85.7640),
        // Kansas
        BlmTrailhead("Monument Rocks Trailhead", "KS", "Gove County, Kansas", "Niobrara Chalk formations with marine fossils — shark teeth, fish vertebrae, and invertebrates. BLM land and private land mixed — verify boundaries. Smoky Hill Chalk badlands.", 38.7960, -100.7720),
        // Kentucky
        BlmTrailhead("Clement Mineral District Trailhead", "KY", "Marion, Kentucky", "Historic fluorspar district in western Kentucky. Private fee-dig mines offer fluorite, calcite, and sphalerite specimens. Visit the Ben E. Clement Mineral Museum for guidance.", 37.3320, -88.0780),
        // Louisiana
        BlmTrailhead("Vernon Parish Opal Area Trailhead", "LA", "Leesville, Louisiana", "Access to Louisiana opal (quartz-opalite) collecting area in the Kisatchie National Forest. USFS-managed, not BLM. Also petrified palm wood — the state fossil.", 31.1370, -93.2270),
        // Maine
        BlmTrailhead("Mount Mica Access", "ME", "Paris, Maine", "Historic tourmaline and beryl locality — the first gem tourmaline discovered in North America. Private mine — fee-dig by appointment. Western Maine pegmatite belt.", 44.2240, -70.5590),
        BlmTrailhead("Newry Mineral Area Trailhead", "ME", "Newry, Maine", "World-famous pegmatite minerals — tourmaline, beryl, apatite, and lepidolite. Private quarries with fee-dig access. Annual Maine Mineral & Gem Show nearby.", 44.3270, -70.8420),
        // Maryland
        BlmTrailhead("Soldier's Delight Trailhead", "MD", "Owings Mills, Maryland", "Serpentinite barrens with chromite and serpentine minerals. Maryland Environmental Trust area — collecting limited to surface finds. Historic chromite mining district.", 39.4750, -76.8460),
        // Massachusetts
        BlmTrailhead("Chester Emery Mines Access", "MA", "Chester, Massachusetts", "Historic emery mine district — emery, corundum, and diaspore. Private land and state forest — access via mineral club field trips. Hampden County.", 42.3970, -72.9880),
        // Michigan
        BlmTrailhead("Keweenaw Peninsula Trailhead", "MI", "Calumet, Michigan", "World-famous native copper collecting area. Old mine dumps and road cuts yield copper, calcite, and prehnite. State and private land mixed — verify access.", 47.2420, -88.4460),
        // Minnesota
        BlmTrailhead("North Shore Agate Trailhead", "MN", "Two Harbors, Minnesota", "Beach collecting for Lake Superior agates along the North Shore. State parks and road cuts — the Lake Superior agate is the Minnesota state gemstone.", 47.0210, -91.6830),
        // Mississippi
        BlmTrailhead("Mississippi Petrified Forest Trailhead", "MS", "Flora, Mississippi", "Petrified wood collecting area. Private park with petrified logs on display; surrounding state and private lands may yield specimens. State fossil is the prehistoric whale Zygorhiza.", 32.3270, -90.3120),
        // Missouri
        BlmTrailhead("Viburnum Trend Access", "MO", "Viburnum, Missouri", "Active mining district famous for galena, calcite, chalcopyrite, and marcasite specimens. Private mine dumps — access via mineral clubs or fee-dig events.", 37.7140, -91.1880),
        // Nebraska
        BlmTrailhead("Blue River Agate Trailhead", "NE", "Seward, Nebraska", "Creek-bed collecting for Blue River agates and Sioux quartzite cobbles. State and county land — verify access. Nebraska is also known for Prairie Agate.", 40.9070, -97.1200),
        // New Jersey
        BlmTrailhead("Franklin Mineral Museum Access", "NJ", "Franklin, New Jersey", "World capital of fluorescent minerals — willemite, calcite, franklinite. Fee-dig on the old mine dumps behind the museum. Over 350 mineral species found here.", 41.1260, -74.5860),
        // New York
        BlmTrailhead("Herkimer Diamond Trailhead", "NY", "Middleville, New York", "Access to Herkimer diamond (doubly-terminated quartz) collecting. Private fee-dig mines — Ace of Diamonds and Herkimer Diamond Mines. Also garnet nearby in the Adirondacks.", 43.2760, -74.8640),
        // North Carolina
        BlmTrailhead("Emerald Hollow Mine Trailhead", "NC", "Hiddenite, North Carolina", "Fee-dig emerald, sapphire, ruby, and aquamarine collecting. The only emerald mine in the U.S. open to the public. Private operation in Alexander County.", 35.9050, -81.0920),
        // North Dakota
        BlmTrailhead("Little Missouri River Trailhead", "ND", "Medora, North Dakota", "Badlands area with petrified wood and lignite. Theodore Roosevelt National Park area — collecting restricted inside park boundaries; check surrounding BLM and state lands.", 46.9140, -103.6220),
        // Ohio
        BlmTrailhead("Flint Ridge Trailhead", "OH", "Brownsville, Ohio", "Historic flint collecting area — Ohio's state gemstone. State memorial with surface collecting permitted. Native American toolstone source for thousands of years.", 39.8110, -82.2950),
        // Oklahoma
        BlmTrailhead("Great Salt Plains Trailhead", "OK", "Jet, Oklahoma", "Selenite crystal digging area — hourglass-shaped crystals unique to this location. State park dig area open April through October. Also barite rose rocks nearby.", 36.7790, -98.6230),
        // Pennsylvania
        BlmTrailhead(" Deer Hill Access", "PA", "Slatington, Pennsylvania", "Quartz, pyrite, and copper minerals in the Lehigh Valley area. Private quarries — access via mineral clubs. Also historic Cornwall iron mine area.", 40.7220, -75.8260),
        // Rhode Island
        BlmTrailhead("Conimicut Point Trailhead", "RI", "Warwick, Rhode Island", "Beach collecting for quartz pebbles and occasional glacial erratics. State shore access — no BLM land in Rhode Island. Limited mineral collecting in the state.", 41.7260, -71.3530),
        // South Carolina
        BlmTrailhead("Diamond Hill Mine Trailhead", "SC", "Abbeville, South Carolina", "Fee-dig quartz crystal, amethyst, and smoky quartz collecting. Private mine open to the public. South Carolina's best-known rockhounding destination.", 34.1680, -82.3780),
        // Tennessee
        BlmTrailhead("Elmwood Mine Access", "TN", "Carthage, Tennessee", "World-class fluorite, calcite, sphalerite, and barite specimens. Private mine — access via mineral dealers and organized field trips. Central Tennessee zinc mining district.", 36.3480, -85.9210),
        // Texas
        BlmTrailhead("Mason County Topaz Trailhead", "TX", "Mason, Texas", "Topaz collecting in Mason County — the Texas state gem. Private ranches offer fee-dig access. Also agate, petrified wood, and smoky quartz in the Hill Country.", 30.7510, -99.1330),
        // Virginia
        BlmTrailhead("Morefield Mine Trailhead", "VA", "Amelia, Virginia", "Fee-dig amazonite, topaz, mica, and garnet collecting. Private mine open seasonally. One of the best pegmatite collecting sites on the East Coast.", 37.3410, -77.9620),
        // Vermont
        BlmTrailhead("Eden Asbestos Quarry Access", "VT", "Eden, Vermont", "Grossular garnet, diopside, and vesuvianite in the historic asbestos quarry area. Private and state land — access via mineral clubs. Vermont state mineral is grossular garnet.", 44.6930, -72.6520),
        // West Virginia
        BlmTrailhead("Seneca Rocks Trailhead", "WV", "Seneca Rocks, West Virginia", "Quartzite formations in the Potomac Highlands. USFS-managed (Monongahela National Forest). Surrounding creeks yield fossils and quartzite specimens.", 38.8330, -79.3720),
        // Wisconsin
        BlmTrailhead("Keweenaw Point Trailhead", "WI", "Bayfield, Wisconsin", "Lake Superior agate and copper mineral collecting along the south shore. State and county land — also lead/zinc minerals in the southwest Wisconsin driftless area.", 46.8110, -91.4560),
    )

    // ── Campgrounds ─────────────────────────────────────────────────────
    val campgrounds: List<BlmCampground> = listOf(
        // Arizona
        BlmCampground("Black Hills Dispersed Camping", "AZ", "Safford, Arizona", "Dispersed camping allowed up to 14 days at the Black Hills Rockhound Area. No facilities — bring everything you need. Best October through April.", "Free", 32.8740, -109.3950),
        // California
        BlmCampground("Wiley's Well Campground", "CA", "Blythe, California", "Closest campground to the Wiley Well Geode Beds. Tables, fire rings, and pit toilets. Good base for geode collecting trips.", "Minimal fee", 33.4750, -114.8700),
        BlmCampground("Midland Campground", "CA", "Kramer Junction, California", "BLM dispersed camping area in the Mojave Desert. No facilities. Good base for exploring surrounding BLM rockhounding areas.", "Free", 34.7680, -117.5180),
        // Colorado
        BlmCampground("Clear Creek Campground", "CO", "Buena Vista, Colorado", "BLM campground near Mount Antero collecting areas. Basic sites with pit toilets. Popular base camp for summer collecting trips.", "Minimal fee", 38.6820, -106.1980),
        BlmCampground("Fourmile Travel Management Area Camping", "CO", "Buena Vista, Colorado", "Dispersed camping on BLM land in the Fourmile area. Access to multiple collecting sites in the Arkansas River Valley.", "Free", 38.7420, -106.1640),
        // Idaho
        BlmCampground("Emerald Creek Dispersed Camping", "ID", "Clarkia, Idaho", "Dispersed camping near the star garnet collecting area. No facilities. Beautiful forested setting in the Idaho panhandle.", "Free", 47.0120, -116.2480),
        // Montana
        BlmCampground("Rock Creek Dispersed Camping", "MT", "Philipsburg, Montana", "Dispersed camping along Rock Creek near sapphire collecting areas. No facilities. Creek access for sapphire screening.", "Free", 46.3280, -113.4520),
        // Nevada
        BlmCampground("Garnet Hill Picnic Area", "NV", "Ely, Nevada", "Four picnic sites with grills, accessible restroom, and group BBQ area. Two informal tent/camper areas. No water. Elevation 7,000 ft.", "Free", 39.2475, -114.8891),
        // Oregon
        BlmCampground("Sunstone Collection Area Camping", "OR", "Plush, Oregon", "Camping is allowed anywhere within the Oregon Sunstone Public Collection Area. Pit toilet, picnic tables, and shade structure on-site.", "Free", 42.7234, -119.8607),
        BlmCampground("Glass Buttes Dispersed Camping", "OR", "Lake County, Oregon", "Dispersed camping permitted on BLM land at Glass Buttes. No facilities. Great base for obsidian collecting off Highway 20.", "Free", 43.7460, -120.0920),
        // South Dakota
        BlmCampground("Black Hills BLM Dispersed Camping", "SD", "Custer, South Dakota", "Dispersed camping on BLM land in the Black Hills. Access to rose quartz and agate collecting areas. No facilities.", "Free", 43.7670, -103.5980),
        // Utah
        BlmCampground("Simpson Springs Campground", "UT", "Dugway, Utah", "BLM campground along the Pony Express Trail, near the Dugway Geode Beds. Good base camp for geode collecting expeditions.", "Minimal fee", 40.3540, -112.8560),
        BlmCampground("Topaz Mountain Dispersed Camping", "UT", "Delta, Utah", "Dispersed camping permitted at Topaz Mountain Rockhound Recreation Area. No facilities. Plan for remote desert conditions.", "Free", 39.7520, -113.2840),
        // Washington
        BlmCampground("Saddle Mountain BLM Camping", "WA", "Mattawa, Washington", "Dispersed camping on BLM land near Saddle Mountain petrified wood area. No facilities. Spring and fall are best.", "Free", 46.6920, -119.5420),
        // Wyoming
        BlmCampground("Sweetwater Campground", "WY", "Jeffrey City, Wyoming", "BLM campground along the Sweetwater River. Basic sites. Good base for jade and agate collecting in the Granite Mountains.", "Free", 42.4980, -107.8260),
        // Alaska
        BlmCampground("Resurrection Creek Dispersed Camping", "AK", "Hope, Alaska", "Dispersed camping on BLM land along Resurrection Creek. Near recreational gold panning area. No facilities.", "Free", 60.9170, -149.5330),
        // Alabama
        BlmCampground("DeSoto State Park Campground", "AL", "Fort Payne, Alabama", "Developed campground near Little River Canyon. Good base for exploring Alabama's Appalachian geology. State park with full facilities — not BLM.", "$20/night", 34.5220, -85.6170),
        // Arkansas (AR)
        BlmCampground("Crater of Diamonds State Park Campground", "AR", "Murfreesboro, Arkansas", "Full-service campground adjacent to the diamond search area. State park, not BLM. Good base for quartz crystal collecting in the Ouachita Mountains.", "$30/night", 34.6620, -93.7010),
        BlmCampground("Gus's Ozark Mountain Campground", "AR", "Mount Ida, Arkansas", "Private campground near Wegner Quartz Crystal Mines. Good base for quartz crystal fee-dig operations in the Mount Ida area.", "$25/night", 34.5530, -93.5940),
        // California Desert (moved from CA3 to CA)
        BlmCampground("Afton Canyon Campground", "CA", "Barstow, California", "BLM campground in Afton Canyon with pit toilets and fire rings. Mojave River runs seasonally. Good base for desert rockhounding. No water.", "$10/night", 35.1400, -116.3950),
        // New Mexico
        BlmCampground("Rockhound State Park Campground", "NM", "Deming, New Mexico", "State park campground adjacent to BLM land. Water, electric, and restrooms. Collecting allowed in the park — thundereggs, geodes, and jasper.", "$14/night", 32.3180, -108.2520),
        // Connecticut
        BlmCampground("Hammonasset State Park Campground", "CT", "Madison, Connecticut", "State park campground near Long Island Sound. Not a rockhounding site but good base for southern Connecticut mineral localities and beach collecting.", "$30/night", 41.2630, -72.5280),
        // Delaware
        BlmCampground("Cape Henlopen State Park Campground", "DE", "Lewes, Delaware", "State park campground with full facilities near the Atlantic shore. Good base for beach fossil and mineral collecting along the Delaware coast.", "$35/night", 38.7800, -75.1070),
        // Florida
        BlmCampground("Peace River Campground & Canoe", "FL", "Arcadia, Florida", "Private campground on the Peace River — popular base for fossil hunting canoe trips. Megalodon teeth and mammal bones. Florida fossil permit required.", "$25/night", 27.2950, -81.8640),
        // Georgia
        BlmCampground("Elijah Clark State Park Campground", "GA", "Lincolnton, Georgia", "State park campground near Graves Mountain mineral locality. Full facilities on J. Strom Thurmond Lake. Good base for Georgia mineral collecting.", "$30/night", 33.8330, -82.4620),
        // Hawaii
        BlmCampground("Namahanoa (Green Sand) Campground", "HI", "South Point, Hawaii", "Rustic camping near South Point — the southernmost point in the U.S. Near Papakolea Green Sand Beach. Hawaii County managed — no facilities. Collecting sand prohibited.", "Free", 19.0000, -155.6450),
        // Iowa
        BlmCampground("Geode State Park Campground", "IA", "Danville, Iowa", "State park campground near the Keokuk geode beds. Good base for geode collecting in southeast Iowa. Full facilities.", "$16/night", 40.8630, -91.2650),
        // Illinois
        BlmCampground("Ferne Clyffe State Park Campground", "IL", "Goreville, Illinois", "State park campground near the Cave-in-Rock fluorite district. Full facilities. Good base for southern Illinois mineral collecting.", "$20/night", 37.3780, -88.9820),
        // Indiana
        BlmCampground("Charlestown State Park Campground", "IN", "Charlestown, Indiana", "State park campground near the Falls of the Ohio fossil beds. Full facilities. Good base for fossil collecting along the Ohio River.", "$25/night", 38.4280, -85.6680),
        // Kansas
        BlmCampground("Wilson State Park Campground", "KS", "Sylvan Grove, Kansas", "State park campground on Wilson Lake. Good base for exploring the Smoky Hill Chalk badlands and Monument Rocks area in western Kansas.", "$18/night", 38.8040, -98.5520),
        // Kentucky
        BlmCampground("Lake Barkley State Park Campground", "KY", "Cadiz, Kentucky", "State park resort campground in the western Kentucky fluorspar district. Full facilities. Good base for fluorite and calcite collecting.", "$25/night", 36.8720, -88.0520),
        // Louisiana
        BlmCampground("Kisatchie National Forest Campground", "LA", "Leesville, Louisiana", "USFS campground in the Vernon Parish area — near Louisiana opal collecting sites. Full facilities. Also good for petrified palm wood.", "$15/night", 31.1370, -93.2270),
        // Maine
        BlmCampground("Mount Blue State Park Campground", "ME", "Weld, Maine", "State park campground in the western Maine pegmatite belt. Good base for tourmaline and beryl fee-dig quarries in the Newry-Paris area.", "$25/night", 44.6810, -70.4240),
        // Maryland
        BlmCampground("Patapsco Valley State Park Campground", "MD", "Ellicott City, Maryland", "State park campground near the Soldier's Delight serpentinite barrens. Full facilities. Good base for central Maryland mineral localities.", "$30/night", 39.2920, -76.7640),
        // Massachusetts
        BlmCampground("Chester-Blandford State Forest Campground", "MA", "Chester, Massachusetts", "State forest campground near the historic Chester Emery Mines. Rustic sites. Good base for western Massachusetts mineral collecting.", "$17/night", 42.3970, -72.9880),
        // Michigan
        BlmCampground("McLain State Park Campground", "MI", "Hancock, Michigan", "State park campground on the Keweenaw Peninsula. Good base for native copper collecting at old mine dumps. Lake Superior shoreline.", "$25/night", 47.1180, -88.5820),
        // Minnesota
        BlmCampground("Tettegouche State Park Campground", "MN", "Silver Bay, Minnesota", "State park campground on the North Shore. Excellent base for Lake Superior agate beach collecting. Full facilities along Lake Superior.", "$23/night", 47.1680, -91.3240),
        // Mississippi
        BlmCampground("Leroy Percy State Park Campground", "MS", "Holly Bluff, Mississippi", "State park campground in the Mississippi Delta. Good base for petrified wood and fossil collecting in west-central Mississippi.", "$18/night", 32.8940, -90.3520),
        // Missouri
        BlmCampground("Onondaga Cave State Park Campground", "MO", "Leasburg, Missouri", "State park campground near the Viburnum mining district. Full facilities. Good base for galena, calcite, and chalcopyrite collecting.", "$22/night", 38.0940, -91.0980),
        // Nebraska
        BlmCampground("Smith Falls State Park Campground", "NE", "Valentine, Nebraska", "State park campground near the Niobrara River. Good base for prairie agate and fossil collecting in north-central Nebraska.", "$15/night", 42.5360, -100.6780),
        // New Jersey
        BlmCampground("High Point State Park Campground", "NJ", "Sussex, New Jersey", "State park campground in northern New Jersey. Good base for Franklin and Sterling Hill fluorescent mineral collecting sites.", "$20/night", 41.3100, -74.7000),
        // New York
        BlmCampground("Herkimer Diamond Mines KOA", "NY", "Middleville, New York", "Private campground adjacent to Herkimer Diamond Mines fee-dig area. Full RV and tent sites. Direct access to quartz crystal collecting.", "$40/night", 43.2760, -74.8640),
        // North Carolina
        BlmCampground("Emerald Hollow Mine Camping", "NC", "Hiddenite, North Carolina", "Private campground at the Emerald Hollow Mine fee-dig site. Tent and RV sites. Direct access to emerald, sapphire, and ruby collecting.", "$30/night", 35.9050, -81.0920),
        // North Dakota
        BlmCampground("Sully Creek State Park Campground", "ND", "Medora, North Dakota", "State park campground near Theodore Roosevelt National Park. Good base for petrified wood and badlands geology in western North Dakota.", "$15/night", 46.9140, -103.6220),
        // Ohio
        BlmCampground("Dillon State Park Campground", "OH", "Nashport, Ohio", "State park campground near Flint Ridge. Full facilities. Good base for flint and Ordovician fossil collecting in central Ohio.", "$23/night", 40.0030, -82.1750),
        // Oklahoma
        BlmCampground("Great Salt Plains State Park Campground", "OK", "Jet, Oklahoma", "State park campground adjacent to the selenite crystal dig area. Full facilities. Good base for barite rose rock collecting in central Oklahoma.", "$18/night", 36.7790, -98.6230),
        // Pennsylvania
        BlmCampground("Ricketts Glen State Park Campground", "PA", "Benton, Pennsylvania", "State park campground in northeastern Pennsylvania. Good base for mineral collecting in the Lehigh Valley and Cornwall iron district.", "$20/night", 41.2990, -76.2820),
        // Rhode Island
        BlmCampground("Burlingame State Park Campground", "RI", "Charlestown, Rhode Island", "State park campground in southern Rhode Island. Good base for beach collecting along the Rhode Island shore. Full facilities.", "$24/night", 41.3770, -71.6670),
        // South Carolina
        BlmCampground("Hickory Knob State Resort Campground", "SC", "McCormick, South Carolina", "State resort campground near the Diamond Hill Mine fee-dig area. Full facilities on Lake Thurmond. Good base for South Carolina mineral collecting.", "$30/night", 33.9180, -82.2980),
        // Tennessee
        BlmCampground("Montgomery Bell State Park Campground", "TN", "Burns, Tennessee", "State park campground in central Tennessee. Good base for fluorite and calcite collecting in the Elmwood-Carthage mining district.", "$20/night", 36.1870, -87.1380),
        // Texas
        BlmCampground("Inks Lake State Park Campground", "TX", "Burnet, Texas", "State park campground in the Texas Hill Country. Good base for topaz collecting in Mason County and agate hunting in the Llano uplift area.", "$20/night", 30.7380, -98.3590),
        // Virginia
        BlmCampground("Pocahontas State Park Campground", "VA", "Chesterfield, Virginia", "State park campground near Richmond. Good base for Morefield Mine fee-dig trips in Amelia and unakite collecting in the Blue Ridge.", "$25/night", 37.3380, -77.5580),
        // Vermont
        BlmCampground("Little River State Park Campground", "VT", "Waterbury, Vermont", "State park campground in northern Vermont. Good base for garnet collecting in the Eden asbestos quarry area and Green Mountain mineral localities.", "$20/night", 44.3830, -72.6260),
        // West Virginia
        BlmCampground("Seneca Shadows Campground", "WV", "Seneca Rocks, West Virginia", "USFS campground near Seneca Rocks in the Monongahela National Forest. Good base for quartzite and fossil collecting in the Potomac Highlands.", "$22/night", 38.8330, -79.3720),
        // Wisconsin
        BlmCampground("Wyalusing State Park Campground", "WI", "Prairie du Chien, Wisconsin", "State park campground at the confluence of the Wisconsin and Mississippi Rivers. Good base for lead/zinc minerals and Lake Superior agates in southwest Wisconsin.", "$20/night", 43.0210, -91.1860),
    )

    // ── Important Info sections ─────────────────────────────────────────
    val infoSections: List<BlmInfoSection> = listOf(
        BlmInfoSection(
            title = "What BLM Land Allows",
            icon = "✅",
            accentHex = 0xFF5CC98C,
            points = listOf(
                "Collecting reasonable amounts of rocks, mineral specimens, and semiprecious gemstones for personal, non-commercial use.",
                "Surface collecting and digging with hand tools (non-motorized).",
                "Collecting common invertebrate fossils (shellfish, corals, trilobites, crinoids) in reasonable amounts.",
                "Collecting petrified wood: up to 25 lbs per day plus one piece, maximum 250 lbs per person per year.",
                "Metal detecting for rocks and minerals (with some site exceptions).",
                "Dispersed camping on most BLM land (up to 14 days in most areas).",
            ),
        ),
        BlmInfoSection(
            title = "What BLM Land Forbids",
            icon = "🚫",
            accentHex = 0xFFE2574C,
            points = listOf(
                "Commercial collecting — selling, trading, or bartering collected materials for profit.",
                "Collecting vertebrate fossils (dinosaurs, mammals, fishes, reptiles) — only permitted researchers with BLM authorization may collect these.",
                "Using power equipment, explosives, or mechanized tools for casual collecting.",
                "Removing cultural or archaeological artifacts (rock art, pottery, bottles, old equipment, structures) — these are protected by law.",
                "Collecting on active mining claims without the claim holder's permission.",
                "Collecting in developed recreation sites, unless designated as a rockhounding area.",
                "Removing rock stockpiles from BLM quarries — this is theft of federal property.",
                "Pooling petrified wood quotas to exceed the 250 lb annual limit.",
            ),
        ),
        BlmInfoSection(
            title = "National Parks & Private Land",
            icon = "⚠️",
            accentHex = 0xFFE8A33D,
            points = listOf(
                "Collecting is PROHIBITED in all National Parks — no rocks, minerals, or fossils may be taken.",
                "National Monuments may have additional restrictions — always check before collecting.",
                "Wilderness and Wilderness Study Areas: collecting allowed but limited to non-motorized hand tools with minimal disturbance.",
                "Areas of Critical Environmental Concern (ACECs) may have special restrictions.",
                "Private land requires the owner's permission — always verify land boundaries before collecting.",
                "Mining claims: minerals on claims belong to the claimant. Check for posted claim notices.",
                "State lands have different rules — contact the state land management office.",
            ),
        ),
        BlmInfoSection(
            title = "Vertebrate Fossils & Archaeological Protections",
            icon = "🦴",
            accentHex = 0xFFC9A87C,
            points = listOf(
                "Vertebrate fossils (dinosaurs, mammals, fish, reptiles, footprints) can ONLY be collected by trained researchers with a BLM permit.",
                "Common invertebrate fossils and plant fossils may be collected in reasonable amounts for personal use.",
                "Human remains must be left in place and reported to BLM immediately.",
                "Archaeological materials (arrowheads, pottery, tools) over 100 years old are protected — do not collect or disturb.",
                "Cave resources are federally protected and cannot be altered, damaged, or removed.",
                "Historic structures and artifacts cannot be removed or damaged.",
            ),
        ),
        BlmInfoSection(
            title = "Responsible Collecting Ethics",
            icon = "🌱",
            accentHex = 0xFF7BA868,
            points = listOf(
                "Backfill all holes and reclaim surface disturbances.",
                "Pack out all trash — practice Leave No Trace.",
                "Stay on existing roads to avoid damaging fragile desert soils.",
                "Respect wildlife and their habitats — do not disturb animals or plants.",
                "Do not collect within 300 feet of developed recreation sites.",
                "Limit your take to what you will actually use — leave specimens for others.",
                "Report vandalism or illegal collecting to the local BLM office.",
            ),
        ),
        BlmInfoSection(
            title = "Safety Reminders",
            icon = "🛡️",
            accentHex = 0xFFE2574C,
            points = listOf(
                "Carry plenty of water — desert areas can exceed 110°F in summer. Collect in cooler months (October through April).",
                "Tell someone where you are going and when you plan to return.",
                "Bring a first aid kit, spare tire, and extra fuel — many collecting areas are remote.",
                "Be aware of old mine shafts and adits — do not enter abandoned mines (they are dangerous and unstable).",
                "Watch for rattlesnakes and other wildlife, especially in rocky areas.",
                "Check road conditions before heading out — many BLM roads are unimproved and impassable when wet.",
                "Carry a map and GPS — cell service is unreliable in remote areas.",
                "Use eye protection when breaking rocks — rock chips can cause serious eye injuries.",
            ),
        ),
    )
}
