import Foundation
import CryptoKit

/// Central error reporter that ships runtime exceptions to the Supabase
/// `rockscout_error_logs` table via the `/error-report` Cloudflare Worker.
///
/// Self-healing: for known recoverable error patterns, the reporter can
/// automatically apply a fix before the error propagates. See `SelfHealer`.
///
/// All network calls are fire-and-forget on a background task — they never
/// block the calling thread or crash the app if the upload fails.
@MainActor
final class ErrorReporter {
    static let shared = ErrorReporter()
    private init() {}

    // MARK: - State

    private var appVersion: String = "unknown"
    private var osVersion: String = "unknown"
    private var deviceModel: String = "unknown"
    private var currentUserId: String?
    private var lastBreadcrumb: String?
    private var lastReportTime: [String: Date] = [:]
    private let dedupWindow: TimeInterval = 300 // 5 minutes

    // MARK: - Init

    func configure() {
        appVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "unknown"
        osVersion = ProcessInfo.processInfo.operatingSystemVersionString
        deviceModel = Self.deviceModel()
    }

    func setUserId(_ userId: String?) {
        currentUserId = userId
    }

    func setBreadcrumb(_ action: String) {
        lastBreadcrumb = String(action.prefix(500))
    }

    // MARK: - Report

    /// Report an error to the central service. Fire-and-forget.
    func report(
        screen: String,
        error: Error,
        isFatal: Bool = false,
        attemptSelfHeal: Bool = true
    ) {
        let errorType = String(describing: type(of: error))
        let message = error.localizedDescription
        let stackTrace = error.customMirror.children
            .compactMap { $0.label == "stackTrace" ? $0.value as? String : nil }
            .first

        var healAction: String?
        var autoHealed = false

        if attemptSelfHeal && !isFatal {
            if let result = SelfHealer.attemptHeal(error: error, screen: screen) {
                autoHealed = true
                healAction = result
            }
        }

        let fingerprint = Self.fingerprint(type: errorType, message: message, screen: screen)

        // Dedup: skip if same fingerprint was reported within the window
        if let lastTime = lastReportTime[fingerprint],
           Date().timeIntervalSince(lastTime) < dedupWindow {
            return
        }
        lastReportTime[fingerprint] = Date()

        let payload = ErrorPayload(
            platform: "ios",
            appVersion: appVersion,
            osVersion: osVersion,
            deviceModel: deviceModel,
            userId: currentUserId,
            errorType: String(errorType.prefix(300)),
            errorMessage: String(message.prefix(2000)),
            stackTrace: stackTrace.map { String($0.prefix(8000)) },
            isFatal: isFatal,
            screen: String(screen.prefix(200)),
            breadcrumb: lastBreadcrumb,
            autoHealed: autoHealed,
            healAction: healAction,
            fingerprint: fingerprint
        )

        Task.detached { [payload] in
            await Self.upload(payload)
        }
    }

    /// Report a plain message (no throwable).
    func reportMessage(
        screen: String,
        message: String,
        isFatal: Bool = false
    ) {
        let fingerprint = Self.fingerprint(type: "Message", message: message, screen: screen)

        if let lastTime = lastReportTime[fingerprint],
           Date().timeIntervalSince(lastTime) < dedupWindow {
            return
        }
        lastReportTime[fingerprint] = Date()

        let payload = ErrorPayload(
            platform: "ios",
            appVersion: appVersion,
            osVersion: osVersion,
            deviceModel: deviceModel,
            userId: currentUserId,
            errorType: "Message",
            errorMessage: String(message.prefix(2000)),
            stackTrace: nil,
            isFatal: isFatal,
            screen: String(screen.prefix(200)),
            breadcrumb: lastBreadcrumb,
            autoHealed: false,
            healAction: nil,
            fingerprint: fingerprint
        )

        Task.detached { [payload] in
            await Self.upload(payload)
        }
    }

    // MARK: - Upload

    nonisolated private static func upload(_ payload: ErrorPayload) async {
        let url = AppSecrets.functionsURL
        let appKey = AppSecrets.appKey
        guard !url.isEmpty, !appKey.isEmpty else { return }

        guard let endpoint = URL(string: "\(url)/error-report") else { return }

        var request = URLRequest(url: endpoint)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.setValue(appKey, forHTTPHeaderField: "X-App-Key")

        do {
            request.httpBody = try JSONEncoder().encode(payload)
        } catch {
            return
        }

        do {
            let (_, response) = try await URLSession.shared.data(for: request)
            if let http = response as? HTTPURLResponse {
                #if DEBUG
                print("[ErrorReporter] Upload status: \(http.statusCode)")
                #endif
            }
        } catch {
            // Fire-and-forget — network failures are expected
        }
    }

    // MARK: - Fingerprint

    nonisolated private static func fingerprint(type: String, message: String, screen: String) -> String {
        let input = "\(type)|\(message)|\(screen)"
        let hash = SHA256.hash(data: Data(input.utf8))
        return hash.compactMap { String(format: "%02x", $0) }.joined().prefix(16).description
    }

    // MARK: - Device

    nonisolated private static func deviceModel() -> String {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machine = withUnsafePointer(to: &systemInfo.machine) { ptr in
            ptr.withMemoryRebound(to: CChar.self, capacity: 1) {
                String(cString: $0)
            }
        }
        return machine
    }
}

// MARK: - Payload

nonisolated struct ErrorPayload: Codable {
    let platform: String
    let appVersion: String
    let osVersion: String
    let deviceModel: String
    let userId: String?
    let errorType: String
    let errorMessage: String
    let stackTrace: String?
    let isFatal: Bool
    let screen: String
    let breadcrumb: String?
    let autoHealed: Bool
    let healAction: String?
    let fingerprint: String

    enum CodingKeys: String, CodingKey {
        case platform
        case appVersion = "appVersion"
        case osVersion = "osVersion"
        case deviceModel = "deviceModel"
        case userId
        case errorType
        case errorMessage
        case stackTrace
        case isFatal
        case screen
        case breadcrumb
        case autoHealed
        case healAction
        case fingerprint
    }
}
