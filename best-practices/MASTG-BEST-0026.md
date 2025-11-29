---
title: Avoid Improper Hashing Algorithms
alias: ios-avoid-improper-hashing
id: MASTG-BEST-0026
platform: ios
---

Use cryptographically strong hashing algorithms appropriate for their intended purpose. Weak algorithms like MD5 and SHA-1 are vulnerable to collision attacks and should not be used for security-sensitive operations.

## General Hashing for Integrity and Fingerprinting

Prefer modern APIs such as [CryptoKit](https://developer.apple.com/documentation/cryptokit) (available from iOS 13.0+) for SHA-256 and SHA-512. Avoid MD5 and SHA-1 for new designs.

MD5 and SHA-1 have known vulnerabilities that make them unsuitable for security purposes:

- **MD5**: [RFC 6151](https://datatracker.ietf.org/doc/html/rfc6151) formally deprecates MD5 for security applications due to collision vulnerabilities
- **SHA-1**: Declared deprecated by NIST in [SP 800-131A Rev. 2](https://csrc.nist.gov/pubs/sp/800/131/a/r2/final) and shown vulnerable through [the SHAttered attack](https://shattered.io/)

### Recommended Approach with CryptoKit (iOS 13.0+)

```swift
import CryptoKit

// Good: Use SHA-256 with CryptoKit
let data = Data("message".utf8)
let hash = SHA256.hash(data: data)
let hashString = hash.compactMap { String(format: "%02x", $0) }.joined()

// Good: Use SHA-512 with CryptoKit
let hash512 = SHA512.hash(data: data)
```

### Using CommonCrypto (Legacy)

If you must support iOS versions prior to 13.0, you can use CommonCrypto, but prefer CryptoKit when possible.

```swift
import CommonCrypto

// Acceptable for legacy support: Use SHA-256 with CommonCrypto
func sha256(_ data: Data) -> Data {
    var hash = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
    data.withUnsafeBytes {
        _ = CC_SHA256($0.baseAddress, CC_LONG(data.count), &hash)
    }
    return Data(hash)
}
```

### What to Avoid

```swift
// Bad: Avoid MD5 and SHA-1
// Do not use CC_MD5 or CC_SHA1 from CommonCrypto
```

For detailed information on secure hashing algorithms, refer to [NIST's guidance on cryptographic algorithms](https://csrc.nist.gov/projects/hash-functions) and [Apple's CryptoKit documentation](https://developer.apple.com/documentation/cryptokit).

## Password Hashing

Never use simple hash functions (even SHA-256 or SHA-512) directly for password hashing. Password hashing requires specialized algorithms that are intentionally slow and resource-intensive to resist brute-force attacks.

### Use PBKDF2 via CommonCrypto

Use [`CCKeyDerivationPBKDF`](https://opensource.apple.com/source/CommonCrypto/CommonCrypto-60074/include/CommonKeyDerivation.h.auto.html) from CommonCrypto with a strong iteration count. PBKDF2 applies many iterations of a hash function, making brute-force attacks computationally expensive.

```swift
import CommonCrypto

func deriveKey(password: String, salt: Data, iterations: Int = 600000) -> Data? {
    let passwordData = Data(password.utf8)
    var derivedKeyData = Data(repeating: 0, count: 32) // 256 bits

    let result = derivedKeyData.withUnsafeMutableBytes { derivedKeyBytes in
        salt.withUnsafeBytes { saltBytes in
            passwordData.withUnsafeBytes { passwordBytes in
                CCKeyDerivationPBKDF(
                    CCPBKDFAlgorithm(kCCPBKDF2),
                    passwordBytes.baseAddress?.assumingMemoryBound(to: Int8.self),
                    passwordData.count,
                    saltBytes.baseAddress?.assumingMemoryBound(to: UInt8.self),
                    salt.count,
                    CCPseudoRandomAlgorithm(kCCPRFHmacAlgSHA256),
                    UInt32(iterations),
                    derivedKeyBytes.baseAddress?.assumingMemoryBound(to: UInt8.self),
                    derivedKeyData.count
                )
            }
        }
    }

    return result == kCCSuccess ? derivedKeyData : nil
}
```

### Important Considerations

- **Iteration Count**: Use at least 600,000 iterations for PBKDF2-SHA256 as recommended by [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#pbkdf2)
- **Salt**: Always use a unique, cryptographically random salt for each password (minimum 16 bytes). Generate salts using [`SecRandomCopyBytes`](https://developer.apple.com/documentation/security/secrandomcopybytes(_:_:_:))
- **Never Use Raw Hash Functions**: Do not use SHA functions directly for password hashing

For comprehensive password storage guidance, see the [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html).

## Message Authentication and Key Derivation

### Use HMAC for Keyed Integrity

When you need message authentication with a secret key, use HMAC (Hash-based Message Authentication Code) rather than manually combining a key with data before hashing.

#### With CryptoKit (iOS 13.0+)

```swift
import CryptoKit

// Good: Use HMAC with CryptoKit
let key = SymmetricKey(size: .bits256)
let message = Data("message".utf8)
let authenticationCode = HMAC<SHA256>.authenticationCode(for: message, using: key)
```

#### With CommonCrypto (Legacy)

```swift
import CommonCrypto

// Good: Use HMAC with CommonCrypto
func hmacSHA256(message: Data, key: Data) -> Data {
    var mac = [UInt8](repeating: 0, count: Int(CC_SHA256_DIGEST_LENGTH))
    key.withUnsafeBytes { keyBytes in
        message.withUnsafeBytes { messageBytes in
            CCHmac(CCHmacAlgorithm(kCCHmacAlgSHA256),
                   keyBytes.baseAddress, key.count,
                   messageBytes.baseAddress, message.count,
                   &mac)
        }
    }
    return Data(mac)
}
```

### Use HKDF Only for High Entropy Inputs

HKDF (HMAC-based Key Derivation Function) is designed for deriving keys from high-entropy secret material, not for password-based key derivation. Use HKDF only when you have a strong source of entropy (such as Diffie-Hellman shared secrets or existing cryptographic keys).

For password-based key derivation, always use PBKDF2 as described above.

#### With CryptoKit (iOS 13.0+)

```swift
import CryptoKit

// Good: Use HKDF with high-entropy input
let inputKeyMaterial = SymmetricKey(size: .bits256)
let salt = Data(/* random salt */)
let info = Data("application-specific-context".utf8)

let derivedKey = HKDF<SHA256>.deriveKey(
    inputKeyMaterial: inputKeyMaterial,
    salt: salt,
    info: info,
    outputByteCount: 32
)
```

For detailed guidance on key derivation, refer to [NIST SP 800-108 Rev. 1](https://csrc.nist.gov/pubs/sp/800/108/r1/upd1/final) and [RFC 5869](https://datatracker.ietf.org/doc/html/rfc5869).

## Additional Resources

- [Apple CryptoKit Documentation](https://developer.apple.com/documentation/cryptokit)
- [Apple Security and Privacy](https://developer.apple.com/documentation/security)
- [NIST Hash Functions](https://csrc.nist.gov/projects/hash-functions)
- [OWASP Cryptographic Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html)
