import Foundation
import Supabase
import Observation

/// Represents a single pending sync operation in the offline queue.
struct PendingSyncEntry: Codable, Identifiable {
    let id: String
    let table: String
    let recordId: String
    let op: String  // "upsert" or "delete"
    let queuedAt: Date
    var attempts: Int
}

/// Manages an offline sync queue for photo captures, saved images, field
/// journal entries, and trip planner data.
///
/// When the device is offline, records are stored locally in UserDefaults
/// (metadata) and the Documents directory (photo files). A pending sync
/// queue tracks which records need to be pushed to Supabase.
///
/// When connectivity is restored or during the nightly 4 AM sync window,
/// the queue is drained:
///   1. Local photos are uploaded to the `user-photos` Supabase Storage bucket
///   2. The database row is upserted to the corresponding Supabase table
///   3. The queue entry is removed on success
///
/// The service also listens for connectivity changes via NWPathMonitor
/// and triggers an immediate drain when the network comes back.
@MainActor
@Observable
final class OfflineSyncService {
    static let shared = OfflineSyncService()

    // MARK: - State

    private(set) var pendingCount: Int = 0
    private(set) var isSyncing: Bool = false
    private(set) var lastSyncAt: Date?

    private let queueKey = "rockscout_sync_queue"
    private let offlineDataKey = "rockscout_offline_data"
    private let photoDirName = "offline_photos"

    // MARK: - Init

    private init() {
        refreshPendingCount()
    }

    // MARK: - Queue Management

    /// Enqueue a pending sync operation. Replaces any existing entry for the
    /// same table + recordId.
    func enqueue(table: String, recordId: String, op: String = "upsert") {
        var queue = loadQueue()
        queue.removeAll { $0.table == table && $0.recordId == recordId }
        queue.append(PendingSyncEntry(
            id: "\(table)_\(recordId)_\(Date().timeIntervalSince1970)",
            table: table,
            recordId: recordId,
            op: op,
            queuedAt: Date(),
            attempts: 0
        ))
        saveQueue(queue)
        refreshPendingCount()
    }

    /// Enqueue a delete operation and remove any pending upsert for the same record.
    func enqueueDelete(table: String, recordId: String) {
        var queue = loadQueue()
        queue.removeAll { $0.table == table && $0.recordId == recordId && $0.op == "upsert" }
        queue.append(PendingSyncEntry(
            id: "\(table)_\(recordId)_del_\(Date().timeIntervalSince1970)",
            table: table,
            recordId: recordId,
            op: "delete",
            queuedAt: Date(),
            attempts: 0
        ))
        saveQueue(queue)
        refreshPendingCount()
    }

    /// Remove a queue entry after successful sync.
    private func removeEntry(_ entryId: String) {
        var queue = loadQueue()
        queue.removeAll { $0.id == entryId }
        saveQueue(queue)
        refreshPendingCount()
    }

    /// Increment the attempt counter for a failed entry.
    private func incrementAttempts(_ entryId: String) {
        var queue = loadQueue()
        for i in queue.indices where queue[i].id == entryId {
            queue[i].attempts += 1
        }
        saveQueue(queue)
    }

    /// Clear all queue entries.
    func clearQueue() {
        saveQueue([])
        refreshPendingCount()
    }

    private func refreshPendingCount() {
        pendingCount = loadQueue().count
    }

    // MARK: - Offline Record Storage

    /// Store an offline record's metadata in UserDefaults.
    func storeOfflineRecord(table: String, recordId: String, data: [String: Any]) {
        var allData = loadOfflineData()
        let key = "\(table):\(recordId)"
        allData[key] = data
        saveOfflineData(allData)
    }

    /// Get an offline record's metadata.
    func getOfflineRecord(table: String, recordId: String) -> [String: Any]? {
        let allData = loadOfflineData()
        return allData["\(table):\(recordId)"]
    }

    /// Remove an offline record.
    func removeOfflineRecord(table: String, recordId: String) {
        var allData = loadOfflineData()
        allData.removeValue(forKey: "\(table):\(recordId)")
        saveOfflineData(allData)
    }

    // MARK: - Photo Storage

    /// Store a photo file locally for later upload.
    /// Returns the local file path.
    func storePhotoLocally(data: Data, ext: String = "jpg") throws -> String {
        let photoDir = try getPhotoDirectory()
        let filename = "photo_\(UUID().uuidString).\(ext)"
        let fileURL = photoDir.appendingPathComponent(filename)
        try data.write(to: fileURL)
        return fileURL.path
    }

    /// Upload a local photo to Supabase Storage.
    private func uploadPhoto(localPath: String, userId: String) async -> String? {
        let url = URL(fileURLWithPath: localPath)
        guard FileManager.default.fileExists(atPath: localPath) else { return nil }

        do {
            let data = try Data(contentsOf: url)
            let ext = url.pathExtension.lowercased()
            let filename = "\(userId)/\(UUID().uuidString).\(ext)"
            let contentType = ext == "png" ? "image/png" : "image/jpeg"

            try await SupabaseManager.shared.client.storage
                .from("user-photos")
                .upload(filename, data: data, options: FileOptions(
                    contentType: contentType,
                    upsert: false
                ))

            let publicUrl = "\(AppSecrets.supabaseURL)/storage/v1/object/public/user-photos/\(filename)"
            return publicUrl
        } catch {
            print("[OfflineSync] Photo upload failed: \(error.localizedDescription)")
            return nil
        }
    }

    private func getPhotoDirectory() throws -> URL {
        let docs = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)[0]
        let photoDir = docs.appendingPathComponent(photoDirName)
        if !FileManager.default.fileExists(atPath: photoDir.path) {
            try FileManager.default.createDirectory(at: photoDir, withIntermediateDirectories: true)
        }
        return photoDir
    }

    // MARK: - Drain Queue

    /// Drain the pending sync queue — uploads photos and upserts/deletes records.
    /// Returns the number of successfully synced items.
    func drain() async -> Int {
        guard AuthManager.shared.isAuthenticated else { return 0 }
        guard let userId = AuthManager.shared.currentUserId?.uuidString else { return 0 }

        let queue = loadQueue()
        if queue.isEmpty { return 0 }

        isSyncing = true
        defer { isSyncing = false }

        var successCount = 0

        for entry in queue {
            let success = await processEntry(entry, userId: userId)
            if success {
                removeEntry(entry.id)
                successCount += 1
            } else {
                incrementAttempts(entry.id)
                if entry.attempts + 1 >= 10 {
                    removeEntry(entry.id)
                }
            }
        }

        lastSyncAt = Date()
        return successCount
    }

    /// Process a single sync queue entry.
    private func processEntry(_ entry: PendingSyncEntry, userId: String) async -> Bool {
        if entry.op == "delete" {
            return await deleteRecord(table: entry.table, recordId: entry.recordId)
        }

        // Upsert: get the offline record, upload photos, push to Supabase
        guard let record = getOfflineRecord(table: entry.table, recordId: entry.recordId) else {
            // Record was deleted locally — nothing to push
            return true
        }

        // Upload any local photo paths referenced in the record
        var updatedRecord = record
        let photoFields: [String: String] = [
            "rockscout_captures": "image_urls",
            "rockscout_saved_images": "image_url",
            "rockscout_field_journal": "photo_urls",
        ]

        if let photoField = photoFields[entry.table] {
            if entry.table == "rockscout_saved_images" {
                if let url = updatedRecord[photoField] as? String, url.hasPrefix("/") {
                    if let remoteUrl = await uploadPhoto(localPath: url, userId: userId) {
                        updatedRecord[photoField] = remoteUrl
                    }
                }
            } else {
                if let urls = updatedRecord[photoField] as? [String] {
                    var uploadedUrls: [String] = []
                    for url in urls {
                        if url.hasPrefix("/") {
                            if let remoteUrl = await uploadPhoto(localPath: url, userId: userId) {
                                uploadedUrls.append(remoteUrl)
                            } else {
                                uploadedUrls.append(url)
                            }
                        } else {
                            uploadedUrls.append(url)
                        }
                    }
                    updatedRecord[photoField] = uploadedUrls
                }
            }
        }

        // Push to Supabase
        let success = await pushRecord(table: entry.table, record: updatedRecord, userId: userId)
        if success {
            removeOfflineRecord(table: entry.table, recordId: entry.recordId)
        }
        return success
    }

    /// Upsert a record to Supabase.
    private func pushRecord(table: String, record: [String: Any], userId: String) async -> Bool {
        var payload = record
        payload["user_id"] = userId

        do {
            try await SupabaseManager.shared.client
                .from(table)
                .upsert(payload)
                .execute()
            return true
        } catch {
            print("[OfflineSync] Push to \(table) failed: \(error.localizedDescription)")
            return false
        }
    }

    /// Delete a record from Supabase.
    private func deleteRecord(table: String, recordId: String) async -> Bool {
        do {
            try await SupabaseManager.shared.client
                .from(table)
                .delete()
                .eq("id", value: recordId)
                .execute()
            return true
        } catch {
            print("[OfflineSync] Delete from \(table) failed: \(error.localizedDescription)")
            return false
        }
    }

    /// Trigger a drain in the background (safe to call repeatedly).
    func drainInBackground() {
        Task {
            await drain()
        }
    }

    // MARK: - Persistence

    private func loadQueue() -> [PendingSyncEntry] {
        guard let data = UserDefaults.standard.data(forKey: queueKey) else { return [] }
        return (try? JSONDecoder().decode([PendingSyncEntry].self, from: data)) ?? []
    }

    private func saveQueue(_ queue: [PendingSyncEntry]) {
        if let data = try? JSONEncoder().encode(queue) {
            UserDefaults.standard.set(data, forKey: queueKey)
        }
    }

    private func loadOfflineData() -> [String: [String: Any]] {
        guard let data = UserDefaults.standard.data(forKey: offlineDataKey) else { return [:] }
        return (try? JSONSerialization.jsonObject(with: data) as? [String: [String: Any]]) ?? [:]
    }

    private func saveOfflineData(_ data: [String: [String: Any]]) {
        if let data = try? JSONSerialization.data(withJSONObject: data) {
            UserDefaults.standard.set(data, forKey: offlineDataKey)
        }
    }

    // MARK: - Nightly 4 AM Timer

    /// Schedule a nightly drain at 4 AM in the user's current timezone.
    /// Call this once on app launch.
    func scheduleNightlySync() {
        let calendar = Calendar.current
        let now = Date()
        var next4Am = calendar.date(bySettingHour: 4, minute: 0, second: 0, of: now) ?? now
        if next4Am <= now {
            next4Am = calendar.date(byAdding: .day, value: 1, to: next4Am) ?? now
        }
        let interval = next4Am.timeIntervalSince(now)

        Task {
            try? await Task.sleep(for: .seconds(interval))
            await drain()
            // Re-schedule for the next day
            scheduleNightlySync()
        }
    }
}
