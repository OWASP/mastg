// SUMMARY: This sample demonstrates source code integrity checking by using dladdr to resolve
// the binary base address, locating the __TEXT/__text section with getsectiondata (which accounts
// for the ASLR slide), and applying CC_SHA256 to hash the section for tamper detection.

import Foundation
import CommonCrypto
import MachO

struct MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        // PASS: [MASTG-TEST-0x01] The app uses dladdr to obtain the binary base address,
        // locates the __TEXT/__text section, and applies CC_SHA256 to compute a runtime hash
        // for source code integrity verification.

        // Step 1: Resolve the Mach-O header of the current image using dladdr
        var info = Dl_info()
        guard dladdr(#dsohandle, &info) != 0,
              let headerPtr = info.dli_fbase?.assumingMemoryBound(to: mach_header_64.self) else {
            completion("Failed to resolve binary base address")
            return
        }

        // Step 2: Locate the __TEXT/__text section. getsectiondata returns a pointer into the
        // loaded image with the ASLR slide already applied.
        var size: UInt = 0
        guard let textPtr = getsectiondata(headerPtr, "__TEXT", "__text", &size), size > 0 else {
            completion("Could not locate __TEXT/__text section")
            return
        }

        // Step 3: Compute SHA-256 hash of the __text section to verify code integrity
        var digest = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
        CC_SHA256(textPtr, CC_LONG(size), &digest)
        let hashHex = digest.map { String(format: "%02x", $0) }.joined()

        let value = """
        Binary base address : \(headerPtr)
        __TEXT/__text size  : \(size) bytes
        SHA-256 of __text   : \(hashHex)
        """
        completion(value)
    }
}