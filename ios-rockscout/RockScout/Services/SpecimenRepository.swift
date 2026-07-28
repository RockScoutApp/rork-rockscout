import Foundation
import Supabase

/// Fetches specimens from the Supabase `specimen_catalog` table.
/// Same table the web PWA uses — shared catalog across all platforms.
@MainActor
final class SpecimenRepository {
    static let shared = SpecimenRepository()

    private var cache: [Specimen] = []

    // MARK: - Fetch all

    /// Fetch all specimens from the catalog. Caches after first load.
    func fetchAll() async throws -> [Specimen] {
        if !cache.isEmpty { return cache }

        let specimens: [Specimen] = try await SupabaseManager.shared.client
            .from("specimen_catalog")
            .select()
            .order("name", ascending: true)
            .execute()
            .value

        cache = specimens
        return specimens
    }

    /// Fetch a single specimen by ID.
    func fetch(id: String) async throws -> Specimen? {
        if let cached = cache.first(where: { $0.id == id }) {
            return cached
        }

        let specimen: Specimen? = try await SupabaseManager.shared.client
            .from("specimen_catalog")
            .select()
            .eq("id", value: id)
            .single()
            .execute()
            .value

        return specimen
    }

    /// Search specimens by name or category.
    func search(query: String) async throws -> [Specimen] {
        let all = try await fetchAll()
        guard !query.isEmpty else { return all }

        let lowered = query.lowercased()
        return all.filter {
            $0.name.lowercased().contains(lowered) ||
            $0.category.lowercased().contains(lowered) ||
            $0.tagline.lowercased().contains(lowered) ||
            $0.colors.lowercased().contains(lowered)
        }
    }

    /// Filter specimens by rarity.
    func filter(rarity: String?) async throws -> [Specimen] {
        let all = try await fetchAll()
        guard let rarity, !rarity.isEmpty else { return all }
        return all.filter { $0.rarity.lowercased().contains(rarity.lowercased()) }
    }
}
