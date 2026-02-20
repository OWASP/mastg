---
platform: android
title: References to APIs for Event-Bound Biometric Authentication
id: MASTG-TEST-0327
apis: [BiometricPrompt, BiometricPrompt.CryptoObject, authenticate]
type: [static]
weakness: MASWE-0044
profiles: [L2]
knowledge: [MASTG-KNOW-0001, MASTG-KNOW-0043, MASTG-KNOW-0047, MASTG-KNOW-0012]
best-practices: []
---

## Overview

This test checks if the app implements event-bound biometric authentication (@MASTG-KNOW-0001) to access sensitive resources (e.g., tokens, keys), where authentication success relies solely on a callback result rather than being cryptographically bound to sensitive operations and requiring user presence.

On Android, `BiometricPrompt.authenticate()` can be called [with a `CryptoObject`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt#authenticate(android.hardware.biometrics.BiometricPrompt.CryptoObject,%20android.os.CancellationSignal,%20java.util.concurrent.Executor,%20android.hardware.biometrics.BiometricPrompt.AuthenticationCallback)) or [without a `CryptoObject`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt#authenticate(android.os.CancellationSignal,%20java.util.concurrent.Executor,%20android.hardware.biometrics.BiometricPrompt.AuthenticationCallback)). When used **without a `CryptoObject`** the app relies on the [`onAuthenticationSucceeded`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt.AuthenticationCallback#onAuthenticationSucceeded(android.hardware.biometrics.BiometricPrompt.AuthenticationResult)) callback to determine if authentication was successful (event-bound). This makes it susceptible to logic manipulation by overwriting the callback without successfully passing the biometric verification.

In contrast, when a `CryptoObject` is used (crypto-bound), the app passes a cryptographic object (e.g., `Cipher`, `Signature`, `Mac`) that requires user authentication. This ensures authentication is not just a one-time boolean, but part of a secure data retrieval path (out-of-process), so bypassing authentication becomes significantly harder.

## Steps

1. Run a static analysis (@MASTG-TECH-0014) tool to identify instances of the relevant APIs.

## Observation

The output should include a list of locations where the relevant APIs are used.

## Evaluation

The test fails for each sensitive operation worth protecting if:

- `BiometricPrompt.authenticate` is used [without a `CryptoObject`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt#authenticate(android.os.CancellationSignal,%20java.util.concurrent.Executor,%20android.hardware.biometrics.BiometricPrompt.AuthenticationCallback)).
- There are no calls to key generation with `setUserAuthenticationRequired(true)` in conjunction with biometric authentication, as by default, the key is authorized to be used regardless of whether the user has been authenticated or not.

The test passes for each sensitive operation worth protecting if:

- `BiometricPrompt.authenticate` is used [with a `CryptoObject`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt#authenticate(android.hardware.biometrics.BiometricPrompt.CryptoObject,%20android.os.CancellationSignal,%20java.util.concurrent.Executor,%20android.hardware.biometrics.BiometricPrompt.AuthenticationCallback)), i.e., with properly configured cryptographic keys from the Android KeyStore for sensitive operations.
- There are calls to key generation with `setUserAuthenticationRequired(true)`, ensuring that the key can only be used after successful biometric authentication, binding the authentication to a cryptographic operation.
