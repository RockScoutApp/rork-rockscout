package com.rork.rockscout.data

import android.util.Log
import com.rork.rockscout.data.MockDataSeeder.LocalTradeInterest
import com.rork.rockscout.data.MockDataSeeder.LocalPublicTradeListing
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Tracks "I'm interested!" taps on trade listings.
 *
 * Each (listing_id, interested_user_id) pair is unique — a user can only
 * express interest once per listing. On insert, the listing owner receives
 * an instant [NotificationRepository.TYPE_TRADE_INTEREST] notification
 * (not throttled by the 4-hour recheck).
 *
 * Fully self-contained: all data stored in [LocalDataStore]. No Supabase.
 * Public trade listings from mock users are also stored locally so the trade
 * board feels populated.
 */
class TradeInterestRepository private constructor() {

    @Serializable
    data class TradeInterestRow(
        val id: String,
        val listing_id: String,
        val listing_owner_id: String,
        val interested_user_id: String,
        val message: String = "",
        val created_at: String,
    )

    private val _interestedListingIds = MutableStateFlow<Set<String>>(emptySet())
    val interestedListingIds: StateFlow<Set<String>> = _interestedListingIds.asStateFlow()

    private val _interestsOnMyListings = MutableStateFlow<List<TradeInterestRow>>(emptyList())
    val interestsOnMyListings: StateFlow<List<TradeInterestRow>> = _interestsOnMyListings.asStateFlow()

    private val _myInterests = MutableStateFlow<List<TradeInterestRow>>(emptyList())
    val myInterests: StateFlow<List<TradeInterestRow>> = _myInterests.asStateFlow()

    /** Public trade listings from the community (mock + user-created). */
    private val _publicListings = MutableStateFlow<List<LocalPublicTradeListing>>(emptyList())
    val publicListings: StateFlow<List<LocalPublicTradeListing>> = _publicListings.asStateFlow()

    private fun currentUserId(): String? = AuthRepository.instance.currentUserId

    /** Load all trade interests the current user has expressed.
     *  Also keeps the set of interested listing IDs used to disable the
     *  "I'm interested!" button. */
    suspend fun loadMyInterests() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalTradeInterest>(LocalDataStore.KEY_TRADE_INTERESTS)
            _myInterests.value = rows
                .filter { it.interested_user_id == me }
                .map { TradeInterestRow(it.id, it.listing_id, it.listing_owner_id, it.interested_user_id, it.message, it.created_at) }
                .sortedByDescending { it.created_at }
            _interestedListingIds.value = _myInterests.value.map { it.listing_id }.toSet()
        }.onFailure { Log.w("TradeInterestRepository", "loadMyInterests failed", it) }
    }

    /** Load all interests expressed on the current user's listings. */
    suspend fun loadInterestsOnMyListings() {
        val me = currentUserId() ?: return
        runCatching {
            val rows = LocalDataStore.getTable<LocalTradeInterest>(LocalDataStore.KEY_TRADE_INTERESTS)
            _interestsOnMyListings.value = rows
                .filter { it.listing_owner_id == me }
                .map { TradeInterestRow(it.id, it.listing_id, it.listing_owner_id, it.interested_user_id, it.message, it.created_at) }
                .sortedByDescending { it.created_at }
        }.onFailure { Log.w("TradeInterestRepository", "loadInterestsOnMyListings failed", it) }
    }

    /** Load all public trade listings (community + user's own posted listings). */
    suspend fun loadPublicListings() {
        runCatching {
            val rows = LocalDataStore.getTable<LocalPublicTradeListing>(LocalDataStore.KEY_PUBLIC_TRADE_LISTINGS)
            // Filter out expired listings.
            val now = System.currentTimeMillis()
            _publicListings.value = rows.filter { it.status == "active" && it.expiresAt > now }
        }.onFailure { Log.w("TradeInterestRepository", "loadPublicListings failed", it) }
    }

    /** Express interest in a listing. Returns:
     *  - `Result.success(InterestResult)` if recorded + notification sent.
     *  - `Result.failure(...)` if not signed in, blocked, or already interested. */
    suspend fun expressInterest(
        listing: TradeListing,
        message: String,
    ): Result<InterestResult> {
        val me = currentUserId()
            ?: return Result.failure(IllegalStateException("Not signed in"))
        val ownerId = listing.ownerUserId
            ?: return Result.failure(IllegalStateException("Listing has no backend owner"))

        if (me == ownerId) {
            return Result.failure(IllegalStateException("Can't express interest in your own listing"))
        }

        return runCatching {
            val rows = LocalDataStore.getTable<LocalTradeInterest>(LocalDataStore.KEY_TRADE_INTERESTS).toMutableList()
            if (rows.any { it.listing_id == listing.id && it.interested_user_id == me }) {
                return@runCatching InterestResult(threadId = null)
            }
            rows.add(LocalTradeInterest(
                id = "ti-" + UUID.randomUUID(),
                listing_id = listing.id,
                listing_owner_id = ownerId,
                interested_user_id = me,
                message = ProfanityFilter.filter(message),
                created_at = java.time.OffsetDateTime.now().toString(),
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_TRADE_INTERESTS, rows)

            _interestedListingIds.value = _interestedListingIds.value + listing.id

            // Open (or find) a DM thread with the owner and send the message.
            val social = SocialRepository.instance
            val threadId = social.ensureThread(ownerId)
            if (threadId != null && message.isNotBlank()) {
                social.sendMessage(threadId, message)
            }

            // Send the owner an instant trade-interest notification.
            val myName = AppRepository.instance.profile.value.name.ifBlank { "A RockScout" }
            NotificationRepository.instance.createNotification(
                userId = ownerId,
                type = NotificationRepository.TYPE_TRADE_INTEREST,
                actorId = me,
                refId = listing.id,
                body = "$myName is interested in your ${listing.specimenName}!",
                deepLinkTarget = "my_trades",
            )

            InterestResult(threadId = threadId)
        }.onFailure { Log.w("TradeInterestRepository", "expressInterest failed", it) }
    }

    /** Express interest in a public (mock) listing by listing id. */
    suspend fun expressInterestInPublicListing(
        listingId: String,
        message: String,
    ): Result<InterestResult> {
        val me = currentUserId()
            ?: return Result.failure(IllegalStateException("Not signed in"))
        val listing = _publicListings.value.firstOrNull { it.id == listingId }
            ?: return Result.failure(IllegalStateException("Listing not found"))

        if (me == listing.ownerUserId) {
            return Result.failure(IllegalStateException("Can't express interest in your own listing"))
        }

        return runCatching {
            val rows = LocalDataStore.getTable<LocalTradeInterest>(LocalDataStore.KEY_TRADE_INTERESTS).toMutableList()
            if (rows.any { it.listing_id == listingId && it.interested_user_id == me }) {
                return@runCatching InterestResult(threadId = null)
            }
            rows.add(LocalTradeInterest(
                id = "ti-" + UUID.randomUUID(),
                listing_id = listingId,
                listing_owner_id = listing.ownerUserId,
                interested_user_id = me,
                message = ProfanityFilter.filter(message),
                created_at = java.time.OffsetDateTime.now().toString(),
            ))
            LocalDataStore.setTable(LocalDataStore.KEY_TRADE_INTERESTS, rows)

            _interestedListingIds.value = _interestedListingIds.value + listingId

            val social = SocialRepository.instance
            val threadId = social.ensureThread(listing.ownerUserId)
            if (threadId != null && message.isNotBlank()) {
                social.sendMessage(threadId, message)
            }

            val myName = AppRepository.instance.profile.value.name.ifBlank { "A RockScout" }
            NotificationRepository.instance.createNotification(
                userId = listing.ownerUserId,
                type = NotificationRepository.TYPE_TRADE_INTEREST,
                actorId = me,
                refId = listingId,
                body = "$myName is interested in your ${listing.specimenName}!",
                deepLinkTarget = "my_trades",
            )

            InterestResult(threadId = threadId)
        }.onFailure { Log.w("TradeInterestRepository", "expressInterestInPublicListing failed", it) }
    }

    /** Result of a successful interest expression — includes the DM thread id
     *  so the caller can navigate to it if desired. */
    data class InterestResult(
        val threadId: String?,
    )

    companion object {
        val instance: TradeInterestRepository by lazy { TradeInterestRepository() }
    }
}
