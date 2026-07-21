package com.rork.rockscout.data

import kotlinx.serialization.Serializable

/**
 * A user in the local self-contained database. Used for both the signed-in
 * user and mock community members. Stored as JSON in [LocalDataStore].
 *
 * Maps to [SocialRepository.HunterProfile] for scan results and friend lists
 * via [toHunterProfile]. The extra fields (bio, home_region, email, password)
 * are used by the profile screen and the local auth system.
 */
@Serializable
data class LocalUser(
    val id: String,
    val email: String = "",
    val password: String = "",
    val display_name: String,
    val avatar_emoji: String = "\u26CF\uFE0F",
    val status: String = "off",
    val level: Int = 1,
    val xp: Int = 0,
    val is_premium: Boolean = false,
    val premium_badge: Boolean = false,
    val coarse_lat: Double? = null,
    val coarse_lng: Double? = null,
    val bio: String = "",
    val home_region: String = "",
    val club_enabled: Boolean = false,
    val scan_radius_miles: Int = 50,
    val collection_ids: List<String> = emptyList(),
    val wishlist_ids: List<String> = emptyList(),
    val favorite_spot_ids: List<String> = emptyList(),
) {
    /** Convert to the HunterProfile shape used by scan results + friend lists. */
    fun toHunterProfile(): SocialRepository.HunterProfile = SocialRepository.HunterProfile(
        id = id,
        display_name = display_name,
        avatar_emoji = avatar_emoji,
        status = status,
        level = level,
        is_premium = is_premium,
        premium_badge = premium_badge,
        coarse_lat = coarse_lat,
        coarse_lng = coarse_lng,
        collection_count = collection_ids.size,
        wishlist_count = wishlist_ids.size,
        favorite_spots_count = favorite_spot_ids.size,
    )
}
