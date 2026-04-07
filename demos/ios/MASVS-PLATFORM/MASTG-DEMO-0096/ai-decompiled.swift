import Foundation
import WebKit
import UIKit

enum MastgTest {
    static let docDir: URL = {
        FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    }()

    static let fileURL: URL = {
        docDir.appendingPathComponent("index.html")
    }()

    static let secretURL: URL = {
        docDir.appendingPathComponent("secret.txt")
    }()

    static func mastg(completion: @escaping () -> Void) {
        DispatchQueue.main.async {
            let secret = "MY SECRET"
            let html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { font-family: -apple-system, sans-serif; text-align: center; padding-top: 50px; }
                    h1 { color: #007AFF; }
                </style>
            </head>
            <body>
                <h1>Registration</h1>
                <p><b>First Name:</b></p>
                <p id="firstname"></p>
            </body>
            </html>
            """

            try? secret.write(to: secretURL, atomically: true, encoding: .utf8)
            try? html.write(to: fileURL, atomically: true, encoding: .utf8)

            DispatchQueue.main.async {
                completion()
            }
        }
    }

    static func showHtmlRegistrationView(
        firstname: String,
        completion: @escaping (String) -> Void
    ) {
        DispatchQueue.main.async {
            let webView = WKWebView()
            webView.loadFileURL(fileURL, allowingReadAccessTo: docDir)

            let viewController = UIViewController()
            viewController.view = webView

            guard let presenter = topViewController(base: nil) else {
                completion("Failed to present: no view controller.")
                return
            }

            presenter.present(viewController, animated: true) {
                let javascript = "document.getElementById('firstname').innerHTML = \"\(firstname)\""

                webView.evaluateJavaScript(javascript) { _, error in
                    if let error = error {
                        print("Injection error: \(error.localizedDescription)")
                    }
                }

                DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
                    viewController.dismiss(animated: true, completion: nil)
                    completion("Finished showing the WebView.")
                }
            }
        }
    }

    static func topViewController(base: UIViewController? = nil) -> UIViewController? {
        fatalError()
    }
}