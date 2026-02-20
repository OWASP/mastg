---
platform: android
title: Uses of setInvalidatedByBiometricEnrollment with semgrep
id: MASTG-DEMO-0091
code: [kotlin]
test: MASTG-TEST-0328
---

## Sample

This sample demonstrates the insecure use of [`setInvalidatedByBiometricEnrollment(false)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment(boolean)) when generating cryptographic keys for biometric authentication.

{{ ../MASTG-DEMO-0090/MastgTest.kt # ../MASTG-DEMO-0090/MastgTest_reversed.java }}

## Steps

Run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-biometric-invalidated-enrollment.yml }}

{{ run.sh }}

## Observation

The output shows the usage of `setInvalidatedByBiometricEnrollment()`.

{{ output.txt }}

## Evaluation

The test fails because the output shows:

- Line 52: `setInvalidatedByBiometricEnrollment(false)` is called when creating a key for biometric authentication.

For sensitive operations, keys should use `setInvalidatedByBiometricEnrollment(true)` (or rely on the default behavior) to ensure that keys are permanently invalidated when biometric enrollment changes, preventing newly enrolled biometrics from accessing existing encrypted data.
