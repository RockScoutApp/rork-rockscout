import Foundation
import Supabase

/// Manages user collection and wishlist via Supabase tables.
/// Mirrors the Android `LocalDataStore` sync layer — same tables, shared data.
@MainActor
final class CollectionService {
    static let shared = CollectionService()

    // MARK: - Collection

    /// Fetch the current user's collected specimens.
    func fetchCollection() async throws -> [CollectionEntry] {
        guard let userId = AuthManager.shared.currentUserId else { return [] }

        let entries: [CollectionEntry] = try await SupabaseManager.shared.client
            .from("rockscout_collection")
            .select()
            .eq("user_id", value: userId.uuidString)
            .order("created_at", ascending: false)
            .execute()
            .value

        return entries
    }

    /// Add a specimen to the collection.
    func addToCollection(specimenId: String, note: String = "", foundAt: String = "") async throws {
        guard let userId = AuthManager.shared.currentUserId else { return }

        let body: [String: Any] = [
            "user_id": userId.uuidString,
            "specimen_id": specimenId,
            "note": note,
            "found_at": foundAt,
            "added_at": Int(Date().timeIntervalSince1970 * 1000),
        ]

        try await SupabaseManager.shared.client
            .from("rockscout_collection")
            .insert(body)
            .execute()
    }

    /// Remove a specimen from the collection.
    func removeFromCollection(specimenId: String) async throws {
        guard let userId = AuthManager.shared.currentUserId else { return }

        try await SupabaseManager.shared.client
            .from("rockscout_collection")
            .delete()
            .eq("user_id", value: userId.uuidString)
            .eq("specimen_id", value: specimenId)
            .execute()
    }

    /// Update the note on a collection entry.
    func updateNote(specimenId: String, note: String) async throws {
        guard let userId = AuthManager.shared.currentUserId else { return }

        try await SupabaseManager.shared.client
            .from("rockscout_collection")
            .update(["note": note])
            .eq("user_id", value: userId.uuidString)
            .eq("specimen_id", value: specimenId)
            .execute()
    }

    // MARK: - Wishlist

    /// Fetch the current user's wishlist.
    func fetchWishlist() async throws -> [WishlistEntry] {
        guard let userId = AuthManager.shared.currentUserId else { return [] }

        let entries: [WishlistEntry] = try await SupabaseManager.shared.client
            .from("rockscout_wishlist")
            .select()
            .eq("user_id", value: userId.uuidString)
            .order("created_at", ascending: false)
            .execute()
            .value

        return entries
    }

    /// Add a specimen to the wishlist.
    func addToWishlist(specimenId: String) async throws {
        guard let userId = AuthManager.shared.currentUserId else { return }

        let body: [String: Any] = [
            "user_id": userId.uuidString,
            "specimen_id": specimenId,
        ]

        try await SupabaseManager.shared.client
            .from("rockscout_wishlist")
            .insert(body)
            .execute()
    }

    /// Remove a specimen from the wishlist.
    func removeFromWishlist(specimenId: String) async throws {
        guard let userId = AuthManager.shared.currentUserId else { return }

        try await SupabaseManager.shared.client
            .from("rockscout_wishlist")
            .delete()
            .eq("user_id", value: userId.uuidString)
            .eq("specimen_id", value: specimenId)
            .execute()
    }

    // MARK: - Liked Specimens

    /// Toggle like status on a specimen.
    func toggleLike(specimenId: String, isLiked: Bool) async throws {
        guard let userId = AuthManager.shared.currentUserId else { return }

        if isLiked {
            let body: [String: Any] = [
                "user_id": userId.uuidString,
                "specimen_id": specimenId,
            ]
            try await SupabaseManager.shared.client
                .from("rockscout_liked_specimens")
                .insert(body)
                .execute()
        } else {
            try await SupabaseManager.shared.client
                .from("rockscout_liked_specimens")
                .delete()
                .eq("user_id", value: userId.uuidString)
                .eq("specimen_id", value: specimenId)
                .execute()
        }
    }
}
