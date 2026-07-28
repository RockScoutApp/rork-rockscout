import SwiftUI

/// Collection screen — shows the user's collected specimens and wishlist.
/// Mirrors the Android CollectionScreen + WishlistScreen in a tabbed view.
struct CollectionScreen: View {
    @State private var selectedTab: CollectionTab = .collection
    @State private var collectionEntries: [CollectionEntry] = []
    @State private var wishlistEntries: [WishlistEntry] = []
    @State private var allSpecimens: [Specimen] = []
    @State private var isLoading: Bool = true

    private enum CollectionTab: String, CaseIterable {
        case collection = "Collection"
        case wishlist = "Wishlist"
    }

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            VStack(spacing: 0) {
                segmentedControl

                if isLoading {
                    loadingView
                } else if selectedTab == .collection {
                    collectionContent
                } else {
                    wishlistContent
                }
            }
        }
        .navigationTitle("My Collection")
        .navigationBarTitleDisplayMode(.large)
        .task { await loadData() }
    }

    // MARK: - Segmented Control

    private var segmentedControl: some View {
        HStack(spacing: 0) {
            ForEach(CollectionTab.allCases, id: \.self) { tab in
                Button {
                    selectedTab = tab
                } label: {
                    VStack(spacing: 4) {
                        Text(tab.rawValue)
                            .font(.subheadline.weight(selectedTab == tab ? .semibold : .regular))
                            .foregroundStyle(selectedTab == tab ? .rsAccent : .rsTextMuted)
                        Rectangle()
                            .fill(selectedTab == tab ? .rsAccent : .clear)
                            .frame(height: 2)
                    }
                }
                .frame(maxWidth: .infinity)
            }
        }
        .background(RockScoutColors.slate900)
    }

    // MARK: - Collection

    private var collectionContent: some View {
        Group {
            if collectionEntries.isEmpty {
                EmptyStateView(
                    icon: "square.stack",
                    title: "No specimens collected",
                    message: "Browse the database and tap Collect to add specimens here."
                )
            } else {
                List(collectionEntries) { entry in
                    if let specimen = allSpecimens.first(where: { $0.id == entry.specimenId }) {
                        NavigationLink(value: specimen) {
                            CollectionRow(specimen: specimen, entry: entry)
                        }
                        .listRowBackground(Color.clear)
                        .listRowSeparator(.hidden)
                        .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 4, trailing: 16))
                    }
                }
                .listStyle(.plain)
                .scrollContentBackground(.hidden)
                .navigationDestination(for: Specimen.self) { specimen in
                    SpecimenDetailScreen(specimen: specimen)
                }
            }
        }
    }

    // MARK: - Wishlist

    private var wishlistContent: some View {
        Group {
            if wishlistEntries.isEmpty {
                EmptyStateView(
                    icon: "heart",
                    title: "Your wishlist is empty",
                    message: "Tap the heart on any specimen to add it to your wishlist."
                )
            } else {
                List(wishlistEntries) { entry in
                    if let specimen = allSpecimens.first(where: { $0.id == entry.specimenId }) {
                        NavigationLink(value: specimen) {
                            WishlistRow(specimen: specimen)
                        }
                        .listRowBackground(Color.clear)
                        .listRowSeparator(.hidden)
                        .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 4, trailing: 16))
                    }
                }
                .listStyle(.plain)
                .scrollContentBackground(.hidden)
                .navigationDestination(for: Specimen.self) { specimen in
                    SpecimenDetailScreen(specimen: specimen)
                }
            }
        }
    }

    // MARK: - Loading

    private var loadingView: some View {
        VStack(spacing: 12) {
            ProgressView().tint(.rsAccent)
            Text("Loading your collection...")
                .font(.subheadline)
                .foregroundStyle(.rsTextMuted)
        }
        .frame(maxHeight: .infinity)
    }

    // MARK: - Data

    private func loadData() async {
        guard AuthManager.shared.isAuthenticated else {
            isLoading = false
            return
        }

        do {
            allSpecimens = try await SpecimenRepository.shared.fetchAll()
            collectionEntries = try await CollectionService.shared.fetchCollection()
            wishlistEntries = try await CollectionService.shared.fetchWishlist()
        } catch {
            // Non-fatal
        }
        isLoading = false
    }
}

// MARK: - Rows

private struct CollectionRow: View {
    let specimen: Specimen
    let entry: CollectionEntry

    var body: some View {
        HStack(spacing: 12) {
            if !specimen.imageUrl.isEmpty, let url = URL(string: specimen.imageUrl) {
                AsyncImage(url: url) { image in
                    image.resizable().aspectRatio(contentMode: .fill)
                } placeholder: {
                    RoundedRectangle(cornerRadius: 8).fill(RockScoutColors.slate700)
                }
                .frame(width: 56, height: 56)
                .clipShape(.rect(cornerRadius: 8))
            } else {
                RoundedRectangle(cornerRadius: 8)
                    .fill(RockScoutColors.slate700)
                    .frame(width: 56, height: 56)
                    .overlay {
                        Image(systemName: "diamond.fill")
                            .foregroundStyle(.rsTextMuted)
                    }
            }

            VStack(alignment: .leading, spacing: 3) {
                Text(specimen.name)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.rsText)
                    .lineLimit(1)
                if !entry.note.isEmpty {
                    Text(entry.note)
                        .font(.caption)
                        .foregroundStyle(.rsTextSecondary)
                        .lineLimit(2)
                } else {
                    Text(specimen.category)
                        .font(.caption)
                        .foregroundStyle(.rsTextMuted)
                        .lineLimit(1)
                }
            }

            Spacer()

            Image(systemName: "square.stack.fill")
                .foregroundStyle(.rsAccent)
                .font(.caption)
        }
        .padding(12)
        .rsCard()
    }
}

private struct WishlistRow: View {
    let specimen: Specimen

    var body: some View {
        HStack(spacing: 12) {
            if !specimen.imageUrl.isEmpty, let url = URL(string: specimen.imageUrl) {
                AsyncImage(url: url) { image in
                    image.resizable().aspectRatio(contentMode: .fill)
                } placeholder: {
                    RoundedRectangle(cornerRadius: 8).fill(RockScoutColors.slate700)
                }
                .frame(width: 56, height: 56)
                .clipShape(.rect(cornerRadius: 8))
            } else {
                RoundedRectangle(cornerRadius: 8)
                    .fill(RockScoutColors.slate700)
                    .frame(width: 56, height: 56)
                    .overlay {
                        Image(systemName: "diamond.fill")
                            .foregroundStyle(.rsTextMuted)
                    }
            }

            VStack(alignment: .leading, spacing: 3) {
                Text(specimen.name)
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.rsText)
                    .lineLimit(1)
                Text(specimen.category)
                    .font(.caption)
                    .foregroundStyle(.rsTextMuted)
                    .lineLimit(1)
            }

            Spacer()

            Image(systemName: "heart.fill")
                .foregroundStyle(RockScoutColors.danger)
                .font(.caption)
        }
        .padding(12)
        .rsCard()
    }
}
