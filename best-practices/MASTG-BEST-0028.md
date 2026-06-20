---
title: WebViews Cache Cleanup
alias: android-webviews-cache-cleanup
id: MASTG-BEST-0028
platform: android
---

Android WebViews cache data when the server responds with specific `Cache-Control` headers that instruct the browser to cache the content. If a WebView processes sensitive data, you should ensure that no residual information remains on the device (disk and/or RAM) once the WebView is no longer required.

Prefer server-side cache prevention by using headers such as `Cache-Control: no-cache` in API responses that contain sensitive data to instruct the webview not to cache.

If server-side control is not possible or as a supplementary control, explicitly set [`WebSettings.setCacheMode()`](https://developer.android.com/reference/android/webkit/WebSettings#setCacheMode(int)) with [`WebSettings.LOAD_NO_CACHE`](https://developer.android.com/reference/kotlin/android/webkit/WebSettings#LOAD_NO_CACHE:kotlin.Int) or clear the WebView cache with [`WebView.clearCache(includeDiskFiles = true)`](https://developer.android.com/reference/android/webkit/WebView#clearCache(boolean)) after WebView use (such as on WebView Activity's `onDestroy` lifecycle call) to reduce this risk. However, this method comes with two disadvantages:

1. The first disadvantage is indiscriminately deleting all cached data, including non-sensitive items that actually benefit from the cache, such as bigger files like images.
2. The second disadvantage is the lack of a guarantee that the clear method will always be called, particularly if the app process is killed abruptly. In this case, evaluation of prior cache clearing and active clearing would be required, such as at the next app start.

@MASTG-KNOW-0018 describes the different storage areas used by WebViews.
