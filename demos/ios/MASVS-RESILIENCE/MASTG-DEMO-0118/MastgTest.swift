// SUMMARY: This sample demonstrates runtime hook detection before sensitive cryptographic operations by checking for local Frida ports responding to D-Bus AUTH.

import SwiftUI
import Foundation
import CommonCrypto
import Security
import Darwin

class MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        if detectHooking() {
            exit(0)
        }

        completion(runCryptoDemo())
    }

    private static func runCryptoDemo() -> String {
        let sensitiveApiKey = "sk-OWASP-MAS-SuperSecretKey-1234567890"
        let key = Data("0123456789abcdef0123456789abcdef".utf8)
        let plaintext = Data(sensitiveApiKey.utf8)

        guard let iv = randomBytes(count: kCCBlockSizeAES128) else {
            return "Error: Could not generate IV."
        }

        if detectHooking() {
            exit(0)
        }

        // PASS: [MASTG-TEST-0354] The app checks for runtime hooks before calling CCCrypt with sensitive data.
        guard let encryptedData = crypt(
            operation: CCOperation(kCCEncrypt),
            data: plaintext,
            key: key,
            iv: iv
        ) else {
            return "Error: Encryption failed."
        }

        if detectHooking() {
            exit(0)
        }

        // PASS: [MASTG-TEST-0354] The app checks for runtime hooks before decrypting sensitive data.
        guard let decryptedData = crypt(
            operation: CCOperation(kCCDecrypt),
            data: encryptedData,
            key: key,
            iv: iv
        ), let decryptedString = String(data: decryptedData, encoding: .utf8) else {
            return "Error: Decryption failed."
        }

        return """
        Encryption and decryption successful.
        IV: \(iv.base64EncodedString())
        Encrypted: \(encryptedData.base64EncodedString())
        Decrypted: \(decryptedString)
        """
    }

    private static func detectHooking() -> Bool {
        let ports: [in_port_t] = [27042]
        return ports.contains(where: respondsToDBusAuth)
    }

    private static func respondsToDBusAuth(_ port: in_port_t) -> Bool {
        let descriptor = socket(AF_INET, SOCK_STREAM, 0)
        guard descriptor >= 0 else {
            return false
        }

        defer {
            close(descriptor)
        }

        var timeout = timeval(tv_sec: 1, tv_usec: 0)
        setsockopt(descriptor, SOL_SOCKET, SO_RCVTIMEO, &timeout, socklen_t(MemoryLayout<timeval>.size))
        setsockopt(descriptor, SOL_SOCKET, SO_SNDTIMEO, &timeout, socklen_t(MemoryLayout<timeval>.size))

        var address = sockaddr_in()
        address.sin_len = UInt8(MemoryLayout<sockaddr_in>.size)
        address.sin_family = sa_family_t(AF_INET)
        address.sin_port = port.bigEndian
        address.sin_addr = in_addr(s_addr: inet_addr("127.0.0.1"))

        let connected = withUnsafePointer(to: &address) { pointer in
            pointer.withMemoryRebound(to: sockaddr.self, capacity: 1) { socketAddress in
                connect(descriptor, socketAddress, socklen_t(MemoryLayout<sockaddr_in>.size)) == 0
            }
        }

        guard connected else {
            return false
        }

        let authProbe = [UInt8]("\0AUTH\r\n".utf8)
        let sent = authProbe.withUnsafeBytes { buffer in
            send(descriptor, buffer.baseAddress, buffer.count, 0)
        }

        guard sent == authProbe.count else {
            return false
        }

        var response = [UInt8](repeating: 0, count: 256)
        let received = recv(descriptor, &response, response.count, 0)
        guard received > 0 else {
            return false
        }

        let responseText = String(decoding: response.prefix(received), as: UTF8.self).uppercased()
        return responseText.contains("REJECTED") || responseText.contains("OK")
    }

    private static func randomBytes(count: Int) -> Data? {
        var data = Data(count: count)
        let status = data.withUnsafeMutableBytes { buffer -> OSStatus in
            guard let baseAddress = buffer.baseAddress else {
                return errSecParam
            }
            return SecRandomCopyBytes(kSecRandomDefault, count, baseAddress)
        }
        return status == errSecSuccess ? data : nil
    }

    private static func crypt(operation: CCOperation, data: Data, key: Data, iv: Data) -> Data? {
        var output = Data(count: data.count + kCCBlockSizeAES128)
        let outputCapacity = output.count
        var outputLength: size_t = 0

        let status = output.withUnsafeMutableBytes { outputBytes in
            data.withUnsafeBytes { dataBytes in
                key.withUnsafeBytes { keyBytes in
                    iv.withUnsafeBytes { ivBytes in
                        CCCrypt(
                            operation,
                            CCAlgorithm(kCCAlgorithmAES),
                            CCOptions(kCCOptionPKCS7Padding),
                            keyBytes.baseAddress,
                            key.count,
                            ivBytes.baseAddress,
                            dataBytes.baseAddress,
                            data.count,
                            outputBytes.baseAddress,
                            outputCapacity,
                            &outputLength
                        )
                    }
                }
            }
        }

        guard status == kCCSuccess else {
            return nil
        }

        output.removeSubrange(outputLength..<output.count)
        return output
    }
}
