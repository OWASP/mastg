import SwiftUI
import CommonCrypto

struct MastgTest {
	static func mastgTest(completion: @escaping (String) -> Void) {
		let key = "0123456789abcdef" // 16-byte key for AES-128
		let data = "This is a sample text for ECB mode testing!".data(using: .utf8)!

		// Create a buffer for encrypted data
		var encryptedBytes = [UInt8](repeating: 0, count: data.count + kCCBlockSizeAES128)
		var numBytesEncrypted: size_t = 0

		let cryptStatus = data.withUnsafeBytes { dataBytes in
			key.withCString { keyBytes in
				CCCrypt(
					CCOperation(kCCEncrypt),
					CCAlgorithm(kCCAlgorithmAES),         // AES Algorithm
					CCOptions(kCCOptionECBMode | kCCOptionPKCS7Padding),          // ECB Mode (INSECURE!)
					keyBytes, kCCKeySizeAES128,
					nil,
					dataBytes.baseAddress, data.count,
					&encryptedBytes, encryptedBytes.count,
					&numBytesEncrypted
				)
			}
		}

		if cryptStatus == kCCSuccess {
			let encryptedData = Data(bytes: encryptedBytes, count: numBytesEncrypted)
			let encryptedHex = encryptedData.map { String(format: "%02hhx", $0) }.joined()
			let value = "Original:\n\n \(String(data: data, encoding: .utf8)!)\n\nEncrypted with ECB mode (Hex):\n \(encryptedHex)"
			completion(value)
		} else {
			completion("Encryption failed with status: \(cryptStatus)")
		}
	}
}
