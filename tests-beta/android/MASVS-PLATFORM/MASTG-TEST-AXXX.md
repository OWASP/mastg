---
platform: android
title: Testing for Tapjacking Protection via onFilterTouchEventForSecurity
id: MASTG-TEST-XXXX
type: [static]
weakness: MASWE-0056
profiles: [L2]
knowledge: [MASTG-KNOW-0022]
---

## Overview

If the app does not implement programmatic overlay attack protection, a malicious app can draw an overlay on top of the legitimate app to trick the user into performing unintended actions. Without overriding `onFilterTouchEventForSecurity` or manually verifying `MotionEvent.FLAG_WINDOW_IS_OBSCURED` and `MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED`, touch events may still be processed when the window is obscured, enabling tapjacking (UI redressing) attacks that can lead to unauthorized actions, credential theft, or unintended permission grants.

## Steps

1. Reverse engineer the app (@MASTG-TECH-0017) and identify sensitive custom views or activities handling critical user actions (e.g., login buttons, permission prompts, payment confirmation).
2. Search for overrides of the `onFilterTouchEventForSecurity` method or manual checks for `MotionEvent.FLAG_WINDOW_IS_OBSCURED` or `MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED`.
3. Analyze the logic in these methods to verify if touch events are discarded (return `false`) when the obscuration flags are detected.

## Observation

The output should contain a list of sensitive views or activities where `onFilterTouchEventForSecurity` is implemented or where `FLAG_WINDOW_IS_OBSCURED` is checked, along with whether the logic correctly rejects touches when an overlay is present.

## Evaluation

The test case fails if sensitive custom views or activities handling critical user actions do not validate the security of the touch event. Specifically, if the code processes touch events without checking `FLAG_WINDOW_IS_OBSCURED` or `FLAG_WINDOW_IS_PARTIALLY_OBSCURED` and does not return `false` in `onFilterTouchEventForSecurity` when an obstruction is detected, the application is vulnerable to UI redressing.
