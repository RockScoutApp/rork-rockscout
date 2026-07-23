package com.rork.rockscout.data

/**
 * Alabaster specimen — fine-grained, massive, translucent variety of gypsum.
 * Added per user request with AI-generated images based on Wikipedia/mindat reference photos.
 * References:
 *   https://en.wikipedia.org/wiki/Alabaster
 *   https://www.mindat.org/min-1797.html (gypsum variety)
 * Generated 2026-07-23.
 */
@Suppress("unused")
object AlabasterSpecimen {
    val specimens: List<Specimen> = listOf(
        Specimen(
            id = "alabaster",
            name = "Alabaster",
            rockClass = RockClass.MINERAL,
            category = "Sulfate mineral — Gypsum variety",
            tagline = "A fine-grained, translucent gypsum prized for carving since antiquity.",
            emoji = "🗿",
            colorHex = 0xFFF5EFE0,
            description = "Alabaster is a fine-grained, massive, translucent variety of gypsum (CaSO₄·2H₂O) valued for carving and sculpture since ancient times. Unlike the crystalline gypsum varieties — selenite, satin spar, and desert rose — alabaster has no visible crystals; instead it is a compact, microcrystalline stone with a smooth, waxy texture and a soft ivory-to-white glow when light passes through it. It is soft enough to carve with a knife and has been used for canopic jars, sarcophagi, cathedral windows, lamps, and ornamental objects for thousands of years. Note: the term 'alabaster' is also historically applied to a banded calcite (calcite alabaster or 'Egyptian alabaster'), but true mineralogical alabaster is the gypsum variety.",
            formation = "Gypsum alabaster forms as a compact, fine-grained evaporite deposit. When inland seas or saline lakes evaporate, dissolved calcium sulfate precipitates as gypsum. Under the right conditions — slow crystallization in a stable, water-saturated environment — the gypsum grows as interlocking microcrystals rather than large transparent crystals, producing the massive, translucent stone we call alabaster. It typically occurs in sedimentary beds alongside other evaporite minerals like halite and anhydrite, and can also form in cave and spring deposits.",
            hardness = "1.5–2",
            luster = "Waxy to silky, pearly on cleavage surfaces",
            streak = "White",
            crystalSystem = "Monoclinic (massive habit — no visible crystals)",
            chemicalFormula = "CaSO₄·2H₂O",
            commonColors = listOf("White", "Ivory", "Cream", "Pale honey", "Banded brown-veined"),
            whereFound = listOf(
                "Volterra, Italy — historic carving quarries since Etruscan times",
                "Cuenca and Aragon, Spain — major European alabaster source",
                "Tuscany, Italy — medieval and Renaissance sculpture material",
                "England (Derbyshire, Nottinghamshire) — 'alabaster of Derby'",
                "Egypt — ancient calcite alabaster quarries near Wadi Gerawi",
                "Iran and Iraq — 'Mosul marble' gypsum alabaster used in Assyrian reliefs",
                "USA (Michigan, Ohio, Indiana) — small deposits in evaporite beds",
                "Mexico (Sonora) — alabaster used for carved decorative objects",
            ),
            funFacts = listOf(
                "Alabaster is so soft (Mohs 1.5–2) that you can scratch it with your fingernail.",
                "Ancient Egyptians carved canopic jars and sarcophagi from alabaster, including Tutankhamun's cosmetic jar.",
                "Medieval European churches used thin alabaster sheets as translucent 'windows' before glass was affordable.",
                "True alabaster is a gypsum variety — but 'calcite alabaster' (a banded travertine) was the stone used in ancient Egypt; the two are chemically different.",
                "Alabaster dissolves slowly in water and is heat-sensitive, so carved objects should be kept dry and away from direct sunlight.",
                "Volterra in Tuscany has been quarrying and carving alabaster for over 2,000 years and is still a center for alabaster art today.",
                "When heated, alabaster loses its water and turns into plaster of Paris — the same material used for casts and molds.",
            ),
            uses = "Carving, sculpture, ornamental objects, lamps, candle holders, vases, architectural panels, translucent window panels, and as a source of plaster of Paris when calcined.",
            rarity = "Common",
        ),
    )
}
