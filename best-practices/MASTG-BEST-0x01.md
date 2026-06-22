---
title: Implementing Storage Integrity Checks on iOS
alias: implementing-storage-integrity-checks-ios
id: MASTG-BEST-0x01
platform: ios
knowledge: [MASTG-KNOW-0086]
---

Implement storage integrity checks in iOS apps to detect unauthorized modifications to data stored on the device (for example, in the Keychain, `UserDefaults`/`NSUserDefaults`, files, or databases). These checks raise the cost for attackers who try to tamper with the app's data, especially on jailbroken devices, through backups, or by directly manipulating the app's data directory. For detecting modifications to the app's own executable code, see @MASTG-BEST-0x03.

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

Alternatively, use `CCHmac` from CommonCrypto for Objective-C or mixed codebases.

If you also encrypt the data, follow the [Encrypt-then-MAC](https://web.archive.org/web/20210804035343/https://cseweb.ucsd.edu/~mihir/papers/oem.html) pattern: encrypt first, then compute the HMAC over the ciphertext.

!!! warning
    File storage integrity checks are bypassable if the attacker can extract the HMAC key from the Keychain (possible on a jailbroken device) or intercept the verification logic. Treat these as a defense-in-depth control rather than a standalone guarantee.
