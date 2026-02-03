import Foundation

struct MastgTest {
    // SUMMARY: This sample demonstrates the use of BSD sockets that bypass ATS.
    
    static func mastgTest(completion: @escaping (String) -> Void) {
        var result = "Testing BSD socket connection (bypasses ATS):\n\n"
        
        // FAIL: [MASTG-TEST-0322] Using BSD sockets bypasses ATS
        let host = "httpbin.org"
        let port: UInt16 = 80
        
        // Resolve hostname to IP address
        var hints = addrinfo()
        hints.ai_family = AF_INET
        hints.ai_socktype = SOCK_STREAM
        hints.ai_protocol = IPPROTO_TCP
        
        var serverInfo: UnsafeMutablePointer<addrinfo>?
        let status = getaddrinfo(host, String(port), &hints, &serverInfo)
        
        guard status == 0, let info = serverInfo else {
            result += "Failed to resolve hostname: \(String(cString: gai_strerror(status)))\n"
            completion(result)
            return
        }
        
        defer { freeaddrinfo(serverInfo) }
        
        // Create socket - this bypasses ATS entirely
        let sock = socket(info.pointee.ai_family, info.pointee.ai_socktype, info.pointee.ai_protocol)
        
        guard sock >= 0 else {
            result += "Failed to create socket\n"
            completion(result)
            return
        }
        
        defer { close(sock) }
        
        // Connect to server using raw socket
        let connectResult = connect(sock, info.pointee.ai_addr, info.pointee.ai_addrlen)
        
        guard connectResult == 0 else {
            result += "Failed to connect: \(String(cString: strerror(errno)))\n"
            completion(result)
            return
        }
        
        result += "Socket connection established to httpbin.org:80 (cleartext)\n"
        result += "This connection bypasses ATS because it uses BSD sockets directly\n"
        
        // Send HTTP request over cleartext socket
        let request = "GET /get HTTP/1.1\r\nHost: httpbin.org\r\nConnection: close\r\n\r\n"
        let sendResult = request.withCString { ptr in
            send(sock, ptr, strlen(ptr), 0)
        }
        
        if sendResult > 0 {
            result += "HTTP request sent successfully over cleartext socket connection\n"
            
            // Receive response
            var buffer = [CChar](repeating: 0, count: 1024)
            let recvResult = recv(sock, &buffer, buffer.count - 1, 0)
            
            if recvResult > 0 {
                let response = String(cString: buffer)
                let firstLine = response.components(separatedBy: "\r\n").first ?? ""
                result += "Received response: \(firstLine)\n"
            }
        } else {
            result += "Failed to send request\n"
        }
        
        completion(result)
    }
}
