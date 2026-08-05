import SwiftUI

/// Home screen — the landing dashboard with quick-action tiles.
/// Mirrors the Android HomeScreen's tile grid.
struct HomeScreen: View {
    @Environment(EntitlementManager.self) private var entitlement
    @State private var specimens: [Specimen] = []
    @State private var isLoading: Bool = true
    @State private var updateService = AppUpdateService.shared

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            ScrollView {
                VStack(spacing: 24) {
                    if updateService.isUpdateAvailable {
                        updateBanner
                    }

                    heroSection

                    statsRow

                    quickActionsGrid

                    featuredSpecimensSection

                    Spacer(minLength: 24)
                }
                .padding(.horizontal, 16)
            }
        }
        .navigationTitle("RockScout")
        .navigationBarTitleDisplayMode(.large)
        .animation(.snappy, value: updateService.isUpdateAvailable)
        .task { await loadData() }
        .task { await updateService.checkForUpdate() }
    }

    // MARK: - Update banner

    /// Surfaces a newer App Store release so users with automatic updates off
    /// still land on the current build with a single tap.
    private var updateBanner: some View {
        Button {
            updateService.openAppStore()
        } label: {
            HStack(spacing: 12) {
                Image(systemName: "arrow.down.circle.fill")
                    .font(.title2)
                    .foregroundStyle(.rsAccent)

                VStack(alignment: .leading, spacing: 2) {
                    Text("Version \(updateService.availableVersion ?? "") available")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.rsText)
                    Text("Tap to update in the App Store")
                        .font(.caption)
                        .foregroundStyle(.rsTextMuted)
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.footnote.weight(.semibold))
                    .foregroundStyle(.rsTextMuted)
            }
            .padding(14)
            .background(Color.rsAccent.opacity(0.12), in: .rect(cornerRadius: 14))
            .overlay {
                RoundedRectangle(cornerRadius: 14)
                    .stroke(Color.rsAccent.opacity(0.35), lineWidth: 1)
            }
        }
        .buttonStyle(.plain)
        .accessibilityLabel("Update available. Tap to open the App Store.")
    }

    // MARK: - Hero

    private var heroSection: some View {
        VStack(spacing: 12) {
            HStack(spacing: 12) {
                Image(systemName: "diamond.fill")
                    .font(.system(size: 32))
                    .foregroundStyle(.rsAccent)
                    .symbolEffect(.pulse)

                VStack(alignment: .leading, spacing: 2) {
                    Text("Welcome back, rockhound")
                        .font(.title3.weight(.bold))
                        .foregroundStyle(.rsText)
                    Text(entitlement.effectiveIsPremium ? "Premium Member" : "Free Tier")
                        .font(.subheadline)
                        .foregroundStyle(entitlement.effectiveIsPremium ? .rsAccent : .rsTextMuted)
                }

                Spacer()

                if entitlement.effectiveIsPremium {
                    Image(systemName: "checkmark.seal.fill")
                        .foregroundStyle(.rsAccent)
                        .font(.title3)
                }
            }
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Stats

    private var statsRow: some View {
        HStack(spacing: 12) {
            StatTile(icon: "doc.text.fill", value: "\(specimens.count)", label: "Specimens", color: .rsAccent)
            StatTile(icon: "square.stack.fill", value: "—", label: "Collected", color: .rsSecondary)
            StatTile(icon: "camera.viewfinder", value: "—", label: "Identified", color: RockScoutColors.amethyst)
        }
    }

    // MARK: - Quick Actions

    private var quickActionsGrid: some View {
        VStack(alignment: .leading, spacing: 12) {
            SectionHeader(title: "Quick Actions", subtitle: "Jump to what you need")

            LazyVGrid(columns: [GridItem(.flexible(), spacing: 12), GridItem(.flexible(), spacing: 12)], spacing: 12) {
                QuickActionTile(icon: "camera.viewfinder", title: "Identify", subtitle: "AI rock scanner", color: .rsAccent)
                QuickActionTile(icon: "doc.text.fill", title: "Database", subtitle: "Browse 900+", color: .rsSecondary)
                QuickActionTile(icon: "square.stack.fill", title: "Collection", subtitle: "Your rocks", color: RockScoutColors.amethyst)
                QuickActionTile(icon: "heart.fill", title: "Wishlist", subtitle: "Want list", color: RockScoutColors.danger)
            }
        }
    }

    // MARK: - Featured Specimens

    private var featuredSpecimensSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            SectionHeader(title: "Featured Specimens", subtitle: "From the catalog")

            if isLoading {
                VStack(spacing: 12) {
                    ForEach(0..<3, id: \.self) { _ in
                        specimenSkeleton
                    }
                }
            } else if specimens.isEmpty {
                EmptyStateView(
                    icon: "doc.text",
                    title: "No specimens loaded",
                    message: "Check your connection and try again."
                )
            } else {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 12) {
                        ForEach(specimens.prefix(10)) { specimen in
                            FeaturedSpecimenCard(specimen: specimen)
                        }
                    }
                }
                .contentMargins(.horizontal, 0)
            }
        }
    }

    private var specimenSkeleton: some View {
        HStack(spacing: 12) {
            RoundedRectangle(cornerRadius: 10)
                .fill(RockScoutColors.slate700)
                .frame(width: 48, height: 48)
            VStack(alignment: .leading, spacing: 4) {
                RoundedRectangle(cornerRadius: 4)
                    .fill(RockScoutColors.slate700)
                    .frame(width: 120, height: 14)
                RoundedRectangle(cornerRadius: 4)
                    .fill(RockScoutColors.slate700)
                    .frame(width: 80, height: 10)
            }
            Spacer()
        }
        .padding(12)
        .rsCard()
        .shimmering()
    }

    // MARK: - Load

    private func loadData() async {
        do {
            specimens = try await SpecimenRepository.shared.fetchAll()
        } catch {
            specimens = []
        }
        isLoading = false
    }
}

// MARK: - Stat Tile

private struct StatTile: View {
    let icon: String
    let value: String
    let label: String
    let color: Color

    var body: some View {
        VStack(spacing: 6) {
            Image(systemName: icon)
                .font(.title3)
                .foregroundStyle(color)
            Text(value)
                .font(.headline)
                .foregroundStyle(.rsText)
            Text(label)
                .font(.caption2)
                .foregroundStyle(.rsTextMuted)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 14)
        .rsCard()
    }
}

// MARK: - Quick Action Tile

struct QuickActionTile: View {
    let icon: String
    let title: String
    let subtitle: String
    let color: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Image(systemName: icon)
                .font(.title2)
                .foregroundStyle(color)
            Text(title)
                .font(.subheadline.weight(.semibold))
                .foregroundStyle(.rsText)
            Text(subtitle)
                .font(.caption2)
                .foregroundStyle(.rsTextMuted)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(14)
        .rsCard()
    }
}

// MARK: - Featured Specimen Card

struct FeaturedSpecimenCard: View {
    let specimen: Specimen

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if !specimen.imageUrl.isEmpty, let url = URL(string: specimen.imageUrl) {
                AsyncImage(url: url) { image in
                    image.resizable().aspectRatio(contentMode: .fill)
                } placeholder: {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(RockScoutColors.slate700)
                }
                .frame(width: 140, height: 100)
                .clipShape(.rect(cornerRadius: 10))
            } else {
                RoundedRectangle(cornerRadius: 10)
                    .fill(RockScoutColors.slate700)
                    .frame(width: 140, height: 100)
                    .overlay {
                        Image(systemName: "diamond.fill")
                            .font(.title2)
                            .foregroundStyle(.rsTextMuted)
                    }
            }

            VStack(alignment: .leading, spacing: 2) {
                Text(specimen.name)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.rsText)
                    .lineLimit(1)
                Text(specimen.category)
                    .font(.caption2)
                    .foregroundStyle(.rsTextMuted)
                    .lineLimit(1)
            }
            .frame(width: 140, alignment: .leading)
        }
        .padding(10)
        .rsCard()
    }
}
