---
title: Enforcing Mandatory In-App Updates
platform: android
id: MASTG-TEST-0x36
type: [static]
weakness: MASWE-0075
profiles: [L2]
---

## Overview

The goal of this test is to verify whether the application enforces mandatory updates and prevents users from accessing the app until the latest version has been successfully downloaded and installed.

A mandatory update can typically be achieved by using the [Google Play Core In-App Update API](https://developer.android.com/guide/playcore/in-app-updates/kotlin-java) and invoking `startUpdateFlowForResult` with an Immediate update type option `AppUpdateType.IMMEDIATE` or value `1`.

However, calling `startUpdateFlowForResult` with `AppUpdateType.IMMEDIATE` is not sufficient to enforce mandatory updates. Users can dismiss the update dialog or background the app before the download starts. To properly enforce updates, the app must also check for pending updates in `onResume` and re-trigger the update flow when `UPDATE_AVAILABLE` or `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` states are detected.

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0110 on codebase for usages of the calls to the Play Core in-app update API, specifically `startUpdateFlowForResult`, that are configured with the integer value `1` (`AppUpdateType.IMMEDIATE`).
2. Review the code to verify that the app checks for pending updates in its `onResume` lifecycle method by calling `appUpdateManager.appUpdateInfo` and handling both `UPDATE_AVAILABLE` and `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` states.

## Observation

The output should contain:

- Locations where `startUpdateFlowForResult` with `AppUpdateOptions.newBuilder(1).build()` is called.
- Evidence of update enforcement logic in `onResume` that checks `updateAvailability()` and re-triggers the update flow when an update is still pending.

## Evaluation

The test fails if:

- The app does not implement enforced updating using Play In-App Updates API.
- The app calls `startUpdateFlowForResult` with `AppUpdateType.IMMEDIATE` but does not implement proper `onResume` checks to prevent users from bypassing the update by dismissing the dialog.
