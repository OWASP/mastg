---
platform: ios
title: App Transport Security Configurations Allowing Cleartext Traffic
id: MASTG-TEST-0322
type: [static, code, manual]
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

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0155 to analyze the ATS configuration for cleartext traffic exceptions.

## Observation

The output should contain the ATS configuration, if present, including any exceptions that allow cleartext traffic.

## Evaluation

The test case fails if cleartext traffic is permitted. This can happen if **any** of the following conditions are met:

1. `NSAllowsArbitraryLoads = true` **only when** none of the fine-grained keys (2-4 below) are present (because on iOS 10+ they cause `NSAllowsArbitraryLoads` to be ignored).
2. `NSAllowsArbitraryLoadsInWebContent = true`.
3. `NSAllowsArbitraryLoadsForMedia = true`.
4. `NSAllowsLocalNetworking = true`.
5. Any domain under `NSExceptionDomains` sets `NSExceptionAllowsInsecureHTTPLoads = true`.

**Further Validation Required:**

Inspect the identified ATS exceptions to determine whether they are justified for the app's intended purpose:

- Determine whether the exception is required for the app to fulfill its core functionality (for example, a browser app must connect to arbitrary websites, including those using HTTP).
- If possible, verify that a proper [justification string](https://developer.apple.com/documentation/security/preventing-insecure-network-connections#Provide-Justification-for-Exceptions) has been provided. This would be only possible if you have contact with the developers, as this information is not included in the app binary.
