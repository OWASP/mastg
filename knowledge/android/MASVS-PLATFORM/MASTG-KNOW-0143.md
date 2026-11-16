---
masvs_category: MASVS-PLATFORM
platform: android
title: Accessibility Data Sensitivity
available_since: 34
best-practices: [MASTG-BEST-0075]
---

Android 14 (API level 34) introduced accessibility data sensitivity, which lets an app specify that a [`View`](https://developer.android.com/reference/android/view/View) only allows interactions from accessibility services that are declared as accessibility tools by setting the [`android:isAccessibilityTool`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#attr_android:isAccessibilityTool) attribute to `true` in its service configuration. Accessibility services that aren't declared as accessibility tools can't read the content of these views or perform accessibility actions on them, while accessibility tools, such as screen readers, keep full access. On Android 16 (API level 36) and higher, the protection also blocks simulated touch gestures that these services inject with [`AccessibilityService.dispatchGesture`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService#dispatchGesture(android.accessibilityservice.GestureDescription,%20android.accessibilityservice.AccessibilityService.GestureResultCallback,%20android.os.Handler)) (see the Android guidance on [tapjacking](https://developer.android.com/privacy-and-security/risks/tapjacking)).

!!! note "Accessibility Tool Declaration"
    Google Play restricts this declaration to apps whose primary purpose is to support people with disabilities, such as screen readers, switch-based input tools, and voice-based input tools. Apps that use accessibility services for other purposes, such as password managers, automation tools, launchers, or antivirus apps, don't qualify. Google Play rejects apps that declare [`android:isAccessibilityTool`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#attr_android:isAccessibilityTool) without meeting these criteria, and Google Play Protect blocks such apps on user devices. See the Google Play [policy on the use of the AccessibilityService API](https://support.google.com/googleplay/android-developer/answer/10964491) and the Android Developers Blog post ["Enhancing Android security: Stop malware from snooping on your app data"](https://android-developers.googleblog.com/2025/12/enhancing-android-security-stop-malware.html) for more details.

Apps can configure accessibility data sensitivity for each view in the following ways:

- In layout files within the `res/layout` directory, using the [`android:accessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#attr_android:accessibilityDataSensitive) XML attribute.
- Programmatically, by calling [`setAccessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#setAccessibilityDataSensitive(int)) on a view with one of the following values:
    - [`View.ACCESSIBILITY_DATA_SENSITIVE_AUTO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_AUTO) (default): the system determines the value automatically (see "Automatic Behavior" below).
    - [`View.ACCESSIBILITY_DATA_SENSITIVE_YES`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_YES): the view only allows interactions from accessibility tools.
    - [`View.ACCESSIBILITY_DATA_SENSITIVE_NO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_NO): the view allows interactions from all accessibility services.
- In Jetpack Compose, by setting the `isSensitiveData` semantics property to `true` with the `semantics` modifier.

The [`isAccessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#isAccessibilityDataSensitive()) method returns the effective state of a view, taking the automatic behavior into account.

## Automatic Behavior

When a view uses [`ACCESSIBILITY_DATA_SENSITIVE_AUTO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_AUTO), the system restricts interactions to accessibility tools if any of the following conditions is met:

- Any parent of the view returns `true` from [`isAccessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#isAccessibilityDataSensitive()).
- The view has [`View.setFilterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) set to `true`, either with the [`android:filterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#attr_android:filterTouchesWhenObscured) XML attribute or by calling `View.setFilterTouchesWhenObscured(true)` (see @MASTG-KNOW-0022).

## Advanced Protection on Android 17

On Android 17, enabling [Advanced Protection](https://blog.google/security/android-advanced-protection-updates/) restricts access to the `AccessibilityService` API to verified apps categorized as accessibility tools. While Advanced Protection is enabled, other accessibility services are restricted for all apps, independently of whether individual views are marked as accessibility data sensitive. Accessibility data sensitivity remains the mechanism that apps control for devices that don't use Advanced Protection.
