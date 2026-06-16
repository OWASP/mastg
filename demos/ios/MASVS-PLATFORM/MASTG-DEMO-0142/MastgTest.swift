import UIKit
import WebKit

// SUMMARY: This sample demonstrates a WKWebView with a WKScriptMessageHandler named "bridge"
// that exposes sensitive native functionality to JavaScript running inside the WebView.
// Any JavaScript in the page can call:
//   window.webkit.messageHandlers.bridge.postMessage({"action": "getSecret"})
// to invoke native code and receive a sensitive API key in response.

// FAIL: [MASTG-TEST-0376] The SecretBridgeHandler exposes sensitive native data
// (a stored API key) to any JavaScript running in the WebView without validating
// the origin or the content of the incoming message.
class SecretBridgeHandler: NSObject, WKScriptMessageHandler {
    func userContentController(_ userContentController: WKUserContentController,
                               didReceive message: WKScriptMessage) {
        guard let body = message.body as? [String: String],
              let action = body["action"] else { return }

        switch action {
        case "getSecret":
            // FAIL: [MASTG-TEST-0376] Sensitive data (API key) is returned to JavaScript
            // without validating the origin of the message.
            let secret = "MASTG_API_KEY=072037ab-1b7b-4b3b-8b7b-1b7b4b3b8b7b"
            let js = "window.receiveSecret('\(secret)')"
            message.webView?.evaluateJavaScript(js, completionHandler: nil)
        case "getCredentials":
            // FAIL: [MASTG-TEST-0376] User credentials are returned to JavaScript
            // without any input validation or origin check.
            let credentials = "user=admin&pass=S3cr3t!"
            let js = "window.receiveCredentials('\(credentials)')"
            message.webView?.evaluateJavaScript(js, completionHandler: nil)
        default:
            break
        }
    }
}

struct MastgTest {
    @inline(never) @_optimize(none)
    public static func mastgTest(completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
            showWebView(completion: completion)
        }
    }

    private static func showWebView(completion: @escaping (String) -> Void) {
        let config = WKWebViewConfiguration()

        // FAIL: [MASTG-TEST-0376] A native bridge named "bridge" is registered via
        // add(_:name:), exposing SecretBridgeHandler to any JavaScript in the WebView.
        config.userContentController.add(SecretBridgeHandler(), name: "bridge")

        let webView = WKWebView(frame: .zero, configuration: config)

        let html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { font-family: -apple-system, sans-serif; text-align: center; padding-top: 50px; }
                button { padding: 10px 20px; margin: 5px; font-size: 16px; }
                pre { text-align: left; padding: 10px; background: #f4f4f4; border-radius: 4px; }
            </style>
        </head>
        <body>
            <h1>Secure Portal</h1>
            <button onclick="getSecret()">Get API Key</button>
            <button onclick="getCredentials()">Get Credentials</button>
            <pre id="result">Press a button to invoke the native bridge.</pre>
            <script>
                window.receiveSecret = function(secret) {
                    document.getElementById('result').textContent = 'API Key: ' + secret;
                };
                window.receiveCredentials = function(creds) {
                    document.getElementById('result').textContent = 'Credentials: ' + creds;
                };
                function getSecret() {
                    window.webkit.messageHandlers.bridge.postMessage({action: 'getSecret'});
                }
                function getCredentials() {
                    window.webkit.messageHandlers.bridge.postMessage({action: 'getCredentials'});
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
            completion("WebView with native bridge loaded.")
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
