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

If an Android app displays sensitive UI elements without protection against
overlay or accessibility abuse, malicious apps may intercept user input or
inject unauthorized interactions, leading to credential theft or transaction
manipulation.

This test verifies whether the app prevents interaction with sensitive UI
elements while overlays or unauthorized accessibility services are active.

## Steps

1. Enable an overlay app or a non-legitimate accessibility service.
2. Interact with sensitive UI elements in the app.
3. Observe whether input is accepted while the app window is obscured.

## Observation

The output should contain evidence showing whether sensitive UI elements can be
interacted with while overlays or unauthorized accessibility services are active.

## Evaluation

The test case fails if sensitive UI elements accept user interaction while the
app window is obscured or controlled by unauthorized accessibility services.
