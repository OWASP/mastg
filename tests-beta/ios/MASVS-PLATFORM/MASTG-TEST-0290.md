---
title: Runtime Verification of Sensitive Content Exposure in Screenshots During App Backgrounding
platform: ios
id: MASTG-TEST-0290
type: [dynamic, manual]
profiles: [L2]
weakness: MASWE-0055
prerequisites:
- identify-sensitive-screens
---

## Overview

This test verifies that the app hides sensitive content from the screen when it moves to the background. This is important because iOS captures a snapshot of the app UI when it transitions to the background. This snapshot is used for the [App Switcher](https://support.apple.com/guide/iphone/switch-between-open-apps-iph1a1f981ad/ios) and transitions, and can expose sensitive content if the app does not protect it.

## Steps

1. Exercise your app until you get to each of the screens identified as sensitive. While on each of those screens, move the app to the background (for example by pressing **Home** or opening the **App Switcher** and exiting it) and continue to the next screen.
2. Once finished, use @MASTG-TECH-0053 to copy the snapshots taken by the system to your analysis workstation. The system stores them under `/var/mobile/Containers/Data/Application/<APP_ID>/Library/SplashBoard/Snapshots/sceneID:<APP_NAME>-default/`. Note that the exact path and structure may vary across iOS versions.

## Observation

The output should include a collection of snapshots cached when the app entered the background state.

## Evaluation

The test case fails if any snapshot displays sensitive data that should have been protected.
