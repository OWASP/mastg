---
title: Enforcing Mandatory In-App Updates
platform: android
id: MASTG-TEST-xxxx
type: [static]
weakness: MASWE-0075
profiles: [L2]
---

## Overview

The goal of this test is to verify whether the application enforces mandatory updates, preventing users from accessing the app until the latest version has been successfully downloaded and installed. A mandatory update can typically be achieved by using the [Google Play Core In-App Update API](https://developer.android.com/guide/playcore/in-app-updates/kotlin-java) and invoking `startUpdateFlowForResult` with an Immediate update type option `AppUpdateType.IMMEDIATE` or value `1`.

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0110 on codebase for usages of the calls to the Play Core in-app update API, specifically `startUpdateFlowForResult`, that are configured with the integer value `1` (`AppUpdateType.IMMEDIATE`).

## Observation

The output should contain the locations where `startUpdateFlowForResult` with `AppUpdateOptions.newBuilder(1).build()` is called.

## Evaluation

The test fails if the app does not implement enforced updating uisng Play In-App Updates API.
