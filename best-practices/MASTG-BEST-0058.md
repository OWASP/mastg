---
title: Restrict Native Functionality Exposed Through WebView Bridges
alias: restrict-native-functionality-exposed-through-webview-bridges
id: MASTG-BEST-0058
platform: ios
knowledge: [MASTG-KNOW-0076]
---

When using `WKWebView`, native functionality can be exposed to JavaScript through message handlers registered via [`WKUserContentController.add(_:name:)`](https://developer.apple.com/documentation/webkit/wkusercontentcontroller/add(_:name:)). Any JavaScript running in the WebView can call `window.webkit.messageHandlers.<name>.postMessage(...)` to invoke the corresponding native handler. If the handler exposes sensitive operations or data without proper validation, an attacker who can run JavaScript in the WebView (for example through content injection or XSS) can abuse it.

## Minimize Exposed Functionality

Only expose the minimal native functionality that JavaScript actually needs. Avoid registering handlers that return sensitive data (such as credentials or API keys), trigger privileged operations, or allow arbitrary writes to storage. Handlers should implement narrow, purpose-built operations rather than generic dispatch mechanisms.

## Validate Message Content

Always validate messages received in `userContentController(_:didReceive:)` before processing them. Check that the message body has the expected type and structure, and reject unexpected inputs.

```swift
func userContentController(_ userContentController: WKUserContentController,
                            didReceive message: WKScriptMessage) {
    guard let body = message.body as? [String: String],
          let action = body["action"],
          action == "allowedAction" else {
        return // reject unexpected messages
    }
    // handle known action
}
```

## Restrict Handler Scope

Remove message handlers when the WebView is no longer needed to prevent accidental reuse:

```swift
webView.configuration.userContentController.removeScriptMessageHandler(forName: "bridge")
```

## Consider Origin Validation

Although `WKWebView` doesn't expose a direct origin-based access control model for script message handlers (unlike the Android `addWebMessageListener` API), you can inspect [`WKScriptMessage.frameInfo`](https://developer.apple.com/documentation/webkit/wkscriptmessage/frameinfo) and its `securityOrigin` to determine the source of the message. For handlers serving sensitive operations, verify that the message comes from an expected trusted origin before processing.

## Avoid Legacy UIWebView Bridges

Don't use `UIWebView` and its `JSContext` or `JSExport` bridge mechanisms. `UIWebView` was deprecated in iOS 12. It always enables JavaScript and doesn't provide the same security controls as `WKWebView`. Migrate to `WKWebView` as described in @MASTG-BEST-0032.
