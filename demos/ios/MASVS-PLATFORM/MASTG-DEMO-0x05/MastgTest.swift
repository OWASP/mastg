import UIKit
import WebKit

// SUMMARY: This sample demonstrates sensitive data being written into the WebView DOM
// via evaluateJavaScript. The app loads a page with placeholder elements and then
// injects a one-time password and an account balance directly into those elements
// using textContent assignments. Any JavaScript running on the page can read those
// values from the DOM at any time after injection.

class MastgTest: NSObject {
    private var webView: WKWebView?

    @inline(never) @_optimize(none)
    public func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            self.showWebView(completion: completion)
        }
    }

    private func showWebView(completion: @escaping (String) -> Void) {
        let config = WKWebViewConfiguration()
        let webView = WKWebView(frame: .zero, configuration: config)
        self.webView = webView

        let html = """
        <html>
        <body>
            <p>Your one-time password:</p>
            <div id="otp-display"></div>
            <p>Account balance:</p>
            <div id="balance-display"></div>
        </body>
        </html>
        """

        webView.loadHTMLString(html, baseURL: nil)

        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            // FAIL: [MASTG-TEST-0x05] The OTP is written into the DOM via textContent.
            // Any page script can read it: document.getElementById('otp-display').textContent
            webView.evaluateJavaScript(
                "document.getElementById('otp-display').textContent = '482910'",
                completionHandler: nil
            )

            // FAIL: [MASTG-TEST-0x05] The account balance is written into the DOM via textContent.
            // Any page script can read it: document.getElementById('balance-display').textContent
            webView.evaluateJavaScript(
                "document.getElementById('balance-display').textContent = '$4,200.00'",
                completionHandler: nil
            )

            completion("Sensitive data injected into DOM.")
        }
    }
}
