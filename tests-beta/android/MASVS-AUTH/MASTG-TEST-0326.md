---
platform: android
title: References to APIs Allowing Fallback to Non-Biometric Authentication
id: MASTG-TEST-0326
apis: [BiometricPrompt, BiometricManager.Authenticators, setAllowedAuthenticators]
type: [static]
weakness: MASWE-0045
profiles: [L2]
knowledge: [MASTG-KNOW-0001]
best-practices: []
---

## Overview

!!! note
    Android offers the `BiometricPrompt` class in 2 different ways:
    1. Via the [androidx.biometric](https://developer.android.com/reference/androidx/biometric/BiometricPrompt) library (Jetpack), which provides backward compatibility to API level 23.
    2. The built-in [android.hardware.biometrics](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) framework API (available from API level 28+)
    The examples in this test uses the `android.hardware.biometrics` framework API.

This test checks if the app uses biometric authentication mechanisms that allow fallback to device credentials (PIN, pattern, or password) for sensitive operations. On Android, the [`BiometricPrompt`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) API can be configured to accept different types of [`BiometricManager.Authenticators`](https://developer.android.com/reference/android/hardware/biometrics/BiometricManager.Authenticators#constants_1) via the method[`setAllowedAuthenticators`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.Builder#setAllowedAuthenticators(int)).

When the authenticator constant `DEVICE_CREDENTIAL` is included (either alone or combined with biometric authenticators using the `OR` operator "`|`"), the authentication allows fallback to device credentials, which is considered weaker than requiring biometrics alone because passcodes are more susceptible to compromise (e.g., through shoulder surfing).

Similarly, using [`setDeviceCredentialAllowed(true)`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.Builder#setDeviceCredentialAllowed(boolean)) (deprecated since API 30) also enables fallback to device credentials.

## Steps

Use @MASTG-TECH-0014 with a tool such as @MASTG-TOOL-0110 to identify instances of `BiometricPrompt.PromptInfo.Builder` with `setAllowedAuthenticators` including `DEVICE_CREDENTIAL` or `setDeviceCredentialAllowed(true)`.

## Observation

The output should contain a list of locations where biometric authentication has been configured, with the option of using device credentials as a fallback.

## Evaluation

The test fails if the app uses `BiometricPrompt` with authenticators that include `DEVICE_CREDENTIAL` for any sensitive data resource that needs protection.

The test passes if the app uses only `BiometricPrompt` with `BIOMETRIC_STRONG` to enforce biometric-only access for any sensitive data resource that needs protection.

**Note:** Using `DEVICE_CREDENTIAL` is not inherently a vulnerability, but in high-security applications (e.g., finance, government, health), their use can represent a weakness or misconfiguration that reduces the intended security posture. This issue is therefore better categorized as a security weakness or hardening issue, not a critical vulnerability.
