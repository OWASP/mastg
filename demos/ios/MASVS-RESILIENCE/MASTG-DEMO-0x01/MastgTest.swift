// SUMMARY: This sample makes a security-sensitive decision (a license check) but never verifies
// the integrity of its own code at runtime. An attacker can patch the compiled check on a
// jailbroken device and the app will not detect the modification.
//
// For contrast, the file also contains a PASS routine that hashes the app's own __TEXT/__text
// section at runtime. Keeping both cases in one binary makes the difference visible in the
// disassembly: the PASS routine references dladdr/getsectiondata/CC_SHA256, the FAIL routine
// references none of them.

import Foundation
import CommonCrypto
import MachO

struct MastgTest {
    // FAIL case: a security-sensitive gate. An attacker who patches the compiled comparison can
    // flip the result; nothing verifies the integrity of this code at runtime.
    static func isLicenseValid(_ licenseKey: String) -> Bool {
        return licenseKey == "MAS-PREMIUM-2025"
    }

    // PASS case: hash the app's own __TEXT/__text section at runtime. A real implementation would
    // compare this digest against a securely stored reference value and react if they differ,
    // letting the app detect binary patching. This routine references dladdr, getsectiondata, and
    // CC_SHA256, so unlike isLicenseValid these integrity APIs are visible in the binary.
    static func currentCodeHash() -> String? {
        var info = Dl_info()
        guard dladdr(#dsohandle, &info) != 0, let base = info.dli_fbase else { return nil }
        let header = base.assumingMemoryBound(to: mach_header_64.self)
        var size: UInt = 0
        guard let text = getsectiondata(header, "__TEXT", "__text", &size) else { return nil }
        var digest = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
        CC_SHA256(text, CC_LONG(size), &digest)
        return digest.map { String(format: "%02x", $0) }.joined()
    }

    static func mastgTest(completion: @escaping (String) -> Void) {
        // FAIL: [MASTG-TEST-0x01] The app makes a security-sensitive decision without computing
        // or verifying a hash over its own __TEXT/__text section, so binary patching goes undetected.
        let providedKey = "INVALID-KEY"
        let unlocked = isLicenseValid(providedKey)

        // PASS: compute an integrity hash over the app's own code for contrast. The FAIL decision
        // above produces no integrity evidence; this routine returns a digest that a real check
        // would compare against a trusted reference.
        let codeHash = currentCodeHash() ?? "unavailable"

        let value = """
        Provided key   : \(providedKey)
        Premium access : \(unlocked ? "GRANTED" : "DENIED")

        FAIL (no integrity check) : the decision above was made without verifying the app's code.
        PASS (code integrity hash): \(codeHash)
        """
        completion(value)
    }
}