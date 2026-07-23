package com.rork.rockscout.data

/**
 * Database expansion wave 17 — Phase 2: Additional varieties & specialty stones.
 * Quartz varieties, garnet varieties, Goldstone (green/blue/brown), trade-name stones,
 * tektites, volcanic glass, and rare collector specimens.
 * Generated 2026-07-22.
 */
@Suppress("unused")
object ExpansionBatch17 {
    val specimens: List<Specimen> = listOf(

        // ════════════════════════════════════════════
        // QUARTZ VARIETIES (7)
        // ════════════════════════════════════════════

        Specimen(
            id = "quartz-brandberg",
            name = "Quartz, Brandberg",
            rockClass = RockClass.CRYSTAL,
            category = "Quartz Variety — Smoky-Amethyst with Phantoms",
            tagline = "Namibian smoky-amethyst quartz with spectacular phantom inclusions.",
            emoji = "🔮",
            colorHex = 0xFF6B4E8C,
            description = "Brandberg Quartz is a rare and highly sought-after variety from the Brandberg area of Namibia. It combines smoky quartz and amethyst in the same crystal, often with striking phantom inclusions (internal layers marking growth stages). Brandberg crystals are known for their exceptional clarity, distinctive color zoning, and spiritual significance.",
            formation = "Found in vugs and pockets in basaltic lavas of the Etendeka Formation in Namibia. The phantoms form when growth pauses and mineral dust settles on the crystal faces before growth resumes.",
            hardness = "7",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "SiO₂",
            commonColors = listOf("Smoky purple", "Amethyst-smoky mix", "Clear with purple phantoms"),
            whereFound = listOf("Brandberg area, Namibia", "Gobobos Mountains, Namibia"),
            funFacts = listOf(
                "Brandberg Quartz is named after the Brandberg Mountain, Namibia's highest peak.",
                "Each crystal often shows both smoky and amethyst color zones in the same specimen.",
                "Phantom inclusions inside the crystals show growth stages like rings of a tree.",
                "The Brandberg area is a sacred site for the Damara people of Namibia."
            ),
            uses = "Collector specimens, jewelry, metaphysical, meditation.",
            rarity = "Rare"
        ),

        Specimen(
            id = "quartz-elestial",
            name = "Quartz, Elestial",
            rockClass = RockClass.CRYSTAL,
            category = "Quartz Variety — Skeletal (Elestial) Quartz",
            tagline = "Skeletal quartz with layered, etched crystal surfaces — formed under rapid growth.",
            emoji = "✨",
            colorHex = 0xFFB8B8D0,
            description = "Elestial Quartz (also called Skeletal Quartz) is a variety of quartz that forms under conditions of rapid growth, creating layered, terraced, or skeletal crystal forms. The surfaces are covered with natural etchings and indentations that give the crystal a distinctive, rippled appearance. Elestials can be smoky, clear, amethyst, or citrine colored.",
            formation = "Forms when quartz grows rapidly under fluctuating temperature and pressure conditions. The skeletal habit results from preferential growth at the crystal edges while the faces grow more slowly.",
            hardness = "7",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "SiO₂",
            commonColors = listOf("Smoky", "Clear", "Amethyst", "Citrine"),
            whereFound = listOf("Brazil", "Madagascar", "Himalayas", "Arkansas, USA", "Namibia"),
            funFacts = listOf(
                "Elestial quartz is also called 'skeletal quartz' because of its hollow, layered structure.",
                "The name 'elestial' may come from 'celestial' due to its ethereal appearance.",
                "Internal cavities in elestial quartz sometimes contain water — these are called 'enhydro elestials'.",
                "The layered growth pattern records changes in the mineral-forming fluid over time."
            ),
            uses = "Collector specimens, jewelry, metaphysical, meditation.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "quartz-faden",
            name = "Quartz, Faden",
            rockClass = RockClass.CRYSTAL,
            category = "Quartz Variety — Faden (Thread) Quartz",
            tagline = "Transparent quartz with a visible white thread running through the crystal axis.",
            emoji = "🧵",
            colorHex = 0xFFE0E0E8,
            description = "Faden Quartz is a distinctive variety of quartz with a visible white, thread-like line running through the center of the crystal along its growth axis. The 'faden' (German for 'thread') is a healed fracture that forms when the crystal breaks during growth and then repairs itself. Faden quartz is typically tabular and transparent.",
            formation = "Forms in Alpine-type fissures where tectonic forces repeatedly fracture and heal the growing crystal. The thread marks the original fracture surface that healed with new quartz.",
            hardness = "7",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "SiO₂",
            commonColors = listOf("Clear", "White thread", "Slightly smoky"),
            whereFound = listOf("Alps (Switzerland, Austria)", "Pakistan", "Arkansas, USA", "Brazil", "Russia"),
            funFacts = listOf(
                "'Faden' is the German word for 'thread' — the white line looks like a thread inside the crystal.",
                "The thread is actually a healed fracture — the crystal broke during growth and repaired itself.",
                "Faden quartz typically forms in Alpine-type fissures under tectonic stress.",
                "The best specimens come from the Swiss and Austrian Alps."
            ),
            uses = "Collector specimens, mineralogical research, metaphysical.",
            rarity = "Rare"
        ),

        Specimen(
            id = "quartz-phantom",
            name = "Quartz, Phantom",
            rockClass = RockClass.CRYSTAL,
            category = "Quartz Variety — Phantom (Ghost) Quartz",
            tagline = "Clear quartz with ghost-like internal crystal shapes from growth interruptions.",
            emoji = "👻",
            colorHex = 0xFF50C878,
            description = "Phantom Quartz (also called Ghost Quartz) contains a visible 'phantom' — a ghost-like outline of a smaller crystal inside the main crystal, created when growth paused and mineral dust or chlorite settled on the surface before growth resumed. The phantom can be green (chlorite), red (hematite), white (calcite), or other colors.",
            formation = "Forms when quartz growth pauses, allowing minerals to settle on the crystal faces. When growth resumes, the settled minerals create a phantom outline marking the earlier growth stage.",
            hardness = "7",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "SiO₂ (with mineral inclusions)",
            commonColors = listOf("Clear with green phantoms", "Clear with red phantoms", "Clear with white phantoms"),
            whereFound = listOf("Brazil", "Madagascar", "Arkansas, USA", "Alps", "Pakistan"),
            funFacts = listOf(
                "The 'phantom' is actually a record of a previous growth stage frozen inside the crystal.",
                "Green phantoms are caused by chlorite inclusions; red phantoms by hematite.",
                "Each phantom tells the story of a pause in the crystal's growth — sometimes millions of years ago.",
                "Phantom quartz is sometimes called 'ghost quartz' because the inner shapes look like spectral outlines."
            ),
            uses = "Collector specimens, jewelry, metaphysical, meditation.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "quartz-tibetan",
            name = "Quartz, Tibetan",
            rockClass = RockClass.CRYSTAL,
            category = "Quartz Variety — Tibetan Quartz",
            tagline = "High-altitude quartz from the Himalayas with distinctive black inclusions.",
            emoji = "🏔️",
            colorHex = 0xFF8B8B8B,
            description = "Tibetan Quartz is a variety of quartz mined at extreme altitudes in the Himalayan mountains of Tibet. It is characterized by clear to slightly smoky crystals with distinctive black inclusions, often carbon or hematite. Tibetan quartz is highly valued by collectors and in metaphysical circles for its remote, high-altitude origin.",
            formation = "Found in quartz veins in the high mountains of the Himalayas, at altitudes often exceeding 15,000 feet. The black inclusions are typically carbon or hematite.",
            hardness = "7",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "SiO₂ (with carbon/hematite inclusions)",
            commonColors = listOf("Clear with black inclusions", "Slightly smoky", "White"),
            whereFound = listOf("Tibet", "Nepal", "Bhutan", "Himalayan region"),
            funFacts = listOf(
                "Tibetan quartz is mined at some of the highest altitudes of any mineral specimen.",
                "The distinctive black inclusions are typically carbon or hematite.",
                "Mining is extremely difficult due to the remote, high-altitude locations.",
                "Tibetan quartz is highly valued in metaphysical communities for its Himalayan origin."
            ),
            uses = "Collector specimens, jewelry, metaphysical, meditation.",
            rarity = "Rare"
        ),

        Specimen(
            id = "quartz-blue",
            name = "Quartz, Blue",
            rockClass = RockClass.CRYSTAL,
            category = "Quartz Variety — Blue Quartz",
            tagline = "Naturally blue quartz colored by microscopic inclusions — rare and beautiful.",
            emoji = "🔵",
            colorHex = 0xFF4F94CD,
            description = "Blue Quartz is a naturally occurring variety of quartz with a blue color caused by microscopic inclusions of minerals such as dumortierite, tourmaline, or zoisite. Unlike treated blue quartz, natural blue quartz has a subtle, often patchy blue color. The most prized specimens have a uniform, deep blue color.",
            formation = "Found in granitic pegmatites and metamorphic rocks. The blue color is caused by sub-microscopic inclusions of dumortierite, tourmaline, or other blue minerals.",
            hardness = "7",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "SiO₂ (with inclusions)",
            commonColors = listOf("Blue", "Blue-gray", "Pale blue", "Blue-violet"),
            whereFound = listOf("Brazil", "Austria", "USA (various locations)", "Madagascar", "Russia"),
            funFacts = listOf(
                "The blue color in natural blue quartz comes from microscopic inclusions, not trace elements.",
                "Dumortierite inclusions are the most common cause of blue coloration in quartz.",
                "Most 'blue quartz' sold commercially is actually treated — natural blue quartz is much rarer.",
                "Some blue quartz shows a subtle asterism (star effect) when cut en cabochon."
            ),
            uses = "Collector specimens, jewelry (cabochons and beads), decorative stone.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "quartz-aura",
            name = "Quartz, Aura",
            rockClass = RockClass.CRYSTAL,
            category = "Quartz Variety — Titanium-Coated (Aura) Quartz",
            tagline = "Rainbow iridescent quartz bonded with titanium and other metals.",
            emoji = "🌈",
            colorHex = 0xFF9370DB,
            description = "Aura Quartz (also called Aqua Aura, Angel Aura, or Titanium Quartz) is created by bonding clear quartz crystals with vaporized titanium, gold, silver, or other metals in a vacuum chamber. The result is a stunning iridescent coating that displays rainbow colors. While the process is synthetic, it starts with natural quartz crystals.",
            formation = "Created by treating natural quartz crystals with vaporized metals in a vacuum chamber at high temperature. The metal bonds to the quartz surface, creating an iridescent layer.",
            hardness = "7 (quartz core)",
            luster = "Iridescent metallic",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "SiO₂ (with Ti/Au/Ag coating)",
            commonColors = listOf("Iridescent blue", "Rainbow", "Purple-gold", "Silver-blue"),
            whereFound = listOf("Created in laboratories worldwide; natural quartz from Brazil, Arkansas, etc."),
            funFacts = listOf(
                "Aura quartz is created by bonding titanium or gold to natural quartz in a vacuum chamber.",
                "Aqua Aura is the original version, created with gold vapor.",
                "Angel Aura uses silver and platinum for a white-blue iridescence.",
                "The iridescent coating is permanent and does not rub off under normal handling."
            ),
            uses = "Jewelry, collector specimens, metaphysical, decorative objects.",
            rarity = "Common (treated)"
        ),

        // ════════════════════════════════════════════
        // GARNET VARIETIES (5)
        // ════════════════════════════════════════════

        Specimen(
            id = "garnet-star",
            name = "Garnet, Star",
            rockClass = RockClass.CRYSTAL,
            category = "Garnet Variety — Asteriated (Star) Garnet",
            tagline = "Rare garnet showing a four-rayed star when cut en cabochon.",
            emoji = "⭐",
            colorHex = 0xFF8B2500,
            description = "Star Garnet is a rare variety of garnet that displays asterism — a four-rayed or six-rayed star when cut en cabochon. The star effect is caused by needle-like inclusions of rutile aligned along the crystal's internal structure. Star garnets are most commonly almandine or a mix of almandine and pyrope.",
            formation = "Found in alluvial placer deposits and some metamorphic rocks. The rutile inclusions that cause the star effect form during the garnet's growth.",
            hardness = "7–7.5",
            luster = "Vitreous to subadamantine",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "Fe₃Al₂(SiO₄)₃ (with rutile inclusions)",
            commonColors = listOf("Dark red", "Reddish-brown", "Brownish-red"),
            whereFound = listOf("Idaho, USA (official state gem)", "Sri Lanka", "India", "Tanzania", "Brazil"),
            funFacts = listOf(
                "Star garnet is the official state gem of Idaho.",
                "The star effect (asterism) is caused by needle-like rutile inclusions.",
                "Star garnets typically show a four-rayed star, though six-rayed stars also occur.",
                "Only a small percentage of garnets from any deposit show the star effect."
            ),
            uses = "Gemstone (cabochon), collector specimens, jewelry.",
            rarity = "Rare"
        ),

        Specimen(
            id = "garnet-malaya",
            name = "Garnet, Malaya",
            rockClass = RockClass.CRYSTAL,
            category = "Garnet Variety — Spessartine-Pyrope Hybrid",
            tagline = "Warm orange-pink garnet hybrid from East Africa — once discarded, now prized.",
            emoji = "🟠",
            colorHex = 0xFFE27B3A,
            description = "Malaya Garnet (also spelled Malaia) is a hybrid garnet between spessartine and pyrope, displaying warm orange, pinkish-orange, and reddish-orange colors. The name 'malaya' means 'outcast' in Swahili because miners initially discarded them, not fitting any known garnet category. Today, malaya garnets are highly valued for their warm, fiery colors.",
            formation = "Found in alluvial deposits in East Africa. The garnets form in metamorphic rocks and are concentrated in placer deposits by weathering.",
            hardness = "7–7.5",
            luster = "Vitreous to subadamantine",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "(Mg,Mn,Fe)₃Al₂(SiO₄)₃",
            commonColors = listOf("Orange", "Pinkish-orange", "Reddish-orange", "Honey-orange"),
            whereFound = listOf("Tanzania", "Kenya", "Madagascar", "Sri Lanka"),
            funFacts = listOf(
                "'Malaya' means 'outcast' in Swahili — miners discarded them because they didn't fit known categories.",
                "Malaya garnet is a hybrid between spessartine and pyrope.",
                "They are now among the most sought-after garnet varieties for their warm, fiery colors.",
                "Some malaya garnets show a color-shift effect, appearing different under daylight and incandescent light."
            ),
            uses = "Gemstone (faceted and cabochon), jewelry, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "garnet-raspberry",
            name = "Garnet, Raspberry",
            rockClass = RockClass.CRYSTAL,
            category = "Garnet Variety — Raspberry-Colored Grossular",
            tagline = "Bright raspberry-red grossular garnet — vivid color from a rare variety.",
            emoji = "🫐",
            colorHex = 0xFFB73E7E,
            description = "Raspberry Garnet is a trade name for a vivid raspberry-red to pinkish-red variety of grossular garnet. The bright color is caused by iron and manganese impurities. It is rarer than the more common orange hessonite or green tsavorite grossular varieties, and the raspberry color makes it a distinctive collector's gem.",
            formation = "Found in metamorphic skarn deposits and alluvial placer deposits. The raspberry color is caused by specific trace element ratios of iron and manganese.",
            hardness = "6.5–7",
            luster = "Vitreous to subadamantine",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "Ca₃Al₂(SiO₄)₃ (with Fe, Mn)",
            commonColors = listOf("Raspberry red", "Pinkish-red", "Purplish-red"),
            whereFound = listOf("Tanzania", "Mexico", "Canada (Quebec)", "Pakistan"),
            funFacts = listOf(
                "Raspberry garnet is one of the rarest color varieties of grossular garnet.",
                "The vivid raspberry color is caused by a specific balance of iron and manganese.",
                "It is distinct from rhodolite (a pyrope-almandine variety) in both chemistry and appearance.",
                "The raspberry color is more saturated than typical red grossular garnets."
            ),
            uses = "Gemstone (faceted), collector specimens, jewelry.",
            rarity = "Rare"
        ),

        Specimen(
            id = "garnet-hydrogrossular",
            name = "Garnet, Hydrogrossular",
            rockClass = RockClass.CRYSTAL,
            category = "Garnet Variety — Massive Green Hydrogrossular",
            tagline = "Massive green garnet with hydroxyl substitution — sometimes called 'Transvaal jade'.",
            emoji = "🟢",
            colorHex = 0xFF4A8B5C,
            description = "Hydrogrossular is a massive, non-crystalline variety of grossular garnet where hydroxyl (OH) groups partially replace the silica. It is typically green, pink, or white and is often called 'Transvaal jade' or 'South African jade' because of its jade-like appearance. It is used for cabochons, carvings, and beads.",
            formation = "Found in contact metamorphic zones and skarn deposits. Forms as massive aggregates rather than individual crystals.",
            hardness = "7–7.5",
            luster = "Vitreous to greasy",
            streak = "White",
            crystalSystem = "Isometric (massive)",
            chemicalFormula = "Ca₃Al₂(SiO₄)₃₋ₓ(OH)₄ₓ",
            commonColors = listOf("Green", "Pink", "White", "Grayish-green"),
            whereFound = listOf("Transvaal, South Africa", "New Zealand", "Pakistan", "Canada"),
            funFacts = listOf(
                "Hydrogrossular is sometimes called 'Transvaal jade' for its jade-like appearance.",
                "It is one of the few garnets that is typically found in massive form, not as crystals.",
                "The hydroxyl substitution makes it slightly less hard than other garnets.",
                "Pink hydrogrossular from New Zealand is sometimes called 'rodingite garnet'."
            ),
            uses = "Cabochons, carvings, beads, decorative objects, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "garnet-rhodolite",
            name = "Garnet, Rhodolite",
            rockClass = RockClass.CRYSTAL,
            category = "Garnet Variety — Pyrope-Almandine (Pink-Purple)",
            tagline = "Beautiful pink-purple garnet hybrid — the most popular garnet for fine jewelry.",
            emoji = "🌸",
            colorHex = 0xFFC45A77,
            description = "Rhodolite is a popular garnet variety that is a natural hybrid between pyrope and almandine garnets. It displays a distinctive raspberry-pink to purplish-red color that is lighter and more vibrant than typical red garnets. The name comes from the Greek 'rhodon' meaning 'rose', referring to its pinkish color. Rhodolite is the most commonly used garnet in fine jewelry.",
            formation = "Found in alluvial placer deposits derived from metamorphic rocks. The pyrope-almandine composition gives it its distinctive pink-purple color.",
            hardness = "7–7.5",
            luster = "Vitreous to subadamantine",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "(Mg,Fe)₃Al₂(SiO₄)₃",
            commonColors = listOf("Raspberry pink", "Purplish-red", "Pinkish-red", "Rose-pink"),
            whereFound = listOf("Sri Lanka", "Tanzania", "Zimbabwe", "Madagascar", "North Carolina, USA"),
            funFacts = listOf(
                "The name 'rhodolite' comes from the Greek 'rhodon' meaning 'rose'.",
                "It is a natural hybrid between pyrope and almandine garnets.",
                "Rhodolite is the most popular garnet variety for fine jewelry.",
                "North Carolina rhodolites are among the finest in the world.",
                "The color is lighter and more vibrant than traditional deep-red garnets."
            ),
            uses = "Gemstone (faceted and cabochon), fine jewelry, collector specimens.",
            rarity = "Uncommon"
        ),

        // ════════════════════════════════════════════
        // TOPAZ, OPAL, SPINEL, ZIRCON (5)
        // ════════════════════════════════════════════

        Specimen(
            id = "topaz-clear",
            name = "Topaz, Clear",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Topaz Variety (Colorless/White)",
            tagline = "Pure colorless topaz — the base form before color treatments.",
            emoji = "⚪",
            colorHex = 0xFFE8E8F0,
            description = "Clear Topaz (also called White Topaz or Silver Topaz) is the pure, colorless variety of topaz. It is the base form of topaz before trace impurities add color. Much of the commercially available blue topaz is actually clear topaz that has been irradiated and heat-treated to produce blue color.",
            formation = "Found in granitic pegmatites and hydrothermal veins. The colorless variety forms when no trace impurities are present during crystal growth.",
            hardness = "8",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Al₂SiO₄(F,OH)₂",
            commonColors = listOf("Colorless", "White", "Slightly silver"),
            whereFound = listOf("Brazil", "Sri Lanka", "Nigeria", "Pakistan", "Russia"),
            funFacts = listOf(
                "Clear topaz is the starting material for most commercial blue topaz.",
                "It has a high refractive index, giving it good brilliance when faceted.",
                "Natural colorless topaz is sometimes called 'silver topaz' in the trade.",
                "Despite being called 'white', it is actually completely colorless."
            ),
            uses = "Gemstone (faceted and cabochon), source material for treated blue topaz, jewelry.",
            rarity = "Common"
        ),

        Specimen(
            id = "opal-violet-flame",
            name = "Opal, Violet Flame",
            rockClass = RockClass.CRYSTAL,
            category = "Opal Variety — Violet Flame Opal",
            tagline = "Rare Ethiopian opal with striking violet-purple flame patterns.",
            emoji = "🟣",
            colorHex = 0xFF7B4FA8,
            description = "Violet Flame Opal is a rare variety of Ethiopian opal displaying vivid violet and purple flame-like play-of-color patterns. The violet flashes are caused by the same silica sphere diffraction that creates all opal play-of-color, but with a specific sphere size that preferentially produces purple-violet colors. It is highly prized by collectors.",
            formation = "Found in volcanic ash deposits in the Welo region of Ethiopia. Forms as silica gel filling cavities in volcanic rock.",
            hardness = "5.5–6",
            luster = "Vitreous to opalescent",
            streak = "White",
            crystalSystem = "Amorphous",
            chemicalFormula = "SiO₂·nH₂O",
            commonColors = listOf("Violet", "Purple", "Lilac", "Deep purple flashes"),
            whereFound = listOf("Welo Province, Ethiopia", "Mezezo, Ethiopia"),
            funFacts = listOf(
                "Violet flame opal is one of the rarest play-of-color patterns in Ethiopian opal.",
                "The purple-violet flashes are caused by a specific silica sphere size in the opal's structure.",
                "Ethiopian opals are hydrophane — they can absorb water and temporarily become more transparent.",
                "The violet color in opal play-of-color is rarer than the more common green and blue flashes."
            ),
            uses = "Gemstone (cabochon), collector specimens, jewelry.",
            rarity = "Rare"
        ),

        Specimen(
            id = "spinel-lavender",
            name = "Spinel, Lavender",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Spinel Variety (Lavender)",
            tagline = "Soft lavender-purple spinel — a delicate and rare color variety.",
            emoji = "🟣",
            colorHex = 0xFFB4A0D4,
            description = "Lavender Spinel is a rare color variety of spinel displaying a soft lavender to pale purple color. The color is caused by trace amounts of iron and possibly chromium. While spinel comes in many colors, the lavender variety is one of the rarest and most sought after by collectors.",
            formation = "Found in marble metamorphic deposits and alluvial placer deposits. The lavender color is caused by specific trace element ratios.",
            hardness = "8",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "MgAl₂O₄ (with Fe, Cr)",
            commonColors = listOf("Lavender", "Pale purple", "Lilac", "Light violet"),
            whereFound = listOf("Burma (Myanmar)", "Sri Lanka", "Tanzania", "Vietnam", "Tajikistan"),
            funFacts = listOf(
                "Lavender spinel is one of the rarest color varieties of spinel.",
                "The soft lavender color is caused by trace amounts of iron.",
                "Spinel has been confused with ruby throughout history — the Black Prince's Ruby is actually spinel.",
                "Lavender spinel is increasingly popular as an alternative to more expensive purple gemstones."
            ),
            uses = "Gemstone (faceted), fine jewelry, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "zircon-red",
            name = "Zircon, Red",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Zircon Variety (Red)",
            tagline = "Deep red zircon with high dispersion — fiery brilliance in a rare color.",
            emoji = "🔴",
            colorHex = 0xFFB22222,
            description = "Red Zircon is a rare color variety of zircon displaying deep red to brownish-red colors. Zircon has one of the highest dispersion rates of any natural gemstone, giving cut stones exceptional fire. The red color is caused by trace impurities. Red zircons are primarily found in Southeast Asia.",
            formation = "Found in alluvial placer deposits derived from weathered pegmatites and metamorphic rocks. The red color can be natural or enhanced by heat treatment.",
            hardness = "7–7.5",
            luster = "Vitreous to subadamantine",
            streak = "White",
            crystalSystem = "Tetragonal",
            chemicalFormula = "ZrSiO₄",
            commonColors = listOf("Deep red", "Brownish-red", "Reddish-brown", "Orange-red"),
            whereFound = listOf("Burma (Myanmar)", "Sri Lanka", "Cambodia", "Thailand", "Vietnam"),
            funFacts = listOf(
                "Zircon has the highest dispersion of any natural colorless or near-colorless gemstone — even higher than diamond.",
                "Red zircon is sometimes called 'hyacinth' in older texts.",
                "Zircon is the oldest mineral on Earth — some grains are 4.4 billion years old.",
                "The red color is often concentrated in the core of the crystal, requiring careful cutting."
            ),
            uses = "Gemstone (faceted), jewelry, collector specimens, geochronology.",
            rarity = "Rare"
        ),

        Specimen(
            id = "opal-moss",
            name = "Opal, Moss",
            rockClass = RockClass.CRYSTAL,
            category = "Opal Variety — Moss Opal (Dendritic Inclusions)",
            tagline = "Opal with moss-like dendritic inclusions creating organic green patterns.",
            emoji = "🌿",
            colorHex = 0xFF6B8E6B,
            description = "Moss Opal is a variety of common opal containing dendritic (moss-like) inclusions of manganese oxide or iron oxide. The inclusions create patterns that look like moss, ferns, or tree branches frozen inside the opal. Unlike precious opal, moss opal typically does not show play-of-color, but the dendritic patterns make it attractive for cabochons and carvings.",
            formation = "Forms in volcanic or sedimentary environments where silica-rich fluids deposit opal. Manganese and iron oxides form dendritic patterns within the opal as it solidifies.",
            hardness = "5.5–6.5",
            luster = "Waxy to vitreous",
            streak = "White",
            crystalSystem = "Amorphous",
            chemicalFormula = "SiO₂·nH₂O (with MnO₂/Fe₂O₃ inclusions)",
            commonColors = listOf("White", "Cream", "Yellowish", "With green/black dendrites"),
            whereFound = listOf("Mexico", "Australia", "Honduras", "USA (Nevada, Oregon)", "Peru"),
            funFacts = listOf(
                "The moss-like patterns are dendrites — crystalline growth patterns of manganese oxide.",
                "Moss opal typically lacks play-of-color, unlike precious opal.",
                "Each dendritic pattern is unique — no two moss opals have the same pattern.",
                "The dendrites can look so much like real moss that people sometimes mistake the stone for fossilized plant material."
            ),
            uses = "Cabochons, carvings, beads, collector specimens, jewelry.",
            rarity = "Uncommon"
        ),

        // ════════════════════════════════════════════
        // GOLDSTONE VARIETIES (3)
        // ════════════════════════════════════════════

        Specimen(
            id = "goldstone-green",
            name = "Goldstone, Green",
            rockClass = RockClass.CRYSTAL,
            category = "Synthetic — Chromium-Sparkle Glass",
            tagline = "Green glittering glass with chromium sparkle — a man-made 'stone' with ancient roots.",
            emoji = "🟢",
            colorHex = 0xFF2E8B57,
            description = "Green Goldstone is a synthetic glass material with embedded chromium crystals that create a sparkling, glittery effect. Like all goldstone, it is manufactured rather than natural — glass is infused with metallic crystals while cooling. Green goldstone gets its color and sparkle from chromium compounds. It is popular for cabochons, beads, and carvings.",
            formation = "Manufactured by adding chromium compounds to molten glass. As the glass cools, the chromium forms small crystalline platelets that reflect light, creating sparkle.",
            hardness = "5.5–6.5",
            luster = "Vitreous with metallic sparkle",
            streak = "White",
            crystalSystem = "Amorphous (glass)",
            chemicalFormula = "SiO₂ (glass with Cr₂O₃ crystals)",
            commonColors = listOf("Green", "Dark green with sparkles"),
            whereFound = listOf("Manufactured in Italy, China, and other countries"),
            funFacts = listOf(
                "Goldstone is entirely synthetic — it was invented by Italian monks in the 17th century.",
                "The green variety uses chromium compounds for its color and sparkle.",
                "Despite being synthetic, goldstone has become a popular collector and jewelry material.",
                "The sparkles are actually tiny crystalline platelets that reflect light like mirrors."
            ),
            uses = "Cabochons, beads, carvings, jewelry, decorative objects.",
            rarity = "Common (synthetic)"
        ),

        Specimen(
            id = "goldstone-blue",
            name = "Goldstone, Blue",
            rockClass = RockClass.CRYSTAL,
            category = "Synthetic — Cobalt-Sparkle Glass",
            tagline = "Deep blue glittering glass with cobalt sparkle — the most popular goldstone color.",
            emoji = "🔵",
            colorHex = 0xFF1C5BA0,
            description = "Blue Goldstone is a synthetic glass material with embedded cobalt or copper crystals that create a deep blue, sparkling appearance. It is the most popular variety of goldstone. The deep blue body color with bright blue-white sparkles resembles a starry night sky, making it a favorite for cabochons, beads, and decorative carvings.",
            formation = "Manufactured by adding cobalt compounds to molten glass. The cobalt provides the blue body color, while metallic crystals create the sparkles.",
            hardness = "5.5–6.5",
            luster = "Vitreous with metallic sparkle",
            streak = "White",
            crystalSystem = "Amorphous (glass)",
            chemicalFormula = "SiO₂ (glass with Co/Cu crystals)",
            commonColors = listOf("Deep blue", "Dark blue with sparkles"),
            whereFound = listOf("Manufactured in Italy, China, and other countries"),
            funFacts = listOf(
                "Blue goldstone is the most popular color of goldstone.",
                "The sparkles resemble stars in a night sky, giving it the nickname 'star stone'.",
                "Like all goldstone, it is entirely man-made — a type of aventurine glass.",
                "The blue color comes from cobalt, while the sparkles come from copper or cobalt crystals."
            ),
            uses = "Cabochons, beads, carvings, jewelry, decorative objects.",
            rarity = "Common (synthetic)"
        ),

        Specimen(
            id = "goldstone-brown",
            name = "Goldstone, Brown",
            rockClass = RockClass.CRYSTAL,
            category = "Synthetic — Copper-Sparkle Glass",
            tagline = "The original goldstone — amber-brown glass with brilliant copper sparkle.",
            emoji = "🟤",
            colorHex = 0xFFB87333,
            description = "Brown Goldstone is the original variety of goldstone — a synthetic glass with embedded copper crystals that create a warm, golden-brown sparkle. It was reportedly invented by Italian monks in the 17th century. The amber-brown glass body with brilliant copper sparkles is the classic goldstone appearance that gave the material its name.",
            formation = "Manufactured by adding copper to molten glass. As the glass cools, the copper forms small triangular crystalline platelets that reflect light with a golden-orange sparkle.",
            hardness = "5.5–6.5",
            luster = "Vitreous with metallic sparkle",
            streak = "White",
            crystalSystem = "Amorphous (glass)",
            chemicalFormula = "SiO₂ (glass with Cu₂O crystals)",
            commonColors = listOf("Amber-brown", "Golden-brown with sparkles"),
            whereFound = listOf("Manufactured in Italy, China, and other countries"),
            funFacts = listOf(
                "Brown goldstone is the original variety, reportedly invented by Italian monks in the 1600s.",
                "The copper crystals inside are triangular platelets that catch light like tiny mirrors.",
                "The legend says it was created by accident when copper shavings fell into a vat of molten glass.",
                "Despite being synthetic, it has been collected and used in jewelry for centuries."
            ),
            uses = "Cabochons, beads, carvings, jewelry, decorative objects.",
            rarity = "Common (synthetic)"
        ),

        // ════════════════════════════════════════════
        // OTHER VARIETIES & TRADE-NAME STONES (10)
        // ════════════════════════════════════════════

        Specimen(
            id = "enstatite-golden",
            name = "Enstatite, Golden",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Enstatite Variety (Golden)",
            tagline = "Rare golden-green enstatite gemstone with a warm, honeyed glow.",
            emoji = "🟡",
            colorHex = 0xFFD4A017,
            description = "Golden Enstatite is a gem variety of enstatite displaying a warm golden to greenish-golden color. While most enstatite is too dark or included for gem use, transparent golden enstatite is occasionally found and makes distinctive faceted gems. It is one of the rarer gem minerals.",
            formation = "Found in mafic and ultramafic igneous rocks. The gem-quality golden variety forms in specific conditions that allow transparency.",
            hardness = "5.5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "MgSiO₃ (with Fe)",
            commonColors = listOf("Golden", "Greenish-gold", "Honey-yellow", "Brownish-gold"),
            whereFound = listOf("Sri Lanka", "Burma (Myanmar)", "Tanzania", "India", "South Africa"),
            funFacts = listOf(
                "Golden enstatite is one of the rarer gem minerals.",
                "Some enstatite specimens show a cat's eye effect when cut en cabochon.",
                "Enstatite is a major component of the Earth's upper mantle.",
                "It is also found in meteorites — enstatite chondrites are an important meteorite type."
            ),
            uses = "Gemstone (faceted), collector specimens, jewelry.",
            rarity = "Rare"
        ),

        Specimen(
            id = "atlantisite",
            name = "Atlantisite",
            rockClass = RockClass.METAMORPHIC,
            category = "Rock — Serpentine + Stichtite Composite",
            tagline = "Green serpentine with pink-purple stichtite — found only in Tasmania.",
            emoji = "🟢",
            colorHex = 0xFF4A8B5C,
            description = "Atlantisite is a trade name for a natural composite rock of green serpentine and pink-purple stichtite from Tasmania, Australia. The contrasting green and pink-purple patterns create a distinctive, attractive appearance. It is used for cabochons, carvings, and ornamental objects. The name was coined for its mythical, oceanic green-purple color combination.",
            formation = "Forms in serpentine deposits where stichtite grows as vein fillings and patches within the serpentine matrix. The two minerals form under different conditions but coexist in the same rock.",
            hardness = "3–4.5 (varies between serpentine and stichtite)",
            luster = "Waxy to vitreous",
            streak = "White to greenish",
            crystalSystem = "Monoclinic (composite)",
            chemicalFormula = "Mg₃Si₂O₅(OH)₄ + Mg₆Cr₂CO₃(OH)₁₆·4H₂O",
            commonColors = listOf("Green with purple", "Green with pink", "Mottled green-purple"),
            whereFound = listOf("Tasmania, Australia (only source)"),
            funFacts = listOf(
                "Atlantisite is found in only one place on Earth: Tasmania, Australia.",
                "It is a natural combination of green serpentine and pink-purple stichtite.",
                "The name was inspired by the legendary Atlantis — the green and purple colors evoke the sea.",
                "Each piece has unique patterns depending on how the two minerals are distributed."
            ),
            uses = "Cabochons, carvings, beads, decorative objects, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "girasol",
            name = "Girasol",
            rockClass = RockClass.CRYSTAL,
            category = "Opal Variety — Girasol (Blue Opal)",
            tagline = "Blue opal with a floating, milky glow — the 'turning sun' stone.",
            emoji = "🔵",
            colorHex = 0xFF87CEEB,
            description = "Girasol is a variety of opal that displays a milky, bluish-white floating glow that appears to move as the stone is turned. The name comes from the Italian 'girasole' meaning 'turning sun'. Unlike precious opal, girasol does not show play-of-color — instead, it has a soft, internal luminescence caused by light scattering from microscopic inclusions.",
            formation = "Forms in volcanic environments where silica-rich fluids deposit opal. The milky blue glow is caused by microscopic inclusions that scatter light.",
            hardness = "5.5–6.5",
            luster = "Waxy to vitreous",
            streak = "White",
            crystalSystem = "Amorphous",
            chemicalFormula = "SiO₂·nH₂O",
            commonColors = listOf("Milky blue", "Bluish-white", "Pale blue-white"),
            whereFound = listOf("Madagascar", "Mexico", "Peru", "Ethiopia"),
            funFacts = listOf(
                "The name 'girasol' means 'turning sun' in Italian.",
                "The floating glow appears to move within the stone as it is turned — unlike play-of-color.",
                "Girasol opal is sometimes confused with moonstone, but they are different minerals.",
                "The milky blue glow is caused by light scattering from microscopic inclusions."
            ),
            uses = "Cabochons, beads, carvings, jewelry, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "darwin-glass",
            name = "Darwin Glass",
            rockClass = RockClass.IGNEOUS,
            category = "Impact Glass — Natural Glass from Meteorite Impact",
            tagline = "Dark green impact glass from Tasmania — formed by a meteorite strike 800,000 years ago.",
            emoji = "🟢",
            colorHex = 0xFF2E5C2E,
            description = "Darwin Glass is a natural impact glass found in Tasmania, Australia, formed approximately 800,000 years ago by a meteorite impact. The glass ranges from dark green to black and has a distinctive knobby, twisted, or irregular shape. It is similar to other impact glasses like Libyan Desert Glass but has its own unique characteristics.",
            formation = "Created when a meteorite impacts the Earth, melting the local rock and ejecting molten material that cools into glass as it falls. Darwin glass is found in a strewn field in western Tasmania.",
            hardness = "5–5.5",
            luster = "Vitreous to greasy",
            streak = "White",
            crystalSystem = "Amorphous (natural glass)",
            chemicalFormula = "SiO₂ (with various impurities)",
            commonColors = listOf("Dark green", "Black", "Brownish-green", "Grayish-green"),
            whereFound = listOf("Tasmania, Australia (only source)"),
            funFacts = listOf(
                "Darwin glass was formed by a meteorite impact about 800,000 years ago.",
                "The impact crater has never been found — it may be buried or offshore.",
                "It is found in a 'strewn field' — the area where the glass was ejected.",
                "Darwin glass is distinct from Darwin crater glass, which is found closer to the suspected impact site."
            ),
            uses = "Collector specimens, lapidary, scientific research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "k2-stone",
            name = "K2 Stone",
            rockClass = RockClass.IGNEOUS,
            category = "Granite — Granite with Azurite Inclusions",
            tagline = "White granite with bright blue azurite spots — from the base of K2.",
            emoji = "🔵",
            colorHex = 0xFF4F94CD,
            description = "K2 Stone (also called K2 Granite or K2 Jasper) is a white granite containing bright blue azurite spots, found at the base of K2, the world's second-highest mountain. The combination of white granite with striking blue azurite is unique and creates a distinctive appearance. It is popular for cabochons and decorative objects.",
            formation = "Found as granite boulders at the base of K2 in the Karakoram Range. The azurite fills fractures and pockets in the granite, creating blue spots.",
            hardness = "5–6 (varies between granite and azurite)",
            luster = "Vitreous (azurite) / Dull (granite)",
            streak = "White",
            crystalSystem = "Varies (granite: multiple; azurite: monoclinic)",
            chemicalFormula = "SiO₂ (granite) + Cu₃(CO₃)₂(OH)₂ (azurite)",
            commonColors = listOf("White with blue spots", "Light gray with blue spots"),
            whereFound = listOf("Base of K2, Karakoram Range, Pakistan (only source)"),
            funFacts = listOf(
                "K2 Stone is found at the base of K2, the world's second-highest mountain.",
                "The bright blue spots are azurite — a copper carbonate mineral.",
                "It is sometimes mislabeled as 'K2 Jasper' but it is actually granite.",
                "The extreme altitude and remote location make collecting K2 stone very difficult."
            ),
            uses = "Cabochons, carvings, decorative objects, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "llanite",
            name = "Llanite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Fine-Grained Granite with Blue Quartz",
            tagline = "Unique granite with blue quartz phenocrysts — found only in one Texas location.",
            emoji = "🔷",
            colorHex = 0xFF5F9EA0,
            description = "Llanite (also called Llanolite) is a very rare, fine-grained porphyritic granite containing distinctive blue quartz phenocrysts. It is found only in Llano County, Texas, and is one of the most distinctive granites in the world. The blue color of the quartz is caused by microscopic inclusions of ilmenite.",
            formation = "Forms as a porphyritic granite intrusion. The blue quartz phenocrysts crystallized first from the magma, followed by the finer-grained groundmass.",
            hardness = "6–7",
            luster = "Vitreous to dull",
            streak = "White",
            crystalSystem = "Multiple (granite)",
            chemicalFormula = "SiO₂ (with various minerals)",
            commonColors = listOf("Gray with blue quartz", "Pinkish-gray with blue spots"),
            whereFound = listOf("Llano County, Texas, USA (only source)"),
            funFacts = listOf(
                "Llanite is found in only one place on Earth: Llano County, Texas.",
                "The distinctive blue quartz is caused by microscopic ilmenite inclusions.",
                "It is the official rock of Llano County, Texas.",
                "The blue quartz phenocrysts can be up to 2 cm across."
            ),
            uses = "Decorative stone, collector specimens, cabochons, architectural stone.",
            rarity = "Rare"
        ),

        Specimen(
            id = "celestobarite",
            name = "Celestobarite",
            rockClass = RockClass.MINERAL,
            category = "Sulfate — Celestine-Barite Mix",
            tagline = "Natural mix of celestine and barite with blue and white banding.",
            emoji = "🔵",
            colorHex = 0xFF87CEEB,
            description = "Celestobarite is a natural mixture of celestine (strontium sulfate) and barite (barium sulfate) that forms as a solid solution or intimate intergrowth. It can display blue, white, and sometimes golden colors depending on the ratio of the two minerals. It is found in sedimentary deposits and is popular for cabochons and carvings.",
            formation = "Forms in sedimentary evaporite deposits where both strontium and barium are present. The two minerals crystallize together as a solid solution series.",
            hardness = "3–3.5",
            luster = "Vitreous to pearly",
            streak = "White",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "(Sr,Ba)SO₄",
            commonColors = listOf("Blue", "White", "Golden-yellow", "Blue-white banded"),
            whereFound = listOf("Madagascar", "Britain", "Mexico", "USA"),
            funFacts = listOf(
                "Celestobarite is a natural mixture of celestine and barite — the two minerals form a solid solution.",
                "The blue color comes from the celestine component.",
                "The name combines 'celestine' and 'barite'.",
                "It is popular for cabochons because the blue-white banding is attractive."
            ),
            uses = "Cabochons, carvings, collector specimens, decorative objects.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "flower-stone",
            name = "Flower Stone",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Flower-Patterned Granite (Spherulitic)",
            tagline = "Granite with natural flower-like patterns from radial crystal growth.",
            emoji = "🌸",
            colorHex = 0xFFD4A0A0,
            description = "Flower Stone is a variety of granite (or rhyolite) containing radiating crystal clusters that look like flower petals. The 'flowers' are spherulitic growths of feldspar or quartz that radiate outward from a central point. When cut and polished, these patterns create beautiful, flower-like designs. The most famous source is Korea.",
            formation = "Forms in volcanic or subvolcanic environments where rapid cooling creates spherulitic (radial) crystal growth. The spherulites form as radial clusters of feldspar needles.",
            hardness = "6–7",
            luster = "Vitreous to dull",
            streak = "White",
            crystalSystem = "Multiple (granite)",
            chemicalFormula = "SiO₂ + KAlSi₃O₈ (with various minerals)",
            commonColors = listOf("Pink", "White", "Gray", "With flower patterns"),
            whereFound = listOf("Korea (Jeju Island)", "China", "Japan", "USA"),
            funFacts = listOf(
                "The 'flowers' are actually spherulites — radial growths of feldspar needles.",
                "Korean flower stone from Jeju Island is the most famous source.",
                "Each cut slab reveals different flower patterns — no two are alike.",
                "The stone has been carved and collected in Korea for centuries."
            ),
            uses = "Cabochons, carvings, decorative objects, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "dragon-scale-stone",
            name = "Dragon Scale Stone",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Scaly-Textured Schist (Trade Name)",
            tagline = "Green schist with a dragon-scale texture — a unique trade-name collector stone.",
            emoji = "🐉",
            colorHex = 0xFF2E6B4A,
            description = "Dragon Scale Stone is a trade name for a green metamorphic schist with a distinctive scaly texture that resembles dragon scales. The surface has overlapping, plate-like mineral grains (typically chlorite or mica) that create a reptilian pattern. It is primarily used for cabochons and decorative carvings.",
            formation = "Forms in low-grade metamorphic environments where mica and chlorite minerals grow in aligned, plate-like habits, creating a scaly texture.",
            hardness = "3–4",
            luster = "Pearly to vitreous",
            streak = "White to greenish",
            crystalSystem = "Monoclinic (mica/chlorite)",
            chemicalFormula = "Various silicates (chlorite, mica)",
            commonColors = listOf("Green", "Dark green", "Greenish-gray"),
            whereFound = listOf("China", "Brazil", "USA"),
            funFacts = listOf(
                "The name 'Dragon Scale Stone' comes from its scaly, reptilian texture.",
                "The scales are actually aligned mica or chlorite mineral grains.",
                "When polished, the surface has a chatoyant, shimmery quality.",
                "The green color comes from iron and magnesium in the chlorite."
            ),
            uses = "Cabochons, carvings, decorative objects, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "picture-sandstone",
            name = "Picture Sandstone",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Scenic Patterned Sandstone",
            tagline = "Sandstone with natural patterns that look like desert landscapes and mountains.",
            emoji = "🏞️",
            colorHex = 0xFFCD853F,
            description = "Picture Sandstone is a variety of sandstone from Utah and Arizona that contains natural iron and manganese oxide patterns resembling desert landscapes, mountains, and skies. The patterns are entirely natural — created by iron-rich fluids staining the sandstone in dendritic and scenic patterns. When cut and polished, each piece looks like a painted landscape.",
            formation = "Forms in aeolian (wind-deposited) sandstone. Iron and manganese oxide minerals percolate through the rock, creating dendritic and landscape-like patterns in the sandstone.",
            hardness = "6–7",
            luster = "Dull to earthy",
            streak = "White to yellowish",
            crystalSystem = "Clastic (sedimentary)",
            chemicalFormula = "SiO₂ (with Fe/Mn oxides)",
            commonColors = listOf("Tan", "Brown", "Red", "With dark patterns"),
            whereFound = listOf("Utah, USA", "Arizona, USA", "Colorado, USA"),
            funFacts = listOf(
                "The landscape-like patterns are entirely natural — created by iron and manganese oxide staining.",
                "Each cut piece shows a unique 'painting' of desert scenery.",
                "It is sometimes called 'picture jasper' but it is actually sandstone.",
                "The best specimens come from the Navajo Sandstone formation in Utah."
            ),
            uses = "Decorative slabs, bookends, carvings, collector specimens.",
            rarity = "Uncommon"
        ),

        // ════════════════════════════════════════════
        // TEKTITES & VOLCANIC GLASS (5)
        // ════════════════════════════════════════════

        Specimen(
            id = "inderite",
            name = "Inderite",
            rockClass = RockClass.MINERAL,
            category = "Borate — Magnesium Borate Hydrate",
            tagline = "Rare magnesium borate gem from Kazakhstan — colorless to white crystals.",
            emoji = "⚪",
            colorHex = 0xFFE8E8E0,
            description = "Inderite is a rare magnesium borate mineral found in evaporite deposits. It forms colorless to white, short prismatic or tabular crystals. Named after the Inder Lake region in Kazakhstan where it was first discovered. Inderite is occasionally cut as a gemstone but is quite rare and soft.",
            formation = "Found in continental evaporite deposits, particularly in borate-rich salt lakes. Forms by precipitation from concentrated boron-rich brines.",
            hardness = "3",
            luster = "Vitreous to greasy",
            streak = "White",
            crystalSystem = "Monoclinic",
            chemicalFormula = "MgB₄O₇·5H₂O (Mg[B₃O₃(OH)₅]·5H₂O)",
            commonColors = listOf("Colorless", "White", "Pale yellow"),
            whereFound = listOf("Inder Lake, Kazakhstan", "Boron, California, USA", "Kirka, Turkey"),
            funFacts = listOf(
                "Named after the Inder Lake region in Kazakhstan.",
                "It is a borate mineral — a group that includes borax and colemanite.",
                "Inderite is soft (hardness 3) and water-soluble, making it challenging to use as a gemstone.",
                "It is one of the rarer borate minerals sought by collectors."
            ),
            uses = "Collector specimens, rare gemstone, mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "indochinite",
            name = "Indochinite",
            rockClass = RockClass.IGNEOUS,
            category = "Tektite — Indochina Tektite Variety",
            tagline = "Black tektite glass from Southeast Asia — formed by a cosmic impact 800,000 years ago.",
            emoji = "🌑",
            colorHex = 0xFF1A1A1A,
            description = "Indochinite is a variety of tektite (natural impact glass) found in Southeast Asia (Indochina). Like all tektites, it was formed when a massive meteorite impact melted terrestrial rock and ejected it into the atmosphere, where it cooled into glass before falling back to Earth. Indochinites are typically black, irregularly shaped, and date to approximately 800,000 years ago.",
            formation = "Created by a meteorite impact that melted terrestrial rock. The molten material was ejected into the atmosphere, solidified into glass, and fell back to Earth across a strewn field covering Southeast Asia.",
            hardness = "5–6",
            luster = "Vitreous to submetallic",
            streak = "White (powdered)",
            crystalSystem = "Amorphous (natural glass)",
            chemicalFormula = "SiO₂ (with Al₂O₃, FeO, etc.)",
            commonColors = listOf("Black", "Dark brown (thin edges)", "Opaque black"),
            whereFound = listOf("Vietnam", "Thailand", "Cambodia", "Laos", "Southern China"),
            funFacts = listOf(
                "Indochinites were formed by a massive meteorite impact about 800,000 years ago.",
                "They are part of the Australasian tektite strewn field — the largest on Earth.",
                "The impact crater has never been found but may be in Southeast Asia or the ocean.",
                "Indochinites are distinguished from Australites by their more irregular shapes."
            ),
            uses = "Collector specimens, jewelry (tumbled), scientific research.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "peles-hair",
            name = "Pele's Hair",
            rockClass = RockClass.IGNEOUS,
            category = "Volcanic Glass — Golden Fiber Threads",
            tagline = "Golden volcanic glass threads — named after the Hawaiian goddess of volcanoes.",
            emoji = "💛",
            colorHex = 0xFFDAA520,
            description = "Pele's Hair is a form of volcanic glass that consists of thin, golden, hair-like strands of basaltic glass. It forms when lava is thrown into the air and wind stretches the molten material into fine threads. Named after Pele, the Hawaiian goddess of volcanoes. The strands can be up to 2 meters long and are extremely delicate.",
            formation = "Forms when molten lava is ejected into the air and strong winds stretch the droplets into fine, hair-like threads of glass. Common at lava fountains and ocean entries.",
            hardness = "5–5.5",
            luster = "Vitreous (glassy)",
            streak = "White (powdered)",
            crystalSystem = "Amorphous (natural glass)",
            chemicalFormula = "SiO₂ (basaltic glass)",
            commonColors = listOf("Golden yellow", "Golden brown", "Yellowish-green"),
            whereFound = listOf("Hawaii, USA", "Iceland", "Ethiopia", "Italy (Etna)", "Nicaragua"),
            funFacts = listOf(
                "Named after Pele, the Hawaiian goddess of fire, lightning, and volcanoes.",
                "The hair-like strands can be up to 2 meters (6 feet) long.",
                "It forms when wind stretches molten lava droplets into fine glass threads.",
                "Pele's Hair is extremely fragile and can be harmful if inhaled or touched.",
                "It is the volcanic equivalent of fiberglass."
            ),
            uses = "Collector specimens, scientific research, educational displays.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "pearlite",
            name = "Pearlite",
            rockClass = RockClass.IGNEOUS,
            category = "Volcanic Glass — Perlite (Hydrated Obsidian)",
            tagline = "Volcanic glass with a perlite structure — expands when heated.",
            emoji = "🟤",
            colorHex = 0xFF8B7355,
            description = "Pearlite (or Perlite) is a volcanic glass characterized by a distinctive 'perlite structure' — concentric shells of cracks that cause the glass to break into small, round, pearl-like bodies. It is a hydrated form of obsidian. When heated to high temperatures, perlite expands dramatically (up to 20 times its original volume) and is used commercially for insulation, soil amendment, and filtration.",
            formation = "Forms from volcanic glass (obsidian) that has been hydrated by groundwater. The perlite structure develops as the glass shrinks and cracks during cooling and hydration.",
            hardness = "5–5.5",
            luster = "Vitreous to pearly",
            streak = "White",
            crystalSystem = "Amorphous (natural glass)",
            chemicalFormula = "SiO₂·nH₂O (hydrated volcanic glass)",
            commonColors = listOf("Gray", "Brown", "Black", "Greenish-gray"),
            whereFound = listOf("New Mexico, USA", "Greece", "Turkey", "Hungary", "Japan"),
            funFacts = listOf(
                "Perlite expands up to 20 times its original volume when heated to 900°C.",
                "Expanded perlite is used in potting soil, insulation, and water filtration.",
                "The 'perlite structure' is a network of concentric cracks that creates pearl-like fragments.",
                "It is essentially hydrated obsidian — volcanic glass that has absorbed water over time."
            ),
            uses = "Construction (insulation), horticulture (soil amendment), filtration, collector specimens.",
            rarity = "Common"
        ),

        Specimen(
            id = "irghizite",
            name = "Irghizite",
            rockClass = RockClass.IGNEOUS,
            category = "Impact Glass — Irghiz River (Zhamanshin Crater)",
            tagline = "Rare impact glass from Kazakhstan — unique layered and sculpted forms.",
            emoji = "⬛",
            colorHex = 0xFF2D2D2D,
            description = "Irghizite is a rare impact glass found near the Irghiz River in Kazakhstan, associated with the Zhamanshin meteorite crater. It is distinctive among impact glasses for its layered, droplet-shaped, and sculpted forms. The glass is typically dark and has a complex chemistry indicating it formed from a mix of target rocks during the impact event.",
            formation = "Created by the Zhamanshin meteorite impact approximately 900,000 years ago. Molten target rock was ejected and formed droplet-shaped and layered glass bodies.",
            hardness = "5–5.5",
            luster = "Vitreous to greasy",
            streak = "White (powdered)",
            crystalSystem = "Amorphous (natural glass)",
            chemicalFormula = "SiO₂ (with Al₂O₃, FeO, etc.)",
            commonColors = listOf("Black", "Dark brown", "Dark gray"),
            whereFound = listOf("Zhamanshin Crater, Kazakhstan (only source)"),
            funFacts = listOf(
                "Irghizite is found only at the Zhamanshin meteorite crater in Kazakhstan.",
                "The Zhamanshin crater is one of the best-preserved meteorite impact craters on Earth.",
                "Irghizite has a layered structure that is unique among impact glasses.",
                "The impact occurred approximately 900,000 years ago."
            ),
            uses = "Collector specimens, scientific research, meteorite impact studies.",
            rarity = "Very Rare"
        ),

        // ════════════════════════════════════════════
        // ADDITIONAL (3)
        // ════════════════════════════════════════════

        Specimen(
            id = "yttrofluorite",
            name = "Yttrofluorite",
            rockClass = RockClass.MINERAL,
            category = "Halide — Yttrium-Rich Fluorite Variety",
            tagline = "Rare yttrium-bearing fluorite with a distinctive color and fluorescence.",
            emoji = "🟣",
            colorHex = 0xFF7B6BA8,
            description = "Yttrofluorite is a rare variety of fluorite where yttrium partially replaces calcium in the crystal structure. It can display unusual colors including pink, violet, and yellow, and often has distinctive fluorescence. The yttrium content also makes it slightly radioactive. It is found in a few rare-earth-bearing pegmatites.",
            formation = "Found in rare-earth-bearing granitic pegmatites and hydrothermal veins. The yttrium replaces calcium in the fluorite structure.",
            hardness = "4",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "(Ca,Y)F₂₋ₓ",
            commonColors = listOf("Pink", "Violet", "Yellow", "Pale purple"),
            whereFound = listOf("Pikes Peak, Colorado, USA", "Sweden", "Norway", "Russia"),
            funFacts = listOf(
                "Yttrofluorite is a rare yttrium-bearing variety of fluorite.",
                "The yttrium content can make it slightly radioactive.",
                "It often shows unusual fluorescence colors under UV light.",
                "The yttrium substitution distorts the crystal lattice, sometimes making it slightly anisotropic."
            ),
            uses = "Collector specimens, yttrium source (minor), mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "kammererite",
            name = "Kammererite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Chromium-Rich Chlorite (Clinochlore Variety)",
            tagline = "Rare purple chromium-rich chlorite — one of the most beautiful rare minerals.",
            emoji = "🟣",
            colorHex = 0xFF7B4FA8,
            description = "Kammererite (also spelled Kammererite) is a rare chromium-rich variety of clinochlore (a chlorite mineral). It displays a beautiful purple to reddish-purple color caused by chromium substitution. The mineral typically forms scaly or micaceous aggregates rather than large crystals. It is one of the most prized chlorite varieties for collectors.",
            formation = "Found in chromite deposits and serpentinites where chromium-rich fluids interact with chlorite minerals. Associated with chromite, uvarovite, and other chromium minerals.",
            hardness = "2–2.5",
            luster = "Pearly to vitreous",
            streak = "White to pale green",
            crystalSystem = "Monoclinic",
            chemicalFormula = "(Mg,Cr)₅Al(Si₃Al)O₁₀(OH)₈",
            commonColors = listOf("Purple", "Reddish-purple", "Lilac", "Violet-purple"),
            whereFound = listOf("Kop Krom Mine, Turkey", "Finland", "Russia (Ural Mountains)", "Pennsylvania, USA"),
            funFacts = listOf(
                "Kammererite is one of the few naturally purple silicate minerals.",
                "The purple color is caused by chromium replacing magnesium in the crystal structure.",
                "It is a variety of clinochlore — the same mineral group as seraphinite.",
                "Despite its beauty, it is too soft for most jewelry applications."
            ),
            uses = "Collector specimens, cabochons (with care), mineralogical research.",
            rarity = "Very Rare"
        ),

        Specimen(
            id = "veszelyite",
            name = "Veszelyite",
            rockClass = RockClass.MINERAL,
            category = "Phosphate — Copper Zinc Phosphate Hydroxide",
            tagline = "Rare blue-green copper zinc phosphate forming delicate drusy crystals.",
            emoji = "🔵",
            colorHex = 0xFF2E8B8B,
            description = "Veszelyite is a rare copper zinc phosphate mineral with a beautiful blue-green to greenish-blue color. It typically forms small, drusy crystals or crusts on matrix. The mineral is found in the oxidized zones of copper-zinc deposits and is highly prized by collectors for its vibrant color and rarity.",
            formation = "Forms in the oxidized zones of copper-zinc deposits. Associated with other secondary copper minerals like malachite, azurite, and hemimorphite.",
            hardness = "3.5–4",
            luster = "Vitreous",
            streak = "White to pale blue-green",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "(Cu,Zn)₃(PO₄)(OH)₃·2H₂O",
            commonColors = listOf("Blue-green", "Greenish-blue", "Deep blue"),
            whereFound = listOf("Tsumeb, Namibia", "Mapimi, Mexico", "Silesia, Poland (type locality)", "USA (various)"),
            funFacts = listOf(
                "Veszelyite is one of the rarest copper phosphate minerals.",
                "The best specimens come from Tsumeb, Namibia.",
                "It is named after Hungarian engineer Adolph Vészely.",
                "The blue-green color makes it a favorite of advanced mineral collectors."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Very Rare"
        )
    )
}
