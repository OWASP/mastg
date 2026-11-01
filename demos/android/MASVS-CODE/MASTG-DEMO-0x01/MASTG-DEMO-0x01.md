---
platform: android
title: Uses of Device Security State APIs with semgrep
id: MASTG-DEMO-0x01
code: [kotlin, java]
test: MASTG-TEST-0x01
---

## Sample

The following sample uses the AndroidX Security State library (@MASTG-KNOW-0x01) to read the global security state and check whether the device is fully updated, and reads `Build.VERSION.SECURITY_PATCH`. The app needs the `androidx.security:security-state` dependency.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the sample code.

{{ ../../../../rules/mastg-android-device-security-state.yml }}

{{ run.sh }}

## Observation

The output shows the code locations where the app uses the Security State APIs and reads the security patch level.

{{ output.txt }}

## Evaluation

The test passes only if the result is evaluated before granting access to sensitive functionality. Reviewing the reported code (lines 16-20) shows that the app creates a `SecurityStateManager` and a `SecurityPatchState` and calls `isDeviceFullyUpdated`. However, the returned values are only concatenated into a string that is returned to the UI: the app never restricts functionality or blocks access based on the device security state, so the test fails.
