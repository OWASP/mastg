---
platform: ios
title: DOM Inspection Using evaluateJavaScript Without Content World Isolation
code: [swift]
id: MASTG-DEMO-0x04
test: MASTG-TEST-0x04
kind: fail
---

## Sample

This sample demonstrates a `WKWebView` that reads sensitive content from the DOM (an account number, a balance, and a form recipient) using `evaluateJavaScript(_:completionHandler:)` without specifying a content world. All three calls run in the `.page` world, where the prototype chain is shared with page JavaScript. A malicious page can override `document.querySelector` before any of these calls run:

```javascript
// Attacker-controlled page script
document.querySelector = function(selector) {
    return { textContent: "ATTACKER_CONTROLLED", value: "attacker@evil.com" };
};
```

After this override, all three `evaluateJavaScript` calls return the attacker's value instead of the real DOM content. The native code receives and acts on poisoned data.

{{ MastgTest.swift }}

## Steps

Let's run the semgrep rule against the sample code.

{{ ../../../../rules/mastg-ios-evaluate-javascript-without-content-world.yaml }}

{{ run.sh }}

## Observation

The rule identified three instances in the code where `evaluateJavaScript` is called with `completionHandler:` but without a content world parameter.

{{ output.txt }}

## Evaluation

The test case fails because the app calls `evaluateJavaScript(_:completionHandler:)` to read security-relevant DOM content in the page world.

1. Line 45: reads the account number from `#account-number` in the page world.
2. Line 52: reads the balance from `#balance` in the page world.
3. Line 59: reads the form recipient from `input[name=recipient]` in the page world.

All three call sites are inside `MastgTest.showWebView`, which loads a `WKWebView` with HTML containing the elements being queried. Because these calls run in the `.page` world, any page script executed before them can override `document.querySelector` or other prototype methods to return attacker-controlled values. The safe alternative is `evaluateJavaScript(_:in:in:completionHandler:)` with a custom `WKContentWorld`, where the prototype chain is independent of page JavaScript.
