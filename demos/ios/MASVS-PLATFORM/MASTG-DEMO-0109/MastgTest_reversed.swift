import Foundation
import UIKit

struct MastgTest {

    // SUMMARY: This sample demonstrates sensitive data exposure through iOS IPC mechanisms.
    static func mastgTest() -> String {
        let accessToken = "tok_demo_sensitive_123456"

        // FAIL: [MASTG-TEST-0056] Sensitive token is written to the general pasteboard.
        UIPasteboard.general.string = accessToken

        // FAIL: [MASTG-TEST-0056] Sensitive token is added without localOnly/expiration restrictions.
        UIPasteboard.general.setItems([["public.utf8-plain-text": accessToken]])

        if let groupURL = FileManager.default.containerURL(forSecurityApplicationGroupIdentifier: "group.org.owasp.mastg.demo") {
            let sharedFile = groupURL.appendingPathComponent("session.txt")
            let coordinator = NSFileCoordinator()

            coordinator.coordinate(writingItemAt: sharedFile, options: [], error: nil) { url in
                try? accessToken.write(to: url, atomically: true, encoding: .utf8)
            }

            // FAIL: [MASTG-TEST-0056] Sensitive token is persisted in an app group shared container.
            return "Wrote token to pasteboard and shared container: \(sharedFile.path)"
        }

        return "Wrote token to pasteboard"
    }
}