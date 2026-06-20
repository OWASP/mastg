---
platform: android
title: Testing Device and App Binary Integrity Verification
id: MASTG-TEST-0x01
type: [static]
weakness: MASWE-0104
best-practices: [MASTG-BEST-0x01]
profiles: [R]
knowledge: [MASTG-KNOW-0035, MASTG-KNOW-0044]
status: placeholder
---

## Overview

This test checks whether the app implements device and app integrity verification by statically analyzing the app binary for relevant API usage patterns. These may include calls to the Google Play Integrity API or Key Attestation related APIs (@MASTG-KNOW-0044).

See @MASTG-KNOW-0044 for more information on Key Attestation and the specific APIs and fields to look for.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for uses of [`IntegrityManagerFactory`](https://developer.android.com/google/play/integrity/reference/com/google/android/play/core/integrity/IntegrityManagerFactory) (Google Play Integrity API) and [`KeyStore.getCertificateChain(alias)`](https://developer.android.com/reference/kotlin/java/security/KeyStore#getcertificatechain) and [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) (manual Key Attestation APIs).

## Observation

The output should contain a list of locations where those APIs are used.

## Evaluation

The test case fails if none of the above APIs are found in the app.
The test case also fails if the app implements `getCertificateChain` but no instances of `setAttestationChallenge` are found to ensure freshness of the attestation.

!!! note
    Even if the app implements app integrity verification APIs, the server must still verify the results from Google Play Integrity API or certificate chains for the control to be trusted. See [Interpreting the Certificate Chain](../../../knowledge/android/MASVS-RESILIENCE/MASTG-KNOW-0044.md#interpreting-the-certificate-chain) for more information.
