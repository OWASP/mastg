---
platform: android
title: Runtime Accessibility Data Sensitivity of Sensitive Views with Frida
id: MASTG-DEMO-0160
code: [kotlin]
test: MASTG-TEST-0404
---

## Sample

This demo uses the same sample as @MASTG-DEMO-0159. The app creates a login and payment form with views that handle sensitive data, each using a different accessibility data sensitivity configuration:

1. **Username field:** isn't marked as accessibility data sensitive.
2. **Password field:** is explicitly marked as accessibility data sensitive.
3. **One-time code field:** sets `filterTouchesWhenObscured`, which implies accessibility data sensitivity.
4. **Payment container:** a `LinearLayout` that is explicitly marked as accessibility data sensitive using `setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_YES)`. It contains two views:
    - **Card number field:** keeps the default value, `ACCESSIBILITY_DATA_SENSITIVE_AUTO`, so it inherits the protection from the payment container.
    - **Confirm Payment button:** explicitly disables the protection using `setAccessibilityDataSensitive(View.ACCESSIBILITY_DATA_SENSITIVE_NO)`, which overrides the value inherited from the payment container.
5. **Cancel button:** is outside the payment container and keeps the default value, `ACCESSIBILITY_DATA_SENSITIVE_AUTO`. It doesn't handle sensitive data, so it isn't protected.

Marking a view as accessibility data sensitive only restricts accessibility services that aren't declared as accessibility tools. Accessibility tools, such as screen readers, can still read and interact with all of these views (see "Accessibility Tools" in @MASTG-KNOW-0143).

{{ MastgTest.kt }}

## Steps

1. Install the app on a device running Android 14 (API level 34) or higher (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida. The script uses @MASTG-TECH-0043 to hook the relevant API calls.
4. Tap the **Start** button.
5. Use @MASTG-TECH-0177 to obtain the view hierarchy of the screen. The script does this automatically shortly after the app changes the hooked settings. On other screens, call `dumpViews()` from the Frida REPL.
6. In another terminal, use @MASTG-TECH-0178 to take a screenshot of the screen with @MASTG-TOOL-0004:

    ```bash
    adb exec-out screencap -p > screenshot.png
    ```

7. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ run.sh # script.js }}

The Frida script covers both techniques:

- **Hooks (@MASTG-TECH-0043):** it hooks `setAccessibilityDataSensitive` and `setFilterTouchesWhenObscured` and prints the arguments and a backtrace for each call.
- **View hierarchy (@MASTG-TECH-0177):** its `dumpViews()` function enumerates the views of the foreground activity and prints the effective value returned by `isAccessibilityDataSensitive` for each view, which includes the protection inherited from parent views and implied by `filterTouchesWhenObscured`.

## Observation

The output shows the calls to the hooked setters, including their arguments and backtraces, followed by the view hierarchy of the screen with the effective accessibility data sensitivity of each view.

{{ output.txt }}

- The hooks capture four calls from `MastgTest.mastgTest`: `setAccessibilityDataSensitive` with the `YES` mode on the **Password** field (`MastgTest.kt:53`) and on the payment container, a `LinearLayout` (`MastgTest.kt:65`), `setFilterTouchesWhenObscured(true)` on the **One-time code** field (`MastgTest.kt:58`), and `setAccessibilityDataSensitive` with the `NO` mode on the **Confirm Payment** button (`MastgTest.kt:75`).
- The view hierarchy shows the form at the end of the `content` frame. The **Username** field and the **Confirm Payment** button return `isAccessibilityDataSensitive=false`, while the **Password**, **One-time code**, and **Card number** fields return `true`.

The screenshot shows the screen with the form at the bottom:

<img src="Images/Demos/MASTG-DEMO-0160/screenshot.png" width="50%" />

It also shows which views display or accept sensitive data: the **Username**, **Password**, **One-time code**, and **Card number** fields, and the **Confirm Payment** button. The **Cancel** button doesn't handle sensitive data.

## Evaluation

The test case fails because two views that handle sensitive data return `false` from `isAccessibilityDataSensitive`:

- The **Username** field doesn't call any of the hooked setters, and the view hierarchy confirms it isn't protected.
- The **Confirm Payment** button explicitly disables the protection with `setAccessibilityDataSensitive` and the `NO` mode, as shown in the hook output and its backtrace (`MastgTest.kt:75`), even though the payment container is marked as accessibility data sensitive.

The **Password**, **One-time code**, and **Card number** fields return `true`. The card number field doesn't call any of the hooked setters, but the view hierarchy shows that it inherits the protection from the payment container. Only the enumeration of the view hierarchy reveals this inherited protection, and the unprotected username field, because neither involves a call to the hooked setters.

```sh
android.widget.LinearLayout isAccessibilityDataSensitive=true filterTouchesWhenObscured=false
    android.widget.EditText hint="Card number" isAccessibilityDataSensitive=true filterTouchesWhenObscured=false
    android.widget.Button text="Confirm Payment" isAccessibilityDataSensitive=false filterTouchesWhenObscured=false
```

The **Cancel** button also returns `false`, which is expected because it doesn't handle sensitive data.
