---
masvs_category: MASVS-RESILIENCE
platform: ios
title: Source Code Integrity Checks
best-practices: [MASTG-BEST-0067]
---

iOS uses code signing to verify app authenticity before launch (see @MASTG-TECH-0084). Apps can also implement additional runtime checks that inspect the Mach-O binary structure to verify the integrity of their own executable code, which complements the OS-level protection and helps detect patched or re-signed binaries. For verifying the integrity of data the app stores on the device, see @MASTG-KNOW-0086.

A common approach is to:

1. Use `dladdr` to resolve the base address of the loaded binary.
2. Parse the 64-bit Mach-O header (`mach_header_64`) and iterate through its load commands to locate the `__TEXT/__text` section.
3. Compute a cryptographic hash over the `__text` section bytes and compare it against a stored reference value.

Older examples of this technique parse the 32-bit Mach-O structures (`mach_header`, `segment_command`, `section`, and `LC_SEGMENT`), which are not used by modern 64-bit iOS apps. The following Swift example uses the 64-bit structures (`mach_header_64`, `segment_command_64`, `section_64`, and `LC_SEGMENT_64`) and computes the hash with `CC_SHA256` from CommonCrypto:

```swift
import Foundation
import MachO
import CommonCrypto

// The expected hash is computed at build time over the final __text section
// and stored (ideally obfuscated) in the binary as a reference value.
let mastgExpectedTextSectionSHA256 = "<precomputed-sha256-of-__text>"

// A dedicated anchor symbol gives `dladdr` a stable address inside this image.
@_cdecl("mastg_source_integrity_anchor")
func mastgSourceIntegrityAnchor() {}

func swiftTextSectionHash() -> String {
    // Step 1: Resolve the binary base address using dladdr
    var info = Dl_info()
    let symbol = unsafeBitCast(
        mastgSourceIntegrityAnchor as @convention(c) () -> Void,
        to: UnsafeRawPointer.self
    )
    guard dladdr(symbol, &info) != 0, let basePtr = info.dli_fbase else {
        return "Failed to resolve binary base address"
    }

    // Step 2: Parse the Mach-O header to locate the __TEXT/__text section
    let base = UnsafeRawPointer(basePtr)
    var offset = MemoryLayout<mach_header_64>.size
    var codePtr: UnsafeRawPointer?
    var textSize: Int = 0

    let header = base.load(as: mach_header_64.self)
    for _ in 0 ..< Int(header.ncmds) {
        let cmd = base.load(fromByteOffset: offset, as: load_command.self)
        if cmd.cmd == LC_SEGMENT_64 {
            let seg = base.load(fromByteOffset: offset, as: segment_command_64.self)
            let segName = withUnsafeBytes(of: seg.segname) { raw in
                String(bytes: raw.prefix(while: { $0 != 0 }), encoding: .utf8) ?? ""
            }
            if segName == "__TEXT" {
                var secOffset = offset + MemoryLayout<segment_command_64>.size
                for _ in 0 ..< Int(seg.nsects) {
                    let sec = base.load(fromByteOffset: secOffset, as: section_64.self)
                    let secName = withUnsafeBytes(of: sec.sectname) { raw in
                        String(bytes: raw.prefix(while: { $0 != 0 }), encoding: .utf8) ?? ""
                    }
                    if secName == "__text" {
                        // sec.addr - seg.vmaddr is the offset of __text within the
                        // segment; adding it to the slid base avoids needing the ASLR slide.
                        let runtimeOffset = Int(sec.addr - seg.vmaddr)
                        codePtr = base.advanced(by: runtimeOffset)
                        textSize = Int(sec.size)
                    }
                    secOffset += MemoryLayout<section_64>.size
                }
            }
        }
        offset += Int(cmd.cmdsize)
    }

    guard textSize > 0, let codePtr = codePtr else {
        return "Could not locate __TEXT/__text section"
    }

    // Step 3: Compute SHA-256 over the __text section and compare it to the reference value
    var digest = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
    CC_SHA256(codePtr, CC_LONG(textSize), &digest)
    let hashHex = digest.map { String(format: "%02x", $0) }.joined()
    let matchesExpectedHash = hashHex == mastgExpectedTextSectionSHA256

    return """
    Binary base address : \(base)
    __TEXT/__text size  : \(textSize) bytes
    SHA-256 of __text   : \(hashHex)
    Expected SHA-256    : \(mastgExpectedTextSectionSHA256)
    Integrity check     : \(matchesExpectedHash ? "pass" : "fail")
    """
}
```

These checks can be bypassed on jailbroken devices, for example by patching the stored reference hash or hooking the comparison logic at runtime.
