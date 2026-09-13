---
masvs_category: MASVS-PLATFORM
platform: android
title: Accessibility Services
---

Android provides the [`AccessibilityService`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService) API for services that assist users in interacting with apps. Accessibility services can receive UI events and, when configured to retrieve window content, inspect the accessibility representation of the active user interface.

## Accessibility Events and Nodes

An accessibility service receives [`AccessibilityEvent`](https://developer.android.com/reference/android/view/accessibility/AccessibilityEvent) objects through `AccessibilityService.onAccessibilityEvent`.

Depending on the event type and service configuration, an event can contain information such as text, content descriptions, the package that generated the event, and a reference to the source [`AccessibilityNodeInfo`](https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo).

An accessibility service can also inspect the active window through APIs such as `AccessibilityService.getRootInActiveWindow()`, `AccessibilityService.getWindows()`, and `AccessibilityEvent.getSource()`.

[`AccessibilityNodeInfo`](https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo) represents a node in the accessibility hierarchy. Depending on the UI element, it can expose properties such as `getText()`, `getContentDescription()`, `getHintText()`, `getStateDescription()`, `isEditable()`, and `isPassword()`.

The accessibility representation does not necessarily correspond directly to the underlying Android View hierarchy. UI toolkits can generate or merge accessibility nodes independently of their internal widget structure.

## Retrieving Window Content

An accessibility service declares its capabilities in an [`AccessibilityServiceInfo`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo) metadata resource.

The `android:canRetrieveWindowContent` attribute determines whether the service can retrieve the content of application windows and access their `AccessibilityNodeInfo` trees. This capability is declared statically in the service configuration and cannot be enabled dynamically after the service has been installed.

## Accessibility Tools

Android 12 (API level 31) added [`AccessibilityServiceInfo.isAccessibilityTool()`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityServiceInfo#isAccessibilityTool%28%29) and the corresponding `android:isAccessibilityTool` service attribute.

This property indicates whether an accessibility service is intended to assist users with disabilities. The [`android:isAccessibilityTool`](https://developer.android.com/reference/android/R.attr#isAccessibilityTool) attribute defaults to `false`.

Newer accessibility APIs use this classification to control which accessibility services can access particular UI data.

## Accessibility Data Sensitivity

Android 14 (API level 34) added accessibility data sensitivity to [`View`](https://developer.android.com/reference/android/view/View) and [`AccessibilityNodeInfo`](https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo).

For views, [`android:accessibilityDataSensitive`](https://developer.android.com/reference/android/R.attr#accessibilityDataSensitive) accepts three values:

- `auto`, the default, where Android determines whether accessibility interactions should be restricted.
- `yes`, where accessibility interactions are restricted to services for which `isAccessibilityTool()` returns `true`.
- `no`, where interactions are available regardless of `isAccessibilityTool()`.

The equivalent constants are `View.ACCESSIBILITY_DATA_SENSITIVE_AUTO`, `View.ACCESSIBILITY_DATA_SENSITIVE_YES`, and `View.ACCESSIBILITY_DATA_SENSITIVE_NO`.

[`AccessibilityNodeInfo.isAccessibilityDataSensitive()`](https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo#isAccessibilityDataSensitive%28%29) reports whether the node is considered sensitive.

With `ACCESSIBILITY_DATA_SENSITIVE_AUTO`, Android restricts accessibility interactions from services that are not accessibility tools when the view has `filterTouchesWhenObscured` enabled or when one of its parents is accessibility-data-sensitive.

## Password Semantics

[`AccessibilityNodeInfo.isPassword()`](https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo#isPassword%28%29) indicates that a node represents a password field.

Password semantics and the text exposed through accessibility are separate properties. A node can carry password semantics while other accessibility metadata, such as its content description, is populated independently by app code.

In Jetpack Compose, password semantics are represented through [`SemanticsPropertyReceiver.password`](https://developer.android.com/reference/kotlin/androidx/compose/ui/semantics/SemanticsPropertyReceiver#password%28kotlin.Boolean%29), which accepts an `isPasswordObfuscated` parameter. According to the API reference, `isPasswordObfuscated` is descriptive for accessibility services and tests, and does not itself control the visual masking of the text field, which is managed separately by the field's visual or codepoint transformation. The [`BasicSecureTextField`](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/BasicSecureTextField.composable) implementation passes this parameter based on the active `TextObfuscationMode`, so that screen readers such as TalkBack can announce revealed characters when the text is visually shown and mask them otherwise, as described in the [associated AndroidX change](https://github.com/androidx/androidx/commit/4bca3ea5cbb3909915cfc4277aa5694949d0b376).

The visual obfuscation behavior of `TextObfuscationMode` is described in @MASTG-KNOW-0x01.

## UI Automation

Android also provides [`UiAutomation`](https://developer.android.com/reference/android/app/UiAutomation), which establishes an automation connection to the accessibility subsystem and exposes APIs such as `getRootInActiveWindow()`.

UI Automator builds on Android's UI automation and accessibility infrastructure and can inspect the accessibility hierarchy during testing.

`UiAutomation` and a regular application-provided `AccessibilityService` are different interfaces to the accessibility subsystem. In particular, service-specific access restrictions such as `isAccessibilityTool` and `accessibilityDataSensitive` depend on the identity and classification of the accessibility client making the request.

## Cross-Platform UI Frameworks

Cross-platform frameworks can integrate with Android accessibility APIs in different ways. Flutter maintains its own semantics tree and exposes its nodes to Android accessibility services as virtual accessibility nodes (see [`AccessibilityBridge`](http://api.flutter.dev/javadoc/io/flutter/view/AccessibilityBridge.html)). React Native (see ["Host View Tree"](https://reactnative.dev/architecture/glossary#host-view-tree-and-host-view) and ["Accessibility"](https://reactnative.dev/docs/accessibility)) and .NET MAUI (see ["Handlers"](https://learn.microsoft.com/mt-mt/dotnet/maui/user-interface/handlers/?view=net-maui-10.0) and ["Accessibility"](http://learn.microsoft.com/en-us/dotnet/maui/fundamentals/accessibility?view=net-maui-10.0)) generally map framework components and accessibility properties to native Android views and accessibility APIs, while WebView based frameworks such as Ionic (see ["Ionic Accessibility Guide"](http://ionic.io/docs/accessibility) and ["ARIA"](https://ionic.io/docs/accessibility/aria)) and Capacitor rely primarily on the accessibility representation of their web content.
