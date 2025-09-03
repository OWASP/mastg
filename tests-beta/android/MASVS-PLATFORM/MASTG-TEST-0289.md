---
title: Runtime Verification of Sensitive Content Exposure in Screenshots During App Backgrounding
platform: android
id: MASTG-TEST-0289
type: [dynamic, manual]
profiles: [L2]
best-practices: [MASTG-BEST-0014]
weakness: MASWE-0055
prerequisites:
- identify-sensitive-screens
---

## Overview

This test verifies that the app hides sensitive content from the screen when it moves to the background. This is important because Android captures a task screenshot of the app UI when it moves to the background. This screenshot is used for the [Recents screen](https://developer.android.com/guide/components/activities/recents) and transitions, and can expose sensitive content if the app does not protect it.

## Steps

1. Exercise your app until you get to each of the screens identified as sensitive. While on each of those screens, move the app to the background (for example by pressing **Home** or opening the **Recents screen** and exiting it) and continue to the next screen.
2. Once finished, use @MASTG-TECH-0002 to copy the screenshot taken by the system to your laptop for further analysis. The system stores the screenshots in their containers `/data/system_ce/0/snapshots` or `/data/system`.

## Observation

The output should include a collection of screenshots cached when the app entered the background state.

## Evaluation

The test case fails if any screenshot displays sensitive data that should have been protected.
