package com.rork.rockscout.data

import com.rork.rockscout.ui.components.DinoBodyPlan

/**
 * Geological era grouping for the dinosaur dictionary.
 */
enum class DinoEra(val label: String, val subtitle: String) {
    TRIASSIC("Triassic", "~252–201 mya"),
    JURASSIC("Jurassic", "~201–145 mya"),
    CRETACEOUS("Cretaceous", "~145–66 mya"),
    PALEOGENE("Paleogene", "~66–23 mya"),
    NEOGENE("Neogene", "~23–2.6 mya"),
    QUATERNARY("Quaternary", "~2.6 mya–Present"),
    OTHER("Other Eras", "Before & after dinosaurs"),
}

/**
 * Diet classification for prehistoric animals.
 */
enum class DinoDiet(val label: String) {
    CARNIVORE("Carnivore"),
    HERBIVORE("Herbivore"),
    OMNIVORE("Omnivore"),
    PISCIVORE("Piscivore (fish-eater)"),
    FILTER_FEEDER("Filter Feeder"),
    INSECTIVORE("Insectivore"),
    SCAVENGER("Scavenger"),
}

/**
 * A single entry in the dinosaur & prehistoric animal dictionary.
 * Accurate paleontological data bundled locally — no external image links.
 * Silhouettes are drawn via [DinoSilhouette] based on [bodyPlan].
 */
data class DinoEntry(
    val id: String,
    val name: String,
    val era: DinoEra,
    val period: String,
    val age: String,
    val diet: DinoDiet,
    val bodyPlan: DinoBodyPlan,
    val length: String,
    val weight: String,
    val habitat: String,
    val description: String,
    val funFacts: List<String>,
    val foundIn: List<String>,
    val accentColor: Long,
) {

    /** Year/location of discovery */
    val discovered: String
        get() = funFacts.firstOrNull { it.startsWith("Discovered") } ?: ""

    companion object {
        /** Helper to build entries concisely */
        fun make(
            id: String,
            name: String,
            era: DinoEra,
            period: String,
            age: String,
            diet: DinoDiet,
            bodyPlan: DinoBodyPlan,
            length: String,
            weight: String,
            habitat: String,
            description: String,
            funFacts: List<String>,
            foundIn: List<String>,
            accentColor: Long,
        ): DinoEntry = DinoEntry(
            id, name, era, period, age, diet, bodyPlan,
            length, weight, habitat, description, funFacts, foundIn, accentColor,
        )
    }
}

/**
 * 200+ dinosaurs and prehistoric animals from all eras, including Ice Age mammals.
 * Data compiled from published paleontological sources.
 * Silhouettes are drawn via [DinoSilhouette] — no external images required.
 */
@Suppress("unused")
object DinoDictionary {

    val all: List<DinoEntry> = DinoEntriesPart1.entries + DinoEntriesPart2.entries

    /** Entries grouped by era, in chronological order. */
    val byEra: Map<DinoEra, List<DinoEntry>> = all.groupBy { it.era }

    /** Count of entries. */
    val count: Int get() = all.size
}
