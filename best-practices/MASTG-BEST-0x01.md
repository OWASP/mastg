---
title: Implementing File Integrity Checks on iOS
alias: implementing-file-integrity-checks-ios
id: MASTG-BEST-0x01
platform: ios
knowledge: [MASTG-KNOW-0086]
---

Implement file integrity checks in iOS apps to detect unauthorized modifications to app binaries and stored data. These checks raise the cost for attackers who try to tamper with the app or its data, especially on jailbroken devices.

## Source Code Integrity

The OS provides code signing to verify the authenticity and integrity of app binaries before launch. However, on jailbroken devices or when an attacker re-signs the app with their own certificate, this protection can be bypassed.

To supplement the OS-level protection, implement runtime source code integrity checks. These checks parse the [Mach-O binary structure](https://developer.apple.com/library/archive/documentation/DeveloperTools/Conceptual/MachOTopics/0-Introduction/introduction.html) to locate the `__TEXT/__text` section, compute a cryptographic hash over it, and compare the hash against a hardcoded or securely stored reference value.

Use a strong hash function such as SHA-256 (via `CC_SHA256` from CommonCrypto) instead of MD5, which is cryptographically weak. @MASTG-DEMO-0x01 shows a working implementation that resolves the binary base address with `dladdr`, locates the `__TEXT/__text` section, and hashes it with `CC_SHA256`.

Store the reference hash value in a location that is itself protected from modification (for example, hardcoded in an obfuscated form in the binary).

!!! warning
    Runtime source code integrity checks are inherently bypassable on jailbroken devices. Attackers can hook the check itself, patch the reference hash, or use Frida to intercept file-system calls and return the original binary. These checks should be treated as a cost-raising measure, not a guarantee.

## File Storage Integrity

Compute an HMAC over any data you store on the device before writing it, and verify the HMAC before reading. Use a key that is stored in the [iOS Keychain](https://developer.apple.com/documentation/security/keychain_services) with a strict accessibility setting (e.g., `kSecAttrAccessibleWhenUnlockedThisDeviceOnly`) so it cannot be extracted from a backup or transferred to another device.

Apple's [CryptoKit](https://developer.apple.com/documentation/cryptokit) provides a modern, Swift-friendly API:

```swift
import CryptoKit
import Security

func hmac(for data: Data, key: SymmetricKey) -> Data {
    let mac = HMAC<SHA256>.authenticationCode(for: data, using: key)
    return Data(mac)
}

func verify(data: Data, mac: Data, key: SymmetricKey) -> Bool {
    let expected = HMAC<SHA256>.authenticationCode(for: data, using: key)
    return Data(expected) == mac
}
```

Alternatively, use `CCHmac` from CommonCrypto for Objective-C or mixed codebases. @MASTG-DEMO-0x02 shows a working `CCHmac` implementation.

If you also encrypt the data, follow the [Encrypt-then-MAC](https://web.archive.org/web/20210804035343/https://cseweb.ucsd.edu/~mihir/papers/oem.html) pattern: encrypt first, then compute the HMAC over the ciphertext.

!!! warning
    File storage integrity checks are bypassable if the attacker can extract the HMAC key from the Keychain (possible on a jailbroken device) or intercept the verification logic. Treat these as a defense-in-depth control rather than a standalone guarantee.
