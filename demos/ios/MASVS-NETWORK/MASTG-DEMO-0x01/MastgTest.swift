import Foundation

struct MastgTest {
    // SUMMARY: This sample demonstrates URLSession usage with domains that have ATS TLS policy exceptions configured in Info.plist.

    // These domains have TLS policy exceptions configured in Info.plist:
    // - example.com: NSExceptionMinimumTLSVersion = TLSv1.1 (allows TLS 1.1 and subdomains)
    // - legacy.example.com: NSExceptionRequiresForwardSecrecy = false (disables PFS requirement)

    // FAIL: [MASTG-TEST-0x01] Domain has NSExceptionMinimumTLSVersion = TLSv1.1
    static let tlsDowngradeEndpoint = "https://example.com/api"

    // FAIL: [MASTG-TEST-0x01] Domain has NSExceptionRequiresForwardSecrecy = false
    static let noPfsEndpoint = "https://legacy.example.com/api"

    // PASS: [MASTG-TEST-0x01] No TLS policy exceptions configured for this domain
    static let secureEndpoint = "https://httpbin.org/get"

    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "ATS TLS policy exception test:\n\n"
        result += "Configured endpoints:\n"
        result += "- \(tlsDowngradeEndpoint) (allows TLS 1.1 via NSExceptionMinimumTLSVersion)\n"
        result += "- \(noPfsEndpoint) (allows non-PFS cipher suites via NSExceptionRequiresForwardSecrecy = false)\n"
        result += "- \(secureEndpoint) (no TLS policy exceptions)\n"
        completion(result)
    }
}
