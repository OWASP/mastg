import Foundation

struct MastgTest {
    // SUMMARY: This sample demonstrates missing certificate pinning configuration in ATS.
    // The app makes HTTPS connections but the Info.plist does not configure NSPinnedDomains,
    // leaving it vulnerable to MITM attacks via rogue or compromised certificate authorities.

    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0x01] No certificate pinning configured via NSPinnedDomains in Info.plist.
        // The app relies solely on the system CA trust store.
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
