---
platform: android
title: Uses of Extended Validity Duration in setUserAuthenticationParameters with semgrep
id: MASTG-DEMO-0093
code: [kotlin]
test: MASTG-TEST-0330
---

## Sample

This sample demonstrates the insecure use of [`setUserAuthenticationParameters`](https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec.Builder#setUserAuthenticationParameters(int,%20int)) with an extended validity duration when generating cryptographic keys for biometric authentication.

When a key is configured with `setUserAuthenticationParameters(86400, type)` (86400 seconds = 24 hours), it remains unlocked for 24 hours after successful authentication. During this window misuse of the app can be a potential risk, for example in case physical access to the device is possible executing sensitive operations without re-authenticating through biometric verification.

{{ ../MASTG-DEMO-0090/MastgTest.kt # ../MASTG-DEMO-0090/MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-biometric-validity-duration.yml }}

{{ run.sh }}

## Observation

The output shows the usage of `setUserAuthenticationParameters` and `setUserAuthenticationValidityDurationSeconds` including the duration values.

{{ output.txt }}

## Evaluation

The test fails because the output shows:

- Line 279: `setUserAuthenticationValidityDurationSeconds(86400)` is called, configuring the key to remain unlocked for 86400 seconds (24 hours) after authentication.
- Line 281: `setUserAuthenticationParameters(86400, ...)` is also called with the same 24-hour duration.

For sensitive operations, keys should use `setUserAuthenticationParameters(0, type)` to require authentication for every cryptographic operation, ensuring that each use of the key requires biometric verification and user presence.
