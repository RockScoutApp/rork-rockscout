package com.rork.rockscout.data

/**
 * Kimberlite specimen — the volcanic rock that is the primary source of natural diamonds.
 * Added per user request. Images already generated in Batch 33 (kimberlite rough/wild/museum).
 */
@Suppress("unused")
object KimberliteSpecimen {
    val specimens: List<Specimen> = listOf(
        Specimen(
            id = "kimberlite",
            name = "Kimberlite",
            rockClass = RockClass.IGNEOUS,
            category = "Igneous — Volcanic (ultramafic)",
            tagline = "The rare volcanic rock that brings diamonds up from deep in the Earth.",
            emoji = "💎",
            colorHex = 0xFF3A3A3A,
            description = "Kimberlite is a rare, dark, potassic ultramafic volcanic rock that erupts from deep pipes in the Earth's mantle. It is famous as the primary host rock for natural diamonds: diamond crystals form under extreme pressure 150–200 km below the surface, then kimberlite magma rips them loose and blasts them upward in explosive volcanic eruptions. Kimberlite pipes are vertical, carrot-shaped columns of fragmented rock that can be only a few hundred meters across. Most kimberlites are actually diamond-barren; only a small fraction contain diamonds in mineable quantities. The rock itself is usually dark gray to blue-gray, with a rough texture from minerals such as olivine, pyrope garnet, ilmenite, and phlogopite mica.",
            formation = "Kimberlite forms from volatile-rich magma generated deep in the mantle. The magma rises rapidly through narrow fractures, picking up fragments of mantle and crustal rock along the way. Because it ascends so fast — sometimes kilometers per day — the magma does not have time to fully crystallize or react with surrounding rocks. When it nears the surface, the sudden release of carbon dioxide and water vapor can create explosive diatreme eruptions. The resulting pipe contains a jumble of fragmented kimberlite, mantle xenoliths, and, occasionally, diamonds. Not all kimberlite eruptions bring diamonds to the surface, but every diamond mine relies on finding these rare pipes.",
            hardness = "6–7",
            luster = "Dull to earthy; some crystals may be vitreous",
            streak = "White to pale green",
            crystalSystem = "N/A (porphyritic volcanic rock)",
            chemicalFormula = "Variable: olivine, pyrope, diopside, phlogopite, calcite, serpentine",
            commonColors = listOf("Dark gray", "Blue-gray", "Black", "Greenish-black", "Brownish"),
            whereFound = listOf("South Africa", "Botswana", "Russia (Yakutia)", "Canada", "Australia", "Brazil", "Arkansas, USA"),
            funFacts = listOf(
                "Kimberlite is named after Kimberley, South Africa, where the first diamond mine was discovered in 1871.",
                "Diamonds are only a tiny accessory mineral in kimberlite; most kimberlite pipes contain no diamonds at all.",
                "Kimberlite eruptions are among the deepest-sourced volcanic eruptions on Earth.",
                "Some kimberlites contain greenish olivine crystals and red pyrope garnets that are direct clues to mantle chemistry.",
                "The famous Crater of Diamonds State Park in Arkansas is a weathered kimberlite pipe open to the public for digging."
            ),
            uses = "Diamond mining, scientific research into the deep mantle, geological indicator of ancient continental structure.",
            rarity = "Rare",
        ),
    )
}
