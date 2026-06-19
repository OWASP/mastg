---
platform: ios
title: Missing Certificate Pinning in ATS
id: MASTG-TEST-0385
type: [static]
weakness: MASWE-0047
profiles: [L2]
prerequisites:
- identify-first-party-domains
knowledge: [MASTG-KNOW-0072]
---

## Overview

iOS apps can configure certificate pinning via App Transport Security (ATS) by declaring expected CA or leaf certificate public key hashes in the `Info.plist` file under the [`NSPinnedDomains`](https://developer.apple.com/documentation/bundleresources/information-property-list/nsapptransportsecurity/nspinneddomains) key. This is Apple's built-in mechanism for pinning connections made through the [URL Loading System](https://developer.apple.com/documentation/foundation/url_loading_system), such as `URLSession`.

This test checks whether the app configures `NSPinnedDomains` for relevant first-party domains that the app connects to. Relevant domains are remote endpoints under the developer's control that support the app's core or security-sensitive functionality, such as authentication, account data, user content, or app-specific APIs. Third-party domains outside the developer's control **should not be reported** as missing ATS pins only because they appear in app traffic.

!!! warning Limitations
    ATS pinning applies to connections made through the URL Loading System, for example `URLSession`. Without ATS pinning, URL Loading System connections to the affected domain rely on the system's default CA trust evaluation, unless the app performs certificate pinning through another mechanism, such as [custom server trust evaluation](https://developer.apple.com/documentation/foundation/performing-manual-server-trust-authentication). This test doesn't cover all possible networking APIs or all possible certificate pinning implementations. The app may use manual `URLSessionDelegate` trust evaluation, third-party libraries, native-code pinning, or another networking stack. This test focuses on the ATS-based approach and should be interpreted together with tests that identify custom pinning implementations and runtime network behavior.

## Steps

1. Extract the app (@MASTG-TECH-0058).
2. Obtain the `Info.plist` file from the app bundle.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` to a readable format (if necessary).
4. Examine the `NSAppTransportSecurity` dictionary for the presence of a `NSPinnedDomains` key.
5. Use @MASTG-TECH-0071 to retrieve hardcoded URLs and identify the first-party domains the app connects to.

## Observation

The output should contain the ATS configuration, if present, including whether `NSPinnedDomains` is defined with one or more pinned domains and their associated public key hashes. The output should also identify any relevant first-party domains that were found in the app but are not listed under `NSPinnedDomains`.

## Evaluation

The test case fails if the app uses URL Loading System connections to relevant first-party domains, but the app's `Info.plist` does not contain an `NSAppTransportSecurity` dictionary with a `NSPinnedDomains` key, or if `NSPinnedDomains` is defined but does not include entries for those domains.

The test case should not fail only because unrelated third-party domains are not pinned.

If another certificate pinning implementation is identified for the same domains, such as custom server trust evaluation, the result should be treated as not covered by ATS pinning rather than as a confirmed absence of certificate pinning.

**Further Validation Required:**

Before reporting a missing pin, confirm that the app actually establishes URL Loading System connections to the relevant first-party domains:

- Statically, follow the data references from the hardcoded URLs to the code that initiates the network connections (@MASTG-TECH-0076).
- Dynamically, capture and analyze the network traffic (@MASTG-TECH-0063) or hook the relevant network APIs at runtime to log the domains the app connects to.

Determining which domains are first-party and security-relevant typically requires information that is not present in the app binary and may require contact with the developers.
