---
platform: android
title: Exported And Unprotected Services That Expose Sensitive Functionality
id: MASTG-TEST-0365
type: [static, config, code, manual]
weakness: MASWE-0062
best-practices: [MASTG-BEST-0052]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0133, MASTG-KNOW-0017, MASTG-KNOW-0020]
---

## Overview

If an exported service does not define [`android:permission`](https://developer.android.com/guide/topics/manifest/service-element#prmsn) with a proper protection level and performs or grants access to sensitive functionality, another third-party app outside the intended trust boundary can start or bind to it and invoke that functionality. See @MASTG-KNOW-0133 for details on services, @MASTG-KNOW-0017 for permissions and protection levels, and @MASTG-KNOW-0020 for the IPC model of Android.

This test checks whether the app exposes sensitive functionality through exported and unprotected services.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml.
3. Use @MASTG-TECH-0161 to list the exported services and their associated `android:permission`.
4. Use @MASTG-TECH-0014 to inspect the code of each exported service.

## Observation

The output should contain a list of exported services and the relevant parts of their implementation, including the interface they expose and any permission checks they perform.

## Evaluation

The test case fails if any exported service is not protected by an appropriate `android:permission` that restricts which apps can start or bind to it and exposes or performs sensitive functionality, for example by returning sensitive data, performing a security-relevant action, or allowing a caller to invoke a bound-service interface without authorization.

**Further Validation Required:**

Inspect each exported service using @MASTG-TECH-0023 to determine whether it exposes sensitive functionality:

- Determine whether the service returns sensitive data or performs a security-relevant action (for example, changing a password or PIN) in response to a request.
- Determine whether the service exposes a started-service or bound-service interface that lets callers trigger sensitive operations or access sensitive data.

Then determine whether external access to the service is appropriately restricted for the functionality it exposes and the app's intended trust boundary:

- Determine whether the service has a legitimate reason to accept start or bind requests from third-party apps. If it doesn't, it shouldn't be exported.
- If external access is required, determine whether the service is protected by an appropriate `android:permission` or an equivalent access control. Appropriate means the control matches the sensitivity of the service operation and the set of apps that should be allowed to start or bind to it.
- Verify that the permission is effective for that trust boundary, for example by using a `signature` protection level or another control that is not broadly grantable to untrusted apps.
- Determine whether the service verifies the caller's permission at runtime (for example, with `checkCallingPermission` or `enforceCallingPermission`) before processing sensitive requests.
