---
platform: ios
title: Missing Certificate Pinning in ATS
id: MASTG-TEST-0x01
type: [static]
weakness: MASWE-0047
profiles: [L2]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0072]
---

## Overview

iOS apps can configure certificate pinning via App Transport Security (ATS) by declaring expected CA or leaf certificate public key hashes in the `Info.plist` file under the [`NSPinnedDomains`](https://developer.apple.com/documentation/bundleresources/information-property-list/nsapptransportsecurity/nspinneddomains) key. This is Apple's recommended declarative approach for enforcing certificate pinning for connections made through the URL Loading System (for example, `URLSession`).

If an app doesn't configure `NSPinnedDomains`, it relies entirely on the system's default CA trust store, which means a MITM attacker with control over a trusted CA can intercept its traffic.

This test checks whether the app configures `NSPinnedDomains` in its ATS settings.

!!! warning Limitations
    ATS pinning only applies to connections made via the URL Loading System (for example, `URLSession`). It doesn't cover connections made using lower-level APIs such as the `Network` framework or `CFNetwork`. Additionally, `NSPinnedDomains` doesn't cover all possible certificate pinning implementations; the app may use manual `URLSessionDelegate` trust evaluation, third-party libraries, or native-code pinning instead. This test focuses exclusively on the ATS-based approach.

## Steps

1. Extract the app (@MASTG-TECH-0058).
2. Obtain the `Info.plist` file from the app bundle.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` to a readable format (if necessary).
4. Examine the `NSAppTransportSecurity` dictionary for the presence of a `NSPinnedDomains` key.

## Observation

The output should contain the ATS configuration, if present, including whether `NSPinnedDomains` is defined with one or more pinned domains and their associated public key hashes.

## Evaluation

The test case fails if the app's `Info.plist` does not contain an `NSAppTransportSecurity` dictionary with a `NSPinnedDomains` key, or if `NSPinnedDomains` is defined but doesn't include entries for all relevant domains the app connects to.
