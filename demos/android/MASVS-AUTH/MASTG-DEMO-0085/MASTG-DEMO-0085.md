---
platform: android
title: Uses of BiometricPrompt without Explicit User Confirmation with semgrep
id: MASTG-DEMO-0085
code: [kotlin]
test: MASTG-TEST-0323
---

### Sample

This sample demonstrates the use of `BiometricPrompt.PromptInfo.Builder` with the `setConfirmationRequired()` method. It shows both, insecure configurations that allow authentication without explicit user action and secure configurations that require explicit confirmation.

When `setConfirmationRequired(false)` is used, passive biometrics (like face recognition) can authenticate the user as soon as the device detects their biometric data, without requiring them to tap a confirmation button.

{{ ../MASTG-DEMO-0082/MastgTest.kt # ../MASTG-DEMO-0082/MastgTest_reversed.java }}

### Steps

Run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-biometric-no-confirmation-required.yml }}

{{ run.sh }}

### Observation

The output shows the configuration of biometric authentication without requiring explicit user confirmation.

{{ output.txt }}

### Evaluation

The test fails because the output shows two references to biometric authentications that disables explicitly user confirmation:

- Line 90 and 181: `setConfirmationRequired(false)` is called, which allows the authentication to succeed implicitly without the user actively confirming the action.
