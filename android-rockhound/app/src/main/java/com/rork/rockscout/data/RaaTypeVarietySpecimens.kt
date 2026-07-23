package com.rork.rockscout.data

/**
 * Type-variety-format copies of select Rocks Are Amazing specimens for the main database.
 * The original RAA entries use the RAA-specific "Variety Type" format (no comma); these
 * duplicate cards use the main database's "Type, Variety" format so the central specimen
 * list stays consistent with the rest of the app.
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

        addAll(RocksAreAmazingSpecimens.mineralInclusions.filter { it.id in setOf(
            "amazing-actinolite-quartz",
            "amazing-chlorite-quartz",
            "amazing-dumortierite-quartz-new",
            "amazing-hematite-quartz"
        ) }.map { specimen ->
            specimen.copy(
                id = "${specimen.id}-tv",
                name = when (specimen.id) {
                    "amazing-actinolite-quartz" -> "Quartz, Actinolite-Included (Thetis Hair Stone)"
                    "amazing-chlorite-quartz" -> "Quartz, Chlorite-Included"
                    "amazing-dumortierite-quartz-new" -> "Quartz, Dumortierite-Included"
                    "amazing-hematite-quartz" -> "Quartz, Hematite-Included (Fire Quartz)"
                    else -> specimen.name
                },
            )
        })

        addAll(RocksAreAmazingSpecimens.petroleumInclusions.filter { it.id == "amazing-bitumen-calcite" }.map { specimen ->
            specimen.copy(
                id = "${specimen.id}-tv",
                name = when (specimen.id) {
                    "amazing-bitumen-calcite" -> "Calcite, Bitumen Included"
                    else -> specimen.name
                },
            )
        })

        addAll(RocksAreAmazingSpecimens.industrialSlag.map { specimen ->
            specimen.copy(
                id = "${specimen.id}-tv",
                name = when (specimen.id) {
                    "amazing-slag-amber" -> "Slag Glass, Amber"
                    "amazing-slag-blue" -> "Slag Glass, Blue"
                    "amazing-copper-smelting-slag" -> "Slag, Copper Smelting"
                    "amazing-slag-green" -> "Slag Glass, Green"
                    "amazing-iron-furnace-slag" -> "Slag, Iron Furnace"
                    "amazing-leland-blue" -> "Slag Glass, Leland Blue"
                    "amazing-slag-manganese" -> "Slag Glass, Manganese"
                    "amazing-slag-purple" -> "Slag Glass, Purple"
                    "amazing-slag-steel-furnace" -> "Slag, Steel Furnace"
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
