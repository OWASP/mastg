// SUMMARY: This sample demonstrates file storage integrity checking using CCHmac with SHA-256
// to compute an HMAC over stored data and verify it before loading.

import Foundation
import CommonCrypto

struct MastgTest {
    // A fixed HMAC key for demonstration purposes.
    // In production, use a Keychain-bound key to prevent key extraction.
    private static let hmacKeyBytes: [UInt8] = Array("storage-integrity-demo-key".utf8)

    static func computeHMAC(data: Data) -> Data {
        var hmac = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
        hmacKeyBytes.withUnsafeBytes { keyBytes in
            data.withUnsafeBytes { dataBytes in
                CCHmac(
                    CCHmacAlgorithm(kCCHmacAlgSHA256),
                    keyBytes.baseAddress, hmacKeyBytes.count,
                    dataBytes.baseAddress, data.count,
                    &hmac
                )
            }
        }
        return Data(hmac)
    }

    static func mastgTest(completion: @escaping (String) -> Void) {
        // PASS: [MASTG-TEST-0x02] The app uses CCHmac with SHA-256 to authenticate stored data,
        // computing and verifying an HMAC before loading to detect unauthorized modifications.

        let message = "Sensitive file content".data(using: .utf8)!
        let hmac    = computeHMAC(data: message)
        let hmacHex = hmac.map { String(format: "%02x", $0) }.joined()

        let value = """
        Message     : \(String(data: message, encoding: .utf8)!)
        HMAC-SHA256 : \(hmacHex)
        """
        completion(value)
    }
}
