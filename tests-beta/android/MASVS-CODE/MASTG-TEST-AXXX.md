---
platform: android
title: Testing for Overlay Attack
id: MASTG-TEST-XXXX
type: [static]
weakness: MASWE-XXXX
profiles: [L2]
---

## Overview

In addition to declarative XML protections, Android allows developers to enforce overlay protection programmatically at the code level. This is particularly relevant for custom views or complex interaction patterns where the `android:filterTouchesWhenObscured` attribute might not suffice. To prevent tapjacking, views can override the `onFilterTouchEventForSecurity` method or manually check the `MotionEvent` flags. This test checks whether the app correctly implements these programmatic checks to discard touch events when the window is obscured.

## Steps

1. Reverse engineer the app (@MASTG-TECH-0017) and identify sensitive custom views or activities handling critical user actions (e.g., login buttons, permission prompts, payment confirmation).
2. Search for overrides of the `onFilterTouchEventForSecurity` method or manual checks for `MotionEvent.FLAG_WINDOW_IS_OBSCURED` or `MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED`.
3. Analyze the logic in these methods to verify if touch events are discarded (return `false`) when the obscuration flags are detected.

## Observation

The output should contain a list of sensitive views or activities where `onFilterTouchEventForSecurity` is implemented or where `FLAG_WINDOW_IS_OBSCURED` is checked, along with whether the logic correctly rejects touches when an overlay is present.

## Evaluation

The test case fails if sensitive custom views or activities handling critical user actions do not validate the security of the touch event. Specifically, if the code processes touch events without checking `FLAG_WINDOW_IS_OBSCURED` or `FLAG_WINDOW_IS_PARTIALLY_OBSCURED` and does not return `false` in `onFilterTouchEventForSecurity` when an obstruction is detected, the application is vulnerable to UI redressing.
