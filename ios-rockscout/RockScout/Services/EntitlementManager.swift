import Foundation
import RevenueCat
import Observation

/// Manages RevenueCat IAP state — entitlements, offerings, purchases, and restores.
/// Mirrors the Android `PurchaseManager`. Single source of truth for premium access.
@Observable
@MainActor
final class EntitlementManager: NSObject {
    static let shared = EntitlementManager()

    // MARK: - State

    private(set) var isPremium: Bool = false

    /// Effective premium — false when device is over the 3-device limit.
    /// Use this for feature gating. Raw `isPremium` stays for billing/badges.
    var effectiveIsPremium: Bool { isPremium && !DeviceManager.shared.deviceOverLimit }
    private(set) var offerings: Offerings?
    private(set) var currentOffering: Offering?
    private(set) var isLoading: Bool = false
    private(set) var isPurchasing: Bool = false
    private(set) var purchaseMessage: String?

    // MARK: - IAP Config (matches Android IapConfig)

    enum IAPConfig {
        static let entitlementPremium = "premium"
        static let entitlementPro = "pro"
        static let packageMonthly = "premium_monthly"
        static let packageAnnual = "premium_annual"
    }

    // MARK: - Init

    private override init() {
        super.init()
    }

    // MARK: - Configure

    func configure() {
        let apiKey = AppSecrets.revenueCatIOSKey
        guard !apiKey.isEmpty else { return }

        Purchases.logLevel = .warn
        Purchases.configure(withAPIKey: apiKey)
        Purchases.shared.delegate = self

        Task { await fetchOfferings() }
    }

    /// Associate the RevenueCat user with the Supabase user ID.
    /// Called after sign-in so purchases are tied to the shared account.
    func login(userId: String) async {
        guard !userId.isEmpty else { return }
        do {
            _ = try await Purchases.shared.logIn(userId)
            await refreshCustomerInfo()
        } catch {
            // Non-fatal — entitlement sync is best-effort
        }
    }

    /// Disconnect the RevenueCat user on sign-out.
    func logout() async {
        do {
            _ = try await Purchases.shared.logOut()
        } catch {
            // Non-fatal
        }
        isPremium = false
    }

    // MARK: - Offerings

    func fetchOfferings() async {
        isLoading = true
        defer { isLoading = false }

        do {
            offerings = try await Purchases.shared.getOfferings()
            currentOffering = offerings?.current
        } catch {
            // Non-fatal — offerings may not be configured yet
        }
    }

    /// Monthly Premium package from the current offering.
    var monthlyPackage: Package? {
        currentOffering?.availablePackages.first { $0.identifier == IAPConfig.packageMonthly }
    }

    /// Annual Premium package from the current offering.
    var annualPackage: Package? {
        currentOffering?.availablePackages.first { $0.identifier == IAPConfig.packageAnnual }
    }

    // MARK: - Purchase

    func purchase(package: Package) async -> PurchaseResult {
        isPurchasing = true
        defer { isPurchasing = false }

        do {
            let result = try await Purchases.shared.purchase(package: package)
            if result.userCancelled {
                return .cancelled
            }
            await refreshCustomerInfo()
            return .success
        } catch let error as PurchasesErrorCode {
            return .error(error.localizedDescription)
        } catch {
            return .error(error.localizedDescription)
        }
    }

    // MARK: - Restore

    func restorePurchases() async -> PurchaseResult {
        do {
            _ = try await Purchases.shared.restorePurchases()
            await refreshCustomerInfo()
            return isPremium ? .success : .noPurchases
        } catch {
            return .error(error.localizedDescription)
        }
    }

    // MARK: - Customer Info

    func refreshCustomerInfo() async {
        do {
            let info = try await Purchases.shared.getCustomerInfo()
            updatePremiumStatus(from: info)
        } catch {
            // Non-fatal
        }
    }

    private func updatePremiumStatus(from info: CustomerInfo) {
        let hasPremium = info.entitlements[IAPConfig.entitlementPremium]?.isActive == true
            || info.entitlements[IAPConfig.entitlementPro]?.isActive == true
        isPremium = hasPremium
    }
}

// MARK: - PurchasesDelegate

extension EntitlementManager: @preconcurrency PurchasesDelegate {
    nonisolated func purchases(_ purchases: Purchases, receivedUpdated customerInfo: CustomerInfo) {
        Task { @MainActor in
            updatePremiumStatus(from: customerInfo)
        }
    }
}

// MARK: - Purchase Result

enum PurchaseResult: Sendable {
    case success
    case cancelled
    case noPurchases
    case error(String)
}
