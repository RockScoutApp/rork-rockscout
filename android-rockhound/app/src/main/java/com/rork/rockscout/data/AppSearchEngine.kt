package com.rork.rockscout.data

import androidx.compose.ui.graphics.Color
import com.rork.rockscout.ui.navigation.Routes
import com.rork.rockscout.ui.theme.Aqua
import com.rork.rockscout.ui.theme.Citrine
import com.rork.rockscout.ui.theme.Fossil
import com.rork.rockscout.ui.theme.Success

/**
 * A single relevance-scored match from a global app search.
 * Each match carries everything needed to render a result row and navigate
 * to the matching detail screen.
 */
sealed class SearchMatch(
    open val score: Float,
    open val title: String,
    open val subtitle: String,
    open val emoji: String,
    open val typeLabel: String,
    open val accent: Color,
    open val route: String,
) {
    data class SpecimenMatch(
        val specimen: Specimen,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = specimen.name,
        subtitle = specimen.tagline,
        emoji = specimen.emoji,
        typeLabel = "Specimen",
        accent = Color(specimen.colorHex),
        route = Routes.specimen(specimen.id),
    )

    data class LocationMatch(
        val location: DigLocation,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = location.name,
        subtitle = "${location.type.emoji} ${location.region}",
        emoji = location.type.emoji,
        typeLabel = location.type.label,
        accent = Success,
        route = Routes.location(location.id),
    )

    data class CaptureMatch(
        val capture: CapturedPhoto,
        val specimenName: String,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = capture.displayName(specimenName),
        subtitle = "Field capture • $specimenName",
        emoji = capture.specimenEmoji,
        typeLabel = "Capture",
        accent = Aqua,
        route = Routes.CAPTURES,
    )

    data class CollectionMatch(
        val entry: CollectionEntry,
        val specimen: Specimen,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = specimen.name,
        subtitle = entry.foundAt.ifBlank { "In your collection" },
        emoji = specimen.emoji,
        typeLabel = "My Rock",
        accent = Color(specimen.colorHex),
        route = Routes.specimen(specimen.id),
    )

    data class WishlistMatch(
        val specimen: Specimen,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = specimen.name,
        subtitle = "On your wishlist",
        emoji = specimen.emoji,
        typeLabel = "Wishlist",
        accent = Color(specimen.colorHex),
        route = Routes.specimen(specimen.id),
    )

    data class FavoriteSpotMatch(
        val location: DigLocation,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = location.name,
        subtitle = "Saved favorite spot",
        emoji = location.type.emoji,
        typeLabel = "Favorite",
        accent = Citrine,
        route = Routes.location(location.id),
    )

    data class FavoriteSpotEntryMatch(
        val entry: FavoriteSpotEntry,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = entry.name,
        subtitle = "Saved favorite · ${entry.typeLabel}",
        emoji = entry.emoji,
        typeLabel = "Favorite",
        accent = entry.accent,
        route = entry.route,
    )

    data class TripMatch(
        val trip: Trip,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = trip.name,
        subtitle = "${trip.stops.size} stop${if (trip.stops.size == 1) "" else "s"} planned",
        emoji = "🗺️",
        typeLabel = "Trip",
        accent = Aqua,
        route = Routes.TRIP_PLANNER,
    )

    data class JournalMatch(
        val entry: JournalEntry,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = entry.location.ifBlank { "Field journal entry" },
        subtitle = "Journal entry",
        emoji = "📓",
        typeLabel = "Journal",
        accent = Citrine,
        route = Routes.FIELD_JOURNAL,
    )

    data class TradeMatch(
        val listing: TradeListing,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = listing.specimenName,
        subtitle = "${listing.type.label} • ${listing.listingMode.label}",
        emoji = "🤝",
        typeLabel = "Trade",
        accent = Success,
        route = Routes.TRADE_BOARD,
    )

    data class GuideMatch(
        val guide: RockGuide,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = guide.title,
        subtitle = guide.intro,
        emoji = guide.emoji,
        typeLabel = "Guide",
        accent = Color(0xFF9B59B6),
        route = Routes.guide(guide.id),
    )

    data class PeriodMatch(
        val period: FossilPeriod,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = period.name,
        subtitle = "${period.era} • ${period.timeRange}",
        emoji = period.emoji,
        typeLabel = "Period",
        accent = Fossil,
        route = Routes.period(period.id),
    )

    data class ElementMatch(
        val element: PeriodicTableElements.Element,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = "${element.name} (${element.symbol})",
        subtitle = "${element.category.label} • Atomic #${element.atomicNumber}",
        emoji = element.symbol,
        typeLabel = "Element",
        accent = Color(element.category.colorHex),
        route = Routes.element(element.atomicNumber),
    )

    data class BlmStateMatch(
        val state: BlmState,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = state.name,
        subtitle = "${state.blmAcreage} of BLM land",
        emoji = state.silhouetteEmoji,
        typeLabel = "BLM State",
        accent = Color(state.accentHex),
        route = Routes.blmState(state.code),
    )

    data class BlmTrailheadMatch(
        val trailhead: BlmTrailhead,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = trailhead.name,
        subtitle = "${trailhead.state} • ${trailhead.region}",
        emoji = "🥾",
        typeLabel = "BLM Trailhead",
        accent = Success,
        route = Routes.blmTrailhead(trailhead.name),
    )

    data class BlmCampgroundMatch(
        val campground: BlmCampground,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = campground.name,
        subtitle = "${campground.state} • ${campground.region}",
        emoji = "⛺",
        typeLabel = "BLM Camp",
        accent = Citrine,
        route = Routes.blmCampground(campground.name),
    )

    data class BlmInfoMatch(
        val section: BlmInfoSection,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = section.title,
        subtitle = "BLM guide",
        emoji = section.icon,
        typeLabel = "BLM Info",
        accent = Color(section.accentHex),
        route = Routes.BLM_GUIDE,
    )

    data class HunterMatch(
        val hunter: SocialRepository.HunterProfile,
        val isFriend: Boolean,
        override val score: Float,
    ) : SearchMatch(
        score = score,
        title = hunter.display_name,
        subtitle = if (isFriend) "RockScout Friend" else "Discoverable hunter",
        emoji = hunter.avatar_emoji,
        typeLabel = if (isFriend) "Friend" else "Hunter",
        accent = Aqua,
        route = Routes.userProfile(hunter.id),
    )
}

/**
 * Searches across every searchable area of the app and returns the most
 * relevant matches up to [limit].
 */
fun performGlobalSearch(
    query: String,
    repo: AppRepository,
    currentLocation: Pair<Double, Double> = repo.currentLocation.value,
    friends: List<SocialRepository.HunterProfile> = emptyList(),
    hunters: List<SocialRepository.HunterProfile> = emptyList(),
    limit: Int = 5,
): List<SearchMatch> {
    if (query.isBlank()) return emptyList()
    val q = query.lowercase().trim()

    val matches = mutableListOf<SearchMatch>()

    // Specimens
    SeedData.allSpecimens.forEach { spec ->
        scoreText(
            q,
            listOf(spec.name, spec.category, spec.tagline, spec.description) + spec.commonColors + spec.whereFound,
        )?.let { matches.add(SearchMatch.SpecimenMatch(spec, it)) }
    }

    // Locations
    SeedData.allLocations.forEach { loc ->
        scoreText(
            q,
            listOf(loc.name, loc.region, loc.summary, loc.type.label) + loc.mineralTags + loc.knownFor,
        )?.let { matches.add(SearchMatch.LocationMatch(loc, it)) }
    }

    // International locations
    InternationalLocations.internationalLocations.forEach { loc ->
        scoreText(
            q,
            listOf(loc.name, loc.region, loc.summary, loc.type.label) + loc.mineralTags + loc.knownFor,
        )?.let { matches.add(SearchMatch.LocationMatch(loc, it)) }
    }

    // User's field captures
    repo.captures.value.forEach { cap ->
        val spec = SeedData.specimenById(cap.specimenId)
        val name = cap.displayName(spec?.name ?: "Unknown specimen")
        scoreText(
            q,
            listOf(name, cap.customLocation, cap.generalInfo, cap.note, spec?.name ?: ""),
        )?.let { matches.add(SearchMatch.CaptureMatch(cap, spec?.name ?: "Unknown specimen", it)) }
    }

    // User's collection
    repo.collection.value.forEach { entry ->
        val spec = SeedData.specimenById(entry.specimenId) ?: return@forEach
        scoreText(
            q,
            listOf(spec.name, entry.note, entry.foundAt, spec.category) + spec.commonColors + spec.whereFound,
        )?.let { matches.add(SearchMatch.CollectionMatch(entry, spec, it)) }
    }

    // User's wishlist
    repo.wishlist.value.forEach { id ->
        val spec = SeedData.specimenById(id) ?: return@forEach
        scoreText(
            q,
            listOf(spec.name, spec.category, spec.tagline, spec.rockClass.label) + spec.commonColors + spec.whereFound,
        )?.let { matches.add(SearchMatch.WishlistMatch(spec, it)) }
    }

    // User's favorite spots — resolves all ID types via FavoriteSpotResolver
    // (bare dig site IDs, park:, trailhead:, campground:, blmsite:, blmstate: prefixes)
    repo.favoriteSpots.value.forEach { id ->
        val entry = FavoriteSpotResolver.resolve(id) ?: return@forEach
        scoreText(
            q,
            listOf(entry.name, entry.region, entry.typeLabel),
        )?.let { matches.add(SearchMatch.FavoriteSpotEntryMatch(entry, it)) }
    }

    // Trips
    repo.trips.value.forEach { trip ->
        val fields = listOf(trip.name, trip.notes) + trip.stops.map { it.locationName } + trip.targetSpecimens
        scoreText(q, fields)
            ?.let { matches.add(SearchMatch.TripMatch(trip, it)) }
    }

    // Journal entries
    repo.journalEntries.value.forEach { entry ->
        val fields = listOf(entry.location, entry.notes, entry.weatherSummary)
        scoreText(q, fields)
            ?.let { matches.add(SearchMatch.JournalMatch(entry, it)) }
    }

    // Trade listings
    repo.tradeListings.value.forEach { listing ->
        val fields = listOf(listing.specimenName, listing.description, listing.condition, listing.wantInReturn) + listing.tags
        scoreText(q, fields)
            ?.let { matches.add(SearchMatch.TradeMatch(listing, it)) }
    }

    // Rock guides
    SeedData.guides.forEach { guide ->
        scoreText(
            q,
            listOf(guide.title, guide.intro, guide.rockClass.label, guide.keyTraits.joinToString(), guide.examples.joinToString()),
        )?.let { matches.add(SearchMatch.GuideMatch(guide, it)) }
    }

    // Fossil periods
    SeedData.fossilPeriods.forEach { period ->
        val organisms = period.keyOrganisms.joinToString { "${it.name} ${it.type}" }
        scoreText(
            q,
            listOf(period.name, period.era, period.climate, period.timeRange, organisms, period.majorEvents.joinToString()),
        )?.let { matches.add(SearchMatch.PeriodMatch(period, it)) }
    }

    // Periodic table elements
    PeriodicTableElements.elements.forEach { element ->
        val fields = listOf(element.name, element.symbol, element.category.label, element.inRocks, element.summary)
        scoreText(q, fields, exactNumber = element.atomicNumber.toString())
            ?.let { matches.add(SearchMatch.ElementMatch(element, it)) }
    }

    // BLM states
    BlmData.allStates.forEach { state ->
        scoreText(q, listOf(state.name, state.code, state.whatYouCanCollect, state.specialNotes))
            ?.let { matches.add(SearchMatch.BlmStateMatch(state, it)) }
    }

    // BLM trailheads
    BlmData.trailheads.forEach { trail ->
        scoreText(q, listOf(trail.name, trail.region, trail.description, trail.state))
            ?.let { matches.add(SearchMatch.BlmTrailheadMatch(trail, it)) }
    }

    // BLM campgrounds
    BlmData.campgrounds.forEach { camp ->
        scoreText(q, listOf(camp.name, camp.region, camp.description, camp.state, camp.feeInfo))
            ?.let { matches.add(SearchMatch.BlmCampgroundMatch(camp, it)) }
    }

    // BLM info sections
    BlmData.infoSections.forEach { section ->
        scoreText(q, listOf(section.title, section.points.joinToString()))
            ?.let { matches.add(SearchMatch.BlmInfoMatch(section, it)) }
    }

    // Friends
    friends.forEach { friend ->
        scoreText(q, listOf(friend.display_name))
            ?.let { matches.add(SearchMatch.HunterMatch(friend, isFriend = true, it)) }
    }

    // Discoverable hunters
    hunters.forEach { hunter ->
        if (friends.none { it.id == hunter.id }) {
            scoreText(q, listOf(hunter.display_name))
                ?.let { matches.add(SearchMatch.HunterMatch(hunter, isFriend = false, it)) }
        }
    }

    return matches
        .sortedByDescending { it.score }
        .take(limit)
}

/** Score a single query against a list of values and optional exact numeric match. */
private fun scoreText(
    q: String,
    values: List<String>,
    exactNumber: String? = null,
): Float? {
    if (exactNumber != null && q == exactNumber.lowercase()) return 110f

    val primary = values.firstOrNull()?.lowercase()?.trim() ?: ""
    val rest = values.drop(1).map { it.lowercase() }

    val allText = (values + listOfNotNull(exactNumber)).joinToString(" ").lowercase()
    if (!allText.contains(q)) return null

    return when {
        primary == q -> 100f
        primary.startsWith(q) -> 85f
        primary.contains(" $q") || primary.contains("$q ") -> 70f
        primary.contains(q) -> 60f
        rest.any { it == q } -> 55f
        rest.any { it.startsWith(q) } -> 45f
        rest.any { it.contains(q) } -> 35f
        else -> 20f
    }
}
