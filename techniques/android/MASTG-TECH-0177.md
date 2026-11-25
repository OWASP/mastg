---
title: Inspecting the UI Hierarchy
platform: android
---

You can inspect the UI hierarchy of an app's screen in several ways. The methods differ in where the data comes from (the accessibility node tree, the app's `View` objects, or the pixels on the screen), what they require, and which properties they reveal. Choose the method based on the property you need to check, and be aware of what each method can't show.

## Using @MASTG-TOOL-0004 with UI Automator

The `uiautomator dump` command writes an XML representation of the foreground window:

```bash
adb shell uiautomator dump /sdcard/ui.xml
adb pull /sdcard/ui.xml
```

Each node includes attributes such as `class`, `text`, `resource-id`, `content-desc`, `bounds`, and flags such as `clickable`, `focusable`, and `password`. The dump also includes UI elements built with Jetpack Compose, because Compose exposes its semantics through the accessibility framework.

This method doesn't require root access or instrumentation and works with release builds. It's useful to get an inventory of the elements and the text shown on a screen, for example, to identify the views that display or accept sensitive data.

However, keep the following limitations in mind:

- The dump only contains the attributes listed above. It doesn't include other `View` properties, such as accessibility data sensitivity (@MASTG-KNOW-0143) or `filterTouchesWhenObscured`.
- UI Automator reads the screen through the platform's UI automation framework, which isn't restricted by accessibility data sensitivity. Views marked as accessibility data sensitive still appear in the dump with their text and attributes. As a result, the dump doesn't reflect what an accessibility service that isn't an accessibility tool can access, and you can't use it to verify this protection.

## Using @MASTG-TOOL-0154

The `hierarchy` command of @MASTG-TOOL-0154 prints the UI hierarchy of the current screen as JSON:

```bash
maestro hierarchy > hierarchy.json
```

Each node includes attributes such as `class`, `text`, `hintText`, `accessibilityText`, `resource-id`, `bounds`, and flags such as `clickable`, `enabled`, and `focused`. The output also includes Jetpack Compose elements and the system UI, such as the status bar.

Maestro reads the UI through the same accessibility layer as UI Automator, so it has the same limitations: the output doesn't include accessibility data sensitivity, and views marked as accessibility data sensitive still appear with their attributes. Compared to `uiautomator dump`, it reports the hint of a text field separately from its text, which makes it easier to identify input fields that are still empty.

Maestro can also interact with views marked as accessibility data sensitive. For example, a flow can tap protected input fields, such as a password field, and enter text into them. Because Maestro acts through test automation with shell privileges and not as an accessibility service, this doesn't show what an accessibility service that isn't an accessibility tool can do. Don't use the results of a Maestro flow to verify this protection.

## Using @MASTG-TOOL-0004 with dumpsys

The `dumpsys activity top` command prints information about the foreground activity, including a "View Hierarchy" section:

```bash
adb shell dumpsys activity top
```

The output can contain a view hierarchy for several activities, for example, the launcher or the Settings app in addition to the app you're testing. Each hierarchy starts with a `DecorView` line followed by the name of the activity in brackets, for example, `[MainActivity]`.

Each line of the view hierarchy contains the class name of a `View`, a summary of its state flags (such as visibility, focusability, and clickability), its bounds, and its resource ID, if any:

```text
android.widget.LinearLayout{5beae27 V.E...... ........ 0,1549-1080,2400 aid=1073741832}
  android.widget.EditText{46a38d4 VFED..CL. ........ 48,32-1032,150 aid=1073741828}
  android.widget.EditText{682fa7d VFED..CL. ........ 48,150-1032,268 aid=1073741829}
```

This method doesn't require root access, instrumentation, or a debuggable build. However, it has the following limitations:

- It only lists `View` objects, so the content of a Jetpack Compose UI appears as a single `AndroidComposeView` without its individual elements.
- The output doesn't include the text or hint of the views, which makes it hard to tell which view handles which data.
- The state flags don't include properties such as accessibility data sensitivity or `filterTouchesWhenObscured`. For example, a protected one-time code field and an unprotected username field have identical state flags.

## Using @MASTG-TOOL-0007

The Layout Inspector in Android Studio shows the live view hierarchy of an app, including Jetpack Compose elements, and lets you inspect the attributes of each view.

The Layout Inspector requires a debuggable build of the app, so it's usually not an option when you test a release build. Use it when the development team provides a debuggable build.

## Using @MASTG-TOOL-0001

You can use @MASTG-TECH-0043 to enumerate the `View` objects of the foreground activity and call any `View` method on them, for example, `isAccessibilityDataSensitive` and `getFilterTouchesWhenObscured`. Because the methods run inside the app, they return the effective values, including values inherited from parent views or implied by other settings.

This method requires instrumentation (see @MASTG-TOOL-0001) and only covers `View` objects. Jetpack Compose elements aren't `View` objects, so they appear as a single `AndroidComposeView`. The values reflect the state at the time of the enumeration, so you need to repeat it on each screen you want to check.

See @MASTG-DEMO-0160 for an example script that enumerates the views of the foreground activity and prints their accessibility data sensitivity.

## Using a Custom Accessibility Service

You can build a test app with an accessibility service that isn't declared as an accessibility tool and log the [`AccessibilityNodeInfo`](https://developer.android.com/reference/android/view/accessibility/AccessibilityNodeInfo) tree that the service receives for each screen. This reflects what any other accessibility service that isn't an accessibility tool, including malware, can access. It's the most direct way to confirm the effect of protections such as accessibility data sensitivity. See @MASTG-DEMO-0x03 for an example.

This method requires building and installing a separate app. You can enable its service in the device's accessibility settings or with @MASTG-TOOL-0004:

```bash
adb shell settings put secure enabled_accessibility_services <package_name>/<service_class_name>
```

On Android 13 (API level 33) and higher, the device's settings may block enabling accessibility services for some sideloaded apps ("restricted settings"). In that case, enable the service with the command above.

## Taking Screenshots

Screenshots only show the pixels on the screen and don't reveal any property of the views. They're still useful to determine which data each screen displays. See @MASTG-TECH-0178 for the available methods.

## Choosing a Method

| Method | Requirements | Source of the data | Shows accessibility data sensitivity |
| --- | --- | --- | --- |
| `uiautomator dump` | @MASTG-TOOL-0004 | Accessibility node tree, not restricted by accessibility data sensitivity | No, and protected views are still included |
| `maestro hierarchy` | @MASTG-TOOL-0154 | Accessibility node tree, not restricted by accessibility data sensitivity | No, and protected views are still included |
| `dumpsys activity top` | @MASTG-TOOL-0004 | Summary of the app's `View` objects, without text | No |
| Layout Inspector | Debuggable build | App's `View` objects and Compose elements | Not usable with release builds |
| View enumeration with @MASTG-TOOL-0001 | Instrumentation | App's `View` objects, using the full `View` API | Yes, the effective value of each view |
| Custom accessibility service | Separate test app with the service enabled | Accessibility node tree as received by a service that isn't an accessibility tool | Indirectly, by showing which views the service can access |
| Screenshots | @MASTG-TECH-0178 | Pixels on the screen | No |

For example, to verify accessibility data sensitivity at runtime (@MASTG-TEST-0404), enumerate the views with @MASTG-TOOL-0001 and use screenshots (@MASTG-TECH-0178), `uiautomator dump`, or `maestro hierarchy` only to identify which views display sensitive data. To confirm what a malicious accessibility service can access, use a custom accessibility service.
