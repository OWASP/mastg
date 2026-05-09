---
platform: ios
title: ATS TLS Policy Exceptions in Info.plist
id: MASTG-TEST-0x01
type: [static]
weakness: MASWE-0050
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0071]
---

## Overview

Apps can weaken ATS TLS enforcement through `NSAppTransportSecurity` exceptions in `Info.plist`. In particular:

- [`NSExceptionMinimumTLSVersion`](https://developer.apple.com/documentation/bundleresources/information_property_list/nsapptransportsecurity/nsexceptiondomains/nsexceptionminimumtlsversion) allows connections to servers with TLS versions below 1.2, including the deprecated TLS 1.0 and TLS 1.1.
- [`NSExceptionRequiresForwardSecrecy`](https://developer.apple.com/documentation/bundleresources/information_property_list/nsapptransportsecurity/nsexceptiondomains/nsexceptionrequiresforwardsecrecy) set to `false` disables the ATS requirement for [Perfect Forward Secrecy (PFS)](https://developer.apple.com/documentation/security/preventing-insecure-network-connections), weakening the confidentiality of the connection even when TLS itself is otherwise required.

These exceptions are applied per domain under `NSExceptionDomains`. When broadly scoped (especially with `NSIncludesSubdomains = true`), they may affect many hosts and increase the attack surface for man-in-the-middle attacks. Apple requires a justification for these exceptions when submitting to the App Store. See @MASTG-KNOW-0071 for more details on ATS configuration and exceptions.

## Steps

1. Extract the app (@MASTG-TECH-0058).
2. Locate the `Info.plist` in the app bundle.
3. Use @MASTG-TECH-0138 to convert the `Info.plist` to a readable format if necessary.
4. Examine the `NSAppTransportSecurity` dictionary for TLS policy exceptions, specifically `NSExceptionMinimumTLSVersion` and `NSExceptionRequiresForwardSecrecy`.

## Observation

The output should contain any TLS policy exceptions configured under `NSAppTransportSecurity`, if present.

## Evaluation

The test case fails if **any** of the following conditions are met:

1. Any domain sets `NSExceptionMinimumTLSVersion` to `TLSv1.0` or `TLSv1.1`.
2. Any domain sets `NSExceptionRequiresForwardSecrecy` to `false` (or `NO`).

**Context Considerations:**

Inspect the justification for each exception. An exception may be acceptable if the domain is a known third-party service that does not yet support TLS 1.2 or forward secrecy, and the exception is narrowly scoped to that specific domain. However, Apple [recommends preferring server-side fixes](https://developer.apple.com/documentation/security/preventing-insecure-network-connections#Configure-Exceptions-Only-When-Needed-Prefer-Server-Fixes) over ATS exceptions whenever possible.
