import UIKit
import WebKit

// SUMMARY: This sample demonstrates sensitive data being written into the WebView DOM
// via evaluateJavaScript. The app loads a page with placeholder elements and then
// injects a one-time-password directly into those elements using textContent assignments.
// Any JavaScript running on the page can read those values from the DOM at any time
// after injection.

class MastgTest: NSObject {
    private var webView: WKWebView?
    private static var currentTest: MastgTest?
    private static let secretOtp = "482910"

    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            let test = MastgTest()
            currentTest = test
            test.showWebView(completion: completion)
        }
    }

    private func showWebView(completion: @escaping (String) -> Void) {
        let config = WKWebViewConfiguration()
        let webView = WKWebView(frame: UIScreen.main.bounds, configuration: config)
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        self.webView = webView

        let html = """
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { font-family: -apple-system, sans-serif; text-align: center; padding-top: 50px; }
                div { font-size: 24px; font-weight: bold; color: #007AFF; margin-top: 20px; }
            </style>
        </head>
        <body>
            <p>Your one time password:</p>
            <div id="otp-display"></div>
        </body>
        </html>
        """

        webView.loadHTMLString(html, baseURL: nil)

        let vc = UIViewController()
        vc.view.backgroundColor = .white
        vc.view.addSubview(webView)

        guard let presenter = topViewController() else {
            completion("Failed to present: no view controller.")
            return
        }

        presenter.present(vc, animated: true) {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                // FAIL: [MASTG-TEST-0380] The OTP is written into the DOM via textContent.
                // Any page script can read it: document.getElementById('otp-display').textContent
                webView.evaluateJavaScript(
                    "document.getElementById('otp-display').textContent = '\(MastgTest.secretOtp)'",
                    completionHandler: nil
                )

                /*
                // PASS: Instead of injecting sensitive data into the DOM, we
                // render a native view over it.
                // The sensitive data remains in the native layer and never enters the DOM.
                let frame = CGRect(
                    x: 0,
                    y: 0,
                    width: 200,
                    height: 44
                )
                let otpLabel = UILabel(frame: frame)
                otpLabel.text = MastgTest.secretOtp
                otpLabel.textAlignment = .center
                otpLabel.textColor = .systemGreen
                otpLabel.font = .boldSystemFont(ofSize: 28)
                otpLabel.center = vc.view.center
                vc.view.addSubview(otpLabel)
                vc.view.bringSubviewToFront(otpLabel)
                */

                completion("Sensitive data injected into DOM.")
            }
        }
    }

    private func topViewController(base: UIViewController? = nil) -> UIViewController? {
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
