---
platform: ios
title: Password Field Rendered in WebView DOM Without Native Overlay
code: [swift]
id: MASTG-DEMO-0144
test: MASTG-TEST-0378
kind: fail
note: This demo requires a MASTestApp binary compiled from the accompanying MastgTest.swift. Run the MASTestApp iOS project with this MastgTest.swift to obtain the binary, then run run.sh.
---

## Sample

This sample loads an HTML login form containing `<input type="password">` directly into a `WKWebView` via `loadHTMLString:baseURL:`. The password field lives in the DOM, so any JavaScript running on the page can read the user's typed value at any time via `document.getElementById('password').value`.

{{ MastgTest.swift }}

## Steps

1. Use @MASTG-TECH-0058 to extract the app. The main binary is `./Payload/MASTestApp.app/MASTestApp`.
2. Use @MASTG-TOOL-0073 with the `-i` option to run this script.

{{ password_field_webview.r2 # run.sh }}

## Observation

The output shows the `input type="password"` string in the binary's string table and its location in the function that constructs the HTML passed to `loadHTMLString:baseURL:`.

{{ output.txt }}

## Evaluation

The test case fails because the app renders `<input type="password">` inside the `WKWebView` DOM without intercepting focus or overlaying a native secure input view.

The `<input type="password">` field is part of the HTML constant stored at `0x10000ac00`, and the cross-reference shows it is loaded inside `sym.MASTestApp.MastgTest.showWebView._6E8AB2C58CE173A727EF27CB85DF8CD8.completion_...FZ_` at `0x100004258`. That string is bridged to an `NSString` (via `Foundation...bridgeToObjectiveC...` at `0x100004268`) and dispatched through the `loadHTMLString:baseURL:` selector, which is loaded into `x1` at `0x100004274`, confirming that the password field is rendered directly in the `WKWebView` DOM.

Inspecting the loading function reveals no `WKUserScript` registration that intercepts focus on the field, and no native `UITextField` overlay is presented. The `submitForm` function in the page JavaScript (visible inside the HTML constant at `0x10000ac00`) reads `document.getElementById('password').value` and sends it through the bridge, meaning the plaintext password is present in the DOM and accessible to any script running on the page before and during submission.
