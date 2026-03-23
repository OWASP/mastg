---
platform: android
title: References to APIs Detecting Biometric Enrollment Changes
id: MASTG-TEST-0328
apis: [KeyGenParameterSpec.Builder, setInvalidatedByBiometricEnrollment]
type: [static]
weakness: MASWE-0046
profiles: [L2]
knowledge: [MASTG-KNOW-0001]
best-practices: []
---

## Overview

This test checks whether the app fails to protect sensitive operations against unauthorized access following biometric enrollment changes (@MASTG-KNOW-0001). An attacker who obtains the device passcode could add a new fingerprint or facial representation via system settings and use it to authenticate in the app.

This behaviour occurs when [`setInvalidatedByBiometricEnrollment`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment(boolean)) is set to `false` when keys are generated.

By default and when set to `true`, a key becomes permanently invalidated if a new biometric is enrolled. As a result, only users whose biometric data was enrolled when the item was created can unlock it. This prevents unauthorized access through biometrics enrolled later.

## Steps

1. Run a static analysis (@MASTG-TECH-0014) tool to identify instances of the relevant APIs.

## Observation

The output should include a list of locations where the relevant APIs are used.

## Evaluation

The test fails if the app uses `setInvalidatedByBiometricEnrollment(false)` for keys used to protect sensitive data resources.

The test passes if the app either:

- uses `setInvalidatedByBiometricEnrollment(true)` explicitly, or
- relies on the default behavior, which invalidates keys on new biometric enrollment when `setUserAuthenticationRequired(true)` is set.
