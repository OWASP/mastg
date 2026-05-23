---
platform: android
title: Activity-Level Overlay Protection Using setHideOverlayWindows
id: MASTG-DEMO-0105
code: [kotlin, java, xml]
test: MASTG-TEST-0340
tools: [semgrep]
kind: pass
---

## Sample

The sample shows an app that uses `setHideOverlayWindows(true)` at the activity level (available from Android 12, API level 31) and declares the required `HIDE_OVERLAY_WINDOWS` permission in the manifest. Unlike the view-level protections in @MASTG-DEMO-0103, this approach prevents any overlay window from appearing over the entire activity.

{{ MastgTest.kt # MastgTest_reversed.java # AndroidManifest.xml # AndroidManifest_reversed.xml }}

## Steps

Let's run our @MASTG-TOOL-0110 rule against the decompiled code and the reversed manifest to detect the overlay protection mechanisms.

{{ ../../../../rules/mastg-android-overlay-protection.yml }}

{{ run.sh }}

## Observation

The rule detected the `setHideOverlayWindows` call in the decompiled code and the `HIDE_OVERLAY_WINDOWS` permission in the manifest:

{{ output.txt }}

## Evaluation

The test passes because the app implements activity-level overlay protection:

- The `setHideOverlayWindows(true)` call (detected in the code output) instructs the system to hide any `TYPE_APPLICATION_OVERLAY` windows when this activity is in the foreground, effectively preventing overlay attacks on all UI elements in the activity.
- The `HIDE_OVERLAY_WINDOWS` permission (detected in the manifest output) is required to use `setHideOverlayWindows` on Android 12 (API level 31) and above.

This is a stronger protection than the view-level approach shown in @MASTG-DEMO-0103, which requires adding protection to every sensitive view individually.
