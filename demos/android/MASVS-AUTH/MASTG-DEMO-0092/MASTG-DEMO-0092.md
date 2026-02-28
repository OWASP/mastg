---
platform: android
title: Uses of BiometricPrompt without Explicit User Confirmation with semgrep
id: MASTG-DEMO-0092
code: [kotlin]
test: MASTG-TEST-0329
---

## Sample

This sample demonstrates the use of [`setConfirmationRequired()`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.Builder#setConfirmationRequired(boolean)). It shows both, insecure configurations that allow authentication without explicit user action and secure configurations that require explicit confirmation.

When `setConfirmationRequired(false)` is used, passive biometrics (like face recognition) can authenticate the user as soon as the device detects their biometric data, without requiring them to tap a confirmation button.

{{ ../MASTG-DEMO-0090/MastgTest.kt # ../MASTG-DEMO-0090/MastgTest_reversed.java }}

## Steps

Run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-biometric-no-confirmation-required.yml }}

{{ run.sh }}

## Observation

The output shows the uses of `setConfirmationRequired()`.

{{ output.txt }}

## Evaluation

The test fails because the output shows one reference to a biometric authentication that disables explicitly user confirmation:

- Line 82: `setConfirmationRequired(false)` is called, which allows the authentication to succeed implicitly without the user actively confirming the action.
