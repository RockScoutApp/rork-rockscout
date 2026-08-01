package com.rork.rockscout.data

/**
 * New specimens added per user request:
 * Aragonite flowers, blue aragonite, Caribbean calcite, purple aragonite,
 * and Helenite (Mount St. Helens glass).
 * Generated 2026-07-04.
 */
@Suppress("unused")
object AragoniteHeleniteSpecimens {
    val specimens: List<Specimen> = listOf(
        Specimen(
            id = "aragonite-flowers", name = "Aragonite, Flowers",
            rockClass = RockClass.MINERAL, category = "Carbonate mineral",
            tagline = "Radiating crystal blossoms that look like coral or tiny flowers.",
            emoji = "🌸", colorHex = 0xFFD4A574,
            description = "Aragonite flowers are clusters of radiating orthorhombic aragonite crystals that spread outward like coral, starbursts, or tiny blossoms. They are typically reddish-brown, peach, amber, or white, with translucent, prismatic crystals that glitter in light. The flower-like habit forms when needle-like aragonite crystals grow outward from a common center, creating a delicate mineral bouquet that is popular with collectors and in spiritual displays.",
            formation = "Forms when calcium-rich water evaporates in caves, hot springs, or sedimentary layers, leaving needle-like aragonite crystals to radiate outward from a central point. The flower habit is caused by rapid growth at many nucleation points growing at equal rates in all directions.",
            hardness = "3.5–4", luster = "Vitreous to resinous", streak = "White",
            crystalSystem = "Orthorhombic", chemicalFormula = "CaCO₃",
            commonColors = listOf("Reddish-brown", "Peach", "Amber", "White", "Pink"),
            whereFound = listOf("Morocco", "Spain", "New Mexico, USA", "Arizona, USA", "China"),
            funFacts = listOf("Aragonite flowers are often sold as 'coral aragonite' or 'aragonite star clusters.'", "The radiating crystals are the same mineral that mollusks use to build shells and pearls.", "Each flower cluster is a single generation of crystals growing outward from a central seed crystal."),
            uses = "Ornamental specimens, aquarium substrates, spiritual collectibles, lapidary display.", rarity = "Common",
        ),
        Specimen(
            id = "blue-aragonite", name = "Aragonite, Blue",
            rockClass = RockClass.MINERAL, category = "Carbonate mineral",
            tagline = "A sky-blue rarity among calcium carbonate minerals.",
            emoji = "🔵", colorHex = 0xFF87CEEB,
            description = "Blue aragonite is a rare variety of aragonite colored sky-blue to aqua by trace copper and other impurities. It often forms compact, botryoidal, or stalactitic masses with a soft, waxy luster, and is much less common than the familiar white or brown aragonite. The blue color ranges from pale baby blue to deeper turquoise, and polished specimens are popular in lapidary and spiritual collections.",
            formation = "Forms from low-temperature hydrothermal fluids or in sedimentary evaporite deposits where trace copper and other metal ions tint the aragonite blue. The botryoidal masses grow as silica-poor calcium carbonate precipitates in layers within cavities and fractures.",
            hardness = "3.5–4", luster = "Vitreous to waxy", streak = "White",
            crystalSystem = "Orthorhombic", chemicalFormula = "CaCO₃ (with Cu traces)",
            commonColors = listOf("Sky blue", "Aqua", "Baby blue", "Turquoise"),
            whereFound = listOf("China", "Argentina", "USA", "Morocco", "Mexico"),
            funFacts = listOf("Blue aragonite gets its color from trace copper and other impurities in the crystal lattice.", "It is much rarer than white or brown aragonite.", "It is often carved into palm stones, spheres, and hearts for collectors."),
            uses = "Collectibles, lapidary carvings, palm stones, ornamental stone.", rarity = "Uncommon",
        ),
        Specimen(
            id = "caribbean-calcite", name = "Calcite, Caribbean",
            rockClass = RockClass.MINERAL, category = "Carbonate mineral",
            tagline = "Tropical blue and white banded calcite with an ocean-breeze palette.",
            emoji = "🌊", colorHex = 0xFF6DD5ED,
            description = "Caribbean calcite is a trade name for a banded combination of pale blue calcite and white-to-cream aragonite, famous for its tropical ocean colors. The blue layers often show soft aqua, sky-blue, or seafoam tones alternating with white, cream, or tan aragonite bands. It is almost always sold as polished palm stones, spheres, and freeforms, and has become a popular collector's stone for its soothing beach-like appearance.",
            formation = "Forms in sedimentary deposits where calcium carbonate minerals precipitate in alternating layers. The blue calcite layers crystallize alongside white aragonite bands, creating the distinctive tropical banding. The blue color comes from mineral impurities and light scattering within the calcite structure.",
            hardness = "3", luster = "Waxy to dull (polished)", streak = "White",
            crystalSystem = "Trigonal", chemicalFormula = "CaCO₃",
            commonColors = listOf("Aqua blue", "Seafoam", "White", "Cream", "Tan"),
            whereFound = listOf("Pakistan", "India"),
            funFacts = listOf("Caribbean calcite is a trade name, not a formal mineral species.", "The blue bands are calcite, while the white and cream bands are often aragonite.", "It is popular for palm stones and spheres because the banding displays beautifully on curved surfaces."),
            uses = "Ornamental carvings, palm stones, spheres, jewelry, collector specimens.", rarity = "Uncommon",
        ),
        Specimen(
            id = "purple-aragonite", name = "Aragonite, Purple",
            rockClass = RockClass.MINERAL, category = "Carbonate mineral",
            tagline = "Lilac-to-violet prismatic crystals, most prized from Cuenca, Spain.",
            emoji = "💜", colorHex = 0xFF9B59B6,
            description = "Purple aragonite is a rare variety of aragonite known for long prismatic crystals colored delicate lilac to deep violet. The finest specimens come from Cuenca, Spain, where aragonite forms striking clusters, hexagonal prisms, and sputnik-like radiating formations. The purple color is caused by trace elements such as manganese, cobalt, or iron incorporated into the calcium carbonate lattice, and the crystals are often translucent to transparent.",
            formation = "Forms in low-temperature hydrothermal veins and cavities where manganese, cobalt, or iron impurities tint the aragonite purple. The long prismatic crystals grow slowly from mineral-rich solutions, allowing the delicate purple color to develop evenly throughout the crystal.",
            hardness = "3.5–4", luster = "Vitreous", streak = "White",
            crystalSystem = "Orthorhombic", chemicalFormula = "CaCO₃ (with Mn/Co/Fe traces)",
            commonColors = listOf("Lilac", "Deep violet", "Purple-gray", "Pale lavender"),
            whereFound = listOf("Cuenca, Spain", "Morocco", "China", "Arizona, USA"),
            funFacts = listOf("Purple aragonite from Cuenca, Spain is especially prized by collectors.", "The purple color comes from trace elements like manganese, cobalt, or iron.", "It can form in striking hexagonal or sputnik-like clusters."),
            uses = "Collector specimens, lapidary, ornamental displays, jewelry accents.", rarity = "Rare",
        ),
        Specimen(
            id = "helenite", name = "Helenite",
            rockClass = RockClass.MINERAL, category = "Man-made volcanic glass",
            tagline = "Emerald-green glass created from the ash of Mount St. Helens.",
            emoji = "🌋", colorHex = 0xFF228B22,
            description = "Helenite — also known as Mount St. Helens glass or emerald obsidianite — is a man-made glass produced by melting volcanic ash from the Mount St. Helens eruption. The ash is collected and fused with trace metals to create a brilliant, emerald-green to deep forest-green glass that is cut and polished into gemstones, cabochons, and jewelry. While it is not a natural mineral, it is one of the most recognizable volcanic souvenirs in the world and tells the story of one of the most significant eruptions in modern U.S. history.",
            formation = "Helenite is created by collecting volcanic ash from the 1980 eruption of Mount St. Helens and melting it at high temperatures with added metal oxides (especially copper and chromium) to produce a vivid green glass. The May 18, 1980 eruption began at 8:32 a.m. PDT with a 5.1 magnitude earthquake that triggered a massive landslide and lateral blast, removing about 1,300 feet from the mountain's summit and sending ash across 11 U.S. states and parts of Canada. Volcanic activity continued, and on April 10, 1981 a second explosive eruption occurred, sending an ash column to about 40,000 feet and creating a new crater. The 1981 eruption was smaller than the 1980 event but still a dramatic reminder that the volcano remained active. The glass made from this ash is sold as a wearable reminder of the mountain's power and geological story.",
            hardness = "5–5.5", luster = "Vitreous", streak = "—",
            crystalSystem = "Amorphous (glass)", chemicalFormula = "Silicate glass with trace metals",
            commonColors = listOf("Emerald green", "Deep forest green", "Blue-green"),
            whereFound = listOf("Mount St. Helens, Washington, USA (volcanic ash source)"),
            funFacts = listOf("Helenite is not a natural mineral; it is man-made glass from remelted volcanic ash.", "The 1980 eruption reduced Mount St. Helens' height by about 1,300 feet.", "A second explosive eruption occurred on April 10, 1981, continuing the volcano's activity.", "Helenite jewelry is popular in the Pacific Northwest as a souvenir of the eruption."),
            uses = "Jewelry, cabochons, faceted gemstones, souvenir glass, volcanic collectibles.", rarity = "Uncommon",
        ),
    )
}
