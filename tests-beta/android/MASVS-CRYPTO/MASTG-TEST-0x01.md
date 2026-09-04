---
title: Broken Hashing Algorithms
platform: android
id: MASTG-TEST-0x01
type: [static, code, manual]
weakness: MASWE-0008
profiles: [L1, L2]
---

## Overview

If an Android app hashes passwords, tokens, or other sensitive data with broken algorithms such as MD5 or SHA-1, an attacker can exploit known collision and preimage attacks to recover or forge that data. To test for this, we need to focus on APIs from cryptographic frameworks and libraries that are used to perform hashing operations.

- **Java Cryptography Architecture (JCA)**: [`MessageDigest.getInstance`](https://developer.android.com/reference/java/security/MessageDigest#getInstance(java.lang.String)) initializes a `MessageDigest` object for hashing. The `algorithm` parameter can be one of the [supported algorithms](https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#MessageDigest). Common JCA names and aliases include:
    - `MD2`
    - `MD5`
    - `SHA-1`, `SHA1`, `SHA` (aliases of SHA-1)
    - `SHA-224`
    - `SHA-256`
    - `SHA-384`
    - `SHA-512`

Some broken hashing algorithms include:

- **MD5**: Collision and preimage attacks are practical. It is not approved by NIST for cryptographic protection and is documented as inadequate in [RFC 6151](https://www.rfc-editor.org/rfc/rfc6151).
- **SHA-1**: Chosen-prefix collisions are practical ([SHAttered](https://shattered.io/)). NIST [deprecated SHA-1](https://csrc.nist.gov/news/2022/nist-retires-sha-1-and-discusses-sha-3) for cryptographic use.

Android also provides additional guidance on [broken cryptographic algorithms](https://developer.android.com/privacy-and-security/risks/broken-cryptographic-algorithm).

Third-party wrappers such as Guava `Hashing.md5()` / `Hashing.sha1()` and Apache Commons Codec `DigestUtils` may call the same algorithms. Native libraries (for example OpenSSL `MD5_*` / `SHA1_*`) can implement hashing without going through `MessageDigest`.

**Out of Scope**: `Mac.getInstance` (HMAC) and password-based key derivation functions such as `PBKDF2WithHmacSHA1` are out of scope for this test.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where broken hashing algorithms are used.

## Evaluation

The test case fails if you can find the use of broken hashing algorithms. For example:

- MD5
- SHA-1

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the algorithm is used in a security-relevant context to protect sensitive data:

- Determine whether the hashing algorithm is used for cryptographic security purposes rather than for non-security tasks such as checksums. For example, using MD5 for hashing passwords is disallowed by NIST, but using MD5 for checksums where security is not a concern is generally acceptable.

**Stay up-to-date**: This is a non-exhaustive list of broken algorithms. Make sure to check the latest standards and recommendations from organizations such as the National Institute of Standards and Technology (NIST), the German Federal Office for Information Security (BSI) or any other relevant authority in your region. This is important when building an app that uses data that will be stored for a long time. Make sure you follow the NIST recommendations from [NIST IR 8547 "Transition to Post-Quantum Cryptography Standards", 2024](https://csrc.nist.gov/pubs/ir/8547/ipd).

**Expected False Negatives:**

This test may produce false negatives if the algorithm name is not a string literal (for example, it is stored in a variable or constructed at runtime), if hashing is implemented in native code, or if a third-party wrapper does not call `MessageDigest.getInstance` with a detectable algorithm name. If you suspect native hashing, use @MASTG-TECH-0018 on bundled libraries and look for symbols such as OpenSSL `MD5_*` or `SHA1_*`. Additional manual reverse engineering of native libraries or third-party code may be required.
