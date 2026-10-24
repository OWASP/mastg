---
platform: android
title: Uses of BiometricPrompt with Event-Bound Authentication with semgrep
id: MASTG-DEMO-0090
code: [kotlin]
test: MASTG-TEST-0327
---

## Sample

The following sample demonstrates the use of the built-in [`android.hardware.biometrics.BiometricPrompt`](https://developer.android.com/reference/android/hardware/biometrics/BiometricPrompt) framework API (available from API level 28+) across multiple scenarios that highlight both insecure and partially secure patterns.

### Test 1: No `CryptoObject`

This scenario calls `BiometricPrompt.authenticate()` **without** a `CryptoObject` and returns a secret token directly after successful authentication.

- The sensitive operation is **not cryptographically bound** to the authentication event.
- The secret token is released purely based on the callback result.
- `BIOMETRIC_STRONG` and `DEVICE_CREDENTIAL` are allowed, meaning PIN, pattern, or password can satisfy the prompt.

This reflects an insecure pattern where authentication is treated as a UI gate rather than being enforced at the cryptographic or Keystore level.

### Test 2a: Encrypt with `CryptoObject`

This scenario generates an AES key in the Android Keystore and uses `BIOMETRIC_STRONG` together with a `CryptoObject` to encrypt the secret token.

- A `Cipher` is initialized in `ENCRYPT_MODE` and wrapped in a `BiometricPrompt.CryptoObject`.
- `BiometricPrompt.authenticate()` is invoked with that `CryptoObject`.
- Only after successful biometric authentication is the returned, authenticated cipher used to encrypt the token.
- The resulting ciphertext and IV are stored for later decryption.

However, the Keystore key is explicitly configured **not** to require user authentication:

- [`setUserAuthenticationRequired(false)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationRequired(boolean))
- [`setInvalidatedByBiometricEnrollment(false)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment(boolean))

Because authentication is not required at the Keystore level, the key can be initialized and used outside of a biometric flow. The cryptographic operation is tied to the prompt instance, but the key itself is not protected by mandatory user presence.

### Test 2b: Decrypt with `CryptoObject`

This scenario performs the complementary operation, decrypting the previously encrypted token.

- A new `Cipher` is initialized in `DECRYPT_MODE` using the stored IV.
- The cipher is wrapped in a `CryptoObject` and passed to `BiometricPrompt.authenticate()`.
- After successful `BIOMETRIC_STRONG` authentication, the authenticated cipher returned in the callback is used to decrypt the token.

Like Test 2a, the decryption operation is bound to the authentication event through `CryptoObject`. However, the same Keystore key is used, and it was generated without requiring user authentication. As a result, neither encryption nor decryption is backed by Keystore enforced user authenticated key usage.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-biometric-event-bound.yml }}

{{ run.sh }}

## Observation

The output shows the usage of the `BiometricPrompt.authenticate` and `setUserAuthenticationRequired` APIs.

{{ output.txt }}

## Evaluation

The test fails because the output shows both:

- Line 139: `BiometricPrompt.authenticate(PromptInfo)` is used without a `CryptoObject` and
- Line 52: `setUserAuthenticationRequired(false)` is set for key generation.

For sensitive operations, the app should use `CryptoObject` when doing biometric authentication and the key generated should have set `setUserAuthenticationRequired(true)`.
