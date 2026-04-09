import UIKit
import WebKit

struct MastgTest {

    private static let cacheDir = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first!
    private static let demoRoot = cacheDir.appendingPathComponent("demoRoot", isDirectory: true)
    private static let appDir = demoRoot.appendingPathComponent("app", isDirectory: true)
    private static let otherDir = demoRoot.appendingPathComponent("other", isDirectory: true)

    private static let indexURL = appDir.appendingPathComponent("index.html")
    private static let apiKeyURL = appDir.appendingPathComponent("api-key.txt")
    private static let otherURL = otherDir.appendingPathComponent("other.html")

    public static func mastgTest(completion: @escaping (String) -> Void) {
        createDirectories()
        createSecretFile()
        createOtherFile()
        createHtmlFile()

        DispatchQueue.main.async {
            showWebView(completion: completion)
        }
    }

    private static func showWebView(completion: @escaping (String) -> Void) {
        let configuration = WKWebViewConfiguration()
        let preferences = WKWebpagePreferences()

        // CONDITION 1: JavaScript allowed, default: true
        preferences.allowsContentJavaScript = true
        configuration.defaultWebpagePreferences = preferences

        // CONDITION 2: file:// URLs must have access to files.
        // This demo sets it to true so the fetch succeeds; change to false to block the fetch.
        configuration.preferences.setValue(true, forKey: "allowFileAccessFromFileURLs")
        configuration.setValue(false, forKey: "allowUniversalAccessFromFileURLs")

        let webView = WKWebView(frame: .zero, configuration: configuration)

        let vc = UIViewController()
        vc.view = webView

        guard let presenter = topViewController() else {
            completion("Failed to present, no view controller.")
            return
        }

        presenter.present(vc, animated: true) {
            //completion("Opening WebView...")
            completion(fileTreeString(at: demoRoot))
            
            // CONDITION 3: Overly broad file read access to demoRoot
            // JavaScript will be able to access any file within demoRoot
            // the content in the iframe will show
            webView.loadFileURL(indexURL, allowingReadAccessTo: demoRoot)
            
            // FIX: restrict allowingReadAccessTo to indexURL or appDir:
            // JavaScript will lose access to the files
            // the content in the iframe will be blocked
            // webView.loadFileURL(indexURL, allowingReadAccessTo: indexURL)
            
        }
    }

    private static func createDirectories() {
        try? FileManager.default.createDirectory(at: appDir, withIntermediateDirectories: true)
        try? FileManager.default.createDirectory(at: otherDir, withIntermediateDirectories: true)
    }
    
    private static func fileTreeString(at url: URL, indent: String = "") -> String {
        var isDirectory: ObjCBool = false
        let exists = FileManager.default.fileExists(atPath: url.path, isDirectory: &isDirectory)

        guard exists else {
            return "\(indent)\(url.lastPathComponent) [missing]"
        }

        if !isDirectory.boolValue {
            return "\(indent)\(url.lastPathComponent)"
        }

        let children = (try? FileManager.default.contentsOfDirectory(
            at: url,
            includingPropertiesForKeys: [.isDirectoryKey],
            options: [.skipsHiddenFiles]
        ))?
        .sorted { $0.lastPathComponent < $1.lastPathComponent } ?? []

        var lines = ["\(indent)\(url.lastPathComponent)/"]

        for child in children {
            lines.append(fileTreeString(at: child, indent: indent + "    "))
        }

        return lines.joined(separator: "\n")
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

            <h3>JavaScript File Access via <code>fetch</code>:</h3>
            <p>Attempting to read: <code>./api-key.txt</code></p>
            <pre id="result">Loading...</pre>

            <script>
                async function readLocalSecret() {
                    const result = document.getElementById("result");
                    try {
                        const response = await fetch("./api-key.txt");
                        const text = await response.text();
                        result.textContent = text;
                        result.style.color = "green";
                    } catch (error) {
                        result.textContent = "BLOCKED: Failed to read local file: " + error;
                        result.style.color = "red";
        
                    }
                }

                readLocalSecret();
            </script>

            <h3>JavaScript File Access via <code>XMLHttpRequest</code>:</h3>
            <p>Attempting to read: <code>../other/other.html</code></p>
            <div id="result2" style="border: 1px solid #ccc; min-height: 220px;"></div>

            <script>
                function readLocalPage() {
                    const display = document.getElementById("result2");
                    const xhr = new XMLHttpRequest();

                    xhr.open("GET", "../other/other.html", true);

                    xhr.onload = function () {
                        if (xhr.status === 200 || (xhr.status === 0 && xhr.responseText.length > 0)) {
                            const frame = document.createElement("iframe");
                            frame.width = "100%";
                            frame.height = "200";
                            frame.style.border = "0";
                            frame.srcdoc = xhr.responseText;

                            display.innerHTML = "";
                            display.appendChild(frame);
                        } else {
                            display.textContent = "BLOCKED: Failed with status " + xhr.status;
                            display.style.color = "red";
                        }
                    };

                    xhr.onerror = function () {
                        display.textContent = "BLOCKED: Failed to read local file.";
                        display.style.color = "red";
                    };

                    xhr.send();
                }

                setTimeout(readLocalPage, 500);
            </script>

            <h3>Direct Embedding into <code>&lt;iframe src=</code>:</h3>
            <p>Attempting to read: <code>../other/other.html</code></p>
            <iframe src="../other/other.html" width="100%" height="200"></iframe>

        </body>

        </html>
        """
        try? htmlContent.write(to: indexURL, atomically: true, encoding: .utf8)
    }

    private static func createOtherFile() {
        let otherHtmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Other File</title>
        </head>
        <body style="background-color: green;">
            <h4>Loaded from sibling folder</h4>
            <p>If you see this green iframe, the iframe loaded.</p>
        </body>
        </html>
        """
        try? otherHtmlContent.write(to: otherURL, atomically: true, encoding: .utf8)
    }

    private static func createSecretFile() {
        try? "MASTG_API_KEY=072037ab-1b7b-4b3b-8b7b-1b7b4b3b8b7b".write(to: apiKeyURL, atomically: true, encoding: .utf8)
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
