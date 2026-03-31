import SwiftUI
import UIKit
import WebKit

struct MastgTest {
  @inline(never) @_optimize(none)
  public static func mastgTest(completion: @escaping (String) -> Void) {
    DispatchQueue.main.async {
      // Build the alert
      completion("Showing WebView for 2s...")
      // 1. Create the WebView
      DispatchQueue.main.asyncAfter(deadline: .now() + 1){
        let webView = UIWebView()
        webView.loadRequest(URLRequest(url: URL(string: "https://owasp.org")!))
        
        // 2. Create a UIViewController to hold the WebView
        let viewControllerToPresent = UIViewController()
        viewControllerToPresent.view = webView
        // Present from the topmost view controller
        if let presenter = topViewController() {
          presenter.present(viewControllerToPresent, animated: true, completion: {})
        } else {
          completion("Failed to present web view (no active view controller).")
        }
      }
    }
  }

//   Finds the currently visible view controller to present from
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
