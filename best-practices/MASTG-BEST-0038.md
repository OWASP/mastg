---
title: Require Explicit User Confirmation for Biometric Authentication
alias: require-explicit-user-confirmation-for-biometric-authentication
id: MASTG-BEST-0038
platform: android
knowledge: [MASTG-KNOW-0001]
---

For sensitive operations requiring explicit user authorization (e.g., payments or access to health data), configure [`setConfirmationRequired(true)`](https://developer.android.com/reference/androidx/biometric/BiometricPrompt.PromptInfo.Builder#setConfirmationRequired(boolean)) in `BiometricPrompt.Builder`, or rely on the default behavior, which requires confirmation.

When `setConfirmationRequired(false)` is used, passive biometrics such as face recognition can authenticate the user implicitly as soon as the device detects their biometric data. This means authentication can complete without the user actively acknowledging the operation, which may not be appropriate for high-value actions.

The [Android documentation](https://developer.android.com/identity/sign-in/biometric-auth#no-explicit-user-action) notes that explicit confirmation provides assurance that the user intentionally initiated the sensitive operation, particularly when passive biometric modalities are used.
