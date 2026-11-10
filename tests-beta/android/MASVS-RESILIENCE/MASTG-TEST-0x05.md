---
platform: android
title: References to Device Attestation APIs
id: MASTG-TEST-0x05
apis: [IntegrityManagerFactory, KeyStore.getCertificateChain, KeyGenParameterSpec.Builder.setAttestationChallenge]
type: [static, code, manual]
available_since: 24
weakness: MASWE-0054
best-practices: [MASTG-BEST-0x01]
profiles: [R]
knowledge: [MASTG-KNOW-0035, MASTG-KNOW-0044, MASTG-KNOW-0120]
---

## Overview

This test checks whether the app obtains a server-verifiable signal about the state of the _device_ it runs on, by statically analyzing the app binary for references to the relevant APIs.

Android exposes two mechanisms, both rooted in the same hardware-backed primitives:

- **Google Play Integrity API** (@MASTG-KNOW-0035), whose device integrity verdict reports whether the app is running on a genuine Android device that passes system integrity checks.
- **Hardware-backed Key Attestation** (@MASTG-KNOW-0044), whose `rootOfTrust` field carries `verifiedBootState`, `verifiedBootKey`, and `deviceLocked`, allowing a server to determine whether the boot chain was verified and the bootloader is locked (@MASTG-KNOW-0120).

The relevant APIs are [`IntegrityManagerFactory`](https://developer.android.com/google/play/integrity/reference/com/google/android/play/core/integrity/IntegrityManagerFactory) for the Google Play Integrity API, and [`KeyStore.getCertificateChain`](https://developer.android.com/reference/kotlin/java/security/KeyStore#getcertificatechain) together with [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) for Key Attestation.

This test is the device-integrity counterpart to @MASTG-TEST-0x01, which covers whether the app attests _itself_. Both mechanisms emit app and device signals from the same API calls, so the same references often satisfy both tests. What differs is which part of the response the server is expected to evaluate, which is covered in @MASTG-BEST-0x01.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where the relevant APIs are used.

## Evaluation

The test case fails if none of the relevant APIs are found in the app, since the backend then has no cryptographic evidence about the device and must rely on client-side claims alone.

The test case also fails if the app retrieves a certificate chain with `getCertificateChain` to attest a key, but the key was generated without `setAttestationChallenge` or with a challenge that is not a one-time value provided by the server. Without a challenge the certificate is self-signed and carries no `rootOfTrust`, and a challenge the server did not issue cannot ensure the freshness of the attestation.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the attested key is bound to a one-time challenge from the server:

- Determine whether the key whose certificate chain is retrieved was generated with `setAttestationChallenge`. Key generation and certificate chain retrieval are frequently implemented in different methods or classes, which pattern-based tools cannot connect.
- Determine whether the challenge passed to `setAttestationChallenge` is a one-time value provided by the server.

!!! note
    The presence of these APIs does not mean the device state is trusted. The `rootOfTrust` values are only meaningful once the server has verified the certificate chain, checked it against Google's revocation status list, and confirmed that `attestationSecurityLevel` is `TrustedEnvironment` or `StrongBox`. A `Software` security level places `rootOfTrust` in the `softwareEnforced` list, where the OS asserts the very state it is meant to vouch for. See @MASTG-KNOW-0120 and @MASTG-BEST-0x01.

!!! warning
    Device attestation reflects the state of the device **at the time the key was generated**, not at the time of use. A key generated on a clean device retains its attestation even if the device is later rooted or its bootloader unlocked. Static presence of these APIs therefore says nothing about how often the app re-attests. See @MASTG-KNOW-0120.
