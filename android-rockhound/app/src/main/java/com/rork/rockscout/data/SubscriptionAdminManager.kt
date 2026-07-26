package com.rork.rockscout.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

/**
 * Admin-level subscription management for the Developer Console.
 *
 * Provides user search by display name or email, and manual override of a
 * user's Premium entitlement status. Because the app's social layer is
 * self-contained ([LocalDataStore]), entitlement overrides are applied
 * directly to the [LocalUser] row.
 *
 * RevenueCat remains the source of truth for the *signed-in* user's real
 * entitlement — this manager is for admin demo/moderation of mock and
 * community users in the local database.
 */
object SubscriptionAdminManager {

    @Serializable
    data class AdminUser(
        val id: String,
        val display_name: String,
        val email: String,
        val avatar_emoji: String,
        val level: Int,
        val is_premium: Boolean,
        val premium_badge: Boolean,
        val status: String,
        val home_region: String,
        val subscription_override: String? = null,
        val override_expires_at: Long? = null,
    )

    @Serializable
    data class OverrideRecord(
        val user_id: String,
        val action: String,
        val previous_premium: Boolean,
        val new_premium: Boolean,
        val admin_note: String,
        val timestamp: Long,
    )

    private const val PREFS_OVERRIDES = "sub_admin_overrides"
    private const val PREFS_LOG = "sub_admin_log"

    private val _searchResults = MutableStateFlow<List<AdminUser>>(emptyList())
    val searchResults: StateFlow<List<AdminUser>> = _searchResults.asStateFlow()

    private val _overrideLog = MutableStateFlow<List<OverrideRecord>>(emptyList())
    val overrideLog: StateFlow<List<OverrideRecord>> = _overrideLog.asStateFlow()

    private var initialized = false

    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        loadOverrideLog(context)
    }

    /**
     * Search all local users by display name or email (case-insensitive).
     * Empty query returns all users.
     */
    suspend fun searchUsers(query: String): List<AdminUser> {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val overrides = loadOverridesMap()
            val q = query.trim().lowercase()
            val filtered = if (q.isEmpty()) users else users.filter {
                it.display_name.lowercase().contains(q) ||
                it.email.lowercase().contains(q) ||
                it.id.lowercase().contains(q)
            }
            val adminUsers = filtered.map { u ->
                val ov = overrides[u.id]
                AdminUser(
                    id = u.id,
                    display_name = u.display_name,
                    email = u.email,
                    avatar_emoji = u.avatar_emoji,
                    level = u.level,
                    is_premium = u.is_premium,
                    premium_badge = u.premium_badge,
                    status = u.status,
                    home_region = u.home_region,
                    subscription_override = ov?.action,
                    override_expires_at = ov?.timestamp,
                )
            }.sortedByDescending { it.is_premium }
            _searchResults.value = adminUsers
            adminUsers
        }.onFailure {
            Log.w("SubAdmin", "searchUsers failed", it)
            _searchResults.value = emptyList()
        }.getOrDefault(emptyList())
    }

    /**
     * Manually cancel a user's subscription by revoking Pro/Premium status.
     * Sets is_premium = false, premium_badge = false in the local users table.
     */
    suspend fun cancelSubscription(context: Context, userId: String, note: String = "Manual admin cancel"): Boolean {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS).toMutableList()
            val idx = users.indexOfFirst { it.id == userId }
            if (idx < 0) return@runCatching false
            val prev = users[idx]
            users[idx] = prev.copy(is_premium = false, premium_badge = false)
            LocalDataStore.setTable(LocalDataStore.KEY_USERS, users)
            logOverride(context, OverrideRecord(
                user_id = userId,
                action = "cancel",
                previous_premium = prev.is_premium,
                new_premium = false,
                admin_note = note,
                timestamp = System.currentTimeMillis(),
            ))
            // If this is the signed-in user, also push to PurchaseManager state.
            if (AuthRepository.instance.currentUserId == userId) {
                PurchaseManager.instance.refreshAdminOverride(isPremium = false)
            }
            true
        }.onFailure {
            Log.w("SubAdmin", "cancelSubscription failed", it)
            false
        }.getOrDefault(false)
    }

    /**
     * Manually renew/grant a user's subscription by enabling Pro status.
     * Sets is_premium = true, premium_badge = true in the local users table.
     */
    suspend fun renewSubscription(context: Context, userId: String, note: String = "Manual admin renew"): Boolean {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS).toMutableList()
            val idx = users.indexOfFirst { it.id == userId }
            if (idx < 0) return@runCatching false
            val prev = users[idx]
            users[idx] = prev.copy(is_premium = true, premium_badge = true)
            LocalDataStore.setTable(LocalDataStore.KEY_USERS, users)
            logOverride(context, OverrideRecord(
                user_id = userId,
                action = "renew",
                previous_premium = prev.is_premium,
                new_premium = true,
                admin_note = note,
                timestamp = System.currentTimeMillis(),
            ))
            // If this is the signed-in user, also push to PurchaseManager state.
            if (AuthRepository.instance.currentUserId == userId) {
                PurchaseManager.instance.refreshAdminOverride(isPremium = true)
            }
            true
        }.onFailure {
            Log.w("SubAdmin", "renewSubscription failed", it)
            false
        }.getOrDefault(false)
    }

    /**
     * Fetch a single admin user by id (for the profile screen admin buttons).
     */
    suspend fun fetchAdminUser(userId: String): AdminUser? {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val u = users.firstOrNull { it.id == userId } ?: return@runCatching null
            val overrides = loadOverridesMap()
            val ov = overrides[u.id]
            AdminUser(
                id = u.id,
                display_name = u.display_name,
                email = u.email,
                avatar_emoji = u.avatar_emoji,
                level = u.level,
                is_premium = u.is_premium,
                premium_badge = u.premium_badge,
                status = u.status,
                home_region = u.home_region,
                subscription_override = ov?.action,
                override_expires_at = ov?.timestamp,
            )
        }.onFailure {
            Log.w("SubAdmin", "fetchAdminUser failed", it)
            null
        }.getOrNull()
    }

    // ---- Subscription analytics -------------------------------------------

    /**
     * Compute aggregate subscription analytics across all local users.
     */
    suspend fun computeAnalytics(): SubAnalytics {
        return runCatching {
            val users = LocalDataStore.getTable<LocalUser>(LocalDataStore.KEY_USERS)
            val total = users.size
            val premiumCount = users.count { it.is_premium }
            val freeCount = total - premiumCount
            val conversionRate = if (total > 0) premiumCount.toDouble() / total else 0.0
            val recentCancels = _overrideLog.value.count { it.action == "cancel" }
            val recentRenews = _overrideLog.value.count { it.action == "renew" }
            SubAnalytics(
                totalUsers = total,
                premiumUsers = premiumCount,
                freeUsers = freeCount,
                conversionRate = conversionRate,
                recentCancels = recentCancels,
                recentRenews = recentRenews,
                totalOverrides = _overrideLog.value.size,
            )
        }.onFailure {
            Log.w("SubAdmin", "computeAnalytics failed", it)
            SubAnalytics()
        }.getOrDefault(SubAnalytics())
    }

    data class SubAnalytics(
        val totalUsers: Int = 0,
        val premiumUsers: Int = 0,
        val freeUsers: Int = 0,
        val conversionRate: Double = 0.0,
        val recentCancels: Int = 0,
        val recentRenews: Int = 0,
        val totalOverrides: Int = 0,
    ) {
        fun estimatedMrr(premiumPrice: Double = 5.99): Double {
            return premiumUsers * premiumPrice
        }
    }

    // ---- Override persistence ---------------------------------------------

    @Serializable
    private data class OverrideEntry(
        val user_id: String,
        val action: String,
        val timestamp: Long,
    )

    private fun loadOverridesMap(): Map<String, OverrideEntry> {
        val raw = LocalDataStore.getString(PREFS_OVERRIDES) ?: return emptyMap()
        return runCatching {
            LocalDataStore.json.decodeFromString<List<OverrideEntry>>(raw).associateBy { it.user_id }
        }.getOrDefault(emptyMap())
    }

    private fun saveOverride(context: Context, userId: String, action: String) {
        val map = loadOverridesMap().toMutableMap()
        map[userId] = OverrideEntry(user_id = userId, action = action, timestamp = System.currentTimeMillis())
        LocalDataStore.setString(PREFS_OVERRIDES, LocalDataStore.json.encodeToString(map.values.toList()))
    }

    private fun logOverride(context: Context, record: OverrideRecord) {
        val log = _overrideLog.value.toMutableList()
        log.add(record)
        if (log.size > 200) log.removeAt(0)
        _overrideLog.value = log
        LocalDataStore.setString(PREFS_LOG, LocalDataStore.json.encodeToString(log))
        saveOverride(context, record.user_id, record.action)
    }

    private fun loadOverrideLog(context: Context) {
        runCatching {
            val raw = LocalDataStore.getString(PREFS_LOG) ?: return
            _overrideLog.value = LocalDataStore.json.decodeFromString<List<OverrideRecord>>(raw)
        }
    }

    fun clearLog(context: Context) {
        _overrideLog.value = emptyList()
        LocalDataStore.setString(PREFS_LOG, "[]")
        LocalDataStore.setString(PREFS_OVERRIDES, "[]")
    }
}
