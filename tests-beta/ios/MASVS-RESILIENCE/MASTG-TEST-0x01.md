---
platform: ios
title: References to Storage Integrity Check APIs
id: MASTG-TEST-0x01
apis: [HMAC, CCHmac, CCHmacFinal, CC_SHA256, CC_SHA512, SecKeyCreateSignature]
type: [static, code, manual]
weakness: MASWE-0105
false_negative_prone: true
profiles: [R]
knowledge: [MASTG-KNOW-0086]
best-practices: [MASTG-BEST-0x01]
---

## Overview

iOS apps can protect the integrity and authenticity of data they store on the device (e.g., files in the Documents directory, `UserDefaults`/`NSUserDefaults`, or databases) by computing an HMAC or a digital signature over the data and verifying it before use (see @MASTG-KNOW-0086). If the app does not implement such checks, an attacker who modifies the stored data can go undetected and the app may trust the tampered input in a security-relevant decision.

Even data stored in the app's sandbox (such as `UserDefaults`/`NSUserDefaults`) normally cannot be modified by other apps, but it can still be tampered with in local attack scenarios, such as on jailbroken devices, during dynamic analysis, through backups, or by directly manipulating the app's data directory after obtaining privileged access, as described in @MASTG-KNOW-0086. Because of that, apps should not blindly trust security-relevant data loaded from local storage.

This test verifies that the app references APIs commonly used to implement storage integrity checks, such as `HMAC` (the modern CryptoKit API), `CCHmac`/`CCHmacFinal` (HMAC via CommonCrypto), `CC_SHA256`/`CC_SHA512` (hash functions), or [`SecKeyCreateSignature`](https://developer.apple.com/documentation/security/seckeycreatesignature(_:_:_:_:)) (asymmetric signing). Depending on the implementation, the relevant logic may also include MAC comparison, cryptographic initialization, signature verification, or other mechanisms intended to detect tampering.

**Example Attack Scenario:**

Suppose an app stores a usage counter or entitlement flag in `UserDefaults` and trusts it without verifying its integrity.

1. An attacker uses @MASTG-TECH-0059 to locate the app's data directories on a jailbroken device.
2. The attacker modifies the stored value (for example, resets a trial counter or flips a "premium" flag).
3. Because the app never verifies an HMAC or signature over the stored data, it loads the tampered value as authentic.
4. The attacker bypasses the intended restriction, gaining access to functionality or content they should not have.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of code locations where the relevant APIs are used. Depending on the storage API and the analysis rule, these locations may include storage read APIs such as `Data(contentsOf:)`, `UserDefaults` reads, or database queries, as well as nearby integrity-verification logic such as HMAC, MAC comparison, or signature verification.

## Evaluation

The test case fails if the app uses data loaded from local storage (files, `UserDefaults`/`NSUserDefaults`, or databases) in a security-relevant decision without verifying its integrity and authenticity beforehand.

**Further Validation Required:**

These APIs are commonly used for unrelated purposes (for example, networking, analytics, or generic checksums), so their mere presence does not confirm a storage integrity mechanism. Inspect each reported code location using @MASTG-TECH-0076 to determine whether the data is protected:

- Determine whether the loaded value can influence a security-relevant decision, such as authentication state, authorization, feature access, configuration, or trust decisions.
- Determine whether the app computes an HMAC or signature over the data it reads back from local storage, and verifies it before use, reacting when verification fails.
- Determine whether that validation is effective for the attacker model in scope.

**Expected False Negatives:**

This test may produce false negatives if the integrity check relies on a third-party library, a custom implementation, or APIs not covered by the analysis. In such cases, the absence of findings does not guarantee the absence of a storage integrity check, and additional manual reverse engineering may be required.
