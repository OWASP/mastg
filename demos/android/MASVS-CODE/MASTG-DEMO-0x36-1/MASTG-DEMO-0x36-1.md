---
platform: android
title: Enforced Immediate Updates with FakeAppUpdateManager using semgrep
id: MASTG-DEMO-0x36-1
tools: [MASTG-TOOL-0110]
code: [kotlin]
test: MASTG-TEST-0x36
kind: pass
---

### Sample

The following code demonstrates the immediate in-app update enforcement pattern using [`FakeAppUpdateManager`](https://developer.android.com/reference/com/google/android/play/core/appupdate/testing/FakeAppUpdateManager) from the Google Play Core testing library. It uses `setUpdateAvailable(2)` to simulate version `2` being available and calls `startUpdateFlowForResult` with `AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)` or value `1`.

The code monitors update progress via `InstallStateUpdatedListener`, re-enforces updates on cancel or failure, and uses `enforceUpdateOnResume()` to prevent bypass when the app resumes. For the production implementation using `AppUpdateManagerFactory.create()`, refer @MASTG-DEMO-0x36.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-enforced-updating.yml }}

{{ run.sh }}

### Observation

The rule has identified one instance where the app calls `startUpdateFlowForResult` with `AppUpdateOptions.newBuilder(1).build()` (`AppUpdateType.IMMEDIATE`). The specified line number can be located in the reversed code for further investigation.

{{ output.txt }}

### Evaluation

The test passes because the app correctly implements enforced immediate updates. Specifically:

- On line 274, `startUpdateFlowForResult` is called with `AppUpdateOptions.newBuilder(1).build()` (`AppUpdateType.IMMEDIATE`), ensuring users are required to install the update before continuing.
- The code also implements `enforceUpdateOnResume()` which checks for both `UPDATE_AVAILABLE` and `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` states, preventing users from bypassing the update by dismissing the dialog or backgrounding the app.
