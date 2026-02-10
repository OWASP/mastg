---
platform: android
title: References to Overlay Attack Protections
id: MASTG-TEST-0x35
apis: [onFilterTouchEventForSecurity, setFilterTouchesWhenObscured, FLAG_WINDOW_IS_OBSCURED, FLAG_WINDOW_IS_PARTIALLY_OBSCURED]
type: [static]
weakness: MASWE-0053
best-practices: [MASTG-BEST-0x29]
profiles: [L2]
knowledge: [MASTG-KNOW-0022]
---

## Overview

Overlay attacks (also known as tapjacking) allow malicious apps to place deceptive UI elements over a legitimate app's interface, potentially tricking users into performing unintended actions such as granting permissions, revealing credentials, or authorizing payments. If the app does not implement appropriate protections, users can interact with overlaid malicious content while believing they are interacting with the legitimate app.

Android provides several mechanisms to protect against overlay attacks through touch filtering. These mechanisms can detect when a view is obscured and filter touch events accordingly. However, if the app does not use these protections on sensitive UI elements, it remains vulnerable to overlay attacks.

This test checks whether the app implements overlay attack protections by looking for references to touch filtering APIs and attributes that prevent interaction when views are obscured.

## Steps

1. Use @MASTG-TECH-0014 to search for references to overlay protection mechanisms:
   - The `setFilterTouchesWhenObscured` method
   - The `android:filterTouchesWhenObscured` attribute in layout files
   - The `onFilterTouchEventForSecurity` method
   - Checks for `FLAG_WINDOW_IS_OBSCURED` or `FLAG_WINDOW_IS_PARTIALLY_OBSCURED` flags
   - The [`setHideOverlayWindows`](https://developer.android.com/reference/android/view/Window#setHideOverlayWindows(boolean)) method
2. Use @MASTG-TECH-0117 to obtain the AndroidManifest.xml file and check the `targetSdkVersion`.

## Observation

The output should contain:

- A list of locations where overlay protection mechanisms are used
- The app's `targetSdkVersion`

## Evaluation

The test fails if the app handles sensitive user interactions (such as login, payment confirmation, permission requests, or security settings) and does not implement any overlay attack protections on those sensitive UI elements.

Consider the following when evaluating:

- Apps targeting older Android versions (API level 25 or lower) are more vulnerable to overlay attacks due to system-level vulnerabilities
- Not all UI elements require overlay protection, only those handling sensitive user interactions
- The absence of protections does not necessarily mean the app is vulnerable, but it increases the risk

The test passes if:

- The app implements `setFilterTouchesWhenObscured(true)` or `android:filterTouchesWhenObscured="true"` on sensitive UI elements
- The app overrides `onFilterTouchEventForSecurity` to implement custom security policies
- The app checks for `FLAG_WINDOW_IS_OBSCURED` or `FLAG_WINDOW_IS_PARTIALLY_OBSCURED` in touch event handlers for sensitive interactions
- The app targets a modern Android API level (26+) which provides system-level protections and does not handle particularly sensitive operations that would benefit from additional app-level protections
