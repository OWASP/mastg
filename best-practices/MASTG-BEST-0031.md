---
title: Enforce Strong Biometrics for Sensitive Operations
alias: enforce-strong-biometrics-for-sensitive-operations
id: MASTG-BEST-0031
platform: android
knowledge: [MASTG-KNOW-0001]
---

For sensitive operations protected by Android biometrics, configure `BiometricPrompt` to require [`BIOMETRIC_STRONG`](https://developer.android.com/reference/androidx/biometric/BiometricManager.Authenticators) rather than allowing weaker biometric classes. Android defines `BIOMETRIC_STRONG` as authentication using a Class 3 biometric, while `BIOMETRIC_WEAK` corresponds to Class 2 biometric authentication.

When the operation is intended to be biometric only, don't include `DEVICE_CREDENTIAL` in the allowed authenticators. `DEVICE_CREDENTIAL` enables fallback to the device screen lock credential (PIN, pattern, or password) instead of requiring a biometric factor. While not inherently a vulnerability, for high-security applications (e.g., finance, government, health) its use reduces the intended security posture and makes authentication more susceptible to shoulder surfing and social engineering.
