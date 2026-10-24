import SwiftUI
import UIKit
import WebKit

struct MastgTest {
    
    private static let docDir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
    private static let fileURL = docDir.appendingPathComponent("index.html")
    private static let secretURL = docDir.appendingPathComponent("secret.txt")
    
    public static func mastgTest(completion: @escaping (String) -> Void) {
        createSecretFile()
        createHtmlFile()
        
        DispatchQueue.main.async {
            showUserInput { userInput in
                completion(#"Filling user input into the webview... "\#(userInput)""#)
                  showHtmlRegistrationView(username: userInput, completion: completion)
                
            }
        }
    }
    
    private static func showHtmlRegistrationView(username: String, completion: @escaping (String) -> Void) {
        DispatchQueue.main.async {
          let urlWithUsername = URL(string: "\(fileURL.absoluteString)?username=\(username)")
          guard let urlWithUsername = urlWithUsername else{
            completion("Failed create URL object.")
            return
          }
          let webView = WKWebView()
          webView.loadFileURL(urlWithUsername, allowingReadAccessTo: docDir)
            
            let vc = UIViewController()
            vc.view = webView
            
            guard let presenter = topViewController() else {
                completion("Failed to present: no view controller.")
                return
            }
            
            presenter.present(vc, animated: true)
        }
    }
    
    static func createHtmlFile() {
        let htmlContent = """
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
            <p id="username"></p>
        </body>
        </html>
        <script>
            const name = new URLSearchParams(window.location.search).get('username');
            document.getElementById('username').innerHTML = name;
        </script>
        """
        try? htmlContent.write(to: fileURL, atomically: true, encoding: .utf8)
    }
    
  
    static func createSecretFile() {
        try? "MY SECRET CONTENT".write(to: secretURL, atomically: true, encoding: .utf8)
    }
    
    private static func showUserInput(completion: @escaping (String) -> Void) {
        let alert = UIAlertController(title: "Enter your username", message: nil, preferredStyle: .alert)
        alert.addTextField { $0.placeholder = "Name" }
        alert.addAction(UIAlertAction(title: "OK", style: .default) { _ in
            completion(alert.textFields?.first?.text ?? "")
        })
        
        topViewController()?.present(alert, animated: true)
    }

    private static func topViewController(base: UIViewController? = nil) -> UIViewController? {
        let root = base ?? UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }.flatMap { $0.windows }
            .first { $0.isKeyWindow }?.rootViewController
        
        if let nav = root as? UINavigationController { return topViewController(base: nav.visibleViewController) }
        if let tab = root as? UITabBarController { return topViewController(base: tab.selectedViewController) }
        if let presented = root?.presentedViewController { return topViewController(base: presented) }
        return root
    }
}
