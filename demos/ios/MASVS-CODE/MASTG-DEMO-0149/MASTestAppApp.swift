import SwiftUI

@main
struct MASTestAppApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // Store the incoming URL so mastgTest() processes it when the user taps Start.
                    URLState.lastURL = url
                }
        }
    }
}
