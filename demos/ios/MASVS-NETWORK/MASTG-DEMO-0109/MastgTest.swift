import Foundation

struct MastgTest {
    // SUMMARY: This sample demonstrates two ATS TLS policy exceptions in Info.plist:
    // 1. NSExceptionMinimumTLSVersion = TLSv1.0 for tls-v1-0.badssl.com (allows TLS 1.0, including subdomains).
    // 2. NSExceptionRequiresForwardSecrecy = false for static-rsa.badssl.com (disables the PFS requirement).

    // FAIL: [MASTG-TEST-0x01] NSExceptionMinimumTLSVersion = TLSv1.0 with NSIncludesSubdomains = true.
    static let tls10Endpoint = "https://tls-v1-0.badssl.com:1010/"

    // FAIL: [MASTG-TEST-0x01] NSExceptionRequiresForwardSecrecy = false.
    static let noPfsEndpoint = "https://static-rsa.badssl.com/"

    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing ATS TLS policy exceptions:\n\n"
        let group = DispatchGroup()

        for endpoint in [tls10Endpoint, noPfsEndpoint] {
            guard let url = URL(string: endpoint) else {
                result += "Invalid URL: \(endpoint)\n"
                continue
            }

            group.enter()
            URLSession.shared.dataTask(with: url) { _, response, error in
                if let error = error as NSError? {
                    result += "\(endpoint) failed: \(error.localizedDescription)\n"
                } else if let httpResponse = response as? HTTPURLResponse {
                    result += "\(endpoint) returned status: \(httpResponse.statusCode)\n"
                } else {
                    result += "\(endpoint) completed without HTTP response.\n"
                }
                group.leave()
            }.resume()
        }

        group.notify(queue: .main) {
            completion(result)
        }
    }
}
