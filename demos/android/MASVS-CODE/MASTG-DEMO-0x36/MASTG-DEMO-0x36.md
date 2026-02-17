---
platform: android
title: Enforced Immediate Updates with Play Core API detected using semgrep
id: MASTG-DEMO-0x36
code: [kotlin]
test: MASTG-TEST-0x36
---

## Sample

The following code implements immediate in-app updates using the Google Play Core API. It calls `startUpdateFlowForResult` with `AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)` or value `1` and includes comprehensive update state handling and bypass prevention logic in `enforceUpdateOnResume`.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-enforced-updating.yml }}

{{ run.sh }}

## Observation

The output file shows usages of the Google Play Core API enforcing immediate update.

{{ output.txt }}

## Evaluation

The test passes because the app correctly implements enforced immediate updates. Specifically:

- On line 274, `startUpdateFlowForResult` is called with `AppUpdateOptions.newBuilder(1).build()` (`AppUpdateType.IMMEDIATE`), ensuring users are required to install the update before continuing.
- The code implements `enforceUpdateOnResume()` which checks for both `UPDATE_AVAILABLE` and `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` states, preventing users from bypassing the update by dismissing the dialog or backgrounding the app.
