---
platform: android
title: References to APIs Detecting Biometric Enrollment Changes
id: MASTG-TEST-0323
apis: [KeyGenParameterSpec.Builder, setInvalidatedByBiometricEnrollment]
type: [static]
weakness: MASWE-0046
profiles: [L2]
knowledge: [MASTG-KNOW-0001]
---

## Overview

> **Note**: Android offers the `BiometricPrompt` class in 2 different ways:
> 1. Via the [androidx.biometric](https://developer.android.com/reference/androidx/biometric/BiometricPrompt) library (Jetpack), which provides backward compatibility to API level 23.
> 2. The built-in [android.hardware.biometrics](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) framework API (available from API level 28+)
> 
> The examples in this test uses the `android.hardware.biometrics` framework API.

This test checks whether the app fails to protect sensitive operations against unauthorized access following biometric enrollment changes. An attacker who obtains the device passcode could add a new fingerprint or facial representation via system settings and use it to authenticate in the app.

The test identifies if [`setInvalidatedByBiometricEnrollment(false)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment(boolean)) is set when keys are generated. 

By default and when set to `true`, a key becomes permanently invalidated if a new biometric is enrolled. As a result, only users whose biometric data was enrolled at the time the item was created can unlock it, preventing unauthorized access through later-enrolled biometrics.

## Steps

Use @MASTG-TECH-0014 with a tool such as @MASTG-TOOL-0110 to identify instances of `KeyGenParameterSpec.Builder` and check if `setInvalidatedByBiometricEnrollment(false)` is called.

## Observation

The output should contain a list of locations where cryptographic key generation is configured, indicating the value of `setInvalidatedByBiometricEnrollment`.

## Evaluation

The test fails if the app uses `setInvalidatedByBiometricEnrollment(false)` for keys used to protect sensitive data resources.

The test passes if the app either:

- uses `setInvalidatedByBiometricEnrollment(true)` explicitly, or
- relies on the default behavior, which invalidates keys on new biometric enrollment when `setUserAuthenticationRequired(true)` is set.
