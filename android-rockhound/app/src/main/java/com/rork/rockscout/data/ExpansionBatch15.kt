package com.rork.rockscout.data

/**
 * Database expansion wave 15 — concretions and unique formations.
 * Hagstones, fairy stone concretions, iron concretions, septarian nodules,
 * Michigan lightning stones, and fossilized mud concretions.
 * Generated 2026-07-17.
 */
@Suppress("unused")
object ExpansionBatch15 {
    val specimens: List<Specimen> = listOf(
        Specimen(
            id = "hagstone",
            name = "Hagstone",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Naturally Holed Stone (water-eroded)",
            tagline = "A naturally holed stone formed by water erosion — legendary for seeing fairies and spirits.",
            emoji = "\uD83D\uDD73\uFE0F",
            colorHex = 0xFF4A4A4A,
            description = "A hagstone is a stone with a natural hole worn completely through it by water, sand, and grit over many years. The hole is not drilled — it is carved by rivers, waves, or rain. The host rock can be flint, limestone, sandstone, or any durable stone. Folklore says looking through the hole reveals the fairy world; in geology, it is a beautiful example of selective erosion.",
            formation = "Water carrying abrasive sand and sediment repeatedly flows over and through a small weakness or cavity in a stone. Over centuries, the constant movement enlarges the cavity into a clean, smooth hole.",
            hardness = "6–7 (varies with host rock)",
            luster = "Dull to waxy",
            streak = "White to gray",
            crystalSystem = "N/A (rock with eroded hole)",
            chemicalFormula = "Varies: SiO₂ (flint), CaCO₃ (limestone), or clastic rock",
            commonColors = listOf("Dark gray", "Black", "Tan", "Brown"),
            whereFound = listOf("Beaches and riverbeds worldwide", "England (folk tradition)", "Ireland", "Michigan, USA"),
            funFacts = listOf(
                "Folklore says hagstones protect against evil spirits and curses.",
                "Some traditions tie them to ships or fishing nets for good luck.",
                "The hole must be natural, not drilled, to count as a true hagstone."
            ),
            uses = "Collector specimens, spiritual talismans, garden decorations, jewelry.",
            rarity = "Uncommon"
        ),
        Specimen(
            id = "fairy-stone-concretions",
            name = "Concretion, Fairy Stone",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Concretion (mud/clay disc)",
            tagline = "Smooth, disc-shaped mud concretions that weather out of glacial clays like ancient cookies.",
            emoji = "\uD83C\uDF0B",
            colorHex = 0xFFD6CFC0,
            description = "Fairy stone concretions are light-colored, flat to disc-shaped concretions that form in fine mud or clay. They are not the brown staurolite 'fairy crosses' found in the Appalachians — these are sedimentary nodules, often with concentric rings and a smooth matte surface. They are most commonly found in glacial lake clays and varved sediments.",
            formation = "Mineral cement (usually calcite or iron oxide) precipitates around a nucleus in soft, fine-grained sediment. The flat shape reflects compaction while the concretions are still within the mud.",
            hardness = "3–5",
            luster = "Dull to earthy",
            streak = "White to pale gray",
            crystalSystem = "N/A (sedimentary concretion)",
            chemicalFormula = "CaCO₃ or Fe-oxide cemented clay/silt",
            commonColors = listOf("Pale gray", "Buff", "Tan", "Light brown"),
            whereFound = listOf("Great Lakes region (glacial lake clays)", "Canada (Quebec and Ontario)", "Scandinavia", "Northern Europe"),
            funFacts = listOf(
                "They are sometimes called 'fairy biscuits' or 'glacial concretions'.",
                "The flat shape comes from compression in the clay before the sediment hardens.",
                "They are distinct from the brown staurolite 'fairy crosses' of the U.S. Southeast."
            ),
            uses = "Collector specimens, educational examples of concretion formation.",
            rarity = "Uncommon"
        ),
        Specimen(
            id = "iron-concretions",
            name = "Iron Concretions",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Concretion (iron-rich)",
            tagline = "Rounded iron-cemented nodules — from siderite and hematite to the famous Moqui marbles.",
            emoji = "\u26AB",
            colorHex = 0xFF5A2D1F,
            description = "Iron concretions are rounded, lumpy nodules formed when iron-rich minerals precipitate around a nucleus in sedimentary rock. They can be made of siderite, hematite, goethite, or a mix of iron oxides and clays. Famous examples include the Moqui marbles of Utah and Arizona, cannonball concretions of the Great Plains, and heavy clay-ironstone nodules in coal measures.",
            formation = "Iron-rich groundwater flows through porous sediment and precipitates iron minerals around a nucleus such as a fossil, shell fragment, or sand grain. The concretion grows layer by layer as more iron is deposited.",
            hardness = "5–7",
            luster = "Dull to submetallic",
            streak = "Reddish-brown to brown",
            crystalSystem = "N/A (sedimentary concretion)",
            chemicalFormula = "FeCO₃ (siderite), Fe₂O₃ (hematite), FeO(OH) (goethite)",
            commonColors = listOf("Dark brown", "Reddish-brown", "Black", "Yellow-brown"),
            whereFound = listOf("Utah and Arizona (Moqui marbles)", "North Dakota (cannonball concretions)", "Kansas", "Michigan, USA"),
            funFacts = listOf(
                "Moqui marbles are iron concretions found in Navajo Sandstone.",
                "Cannonball concretions can be as big as bowling balls.",
                "Iron concretions are often mistaken for meteorites ('meteor-wrongs')."
            ),
            uses = "Collector specimens, iron ore, geological education, lapidary.",
            rarity = "Common to Uncommon"
        ),
        Specimen(
            id = "septarian-nodule",
            name = "Septarian Nodule",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Concretion (cracked mud-ball geode)",
            tagline = "Cracked mud-ball concretions filled with bright calcite and aragonite veins.",
            emoji = "\uD83C\uDF00",
            colorHex = 0xFF8B5A2B,
            description = "Septarian nodules are spherical to oval concretions that form in mud and then crack internally as they dry and shrink. Mineral-rich groundwater later fills the cracks with yellow calcite, brown aragonite, white barite, or other minerals, creating dramatic vein patterns when the nodules are cut open. They are often called 'lightning stones' because of the radiating crack-fill pattern.",
            formation = "A mud concretion dries and shrinks, creating internal cracks. Later, groundwater carrying dissolved calcium, carbon, and other minerals precipitates crystals in the cracks, filling them with calcite, aragonite, or barite.",
            hardness = "3–7 (varies by mineral fill)",
            luster = "Dull exterior, vitreous crystal interior",
            streak = "White to pale yellow",
            crystalSystem = "N/A (concretion with crystal fills)",
            chemicalFormula = "CaCO₃ (calcite/aragonite), BaSO₄ (barite), clay matrix",
            commonColors = listOf("Dark brown exterior", "Yellow calcite", "Brown aragonite", "White barite"),
            whereFound = listOf("Utah, USA", "Kansas, USA", "Michigan, USA", "England", "Madagascar"),
            funFacts = listOf(
                "The name comes from the Latin 'septum' meaning partition, because of the internal walls.",
                "When cut and polished, they reveal one of the most beautiful patterns in geology.",
                "The yellow calcite veins are often fluorescent under UV light."
            ),
            uses = "Collector specimens, lapidary slices, home decor, metaphysical.",
            rarity = "Common to Uncommon"
        ),
        Specimen(
            id = "michigan-lightning-stone",
            name = "Michigan Lightning Stone",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Concretion (regional septarian)",
            tagline = "Lake Michigan septarian nodules whose cut interiors flash like frozen lightning bolts.",
            emoji = "\u26A1",
            colorHex = 0xFF5C3A1E,
            description = "Michigan lightning stones are regional septarian nodules found along the shores of Lake Michigan and nearby glacial deposits. They have a dark brown, water-worn exterior and a distinctive interior of bright calcite veins that look like lightning bolts frozen in stone. They are technically the same type of formation as septarian nodules, but their Lake Michigan origin gives them a unique beach-cobble character.",
            formation = "Formed in the same way as other septarian nodules: a mud concretion dries and cracks internally, then groundwater fills the cracks with calcite. Glacial action and lake waves later rounded and polished the exterior of Michigan specimens.",
            hardness = "3–7 (varies by mineral fill)",
            luster = "Dull exterior, vitreous crystal interior",
            streak = "White to pale yellow",
            crystalSystem = "N/A (concretion with crystal fills)",
            chemicalFormula = "CaCO₃ (calcite), clay matrix",
            commonColors = listOf("Dark brown exterior", "White calcite", "Yellow calcite", "Gray-brown matrix"),
            whereFound = listOf("Lake Michigan, USA (South Haven area)", "Western Michigan beaches", "Michigan glacial deposits"),
            funFacts = listOf(
                "The 'lightning' pattern is the calcite-filled crack network inside a septarian nodule.",
                "They are most commonly found along southwest Michigan beaches after storms.",
                "Some collectors call them 'Michigan lightning stones' to distinguish them from Utah septarians."
            ),
            uses = "Collector specimens, lapidary slices, beachcombing prizes, metaphysical.",
            rarity = "Uncommon"
        ),
        Specimen(
            id = "fossilized-mud-concretions",
            name = "Concretion, Fossilized Mud",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Concretion (fossiliferous mud nodule)",
            tagline = "Ancient mudstone nodules that preserve hidden fossils, cracks, and mineral secrets inside.",
            emoji = "\uD83D\uDC8E",
            colorHex = 0xFF6B655E,
            description = "Fossilized mud concretions are rounded nodules of ancient mudstone that have been cemented and hardened over time. They often contain internal cracks, fossil fragments, and mineral fills that are only visible when the nodule is cut or broken open. They are related to septarian nodules but typically lack the dramatic calcite 'lightning' veins, instead preserving softer textures and fossil evidence.",
            formation = "Fine mud and silt accumulates around a nucleus such as a shell, bone, or plant fragment. As the sediment lithifies into mudstone, mineral cement hardens the nodule and can preserve fossils inside.",
            hardness = "4–6",
            luster = "Dull to earthy",
            streak = "Gray to pale brown",
            crystalSystem = "N/A (sedimentary rock)",
            chemicalFormula = "Clay minerals, calcite, fossil fragments",
            commonColors = listOf("Gray-brown", "Dull tan", "Greenish-gray"),
            whereFound = listOf("Mazon Creek, Illinois, USA", "North Dakota", "Kansas", "Michigan, USA"),
            funFacts = listOf(
                "Mazon Creek fossils are often preserved inside ironstone concretions.",
                "Cracking open a mud concretion can reveal a fossil that has never been seen before.",
                "They are a favorite target for fossil collectors because they protect delicate fossils."
            ),
            uses = "Collector specimens, fossil hunting, geological education.",
            rarity = "Uncommon"
        ),
        Specimen(
            id = "concretion-pyrite",
            name = "Concretion, Pyrite",
            rockClass = RockClass.SEDIMENTARY,
            category = "Sedimentary — Concretion (pyrite-rich)",
            tagline = "Spherical ironstone concretions laced with glittering golden pyrite bands.",
            emoji = "\u26AA",
            colorHex = 0xFF4A4A3A,
            description = "Pyrite concretions are rounded to spherical nodules of dark ironstone or shale matrix that are laced, banded, or dusted with golden brassy pyrite. The pyrite can form concentric rings, crystalline crusts, or scattered metallic flecks across the concretion's surface. They form when iron and sulfur-rich groundwater precipitates pyrite around a nucleus in soft sediment, then the surrounding rock weathers away to expose the harder nodule.",
            formation = "Iron- and sulfur-rich groundwater precipitates pyrite (FeS₂) around a nucleus in soft sediment. The concretion grows layer by layer as more pyrite and iron-rich cement are deposited, often forming concentric bands or crystalline surfaces.",
            hardness = "5–6.5",
            luster = "Dull to metallic",
            streak = "Greenish-black to brown",
            crystalSystem = "N/A (sedimentary concretion with cubic pyrite)",
            chemicalFormula = "FeS₂ (pyrite) in clay/ironstone matrix",
            commonColors = listOf("Dark gray", "Black", "Gold-banded", "Brassy metallic"),
            whereFound = listOf("Spain", "Germany", "Russia", "Colorado, USA", "Pennsylvania, USA"),
            funFacts = listOf(
                "Pyrite concretions can look like fossil eggs or metallic cannonballs at first glance.",
                "Cutting or cracking one open sometimes reveals a perfectly preserved fossil at the center.",
                "The concentric bands are created by pulses of iron-rich groundwater as the nodule grows."
            ),
            uses = "Collector specimens, lapidary, geological education, metaphysical.",
            rarity = "Uncommon"
        ),
    )
}
