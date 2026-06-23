---
platform: android
title: References to Enforced Updating APIs
id: MASTG-TEST-0392
type: [static, code, manual]
weakness: MASWE-0075
profiles: [L2]
knowledge: [MASTG-KNOW-0023]
---

## Overview

Android apps may fail to enforce updates when critical security patches or minimum version requirements are needed. For Google Play-distributed apps, enforced updating can be implemented using the [Google Play In-App Updates API](https://developer.android.com/guide/playcore/in-app-updates) (for example, `AppUpdateManagerFactory.create`, `AppUpdateManager#getAppUpdateInfo`, `UpdateAvailability.UPDATE_AVAILABLE`, `UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS`, `AppUpdateType.IMMEDIATE`, `AppUpdateOptions`, `startUpdateFlowForResult`) or through a custom backend-gated flow that retrieves the app version (for example, `BuildConfig.VERSION_NAME`/`BuildConfig.VERSION_CODE`, or `PackageInfo` via `PackageManager`) and blocks usage when the backend returns a minimum version the current app does not meet. If these mechanisms are absent, implemented incorrectly (for example, using only a dismissible dialog for a mandatory update), or not triggered before access to protected functionality or backend services, an outdated app may continue to be used.

This test checks whether the app contains code that implements update enforcement, either through Play In-App Updates or through a custom version-based gating mechanism (@MASTG-KNOW-0023).

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of code locations where the app retrieves its version (for example, `BuildConfig.VERSION_NAME`, `BuildConfig.VERSION_CODE`, or `PackageManager.getPackageInfo`) or interacts with update enforcement APIs (for example, `AppUpdateManagerFactory.create`, `AppUpdateManager#getAppUpdateInfo`, `startUpdateFlowForResult`, activity result handling, `onResume` or other app entry point checks, `DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS`, or comparisons against a backend-supplied `minVersion`).

## Evaluation

The test case fails if no code locations show an update enforcement mechanism.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the update enforcement is correct:

- Determine whether the update check executes before access to protected functionality or backend services and cannot be bypassed (for example, by checking the call graph or entry point context).
- Determine whether an immediate update flow (for example, `AppUpdateType.IMMEDIATE` via `startUpdateFlowForResult`) or a non-dismissible blocking screen (for example, a full-screen `Activity` or dialog that disables navigation) is used when an update is required, rather than a dismissible prompt.
- For Google Play In-App Updates, determine whether the app handles cancellation or denial of the update flow, checks update state when returning to the foreground, and restarts the immediate update flow when `UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS` is reported.
- For backend-gated flows, determine whether the app compares the installed version against a backend-supplied minimum version and blocks access when the current version does not meet the policy.
