---
platform: android
title: References to APIs for Event-Bound Biometric Authentication
id: MASTG-TEST-0321
apis: [BiometricPrompt, BiometricPrompt.CryptoObject, authenticate]
type: [static]
weakness: MASWE-0044
profiles: [L2]
---

## Overview

This test checks if the app implements event-bound biometric authentication to access sensitive resources (e.g., tokens, keys), where authentication success relies solely on a callback result rather than being cryptographically bound to sensitive operations and requiring user presence.

On Android, [`BiometricPrompt.authenticate()`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt#authenticate(androidx.biometric.BiometricPrompt.PromptInfo)) can be called with or without a [`CryptoObject`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.CryptoObject). When used **without CryptoObject** the app relies on the [`onAuthenticationSucceeded`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.AuthenticationCallback#onAuthenticationSucceeded(androidx.biometric.BiometricPrompt.AuthenticationResult)) callback to determine if authentication was successful. This makes it susceptible to logic manipulation by overwrite the callback without successfully passing the biometric verification.

In contrast, when **CryptoObject** is used (crypto-bound), the app passes a cryptographic object (e.g., `Cipher`, `Signature`, `Mac`) that requires user authentication. This ensures authentication is not just a one-time boolean, but part of a secure data retrieval path (out-of-process), so bypassing authentication becomes significantly harder.

## Steps

1. Use @MASTG-TECH-0014 with a tool such as @MASTG-TOOL-0110 to identify instances of `BiometricPrompt.authenticate()`.
2. Analyze whether the calls include a `CryptoObject` parameter.
3. Analyze whether `setUserAuthenticationRequired(true)` is set when generating the key.

## Observation

The output should contain a list of locations where `BiometricPrompt.authenticate()` is called, indicating whether a `CryptoObject` is passed and if `setUserAuthenticationRequired(true)` is set.

## Evaluation

The test fails for each sensitive operation worth protecting if:

- `BiometricPrompt.authenticate(PromptInfo)` is used without a `CryptoObject`.
- There are no calls to key generation with `setUserAuthenticationRequired(true)` in conjunction with biometric authentication, as by default, the key is authorized to be used regardless of whether the user has been authenticated or not.

The test passes if the app uses `BiometricPrompt.authenticate(PromptInfo, CryptoObject)` with properly configured cryptographic keys from the Android KeyStore for sensitive operations and uses for key generation `.setUserAuthenticationRequired(true)`. This ensures that the key can only be used after successful biometric authentication, binding the authentication to a cryptographic operation.
