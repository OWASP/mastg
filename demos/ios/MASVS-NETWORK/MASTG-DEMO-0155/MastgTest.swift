import UIKit
import WebKit

// SUMMARY: This sample demonstrates a custom WKNavigationDelegate that bypasses server certificate validation
// for WebView connections by accepting any certificate without calling SecTrustEvaluateWithError.

class InsecureWKNavigationDelegate: NSObject, WKNavigationDelegate {
    // FAIL: [MASTG-TEST-0397] Accepts any certificate in WebView without evaluating server trust
    func webView(_ webView: WKWebView, didReceive challenge: URLAuthenticationChallenge,
                 completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        guard let serverTrust = challenge.protectionSpace.serverTrust else {
            completionHandler(.cancelAuthenticationChallenge, nil)
            return
        }
        // Insecure: creates a credential from serverTrust without ever calling SecTrustEvaluateWithError.
        // This accepts expired, self-signed, or wrong-hostname certificates in the WebView.
        completionHandler(.useCredential, URLCredential(trust: serverTrust))
    }
}

struct MastgTest {
    private static var navigationDelegate: InsecureWKNavigationDelegate?

    static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            let navDelegate = InsecureWKNavigationDelegate()
            MastgTest.navigationDelegate = navDelegate

            let webView = WKWebView(frame: .zero)
            webView.navigationDelegate = navDelegate

            let vc = UIViewController()
            vc.view = webView

            guard let presenter = topViewController() else {
                completion("Failed to present: no view controller.")
                return
            }

            presenter.present(vc, animated: true) {
                guard let url = URL(string: "https://self-signed.badssl.com/") else {
                    completion("Invalid URL")
                    return
                }
                // self-signed.badssl.com serves a self-signed certificate not trusted by the system.
                // A correctly implemented delegate would cancel this connection.
                webView.load(URLRequest(url: url))
                completion("The self-signed certificate was accepted because the delegate did not call SecTrustEvaluateWithError.")
            }
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
