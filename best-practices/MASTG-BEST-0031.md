---
title: Enforce Strong Biometrics for Sensitive Operations
alias: enforce-strong-biometrics-for-sensitive-operations
id: MASTG-BEST-0031
platform: android
knowledge: [MASTG-KNOW-0001]
---

Apps should use the [`BIOMETRIC_STRONG`](https://developer.android.com/reference/android/hardware/biometrics/BiometricManager.Authenticators) authenticator for sensitive operations protected by biometrics. Using `DEVICE_CREDENTIAL` (PINs, patterns or passwords) are more susceptible to shoulder surfing and social engineering.

For high-security operations (e.g. payments or access to health data), enforcing biometrics only provides strong protection and verifies user presence.
