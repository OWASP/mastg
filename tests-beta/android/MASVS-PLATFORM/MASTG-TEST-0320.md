---
platform: android
title: WebViews Not Cleaning Up Sensitive Data
id: MASTG-TEST-0320
type: [dynamic]
weakness: MASWE-0118
profiles: [L1, L2]
best-practices: [MASTG-BEST-0028]
knowledge: [MASTG-KNOW-0018]
prerequisites:
- identify-sensitive-data
---

## Overview

This test verifies whether the app cleans up sensitive data used by WebViews. Apps can enable several specific storage areas in their WebViews and not clean them up properly, leading to sensitive data being stored on the device longer than necessary. For example:

- Not calling [`WebView.clearCache(includeDiskFiles = true)`](https://developer.android.com/reference/android/webkit/WebView#clearCache(boolean)) when:
    - `WebSettings.setAppCacheEnabled()` is enabled,
    - or [`WebSettings.setCacheMode()`](https://developer.android.com/reference/android/webkit/WebSettings#setCacheMode(int)) is any value other than [`WebSettings.LOAD_NO_CACHE`](https://developer.android.com/reference/kotlin/android/webkit/WebSettings#LOAD_NO_CACHE:kotlin.Int).
- Not calling [`WebStorage.deleteAllData()`](https://developer.android.com/reference/android/webkit/WebStorage#deleteAllData()) when:
    - [`WebSettings.setDomStorageEnabled`](https://developer.android.com/reference/android/webkit/WebSettings#setDomStorageEnabled(boolean)) is enabled.
- Not calling [`WebStorage.deleteAllData()`](https://developer.android.com/reference/android/webkit/WebStorage#deleteAllData()) when:
    - [`WebSettings.setDatabaseEnabled()`](https://developer.android.com/reference/android/webkit/WebSettings#setDatabaseEnabled(boolean)) is enabled.
- Not calling [`CookieManager.removeAllCookies(ValueCallback<Boolean> ...)`](https://developer.android.com/reference/android/webkit/CookieManager#removeAllCookies(android.webkit.ValueCallback%3Cjava.lang.Boolean%3E)) when:
    - [`CookieManager.setAcceptCookie()`](https://developer.android.com/reference/android/webkit/CookieManager#setAcceptCookie(boolean)) is not explicitly set to `false` (default is set to `true`).

This test uses dynamic analysis to monitor the relevant API calls and file system operations. Regardless of whether the app uses these APIs directly, WebViews may use them internally when rendering content (e.g., JavaScript code using `localStorage`). So tracing calls to APIs such as `open`, `openat`, `opendir`, `unlinkat`, etc., can help identify file operations in the WebView storage directory.

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Use @MASTG-TECH-0033 to target WebView APIs for storage enablement and cleanup.
3. Open the app.
4. Use the app extensively to ensure that all relevant WebViews are covered and that sensitive data is loaded into them. Ensure you keep a list of the sensitive data you expect to be cleaned up.
5. Close the app.
6. Use @MASTG-TECH-0002 to pull the contents of the `/data/data/<app_package>/app_webview/` directory or simply search for the sensitive data used in the WebView within that directory.

## Observation

The output should include:

1. The list of WebView storage enablement APIs used.
2. The list of WebView storage cleanup APIs used.
3. The list of sensitive data expected to be cleaned up.
4. The result of searching the contents of the `/data/data/<app_package>/app_webview/` directory for the sensitive data used in the WebView after closing the app.

## Evaluation

The test case fails if the app still has sensitive data on the `/data/data/<app_package>/app_webview/` directory after the app is closed. This could be due to the app not calling the relevant cleanup APIs after using the WebView.

The test passes if all sensitive data used by the WebView is properly cleaned up using the relevant APIs, and no sensitive data remains in the `/data/data/<app_package>/app_webview/` directory after closing the app.

!!! note
    It can be challenging to determine whether the right cleanup APIs were called for the enabled storage areas. @MASTG-KNOW-0018 describes the storage areas used by WebViews and the challenges of evaluating their cleanup.

**Additional Guidance**:

If you need more introspection during runtime, you can rerun the test with additional tracing of file system operations in the WebView storage directory. See @MASTG-TECH-0143.
