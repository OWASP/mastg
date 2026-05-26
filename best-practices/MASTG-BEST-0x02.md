---
title: Render Sensitive UI as Native Views Over the WebView
alias: render-sensitive-ui-as-native-views-over-webview
id: MASTG-BEST-0x02
platform: ios
knowledge: [MASTG-KNOW-0076]
---

When a `WKWebView` needs to present sensitive UI, such as a credential picker, autofill suggestion, or payment confirmation, rendering that interface as HTML elements inside the WebView exposes it to any JavaScript running on the page. An attacker who can execute JavaScript in the WebView (for example, through XSS or content injection) can read, modify, or visually spoof those elements.

The safer approach is to read only layout information from the DOM using an isolated [`WKContentWorld`](https://developer.apple.com/documentation/webkit/wkcontentworld) script, pass those coordinates to native code, and render a UIKit or SwiftUI view directly over the WebView at the computed position. The sensitive UI never enters the DOM and remains completely outside the reach of page JavaScript. This is the pattern used by iOS password managers and Safari's autofill.

## Isolate the Coordinate-Reading Script

Register the script that reads DOM geometry in a custom content world, separate from the `.page` world. Each content world maintains its own JavaScript global scope and prototype chain. Page JavaScript cannot override or intercept scripts in a different world, even through prototype poisoning such as overriding `EventTarget.prototype.addEventListener` before your script runs.

```swift
let appWorld = WKContentWorld.world(withName: "AppWorld")

webView.evaluateJavaScript("""
    const el = document.querySelector('input[type="password"]');
    if (!el) return null;
    const r = el.getBoundingClientRect();
    return { x: r.left, y: r.top, width: r.width, height: r.height };
""", in: nil, in: appWorld) { result, error in
    guard case .success(let value) = result,
          let rect = value as? [String: Double] else { return }
    showNativeOverlay(at: rect)
}
```

The script passes only coordinates to native. No sensitive data crosses through the DOM.

## Render a Native View at the Coordinates

Use the received coordinates to position a UIKit or SwiftUI view on top of the WebView. Because this view lives outside the WebView's rendering context entirely, page JavaScript cannot read, alter, or spoof it.

```swift
func showNativeOverlay(at rect: [String: Double]) {
    let frame = CGRect(
        x: rect["x"] ?? 0,
        y: rect["y"] ?? 0,
        width: rect["width"] ?? 200,
        height: rect["height"] ?? 44
    )
    let overlay = CredentialPickerView(frame: frame)
    webView.superview?.addSubview(overlay)
}
```

## Use `callAsyncJavaScript` for Complex DOM Queries

For queries that involve multiple DOM reads or asynchronous operations, [`callAsyncJavaScript(_:arguments:in:in:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/callasyncjavascript(_:arguments:in:in:completionhandler:)) allows you to run a JavaScript async function inside the isolated world and receive the result natively without any intermediate DOM state:

```swift
webView.callAsyncJavaScript("""
    const forms = document.querySelectorAll('form');
    return Array.from(forms).map(f => {
        const r = f.getBoundingClientRect();
        return { x: r.left, y: r.top, action: f.action };
    });
""", arguments: [:], in: nil, in: appWorld) { result in
    // process form positions natively
}
```

## Do Not Write Sensitive Data into the DOM

Content world isolation protects your script's internal state, but any value written into a DOM element (an input field, a text node, a data attribute) is visible to page JavaScript regardless of which world wrote it. Never populate a DOM element with a credential, token, or sensitive identifier that page scripts should not see. Keep sensitive data flowing only between the native layer and your isolated script.

**Note:** Page JavaScript can dispatch DOM events on shared elements, which will cause isolated world listeners attached to those elements to fire. The attacker triggers the event blindly and cannot read your listener's local variables or intercept what it sends to native code through the message bridge. However, if your listener takes action based on the DOM state that was just manipulated by the attacker (for example, reading an input value the attacker changed), the resulting action may be attacker-influenced. Validate data received from the DOM before acting on it, as described in @MASTG-BEST-0x01.
