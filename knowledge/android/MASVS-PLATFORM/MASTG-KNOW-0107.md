---
title: Screenshots and Screen Recording Detection
platform: android
masvs_category: MASVS-PLATFORM
---

Screen recording detection lets an app become aware when its screen is being captured or recorded.

## Screenshot Detection

Starting on Android 14 (API Level 34), apps can [detect when a screenshot is taken](https://developer.android.com/about/versions/14/features/screenshot-detection). To implement screenshot detection the app must:

- Declare the install-time permission [`android.permission.DETECT_SCREEN_CAPTURE`](https://developer.android.com/reference/android/Manifest.permission#DETECT_SCREEN_CAPTURE).
- In every activity where screen capture detection is needed, apps can implement the `Activity.ScreenCaptureCallback` interface, override `onScreenCaptured()`, then register and unregister the callback with `registerScreenCaptureCallback(...)` and `unregisterScreenCaptureCallback(...)` in `onStart()` and `onStop()` respectively.

**NOTE:** The API only detects screenshots via hardware key combinations; screenshots initiated via ADB or automation are not detected (see ["Use Cases"](https://developer.android.com/about/versions/14/features/screenshot-detection#use-cases)).

## Screen Recording Detection

Starting on Android 15 (API Level 35), apps can [detect when screen recording is active](https://developer.android.com/about/versions/15/features#screen-recording-detection). To implement screen recording detection the app must:

- Declare the install-time permission [`android.permission.DETECT_SCREEN_RECORDING`](https://developer.android.com/reference/android/Manifest.permission#DETECT_SCREEN_RECORDING) in the manifest.
- Use the [`addScreenRecordingCallback(...)`](https://developer.android.com/reference/android/view/WindowManager#addScreenRecordingCallback(java.util.concurrent.Executor,%20java.util.function.Consumer%3Cjava.lang.Integer%3E)) and [`removeScreenRecordingCallback(...)`](https://developer.android.com/reference/android/view/WindowManager#removeScreenRecordingCallback(java.util.function.Consumer%3Cjava.lang.Integer%3E)) methods on the `Activity` to monitor recording state changes. Callback provides `SCREEN_RECORDING_STATE_VISIBLE` when the app is visible in the recording and `SCREEN_RECORDING_STATE_NOT_VISIBLE` when not.

## Pre-Permissions Detection

Before these official APIs were available, [older security guidance](https://wwws.nightwatchcybersecurity.com/2016/04/13/research-securing-android-applications-from-screen-capture/) recommended `FLAG_SECURE` and related mitigations rather than detection, since no stable detection API existed for third party apps.

Some of those workarounds were:

- **MediaProjection inspection**: call `MediaProjectionManager.getActiveProjectionInfo()` to check if a screen-capture session is active. A non-null return indicates active recording/capture. This API is [hidden](https://android.googlesource.com/platform/frameworks/base/+/9e8d9ac/media/java/android/media/projection/MediaProjectionManager.java#110) (`@hide`), so not officially supported.
- **Monitoring MediaProjection sessions**: internal methods like `MediaProjectionManager.addCallback(...)` and `removeCallback(...)` exist but require `MANAGE_MEDIA_PROJECTION` permission and are intended for system-level use.

These workarounds lacked reliable callbacks, depended on hidden APIs or OEM behavior, and were not guaranteed to work across devices or Android versions, which is why the new permissions based detection APIs were introduced.

## Security Considerations

In contrast to screen recording prevention, which actively blocks screen recording attempts, screen recording detection is not a protection mechanism but can be used to let the user know that their actions are being recorded, or to disable certain features while recording is active, e.g. in-app notifications for privacy reasons. See @MASTG-BEST-0014 for best practices on preventing screenshots and screen recording.
