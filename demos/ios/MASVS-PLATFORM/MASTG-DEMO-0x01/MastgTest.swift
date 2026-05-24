import SwiftUI
import UIKit

// SUMMARY: This sample demonstrates an app that handles incoming custom URL scheme requests
// using the deprecated application:handleOpenURL: delegate method.
// The deprecated API lacks the options dictionary that provides source application context,
// preventing any source-based validation.

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        completion("""
        This app registers the custom URL scheme 'mastgtest://' and handles it \
        using the deprecated application:handleOpenURL: method. \
        Open this URL from another app or Safari to trigger the handler: mastgtest://action?param=value
        """)
    }
}

// FAIL: [MASTG-TEST-0x01] Deprecated URL scheme handler method used.
// The app implements application:handleOpenURL: (deprecated since iOS 9.0) instead
// of application:openURL:options:. The deprecated method lacks the options dictionary,
// which means the source application (UIApplicationOpenURLOptionsSourceApplicationKey)
// cannot be identified, preventing source-based validation.
@objc class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(_ application: UIApplication, handleOpen url: URL) -> Bool {
        let scheme = url.scheme ?? ""
        let host = url.host ?? ""
        let params = url.query ?? ""

        // No source validation is possible with this deprecated API.
        // Any app can trigger this handler without the app being able to identify the caller.
        if scheme == "mastgtest" {
            // Process URL without knowing who called it
            _ = "Handling URL: \(scheme)://\(host)?\(params)"
            return true
        }
        return false
    }
}
