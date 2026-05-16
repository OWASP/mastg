import Foundation
import Network

struct MastgTest {
    // SUMMARY: This sample demonstrates NWProtocolTLS.Options configured with an insecure minimum TLS version, bypassing ATS entirely.

    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing Network.framework TLS configuration:\n\n"

        let host = NWEndpoint.Host("tls-v1-0.badssl.com")
        let port = NWEndpoint.Port(rawValue: 1010)!

        // FAIL: Minimum TLS version set to TLS 1.0 for a Network.framework connection.
        let tlsOptions = NWProtocolTLS.Options()
        sec_protocol_options_set_min_tls_protocol_version(
            tlsOptions.securityProtocolOptions,
            .TLSv10
        )

        // Optional, but useful for the demo because this endpoint only supports TLS 1.0.
        sec_protocol_options_set_max_tls_protocol_version(
            tlsOptions.securityProtocolOptions,
            .TLSv10
        )

        // Make the TLS server name explicit.
        sec_protocol_options_set_tls_server_name(
            tlsOptions.securityProtocolOptions,
            "tls-v1-0.badssl.com"
        )

        let parameters = NWParameters(tls: tlsOptions)
        let connection = NWConnection(host: host, port: port, using: parameters)

        var didComplete = false

        func finish(_ text: String) {
            guard !didComplete else { return }
            didComplete = true
            connection.cancel()

            DispatchQueue.main.async {
                completion(text)
            }
        }

        connection.stateUpdateHandler = { state in
            switch state {
            case .ready:
                result += "TLS connection established.\n"
                result += "Host: tls-v1-0.badssl.com\n"
                result += "Port: 1010\n"
                result += "Minimum TLS version: TLS 1.0\n\n"

                let request =
                    """
                    GET / HTTP/1.1\r
                    Host: tls-v1-0.badssl.com\r
                    Connection: close\r
                    \r

                    """

                connection.send(
                    content: request.data(using: .utf8),
                    completion: .contentProcessed { sendError in
                        if let sendError = sendError {
                            finish(result + "HTTP request send failed: \(sendError)\n")
                            return
                        }

                        receiveResponse(
                            connection: connection,
                            result: result,
                            finish: finish
                        )
                    }
                )

            case .failed(let error):
                finish(result + "Connection failed: \(error)\n")

            case .cancelled:
                break

            default:
                break
            }
        }

        connection.start(queue: .main)
    }

    private static func receiveResponse(
        connection: NWConnection,
        result: String,
        finish: @escaping (String) -> Void
    ) {
        var mutableResult = result

        connection.receive(minimumIncompleteLength: 1, maximumLength: 4096) { data, _, isComplete, error in
            if let data = data, !data.isEmpty {
                let text = String(data: data, encoding: .utf8) ?? "<non UTF8 response data>"
                mutableResult += "Received response:\n"
                mutableResult += text
                mutableResult += "\n"
            }

            if let error = error {
                finish(mutableResult + "Receive failed: \(error)\n")
                return
            }

            if isComplete {
                finish(mutableResult + "\nConnection closed by server.\n")
                return
            }

            receiveResponse(
                connection: connection,
                result: mutableResult,
                finish: finish
            )
        }
    }
}
