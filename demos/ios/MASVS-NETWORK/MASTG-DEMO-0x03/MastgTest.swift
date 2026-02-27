import Foundation

struct MastgTest: URLSessionDelegate {
    // SUMMARY: This sample demonstrates certificate pinning via URLSessionDelegate.
    // The URLSession is initialized with this delegate to perform manual server trust evaluation
    // during each TLS handshake.

    // FAIL: [MASTG-TEST-0x03] The pinning implementation accepts any server credential
    // unconditionally, without comparing the certificate against an expected value.
    // This means the "pinning" check is always bypassed.
    func urlSession(
        _ session: URLSession,
        didReceive challenge: URLAuthenticationChallenge,
        completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void
    ) {
        guard challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust,
              let serverTrust = challenge.protectionSpace.serverTrust else {
            completionHandler(.performDefaultHandling, nil)
            return
        }
        // Insecure: accepts any server certificate without validating against a pinned value
        let credential = URLCredential(trust: serverTrust)
        completionHandler(.useCredential, credential)
    }

    static func mastgTest(completion: @escaping (String) -> Void) {
        let delegate = MastgTest()
        let session = URLSession(configuration: .default, delegate: delegate, delegateQueue: nil)
        let url = URL(string: "https://api.example.com/data")!
        let task = session.dataTask(with: url) { _, response, error in
            if let error = error {
                completion("Request failed: \(error.localizedDescription)")
            } else if let httpResponse = response as? HTTPURLResponse {
                completion("Request to api.example.com returned status: \(httpResponse.statusCode)")
            }
        }
        task.resume()
    }
}
