---
title: Use Secure Algorithms for Digital Signature Generation
alias: use-secure-digital-signature-algorithms
id: MASTG-BEST-0025
platform: android
---

When generating digital signatures in Android applications, use secure cryptographic algorithms and avoid deprecated or weak implementations. Improper signature generation can allow attackers to forge signatures, tamper with data, or bypass authentication mechanisms.

## Use Secure Signature Algorithms

Use [`java.security.Signature`](https://developer.android.com/reference/java/security/Signature) with secure algorithm names. Recommended algorithms include:

- **RSA-based**: `SHA256withRSA`, `SHA384withRSA`, `SHA512withRSA`
- **ECDSA-based**: `SHA256withECDSA`, `SHA384withECDSA`, `SHA512withECDSA`

Avoid deprecated or insecure algorithms such as:

- `MD5withRSA` - MD5 is cryptographically broken
- `SHA1withRSA` - SHA-1 is deprecated and vulnerable to collision attacks
- `NONEwithRSA` - Allows signing arbitrary data without hashing

Example of secure signature generation:

```java
// Generate a key pair
KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
keyGen.initialize(3072);
KeyPair keyPair = keyGen.generateKeyPair();

// Create and initialize signature instance
Signature signature = Signature.getInstance("SHA256withRSA");
signature.initSign(keyPair.getPrivate());

// Sign the data
byte[] data = "Important message".getBytes(StandardCharsets.UTF_8);
signature.update(data);
byte[] digitalSignature = signature.sign();
```

For verification:

```java
Signature signature = Signature.getInstance("SHA256withRSA");
signature.initVerify(keyPair.getPublic());
signature.update(data);
boolean isValid = signature.verify(digitalSignature);
```

## Configure Android Keystore Securely

When generating keys in the [Android Keystore](https://developer.android.com/privacy-and-security/keystore), use [`KeyGenParameterSpec`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec) to specify secure parameters:

- **Digest algorithms**: Use SHA-256, SHA-384, or SHA-512
- **RSA key size**: At least 3072 bits (4096 bits recommended for long-term security)
- **EC curves**: Use NIST P-256 (secp256r1) or higher (P-384, P-521)

Example of secure key generation:

```java
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
    KeyProperties.KEY_ALGORITHM_RSA, 
    "AndroidKeyStore"
);

KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
    "myKeyAlias",
    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY
)
    .setDigests(
        KeyProperties.DIGEST_SHA256,
        KeyProperties.DIGEST_SHA384,
        KeyProperties.DIGEST_SHA512
    )
    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
    .setKeySize(3072)
    .setUserAuthenticationRequired(false)
    .build();

keyPairGenerator.initialize(spec);
KeyPair keyPair = keyPairGenerator.generateKeyPair();
```

For ECDSA:

```java
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
    KeyProperties.KEY_ALGORITHM_EC,
    "AndroidKeyStore"
);

KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
    "myEcKeyAlias",
    KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY
)
    .setDigests(
        KeyProperties.DIGEST_SHA256,
        KeyProperties.DIGEST_SHA384,
        KeyProperties.DIGEST_SHA512
    )
    .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
    .build();

keyPairGenerator.initialize(spec);
KeyPair keyPair = keyPairGenerator.generateKeyPair();
```

## Rely on Platform Cryptographic Providers

Do not implement signature schemes yourself in application code. Always prefer well-maintained libraries and the Android platform crypto providers:

- Use the [Android Keystore](https://developer.android.com/privacy-and-security/keystore) for key generation and storage
- Use [java.security](https://developer.android.com/reference/java/security/package-summary) APIs for signature operations
- Keep Google Play Services updated to receive security patches (see @MASTG-BEST-0020)

Custom implementations are prone to critical vulnerabilities such as:

- **Weak nonces for ECDSA**: Reusing nonces or using predictable nonces can leak the private key
- **Improper padding**: Incorrect RSA padding can lead to signature forgery
- **Timing attacks**: Side-channel vulnerabilities in custom implementations

## References

- [Android Developers - Cryptography](https://developer.android.com/privacy-and-security/cryptography)
- [Android Developers - Broken or risky cryptographic algorithm](https://developer.android.com/privacy-and-security/risks/broken-cryptographic-algorithm)
- [NIST SP 800-57 Part 1 Rev. 5 - Recommendation for Key Management](https://csrc.nist.gov/pubs/sp/800/57/pt1/r5/final)
- [NIST FIPS 186-5 - Digital Signature Standard (DSS)](https://csrc.nist.gov/pubs/fips/186-5/final)
- [RFC 8017 - PKCS #1: RSA Cryptography Specifications Version 2.2](https://datatracker.ietf.org/doc/html/rfc8017)
