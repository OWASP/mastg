---
title: Restricting Accessibility Service Access to Sensitive Views
alias: restricting-accessibility-service-access-to-sensitive-views
id: MASTG-BEST-0x01
platform: android
knowledge: [MASTG-KNOW-0x01]
available_since: 34
---

Mark views that display or accept sensitive data as accessibility data sensitive (@MASTG-KNOW-0x01). On Android 14 (API level 34) and higher, this restricts access to these views to accessibility services that declare themselves as accessibility tools by setting [`android:isAccessibilityTool`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#attr_android:isAccessibilityTool) to `true` in their service configuration, such as screen readers. Accessibility services that don't declare `android:isAccessibilityTool="true"`, including malware that tricks the user into enabling it, can't read the content of these views or perform interactions on them, for example, capturing typed credentials or tapping a payment confirmation button on the user's behalf.

Apply this protection to views that handle or display sensitive information, such as:

- Login screens (username and password fields)
- System prompts, permissions, settings, and app-specific confirmations, such as payment and transaction confirmations
- Personal information forms (for example, national ID numbers or credit card numbers)
- Two-factor authentication inputs
- Biometric authentication prompts
- Any view displaying confidential user data

## Marking Views as Sensitive

Set the [`android:accessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#attr_android:accessibilityDataSensitive) attribute to `true` in the layout file:

```xml
<EditText
    android:id="@+id/passwordField"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:inputType="textPassword"
    android:accessibilityDataSensitive="true" />
```

Or call [`setAccessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#setAccessibilityDataSensitive(int)) with [`ACCESSIBILITY_DATA_SENSITIVE_YES`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_YES) for views created or configured in code:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
    confirmButton.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)
}
```

Views using the default value, [`ACCESSIBILITY_DATA_SENSITIVE_AUTO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_AUTO), inherit the protection from any parent that is marked as sensitive. Marking a container protects all its children, so you can protect a group of related views at once. Don't set [`ACCESSIBILITY_DATA_SENSITIVE_NO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_NO) on views that handle sensitive data, because it overrides the inherited protection.

## Using filterTouchesWhenObscured

Views that set [`filterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) to `true` to protect against overlay attacks (see @MASTG-BEST-0040) are also treated as accessibility data sensitive, as long as they keep the default [`ACCESSIBILITY_DATA_SENSITIVE_AUTO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_AUTO) value. If your app already uses `filterTouchesWhenObscured` on sensitive views, those views are already protected. Setting `android:accessibilityDataSensitive="true"` explicitly still makes the intent clear and keeps the protection in place if the touch filtering configuration changes later.

## Caveats and Considerations

- **Impact on accessibility:** accessibility services that don't declare `android:isAccessibilityTool="true"` lose access to the marked views, which may affect users who rely on them. Apply the protection to the views that handle sensitive data rather than to entire screens, and test the app with accessibility tools such as TalkBack and Voice Access.
- **Platform version:** on devices running versions earlier than Android 14 (API level 34), the attribute is ignored and there's no equivalent platform protection against accessibility services. Use additional protections for sensitive flows, such as @MASTG-BEST-0040 against overlay attacks.
- **Not a complete defense:** the protection relies on accessibility tools being declared correctly. Google Play and Google Play Protect enforce the `android:isAccessibilityTool` declaration, but apps from other sources may not be subject to these checks.

For more details, see the Android Developers Blog post ["Enhancing Android security: Stop malware from snooping on your app data"](https://android-developers.googleblog.com/2025/12/enhancing-android-security-stop-malware.html) and the Android guidance on [tapjacking mitigations](https://developer.android.com/privacy-and-security/risks/tapjacking#mitigations).
