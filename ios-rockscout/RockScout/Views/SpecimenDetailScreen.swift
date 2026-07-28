import SwiftUI

/// Specimen Detail screen — full info about a single specimen.
/// Mirrors the Android SpecimenDetailScreen.
struct SpecimenDetailScreen: View {
    let specimen: Specimen

    @Environment(EntitlementManager.self) private var entitlement
    @State private var isInCollection: Bool = false
    @State private var isInWishlist: Bool = false
    @State private var isLiked: Bool = false
    @State private var toggleInProgress: Bool = false

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            ScrollView {
                VStack(spacing: 20) {
                    heroImage

                    headerSection

                    actionButtons

                    statsSection

                    descriptionSection

                    if !specimen.colorList.isEmpty {
                        colorsSection
                    }

                    if !specimen.whereFoundList.isEmpty {
                        whereFoundSection
                    }

                    Spacer(minLength: 24)
                }
                .padding(.horizontal, 16)
            }
        }
        .navigationTitle(specimen.name)
        .navigationBarTitleDisplayMode(.inline)
        .task { await checkStatus() }
    }

    // MARK: - Hero

    private var heroImage: some View {
        Group {
            if !specimen.imageUrl.isEmpty, let url = URL(string: specimen.imageUrl) {
                Color.rsSurface
                    .frame(height: 240)
                    .overlay {
                        AsyncImage(url: url) { image in
                            image.resizable().aspectRatio(contentMode: .fill)
                        } placeholder: {
                            ProgressView().tint(.rsAccent)
                        }
                        .allowsHitTesting(false)
                    }
                    .clipShape(.rect(cornerRadius: 16))
            } else {
                Color.rsSurface
                    .frame(height: 240)
                    .overlay {
                        Image(systemName: "diamond.fill")
                            .font(.system(size: 48))
                            .foregroundStyle(.rsTextMuted)
                    }
                    .clipShape(.rect(cornerRadius: 16))
            }
        }
    }

    // MARK: - Header

    private var headerSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(spacing: 8) {
                Text(specimen.name)
                    .font(.title2.bold())
                    .foregroundStyle(.rsText)
                if specimen.isNew {
                    NewBadge()
                }
            }

            Text(specimen.tagline)
                .font(.subheadline)
                .foregroundStyle(.rsAccent)
                .italic()

            HStack(spacing: 8) {
                RarityBadge(rarity: specimen.rarity)
                if !specimen.category.isEmpty {
                    TagChip(text: specimen.category, color: RockScoutColors.amethyst)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .rsCard()
    }

    // MARK: - Actions

    private var actionButtons: some View {
        HStack(spacing: 12) {
            Button {
                Task { await toggleCollection() }
            } label: {
                VStack(spacing: 4) {
                    Image(systemName: isInCollection ? "square.stack.fill" : "square.stack")
                        .font(.title3)
                    Text(isInCollection ? "Collected" : "Collect")
                        .font(.caption)
                }
                .foregroundStyle(isInCollection ? .rsAccent : .rsTextSecondary)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
            }
            .rsCard()
            .disabled(toggleInProgress)

            Button {
                Task { await toggleWishlist() }
            } label: {
                VStack(spacing: 4) {
                    Image(systemName: isInWishlist ? "heart.fill" : "heart")
                        .font(.title3)
                    Text(isInWishlist ? "Wishlisted" : "Wishlist")
                        .font(.caption)
                }
                .foregroundStyle(isInWishlist ? RockScoutColors.danger : .rsTextSecondary)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
            }
            .rsCard()
            .disabled(toggleInProgress)

            Button {
                Task { await toggleLike() }
            } label: {
                VStack(spacing: 4) {
                    Image(systemName: isLiked ? "hand.thumbsup.fill" : "hand.thumbsup")
                        .font(.title3)
                    Text(isLiked ? "Liked" : "Like")
                        .font(.caption)
                }
                .foregroundStyle(isLiked ? .rsAccent : .rsTextSecondary)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
            }
            .rsCard()
            .disabled(toggleInProgress)
        }
    }

    // MARK: - Stats

    private var statsSection: some View {
        VStack(alignment: .leading, spacing: 4) {
            SectionHeader(title: "Properties")

            VStack(spacing: 0) {
                if !specimen.hardness.isEmpty {
                    StatRow(label: "Hardness", value: specimen.hardness, icon: "scalemass")
                    Divider().background(RockScoutColors.stoneLine.opacity(0.3))
                }
                if !specimen.luster.isEmpty {
                    StatRow(label: "Luster", value: specimen.luster, icon: "sparkles")
                    Divider().background(RockScoutColors.stoneLine.opacity(0.3))
                }
                if !specimen.streak.isEmpty {
                    StatRow(label: "Streak", value: specimen.streak, icon: "paintpalette")
                    Divider().background(RockScoutColors.stoneLine.opacity(0.3))
                }
                if !specimen.crystalSystem.isEmpty {
                    StatRow(label: "Crystal System", value: specimen.crystalSystem, icon: "cube")
                    Divider().background(RockScoutColors.stoneLine.opacity(0.3))
                }
            }
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Description

    private var descriptionSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            if let desc = specimen.description, !desc.isEmpty {
                VStack(alignment: .leading, spacing: 6) {
                    SectionHeader(title: "Description")
                    Text(desc)
                        .font(.body)
                        .foregroundStyle(.rsTextSecondary)
                }
            }

            if let formation = specimen.formation, !formation.isEmpty {
                VStack(alignment: .leading, spacing: 6) {
                    SectionHeader(title: "Formation")
                    Text(formation)
                        .font(.body)
                        .foregroundStyle(.rsTextSecondary)
                }
            }
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Colors

    private var colorsSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            SectionHeader(title: "Common Colors")
            FlowLayoutTags(items: specimen.colorList)
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Where Found

    private var whereFoundSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            SectionHeader(title: "Where Found")
            FlowLayoutTags(items: specimen.whereFoundList, color: RockScoutColors.metamorphic)
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Actions

    private func checkStatus() async {
        guard AuthManager.shared.isAuthenticated else { return }
        // Check collection/wishlist status — best-effort, non-blocking
        do {
            let collection = try await CollectionService.shared.fetchCollection()
            isInCollection = collection.contains { $0.specimenId == specimen.id }

            let wishlist = try await CollectionService.shared.fetchWishlist()
            isInWishlist = wishlist.contains { $0.specimenId == specimen.id }
        } catch {
            // Non-fatal
        }
    }

    private func toggleCollection() async {
        toggleInProgress = true
        defer { toggleInProgress = false }

        do {
            if isInCollection {
                try await CollectionService.shared.removeFromCollection(specimenId: specimen.id)
                isInCollection = false
            } else {
                try await CollectionService.shared.addToCollection(specimenId: specimen.id)
                isInCollection = true
            }
        } catch {
            // Non-fatal
        }
    }

    private func toggleWishlist() async {
        toggleInProgress = true
        defer { toggleInProgress = false }

        do {
            if isInWishlist {
                try await CollectionService.shared.removeFromWishlist(specimenId: specimen.id)
                isInWishlist = false
            } else {
                try await CollectionService.shared.addToWishlist(specimenId: specimen.id)
                isInWishlist = true
            }
        } catch {
            // Non-fatal
        }
    }

    private func toggleLike() async {
        do {
            try await CollectionService.shared.toggleLike(specimenId: specimen.id, isLiked: !isLiked)
            isLiked.toggle()
        } catch {
            // Non-fatal
        }
    }
}

// MARK: - Flow Layout Tags

struct FlowLayoutTags: View {
    let items: [String]
    var color: Color = RockScoutColors.copper

    var body: some View {
        LazyVGrid(columns: [GridItem(.adaptive(minimum: 80), spacing: 8)], spacing: 8) {
            ForEach(items, id: \.self) { item in
                TagChip(text: item, color: color)
            }
        }
    }
}
