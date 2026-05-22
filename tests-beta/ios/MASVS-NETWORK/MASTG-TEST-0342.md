---
platform: ios
title: References to Weak ATS TLS Policy Exceptions in Info.plist
id: MASTG-TEST-0342
type: [static, code]
weakness: MASWE-0050
profiles: [L1, L2]
best-practices: [MASTG-BEST-0042]
knowledge: [MASTG-KNOW-0071]
---

## Overview

Apps can weaken ATS TLS enforcement through `NSAppTransportSecurity` exceptions in `Info.plist`. In particular:

- [`NSExceptionMinimumTLSVersion`](https://developer.apple.com/documentation/bundleresources/information-property-list/nsexceptionminimumtlsversion) allows connections to servers with TLS versions below 1.2, including the deprecated TLS 1.0 and TLS 1.1.
- [`NSExceptionRequiresForwardSecrecy`](https://developer.apple.com/documentation/bundleresources/information-property-list/nsexceptionrequiresforwardsecrecy) set to `false` disables the ATS requirement for [Perfect Forward Secrecy (PFS)](https://developer.apple.com/documentation/security/preventing-insecure-network-connections), weakening the confidentiality of the connection even when TLS itself is otherwise required.

These exceptions are applied per domain under `NSExceptionDomains`. When broadly scoped (especially with `NSIncludesSubdomains = true`), they may affect many hosts and increase the attack surface for [Machine-in-the-Middle (MITM)](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) attacks. Apple requires a justification for these exceptions when submitting to the App Store. See @MASTG-KNOW-0071 for more details on ATS configuration and exceptions.

Apps can also globally disable ATS by setting [`NSAllowsArbitraryLoads`](https://developer.apple.com/documentation/bundleresources/information_property_list/nsapptransportsecurity/nsallowsarbitraryloads) to `true`. This disables ATS protections for connections made through the URL Loading System, including ATS requirements such as minimum TLS version and forward secrecy, and permits plaintext HTTP communication. It may also relax ATS-specific certificate requirements. Baseline TLS/X.509 certificate chain validation and server trust evaluation performed by the URL Loading System still apply. Per-domain entries under `NSExceptionDomains` override the global setting. For example, if `NSAllowsArbitraryLoads` is `true` but `tls-v1-2.example.com` has `NSExceptionMinimumTLSVersion = "TLSv1.2"`, that domain still requires TLS 1.2 or higher, while all other domains have ATS disabled. This includes the ability of all other domains to use plaintext HTTP.

## Steps

1. Use @MASTG-TECH-0058 to unzip the app package.
2. Use @MASTG-TECH-0153 to retrieve the `Info.plist` file.
3. Use @MASTG-TECH-0155 to analyze the ATS configuration for TLS policy exceptions, specifically `NSExceptionMinimumTLSVersion`, `NSExceptionRequiresForwardSecrecy`, and `NSAllowsArbitraryLoads`.

## Observation

The output should contain any TLS policy exceptions configured under `NSAppTransportSecurity`, if present.

## Evaluation

The test case fails if **any** of the following conditions are met:

1. `NSAllowsArbitraryLoads` is set to `true`. This disables ATS for all connections to domains not listed in `NSExceptionDomains`. Per-domain exceptions in `NSExceptionDomains` still apply to their respective domains, but all other domains have no ATS restrictions.
2. Any domain, IP address, or IP address range sets `NSExceptionMinimumTLSVersion` to `TLSv1.0` or `TLSv1.1`.
3. Any domain, IP address, or IP address range sets `NSExceptionRequiresForwardSecrecy` to `false`, `NO`, or `0`.

!!! note "App Store Submission Context"
    Apple may require [justification](https://developer.apple.com/documentation/security/preventing-insecure-network-connections#Provide-Justification-for-Exceptions) for ATS exceptions during App Store submission. If available, record that evidence in the report as contextual information only.
