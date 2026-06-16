import UIKit
import WebKit

// SUMMARY: This sample demonstrates DOM inspection using evaluateJavaScript without
// a content world parameter. The scripts run in the page world (.page), where the
// prototype chain is shared with page JavaScript. A malicious page can override
// document.querySelector or other built-ins before these calls run, causing the app
// to receive attacker-controlled values instead of real DOM content.

class MastgTest: NSObject, WKNavigationDelegate {
    private var webView: WKWebView?
    private static var currentTest: MastgTest?
    private var completion: ((String) -> Void)?

    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            let test = MastgTest()
            currentTest = test
            test.showWebView(completion: completion)
        }
    }

    private func showWebView(completion: @escaping (String) -> Void) {
        self.completion = completion

        let config = WKWebViewConfiguration()
        let webView = WKWebView(frame: .zero, configuration: config)
        self.webView = webView

        let html = """
        <html>
        <body>
            <div id="recipient_account_number">ACC_9876543210</div>
        </body>
        </html>
        """

        webView.navigationDelegate = self
        webView.loadHTMLString(html, baseURL: nil)
    }

    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {

        // Maliciously injected code by an attacker. It overwrites the recipient data
        webView.evaluateJavaScript("""
            document.querySelector = function(selector) {
                return { textContent: "ATTACKER_CONTROLLED" };
            };
        """)

        // FAIL: [MASTG-TEST-0379] evaluateJavaScript runs in the page world.
        // A malicious page can override document.querySelector before this executes:
        // document.querySelector = () => ({ textContent: "ATTACKER_CONTROLLED" })
        webView.evaluateJavaScript("document.querySelector('#recipient_account_number').textContent",
                                   completionHandler: { [weak self] value, _ in
            let account = value as? String ?? "unknown"
            self?.completion?("Recipient Account Number: \(account)")
        })

        /*
        // PASS: Using WKContentWorld.defaultClient (or a custom world) provides isolation.
        // The script runs in a separate world where document.querySelector is the original
        // built-in, even if the page world has overridden it.
        webView.evaluateJavaScript("document.querySelector('#recipient_account_number').textContent",
                                   in: nil,
                                   in: .defaultClient) { [weak self] result in
            switch result {
            case .success(let value):
                let account = value as? String ?? "unknown"
                self?.completion?("Recipient Account Number: \(account)")
            case .failure:
                self?.completion?("Error: Failed to retrieve account number")
            }
        }
        */
    }
}
