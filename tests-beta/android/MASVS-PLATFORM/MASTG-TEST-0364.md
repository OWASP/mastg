---
platform: android
title: Exported And Unprotected Activities That Expose Sensitive Functionality
id: MASTG-TEST-0364
type: [static, config, code, manual]
weakness: MASWE-0x01
best-practices: [MASTG-BEST-0052]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0132, MASTG-KNOW-0017, MASTG-KNOW-0020]
---

## Overview

If an exported activity does not define [`android:permission`](https://developer.android.com/guide/topics/manifest/activity-element#prmsn) with a proper protection level and performs or grants access to sensitive functionality, another third-party app outside the intended trust boundary can start it with an `Intent` and reach that functionality without going through the app's intended flow. See @MASTG-KNOW-0132 for details on activities, @MASTG-KNOW-0017 for permissions and protection levels, and @MASTG-KNOW-0020 for the IPC model of Android.

This test checks whether the app exposes sensitive functionality through exported and unprotected activities.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0160 to list the exported activities and their associated `android:permission`.
4. Use @MASTG-TECH-0014 to inspect the code of each exported activity.

## Observation

The output should contain a list of exported activities and the relevant parts of their implementation.

## Evaluation

The test case fails if any exported activity is not protected by an appropriate `android:permission` that restricts which apps can start it and exposes or performs sensitive functionality, for example by displaying sensitive data, performing a security-relevant action, or allowing a caller to bypass authentication.

**Further Validation Required:**

Inspect each exported activity using @MASTG-TECH-0023 to determine whether it exposes sensitive functionality:

- Determine whether the activity displays or returns sensitive data (for example, account details, messages, or stored secrets).
- Determine whether the activity performs a security-relevant action (for example, changing settings or credentials).
- Determine whether starting the activity directly bypasses an authentication step, such as a login or PIN screen, that the app relies on elsewhere.

Then determine whether external access to the activity is appropriately restricted for the functionality it exposes and the app's intended trust boundary:

- Determine whether the activity has a legitimate reason to be started by third-party apps. If it doesn't, it shouldn't be exported.
- If external access is required, determine whether the activity is protected by an appropriate `android:permission` or an equivalent access control. Appropriate means the control matches the sensitivity of the activity and the set of apps that should be allowed to start it.
- Verify that the permission is effective for that trust boundary, for example by using a `signature` protection level or another control that is not broadly grantable to untrusted apps.
