---
platform: android
title: References to APIs for Detecting Secure Screen Lock
id: MASTG-TEST-0247
apis: [KeyguardManager, BiometricManager#canAuthenticate]
type: [static, code]
weakness: MASWE-0008
best-practices: []
profiles: [L2]
knowledge: [MASTG-KNOW-0001]
---

## Overview

This test verifies whether an app is running on a device with a passcode set. Android apps can determine whether a secure [screen lock (such as PIN, or password)](https://support.google.com/android/answer/9079129) is enabled by using platform-provided APIs. Specifically, apps can utilize the [KeyguardManager](https://developer.android.com/reference/android/app/KeyguardManager) API, which provides the [isDeviceSecure()](https://developer.android.com/reference/android/app/KeyguardManager#isDeviceSecure()) and [isKeyguardSecure()](https://developer.android.com/reference/android/app/KeyguardManager#isKeyguardLocked()) methods to check if the device has a secure lock mechanism in place.

Additionally, apps can use the [BiometricManager#canAuthenticate(int)](https://developer.android.com/reference/android/hardware/biometrics/BiometricManager#canAuthenticate(int)) API to check whether biometric authentication is available and can be used. Since biometric authentication on Android requires a secure screen lock as a fallback, this method can serve as an alternative check when [KeyguardManager](https://developer.android.com/reference/android/app/KeyguardManager) is unavailable or restricted by device manufacturers.

If an app relies on biometrics for authentication, it should ensure that biometric authentication is enforced using the [BiometricPrompt](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) API or by requiring authentication for cryptographic key access via the **Android KeyStore System**. However, apps **cannot force** users to enable biometrics at the system level, only enforce their use within the app for accessing sensitive functionality.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where relevant APIs are used.

## Evaluation

The test case fails if an app doesn't use any APIs to verify the presence of a secure screen lock.
