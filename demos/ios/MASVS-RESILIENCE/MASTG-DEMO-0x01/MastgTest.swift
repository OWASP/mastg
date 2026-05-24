// SUMMARY: This sample demonstrates source code integrity checking by using dladdr to resolve
// the binary base address, parsing the Mach-O header to find the __TEXT/__text section,
// and applying CC_SHA256 to hash the section for tamper detection.

import Foundation
import CommonCrypto
import MachO

struct MastgTest {
    static func mastgTest(completion: @escaping (String) -> Void) {
        // PASS: [MASTG-TEST-0x01] The app uses dladdr to obtain the binary base address,
        // parses the Mach-O header to locate the __TEXT/__text section, and applies CC_SHA256
        // to compute a runtime hash for source code integrity verification.

        // Step 1: Resolve the binary base address using dladdr
        var info = Dl_info()
        let symbol = unsafeBitCast(MastgTest.mastgTest as Any, to: UnsafeRawPointer.self)
        guard dladdr(symbol, &info) != 0, let basePtr = info.dli_fbase else {
            completion("Failed to resolve binary base address")
            return
        }

        // Step 2: Parse the Mach-O header to locate the __TEXT/__text section
        let base = UnsafeRawPointer(basePtr)
        var offset = MemoryLayout<mach_header_64>.size
        var textAddr: UInt = 0
        var textSize: Int  = 0

        let header = base.load(as: mach_header_64.self)
        for _ in 0 ..< Int(header.ncmds) {
            let cmd = base.load(fromByteOffset: offset, as: load_command.self)
            if cmd.cmd == LC_SEGMENT_64 {
                let seg = base.load(fromByteOffset: offset, as: segment_command_64.self)
                let segName = withUnsafeBytes(of: seg.segname) { raw in
                    String(bytes: raw.prefix(while: { $0 != 0 }), encoding: .utf8) ?? ""
                }
                if segName == "__TEXT" {
                    let secOffset = offset + MemoryLayout<segment_command_64>.size
                    let sec = base.load(fromByteOffset: secOffset, as: section_64.self)
                    textAddr = UInt(sec.addr)
                    textSize  = Int(sec.size)
                }
            }
            offset += Int(cmd.cmdsize)
        }

        guard textSize > 0, let codePtr = UnsafeRawPointer(bitPattern: textAddr) else {
            completion("Could not locate __TEXT/__text section")
            return
        }

        // Step 3: Compute SHA-256 hash of the __text section to verify code integrity
        var digest = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
        CC_SHA256(codePtr, CC_LONG(textSize), &digest)
        let hashHex = digest.map { String(format: "%02x", $0) }.joined()

        let value = """
        Binary base address : \(base)
        __TEXT/__text size  : \(textSize) bytes
        SHA-256 of __text   : \(hashHex)
        """
        completion(value)
    }
}
