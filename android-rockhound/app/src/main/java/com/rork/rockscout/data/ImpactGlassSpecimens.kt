package com.rork.rockscout.data

/**
 * Tektites and impact glasses — natural glasses formed by meteorite impacts.
 * Added 2026-07-02. Merged with SeedData.specimens at runtime via specimenById().
 */
@Suppress("unused")
object ImpactGlassSpecimens {
    val specimens: List<Specimen> = listOf(
        Specimen(
            id = "libyan-desert-glass",
            name = "Libyan Desert Glass",
            rockClass = RockClass.MINERAL,
            category = "Natural glass — Impactite",
            tagline = "Golden-yellow silica glass scattered across the Sahara by an ancient meteorite impact.",
            emoji = "\uD83C\uDF1E",
            colorHex = 0xFFE8C860,
            description = "Libyan Desert Glass is a pale yellow to golden natural silica glass found scattered across the Great Sand Sea of the Libyan Desert. Formed by a meteorite impact or airburst roughly 28 million years ago, it was heated to such extreme temperatures that desert sand fused into translucent yellow glass. It has been used in jewelry since prehistoric times — a carved scarab of Libyan Desert Glass was found in King Tutankhamun's tomb.",
            formation = "Approximately 28 million years ago, a meteorite impact or high-altitude airburst over the Sahara Desert generated temperatures exceeding 1,800\u00B0C, instantly melting the quartz-rich desert sand into a pure yellow silica glass. The glass cooled rapidly and was scattered across a vast area of the Great Sand Sea. Wind erosion over millions of years has given specimens a frosted, etched surface.",
            hardness = "6–7",
            luster = "Vitreous (interior), frosted (exterior)",
            streak = "White",
            crystalSystem = "Amorphous (glass)",
            chemicalFormula = "SiO\u2082 (~98% pure silica glass)",
            commonColors = listOf("Golden yellow", "Pale lemon", "Honey amber", "Milky white (rare)"),
            whereFound = listOf("Great Sand Sea, Western Desert, Egypt/Libya border", "Libyan Desert, Egypt"),
            funFacts = listOf("A beautiful carved scarab of Libyan Desert Glass was found in King Tutankhamun\u2019s tomb — the ancient Egyptians treasured it.", "It is 98% pure silica — purer than most manufactured glass.", "Scientists still debate whether it formed from a ground impact or an airburst explosion."),
            uses = "Jewelry, collector specimens, scientific study of impact processes.",
            rarity = "Rare"
        ),
        Specimen(
            id = "moldavite",
            name = "Tektite, Moldavite",
            rockClass = RockClass.MINERAL,
            category = "Natural glass — Tektite",
            tagline = "Olive-green glass forged by a meteorite impact 15 million years ago.",
            emoji = "\uD83D\uDFE2",
            colorHex = 0xFF4A6B3A,
            description = "Moldavite is a forest-green to olive-green natural glass formed when a massive meteorite struck southern Germany 15 million years ago, melting the local rock and blasting molten glass across central Europe. Its deeply etched, sculpted surface texture is unmistakable — no two pieces are alike. Moldavite is the only tektite used as a gemstone. Rarity: Rare (finite resource).",
            formation = "About 15 million years ago, a meteorite struck the Ries crater in Bavaria, Germany. The impact was so violent that it melted the local sandstone, blasting molten silica glass hundreds of kilometers across what is now the Czech Republic, Germany, and Austria. The glass cooled while flying through the air, acquiring its characteristic deeply etched, sculpted form.",
            hardness = "5.5–6",
            luster = "Vitreous",
            streak = "White",
            crystalSystem = "Amorphous (glass)",
            chemicalFormula = "SiO\u2082 + Al\u2082O\u2083 + other oxides (natural glass)",
            commonColors = listOf("Olive green", "Forest green", "Brownish-green", "Pale green"),
            whereFound = listOf("Czech Republic (Bohemia & Moravia)", "Southern Germany", "Austria"),
            funFacts = listOf("Moldavite is the only tektite used as a gemstone — highly prized in jewelry.", "The Ries Crater in Germany is the impact site — one of the best-preserved impact craters on Earth.", "Some pieces still contain tiny bubbles of 15-million-year-old atmosphere trapped inside."),
            uses = "Gemstone, jewelry, metaphysical/spiritual collector specimens.",
            rarity = "Rare (finite resource)"
        ),
        Specimen(
            id = "trinitite",
            name = "Trinitite",
            rockClass = RockClass.MINERAL,
            category = "Natural glass — Anthropogenic impactite",
            tagline = "Pale green glass created by the first atomic bomb test at Trinity Site, New Mexico.",
            emoji = "\u2622\uFE0F",
            colorHex = 0xFF8FA88C,
            description = "Trinitite is a rare, pale greenish-gray glass formed when the world's first nuclear device was detonated on July 16, 1945, near Alamogordo, New Mexico. The intense heat of the Trinity explosion — estimated at over 8,000°C — instantly melted the desert sand and surface minerals into a thin, frothy, radioactive glass. Most surface trinitite was removed and buried in 1953, making intact specimens historically significant and scientifically unique.",
            formation = "The Gadget plutonium implosion device released enormous heat and a shockwave that melted quartz-rich sand, feldspar, and calcite at the Trinity Site. The molten material was thrown into the air, cooled rapidly, and rained down as a thin greenish crust with a frothy, aerated texture. Trinitite contains fused sand, trace iron from the tower, and radionuclides from the blast.",
            hardness = "5–6",
            luster = "Vitreous to dull (frosted exterior)",
            streak = "White",
            crystalSystem = "Amorphous (glass)",
            chemicalFormula = "SiO\u2082 (fused desert sand) with trace Al, Fe, Ca, and radionuclides",
            commonColors = listOf("Pale green", "Grayish-green", "Light olive", "Frothy gray"),
            whereFound = listOf("Trinity Site, White Sands Missile Range, New Mexico, USA"),
            funFacts = listOf("Trinitite is the only glass on Earth created by a human-made atomic explosion.", "The original surface layer was mostly bulldozed and buried in 1953; remaining pieces are protected historical artifacts.", "Trinitite is still slightly radioactive, but generally safe to handle in small specimens for short periods."),
            uses = "Historical and scientific collector specimen, atomic age museum piece, educational material.",
            rarity = "Rare"
        ),
    )
}
