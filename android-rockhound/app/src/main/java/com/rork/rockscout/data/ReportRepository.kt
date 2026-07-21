package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import com.rork.rockscout.data.MockDataSeeder.LocalUserReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlinx.serialization.Serializable

/**
 * Family-friendly reporting system.
 *
 * Records reports against users. Each report is stored locally. The app shows
 * escalating warning dialogs:
 *   1 report -> warning popup (must acknowledge)
 *   2 reports -> 2-week social feature block + warning
 *   3 reports -> permanent social feature termination + warning
 *
 * Reports now carry an optional screenshot path, reporter display name, and
 * reported user display name + avatar so the Developer Console moderation
 * panel can display them without re-fetching user profiles.
 *
 * Fully self-contained: all data stored in [LocalDataStore]. No Supabase.
 */
class ReportRepository private constructor() {

    @Serializable
    data class ReportStatus(
        val reportCount: Int = 0,
        val socialBlockedUntil: Long? = null,
        val socialPermanentlyTerminated: Boolean = false,
    )

    /** Grouped moderation view for the Developer Console. */
    data class ModerationGroup(
        val reportedUserId: String,
        val reportedName: String,
        val reportedAvatar: String,
        val reportCount: Int,
        val reports: List<LocalUserReport>,
    )

    private val _myStatus = MutableStateFlow(ReportStatus())
    val myStatus: StateFlow<ReportStatus> = _myStatus.asStateFlow()

    companion object {
        val instance: ReportRepository by lazy { ReportRepository() }
    }

    /** Load the current user's report status from local storage. */
    suspend fun loadMyStatus() {
        val me = AuthRepository.instance.currentUserId ?: return
        withContext(Dispatchers.IO) {
            runCatching {
                val rows = LocalDataStore.getTable<LocalUserReport>(LocalDataStore.KEY_USER_REPORTS)
                    .filter { it.reported_user_id == me }
                val count = rows.size
                val permanent = count >= 3
                val blockedUntil = if (count >= 2 && !permanent) {
                    rows.maxOfOrNull { it.created_at }?.let { it + TimeUnit.DAYS.toMillis(14) }
                } else null
                _myStatus.value = ReportStatus(
                    reportCount = count,
                    socialBlockedUntil = blockedUntil,
                    socialPermanentlyTerminated = permanent,
                )
            }.onFailure { /* keep current status if fails */ }
        }
    }

    /** Report a user (legacy — no screenshot metadata). */
    suspend fun reportUser(reportedUserId: String, reason: String): Result<Int> =
        reportUser(reportedUserId, reason, screenshotPath = null, reporterName = null, reportedName = null, reportedAvatar = null)

    /**
     * Report a user with optional screenshot path and display names for the
     * Developer Console moderation panel.
     */
    suspend fun reportUser(
        reportedUserId: String,
        reason: String,
        screenshotPath: String?,
        reporterName: String?,
        reportedName: String?,
        reportedAvatar: String?,
    ): Result<Int> {
        val me = AuthRepository.instance.currentUserId
            ?: return Result.failure(IllegalStateException("Not signed in"))
        return withContext(Dispatchers.IO) {
            runCatching {
                val rows = LocalDataStore.getTable<LocalUserReport>(LocalDataStore.KEY_USER_REPORTS).toMutableList()
                rows.add(LocalUserReport(
                    id = "report-" + UUID.randomUUID(),
                    reporter_id = me,
                    reported_user_id = reportedUserId,
                    reason = ProfanityFilter.filter(reason),
                    status = "pending",
                    created_at = System.currentTimeMillis(),
                    screenshotPath = screenshotPath,
                    reporter_name = reporterName,
                    reported_name = reportedName,
                    reported_avatar = reportedAvatar,
                ))
                LocalDataStore.setTable(LocalDataStore.KEY_USER_REPORTS, rows)
                val newCount = rows.count { it.reported_user_id == reportedUserId }
                // Notify the reported user about the report/ban escalation.
                // No toggle — always on, instant.
                val notifType = when {
                    newCount >= 3 -> NotificationRepository.TYPE_REPORT_BAN
                    newCount >= 2 -> NotificationRepository.TYPE_REPORT_BAN
                    else -> NotificationRepository.TYPE_REPORT_BAN
                }
                val body = when {
                    newCount >= 3 -> "Your social features have been permanently terminated due to multiple reports. Tap to appeal."
                    newCount >= 2 -> "Your social features have been temporarily blocked (2 weeks) due to reports. Tap to appeal."
                    else -> "You received a report from a fellow RockScout. Tap to view details or appeal."
                }
                NotificationRepository.instance.createNotification(
                    userId = reportedUserId,
                    type = notifType,
                    actorId = me,
                    refId = reportedUserId,
                    body = body,
                    deepLinkTarget = "contact_us",
                )
                newCount
            }
        }
    }

    /** All reports grouped by reported user, for the Developer Console moderation panel. */
    suspend fun getAllModerationGroups(): List<ModerationGroup> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val rows = LocalDataStore.getTable<LocalUserReport>(LocalDataStore.KEY_USER_REPORTS)
                rows.groupBy { it.reported_user_id }
                    .map { (userId, userReports) ->
                        val latest = userReports.maxByOrNull { it.created_at }
                        ModerationGroup(
                            reportedUserId = userId,
                            reportedName = latest?.reported_name
                                ?: userReports.firstOrNull()?.reported_name
                                ?: "Unknown user",
                            reportedAvatar = latest?.reported_avatar
                                ?: userReports.firstOrNull()?.reported_avatar
                                ?: "\uD83D\uDC64",
                            reportCount = userReports.size,
                            reports = userReports.sortedByDescending { it.created_at },
                        )
                    }
                    .sortedByDescending { it.reportCount }
            }.getOrDefault(emptyList())
        }
    }

    /**
     * Remove a single report by its ID. Used when an admin reviews a report
     * and determines it's not a reportable offense — the report is removed
     * without clearing all reports for that user. The 3-strike status
     * recalculates automatically on the next [loadMyStatus] call.
     */
    suspend fun removeReport(reportId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val rows = LocalDataStore.getTable<LocalUserReport>(LocalDataStore.KEY_USER_REPORTS)
                val remaining = rows.filterNot { it.id == reportId }
                val removed = remaining.size < rows.size
                if (removed) {
                    LocalDataStore.setTable(LocalDataStore.KEY_USER_REPORTS, remaining)
                }
                removed
            }
        }
    }

    /**
     * Reinstate a user — clears all reports for [reportedUserId], restoring
     * their social access. Used from the Developer Console moderation panel.
     */
    suspend fun reinstateUser(reportedUserId: String): Result<Int> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val rows = LocalDataStore.getTable<LocalUserReport>(LocalDataStore.KEY_USER_REPORTS)
                val remaining = rows.filter { it.reported_user_id != reportedUserId }
                val removed = rows.size - remaining.size
                LocalDataStore.setTable(LocalDataStore.KEY_USER_REPORTS, remaining)
                removed
            }
        }
    }

    /** Whether the current user is currently blocked from social features. */
    fun isSocialBlocked(): Boolean {
        val status = _myStatus.value
        if (status.socialPermanentlyTerminated) return true
        val until = status.socialBlockedUntil ?: return false
        return System.currentTimeMillis() < until
    }
}
