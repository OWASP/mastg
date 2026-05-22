---
platform: ios
title: Broken Hashing Algorithms
id: MASTG-TEST-0211
type: [static, code, manual]
weakness: MASWE-0021
profiles: [L1, L2]
---

## Overview

To test for the use of broken hashing algorithms in iOS apps, we need to focus on APIs from cryptographic frameworks and libraries that are used to perform hashing operations.

- **CommonCrypto**: [CommonDigest.h](https://web.archive.org/web/20240606000312/https://opensource.apple.com/source/CommonCrypto/CommonCrypto-36064/CommonCrypto/CommonDigest.h) defines the following **hashing algorithms**:
    - `CC_MD2`
    - `CC_MD4`
    - `CC_MD5`
    - `CC_SHA1`
    - `CC_SHA224`
    - `CC_SHA256`
    - `CC_SHA384`
    - `CC_SHA512`

- **CryptoKit**: Supports three cryptographically secure **hashing algorithms** and two insecure ones in a dedicated class called [`Insecure`](https://developer.apple.com/documentation/cryptokit/insecure):
    - `SHA256`
    - `SHA384`
    - `SHA512`
    - `Insecure.MD5`
    - `Insecure.SHA1`

Note: the **Security** framework only supports asymmetric algorithms and is therefore out of scope for this test.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain the disassembled code of the functions using the relevant cryptographic functions.

## Evaluation

The test case fails if you can find the use of broken hashing algorithms within the source code. For example:

- MD5
- SHA-1

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine whether the algorithm is used in a security-relevant context to protect sensitive data:

- Determine whether the hashing algorithm is used for cryptographic security purposes rather than for non-security tasks such as checksums. For example, using MD5 for hashing passwords is disallowed by NIST, but using MD5 for checksums where security is not a concern is generally acceptable.

**Stay up-to-date**: This is a non-exhaustive list of broken algorithms. Make sure to check the latest standards and recommendations from organizations such as the National Institute of Standards and Technology (NIST), the German Federal Office for Information Security (BSI) or any other relevant authority in your region. This is important when building an app that uses data that will be stored for a long time. Make sure you follow the NIST recommendations from [NIST IR 8547 "Transition to Post-Quantum Cryptography Standards", 2024](https://csrc.nist.gov/pubs/ir/8547/ipd).
