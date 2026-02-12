---
platform: android
title: References to APIs for Keys used in Biometric Authentication with Extended Validity Duration
id: MASTG-TEST-0330
apis: [KeyGenParameterSpec.Builder, setUserAuthenticationParameters, setUserAuthenticationValidityDurationSeconds]
type: [static]
weakness: MASWE-0044
profiles: [L2]
knowledge: [MASTG-KNOW-0001, MASTG-KNOW-0043, MASTG-KNOW-0047, MASTG-KNOW-0012]
best-practices: []
---

## Overview

!!! note
    Android offers the `BiometricPrompt` class in 2 different ways:
    1. Via the [androidx.biometric](https://developer.android.com/reference/androidx/biometric/BiometricPrompt) library (Jetpack), which provides backward compatibility to API level 23.
    2. The built-in [android.hardware.biometrics](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) framework API (available from API level 28+)
    The examples in this test uses the `android.hardware.biometrics` framework API.

This test checks if the app configures cryptographic keys with an extended validity duration that allows keys to remain unlocked beyond the immediate operation. When using biometric authentication with [`CryptoObject`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.CryptoObject), the authentication validity duration determines how long a key remains usable after successful authentication.

On Android, developers can configure this behavior using [`setUserAuthenticationParameters(int timeout, int type)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationParameters(int,%20int)) or the deprecated [`setUserAuthenticationValidityDurationSeconds(int)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationValidityDurationSeconds(int)) when generating keys with [`KeyGenParameterSpec.Builder`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder):

- **Duration = 0**: The key requires authentication for every cryptographic operation. This is the most secure configuration as each use of the key requires biometric verification.

- **Duration > 0**: The key remains unlocked for the specified duration (in seconds) after successful authentication. When the duration is set to a high value in the range of minutes or hours an attacker with physical access to the phone could trigger sensitive operations without biometric verification.

## Steps

1. Use @MASTG-TECH-0014 with a tool such as @MASTG-TOOL-0110 to identify instances of `KeyGenParameterSpec.Builder` with `setUserAuthenticationParameters` or `setUserAuthenticationValidityDurationSeconds`.
2. Identify the timeout/duration values configured for keys used with biometric authentication.

## Observation

The output should contain a list of locations where cryptographic keys are generated with authentication validity duration settings, showing the configured timeout values.

## Evaluation

The test fails if the app configures keys used for sensitive operations with:

- `setUserAuthenticationParameters(duration, type)` where duration > 0
- `setUserAuthenticationValidityDurationSeconds(duration)` where duration > 0

The test passes if the app uses `setUserAuthenticationParameters(0, type)` to require authentication for every cryptographic operation when protecting sensitive data resources or sensitive functionality.

> Note: A non-zero authentication validity duration is not inherently a vulnerability. Short durations in the range of seconds may be acceptable for certain use cases where multiple related operations need to be performed in quick succession. However, for high-security applications and sensitive operations, requiring authentication per use (duration = 0) provides the strongest protection against unauthorized key usage and runtime attacks.
