import SwiftUI
import UIKit

// SUMMARY: This sample demonstrates two custom URL scheme handlers registered in Info.plist:
// - "mastgtest://" (FAIL): the handler does not validate the source application or URL parameters.
// - "mastgtest-safe://" (PASS): the handler validates the source application and sanitizes parameters.
// Both are dispatched from the single application:openURL:options: delegate entry point.

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        completion("""
        This app registers two custom URL schemes:
        - mastgtest://    (insecure — no source or parameter validation)
        - mastgtest-safe:// (secure — validates source app and parameters)

        To trigger the insecure handler from the Notes app, create a note containing:
          mastgtest://transfer?amount=9999
        Then long-press the link and tap Open.

        To trigger the secure handler:
          mastgtest-safe://transfer?amount=100
        """)
    }
}

// FAIL: [MASTG-TEST-0x02] The "mastgtest" handler processes the URL without
// validating the source application or sanitizing the "amount" parameter.
// Any app or web page can trigger a fund transfer with an arbitrary amount.

// PASS: [MASTG-TEST-0x02] The "mastgtest-safe" handler reads
// UIApplicationOpenURLOptionsSourceApplicationKey and rejects callers whose
// bundle ID is not in the allowlist. It also validates the amount parameter
// before performing the sensitive operation.

@objc class AppDelegate: UIResponder, UIApplicationDelegate {

    private let allowedSources: Set<String> = [
        "com.apple.mobilenotes",
        "com.apple.MobileSafari"
    ]

    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:]
    ) -> Bool {

        // FAIL: [MASTG-TEST-0x02] "mastgtest://" handler — no source or parameter validation.
        if url.scheme == "mastgtest" {
            let components = URLComponents(url: url, resolvingAgainstBaseURL: false)
            let action = url.host ?? ""

            if action == "transfer" {
                // No check of UIApplicationOpenURLOptionsSourceApplicationKey.
                // No bounds-checking or sanitization of "amount".
                let amount = components?.queryItems?.first(where: { $0.name == "amount" })?.value ?? "0"
                _ = "Initiating transfer of \(amount) units"
                return true
            }
            return false
        }

        // PASS: [MASTG-TEST-0x02] "mastgtest-safe://" handler — validates source and parameters.
        if url.scheme == "mastgtest-safe" {
            // Check source application.
            let source = options[.sourceApplication] as? String ?? ""
            guard allowedSources.contains(source) else {
                return false
            }

            let components = URLComponents(url: url, resolvingAgainstBaseURL: false)
            let action = url.host ?? ""

            if action == "transfer" {
                // Validate and sanitize the "amount" parameter.
                guard let amountString = components?.queryItems?.first(where: { $0.name == "amount" })?.value,
                      let amount = Int(amountString),
                      amount > 0, amount <= 10_000 else {
                    return false
                }
                _ = "Initiating safe transfer of \(amount) units"
                return true
            }
            return false
        }

        return false
    }
}
