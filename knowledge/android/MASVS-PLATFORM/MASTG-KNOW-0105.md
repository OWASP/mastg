---
title: User-Initiated Screenshots and Screen Recording
platform: android
masvs_category: MASVS-PLATFORM
---
On Android, the content displayed on the screen can be captured through several user-initiated mechanisms, independently of the app. This page describes those capture flows and the secure flags that determine whether the content appears in the captured output during these flows.
 
A display secure flag is a system setting that determines whether the content displayed in a particular window or surface is included in capture operations such as screenshots, screen recording, or screen sharing.
 
Screenshots can be taken through [hardware-based shortcuts or system-level screen-capture mechanisms](https://support.google.com/android/answer/9075928 "Take a screenshot or record your screen on your Android device"), independently of the app in use. On Android 11 (API level 30) and later, the platform provides a built-in screen recorder that the user can start from the Quick Settings menu. This feature records the screen as a video, optionally including system audio.
 
The Android display system consists of multiple layers, and these layers can be marked as secure. When a layer is marked as secure, the system excludes its content from the output during capture operations such as screenshots, screen recording, or screen sharing. In that case, the content may appear black or blank, or the surface may be omitted from the capture entirely. The available secure mechanisms differ in the layer at which they apply and in the capture methods they affect.
 
Android provides several mechanisms that offer different levels of protection for this purpose. `FLAG_SECURE` applies protection at the window level, while `SurfaceView.setSecure()` protects only the content of a specific SurfaceView at the surface level. In Jetpack Compose, `SecureFlagPolicy` provides a similar protection mechanism for Compose components and dialogs. In addition, the `setRecentsScreenshotEnabled()` method introduced in Android 13 (API level 33) controls whether the preview image shown for the app on the Recents screen is generated.
 
### FLAG_SECURE
 
[`FLAG_SECURE`](https://developer.android.com/reference/android/view/WindowManager.LayoutParams#FLAG_SECURE "WindowManager.LayoutParams.FLAG_SECURE") is a `WindowManager.LayoutParams` flag that applies at the window level. It is applied to the window of an Activity or a Dialog. When the flag is enabled, the system treats the entire content of the window as secure and excludes it from capture operations such as screenshots, screen recording, or screen sharing.
 
### SurfaceView.setSecure
 
[`SurfaceView.setSecure(true)`](https://developer.android.com/reference/android/view/SurfaceView#setSecure%28boolean%29 "SurfaceView.setSecure") marks the content of a specific `SurfaceView` as secure, providing protection at the surface level. This mechanism works independently of the containing window's security settings and is typically used to protect data rendered in dedicated surfaces, such as hardware-accelerated video playback or decoded video content.
 
### Compose SecureFlagPolicy
 
In Jetpack Compose, components that create their own windows, such as `Dialog` and `Popup`, support the [`SecureFlagPolicy`](https://developer.android.com/reference/kotlin/androidx/compose/ui/window/SecureFlagPolicy "SecureFlagPolicy") configuration (`SecureOn`, `SecureOff`, or `Inherit`). This policy determines whether `FLAG_SECURE` is applied to the corresponding window. The `Inherit` option inherits the security settings of the parent window. See @MASTG-TEST-0294 and @MASTG-BEST-0018.
 
### setRecentsScreenshotEnabled
 
Available since Android 13 (API level 33), the [`Activity.setRecentsScreenshotEnabled(boolean)`](https://developer.android.com/reference/android/app/Activity#setRecentsScreenshotEnabled%28boolean%29 "Activity.setRecentsScreenshotEnabled") method controls whether the system generates the activity preview image (thumbnail) shown on the Recents screen. When this feature is disabled, a generic placeholder is shown on the Recents screen instead of the activity's content.
 
The scope of this method is narrower than that of `FLAG_SECURE`. It affects only the preview image on the Recents screen; it does not block screenshots, screen recordings, or screen-sharing scenarios.
 
This API replaces the [`Activity.setDisablePreviewScreenshots(boolean)`](https://developer.android.com/reference/android/app/Activity#setDisablePreviewScreenshots%28boolean%29 "Activity.setDisablePreviewScreenshots") method, which was introduced in Android 9 (API level 28) and deprecated in Android 13. The two methods use inverted boolean logic. See @MASTG-TEST-0292.
