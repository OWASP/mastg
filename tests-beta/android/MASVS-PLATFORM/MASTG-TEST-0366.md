---
platform: android
title: Exported And Unprotected Broadcast Receivers That Expose Sensitive Functionality
id: MASTG-TEST-0366
type: [static, config, code, manual]
weakness: MASWE-0063
best-practices: [MASTG-BEST-0052]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0134, MASTG-KNOW-0017, MASTG-KNOW-0020]
---

## Overview

If an exported receiver does not define [`android:permission`](https://developer.android.com/guide/topics/manifest/receiver-element#prmsn) with a proper protection level and performs or grants access to sensitive functionality, another third-party app outside the intended trust boundary can send a broadcast to it and invoke that functionality. See @MASTG-KNOW-0134 for details on broadcast receivers, @MASTG-KNOW-0017 for permissions and protection levels, and @MASTG-KNOW-0020 for the IPC model of Android.

This test checks whether the app exposes sensitive functionality through exported and unprotected broadcast receivers.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0162 to list the exported broadcast receivers and their associated `android:permission`, including context-registered receivers found in the code.
4. Use @MASTG-TECH-0014 to inspect the `onReceive` implementation of each exported receiver.

## Observation

The output should contain a list of exported broadcast receivers and the relevant parts of their `onReceive` implementation, including how they use data from the received intent and any permission they require.

## Evaluation

The test case fails if any exported broadcast receiver is not protected by an appropriate `android:permission` that restricts which apps can send broadcasts to it and exposes or performs sensitive functionality, for example by performing a security-relevant action or disclosing sensitive data when it receives a broadcast.

**Further Validation Required:**

Inspect each exported broadcast receiver using @MASTG-TECH-0023 to determine whether it exposes sensitive functionality:

- Determine whether `onReceive` performs a security-relevant action or discloses sensitive data based on the received intent (for example, reading extras and using them to send a message or change state).
- Determine whether the receiver validates the data it reads from the intent before acting on it.

Then determine whether external access to the receiver is appropriately restricted for the functionality it exposes and the app's intended trust boundary:

- Determine whether the receiver has a legitimate reason to accept broadcasts from third-party apps. If it doesn't, it shouldn't be exported.
- If external access is required, determine whether the receiver is protected by an appropriate `android:permission` or an equivalent access control. Appropriate means the control matches the sensitivity of the receiver action and the set of apps that should be allowed to send broadcasts to it.
- Verify that the permission is effective for that trust boundary, for example by using a `signature` protection level or another control that is not broadly grantable to untrusted apps.
- Determine whether context-registered receivers are registered with `RECEIVER_NOT_EXPORTED` when they don't need to receive broadcasts from other apps.
