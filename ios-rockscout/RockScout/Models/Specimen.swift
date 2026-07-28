import Foundation

/// Broad geological classification of a specimen.
enum RockClass: String, Codable, CaseIterable, Sendable {
    case igneous
    case sedimentary
    case metamorphic
    case mineral
    case crystal
    case fossil

    var label: String {
        switch self {
        case .igneous: "Igneous"
        case .sedimentary: "Sedimentary"
        case .metamorphic: "Metamorphic"
        case .mineral: "Mineral"
        case .crystal: "Crystal / Gem"
        case .fossil: "Fossil"
        }
    }

    var icon: String {
        switch self {
        case .igneous: "flame.fill"
        case .sedimentary: "mountain.2.fill"
        case .metamorphic: "circle.hexagongrid.fill"
        case .mineral: "diamond.fill"
        case .crystal: "sparkles"
        case .fossil: "fossil.shell.fill"
        }
    }

    var color: Color {
        switch self {
        case .igneous: RockScoutColors.igneous
        case .sedimentary: RockScoutColors.sedimentary
        case .metamorphic: RockScoutColors.metamorphic
        case .mineral: RockScoutColors.copper
        case .crystal: RockScoutColors.amethyst
        case .fossil: RockScoutColors.fossil
        }
    }
}

/// A rock, mineral, or crystal entry from the Supabase `specimen_catalog` table.
struct Specimen: Identifiable, Codable, Sendable, Hashable {
    let id: String
    let name: String
    let category: String
    let tagline: String
    let colors: String
    let hardness: String
    let luster: String
    let crystalSystem: String
    let streak: String
    let rarity: String
    let imageUrl: String
    let description: String?
    let formation: String?
    let whereFound: String?
    let createdAt: String?

    enum CodingKeys: String, CodingKey {
        case id, name, category, tagline, colors, hardness, luster, rarity
        case crystalSystem = "crystal_system"
        case streak
        case imageUrl = "image_url"
        case description, formation
        case whereFound = "where_found"
        case createdAt = "created_at"
    }

    /// True when this entry was added to the catalog within the last 7 days.
    var isNew: Bool {
        guard let createdAt, let date = ISO8601DateFormatter().date(from: createdAt) else { return false }
        let sevenDays: TimeInterval = 7 * 24 * 60 * 60
        return Date().timeIntervalSince(date) < sevenDays
    }

    /// Parsed color list from the comma-separated `colors` field.
    var colorList: [String] {
        colors.split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
    }

    /// Parsed location list from the comma-separated `where_found` field.
    var whereFoundList: [String] {
        (whereFound ?? "").split(separator: ",").map { $0.trimmingCharacters(in: .whitespaces) }.filter { !$0.isEmpty }
    }

    /// Rarity color for badges.
    var rarityColor: Color {
        switch rarity.lowercased() {
        case let r where r.contains("rare"): RockScoutColors.citrine
        case let r where r.contains("uncommon"): RockScoutColors.copper
        default: RockScoutColors.textLow
        }
    }
}

/// A specimen identification match result from the AI vision endpoint.
struct IdentifyMatch: Identifiable, Codable, Sendable, Hashable {
    let id: String
    let name: String
    let confidence: Int
    let reasoning: String?

    enum CodingKeys: String, CodingKey {
        case id
        case name = "specimen_name"
        case confidence
        case reasoning
    }
}

/// Full identification response from the `/identify` endpoint.
struct IdentifyResponse: Codable, Sendable {
    let matches: [IdentifyMatch]
    let summary: String?
}

/// A user's collected specimen entry from `rockscout_collection`.
struct CollectionEntry: Identifiable, Codable, Sendable, Hashable {
    let id: UUID
    let userId: UUID
    let specimenId: String
    let note: String
    let foundAt: String
    let addedAt: Int
    let createdAt: String

    enum CodingKeys: String, CodingKey {
        case id
        case userId = "user_id"
        case specimenId = "specimen_id"
        case note
        case foundAt = "found_at"
        case addedAt = "added_at"
        case createdAt = "created_at"
    }
}

/// A user's wishlist entry from `rockscout_wishlist`.
struct WishlistEntry: Identifiable, Codable, Sendable, Hashable {
    let id: UUID
    let userId: UUID
    let specimenId: String
    let createdAt: String

    enum CodingKeys: String, CodingKey {
        case id
        case userId = "user_id"
        case specimenId = "specimen_id"
        case createdAt = "created_at"
    }
}

/// User profile from `rockscout_profiles`.
struct UserProfile: Codable, Sendable {
    let id: UUID
    let displayName: String
    let avatarEmoji: String
    let isPro: Bool
    let level: Int
    let xp: Int

    enum CodingKeys: String, CodingKey {
        case id
        case displayName = "display_name"
        case avatarEmoji = "avatar_emoji"
        case isPro = "is_pro"
        case level
        case xp
    }
}
