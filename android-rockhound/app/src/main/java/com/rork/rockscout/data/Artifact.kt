package com.rork.rockscout.data

/**
 * A standalone artifact entry — arrowheads, hand axes, beads, effigies,
 * pipes, game discs, pottery, and other prehistoric tools and ornaments
 * made of stone, shell, wood, or ceramic.
 *
 * This is a SEPARATE data class from [Specimen] — the two never mix.
 * Artifacts are not added to [SeedData.allSpecimens], never appear in
 * specimen search, and never inflate specimen counts. The only connection
 * point is the ARTIFACTS chip in the specimen database, which swaps the
 * list source at the screen level.
 *
 * @param id unique artifact identifier (e.g. "art-clovis-point")
 * @param name display name (e.g. "Clovis Point")
 * @param family broad group (e.g. "Arrowheads", "Hand Axes")
 * @param subFamily shape or technique subgroup (e.g. "Lanceolate Point")
 * @param tagline one-line evocative summary, in the voice of specimen taglines
 * @param emoji simple visual glyph
 * @param accentHex representative accent color (warm clay/ochre palette)
 * @param imageUrl single replica shape image URL (one per card)
 * @param whereFound regions where this type is found
 * @param description overview paragraph
 * @param howMade how it was knapped, carved, or formed
 * @param funFacts notable facts
 * @param tribe associated culture or tradition
 * @param timePeriod age range (e.g. "Paleoindian · 10,000–9,000 BCE")
 * @param addedAtMs epoch milliseconds when this entry was added to the catalog;
 *   0 means it is a legacy entry (not shown as new). Entries with a timestamp
 *   within the last 7 days display a "NEW" badge in list views.
 */
data class Artifact(
    val id: String,
    val name: String,
    val family: String,
    val subFamily: String,
    val tagline: String,
    val emoji: String,
    val accentHex: Long,
    val imageUrl: String,
    val whereFound: List<String>,
    val description: String,
    val howMade: String,
    val funFacts: List<String>,
    val tribe: String,
    val timePeriod: String,
    val addedAtMs: Long = 0L,
) {
    /** True when this entry was added to the catalog within the last 7 days. */
    fun isNew(): Boolean {
        if (addedAtMs == 0L) return false
        val ageMs = System.currentTimeMillis() - addedAtMs
        return ageMs in 0..(SEVEN_DAYS_MS)
    }

    private companion object {
        const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000
    }
}
