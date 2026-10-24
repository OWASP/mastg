---
platform: android
title: Uses of BiometricPrompt with Device Credential Fallback with semgrep
id: MASTG-DEMO-0089
code: [kotlin]
test: MASTG-TEST-0326
---

## Sample

The following sample demonstrates the use of the built-in [`android.hardware.biometrics.BiometricPrompt`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) framework API (available from API level 28+) with different authenticator configurations used in `BiometricPrompt.Builder()`.

It shows both weaker configurations that allow fallback to device credentials (PIN, pattern, password), which are more susceptible to compromise (e.g., through shoulder surfing) and secure configurations that requires a strong biometric authentication only.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-biometric-device-credential-fallback.yml }}

{{ run.sh }}

## Observation

The output shows all usages of APIs that configure biometric authentication.

{{ output.txt }}

## Evaluation

The test fails because the output shows references to biometric authentication configurations that allow fallback to device credentials:

- Line 38: `setAllowedAuthenticators(32783)` is called with `BIOMETRIC_STRONG | DEVICE_CREDENTIAL`, which allows the user to authenticate with either biometrics or their device PIN/pattern/password. The value `32783` is the sum of `32768` and `15`. Decompiled code contains integer values of the [`Authenticator` constants](https://developer.android.com/reference/android/hardware/biometrics/BiometricManager.Authenticators) instead of the name:
    - `BIOMETRIC_STRONG` = 15 (0x000F)
    - `BIOMETRIC_WEAK` = 255 (0x00FF)
    - `DEVICE_CREDENTIAL` = 32768 (0x8000)
- Also in line 38: [`setDeviceCredentialAllowed(true)`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.Builder#setDeviceCredentialAllowed(boolean)) is called and can give the user the option to authenticate with their device PIN, pattern, or password instead of a biometric.

For sensitive operations, the app should use [`BIOMETRIC_STRONG`](https://developer.android.com/identity/sign-in/biometric-auth#declare-supported-authentication-types) to enforce biometric-only authentication.
