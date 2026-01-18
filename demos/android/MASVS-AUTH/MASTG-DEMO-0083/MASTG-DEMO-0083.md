---
platform: android
title: Uses of BiometricPrompt with Event-Bound Authentication with semgrep
id: MASTG-DEMO-0083
code: [kotlin]
test: MASTG-TEST-0321
---

### Sample

The following sample insecurely accesses sensitive resources, a secret token, relying solely on the `BiometricPrompt` API without a `CryptoObject` for event-bound biometric authentication, instead of using the crypto-bound authentication (with a `CryptoObject`) and requiring user presence.

The key being generated and used with `CryptoObject` has not set [`.setUserAuthenticationRequired(true)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationRequired(boolean)) which means the key is authorized to be used regardless of whether the user has been authenticated or not, which is the default behavior when generating keys.

{{ ../MASTG-DEMO-0082/MastgTest.kt # ../MASTG-DEMO-0082/MastgTest_reversed.java }}

### Steps

Run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-biometric-event-bound.yml }}

{{ run.sh }}

### Observation

The output shows the usage of `BiometricPrompt.authenticate()` without using `CryptoObject` and setting `.setUserAuthenticationRequired(false)`.

{{ output.txt }}

### Evaluation

The test fails because the output shows both:

- Line 76: `BiometricPrompt.authenticate(PromptInfo)` is used without a `CryptoObject` and
- Line 192:  `setUserAuthenticationRequired(false)` is set for key generation.

For sensitive operations, the app should use `CryptoObject` when doing biometric authentication and the key generated should have set `setUserAuthenticationRequired(true)`.
