import UIKit

// SUMMARY: This sample collects a banking PIN and allows third-party keyboards app-wide, because its
// app delegate returns true from application(_:shouldAllowExtensionPointIdentifier:) for every
// extension point, including custom keyboards. With an installed third-party keyboard enabled, the
// user can type the PIN with it.

struct MastgTest {

    public static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            let alert = UIAlertController(
                title: "Sign in",
                message: "Enter your banking PIN.",
                preferredStyle: .alert
            )

            // The PIN is sensitive input, but the keyboard used for this field is whichever the user
            // has selected, because the app does not restrict custom keyboards (see AppDelegate below).
            alert.addTextField { tf in
                tf.placeholder = "Banking PIN"
                tf.keyboardType = .numberPad
                tf.accessibilityIdentifier = "pin_field"
            }

            alert.addAction(UIAlertAction(title: "Sign in", style: .default, handler: { _ in
                let pin = alert.textFields?[0].text ?? ""
                completion("Entered banking PIN: \(pin)")
            }))

            if let presenter = topViewController() {
                presenter.present(alert, animated: true)
            } else {
                completion("Failed to present input form.")
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

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication,
                     shouldAllowExtensionPointIdentifier extensionPointIdentifier: UIApplication.ExtensionPointIdentifier) -> Bool {
        // FAIL: [MASTG-TEST-0x02] The app allows every extension point, including custom keyboards
        // (`.keyboard`), so the user can type the banking PIN above with an installed third-party
        // keyboard. Returning `false` for `.keyboard` would force the system keyboard app-wide.
        return true
    }
}
