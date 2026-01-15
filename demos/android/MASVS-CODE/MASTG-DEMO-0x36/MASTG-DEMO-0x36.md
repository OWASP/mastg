---
platform: android
title: Enforced Immediate Updates with Play Core API detected using semgrep
id: MASTG-DEMO-0x36
code: [kotlin]
test: MASTG-TEST-0x36
---

### Sample

The following code implements immediate in-app updates using the Google Play Core API. It calls `startUpdateFlowForResult` with `AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)` or value `1` and includes comprehensive update state handling and bypass prevention logic in `enforceUpdateOnResume`.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-enforced-updating.yml }}

{{ run.sh }}

### Observation

The output file shows usages of the Google Play Core API enforcing immediate update.

{{ output.txt }}

### Evaluation

This code correctly implements mandatory immediate updates using the Play Core API. The app calls `startUpdateFlowForResult()` with `AppUpdateType.IMMEDIATE` or value `1`, which forces the user to install the update before continuing. The implementation includes:

- State tracking via `InstallStateUpdatedListener` to monitor update progress
- Re-enforcement of updates when canceled or failed via `handleInstallState`
- Comprehensive `enforceUpdateOnResume()` method that checks for both `UPDATE_AVAILABLE` and `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` states to prevent bypass scenarios
