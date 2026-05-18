// SUMMARY: This sample demonstrates loading a security-relevant role flag from UserDefaults without integrity or authenticity validation (insecure) and with HMAC validation (secure).

import Foundation
import CryptoKit

struct MastgTest {

    static let keyRoleInsecure = "user_role_insecure"
    static let keyRoleSecure = "user_role_secure"
    static let keyRoleSecureHmac = "user_role_secure_hmac"

    // Hardcoded key used for demo purposes only — in a real app, store this in the Keychain.
    static let hmacKeyData = Data("this-is-a-very-secret-key-for-demo".utf8)
    static var hmacKey: SymmetricKey { SymmetricKey(data: hmacKeyData) }

    static func computeHMAC(for value: String) -> String {
        let mac = HMAC<SHA256>.authenticationCode(for: Data(value.utf8), using: hmacKey)
        return Data(mac).base64EncodedString()
    }

    static func mastgTest(completion: @escaping (String) -> Void) {
        let defaults = UserDefaults.standard

        // First run: initialize both storage entries
        if defaults.object(forKey: keyRoleInsecure) == nil {
            defaults.set("user", forKey: keyRoleInsecure)
            defaults.set("user", forKey: keyRoleSecure)
            defaults.set(computeHMAC(for: "user"), forKey: keyRoleSecureHmac)
            completion("Initialized: both roles set to 'user'. Run again to check values.")
            return
        }

        // FAIL: [MASTG-TEST-0x02] Load role from UserDefaults without any integrity or authenticity check.
        // An attacker with access to the device (jailbroken, backup restore) can tamper with the plist file.
        let insecureRole = defaults.string(forKey: keyRoleInsecure) ?? "error"

        // PASS: [MASTG-TEST-0x02] Load role and verify HMAC before trusting the value.
        let secureRole: String
        if let storedRole = defaults.string(forKey: keyRoleSecure),
           let storedHmac = defaults.string(forKey: keyRoleSecureHmac),
           storedHmac == computeHMAC(for: storedRole) {
            secureRole = storedRole
        } else {
            secureRole = "tampered or missing"
        }

        let insecureResult = insecureRole == "admin" ? "❌ Insecure check bypassed." : "✅ Insecure value unchanged."
        let secureResult = secureRole == "user" ? "✅ Secure value unchanged." : "✅ Secure check detected tampering."

        completion("\(insecureResult)\n\(secureResult)")
    }
}
