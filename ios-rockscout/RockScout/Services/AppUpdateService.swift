import Foundation
import UIKit

/// Result of an App Store version lookup.
nonisolated struct AppStoreLookupResponse: Codable {
    let resultCount: Int
    let results: [AppStoreLookupResult]
}

nonisolated struct AppStoreLookupResult: Codable {
    let version: String
    let trackViewUrl: String
    let releaseNotes: String?
}

/// Checks the App Store for a newer release and exposes it to the UI.
///
/// iOS installs updates through the App Store, so the app's job is simply to
/// notice a newer version exists and give the user a direct route to it —
/// otherwise people sit on an old build indefinitely with automatic updates off.
///
/// The lookup uses Apple's public iTunes search API keyed on this app's bundle
/// identifier, so it keeps working without any backend configuration.
@Observable
final class AppUpdateService {

    static let shared = AppUpdateService()

    /// Version string available on the App Store, when it's newer than this build.
    private(set) var availableVersion: String?
    /// Deep link to the App Store listing for the available update.
    private(set) var storeURL: URL?
    /// "What's new" text from the App Store listing.
    private(set) var releaseNotes: String?

    private var lastCheck: Date?
    /// Don't hammer the lookup API on every screen appearance.
    private let minimumCheckInterval: TimeInterval = 60 * 60

    private init() {}

    /// The marketing version of the running build, e.g. "1.1.5".
    var installedVersion: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "0"
    }

    /// True when the App Store has a newer version than the installed build.
    var isUpdateAvailable: Bool {
        availableVersion != nil
    }

    /// Queries the App Store. Safe to call from `.task` on any screen.
    func checkForUpdate() async {
        if let lastCheck, Date().timeIntervalSince(lastCheck) < minimumCheckInterval {
            return
        }
        lastCheck = Date()

        guard let bundleID = Bundle.main.bundleIdentifier,
              var components = URLComponents(string: "https://itunes.apple.com/lookup") else {
            return
        }
        components.queryItems = [
            URLQueryItem(name: "bundleId", value: bundleID),
            // Cache-buster: the lookup API is aggressively cached at the edge and
            // would otherwise keep reporting the previous release for hours.
            URLQueryItem(name: "t", value: String(Int(Date().timeIntervalSince1970))),
        ]
        guard let url = components.url else { return }

        do {
            var request = URLRequest(url: url)
            request.cachePolicy = .reloadIgnoringLocalCacheData
            request.timeoutInterval = 15

            let (data, response) = try await URLSession.shared.data(for: request)
            guard let http = response as? HTTPURLResponse, (200..<300).contains(http.statusCode) else {
                return
            }

            let decoded = try JSONDecoder().decode(AppStoreLookupResponse.self, from: data)
            guard let result = decoded.results.first else {
                // App isn't on the store yet — nothing to offer.
                clear()
                return
            }

            if Self.isVersion(result.version, newerThan: installedVersion) {
                availableVersion = result.version
                storeURL = URL(string: result.trackViewUrl)
                releaseNotes = result.releaseNotes
            } else {
                clear()
            }
        } catch {
            // A failed version check must never disrupt the app.
            print("[AppUpdateService] Version check failed: \(error.localizedDescription)")
        }
    }

    /// Opens the App Store listing so the user can install the update.
    func openAppStore() {
        guard let storeURL else { return }
        UIApplication.shared.open(storeURL)
    }

    private func clear() {
        availableVersion = nil
        storeURL = nil
        releaseNotes = nil
    }

    /// Semantic-ish comparison of dotted version strings ("1.2.10" > "1.2.9").
    /// String comparison gets this wrong, which would either hide real updates
    /// or nag about phantom ones.
    static func isVersion(_ candidate: String, newerThan current: String) -> Bool {
        let candidateParts = candidate.split(separator: ".").map { Int($0) ?? 0 }
        let currentParts = current.split(separator: ".").map { Int($0) ?? 0 }
        let count = max(candidateParts.count, currentParts.count)
        for index in 0..<count {
            let lhs = index < candidateParts.count ? candidateParts[index] : 0
            let rhs = index < currentParts.count ? currentParts[index] : 0
            if lhs != rhs { return lhs > rhs }
        }
        return false
    }
}
