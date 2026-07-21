package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Merged periodic worker that runs on a **1-hour cycle** and handles:
 *
 * 1. **Engagement summary push** — aggregates unread likes/comments/replies
 *    on the user's posts into a single push notification. Updates the
 *    existing push in-place (reuses the same notification ID) so they
 *    don't stack up. Respects the engagement-summary toggle.
 * 2. **New-posts summary push** — aggregates unread new-post notifications
 *    into a single push. Cooldown is 1 hour (was previously 2 hours).
 *    Respects the new-posts toggle.
 * 3. **30-day auto-deletion sweep** — removes all notifications older
 *    than 30 days.
 *
 * Instant push types (friend requests, messages, trade interest,
 * marked-traded, location approved, image rejection, report/ban) are
 * fired immediately by [NotificationRepository] at creation time and
 * are NOT handled here.
 *
 * Replaces the former [NotificationCheckWorker].
 */
class NotificationSummaryWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "rockscout_notification_summary"
        private const val TAG = "NotificationSummaryWorker"
        private const val PREFS_NAME = "social_notif_prefs"
        private const val KEY_LAST_SUMMARY_NOTIFIED = "last_summary_notified_at"
        private const val COOLDOWN_MS = 60 * 60 * 1000L // 1 hour
    }

    override suspend fun doWork(): Result {
        val me = AuthRepository.instance.currentUserId ?: run {
            Log.d(TAG, "Not signed in — skipping")
            return Result.success()
        }
        return runCatching {
            val context = applicationContext

            NotificationHelper.createChannels(context)

            // Run the 30-day auto-deletion sweep first.
            NotificationRepository.instance.autoDeleteOldNotifications()

            // Load fresh notifications from local storage.
            NotificationRepository.instance.loadNotifications()
            val unread = NotificationRepository.instance.notifications.value
                .filter { it.read_at == null }

            if (unread.isEmpty()) {
                Log.d(TAG, "No unread notifications — skipping")
                return@runCatching Result.success()
            }

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val now = System.currentTimeMillis()
            var notificationsSent = 0

            // --- Engagement summary (likes / comments / replies) ---
            val engagementUnread = unread.filter {
                it.type in NotificationRepository.ENGAGEMENT_TYPES
            }
            if (engagementUnread.isNotEmpty() &&
                PersistenceManager.isNotifEngagementSummaryEnabled()) {
                val likes = engagementUnread.count { it.type == NotificationRepository.TYPE_POST_LIKE }
                val comments = engagementUnread.count { it.type == NotificationRepository.TYPE_POST_COMMENT }
                val replies = engagementUnread.count { it.type == NotificationRepository.TYPE_POST_REPLY }
                NotificationHelper.showEngagementSummaryNotification(
                    context = context,
                    likes = likes,
                    comments = comments,
                    replies = replies,
                )
                notificationsSent++
                Log.d(TAG, "Engagement summary sent: $likes likes, $comments comments, $replies replies")
            }

            // --- New-posts summary ---
            val newPostUnread = unread.filter {
                it.type == NotificationRepository.TYPE_NEW_POST
            }
            if (newPostUnread.isNotEmpty() &&
                PersistenceManager.isNotifNewPostsEnabled()) {
                val lastNotified = prefs.getLong(KEY_LAST_SUMMARY_NOTIFIED, 0L)
                if (now - lastNotified >= COOLDOWN_MS) {
                    NotificationHelper.showSocialSummaryNotification(
                        context = context,
                        items = newPostUnread.map { it.body },
                    )
                    prefs.edit().putLong(KEY_LAST_SUMMARY_NOTIFIED, now).apply()
                    notificationsSent++
                    Log.d(TAG, "New-posts summary sent: ${newPostUnread.size} items")
                } else {
                    Log.d(TAG, "New-posts summary on cooldown (${(now - lastNotified) / 60_000}min elapsed)")
                }
            }

            Log.d(TAG, "Summary worker complete — $notificationsSent notification(s) sent")
            Result.success()
        }.onFailure {
            Log.w(TAG, "Summary worker failed", it)
        }.getOrDefault(Result.success())
    }
}
