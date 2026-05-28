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

Let's run the semgrep rule against the sample code.

{{ ../../../../rules/mastg-ios-evaluate-javascript-without-content-world.yaml }}

{{ run.sh }}

## Observation

The rule identified one instance in the code where `evaluateJavaScript` is called with `completionHandler:` but without a content world parameter.

{{ output.txt }}

## Evaluation

The test case fails because the app calls `evaluateJavaScript(_:completionHandler:)` to read security relevant DOM content in the page world.

1. Line 55: reads the recipient account number from `#recipient_account_number` in the page world.

The call site is inside `MastgTest.webView(_:didFinish:)`, which is called after the `WKWebView` finishes loading the HTML. Because this call runs in the `.page` world, any page script executed before it can override `document.querySelector` or other prototype methods to return attacker controlled values.
