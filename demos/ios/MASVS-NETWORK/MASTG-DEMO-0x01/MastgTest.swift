import Foundation

struct MastgTest {
    // SUMMARY: This sample makes HTTPS connections to three domains used across the ATS pinning demos:
    // two domains the app intends to pin and one developer-owned domain that is not pinned at all.
    // The connections themselves always succeed; whether each domain is actually protected depends on
    // the certificate pinning configuration declared in Info.plist (see MASTG-DEMO-0x01 and MASTG-DEMO-0x02).

    // A correctly pinned domain.
    static let pinnedEndpoint = "https://sha256.badssl.com/"

    // FAIL: [MASTG-TEST-0x02] A pinned domain whose pin expires (see the TrustKit config in MASTG-DEMO-0x02).
    static let expiringPinEndpoint = "https://rsa2048.badssl.com/"

    // FAIL: [MASTG-TEST-0x01] The app's own backend. It is a relevant domain that should be pinned but isn't.
    static let developerEndpoint = "https://example.com/"

    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing HTTPS connections for ATS certificate pinning:\n\n"
        let group = DispatchGroup()

        for endpoint in [pinnedEndpoint, expiringPinEndpoint, developerEndpoint] {
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
