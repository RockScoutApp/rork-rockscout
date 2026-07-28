import SwiftUI
import RevenueCat

@main
struct RockScoutApp: App {
    @State private var authManager = AuthManager.shared
    @State private var entitlementManager = EntitlementManager.shared

    init() {
        // Configure RevenueCat on launch
        EntitlementManager.shared.configure()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environment(authManager)
                .environment(entitlementManager)
                .preferredColorScheme(.dark)
        }
    }
}

/// Root view that switches between loading, auth, and main app states.
struct RootView: View {
    @Environment(AuthManager.self) private var auth

    var body: some View {
        Group {
            if auth.isLoading {
                SplashView()
            } else if auth.isAuthenticated {
                MainTabView()
            } else {
                SignInView()
            }
        }
        .animation(.snappy, value: auth.isAuthenticated)
        .animation(.snappy, value: auth.isLoading)
    }
}

/// Splash/loading view shown while session is restoring.
struct SplashView: View {
    var body: some View {
        ZStack {
            Color.rsBackground.ignoresSafeArea()

            VStack(spacing: 24) {
                Image(systemName: "diamond.fill")
                    .font(.system(size: 64))
                    .foregroundStyle(.rsAccent)
                    .symbolEffect(.pulse)

                Text("RockScout")
                    .font(.system(.largeTitle, design: .rounded, weight: .bold))
                    .foregroundStyle(.rsText)

                ProgressView()
                    .tint(.rsAccent)
            }
        }
    }
}
