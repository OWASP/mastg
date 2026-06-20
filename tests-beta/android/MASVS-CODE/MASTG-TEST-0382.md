---
platform: android
title: Runtime Use of Enforced Updating APIs
id: MASTG-TEST-0382
type: [dynamic, network, hooks, manual]
weakness: MASWE-0075
profiles: [L2]
knowledge: [MASTG-KNOW-0023]
---

## Overview

At runtime, Android apps implementing enforced updating typically either invoke the [Google Play In-App Updates API](https://developer.android.com/guide/playcore/in-app-updates) (for example, `AppUpdateManager`) or perform a custom version check, for example by retrieving `BuildConfig.VERSION_NAME`, `BuildConfig.VERSION_CODE`, or `PackageManager.getPackageInfo` values and sending them to a backend that returns a minimum version policy. If the app does not perform this check before access to protected functionality or backend services, or if the enforcement can be bypassed, for example by dismissing an update dialog, cancelling an immediate update flow, backgrounding the app before the update completes, or manipulating the reported version, the app fails to properly enforce the update.

This test checks whether the app triggers the expected update enforcement behavior at runtime by capturing version-related network traffic where applicable and hooking update-related API calls (@MASTG-KNOW-0023).

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0010 to capture the app traffic.
3. Use @MASTG-TECH-0043 to hook the relevant API calls.
4. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain:

- a network traffic trace showing version values in requests and the corresponding backend responses, where a backend-gated update flow is used
- a method trace showing which APIs were called, for example version retrieval, `AppUpdateManager` calls, `getAppUpdateInfo()`, `startUpdateFlowForResult(...)`, activity result handling, `onResume` or other app entry point checks, `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` handling, or backend `minVersion` evaluation

## Evaluation

The test case fails if the app does not perform a runtime update check, or if the update is not enforced at runtime.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0023:

- Determine whether the update check executes before access to protected functionality or backend services and cannot be bypassed.
- For Google Play In-App Updates, determine whether the app handles cancellation or denial of an immediate update flow, checks update state when returning to the foreground, and restarts the immediate update flow when `UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` is reported.
- For mandatory updates, determine whether the app continues blocking access after the update flow is cancelled, interrupted, backgrounded, or left incomplete.
- For backend-gated flows, determine whether lowering the reported version value in network requests, for example `version`, `versionCode`, or `build` using an interception proxy, results in an update-required response that the app properly enforces.
