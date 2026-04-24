---
title: Preventing Overlay Attacks
alias: preventing-overlay-attacks
id: MASTG-BEST-0040
platform: android
knowledge: [MASTG-KNOW-0022]
---

Apps should protect sensitive user interactions from overlay attacks by implementing appropriate defensive mechanisms. Overlay attacks (including [tapjacking](https://developer.android.com/privacy-and-security/risks/tapjacking)) occur when malicious apps place deceptive UI elements over legitimate app interfaces to trick users into unintended actions.

Implement appropriate mechanisms to protect against overlay attacks. The following approaches are listed from most robust to least robust:

## Prevention Mechanisms

These mechanisms prevent overlays from appearing or block touch events when overlays are detected:

1. **Use `HIDE_OVERLAY_WINDOWS` permission and `setHideOverlayWindows(true)`** (API level 31+): Declare the [`HIDE_OVERLAY_WINDOWS`](https://developer.android.com/reference/android/Manifest.permission#HIDE_OVERLAY_WINDOWS) permission in the manifest and call [`setHideOverlayWindows(true)`](https://developer.android.com/reference/android/view/Window#setHideOverlayWindows(boolean)) on the window to hide all non-system overlay windows while the activity is in the foreground. This is the most robust solution as it prevents overlays entirely rather than just filtering touch events.

2. **Set `android:filterTouchesWhenObscured="true"` or call `setFilterTouchesWhenObscured(true)`**: Set the layout attribute [`android:filterTouchesWhenObscured="true"`](https://developer.android.com/reference/android/view/View#attr_android:filterTouchesWhenObscured) in XML for sensitive views, or call [`setFilterTouchesWhenObscured(true)`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) programmatically on sensitive views such as login buttons, payment confirmations, or permission requests. This filters touch events when the view is obscured by another visible window.

3. **Override `onFilterTouchEventForSecurity`**: Override the [`onFilterTouchEventForSecurity`](https://developer.android.com/reference/android/view/View#onFilterTouchEventForSecurity(android.view.MotionEvent)) method for more granular control and to implement custom security policies based on your app's specific requirements.

## Detection Mechanisms

These mechanisms detect when overlays are present but do not automatically prevent them. They allow the app to respond accordingly:

- **Check motion event flags** such as [`FLAG_WINDOW_IS_OBSCURED`](https://developer.android.com/reference/android/view/MotionEvent#FLAG_WINDOW_IS_OBSCURED) (API level 9+) or [`FLAG_WINDOW_IS_PARTIALLY_OBSCURED`](https://developer.android.com/reference/android/view/MotionEvent#FLAG_WINDOW_IS_PARTIALLY_OBSCURED) (API level 29+) in touch event handlers to detect obscured windows and respond appropriately. Note that this approach requires custom implementation to decide how to handle detected overlays.

Apply these protections selectively to security-sensitive UI elements where user confirmation is critical, such as:

- Login and authentication screens
- Permission request dialogs
- Payment confirmation buttons
- Sensitive data entry fields
- Security settings changes

## Caveats and Considerations

- Touch filtering is not a complete solution on older Android versions that have system-level vulnerabilities. Apps should target modern API levels when possible.
- Some attacks, particularly those exploiting system-level vulnerabilities (for example, Toast Overlay on Android versions before 8.0), cannot be fully mitigated at the app level.
- Applying touch filtering too broadly may impact legitimate use cases where overlays are expected (for example, system dialogs, accessibility features).
- Users can still be tricked through social engineering even with touch filtering enabled. Apps should combine these protections with user education and clear UI indicators.
- For maximum protection, apps targeting older API levels should consider upgrading their `targetSdkVersion` to benefit from platform-level protections introduced in newer Android versions.
