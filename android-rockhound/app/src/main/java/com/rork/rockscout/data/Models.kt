package com.rork.rockscout.data

import kotlinx.serialization.Serializable

/** Broad geological classification of a specimen. */
enum class RockClass(val label: String) {
    IGNEOUS("Igneous"),
    SEDIMENTARY("Sedimentary"),
    METAMORPHIC("Metamorphic"),
    MINERAL("Mineral"),
    CRYSTAL("Crystal / Gem"),
    FOSSIL("Fossil"),
}

/** A rock, mineral, or crystal entry — mindat-style reference data. */
data class Specimen(
    val id: String,
    val name: String,
    val rockClass: RockClass,
    val category: String,          // e.g. "Silicate", "Volcanic rock"
    val tagline: String,           // one-line summary
    val emoji: String,             // simple visual glyph
    val colorHex: Long,            // representative accent color
    val description: String,       // overview paragraph
    val formation: String,         // how it forms / fossilized
    val hardness: String,          // Mohs scale or "—"
    val luster: String,
    val streak: String,
    val crystalSystem: String,
    val chemicalFormula: String,
    val commonColors: List<String>,
    val whereFound: List<String>,
    val funFacts: List<String>,
    val uses: String,
    val rarity: String,            // Common / Uncommon / Rare
    val geologicalPeriod: String? = null,  // fossil-only: which period it lived
    val age: String? = null,               // fossil-only: age in millions of years
    val imageUrls: List<String> = emptyList(), // real specimen photographs
    val catalogAddedAtMs: Long = 0L,       // epoch ms when added to catalog; 0 = legacy (no badge)
) {
    /** True when this entry was added to the catalog within the last 7 days. */
    fun isNew(): Boolean {
        if (catalogAddedAtMs == 0L) return false
        val ageMs = System.currentTimeMillis() - catalogAddedAtMs
        return ageMs in 0..(SEVEN_DAYS_MS)
    }

    private companion object {
        const val SEVEN_DAYS_MS = 7L * 24 * 60 * 60 * 1000
    }
}

/** A dig site, mine, quarry, or public collecting area. */
data class DigLocation(
    val id: String,
    val name: String,
    val type: LocationType,
    val region: String,            // "Arkansas, USA"
    val latitude: Double,
    val longitude: Double,
    val summary: String,
    val knownFor: List<String>,    // specimen ids or names you can find
    val mineralTags: List<String>, // searchable mineral names
    val feeInfo: String,           // pricing / fees
    val hours: String,
    val website: String?,
    val phone: String?,
    val difficulty: String,        // "Easy", "Moderate", "Hard"
    val publicAccess: Boolean,
    val tips: String,
    val submitterName: String? = null,
    val submittedPhotoUris: List<String> = emptyList(),
)

enum class LocationType(val label: String, val emoji: String) {
    PUBLIC_DIG("Public Dig Site", "⛏️"),
    MINE("Mine", "🏔️"),
    QUARRY("Quarry", "🪨"),
    BEACH("Beach / Shore", "🏖️"),
    RIVER("River / Creek", "🌊"),
    DESERT("Desert / Field", "🏜️"),
    ROCK_SHOP("Rock Shop", "💎"),
    METAPHYSICAL("Metaphysical Shop", "🔮"),
    LAPIDARY_CLUB("Lapidary Club", "🏛️"),
}

/** General educational article about a rock-forming process. */
data class RockGuide(
    val id: String,
    val title: String,
    val rockClass: RockClass,
    val emoji: String,
    val intro: String,
    val howItForms: String,
    val keyTraits: List<String>,
    val examples: List<String>,    // specimen ids
    val cycleNote: String,
)

/** A prehistoric geological period with climate, continents, and key fossil organisms. */
data class FossilPeriod(
    val id: String,
    val name: String,
    val era: String,               // e.g. "Paleozoic"
    val emoji: String,
    val timeRange: String,         // e.g. "541 – 485 million years ago"
    val duration: String,          // e.g. "~56 million years"
    val climate: String,
    val continents: String,
    val majorEvents: List<String>,
    val keyOrganisms: List<PeriodOrganism>,
    val funFact: String,
)

data class PeriodOrganism(
    val name: String,
    val type: String,              // e.g. "Trilobite", "Ammonite"
    val emoji: String,
    val description: String,
    val relatedSpecimenId: String?, // link to a Specimen entry if one exists
)

/** A photo capture from the in-app identification camera, saved for the user's field journal. */
@Serializable
data class CapturedPhoto(
    val id: String,
    val specimenId: String,
    val specimenEmoji: String,
    val confidence: Int,
    val timestamp: Long,           // System.currentTimeMillis() when captured
    val note: String = "",
    // Editable user fields — override the auto-detected specimen name
    val customName: String = "",
    val customLocation: String = "",
    val generalInfo: String = "",
    // Multiple photo URIs for a single field capture card
    val imageUris: List<String> = emptyList(),
    // Pending image URI awaiting moderation review (questionable content)
    val pendingImageUri: String? = null,
    // Whether this capture has been promoted to My Rocks or Wishlist
    val inCollection: Boolean = false,
    val inWishlist: Boolean = false,
    // Map pin coordinates — set when the user drops a pin on the Field Camera save form.
    // Null when no pin was dropped.
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    /** Display name: user's custom name if set, otherwise falls back to the specimen name from the database. */
    fun displayName(specimenName: String): String =
        customName.ifBlank { specimenName }

    /** Whether this capture has map coordinates for the specimen marker map. */
    val hasCoordinates: Boolean get() = latitude != null && longitude != null
}

/** A single stop on a planned rockhounding trip — references an existing dig site
 *  or a custom pin dropped by the user (food stops, gas, unnamed creek, etc.).
 *  Custom pins use a generated locationId ("custom-pin-{UUID}") and carry
 *  optional lat/lng so they appear on the route map and get directions. */
/** Type of trip stop — determines how coordinates are resolved and whether
 *  tapping the stop opens a detail page. */
enum class TripStopType(val label: String) {
    DIG_SITE("Dig Site"),
    TRAILHEAD("Trailhead"),
    CAMPGROUND("Campground"),
    CUSTOM_PIN("Custom Pin"),
    STATE_PARK("State Park"),
    BLM_DIG_SITE("BLM Dig Site"),
}

@Serializable
data class TripStop(
    val locationId: String,
    val locationName: String,
    val order: Int,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isCustomPin: Boolean = false,
    /** Whether this stop is a dig site, trailhead, campground, or custom pin.
     *  Defaults to DIG_SITE for backward compatibility with existing trips. */
    val stopType: String = "dig_site",
)

/** A specimen marker pinned by the user on a trip's specimen marker map.
 *  Stored in [Trip.specimenMarkers] and displayed on the trip detail route map. */
@Serializable
data class SpecimenMarker(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "Other",
)

/** A bookmarked location for tracking aurora visibility at specific coordinates.
 *  Stored locally via PersistenceManager in the aurora saved spots list. */
@Serializable
data class AuroraSavedSpot(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val createdAt: Long = System.currentTimeMillis(),
)

/** A planned multi-stop rockhounding trip.
 *  Stored locally via PersistenceManager; shareable via the system share sheet. */
@Serializable
data class Trip(
    val id: String,
    val name: String,
    val date: Long,                  // epoch millis for the planned trip date
    val stops: List<TripStop>,       // ordered list of dig sites to visit
    val targetSpecimens: List<String> = emptyList(), // free-text specimen names the user is hunting
    val gearChecklist: List<String> = emptyList(),   // gear items to bring
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false,
    val completedAt: Long? = null,
    // Specimen markers pinned by the user on the trip's specimen marker map (edit mode only).
    val specimenMarkers: List<SpecimenMarker> = emptyList(),
)

/** Type of trade board listing. */
enum class ListingType(val label: String) {
    HAVE("Have"),
    WANT("Want"),
}

/** Whether a listing is a swap, sale, or buy request. */
enum class ListingMode(val label: String) {
    SWAP("Swap"),
    SELLING("Selling"),
    BUYING("Buying"),
}

/** A trade board listing — either a specimen the user is offering (HAVE)
 *  or a specimen the user is hunting for (WANT). Stored locally via
 *  PersistenceManager; designed to wire to a backend `trade_listings` table later.
 *
 *  For HAVE listings, the photo/specimen can be sourced from a Field Capture,
 *  a Collection entry, the camera gallery, or a fresh camera capture — the
 *  `sourceCaptureId` / `sourceCollectionSpecimenId` fields preserve that link.
 *  For WANT listings, the listing can be derived from a Wishlist entry — the
 *  `sourceWishlistSpecimenId` field preserves that link so the wishlist can show
 *  a "Posted to Trade Board" badge. */
@Serializable
data class TradeListing(
    val id: String,
    val type: ListingType,             // HAVE vs WANT
    val listingMode: ListingMode = ListingMode.SWAP, // swap or sell
    val price: String = "",            // SELL only: asking price (free text, user types amount)
    val specimenName: String,          // free text or pulled from the database
    val condition: String = "",        // HAVE only: rough / tumbled / cut / polished / slab
    val description: String = "",      // what they're offering (HAVE) or what they want (WANT)
    val wantInReturn: String = "",     // HAVE only: what they'd accept in return (free text + tags)
    val photoUri: String? = null,      // single listing photo (gallery/camera/capture)
    val tags: List<String> = emptyList(),
    val sourceCaptureId: String? = null,           // HAVE: link to a CapturedPhoto
    val sourceCollectionSpecimenId: String? = null,// HAVE: link to a CollectionEntry specimen
    val sourceWishlistSpecimenId: String? = null,  // WANT: link to a wishlist specimen id
    val status: String = "active",     // active / traded / expired
    val ownerUserId: String? = null,   // backend owner id; null for local-only listings
    val ownerUsername: String? = null, // display name of the listing owner for the Trade Board card
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000, // 14-day auto-expiry
)

/** A long-form journal entry for a day or trip in the field — distinct from per-specimen CapturedPhoto.
 *  Stored locally via PersistenceManager. */
@Serializable
data class JournalEntry(
    val id: String,
    val date: Long,                  // epoch millis for the entry date
    val location: String = "",       // free-text location or selected dig site name
    val digSiteId: String? = null,   // optional link to a DigLocation
    val tripId: String? = null,      // optional link to a Trip
    val weatherSummary: String = "", // auto-populated from weather feature when a dig site is selected
    val notes: String = "",          // free-form long text — the story of the day
    val attachedCaptureIds: List<String> = emptyList(), // links to CapturedPhoto entries
    val photoUris: List<String> = emptyList(),          // landscape/group photos beyond per-specimen captures
    val specimenMarkers: List<SpecimenMarker> = emptyList(), // pins marking rocks found during this field day
    val createdAt: Long = System.currentTimeMillis(),
)
