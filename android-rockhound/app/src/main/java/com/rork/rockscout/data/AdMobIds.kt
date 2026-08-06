package com.rork.rockscout.data

/**
 * Production Google AdMob identifiers for RockScout.
 *
 * These IDs are tied to the AdMob app ca-app-pub-4371366555693080~9235421799
 * and must match the values shown in the AdMob console.
 */
object AdMobIds {
    /** Banner ad unit used on the home dashboard and other premium-upgrade banners. */
    const val BANNER_AD_UNIT_ID = "ca-app-pub-4371366555693080/8934097529"

    /** Interstitial ad unit shown on navigation between key screens for non-premium users. */
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-4371366555693080/4739701644"

    /** Rewarded ad unit used to grant free users bonus identification tokens. */
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-4371366555693080/6139655057"
}
