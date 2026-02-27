import Foundation

struct MastgTest {
    // SUMMARY: This sample demonstrates expired certificate pinning configuration using TrustKit.
    // TrustKit is configured via Info.plist with a TSKExpirationDate set in the past.
    // After the expiration date, TrustKit stops enforcing certificate pinning for the affected domains.

    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0x02] The TrustKit pin configuration for api.example.com has an
        // expired TSKExpirationDate (2020-01-01). TrustKit will no longer enforce pinning.
        let url = URL(string: "https://api.example.com/data")!
        let task = URLSession.shared.dataTask(with: url) { _, response, error in
            if let error = error {
                completion("Request failed: \(error.localizedDescription)")
            } else if let httpResponse = response as? HTTPURLResponse {
                completion("Request to api.example.com returned status: \(httpResponse.statusCode)")
            }
        }
        task.resume()
    }
}
