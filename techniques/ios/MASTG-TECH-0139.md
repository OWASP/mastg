---
title: Attach to WKWebView
platform: ios
---

## Safari Web Inspector

Enabling the [Safari Web Inspector](https://developer.apple.com/library/archive/documentation/AppleApplications/Conceptual/Safari_Developer_Guide/GettingStarted/GettingStarted.html) on iOS allows you to remotely [inspect the contents of a WebView from a macOS device](https://developer.apple.com/documentation/safari-developer-tools/inspecting-ios). This is particularly useful in apps that expose native APIs using a JavaScript bridge, such as hybrid apps.

The Safari Web Inspector requires apps to have the `get-task-allowed` entitlement. The Safari app has this entitlement by default, so you can view the contents of any page loaded into it. If you're developing an app, you can add the following to enable inspection:

```swift
let webView = WKWebView()
...
if #available(iOS 16.4, *) {
    webView.isInspectable = true
}
```

However, apps installed from the App Store will not have this entitlement and cannot be attached. On jailbroken devices, you can add this entitlement to any app by installing @MASTG-TOOL-0137. After installing that, it is possible to attach the Safari developer tools to any `WKWebView` (@MASTG-KNOW-0076) inside of apps installed from the App Store.

To activate the web inspection, follow these steps:

1. On the iOS device, open the Settings app: Go to **Safari** -> **Advanced** and toggle on _Web Inspector_.
2. On the macOS device, open Safari: in the menu bar, go to **Safari** -> **Preferences** -> **Advanced** and enable _Show Develop menu in menu bar_.
3. Connect your iOS device to the macOS device and unlock it: the iOS device name should appear in the **Develop** menu.
4. (If not yet trusted) On macOS's Safari, go to the **Develop** menu, click on the **'iOS device name'** -> **Use for Development** and enable trust.

To open the web inspector and debug a WebView:

1. In iOS, open the app and navigate to any screen containing a WebView.
2. In macOS Safari, go to **Developer** -> **'iOS Device Name'**, and you should see the name of the WebView-based context. Click on it to open the Web Inspector.

Now you're able to debug the WebView as you would with a regular web page on your desktop browser.

<img src="Images/Tools/TOOL-0137-safari-dev.png" width="400px"/>

If everything is set up correctly, you can attach to any WebView with Safari:

<img src="Images/Tools/TOOL-0137-attach-webview.png" width="400px"/>

<img src="Images/Tools/TOOL-0137-web-inspector.png" width="400px"/>
