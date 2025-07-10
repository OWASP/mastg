---
platform: ios
title: Weak Encryption Algorithms
id: MASTG-TEST-0210
type: [static, dynamic]
weakness: MASWE-0020
profiles: [L1, L2]
---

## Overview

To test for the use of weak encryption algorithms in iOS apps, we need to focus on methods from cryptographic frameworks and libraries that are used to perform encryption and decryption operations.

- **CommonCrypto**: The [`CCCrypt`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man3/CCCrypt.3cc.html) function is used for **symmetric algorithms** and specifies the algorithm in its second parameter `alg` which can be one of the following [`CCAlgorithm`](https://github.com/Apple-FOSS-Mirror/CommonCrypto/blob/v60026/CommonCrypto/CommonCryptor.h#L149-L158) constants:
    - `kCCAlgorithmAES128`
    - `kCCAlgorithmDES`
    - `kCCAlgorithm3DES`
    - `kCCAlgorithmCAST`
    - `kCCAlgorithmRC4`
    - `kCCAlgorithmRC2`
    - `kCCAlgorithmBlowfish`

- **CryptoKit**: This library does not support weak encryption algorithms. It only supports secure **symmetric algorithms** including `AES.GCM` and `ChaChaPoly`.
- **Third-party libraries**: Applications may include cryptographic functions through third-party libraries such as OpenSSL, BoringSSL, Libsodium, or even custom cryptographic implementations. These may support or expose weak encryption algorithms like DES, 3DES, RC2, RC4, or Blowfish.

Note: the **Security** framework only supports asymmetric algorithms and is therefore out of scope for this test (see [note about symmetric keys](https://developer.apple.com/documentation/security/certificate_key_and_trust_services/keys/generating_new_cryptographic_keys#2863931)).

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0073 on the app binary, or use a dynamic analysis tool like @MASTG-TOOL-0039, and look for uses of the cryptographic functions that perform encryption and decryption operations.

## Observation

The output should contain the disassembled code of the functions using the relevant cryptographic functions.

## Evaluation

The test case fails if you can find the use of weak encryption algorithms within the source code. For example:

- DES
- 3DES
- RC2
- RC4
- Blowfish

This applies to Apple-provided APIs (e.g., CommonCrypto), third-party libraries (e.g., OpenSSL, Libsodium) and any custom implementations of cryptographic routines that replicate insecure algorithms (e.g., [DES in C code](https://github.com/tarequeh/DES/blob/master/des.c)) or use insecure configurations (e.g., ECB mode, short key sizes).

**Stay up-to-date**: This is a non-exhaustive list of weak algorithms. Make sure to check the latest standards and recommendations from organizations such as the National Institute of Standards and Technology (NIST), the German Federal Office for Information Security (BSI) or any other relevant authority in your region.

Some algorithms may not be considered weak as a whole, but may have **weak configurations** that should be avoided. Such as using a key with insufficient strength or not being considered quantum-safe. For example, an AES 128-bit key size is considered weak considering quantum computing attacks.

**Context Considerations**:

To reduce false positives, make sure you understand the context in which the algorithm is being used before reporting the associated code as insecure. Ensure that it is being used in a security-relevant context to protect sensitive data.
