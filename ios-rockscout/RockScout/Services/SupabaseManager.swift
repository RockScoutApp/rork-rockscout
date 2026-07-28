import Foundation
import Supabase

/// Singleton Supabase client shared across the app.
/// Uses the same project as the web PWA and Android app — accounts are shared.
@MainActor
final class SupabaseManager {
    static let shared = SupabaseManager()

    let client: SupabaseClient

    private init() {
        let urlString = Config.EXPO_PUBLIC_SUPABASE_URL
        let anonKey = Config.EXPO_PUBLIC_SUPABASE_ANON_KEY

        guard let url = URL(string: urlString), !anonKey.isEmpty else {
            fatalError("Supabase URL or anon key not configured")
        }

        client = SupabaseClient(
            supabaseURL: url,
            supabaseKey: anonKey,
            options: SupabaseClientOptions(
                auth: .init(flowType: .pkce)
            )
        )
    }
}
