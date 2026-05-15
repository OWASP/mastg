import Foundation

struct MastgTest {
    // SUMMARY: This sample demonstrates the use of hardcoded HTTP URLs in iOS apps.
    
    static let httpEndpoint = "http://httpbin.org/get"  // FAIL: [MASTG-TEST-0321] Hardcoded HTTP URL
    static let httpApiUrl = "http://example.com/api"    // FAIL: [MASTG-TEST-0321] Hardcoded HTTP URL
    static let httpsEndpoint = "https://httpbin.org/get" // PASS: [MASTG-TEST-0321] HTTPS URL
    
    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing HTTP URL connections:\n\n"
        
        // Attempt to connect to HTTP endpoint
        if let url = URL(string: httpEndpoint) {
            let task = URLSession.shared.dataTask(with: url) { data, response, error in
                if let error = error {
                    result += "HTTP request to \(httpEndpoint) failed: \(error.localizedDescription)\n"
                } else if let httpResponse = response as? HTTPURLResponse {
                    result += "HTTP request to \(httpEndpoint) returned status: \(httpResponse.statusCode)\n"
                }
                completion(result)
            }
            task.resume()
        } else {
            result += "Invalid URL: \(httpEndpoint)\n"
            completion(result)
        }
    }
}
