---
platform: ios
title: Sensitive Data and Functionality Exposed Through a WKWebView Native Bridge
code: [swift]
id: MASTG-DEMO-0x01
test: MASTG-TEST-0x01
kind: fail
status: placeholder
note: This demo requires a MASTestApp binary compiled from the accompanying MastgTest.swift. Run the MASTestApp iOS project with this MastgTest.swift to obtain the binary, then run run.sh.
---

## Sample

This sample demonstrates a `WKWebView` that registers a native bridge handler named `"bridge"` via `WKUserContentController.add(_:name:)`. The `SecretBridgeHandler` class implements `WKScriptMessageHandler` and handles two actions (`"getSecret"` and `"getCredentials"`) that return sensitive native data directly to JavaScript without validating the message origin. Any JavaScript running in the WebView can invoke these actions using:

```javascript
window.webkit.messageHandlers.bridge.postMessage({action: 'getSecret'})
window.webkit.messageHandlers.bridge.postMessage({action: 'getCredentials'})
```

{{ MastgTest.swift }}

## Steps

1. Unzip the app package and locate the main binary file (@MASTG-TECH-0058), which in this case is `./Payload/MASTestApp.app/MASTestApp`.
2. Open the app binary with @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ webview_native_bridge.r2 # run.sh }}

## Observation

The output shows the `addScriptMessageHandler:name:` selector used in the binary and its cross-reference back to the function that registers the bridge.

{{ output.txt }}

## Evaluation

The test case fails because the app registers a native bridge handler named `"bridge"` via `WKUserContentController.add(_:name:)` and the corresponding `SecretBridgeHandler.userContentController(_:didReceive:)` exposes sensitive data to any JavaScript running in the WebView.

In the output, the `addScriptMessageHandler:name:` selector is found at `reloc.fixup.addScriptMessageHandler:name:`, and the cross-reference points to `sym.MASTestApp.MastgTest.showWebView_completion__1`. Inspecting that function (using @MASTG-TECH-0076) reveals that:

- The bridge named `"bridge"` is registered unconditionally on the `WKUserContentController` without any origin-based restrictions.
- The `SecretBridgeHandler` handles a `"getSecret"` action that returns a hardcoded API key string to JavaScript, and a `"getCredentials"` action that returns user credentials, both via `evaluateJavaScript:completionHandler:`.
- No validation is performed on the incoming message content or origin, so any JavaScript in the page can call these actions.

Because the WebView loads a locally constructed HTML page that a developer controls, the immediate impact in this demo is limited. However, if the WebView were to load attacker-controlled content (for example through a deep link, open redirect, or XSS), an attacker could invoke `window.webkit.messageHandlers.bridge.postMessage({action: 'getSecret'})` and receive the API key in the `receiveSecret` callback.
