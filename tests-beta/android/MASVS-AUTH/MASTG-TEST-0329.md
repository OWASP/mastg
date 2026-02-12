---
platform: android
title: References to APIs Enforcing Authentication without Explicit User Action
id: MASTG-TEST-0329
apis: [BiometricPrompt.PromptInfo.Builder, setConfirmationRequired]
type: [static]
weakness: MASWE-0044
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

This test checks if the app enforces biometric authentication [without requiring explicit user action](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action). When using [`BiometricPrompt`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt), the [`setConfirmationRequired()`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.PromptInfo.Builder#setConfirmationRequired(boolean)) method in [`BiometricPrompt.PromptInfo.Builder`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.PromptInfo.Builder) controls whether the user must explicitly confirm their authentication, which is enforced by default.

## Steps

Use @MASTG-TECH-0014 with a tool such as @MASTG-TOOL-0110 to identify instances of `BiometricPrompt.PromptInfo.Builder` with the method `setConfirmationRequired(false)`.

## Observation

The output should contain a list of locations where biometric authentication is configured without explicit user confirmation.

## Evaluation

The test fails if the app uses `BiometricPrompt.PromptInfo.Builder` with `setConfirmationRequired(false)` for sensitive operations that require explicit user authorization.

The test passes if the app either:

- Uses `setConfirmationRequired(true)` explicitly for sensitive operations, or
- Relies on the default behavior, which requires confirmation.

**Note:** Using [`setConfirmationRequired(false)`](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action) is not inherently a vulnerability. It may be appropriate for low-risk operations, but for sensitive operations like payments or data access, the app should use `setConfirmationRequired(true)` or rely on the default behavior to [ensure the user explicitly confirms the authentication](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action). 
