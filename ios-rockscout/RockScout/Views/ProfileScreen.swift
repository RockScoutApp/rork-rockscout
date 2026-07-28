import SwiftUI

/// Profile screen — user account info, premium status, settings, and sign-out.
/// Mirrors the Android ProfileScreen.
struct ProfileScreen: View {
    @Environment(AuthManager.self) private var auth
    @Environment(EntitlementManager.self) private var entitlement
    @State private var showSignOutConfirm: Bool = false
    @State private var showPaywall: Bool = false

    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            ScrollView {
                VStack(spacing: 20) {
                    profileHeader

                    premiumStatusCard

                    settingsSection

                    aboutSection

                    signOutButton

                    Spacer(minLength: 24)
                }
                .padding(.horizontal, 16)
            }
        }
        .navigationTitle("Profile")
        .navigationBarTitleDisplayMode(.large)
        .alert("Sign Out?", isPresented: $showSignOutConfirm) {
            Button("Sign Out", role: .destructive) {
                Task { await signOut() }
            }
            Button("Cancel", role: .cancel) {}
        } message: {
            Text("You'll need to sign in again to access your collection and sync.")
        }
        .navigationDestination(isPresented: $showPaywall) {
            PaywallScreen()
        }
    }

    // MARK: - Profile Header

    private var profileHeader: some View {
        VStack(spacing: 12) {
            // Avatar
            ZStack {
                Circle()
                    .fill(LinearGradient(
                        colors: [RockScoutColors.citrine, RockScoutColors.copper],
                        startPoint: .top,
                        endPoint: .bottom
                    ))
                    .frame(width: 72, height: 72)
                Image(systemName: "person.fill")
                    .font(.system(size: 32))
                    .foregroundStyle(.ink)
            }

            VStack(spacing: 4) {
                Text(auth.currentUserEmail ?? "Rockhound")
                    .font(.headline)
                    .foregroundStyle(.rsText)
                Text(auth.currentUserId?.uuidString.prefix(8) ?? "")
                    .font(.caption)
                    .foregroundStyle(.rsTextMuted)
            }
        }
        .padding(.top, 8)
    }

    // MARK: - Premium Status

    private var premiumStatusCard: some View {
        VStack(spacing: 12) {
            HStack(spacing: 12) {
                Image(systemName: entitlement.isPremium ? "crown.fill" : "crown")
                    .font(.title2)
                    .foregroundStyle(.rsAccent)

                VStack(alignment: .leading, spacing: 2) {
                    Text(entitlement.isPremium ? "Premium Member" : "Free Tier")
                        .font(.subheadline.weight(.semibold))
                        .foregroundStyle(.rsText)
                    Text(entitlement.isPremium
                        ? "All features unlocked"
                        : "Upgrade for unlimited access"
                    )
                    .font(.caption)
                    .foregroundStyle(.rsTextSecondary)
                }

                Spacer()

                if !entitlement.isPremium {
                    Button("Upgrade") {
                        showPaywall = true
                    }
                    .font(.caption.weight(.semibold))
                    .foregroundStyle(.ink)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(.rsAccent, in: .capsule)
                }
            }
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Settings

    private var settingsSection: some View {
        VStack(alignment: .leading, spacing: 0) {
            SectionHeader(title: "Settings")
                .padding(.bottom, 8)

            SettingsRow(icon: "bell.fill", title: "Notifications", value: "On")
            Divider().background(RockScoutColors.stoneLine.opacity(0.3))
            SettingsRow(icon: "location.fill", title: "Location Services", value: "Enabled")
            Divider().background(RockScoutColors.stoneLine.opacity(0.3))
            SettingsRow(icon: "camera.fill", title: "Camera Access", value: "Granted")
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - About

    private var aboutSection: some View {
        VStack(alignment: .leading, spacing: 0) {
            SectionHeader(title: "About")
                .padding(.bottom, 8)

            Link(destination: URL(string: "https://rockscout.app/terms")!) {
                SettingsRow(icon: "doc.text.fill", title: "Terms of Use", value: nil, isLink: true)
            }
            Divider().background(RockScoutColors.stoneLine.opacity(0.3))
            Link(destination: URL(string: "https://rockscout.app/privacy")!) {
                SettingsRow(icon: "lock.fill", title: "Privacy Policy", value: nil, isLink: true)
            }
            Divider().background(RockScoutColors.stoneLine.opacity(0.3))
            Link(destination: URL(string: "https://rockscout.app/support")!) {
                SettingsRow(icon: "questionmark.circle.fill", title: "Help & Support", value: nil, isLink: true)
            }
            Divider().background(RockScoutColors.stoneLine.opacity(0.3))
            SettingsRow(icon: "info.circle.fill", title: "Version", value: "1.0.0")
        }
        .padding(16)
        .rsCard()
    }

    // MARK: - Sign Out

    private var signOutButton: some View {
        Button {
            showSignOutConfirm = true
        } label: {
            HStack {
                Image(systemName: "arrow.right.square.fill")
                Text("Sign Out")
                    .fontWeight(.medium)
            }
            .foregroundStyle(RockScoutColors.danger)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 14)
        }
        .rsCard()
    }

    // MARK: - Actions

    private func signOut() async {
        await entitlement.logout()
        await auth.signOut()
    }
}

// MARK: - Settings Row

private struct SettingsRow: View {
    let icon: String
    let title: String
    var value: String?
    var isLink: Bool = false

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: icon)
                .font(.body)
                .foregroundStyle(.rsAccent)
                .frame(width: 24)
            Text(title)
                .font(.subheadline)
                .foregroundStyle(.rsText)
            Spacer()
            if let value {
                Text(value)
                    .font(.caption)
                    .foregroundStyle(.rsTextMuted)
            }
            if isLink {
                Image(systemName: "chevron.right")
                    .font(.caption)
                    .foregroundStyle(.rsTextMuted)
            }
        }
        .padding(.vertical, 10)
    }
}
