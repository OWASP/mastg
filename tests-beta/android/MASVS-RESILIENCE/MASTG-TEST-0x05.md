---
platform: android
title: References to Device Attestation APIs
id: MASTG-TEST-0x05
apis: [IntegrityManagerFactory, KeyStore.getCertificateChain, KeyGenParameterSpec.Builder.setAttestationChallenge]
type: [static]
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

This test is the device-integrity counterpart to @MASTG-TEST-0x01, which covers whether the app attests _itself_. Both mechanisms emit app and device signals from the same API calls, so the same references often satisfy both tests. What differs is which part of the response the server is expected to evaluate, which is covered in @MASTG-BEST-0x01.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for uses of [`IntegrityManagerFactory`](https://developer.android.com/google/play/integrity/reference/com/google/android/play/core/integrity/IntegrityManagerFactory) (Google Play Integrity API) and [`KeyStore.getCertificateChain(alias)`](https://developer.android.com/reference/kotlin/java/security/KeyStore#getcertificatechain) together with [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) (Key Attestation).

## Observation

The output should contain a list of locations where those APIs are used.

## Evaluation

The test case fails if none of the above APIs are found in the app, since the backend then has no cryptographic evidence about the device and must rely on client-side claims alone.

The test case also fails if the app retrieves a certificate chain via `getCertificateChain` but no `setAttestationChallenge` is found, because without an embedded challenge the chain carries no freshness guarantee and a previously captured chain can be replayed. As described in @MASTG-TEST-0x01, correlate the two APIs manually: key generation and certificate chain retrieval are frequently implemented in different methods or classes.

!!! note
    The presence of these APIs does not mean the device state is trusted. The `rootOfTrust` values are only meaningful once the server has verified the certificate chain, checked it against Google's revocation status list, and confirmed that `attestationSecurityLevel` is `TrustedEnvironment` or `StrongBox`. A `Software` security level places `rootOfTrust` in the `softwareEnforced` list, where the OS asserts the very state it is meant to vouch for. See @MASTG-KNOW-0120 and @MASTG-BEST-0x01.

!!! warning
    Device attestation reflects the state of the device **at the time the key was generated**, not at the time of use. A key generated on a clean device retains its attestation even if the device is later rooted or its bootloader unlocked. Static presence of these APIs therefore says nothing about how often the app re-attests. See @MASTG-KNOW-0120.
