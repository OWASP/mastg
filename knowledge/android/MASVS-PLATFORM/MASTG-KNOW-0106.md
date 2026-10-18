---
title: App-Initiated Screenshots and Screen Recording
platform: android
masvs_category: MASVS-PLATFORM
best-practices: [MASTG-BEST-0014, MASTG-BEST-0015, MASTG-BEST-0017, MASTG-BEST-0018]
---
On Android, apps can capture screen content programmatically. An app can capture a single image (a screenshot) or record the screen as a stream (a screen recording).

The mechanism an app uses depends primarily on the required capture scope and the level of user authorization:

- **Capturing the App's Own Content**: The app captures only its own user interface directly in-process. This does not require any special permissions, but access is strictly limited to the app's own content.
- **System-Wide and Cross-App Capture**: The app captures the entire device display, another app's window, or records ongoing display activity as a video stream. Because this crosses app sandbox boundaries, the system requires explicit runtime consent from the user.

## Capturing the App's Own Content

An app can render its own view surfaces into a `Bitmap` object without requesting any permission. This mechanism is limited to the app's own content; it cannot read the pixels of another app.

### Drawing a View to a Canvas

With this approach, the app reconstructs the pixels of the on-screen image hierarchically and converts the result into a `Bitmap` object. The view is redrawn onto the surface by attaching a [`Canvas`](https://developer.android.com/reference/android/graphics/Canvas "android.graphics.Canvas") to the object through the [`View.draw(Canvas)`](https://developer.android.com/reference/android/view/View#draw%28android.graphics.Canvas%29 "View.draw") call.

As a result, this approach does not capture the actual screen output; only the content defined in the app's `View` tree is reconstructed. For this reason, `SurfaceView`, `TextureView`, video output, OpenGL scenes, and GPU-composited hardware-accelerated layers may appear missing or blank (such as a black screen).
> **Legacy note:** Historically, apps used `View.setDrawingCacheEnabled(true)` and `View.getDrawingCache()` to capture views. This mechanism was deprecated in Android 9 (API level 28) in favor of `View.draw(Canvas)` and `PixelCopy`.
### PixelCopy

`android.view.PixelCopy` is a capture mechanism that allows an app to copy the output produced on its own `Window` or `Surface` into a bitmap.

Unlike the `Canvas`-based approach, `PixelCopy` does not redraw the image. Instead, it reads the pixel data from the image and transfers the result into a bitmap.

For this reason, it can also capture content that the `Canvas` approach cannot, such as `SurfaceView`, camera preview, video playback, OpenGL content, and hardware-accelerated layers.

The capture operation is started with `PixelCopy.request(...)`, and the result is reported asynchronously through `PixelCopy.OnPixelCopyFinishedListener`.

## Capturing the Device Screen or an App Window

### MediaProjection

This mechanism works by routing the screen content to a virtual display surface (`VirtualDisplay`) and delivering the image content obtained from that surface to `Surface`-based components such as `MediaCodec`, `MediaRecorder`, or `ImageReader`. This makes operations such as taking a screenshot, recording video, or streaming to a remote device possible.

The `MediaProjection` mechanism begins with the app first requesting screen-capture permission through the [`MediaProjectionManager.createScreenCaptureIntent()`](https://developer.android.com/reference/android/media/projection/MediaProjectionManager "android.media.projection.MediaProjectionManager") call. Following this request, the system sends the user a notification to approve screen sharing. If the user grants permission, the app creates a `MediaProjection` token through the `MediaProjectionManager.getMediaProjection(resultCode, data)` method. This token is then used to call `MediaProjection.createVirtualDisplay()`, which routes the screen content to a virtual display so that it can be captured.

Unlike the `Canvas` and `PixelCopy` approaches, `MediaProjection` has the ability to capture not only the app's own interface but also the content of other apps and system UI components.

## Capturing the Screen Through an Accessibility Service

With Android 11 (API level 30), the ability to take screenshots was added to accessibility services. This feature lets `AccessibilityService`s directly access the interface content displayed on the screen, allowing the user interface to be analyzed more comprehensively. This mechanism provides an especially important function for screen readers and assistive accessibility tools.

To use this feature, an `AccessibilityService` must be defined in the app, and the screenshot permission must be explicitly stated in the service configuration. For this, the `android:canTakeScreenshot="true"` parameter is added to the service definition, and the service declares that it supports the `CAPABILITY_CAN_TAKE_SCREENSHOT` capability. The user then manually enables this service through the device's accessibility settings.

After the service is activated, the screenshot operation is performed through the `takeScreenshot()` method introduced in Android 11. When this method is called, the system captures the current screen content and returns the result as an `AccessibilityService.ScreenshotResult` object. This object contains a [`HardwareBuffer`](https://developer.android.com/reference/android/hardware/HardwareBuffer "android.hardware.HardwareBuffer") holding the screenshot along with color space information. The pixel data can be accessed through the obtained buffer to perform operations such as image processing, OCR, or user interface analysis.

Overall, this process consists of the following steps: configuring the service with the required permissions, having it enabled by the user, calling the `takeScreenshot()` method, and processing the visual data returned by the system through the `HardwareBuffer`. In this way, accessibility services gain access to a data source through which they can programmatically analyze the content on an app's screen.
