import Foundation

struct MastgTest {
    // SUMMARY: This sample demonstrates an app that does not enforce certificate pinning.
    // The app makes HTTPS connections via URLSession with no NSPinnedDomains in Info.plist
    // and no custom URLSessionDelegate trust evaluation. A MITM attack using a proxy
    // CA certificate trusted by the device will successfully intercept traffic.

    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0x04] No certificate pinning enforced. MITM interception succeeds.
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
