---
title: Inspecting Android WebView Storage
platform: android
---

Android applications that embed WebView components may persist
application-specific data such as cookies, localStorage, IndexedDB,
and cached content on the device. Improper handling of this data can
lead to exposure of sensitive information.

Inspecting WebView storage helps determine whether authentication
tokens, session identifiers, or other sensitive data are stored
insecurely or persist longer than intended.

### WebView Storage Location

Android WebView stores data under the application’s private data
directory:

`/data/data/<app_package>/app_webview/`

Access to this directory typically requires a rooted device, emulator,
or a debug build with appropriate permissions.


### Using adb

1. Connect a device or emulator with debugging enabled.
2. Obtain a shell using `adb shell`.
3. Navigate to the application data directory:

`cd /data/data/<app_package>/app_webview/`

4. Inspect files and subdirectories to identify persisted WebView data.

### Using @MASTG-TOOL-0006

1. Attach objection to the running application.
2. Use objection commands to explore the application’s private storage.
3. Inspect the WebView storage directory for persisted data.

### Using Android Studio

1. Open Android Studio and connect a device or emulator.
2. Open **View → Tool Windows → Device File Explorer**.
3. Navigate to `/data/data/<app_package>/app_webview/`.
4. Inspect the stored WebView data.

During inspection, testers should verify that sensitive information is
not stored in cleartext and that WebView data is properly cleared when
no longer required.
