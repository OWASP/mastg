---
platform: android
title: Key Attestation Without a Server-Issued Challenge
id: MASTG-DEMO-0109
code: [kotlin]
test: MASTG-TEST-0342
---

## Sample

This sample generates an EC key pair in the Android KeyStore (hardware-backed via the TEE) and retrieves the resulting certificate chain to send to a server for verification. However, no attestation challenge is set via [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) during key generation, so the server cannot determine when the attestation was produced.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 against the reversed Java code.

{{ ../../../../rules/mastg-android-key-attestation-missing-challenge.yml }}

{{ run.sh }}

## Observation

The rule identifies one location where a `KeyGenParameterSpec` is built via method chaining without `setAttestationChallenge`. The certificate chain created from this spec is sent to the server without any embedded nonce.

{{ output.txt }}

## Evaluation

The test fails because the output shows that `KeyGenParameterSpec.build()` is invoked without `setAttestationChallenge` anywhere in the chain. The `attestationChallenge` field in the leaf certificate will be null, so the server cannot verify the freshness of the attestation and cannot distinguish a freshly generated certificate chain from a replayed one. See @MASTG-BEST-00be for the correct server-driven challenge-response flow.
