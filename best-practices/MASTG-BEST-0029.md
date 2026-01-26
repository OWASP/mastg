---
title: Preventing Overlay Attacks
alias: preventing-overlay-attacks
id: MASTG-BEST-0029
platform: android
knowledge: [MASTG-KNOW-0022]
---

Apps should protect sensitive user interactions from overlay attacks by implementing appropriate defensive mechanisms. Overlay attacks (including tapjacking) occur when malicious apps place deceptive UI elements over legitimate app interfaces to trick users into unintended actions.

## Recommendation

Implement touch filtering to prevent touch events when the app's UI is obscured by another app. Use one or more of the following mechanisms:

1. **Set the layout attribute `android:filterTouchesWhenObscured="true"`** for sensitive views such as login buttons, payment confirmations, or permission requests. This filters touch events when the view is obscured.

2. **Call `setFilterTouchesWhenObscured(true)`** programmatically on sensitive views to enable touch filtering at runtime.

3. **Override `onFilterTouchEventForSecurity`** for more granular control and to implement custom security policies based on your app's specific requirements.

4. **Check motion event flags** such as `FLAG_WINDOW_IS_OBSCURED` (API level 9+) or `FLAG_WINDOW_IS_PARTIALLY_OBSCURED` (API level 29+) in touch event handlers to detect obscured windows and respond appropriately.

Apply these protections selectively to security-sensitive UI elements where user confirmation is critical, such as:

- Login and authentication screens
- Permission request dialogs
- Payment confirmation buttons
- Sensitive data entry fields
- Security settings changes

## Rationale

Without overlay protection, malicious apps can:

- Capture user credentials by overlaying fake login screens
- Trick users into granting dangerous permissions
- Intercept sensitive data entry
- Perform unauthorized actions by obscuring the true nature of UI elements

Touch filtering mechanisms help ensure that user interactions occur with the intended UI elements and not with overlays placed by malicious apps.

## Caveats and Considerations

- Touch filtering is not a complete solution on older Android versions that have system-level vulnerabilities. Apps should target modern API levels when possible.
- Some attacks, particularly those exploiting system-level vulnerabilities (for example, Toast Overlay on Android versions before 8.0), cannot be fully mitigated at the app level.
- Applying touch filtering too broadly may impact legitimate use cases where overlays are expected (for example, system dialogs, accessibility features).
- Users can still be tricked through social engineering even with touch filtering enabled. Apps should combine these protections with user education and clear UI indicators.
- For maximum protection, apps targeting older API levels should consider upgrading their `targetSdkVersion` to benefit from platform-level protections introduced in newer Android versions.

## References

- Android Developer Documentation: [Tapjacking](https://developer.android.com/privacy-and-security/risks/tapjacking)
- Android Developer Documentation: [View Security](https://developer.android.com/reference/android/view/View#security)
- Android Developer Documentation: [setFilterTouchesWhenObscured](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean))
- Android Developer Documentation: [onFilterTouchEventForSecurity](https://developer.android.com/reference/android/view/View#onFilterTouchEventForSecurity(android.view.MotionEvent))
- Android Developer Documentation: [FLAG_WINDOW_IS_OBSCURED](https://developer.android.com/reference/android/view/MotionEvent#FLAG_WINDOW_IS_OBSCURED)
- Android Developer Documentation: [FLAG_WINDOW_IS_PARTIALLY_OBSCURED](https://developer.android.com/reference/android/view/MotionEvent#FLAG_WINDOW_IS_PARTIALLY_OBSCURED)
