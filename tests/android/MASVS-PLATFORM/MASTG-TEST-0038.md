---
masvs_v1_id:
- MSTG-PLATFORM-10
masvs_v2_id:
- MASVS-PLATFORM-2
platform: android
title: Testing for Protection of Sensitive UI Views Against Accessibility Abuse
masvs_v1_levels:
- L2
profiles: [L2]
---

## Overview

Starting with Android 16, the platform introduces the
`accessibilityDataSensitive` flag, which allows developers to explicitly mark
UI elements as containing sensitive data. When applied, this flag restricts
unauthorized accessibility services from reading or interacting with sensitive
views unless they are explicitly declared as legitimate accessibility tools.

This mechanism strengthens existing protections against overlay-based attacks,
such as tapjacking and click injection, and helps mitigate abuse by malicious
accessibility services. These protections should be treated as defense-in-depth
measures and must not replace other security controls.

## Static Analysis

During static analysis, verify whether the application applies accessibility-
related protections to UI elements that handle sensitive data, such as login
screens, OTP input fields, or transaction confirmation views.

Check for the following attributes and APIs (non-exhaustive list):

- Usage of the layout attribute
  [`android:accessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#accessibilityDataSensitive)
  on sensitive UI elements (Android 16 and higher).
- Usage of the layout attribute
  [`android:filterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#attr_android:filterTouchesWhenObscured)
  or the corresponding method
  [`setFilterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)),
  which implicitly treats views as accessibility-sensitive.
- Custom handling of obscured touch events by overriding
  [`onFilterTouchEventForSecurity`](https://developer.android.com/reference/android/view/View#onFilterTouchEventForSecurity(android.view.MotionEvent)).

Confirm that these protections are applied selectively to sensitive UI elements,
especially when overlays are otherwise permitted for legitimate functionality.

## Dynamic Analysis

Dynamic testing of accessibility abuse and overlay attacks depends on the target
Android version and the presence of malicious overlay or accessibility services.

- Enable a third-party overlay application and attempt to interact with
  sensitive UI elements.
- Enable a non-legitimate accessibility service and observe whether it can read
  or interact with views marked as accessibility-sensitive.
- Verify that interactions with protected UI elements are blocked or ignored
  when the application window is obscured.

On modern Android versions, these protections should prevent unauthorized
interaction and data exposure while still allowing legitimate accessibility
tools explicitly recognized by the platform to function as intended.

## References

- Android Developers Blog: Enhancing Android Security Against Overlay and
  Accessibility Abuse
- Android Developer Documentation: View Security and Tapjacking Mitigations
