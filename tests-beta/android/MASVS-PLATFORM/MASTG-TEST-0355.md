---
platform: android
title: References to Unauthorized Database Access through Content Providers
id: MASTG-TEST-0355
type: [static, config, manual]
weakness: MASWE-0064
profiles: [L1, L2]
best-practices: [MASTG-BEST-0049]
knowledge: [MASTG-KNOW-0020, MASTG-KNOW-0117]
---

## Overview

This test checks whether the app exposes content providers that can be accessed by other apps without appropriate permission enforcement. Specifically, it verifies whether exported `<provider>` elements in the `AndroidManifest.xml` enforce access control via [`android:readPermission` and `android:writePermission`](https://developer.android.com/guide/topics/manifest/provider-element#rprmsn) (or the combined `android:permission`). If a content provider is exported (`android:exported="true"`) without these permissions, any app on the device can query the underlying database to retrieve sensitive data such as user PII, account details, or internal app configurations.

The same applies when no protection level is configured and becomes automatically `android:protectionLevel="normal"`, which is granting access automatically to any requesting app.

**Example Attack Scenario:**

Suppose a health app exposes a content provider backed by a database of medical records, and the `<provider>` element in the AndroidManifest declares no `android:readPermission`.

1. An attacker reverse engineers the app and finds an exported `<provider>` element in the AndroidManifest with no permission restrictions.
2. The manifest shows the provider's authority and no declared read or write permission.
3. Because no permission guards the provider, any app on the device can call `ContentResolver.query()` against it and retrieve the underlying data without any user interaction.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0150 to identify exported content providers and check their permission configuration.

## Observation

The output should contain a list of exported `<provider>` elements from the AndroidManifest, including their declared read and write permission attributes.

## Evaluation

The test case fails if one or more content providers are exported (`android:exported="true"`) without declaring `android:readPermission`, `android:writePermission`, or `android:permission`.

**Further Validation Required:**

Inspect the permission configuration of each reported provider to determine whether the enforced permission provides adequate protection:

- Determine whether the declared permission uses `android:protectionLevel="normal"` or `android:protectionLevel="dangerous"`, which does not guarantee that only trusted apps can access the provider.
- Determine whether the data exposed through the provider is sensitive.
