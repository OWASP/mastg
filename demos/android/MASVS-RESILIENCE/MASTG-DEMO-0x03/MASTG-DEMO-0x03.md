---
platform: android
title: Key Attestation With a Server-Issued Challenge
id: MASTG-DEMO-0x03
code: [kotlin]
test: MASTG-TEST-0x01
kind: pass
---

## Sample

This sample generates an EC key pair in the Android KeyStore and retrieves the resulting certificate chain to send to a server for verification. Unlike @MASTG-DEMO-0x01, it passes a server-issued challenge to [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) during key generation, so the nonce is embedded in the leaf certificate and the server can verify the freshness of the attestation.

The challenge is hardcoded here only to keep the demo self-contained. In a real implementation the server generates it with a CSPRNG for each attestation request and never reuses it, as described in @MASTG-BEST-0x01.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 against the reversed Java code.

{{ ../../../../rules/mastg-android-key-attestation-missing-challenge.yml }}

{{ run.sh }}

## Observation

The rule reports no findings. Because it flags the _absence_ of `setAttestationChallenge` in the builder chain, a compliant sample produces an empty finding list, so the scan summary is captured to make the result visible.

{{ output.txt }}

## Evaluation

The test passes because `setAttestationChallenge` is present in the `KeyGenParameterSpec.Builder` chain that produces the attested key. The `attestationChallenge` field of the leaf certificate will contain the server-issued nonce, allowing the server to reject replayed certificate chains.

!!! note
    A passing static result only confirms that a challenge is embedded. The server must still verify the certificate chain, confirm that the embedded challenge matches the one it issued, and evaluate the `rootOfTrust` and `attestationApplicationId` fields before trusting the client. See @MASTG-BEST-0x01.
