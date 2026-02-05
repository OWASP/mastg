---
title: Marking UI Views as Containing Sensitive Data
alias: marking-ui-views-sensitive-data
id: MASTG-BEST-0029
platform: android
knowledge: [MASTG-KNOW-0108]
available_since: 35
---

## Overview

Starting with Android 16 (API level 35), developers should mark sensitive UI views using the [`accessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#attr_android:accessibilityDataSensitive) attribute to protect against malicious accessibility services. This protection prevents unauthorized apps with accessibility permissions from reading sensitive view content or performing interactions unless they are declared as legitimate accessibility tools via [`isAccessibilityTool`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#FLAG_ACCESSIBILITY_TOOL).

## Recommendation

Apply `accessibilityDataSensitive="true"` to views that handle or display sensitive information, such as:

- Login screens (username and password fields)
- Payment and transaction confirmation screens
- Personal information forms (SSN, credit card numbers, etc.)
- Two-factor authentication inputs
- Biometric authentication prompts
- Any view displaying confidential user data

### XML Implementation

```xml
<EditText
    android:id="@+id/passwordField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:inputType="textPassword"
    android:accessibilityDataSensitive="true" />

<Button
    android:id="@+id/confirmPayment"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Confirm Payment"
    android:accessibilityDataSensitive="true" />
```

### Programmatic Implementation

```kotlin
passwordField.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)
confirmButton.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)
```

## Rationale

Malicious apps can abuse Android's accessibility framework to:

- Eavesdrop on sensitive user inputs (credentials, payment details)
- Perform automated click injection on critical actions (approve transactions)
- Extract confidential information displayed on screen
- Conduct phishing attacks by monitoring user behavior

By marking views as `accessibilityDataSensitive`, the system restricts non-tool accessibility services from accessing or interacting with these views, significantly reducing the attack surface for malware that relies on accessibility permissions.

## Automatic Protection with filterTouchesWhenObscured

Apps already using [`filterTouchesWhenObscured="true"`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) for tapjacking protection automatically gain `accessibilityDataSensitive` benefits on Android 16+. This provides an instant additional layer of defense with no extra code:

```xml
<Button
    android:id="@+id/transferFunds"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:filterTouchesWhenObscured="true" />
    <!-- Automatically protected from malicious accessibility services on Android 16+ -->
```

If your app already implements `filterTouchesWhenObscured` on sensitive views, you receive this protection automatically. However, explicitly setting `accessibilityDataSensitive` is recommended for clarity and to ensure protection even if `filterTouchesWhenObscured` is later changed.

## Considerations and Caveats

### Legitimate Accessibility Tools

This protection can impact legitimate accessibility services like screen readers. Users relying on accessibility tools may experience degraded functionality on marked views unless the accessibility service declares `isAccessibilityTool="true"`.

- **Testing**: Test your app with popular accessibility tools (TalkBack, Voice Access) to ensure they can still function properly with marked views.
- **User Communication**: If certain views must be marked sensitive for security, consider providing alternative accessible pathways or clear user guidance.

### API Level Compatibility

The `accessibilityDataSensitive` attribute is only available on Android 16 (API level 35) and higher. On older devices:

- The attribute is ignored; the app functions normally without this protection
- Consider using `filterTouchesWhenObscured` for broader compatibility against overlay attacks
- Implement defense-in-depth by combining multiple security measures

### Granularity

Apply the attribute judiciously:

- **Too Broad**: Marking entire screens reduces accessibility for users who need it
- **Too Narrow**: Missing critical views leaves security gaps

Focus on views that directly handle or display sensitive data, not general UI chrome.

### View Hierarchy

The `accessibilityDataSensitive` attribute can be inherited through the view hierarchy when using `View.ACCESSIBILITY_DATA_SENSITIVE_AUTO`. Plan your view structure to minimize repetition while ensuring comprehensive coverage.

## References

- [Android Developers Blog - Enhancing Android Security: Stop Malware with Accessibility Data Sensitivity](https://android-developers.googleblog.com/2025/12/enhancing-android-security-stop-malware.html)
- [Android Security: Tapjacking Mitigations](https://developer.android.com/privacy-and-security/risks/tapjacking#mitigations)
- [View.setAccessibilityDataSensitive](https://developer.android.com/reference/android/view/View#setAccessibilityDataSensitive(int))
