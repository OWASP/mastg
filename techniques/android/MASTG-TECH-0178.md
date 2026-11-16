---
title: Taking Screenshots
platform: android
---

Screenshots show what an app displays on a screen at a given moment. They're useful as evidence and to determine which data a screen displays, for example, during manual validation. However, they only capture pixels and don't reveal any property of the UI elements, such as whether a view is marked as accessibility data sensitive. To inspect those properties, see @MASTG-TECH-0177.

Windows that use [`FLAG_SECURE`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_SECURE) appear black in screenshots taken with these methods.

## Using @MASTG-TOOL-0004

Use the `screencap` command to capture the screen and write the PNG image directly to your computer:

```bash
adb exec-out screencap -p > screenshot.png
```

## Using @MASTG-TOOL-0155

Use the `screen capture` command:

```bash
android screen capture --output=screenshot.png
```

Add `--annotate` to draw labeled bounding boxes around the UI elements detected on the screen, which helps you refer to specific elements when you document the screen.

## Using @MASTG-TOOL-0154

Use the `takeScreenshot` command in a flow to capture the screen at a specific point, for example, after the flow has navigated to a screen that handles sensitive data:

```yaml
appId: org.owasp.mastestapp
---
- tapOn: "Start"
- takeScreenshot: payment-form
```

This is useful when you need screenshots of several screens or want to capture the same screens repeatedly.
