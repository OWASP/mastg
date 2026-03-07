---
title: Inspecting WebView Storage
platform: android
---

You can inspect WebView storage either directly on the device file system or using Chrome DevTools. @MASTG-KNOW-0018 describes the different storage areas and what to expect in each.

## Direct File System Inspection

Android WebView stores its data in the `/data/data/<app_package>/app_webview/` directory. You can access and retrieve the contents of this directory using @MASTG-TECH-0002:

- **Using @MASTG-TOOL-0004**: Pull the directory directly with `adb pull`:

    ```bash
    adb pull /data/data/<app_package>/app_webview/ ./app_webview/
    ```

    You can then inspect the contents locally. To search for specific data without pulling the directory, use `adb shell`:

    ```bash
    adb shell grep -r "<sensitive_data>" /data/data/<app_package>/app_webview/
    ```

    !!! note
        This requires either a rooted device or a debuggable app. On a non-rooted device with a non-debuggable app, `adb` won't have access to the app's private data directory.

- **Using @MASTG-TOOL-0007**: Open the Device File Explorer in Android Studio via **View** -> **Tool Windows** -> **Device File Explorer** and navigate to `/data/data/<app_package>/app_webview/`. You can then browse and download files directly from the UI.

    !!! note
        This requires either a rooted device or a debuggable app.

- **Using @MASTG-TOOL-0038**: Connect to the app with objection and navigate to the `app_webview` directory to list and download files:

    ```bash
    objection -g <app_package> explore
    ```

    ```bash
    # Navigate to the app_webview directory
    cd app_webview

    # List contents
    ls

    # Download a specific file
    filesystem download <file_name>

    # Download the entire directory
    filesystem download app_webview ./app_webview --folder
    ```

    This approach works without requiring a rooted device and without setting the app as debuggable.

## Using Chrome DevTools

To inspect WebView storage interactively, first follow the steps in the official ["Remote debugging WebViews" documentation](https://developer.chrome.com/docs/devtools/remote-debugging/webviews) to connect Chrome DevTools to the WebView of your app.

With the DevTools open, navigate to the **Application** tab. In the **Storage** section, you can inspect the different storage areas of the WebView.
