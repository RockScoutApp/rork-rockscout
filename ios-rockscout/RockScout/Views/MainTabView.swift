import SwiftUI

/// Main tab navigation — mirrors the Android bottom nav.
/// Five tabs: Home, Database, Identify, Collection, Profile.
struct MainTabView: View {
    @State private var selectedTab: AppTab = .home
    @Environment(EntitlementManager.self) private var entitlement

    var body: some View {
        TabView(selection: $selectedTab) {
            Tab("Home", systemImage: "house.fill", value: AppTab.home) {
                NavigationStack {
                    HomeScreen()
                }
            }

            Tab("Database", systemImage: "doc.text.fill", value: AppTab.database) {
                NavigationStack {
                    SpecimenDatabaseScreen()
                }
            }

            Tab("Identify", systemImage: "camera.viewfinder", value: AppTab.identify) {
                NavigationStack {
                    IdentifyScreen()
                }
            }

            Tab("Collection", systemImage: "square.stack.fill", value: AppTab.collection) {
                NavigationStack {
                    CollectionScreen()
                }
            }

            Tab("Profile", systemImage: "person.fill", value: AppTab.profile) {
                NavigationStack {
                    ProfileScreen()
                }
            }
        }
        .tint(.rsAccent)
        .backgroundColor(.rsBackground)
    }
}

enum AppTab: String, Hashable {
    case home
    case database
    case identify
    case collection
    case profile
}
