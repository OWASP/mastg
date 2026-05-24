import SwiftUI
import UIKit

// SUMMARY: This sample demonstrates an app that handles custom URL scheme requests
// using the modern application:openURL:options: method, but without validating
// the URL parameters or checking the source application before performing a
// sensitive operation (a simulated fund transfer).

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        completion("""
        This app registers the custom URL scheme 'mastgtest://' and handles it \
        using application:openURL:options:. \
        Open this URL to trigger a simulated transfer: mastgtest://transfer?amount=1000
        """)
    }
}

// FAIL: [MASTG-TEST-0x02] URL scheme handler does not validate URL parameters or source application.
// The handler accepts any value for the 'amount' parameter and does not check
// UIApplicationOpenURLOptionsSourceApplicationKey before performing a sensitive operation.
@objc class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {
        // Missing: no validation of url.scheme against an allowlist
        guard url.scheme == "mastgtest" else { return false }

        let components = URLComponents(url: url, resolvingAgainstBaseURL: false)
        let action = url.host ?? ""

        if action == "transfer" {
            // Missing: no check of UIApplicationOpenURLOptionsSourceApplicationKey
            // Missing: no validation or bounds checking of the 'amount' parameter
            let amount = components?.queryItems?.first(where: { $0.name == "amount" })?.value ?? "0"

            // Sensitive operation triggered without source or parameter validation
            _ = "Initiating transfer of \(amount) units"
            return true
        }
        return false
    }
}
