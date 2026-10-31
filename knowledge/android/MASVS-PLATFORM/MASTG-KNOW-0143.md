---
masvs_category: MASVS-PLATFORM
platform: android
title: Accessibility Data Sensitivity
available_since: 34
best-practices: [MASTG-BEST-0075]
---

Android 14 (API level 34) introduced accessibility data sensitivity, which lets an app specify that a [`View`](https://developer.android.com/reference/android/view/View) only allows interactions from accessibility services that are declared as accessibility tools. Accessibility services that aren't declared as [accessibility tools](#accessibility-tools) are denied access to the content of these views and can't perform interactions on them. Accessibility tools, such as screen readers, keep full access.

The feature complements touch filtering for obscured windows (see @MASTG-KNOW-0022), which uses the same [`View.setFilterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) setting. However, on devices running Android versions earlier than Android 14 (API level 34), this mechanism only filters touch events when the view is obscured by another window and doesn't affect accessibility services.

## Configuration

Apps can configure accessibility data sensitivity for each view in the following ways:

- In layout files within the `res/layout` directory, using the [`android:accessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#attr_android:accessibilityDataSensitive) XML attribute.
- Programmatically, by calling [`setAccessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#setAccessibilityDataSensitive(int)) on a view with one of the following values:
    - [`View.ACCESSIBILITY_DATA_SENSITIVE_AUTO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_AUTO) (default): the system determines the value automatically (see "Automatic Behavior" below).
    - [`View.ACCESSIBILITY_DATA_SENSITIVE_YES`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_YES): the view only allows interactions from accessibility tools.
    - [`View.ACCESSIBILITY_DATA_SENSITIVE_NO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_NO): the view allows interactions from all accessibility services.

The [`isAccessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#isAccessibilityDataSensitive()) method returns the effective state of a view, taking the automatic behavior into account.

In decompiled code, the constants usually appear as their integer values: `0` for `ACCESSIBILITY_DATA_SENSITIVE_AUTO`, `1` for `ACCESSIBILITY_DATA_SENSITIVE_YES`, and `2` for `ACCESSIBILITY_DATA_SENSITIVE_NO`.

## Automatic Behavior

When a view uses [`ACCESSIBILITY_DATA_SENSITIVE_AUTO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_AUTO), the system restricts interactions to accessibility tools if any of the following conditions is met:

- The view has [`View.setFilterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) set to `true`, either with the [`android:filterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#attr_android:filterTouchesWhenObscured) XML attribute or by calling `View.setFilterTouchesWhenObscured(true)`.
- Any parent of the view returns `true` from [`isAccessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#isAccessibilityDataSensitive()).

As a result, views that already use [`View.setFilterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) and views inside a container marked as sensitive get the same treatment without further configuration, unless they explicitly set [`ACCESSIBILITY_DATA_SENSITIVE_NO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_NO).

## Accessibility Tools

An accessibility service declares itself as an accessibility tool by setting the [`android:isAccessibilityTool`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#attr_android:isAccessibilityTool) attribute to `true` in its service configuration. Apps can check this value with [`AccessibilityServiceInfo.isAccessibilityTool`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#isAccessibilityTool()), which is available since Android 12 (API level 31).

Google Play restricts this declaration to apps whose primary purpose is to support people with disabilities, such as screen readers, switch-based input tools, and voice-based input tools. Google Play rejects apps that declare [`android:isAccessibilityTool`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#attr_android:isAccessibilityTool) without meeting these criteria, and Google Play Protect blocks such apps on user devices. See the Google Play [policy on the use of the AccessibilityService API](https://support.google.com/googleplay/android-developer/answer/10964491) and the Android Developers Blog post ["Enhancing Android security: Stop malware from snooping on your app data"](https://android-developers.googleblog.com/2025/12/enhancing-android-security-stop-malware.html) for more details.
