package com.rork.rockscout.data

/**
 * RevenueCat IAP configuration constants.
 * These match the dashboard configuration in RevenueCat.
 */
object IapConfig {
    /** Premium entitlement identifier configured in RevenueCat dashboard. */
    const val ENTITLEMENT_ID = "premium"

    /** Pro entitlement identifier — legacy Pro tier. Kept for backward compatibility
     *  so existing Pro subscribers keep working. New subscribers buy Premium which
     *  now includes all 3 AI models. */
    const val PRO_ENTITLEMENT_ID = "pro"

    /** Offering identifier configured in RevenueCat dashboard. */
    const val OFFERING_ID = "default"

    /** Monthly Premium subscription — $5.99/mo, full unlimited access, ad-free, all 3 AI models. */
    const val PACKAGE_MONTHLY = "monthly"

    /** Annual Premium subscription — legacy, kept for existing subscribers only. Not shown in UI. */
    const val PACKAGE_PREMIUM_ANNUAL = "premium_annual"

    /** Monthly Pro subscription — legacy, kept for existing subscribers only. Not shown in UI. */
    const val PACKAGE_PRO_MONTHLY = "pro_monthly"

    /** Annual Pro subscription — legacy, kept for existing subscribers only. Not shown in UI. */
    const val PACKAGE_PRO_ANNUAL = "pro_annual"

    /** Single-use identifier token packages. */
    const val PACKAGE_TOKEN_1 = "token_1"
    const val PACKAGE_TOKEN_4 = "token_4"
    const val PACKAGE_TOKEN_10 = "token_10"

    /** All token package identifiers, sorted by quantity. */
    val ALL_TOKEN_PACKAGES = listOf(
        PACKAGE_TOKEN_1,
        PACKAGE_TOKEN_4,
        PACKAGE_TOKEN_10,
    )

    /** Number of tokens granted by each package. */
    val TOKEN_PACKAGE_QTY: Map<String, Int> = mapOf(
        PACKAGE_TOKEN_1 to 1,
        PACKAGE_TOKEN_4 to 4,
        PACKAGE_TOKEN_10 to 10,
    )

    /** Preset token purchase option shown in the dropdown. */
    data class TokenOption(
        val packageId: String,
        val tokenCount: Int,
        val fallbackPrice: String,
    )

    /** Preset token tiers shown in the dropdown. */
    val PRESET_TOKENS = listOf(
        TokenOption(PACKAGE_TOKEN_1, 1, "$0.35"),
        TokenOption(PACKAGE_TOKEN_4, 4, "$1.25"),
        TokenOption(PACKAGE_TOKEN_10, 10, "$2.50"),
    )

    /**
     * Donation package identifiers.
     *
     * Donations grant identifier tokens (sized off the $0.50/token rate) and a
     * tier-based temporary full-feature unlock (location monitoring, field
     * captures, wishlist, my rocks, favorite spots, RockScout Friends).
     * All donations grant Haiku AND Sonnet model access.
     *   $2.00   →  5 tokens   (+ 2 days unlock)  — Haiku + Sonnet
     *   $4.00   → 10 tokens   (+ 5 days unlock)  — Haiku + Sonnet
     */
    const val PACKAGE_DONATION_2 = "donation_2"
    const val PACKAGE_DONATION_5 = "donation_5"
    const val PACKAGE_DONATION_10 = "donation_10"
    const val PACKAGE_DONATION_20 = "donation_20"
    const val PACKAGE_DONATION_50 = "donation_50"

    /** Active donation package identifiers shown in the UI (trimmed to 2 options). */
    val ACTIVE_DONATION_PACKAGES = listOf(
        PACKAGE_DONATION_2,
        PACKAGE_DONATION_5,
    )

    /** All donation package identifiers including legacy tiers (for restore support). */
    val ALL_DONATION_PACKAGES = listOf(
        PACKAGE_DONATION_2,
        PACKAGE_DONATION_5,
        PACKAGE_DONATION_10,
        PACKAGE_DONATION_20,
        PACKAGE_DONATION_50,
    )

    /** Preset donation options with display amounts, token grants, and backing package IDs. */
    data class DonationOption(
        val packageId: String,
        val displayAmount: Int,
        val fallbackPrice: String,
        /** Number of identifier tokens granted by this donation tier. */
        val tokenGrant: Int,
        /** Number of days of location-monitoring unlock granted by this tier (0 = none). */
        val locationDays: Int,
    )

    /** Preset donation tiers shown in the dropdown (trimmed to 2 options). */
    val PRESET_DONATIONS = listOf(
        DonationOption(PACKAGE_DONATION_2, 2, "$2.00", tokenGrant = 5, locationDays = 2),
        DonationOption(PACKAGE_DONATION_5, 4, "$4.00", tokenGrant = 10, locationDays = 5),
    )

    /** Map of donation packageId → token grant quantity. */
    val DONATION_TOKEN_GRANT: Map<String, Int> = PRESET_DONATIONS.associate { it.packageId to it.tokenGrant }

    /** Number of days of location-monitoring unlock granted by a donation package (0 = none). */
    fun donationLocationDays(packageId: String): Int {
        return PRESET_DONATIONS.firstOrNull { it.packageId == packageId }?.locationDays ?: 0
    }

    /** Human-readable label for the full-feature unlock duration granted by a tier. */
    fun donationLocationLabel(packageId: String): String {
        return when (donationLocationDays(packageId)) {
            0 -> ""
            2 -> "+ 2 days unlock"
            5 -> "+ 5 days unlock"
            7 -> "+ 1 wk unlock"
            14 -> "+ 2 wks unlock"
            21 -> "+ 3 wks unlock"
            30 -> "+ 1 mo unlock"
            else -> "+ ${donationLocationDays(packageId)} days unlock"
        }
    }

    /**
     * Find the nearest preset donation package for a custom dollar amount.
     * Maps arbitrary user input to the closest available IAP product.
     */
    fun nearestDonationPackage(amount: Double): DonationOption {
        return when {
            amount <= 4.49 -> PRESET_DONATIONS[0]
            else -> PRESET_DONATIONS[1]
        }
    }
}
