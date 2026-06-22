---
title: Render Sensitive UI as Native Views Over the WebView
alias: render-sensitive-ui-as-native-views-over-webview
id: MASTG-BEST-0059
platform: ios
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0139]
---

When a `WKWebView` needs to present sensitive UI, such as a credential picker, autofill suggestion, or payment confirmation, rendering that interface as HTML elements inside the WebView exposes it to any JavaScript running on the page. An attacker who can execute JavaScript in the WebView (for example, through XSS or content injection) can read, modify, or visually spoof those elements.

The safer approach is to display sensitive information as a native element over the WebView. By using native components, the data remains entirely outside the DOM and is not accessible to JavaScript.

## Overlaying Native Views on DOM Elements

The app can retrieve the coordinates of a specific HTML element and display a native view directly over this element specifically. This allows the app to maintain the look and feel of the web page while keeping the actual data secure in the native layer.

To retrieve the coordinates safely, an isolated [`WKContentWorld`](https://developer.apple.com/documentation/webkit/wkcontentworld) can be used. Registering the script that reads DOM geometry in a custom world, separate from the page world, ensures that page JavaScript cannot override or intercept the script. For more details on using worlds for script isolation, see @MASTG-BEST-0061.
