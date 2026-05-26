import UIKit
import WebKit

// SUMMARY: This sample demonstrates DOM inspection using evaluateJavaScript without
// a content world parameter. The scripts run in the page world (.page), where the
// prototype chain is shared with page JavaScript. A malicious page can override
// document.querySelector or other built-ins before these calls run, causing the app
// to receive attacker-controlled values instead of real DOM content.

class MastgTest: NSObject, WKScriptMessageHandler {
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
            <div id="account-number">ACC-9876543210</div>
            <div id="balance">$4,200.00</div>
            <form id="transferForm">
                <input type="text" name="recipient" value="alice@example.com" />
                <input type="number" name="amount" value="100" />
            </form>
        </body>
        </html>
        """

        webView.loadHTMLString(html, baseURL: nil)

        webView.navigationDelegate = self as? WKNavigationDelegate

        // FAIL: [MASTG-TEST-0x04] evaluateJavaScript runs in the page world.
        // A malicious page can override document.querySelector before this executes:
        //   document.querySelector = () => ({ textContent: "ATTACKER_CONTROLLED" })
        webView.evaluateJavaScript("document.querySelector('#account-number').textContent",
                                   completionHandler: { value, _ in
            let account = value as? String ?? "unknown"
            completion("Account: \(account)")
        })

        // FAIL: [MASTG-TEST-0x04] evaluateJavaScript reads balance in the page world.
        webView.evaluateJavaScript("document.querySelector('#balance').textContent",
                                   completionHandler: { value, _ in
            let balance = value as? String ?? "unknown"
            completion("Balance: \(balance)")
        })

        // FAIL: [MASTG-TEST-0x04] reads form recipient in the page world.
        webView.evaluateJavaScript(
            "document.querySelector('input[name=recipient]').value",
            completionHandler: { value, _ in
                let recipient = value as? String ?? "unknown"
                completion("Recipient: \(recipient)")
            }
        )
    }
}
