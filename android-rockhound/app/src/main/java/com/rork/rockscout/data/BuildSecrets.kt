package com.rork.rockscout.data

/**
 * Build-time secrets baked directly into the app.
 *
 * The auto-generated `com.rork.rockscout.Config` is supposed to inject
 * EXPO_PUBLIC_* env vars but in this project it regenerates empty. This
 * file provides the real values so backend calls (identify, supabase,
 * revenuecat, etc.) authenticate correctly. If Config ever gets populated
 * by the build system, those values take precedence via [resolve].
 */
internal object BuildSecrets {
    val RORK_APP_KEY: String = "rpk_munggtdkjtv3tbx5sw9ge3kebajzh39k"
    val RORK_FUNCTIONS_URL: String = "https://rockscout-finder-backend.rork.app"
    val TOOLKIT_URL: String = "https://toolkit.rork.com"
    val RORK_TOOLKIT_SECRET_KEY: String = "rork_sk_kh0hz7qm6std3qhpjinwahh056a37a5a"
    val SUPABASE_URL: String = "https://kblsiyyelyokhxaxefhy.supabase.co"
    val SUPABASE_ANON_KEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtibHNpeXllbHlva2h4YXhlZmh5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUwNjUxMjIsImV4cCI6MjEwMDY0MTEyMn0.qrJW7EH6HHcvYtprhU26dwHX0RBfZjQ9JDEObmrFDmE"
    val REVENUECAT_ANDROID_API_KEY: String = "goog_CaFgvliztBwDdFcsrcjgHzcZaEN"
    val REVENUECAT_IOS_API_KEY: String = "appl_vfaAnYfbiMkgwtUSrQqmMnWGVPe"
    val REVENUECAT_TEST_API_KEY: String = "test_ftsMKaJTfnSAjBYzcqUfhuhnKrC"
    val PROJECT_ID: String = "jvns5dfy7fpytx79a2tb3"

    /** Returns the Config value if non-empty, otherwise the baked fallback. */
    fun resolve(configKey: String, fallback: String): String {
        val fromConfig = com.rork.rockscout.Config.allValues[configKey]
        return if (!fromConfig.isNullOrEmpty()) fromConfig else fallback
    }
}
