---
id: MASTG-TEST-0317
title: Runtime Protection of Sensitive UI Against Overlay and Accessibility Abuse
platform: android
weakness: MASWE-0067
type: [dynamic, manual]
best-practices: [MASTG-BEST-0317]
profiles: [L2]
available_since: 16
apis:
- android.view.View#setFilterTouchesWhenObscured(boolean)
---

## Overview

If an Android app allows user interaction with sensitive UI elements while the
application window is obscured by overlays or accessed by unauthorized
accessibility services, malicious apps may intercept input or inject unintended
actions. This can lead to credential theft, transaction manipulation, or
bypassing user intent.

This test checks whether the app properly blocks interaction with sensitive UI
elements when overlay or accessibility abuse conditions are present.


## Steps

1. Enable an application capable of drawing overlays or an unauthorized
   accessibility service on the device.
2. Use @MASTG-TECH-0009 to interact with the app while an overlay is present.
3. Attempt to interact with sensitive UI elements such as authentication or
   transaction confirmation fields.
4. Observe whether user input is accepted while the application window is
   obscured.

## Observation

The output should contain evidence showing whether sensitive UI elements can be
interacted with while overlays or unauthorized accessibility services are active.

## Evaluation

The test case fails if sensitive UI elements accept user interaction while the
app window is obscured or controlled by unauthorized accessibility services.
