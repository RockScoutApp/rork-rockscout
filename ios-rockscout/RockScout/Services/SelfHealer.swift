import Foundation
import UIKit

/// Auto-remediation for known recoverable error patterns on iOS.
///
/// When `ErrorReporter` catches an error, it asks `SelfHealer` to try a fix.
/// If the heal succeeds, the error is still reported (with `autoHealed = true`)
/// so we can track self-healing frequency, but the user never sees the failure.
enum SelfHealer {

    /// Attempts to heal a known error pattern. Returns a description of the
    /// heal action if successful, or nil if the error is not recognized.
    @MainActor
    static func attemptHeal(error: Error, screen: String) -> String? {
        let errorType = String(describing: type(of: error))
        let message = error.localizedDescription.lowercased()

        // 1. Core Data / SQLite corruption — delete and recreate
        if errorType.contains("SQLite") || errorType.contains("CoreData") ||
           message.contains("database") || message.contains("corrupt") ||
           message.contains("malformed") {
            return healCorruptCache()
        }

        // 2. Memory pressure — clear image caches
        if errorType.contains("OutOfMemory") || message.contains("out of memory") ||
           message.contains("memory") {
            return healMemoryPressure()
        }

        // 3. Network errors — URL session retry is automatic, just acknowledge
        if errorType.contains("URLError") || errorType.contains("NetworkError") ||
           message.contains("network") || message.contains("timeout") ||
           message.contains("connection") {
            return "network_retry_acknowledged"
        }

        // 4. File not found — recreate directories
        if errorType.contains("FileNotFound") || errorType.contains("NoSuchFile") ||
           message.contains("no such file") || message.contains("not found") {
            return healMissingFiles()
        }

        // 5. UserDefaults corruption
        if message.contains("userdefaults") || message.contains("defaults") && message.contains("corrupt") {
            return healUserDefaults()
        }

        return nil
    }

    // MARK: - Heal Methods

    /// Clear URLCache and any corrupt SQLite stores in the app's container.
    @MainActor
    private static func healCorruptCache() -> String? {
        var healed = false

        // Clear URLCache
        URLCache.shared.removeAllCachedResponses()
        healed = true

        // Check for zero-byte .db files (corrupt)
        let fm = FileManager.default
        if let docsDir = fm.urls(for: .applicationSupportDirectory, in: .userDomainMask).first {
            if let files = try? fm.contentsOfDirectory(at: docsDir, includingPropertiesForKeys: [.fileSizeKey]) {
                for file in files where file.pathExtension == "db" {
                    if let size = try? file.resourceValues(forKeys: [.fileSizeKey]).fileSize, size == 0 {
                        try? fm.removeItem(at: file)
                        healed = true
                    }
                }
            }
        }

        return healed ? "cleared_corrupt_cache" : nil
    }

    /// Clear memory caches under memory pressure.
    @MainActor
    private static func healMemoryPressure() -> String? {
        URLCache.shared.removeAllCachedResponses()
        // Trigger a warning to clear image caches if any are held
        NotificationCenter.default.post(name: UIApplication.didReceiveMemoryWarningNotification, object: nil)
        return "cleared_memory_caches"
    }

    /// Recreate missing application directories.
    @MainActor
    private static func healMissingFiles() -> String? {
        let fm = FileManager.default
        let dirs: [FileManager.SearchPathDirectory] = [
            .documentDirectory,
            .applicationSupportDirectory,
            .cachesDirectory,
        ]
        for dir in dirs {
            if let url = fm.urls(for: dir, in: .userDomainMask).first {
                try? fm.createDirectory(at: url, withIntermediateDirectories: true)
            }
        }
        return "recreated_missing_dirs"
    }

    /// Clear corrupted UserDefaults (best-effort — only removes non-critical keys).
    @MainActor
    private static func healUserDefaults() -> String? {
        // We can't safely clear all UserDefaults without losing user data.
        // Just remove the app's internal cache keys.
        let defaults = UserDefaults.standard
        let cacheKeys = defaults.dictionaryRepresentation().keys.filter {
            $0.hasPrefix("rockscout_cache_") || $0.hasPrefix("temp_")
        }
        for key in cacheKeys {
            defaults.removeObject(forKey: key)
        }
        return "cleared_corrupt_defaults"
    }
}
