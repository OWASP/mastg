---
title: Attach to WKWebView
platform: ios
---

## Safari Web Inspector

Enabling the [Safari Web Inspector](https://developer.apple.com/library/archive/documentation/AppleApplications/Conceptual/Safari_Developer_Guide/GettingStarted/GettingStarted.html) on iOS allows you to remotely [inspect the contents of a WebView from a macOS device](https://developer.apple.com/documentation/safari-developer-tools/inspecting-ios). This is particularly useful in apps that expose native APIs using a JavaScript bridge, such as hybrid apps.

Starting with iOS 16.4, apps must explicitly opt in to inspection for `WKWebView` content by setting [`isInspectable`](https://developer.apple.com/documentation/webkit/wkwebview/isinspectable) to `true`:

```swift
let webView = WKWebView()
...
if #available(iOS 16.4, *) {
    webView.isInspectable = true
}
```

Apps installed from the App Store can still be inspected if the app enables `WKWebView.isInspectable = true`. On jailbroken devices, you can use @MASTG-TOOL-0137, to force enable WebView inspection for apps that do not opt in themselves. After installing it, Safari Web Inspector can attach to `WKWebView` (@MASTG-KNOW-0076) instances in those apps.

To activate web inspection, follow these steps:

1. On the iOS device, open the Settings app: go to **Safari** -> **Advanced** and toggle on **Web Inspector**.
2. On the macOS device, open Safari: in the menu bar, go to **Safari** -> **Settings** -> **Advanced** and enable **Show features for web developers**.
3. Connect your iOS device to the macOS device and unlock it: the iOS device name should appear in the **Develop** menu.
4. If needed, in macOS Safari go to **Develop** -> **'iOS device name'** -> **Use for Development** and trust the device.

To open Web Inspector and debug a WebView:

1. On iOS, open the app and navigate to any screen containing a WebView.
2. In macOS Safari, go to **Develop** -> **'iOS Device Name'**, and you should see the name of the WebView based context. Click it to open Web Inspector.

Now you're able to debug the WebView as you would with a regular web page in your desktop browser.

<img src="Images/Tools/TOOL-0137-safari-dev.png" width="400px"/>

If everything is set up correctly, you can attach to any WebView with Safari:

<img src="Images/Tools/TOOL-0137-attach-webview.png" width="400px"/>

<img src="Images/Tools/TOOL-0137-web-inspector.png" width="400px"/>
