import Foundation
import Supabase
import Observation

/// Authentication state observable for the entire app.
/// Mirrors the Android `AuthRepository` + web `useAuth` — same Supabase project,
/// shared accounts across all platforms.
@Observable
@MainActor
final class AuthManager {
    static let shared = AuthManager()

    // MARK: - State

    private(set) var session: Session?
    private(set) var isLoading: Bool = true
    private(set) var error: String?

    var isAuthenticated: Bool { session != nil }
    var currentUserId: UUID? { session?.user.id }
    var currentUserEmail: String? { session?.user.email }

    // MARK: - Init

    private init() {
        Task { await restoreSession() }
    }

    // MARK: - Session restore

    func restoreSession() async {
        isLoading = true
        do {
            let restored = try await SupabaseManager.shared.client.auth.session
            session = restored
        } catch {
            session = nil
        }
        isLoading = false
    }

    // MARK: - Sign up

    func signUp(email: String, password: String) async throws {
        isLoading = true
        error = nil
        defer { isLoading = false }

        do {
            let response = try await SupabaseManager.shared.client.auth.signUp(
                email: email,
                password: password
            )
            if let session = response.session {
                self.session = session
            }
        } catch {
            self.error = userFriendlyError(error)
            throw error
        }
    }

    // MARK: - Sign in

    func signIn(email: String, password: String) async throws {
        isLoading = true
        error = nil
        defer { isLoading = false }

        do {
            let response = try await SupabaseManager.shared.client.auth.signIn(
                email: email,
                password: password
            )
            session = response.session
        } catch {
            self.error = userFriendlyError(error)
            throw error
        }
    }

    // MARK: - Sign out

    func signOut() async {
        do {
            try await SupabaseManager.shared.client.auth.signOut()
        } catch {
            // Best-effort — clear local state regardless
        }
        session = nil
    }

    // MARK: - Password reset

    func resetPassword(email: String) async throws {
        do {
            try await SupabaseManager.shared.client.auth.resetPasswordForEmail(
                email,
                redirectToURL: nil
            )
        } catch {
            self.error = userFriendlyError(error)
            throw error
        }
    }

    // MARK: - Error helpers

    private func userFriendlyError(_ error: Error) -> String {
        let msg = error.localizedDescription
        if msg.contains("Invalid login") || msg.contains("invalid") {
            return "Invalid email or password."
        }
        if msg.contains("already registered") || msg.contains("already") {
            return "An account with this email already exists."
        }
        if msg.contains("rate limit") || msg.contains("rate") {
            return "Too many attempts. Please wait a moment and try again."
        }
        if msg.contains("network") || msg.contains("connection") {
            return "Network error. Check your connection and try again."
        }
        return msg
    }
}
