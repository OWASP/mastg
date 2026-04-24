import UIKit

// SUMMARY: This sample demonstrates creating a UIActivityViewController that shares sensitive data
// without restricting the available activity types via excludedActivityTypes. This allows the data
// to be shared through any system activity, including AirDrop, Mail, Messages, social networks, etc.

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            // Sensitive data prepared for sharing
            let sensitiveText = "Account token: s3cr3t-t0ken-ABCD1234XYZ"
            let sensitiveURL = URL(string: "https://example.com/private-report?key=s3cr3t")!

            // FAIL: [MASTG-TEST-0x71] UIActivityViewController is created with sensitive data and
            // excludedActivityTypes is not set, allowing sharing via any system activity type.
            let activityVC = UIActivityViewController(
                activityItems: [sensitiveText, sensitiveURL],
                applicationActivities: nil
            )
            // excludedActivityTypes is not configured — all system activities are available.

            if let presenter = topViewController() {
                presenter.present(activityVC, animated: true, completion: nil)
                completion("UIActivityViewController presented with sensitive data (no excluded activity types)")
            } else {
                completion("Failed to present UIActivityViewController (no active view controller).")
            }
        }
    }

    private static func topViewController(
        base: UIViewController? = {
            let scenes = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
            let keyWindow = scenes
                .flatMap { $0.windows }
                .first { $0.isKeyWindow }
            return keyWindow?.rootViewController
        }()
    ) -> UIViewController? {
        if let nav = base as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController {
            return topViewController(base: tab.selectedViewController)
        }
        if let presented = base?.presentedViewController {
            return topViewController(base: presented)
        }
        return base
    }
}
