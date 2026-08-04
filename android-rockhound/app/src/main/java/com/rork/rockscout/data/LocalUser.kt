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
    val email_verified: Boolean = true,
    /** Epoch millis when the user last updated their coarse location (for Most Recent sort). */
    val last_location_update: Long = 0L,
    /** Admin-controlled deletion: when true, the user sees a blocking popup and cannot access the app.
     *  The account remains in the DB so it can be identified on sign-in and restored. */
    val account_deleted: Boolean = false,
    /** Reason for the admin deletion (shown to the user in the blocking popup). */
    val deletion_reason: String? = null,
    /** Timestamp (millis) when the admin deleted the account. */
    val deleted_at: Long? = null,
    /** Timestamp (millis) when the admin restored the account (null if never restored). */
    val restored_at: Long? = null,
    /** User's gender: "male", "female", or "rather_not_say". Shown on public profile. */
    val gender: String = "rather_not_say",
    /** Birthday as epoch millis (00:00 UTC). Null = not set / rather not say. */
    val birthday_millis: Long? = null,
    /** Whether the birthday is shown publicly. */
    val birthday_public: Boolean = false,
    /** User's favorite rock — free text shown on the profile card. */
    val favorite_rock: String = "",
    /** Custom highlight color for the profile page (hex string like "#FF3B30"). Null = default. */
    val highlight_color: String? = null,
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
        last_location_update = last_location_update,
        gender = gender,
        birthday_millis = if (birthday_public) birthday_millis else null,
        birthday_public = birthday_public,
        favorite_rock = favorite_rock,
        highlight_color = highlight_color,
    )
}
