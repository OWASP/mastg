import Foundation
import Network

struct MastgTest {
    // SUMMARY: This sample demonstrates the use of low-level Network framework APIs that bypass ATS.
    
    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing Network framework connection (bypasses ATS):\n\n"
        
        // FAIL: [MASTG-TEST-0322] Using Network framework without TLS bypasses ATS
        let host = NWEndpoint.Host("httpbin.org")
        let port = NWEndpoint.Port(integerLiteral: 80)
        
        // Creating a TCP connection without TLS - this bypasses ATS entirely
        let connection = NWConnection(host: host, port: port, using: .tcp)
        
        connection.stateUpdateHandler = { state in
            switch state {
            case .ready:
                result += "Connection established to httpbin.org:80 (cleartext)\n"
                result += "This connection bypasses ATS because it uses Network framework\n"
                
                // Send a simple HTTP GET request
                let request = "GET /get HTTP/1.1\r\nHost: httpbin.org\r\nConnection: close\r\n\r\n"
                let requestData = request.data(using: .utf8)!
                
                connection.send(content: requestData, completion: .contentProcessed { error in
                    if let error = error {
                        result += "Send error: \(error)\n"
                    } else {
                        result += "HTTP request sent successfully over cleartext connection\n"
                    }
                    connection.cancel()
                    completion(result)
                })
                
            case .failed(let error):
                result += "Connection failed: \(error)\n"
                completion(result)
                
            case .cancelled:
                result += "Connection cancelled\n"
                
            default:
                break
            }
        }
        
        connection.start(queue: .main)
    }
    
    // PASS: [MASTG-TEST-0322] Using Network framework with TLS is secure
    static func secureNetworkConnection(completion: @escaping (String) -> Void) {
        var result = "Testing secure Network framework connection:\n\n"
        
        let host = NWEndpoint.Host("httpbin.org")
        let port = NWEndpoint.Port(integerLiteral: 443)
        
        // Creating a TLS connection - secure usage of Network framework
        let tlsParameters = NWParameters.tls
        let connection = NWConnection(host: host, port: port, using: tlsParameters)
        
        connection.stateUpdateHandler = { state in
            switch state {
            case .ready:
                result += "Secure TLS connection established to httpbin.org:443\n"
                completion(result)
                connection.cancel()
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
