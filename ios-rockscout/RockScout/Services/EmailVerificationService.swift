import Foundation

/// Calls the RockScout Cloudflare Worker `/email-verification` endpoint to send
/// and check the 6-digit sign-up code.
///
/// The same endpoint backs Android and the web app, so a code issued on one
/// platform verifies on any of them. When the code checks out the backend also
/// marks the Supabase email confirmed, which is what lets the new account sign
/// in immediately instead of waiting on a confirmation link.
///
/// The endpoint is app-key guarded — requests without `X-App-Key` get a 401.
nonisolated enum EmailVerificationService {

    /// Outcome of a verification call.
    nonisolated enum Outcome: Sendable {
        case sent
        case verified(emailConfirmed: Bool, hint: String?)
        case failed(message: String)
    }

    private nonisolated struct WorkerResponse: Decodable, Sendable {
        let ok: Bool?
        let verified: Bool?
        let emailConfirmed: Bool?
        let confirmReason: String?
        let confirmHint: String?
        let error: String?
        let reason: String?
    }

    private static let genericFailure =
        "Verification failed. Please try again."

    /// Emails a fresh 6-digit code to `email`.
    static func sendCode(to email: String) async -> Outcome {
        await post(body: ["action": "send", "email": normalized(email)])
    }

    /// Checks `code` for `email`, activating the Supabase account on success.
    static func verify(email: String, code: String, userId: String?) async -> Outcome {
        var body: [String: String] = [
            "action": "verify",
            "email": normalized(email),
            "code": code.trimmingCharacters(in: .whitespacesAndNewlines),
        ]
        if let userId, !userId.isEmpty {
            body["supabaseUserId"] = userId
        }
        return await post(body: body)
    }

    // MARK: - Networking

    private static func post(body: [String: String]) async -> Outcome {
        guard
            let url = URL(string: "\(AppSecrets.functionsURL)/email-verification"),
            !AppSecrets.appKey.isEmpty
        else {
            return .failed(message: "Verification is unavailable right now.")
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.timeoutInterval = 20
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue(AppSecrets.appKey, forHTTPHeaderField: "X-App-Key")
        request.httpBody = try? JSONSerialization.data(withJSONObject: body)

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            let status = (response as? HTTPURLResponse)?.statusCode ?? 0
            let decoded = try? JSONDecoder().decode(WorkerResponse.self, from: data)

            if decoded?.ok == true {
                if body["action"] == "verify" {
                    return .verified(
                        emailConfirmed: decoded?.emailConfirmed ?? false,
                        hint: decoded?.confirmHint,
                    )
                }
                return .sent
            }

            if status == 429 {
                return .failed(message: "Too many attempts. Wait a moment and retry.")
            }
            return .failed(message: decoded?.error ?? genericFailure)
        } catch {
            return .failed(message: "Network error. Check your connection and try again.")
        }
    }

    private static func normalized(_ email: String) -> String {
        email.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
    }
}

/// Build-time values with baked fallbacks.
///
/// The generated `Config` regenerates empty in this project, so backend calls
/// would silently 401. These fallbacks mirror the Android `BuildSecrets` object
/// and are superseded by `Config` whenever the build system populates it.
nonisolated enum AppSecrets {
    private static let fallbackFunctionsURL = "https://rockscout-finder-backend.rork.app"
    private static let fallbackAppKey = "rpk_munggtdkjtv3tbx5sw9ge3kebajzh39k"
    private static let fallbackSupabaseURL = "https://kblsiyyelyokhxaxefhy.supabase.co"
    private static let fallbackSupabaseAnonKey = "sb_publishable_xNJLANDaaAfuEF9q_lsBdw_3zfL6lBk"
    private static let fallbackRevenueCatIOSKey = "appl_vfaAnYfbiMkgwtUSrQqmMnWGVPe"

    static var functionsURL: String {
        Config.EXPO_PUBLIC_RORK_FUNCTIONS_URL.isEmpty
            ? fallbackFunctionsURL
            : Config.EXPO_PUBLIC_RORK_FUNCTIONS_URL
    }

    static var appKey: String {
        Config.EXPO_PUBLIC_RORK_APP_KEY.isEmpty
            ? fallbackAppKey
            : Config.EXPO_PUBLIC_RORK_APP_KEY
    }

    static var supabaseURL: String {
        Config.EXPO_PUBLIC_SUPABASE_URL.isEmpty
            ? fallbackSupabaseURL
            : Config.EXPO_PUBLIC_SUPABASE_URL
    }

    static var supabaseAnonKey: String {
        Config.EXPO_PUBLIC_SUPABASE_ANON_KEY.isEmpty
            ? fallbackSupabaseAnonKey
            : Config.EXPO_PUBLIC_SUPABASE_ANON_KEY
    }

    static var revenueCatIOSKey: String {
        Config.EXPO_PUBLIC_REVENUECAT_IOS_API_KEY.isEmpty
            ? fallbackRevenueCatIOSKey
            : Config.EXPO_PUBLIC_REVENUECAT_IOS_API_KEY
    }
}
