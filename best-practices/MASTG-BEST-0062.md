---
title: Use WKScriptMessageHandlerWithReply to Return Data to JavaScript
alias: use-wkscriptmessagehandlerwithreply-to-return-data-to-javascript
id: MASTG-BEST-0062
platform: ios
available_since: 14
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0139]
---

When a native bridge handler needs to return data to JavaScript, the common pattern of calling [`evaluateJavaScript:completionHandler:`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)) with a callback string such as `window.receiveData(...)` injects the return value into the page's JavaScript context. Any page script can override that global function before the handler fires it and intercept the returned data.

[`WKScriptMessageHandlerWithReply`](https://developer.apple.com/documentation/webkit/wkscriptmessagehandlerwithreply) eliminates this by delivering the reply directly to the JavaScript `Promise` that initiated the call, within the calling content world, without touching any global page-world scope.

## Replace the evaluateJavaScript Callback Pattern

The old pattern injects data into the page world via a global function:

```swift
// Avoid: return value is injected into page-world JavaScript
func userContentController(_ ucc: WKUserContentController,
                            didReceive message: WKScriptMessage) {
    let secret = fetchSecret()
    let js = "window.receiveSecret('\(secret)')"
    message.webView?.evaluateJavaScript(js, completionHandler: nil)
}
```

Replace it with `WKScriptMessageHandlerWithReply`, which returns directly to the caller:

```swift
// Preferred: reply goes directly to the awaiting Promise in the caller's world
func userContentController(_ ucc: WKUserContentController,
                            didReceive message: WKScriptMessage,
                            replyHandler: @escaping (Any?, String?) -> Void) {
    let secret = fetchSecret()
    replyHandler(secret, nil)
}
```

On the JavaScript side, the call becomes a simple `await`:

```javascript
// No global callback needed, the reply arrives as a resolved Promise value
const secret = await window.webkit.messageHandlers.bridge.postMessage({action: "getSecret"});
```
