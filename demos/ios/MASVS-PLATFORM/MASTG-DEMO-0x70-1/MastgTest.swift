import SwiftUI

// SUMMARY: This sample demonstrates insecure Universal Link handling: a wildcard associated-domains entitlement, a data handler that blindly accepts incoming URLs without validation, and an outgoing link that passes unvalidated URLs to UIApplication.shared.open.

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

        results += "--- Receiver & Data Handler ---\n"
        // Simulating the modern SceneDelegate Universal Link routing
        testInstance.scene(nil, continue: userActivity)
        results += "App blindly accepted and processed link from attacker.example.com\n\n"

        results += "--- Outgoing App Links ---\n"
        let maliciousExternalURL = URL(string: "malicious-app://steal-data?payload=123")!
        testInstance.openOtherAppLinkInsecurely(url: maliciousExternalURL)
        results += "App blindly attempted to execute \(maliciousExternalURL.scheme ?? "unknown") scheme.\n\n"

        completion(results)
    }

    // FAIL: [MASTG-TEST-0x70-1] The app declares a wildcard associated-domains entitlement (applinks:*.example.com), expanding the attack surface to any subdomain.
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

    // FAIL: [MASTG-TEST-0x70-4] The binary implements the scene:continueUserActivity: selector, confirming the Universal Link receiver attack surface. The handler also extracts the webpageURL directly without validating the host, path, or query parameters.
    @objc(scene:continueUserActivity:)
    func scene(_ scene: UIScene?, continue userActivity: NSUserActivity) {
        if userActivity.activityType == NSUserActivityTypeBrowsingWeb {
            if let url = userActivity.webpageURL {
                // VULNERABLE: Direct processing of the raw URL without strict URLComponents validation
                print("VULNERABLE: Processing URL directly - \(url.absoluteString)")
            }
        }
    }
    
    // FAIL: [MASTG-TEST-0x70-5] The app imports and uses openURL:options:completionHandler:, passing an unvalidated URL directly to UIApplication.shared.open.
    @objc func openOtherAppLinkInsecurely(url: URL) {
        print("VULNERABLE: Executing outgoing link to \(url.absoluteString)")
        UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }
}
