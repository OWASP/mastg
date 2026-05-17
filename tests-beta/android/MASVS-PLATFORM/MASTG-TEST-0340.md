---
platform: android
title: References to Overlay Attack Protections
id: MASTG-TEST-0340
apis: [onFilterTouchEventForSecurity, setFilterTouchesWhenObscured, FLAG_WINDOW_IS_OBSCURED, FLAG_WINDOW_IS_PARTIALLY_OBSCURED]
type: [static]
weakness: MASWE-0053
best-practices: [MASTG-BEST-0040]
profiles: [L2]
knowledge: [MASTG-KNOW-0022]
---

## Overview

Overlay attacks (also known as tapjacking) allow malicious apps to place deceptive UI elements over a legitimate app's interface, potentially tricking users into performing unintended actions such as granting permissions, revealing credentials, or authorizing payments. If the app does not implement appropriate protections, users can interact with overlaid malicious content while believing they are interacting with the legitimate app.

Android provides several mechanisms to protect against overlay attacks through touch filtering. These mechanisms can detect when a view is obscured and filter touch events accordingly. However, if the app does not use these protections on sensitive UI elements, it remains vulnerable to overlay attacks.

This test checks whether the app implements overlay attack protections by looking for references to touch filtering APIs and attributes that prevent interaction when views are obscured.

These include:

- The `setFilterTouchesWhenObscured` method.
- The `android:filterTouchesWhenObscured` attribute in layout files.
- The `onFilterTouchEventForSecurity` method.
- Checks for `FLAG_WINDOW_IS_OBSCURED` or `FLAG_WINDOW_IS_PARTIALLY_OBSCURED` flags.
- The [`setHideOverlayWindows`](https://developer.android.com/reference/android/view/Window#setHideOverlayWindows(boolean)) method and the required `HIDE_OVERLAY_WINDOWS` permission for API level 31 and above.

## Steps

1. Use @MASTG-TECH-0014 to search for references to overlay protection mechanisms.
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml file and check the `targetSdkVersion` and any relevant permissions.

## Observation

The output should contain:

- A list of locations where overlay protection mechanisms are used
- The app's `targetSdkVersion`
- Any relevant permissions, such as `HIDE_OVERLAY_WINDOWS`

## Evaluation

The test fails if the app handles sensitive user interactions (such as login, payment confirmation, permission requests, or security settings) and does not implement any overlay attack protections on those sensitive UI elements.

For example:

- The app doesn't implement `setFilterTouchesWhenObscured(true)` or `android:filterTouchesWhenObscured="true"` on sensitive UI elements.
- The app doesn't override `onFilterTouchEventForSecurity` to implement custom security policies.
- The app doesn't check for `FLAG_WINDOW_IS_OBSCURED` or `FLAG_WINDOW_IS_PARTIALLY_OBSCURED` in touch event handlers for sensitive interactions.
- The app targets API level 31 or higher but does not use `setHideOverlayWindows(true)` and declare the `HIDE_OVERLAY_WINDOWS` permission.
