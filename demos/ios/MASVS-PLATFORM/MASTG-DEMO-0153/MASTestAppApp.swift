import SwiftUI

@main
struct MASTestAppApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onContinueUserActivity(NSUserActivityTypeBrowsingWeb) { activity in
                    // Store the incoming universal link so mastgTest() can process it
                    UniversalLinkState.lastActivity = activity
                }
        }
    }
}
