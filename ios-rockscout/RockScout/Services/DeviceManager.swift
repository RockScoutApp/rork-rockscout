import Foundation
import Supabase
import UIKit
import Observation

/// Manages the 3-device limit for Premium subscribers on iOS.
/// Mirrors the Android `DeviceManager` — same Supabase table, same logic.
///
/// On sign-in, upserts this device into `rockscout_installed_devices`.
/// On every launch, queries all devices ordered by `installed_at` ascending.
/// If the current device is at index 3+, `deviceOverLimit` is set to true,
/// blocking premium features via `EntitlementManager.effectiveIsPremium`.
@Observable
@MainActor
final class DeviceManager {
    static let shared = DeviceManager()

    private(set) var deviceOverLimit: Bool = false
    private(set) var devices: [DeviceInfo] = []
    private(set) var isLoading: Bool = false

    private init() {}

    // MARK: - Device fingerprint

    /// Stable device fingerprint using `identifierForVendor`.
    var fingerprint: String {
        UIDevice.current.identifierForVendor?.uuidString ?? UUID().uuidString
    }

    /// Human-readable device label, e.g. "iPhone" or "iPad".
    private var deviceLabel: String {
        UIDevice.current.model
    }

    // MARK: - Register

    /// Upsert this device into `rockscout_installed_devices`.
    func registerDevice(userId: UUID) async {
        let userIdStr = userId.uuidString
        guard !userIdStr.isEmpty else { return }

        do {
            let body: [String: Any] = [
                "user_id": userIdStr,
                "device_fingerprint": fingerprint,
                "device_label": deviceLabel,
                "device_platform": "ios",
                "user_agent": "\(UIDevice.current.systemName) \(UIDevice.current.systemVersion) / \(UIDevice.current.model)",
                "last_seen_at": ISO8601DateFormatter().string(from: Date()),
            ]

            try await SupabaseManager.shared.client
                .from("rockscout_installed_devices")
                .upsert(body, onConflict: "user_id,device_fingerprint")
                .execute()
        } catch {
            // Non-fatal — device registration is best-effort
        }
    }

    // MARK: - Check access

    /// Query all devices for the user and check if this device is over the limit.
    func checkDeviceAccess(userId: UUID) async {
        let userIdStr = userId.uuidString
        guard !userIdStr.isEmpty else { return }

        isLoading = true
        defer { isLoading = false }

        do {
            let result: [DeviceInfo] = try await SupabaseManager.shared.client
                .from("rockscout_installed_devices")
                .select("id,device_fingerprint,device_label,device_platform,installed_at,last_seen_at")
                .eq("user_id", value: userIdStr)
                .order("installed_at", ascending: true)
                .execute()
                .value

            devices = result

            let myFingerprint = fingerprint
            let myIndex = result.firstIndex { $0.deviceFingerprint == myFingerprint } ?? -1

            if myIndex >= 3 {
                deviceOverLimit = true
            } else {
                deviceOverLimit = false
            }
        } catch {
            // Network failure — default to not blocked
            deviceOverLimit = false
        }
    }

    // MARK: - Refresh

    /// Refresh the device list without changing `deviceOverLimit`.
    func refreshDevices(userId: UUID) async {
        let userIdStr = userId.uuidString
        guard !userIdStr.isEmpty else { return }

        do {
            let result: [DeviceInfo] = try await SupabaseManager.shared.client
                .from("rockscout_installed_devices")
                .select("id,device_fingerprint,device_label,device_platform,installed_at,last_seen_at")
                .eq("user_id", value: userIdStr)
                .order("installed_at", ascending: true)
                .execute()
                .value
            devices = result
        } catch {
            // Non-fatal
        }
    }

    // MARK: - Remove

    /// Remove a device by its row ID. Returns true on success.
    @discardableResult
    func removeDevice(deviceId: String) async -> Bool {
        do {
            try await SupabaseManager.shared.client
                .from("rockscout_installed_devices")
                .delete()
                .eq("id", value: deviceId)
                .execute()
            return true
        } catch {
            return false
        }
    }

    // MARK: - Reset

    /// Reset state on sign-out.
    func reset() {
        deviceOverLimit = false
        devices = []
        isLoading = false
    }
}

// MARK: - DeviceInfo

struct DeviceInfo: Codable, Identifiable, Sendable {
    let id: String
    let deviceFingerprint: String
    let deviceLabel: String?
    let devicePlatform: String?
    let installedAt: String?
    let lastSeenAt: String?

    enum CodingKeys: String, CodingKey {
        case id
        case deviceFingerprint = "device_fingerprint"
        case deviceLabel = "device_label"
        case devicePlatform = "device_platform"
        case installedAt = "installed_at"
        case lastSeenAt = "last_seen_at"
    }
}
