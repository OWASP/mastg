---
id: MASTG-DEMO-0317
title: Overlay Interaction on Sensitive UI Elements
platform: android
code: [kotlin]
---

## Sample

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

1. Build and install the modified MASTestApp.
2. Enable an overlay application or draw-over-other-apps permission.
3. Navigate to the screen containing sensitive input fields.
4. Attempt to interact with the fields while the window is obscured.

## Observation

{{ output.txt }}

## Evaluation

If interaction with sensitive input fields is still possible while an overlay
is present, the demo represents a failing case for @MASTG-TEST-0317.

If interaction is blocked when the window is obscured, the demo represents a
passing case for @MASTG-TEST-0317.
