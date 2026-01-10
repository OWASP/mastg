---
title: Monitor File System Operations in WebViews
platform: android
---

You can monitor any file system operations in the WebView storage directory using various techniques:

Use @MASTG-TECH-0033 to monitor file system operations in the `/data/data/<app_package>/app_webview/` directory. Regardless of whether the app uses these APIs directly, WebViews may use them internally when rendering content (e.g., JavaScript code using `localStorage`). So tracing calls to APIs such as `open`, `openat`, `opendir`, `unlinkat`, etc., can help identify file operations in the WebView storage directory.

In addition to tracing the method calls, you can also monitor all file operations:

- use @MASTG-TECH-0027 to monitor file operations in that directory, e.g., with `lsof -p <app_pid> | grep /app_webview/`.
- or use @MASTG-TECH-0032, e.g. with `strace -p <app_pid>`, to monitor file operations in that directory.
