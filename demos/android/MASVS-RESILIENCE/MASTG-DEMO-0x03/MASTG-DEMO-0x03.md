---
platform: android
title: Key Attestation with a Server-Issued Challenge
id: MASTG-DEMO-0x03
code: [kotlin]
test: MASTG-TEST-0x01
kind: pass
---

## Sample

This sample generates an EC key pair in the Android KeyStore and retrieves the resulting certificate chain to send to a server for verification. Unlike @MASTG-DEMO-0x01, it fetches a challenge from the server and passes it to [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) during key generation, so the challenge is embedded in the leaf certificate and the server can verify the freshness of the attestation.

The endpoint is a placeholder here to keep the demo self-contained. In a real implementation the server generates the challenge with a CSPRNG for each attestation request and never reuses it, as described in @MASTG-BEST-0x01.

{{ MastgTest.kt # MastgTest_reversed.java }}

!!! note "This sample cannot succeed against the placeholder endpoint"
    The endpoint does not exist, so running the sample stops with `Could not obtain a challenge from the server.` and no key is generated. This does not affect the analysis below, which is static.

## Steps

Let's run @MASTG-TOOL-0110 against the reversed Java code.

{{ ../../../../rules/mastg-android-key-attestation-missing-challenge.yml }}

{{ run.sh }}

## Observation

The rule reports no findings. Because it flags the _absence_ of `setAttestationChallenge` in the builder chain, a compliant sample produces an empty finding list, so the scan summary is captured to make the result visible.

{{ output.txt }}

## Evaluation

The test passes because `setAttestationChallenge` is present in the `KeyGenParameterSpec.Builder` chain that produces the attested key (line 54 of the reversed code), and the value passed to it is not a constant in the app. The reversed code shows that it is the return value of `fetchChallenge` (line 43), which opens a connection to the challenge endpoint and reads the response (lines 80 to 90). The `attestationChallenge` field of the leaf certificate will contain the server-issued challenge, allowing the server to reject replayed certificate chains.

!!! note
    A passing static result only confirms that a challenge is embedded. The server must still verify the certificate chain, confirm that the embedded challenge matches the one it issued, and evaluate the `rootOfTrust` and `attestationApplicationId` fields before trusting the client. See @MASTG-BEST-0x01.
