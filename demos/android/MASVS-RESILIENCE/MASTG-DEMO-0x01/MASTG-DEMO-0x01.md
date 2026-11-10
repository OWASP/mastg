---
platform: android
title: Key Attestation Without a Server-Issued Challenge
id: MASTG-DEMO-0x01
code: [kotlin]
test: MASTG-TEST-0x01
kind: fail
---

## Sample

This sample generates an EC key pair in the Android KeyStore (hardware-backed via the TEE) and retrieves the resulting certificate chain to send to a server for verification. However, no attestation challenge is set via [`setAttestationChallenge`](https://developer.android.com/reference/kotlin/android/security/keystore/KeyGenParameterSpec.Builder#setattestationchallenge) during key generation. Android then returns a self-signed certificate without an attestation, so the server has nothing to verify: it learns nothing about the app or the device, and cannot tell when the certificate was produced.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 against the reversed Java code.

{{ ../../../../rules/mastg-android-key-attestation-missing-challenge.yml }}

{{ run.sh }}

## Observation

The rule identifies one location where a `KeyGenParameterSpec` is built via method chaining without `setAttestationChallenge`, in a method that also retrieves the key's certificate chain. The certificate created from this spec is sent to the server without any attestation.

{{ output.txt }}

## Evaluation

The test fails because the output shows that `KeyGenParameterSpec.build()` is invoked without `setAttestationChallenge` anywhere in the chain. Without a challenge, Android returns a single self-signed certificate that carries no attestation (@MASTG-KNOW-0044), so the server receives nothing it can verify.

Running the sample on an emulator confirms this:

```txt
Sending certificate chain to https://example.com/attestation-verify ...
Chain length: 1
```

See @MASTG-BEST-0x01 for the correct server-driven challenge-response flow.

!!! note
    The rule reports a `KeyGenParameterSpec` only when the same method also calls `getCertificateChain`, since that is what shows the key is meant to be attested. Otherwise every AndroidKeyStore key would be reported, including keys generated only for local encryption. An app that generates the key and retrieves the chain in separate methods is therefore not reported by this rule and must be reviewed manually, as described in @MASTG-TEST-0x01.
