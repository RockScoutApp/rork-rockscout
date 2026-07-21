package com.rork.rockscout.data

/**
 * Additional specimens — fossil soup, natural pearls, grape agate, fordite,
 * and expanded petrified wood varieties. Added 2026-07-01.
 * Merged with SeedData.specimens at runtime via specimenById().
 */
@Suppress("unused")
object AdditionalSpecimens {
    val specimens: List<Specimen> = listOf(
        Specimen(
            id = "grape-agate", name = "Chalcedony, Grape Agate",
            rockClass = RockClass.MINERAL, category = "Silicate — Chalcedony variety",
            tagline = "A cluster of tiny purple spheres — nature's grape bunch in stone.",
            emoji = "\uD83C\uDF47", colorHex = 0xFF8B6BAA,
            description = "Grape agate is a marketing name for botryoidal purple chalcedony from Indonesia. Tiny spheres of amethyst-colored quartz cluster together like a bunch of miniature grapes, with sparkling druzy surfaces. Despite the name, it's technically not an agate — it's a form of chalcedony quartz with a unique spherical growth habit.",
            formation = "Forms in volcanic rock cavities where silica-rich solutions deposit layer after layer on tiny nuclei, creating the spherical botryoidal habit. Trace iron and manganese create the purple color.",
            hardness = "6.5–7", luster = "Vitreous to druzy", streak = "White",
            crystalSystem = "Trigonal (microcrystalline)",
            chemicalFormula = "SiO₂ (amethyst-colored chalcedony)",
            commonColors = listOf("Purple", "Lavender", "Violet", "Green (rare)"),
            whereFound = listOf("Mamuju, Sulawesi, Indonesia"),
            funFacts = listOf("Discovered around 2016 and immediately became a collector sensation.", "Despite the name, it's not actually an agate — it's chalcedony.", "The grape-like spheres range from pea-size to marble-size."),
            uses = "Collector specimens, mineral displays, lapidary.",
            rarity = "Uncommon"
        ),
        Specimen(
            id = "fordite", name = "Fordite",
            rockClass = RockClass.MINERAL, category = "Anthropogenic — Layered automotive paint",
            tagline = "Born in a Detroit auto factory — the accidental gemstone.",
            emoji = "\uD83D\uDE97", colorHex = 0xFFCC3344,
            description = "Fordite is a man-made 'gemstone' composed of hundreds of layers of hardened automotive enamel paint that built up on the skids and racks of car factories over decades. When cut and polished, the swirling layers of red, blue, green, yellow, white, and metallic paint create mesmerizing patterns that look like natural agate — hence the nickname 'Detroit Agate.' No new fordite is being made since modern factories use electrostatic painting that doesn't create buildup.",
            formation = "Pre-1980s automotive factories used hand-spraying techniques. Overspray paint accumulated on metal racks and skids, baking into hard enamel layers with each pass through the curing ovens. Workers eventually chipped off this material and lapidary artists discovered its beauty.",
            hardness = "4–6 (varies with paint type)", luster = "Vitreous (polished)", streak = "Varies",
            crystalSystem = "N/A (layered polymer)", chemicalFormula = "Acrylic and enamel polymers",
            commonColors = listOf("Red", "Blue", "Green", "Yellow", "White", "Metallic silver", "Rainbow layers"),
            whereFound = listOf("Detroit, Michigan, USA (original)", "Other automotive factory cities"),
            funFacts = listOf("No new Fordite is being made — modern painting methods don't create buildup.", "Some pieces show specific color layers that can be dated to exact car models and years.", "It's the only 'gem' made entirely by industrial accident."),
            uses = "Jewelry, cabochons, lapidary art, collector specimens.",
            rarity = "Finite — no new material"
        ),
        Specimen(
            id = "natural-pearls", name = "Pearl, Freshwater",
            rockClass = RockClass.MINERAL, category = "Biogenic mineral — Calcium carbonate",
            tagline = "The only gems grown by living creatures — natural pearls.",
            emoji = "\uD83E\uDEAA", colorHex = 0xFFF0E6D0,
            description = "Natural pearls are organic gems formed inside mollusks when an irritant becomes coated with layers of nacre (aragonite). Unlike cultured pearls, natural pearls form without human intervention and are extremely rare. Each pearl has a unique luster, shape, and color — from classic white to pink, lavender, gold, and silver.",
            formation = "Forms inside freshwater mussels and saltwater oysters when the mollusk secretes nacre around an intruding particle. Layers of aragonite and conchiolin build up over years, creating the pearl's iridescence.",
            hardness = "2.5–4.5", luster = "Pearly", streak = "White",
            crystalSystem = "Amorphous (aragonite microcrystals)",
            chemicalFormula = "CaCO₃ (aragonite) + organic conchiolin",
            commonColors = listOf("White", "Cream", "Pink", "Lavender", "Golden", "Silver"),
            whereFound = listOf("Tennessee River, USA (freshwater)", "Persian Gulf (saltwater)", "Mississippi River, USA", "China (freshwater)"),
            funFacts = listOf("Natural pearls are rarer than diamonds — only about 1 in 10,000 wild oysters contains a pearl.", "The oldest known pearl jewelry is 7,500 years old, found in the UAE.", "Freshwater mussels can produce up to 50 pearls at once."),
            uses = "Jewelry, the original 'organic gem.'",
            rarity = "Rare (natural), Common (cultured)"
        ),
        Specimen(
            id = "petrified-wood-black", name = "Petrified Wood, Black",
            rockClass = RockClass.FOSSIL, category = "Fossil — Wood replacement",
            tagline = "Elegant dark chalcedony preserving ancient forests.",
            emoji = "\u2B1B", colorHex = 0xFF333333,
            description = "Black petrified wood gets its dark color from carbon or manganese that was present during the silicification process. The result is striking — deep black to dark gray chalcedony with subtle but visible wood grain that catches the light beautifully when polished. It's found in several worldwide localities.",
            formation = "Wood buried in environments rich in organic carbon or manganese. During petrification, these dark minerals were incorporated into the silica, creating black chalcedony.",
            hardness = "7", luster = "Vitreous", streak = "—",
            crystalSystem = "N/A (chalcedony replacement)", chemicalFormula = "SiO₂ (with C, Mn)",
            commonColors = listOf("Black", "Dark gray", "Charcoal"),
            whereFound = listOf("Arizona, USA", "Oregon, USA", "Nevada, USA", "Indonesia"),
            funFacts = listOf("The black color comes from microscopic carbon particles or manganese.", "Black petrified wood is popular for jewelry because it's visually striking.", "Some specimens show metallic-looking wood grain when polished."),
            uses = "Jewelry, cabochons, decorative specimens.",
            rarity = "Uncommon"
        ),
        Specimen(
            id = "petrified-wood-blue-forest", name = "Petrified Wood, Blue Forest",
            rockClass = RockClass.FOSSIL, category = "Fossil — Conifer wood replacement",
            tagline = "Teal-blue chalcedony preserving ancient Wyoming forest.",
            emoji = "\uD83C\uDF32", colorHex = 0xFF5B8FA8,
            description = "Blue Forest petrified wood from Wyoming is famous for its stunning blue-gray to teal coloration, caused by trace amounts of chromium and copper during the silicification process. The blue chalcedony perfectly preserves the wood grain and tree rings of 50-million-year-old Eocene trees. Specimens often have a natural dark bark rind.",
            formation = "Eocene trees were buried by volcanic ash in the Green River Formation. Silica-rich groundwater replaced the wood cell by cell, with trace metals creating the distinctive blue color.",
            hardness = "7", luster = "Vitreous", streak = "—",
            crystalSystem = "N/A (chalcedony replacement)", chemicalFormula = "SiO₂ (with trace Cr, Cu)",
            commonColors = listOf("Teal", "Blue-gray", "Turquoise-blue"),
            whereFound = listOf("Blue Forest, Sweetwater County, Wyoming, USA", "Green River Formation, Wyoming"),
            funFacts = listOf("The blue color is natural — from trace chromium and copper in the silica.", "The original 'Blue Forest' collecting area is now on private land and inaccessible.", "Some logs are over 100 feet long and 10 feet in diameter."),
            uses = "Jewelry, bookends, tabletops, collector specimens.",
            rarity = "Rare", geologicalPeriod = "Eocene", age = "~50 mya"
        ),
        Specimen(
            id = "petrified-wood-opalized", name = "Petrified Wood, Opalized",
            rockClass = RockClass.FOSSIL, category = "Fossil — Wood replaced by precious opal",
            tagline = "Wood grain preserved in shimmering precious opal.",
            emoji = "\uD83D\uDC8E", colorHex = 0xFF88CCEE,
            description = "Opalized petrified wood is one of the rarest forms of petrified wood — where silica replacement includes precious opal with brilliant play-of-color. The wood grain and tree rings are preserved in creamy white to translucent opal that flashes blue, green, and orange fire. It's found in only a few locations worldwide, notably Nevada's Virgin Valley.",
            formation = "Wood buried in silica-rich volcanic ash where conditions favored opal formation rather than chalcedony. The silica spheres are regularly arranged, creating diffraction and play-of-color.",
            hardness = "5.5–6.5", luster = "Vitreous to waxy", streak = "—",
            crystalSystem = "Amorphous (opal)", chemicalFormula = "SiO₂·nH₂O (opal)",
            commonColors = listOf("Cream with blue fire", "White with green flash", "Translucent with rainbow fire"),
            whereFound = listOf("Virgin Valley, Nevada, USA", "Oregon, USA", "Australia (rare)", "Indonesia"),
            funFacts = listOf("The finest opalized wood can contain precious black opal.", "Virgin Valley opalized wood shows brilliant play-of-color.", "Australian opalized wood can be 100+ million years old."),
            uses = "Gemstone, jewelry, high-end collector specimens.",
            rarity = "Rare", geologicalPeriod = "Miocene–Eocene"
        ),
        Specimen(
            id = "petrified-wood-oregon-green", name = "Petrified Wood, Oregon",
            rockClass = RockClass.FOSSIL, category = "Fossil — Conifer wood replacement",
            tagline = "Moss-green agate preserving ancient Oregon forests.",
            emoji = "\uD83D\uDFE2", colorHex = 0xFF558844,
            description = "Oregon green petrified wood is characterized by its distinctive moss-green to olive coloration from chromium-rich silica. The Hampton Butte area in central Oregon produces specimens with beautiful green hues and striking wood grain patterns from ancient conifer forests.",
            formation = "Ancient conifer trees buried by volcanic ash in central Oregon's Eocene deposits. Chromium-bearing silica replaced the wood, creating the green color.",
            hardness = "7", luster = "Vitreous", streak = "—",
            crystalSystem = "N/A (chalcedony replacement)", chemicalFormula = "SiO₂ (with Cr)",
            commonColors = listOf("Moss green", "Olive", "Forest green", "Green with brown"),
            whereFound = listOf("Hampton Butte, Oregon, USA", "Central Oregon", "Prineville area, Oregon"),
            funFacts = listOf("The green color comes from chromium — the same element that colors emeralds.", "Hampton Butte petrified wood is popular with lapidary artists.", "Some specimens show both green and brown color zones."),
            uses = "Jewelry, cabochons, lapidary slabs.",
            rarity = "Uncommon", geologicalPeriod = "Eocene", age = "~40 mya"
        ),
        Specimen(
            id = "petrified-wood-rainbow", name = "Petrified Wood, Rainbow",
            rockClass = RockClass.FOSSIL, category = "Fossil — Conifer wood replacement",
            tagline = "Every color in the rainbow frozen in stone.",
            emoji = "\uD83C\uDF08", colorHex = 0xFFCC6644,
            description = "Rainbow petrified wood from Arizona's Petrified Forest National Park displays the most spectacular colors in the petrified wood world — vivid bands of red, orange, yellow, pink, purple, and white. Iron oxide creates the reds, manganese the pinks and purples, and pure silica the whites. Each log is a natural work of abstract art.",
            formation = "Late Triassic conifer trees were buried by volcanic ash in the Chinle Formation. Silica from the ash dissolved in groundwater and replaced the wood. Different trace elements created the rainbow of colors.",
            hardness = "7", luster = "Vitreous", streak = "—",
            crystalSystem = "N/A (chalcedony replacement)", chemicalFormula = "SiO₂ (with Fe, Mn oxides)",
            commonColors = listOf("Red", "Orange", "Yellow", "Pink", "Purple", "White", "Rainbow bands"),
            whereFound = listOf("Petrified Forest National Park, Arizona, USA", "Chinle Formation, Arizona"),
            funFacts = listOf("Arizona's Petrified Forest preserves 225-million-year-old Triassic trees.", "Removing petrified wood from the National Park is a federal crime.", "The most colorful specimens come from the Rainbow Forest area."),
            uses = "Museum displays, collector specimens, lapidary.",
            rarity = "Common (in the Park)", geologicalPeriod = "Late Triassic", age = "~225 mya"
        ),
    )
}
