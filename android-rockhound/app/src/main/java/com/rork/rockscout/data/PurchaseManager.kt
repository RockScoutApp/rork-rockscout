package com.rork.rockscout.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.revenuecat.purchases.CacheFetchPolicy
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesErrorCode
import com.revenuecat.purchases.PurchasesException
import com.revenuecat.purchases.PurchasesTransactionException
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitLogIn
import com.revenuecat.purchases.awaitLogOut
import com.revenuecat.purchases.awaitOfferings
import com.revenuecat.purchases.awaitPurchase
import com.revenuecat.purchases.awaitRestore
import com.revenuecat.purchases.models.StoreProduct
import com.revenuecat.purchases.PurchaseParams
import com.rork.rockscout.data.IapConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Result of a purchase or restore operation.
 */
sealed class PurchaseResult {
    data object Success : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
    data object Cancelled : PurchaseResult()
    data object AlreadyPurchased : PurchaseResult()
}

/**
 * Manages RevenueCat IAP state — entitlements, offerings, purchases, and restores.
 * Single source of truth for whether the user has premium access (ad-free).
 */
class PurchaseManager {

    private val scope = MainScope()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    /** True if the user has an active Premium subscription (includes legacy Pro). */
    val hasPaidAccess: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _currentOffering = MutableStateFlow<Offering?>(null)
    val currentOffering: StateFlow<Offering?> = _currentOffering.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isPurchasing = MutableStateFlow(false)
    val isPurchasing: StateFlow<Boolean> = _isPurchasing.asStateFlow()

    private val _purchaseMessage = MutableStateFlow<String?>(null)
    val purchaseMessage: StateFlow<String?> = _purchaseMessage.asStateFlow()

    /** Tracks the lifecycle of the entitlement sync to the backend. */
    enum class SyncStatus { IDLE, SYNCING, SYNCED, FAILED }

    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _entitlementSynced = MutableStateFlow(false)
    val entitlementSynced: StateFlow<Boolean> = _entitlementSynced.asStateFlow()

    /** Monthly Premium subscription package from the current offering. */
    val monthlyPackage: Package?
        get() = _currentOffering.value?.availablePackages?.find {
            it.identifier == IapConfig.PACKAGE_MONTHLY
        }

    /** Annual Premium subscription package — legacy, not shown in UI. */
    val premiumAnnualPackage: Package?
        get() = _currentOffering.value?.availablePackages?.find {
            it.identifier == IapConfig.PACKAGE_PREMIUM_ANNUAL
        }

    /** Monthly Pro subscription package — legacy, not shown in UI. */
    val proMonthlyPackage: Package?
        get() = _currentOffering.value?.availablePackages?.find {
            it.identifier == IapConfig.PACKAGE_PRO_MONTHLY
        }

    /** Annual Pro subscription package — legacy, not shown in UI. */
    val proAnnualPackage: Package?
        get() = _currentOffering.value?.availablePackages?.find {
            it.identifier == IapConfig.PACKAGE_PRO_ANNUAL
        }

    /** All token packages keyed by their identifier, sorted by token quantity. */
    val allTokenPackages: Map<String, Package>
        get() = IapConfig.ALL_TOKEN_PACKAGES.mapNotNull { id ->
            _currentOffering.value?.availablePackages?.find { it.identifier == id }
                ?.let { id to it }
        }.toMap()

    /** Fallback: first available donation package from the current offering. */
    val donationPackage: Package?
        get() = IapConfig.ALL_DONATION_PACKAGES.mapNotNull { id ->
            _currentOffering.value?.availablePackages?.find { it.identifier == id }
        }.firstOrNull()

    /** All donation packages keyed by their identifier, sorted by amount tier. */
    val allDonationPackages: Map<String, Package>
        get() = IapConfig.ALL_DONATION_PACKAGES.mapNotNull { id ->
            _currentOffering.value?.availablePackages?.find { it.identifier == id }
                ?.let { id to it }
        }.toMap()

    /**
     * Initialize RevenueCat SDK. Call from Application.onCreate().
     * Uses the production Android key if available, falls back to Test Store key.
     */
    fun initialize(context: Context) {
        // Premium-unlocked build variant: permanently force Premium entitlement on.
        // This bypasses RevenueCat entirely — the user gets all Premium features
        // without any purchase or server check.
        if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) {
            _isPremium.value = true
            Log.i("PurchaseManager", "FORCE_PREMIUM build — Premium entitlement permanently unlocked")
        }

        val apiKey = getRevenueCatApiKey()
        if (apiKey.isEmpty()) {
            Log.w("PurchaseManager", "No RevenueCat API key found — IAP disabled")
            return
        }

        Purchases.configure(
            PurchasesConfiguration.Builder(context, apiKey)
                .appUserID(null)
                .build()
        )

        Purchases.sharedInstance.updatedCustomerInfoListener = { info ->
            // Don't let RevenueCat override the FORCE_PREMIUM entitlement.
            if (!com.rork.rockscout.BuildConfig.FORCE_PREMIUM) {
                updatePremiumStatus(info)
            }
        }

        if (!com.rork.rockscout.BuildConfig.FORCE_PREMIUM) {
            refreshCustomerInfo()
        }
        fetchOfferings()
    }

    private fun updatePremiumStatus(info: CustomerInfo) {
        val premiumActive = info.entitlements[IapConfig.ENTITLEMENT_ID]?.isActive == true
        val legacyProActive = info.entitlements[IapConfig.PRO_ENTITLEMENT_ID]?.isActive == true
        _isPremium.value = premiumActive || legacyProActive
    }

    /**
     * Fetch the current customer info and update premium status.
     */
    private fun refreshCustomerInfo() {
        scope.launch {
            try {
                val info = Purchases.sharedInstance.awaitCustomerInfo(
                    fetchPolicy = CacheFetchPolicy.FETCH_CURRENT
                )
                updatePremiumStatus(info)
            } catch (e: Exception) {
                Log.w("PurchaseManager", "Failed to fetch customer info: ${e.message}")
            }
        }
    }

    /**
     * Fetch available offerings from RevenueCat.
     */
    fun fetchOfferings() {
        scope.launch {
            _isLoading.value = true
            try {
                val offerings = Purchases.sharedInstance.awaitOfferings()
                _currentOffering.value = offerings.current
            } catch (e: Exception) {
                Log.w("PurchaseManager", "Failed to fetch offerings: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Purchase a specific package. Returns a PurchaseResult.
     */
    suspend fun purchasePackage(activity: Activity, pkg: Package): PurchaseResult {
        return try {
            _isPurchasing.value = true
            val params = PurchaseParams.Builder(activity, pkg).build()
            val result = Purchases.sharedInstance.awaitPurchase(params)
            updatePremiumStatus(result.customerInfo)
            syncEntitlementToBackend()
            _purchaseMessage.value = "Purchase successful! Ads removed."
            PurchaseResult.Success
        } catch (e: PurchasesTransactionException) {
            when {
                e.userCancelled -> PurchaseResult.Cancelled
                e.error?.code == PurchasesErrorCode.ProductAlreadyPurchasedError -> {
                    _purchaseMessage.value = "You already own this product."
                    PurchaseResult.AlreadyPurchased
                }
                else -> {
                    val msg = e.error?.message ?: "Purchase failed"
                    _purchaseMessage.value = msg
                    PurchaseResult.Error(msg)
                }
            }
        } catch (e: PurchasesException) {
            val msg = e.error?.message ?: "Purchase failed"
            _purchaseMessage.value = msg
            PurchaseResult.Error(msg)
        } catch (e: Exception) {
            val msg = e.message ?: "Purchase failed"
            _purchaseMessage.value = msg
            PurchaseResult.Error(msg)
        } finally {
            _isPurchasing.value = false
        }
    }

    /**
     * Purchase the annual Premium subscription — legacy, kept for existing
     * subscribers. Not surfaced in any UI.
     */
    suspend fun purchasePremiumAnnual(activity: Activity): PurchaseResult =
        purchaseSubscription(activity, premiumAnnualPackage, "Premium annual")

    /**
     * Purchase the monthly Pro subscription — legacy, kept for existing
     * subscribers. Not surfaced in any UI.
     */
    suspend fun purchaseProMonthly(activity: Activity): PurchaseResult =
        purchaseSubscription(activity, proMonthlyPackage, "Pro monthly")

    /**
     * Purchase the annual Pro subscription — legacy, kept for existing
     * subscribers. Not surfaced in any UI.
     */
    suspend fun purchaseProAnnual(activity: Activity): PurchaseResult =
        purchaseSubscription(activity, proAnnualPackage, "Pro annual")

    /**
     * Internal helper for any subscription package (Premium monthly/annual,
     * Pro monthly/annual). Updates the corresponding entitlement flow on success.
     */
    private suspend fun purchaseSubscription(
        activity: Activity,
        pkg: Package?,
        label: String,
    ): PurchaseResult {
        if (pkg == null) {
            _purchaseMessage.value = "$label unavailable"
            return PurchaseResult.Error("$label unavailable")
        }
        return try {
            _isPurchasing.value = true
            val params = PurchaseParams.Builder(activity, pkg).build()
            val result = Purchases.sharedInstance.awaitPurchase(params)
            updatePremiumStatus(result.customerInfo)
            syncEntitlementToBackend()
            _purchaseMessage.value = "Premium active! Unlimited access unlocked."
            PurchaseResult.Success
        } catch (e: PurchasesTransactionException) {
            when {
                e.userCancelled -> PurchaseResult.Cancelled
                e.error?.code == PurchasesErrorCode.ProductAlreadyPurchasedError -> {
                    _purchaseMessage.value = "You already own this product."
                    PurchaseResult.AlreadyPurchased
                }
                else -> {
                    val msg = e.error?.message ?: "$label failed"
                    _purchaseMessage.value = msg
                    PurchaseResult.Error(msg)
                }
            }
        } catch (e: PurchasesException) {
            val msg = e.error?.message ?: "$label failed"
            _purchaseMessage.value = msg
            PurchaseResult.Error(msg)
        } catch (e: Exception) {
            val msg = e.message ?: "$label failed"
            _purchaseMessage.value = msg
            PurchaseResult.Error(msg)
        } finally {
            _isPurchasing.value = false
        }
    }

    /**
     * Purchase the monthly subscription. Grants full unlimited, ad-free access.
     */
    suspend fun purchaseMonthlySubscription(activity: Activity): PurchaseResult =
        purchaseSubscription(activity, monthlyPackage, "Premium monthly")

    /**
     * Purchase a single-use identifier token package.
     * On success, grants the corresponding number of tokens to the user's bank.
     * @param packageIdentifier one of [IapConfig.ALL_TOKEN_PACKAGES].
     */
    suspend fun purchaseTokens(activity: Activity, packageIdentifier: String): PurchaseResult {
        val pkg = allTokenPackages[packageIdentifier] ?: run {
            _purchaseMessage.value = "Token purchase unavailable"
            return PurchaseResult.Error("Token purchase unavailable")
        }
        return try {
            _isPurchasing.value = true
            val params = PurchaseParams.Builder(activity, pkg).build()
            Purchases.sharedInstance.awaitPurchase(params)
            val qty = IapConfig.TOKEN_PACKAGE_QTY[packageIdentifier] ?: 1
            IdentifyAccessManager.instance.addTokens(qty)
            _purchaseMessage.value = "$qty identifier token${if (qty > 1) "s" else ""} added to your bank!"
            PurchaseResult.Success
        } catch (e: PurchasesTransactionException) {
            when {
                e.userCancelled -> PurchaseResult.Cancelled
                else -> {
                    val msg = e.error?.message ?: "Token purchase failed"
                    _purchaseMessage.value = msg
                    PurchaseResult.Error(msg)
                }
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Token purchase failed"
            _purchaseMessage.value = msg
            PurchaseResult.Error(msg)
        } finally {
            _isPurchasing.value = false
        }
    }

    /**
     * Purchase a donation to support the developer using a custom dollar amount.
     * Maps the amount to the nearest available donation product.
     * Grants the corresponding number of identifier tokens and a tier-based
     * temporary location-monitoring unlock (1 day → 1 month depending on tier)
     * on success.
     * @param amount the user-entered dollar amount.
     */
    suspend fun purchaseDonationByAmount(activity: Activity, amount: Double): PurchaseResult {
        val option = IapConfig.nearestDonationPackage(amount)
        return purchaseDonation(activity, option.packageId)
    }

    /**
     * Purchase a donation. On success, grants identifier tokens based on the
     * donation tier and a tier-based temporary location-monitoring unlock
     * (1 day → 1 month depending on tier).
     * @param packageIdentifier the RevenueCat package identifier for the donation tier.
     */
    suspend fun purchaseDonation(activity: Activity, packageIdentifier: String = IapConfig.PACKAGE_DONATION_5): PurchaseResult {
        val pkg = allDonationPackages[packageIdentifier] ?: donationPackage ?: run {
            _purchaseMessage.value = "Donation unavailable"
            return PurchaseResult.Error("Donation unavailable")
        }
        return try {
            _isPurchasing.value = true
            val params = PurchaseParams.Builder(activity, pkg).build()
            Purchases.sharedInstance.awaitPurchase(params)
            val tokens = IapConfig.DONATION_TOKEN_GRANT[packageIdentifier] ?: 0
            val locationDays = IapConfig.donationLocationDays(packageIdentifier)
            if (tokens > 0) {
                IdentifyAccessManager.instance.addTokens(tokens)
            }
            if (locationDays > 0) {
                IdentifyAccessManager.instance.grantLocationUnlock(locationDays)
                IdentifyAccessManager.instance.grantAdFreeUnlock(locationDays)
            }
            val tokenPart = if (tokens > 0) "$tokens identifier token${if (tokens > 1) "s" else ""} added" else ""
            val locPart = if (locationDays > 0) " + location monitoring & ad-free unlocked for ${locationDurationLabel(locationDays)}" else ""
            _purchaseMessage.value = "Thank you for your support! $tokenPart$locPart".trim().replace("  +", " +")
            PurchaseResult.Success
        } catch (e: PurchasesTransactionException) {
            when {
                e.userCancelled -> PurchaseResult.Cancelled
                else -> {
                    val msg = e.error?.message ?: "Donation failed"
                    _purchaseMessage.value = msg
                    PurchaseResult.Error(msg)
                }
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Donation failed"
            _purchaseMessage.value = msg
            PurchaseResult.Error(msg)
        } finally {
            _isPurchasing.value = false
        }
    }

    /**
     * Restore previous purchases. Required by App Store review.
     * Also re-grants non-subscription purchases (donations, token packs) that
     * were lost on reinstall — prevents data loss for users who donated and
     * then got a new device or reinstalled.
     */
    suspend fun restorePurchases(): PurchaseResult {
        return try {
            _isLoading.value = true
            val info = Purchases.sharedInstance.awaitRestore()
            updatePremiumStatus(info)
            syncEntitlementToBackend()

            // Re-grant non-subscription purchases (donations + token packs).
            // Track restored IDs to avoid double-granting on repeated restores.
            val restoredIds = getRestoredPurchaseIds()
            info.nonSubscriptionTransactions.forEach { transaction ->
                val productId = transaction.productIdentifier
                if (productId in restoredIds) return@forEach

                // Check if it's a donation package.
                val donationTokens = IapConfig.DONATION_TOKEN_GRANT[productId] ?: 0
                val donationDays = IapConfig.donationLocationDays(productId)
                // Check if it's a token package.
                val tokenQty = IapConfig.TOKEN_PACKAGE_QTY[productId] ?: 0

                if (donationTokens > 0) {
                    IdentifyAccessManager.instance.addTokens(donationTokens)
                    if (donationDays > 0) {
                        IdentifyAccessManager.instance.grantLocationUnlock(donationDays)
                        IdentifyAccessManager.instance.grantAdFreeUnlock(donationDays)
                    }
                    markRestored(productId)
                } else if (tokenQty > 0) {
                    IdentifyAccessManager.instance.addTokens(tokenQty)
                    markRestored(productId)
                }
            }

            if (_isPremium.value) {
                _purchaseMessage.value = "Purchases restored! Ads removed."
                PurchaseResult.Success
            } else if (info.nonSubscriptionTransactions.isNotEmpty()) {
                _purchaseMessage.value = "Purchases restored! Token grants and unlocks have been re-applied."
                PurchaseResult.Success
            } else {
                _purchaseMessage.value = "No active purchases found."
                PurchaseResult.Error("No active purchases found")
            }
        } catch (e: Exception) {
            val msg = e.message ?: "Restore failed"
            _purchaseMessage.value = msg
            PurchaseResult.Error(msg)
        } finally {
            _isLoading.value = false
        }
    }

    /** Set of already-restored non-subscription product IDs, backed by LocalDataStore. */
    private fun getRestoredPurchaseIds(): MutableSet<String> {
        return try {
            val stored = com.rork.rockscout.data.LocalDataStore.getString("restored_purchase_ids")
            if (stored != null) {
                com.rork.rockscout.data.LocalDataStore.json.decodeFromString<List<String>>(stored).toMutableSet()
            } else {
                mutableSetOf()
            }
        } catch (e: Exception) {
            mutableSetOf()
        }
    }

    private fun markRestored(productId: String) {
        try {
            val ids = getRestoredPurchaseIds()
            ids.add(productId)
            com.rork.rockscout.data.LocalDataStore.setString(
                "restored_purchase_ids",
                com.rork.rockscout.data.LocalDataStore.json.encodeToString(ids.toList()),
            )
        } catch (e: Exception) {
            Log.w("PurchaseManager", "Failed to mark restored: ${e.message}")
        }
    }

    /**
     * Link the RevenueCat anonymous user to the app's user account.
     * Called after sign-in/sign-up so purchases are tied to the user's account
     * and carry over across devices.
     */
    suspend fun linkRevenueCatUser(userId: String) {
        if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) return
        try {
            val result = Purchases.sharedInstance.awaitLogIn(userId)
            updatePremiumStatus(result.customerInfo)
            Log.i("PurchaseManager", "RevenueCat user linked: $userId")
        } catch (e: Exception) {
            Log.w("PurchaseManager", "Failed to link RevenueCat user: ${e.message}")
        }
    }

    /**
     * Log out the RevenueCat user, reverting to anonymous mode.
     * Called on sign-out so the next user starts fresh.
     */
    suspend fun logoutRevenueCatUser() {
        if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) return
        try {
            Purchases.sharedInstance.awaitLogOut()
            _isPremium.value = false
            Log.i("PurchaseManager", "RevenueCat user logged out")
        } catch (e: Exception) {
            Log.w("PurchaseManager", "Failed to log out RevenueCat user: ${e.message}")
        }
    }

    fun clearMessage() {
        _purchaseMessage.value = null
    }

    /**
     * Push the current Premium entitlement state to the backend so the web PWA
     * sees the user's Premium status. Fire-and-forget — failures are logged
     * but never block the purchase/restore flow.
     *
     * Uses the Supabase user ID (the shared identity across Android, web, iOS)
     * from [AuthRepository]. RevenueCat is linked to the same ID via
     * [linkRevenueCatUser], so the backend can look up the entitlement by ID.
     */
    private fun syncEntitlementToBackend() {
        if (com.rork.rockscout.BuildConfig.FORCE_PREMIUM) {
            _syncStatus.value = SyncStatus.SYNCED
            _entitlementSynced.value = true
            return
        }
        val userId = AuthRepository.instance.currentUserId ?: return
        _syncStatus.value = SyncStatus.SYNCING
        scope.launch {
            val ok = EntitlementApi.syncEntitlement(userId)
            if (ok) {
                _syncStatus.value = SyncStatus.SYNCED
                _entitlementSynced.value = true
            } else {
                _syncStatus.value = SyncStatus.FAILED
            }
        }
    }

    /**
     * Apply an admin-initiated entitlement override (from the Developer Console).
     * Pushes the given Pro/Premium status into the state flows without touching
     * RevenueCat. Used by [SubscriptionAdminManager] when an admin manually
     * cancels or renews a subscription for the *signed-in* user.
     */
    fun refreshAdminOverride(isPremium: Boolean) {
        _isPremium.value = isPremium
    }

    /**
     * Get the RevenueCat API key, preferring production Android key, falling back to Test Store.
     */
    private fun getRevenueCatApiKey(): String {
        return try {
            val androidKey = BuildSecrets.resolve("EXPO_PUBLIC_REVENUECAT_ANDROID_API_KEY", BuildSecrets.REVENUECAT_ANDROID_API_KEY)
            val testKey = BuildSecrets.resolve("EXPO_PUBLIC_REVENUECAT_TEST_API_KEY", BuildSecrets.REVENUECAT_TEST_API_KEY)
            androidKey.ifEmpty { testKey }
        } catch (e: Exception) {
            BuildSecrets.REVENUECAT_ANDROID_API_KEY.ifEmpty { BuildSecrets.REVENUECAT_TEST_API_KEY }
        }
    }

    companion object {
        val instance: PurchaseManager by lazy { PurchaseManager() }

        /** Human-readable label for a location-monitoring unlock duration in days. */
        fun locationDurationLabel(days: Int): String = when (days) {
            0 -> ""
            1 -> "1 day"
            7 -> "1 week"
            14 -> "2 weeks"
            21 -> "3 weeks"
            30 -> "1 month"
            else -> "$days days"
        }
    }
}
