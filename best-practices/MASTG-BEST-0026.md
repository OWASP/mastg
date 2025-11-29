---
title: Use Secure Algorithms for Digital Signature Generation
alias: use-secure-digital-signature-algorithms
id: MASTG-BEST-0026
platform: ios
---

When generating digital signatures in iOS applications, use modern secure cryptographic APIs provided by Apple. Improper signature generation can allow attackers to forge signatures, tamper with data, or bypass authentication mechanisms.

## Use CryptoKit for Modern Signature Operations

Prefer [CryptoKit](https://developer.apple.com/documentation/cryptokit) (available from iOS 13.0+) for cryptographic operations. CryptoKit provides type-safe, modern APIs that prevent common pitfalls:

### ECDSA Signing with P-256

```swift
import CryptoKit

// Generate a P-256 signing key
let signingKey = P256.Signing.PrivateKey()

// Sign data
let data = "Important message".data(using: .utf8)!
let signature = try signingKey.signature(for: data)

// Verify signature
let publicKey = signingKey.publicKey
let isValid = publicKey.isValidSignature(signature, for: data)
```

### ECDSA Signing with P-384

For higher security requirements, use P-384:

```swift
import CryptoKit

let signingKey = P384.Signing.PrivateKey()
let data = "Important message".data(using: .utf8)!
let signature = try signingKey.signature(for: data)

let isValid = signingKey.publicKey.isValidSignature(signature, for: data)
```

### EdDSA with Curve25519

For modern elliptic curve cryptography, use Ed25519:

```swift
import CryptoKit

let signingKey = Curve25519.Signing.PrivateKey()
let data = "Important message".data(using: .utf8)!
let signature = try signingKey.signature(for: data)

let isValid = signingKey.publicKey.isValidSignature(signature, for: data)
```

## Use Secure SecKey APIs

When using the [Security framework](https://developer.apple.com/documentation/security) with [`SecKey`](https://developer.apple.com/documentation/security/seckey) APIs, specify secure algorithms and avoid deprecated options.

### Recommended Algorithms

For RSA signatures:

- `kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA256`
- `kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA384`
- `kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA512`
- `kSecKeyAlgorithmRSASignatureMessagePSSSHA256`
- `kSecKeyAlgorithmRSASignatureMessagePSSSHA384`
- `kSecKeyAlgorithmRSASignatureMessagePSSSHA512`

For ECDSA signatures:

- `kSecKeyAlgorithmECDSASignatureMessageX962SHA256`
- `kSecKeyAlgorithmECDSASignatureMessageX962SHA384`
- `kSecKeyAlgorithmECDSASignatureMessageX962SHA512`

### Avoid Deprecated Algorithms

Do not use algorithms based on SHA-1 or weaker hash functions:

- `kSecKeyAlgorithmRSASignatureMessagePKCS1v15SHA1` - SHA-1 is vulnerable to collision attacks
- `kSecKeyAlgorithmECDSASignatureMessageX962SHA1` - SHA-1 is deprecated
- Any algorithm using MD5 or MD2

### Example with SecKey

```swift
import Security

// Generate RSA key pair (3072-bit or higher)
let attributes: [String: Any] = [
    kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
    kSecAttrKeySizeInBits as String: 3072
]

var error: Unmanaged<CFError>?
guard let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &error) else {
    // Handle error
    return
}

let publicKey = SecKeyCopyPublicKey(privateKey)!

// Sign data
let data = "Important message".data(using: .utf8)! as CFData
let algorithm = SecKeyAlgorithm.rsaSignatureMessagePKCS1v15SHA256

guard SecKeyIsAlgorithmSupported(privateKey, .sign, algorithm) else {
    // Algorithm not supported
    return
}

guard let signature = SecKeyCreateSignature(
    privateKey,
    algorithm,
    data,
    &error
) else {
    // Handle signing error
    return
}

// Verify signature
guard SecKeyIsAlgorithmSupported(publicKey, .verify, algorithm) else {
    // Algorithm not supported
    return
}

let isValid = SecKeyVerifySignature(
    publicKey,
    algorithm,
    data,
    signature,
    &error
)
```

### ECDSA with SecKey

```swift
import Security

// Generate ECDSA key pair with P-256 curve
let attributes: [String: Any] = [
    kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
    kSecAttrKeySizeInBits as String: 256
]

var error: Unmanaged<CFError>?
guard let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &error) else {
    return
}

let publicKey = SecKeyCopyPublicKey(privateKey)!

// Sign with ECDSA
let data = "Important message".data(using: .utf8)! as CFData
let algorithm = SecKeyAlgorithm.ecdsaSignatureMessageX962SHA256

guard let signature = SecKeyCreateSignature(
    privateKey,
    algorithm,
    data,
    &error
) else {
    return
}

// Verify
let isValid = SecKeyVerifySignature(
    publicKey,
    algorithm,
    data,
    signature,
    &error
)
```

## Key Size Recommendations

When generating keys, follow these minimum sizes for long-term security:

- **RSA**: 3072 bits minimum, 4096 bits recommended
- **ECDSA**: P-256 (256 bits) minimum, P-384 (384 bits) for higher security
- **EdDSA**: Curve25519 (256 bits)

## Rely on System Cryptographic Libraries

Do not implement signature schemes manually. Custom implementations are prone to critical vulnerabilities:

- **Weak nonces for ECDSA**: Reusing or predictable nonces can leak the private key
- **Improper padding**: Incorrect RSA padding can lead to signature forgery
- **Timing attacks**: Side-channel vulnerabilities in custom implementations
- **Random number generation**: Poor entropy sources can compromise security

Always rely on:

- [CryptoKit](https://developer.apple.com/documentation/cryptokit) for modern APIs (iOS 13.0+)
- [Security framework](https://developer.apple.com/documentation/security) for SecKey operations
- Built-in random number generators like [`SystemRandomNumberGenerator`](https://developer.apple.com/documentation/swift/systemrandomnumbergenerator)

## References

- [Apple Developer - CryptoKit](https://developer.apple.com/documentation/cryptokit)
- [Apple Developer - Security Framework](https://developer.apple.com/documentation/security)
- [Apple Developer - Certificate, Key, and Trust Services](https://developer.apple.com/documentation/security/certificate_key_and_trust_services)
- [NIST SP 800-57 Part 1 Rev. 5 - Recommendation for Key Management](https://csrc.nist.gov/pubs/sp/800/57/pt1/r5/final)
- [NIST FIPS 186-5 - Digital Signature Standard (DSS)](https://csrc.nist.gov/pubs/fips/186-5/final)
- [RFC 8032 - Edwards-Curve Digital Signature Algorithm (EdDSA)](https://datatracker.ietf.org/doc/html/rfc8032)
