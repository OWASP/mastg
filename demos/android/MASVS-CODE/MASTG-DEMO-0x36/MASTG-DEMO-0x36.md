---
platform: android
title: Enforced Immediate Updates with Play Core API detected using semgrep
id: MASTG-DEMO-0x36
code: [kotlin]
test: MASTG-TEST-0x36
---

### Sample

The following code implements immediate in-app updates using the Google Play Core API by calling `startUpdateFlowForResult` with `AppUpdateOptions.newBuilder(1)`.

{{ MastgTest.kt # MastgTest_reversed.java }}

### Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-enforced-updating.yml }}

{{ run.sh }}

### Observation

The output file shows usages of the Google Play Core API enforcing immediate update.

{{ output.txt }}

### Evaluation

This code correctly implements mandatory immediate updates using the Play Core API. The app calls `startUpdateFlowForResult()` with priority level 1 (IMMEDIATE), which forces the user to install the update before continuing. There is no fallback or way to bypass the update, ensuring users cannot access the app with an outdated version.
