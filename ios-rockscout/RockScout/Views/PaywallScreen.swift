import SwiftUI
import RevenueCat

/// Paywall screen — RevenueCat-powered subscription purchase.
/// Mirrors the Android PaywallScreen.
struct PaywallScreen: View {
    @Environment(EntitlementManager.self) private var entitlement
    @Environment(\.dismiss) private var dismiss

    @State private var isPurchasing: Bool = false
    @State private var purchaseMessage: String?
    @State private var selectedPackage: Package?
    @State private var showRestoreAlert: Bool = false
    @State private var restoreResult: String?

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            ScrollView {
                VStack(spacing: 24) {
                    headerSection

                    premiumFeaturesSection

                    ageSafetyBanner

                    pricingCards

                    restoreButton

                    termsSection

                    Spacer(minLength: 16)
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)
            }
        }
        .navigationTitle("Go Premium")
        .navigationBarTitleDisplayMode(.inline)
        .alert("Restore Purchases", isPresented: $showRestoreAlert) {
            Button("OK") { showRestoreAlert = false }
        } message: {
            Text(restoreResult ?? "")
        }
        .task {
            if entitlement.currentOffering == nil {
                await entitlement.fetchOfferings()
            }
            selectedPackage = entitlement.monthlyPackage
        }
    }

    // MARK: - Header

    private var headerSection: some View {
        VStack(spacing: 12) {
            Image(systemName: "crown.fill")
                .font(.system(size: 48))
                .foregroundStyle(.rsAccent)
                .symbolEffect(.pulse)

            Text("RockScout Premium")
                .font(.title.bold())
                .foregroundStyle(.rsText)

            Text("5-source AI Rock ID · unlimited everything")
                .font(.subheadline)
                .foregroundStyle(.rsTextSecondary)
                .multilineTextAlignment(.center)
        }
        .padding(.top, 16)
    }

    // MARK: - Features

    private var premiumFeaturesSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            SectionHeader(title: "Premium Features")

            VStack(spacing: 10) {
                FeatureRow(icon: "camera.viewfinder", text: "Unlimited 5-source AI rock identification")
                FeatureRow(icon: "magnifyingglass", text: "Ultra-advanced search filters")
                FeatureRow(icon: "internaldrive", text: "Unlimited storage & 2GB offline cache")
                FeatureRow(icon: "square.stack.fill", text: "Unlimited collection & wishlist")
                FeatureRow(icon: "heart.fill", text: "Ad-free experience")
                FeatureRow(icon: "doc.text.fill", text: "PDF specimen reports")
                FeatureRow(icon: "icloud.fill", text: "Cloud sync across all devices")
            }
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Pricing

    private var pricingCards: some View {
        VStack(spacing: 12) {
            if let monthly = entitlement.monthlyPackage {
                PricingCard(
                    package: monthly,
                    title: "Monthly",
                    icon: "calendar",
                    isSelected: selectedPackage?.identifier == monthly.identifier
                ) {
                    selectedPackage = monthly
                }
            }

            if let annual = entitlement.annualPackage {
                PricingCard(
                    package: annual,
                    title: "Annual",
                    icon: "star.fill",
                    subtitle: "Best value",
                    isSelected: selectedPackage?.identifier == annual.identifier
                ) {
                    selectedPackage = annual
                }
            }

            if entitlement.monthlyPackage == nil && entitlement.annualPackage == nil {
                VStack(spacing: 8) {
                    ProgressView().tint(.rsAccent)
                    Text("Loading pricing...")
                        .font(.subheadline)
                        .foregroundStyle(.rsTextMuted)
                }
                .padding(32)
            }

            // Purchase button
            Button {
                Task { await purchase() }
            } label: {
                HStack {
                    if isPurchasing {
                        ProgressView().tint(.ink)
                    }
                    Text("Subscribe Now")
                        .fontWeight(.semibold)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
            }
            .buttonStyle(.rockScoutPrimary)
            .disabled(selectedPackage == nil || isPurchasing)

            if let message = purchaseMessage {
                Text(message)
                    .font(.caption)
                    .foregroundStyle(.rsTextSecondary)
                    .multilineTextAlignment(.center)
            }
        }
    }

    // MARK: - Restore

    private var restoreButton: some View {
        Button {
            Task { await restore() }
        } label: {
            Text("Restore Purchases")
                .font(.subheadline)
                .foregroundStyle(.rsAccent)
        }
    }

    // MARK: - Terms

    private var ageSafetyBanner: some View {
        HStack(spacing: 10) {
            Image(systemName: "shield.fill")
                .foregroundStyle(.rsSuccess)
            VStack(alignment: .leading, spacing: 2) {
                Text("Free is recommended for everyone.")
                    .font(.subheadline.weight(.bold))
                    .foregroundStyle(.rsText)
                Text("Premium is recommended for users 18+ because it unlocks the social layer (friends, messaging, trade, community). Safety is the first, second, and third rule.")
                    .font(.caption)
                    .foregroundStyle(.rsTextSecondary)
            }
        }
        .padding(16)
        .rsCard()
    }

    private var termsSection: some View {
        VStack(spacing: 4) {
            Text("Payment charged to your Apple ID. Subscriptions auto-renew unless cancelled at least 24 hours before the period ends. Manage in Settings > Apple ID > Subscriptions.")
                .font(.caption2)
                .foregroundStyle(.rsTextMuted)
                .multilineTextAlignment(.center)

            HStack(spacing: 16) {
                Link("Terms of Use", destination: URL(string: "https://rockscout.app/terms")!)
                    .font(.caption2)
                    .foregroundStyle(.rsTextMuted)
                Link("Privacy Policy", destination: URL(string: "https://rockscout.app/privacy")!)
                    .font(.caption2)
                    .foregroundStyle(.rsTextMuted)
            }
        }
        .padding(.horizontal, 8)
    }

    // MARK: - Actions

    private func purchase() async {
        guard let package = selectedPackage else { return }
        isPurchasing = true
        purchaseMessage = nil
        defer { isPurchasing = false }

        let result = await entitlement.purchase(package: package)
        switch result {
        case .success:
            purchaseMessage = "Premium unlocked! Enjoy RockScout."
            dismiss()
        case .cancelled:
            purchaseMessage = nil
        case .noPurchases:
            purchaseMessage = "No purchases found."
        case .error(let message):
            purchaseMessage = message
        }
    }

    private func restore() async {
        let result = await entitlement.restorePurchases()
        switch result {
        case .success:
            restoreResult = "Premium restored successfully!"
            showRestoreAlert = true
            dismiss()
        case .noPurchases:
            restoreResult = "No previous purchases found."
            showRestoreAlert = true
        case .error(let message):
            restoreResult = message
            showRestoreAlert = true
        case .cancelled:
            break
        }
    }
}

// MARK: - Feature Row

private struct FeatureRow: View {
    let icon: String
    let text: String

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.body)
                .foregroundStyle(.rsAccent)
                .frame(width: 24)
            Text(text)
                .font(.subheadline)
                .foregroundStyle(.rsText)
            Spacer()
            Image(systemName: "checkmark.circle.fill")
                .foregroundStyle(.rsSuccess)
                .font(.caption)
        }
    }
}

// MARK: - Pricing Card

private struct PricingCard: View {
    let package: Package
    let title: String
    let icon: String
    var subtitle: String?
    let isSelected: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                Image(systemName: icon)
                    .font(.title3)
                    .foregroundStyle(isSelected ? .rsAccent : .rsTextMuted)

                VStack(alignment: .leading, spacing: 2) {
                    HStack(spacing: 6) {
                        Text(title)
                            .font(.subheadline.weight(.semibold))
                            .foregroundStyle(.rsText)
                        if let subtitle {
                            Text(subtitle)
                                .font(.caption2.weight(.bold))
                                .foregroundStyle(.ink)
                                .padding(.horizontal, 6)
                                .padding(.vertical, 2)
                                .background(.rsAccent, in: .capsule)
                        }
                    }
                    if let priceString = package.storeProduct.priceString {
                        Text(priceString)
                            .font(.caption)
                            .foregroundStyle(.rsTextSecondary)
                    }
                }

                Spacer()

                Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                    .font(.title3)
                    .foregroundStyle(isSelected ? .rsAccent : .rsTextMuted)
            }
            .padding(16)
            .rsCard()
            .overlay(
                .rect(cornerRadius: 14)
                    .stroke(isSelected ? RockScoutColors.citrine : .clear, lineWidth: 2)
            )
        }
        .buttonStyle(.plain)
    }
}
