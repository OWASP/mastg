import SwiftUI
import UIKit

// SUMMARY: This sample demonstrates a custom URL scheme handler registered in Info.plist
// that validates the source application using UIOpenURLContext.options.sourceApplication.
// The SceneDelegate reads the source bundle ID from each incoming URL context and checks
// it against an allowlist before processing. Note that sourceApplication is only populated
// for apps that belong to the same Apple Developer Team as the receiving app.

struct URLSchemeEvent {
    let url: String
    let source: String
    let result: String
}

enum AppDelegateState {
    static var lastEvent: URLSchemeEvent?
    static var onEvent: ((URLSchemeEvent) -> Void)?
}

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        func display(_ event: URLSchemeEvent) {
            completion("""
            Incoming URL: \(event.url)
            Source app:   \(event.source)
            Handler returned: \(event.result)
            """)
        }

        if let existing = AppDelegateState.lastEvent {
            display(existing)
        } else {
            completion("""
            Waiting for incoming URL scheme...

            Open one of the registered schemes from another app to see the result:
              mastgtest://transfer?amount=500
            """)
        }

        AppDelegateState.onEvent = { event in
            display(event)
        }
    }
}

@objc class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        configurationForConnecting connectingSceneSession: UISceneSession,
        options: UIScene.ConnectionOptions
    ) -> UISceneConfiguration {
        let config = UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
        config.delegateClass = SceneDelegate.self
        return config
    }
}

// PASS: [MASTG-TEST-0371] The SceneDelegate reads UIOpenURLContext.options.sourceApplication
// and checks it against an allowlist before processing the URL.

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    private let allowedSources: Set<String> = [
        "com.mastg.testing-app",
    ]

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession,
               options connectionOptions: UIScene.ConnectionOptions) {
        connectionOptions.urlContexts.forEach { handleURL($0) }
    }

    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        URLContexts.forEach { handleURL($0) }
    }

    private func handleURL(_ context: UIOpenURLContext) {
        let url = context.url
        let source = context.options.sourceApplication ?? "(none)"
        let result = allowedSources.contains(source)

        postEvent(url: url, source: source, result: result)
    }

    private func postEvent(url: URL, source: String, result: Bool) {
        let event = URLSchemeEvent(
            url: url.absoluteString,
            source: source,
            result: result ? "true" : "false"
        )
        AppDelegateState.lastEvent = event
        AppDelegateState.onEvent?(event)
    }
}
