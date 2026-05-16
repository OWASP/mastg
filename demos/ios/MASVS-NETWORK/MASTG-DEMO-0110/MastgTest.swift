import Foundation
import Network

struct MastgTest {
    // SUMMARY: This sample demonstrates an attempt to use TLS 1.0 endpoints in iOS apps.
    // However, the connection will fail because App Transport Security (ATS) requires TLS 1.2 or later by default.
    // This test shows that URLSession's TLS settings do not override ATS requirements, and that explicit exceptions in Info.plist are needed to allow older TLS versions.

    static let tls10Endpoint = "https://tls-v1-0.badssl.com:1010/"

    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing TLS 1.0 URL connections:\n\n"

        guard let url = URL(string: tls10Endpoint) else {
            completion(result + "Invalid URL: \(tls10Endpoint)\n")
            return
        }

        let configuration = URLSessionConfiguration.ephemeral
        configuration.tlsMinimumSupportedProtocolVersion = .TLSv10

        let session = URLSession(configuration: configuration)

        let task = session.dataTask(with: url) { _, response, error in
            if let error = error as NSError? {
                result += "HTTP request to \(tls10Endpoint) failed:\n"
                result += "Domain: \(error.domain)\n"
                result += "Code: \(error.code)\n"
                result += "Description: \(error.localizedDescription)\n\n"
                result += "This is expected if ATS is not relaxed in Info.plist.\n"
                result += "URLSession TLS settings do not replace ATS exceptions.\n"
            } else if let httpResponse = response as? HTTPURLResponse {
                result += "HTTP request to \(tls10Endpoint) returned status: \(httpResponse.statusCode)\n"
            } else {
                result += "HTTP request to \(tls10Endpoint) completed without HTTP response.\n"
            }

            DispatchQueue.main.async {
                completion(result)
            }
        }

        task.resume()
    }
}
