package com.rork.rockscout.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.rork.rockscout.MainActivity
import com.rork.rockscout.R

/**
 * Centralised helper for creating notification channels and posting notifications.
 * Used by both [UpdateCheckWorker] and [ProximityCheckWorker].
 */
object NotificationHelper {

    const val CHANNEL_UPDATES = "rockscout_updates"
    const val CHANNEL_PROXIMITY = "rockscout_proximity"
    const val CHANNEL_SOCIAL = "rockscout_social"
    const val CHANNEL_WEATHER = "rockscout_weather"
    const val CHANNEL_TRADE = "rockscout_trade"
    const val CHANNEL_ENGAGEMENT = "rockscout_engagement"
    const val CHANNEL_MODERATION = "rockscout_moderation"
    const val CHANNEL_LOCATIONS = "rockscout_locations"
    const val CHANNEL_DEVELOPER = "rockscout_developer"
    const val CHANNEL_AURORA = "rockscout_aurora"
    const val CHANNEL_OFFLINE_SYNC = "rockscout_offline_sync"

    const val NOTIF_UPDATE_ID = 1001
    const val NOTIF_DEVELOPER_PIN_ID = 1010
    const val NOTIF_PROXIMITY_BASE = 2000
    const val NOTIF_FRIEND_REQUEST_ID = 3001
    const val NOTIF_MESSAGE_ID = 3002
    const val NOTIF_SOCIAL_SUMMARY_ID = 3003
    const val NOTIF_TRADE_INTEREST_ID = 3004
    const val NOTIF_NEARBY_FRIENDS_ID = 3005
    const val NOTIF_TRADE_ACTIVITY_ID = 3006
    const val NOTIF_ENGAGEMENT_SUMMARY_ID = 3007
    const val NOTIF_MODERATION_ID = 3008
    const val NOTIF_LOCATION_APPROVED_ID = 3009
    const val NOTIF_WEATHER_BASE = 4000
    const val NOTIF_AURORA_ID = 4500
    const val NOTIF_OFFLINE_SYNC_ID = 5001

    /** Create the two notification channels. Call from Application.onCreate(). */
    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_UPDATES,
                "App Updates",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Get notified when a new version of RockScout is available."
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_PROXIMITY,
                "Nearby Spot Alerts",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Alerts when you're near a dig site, rock shop, or favorite spot."
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_SOCIAL,
                "RockScout Friends",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Friend requests, private messages, and social updates."
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_WEATHER,
                "Severe Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Instant NWS severe-weather warnings for your area: severe thunderstorm, tornado, flash flood, hurricane, tropical storm, tsunami, blizzard, winter storm, ice storm, extreme heat, extreme cold, high wind, dust storm, fire weather, red flag, smoke & air quality. Monitors your location independently."
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_TRADE,
                "Trade Activity",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Trade interest on your listings and marked-as-traded updates."
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ENGAGEMENT,
                "Post Engagement",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Hourly summary of likes, comments, and replies on your profile posts."
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_MODERATION,
                "Moderation Alerts",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Image rejections and report/ban alerts. These cannot be turned off."
                enableVibration(true)
                enableLights(true)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_LOCATIONS,
                "Location Submissions",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Approvals for your submitted dig sites and rock shops."
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_DEVELOPER,
                "Developer Access",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Instant verification codes for the developer console."
                enableVibration(true)
                enableLights(true)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_AURORA,
                "Aurora Alerts",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Notifications when the northern lights are likely visible from your location."
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_OFFLINE_SYNC,
                "Offline Sync",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Quiet nightly updates that keep your offline specimen database current while the device is charging."
                setShowBadge(false)
            }
        )
    }

    /** Whether the user has granted POST_NOTIFICATIONS permission (Android 13+). */
    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Post an "update available" notification that deep-links to the Play Store.
     */
    fun showUpdateNotification(
        context: Context,
        newVersionName: String,
        storeUrl: String,
        changelog: String,
    ) {
        if (!hasNotificationPermission(context)) return

        // Try to open the Play Store, fall back to the provided URL
        val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_UPDATE_ID,
            playStoreIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_UPDATES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("RockScout updated to v$newVersionName")
            .setContentText(changelog.take(80))
            .setStyle(NotificationCompat.BigTextStyle().bigText(changelog))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_UPDATE_ID, notification)
    }

    /**
     * Quiet notification posted by [NightlySyncWorker] after a successful nightly
     * cache refresh. Uses the low-importance Offline Sync channel so it doesn't
     * buzz the device at 2:30 AM. Tapping it opens the app.
     *
     * @param newImages  Number of new/refreshed images written this run (may be 0
     *                   if the cache was already current).
     * @param totalCached Total images now available offline.
     */
    fun showOfflineSyncCompleteNotification(
        context: Context,
        newImages: Int,
        totalCached: Int,
    ) {
        if (!hasNotificationPermission(context)) return

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_OFFLINE_SYNC_ID,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = if (newImages > 0) {
            "Offline catalog refreshed — $newImages updated"
        } else {
            "Offline catalog up to date"
        }
        val body = "$totalCached specimen + guide images available offline. RockScout is ready for the field."

        val notification = NotificationCompat.Builder(context, CHANNEL_OFFLINE_SYNC)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_OFFLINE_SYNC_ID, notification)
    }

    /**
     * Post a proximity notification for a nearby dig site or favorite spot.
     * Tapping it opens the app to that location's detail page.
     */
    fun showProximityNotification(
        context: Context,
        locationId: String,
        title: String,
        message: String,
    ) {
        if (!hasNotificationPermission(context)) return

        // Deep-link into the app at the location detail screen
        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("rockscout://location/$locationId")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            locationId.hashCode(),
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notifId = NOTIF_PROXIMITY_BASE + (locationId.hashCode() and 0x3FF)

        val notification = NotificationCompat.Builder(context, CHANNEL_PROXIMITY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(notifId, notification)
    }

    /**
     * Post an aggregate notification for nearby rock spots.
     * Tapping it opens the Locations screen so the user can see all nearby spots.
     * If the app no longer has location permission, it falls back to the sign-in screen.
     */
    fun showNearbySpotsNotification(
        context: Context,
        count: Int,
    ) {
        if (!hasNotificationPermission(context)) return

        val deepLink = if (LocationFetcher.hasPermission(context)) {
            "rockscout://locations"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_PROXIMITY_BASE,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = "There are $count rock spots near you!"
        val message = "Click to find them!"

        val notification = NotificationCompat.Builder(context, CHANNEL_PROXIMITY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_PROXIMITY_BASE, notification)
    }

    /**
     * Post a notification for pending friend requests.
     * Tapping it opens the Friends screen's Requests tab if the user is still
     * signed in, or the Sign In screen if the session was lost.
     *
     * @param count  Number of pending incoming friend requests.
     */
    fun showFriendRequestNotification(
        context: Context,
        count: Int,
    ) {
        if (!hasNotificationPermission(context)) return

        val deepLink = if (AuthRepository.instance.currentUserId != null) {
            "rockscout://friends"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_FRIEND_REQUEST_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = if (count == 1) {
            "You have a new friend request!"
        } else {
            "You have $count new friend requests!"
        }
        val message = "Tap to view and connect with fellow RockScouts!"

        val notification = NotificationCompat.Builder(context, CHANNEL_SOCIAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_FRIEND_REQUEST_ID, notification)
    }

    /**
     * Post a notification for unread private messages.
     * Tapping it opens the Messenger screen if the user is still signed in,
     * or the Sign In screen if the session was lost.
     *
     * @param count  Number of unread private messages.
     */
    fun showPrivateMessageNotification(
        context: Context,
        count: Int,
    ) {
        if (!hasNotificationPermission(context)) return

        val deepLink = if (AuthRepository.instance.currentUserId != null) {
            "rockscout://messenger"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_MESSAGE_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = if (count == 1) {
            "You have a new private message!"
        } else {
            "You have $count new private messages!"
        }
        val message = "Tap to read and reply!"

        val notification = NotificationCompat.Builder(context, CHANNEL_SOCIAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_MESSAGE_ID, notification)
    }

    /**
     * Post an instant notification when a connected friend comes within
     * the 50-mile radius. Tapping it opens the Scan screen (nearby hunters)
     * if the user is still signed in and has location permission, or the
     * Sign In screen if the session was lost.
     *
     * @param friendCount Number of connected friends currently within 50 miles.
     */
    fun showNearbyFriendsNotification(
        context: Context,
        friendCount: Int,
    ) {
        if (!hasNotificationPermission(context)) return

        val deepLink = if (AuthRepository.instance.currentUserId != null &&
            LocationFetcher.hasPermission(context)) {
            "rockscout://scan"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_NEARBY_FRIENDS_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = if (friendCount == 1) {
            "A friend is nearby!"
        } else {
            "$friendCount friends are near you!"
        }
        val message = "Tap to see who's close and start a hunt!"

        val notification = NotificationCompat.Builder(context, CHANNEL_SOCIAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_NEARBY_FRIENDS_ID, notification)
    }

    /**
     * Post a severe-weather alert notification sourced from the National Weather
     * Service API. Tapping it opens the app to the home screen.
     *
     * Uses a per-alert-ID notification slot so multiple simultaneous alerts
     * (e.g. a Tornado Warning + a Severe Thunderstorm Warning) each get their
     * own notification rather than overwriting each other.
     *
     * @param alertId  NWS alert ID (used to derive a unique notification ID).
     * @param title    Notification title (includes the event emoji).
     * @param message  Alert headline + affected area description.
     */
    fun showWeatherAlertNotification(
        context: Context,
        alertId: String,
        title: String,
        message: String,
    ) {
        if (!hasNotificationPermission(context)) return

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("rockscout://home")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alertId.hashCode(),
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notifId = NOTIF_WEATHER_BASE + (alertId.hashCode() and 0x3FF)

        val notification = NotificationCompat.Builder(context, CHANNEL_WEATHER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message.take(100))
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(notifId, notification)
    }

    /**
     * Post an aurora alert notification when Kp reaches the visibility threshold
     * for the user's latitude. Tapping it opens the app to the home screen.
     */
    fun showAuroraAlertNotification(
        context: Context,
        title: String,
        message: String,
    ) {
        if (!hasNotificationPermission(context)) return

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("rockscout://home")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_AURORA_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_AURORA)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message.take(100))
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_AURORA_ID, notification)
    }

    /**
     * Post a summary notification for other social events (new posts)
     * that don't have their own dedicated notification.
     * Tapping it opens the Friends screen if signed in, or Sign In if not.
     *
     * @param items  List of notification body texts to include in the summary.
     */
    fun showSocialSummaryNotification(
        context: Context,
        items: List<String>,
    ) {
        if (items.isEmpty()) return
        if (!hasNotificationPermission(context)) return

        val deepLink = if (AuthRepository.instance.currentUserId != null) {
            "rockscout://friends"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_SOCIAL_SUMMARY_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = if (items.size == 1) "RockScout Friends" else "${items.size} new updates from friends"
        val bigText = items.joinToString("\n") { "• $it" }.take(200)

        val notification = NotificationCompat.Builder(context, CHANNEL_SOCIAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(items.first())
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_SOCIAL_SUMMARY_ID, notification)
    }

    /**
     * Post a trade activity notification (trade interest or marked-as-traded)
     * on the dedicated trade channel. Tapping it opens My Trades if signed in.
     */
    fun showTradeActivityNotification(
        context: Context,
        title: String,
        message: String,
    ) {
        if (!hasNotificationPermission(context)) return

        val deepLink = if (AuthRepository.instance.currentUserId != null) {
            "rockscout://my_trades"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_TRADE_ACTIVITY_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TRADE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_TRADE_ACTIVITY_ID, notification)
    }

    /**
     * Post an engagement summary notification (likes/comments/replies)
     * on the engagement channel. Updates the existing notification in-place
     * by reusing the same notification ID.
     */
    fun showEngagementSummaryNotification(
        context: Context,
        likes: Int,
        comments: Int,
        replies: Int,
    ) {
        if (!hasNotificationPermission(context)) return
        if (likes == 0 && comments == 0 && replies == 0) return

        val deepLink = if (AuthRepository.instance.currentUserId != null) {
            "rockscout://notifications"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_ENGAGEMENT_SUMMARY_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val parts = mutableListOf<String>()
        if (likes > 0) parts.add("$likes new \u2764\uFE0F")
        if (comments > 0) parts.add("$comments new comments")
        if (replies > 0) parts.add("$replies replies")
        val title = "Post engagement update"
        val message = parts.joinToString(", ") + " on your profile posts"

        val notification = NotificationCompat.Builder(context, CHANNEL_ENGAGEMENT)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_ENGAGEMENT_SUMMARY_ID, notification)
    }

    /**
     * Post a moderation notification (image rejection or report/ban)
     * on the dedicated moderation channel. Always on, no toggle.
     */
    fun showModerationNotification(
        context: Context,
        title: String,
        message: String,
    ) {
        if (!hasNotificationPermission(context)) return

        val deepLink = if (AuthRepository.instance.currentUserId != null) {
            "rockscout://notifications"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_MODERATION_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MODERATION)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_MODERATION_ID, notification)
    }

    /**
     * Post an instant developer-access notification displaying the 6-digit PIN.
     * This serves as a reliable fallback when the SMS is delayed or not received.
     */
    fun showDeveloperPinNotification(
        context: Context,
        pin: String,
    ) {
        if (!hasNotificationPermission(context)) return

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("rockscout://home")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_DEVELOPER_PIN_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_DEVELOPER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("RockScout Developer PIN")
            .setContentText("Your code is $pin")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Your 6-digit developer access code is $pin. It expires in 5 minutes."))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_DEVELOPER_PIN_ID, notification)
    }

    /**
     * Post a location-approved notification when a user's submitted
     * location is auto-verified or Dev-verified.
     */
    fun showLocationApprovedNotification(
        context: Context,
        title: String,
        message: String,
    ) {
        if (!hasNotificationPermission(context)) return

        val deepLink = if (AuthRepository.instance.currentUserId != null) {
            "rockscout://locations"
        } else {
            "rockscout://sign_in"
        }

        val appIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(deepLink)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_LOCATION_APPROVED_ID,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_LOCATIONS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NOTIF_LOCATION_APPROVED_ID, notification)
    }
}
