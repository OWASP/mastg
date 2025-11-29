---
title: Avoid Improper Hashing Algorithms
alias: android-avoid-improper-hashing
id: MASTG-BEST-0025
platform: android
---

Use cryptographically strong hashing algorithms appropriate for their intended purpose. Weak algorithms like MD5 and SHA-1 are vulnerable to collision attacks and should not be used for security-sensitive operations.

## General Hashing for Integrity and Fingerprinting

Avoid using `MessageDigest` with weak algorithms such as "MD5" and "SHA1". Instead, use SHA-256 or SHA-512 when hashing for integrity checks or fingerprinting purposes.

MD5 and SHA-1 have known vulnerabilities that make them unsuitable for security purposes:

- **MD5**: [RFC 6151](https://datatracker.ietf.org/doc/html/rfc6151) formally deprecates MD5 for security applications due to collision vulnerabilities demonstrated in [research by Wang et al.](https://www.iacr.org/archive/crypto2004/31520306/31520306.pdf)
- **SHA-1**: Declared deprecated by NIST in [SP 800-131A Rev. 2](https://csrc.nist.gov/pubs/sp/800/131/a/r2/final) and shown vulnerable through [the SHAttered attack](https://shattered.io/)

### Recommended Approach

```java
// Good: Use SHA-256 or stronger
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hash = digest.digest(data);
```

```kotlin
// Good: Use SHA-256 or stronger
val digest = MessageDigest.getInstance("SHA-256")
val hash = digest.digest(data)
```

### What to Avoid

```java
// Bad: Avoid MD5 and SHA-1
MessageDigest md5 = MessageDigest.getInstance("MD5");
MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
```

For detailed information on secure hashing algorithms, refer to [NIST's guidance on cryptographic algorithms](https://csrc.nist.gov/projects/hash-functions).

## Password Hashing

Never use simple hash functions (even SHA-256 or SHA-512) directly for password hashing. Password hashing requires specialized algorithms that are intentionally slow and resource-intensive to resist brute-force attacks.

### Use PBKDF2 with High Iteration Counts

Use [`SecretKeyFactory`](https://developer.android.com/reference/javax/crypto/SecretKeyFactory) with `PBKDF2WithHmacSHA256` or `PBKDF2WithHmacSHA512` for password hashing. These algorithms apply many iterations of a hash function, making brute-force attacks computationally expensive.

```java
// Good: Use PBKDF2 for password hashing
int iterations = 600000; // OWASP recommendation for PBKDF2-SHA256
int keyLength = 256; // bits

SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
byte[] hash = factory.generateSecret(spec).getEncoded();
```

```kotlin
// Good: Use PBKDF2 for password hashing
val iterations = 600000 // OWASP recommendation for PBKDF2-SHA256
val keyLength = 256 // bits

val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength)
val hash = factory.generateSecret(spec).encoded
```

### Important Considerations

- **Iteration Count**: Use at least 600,000 iterations for PBKDF2-SHA256 as recommended by [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#pbkdf2)
- **Salt**: Always use a unique, cryptographically random salt for each password (minimum 16 bytes). Generate salts using `SecureRandom`
- **Avoid Custom Constructions**: Do not attempt to create your own password hashing scheme by combining hash functions

For comprehensive password storage guidance, see the [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html).

## Message Authentication and Key Derivation

### Use HMAC for Keyed Integrity

When you need message authentication with a secret key, use HMAC (Hash-based Message Authentication Code) rather than manually combining a key with data before hashing.

```java
// Good: Use HMAC for message authentication
Mac hmac = Mac.getInstance("HmacSHA256");
SecretKeySpec keySpec = new SecretKeySpec(secretKey, "HmacSHA256");
hmac.init(keySpec);
byte[] mac = hmac.doFinal(message);
```

```kotlin
// Good: Use HMAC for message authentication
val hmac = Mac.getInstance("HmacSHA256")
val keySpec = SecretKeySpec(secretKey, "HmacSHA256")
hmac.init(keySpec)
val mac = hmac.doFinal(message)
```

### Use HKDF Only for High Entropy Inputs

HKDF (HMAC-based Key Derivation Function) is designed for deriving keys from high-entropy secret material, not for password-based key derivation. Use HKDF only when you have a strong source of entropy (such as Diffie-Hellman shared secrets or existing cryptographic keys).

For password-based key derivation, always use PBKDF2 as described above.

HKDF is available through [`HKDFParameterSpec`](https://developer.android.com/reference/javax/crypto/spec/HKDFParameterSpec) (Android API level 35+) or third-party libraries like [Google Tink](https://github.com/tink-crypto/tink-java).

For detailed guidance on key derivation, refer to [NIST SP 800-108 Rev. 1](https://csrc.nist.gov/pubs/sp/800/108/r1/upd1/final) and [RFC 5869](https://datatracker.ietf.org/doc/html/rfc5869).

## Additional Resources

- [Android Cryptography Guidance](https://developer.android.com/privacy-and-security/cryptography)
- [NIST Hash Functions](https://csrc.nist.gov/projects/hash-functions)
- [OWASP Cryptographic Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html)
