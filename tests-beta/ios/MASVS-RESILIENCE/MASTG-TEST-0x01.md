---
platform: ios
title: Testing Device and App Binary Integrity Verification
id: MASTG-TEST-0x01
apis: [DCAppAttestService, DCAppAttestService.generateKey, DCAppAttestService.attestKey, DCAppAttestService.generateAssertion]
type: [static]
weakness: MASWE-0104
best-practices: [MASTG-BEST-0x01]
profiles: [R]
knowledge: [MASTG-KNOW-0x03]
status: placeholder
---

## Overview

This test checks whether the app implements device and app integrity verification by statically analyzing the app binary for relevant API usage patterns. On iOS, modern app attestation is provided by [App Attest](https://developer.apple.com/documentation/devicecheck), part of the DeviceCheck framework, which lets a server cryptographically verify that a request comes from a genuine, unmodified instance of the app running on a real Apple device (@MASTG-KNOW-0x03).

See @MASTG-KNOW-0x03 for more information on App Attest and the specific APIs and fields to look for.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where the App Attest APIs are used.

## Evaluation

The test case fails if none of the App Attest APIs (`DCAppAttestService.generateKey`, `attestKey`, `generateAssertion`) are found in the app.

**Further Validation Required:**

The presence of these APIs does not by itself confirm a robust implementation. The following properties cannot be determined from a reference search alone and require manual or dynamic analysis:

- The `clientDataHash` passed to `attestKey` and `generateAssertion` must be derived from a one-time, server-provided challenge; otherwise the attestation can be replayed.
- The app should generate an assertion (`generateAssertion`) for sensitive requests, not only attest the key once (`attestKey`), so that ongoing requests remain bound to the attested app instance.
- The server must verify the attestation object and assertions against Apple's [published verification steps](https://developer.apple.com/documentation/devicecheck/validating-apps-that-connect-to-your-server); client-side use of `DCAppAttestService` alone provides no security guarantee.
