---
platform: android
title: References to APIs Allowing Fallback to Non-Biometric Authentication
id: MASTG-TEST-0326
apis: [BiometricPrompt, BiometricManager.Authenticators, setAllowedAuthenticators]
type: [static]
weakness: MASWE-0045
profiles: [L2]
knowledge: [MASTG-KNOW-0001]
best-practices: [MASTG-BEST-0031]
---

## Overview

This test checks if the app uses biometric authentication mechanisms (@MASTG-KNOW-0001) that allow fallback to device credentials (PIN, pattern, or password) for sensitive operations.

On Android, the [`android.hardware.biometrics.BiometricPrompt`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) API (or its Jetpack counterpart [`androidx.biometric.BiometricPrompt`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt) that backward compatibility to API level 23) can be configured to accept different types of [`BiometricManager.Authenticators`](https://developer.android.com/reference/android/hardware/biometrics/BiometricManager.Authenticators#constants_1) via the [`setAllowedAuthenticators`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.Builder#setAllowedAuthenticators(int)) method.

When the authenticator constant `DEVICE_CREDENTIAL` is included (either alone or combined with biometric authenticators using the `OR` operator "`|`"), the authentication allows fallback to device credentials, which is considered weaker than requiring biometrics alone because passcodes are more susceptible to compromise (e.g., through [shoulder surfing](https://en.wikipedia.org/wiki/Shoulder_surfing_%28computer_security%29)).

Similarly, using [`setDeviceCredentialAllowed(true)`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.Builder#setDeviceCredentialAllowed(boolean)) (deprecated since API 30) also enables fallback to device credentials.

## Steps

1. Run a static analysis (@MASTG-TECH-0014) tool to identify instances of the relevant APIs.

## Observation

The output should include a list of locations where the relevant APIs are used.

## Evaluation

The test fails if the app uses `BiometricPrompt` with authenticators that include `DEVICE_CREDENTIAL` for any sensitive data resource that needs protection.

The test passes if the app uses only `BiometricPrompt` with `BIOMETRIC_STRONG` to enforce biometric-only access for any sensitive data resource that needs protection.

!!! note
    Using `DEVICE_CREDENTIAL` is not inherently a vulnerability, but in high-security applications (e.g., finance, government, health), their use can represent a weakness or misconfiguration that reduces the intended security posture. This issue is therefore better categorized as a security weakness or hardening issue, not a critical vulnerability.
