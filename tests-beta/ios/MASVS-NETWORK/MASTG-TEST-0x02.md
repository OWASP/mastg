---
platform: ios
title: Expired Certificate Pins in ATS
id: MASTG-TEST-0x02
type: [static]
weakness: MASWE-0047
profiles: [L2]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0072]
---

## Overview

iOS apps can configure certificate pinning via App Transport Security (ATS) using the [`NSPinnedDomains`](https://developer.apple.com/documentation/bundleresources/information-property-list/nsapptransportsecurity/nspinneddomains) key in `Info.plist`. Unlike Android's Network Security Configuration, ATS `NSPinnedDomains` doesn't support expiration dates for pins. However, apps that use third-party certificate pinning libraries (such as [TrustKit](https://github.com/datatheorem/TrustKit)) may configure pin expiration dates in `Info.plist` through the library's own configuration keys (for example, TrustKit's `kTSKExpirationDate`).

If a pin expiration date is set and has passed, pinning enforcement is typically disabled by the library, causing the app to accept any certificate valid according to the system trust store. If developers assume pinning is still in effect after it has expired, the app may start trusting CAs it was never intended to trust.

> Example: A financial app previously pinned to its own private CA, but after the expiration date passes, the library stops enforcing pinning and starts accepting certificates from any publicly trusted CA, increasing the risk of compromise if a CA is breached.

The goal of this test is to check if any pin expiration date configured in `Info.plist` is in the past.

## Steps

1. Extract the app (@MASTG-TECH-0058).
2. Obtain the `Info.plist` file from the app bundle.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` to a readable format (if necessary).
4. Look for certificate pinning library configuration keys in `Info.plist` (for example, TrustKit's `TSKConfiguration` dictionary with `kTSKExpirationDate` values).
5. Extract all expiration dates from the pinning configuration.

## Observation

The output should contain a list of expiration dates found for pinned certificates, alongside the domains they are associated with.

## Evaluation

The test case fails if any expiration date found is in the past.
