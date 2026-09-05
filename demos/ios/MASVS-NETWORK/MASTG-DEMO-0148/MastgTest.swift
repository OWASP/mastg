import Foundation

struct MastgTest {
    // SUMMARY: This sample makes HTTPS connections to three first-party domains. Two of them
    // (sha256.badssl.com and rsa2048.badssl.com) are pinned through ATS NSPinnedDomains, while the
    // app's own backend (example.com) is not pinned at all. The connections themselves always succeed;
    // whether each domain is actually protected depends on the NSPinnedDomains configuration in Info.plist.

    // PASS: [MASTG-TEST-0385] A first-party domain that is pinned via NSPinnedDomains.
    static let pinnedEndpoint = "https://sha256.badssl.com/"

    // PASS: [MASTG-TEST-0385] Another first-party domain that is pinned via NSPinnedDomains.
    static let secondPinnedEndpoint = "https://rsa2048.badssl.com/"

    // FAIL: [MASTG-TEST-0385] The app's own backend. It is a relevant first-party domain that should be pinned but isn't.
    static let developerEndpoint = "https://example.com/"

    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing HTTPS connections for ATS certificate pinning:\n\n"
        let group = DispatchGroup()

        for endpoint in [pinnedEndpoint, secondPinnedEndpoint, developerEndpoint] {
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
