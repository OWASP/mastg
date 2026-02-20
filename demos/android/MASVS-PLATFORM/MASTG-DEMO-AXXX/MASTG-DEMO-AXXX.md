---
platform: android
title: Testing for Tapjacking Protection using Semgrep
id: MASTG-DEMO-AXXX
code: [kotlin]
Test: MASTG-TEST-AXXX 
---

## Sample

The snippet below shows sample code that protects against overlay attacks (tapjacking) by overriding `onFilterTouchEventForSecurity`. The method checks for `FLAG_WINDOW_IS_OBSCURED` or `FLAG_WINDOW_IS_PARTIALLY_OBSCURED` flags on the `MotionEvent` and rejects touches when an overlay is detected.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-overlay-protection.yml }}

{{ run.sh }}

## Observation

The rule has identified that the application explicitly handles touch filtering by overriding `onFilterTouchEventForSecurity` or checking the obscuration flags. This pattern indicates that the application is programmatically verifying the integrity of touch events.

{{ output.txt }}

## Evaluation

The test passes because the application implements adequate defenses against overlay attacks on the code level. By overriding `onFilterTouchEventForSecurity` or checking `FLAG_WINDOW_IS_OBSCURED`, the application ensures that touch events are not processed if the window is fully or partially covered by another window. This prevents attackers from tricking the user into interacting with the application through a malicious overlay.