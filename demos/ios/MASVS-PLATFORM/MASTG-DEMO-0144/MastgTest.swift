import UIKit
import WebKit

// SUMMARY: This sample demonstrates a WKWebView that loads HTML containing a password field.
// The password input is rendered in the DOM, which means any JavaScript running on the page
// can read the typed value via element.value at any time, including XSS payloads.

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            showWebView(completion: completion)
        }
    }

    private static func showWebView(completion: @escaping (String) -> Void) {
        let config = WKWebViewConfiguration()
        let webView = WKWebView(frame: .zero, configuration: config)

        // FAIL: [MASTG-TEST-0378] The app loads HTML containing <input type="password">
        // into a WKWebView. The typed value is stored in element.value and is readable
        // by any JavaScript on the page, including XSS payloads:
        //   document.querySelector('input[type=password]').value
        let html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { font-family: -apple-system, sans-serif; text-align: center; padding-top: 60px; }
                input { width: 80%; padding: 10px; margin: 8px 0; font-size: 16px; }
                button { padding: 12px 24px; font-size: 16px; margin-top: 8px; }
            </style>
        </head>
        <body>
            <h2>Login</h2>
            <input type="text" id="username" placeholder="Username" />
            <input type="password" id="password" placeholder="Password" />
            <br>
            <button onclick="submitForm()">Sign In</button>
            <script>
                function submitForm() {
                    const user = document.getElementById('username').value;
                    // FAIL: [MASTG-TEST-0378] password value is accessible to page JavaScript
                    const pass = document.getElementById('password').value;
                    window.webkit.messageHandlers.bridge.postMessage({
                        action: 'login', username: user, password: pass
                    });
                }
            </script>
        </body>
        </html>
        """

        webView.loadHTMLString(html, baseURL: nil)

        let vc = UIViewController()
        vc.view = webView

        guard let presenter = topViewController() else {
            completion("Failed to present: no view controller.")
            return
        }

        presenter.present(vc, animated: true) {
            completion("WebView with password field loaded.")
        }
    }

    private static func topViewController(base: UIViewController? = nil) -> UIViewController? {
        let root = base ?? UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }?.rootViewController

        if let nav = root as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = root as? UITabBarController {
            return topViewController(base: tab.selectedViewController)
        }
        if let presented = root?.presentedViewController {
            return topViewController(base: presented)
        }
        return root
    }
}
