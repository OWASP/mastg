---
title: Use Cryptographic Binding for Biometric Authentication
alias: use-cryptographic-binding-for-biometric-authentication
id: MASTG-BEST-0036
platform: android
knowledge: [MASTG-KNOW-0001]
---

For sensitive operations protected by biometric authentication, use [`BiometricPrompt.authenticate()`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt#authenticate(androidx.biometric.BiometricPrompt.PromptInfo,androidx.biometric.BiometricPrompt.CryptoObject)) with a [`CryptoObject`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.CryptoObject) backed by an [Android Keystore](https://developer.android.com/privacy-and-security/keystore) key configured with `setUserAuthenticationRequired(true)`. This cryptographically binds the authentication result to the key operation, ensuring that the sensitive operation can only proceed after successful biometric verification.

Without a `CryptoObject`, authentication is event-bound and relies solely on the `onAuthenticationSucceeded` callback. This makes it susceptible to runtime logic manipulation, for example by hooking the callback to return success without actually passing biometric verification.

## Keystore Key Configuration

When generating the key with [`KeyGenParameterSpec.Builder`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder), configure:

- [`setUserAuthenticationRequired(true)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationRequired(boolean)): requires the user to authenticate before the key can be used.
- [`setUserAuthenticationParameters(0, type)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationParameters(int,int)): a timeout of `0` requires authentication for every individual cryptographic operation. Avoid extended validity durations for sensitive operations, as the key remains usable for the entire validity window even if the device is later accessed by an unauthorized person.

!!! note
    [`setUserAuthenticationValidityDurationSeconds(int)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationValidityDurationSeconds(int)) is deprecated from API level 30 in favor of `setUserAuthenticationParameters(int, int)`.
