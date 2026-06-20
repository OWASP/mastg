---
platform: ios
title: References to Password Fields in WebView-Loaded HTML
id: MASTG-TEST-0378
type: [static, code, manual]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0059, MASTG-BEST-0060]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076, MASTG-KNOW-0139]
---

## Overview

When an iOS app renders HTML containing `<input type="password">` elements inside a `WKWebView`, any JavaScript running on the page, including injected XSS payloads and third-party scripts loaded by the page, can read the typed value via `element.value`.

This test checks whether the app loads HTML containing password input fields into a `WKWebView` without using a native secure input overlay. When a password field is present in the DOM, the appropriate mitigation is to intercept focus with an isolated `WKUserScript`, prevent typing in the HTML field, and present a native `UITextField` with `isSecureTextEntry = true` overlaid at the same position, so the typed value never enters the DOM.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0071 to look for the string `type="password"` or `type='password'` in the binary's string table.

## Observation

The output should contain a list of locations in the binary where password field HTML is referenced.

## Evaluation

The test case fails if the binary contains `type="password"` references and the app does not implement a native input overlay.

**Further Validation Required:**

Inspect each reported call site using @MASTG-TECH-0076 to confirm the password field HTML is loaded into a `WKWebView` and to check whether the app registers a `WKUserScript` that intercepts focus and overlays a native `UITextField` with `isSecureTextEntry = true` at the corresponding position.
