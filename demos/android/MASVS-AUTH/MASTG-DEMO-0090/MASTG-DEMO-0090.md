---
platform: android
title: Uses of BiometricPrompt with Event-Bound Authentication with semgrep
id: MASTG-DEMO-0090
code: [kotlin]
test: MASTG-TEST-0327
---

### Sample

The first biometric authentication sample insecurely accesses sensitive resources, a secret token, relying solely on the `BiometricPrompt` API without a `CryptoObject` for event-bound biometric authentication. The second and third biometric authentication prompt uses instead the crypto-bound authentication (with a `CryptoObject`) to encrypt and decrypt the token, which requires user presence.

The key being generated and used with `CryptoObject` has not set [`.setUserAuthenticationRequired(true)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationRequired(boolean)) which means the key is authorized to be used regardless of whether the user has been authenticated or not, which is the default behavior when generating keys.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-biometric-event-bound.yml }}

{{ run.sh }}

### Observation

The output shows the usage of `BiometricPrompt.authenticate()` without using `CryptoObject` and setting `.setUserAuthenticationRequired(false)`.

{{ output.txt }}

### Evaluation

The test fails because the output shows both:

- Line 139: `BiometricPrompt.authenticate(PromptInfo)` is used without a `CryptoObject` and
- Line 52:  `setUserAuthenticationRequired(false)` is set for key generation.

For sensitive operations, the app should use `CryptoObject` when doing biometric authentication and the key generated should have set `setUserAuthenticationRequired(true)`.
