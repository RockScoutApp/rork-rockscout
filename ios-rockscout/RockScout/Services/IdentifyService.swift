import Foundation

/// Calls the Cloudflare `/identify` endpoint to identify a specimen from a photo.
/// Mirrors the Android `IdentifyApi` — same backend, same response format.
struct IdentifyService {
    /// Identify a specimen from an image.
    /// - Parameters:
    ///   - imageData: Raw JPEG/PNG image data.
    ///   - mimeType: The MIME type of the image (default "image/jpeg").
    ///   - isPremium: Whether the user has premium (controls accuracy ladder).
    /// - Returns: The identification response with matches.
    static func identify(
        imageData: Data,
        mimeType: String = "image/jpeg",
        isPremium: Bool
    ) async throws -> IdentifyResponse {
        let functionsUrl = AppSecrets.functionsURL
        guard !functionsUrl.isEmpty else {
            throw IdentifyError.notConfigured
        }

        let appKey = AppSecrets.appKey
        let urlString = "\(functionsUrl)/identify"
        guard let url = URL(string: urlString) else {
            throw IdentifyError.invalidURL
        }

        let base64 = imageData.base64EncodedString()
        let tier = isPremium ? "premium" : "free"

        let body: [String: Any] = [
            "imageBase64": base64,
            "mimeType": mimeType,
            "entitlement": tier,
            "searchMode": "rocks",
        ]

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        if !appKey.isEmpty {
            request.setValue(appKey, forHTTPHeaderField: "X-App-Key")
        }
        request.httpBody = try JSONSerialization.data(withJSONObject: body)

        let (data, response) = try await URLSession.shared.data(for: request)

        guard let http = response as? HTTPURLResponse else {
            throw IdentifyError.invalidResponse
        }

        if http.statusCode == 429 {
            throw IdentifyError.rateLimited
        }

        guard (200...299).contains(http.statusCode) else {
            throw IdentifyError.serverError(http.statusCode)
        }

        let decoder = JSONDecoder()
        return try decoder.decode(IdentifyResponse.self, from: data)
    }
}

enum IdentifyError: LocalizedError {
    case notConfigured
    case invalidURL
    case invalidResponse
    case rateLimited
    case serverError(Int)

    var errorDescription: String? {
        switch self {
        case .notConfigured:
            return "Identification service is not configured."
        case .invalidURL:
            return "Invalid service URL."
        case .invalidResponse:
            return "Invalid response from the server."
        case .rateLimited:
            return "Too many scans. Please wait a moment and try again."
        case .serverError(let code):
            return "Server error (HTTP \(code)). Please try again."
        }
    }
}
