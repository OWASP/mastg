// AI-assisted reconstruction
// Derived from showWebView.asm, docDir-init.asm and fileURL-init.asm. May be inaccurate; the assembly is the authoritative source.

import Foundation
import UIKit
import WebKit

struct MastgTest {

    private static let docDir: URL = {
        FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    }()

    private static let fileURL: URL = {
        docDir.appendingPathComponent("index.html")
    }()

    private static let secretURL: URL = {
        docDir.appendingPathComponent("secret.txt")
    }()

    public static func mastgTest(completion: @escaping (String) -> Void) {
        showHtmlRegistrationView(username: "user", completion: completion)
    }

    public static func showHtmlRegistrationView(username: String, completion: @escaping (String) -> Void) {
        let urlString = fileURL.absoluteString + "?username=" + username

        guard let url = URL(string: urlString) else {
            completion("Failed create URL object.")
            return
        }

        let webView = WKWebView()
        webView.loadFileURL(url, allowingReadAccessTo: docDir)

        let viewController = UIViewController()
        viewController.view = webView

        guard let topViewController = topViewController(base: nil) else {
            completion("Failed to present: no view controller.")
            return
        }

        topViewController.present(viewController, animated: true, completion: nil)
    }

    private static func topViewController(base: UIViewController?) -> UIViewController? {
        let baseVC: UIViewController?

        if let base = base {
            baseVC = base
        } else {
            baseVC = UIApplication.shared.connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .flatMap { $0.windows }
                .first(where: { $0.isKeyWindow })?
                .rootViewController
        }

        if let nav = baseVC as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }

        if let tab = baseVC as? UITabBarController {
            return topViewController(base: tab.selectedViewController)
        }

        if let presented = baseVC?.presentedViewController {
            return topViewController(base: presented)
        }

        return baseVC
    }
}