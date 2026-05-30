// SUMMARY: This sample demonstrates a sensitive cryptographic operation that can be intercepted at runtime because the app does not implement runtime hook detection.

import SwiftUI
import Foundation
import CommonCrypto
import Security

class MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        completion(runCryptoDemo())
    }

    private static func runCryptoDemo() -> String {
        let sensitiveApiKey = "sk-OWASP-MAS-SuperSecretKey-1234567890"
        let key = Data("0123456789abcdef0123456789abcdef".utf8)
        let plaintext = Data(sensitiveApiKey.utf8)

        guard let iv = randomBytes(count: kCCBlockSizeAES128) else {
            return "Error: Could not generate IV."
        }

        // FAIL: [MASTG-TEST-0354] The app calls CCCrypt with sensitive data without detecting runtime hooks.
        guard let encryptedData = crypt(
            operation: CCOperation(kCCEncrypt),
            data: plaintext,
            key: key,
            iv: iv
        ) else {
            return "Error: Encryption failed."
        }

        // FAIL: [MASTG-TEST-0354] The app decrypts sensitive data without detecting runtime hooks.
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
