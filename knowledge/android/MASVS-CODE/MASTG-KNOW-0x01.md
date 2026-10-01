---
masvs_category: MASVS-CODE
platform: android
title: AndroidX Security State
---

The [AndroidX Security State library](https://developer.android.com/privacy-and-security/understand-device-security-state) (`androidx.security:security-state`) is a Jetpack library that gives apps unified access to the security state of an Android device. It combines AOSP APIs with Android public vulnerability feeds to report a precise and actionable device security status, beyond the Security Patch Level (SPL) reported by `Build.VERSION.SECURITY_PATCH`.

## Components

The library reports the patch level of individual components, such as the system, the vendor partition, the kernel, and the updatable system modules (for example, those delivered via Google Play system updates). It relies on:

- `SecurityStateManager`: provides the device's global security state, including the SPL of each component (`getGlobalSecurityState`).
- `SecurityPatchState`: compares the installed component patch levels against a published vulnerability report (`loadVulnerabilityReport`) and exposes the result with methods such as `getComponentSecurityPatchLevel`, `getPublishedSecurityPatchLevel`, `getPatchedCves`, and `isDeviceFullyUpdated`.

The vulnerability report is based on the public [Android Security Bulletins](https://source.android.com/docs/security/bulletin) feed and can be fetched from a remote URL or loaded from a local JSON string.

## Scope

The library evaluates software patch compliance and update availability. It does not provide hardware-backed device authenticity, tampering detection, or app licensing verification. These are covered by the [Play Integrity API](https://developer.android.com/google/play/integrity), which can be used together with this library.
