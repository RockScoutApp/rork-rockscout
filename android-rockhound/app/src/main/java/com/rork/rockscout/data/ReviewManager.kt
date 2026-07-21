package com.rork.rockscout.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Manages Google Play in-app review flow with a fallback to opening the
 * Play Store listing directly. Tracks popup cooldown so the random review
 * prompt doesn't appear too frequently.
 *
 * Premium (ad-free) users never see the random popup — callers check
 * [PurchaseManager.isPremium] before calling [shouldShowRandomPrompt].
 */
class ReviewManager(private val context: Context) {

    private val playReviewManager = ReviewManagerFactory.create(context)

    /** SharedPreferences key for last random-prompt timestamp. */
    private val prefs = context.getSharedPreferences("review_prefs", Context.MODE_PRIVATE)

    /**
     * Launch the in-app review flow. Falls back to opening the Play Store
     * listing page if the Play Core API is unavailable (e.g. on devices
     * without Play Services, or during testing on the emulator).
     *
     * @param activity the current Activity (required by the Review API).
     * @return true if the in-app flow launched or the Play Store page opened.
     */
    suspend fun requestReview(activity: Activity): Boolean {
        return try {
            val reviewInfo: ReviewInfo? = suspendCancellableCoroutine { cont ->
                val task = playReviewManager.requestReviewFlow()
                task.addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        cont.resume(result.result as ReviewInfo?)
                    } else {
                        cont.resume(null)
                    }
                }
            }
            if (reviewInfo == null) return fallbackToPlayStore()

            suspendCancellableCoroutine<Unit> { cont ->
                val task = playReviewManager.launchReviewFlow(activity, reviewInfo)
                task.addOnCompleteListener { cont.resume(Unit) }
            }
            markRandomPromptShown()
            Log.d("ReviewManager", "In-app review flow launched")
            true
        } catch (e: ReviewException) {
            Log.w("ReviewManager", "Review flow error code: ${e.errorCode}")
            fallbackToPlayStore()
        } catch (e: Exception) {
            Log.w("ReviewManager", "Review flow failed: ${e.message}")
            fallbackToPlayStore()
        }
    }

    /**
     * Open the app's Play Store listing page directly.
     * Used as a fallback when the in-app review API isn't available.
     */
    private fun fallbackToPlayStore(): Boolean {
        return try {
            val packageName = context.packageName
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Play Store not installed — try the web URL
            try {
                val packageName = context.packageName
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName"),
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(intent)
                true
            } catch (e2: Exception) {
                Log.w("ReviewManager", "Could not open Play Store: ${e2.message}")
                false
            }
        }
    }

    /**
     * Whether enough time has passed since the last random review prompt.
     * Cooldown is 7 days to avoid annoying users.
     */
    fun shouldShowRandomPrompt(): Boolean {
        val lastShown = prefs.getLong(KEY_LAST_PROMPT, 0L)
        if (lastShown == 0L) return true
        val elapsed = System.currentTimeMillis() - lastShown
        return elapsed >= RANDOM_COOLDOWN_MS
    }

    /** Record that a random prompt was shown (or dismissed). */
    fun markRandomPromptShown() {
        prefs.edit().putLong(KEY_LAST_PROMPT, System.currentTimeMillis()).apply()
    }

    companion object {
        private const val KEY_LAST_PROMPT = "last_random_prompt_ts"
        private const val RANDOM_COOLDOWN_MS = 7 * 24 * 60 * 60 * 1000L // 7 days
    }
}
