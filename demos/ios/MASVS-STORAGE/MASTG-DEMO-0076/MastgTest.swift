import SwiftUI
import UIKit

struct MastgTest {

  static func mastgTest(completion: @escaping (String) -> Void) {
    DispatchQueue.main.async {
      // Build the alert
      let alert = UIAlertController(
        title: "Enter sensitive text",
        message: "Please fill in all fields.",
        preferredStyle: .alert
      )

      alert.addTextField { tf in
        tf.placeholder = "Name"
        tf.autocorrectionType = .default
        tf.spellCheckingType = .no
        tf.accessibilityIdentifier = "name_field"
      }
      alert.addTextField { tf in
        tf.placeholder = "E-Mail"
        tf.autocorrectionType = .no
        tf.accessibilityIdentifier = "email_field"
      }
      
      alert.addTextField { tf in
        tf.placeholder = "Password"
        tf.isSecureTextEntry = true
        tf.accessibilityIdentifier = "password_field"
      }

      alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { _ in
        let first = alert.textFields?[0].text ?? ""
        let second = alert.textFields?[1].text ?? ""
        let third = alert.textFields?[2].text ?? ""
        completion("Submitted values: '\(first)', '\(second)', \(third)")
      }))

      // Present from the topmost view controller
      if let presenter = topViewController() {
        presenter.present(alert, animated: true, completion: nil)
      } else {
        completion("Failed to present alert (no active view controller).")
      }
    }
  }

  // Finds the currently visible view controller to present from
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
