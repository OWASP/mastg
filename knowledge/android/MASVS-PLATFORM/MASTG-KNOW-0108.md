---
masvs_category: MASVS-PLATFORM
platform: android
title: Accessibility Data Sensitivity
available_since: 35
---

## Overview

Starting with Android 16 (API level 35), the [`accessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#attr_android:accessibilityDataSensitive) attribute provides a mechanism for developers to protect sensitive UI views from being accessed by accessibility services that are not legitimate accessibility tools. When this attribute is set to `true` on a view, the system restricts apps with accessibility permissions from reading or interacting with that view's content unless the accessibility service declares itself as a legitimate accessibility tool via [`isAccessibilityTool`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#FLAG_ACCESSIBILITY_TOOL) in its manifest.

## Purpose and Behavior

The `accessibilityDataSensitive` flag addresses a security concern where malicious apps can abuse Android's accessibility framework to eavesdrop on sensitive user interactions or inject clicks on behalf of the user. By marking views as sensitive, developers can:

- Prevent unauthorized accessibility services from reading view content (text, descriptions, etc.)
- Block non-legitimate accessibility services from performing clicks or other interactions on sensitive views
- Protect against overlay attacks that exploit accessibility permissions

## Configuration

The attribute can be set in two ways:

### XML Layout

In layout files, developers can explicitly mark views as containing sensitive data:

```xml
<EditText
    android:id="@+id/passwordField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:accessibilityDataSensitive="true" />
```

### Programmatic API

At runtime, developers can use the [`setAccessibilityDataSensitive()`](https://developer.android.com/reference/android/view/View#setAccessibilityDataSensitive(int)) method:

```kotlin
passwordField.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)
```

The method accepts three values:

- `View.ACCESSIBILITY_DATA_SENSITIVE_AUTO` (default): Inherits from parent view or defaults based on other flags
- `View.ACCESSIBILITY_DATA_SENSITIVE_YES`: Explicitly marks the view as sensitive
- `View.ACCESSIBILITY_DATA_SENSITIVE_NO`: Explicitly marks the view as not sensitive

## Automatic Enablement via filterTouchesWhenObscured

An important feature is that views with [`filterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) set to `true` automatically inherit `accessibilityDataSensitive` protection. This means apps already using tapjacking protection gain accessibility security benefits without code changes:

```xml
<Button
    android:id="@+id/confirmButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:filterTouchesWhenObscured="true" />
    <!-- This button also gets accessibilityDataSensitive protection -->
```

## Legitimate Accessibility Tools

For accessibility services to access views marked as sensitive, they must declare the `isAccessibilityTool` flag in their service configuration:

```xml
<accessibility-service
    android:accessibilityFlags="flagReportViewIds"
    android:isAccessibilityTool="true" />
```

Services declaring this flag should be legitimate accessibility tools like screen readers, magnification tools, or switch access utilities. Malicious apps attempting to abuse accessibility permissions typically would not declare this flag, as it subjects them to additional scrutiny during Play Store review and by users during installation.

## Platform Enforcement

When a non-tool accessibility service attempts to access a view marked as `accessibilityDataSensitive="true"`:

- The view's content is redacted from accessibility events
- Interaction attempts (clicks, long clicks, etc.) are blocked
- The system logs a security event for audit purposes

## References

- [Android Developers Blog - Enhancing Android Security: Stop Malware with Accessibility Data Sensitivity](https://android-developers.googleblog.com/2025/12/enhancing-android-security-stop-malware.html)
- [Android Security: Tapjacking Mitigations](https://developer.android.com/privacy-and-security/risks/tapjacking#mitigations)
- [View.setAccessibilityDataSensitive](https://developer.android.com/reference/android/view/View#setAccessibilityDataSensitive(int))
- [AccessibilityServiceInfo.isAccessibilityTool](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#FLAG_ACCESSIBILITY_TOOL)
