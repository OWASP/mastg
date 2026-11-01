---
platform: android
title: Runtime Jetpack Compose Accessibility Data Sensitivity with Frida
id: MASTG-DEMO-0x02
code: [kotlin]
test: MASTG-TEST-0404
---

## Sample

This demo uses the payment dialog from @MASTG-DEMO-0x01. The username field explicitly sets `isSensitiveData` to `false`, while the password field and the **Confirm Payment** button don't set it. All three handle sensitive data. The card number field sets `isSensitiveData` to `true`. The **Cancel** button doesn't handle sensitive data.

The username field also uses `sensitiveContent` for screen-sharing protection, which is separate from accessibility data sensitivity. We won't consider it further in this demo.

{{ MainActivity.kt }}

## Steps

The following steps were tested on a Pixel 5a running Android 14 (API level 34).

1. Build and install the app with the sample `MainActivity.kt` (@MASTG-TECH-0005).
2. Make sure @MASTG-TOOL-0001 is installed on your machine and frida-server is running on the device.
3. From the demo directory, run `./run.sh` to spawn the app with Frida. The script hooks `SemanticsPropertiesKt.setSensitiveData` (@MASTG-TECH-0043).
4. Tap **Start** to open the payment dialog. The hook schedules a dump of the focused window's merged Compose semantics tree.
5. To inspect another screen, open it and call `dumpComposeViews()` in the Frida REPL. Close the keyboard first if it has taken window focus.
6. Compare the output with the screen or a screenshot (@MASTG-TECH-0178). Stop Frida with `Ctrl+C` or `q`.

{{ run.sh # script.js }}

The script enumerates attached `AndroidComposeView` instances, including the dialog's separate window, and walks the focused root's merged semantics tree. It reports `isSensitiveData=null` when the merged configuration doesn't contain the property, and prints labels, content descriptions, and markers for editable, password, and clickable nodes. It doesn't print the `EditableText` or `InputText` properties. Labels and content descriptions may still contain sensitive data, so use dummy data and protect the output.

The dump describes Compose semantics, not ordinary Android `View` objects or the value returned by `View.isAccessibilityDataSensitive`. It doesn't resolve sensitivity inherited from a parent or a containing Android view, and it doesn't prove what a particular accessibility service receives. The automatic dump only runs after a hooked setter call; use the manual command for screens that don't call the setter. The script relies on Compose class and method names, which can differ between library versions or be obfuscated in release builds.

## Observation

The hook reports calls to `setSensitiveData(false)` for the username field and `setSensitiveData(true)` for the card number field, with backtraces pointing to their respective source locations. The dump shows `isSensitiveData=false` for the username node and `isSensitiveData=true` for the card number node. The password, confirmation, and cancel nodes have `isSensitiveData=null`, indicating that the property isn't set in their merged configurations.

{{ output.txt }}

The password node also has the `Password` marker. The marker indicates password semantics, not the `isSensitiveData` setting.

The screenshot from @MASTG-DEMO-0x01 shows the same payment dialog:

<img src="demo-compose.png" width="50%" />

## Evaluation

The test case fails because the sample's username and password fields and **Confirm Payment** button handle sensitive data but aren't marked as sensitive with `isSensitiveData`. The username field explicitly sets the property to `false`; the password field and confirmation button don't set it. The card number field is marked sensitive, while the **Cancel** button doesn't need this protection.

The dump alone doesn't establish that a node with an unset property is exposed: investigate its ancestors and any containing Android views, and compare the result with what a non-tool accessibility service can access. In this sample, the containing `AndroidComposeView` and its Android view ancestors returned `false` from `isAccessibilityDataSensitive` during testing.
