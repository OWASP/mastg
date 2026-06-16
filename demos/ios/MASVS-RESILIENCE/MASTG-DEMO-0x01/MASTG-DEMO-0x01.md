---
platform: ios
title: Running Security-Sensitive Code Without Source Code Integrity Checks
code: [swift]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
---

<!-- Placeholder: the rest of this demo lives on the MASTG-TEST-0090 branch.
     Only the v2.1 spec sections (Exploitation, Fix) are staged here. -->

### Exploitation

You can confirm the missing integrity check by patching the security-sensitive routine and observing that the app still runs:

1. Use @MASTG-TECH-0065 to locate the `isLicenseValid` comparison in the disassembly.
2. Use @MASTG-TECH-0147 to patch the binary so the check always grants access (for example, force the comparison to return `true`).
3. Use @MASTG-TECH-0092 to re-sign and repackage the patched app, then reinstall it.
4. Launch the app with any key and observe that it grants premium access. The app never detected the patch because it has no source code integrity check.

## Fix

Implement a runtime source code integrity check that detects binary patching. See @MASTG-BEST-0x01 for full guidance.

**Option 1: Hash the `__TEXT/__text` section at runtime and compare it to a reference value (recommended)**

Resolve the loaded image with `dladdr`, locate the `__TEXT/__text` section (for example with `getsectiondata`, which applies the ASLR slide), compute a SHA-256 hash over it, and compare the result against a reference value embedded in the app. If the values differ, the binary has been modified:

```swift
import CommonCrypto
import MachO

var info = Dl_info()
dladdr(#dsohandle, &info)
let header = info.dli_fbase!.assumingMemoryBound(to: mach_header_64.self)
var size: UInt = 0
let text = getsectiondata(header, "__TEXT", "__text", &size)!
var digest = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
CC_SHA256(text, CC_LONG(size), &digest)
// Compare `digest` against a securely stored reference hash and react if they differ.
```

Store the reference hash where it is hard to locate and modify (for example, obfuscated in the binary or derived at build time), so an attacker cannot simply patch it alongside the code.

**Why this is only a cost-raising measure:** a determined attacker on a jailbroken device can still patch the check itself or the stored reference hash, or hook the comparison with @MASTG-TECH-0095. Combine it with other resilience controls rather than relying on it alone.