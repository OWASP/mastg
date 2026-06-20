// SUMMARY: This sample stores sensitive data in a file in the app's Documents directory and
// later reads it back without computing or verifying any integrity value (HMAC or signature).
// An attacker who modifies the file on a jailbroken device can tamper with the data undetected.
//
// For contrast, the file also contains a PASS routine that stores the same data with an appended
// HMAC-SHA256 and verifies it on read. Keeping both cases in one binary makes the difference
// visible in the disassembly: the PASS routine references CryptoKit's HMAC, the FAIL routine
// references no integrity API at all.

import Foundation
import CryptoKit

struct MastgTest {
    // PASS case: store data with an appended HMAC-SHA256 tag and verify it before trusting the
    // data on read. A mismatch (for example after the file is tampered with) is detected. In a
    // real app the key would be held in the Keychain rather than generated in-process.
    static func storeWithIntegrity(_ data: Data, at fileURL: URL, using key: SymmetricKey) -> String {
        let tagLength = 32 // HMAC-SHA256 tag size in bytes
        do {
            let mac = HMAC<SHA256>.authenticationCode(for: data, using: key)
            try (data + Data(mac)).write(to: fileURL)

            let stored = try Data(contentsOf: fileURL)
            let payload = stored.prefix(stored.count - tagLength)
            let tag = stored.suffix(tagLength)
            let valid = HMAC<SHA256>.isValidAuthenticationCode(Data(tag), authenticating: Data(payload), using: key)
            return valid ? "verified" : "tampering detected"
        } catch {
            return "failed: \(error.localizedDescription)"
        }
    }

    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0x01] The app writes sensitive data to disk and later reads it back
        // without computing or verifying an HMAC or signature, so it cannot detect tampering.

        let fileManager = FileManager.default
        guard let documents = fileManager.urls(for: .documentDirectory, in: .userDomainMask).first else {
            completion("Could not locate the Documents directory")
            return
        }
        let fileURL = documents.appendingPathComponent("user_profile.json")

        // Store sensitive data without any integrity protection
        let sensitiveData = #"{"username":"alice","role":"user","premium":false}"#.data(using: .utf8)!

        do {
            try sensitiveData.write(to: fileURL)
        } catch {
            completion("Failed to write file: \(error.localizedDescription)")
            return
        }

        // Later, the app reads the data back and trusts it without verifying its integrity
        guard let loaded = try? Data(contentsOf: fileURL),
              let contents = String(data: loaded, encoding: .utf8) else {
            completion("Failed to read the file back")
            return
        }

        // PASS: for contrast, store the same data with an HMAC and verify it on read. The FAIL path
        // above produces no integrity value; this routine returns the verification result.
        let protectedURL = documents.appendingPathComponent("user_profile_protected.json")
        let integrityResult = storeWithIntegrity(sensitiveData, at: protectedURL, using: SymmetricKey(size: .bits256))

        let value = """
        Stored file : \(fileURL.path)
        Contents    : \(contents)

        FAIL (no integrity check) : the data above was read back without verifying any HMAC or signature.
        PASS (HMAC verified)      : \(integrityResult)
        """
        completion(value)
    }
}