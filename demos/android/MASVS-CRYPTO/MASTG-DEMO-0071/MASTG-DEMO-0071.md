---
platform: android
title: References to Asymmetric Key Pairs Used For Multiple Purposes with Semgrep
id: MASTG-DEMO-0071
code: [java]
test: MASTG-TEST-0307
---

### Sample

This sample generates an RSA key pair using `KeyGenParameterSpec` with multiple purposes: `PURPOSE_SIGN`, `PURPOSE_VERIFY`, `PURPOSE_ENCRYPT`, and `PURPOSE_DECRYPT`. It subsequently uses it for encryption, decryption, signing, and verification.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Run the @MASTG-TOOL-0110 rule, as defined below, against the sample code.

{{ ../../../../rules/mastg-android-asymmetric-key-pair-used-for-multiple-purposes.yml }}

{{ run.sh }}

### Observation

The rule flags the constructor call to `KeyGenParameterSpec.Builder` in the decompiled Java code. This includes the key alias and a number representing the combined purposes used during key generation.

{{ output.txt }}

### Evaluation

The test fails because the key is configured for multiple purposes.

In the output, we can see how the constructor receives a combined purpose value of `15`, which is the bitwise OR of `PURPOSE_ENCRYPT` (`1`), `PURPOSE_DECRYPT` (`2`), `PURPOSE_SIGN` (`4`), and `PURPOSE_VERIFY` (`8`).
