import SwiftUI

/// Specimen Database screen — browse and search the full 900+ catalog.
/// Mirrors the Android SpecimenListScreen.
struct SpecimenDatabaseScreen: View {
    @State private var specimens: [Specimen] = []
    @State private var filtered: [Specimen] = []
    @State private var searchText: String = ""
    @State private var selectedRarity: String? = nil
    @State private var isLoading: Bool = true
    @State private var loadError: String?

    private let rarityFilters: [String] = ["Common", "Uncommon", "Rare"]

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            VStack(spacing: 0) {
                if isLoading {
                    loadingView
                } else if let loadError {
                    errorView(loadError)
                } else if filtered.isEmpty {
                    emptyView
                } else {
                    filterChips
                    specimenList
                }
            }
        }
        .navigationTitle("Database")
        .navigationBarTitleDisplayMode(.large)
        .searchable(text: $searchText, prompt: "Search 900+ specimens")
        .onChange(of: searchText) { _, _ in applyFilters() }
        .task { await loadData() }
    }

    // MARK: - Filter Chips

    private var filterChips: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(rarityFilters, id: \.self) { rarity in
                    let isSelected = selectedRarity == rarity
                    Button {
                        selectedRarity = isSelected ? nil : rarity
                        applyFilters()
                    } label: {
                        Text(rarity)
                            .font(.caption.weight(.semibold))
                            .foregroundStyle(isSelected ? .ink : .rsTextSecondary)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background {
                                if isSelected {
                                    RockScoutColors.citrine
                                } else {
                                    RockScoutColors.slate800
                                }
                            }
                            .clipShape(.capsule)
                    }
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
        }
    }

    // MARK: - List

    private var specimenList: some View {
        List(filtered) { specimen in
            NavigationLink(value: specimen) {
                SpecimenRow(specimen: specimen)
            }
            .listRowBackground(Color.clear)
            .listRowSeparator(.hidden)
            .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 4, trailing: 16))
        }
        .listStyle(.plain)
        .scrollContentBackground(.hidden)
        .navigationDestination(for: Specimen.self) { specimen in
            SpecimenDetailScreen(specimen: specimen)
        }
    }

    // MARK: - States

    private var loadingView: some View {
        VStack(spacing: 12) {
            ProgressView().tint(.rsAccent)
            Text("Loading 900+ specimens...")
                .font(.subheadline)
                .foregroundStyle(.rsTextMuted)
        }
        .frame(maxHeight: .infinity)
    }

    private func errorView(_ message: String) -> some View {
        EmptyStateView(
            icon: "wifi.exclamationmark",
            title: "Couldn't load specimens",
            message: message
        )
        .frame(maxHeight: .infinity)
    }

    private var emptyView: some View {
        EmptyStateView(
            icon: "magnifyingglass",
            title: "No specimens found",
            message: "Try a different search term or filter."
        )
        .frame(maxHeight: .infinity)
    }

    // MARK: - Data

    private func loadData() async {
        do {
            specimens = try await SpecimenRepository.shared.fetchAll()
            filtered = specimens
        } catch {
            loadError = error.localizedDescription
        }
        isLoading = false
    }

    private func applyFilters() {
        var result = specimens

        if let rarity = selectedRarity {
            result = result.filter { $0.rarity.lowercased().contains(rarity.lowercased()) }
        }

        if !searchText.isEmpty {
            let lowered = searchText.lowercased()
            result = result.filter {
                $0.name.lowercased().contains(lowered) ||
                $0.category.lowercased().contains(lowered) ||
                $0.tagline.lowercased().contains(lowered) ||
                $0.colors.lowercased().contains(lowered)
            }
        }

        filtered = result
    }
}

// MARK: - Specimen Row

private struct SpecimenRow: View {
    let specimen: Specimen

    var body: some View {
        HStack(spacing: 12) {
            // Thumbnail
            if !specimen.imageUrl.isEmpty, let url = URL(string: specimen.imageUrl) {
                AsyncImage(url: url) { image in
                    image.resizable().aspectRatio(contentMode: .fill)
                } placeholder: {
                    RoundedRectangle(cornerRadius: 8)
                        .fill(RockScoutColors.slate700)
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

            // Info
            VStack(alignment: .leading, spacing: 3) {
                HStack(spacing: 6) {
                    Text(specimen.name)
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.rsText)
                        .lineLimit(1)
                    if specimen.isNew {
                        NewBadge()
                    }
                }
                Text(specimen.category)
                    .font(.caption)
                    .foregroundStyle(.rsTextMuted)
                    .lineLimit(1)
                HStack(spacing: 6) {
                    RarityBadge(rarity: specimen.rarity)
                    if !specimen.hardness.isEmpty {
                        TagChip(text: "Mohs \(specimen.hardness)", color: RockScoutColors.copper)
                    }
                }
            }

            Spacer()

            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundStyle(.rsTextMuted)
        }
        .padding(12)
        .rsCard()
    }
}
