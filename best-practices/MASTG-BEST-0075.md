---
title: Restricting Accessibility Service Access to Sensitive Views
alias: restricting-accessibility-service-access-to-sensitive-views
id: MASTG-BEST-0075
platform: android
knowledge: [MASTG-KNOW-0143]
available_since: 34
---

On Android 14 (API level 34) and higher, mark views that display or accept sensitive data as accessibility data sensitive using `accessibilityDataSensitive` or `filterTouchesWhenObscured` (see @MASTG-KNOW-0143). This prevents access to these views by accessibility services that don't declare `android:isAccessibilityTool="true"`, including malware that tricks the user into enabling it. Such services won't be able to read the content of any protected views or perform interactions on them, for example, capturing typed credentials or tapping a payment confirmation button on the user's behalf. On Android 16 (API level 36) and higher, it also prevents those services from injecting gestures that target sensitive views.

Apply this protection to views that handle or display sensitive information, such as:

- Login screens (username and password fields)
- System prompts, permissions, settings, and app-specific confirmations, such as payment and transaction confirmations
- Personal information forms (for example, national ID numbers or credit card numbers)
- Two-factor authentication inputs
- Biometric authentication prompts
- Any view displaying confidential user data

## Marking Views as Sensitive

**For Jetpack Compose**, use the `semantics` modifier to set the `isSensitiveData` property to `true`:

```kotlin
BasicText(
    text = "Your password",
    modifier = Modifier.semantics { isSensitiveData = true }
)
```

**For XML layout files**, set the [`android:accessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#attr_android:accessibilityDataSensitive) attribute to `true`:

```xml
<EditText android:accessibilityDataSensitive="true" />
```

**For views created or configured in code**, call [`setAccessibilityDataSensitive`](https://developer.android.com/reference/android/view/View#setAccessibilityDataSensitive(int)) with [`ACCESSIBILITY_DATA_SENSITIVE_YES`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_YES):

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
    myView.setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)
}
```

Views using the default value, [`ACCESSIBILITY_DATA_SENSITIVE_AUTO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_AUTO), inherit the protection from any parent that is marked as sensitive. Marking a container protects all its children, so you can protect a group of related views at once. Don't set [`ACCESSIBILITY_DATA_SENSITIVE_NO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_NO) on views that handle sensitive data, because it overrides the inherited protection.

## Using filterTouchesWhenObscured

On Android 14 (API level 34) and higher, views that set [`filterTouchesWhenObscured`](https://developer.android.com/reference/android/view/View#setFilterTouchesWhenObscured(boolean)) to `true` to protect against overlay attacks (see @MASTG-BEST-0040) are automatically treated as accessibility data sensitive, as long as they keep the default [`ACCESSIBILITY_DATA_SENSITIVE_AUTO`](https://developer.android.com/reference/android/view/View.html#ACCESSIBILITY_DATA_SENSITIVE_AUTO) value.

Touch filtering rejects events marked with `FLAG_WINDOW_IS_OBSCURED`; it doesn't by itself block injected gestures. On Android 16 (API level 36) and higher, the separate accessibility-data-sensitive policy blocks injected gestures targeting these views, even when no overlay is present.

If your app already uses `filterTouchesWhenObscured` on sensitive views, those views are already protected. Setting `android:accessibilityDataSensitive="true"` explicitly still makes the intent clear and keeps the protection in place if the touch filtering configuration changes later.

## Caveats and Considerations

- **Impact on accessibility:** accessibility services that don't declare `android:isAccessibilityTool="true"` lose access to the marked views, which may affect users who rely on them. Apply the protection to the views that handle sensitive data rather than to entire screens, and test the app with accessibility tools such as TalkBack and Voice Access.
- **Impact on other legitimate apps:** apps that use an accessibility service for purposes other than supporting people with disabilities, such as password managers, automation tools, or launchers, don't qualify as accessibility tools and also lose access to protected views. This is an expected trade-off, not a reason to leave sensitive views unprotected. To keep password managers working, support the [Android Autofill framework](https://developer.android.com/identity/autofill/autofill-optimize), which doesn't rely on accessibility services, for example, by setting autofill hints on credential fields.
- **Platform version:** on devices running versions earlier than Android 14 (API level 34), the attribute is ignored and there's no equivalent platform protection against accessibility services. Use additional protections for sensitive flows, such as @MASTG-BEST-0040 against overlay attacks.
- **Not a complete defense:** accessibility services that declare `android:isAccessibilityTool="true"` keep full access to protected views and can still read and interact with them. Google Play and Google Play Protect enforce this declaration, but apps from other sources may not be subject to these checks, so a malicious app that falsely declares itself as an accessibility tool isn't blocked by this protection.

For more details, see the Android Developers Blog post ["Enhancing Android security: Stop malware from snooping on your app data"](https://android-developers.googleblog.com/2025/12/enhancing-android-security-stop-malware.html) and the Android guidance on [tapjacking mitigations](https://developer.android.com/privacy-and-security/risks/tapjacking#mitigations).
