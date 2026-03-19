---
platform: android
title: Testing for Tapjacking Protection using Semgrep
id: MASTG-DEMO-AXXX
code: [kotlin]
test: MASTG-TEST-AXXX
tools: [semgrep]
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

The test passes because the application implements programmatic tapjacking protection.

- Line 132: The `onFilterTouchEventForSecurity` method is overridden in an anonymous subclass of `AppCompatButton`, establishing the interception point for touch events.
- Line 134: The method checks `event.getFlags() & 1` (`FLAG_WINDOW_IS_OBSCURED`) and `event.getFlags() & 2` (`FLAG_WINDOW_IS_PARTIALLY_OBSCURED`). When either flag is set, the method returns `false` (lines 139–142), blocking the touch event from being processed.
