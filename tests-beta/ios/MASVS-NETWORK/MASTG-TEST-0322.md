---
platform: ios
title: App Transport Security Configurations Allowing Cleartext Traffic
id: MASTG-TEST-0322
type: [static]
weakness: MASWE-0050
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0071]
---

## Overview

Since iOS 9 App Transport Security (ATS) blocks cleartext HTTP traffic by default for connections using the [URL Loading System](https://developer.apple.com/documentation/foundation/url_loading_system) (typically via `URLSession`). However, an app can still send cleartext traffic through several ATS exceptions configured in the `Info.plist` file under the `NSAppTransportSecurity` key.

The following configurations allow cleartext traffic:

- **`NSAllowsArbitraryLoads`**: When set to `true`, disables ATS restrictions globally except for individual domains specified under `NSExceptionDomains`. This allows all HTTP connections.
- **`NSAllowsArbitraryLoadsInWebContent`**: When set to `true`, disables ATS restrictions for all connections made from WebViews.
- **`NSAllowsArbitraryLoadsForMedia`**: When set to `true`, disables all ATS restrictions for media loaded through the AV Foundations framework.
- **`NSExceptionAllowsInsecureHTTPLoads`**: When set to `true` for a specific domain under `NSExceptionDomains`, allows HTTP connections to that domain.

For more information on ATS configuration, see @MASTG-KNOW-0071.

!!! warning Limitations
    ATS only applies to connections made via the [URL Loading System](https://developer.apple.com/documentation/foundation/url_loading_system). Lower-level APIs such as the [`Network`](https://developer.apple.com/documentation/network) framework or [`CFNetwork`](https://developer.apple.com/documentation/cfnetwork) are not affected by ATS settings and may still allow cleartext traffic regardless of the configuration. See @MASTG-TEST-0323 for more details.

## Steps

1. Extract the app (@MASTG-TECH-0058).
2. Obtain the `Info.plist` file from the app bundle.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` to a readable format (if necessary).
4. Examine the `NSAppTransportSecurity` dictionary for cleartext traffic exceptions.

## Observation

The output should contain the ATS configuration, if present, including any exceptions that allow cleartext traffic.

## Evaluation

The test case fails if cleartext traffic is permitted. This can happen if **any** of the following conditions are met:

1. `NSAllowsArbitraryLoads = true` **only when** none of the fine-grained keys (2-4 below) are present (because on iOS 10+ they cause `NSAllowsArbitraryLoads` to be ignored).
2. `NSAllowsArbitraryLoadsInWebContent = true`.
3. `NSAllowsArbitraryLoadsForMedia = true`.
4. `NSAllowsLocalNetworking = true`.
5. Any domain under `NSExceptionDomains` sets `NSExceptionAllowsInsecureHTTPLoads = true`.

**Context Considerations**:

Note that ATS exceptions should be examined taking the app's context into consideration. The app may _have to_ define ATS exceptions to fulfill its intended purpose. For example, a browser app must connect to arbitrary websites, including those using HTTP. In such cases, the exception may be acceptable if a proper [justification string](https://developer.apple.com/documentation/security/preventing-insecure-network-connections#Provide-Justification-for-Exceptions) is provided. However, Apple recommends preferring server-side fixes over client-side ATS exceptions whenever possible.
