import Foundation

// SUMMARY: Demonstrates both insecure (bypass) and secure (correct) URLSessionDelegate implementations
// connecting to expired.badssl.com. The insecure delegate accepts the expired certificate without
// evaluating trust; the secure delegate correctly rejects it via SecTrustEvaluateWithError.

class InsecureURLSessionDelegate: NSObject, URLSessionDelegate {
    // FAIL: [MASTG-TEST-0396] Accepts any certificate without evaluating server trust
    func urlSession(_ session: URLSession, didReceive challenge: URLAuthenticationChallenge,
                    completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        guard let serverTrust = challenge.protectionSpace.serverTrust else {
            completionHandler(.cancelAuthenticationChallenge, nil)
            return
        }
        // Insecure: creates a credential from serverTrust without calling SecTrustEvaluateWithError.
        completionHandler(.useCredential, URLCredential(trust: serverTrust))
    }
}

class SecureURLSessionDelegate: NSObject, URLSessionDelegate {
    // PASS: Correctly evaluates server trust before accepting the credential
    func urlSession(_ session: URLSession, didReceive challenge: URLAuthenticationChallenge,
                    completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        guard challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust,
              let serverTrust = challenge.protectionSpace.serverTrust else {
            completionHandler(.performDefaultHandling, nil)
            return
        }
        var error: CFError?
        if SecTrustEvaluateWithError(serverTrust, &error) {
            completionHandler(.useCredential, URLCredential(trust: serverTrust))
        } else {
            completionHandler(.cancelAuthenticationChallenge, nil)
        }
    }
}

struct MastgTest {
    private static var insecureDelegate: InsecureURLSessionDelegate?
    private static var secureDelegate: SecureURLSessionDelegate?

    static func mastgTest(completion: @escaping (String) -> Void) {
        guard let url = URL(string: "https://expired.badssl.com/") else {
            DispatchQueue.main.async { completion("Invalid URL") }
            return
        }

        let insecureDel = InsecureURLSessionDelegate()
        MastgTest.insecureDelegate = insecureDel
        let insecureSession = URLSession(configuration: .default, delegate: insecureDel, delegateQueue: nil)

        insecureSession.dataTask(with: url) { _, response, error in
            let insecureResult: String
            if let http = response as? HTTPURLResponse {
                insecureResult = "INSECURE: expired.badssl.com accepted (HTTP \(http.statusCode)) — SecTrustEvaluateWithError not called"
            } else {
                insecureResult = "INSECURE: \(error?.localizedDescription ?? "unknown error")"
            }

            let secureDel = SecureURLSessionDelegate()
            MastgTest.secureDelegate = secureDel
            let secureSession = URLSession(configuration: .default, delegate: secureDel, delegateQueue: nil)

            secureSession.dataTask(with: url) { _, response, error in
                let secureResult: String
                if let http = response as? HTTPURLResponse {
                    secureResult = "SECURE (unexpected): expired.badssl.com accepted (HTTP \(http.statusCode))"
                } else {
                    secureResult = "SECURE: expired.badssl.com rejected — \(error?.localizedDescription ?? "certificate invalid")"
                }
                DispatchQueue.main.async {
                    completion("\(insecureResult)\n\n\(secureResult)")
                }
            }.resume()
        }.resume()
    }
}
