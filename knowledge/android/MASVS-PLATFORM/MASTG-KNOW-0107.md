---
title: Screenshots and Screen Recording Detection
platform: android
masvs_category: MASVS-PLATFORM
---
Android applications can detect when their screen contents are captured through various mechanisms. These mechanisms notify the application when a capture operation, such as a screenshot or screen recording, takes place. Depending on how the capture is performed, different APIs and detection methods are used.

The platform provides specific callbacks that notify the application when a screenshot is taken or a screen recording starts. Besides this, applications can also indirectly detect the screen capture process by monitoring system resources such as MediaStore or active displays. However, each of these mechanisms covers only specific capture methods. In other words, there is no single mechanism that can detect all the ways screen content can be captured on its own.

## Screenshot Detection

### Screen Capture Callback

With Android 14 (API level 34), a specific mechanism was introduced that notifies the application when the user takes a screenshot of a visible activity of the application. To use this feature, the application must declare the android.permission.DETECT_SCREEN_CAPTURE install-time permission and register an [Activity.ScreenCaptureCallback](https://developer.android.com/reference/android/app/Activity.ScreenCaptureCallback "android.app.Activity.ScreenCaptureCallback").

The callback operates on a per-activity basis. Therefore, the callback is usually registered by calling registerScreenCaptureCallback() within onStart(), and unregistered by calling unregisterScreenCaptureCallback() within onStop(). When the user takes a screenshot while the activity where the callback is registered is visible, the system triggers the onScreenCaptured() method.

This mechanism only reports that a screenshot was taken; it does not provide the captured image itself to the application. Therefore, the application must determine from its own state what content was being displayed at the moment the screenshot was taken.

ScreenCaptureCallback only reports screenshots taken by the user using the device's standard screenshot mechanism (for example, a hardware key combination). It does not report screenshots taken via adb or screenshots generated with other tools that capture the current screen content. Additionally, since the callback operates on a per-activity basis, only screenshots taken while the registered activity is visible can be detected.

### Monitoring the Media Store

An application can indirectly detect that a screenshot has been taken by monitoring new images added to shared storage. For this, a [ContentObserver](https://developer.android.com/reference/android/database/ContentObserver "android.database.ContentObserver") is registered to the [MediaStore](https://developer.android.com/reference/android/provider/MediaStore "android.provider.MediaStore") image collection, or a [FileObserver](https://developer.android.com/reference/android/os/FileObserver "android.os.FileObserver") is registered to the directory where screenshots are saved. When a new image file is created while the application is running, it becomes aware of this change and tries to determine whether it is a screenshot by examining the display name or relative path of the file. This determination is generally made through heuristic methods based on the presence of phrases like "Screenshot" in the file name or directory path.

For this approach to work, media reading permissions such as READ_MEDIA_IMAGES are required. Furthermore, only screenshots saved to storage can be detected; captures that are not written to disk cannot be detected with this method. Since the detection process relies on file name and directory structure, the accuracy of the heuristic methods used may vary depending on how the device manufacturer names screenshots and in which directory they store them.

## Screen Recording Detection

### Screen Recording Callback

With Android 15 (API level 35), a specific mechanism was introduced that allows the application to detect that it is included in a screen recording. To use this feature, the application must declare the android.permission.DETECT_SCREEN_RECORDING install-time permission. Afterwards, a callback is registered with [WindowManager](https://developer.android.com/reference/android/view/WindowManager "android.view.WindowManager").addScreenRecordingCallback() and removed with WindowManager.removeScreenRecordingCallback() when no longer needed.
The callback reports the current screen recording state via a Consumer<Integer>. The state value indicates whether the application's screen is visible in an active screen recording and is transmitted as WindowManager.SCREEN_RECORDING_STATE_VISIBLE or WindowManager.SCREEN_RECORDING_STATE_NOT_VISIBLE.

This callback is triggered when the application's screen is included in the screen recording or exits the recording. Additionally, when addScreenRecordingCallback() is called, the current recording state is also returned as the initial value. Since the callback only operates on state changes, a new event is not generated for a screen recording that had already started before the callback was registered. In this case, the application only receives the state information returned initially.

This mechanism can only detect screen recordings performed using the [MediaProjection](https://developer.android.com/reference/android/media/projection/MediaProjection "android.media.projection.MediaProjection") API. Therefore, screen recordings taken by another application via MediaProjection can also be detected. Conversely, capture methods that do not use the MediaProjection infrastructure, such as screen recordings started with adb, are not reported by this callback.

### Monitoring Active Displays

An application can indirectly detect operations like screen mirroring, casting, or screen recording by monitoring the active displays on the device via the [DisplayManager](https://developer.android.com/reference/android/hardware/display/DisplayManager "android.hardware.display.DisplayManager"). The presence of an additional display apart from the built-in screen might indicate that the screen content is being routed to another destination. This display could be, for example, a virtual display created by screen mirroring or MediaProjection.
However, this method alone does not prove that a screen recording is taking place. It merely reports that an additional display is present; it cannot determine for what purpose this display was created or by which application it is being used.
