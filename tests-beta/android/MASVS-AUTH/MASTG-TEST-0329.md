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

This test checks if the app enforces biometric authentication (@MASTG-KNOW-0001) [without requiring explicit user action](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action). When using [`android.hardware.biometrics.BiometricPrompt`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) API (or its Jetpack counterpart [`androidx.biometric.BiometricPrompt`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt) that backward compatibility to API level 23), the [`setConfirmationRequired()`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.Builder#setConfirmationRequired(boolean)) method in [`BiometricPrompt.Builder`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.Builder) controls whether the user must explicitly confirm their authentication, which is enforced by default.

## Steps

1. Run a static analysis (@MASTG-TECH-0014) tool to identify instances of the relevant APIs.

## Observation

The output should include a list of locations where the relevant APIs are used.

## Evaluation

The test fails if the app sets `setConfirmationRequired()` to `false` for sensitive operations that require explicit user authorization.

The test passes if the app either:

- Sets `setConfirmationRequired()` to `true` explicitly for sensitive operations, or
- Relies on the default behavior, which requires confirmation.

!!! note
    Using [`setConfirmationRequired(false)`](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action) is not inherently a vulnerability. It may be appropriate for low-risk operations, but for sensitive operations like payments or data access, the app should use `setConfirmationRequired(true)` or rely on the default behavior to [ensure the user explicitly confirms the authentication](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action).
