import Foundation
import Network

struct MastgTest {
    // SUMMARY: This sample demonstrates NWProtocolTLS.Options configured with an insecure minimum TLS version, bypassing ATS entirely.

    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing Network.framework TLS configuration:\n\n"

        // FAIL: [MASTG-TEST-0x03] Minimum TLS version set to TLS 1.0 for a Network.framework connection (outside ATS)
        let tlsOptions = NWProtocolTLS.Options()
        sec_protocol_options_set_min_tls_protocol_version(tlsOptions.securityProtocolOptions, .TLSv10)

        let parameters = NWParameters(tls: tlsOptions)
        let connection = NWConnection(
            host: "httpbin.org",
            port: 443,
            using: parameters
        )

        connection.stateUpdateHandler = { state in
            switch state {
            case .ready:
                result += "Connection established to httpbin.org:443 with TLS minimum version TLS 1.0\n"
                result += "Note: ATS does not apply to Network.framework connections.\n"
                connection.cancel()
                completion(result)
            case .failed(let error):
                result += "Connection failed: \(error)\n"
                completion(result)
            default:
                break
            }
        }

        connection.start(queue: .main)
    }
}
