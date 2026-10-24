---
title: Invalidate Biometric Keys on Enrollment Changes
alias: invalidate-biometric-keys-on-enrollment-changes
id: MASTG-BEST-0037
platform: android
knowledge: [MASTG-KNOW-0001]
---

When generating cryptographic keys for biometric authentication, ensure keys are invalidated when new biometrics are enrolled. Either configure [`setInvalidatedByBiometricEnrollment(true)`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment(boolean)) explicitly, or rely on the default behavior, which invalidates keys when `setUserAuthenticationRequired(true)` is set.

When `setInvalidatedByBiometricEnrollment(false)` is used, a key remains valid even after new biometrics are enrolled. An attacker who obtains the device passcode could enroll a new biometric and use it to access existing encrypted data or trigger sensitive operations.

By invalidating keys on enrollment changes, only biometrics that were enrolled when the key was created can unlock it, preventing newly enrolled biometrics from accessing previously protected data.

!!! note
    Key invalidation is immediate and permanent when a new biometric is enrolled. The app must handle [`KeyPermanentlyInvalidatedException`](https://developer.android.com/reference/android/security/keystore/KeyPermanentlyInvalidatedException) and guide the user to re-authenticate to create a new key.
