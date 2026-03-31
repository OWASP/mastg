func completion(username: String, completionHandler: @escaping (Int, String) -> Void) {
    // The code resolves a base file URL (likely from a singleton or static property)
    guard let baseURL = MastgTest.fileURL else { return }
    
    // 1. Construct the URL with the username query parameter
    var urlString = baseURL.absoluteString
    urlString.append("?username=")
    urlString.append(username)
    
    // 2. Attempt to create a new URL object from the combined string
    if let finalURL = URL(string: urlString) {
        
        // 3. Initialize a WKWebView
        let webView = WKWebView(frame: .zero)
        
        // 4. Resolve the directory for read access (likely Documents Dir)
        let accessURL = MastgTest.docDir
        
        // 5. Load the local file into the web view
        // Assembly: objc_msgSend(webView, "loadFileURL:allowingReadAccessToURL:", finalURL, accessURL)
        webView.loadFileURL(finalURL, allowingReadAccessToURL: accessURL)
        
        // 6. Present the WebView in a new View Controller
        let viewController = UIViewController()
        viewController.view = webView
        
        // Find the top-most view controller to present from
        if let topVC = MastgTest.topViewController() {
            topVC.present(viewController, animated: true, completion: nil)
        } else {
            // Error path: "Failed to present: no view controller."
            completionHandler(13, "Failed to present: no view controller.")
        }
        
    } else {
        // Error path: "Failed create URL object."
        completionHandler(25, "Failed create URL object.")
    }
}
