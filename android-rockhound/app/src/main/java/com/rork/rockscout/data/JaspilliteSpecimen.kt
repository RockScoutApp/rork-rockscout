package com.rork.rockscout.data

/**
 * Jaspillite specimen — a banded iron formation rich in red jasper and hematite.
 * Added per user request. Image regenerated from three reference photos of
 * Michigan jaspillite showing rough red jasper bands and dark iron-oxide layers.
 */
@Suppress("unused")
object JaspilliteSpecimen {
    val specimens: List<Specimen> = listOf(
        Specimen(
            id = "jaspillite",
            name = "Jaspillite",
            rockClass = RockClass.SEDIMENTARY,
            category = "Banded iron formation (BIF) / Chemical sedimentary rock",
            tagline = "A banded iron formation where the red bands are jasper.",
            emoji = "🟥",
            colorHex = 0xFF8B2635,
            description = "Jaspillite is a distinctive variety of banded iron formation (BIF) in which the silica-rich layers are made of red jasper — a cryptocrystalline variety of quartz colored by oxidized iron. It forms striking red, orange, and deep gray metallic bands, and is one of the most visually dramatic Precambrian rocks. Classic jaspillite comes from the Lake Superior region, especially the Marquette Iron Range near Ishpeming, Michigan, where road cuts and mine dumps expose banded red-and-black specimens that look almost painted. Rarity: Common in iron ranges; collectible specimens are locally abundant.",
            formation = "Banded iron formations are layered chemical sedimentary rocks made of alternating iron-rich and silica-rich layers. They formed mostly between about 3.8 and 1.8 billion years ago, when Earth's oceans had little free oxygen but photosynthetic cyanobacteria were beginning to release oxygen into the surface environment. Dissolved iron from submarine hydrothermal vents reacted with that oxygen to precipitate iron oxides, while dissolved silica also settled out in rhythmic layers. Jaspillite is the variety where the silica layers later recrystallized into red jasper, often through diagenesis and low-grade metamorphism. The red color comes from finely dispersed hematite within the chert/jasper layers, while the dark metallic bands are hematite and magnetite. So all jaspillite is BIF, but not all BIF is jaspillite — the name specifically refers to the jasper-rich, brightly colored type.",
            hardness = "6.5–7 (jasper/quartz layers); iron oxide layers are very hard",
            luster = "Dull to waxy on jasper bands; metallic to submetallic on iron bands",
            streak = "Red-brown to black",
            crystalSystem = "None (microcrystalline layered aggregate)",
            chemicalFormula = "Variable: SiO₂ + Fe₂O₃/Fe₃O₄ (silica plus iron oxides)",
            commonColors = listOf("Deep red", "Brick red", "Orange-red", "Dark gray", "Silver-black metallic bands"),
            whereFound = listOf("Jasper Hill, Ishpeming, Michigan, USA", "Marquette Iron Range, Michigan", "Lake Superior region, USA/Canada", "Hamersley Basin, Western Australia", "Labrador Trough, Canada"),
            funFacts = listOf(
                "Jaspillite is essentially a BIF that decided to be beautiful — its red jasper bands make it a favorite lapidary and decorative stone.",
                "The famous Jasper Hill locality in Ishpeming, Michigan, is where many collectors first encounter jaspillite in the field.",
                "BIFs are the world's most important iron ore source, but jaspillite is often valued as much for its appearance as for its iron content.",
                "The alternating red and dark bands can be fractions of an inch to inches thick and are usually continuous across large outcrops.",
                "Polished jaspillite slabs are used for bookends, display pieces, and jewelry because of their striking contrast."
            ),
            uses = "Iron ore, decorative stone, lapidary slabs, bookends, geological research into early Earth's oxygen history.",
            rarity = "Common in iron ranges; collectible specimens are locally abundant",
        ),
    )
}
