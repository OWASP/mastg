---
platform: ios
title: References to File Storage Integrity Check APIs
id: MASTG-TEST-0355
type: [static, code]
weakness: MASWE-0105
false_negative_prone: true
profiles: [R]
knowledge: [MASTG-KNOW-0086]
best-practices: [MASTG-BEST-0048]
---

## Overview

iOS apps can protect the integrity of data they store on the device (e.g., files in the Documents directory, `UserDefaults`/`NSUserDefaults`, or databases) by computing an HMAC or a digital signature over the data and verifying it before use. If the app does not implement such checks, an attacker who modifies stored data (see @MASTG-KNOW-0086) may go undetected.

This test verifies that the app references APIs commonly used to implement file storage integrity checks, such as [`CCHmac`](https://developer.apple.com/documentation/cryptokit) (HMAC via CommonCrypto), `CC_SHA256`, `CC_SHA512`, or [`SecKeyCreateSignature`](https://developer.apple.com/documentation/security/seckeyoperationtype/createsignature) (asymmetric signing).

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should include any references to file storage integrity check APIs such as `CCHmac`, `CCHmacFinal`, `CC_SHA256`, `CC_SHA512`, or `SecKeyCreateSignature`.

## Evaluation

The test case fails if the app contains no references to file storage integrity check APIs.

Note that this test is not exhaustive and may not detect all file storage integrity check implementations, especially if they use third-party libraries or custom implementations not covered by the analysis.
