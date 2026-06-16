---
title: Verify Integrity and Authenticity of Local Storage Data
alias: verify-integrity-authenticity-local-storage
id: MASTG-BEST-0x02
platform: ios
knowledge: [MASTG-KNOW-0091, MASTG-KNOW-0093]
---

Before using data loaded from local storage (files, `UserDefaults`, Core Data, Realm, or other app-managed storage) in security-relevant decisions, verify its integrity and authenticity. An HMAC or digital signature over the stored value, with the key protected in the Keychain, ensures the app detects tampering even on jailbroken devices or after backup restoration.

## Protecting Data with an HMAC

Store a separate HMAC alongside each security-relevant value. When loading the value, recompute the HMAC and compare it to the stored one before trusting the data:

```swift
import CryptoKit
import Security

// Compute HMAC-SHA256 over a value using a key from the Keychain
func computeHMAC(for value: String, key: SymmetricKey) -> String {
    let mac = HMAC<SHA256>.authenticationCode(for: Data(value.utf8), using: key)
    return Data(mac).base64EncodedString()
}

// Verify before using the value
func loadVerified(key: String, hmacKey: SymmetricKey) -> String? {
    let defaults = UserDefaults.standard
    guard
        let value = defaults.string(forKey: key),
        let storedHmac = defaults.string(forKey: key + "_hmac")
    else { return nil }

    let expectedHmac = computeHMAC(for: value, key: hmacKey)
    guard storedHmac == expectedHmac else { return nil }
    return value
}
```

Store the HMAC key in the Keychain with an appropriate access control policy, for example using `.whenUnlockedThisDeviceOnly` to prevent extraction in backups:

```swift
let key = SymmetricKey(size: .bits256)
// Store key in Keychain using SecItemAdd with kSecAttrAccessible = kSecAttrAccessibleWhenUnlockedThisDeviceOnly
```

## Caveats

Hardcoding the HMAC key in the app binary undermines this protection. An attacker who recovers the key through reverse engineering can compute a valid HMAC for any forged value. Always store the key in the Keychain and consider binding it to device-specific attributes.

