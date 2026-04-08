---
title: Enforcing Mandatory In-App Updates
platform: android
id: MASTG-TEST-0x36
type: [static]
weakness: MASWE-0075
profiles: [L2]
knowledge: [MASTG-KNOW-0023]
---

## Overview

If the app does not properly enforce mandatory updates, users may continue running outdated versions that contain known security vulnerabilities or deprecated functionality. This can lead to exploitation of patched vulnerabilities, data leakage, or incompatibility with backend security requirements.

On Android, mandatory updates can be implemented using the [Google Play Core In-App Update API](https://developer.android.com/guide/playcore/in-app-updates/kotlin-java) by invoking `startUpdateFlowForResult` with `AppUpdateType.IMMEDIATE` (value `1`). However, simply calling `startUpdateFlowForResult` is insufficient; users can dismiss the update dialog or background the app before the update completes. Without additional enforcement in the `onResume` lifecycle method to detect `UPDATE_AVAILABLE` or `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` states, users can bypass the update entirely.

## Steps

1. Use @MASTG-TECH-0014 to identify calls to the Play Core In-App Update API.
2. Review the code to verify that the app checks for pending updates in its `onResume` lifecycle method.

## Observation

The output should contain a list of the locations where the Play Core In-App Update API is invoked.

## Evaluation

The test case fails if:

- The app does not implement enforced updating using Play In-App Updates API.
- The app triggers an immediate update flow but lacks enforcement logic in `onResume` to handle `UPDATE_AVAILABLE` or `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` states.
