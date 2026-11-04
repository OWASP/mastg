---
platform: android
title: References to App Attestation APIs
id: MASTG-TEST-0x01
apis: [IntegrityManagerFactory, KeyStore.getCertificateChain, KeyGenParameterSpec.Builder.setAttestationChallenge]
type: [static]
weakness: MASWE-0056
best-practices: [MASTG-BEST-0x01]
profiles: [R]
knowledge: [MASTG-KNOW-0035, MASTG-KNOW-0044]
---

## Overview

This test checks whether the app attests its own integrity to a server, by statically analyzing the app binary for references to the relevant APIs. On Android these are the Google Play Integrity API (@MASTG-KNOW-0035), whose verdict covers the authenticity of the app binary and its installation source, and hardware-backed Key Attestation (@MASTG-KNOW-0044), whose `attestationApplicationId` field carries the package name and signing certificate digests of the calling app (@MASTG-KNOW-0119).

Both mechanisms also produce device integrity signals, such as the Play Integrity device verdict and the `rootOfTrust` field of an attestation certificate (@MASTG-KNOW-0120). Those signals are outside the scope of this test, which only establishes whether the app attests itself. How a server should evaluate them is covered in @MASTG-BEST-0x01.

See @MASTG-KNOW-0044 for more information on Key Attestation and the specific APIs and fields to look for.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for uses of [`IntegrityManagerFactory`](https://developer.android.com/google/play/integrity/reference/com/google/android/play/core/integrity/IntegrityManagerFactory) (Google Play Integrity API) and [`KeyStore.getCertificateChain(alias)`](https://developer.android.com/reference/kotlin/java/security/KeyStore#getcertificatechain) and [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) (manual Key Attestation APIs).

## Observation

The output should contain a list of locations where those APIs are used.

## Evaluation

The test case fails if none of the above APIs are found in the app.
The test case also fails if the app implements `getCertificateChain` but no instances of `setAttestationChallenge` are found to ensure freshness of the attestation.

Correlate the two APIs manually rather than relying on automated pattern matching alone. Key generation and certificate chain retrieval are frequently implemented in different methods or classes, which pattern-based tools cannot connect. Note also that an app may legitimately build a `KeyGenParameterSpec` without a challenge for keys that are never attested, such as keys used only for local encryption, so the absence of `setAttestationChallenge` is only relevant for keys whose chain is sent to a server.

!!! note
    The presence of these APIs does not mean the attestation is trusted. The server must still verify the Play Integrity verdict or the certificate chain, which cannot be determined from the app binary alone. See @MASTG-KNOW-0044 and @MASTG-BEST-0x01 for more information.
