---
title: "`setRecentsScreenshotEnabled` Not Used to Prevent Screenshots When Backgrounded"
platform: android
id: MASTG-TEST-0292
type: [static]
profiles: [L2]
best-practices: [MASTG-BEST-0014, MASTG-BEST-0015]
weakness: MASWE-0055
threat: [root]
---

## Overview

This test verifies whether an app prevents sensitive data from being captured in the Recents screen when backgrounded. Starting with Android 13 (API level 33), developers could be explicitly allowing _Recents screenshots_ using [`setRecentsScreenshotEnabled(true)`](https://developer.android.com/reference/android/app/Activity#setRecentsScreenshotEnabled(boolean)) or not calling it at all, which results in the default behavior of allowing Recents screenshots. This does not affect user-initiated screenshots or screen recordings.

**Note:** [`FLAG_SECURE`](https://developer.android.com/security/fraud-prevention/activities#flag_secure) also prevents the app's content from appearing in the Recents screen when backgrounded and it's addressed in @MASTG-TEST-0291.

## Steps

1. Run a static analysis (@MASTG-TECH-0014) tool to identify instances of the relevant APIs.

## Observation

The output should include a list of locations where the relevant APIs are used.

## Evaluation

The test case fails if `setRecentsScreenshotEnabled(true)` is called or the app does not call it at all on Android 13 (API level 33) or higher.
