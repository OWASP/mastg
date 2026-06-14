---
platform: ios
title: References to File Storage Integrity Check APIs
id: MASTG-TEST-0x02
type: [static, code, manual]
weakness: MASWE-0105
false_negative_prone: true
profiles: [R]
knowledge: [MASTG-KNOW-0086]
best-practices: [MASTG-BEST-0x01]
---

## Overview

iOS apps can protect the integrity of data they store on the device (e.g., files in the Documents directory, `UserDefaults`/`NSUserDefaults`, or databases) by computing an HMAC or a digital signature over the data and verifying it before use (see @MASTG-KNOW-0086). If the app does not implement such checks, an attacker who modifies stored data can go undetected.

This test verifies that the app references APIs commonly used to implement file storage integrity checks, such as `CCHmac` (HMAC via CommonCrypto), `CC_SHA256`, `CC_SHA512`, or [`SecKeyCreateSignature`](https://developer.apple.com/documentation/security/seckeycreatesignature(_:_:_:_:)) (asymmetric signing).

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

The output should include any references to file storage integrity check APIs such as `CCHmac`, `CCHmacFinal`, `CC_SHA256`, `CC_SHA512`, or `SecKeyCreateSignature`.

## Evaluation

The test case fails if the app contains no references to file storage integrity check APIs.

**Further Validation Required:**

These APIs are commonly linked for unrelated purposes (for example, networking, analytics, or generic checksums), so their mere presence does not confirm a storage integrity mechanism. Inspect each reported code location using @MASTG-TECH-0076 to determine whether the referenced APIs actually protect stored data:

- Determine whether the HMAC or signature is computed over data the app reads back from local storage (files, `UserDefaults`/`NSUserDefaults`, or a database).
- Determine whether the app verifies the HMAC or signature before using the data and reacts when verification fails.

**Expected False Negatives:**

This test may produce false negatives if the integrity check relies on a third-party library, a custom implementation, or APIs not covered by the analysis. In such cases, the absence of findings does not guarantee the absence of a storage integrity check, and additional manual reverse engineering may be required.
