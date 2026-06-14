---
platform: android
title: Runtime Verification of Unauthorized Database Access through Content Providers
id: MASTG-TEST-0356
type: [dynamic, filesystem, manual]
weakness: MASWE-0064
profiles: [L1, L2]
best-practices: [MASTG-BEST-0049]
knowledge: [MASTG-KNOW-0020, MASTG-KNOW-0117]
---

## Overview

If an app exports a content provider without requiring permissions, any app on the device can directly query its underlying database using [`ContentResolver`](https://developer.android.com/reference/android/content/ContentResolver) or using the `adb shell content` command. Even when a permission is declared, a misconfigured protection level (for example, `android:protectionLevel="normal"`) allows any requesting app to obtain it automatically, effectively bypassing the restriction. This test verifies at runtime whether the app's exported content providers are accessible without the required permissions.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.
3. Use @MASTG-TECH-0148 to query the app's exported content providers.

## Observation

The output should contain the content of the database that is available through the content provider.

## Evaluation

The test case fails if sensitive data can be accessed through content providers.

**Further Validation Required:**

Inspect the content of each row returned by the query to determine whether the data is sensitive:

- Determine whether the records contain sensitive information (e.g., personal data, credentials, tokens, or health data).
- Determine whether the accessible data represents a security risk given the app's data classification.
