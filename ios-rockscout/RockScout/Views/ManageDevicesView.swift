import SwiftUI

/// Manage Devices screen — shows all devices on the user's account and lets
/// them remove old ones to free up a slot. Premium is limited to 3 devices.
struct ManageDevicesView: View {
    @Environment(AuthManager.self) private var auth
    @State private var deviceManager = DeviceManager.shared
    @State private var showRemoveConfirm: Bool = false
    @State private var deviceToRemove: DeviceInfo?
    @State private var isRemoving: Bool = false

    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                limitBanner

                if deviceManager.isLoading && deviceManager.devices.isEmpty {
                    ProgressView()
                        .tint(.rsAccent)
                        .frame(maxWidth: .infinity)
                        .padding(.top, 40)
                }

                ForEach(deviceManager.devices) { device in
                    DeviceRowView(
                        device: device,
                        isCurrentDevice: device.deviceFingerprint == deviceManager.fingerprint,
                        onRemove: {
                            deviceToRemove = device
                            showRemoveConfirm = true
                        }
                    )
                }

                if deviceManager.devices.isEmpty && !deviceManager.isLoading {
                    Text("No devices registered yet.")
                        .font(.body)
                        .foregroundStyle(.rsTextMuted)
                        .padding(.top, 40)
                }

                Spacer(minLength: 24)
            }
            .padding(.horizontal, 16)
        }
        .background(Color.rsBackground.ignoresSafeArea())
        .navigationTitle("Manage Devices")
        .navigationBarTitleDisplayMode(.large)
        .task {
            if let userId = auth.currentUserId {
                await deviceManager.refreshDevices(userId: userId)
                await deviceManager.checkDeviceAccess(userId: userId)
            }
        }
        .alert("Remove device?", isPresented: $showRemoveConfirm) {
            Button("Remove", role: .destructive) {
                guard let device = deviceToRemove else { return }
                Task {
                    isRemoving = true
                    let success = await deviceManager.removeDevice(deviceId: device.id)
                    isRemoving = false
                    if success, let userId = auth.currentUserId {
                        await deviceManager.refreshDevices(userId: userId)
                        await deviceManager.checkDeviceAccess(userId: userId)
                    }
                }
            }
            Button("Cancel", role: .cancel) {}
        } message: {
            if let device = deviceToRemove {
                Text("Remove \(device.deviceLabel ?? "this device")? If this is an active device, premium will be paused on it until you sign in again on a device within the 3-device limit.")
            }
        }
    }

    // MARK: - Limit Banner

    @ViewBuilder
    private var limitBanner: some View {
        let count = deviceManager.devices.count
        let limitColor: Color = if deviceManager.deviceOverLimit {
            RockScoutColors.danger
        } else if count >= 3 {
            Color(red: 0.91, green: 0.64, blue: 0.24)
        } else {
            .rsAccent
        }

        HStack(spacing: 12) {
            Image(systemName: "laptopcomputer.and.iphone")
                .font(.title2)
                .foregroundStyle(limitColor)

            VStack(alignment: .leading, spacing: 4) {
                Text("\(count) of 3 devices used")
                    .font(.subheadline.weight(.bold))
                    .foregroundStyle(limitColor)

                if deviceManager.deviceOverLimit {
                    Text("This device is over the limit. Premium features are paused. Remove an old device below to restore access.")
                        .font(.caption)
                        .foregroundStyle(.rsTextMuted)
                } else if count >= 3 {
                    Text("You've reached the limit. Remove a device before signing in on a new one.")
                        .font(.caption)
                        .foregroundStyle(.rsTextMuted)
                } else {
                    Text("Premium works on up to 3 devices. Remove old devices you no longer use.")
                        .font(.caption)
                        .foregroundStyle(.rsTextMuted)
                }
            }
        }
        .padding(16)
        .rsCard()
    }
}

// MARK: - Device Row

private struct DeviceRowView: View {
    let device: DeviceInfo
    let isCurrentDevice: Bool
    let onRemove: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            platformIcon

            VStack(alignment: .leading, spacing: 4) {
                HStack(spacing: 8) {
                    Text(device.deviceLabel ?? "Unknown device")
                        .font(.subheadline.weight(.bold))
                        .foregroundStyle(.rsText)

                    if isCurrentDevice {
                        Text("This device")
                            .font(.caption2.weight(.bold))
                            .foregroundStyle(.ink)
                            .padding(.horizontal, 6)
                            .padding(.vertical, 2)
                            .background(.rsAccent.opacity(0.8), in: .capsule)
                    }
                }

                HStack(spacing: 8) {
                    if let platform = device.devicePlatform {
                        platformBadge(platform)
                    }
                    Text("Added \(device.installedAt?.prefix(10) ?? "—")")
                        .font(.caption)
                        .foregroundStyle(.rsTextMuted)
                }
            }

            Spacer()

            if !isCurrentDevice {
                Button(role: .destructive, action: onRemove) {
                    Image(systemName: "trash")
                        .font(.body)
                        .foregroundStyle(RockScoutColors.danger)
                }
            }
        }
        .padding(16)
        .rsCard()
    }

    @ViewBuilder
    private var platformIcon: some View {
        let (iconName, color) = platformIconAndColor(device.devicePlatform)
        Image(systemName: iconName)
            .font(.title3)
            .foregroundStyle(color)
            .frame(width: 40, height: 40)
            .background(color.opacity(0.12), in: .circle)
    }

    private func platformIconAndColor(_ platform: String?) -> (String, Color) {
        switch platform?.lowercased() {
        case "android": return ("iphone.radiowaves.left.and.right", Color(red: 0.24, green: 0.86, blue: 0.52))
        case "ios": return ("iphone", Color(red: 0.0, green: 0.48, blue: 1.0))
        case "web": return ("macbook", .rsAccent)
        default: return ("questionmark", .rsAccent)
        }
    }

    @ViewBuilder
    private func platformBadge(_ platform: String) -> some View {
        let label: String = switch platform.lowercased() {
        case "android": "Android"
        case "ios": "iOS"
        case "web": "Web"
        default: "Unknown"
        }
        let color = platformIconAndColor(platform).1

        Text(label)
            .font(.caption2.weight(.semibold))
            .foregroundStyle(color)
            .padding(.horizontal, 6)
            .padding(.vertical, 1)
            .background(color.opacity(0.12), in: .rect(cornerRadius: 4))
    }
}
