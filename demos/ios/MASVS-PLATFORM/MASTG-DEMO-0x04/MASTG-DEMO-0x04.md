---
platform: ios
title: DOM Inspection Using evaluateJavaScript Without Content World Isolation
code: [swift]
id: MASTG-DEMO-0x04
test: MASTG-TEST-0x04
kind: fail
---

## Sample

This sample demonstrates a `WKWebView` that reads sensitive content from the DOM (a recipient account number) using `evaluateJavaScript(_:completionHandler:)` without specifying a content world. The script runs in the `.page` world, where the prototype chain is shared with page JavaScript. A malicious page can override `document.querySelector` before the call runs:

```javascript
// Attacker controlled page script
document.querySelector = function(selector) {
    return { textContent: "ATTACKER_CONTROLLED" };
};
```

After this override, the `evaluateJavaScript` call returns the attacker value instead of the real DOM content. The native code receives and acts on poisoned data.

{{ MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the app. The main binary is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ evaluate_js_no_world.r2 }}

{{ run.sh }}

## Observation

The script identifies the call sites where `evaluateJavaScript:completionHandler:` is used. In the disassembly we can see the call to `objc_msgSend` with the `evaluateJavaScript:completionHandler:` selector.

{{ output.txt }}

## Evaluation

The test case fails because the app calls `evaluateJavaScript:completionHandler:` to read security relevant DOM content in the page world. Address `0x1000048b0`: reads the recipient account number from `#recipient_account_number` in the page world.

The call site is inside `MastgTest.webView(_:didFinish:)`, which is called after the `WKWebView` finishes loading the HTML. Because this call runs in the `.page` world, any page script executed before it can override `document.querySelector` or other prototype methods to return attacker controlled values.
