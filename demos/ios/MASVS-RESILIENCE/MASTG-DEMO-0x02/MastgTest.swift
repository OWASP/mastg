// SUMMARY: This sample stores sensitive data in a file in the app's Documents directory and
// later reads it back without computing or verifying any integrity value (HMAC or signature).
// An attacker who modifies the file on a jailbroken device can tamper with the data undetected.

import Foundation

struct MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0x02] The app writes sensitive data to disk and later reads it back
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

        let value = """
        Stored file : \(fileURL.path)
        Contents    : \(contents)

        The app read this data back without verifying any HMAC or signature.
        """
        completion(value)
    }
}