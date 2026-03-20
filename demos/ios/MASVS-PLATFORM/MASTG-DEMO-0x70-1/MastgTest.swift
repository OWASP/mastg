import SwiftUI

// SUMMARY: This sample demonstrates insecure Universal Link handling: a wildcard associated-domains entitlement, a receiver that blindly accepts incoming URLs without validation, and an outgoing link that passes unvalidated URLs to UIApplication.shared.open.

// Inherits NSObject so the Objective-C selectors are visible to binary analysis tools.
class MastgTest: NSObject {

    static func mastgTest(completion: @escaping (String) -> Void) {
        var results = ""
        let testInstance = MastgTest()

        // Simulated incoming malicious Universal Link
        let incomingUrl = URL(string: "https://attacker.example.com/reset_password?token=malicious_123")!
        let userActivity = NSUserActivity(activityType: NSUserActivityTypeBrowsingWeb)
        userActivity.webpageURL = incomingUrl

        results += "--- Entitlements ---\n"
        results += testInstance.readEntitlementsFile() + "\n"

        results += "--- Receiver & Handler ---\n"
        let didAccept = testInstance.application(UIApplication.shared, continue: userActivity, restorationHandler: { _ in })
        results += "App blindly accepted and processed link from attacker.example.com: \(didAccept)\n\n"

        results += "--- Outgoing App Links ---\n"
        let maliciousExternalURL = URL(string: "malicious-app://steal-data?payload=123")!
        testInstance.openOtherAppLinkInsecurely(url: maliciousExternalURL)
        results += "App blindly attempted to execute \(maliciousExternalURL.scheme ?? "unknown") scheme.\n\n"

        completion(results)
    }

    // MARK: - Entitlements

    // FAIL: [MASTG-TEST-0070-1] The app declares a wildcard associated-domains entitlement (applinks:*.example.com), expanding the attack surface to any subdomain.
    func readEntitlementsFile() -> String {
        guard let path = Bundle.main.path(forResource: "entitlements", ofType: "plist"),
              let dict = NSDictionary(contentsOfFile: path) as? [String: Any] else {
            return "Error: Could not find or read entitlements.plist in the app bundle."
        }

        if let associatedDomains = dict["com.apple.developer.associated-domains"] as? [String] {
            let domainsString = associatedDomains.joined(separator: "\n  - ")
            return "Found Associated Domains:\n  - \(domainsString)\n\nNOTE: Wildcards (*) increase attack surface!"
        } else {
            return "No Associated Domains found in entitlements."
        }
    }

    // MARK: - Insecure Receiver

    // FAIL: [MASTG-TEST-0070-3] The app implements the application:continue:restorationHandler: delegate method, confirming the Universal Link attack surface in the binary.
    // FAIL: [MASTG-TEST-0070-4] The receiver extracts the webpageURL directly without validating the host, path, or query parameters using URLComponents.
    // @objc exposes this selector to the Objective-C runtime so it appears in binary analysis output.
    @objc func application(_ application: UIApplication, continue userActivity: NSUserActivity, restorationHandler: @escaping ([Any]?) -> Void) -> Bool {
        if userActivity.activityType == NSUserActivityTypeBrowsingWeb {
            if let url = userActivity.webpageURL {
                print("VULNERABLE: Processing URL directly - \(url.absoluteString)")
                return true
            }
        }
        return false
    }

    // MARK: - Insecure Outgoing Link

    // FAIL: [MASTG-TEST-0070-5] The app imports and uses openURL:options:completionHandler:, passing an unvalidated URL directly to UIApplication.shared.open.
    // @objc exposes this selector to the Objective-C runtime so it appears in binary analysis output.
    @objc func openOtherAppLinkInsecurely(url: URL) {
        print("VULNERABLE: Executing outgoing link to \(url.absoluteString)")
        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }
}
