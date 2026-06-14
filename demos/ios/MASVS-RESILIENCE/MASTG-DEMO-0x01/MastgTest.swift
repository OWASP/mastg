// SUMMARY: This sample makes a security-sensitive decision (a license check) but never verifies
// the integrity of its own code at runtime. An attacker can patch the compiled check on a
// jailbroken device and the app will not detect the modification.

import Foundation

struct MastgTest {
    // A security-sensitive gate. An attacker who patches the compiled comparison can flip the
    // result; nothing verifies the integrity of this code at runtime.
    static func isLicenseValid(_ licenseKey: String) -> Bool {
        return licenseKey == "MAS-PREMIUM-2025"
    }

    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0x01] The app makes a security-sensitive decision without computing
        // or verifying a hash over its own __TEXT/__text section, so binary patching goes undetected.

        let providedKey = "INVALID-KEY"
        let unlocked = isLicenseValid(providedKey)

        let value = """
        Provided key   : \(providedKey)
        Premium access : \(unlocked ? "GRANTED" : "DENIED")

        The app made this decision without verifying the integrity of its own code.
        """
        completion(value)
    }
}