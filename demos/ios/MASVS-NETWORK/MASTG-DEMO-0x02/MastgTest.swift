import Foundation

struct MastgTest {
    // SUMMARY: This sample demonstrates URLSessionConfiguration with an insecure minimum TLS version setting.

    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing URLSession with insecure TLS configuration:\n\n"

        let config = URLSessionConfiguration.default

        // FAIL: [MASTG-TEST-0x02] Setting minimum TLS version to TLS 1.1 allows insecure connections
        config.tlsMinimumSupportedProtocolVersion = tls_protocol_version_t(rawValue: 0x0302)! // TLS 1.1

        let session = URLSession(configuration: config)

        guard let url = URL(string: "https://httpbin.org/get") else {
            completion("Invalid URL")
            return
        }

        let task = session.dataTask(with: url) { _, response, error in
            if let error = error {
                result += "Request failed: \(error.localizedDescription)\n"
            } else if let httpResponse = response as? HTTPURLResponse {
                result += "Request succeeded with status: \(httpResponse.statusCode)\n"
                result += "Note: The session configuration allows connections with TLS 1.1 or higher.\n"
            }
            completion(result)
        }
        task.resume()
    }
}
