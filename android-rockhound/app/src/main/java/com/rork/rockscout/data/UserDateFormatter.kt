package com.rork.rockscout.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Centralized date formatting for all user-facing timestamps.
 * Every format reads [UserTimezoneProvider.effectiveTimeZone] at call time
 * so a timezone change (GPS update, profile region change) is picked up
 * automatically on the next composition/render.
 *
 * Replaces inline `SimpleDateFormat(..., Locale.getDefault())` calls that
 * left `timeZone` unset and fell back to the device's possibly-wrong
 * system timezone.
 */
object UserDateFormatter {

    /** "MMM d, yyyy" — e.g. "Jul 28, 2026" */
    fun formatDate(millis: Long): String =
        format(millis, "MMM d, yyyy")

    /** "MMM d, yyyy 'at' h:mm a" — e.g. "Jul 28, 2026 at 3:45 PM" */
    fun formatDateTime(millis: Long): String =
        format(millis, "MMM d, yyyy 'at' h:mm a")

    /** "h:mm a" — e.g. "3:45 PM" */
    fun formatTime(millis: Long): String =
        format(millis, "h:mm a")

    /** "EEE, MMM d" — e.g. "Mon, Jul 28" */
    fun formatMonthDay(millis: Long): String =
        format(millis, "EEE, MMM d")

    /** "EEEE, MMM d, yyyy" — e.g. "Monday, July 28, 2026" */
    fun formatFullWeekday(millis: Long): String =
        format(millis, "EEEE, MMM d, yyyy")

    /** "MMMM yyyy" — e.g. "July 2026" */
    fun formatMonthYear(millis: Long): String =
        format(millis, "MMMM yyyy")

    /** "MMM d" — e.g. "Jul 28" */
    fun formatShortDate(millis: Long): String =
        format(millis, "MMM d")

    /** "MM/dd/yyyy HH:mm" — e.g. "07/28/2026 15:45" (Dev Console) */
    fun formatDevConsoleTimestamp(millis: Long): String =
        format(millis, "MM/dd/yyyy HH:mm")

    /** "EEE, MMM d, yyyy" — e.g. "Mon, Jul 28, 2026" (trips) */
    fun formatTripDate(millis: Long): String =
        format(millis, "EEE, MMM d, yyyy")

    /** "yyyyMMdd_HHmm" — e.g. "20260728_1545" (file stamps) */
    fun formatFileStamp(millis: Long): String =
        format(millis, "yyyyMMdd_HHmm")

    /**
     * Core formatter — creates a new [SimpleDateFormat] with the user's
     * effective timezone on each call. SimpleDateFormat is not thread-safe
     * and is cheap to construct, so this is safe and efficient.
     */
    private fun format(millis: Long, pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = UserTimezoneProvider.effectiveTimeZone.value
        return sdf.format(Date(millis))
    }

    /** Direct access to the effective timezone for callers that need it. */
    val effectiveTimeZone: java.util.TimeZone
        get() = UserTimezoneProvider.effectiveTimeZone.value

    /** Effective [java.time.ZoneId] for APIs like ZonedDateTime. */
    val effectiveZoneId: java.time.ZoneId
        get() = UserTimezoneProvider.effectiveZoneId
}
