---
platform: ios
title: References to App Attestation APIs
id: MASTG-TEST-0x02
apis: [DCAppAttestService, DCAppAttestService.generateKey, DCAppAttestService.attestKey, DCAppAttestService.generateAssertion]
type: [static]
weakness: MASWE-0056
best-practices: [MASTG-BEST-0x03]
profiles: [R]
knowledge: [MASTG-KNOW-0x03]
status: placeholder
---

## Overview

This test checks whether the app attests its own integrity to a server, by statically analyzing the app binary for references to the relevant APIs. On iOS this is provided by [App Attest](https://developer.apple.com/documentation/devicecheck), part of the DeviceCheck framework, which binds a Secure Enclave key to the app's App ID so that a server can verify that a request comes from a legitimate, unmodified instance of the app (@MASTG-KNOW-0x03).

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

- Whether the `clientDataHash` passed to `attestKey` and `generateAssertion` is derived from a one-time, server-provided challenge.
- Whether the app generates an assertion (`generateAssertion`) for sensitive requests, rather than only attesting the key once (`attestKey`).
- Whether the server verifies the attestation object and assertions, since client-side use of `DCAppAttestService` alone provides no security guarantee.

See @MASTG-BEST-0x03 for the expected implementation.
