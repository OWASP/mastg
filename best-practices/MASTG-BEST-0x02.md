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

## What Qualifies as Security-Relevant

Apply integrity and authenticity validation to data that influences:

- Authentication or session state (for example, stored tokens or role flags).
- Authorization or feature access (for example, premium feature flags, user role, access level).
- Configuration affecting security behavior (for example, certificate pinning settings, allowed domains).
- Trust decisions (for example, whether a server certificate was previously accepted).

Data used only for UI preferences or non-sensitive settings does not require this level of protection.

## Caveats

Hardcoding the HMAC key in the app binary undermines this protection. An attacker who recovers the key through reverse engineering can compute a valid HMAC for any forged value. Always store the key in the Keychain and consider binding it to device-specific attributes.

See [Protecting user privacy](https://developer.apple.com/documentation/uikit/protecting-user-privacy) and [Storing Keys in the Keychain](https://developer.apple.com/documentation/security/storing-keys-in-the-keychain) in Apple's developer documentation.
