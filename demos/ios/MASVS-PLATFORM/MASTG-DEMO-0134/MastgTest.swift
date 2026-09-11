import SwiftUI

// SUMMARY: This sample demonstrates a custom URL scheme handler that accepts
// URL parameters without input validation. The handler reads the "amount"
// query parameter and uses it directly without bounds-checking or type conversion.

enum URLState {
    static var lastURL: URL?
}

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        guard let url = URLState.lastURL else {
            completion("""
            Waiting for incoming URL scheme...

            Open the registered scheme from another app to see the result:
              mastgtest://transfer?amount=500
            """)
            return
        }
        handleURL(url, completion: completion)
    }

    // FAIL: [MASTG-TEST-0370] The handler uses the "amount" parameter directly
    // without bounds-checking or type validation.
    @inline(never) @_optimize(none)
    public static func handleURL(_ url: URL, completion: @escaping (String) -> Void) {
        let components = URLComponents(url: url, resolvingAgainstBaseURL: false)
        let action = url.host ?? ""

        if action == "transfer" {
            let amount = components?.queryItems?.first(where: { $0.name == "amount" })?.value ?? "0"
            completion("Transferring \(amount) units")
            return
        }
        completion("Unknown action: \(action)")
    }
}
