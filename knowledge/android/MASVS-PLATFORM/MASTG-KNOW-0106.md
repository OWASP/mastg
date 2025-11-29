---
title: App-Initiated Screenshots and Screen Recording
platform: android
masvs_category: MASVS-PLATFORM
---

Another app can capture your screen by starting a [MediaProjection](https://developer.android.com/media/grow/media-projection) session. It requests user consent with [`MediaProjectionManager.createScreenCaptureIntent`](https://developer.android.com/reference/android/media/projection/MediaProjectionManager), the user approves in a system dialog, then the app creates a `VirtualDisplay` and records frames from the device screen or a selected app window. On Android 14 and newer, the system supports app screen sharing so the user can choose a single window, and for apps targeting API 34 or higher, [consent is required for each capture session](https://developer.android.com/about/versions/14/behavior-changes-14#media-projection-consent).

The capturing app must run a foreground service of type media projection, declared in the manifest and started with `startForeground`, or the system throws an error. This requirement is enforced on modern Android versions and is documented in the media projection guide and foreground service changes.

Your app's windows are capturable unless you mark them as "secure". Setting `FLAG_SECURE`, including via `SecureFlagPolicy.SecureOn` in Compose, causes your content to be blanked in screenshots, screen recordings, and media projection or casting outputs. This protection is per window.
