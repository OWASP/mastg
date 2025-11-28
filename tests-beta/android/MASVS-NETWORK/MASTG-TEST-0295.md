---
title: GMS Security Provider Not Updated
platform: android
id: MASTG-TEST-0295
type: [static]
weakness: MASWE-0052
profiles: [L2]
best-practices: [MASTG-BEST-0020, MASTG-KNOW-0021]
---

## Overview

This test checks whether the Android app ensures the Security Provider (@MASTG-KNOW-0011) is [updated to mitigate SSL/TLS vulnerabilities](https://developer.android.com/privacy-and-security/security-gms-provider). The provider should be updated using Google Play Services APIs, and the implementation should handle exceptions properly (see @MASTG-KNOW-0010).

## Steps

1. Reverse engineer the app (@MASTG-TECH-0017).
2. Use static analysis (@MASTG-TECH-0014) to search for usage of `ProviderInstaller.installIfNeeded` or `ProviderInstaller.installIfNeededAsync`.

## Observation

The output should list all locations where the Security Provider update is performed and how exceptions are handled (for `installIfNeeded`), or how the `ProviderInstallListener` handles errors (for `installIfNeededAsync`).

## Evaluation

The test fails if the app does not update the provider, or it does not handle exceptions properly. Check that these calls occur before any network connections are made.
