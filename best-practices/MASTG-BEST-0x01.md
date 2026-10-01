---
title: Verifying the Device Security State with the AndroidX Security State Library
alias: verifying-device-security-state-android
id: MASTG-BEST-0x01
platform: android
knowledge: [MASTG-KNOW-0x01]
---

Apps that handle sensitive data or high-risk flows should verify that the device runs up-to-date security patches, instead of relying only on the OS version or the `Build.VERSION.SECURITY_PATCH` string, which covers only the overall SPL.

Use the [AndroidX Security State library](https://developer.android.com/privacy-and-security/understand-device-security-state) (@MASTG-KNOW-0x01) to obtain the patch level of each component (system, vendor, kernel, and updatable modules), compare it against the published vulnerability report, and decide whether the device is sufficiently updated, for example with `SecurityPatchState.isDeviceFullyUpdated`.

- Evaluate the result before granting access to sensitive functionality, and react proportionally, for example by warning the user, restricting functionality, or sending a risk signal to the backend.
- Prefer enforcing the final decision on the backend, since client-side checks can be bypassed on a compromised device.
- Handle failures to load the vulnerability report (for example, no network) explicitly, and decide whether to fail open or closed based on the sensitivity of the flow.
- Combine this check with the [Play Integrity API](https://developer.android.com/google/play/integrity) to assess device authenticity and tampering, which the Security State library does not cover.
