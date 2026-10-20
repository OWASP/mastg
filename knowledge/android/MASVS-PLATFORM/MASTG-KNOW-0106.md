---
title: App-Initiated Screenshots and Screen Recording
platform: android
masvs_category: MASVS-PLATFORM
best-practices: [MASTG-BEST-0014, MASTG-BEST-0015, MASTG-BEST-0017, MASTG-BEST-0018]
---
Android apps can capture their own user interface (UI), capture the device display or an app window with user consent, or take screenshots through an enabled accessibility service. The available capture scope and authorization requirements depend on the mechanism.

## Capturing the App's Own Content

Apps can capture their own UI content without requesting screen-capture permission. These mechanisms don't provide general access to another app's screen content.

### Drawing a View to a Canvas

You can capture a view by calling `View.draw` with a [`Canvas`](https://developer.android.com/reference/android/graphics/Canvas) backed by an in-memory [`Bitmap`](https://developer.android.com/reference/android/graphics/Bitmap).

This redraws the view tree onto the bitmap rather than capturing the final screen output. Content rendered into separate surfaces, such as a `SurfaceView`, isn't included in the redraw. Hardware-dependent rendering features may also differ or be unavailable when drawing onto a bitmap-backed software canvas.

> The drawing cache methods `View.setDrawingCacheEnabled` and `View.getDrawingCache` were deprecated in Android 9 (API level 28). Use `View.draw` for software-rendered snapshots or `PixelCopy` to copy rendered window or surface content.

### PixelCopy

[`PixelCopy`](https://developer.android.com/reference/android/view/PixelCopy) copies pixels from a `Window`, `Surface`, or `SurfaceView` into a bitmap. Unlike `View.draw`, it uses the source surface's most recently queued buffer instead of redrawing the view tree.

You can use the corresponding source surface to capture content such as camera previews, video playback, or OpenGL output. The source must contain a rendered frame and support copying. For example, copying from a hardware-protected source can fail.

Call `PixelCopy.request` to start the operation. The listener-based overloads report completion asynchronously through `PixelCopy.OnPixelCopyFinishedListener`.

### Jetpack Compose UI Capture

Jetpack Compose renders composables without representing each UI element as a traditional Android `View`. You can capture Compose content using either of these approaches:

- **Graphics layer:** With Compose 1.7 or later, create a `GraphicsLayer` using `rememberGraphicsLayer`, record the composable's drawing commands using `GraphicsLayer.record`, and convert the recorded content into an `ImageBitmap` using `GraphicsLayer.toImageBitmap`. See [Write contents of a composable to a bitmap](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers#composable-to-bitmap).
- **Window capture:** Use `PixelCopy` to capture the `Window` hosting the Compose UI. You can restrict the capture to the rectangular area containing the desired content.

## Capturing the Device Screen or an App Window

### MediaProjection

[`MediaProjection`](https://developer.android.com/media/grow/media-projection) captures the device display or a user-selected app window with explicit user consent. It routes the captured content through a `VirtualDisplay` to a `Surface` supplied by the app. Components such as `MediaCodec`, `MediaRecorder`, or `ImageReader` consume this output for screenshots, screen recording, or streaming.

To start a capture session:

1. Call [`MediaProjectionManager.createScreenCaptureIntent`](https://developer.android.com/reference/android/media/projection/MediaProjectionManager#createScreenCaptureIntent()) and launch the returned intent to display the system consent dialog.
2. After the user grants consent, start the required foreground service and call `MediaProjectionManager.getMediaProjection` with the result code and data to obtain a `MediaProjection` instance.
3. Call `MediaProjection.createVirtualDisplay` to direct the captured content to your target surface.

Apps targeting Android 10 (API level 29) or later must run a foreground service with the `mediaProjection` service type while obtaining and using the media projection. Apps targeting Android 14 (API level 34) or later must also declare the `FOREGROUND_SERVICE_MEDIA_PROJECTION` permission in addition to `FOREGROUND_SERVICE`.

Android 14 (API level 34) introduced app screen sharing, which lets users select a single app window instead of sharing the entire display.

## Capturing the Screen Through an Accessibility Service

Android 11 (API level 30) introduced screenshot capture for accessibility services. An enabled `AccessibilityService` with screenshot capability can obtain screen images for features such as visual content recognition.

To capture a screenshot through an accessibility service:

1. Define an `AccessibilityService` in your app and set `android:canTakeScreenshot="true"` in its accessibility service XML configuration. This declares the capability represented by `AccessibilityServiceInfo.CAPABILITY_CAN_TAKE_SCREENSHOT`. The user must then enable the service in the device's accessibility settings.
2. Call [`AccessibilityService.takeScreenshot`](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService#takeScreenshot(int,%20java.util.concurrent.Executor,%20android.accessibilityservice.AccessibilityService.TakeScreenshotCallback)) with the target display ID, an executor, and a callback. On success, the callback receives an `AccessibilityService.ScreenshotResult`, which provides a [`HardwareBuffer`](https://developer.android.com/reference/android/hardware/HardwareBuffer) containing the captured image and its color space information. Use `Bitmap.wrapHardwareBuffer` to construct a bitmap for image processing, optical character recognition, or UI analysis.

