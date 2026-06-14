import SwiftUI

@main
struct MASTestAppApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    // Store the URL so mastgTest() can process it when the user taps Start
                    URLState.lastURL = url
                }
        }
    }
}
