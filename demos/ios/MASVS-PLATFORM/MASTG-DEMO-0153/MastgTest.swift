import SwiftUI

// SUMMARY: This sample demonstrates a universal link handler that accepts the
// URL path and query parameters without input validation. The handler reads the
// "amount" query parameter from the verified domain's webpageURL and uses it
// directly, without converting it to a numeric type or checking it against any bounds.

enum UniversalLinkState {
    static var lastActivity: NSUserActivity?
}

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        // In a real app, the system delivers the universal link through
        // onContinueUserActivity(NSUserActivityTypeBrowsingWeb) (see MASTestAppApp.swift).
        // For a self-contained demo, simulate an incoming universal link to the
        // app's verified domain when no real activity has been received.
        let activity = UniversalLinkState.lastActivity ?? {
            let a = NSUserActivity(activityType: NSUserActivityTypeBrowsingWeb)
            a.webpageURL = URL(string: "https://demo.mas.owasp.org/transfer?amount=9999999")
            return a
        }()
        handleUniversalLink(activity, completion: completion)
    }

    // FAIL: [MASTG-TEST-0395] The handler uses the "amount" parameter from the
    // universal link directly without bounds-checking or type validation.
    @inline(never) @_optimize(none)
    public static func handleUniversalLink(_ userActivity: NSUserActivity, completion: @escaping (String) -> Void) {
        guard userActivity.activityType == NSUserActivityTypeBrowsingWeb,
              let url = userActivity.webpageURL,
              let components = URLComponents(url: url, resolvingAgainstBaseURL: true) else {
            completion("Invalid universal link")
            return
        }

        if url.path == "/transfer" {
            let amount = components.queryItems?.first(where: { $0.name == "amount" })?.value ?? "0"
            completion("Transferring \(amount) units")
            return
        }
        completion("Unknown path: \(url.path)")
    }
}
