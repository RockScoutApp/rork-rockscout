package com.rork.rockscout.data

/**
 * Type-variety-format copies of select Rocks Are Amazing specimens for the main database.
 * The original RAA entries remain unchanged; these are duplicate cards with names reformatted
 * as "Type, Variety" so they appear in the central Specimen Database alongside the originals.
 */
object RaaTypeVarietySpecimens {

    val specimens: List<Specimen> = buildList {
        addAll(RocksAreAmazingSpecimens.copperInclusionAgates.map { specimen ->
            specimen.copy(
                id = "${specimen.id}-tv",
                name = when (specimen.id) {
                    "amazing-copper-replacement-agate" -> "Agate, Copper Replacement"
                    "amazing-copper-banded-agate" -> "Agate, Copper-Banded"
                    "amazing-copper-infused-agate" -> "Agate, Copper-Infused (Wolverine)"
                    "amazing-silver-copper-agate" -> "Agate, Silver-Copper"
                    else -> specimen.name
                },
            )
        })

        addAll(RocksAreAmazingSpecimens.otherAmazing.map { specimen ->
            specimen.copy(
                id = "${specimen.id}-tv",
                name = when (specimen.id) {
                    "amazing-cave-pearl" -> "Calcite, Cave Pearl (Pisolith)"
                    "amazing-desert-rose-new" -> "Selenite, Desert Rose"
                    "amazing-fulgurite" -> "Fulgurite, Petrified Lightning"
                    "amazing-zeolite-natrolite" -> "Natrolite Spray"
                    "amazing-pyrite-sun" -> "Pyrite, Sun (Dollar)"
                    "amazing-tenebrescent-sodalite" -> "Hackmanite"
                    "amazing-thunderegg" -> "Agate, Thunderegg (Lithophysae)"
                    "amazing-trapiche-emerald" -> "Trapiche"
                    "amazing-vivianite-crystals" -> "Vivianite, Unstable Green"
                    else -> specimen.name
                },
            )
        })

        addAll(RocksAreAmazingSpecimens.coprolites.map { specimen ->
            specimen.copy(
                id = "${specimen.id}-tv",
                name = when (specimen.id) {
                    "amazing-coprolite-crocodilian" -> "Coprolite, Crocodilian"
                    "amazing-coprolite-fish" -> "Coprolite, Fish"
                    "amazing-coprolite-herbivore" -> "Coprolite, Herbivore Dinosaur"
                    "amazing-coprolite-jurassic" -> "Coprolite, Jurassic Herbivore"
                    "amazing-coprolite-shark" -> "Coprolite, Shark"
                    "amazing-coprolite-trex" -> "Coprolite, T. rex"
                    else -> specimen.name
                },
            )
        })
    }
}
