package com.rork.rockscout.data

/**
 * Database expansion wave 16 — Phase 1: High-priority collectible minerals.
 * Sulfides, oxides, halides, carbonates, phosphates, silicates, sulfates,
 * gemstone varieties, and specialty stones from Wikipedia/mindat reference sources.
 * Includes user-requested Seraphinite and Ajoite.
 * Generated 2026-07-22.
 */
@Suppress("unused")
object ExpansionBatch16 {
    val specimens: List<Specimen> = listOf(

        // ════════════════════════════════════════════
        // SULFIDES & SULFOSALTS (12)
        // ════════════════════════════════════════════

        Specimen(
            id = "proustite",
            name = "Proustite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Silver Arsenic Sulfosalt",
            tagline = "Ruby silver — a deep scarlet ore prized by miners for its silver content.",
            emoji = "🔴",
            colorHex = 0xFFB22222,
            description = "Proustite is a silver arsenic sulfosalt mineral known for its striking ruby-red color. It was historically one of the most important silver ores, sometimes called 'ruby silver' or 'light red silver ore'. The deep crimson color comes from the silver content. Crystals are typically prismatic or scalenohedral, and the mineral is transparent to translucent with a brilliant adamantine to submetallic luster.",
            formation = "Forms in hydrothermal veins at moderate temperatures, typically associated with other silver minerals, galena, sphalerite, and calcite. Found in the oxidized zones of silver deposits.",
            hardness = "2–2.5",
            luster = "Adamantine to submetallic",
            streak = "Scarlet-red",
            crystalSystem = "Trigonal",
            chemicalFormula = "Ag₃AsS₃",
            commonColors = listOf("Deep red", "Scarlet", "Crimson"),
            whereFound = listOf("Freiberg, Germany", "Chañarcillo, Chile", "Pribram, Czech Republic", "Guanajuato, Mexico", "Cobalt, Ontario, Canada"),
            funFacts = listOf(
                "Also known as 'ruby silver' or 'light red silver ore' in mining communities.",
                "Proustite was named after French chemist Joseph Louis Proust in 1832.",
                "The mineral darkens on exposure to light — old specimens are often nearly black on the surface.",
                "It was a major silver ore in the famous Chañarcillo district of Chile."
            ),
            uses = "Silver ore, collector specimens, historical importance in mining.",
            rarity = "Rare"
        ),

        Specimen(
            id = "polybasite",
            name = "Polybasite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Silver Antimony Arsenic Sulfosalt",
            tagline = "Black to steel-gray silver sulfosalt with tabular, pseudohexagonal crystals.",
            emoji = "⬛",
            colorHex = 0xFF2F2F2F,
            description = "Polybasite is a silver antimony arsenic sulfosalt that forms distinctive tabular, pseudohexagonal crystals. It is an important silver ore and often occurs with proustite, pyrargyrite, and other silver minerals. The name comes from the Greek 'poly' (many) and 'basis' (base), referring to its many crystal forms.",
            formation = "Forms in mesothermal to epithermal hydrothermal veins associated with silver and lead mineralization. Common in the oxidized and supergene zones of silver deposits.",
            hardness = "2–3",
            luster = "Metallic",
            streak = "Black to iron-black",
            crystalSystem = "Monoclinic (pseudohexagonal)",
            chemicalFormula = "[(Ag,Cu)₆Sb₂S₇][Ag₉SbS₃]",
            commonColors = listOf("Iron-black", "Steel-gray", "Dark gray"),
            whereFound = listOf("Guanajuato, Mexico", "Freiberg, Germany", "Chañarcillo, Chile", "Cobalt, Ontario, Canada", "Peru"),
            funFacts = listOf(
                "The name 'polybasite' means 'many bases' in Greek, referencing its numerous crystal forms.",
                "Crystals are often well-formed tabular plates with a hexagonal outline.",
                "It is one of the few minerals that shows a perfect basal cleavage among the silver sulfosalts."
            ),
            uses = "Silver ore, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "stephanite",
            name = "Stephanite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Silver Antimony Sulfosalt",
            tagline = "Brittle silver ore — a historically significant silver mineral with prismatic crystals.",
            emoji = "🌑",
            colorHex = 0xFF3B3B3B,
            description = "Stephanite is a silver antimony sulfosalt that was historically one of the most important silver ores. It is often called 'brittle silver ore' because of its brittleness. Crystals are orthorhombic, typically short prismatic to tabular, and the mineral has a black to dark gray metallic appearance.",
            formation = "Forms in hydrothermal veins at moderate to low temperatures, commonly associated with galena, sphalerite, tetrahedrite, and other silver minerals.",
            hardness = "2–2.5",
            luster = "Metallic",
            streak = "Black",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Ag₅SbS₄",
            commonColors = listOf("Iron-black", "Dark gray", "Black"),
            whereFound = listOf("Freiberg, Germany", "Comstock Lode, Nevada, USA", "Guanajuato, Mexico", "Pribram, Czech Republic", "Cobalt, Ontario, Canada"),
            funFacts = listOf(
                "Named after Archduke Stephan of Austria, a mineral collector.",
                "Historically called 'brittle silver ore' or 'black silver' by miners.",
                "It was a primary ore at the famous Comstock Lode in Nevada.",
                "Despite its metallic appearance, it is quite soft and brittle."
            ),
            uses = "Silver ore, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "dyscrasite",
            name = "Dyscrasite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Silver Antimonide",
            tagline = "Rare silver antimonide with bright silvery luster and intricate crystal forms.",
            emoji = "⚪",
            colorHex = 0xFFC0C0C0,
            description = "Dyscrasite is a rare silver antimonide mineral with a bright, silvery-white metallic luster. It forms distinctive cyclic twinned crystals that can create star-like or reticulated patterns. The name comes from the Greek 'dyskrasia' meaning 'bad mixture', referring to its difficult chemical analysis.",
            formation = "Occurs in hydrothermal silver deposits, typically associated with native silver, proustite, polybasite, calcite, and barite.",
            hardness = "3.5–4",
            luster = "Metallic",
            streak = "Silvery-white",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Ag₃Sb",
            commonColors = listOf("Silvery-white", "Tin-white", "Light gray"),
            whereFound = listOf("Freiberg, Germany", "Cobalt, Ontario, Canada", "Andreasberg, Germany", "Guanajuato, Mexico"),
            funFacts = listOf(
                "The name comes from Greek for 'bad mixture' due to its tricky chemistry.",
                "Forms spectacular cyclic twins that look like six-pointed stars.",
                "It tarnishes to a darker color on exposure to air.",
                "One of the few silver minerals that is not a sulfide or sulfosalt."
            ),
            uses = "Silver ore, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "nickeline",
            name = "Nickeline",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Nickel Arsenide",
            tagline = "Copper-red nickel arsenide — the original 'kupfernickel' that gave nickel its name.",
            emoji = "🟤",
            colorHex = 0xFFB87333,
            description = "Nickeline (formerly niccolite) is a nickel arsenide mineral with a distinctive copper-red to bronze color. It was the original 'kupfernickel' (copper-devil) ore that frustrated medieval Saxon miners who thought it was copper ore but could not extract copper from it. Later, it became the source of the element nickel, which was named from this mineral.",
            formation = "Forms in hydrothermal veins associated with cobalt and silver minerals, typically in medium-temperature deposits. Also found in some magmatic sulfide deposits.",
            hardness = "5–5.5",
            luster = "Metallic",
            streak = "Brownish-black",
            crystalSystem = "Hexagonal",
            chemicalFormula = "NiAs",
            commonColors = listOf("Copper-red", "Bronze", "Pinkish-tarnished"),
            whereFound = listOf("Cobalt, Ontario, Canada", "Freiberg, Germany", "Kongsberg, Norway", "Bou Azzer, Morocco", "Eastern Oregon, USA"),
            funFacts = listOf(
                "The name 'nickel' comes from this mineral — 'kupfernickel' meant 'copper-devil' in German.",
                "Medieval miners cursed it because it looked like copper ore but yielded no copper.",
                "It tarnishes quickly to a dark, iridescent surface when exposed to air.",
                "The element nickel was first isolated from this mineral by Axel Fredrik Cronstedt in 1751."
            ),
            uses = "Nickel ore, collector specimens, historical importance.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "tennantite",
            name = "Tennantite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Copper Iron Arsenic Sulfosalt",
            tagline = "Gray metallic sulfosalt in the tetrahedrite group, often twinned.",
            emoji = "🔺",
            colorHex = 0xFF4A4A4A,
            description = "Tennantite is a copper iron arsenic sulfosalt in the tetrahedrite group. It is the arsenic analog of tetrahedrite and forms similar tetrahedral crystals. The mineral is typically steel-gray to iron-black with a metallic luster and is an important copper ore in some districts.",
            formation = "Forms in hydrothermal veins at moderate temperatures, often associated with tetrahedrite, chalcopyrite, galena, sphalerite, and quartz.",
            hardness = "3.5–4.5",
            luster = "Metallic",
            streak = "Black to brownish-black",
            crystalSystem = "Isometric",
            chemicalFormula = "Cu₁₂As₄S₁₃",
            commonColors = listOf("Steel-gray", "Iron-black", "Dark gray"),
            whereFound = listOf("Tsumeb, Namibia", "Bingham, Utah, USA", "Butte, Montana, USA", "Llallagua, Bolivia", "Cornwall, England"),
            funFacts = listOf(
                "Named after English chemist Smithson Tennant, who also discovered the elements osmium and iridium.",
                "It forms a complete solid solution series with tetrahedrite (the antimony analog).",
                "Tsumeb, Namibia produced some of the world's finest tennantite crystals.",
                "Some varieties fluoresce under UV light, which is unusual for sulfides."
            ),
            uses = "Copper ore, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "alabandite",
            name = "Alabandite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Manganese Sulfide",
            tagline = "Dark manganese sulfide named after an ancient city in Caria.",
            emoji = "⬛",
            colorHex = 0xFF1A1A1A,
            description = "Alabandite is a manganese sulfide mineral that forms dark, iron-black masses and granular aggregates. It is rarely found in well-developed crystals and typically occurs as compact, granular, or embedded masses in manganese deposits. The name derives from Alabanda, an ancient city in Caria (modern Turkey) where the mineral was supposedly first found.",
            formation = "Forms in hydrothermal manganese deposits, hot spring deposits, and some metamorphic manganese-rich environments. Often associated with rhodochrosite, rhodonite, and other manganese minerals.",
            hardness = "3.5–4",
            luster = "Submetallic",
            streak = "Greenish-black to brown",
            crystalSystem = "Isometric",
            chemicalFormula = "MnS",
            commonColors = listOf("Iron-black", "Dark gray", "Brownish-black"),
            whereFound = listOf("Alabanda, Turkey", "Egas Moniz, Portugal", "Trepca, Kosovo", "Chita, Russia", "Sao Paulo, Brazil"),
            funFacts = listOf(
                "Named after the ancient city of Alabanda in Caria (modern-day Turkey).",
                "It is one of the few manganese sulfide minerals.",
                "Fresh surfaces are almost black but may tarnish to a brownish hue.",
                "It can be a significant manganese ore in some deposits."
            ),
            uses = "Manganese ore, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "cubanite",
            name = "Cubanite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Copper Iron Sulfide",
            tagline = "Bronze-colored copper iron sulfide known for its distinctive orthorhombic twins.",
            emoji = "🟫",
            colorHex = 0xFF8B5A2B,
            description = "Cubanite is a copper iron sulfide mineral with a bronze to bronze-brown color. It is closely related to chalcopyrite but has a different crystal structure. Cubanite forms tabular to thick tabular crystals, often twinned, and is an important mineral in some copper deposits. Despite its name, it is not exclusive to Cuba.",
            formation = "Forms in high-temperature hydrothermal sulfide deposits, often associated with pyrrhotite, chalcopyrite, and magnetite. Also found in some magmatic nickel-copper deposits.",
            hardness = "3.5–4",
            luster = "Metallic",
            streak = "Black",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "CuFe₂S₃",
            commonColors = listOf("Bronze", "Bronze-brown", "Golden-brown"),
            whereFound = listOf("Baracoa, Cuba", "Sudbury, Ontario, Canada", "Duluth, Minnesota, USA", "Bushveld, South Africa", "Outokumpu, Finland"),
            funFacts = listOf(
                "Named after Cuba, where it was first discovered in 1843.",
                "It forms a distinctive twin pattern known as 'cubanite twins'.",
                "It is one of the few sulfides that is magnetic.",
                "Cubanite inclusions in sphalerite create a classic 'stars and stripes' texture under the microscope."
            ),
            uses = "Copper ore, collector specimens, important for geological studies.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "bournonite",
            name = "Bournonite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Copper Lead Antimony Sulfosalt",
            tagline = "Wheel ore — distinctive cogwheel-twinned crystals of copper, lead, and antimony.",
            emoji = "⚙️",
            colorHex = 0xFF3D3D3D,
            description = "Bournonite is a copper lead antimony sulfosalt famous for its distinctive cogwheel-shaped twinned crystals. It is historically known as 'wheel ore' or 'endellionite'. The mineral has a steel-gray to iron-black metallic luster and forms short prismatic to tabular crystals that are frequently twinned in a cyclic pattern resembling gear teeth.",
            formation = "Forms in mesothermal hydrothermal veins associated with galena, chalcopyrite, sphalerite, tetrahedrite, and quartz.",
            hardness = "2.5–3",
            luster = "Metallic",
            streak = "Black",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "CuPbSbS₃",
            commonColors = listOf("Steel-gray", "Iron-black", "Dark gray"),
            whereFound = listOf("Endellion, Cornwall, England", "Pribram, Czech Republic", "Baia Mare, Romania", "Tsumeb, Namibia", "Oruro, Bolivia"),
            funFacts = listOf(
                "Also called 'wheel ore' because of its cogwheel-shaped twinned crystals.",
                "First described from Endellion, Cornwall, England — hence the old name 'endellionite'.",
                "Named after French crystallographer Jacques Louis de Bournon.",
                "The cogwheel twins are among the most recognizable crystal forms in mineralogy."
            ),
            uses = "Copper and lead ore, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "boulangerite",
            name = "Boulangerite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Lead Antimony Sulfosalt",
            tagline = "Plumose lead antimony sulfosalt forming feathery, hair-like crystal masses.",
            emoji = "🪶",
            colorHex = 0xFF2E2E2E,
            description = "Boulangerite is a lead antimony sulfosalt that typically forms fibrous, feathery, or plumose aggregates. It has a lead-gray to dark gray metallic appearance and can form dense mats of fine, hair-like crystals. The mineral is sometimes called 'plumosite' because of its feathery, plumose habit.",
            formation = "Forms in hydrothermal veins at moderate to low temperatures, typically associated with galena, stibnite, sphalerite, and quartz.",
            hardness = "2.5–3",
            luster = "Metallic",
            streak = "Brownish-black",
            crystalSystem = "Monoclinic",
            chemicalFormula = "Pb₅Sb₄S₁₁",
            commonColors = listOf("Lead-gray", "Dark gray", "Bluish-gray"),
            whereFound = listOf("Baiut, Romania", "Pezinok, Slovakia", "Madan, Bulgaria", "Fresnillo, Mexico", "Coeur d'Alene, Idaho, USA"),
            funFacts = listOf(
                "Also known as 'plumosite' due to its feathery, plumose crystal habit.",
                "Named after French mining engineer Charles Boulanger.",
                "It can form spectacular hair-like mats that look like metallic felt.",
                "It is a significant lead ore in some districts."
            ),
            uses = "Lead ore, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "miargyrite",
            name = "Miargyrite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Silver Antimony Sulfosalt",
            tagline = "Dark silver antimony sulfosalt with a red-black streak.",
            emoji = "🌑",
            colorHex = 0xFF1C1C1C,
            description = "Miargyrite is a silver antimony sulfosalt mineral with a dark, almost black metallic appearance. It is distinguished from other silver sulfosalts by its reddish-black streak. The name comes from the Greek 'meion' (less) and 'argyros' (silver), referring to its lower silver content compared to other silver minerals.",
            formation = "Forms in epithermal to mesothermal hydrothermal veins associated with other silver minerals, galena, sphalerite, and quartz.",
            hardness = "2–2.5",
            luster = "Metallic",
            streak = "Reddish-black",
            crystalSystem = "Monoclinic",
            chemicalFormula = "AgSbS₂",
            commonColors = listOf("Iron-black", "Dark gray", "Steel-gray"),
            whereFound = listOf("Freiberg, Germany", "Wolfsberg, Germany", "Pribram, Czech Republic", "Chañarcillo, Chile", "Cobalt, Ontario, Canada"),
            funFacts = listOf(
                "The name means 'less silver' in Greek, because it has less silver than other silver minerals.",
                "Its reddish-black streak is a key diagnostic feature.",
                "It is one of the rarer silver sulfosalt minerals.",
                "Crystals are typically small and tabular, often twinned."
            ),
            uses = "Silver ore, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "greenockite",
            name = "Greenockite",
            rockClass = RockClass.MINERAL,
            category = "Sulfide — Cadmium Sulfide",
            tagline = "Rare yellow cadmium sulfide — the only ore mineral of cadmium.",
            emoji = "🟡",
            colorHex = 0xFFDAA520,
            description = "Greenockite is a rare cadmium sulfide mineral and the only significant ore of cadmium. It has a distinctive yellow to yellow-orange color and typically occurs as earthy coatings or as small hexagonal crystals on other minerals. It is almost always found as a secondary mineral associated with sphalerite and other zinc minerals.",
            formation = "Forms as a secondary mineral in the oxidized zones of zinc and lead deposits. Cadmium is released during the weathering of cadmium-rich sphalerite and precipitates as greenockite.",
            hardness = "3–3.5",
            luster = "Adamantine to resinous",
            streak = "Yellow to reddish-yellow",
            crystalSystem = "Hexagonal",
            chemicalFormula = "CdS",
            commonColors = listOf("Yellow", "Yellow-orange", "Orange-yellow"),
            whereFound = listOf("Greenock, Scotland", "Pribram, Czech Republic", "Joplin, Missouri, USA", "Raibl, Italy", "Tsumeb, Namibia"),
            funFacts = listOf(
                "It is the only mineral that is a significant source of cadmium.",
                "Named after Greenock, Scotland where it was first found in 1840.",
                "It is almost always found as tiny crystals or earthy coatings on sphalerite.",
                "Cadmium from greenockite is used in rechargeable batteries and pigments."
            ),
            uses = "Cadmium ore, collector specimens, source of cadmium for industrial use.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // OXIDES (10)
        // ════════════════════════════════════════════

        Specimen(
            id = "perovskite",
            name = "Perovskite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Calcium Titanium Oxide",
            tagline = "Black to brown calcium titanium oxide — namesake of the most famous crystal structure in materials science.",
            emoji = "🟫",
            colorHex = 0xFF4A3520,
            description = "Perovskite is a calcium titanium oxide mineral that gives its name to the perovskite crystal structure — one of the most important structures in materials science. The mineral itself is typically dark, forming cubic or pseudo-cubic crystals. Recent interest in 'perovskite solar cells' has made this mineral name widely known beyond geology.",
            formation = "Found in ultramafic igneous rocks, particularly kimberlites and carbonatites. Also common in contact metamorphic zones in skarn deposits and in some schists.",
            hardness = "5–5.5",
            luster = "Submetallic to adamantine",
            streak = "White to grayish-white",
            crystalSystem = "Orthorhombic (pseudo-cubic)",
            chemicalFormula = "CaTiO₃",
            commonColors = listOf("Black", "Dark brown", "Yellow-brown"),
            whereFound = listOf("Akhmata, Ural Mountains, Russia", "Dunedin, New Zealand", "Langesundsfjord, Norway", "Eifel, Germany", "Slyudyanka, Russia"),
            funFacts = listOf(
                "The 'perovskite structure' is one of the most important crystal structures in all of materials science.",
                "Named after Russian mineralogist Lev Perovski.",
                "Perovskite solar cells are revolutionizing renewable energy technology.",
                "It is a common mineral in Earth's deep mantle, where it is the most abundant mineral phase."
            ),
            uses = "Source of titanium, research material, collector specimens, namesake of perovskite materials.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "zincite",
            name = "Zincite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Zinc Oxide",
            tagline = "Vibrant red zinc oxide — naturally found in only one place on Earth.",
            emoji = "🔴",
            colorHex = 0xFFCC3322,
            description = "Zincite is a zinc oxide mineral famous for its deep red to orange-red color. Natural zincite is found in significant amounts at only one location in the world — the zinc manganese deposits of Franklin and Sterling Hill, New Jersey. Synthetic zincite crystals are grown commercially and are often more brightly colored than natural specimens.",
            formation = "Naturally forms in metamorphosed zinc-manganese deposits, particularly at Franklin and Sterling Hill, New Jersey. Also found as a product of furnace fumes in zinc smelters.",
            hardness = "4–4.5",
            luster = "Submetallic to resinous",
            streak = "Yellow to orange-yellow",
            crystalSystem = "Hexagonal",
            chemicalFormula = "ZnO",
            commonColors = listOf("Deep red", "Orange-red", "Yellow-orange"),
            whereFound = listOf("Franklin, New Jersey, USA", "Sterling Hill, New Jersey, USA", "Lubin, Poland (smelter)", "Italy (Vesuvius fumerole)"),
            funFacts = listOf(
                "Natural zincite crystals are found in significant quantities at only one place on Earth: Franklin, New Jersey.",
                "Synthetic zincite is grown commercially and often has brighter, more vibrant colors.",
                "Zincite is strongly piezoelectric and pyroelectric.",
                "Franklin zincite is fluorescent under UV light, glowing bright green."
            ),
            uses = "Zinc ore, collector specimens, piezoelectric applications, phosphors.",
            rarity = "Rare (natural specimens)"
        ),

        Specimen(
            id = "gahnite",
            name = "Gahnite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Zinc Aluminum Spinel",
            tagline = "Dark green to black zinc aluminum spinel from metamorphic environments.",
            emoji = "🟢",
            colorHex = 0xFF2E4E2E,
            description = "Gahnite is a zinc aluminum spinel mineral that typically forms dark green to black octahedral crystals. It is a member of the spinel group and is common in metamorphosed zinc-rich deposits. Named after Swedish chemist Johan Gottlieb Gahn, who discovered manganese.",
            formation = "Found in metamorphosed massive sulfide deposits, granitic pegmatites, and contact metamorphic zones. Often associated with sphalerite, franklinite, and other zinc minerals.",
            hardness = "7.5–8",
            luster = "Vitreous to submetallic",
            streak = "Gray to brownish",
            crystalSystem = "Isometric",
            chemicalFormula = "ZnAl₂O₄",
            commonColors = listOf("Dark green", "Blue-green", "Black", "Brownish-green"),
            whereFound = listOf("Franklin, New Jersey, USA", "Charlemont, Massachusetts, USA", "Falun, Sweden", "Ampangabe, Madagascar", "Broken Hill, Australia"),
            funFacts = listOf(
                "Named after Johan Gottlieb Gahn, the Swedish chemist who discovered manganese.",
                "It is the zinc member of the spinel group.",
                "Octahedral crystals are characteristic, though they can be small.",
                "It is an indicator mineral for zinc-rich metamorphic environments."
            ),
            uses = "Indicator mineral for zinc deposits, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "tantalite",
            name = "Tantalite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Tantalum Iron Oxide",
            tagline = "Heavy black tantalum ore — the source of tantalum for electronics.",
            emoji = "⬛",
            colorHex = 0xFF1A1A1A,
            description = "Tantalite is a tantalum iron oxide mineral and the primary ore of tantalum. It is a member of the columbite-tantalite (coltan) series and forms dark, heavy, submetallic crystals. Tantalum extracted from tantalite is critical for the manufacture of electronic capacitors used in cell phones and computers.",
            formation = "Found in granitic pegmatites and alluvial deposits derived from weathered pegmatites. Associated with cassiterite, beryl, spodumene, and other pegmatite minerals.",
            hardness = "6–6.5",
            luster = "Submetallic to resinous",
            streak = "Dark red to brown",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "(Fe,Mn)Ta₂O₆",
            commonColors = listOf("Iron-black", "Dark brown", "Black"),
            whereFound = listOf("Bikita, Zimbabwe", "Greenbushes, Australia", "Manono, DR Congo", "Tanco Mine, Manitoba, Canada", "Mozambique"),
            funFacts = listOf(
                "Tantalum from tantalite is essential for manufacturing capacitors in cell phones and computers.",
                "It forms a solid solution series with columbite — together they are called 'coltan'.",
                "Named after Tantalus from Greek mythology (because it was difficult to dissolve).",
                "Coltan mining has been linked to conflict in the Democratic Republic of Congo."
            ),
            uses = "Tantalum ore for electronic components, surgical implants, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "bixbyite",
            name = "Bixbyite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Manganese Iron Oxide",
            tagline = "Cubic black manganese iron oxide from volcanic environments.",
            emoji = "⬛",
            colorHex = 0xFF2B2B2B,
            description = "Bixbyite is a manganese iron oxide mineral that forms distinctive cubic crystals. It is typically found in volcanic environments, particularly in rhyolitic tuffs. The mineral is dark to iron-black with a submetallic luster and is often associated with topaz, beryl, and pseudobrookite.",
            formation = "Occurs in volcanic tuffs and rhyolites, typically formed by pneumatolytic or hydrothermal processes in volcanic environments.",
            hardness = "6–6.5",
            luster = "Submetallic",
            streak = "Black to brownish-black",
            crystalSystem = "Isometric",
            chemicalFormula = "MnFe₂O₄ (Mn,Fe)₂O₃",
            commonColors = listOf("Iron-black", "Dark gray", "Black"),
            whereFound = listOf("Thomas Range, Utah, USA", "Dugway, Utah, USA", "Lake County, Oregon, USA", "Ciudad Victoria, Mexico", "Singhbhum, India"),
            funFacts = listOf(
                "Named after Maynard Bixby, an American mineralogist.",
                "It forms perfect cubic crystals, which is unusual for an oxide mineral.",
                "The best specimens come from the Thomas Range in Utah.",
                "Often found with topaz and beryl in volcanic tuffs."
            ),
            uses = "Collector specimens, minor manganese ore.",
            rarity = "Rare"
        ),

        Specimen(
            id = "baddeleyite",
            name = "Baddeleyite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Zirconium Oxide",
            tagline = "Brown zirconium oxide — a rare mineral used for dating ancient rocks.",
            emoji = "🟫",
            colorHex = 0xFF5C3D1E,
            description = "Baddeleyite is a zirconium oxide mineral that is important both as a zirconium source and as a geochronology tool. It is used for uranium-lead dating of silica-undersaturated rocks where zircon is absent. The mineral is typically brown, tabular to prismatic, and has a high luster.",
            formation = "Found in silica-undersaturated igneous rocks (carbonatites, kimberlites, alkaline syenites) and in some metamorphic rocks. Also occurs in alluvial placer deposits.",
            hardness = "6.5",
            luster = "Vitreous to submetallic",
            streak = "White to yellowish",
            crystalSystem = "Monoclinic",
            chemicalFormula = "ZrO₂",
            commonColors = listOf("Brown", "Yellowish-brown", "Colorless", "Black"),
            whereFound = listOf("Phalaborwa, South Africa", "Jacupiranga, Brazil", "Kovdor, Russia", "Panda Hill, Tanzania", "Palabora, South Africa"),
            funFacts = listOf(
                "Named after Joseph Baddeley, who first described it from Sri Lanka.",
                "It is the monoclinic polymorph of zirconia (the tetragonal form is synthetic cubic zirconia).",
                "Baddeleyite is used for uranium-lead geochronology of silica-poor rocks where zircon is absent.",
                "It can survive intense metamorphism, making it useful for dating ancient rocks."
            ),
            uses = "Zirconium source, geochronology, collector specimens, ceramics.",
            rarity = "Rare"
        ),

        Specimen(
            id = "minium",
            name = "Minium",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Lead Oxide (Red Lead)",
            tagline = "Bright red lead oxide — the original red paint pigment of the ancients.",
            emoji = "🔴",
            colorHex = 0xFFCC2200,
            description = "Minium is a rare bright red lead oxide mineral, also known as red lead. It was historically used as a red pigment in illuminated manuscripts and paintings, and the word 'miniature' derives from the practice of painting with minium. The mineral forms as a secondary oxidation product of lead minerals.",
            formation = "Forms as a secondary mineral in the oxidized zones of lead deposits. Also forms naturally in fire-affected mine waste and some volcanic sublimates.",
            hardness = "2.5–3",
            luster = "Greasy to dull",
            streak = "Orange-red to scarlet",
            crystalSystem = "Tetragonal",
            chemicalFormula = "Pb₃O₄",
            commonColors = listOf("Bright red", "Scarlet", "Orange-red"),
            whereFound = listOf("Broken Hill, Australia", "Mendip Hills, England", "Ingurtosu, Sardinia (Italy)", "Leadville, Colorado, USA"),
            funFacts = listOf(
                "The word 'miniature' comes from 'minium' — the practice of painting small illustrations with red lead.",
                "Minium was used as a red pigment in ancient Roman and medieval manuscripts.",
                "It is one of the few naturally occurring lead oxides.",
                "It is toxic, like all lead compounds, and modern paint no longer uses it."
            ),
            uses = "Historical pigment, collector specimens, industrial red lead production.",
            rarity = "Rare"
        ),

        Specimen(
            id = "hercynite",
            name = "Hercynite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Iron Aluminum Spinel",
            tagline = "Dark green to black iron aluminum spinel from metamorphic rocks.",
            emoji = "⬛",
            colorHex = 0xFF1F2F1F,
            description = "Hercynite is an iron aluminum spinel mineral that forms dark green to black crystals. It is the iron analog of spinel and is common in metamorphosed rocks, particularly in emery deposits and in some eclogites. Named after the Latin name for the Harz Mountains (Silva Hercynia) where it was first found.",
            formation = "Found in regionally metamorphosed rocks, particularly in aluminum-rich, silica-poor environments. Common in emery deposits, eclogites, and granulite-facies rocks.",
            hardness = "7.5–8",
            luster = "Vitreous to submetallic",
            streak = "Greenish-white to gray",
            crystalSystem = "Isometric",
            chemicalFormula = "FeAl₂O₄",
            commonColors = listOf("Dark green", "Black", "Brownish-green"),
            whereFound = listOf("Harz Mountains, Germany", "Naxos, Greece", "Chester, Massachusetts, USA", "Buckskin Mountain, Arizona, USA", "Madagascar"),
            funFacts = listOf(
                "Named after the Harz Mountains in Germany (Silva Hercynia in Latin).",
                "It is the iron end-member of the spinel series.",
                "Common in emery, a natural abrasive rock.",
                "It can survive intense metamorphism, making it useful for studying metamorphic history."
            ),
            uses = "Collector specimens, component of emery abrasive, indicator mineral for metamorphic grade.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "stibiconite",
            name = "Stibiconite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Antimony Oxide (Weathering Product)",
            tagline = "Yellow to white antimony oxidation product forming pseudomorphs after stibnite.",
            emoji = "🟡",
            colorHex = 0xFFD4A017,
            description = "Stibiconite is an antimony oxide mineral that forms as a weathering product of stibnite and other antimony minerals. It frequently forms pseudomorphs after stibnite, preserving the original crystal shape while replacing the chemistry. The color ranges from yellow to white, and it is often earthy or powdery.",
            formation = "Forms in the oxidized zones of antimony deposits by weathering of stibnite and other antimony sulfides. Common in surface exposures of stibnite deposits.",
            hardness = "4–5.5",
            luster = "Dull to earthy",
            streak = "Yellowish-white to pale yellow",
            crystalSystem = "Isometric (metamict)",
            chemicalFormula = "Sb₃O₆(OH)",
            commonColors = listOf("Yellow", "Yellowish-white", "White", "Grayish-yellow"),
            whereFound = listOf("Sombrerete, Mexico", "Peruk, Slovakia", "Malaya (Malaysia)", "Shikoku, Japan", "Bau, Malaysia"),
            funFacts = listOf(
                "It commonly forms pseudomorphs after stibnite — replacing the mineral while keeping the crystal shape.",
                "The name comes from 'stibium' (antimony) and 'konis' (powder) in Greek.",
                "Its powdery, earthy texture makes it easy to identify in the field.",
                "It is an important indicator that antimony sulfides have been weathering nearby."
            ),
            uses = "Antimony ore, collector specimens (especially pseudomorphs).",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "coltan",
            name = "Columbite-Tantalite (Coltan)",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Niobium-Tantalum-Iron-Manganese Oxide",
            tagline = "The strategic 'coltan' ore — source of niobium and tantalum for modern technology.",
            emoji = "⬛",
            colorHex = 0xFF222222,
            description = "Columbite-tantalite, commonly known as coltan, is a series of oxides ranging from columbite (niobium-rich) to tantalite (tantalum-rich). It is the primary source of both niobium and tantalum — critical metals for electronic devices, superalloys, and medical implants. The mineral is heavy, black, and typically forms in granitic pegmatites.",
            formation = "Found in granitic pegmatites and alluvial placer deposits. Concentrated by weathering of pegmatites into stream deposits.",
            hardness = "6",
            luster = "Submetallic to resinous",
            streak = "Dark red to black",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "(Fe,Mn)(Nb,Ta)₂O₆",
            commonColors = listOf("Iron-black", "Dark brown", "Black"),
            whereFound = listOf("Kivu, DR Congo", "Greenbushes, Australia", "Bikita, Zimbabwe", "Manono, DR Congo", "Tanco Mine, Canada"),
            funFacts = listOf(
                "Coltan is essential for making tantalum capacitors in virtually all electronic devices.",
                "The name 'coltan' is a contraction of 'columbite-tantalite'.",
                "Coltan mining in the DR Congo has been linked to conflict and environmental damage.",
                "Australia is the world's largest producer of coltan from a single mine (Greenbushes)."
            ),
            uses = "Source of niobium and tantalum for electronics, superalloys, medical implants.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // HALIDES (4)
        // ════════════════════════════════════════════

        Specimen(
            id = "atacamite",
            name = "Atacamite",
            rockClass = RockClass.MINERAL,
            category = "Halide — Copper Chloride Hydroxide",
            tagline = "Vibrant green copper chloride — formed in arid copper-rich environments.",
            emoji = "🟢",
            colorHex = 0xFF2E8B57,
            description = "Atacamite is a copper chloride hydroxide mineral with a striking bright green color. It is one of the most colorful copper minerals and forms in arid, copper-rich environments, particularly in the Atacama Desert of Chile. The mineral can form prismatic crystals, fibrous aggregates, or granular masses.",
            formation = "Forms in the oxidized zones of copper deposits in arid climates. Also forms as a secondary mineral around fumaroles and in some hydrothermal deposits.",
            hardness = "3–3.5",
            luster = "Vitreous to adamantine",
            streak = "Green",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Cu₂Cl(OH)₃",
            commonColors = listOf("Bright green", "Emerald green", "Dark green"),
            whereFound = listOf("Atacama Desert, Chile", "Burra Burra, South Australia", "Tsumeb, Namibia", "La Farola Mine, Chile", "Bisbee, Arizona, USA"),
            funFacts = listOf(
                "Named after the Atacama Desert in Chile, where it was first discovered.",
                "It is one of the few copper halide minerals found in nature.",
                "Atacamite was used as a green pigment in ancient South American art.",
                "It requires both copper and chloride in an arid environment to form — a rare combination."
            ),
            uses = "Copper ore, collector specimens, historical pigment.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "villiaumite",
            name = "Villiaumite",
            rockClass = RockClass.MINERAL,
            category = "Halide — Sodium Fluoride",
            tagline = "Rare red sodium fluoride from nepheline syenites and volcanic environments.",
            emoji = "🔴",
            colorHex = 0xFFC13D3D,
            description = "Villiaumite is a rare sodium fluoride mineral with a distinctive red to carmine-red color. It is found in alkaline igneous rocks, particularly nepheline syenites, and in some volcanic environments. The mineral is translucent and can form cubic crystals, though it is more commonly found as massive or granular aggregates.",
            formation = "Found in nepheline syenites, alkalic volcanic rocks, and some carbonatites. Forms in sodium-rich, silica-poor environments.",
            hardness = "2–2.5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "NaF",
            commonColors = listOf("Red", "Carmine-red", "Pinkish-red"),
            whereFound = listOf("Villiaume, Guinea", "Khibiny Massif, Russia", "Lovozero, Russia", "Ilimaussaq, Greenland", "Mont Saint-Hilaire, Canada"),
            funFacts = listOf(
                "Villiaumite is one of the few naturally occurring fluoride minerals.",
                "It is toxic and should be handled carefully — sodium fluoride is used in rat poison.",
                "It is water-soluble, so specimens can degrade if exposed to moisture.",
                "The red color is due to crystal lattice defects (color centers), not trace elements."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "pachnolite",
            name = "Pachnolite",
            rockClass = RockClass.MINERAL,
            category = "Halide — Calcium Aluminum Fluoride",
            tagline = "Colorless to white calcium aluminum fluoride from cryolite deposits.",
            emoji = "⚪",
            colorHex = 0xFFE8E8E8,
            description = "Pachnolite is a calcium aluminum fluoride mineral found in cryolite deposits. It forms colorless to white prismatic or tabular crystals with a vitreous luster. The name comes from the Greek 'pachnos' (thick) and 'lithos' (stone), referring to its thick, prismatic crystals.",
            formation = "Found in cryolite deposits and some granite pegmatites. Associated with cryolite, thomsenolite, and other fluoride minerals.",
            hardness = "4.5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Monoclinic",
            chemicalFormula = "CaAlF₄(OH)·H₂O",
            commonColors = listOf("Colorless", "White", "Pale yellow"),
            whereFound = listOf("Ivigtut, Greenland", "Miass, Russia", "St. Peter's Dome, Colorado, USA"),
            funFacts = listOf(
                "The name comes from Greek 'pachnos' meaning 'thick', referring to its crystal habit.",
                "It is almost exclusively found in cryolite deposits.",
                "The Ivigtut deposit in Greenland is the type locality and main source.",
                "It is water-soluble and can decompose over time in humid conditions."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "mendipite",
            name = "Mendipite",
            rockClass = RockClass.MINERAL,
            category = "Halide — Lead Oxychloride",
            tagline = "Rare white lead oxychloride forming fibrous masses in oxidized lead deposits.",
            emoji = "⬜",
            colorHex = 0xFFE0E0E0,
            description = "Mendipite is a rare lead oxychloride mineral that forms white to colorless fibrous, lamellar, or massive aggregates. It is found in oxidized lead deposits, typically in arid or semi-arid environments. Named after the Mendip Hills in England, where it was first discovered.",
            formation = "Forms in the oxidized zones of lead deposits, particularly where chloride-rich groundwater interacts with lead minerals. Common in arid environments.",
            hardness = "2.5–3",
            luster = "Pearly to vitreous",
            streak = "White",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Pb₃O₂Cl₂",
            commonColors = listOf("White", "Colorless", "Cream", "Grayish-white"),
            whereFound = listOf("Mendip Hills, England", "Broken Hill, Australia", "Mammoth Mine, California, USA", "Tsumeb, Namibia"),
            funFacts = listOf(
                "Named after the Mendip Hills in Somerset, England.",
                "It is one of the few lead oxychloride minerals found in nature.",
                "Its fibrous habit can make it look like asbestos at first glance.",
                "It requires very specific conditions to form: lead + oxygen + chlorine in a dry environment."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // CARBONATES (4)
        // ════════════════════════════════════════════

        Specimen(
            id = "phosgenite",
            name = "Phosgenite",
            rockClass = RockClass.MINERAL,
            category = "Carbonate — Lead Chlorocarbonate",
            tagline = "Rare transparent lead chlorocarbonate with brilliant adamantine luster.",
            emoji = "💎",
            colorHex = 0xFFD4D4D4,
            description = "Phosgenite is a rare lead chlorocarbonate mineral with a brilliant adamantine luster. It forms short prismatic to tabular crystals that are typically colorless, white, or pale yellow. Despite the name, it is not related to phosgene gas — the name comes from its chemical composition (phosgene was itself named after the mineral).",
            formation = "Forms in the oxidized zones of lead deposits, particularly in arid environments. Associated with cerussite, anglesite, and other secondary lead minerals.",
            hardness = "2.5–3",
            luster = "Adamantine",
            streak = "White",
            crystalSystem = "Tetragonal",
            chemicalFormula = "Pb₂(CO₃)Cl₂",
            commonColors = listOf("Colorless", "White", "Pale yellow", "Grayish-white"),
            whereFound = listOf("Tsumeb, Namibia", "Sardinia, Italy", "Matlock, England", "Dundas, Tasmania", "Broken Hill, Australia"),
            funFacts = listOf(
                "Phosgenite has one of the highest lusters of any mineral — truly brilliant adamantine.",
                "The gas 'phosgene' was named after this mineral, not the other way around.",
                "Tsumeb, Namibia produced some of the finest phosgenite crystals ever found.",
                "It is one of the heaviest non-metallic minerals due to its lead content."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "bastnasite",
            name = "Bastnasite",
            rockClass = RockClass.MINERAL,
            category = "Carbonate — Rare Earth Fluorocarbonate",
            tagline = "The world's primary source of rare earth elements — critical for green technology.",
            emoji = "🟤",
            colorHex = 0xFF8B6914,
            description = "Bastnasite is a rare earth fluorocarbonate mineral and the world's primary source of rare earth elements. It is critical for the production of neodymium magnets used in wind turbines, electric vehicles, and electronics. The mineral is typically brown, yellow, or honey-colored and forms granular masses or tabular crystals.",
            formation = "Found in carbonatite deposits and hydrothermal veins associated with alkaline igneous complexes. The largest deposits are in carbonatites.",
            hardness = "4–4.5",
            luster = "Vitreous to greasy",
            streak = "White to pale yellow",
            crystalSystem = "Hexagonal",
            chemicalFormula = "(Ce,La)CO₃F",
            commonColors = listOf("Brown", "Yellow", "Honey-yellow", "Wax-yellow"),
            whereFound = listOf("Mountain Pass, California, USA", "Bayan Obo, China", "Mountain Pass, USA", "Kangankunde, Malawi", "Gakara, Burundi"),
            funFacts = listOf(
                "Bastnasite is the world's most important source of rare earth elements.",
                "China controls over 80% of rare earth production, largely from bastnasite deposits at Bayan Obo.",
                "Rare earths from bastnasite are essential for wind turbines, EVs, and defense technology.",
                "The Mountain Pass mine in California was once the world's largest rare earth producer."
            ),
            uses = "Rare earth ore (cerium, lanthanum, neodymium), critical for green technology and electronics.",
            rarity = "Rare"
        ),

        Specimen(
            id = "kutnohorite",
            name = "Kutnohorite",
            rockClass = RockClass.MINERAL,
            category = "Carbonate — Manganese Calcium Carbonate",
            tagline = "Pink to white manganese calcium carbonate from manganese deposits.",
            emoji = "🌸",
            colorHex = 0xFFD4A0A0,
            description = "Kutnohorite is a manganese calcium carbonate mineral in the dolomite group. It forms pink to white rhombohedral crystals or granular masses. The mineral is found in manganese deposits and is the manganese-rich analog of dolomite. Named after Kutná Hora, Czech Republic.",
            formation = "Found in sedimentary manganese deposits and metamorphosed manganese-rich rocks. Associated with rhodochrosite, calcite, and other manganese minerals.",
            hardness = "3.5–4",
            luster = "Vitreous to pearly",
            streak = "White to pinkish-white",
            crystalSystem = "Trigonal",
            chemicalFormula = "CaMn(CO₃)₂",
            commonColors = listOf("Pink", "White", "Pale pink", "Cream"),
            whereFound = listOf("Kutná Hora, Czech Republic", "Polk County, Georgia, USA", "Kuruman, South Africa", "Tachkova, Russia", "Noda-Tamagawa, Japan"),
            funFacts = listOf(
                "Named after Kutná Hora, a historic silver mining town in the Czech Republic.",
                "It is the manganese-rich member of the dolomite group.",
                "The pink color comes from manganese content.",
                "It was only approved as a distinct mineral species in 1951."
            ),
            uses = "Manganese ore (minor), collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "benstonite",
            name = "Benstonite",
            rockClass = RockClass.MINERAL,
            category = "Carbonate — Barium Magnesium Carbonate",
            tagline = "Rare barium magnesium carbonate from barium-rich environments.",
            emoji = "⚪",
            colorHex = 0xFFE8E0D0,
            description = "Benstonite is a rare barium magnesium carbonate mineral that forms white to pale yellow granular masses or rhombohedral crystals. It is found in barium-rich environments, particularly in association with barite and witherite. Named after Orlando J. Benston, an American mineralogist.",
            formation = "Found in hydrothermal barium-rich deposits and in some sedimentary environments with high barium content.",
            hardness = "3.5–4",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "Ba₆(Ca,Mn)₆Mg(CO₃)₁₃",
            commonColors = listOf("White", "Pale yellow", "Cream", "Grayish-white"),
            whereFound = listOf("Hot Springs, Arkansas, USA", "Barite Mine, Virginia, USA", "Settlingstones, England", "Kipawa, Canada"),
            funFacts = listOf(
                "Named after Orlando J. Benston, who studied barium minerals.",
                "It is one of the most chemically complex carbonate minerals.",
                "It contains both barium and magnesium — an unusual combination for carbonates.",
                "Only a handful of localities worldwide produce benstonite specimens."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // PHOSPHATES & ARSENATES (7)
        // ════════════════════════════════════════════

        Specimen(
            id = "monazite",
            name = "Monazite",
            rockClass = RockClass.MINERAL,
            category = "Phosphate — Rare Earth Phosphate",
            tagline = "The primary ore of thorium and rare earths — common in beach sands worldwide.",
            emoji = "🟫",
            colorHex = 0xFF8B7355,
            description = "Monazite is a rare earth phosphate mineral and an important ore of thorium and rare earth elements. It is commonly found as small, reddish-brown to brown crystals in granitic pegmatites and in alluvial beach sands. Monazite sand deposits are mined for their rare earth and thorium content.",
            formation = "Found as an accessory mineral in granitic pegmatites, gneisses, and metamorphic rocks. Concentrated in alluvial deposits by weathering, forming monazite sands.",
            hardness = "5–5.5",
            luster = "Resinous to vitreous",
            streak = "White to pale brown",
            crystalSystem = "Monoclinic",
            chemicalFormula = "(Ce,La,Nd,Th)PO₄",
            commonColors = listOf("Reddish-brown", "Brown", "Yellow-brown", "Pink"),
            whereFound = listOf("Beach sands of India", "Western Australia", "Brazil", "South Africa", "Blue River, British Columbia, Canada"),
            funFacts = listOf(
                "Monazite is radioactive due to its thorium content.",
                "It is the primary source of thorium, a potential nuclear fuel.",
                "Monazite beach sands in India and Australia are among the world's largest rare earth resources.",
                "It can contain up to 20% thorium by weight."
            ),
            uses = "Rare earth ore, thorium source, collector specimens, geochronology.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "herderite",
            name = "Herderite",
            rockClass = RockClass.MINERAL,
            category = "Phosphate — Beryllium Calcium Phosphate",
            tagline = "Rare beryllium phosphate from granitic pegmatites with distinctive crystal forms.",
            emoji = "🟡",
            colorHex = 0xFFDAA520,
            description = "Herderite is a rare beryllium calcium phosphate mineral found in granitic pegmatites. It forms distinctive pale-colored crystals, often with complex crystal habits. The mineral is sought after by collectors for its well-formed crystals and its association with other rare pegmatite minerals.",
            formation = "Found in granitic pegmatites, typically in the late-stage phosphate zones. Associated with tourmaline, apatite, beryl, and other pegmatite minerals.",
            hardness = "5–5.5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Monoclinic",
            chemicalFormula = "CaBePO₄(OH)",
            commonColors = listOf("Pale yellow", "Greenish-white", "Colorless", "Pink"),
            whereFound = listOf("Palemero Mine, Brazil", "Stoneham, Maine, USA", "Dattas Mine, Brazil", "Nassau, Germany", "Verkhaiya, Russia"),
            funFacts = listOf(
                "Named after German mining official Sigmund August Wolfgang von Herder.",
                "It is one of the few beryllium phosphate minerals.",
                "Some herderite specimens are fluorescent under UV light.",
                "It forms a series with hydroxylherderite."
            ),
            uses = "Collector specimens, beryllium source (minor), mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "legrandite",
            name = "Legrandite",
            rockClass = RockClass.MINERAL,
            category = "Arsenate — Zinc Arsenate",
            tagline = "Stunning bright yellow zinc arsenate — one of the rarest and most beautiful minerals.",
            emoji = "🟡",
            colorHex = 0xFFFFD700,
            description = "Legrandite is an extremely rare zinc arsenate mineral with a stunning bright yellow to golden-yellow color. It forms long, prismatic to acicular crystals that can create spectacular sprays and radiating clusters. The finest specimens come from the Ojuela Mine in Mexico and are among the most prized mineral specimens in the world.",
            formation = "Found in the oxidized zones of zinc-arsenic deposits, typically as a secondary mineral. Requires very specific conditions of zinc and arsenic enrichment.",
            hardness = "4.5–5",
            luster = "Vitreous",
            streak = "White to pale yellow",
            crystalSystem = "Monoclinic",
            chemicalFormula = "Zn₂AsO₄(OH)·H₂O",
            commonColors = listOf("Bright yellow", "Golden-yellow", "Lemon-yellow"),
            whereFound = listOf("Ojuela Mine, Mapimi, Mexico", "Tsumeb, Namibia (very rare)", "Gold Hill, Utah, USA"),
            funFacts = listOf(
                "Legrandite is one of the most sought-after mineral specimens by collectors.",
                "The Ojuela Mine in Mexico has produced virtually all of the world's fine legrandite specimens.",
                "Named after Belgian mining engineer Maxime Legrand.",
                "Fine spray-like crystal groups can sell for thousands of dollars."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Very Rare"
        ),

        Specimen(
            id = "cavansite",
            name = "Cavansite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Calcium Vanadium Silicate",
            tagline = "Electric blue vanadium silicate forming radiating crystal balls.",
            emoji = "🔵",
            colorHex = 0xFF1E90FF,
            description = "Cavansite is a striking electric blue calcium vanadium silicate mineral. It forms radiating, ball-like clusters of bladed crystals that are among the most intensely blue of all minerals. Discovered in 1967, it is found almost exclusively in the Deccan Traps of India, where it occurs in vesicles and fractures of basalt.",
            formation = "Found in volcanic basalt (the Deccan Traps), filling vesicles and fractures. Forms as a late-stage mineral in the alteration of basalt.",
            hardness = "3–4",
            luster = "Vitreous to pearly",
            streak = "Pale blue",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Ca(VO)Si₄O₁₀·4H₂O",
            commonColors = listOf("Electric blue", "Bright blue", "Deep blue"),
            whereFound = listOf("Pune District, Maharashtra, India", "Wagholi Quarry, India", "Nasik, India"),
            funFacts = listOf(
                "Cavansite is one of the most intensely blue minerals in the world.",
                "It was only discovered in 1967 — relatively recent for a major mineral species.",
                "The name comes from its chemistry: CA-lcium, VAN-adium, SI-licate.",
                "Almost all specimens come from a single region in India (the Deccan Traps)."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "libethenite",
            name = "Libethenite",
            rockClass = RockClass.MINERAL,
            category = "Phosphate — Copper Phosphate Hydroxide",
            tagline = "Dark green copper phosphate forming distinctive short prismatic crystals.",
            emoji = "🟢",
            colorHex = 0xFF2E6B2E,
            description = "Libethenite is a copper phosphate hydroxide mineral with a dark green to olive-green color. It forms short prismatic to equant crystals that can look almost cubic. The mineral occurs in the oxidized zones of copper deposits where phosphate is present, and was first found in Libethen (now Lubietová), Slovakia.",
            formation = "Forms in the oxidized zones of copper deposits, particularly where phosphate-bearing fluids interact with copper minerals. Associated with malachite, azurite, and other secondary copper minerals.",
            hardness = "4",
            luster = "Vitreous to subadamantine",
            streak = "Olive-green to grayish-green",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Cu₂PO₄(OH)",
            commonColors = listOf("Dark green", "Olive-green", "Blackish-green"),
            whereFound = listOf("Lubietová (Libethen), Slovakia", "Tsumeb, Namibia", "Chile", "Redruth, Cornwall, England", "Chessy, France"),
            funFacts = listOf(
                "Named after Libethen (now Lubietová), the Slovak town where it was first found.",
                "It forms crystals that can look deceptively cubic, but it is actually orthorhombic.",
                "It was one of the first phosphate minerals to be recognized as distinct.",
                "Libethenite can be confused with olivenite, its arsenic analog."
            ),
            uses = "Copper ore (minor), collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "crandallite",
            name = "Crandallite",
            rockClass = RockClass.MINERAL,
            category = "Phosphate — Calcium Aluminum Phosphate",
            tagline = "White to yellow aluminum phosphate from weathered phosphate deposits.",
            emoji = "🟡",
            colorHex = 0xFFDAA520,
            description = "Crandallite is a calcium aluminum phosphate mineral found in weathered phosphate deposits. It forms white to yellowish, granular to massive aggregates, and occasionally crusts or botryoidal forms. It is part of the alunite supergroup and is common in lateritic bauxite deposits.",
            formation = "Forms by weathering of phosphate-bearing rocks in tropical and subtropical climates. Common in lateritic bauxite and phosphorite deposits.",
            hardness = "4.5–5",
            luster = "Dull to vitreous",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "CaAl₃(PO₄)(PO₃OH)(OH)₆",
            commonColors = listOf("White", "Yellowish-white", "Cream", "Pale yellow"),
            whereFound = listOf("Fairfield, Utah, USA", "Eureka, Nevada, USA", "Kep Island, New Caledonia", "Minas Gerais, Brazil", "Senegal"),
            funFacts = listOf(
                "Named after Milan L. Crandall, an American mining engineer.",
                "It is part of the alunite supergroup of minerals.",
                "Crandallite is common in tropical bauxite deposits but rarely forms visible crystals.",
                "It can be an ore of aluminum in some deposits."
            ),
            uses = "Aluminum ore (minor), collector specimens, phosphorus source.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "wardite",
            name = "Wardite",
            rockClass = RockClass.MINERAL,
            category = "Phosphate — Sodium Aluminum Phosphate",
            tagline = "Rare colorless to pale green phosphate forming tetrahedral crystals.",
            emoji = "⚪",
            colorHex = 0xFFE0F0E0,
            description = "Wardite is a sodium aluminum phosphate mineral that forms colorless to pale green tetrahedral crystals. It is a rare mineral found in phosphate-rich environments, particularly in pegmatites and in some sedimentary phosphate deposits. Named after Henry Augustus Ward, founder of Ward's Natural Science Establishment.",
            formation = "Found in granitic pegmatites in the phosphate zones, and in some sedimentary phosphate deposits. Associated with other phosphate minerals.",
            hardness = "5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Tetragonal",
            chemicalFormula = "NaAl₃(PO₄)₂(OH)₄·2H₂O",
            commonColors = listOf("Colorless", "Pale green", "White", "Pale blue"),
            whereFound = listOf("Rapid Creek, Yukon, Canada", "Palemero Mine, Brazil", "Sapucaia Mine, Brazil", "Branchville, Connecticut, USA"),
            funFacts = listOf(
                "Named after Henry Augustus Ward, founder of Ward's Natural Science Establishment.",
                "It forms distinctive tetrahedral crystals that are unusual for phosphate minerals.",
                "Rapid Creek in the Yukon is the premier locality for wardite specimens.",
                "It is one of the few sodium-bearing phosphate minerals."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // SILICATES (8)
        // ════════════════════════════════════════════

        Specimen(
            id = "clinozoisite",
            name = "Clinozoisite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Calcium Aluminum Silicate (Epidote Group)",
            tagline = "Pale green to pink calcium aluminum silicate in the epidote group.",
            emoji = "🟢",
            colorHex = 0xFF9ACD32,
            description = "Clinozoisite is a calcium aluminum silicate mineral in the epidote group. It is the iron-free end member of the epidote series and forms prismatic to elongated crystals with a pale green, pink, or grayish color. It is common in metamorphic rocks, particularly in greenschist and amphibolite facies.",
            formation = "Found in regionally metamorphosed rocks, contact metamorphic zones, and as a secondary mineral in altered igneous rocks. Associated with epidote, garnet, and actinolite.",
            hardness = "6.5",
            luster = "Vitreous",
            streak = "White to gray",
            crystalSystem = "Monoclinic",
            chemicalFormula = "Ca₂Al₃(SiO₄)(Si₂O₇)O(OH)",
            commonColors = listOf("Pale green", "Pink", "Grayish-green", "Colorless"),
            whereFound = listOf("Ala Valley, Italy", "Arendal, Norway", "Tyrol, Austria", "Piedmont, Italy", "Eden Mills, Vermont, USA"),
            funFacts = listOf(
                "It is the iron-free end-member of the epidote group.",
                "Some specimens show a beautiful pink color (variety known as 'clinozoisite-manganese').",
                "It forms a complete solid solution with epidote.",
                "Named for its monoclinic symmetry and its relationship to zoisite."
            ),
            uses = "Collector specimens, indicator mineral for metamorphic grade.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "seraphinite",
            name = "Seraphinite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Chlorite Variety (Feathery Silver-Green)",
            tagline = "Feathery silver-green chlorite with angel-wing patterns — a collector's treasure.",
            emoji = "😇",
            colorHex = 0xFF2E8B57,
            description = "Seraphinite is a trade name for a distinctive variety of clinochlore (a chlorite mineral) characterized by feathery, silver-green patterns that resemble angel wings. The name comes from 'Seraphim' — the highest order of angels — because the chatoyant, feather-like inclusions create an ethereal, angelic appearance. It is primarily found in the Lake Baikal region of Siberia.",
            formation = "Forms in metamorphic rocks, particularly in chlorite schists and in contact metamorphic zones. The feathery pattern is caused by crystallographic intergrowths and fibrous inclusions.",
            hardness = "2–2.5",
            luster = "Pearly to vitreous",
            streak = "White to greenish-white",
            crystalSystem = "Monoclinic (Trigonal pseudo-hexagonal)",
            chemicalFormula = "(Mg,Fe²⁺)₅Al(Si₃Al)O₁₀(OH)₈",
            commonColors = listOf("Deep green", "Silver-green", "Dark green with silver feathers"),
            whereFound = listOf("Lake Baikal region, Siberia, Russia", "Korshunovskoye, Russia"),
            funFacts = listOf(
                "The name 'Seraphinite' comes from the Seraphim — the highest order of angels in Christian theology.",
                "The silver feather patterns are caused by fibrous intergrowths of clinochlore.",
                "It is found almost exclusively in the Lake Baikal region of Siberia.",
                "When polished, the chatoyant feathers create a shimmering, angelic effect.",
                "Despite its delicate appearance, it is a relatively common mineral — but gem-quality seraphinite is rare."
            ),
            uses = "Collector specimens, lapidary, jewelry (cabochons and carvings), metaphysical.",
            rarity = "Rare"
        ),

        Specimen(
            id = "ajoite",
            name = "Ajoite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Copper Aluminum Silicate (Phyllosilicate)",
            tagline = "Mint-green copper silicate growing on quartz — one of the rarest and most beautiful minerals.",
            emoji = "🌿",
            colorHex = 0xFF98FB98,
            description = "Ajoite is an extremely rare copper aluminum silicate mineral with a distinctive mint-green to bluish-green color. It is most famous for growing as delicate, fluffy coatings on quartz crystals, creating specimens of extraordinary beauty. The finest ajoite specimens come from the Messina Mine in South Africa, where it coats quartz with a soft, cotton-like green layer.",
            formation = "Found as a secondary mineral in the oxidized zones of copper deposits. Grows on quartz and other minerals in copper-rich environments. Associated with shattuckite, plancheite, and papagoite.",
            hardness = "3",
            luster = "Silky to dull",
            streak = "Greenish-white",
            crystalSystem = "Monoclinic",
            chemicalFormula = "(K,Na)Cu₇AlSi₉O₂₄(OH)₆·3H₂O",
            commonColors = listOf("Mint green", "Light blue-green", "Bluish-green"),
            whereFound = listOf("Ajo, Arizona, USA", "Messina Mine, South Africa", "Tsumeb, Namibia (very rare)"),
            funFacts = listOf(
                "Ajoite was named after Ajo, Arizona, where it was first discovered.",
                "The Messina Mine in South Africa produced the world's finest ajoite-on-quartz specimens.",
                "Ajoite-coated quartz crystals are among the most beautiful and sought-after mineral specimens.",
                "It is so rare that only a few hundred quality specimens exist worldwide.",
                "It often grows with shattuckite and papagoite, creating stunning multi-mineral specimens."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Very Rare"
        ),

        Specimen(
            id = "brucite",
            name = "Brucite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Magnesium Hydroxide",
            tagline = "White to pale green magnesium hydroxide with pearly luster and tabular crystals.",
            emoji = "⚪",
            colorHex = 0xFFE0F0E0,
            description = "Brucite is a magnesium hydroxide mineral that forms tabular crystals, fibrous masses, or foliated plates. It has a pearly luster and is typically white, pale green, or pale blue. Brucite is found in metamorphosed dolomite and in serpentine-rich environments. Recent discoveries of bright pink and blue brucite in Pakistan have elevated it to collector status.",
            formation = "Found in metamorphosed dolomite and limestone, and in serpentine deposits formed by hydration of peridotite. Also occurs in some hydrothermal veins.",
            hardness = "2.5",
            luster = "Pearly to waxy",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "Mg(OH)₂",
            commonColors = listOf("White", "Pale green", "Blue", "Pink", "Pale yellow"),
            whereFound = listOf("Tilya, Pakistan (pink)", "Khaldon, Pakistan (blue)", "Unst, Shetland, Scotland", "Texas, Pennsylvania, USA", "Snarum, Norway"),
            funFacts = listOf(
                "Brucite is named after American mineralogist Archibald Bruce.",
                "Recent discoveries of pink and blue brucite in Pakistan have made it highly collectible.",
                "It feels soapy or greasy to the touch, like talc.",
                "Brucite can contain nickel, giving some specimens a beautiful blue color."
            ),
            uses = "Magnesium source, collector specimens, fire retardant material.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "manganite",
            name = "Manganite",
            rockClass = RockClass.MINERAL,
            category = "Oxide — Manganese Oxide-Hydroxide",
            tagline = "Dark metallic manganese mineral forming distinctive prismatic crystals.",
            emoji = "⬛",
            colorHex = 0xFF2B2B2B,
            description = "Manganite is a manganese oxide-hydroxide mineral with a dark, metallic to submetallic luster. It forms distinctive prismatic to columnar crystals with striations along their length. Manganite is an important ore of manganese and was historically significant in the steel industry. It is often confused with pyrolusite but has a different crystal habit.",
            formation = "Forms in hydrothermal veins and as a secondary mineral in the oxidized zones of manganese deposits. Associated with pyrolusite, barite, calcite, and goethite.",
            hardness = "4",
            luster = "Submetallic",
            streak = "Reddish-brown to brownish-black",
            crystalSystem = "Monoclinic",
            chemicalFormula = "MnO(OH)",
            commonColors = listOf("Iron-black", "Dark gray", "Dark steel-gray"),
            whereFound = listOf("Ilmenau, Germany", "Ilfeld, Germany", "Sandford Mine, New Jersey, USA", "St. Just, Cornwall, England", "Trepca, Kosovo"),
            funFacts = listOf(
                "Manganite crystals are often beautifully striated along their length.",
                "It was once thought to be a separate mineral from pyrolusite, but they are closely related.",
                "Manganite was an important manganese ore for the steel industry.",
                "The best crystals come from the Ilfeld district in Germany."
            ),
            uses = "Manganese ore, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "allanite",
            name = "Allanite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Calcium Rare Earth Aluminum Silicate (Epidote Group)",
            tagline = "Black radioactive rare earth silicate in the epidote group.",
            emoji = "⬛",
            colorHex = 0xFF2D2D2D,
            description = "Allanite is a calcium rare earth aluminum silicate mineral in the epidote group. It is typically black, dark brown, or greenish-black and is notable for being radioactive due to its thorium and uranium content. Allanite is a common accessory mineral in granitic and metamorphic rocks but is often overlooked due to its dark color and metamictization.",
            formation = "Found as an accessory mineral in granitic rocks, pegmatites, schists, and gneisses. Common in S-type granites and metamorphic rocks rich in aluminum.",
            hardness = "5.5–6 (varies due to metamictization)",
            luster = "Submetallic to vitreous",
            streak = "Gray to brownish-gray",
            crystalSystem = "Monoclinic",
            chemicalFormula = "(Ca,Ce)(Al₂Fe²⁺)(Si₂O₇)(SiO₄)O(OH)",
            commonColors = listOf("Black", "Dark brown", "Greenish-black", "Brown"),
            whereFound = listOf("Ala, Finland (type locality)", "Greenland", "Madagascar", "Barringer Hill, Texas, USA", "Renfrew, Ontario, Canada"),
            funFacts = listOf(
                "Allanite is radioactive due to its rare earth and thorium content.",
                "It becomes metamict (amorphous) over time due to radiation damage.",
                "Named after Scottish mineralogist Thomas Allan.",
                "It is one of the most common rare earth minerals in granitic rocks."
            ),
            uses = "Rare earth ore (minor), collector specimens, geochronology.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "kinoite",
            name = "Kinoite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Calcium Copper Silicate",
            tagline = "Deep blue calcium copper silicate forming drusy coatings and small crystals.",
            emoji = "🔵",
            colorHex = 0xFF1C5BA0,
            description = "Kinoite is a rare calcium copper silicate mineral with a beautiful deep blue color. It typically forms drusy coatings, microcrystals, or massive fillings in veins. The mineral is found in copper-rich environments and is named after Father Eusebio Kino, the Jesuit explorer of the American Southwest.",
            formation = "Found in the oxidized zones of copper deposits, typically in fractures and veins. Associated with other copper silicates like chrysocolla and shattuckite.",
            hardness = "4.5–5",
            luster = "Vitreous",
            streak = "Blue",
            crystalSystem = "Monoclinic",
            chemicalFormula = "Ca₂Cu₂Si₃O₁₀(OH)₂",
            commonColors = listOf("Deep blue", "Royal blue", "Dark blue"),
            whereFound = listOf("Ray Mine, Arizona, USA", "Mammoth-St. Anthony Mine, Arizona, USA", "Christmas Mine, Arizona, USA", "Copiapo, Chile"),
            funFacts = listOf(
                "Named after Father Eusebio Kino, a 17th-century Jesuit missionary and explorer.",
                "It is almost exclusively found in Arizona, USA.",
                "Kinoite is often associated with apophyllite and other zeolite-type minerals.",
                "Despite its beauty, it is rarely found in large enough crystals for cutting."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "hedenbergite",
            name = "Hedenbergite",
            rockClass = RockClass.MINERAL,
            category = "Silicate — Calcium Iron Pyroxene",
            tagline = "Dark green to black iron-rich pyroxene from skarns and metamorphic rocks.",
            emoji = "⬛",
            colorHex = 0xFF1A2B1A,
            description = "Hedenbergite is a calcium iron silicate pyroxene mineral. It is the iron-rich end member of the diopside-hedenbergite series and forms dark green to black prismatic crystals. The mineral is common in skarn deposits and in some metamorphic rocks. Hedenbergite is an important rock-forming mineral in iron-rich environments.",
            formation = "Found in skarn deposits, contact metamorphic zones, and in some alkaline igneous rocks. Common in iron-rich metamorphic environments.",
            hardness = "5.5–6",
            luster = "Vitreous to dull",
            streak = "Greenish-gray to brownish-green",
            crystalSystem = "Monoclinic",
            chemicalFormula = "CaFe²⁺Si₂O₆",
            commonColors = listOf("Dark green", "Black", "Brownish-green", "Greenish-black"),
            whereFound = listOf("Långban, Sweden", "Dannemora, Sweden", "Bjørnehamn, Norway", "Marmoraton Mine, Ontario, Canada", "Edenville, New York, USA"),
            funFacts = listOf(
                "Hedenbergite is the iron end-member of the diopside-hedenbergite series.",
                "Named after Swedish chemist M.A. Ludwig Hedenberg.",
                "It is a key mineral in skarn deposits, which can host iron, copper, and tungsten ore.",
                "The famous Dannemora iron mine in Sweden produced fine hedenbergite specimens."
            ),
            uses = "Collector specimens, iron ore indicator, rock-forming mineral.",
            rarity = "Uncommon"
        ),

        // ════════════════════════════════════════════
        // SULFATES & OTHERS (4)
        // ════════════════════════════════════════════

        Specimen(
            id = "hanksite",
            name = "Hanksite",
            rockClass = RockClass.MINERAL,
            category = "Sulfate — Sodium Potassium Sulfate Carbonate Chloride",
            tagline = "Yellowish hexagonal crystal from Searles Lake — a mineral with an incredibly complex formula.",
            emoji = "🔶",
            colorHex = 0xFFC8B560,
            description = "Hanksite is a sodium potassium sulfate carbonate chloride mineral with one of the most complex formulas of any mineral. It forms distinctive hexagonal crystals with a pale yellow to colorless appearance. Hanksite is found almost exclusively at Searles Lake in California, an evaporite deposit where it crystallizes from brines.",
            formation = "Forms as an evaporite mineral in saline lake deposits. Crystallizes from concentrated brines in arid, closed-basin lake environments.",
            hardness = "3",
            luster = "Vitreous to greasy",
            streak = "White",
            crystalSystem = "Hexagonal",
            chemicalFormula = "Na₂₂K(SO₄)₉(CO₃)₂Cl",
            commonColors = listOf("Pale yellow", "Colorless", "Grayish-white", "Yellowish-white"),
            whereFound = listOf("Searles Lake, California, USA", "Soda Lake, California, USA"),
            funFacts = listOf(
                "Hanksite has one of the most complex chemical formulas of any mineral.",
                "It is found almost exclusively at Searles Lake, California.",
                "Named after Henry Garber Hanks, the first state mineralogist of California.",
                "It is water-soluble and should be kept dry to prevent dissolution.",
                "Some hanksite crystals fluoresce white under shortwave UV light."
            ),
            uses = "Collector specimens, mineralogical research, industrial chemical source.",
            rarity = "Rare"
        ),

        Specimen(
            id = "augelite",
            name = "Augelite",
            rockClass = RockClass.MINERAL,
            category = "Phosphate — Aluminum Phosphate Hydroxide",
            tagline = "Colorless to pale green aluminum phosphate with a vitreous to pearly luster.",
            emoji = "⚪",
            colorHex = 0xFFE8F5E8,
            description = "Augelite is an aluminum phosphate mineral that forms colorless to pale green tabular or prismatic crystals. It has a vitreous to pearly luster and is found in hydrothermal deposits and some metamorphic environments. Named after the Greek 'auge' meaning 'brightness' for its luster.",
            formation = "Found in hydrothermal veins, metamorphosed bauxite deposits, and in some pegmatites. Associated with other phosphate minerals.",
            hardness = "4.5–5",
            luster = "Vitreous to pearly",
            streak = "White",
            crystalSystem = "Monoclinic",
            chemicalFormula = "Al₂PO₄(OH)₃",
            commonColors = listOf("Colorless", "White", "Pale green", "Pale yellow"),
            whereFound = listOf("Bohemian Massif, Czech Republic", "Prospect Peak, California, USA", "Tibchi, Nigeria", "Cerro de Mercado, Mexico", "Horcajuelo, Spain"),
            funFacts = listOf(
                "The name comes from Greek 'auge' meaning 'brightness', referring to its luster.",
                "It is one of the rarer aluminum phosphate minerals.",
                "Some augelite specimens are found in meteorites — extremely rare.",
                "It often occurs as small, wedge-shaped crystals."
            ),
            uses = "Collector specimens, mineralogical research.",
            rarity = "Rare"
        ),

        Specimen(
            id = "alunite",
            name = "Alunite",
            rockClass = RockClass.MINERAL,
            category = "Sulfate — Potassium Aluminum Sulfate Hydroxide",
            tagline = "White to pink sulfate mineral also known as 'alum stone' — used since antiquity.",
            emoji = "🟠",
            colorHex = 0xFFD4A0A0,
            description = "Alunite is a potassium aluminum sulfate mineral also known as 'alum stone'. It forms granular to massive aggregates, and less commonly tabular crystals. Alunite has been used since antiquity as a source of alum for dyes, medicines, and industrial processes. The mineral is found in hydrothermal alteration zones and is an indicator of acid sulfate environments.",
            formation = "Forms in hydrothermal alteration zones, particularly in acid sulfate environments associated with volcanic activity. Also found in some sedimentary deposits.",
            hardness = "3.5–4",
            luster = "Vitreous to pearly",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "KAl₃(SO₄)₂(OH)₆",
            commonColors = listOf("White", "Pink", "Cream", "Yellowish-white", "Reddish"),
            whereFound = listOf("Tolfa, Italy (historical type locality)", "Goldfield, Nevada, USA", "Marysvale, Utah, USA", "Bulgaria", "Australia"),
            funFacts = listOf(
                "Alunite has been mined for alum since Roman times.",
                "The Tolfa alunite deposits in Italy were a papal monopoly in the Renaissance.",
                "It is an indicator mineral for hydrothermal gold and silver deposits.",
                "Alunite can be processed to produce potash (potassium fertilizer)."
            ),
            uses = "Alum source, potassium source, collector specimens, indicator for mineral exploration.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "lazurite",
            name = "Lazurite",
            rockClass = RockClass.MINERAL,
            category = "Sulfate — Sodium Calcium Aluminum Silicate Sulfate",
            tagline = "The blue mineral that gives lapis lazuli its color — prized for 6,000 years.",
            emoji = "🔵",
            colorHex = 0xFF1C5BA0,
            description = "Lazurite is a sodium calcium aluminum silicate sulfate mineral and the primary component of lapis lazuli, the ancient gemstone. It gives lapis its deep blue color. Lazurite has been prized for over 6,000 years and was ground into ultramarine, the most expensive blue pigment in Renaissance art. The mineral itself is deep blue and forms in contact metamorphic deposits.",
            formation = "Found in contact metamorphic deposits in limestone, typically associated with pyrite, calcite, and other skarn minerals. Forms in silica-poor, sulfate-rich environments.",
            hardness = "5–5.5",
            luster = "Vitreous to dull",
            streak = "Blue",
            crystalSystem = "Isometric (Cubic)",
            chemicalFormula = "(Na,Ca)₈(AlSiO₄)₆(SO₄,S,Cl)₁₋₂",
            commonColors = listOf("Deep blue", "Azure blue", "Ultramarine blue"),
            whereFound = listOf("Sar-e-Sang, Afghanistan", "Lake Baikal, Russia", "Chile (Coquimbo)", "Pamir Mountains", "Colorado, USA"),
            funFacts = listOf(
                "Lazurite is the mineral that gives lapis lazuli its famous deep blue color.",
                "Ground lazurite was the source of ultramarine — the most expensive pigment in Renaissance art.",
                "Afghanistan has been mining lazurite for over 6,000 years.",
                "The name comes from the Persian 'lazward' meaning 'blue'.",
                "Natural ultramarine was more expensive than gold until a synthetic version was invented in 1828."
            ),
            uses = "Gemstone (as lapis lazuli), pigment (ultramarine), collector specimens, carving material.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // GEMSTONE VARIETIES & SPECIALTY STONES (10)
        // ════════════════════════════════════════════

        Specimen(
            id = "hessonite",
            name = "Garnet, Hessonite",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Grossular Garnet Variety (Cinnamon Orange)",
            tagline = "Cinnamon-orange grossular garnet — the 'cinnamon stone' prized since antiquity.",
            emoji = "🟠",
            colorHex = 0xFFC87533,
            description = "Hessonite is a variety of grossular garnet with a distinctive cinnamon-orange to honey-brown color. The name comes from the Greek 'hesson' meaning 'inferior', referring to its lower hardness compared to other garnets. Hessonite has been used as a gemstone since ancient times and is sometimes called the 'cinnamon stone'.",
            formation = "Found in contact metamorphic deposits and skarns. Also found in alluvial placer deposits. Associated with other grossular varieties and calc-silicate minerals.",
            hardness = "6.5–7",
            luster = "Vitreous to resinous",
            streak = "White",
            crystalSystem = "Isometric",
            chemicalFormula = "Ca₃Al₂(SiO₄)₃",
            commonColors = listOf("Cinnamon-orange", "Honey-brown", "Yellow-orange", "Brownish-orange"),
            whereFound = listOf("Sri Lanka", "Tanzania", "Brazil", "India", "Mexico", "Canada (Quebec)"),
            funFacts = listOf(
                "The name 'hessonite' means 'inferior' in Greek, due to its slightly lower hardness.",
                "Also known as the 'cinnamon stone' for its distinctive color.",
                "It has been used as a gemstone since Roman times.",
                "Hessonite is one of the few garnets that typically shows a roiled or oily appearance under magnification."
            ),
            uses = "Gemstone (faceted and cabochon), collector specimens, jewelry.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "melanite",
            name = "Garnet, Melanite",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Andradite Garnet Variety (Black)",
            tagline = "Brilliant black andradite garnet with adamantine luster — the 'black garnet'.",
            emoji = "⬛",
            colorHex = 0xFF1A1A1A,
            description = "Melanite is a titanium-rich variety of andradite garnet with a deep black to dark brown color. Despite its dark color, it has a brilliant adamantine luster due to its high refractive index. Melanite is sometimes called 'black garnet' and is occasionally used as a gemstone in jewelry, particularly for men's rings.",
            formation = "Found in alkaline igneous rocks, particularly nepheline syenites and phonolites. Also found in some skarn deposits.",
            hardness = "6.5–7",
            luster = "Adamantine to submetallic",
            streak = "Brownish",
            crystalSystem = "Isometric",
            chemicalFormula = "Ca₃Fe₂Ti(SiO₄)₃ (with Ti substitution)",
            commonColors = listOf("Black", "Dark brown", "Very dark red-brown"),
            whereFound = listOf("Alban Hills, Italy", "Eifel, Germany", "Kola Peninsula, Russia", "Mali", "California, USA"),
            funFacts = listOf(
                "Melanite is the black, titanium-rich variety of andradite garnet.",
                "Despite being black, it has one of the most brilliant lusters of any garnet.",
                "The name comes from the Greek 'melanos' meaning 'black'.",
                "It is occasionally faceted as a gemstone for distinctive black jewelry."
            ),
            uses = "Gemstone (faceted), collector specimens, jewelry.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "imperial-topaz",
            name = "Topaz, Imperial",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Topaz Variety (Golden Orange)",
            tagline = "Precious golden-orange topaz — the most valued topaz variety in the world.",
            emoji = "🟡",
            colorHex = 0xFFDAA520,
            description = "Imperial Topaz is the most prized variety of topaz, displaying a rich golden-orange to sherry color. The name 'imperial' reportedly comes from 19th-century Russian royalty who reserved the finest specimens for the Tsar's family. Imperial topaz is primarily mined in Ouro Preto, Brazil, and is one of the most valuable topaz varieties.",
            formation = "Found in granitic pegmatites and hydrothermal veins. The finest specimens come from topaz-bearing pegmatites in Brazil.",
            hardness = "8",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Al₂SiO₄(F,OH)₂",
            commonColors = listOf("Golden orange", "Sherry", "Pinkish-orange", "Champagne"),
            whereFound = listOf("Ouro Preto, Minas Gerais, Brazil", "Pakistan", "Russia (Ural Mountains)", "Nigeria"),
            funFacts = listOf(
                "The name 'Imperial Topaz' reportedly comes from Russian Tsars who reserved the best stones.",
                "Ouro Preto, Brazil is the world's primary source of imperial topaz.",
                "It is the November birthstone and the 23rd anniversary gem.",
                "Natural imperial topaz is much more valuable than treated blue topaz.",
                "The color can fade if exposed to strong sunlight for prolonged periods."
            ),
            uses = "Gemstone (faceted and cabochon), collector specimens, jewelry.",
            rarity = "Rare"
        ),

        Specimen(
            id = "blue-topaz",
            name = "Topaz, Blue",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Topaz Variety (Blue)",
            tagline = "Bright sky-blue to Swiss blue topaz — one of the most popular gemstones.",
            emoji = "🔵",
            colorHex = 0xFF4FC3F7,
            description = "Blue Topaz is a popular topaz variety ranging from pale sky-blue to deep London blue. While some blue topaz occurs naturally, most commercial blue topaz is colorless topaz that has been irradiated and heat-treated to achieve the blue color. It is one of the most affordable and widely available blue gemstones.",
            formation = "Found in granitic pegmatites. Natural blue topaz is rare; most commercial stones are treated colorless topaz.",
            hardness = "8",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "Al₂SiO₄(F,OH)₂",
            commonColors = listOf("Sky blue", "Swiss blue", "London blue", "Pale blue"),
            whereFound = listOf("Brazil", "Sri Lanka", "Nigeria", "Pakistan", "Russia"),
            funFacts = listOf(
                "Most blue topaz on the market is treated — natural blue topaz is quite rare.",
                "The three main commercial shades are Sky Blue, Swiss Blue, and London Blue.",
                "Blue topaz is the December birthstone (along with turquoise and tanzanite).",
                "London blue topaz has a slightly grayish, steely tone compared to brighter Swiss blue."
            ),
            uses = "Gemstone (faceted and cabochon), jewelry, collector specimens.",
            rarity = "Common (treated), Rare (natural)"
        ),


        Specimen(
            id = "angelite",
            name = "Angelite",
            rockClass = RockClass.MINERAL,
            category = "Sulfate — Blue Anhydrite Variety",
            tagline = "Soft blue anhydrite with a serene, sky-blue color — the 'angel stone'.",
            emoji = "😇",
            colorHex = 0xFF87CEEB,
            description = "Angelite is a trade name for a blue, massive variety of anhydrite (calcium sulfate). It has a soft, serene sky-blue to lilac-blue color and a matte to slightly waxy luster. Angelite is found in Peru and is popular in carvings and metaphysical use. The stone is relatively soft and should be kept away from water, as anhydrite can absorb water and convert to gypsum.",
            formation = "Found in evaporite deposits as anhydrite. The blue color is due to trace impurities. Found in massive form rather than as crystals.",
            hardness = "3.5",
            luster = "Waxy to vitreous",
            streak = "White",
            crystalSystem = "Orthorhombic",
            chemicalFormula = "CaSO₄",
            commonColors = listOf("Sky blue", "Lilac blue", "Pale blue", "Blue-white"),
            whereFound = listOf("Peru", "Mexico", "Britain", "Egypt", "Poland"),
            funFacts = listOf(
                "Angelite is a trade name — the mineral is actually anhydrite.",
                "It should never be placed in water, as anhydrite absorbs water and turns into gypsum.",
                "The blue color is natural but the exact cause of the coloration is debated.",
                "It is primarily found in Peru and is popular for carvings and beads."
            ),
            uses = "Carvings, beads, metaphysical, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "shungite",
            name = "Shungite",
            rockClass = RockClass.METAMORPHIC,
            category = "Carbon-Rich Rock — Fullerene-Containing Metamorphic Rock",
            tagline = "Ancient carbon-rich rock from Russia containing fullerenes — a natural marvel.",
            emoji = "⬛",
            colorHex = 0xFF1C1C1C,
            description = "Shungite is a black, carbon-rich metamorphic rock from the Karelia region of Russia. It is notable for containing fullerenes (carbon molecules shaped like soccer balls) — the only natural source of fullerenes known. Shungite has been used in Russian medicine since Peter the Great's time and is claimed to have purifying properties. It has a matte black appearance and is used for carvings, polishing, and water filtration.",
            formation = "Formed from organic-rich sediments that were metamorphosed over 2 billion years. The carbon was originally from organic organisms in Precambrian seas.",
            hardness = "3.5–4",
            luster = "Submetallic to vitreous",
            streak = "Black",
            crystalSystem = "Amorphous (carbon)",
            chemicalFormula = "C (with silicate minerals)",
            commonColors = listOf("Black", "Very dark gray", "Dark silver"),
            whereFound = listOf("Karelia, Russia", "Zazhoginskoye deposit, Russia"),
            funFacts = listOf(
                "Shungite is the only known natural source of fullerenes — carbon molecules shaped like soccer balls.",
                "It is estimated to be about 2 billion years old.",
                "Peter the Great used shungite to purify water for his army.",
                "It contains up to 98% carbon, making it one of the most carbon-rich natural materials.",
                "The discovery of fullerenes in shungite led to the 1996 Nobel Prize in Chemistry."
            ),
            uses = "Water filtration, carvings, polishing, metaphysical, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "bumblebee-jasper",
            name = "Bumblebee Jasper",
            rockClass = RockClass.IGNEOUS,
            category = "Volcanic Rock — Banded Sulfur/Manganese Stone",
            tagline = "Vibrant yellow and black banded volcanic stone from Indonesia — looks like a bumblebee.",
            emoji = "🐝",
            colorHex = 0xFFFFD700,
            description = "Bumblebee Jasper (also called Bumble Bee Stone) is a vibrant yellow, orange, and black banded volcanic rock from Indonesia. Despite the name 'jasper', it is not actually a jasper — it is a volcanic rock containing sulfur, hematite, and goethite. The striking yellow and black banding gives it the appearance of a bumblebee. It should be handled with care as it contains sulfur and can be toxic when cut or polished without proper protection.",
            formation = "Forms in volcanic fumarole deposits near hot springs in Indonesia. The yellow color comes from sulfur, the black from manganese and hematite.",
            hardness = "4–5",
            luster = "Vitreous to waxy",
            streak = "Yellow to orange",
            crystalSystem = "Amorphous (volcanic rock)",
            chemicalFormula = "SiO₂ with S, Fe₂O₃, Mn (variable)",
            commonColors = listOf("Yellow", "Orange", "Black", "White bands"),
            whereFound = listOf("Mount Papandayan, West Java, Indonesia"),
            funFacts = listOf(
                "Bumblebee Jasper is not actually jasper — it is a volcanic rock.",
                "It contains sulfur, which gives it the bright yellow color.",
                "It should be cut with care — the dust contains sulfur and can be harmful.",
                "All specimens come from a single location on Mount Papandayan in Indonesia.",
                "The banding pattern is caused by alternating layers of sulfur and iron/manganese minerals."
            ),
            uses = "Cabochons, carvings, collector specimens, jewelry (with precautions).",
            rarity = "Rare"
        ),

        Specimen(
            id = "tiger-iron",
            name = "Tiger Iron",
            rockClass = RockClass.METAMORPHIC,
            category = "Banded Rock — Tiger's Eye + Hematite + Jasper",
            tagline = "Banded rock of golden tiger's eye, red jasper, and silver hematite — a natural tri-color composite.",
            emoji = "🐅",
            colorHex = 0xFFB8860B,
            description = "Tiger Iron is a banded metamorphic rock composed of three minerals: golden tiger's eye (chatoyant quartz), red jasper, and metallic silver hematite. The alternating bands create a striking tri-color pattern that is popular in cabochons and carvings. It is a Precambrian banded iron formation that has been metamorphosed.",
            formation = "Forms from Precambrian banded iron formations that have been metamorphosed. The tiger's eye component forms when crocidolite (blue asbestos) is replaced by silica.",
            hardness = "6.5–7",
            luster = "Vitreous to submetallic",
            streak = "Yellow-brown to red-brown",
            crystalSystem = "Amorphous (rock)",
            chemicalFormula = "SiO₂ + Fe₂O₃ (composite rock)",
            commonColors = listOf("Golden brown", "Red", "Silver-gray", "Black"),
            whereFound = listOf("Pilbara region, Western Australia", "South Africa", "India"),
            funFacts = listOf(
                "Tiger Iron is a natural composite of tiger's eye, red jasper, and hematite.",
                "It is a Precambrian banded iron formation — over 2 billion years old.",
                "The chatoyant (cat's eye) effect of the tiger's eye component creates a shimmering golden band.",
                "The best specimens come from the Pilbara region of Western Australia."
            ),
            uses = "Cabochons, carvings, jewelry, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "wild-horse-magnesite",
            name = "Wild Horse Magnesite",
            rockClass = RockClass.MINERAL,
            category = "Carbonate — White Magnesite with Brown Manganese Veins",
            tagline = "White magnesite with dramatic brown veins — a striking patterned cabbing material.",
            emoji = "🐴",
            colorHex = 0xFFF5F0E0,
            description = "Wild Horse Magnesite (also called 'Crazy Horse Stone') is a white magnesite with dramatic brown to black manganese dioxide veins. Despite the trade name, it is not related to horses — the name comes from the wild, striking patterns. It is primarily found in South Dakota, USA, and is popular for cabochons and carvings due to its bold contrast.",
            formation = "Forms as magnesite veins in metamorphic rocks, with manganese dioxide forming dark dendritic patterns along fractures.",
            hardness = "3.5–4.5",
            luster = "Vitreous to earthy",
            streak = "White",
            crystalSystem = "Trigonal",
            chemicalFormula = "MgCO₃ (with MnO₂ inclusions)",
            commonColors = listOf("White", "Cream", "Brown veins", "Black veins"),
            whereFound = listOf("South Dakota, USA", "Cave Creek, Arizona, USA"),
            funFacts = listOf(
                "Also known as 'Crazy Horse Stone' — both names reference the striking wild patterns.",
                "Despite the name, it has nothing to do with horses.",
                "The brown/black patterns are manganese dioxide dendrites growing through white magnesite.",
                "It is primarily found in South Dakota and Arizona."
            ),
            uses = "Cabochons, carvings, beads, collector specimens.",
            rarity = "Uncommon"
        ),

        // ════════════════════════════════════════════
        // ADDITIONAL FROM FOSSILERA (3)
        // ════════════════════════════════════════════

        Specimen(
            id = "chrome-diopside",
            name = "Chrome Diopside",
            rockClass = RockClass.CRYSTAL,
            category = "Gemstone — Diopside Variety (Chromium-Rich Green)",
            tagline = "Intense green chromium-rich diopside — an affordable emerald alternative.",
            emoji = "🟢",
            colorHex = 0xFF228B22,
            description = "Chrome Diopside is a chromium-rich variety of diopside with an intense, vivid green color that rivals emerald. The chromium impurities give it a bright green color that can range from yellowish-green to deep forest green. While it is a beautiful gemstone, it is softer than emerald and less suitable for everyday wear rings.",
            formation = "Found in peridotite and kimberlite deposits. The chromium that gives it its green color comes from the ultramafic host rocks.",
            hardness = "5.5–6.5",
            luster = "Vitreous",
            streak = "White to pale green",
            crystalSystem = "Monoclinic",
            chemicalFormula = "CaMgSi₂O₆ (with Cr³⁺)",
            commonColors = listOf("Deep green", "Forest green", "Yellowish-green", "Bright green"),
            whereFound = listOf("Siberia, Russia", "Pakistan", "Finland", "South Africa", "Australia"),
            funFacts = listOf(
                "Chrome diopside's vivid green color can rival fine emeralds at a fraction of the cost.",
                "The green color comes from chromium, the same element that colors emerald and ruby.",
                "Most chrome diopside comes from Siberia, where mining can only occur in summer.",
                "It is relatively soft for a gemstone and is best used in pendants and earrings."
            ),
            uses = "Gemstone (faceted and cabochon), jewelry, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "striped-flint",
            name = "Striped Flint",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Banded Flint (Chert) Variety",
            tagline = "Banded flint from Poland with striking parallel stripes — 350 million years old.",
            emoji = "🟫",
            colorHex = 0xFF8B7355,
            description = "Striped Flint (also known as Banded Flint) is a distinctive variety of flint from Poland featuring parallel dark and light bands. It is approximately 350 million years old (Carboniferous period) and is found only in the Świętokrzyskie (Holy Cross) Mountains of Poland. When cut and polished, the parallel banding creates striking geometric patterns.",
            formation = "Forms in marine sedimentary environments as silica-rich nodules in limestone. The banding reflects alternating conditions during silica deposition.",
            hardness = "7",
            luster = "Vitreous to waxy",
            streak = "White",
            crystalSystem = "Amorphous (cryptocrystalline silica)",
            chemicalFormula = "SiO₂",
            commonColors = listOf("Gray", "Brown", "Black", "White (banded)"),
            whereFound = listOf("Świętokrzyskie Mountains, Poland (only source)"),
            funFacts = listOf(
                "Striped Flint is found in only one place on Earth: the Holy Cross Mountains in Poland.",
                "It is about 350 million years old, from the Carboniferous period.",
                "When cut perpendicular to the banding, it creates striking concentric or parallel patterns.",
                "It has been used for tools since the Stone Age — Polish archaeological sites show its use.",
                "Modern Polish artisans create distinctive jewelry and decorative objects from it."
            ),
            uses = "Jewelry, carvings, collector specimens, decorative objects.",
            rarity = "Rare"
        ),

        Specimen(
            id = "chrysanthemum-stone",
            name = "Chrysanthemum Stone",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Flower-Patterned Stone with Crystal Inclusions",
            tagline = "Stone with natural crystal patterns that look like blooming chrysanthemum flowers.",
            emoji = "🌼",
            colorHex = 0xFFD4A017,
            description = "Chrysanthemum Stone is a dark limestone or shale containing white crystal formations that resemble chrysanthemum flowers. The 'flower' patterns are created by radiating crystals of celestite, calcite, or andalusite that grow in a star-like pattern within the dark matrix. It has been prized in China and Japan for centuries as a symbol of longevity and happiness.",
            formation = "Forms in marine sedimentary deposits where mineral-rich solutions crystallize radiating patterns in limestone or shale. The crystals grow outward from a central point.",
            hardness = "4–5 (varies with composition)",
            luster = "Vitreous (crystals) / Dull (matrix)",
            streak = "White",
            crystalSystem = "Varies (celestite: orthorhombic; calcite: trigonal)",
            chemicalFormula = "Matrix: CaCO₃ (limestone). Crystals: SrSO₄ (celestite) or CaCO₃ (calcite)",
            commonColors = listOf("Dark gray/black matrix", "White crystal 'petals'", "Brownish matrix"),
            whereFound = listOf("Hunan Province, China", "Yokohama, Japan", "British Columbia, Canada", "Pennsylvania, USA"),
            funFacts = listOf(
                "The white crystal patterns look like blooming chrysanthemum flowers — hence the name.",
                "It has been carved and collected in China for over 300 years.",
                "In Chinese culture, the chrysanthemum symbolizes longevity and resilience.",
                "The 'flower' crystals are actually celestite, calcite, or andalusite growing in a radiating pattern.",
                "Skilled carvers enhance the natural flower patterns by carving the surrounding matrix."
            ),
            uses = "Carvings, decorative objects, collector specimens, jewelry.",
            rarity = "Rare"
        )
    )
}
