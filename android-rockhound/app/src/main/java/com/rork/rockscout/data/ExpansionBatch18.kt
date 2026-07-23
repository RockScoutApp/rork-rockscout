package com.rork.rockscout.data

/**
 * Database expansion wave 18 — Phase 3: Rock types from Wikipedia List of Rock Types.
 * Igneous, sedimentary, and metamorphic rock types not already in the app.
 * Generated 2026-07-22.
 */
@Suppress("unused")
object ExpansionBatch18 {
    val specimens: List<Specimen> = listOf(

        // ════════════════════════════════════════════
        // IGNEOUS (18)
        // ════════════════════════════════════════════

        Specimen(
            id = "dolerite",
            name = "Dolerite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Mafic Intrusive (Subvolcanic)",
            tagline = "Dark, medium-grained intrusive rock — the same as diabase, widely used in construction.",
            emoji = "🪨",
            colorHex = 0xFF3D3D3D,
            description = "Dolerite (also called Diabase in North America) is a mafic, holocrystalline, subvolcanic rock equivalent to basalt in composition but with medium grain size. It forms in shallow intrusions like sills and dikes. Dolerite is one of the most common intrusive rocks and is widely used as crushed stone, dimension stone, and in road construction.",
            formation = "Forms when basaltic magma intrudes into shallow crustal layers as sills, dikes, or plugs. The moderate cooling rate produces medium-grained textures between fine-grained basalt and coarse-grained gabbro.",
            hardness = "6–7",
            luster = "Dull",
            streak = "Dark gray to black",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Ca-rich plagioclase + pyroxene (augite)",
            commonColors = listOf("Dark gray", "Black", "Greenish-black"),
            whereFound = listOf("Worldwide (Whin Sill, England)", "Palisades Sill, New Jersey, USA", "Giant's Causeway, Northern Ireland", "Australia", "South Africa"),
            funFacts = listOf(
                "Dolerite is the European name; the same rock is called 'diabase' in North America.",
                "The Whin Sill in England is one of the largest dolerite intrusions in the world.",
                "Giant's Causeway columns are made of dolerite (technically basalt, but closely related).",
                "Dolerite is extremely tough and resistant to weathering."
            ),
            uses = "Crushed stone, road construction, dimension stone, kerbstones.",
            rarity = "Common"
        ),

        Specimen(
            id = "ignimbrite",
            name = "Ignimbrite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Welded Tuff (Pyroclastic)",
            tagline = "Welded volcanic ash from devastating pyroclastic flows — the rock of cataclysmic eruptions.",
            emoji = "🌋",
            colorHex = 0xFF8B7355,
            description = "Ignimbrite is a volcanic rock formed from the deposits of pyroclastic flows — superheated avalanches of gas, ash, and rock fragments. The material is so hot when deposited that the ash particles weld together, creating a hard, dense rock. Ignimbrites are the product of the most catastrophic volcanic eruptions on Earth.",
            formation = "Forms when a pyroclastic flow — a fast-moving current of hot gas and volcanic matter — settles and cools. The extreme heat welds the ash particles together into a solid rock.",
            hardness = "5–6",
            luster = "Dull",
            streak = "White to grayish",
            crystalSystem = "Volcanic (composite)",
            chemicalFormula = "Variable (rhyolitic to andesitic composition)",
            commonColors = listOf("Gray", "Pink", "White", "Reddish-brown"),
            whereFound = listOf("Bandelier Tuff, New Mexico, USA", "Yellowstone, USA", "Taupo, New Zealand", "Campi Flegrei, Italy", "Turkey"),
            funFacts = listOf(
                "Ignimbrites form from pyroclastic flows — the most destructive volcanic phenomenon.",
                "The Bandelier Tuff in New Mexico was formed by a massive eruption 1.25 million years ago.",
                "Ignimbrite deposits can cover hundreds of square kilometers from a single eruption.",
                "The word 'ignimbrite' means 'fire rain stone' in Latin."
            ),
            uses = "Building stone, geological research, collector specimens.",
            rarity = "Common"
        ),

        Specimen(
            id = "tonalite",
            name = "Tonalite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Felsic Plutonic (Granite Family)",
            tagline = "Light-colored granitic rock with abundant quartz and sodic plagioclase.",
            emoji = "⬜",
            colorHex = 0xFFE8E0D0,
            description = "Tonalite is a felsic intrusive igneous rock, similar to granite but containing little to no alkali feldspar. It is composed primarily of plagioclase feldspar and quartz, with minor biotite and hornblende. Tonalite is light-colored and is an important rock type in the continental crust.",
            formation = "Forms from the slow crystallization of felsic magma deep within the Earth's crust. Commonly found in batholiths and large plutonic intrusions.",
            hardness = "6–7",
            luster = "Dull",
            streak = "White to gray",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Plagioclase (Na-rich) + quartz + biotite/hornblende",
            commonColors = listOf("Light gray", "White", "Pinkish-gray"),
            whereFound = listOf("Tonale Pass, Italian Alps (type locality)", "Sierra Nevada, California, USA", "Coast Mountains, British Columbia, Canada", "Norway", "Finland"),
            funFacts = listOf(
                "Named after the Tonale Pass in the Italian Alps.",
                "Tonalite is essentially granite without alkali feldspar.",
                "It is a major component of the continental crust.",
                "The Coast Plutonic Complex in British Columbia is one of the largest tonalite bodies on Earth."
            ),
            uses = "Building stone, geological research, collector specimens.",
            rarity = "Common"
        ),

        Specimen(
            id = "troctolite",
            name = "Troctolite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Ultramafic Plutonic",
            tagline = "Olive-green intrusive rock made of plagioclase and olivine — no pyroxene.",
            emoji = "🟢",
            colorHex = 0xFF4A6B4A,
            description = "Troctolite is an ultramafic intrusive igneous rock composed essentially of plagioclase feldspar and olivine, with little or no pyroxene. It is a coarse-grained rock with a distinctive mottled appearance — the light plagioclase and green olivine create a speckled texture. Troctolite is part of the layered igneous complexes that host important mineral deposits.",
            formation = "Forms in layered mafic-ultramafic intrusions through crystal settling. The dense olivine crystals sink while lighter plagioclase floats, creating alternating layers.",
            hardness = "6–7",
            luster = "Dull",
            streak = "White to greenish",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Plagioclase + olivine",
            commonColors = listOf("Green and white", "Mottled gray-green", "Olive green"),
            whereFound = listOf("Stillwater Complex, Montana, USA", "Sudbury, Ontario, Canada", "Bushveld Complex, South Africa", "Rum, Scotland", "Kola Peninsula, Russia"),
            funFacts = listOf(
                "The name comes from the Greek 'troktes' meaning 'trout' — the speckled texture resembles trout skin.",
                "Troctolite is one of the few rocks that contains olivine but no pyroxene.",
                "It forms in layered intrusions where crystals separate by density.",
                "Some troctolites contain valuable nickel-copper-platinum deposits."
            ),
            uses = "Geological research, collector specimens, indicator of layered intrusion environments.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "adakite",
            name = "Adakite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Intermediate Volcanic",
            tagline = "High-silica volcanic rock formed by melting subducted oceanic crust.",
            emoji = "🟫",
            colorHex = 0xFF8B6B4A,
            description = "Adakite is a volcanic rock with unusual chemistry — high silica and strontium but low yttrium and heavy rare earth elements. This distinctive chemistry forms when young, hot oceanic crust melts during subduction, rather than the normal mantle-wedge melting that produces most arc volcanics. Adakites are important for understanding subduction zone processes.",
            formation = "Forms when young, hot subducted oceanic crust melts directly, rather than triggering melting in the mantle wedge above. This typically occurs where young crust is being subducted.",
            hardness = "5–6",
            luster = "Dull",
            streak = "White to grayish",
            crystalSystem = "Volcanic (composite)",
            chemicalFormula = "Intermediate volcanic (high SiO₂, Sr/Y)",
            commonColors = listOf("Gray", "Brownish-gray", "Light gray"),
            whereFound = listOf("Adak Island, Aleutian Islands, USA (type locality)", "Cook Islands", "Ecuador", "Japan", "Philippines"),
            funFacts = listOf(
                "Named after Adak Island in the Aleutian Islands where it was first identified.",
                "Adakites form by melting of subducted oceanic crust, which is geologically unusual.",
                "They are associated with gold and copper porphyry deposits.",
                "Their distinctive chemistry helps geologists understand subduction dynamics."
            ),
            uses = "Geological research, indicator for mineral exploration, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "essexite",
            name = "Essexite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Alkaline Mafic Plutonic",
            tagline = "Dark alkaline intrusive rock with nepheline and augite — an uncommon rock type.",
            emoji = "⬛",
            colorHex = 0xFF2D2D2D,
            description = "Essexite is a dark, alkaline, mafic intrusive igneous rock. It is the intrusive equivalent of basanite and contains plagioclase, augite, nepheline, and sometimes biotite or hornblende. Essexite is part of the alkaline igneous rock series and is found in specific tectonic settings involving continental rifting.",
            formation = "Forms in alkaline magmatic complexes, typically associated with continental rifting or hotspots. The alkaline chemistry indicates low silica and high alkali content.",
            hardness = "5–6",
            luster = "Dull",
            streak = "Dark gray to black",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Plagioclase + augite + nepheline + biotite",
            commonColors = listOf("Black", "Dark gray", "Greenish-black"),
            whereFound = listOf("Essex County, Massachusetts, USA (type locality)", "Kola Peninsula, Russia", "Tamazeght Complex, Morocco", "Scotland", "Norway"),
            funFacts = listOf(
                "Named after Essex County, Massachusetts where it was first described.",
                "Essexite is part of the rare alkaline igneous rock family.",
                "It is the intrusive equivalent of basanite (a volcanic rock).",
                "Alkaline igneous complexes containing essexite are often associated with rare earth deposits."
            ),
            uses = "Geological research, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "icelandite",
            name = "Icelandite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Intermediate Volcanic",
            tagline = "Iron-rich intermediate volcanic rock — the halfway point between basalt and andesite.",
            emoji = "🟫",
            colorHex = 0xFF6B5B4A,
            description = "Icelandite is an intermediate volcanic rock with a composition between basalt and andesite. It is characteristically iron-rich and is found primarily in Iceland. Icelandite has higher iron content than typical andesite, which distinguishes it as a separate rock type.",
            formation = "Forms in volcanic settings, particularly at mid-ocean ridges and hotspot-related volcanism. In Iceland, it results from partial melting of a mantle plume source.",
            hardness = "5–6",
            luster = "Dull",
            streak = "Grayish-white",
            crystalSystem = "Volcanic (composite)",
            chemicalFormula = "Intermediate volcanic (Fe-rich andesite)",
            commonColors = listOf("Dark gray", "Brownish-gray", "Black"),
            whereFound = listOf("Iceland (type locality and main source)", "Galapagos Islands", "Easter Island"),
            funFacts = listOf(
                "Icelandite is named after Iceland where it was first identified.",
                "It occupies a compositional gap between basalt and andesite.",
                "The high iron content distinguishes it from typical andesite.",
                "It forms at constructive plate margins and hotspot volcanoes, not at subduction zones like most andesite."
            ),
            uses = "Geological research, collector specimens, building material.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "trachyandesite",
            name = "Trachyandesite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Alkaline Intermediate Volcanic",
            tagline = "Alkaline volcanic rock between trachyte and andesite in composition.",
            emoji = "🟫",
            colorHex = 0xFF7B6B5A,
            description = "Trachyandesite (also called Latite) is a volcanic rock intermediate between trachyte and andesite. It contains both alkali feldspar and plagioclase in roughly equal amounts, along with mafic minerals like augite and hornblende. It is part of the alkaline volcanic rock series.",
            formation = "Forms in volcanic settings associated with continental rifting or hotspot magmatism. The alkaline composition indicates a mantle source with low partial melting.",
            hardness = "5–6",
            luster = "Dull",
            streak = "White to grayish",
            crystalSystem = "Volcanic (composite)",
            chemicalFormula = "Alkali feldspar + plagioclase + augite/hornblende",
            commonColors = listOf("Gray", "Pinkish-gray", "Brownish-gray"),
            whereFound = listOf("Roman Volcanic Province, Italy", "Eifel, Germany", "Western USA", "East Africa", "Turkey"),
            funFacts = listOf(
                "Also called 'latite' in some classification schemes.",
                "The name combines 'trachyte' and 'andesite' — it's intermediate between the two.",
                "Trachyandesites are common in the Roman Volcanic Province of Italy.",
                "Some trachyandesite eruptions are highly explosive."
            ),
            uses = "Geological research, building stone, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "trachybasalt",
            name = "Trachybasalt",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Alkaline Mafic Volcanic",
            tagline = "Alkaline basalt with feldspathoids — a close relative of basanite.",
            emoji = "⬛",
            colorHex = 0xFF3D3D3D,
            description = "Trachybasalt is an alkaline, mafic volcanic rock that is similar to basalt but with higher alkali content. It contains plagioclase, augite, and some alkali feldspar or foid minerals. Trachybasalt is part of the alkaline volcanic rock series and is found in continental rift and ocean island settings.",
            formation = "Forms in continental rift zones and ocean island settings from low-degree partial melting of the mantle. The alkaline chemistry results from a deeper mantle source.",
            hardness = "5–6",
            luster = "Dull",
            streak = "Grayish",
            crystalSystem = "Volcanic (composite)",
            chemicalFormula = "Plagioclase + augite + foids/alkali feldspar",
            commonColors = listOf("Dark gray", "Black", "Brownish-black"),
            whereFound = listOf("Etna, Sicily", "East African Rift", "Hawaii", "Iceland", "Australia"),
            funFacts = listOf(
                "Trachybasalt is the volcanic equivalent of essexite.",
                "Mount Etna in Sicily erupts trachybasaltic lava.",
                "The high alkali content makes the lava more fluid than typical basalt in some cases.",
                "It is part of a spectrum of alkaline volcanic rocks from basanite to trachyte."
            ),
            uses = "Geological research, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "tephrite",
            name = "Tephrite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Alkaline Mafic Volcanic",
            tagline = "Dark volcanic rock with feldspathoids — similar to basanite but without olivine.",
            emoji = "⬛",
            colorHex = 0xFF2D2D2D,
            description = "Tephrite is an alkaline, mafic volcanic rock similar to basanite but lacking olivine. It is composed primarily of plagioclase, augite, and feldspathoid minerals (nepheline or leucite). The name comes from the Greek 'tephra' meaning 'ash', reflecting its common occurrence in pyroclastic deposits.",
            formation = "Forms in alkaline volcanic settings, typically continental rifts or ocean islands. The presence of feldspathoids instead of feldspar indicates silica-undersaturated conditions.",
            hardness = "5–6",
            luster = "Dull",
            streak = "White to grayish",
            crystalSystem = "Volcanic (composite)",
            chemicalFormula = "Plagioclase + augite + nepheline/leucite",
            commonColors = listOf("Dark gray", "Black", "Brownish-gray"),
            whereFound = listOf("Eifel, Germany", "Italy (Vesuvius)", "East African Rift", "Canary Islands", "Spain"),
            funFacts = listOf(
                "The name comes from the Greek 'tephra' meaning 'ash'.",
                "Tephrite is distinguished from basanite by the absence of olivine.",
                "Mount Vesuvius has erupted tephritic lava.",
                "The presence of leucite or nepheline indicates the magma was silica-undersaturated."
            ),
            uses = "Geological research, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "quartz-monzonite",
            name = "Quartz Monzonite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Felsic Plutonic (Granite Family)",
            tagline = "Granitic rock with roughly equal alkali feldspar and plagioclase, plus quartz.",
            emoji = "⬜",
            colorHex = 0xFFD8D0C0,
            description = "Quartz Monzonite is a felsic intrusive igneous rock with roughly equal amounts of alkali feldspar and plagioclase, plus 20-40% quartz. It is similar to granite but with more plagioclase. Quartz monzonite is a common rock type in the continental crust and is used as a building and decorative stone.",
            formation = "Forms from the slow crystallization of felsic magma in large plutonic intrusions. Commonly found in batholiths and stocks.",
            hardness = "6–7",
            luster = "Dull",
            streak = "White to gray",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Alkali feldspar + plagioclase + quartz (20-40%)",
            commonColors = listOf("Light gray", "Pinkish-gray", "White with dark specks"),
            whereFound = listOf("Yosemite, California, USA", "Idaho Batholith, USA", "Brazil", "Norway", "China"),
            funFacts = listOf(
                "Quartz monzonite is the rock that makes up much of the Yosemite batholith.",
                "Half Dome in Yosemite is made of quartz monzonite (technically a granodiorite variant).",
                "It has roughly equal amounts of alkali feldspar and plagioclase, distinguishing it from granite.",
                "It is widely used as a dimension and decorative stone."
            ),
            uses = "Building stone, dimension stone, decorative stone, geological research.",
            rarity = "Common"
        ),

        Specimen(
            id = "volcanic-bomb",
            name = "Volcanic Bomb",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Pyroclastic (Lava Projectile)",
            tagline = "Solidified lava projectile — molten rock thrown from a volcano that cooled in flight.",
            emoji = "💣",
            colorHex = 0xFF3D3D3D,
            description = "Volcanic bombs are masses of molten lava ejected from a volcano that solidify in flight or upon landing. They range from a few centimeters to over a meter in diameter and often have aerodynamic shapes like spindle, ribbon, or bread-crust forms. Volcanic bombs are important pyroclastic deposits that record eruption dynamics.",
            formation = "Forms when molten lava is ejected from a volcanic vent and cools as it flies through the air. The spinning motion creates aerodynamic shapes, and the outer surface often cracks into a 'bread-crust' texture as it cools.",
            hardness = "5–6",
            luster = "Dull to vitreous",
            streak = "Variable",
            crystalSystem = "Volcanic (varies)",
            chemicalFormula = "Variable (basaltic to andesitic)",
            commonColors = listOf("Black", "Dark red", "Gray", "Brown"),
            whereFound = listOf("Stromboli, Italy", "Hawaii, USA", "Iceland", "Mount Etna, Italy", "Nicaragua"),
            funFacts = listOf(
                "Volcanic bombs can travel hundreds of meters through the air.",
                "The 'bread-crust' texture forms when the outer surface cools and cracks as the interior continues to expand.",
                "Spindle bombs form when the lava spins during flight, creating a streamlined shape.",
                "Volcanic bombs can still be molten inside when they land, creating splash patterns."
            ),
            uses = "Collector specimens, geological research, educational displays.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "granophyre",
            name = "Granophyre",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Felsic Subvolcanic (Graphic Granite Texture)",
            tagline = "Granitic rock with intergrown quartz and feldspar creating a graphic texture.",
            emoji = "⬜",
            colorHex = 0xFFD0C8B8,
            description = "Granophyre is a felsic, subvolcanic igneous rock characterized by a granophyric texture — intergrowth of quartz and alkali feldspar that creates a 'graphic' or cuneiform pattern. This texture forms during the final stages of crystallization when quartz and feldspar grow simultaneously from the remaining melt.",
            formation = "Forms at shallow depths from felsic magma. The granophyric texture develops during rapid final crystallization when quartz and feldspar grow simultaneously.",
            hardness = "6–7",
            luster = "Dull",
            streak = "White",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Quartz + alkali feldspar",
            commonColors = listOf("Pink", "Gray", "White", "Reddish-pink"),
            whereFound = listOf("Skaergaard Intrusion, Greenland", "Muskox Intrusion, Canada", "Western Australia", "Scotland", "South Africa"),
            funFacts = listOf(
                "The granophyric texture looks like ancient cuneiform writing — hence 'graphic granite'.",
                "It forms during the final stages of crystallization of a felsic magma.",
                "Granophyre is common in the upper portions of layered intrusions.",
                "The intergrowth texture records the simultaneous growth of quartz and feldspar."
            ),
            uses = "Geological research, collector specimens, decorative stone.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "monzodiorite",
            name = "Monzodiorite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Intermediate Plutonic",
            tagline = "Intermediate intrusive rock between monzonite and diorite.",
            emoji = "🟫",
            colorHex = 0xFF9B8B7A,
            description = "Monzodiorite is an intermediate intrusive igneous rock between monzonite and diorite in composition. It contains more plagioclase than alkali feldspar, plus minor quartz and mafic minerals. Monzodiorite is part of the granitic rock family and is found in many large intrusive complexes.",
            formation = "Forms in large plutonic intrusions from intermediate-composition magma. Common in continental arc settings.",
            hardness = "6–7",
            luster = "Dull",
            streak = "White to gray",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Plagioclase + alkali feldspar + mafic minerals",
            commonColors = listOf("Gray", "Salt-and-pepper", "Dark gray"),
            whereFound = listOf("Andes (continental arc)", "Sierra Nevada, USA", "Alps", "Japan", "Iran"),
            funFacts = listOf(
                "Monzodiorite sits between monzonite and diorite in the QAPF classification.",
                "It is common in continental arc magmatism above subduction zones.",
                "Some monzodiorites host significant copper and gold deposits.",
                "The rock has a 'salt and pepper' appearance from light feldspar and dark mafic minerals."
            ),
            uses = "Geological research, building stone, collector specimens.",
            rarity = "Common"
        ),

        Specimen(
            id = "pyroxenite",
            name = "Pyroxenite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Ultramafic Plutonic",
            tagline = "Dark, ultramafic rock composed almost entirely of pyroxene minerals.",
            emoji = "⬛",
            colorHex = 0xFF1F2F1F,
            description = "Pyroxenite is an ultramafic intrusive igneous rock composed essentially of pyroxene minerals (clinopyroxene and/or orthopyroxene). It is dark green to black and forms in layered intrusions, mantle fragments, and as xenoliths in basalt. Pyroxenite is an important mantle-derived rock type.",
            formation = "Forms in layered mafic-ultramafic intrusions through crystal accumulation, or as mantle fragments brought up by basaltic magmas. Also found in ophiolite complexes.",
            hardness = "5–6",
            luster = "Dull",
            streak = "Grayish-green to brown",
            crystalSystem = "Igneous (pyroxene minerals)",
            chemicalFormula = "(Ca,Mg,Fe)SiO₃ (pyroxene)",
            commonColors = listOf("Dark green", "Black", "Greenish-black"),
            whereFound = listOf("Bushveld Complex, South Africa", "Stillwater Complex, Montana, USA", "Oman Ophiolite", "Bay of Islands, Newfoundland, Canada", "Lherz, France"),
            funFacts = listOf(
                "Pyroxenite is one of the few rocks that is almost entirely composed of a single mineral group.",
                "It represents parts of the Earth's upper mantle that have been brought to the surface.",
                "Layered pyroxenites in the Bushveld Complex contain important platinum deposits.",
                "Mantle xenoliths brought up by basaltic eruptions are often pyroxenite."
            ),
            uses = "Geological research, chromium and platinum source, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "rhyodacite",
            name = "Rhyodacite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Felsic Volcanic",
            tagline = "Volcanic rock intermediate between rhyolite and dacite.",
            emoji = "⬜",
            colorHex = 0xFFD0C0B0,
            description = "Rhyodacite is a volcanic rock with a composition between rhyolite and dacite. It is a felsic rock with high silica content, containing quartz, plagioclase, and alkali feldspar. Rhyodacite lava is viscous and often produces explosive eruptions or thick, stubby lava flows.",
            formation = "Forms from felsic magma at volcanic arcs above subduction zones. The high silica content makes the lava viscous, often leading to explosive eruptions.",
            hardness = "6–7",
            luster = "Dull",
            streak = "White to gray",
            crystalSystem = "Volcanic (composite)",
            chemicalFormula = "Quartz + plagioclase + alkali feldspar (high SiO₂)",
            commonColors = listOf("Light gray", "Pinkish-gray", "White", "Reddish"),
            whereFound = listOf("Yellowstone, USA", "Andes", "Japan", "New Zealand", "Iceland"),
            funFacts = listOf(
                "Rhyodacite sits between rhyolite and dacite in the TAS volcanic classification.",
                "Yellowstone's large volcanic eruptions produced vast quantities of rhyodacite ash.",
                "Its high silica content makes rhyodacite lava extremely viscous.",
                "Some rhyodacites contain observable phenocrysts of quartz and feldspar in a fine-grained matrix."
            ),
            uses = "Geological research, building stone, collector specimens.",
            rarity = "Common"
        ),

        Specimen(
            id = "trondhjemite",
            name = "Trondhjemite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Felsic Plutonic (Tonalite Variant)",
            tagline = "Light-colored leucocratic tonalite — a major component of ancient continental crust.",
            emoji = "⬜",
            colorHex = 0xFFE0D8C8,
            description = "Trondhjemite is a light-colored, leucocratic variety of tonalite. It is composed primarily of plagioclase and quartz with very little dark mafic minerals. Trondhjemite is a major component of Archean continental crust and is important in understanding early Earth geology.",
            formation = "Forms in two main settings: as leucocratic differentiates of tonalite intrusions, and as the dominant rock type in Archean TTG (tonalite-trondhjemite-granodiorite) crust.",
            hardness = "6–7",
            luster = "Dull",
            streak = "White",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Na-rich plagioclase + quartz (low mafic content)",
            commonColors = listOf("White", "Light gray", "Cream"),
            whereFound = listOf("Trondheim, Norway (type locality)", "Pilbara Craton, Western Australia", "Superior Province, Canada", "Greenland (Archean)", "Finland"),
            funFacts = listOf(
                "Named after Trondheim, Norway, where it was first described.",
                "Trondhjemite is one of the most abundant rocks in Earth's early continental crust.",
                "It is part of the Archean TTG (tonalite-trondhjemite-granodiorite) suite.",
                "Most of the Earth's oldest rocks (3.5+ billion years) are trondhjemitic."
            ),
            uses = "Geological research, especially Archean crustal studies, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "tephriphonolite",
            name = "Tephriphonolite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Alkaline Intermediate Volcanic",
            tagline = "Rare alkaline volcanic rock between tephrite and phonolite.",
            emoji = "🟫",
            colorHex = 0xFF6B5B4A,
            description = "Tephriphonolite is an alkaline volcanic rock transitional between tephrite and phonolite. It contains both plagioclase and feldspathoid minerals, with more alkali feldspar than tephrite but less than phonolite. It is a relatively rare rock type found in specific alkaline volcanic provinces.",
            formation = "Forms in continental rift and hotspot volcanic settings from low-degree partial melting of the mantle. The alkaline chemistry results from deep mantle sources.",
            hardness = "5–6",
            luster = "Dull",
            streak = "White to grayish",
            crystalSystem = "Volcanic (composite)",
            chemicalFormula = "Plagioclase + foids + alkali feldspar",
            commonColors = listOf("Gray", "Greenish-gray", "Brownish-gray"),
            whereFound = listOf("Eifel, Germany", "East African Rift", "Canary Islands", "Italy", "France (Massif Central)"),
            funFacts = listOf(
                "Tephriphonolite sits between tephrite and phonolite in the TAS classification.",
                "It is one of the less common alkaline volcanic rock types.",
                "The Eifel volcanic field in Germany has good examples.",
                "The rock name reflects its intermediate position in the alkaline volcanic series."
            ),
            uses = "Geological research, collector specimens.",
            rarity = "Rare"
        ),

        // ════════════════════════════════════════════
        // SEDIMENTARY (12)
        // ════════════════════════════════════════════

        Specimen(
            id = "claystone",
            name = "Claystone",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Mudrock (Clay-Rich)",
            tagline = "Fine-grained sedimentary rock composed primarily of clay-sized particles.",
            emoji = "🟤",
            colorHex = 0xFF8B7355,
            description = "Claystone is a fine-grained sedimentary rock composed primarily of clay-sized particles (less than 1/256 mm). Unlike shale, claystone does not have visible lamination or fissility. It is one of the most common sedimentary rocks and forms from the compaction of clay-rich sediments.",
            formation = "Forms from the compaction and lithification of clay-rich sediments deposited in low-energy aquatic environments like lakes, deep ocean basins, and floodplains.",
            hardness = "1–3",
            luster = "Dull to earthy",
            streak = "Variable (white, gray, red, green)",
            crystalSystem = "Sedimentary (clay minerals)",
            chemicalFormula = "Hydrous aluminum silicates (clay minerals)",
            commonColors = listOf("Gray", "Red", "Brown", "Green", "White"),
            whereFound = listOf("Worldwide", "Green River Formation, Wyoming, USA", "Clarkia Fossil Beds, Idaho, USA"),
            funFacts = listOf(
                "Claystone is made of particles so fine they cannot be seen with a regular microscope.",
                "Unlike shale, claystone lacks the characteristic lamination that allows shale to split into layers.",
                "Some claystones contain exceptionally preserved fossils, like the Clarkia fossil beds in Idaho.",
                "Bentonite is a type of claystone formed from altered volcanic ash."
            ),
            uses = "Ceramics, construction materials, geological research, paleontology.",
            rarity = "Common"
        ),

        Specimen(
            id = "calcarenite",
            name = "Calcarenite",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Limestone (Sand-Sized Calcium Carbonate)",
            tagline = "Limestone made of sand-sized calcite fragments — essentially a 'calcareous sandstone'.",
            emoji = "⬜",
            colorHex = 0xFFE8E0D0,
            description = "Calcarenite is a type of limestone composed of sand-sized (0.0625 to 2 mm) calcium carbonate grains. These grains can be shell fragments, coral debris, ooids, or other calcareous material. Calcarenite is essentially a calcareous sandstone and is common in shallow marine environments.",
            formation = "Forms in shallow marine environments where calcium carbonate material accumulates. The sand-sized grains are typically shell fragments, peloids, or ooids that become lithified.",
            hardness = "3–4",
            luster = "Dull",
            streak = "White",
            crystalSystem = "Sedimentary (calcite)",
            chemicalFormula = "CaCO₃",
            commonColors = listOf("White", "Cream", "Tan", "Gray"),
            whereFound = listOf("Florida, USA", "Bahamas", "Bermuda", "Western Australia", "Italy"),
            funFacts = listOf(
                "Calcarenite is a limestone made of sand-sized carbonate grains.",
                "The grains are often shell or coral fragments — making it a 'bioclastic' rock.",
                "Many tropical beaches are made of calcarenite sand.",
                "The Miami Oolite of Florida is a well-known calcarenite formation."
            ),
            uses = "Building stone, geological research, limestone source.",
            rarity = "Common"
        ),

        Specimen(
            id = "geyserite",
            name = "Geyserite",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Siliceous Sinter (Hot Spring Deposit)",
            tagline = "White silica deposits formed by hot springs and geysers — the rock of Yellowstone's terraces.",
            emoji = "⛲",
            colorHex = 0xFFE8E8E0,
            description = "Geyserite (also called siliceous sinter) is a porous, light-colored deposit of opaline silica formed by hot springs and geysers. As hot water rises to the surface, it cools and releases dissolved silica, which precipitates as a crust around the vent. Famous examples include the terraces of Yellowstone and New Zealand.",
            formation = "Forms when silica-rich hot spring water cools at the surface, causing dissolved silica to precipitate. The deposits build up layer by layer, creating terraces and mounds.",
            hardness = "5–6.5",
            luster = "Dull to vitreous",
            streak = "White",
            crystalSystem = "Amorphous (opaline silica)",
            chemicalFormula = "SiO₂·nH₂O",
            commonColors = listOf("White", "Cream", "Pink", "Yellowish", "Orange"),
            whereFound = listOf("Yellowstone National Park, USA", "Rotorua, New Zealand", "Iceland", "Turkey (Pamukkale)", "Chile"),
            funFacts = listOf(
                "Geyserite forms the famous terraces at Yellowstone National Park.",
                "The silica comes from deep geothermal fluids dissolving volcanic rock.",
                "Pamukkale in Turkey is a UNESCO World Heritage site made of geyserite terraces.",
                "Some geyserite deposits contain exceptionally preserved microfossils."
            ),
            uses = "Geological research, tourism, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "gritstone",
            name = "Gritstone",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Coarse Sandstone",
            tagline = "Rough, coarse-grained sandstone that built the mills of England's Industrial Revolution.",
            emoji = "🟫",
            colorHex = 0xFF8B7355,
            description = "Gritstone (also called grit) is a coarse-grained, hard sandstone composed of angular to sub-angular grains. It is typically Carboniferous in age and was widely used as a building material, especially for millstones in England's Industrial Revolution. Gritstone is tougher and coarser than typical sandstone.",
            formation = "Forms from the deposition and lithification of coarse sand in river or delta environments. The angular grains indicate limited transport before deposition.",
            hardness = "6–7",
            luster = "Dull",
            streak = "White to buff",
            crystalSystem = "Sedimentary (quartz grains)",
            chemicalFormula = "SiO₂ (with various cement)",
            commonColors = listOf("Brown", "Gray", "Buff", "Yellowish-brown"),
            whereFound = listOf("Peak District, England", "Pennines, England", "Yorkshire, England", "Scotland", "Wales"),
            funFacts = listOf(
                "Gritstone was used to make the millstones that powered England's Industrial Revolution.",
                "The Peak District gritstone edges are famous rock climbing locations.",
                "It is harder and coarser than typical sandstone due to angular quartz grains.",
                "Many dry stone walls in northern England are built from gritstone."
            ),
            uses = "Building stone, millstones, rock climbing surfaces, paving.",
            rarity = "Common"
        ),

        Specimen(
            id = "oolite",
            name = "Oolite",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Oolitic Limestone",
            tagline = "Limestone made of tiny spherical 'ooids' — resembling fish eggs in texture.",
            emoji = "⚪",
            colorHex = 0xFFE8E0D0,
            description = "Oolite (or oolitic limestone) is a sedimentary rock made of small, spherical, concentrically layered grains called ooids. Each ooid is typically 0.25-2 mm in diameter and forms in warm, shallow, agitated marine water where calcium carbonate precipitates in layers around a nucleus. The texture resembles fish roe (eggs).",
            formation = "Forms in warm, shallow marine environments with strong currents. Calcium carbonate precipitates in concentric layers around a nucleus (sand grain or shell fragment) as it rolls back and forth in the turbulent water.",
            hardness = "3–4",
            luster = "Dull to vitreous",
            streak = "White",
            crystalSystem = "Sedimentary (calcite ooids)",
            chemicalFormula = "CaCO₃",
            commonColors = listOf("White", "Cream", "Tan", "Gray"),
            whereFound = listOf("Great Salt Lake, Utah, USA", "Bahamas", "Indiana, USA", "Cotswolds, England", "Shark Bay, Australia"),
            funFacts = listOf(
                "The name comes from the Greek 'oon' meaning 'egg' — the small spheres resemble fish eggs.",
                "Each ooid has concentric layers like an onion, built up over time.",
                "Oolite from the Cotswolds in England has been used as a building stone for centuries.",
                "The Great Salt Lake in Utah is actively forming ooids today."
            ),
            uses = "Building stone (Cotswold stone), geological research, decorative stone.",
            rarity = "Common"
        ),

        Specimen(
            id = "sylvinite",
            name = "Sylvinite",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Evaporite (Potash Ore)",
            tagline = "Mixed potassium and sodium chloride — the world's most important potash ore.",
            emoji = "🟪",
            colorHex = 0xFFB48EC8,
            description = "Sylvinite is a naturally occurring mixture of sylvite (KCl) and halite (NaCl). It is the most important ore of potash (potassium) and is mined extensively for fertilizer production. Sylvinite forms in evaporite deposits and is typically pink to red due to trace iron impurities.",
            formation = "Forms in evaporite deposits by the evaporation of saline water in restricted basins. Sylvite precipitates after halite as the brine becomes more concentrated.",
            hardness = "2–2.5",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Isometric (halide)",
            chemicalFormula = "KCl + NaCl",
            commonColors = listOf("Pink", "Red", "White", "Orange-red"),
            whereFound = listOf("Saskatchewan, Canada", "New Mexico, USA", "Russia (Urals)", "Belarus", "Germany"),
            funFacts = listOf(
                "Sylvinite is the world's most important source of potash for fertilizer.",
                "The pink to red color comes from tiny hematite inclusions.",
                "Saskatchewan, Canada has some of the world's largest sylvinite deposits.",
                "Sylvite (the pure KCl component) tastes bitter and salty, unlike pure halite which only tastes salty."
            ),
            uses = "Potash fertilizer production, potassium source, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "turbidite",
            name = "Turbidite",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Deep Water (Turbidity Current Deposit)",
            tagline = "Sedimentary rock deposited by underwater avalanches — the record of deep-sea currents.",
            emoji = "🟫",
            colorHex = 0xFF6B6B5A,
            description = "Turbidite is a sedimentary rock deposited by turbidity currents — underwater sediment avalanches that flow down submarine slopes. Each turbidite deposit shows a characteristic sequence of grain sizes called a 'Bouma sequence', grading from coarse sand at the base to fine mud at the top.",
            formation = "Forms when sediment-laden currents flow down submarine slopes. The current slows as it reaches the flat basin floor, depositing coarse grains first and fine grains last, creating graded bedding.",
            hardness = "3–6 (varies with composition)",
            luster = "Dull",
            streak = "Variable",
            crystalSystem = "Sedimentary (clastic)",
            chemicalFormula = "Variable (sandstone to mudstone)",
            commonColors = listOf("Gray", "Tan", "Brown", "Dark gray"),
            whereFound = listOf("Apennines, Italy", "California Coast Ranges, USA", "Alps", "Scotland (Southern Uplands)", "Newfoundland, Canada"),
            funFacts = listOf(
                "Turbidites are deposited by 'turbidity currents' — underwater avalanches of sediment.",
                "Each turbidite shows a 'Bouma sequence' — a characteristic grading from coarse to fine.",
                "The Bouma sequence was first described from the Apennines in Italy.",
                "Turbidites are important reservoir rocks for oil and gas."
            ),
            uses = "Geological research, petroleum exploration, collector specimens.",
            rarity = "Common"
        ),

        Specimen(
            id = "wackestone",
            name = "Wackestone",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Carbonate Mudstone",
            tagline = "Limestone with more than 10% carbonate grains suspended in a mud matrix.",
            emoji = "⬜",
            colorHex = 0xFFD0C8B8,
            description = "Wackestone is a type of limestone in the Dunham carbonate classification system. It contains more than 10% carbonate grains (like shells or pellets) suspended in a carbonate mud matrix. The grains are not in contact with each other — they float in the mud. Wackestone represents deposition in low-energy environments.",
            formation = "Forms in low-energy marine environments where carbonate mud accumulates with scattered shells or pellets. The mud-supported texture indicates quiet water conditions.",
            hardness = "3–4",
            luster = "Dull",
            streak = "White",
            crystalSystem = "Sedimentary (calcite)",
            chemicalFormula = "CaCO₃",
            commonColors = listOf("Gray", "Tan", "White", "Brownish-gray"),
            whereFound = listOf("Worldwide (carbonate platforms)", "Madagascar", "Texas, USA", "Britain", "France"),
            funFacts = listOf(
                "Wackestone is a Dunham classification term for mud-supported limestone with grains.",
                "The name combines 'wacke' (a muddy sandstone) and 'stone'.",
                "It indicates quiet, low-energy depositional environments like lagoons.",
                "Wackestone is important in petroleum geology as both a source and reservoir rock."
            ),
            uses = "Geological research, petroleum geology, building stone, collector specimens.",
            rarity = "Common"
        ),

        // ════════════════════════════════════════════
        // METAMORPHIC (10)
        // ════════════════════════════════════════════

        Specimen(
            id = "cataclasite",
            name = "Cataclasite",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Fault Rock (Cataclastic)",
            tagline = "Rock crushed and sheared by fault movement — the product of geological violence.",
            emoji = "⚡",
            colorHex = 0xFF5A5A4A,
            description = "Cataclasite is a metamorphic rock formed by the crushing and shearing of rock along a fault zone. Unlike mylonite, which forms by ductile deformation, cataclasite forms by brittle fracturing — the rock is literally shattered and then re-cemented. Cataclasites are important records of fault activity.",
            formation = "Forms in fault zones where rocks are crushed and sheared by tectonic movement. The process is brittle — the rock fractures rather than flows. The fragments are then cemented by mineral precipitates.",
            hardness = "Variable (varies with cementation)",
            luster = "Dull",
            streak = "Variable",
            crystalSystem = "Metamorphic (cataclastic)",
            chemicalFormula = "Variable (depends on parent rock)",
            commonColors = listOf("Gray", "Dark gray", "Greenish-gray", "Brownish"),
            whereFound = listOf("San Andreas Fault, California, USA", "Alpine Fault, New Zealand", "Scotland (Moine Thrust)", "Norway", "Japan"),
            funFacts = listOf(
                "Cataclasite forms by brittle crushing — the rock is literally shattered by fault movement.",
                "It is distinguished from mylonite (which forms by ductile flow) by its angular, fractured texture.",
                "Cataclasite zones can be hundreds of meters thick along major faults.",
                "The crushed rock fragments are 'clasts' floating in a finer-grained matrix."
            ),
            uses = "Geological research, fault studies, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "jadeitite",
            name = "Jadeitite",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — High-Pressure (Jadeite Rock)",
            tagline = "Rock composed almost entirely of jadeite — the source of the world's finest jade.",
            emoji = "🟢",
            colorHex = 0xFF4A8B5C,
            description = "Jadeitite is a rare metamorphic rock composed almost entirely of jadeite, a pyroxene mineral. It is the source of 'jadeite jade' — the most valuable form of jade. Jadeitite forms under extremely high-pressure, low-temperature conditions associated with subduction zones. The finest jadeitite comes from Burma (Myanmar).",
            formation = "Forms in subduction zone environments under extremely high pressures and relatively low temperatures. The specific conditions are found at depths of 20-60 km in subduction zones.",
            hardness = "6–7",
            luster = "Vitreous to greasy",
            streak = "White",
            crystalSystem = "Monoclinic (jadeite)",
            chemicalFormula = "NaAlSi₂O₆",
            commonColors = listOf("Green", "White", "Lavender", "Yellow", "Black"),
            whereFound = listOf("Hpakant, Burma (Myanmar)", "Guatemala", "California, USA", "Japan", "Kazakhstan"),
            funFacts = listOf(
                "Jadeitite is the source of jadeite jade — the most valuable type of jade.",
                "It forms at extreme pressures (10-20 kbar) in subduction zones.",
                "The 'Hutton-M-diva' necklace (a jadeitite piece) sold for $27.4 million in 2014.",
                "Burma (Myanmar) is the primary source of the world's finest jadeitite.",
                "Jadeitite is rarer than nephrite jade, which is a different mineral entirely."
            ),
            uses = "Jade carving, jewelry, cultural artifacts, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "litchfieldite",
            name = "Litchfieldite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Alkaline Plutonic (Nepheline Syenite Variant)",
            tagline = "Rare coarse-grained alkaline rock from Maine — a nepheline syenite variety.",
            emoji = "⬜",
            colorHex = 0xFFD8D0C0,
            description = "Litchfieldite is a variety of nepheline syenite, an alkaline igneous rock. It is coarse-grained and composed of nepheline, microcline, albite, and minor mafic minerals. It is found in Litchfield, Maine, and is part of a rare alkaline igneous complex. Litchfieldite is important for studying alkaline magmatism.",
            formation = "Forms from alkaline magma in continental settings. The silica-undersaturated composition (nepheline instead of feldspar) indicates a specific mantle melting regime.",
            hardness = "5.5–6.5",
            luster = "Dull",
            streak = "White to gray",
            crystalSystem = "Igneous (multiple minerals)",
            chemicalFormula = "Nepheline + microcline + albite",
            commonColors = listOf("Light gray", "White", "Pinkish-gray"),
            whereFound = listOf("Litchfield, Maine, USA (type locality)", "Kola Peninsula, Russia", "Brazil", "Norway"),
            funFacts = listOf(
                "Named after Litchfield, Maine where it was first identified.",
                "It is a variety of nepheline syenite — a rare alkaline igneous rock.",
                "The presence of nepheline (instead of feldspar) means it is silica-undersaturated.",
                "Alkaline complexes like this are often associated with rare earth mineralization."
            ),
            uses = "Geological research, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "metapelite",
            name = "Metapelite",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Metamorphosed Mudstone/Shale",
            tagline = "Metamorphosed clay-rich sediment — produces slate, schist, and gneiss at higher grades.",
            emoji = "🟫",
            colorHex = 0xFF6B5B5A,
            description = "Metapelite is a metamorphic rock derived from the metamorphism of clay-rich sedimentary rocks (shale or mudstone). Depending on the metamorphic grade, metapelites can be slate, phyllite, schist, or gneiss. They are characterized by aluminum-rich minerals like micas, garnet, staurolite, and kyanite.",
            formation = "Forms when shale or mudstone is metamorphosed at increasing temperatures and pressures. Low-grade metamorphism produces slate; high-grade produces gneiss.",
            hardness = "Variable (3–7)",
            luster = "Variable",
            streak = "Variable",
            crystalSystem = "Metamorphic (multiple minerals)",
            chemicalFormula = "Variable (mica, garnet, etc.)",
            commonColors = listOf("Gray", "Silver", "Green", "Red", "Dark gray"),
            whereFound = listOf("Worldwide (metamorphic terrains)", "Scottish Highlands", "New England, USA", "Alps", "Himalayas"),
            funFacts = listOf(
                "Metapelite is one of the most important rock types in metamorphic geology.",
                "Different metamorphic grades of metapelite produce different minerals — a key tool for geologists.",
                "At low grade, metapelite becomes slate; at high grade, it becomes gneiss.",
                "The minerals in metapelite (like garnet, staurolite, and kyanite) are used as geothermometers."
            ),
            uses = "Geological research, metamorphic grade indicators, collector specimens.",
            rarity = "Common"
        ),

        Specimen(
            id = "serpentinite",
            name = "Serpentinite",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Hydrothermally Altered Ultramafic Rock",
            tagline = "Green, slippery metamorphic rock formed by hydration of peridotite — California's state rock.",
            emoji = "🐍",
            colorHex = 0xFF2E6B4A,
            description = "Serpentinite is a metamorphic rock formed by the hydration and alteration of ultramafic rocks (primarily peridotite). It is composed mainly of serpentine minerals and has a characteristic green color, waxy to greasy luster, and soapy feel. Serpentinite is the official state rock of California.",
            formation = "Forms when peridotite from the Earth's mantle is hydrated by water at moderate temperatures. The original olivine and pyroxene minerals are replaced by serpentine minerals.",
            hardness = "2.5–4",
            luster = "Waxy to greasy",
            streak = "White to greenish-white",
            crystalSystem = "Metamorphic (serpentine minerals)",
            chemicalFormula = "Mg₃Si₂O₅(OH)₄",
            commonColors = listOf("Green", "Black", "Dark green", "Greenish-gray"),
            whereFound = listOf("California Coast Ranges, USA", "New Caledonia", "Oman (Ophiolite)", "Cyprus", "Italy (Ligurian Alps)"),
            funFacts = listOf(
                "Serpentinite is the official state rock of California.",
                "It forms by the hydration of mantle rock — a process called 'serpentinization'.",
                "Serpentinization produces hydrogen gas, which can support deep microbial life.",
                "Serpentinite is often associated with asbestos (a serpentine mineral variety).",
                "The green color and soapy feel make serpentinite easy to identify."
            ),
            uses = "Building stone, decorative stone, geological research, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "suevite",
            name = "Suevite",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Impact Breccia (Suevite)",
            tagline = "Impact breccia with melted rock fragments — the signature rock of meteorite craters.",
            emoji = "💥",
            colorHex = 0xFF5A5A4A,
            description = "Suevite is a rock type found at meteorite impact craters, consisting of a breccia (fragmental rock) containing both shocked and melted rock fragments suspended in a fine-grained matrix. It is one of the key diagnostic rocks for confirming meteorite impact structures. The Nördlinger Ries crater in Germany is the type locality.",
            formation = "Forms during a meteorite impact when the target rock is shattered, melted, and mixed. The resulting breccia contains fragments of different rock types plus glass (melted rock) in a chaotic matrix.",
            hardness = "Variable",
            luster = "Dull",
            streak = "Variable",
            crystalSystem = "Impact breccia (composite)",
            chemicalFormula = "Variable (depends on target rock)",
            commonColors = listOf("Gray", "Greenish-gray", "Brownish", "Multicolored fragments"),
            whereFound = listOf("Ries Crater, Germany (type locality)", "Chicxulub, Mexico", "Sudbury, Ontario, Canada", "Chesapeake Bay, Virginia, USA", "Lonar Crater, India"),
            funFacts = listOf(
                "Suevite is named after the Suevian region of Germany (now Swabia).",
                "It is one of the key diagnostic rocks for confirming meteorite impact structures.",
                "The Ries Crater suevite was used as building stone for centuries before its impact origin was recognized.",
                "Suevite contains 'flädle' — glass bombs formed during the impact that flattened on landing."
            ),
            uses = "Geological research, impact crater studies, building stone (historical), collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "pseudotachylite",
            name = "Pseudotachylite",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Fault-Generated Glass (Friction Melt)",
            tagline = "Glassy rock formed by frictional melting during earthquakes or impacts.",
            emoji = "⬛",
            colorHex = 0xFF2D2D2D,
            description = "Pseudotachylite is a glassy or fine-grained rock formed by frictional melting during extreme fault movement or meteorite impacts. The friction generates enough heat to melt the rock, producing a dark, glassy material that fills fractures. Pseudotachylite is essentially 'fossilized earthquakes'.",
            formation = "Forms when extreme friction along a fault plane or during a meteorite impact generates enough heat to melt the surrounding rock. The melt injects into fractures and solidifies as a glassy or fine-grained material.",
            hardness = "Variable (5–6)",
            luster = "Vitreous to dull",
            streak = "Variable",
            crystalSystem = "Amorphous (glassy)",
            chemicalFormula = "Variable (depends on host rock)",
            commonColors = listOf("Black", "Dark brown", "Dark gray"),
            whereFound = listOf("Sudbury, Ontario, Canada", "Vredefort, South Africa", "Ries Crater, Germany", "Outer Hebrides, Scotland", "Italian Alps"),
            funFacts = listOf(
                "Pseudotachylite is essentially a 'fossilized earthquake' — it forms by frictional melting during fault slip.",
                "The name means 'false tachylite' — it resembles tachylite (basaltic glass) but has a different origin.",
                "It forms in seconds during extreme fault movement or meteorite impacts.",
                "The Vredefort and Sudbury impact structures have massive pseudotachylite bodies."
            ),
            uses = "Geological research, earthquake and impact studies, collector specimens.",
            rarity = "Rare"
        ),

        Specimen(
            id = "calcflinta",
            name = "Calcflinta",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Calcareous Hornfels (Contact Metamorphic)",
            tagline = "Hard, flinty metamorphic rock formed by contact metamorphism of limestone.",
            emoji = "⬜",
            colorHex = 0xFFB8B0A0,
            description = "Calcflinta is a hard, flinty metamorphic rock formed by the contact metamorphism of impure limestone or calcareous mudstone. It has a hornfels texture and is composed of calc-silicate minerals like diopside, wollastonite, and epidote. Calcflinta is found in contact metamorphic aureoles around igneous intrusions.",
            formation = "Forms when impure limestone is baked by an igneous intrusion. The heat causes reactions between the calcite and clay/silica impurities, producing calc-silicate minerals.",
            hardness = "5–6",
            luster = "Dull",
            streak = "White to grayish",
            crystalSystem = "Metamorphic (calc-silicate minerals)",
            chemicalFormula = "Ca-Mg-Fe silicates (diopside, wollastonite, etc.)",
            commonColors = listOf("Gray", "White", "Greenish-gray", "Brownish"),
            whereFound = listOf("Scottish Highlands", "Connemara, Ireland", "Alps", "Skarn environments worldwide", "Norway"),
            funFacts = listOf(
                "The name combines 'calc' (calcium) and 'flint' — it's a flint-like calcareous rock.",
                "It forms in the contact aureole around igneous intrusions.",
                "The flinty texture comes from the very fine-grained intergrowth of calc-silicate minerals.",
                "Calcflinta is an indicator of skarn-type mineral deposits."
            ),
            uses = "Geological research, indicator for skarn mineralization, collector specimens.",
            rarity = "Uncommon"
        ),

        Specimen(
            id = "metaconglomerate",
            name = "Metaconglomerate",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Metamorphosed Conglomerate",
            tagline = "Conglomerate that has been metamorphosed — pebbles stretched and flattened by heat and pressure.",
            emoji = "🪨",
            colorHex = 0xFF7B6B5A,
            description = "Metaconglomerate is a conglomerate that has been metamorphosed by heat and pressure. The original pebbles and cobbles are deformed — stretched, flattened, or elongated — and the matrix recrystallizes. The degree of deformation records the intensity of metamorphism. Famous examples show dramatically elongated pebbles.",
            formation = "Forms when an existing conglomerate is subjected to regional metamorphism. The pressure flattens and stretches the pebbles, and the matrix recrystallizes into metamorphic minerals.",
            hardness = "6–7",
            luster = "Dull",
            streak = "Variable",
            crystalSystem = "Metamorphic (composite)",
            chemicalFormula = "Variable (quartz, feldspar, etc.)",
            commonColors = listOf("Gray", "Greenish-gray", "Multicolored pebbles", "Pink"),
            whereFound = listOf("Swiss Alps", "Norwegian Caledonides", "Scottish Highlands", "Appalachians, USA", "New Zealand"),
            funFacts = listOf(
                "Metaconglomerate pebbles can be stretched to 10 times their original length by metamorphic deformation.",
                "The shape of the deformed pebbles records the direction of tectonic stress.",
                "The famous 'stretched pebble conglomerates' of the Swiss Alps show dramatic elongation.",
                "By measuring pebble deformation, geologists can reconstruct ancient tectonic forces."
            ),
            uses = "Geological research, tectonic studies, collector specimens.",
            rarity = "Uncommon"
        ),

        // ════════════════════════════════════════════
        // FROM FOSSILERA (1)
        // ════════════════════════════════════════════

        Specimen(
            id = "amphibolite-golden",
            name = "Amphibolite, Golden",
            rockClass = RockClass.METAMORPHIC,
            category = "Metamorphic — Golden Amphibolite Variety",
            tagline = "Golden variety of amphibolite with shimmering hornblende and feldspar.",
            emoji = "🟡",
            colorHex = 0xFFD4A017,
            description = "Golden Amphibolite is a distinctive variety of amphibolite characterized by a golden to bronze color. The golden hue comes from iron-rich hornblende and biotite combined with light-colored plagioclase feldspar. This variety is less common than typical dark green/black amphibolite and is valued as a decorative stone.",
            formation = "Forms by regional metamorphism of mafic igneous rocks (basalt or gabbro) at amphibolite facies temperatures and pressures. The golden color comes from specific mineral compositions.",
            hardness = "5–6",
            luster = "Vitreous to submetallic",
            streak = "White to grayish-green",
            crystalSystem = "Metamorphic (hornblende + plagioclase)",
            chemicalFormula = "Ca₂(Mg,Fe)₅Si₈O₂₂(OH)₂ + plagioclase",
            commonColors = listOf("Golden brown", "Bronze", "Yellowish-brown", "Amber"),
            whereFound = listOf("North Carolina, USA", "Canada (Canadian Shield)", "Scandinavia", "Brazil", "Australia"),
            funFacts = listOf(
                "Golden amphibolite is a rarer color variety of the common metamorphic rock amphibolite.",
                "The golden color comes from iron-rich hornblende and biotite.",
                "Some golden amphibolites show a chatoyant (cat's eye) shimmer.",
                "It is sometimes used as a decorative building stone."
            ),
            uses = "Decorative stone, building material, collector specimens, geological research.",
            rarity = "Uncommon"
        )
    )
}
