package com.rork.rockscout.data

/**
 * Opalite specimen (man-made opal glass).
 * Added per user request with AI-generated image based on web reference photos.
 * Generated 2026-07-04.
 */
@Suppress("unused")
object OpaliteSpecimen {
    val specimens: List<Specimen> = listOf(
        Specimen(
            id = "opalite",
            name = "Opalite",
            rockClass = RockClass.MINERAL,
            category = "Man-made glass / opal simulant",
            tagline = "A milky, man-made glass with a soft blue glow and opalescent sheen.",
            emoji = "🔮",
            colorHex = 0xFFD6EAF8,
            description = "Opalite is a man-made opal-like glass, also known as sea opal glass or opalite glass. It is created by melting silica glass with added metal oxides to produce a milky, translucent material that shows a soft blue or lavender glow when held to light. While it is often sold in crystal shops as a gemstone, it is not a natural mineral or opal. It is popular for tumbled stones, spheres, carvings, and jewelry because of its gentle, dreamy appearance and affordable price.",
            formation = "Opalite is manufactured by melting silica-based glass with small amounts of metal oxides (such as fluorine or phosphorus) that create a milky, opalescent effect. The glass is cooled and then cut, polished, or tumbled into smooth shapes. Unlike natural opal, it does not contain water, has no play-of-color from stacked silica spheres, and forms in industrial furnaces rather than in the Earth.",
            hardness = "5–6",
            luster = "Vitreous to waxy",
            streak = "White",
            crystalSystem = "Amorphous (glass)",
            chemicalFormula = "SiO₂ glass with opacifying metal oxides",
            commonColors = listOf("Milky white", "Pale blue", "Lavender", "Opalescent"),
            whereFound = listOf("Manufactured worldwide"),
            funFacts = listOf(
                "Opalite is man-made glass, not a natural mineral or true opal.",
                "It is sometimes called 'sea opal' or 'opalite glass' in jewelry shops.",
                "The blue glow comes from light scattering in the glass, not from opal's play-of-color.",
                "It is much harder and less water-sensitive than natural opal, making it durable for everyday jewelry."
            ),
            uses = "Jewelry, tumbled stones, spheres, carvings, beads, and ornamental collectibles.",
            rarity = "Common",
        ),
    )
}
