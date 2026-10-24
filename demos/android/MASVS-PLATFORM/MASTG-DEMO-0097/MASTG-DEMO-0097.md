---
platform: android
title: Sensitive Data and Functionality Exposed Through WebView JavaScript Bridges
id: MASTG-DEMO-0097
code: [kotlin]
test: MASTG-TEST-0334
---

## Sample

The following demo demonstrates a `WebView` component rendering a "Contact Support" form. A native bridge class, `SupportBridge`, is registered via `addJavascriptInterface()` and exposes three methods to JavaScript: one that reads the user's name, email, and JWT from `SharedPreferences` and returns them as JSON, one that submits a support message, and one that writes a preference value back to `SharedPreferences`. JavaScript is explicitly enabled on the `WebView`, which means any JavaScript running in the page (including injected scripts) can call these bridge methods directly.

{{ MastgTestWebView.kt # MastgTestWebView_reversed.java }}

Some payloads that could be executed by an attacker who can run JavaScript in this page include:

- `<img src=x onerror='document.getElementById("resultText").innerText = MASBridge.getUserProfileJson()'>`
- `<img src=x onerror='MASBridge.updateSupportPreference("malicious value")'>`

## Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed Java code.

{{ ../../../../rules/mastg-android-webview-bridges.yml }}

{{ run.sh }}

## Observation

The rule detected the `SupportBridge` class with three methods annotated with `@JavascriptInterface`, as well as the `addJavascriptInterface()` call that registers the bridge on a `WebView` with JavaScript enabled.

{{ output.txt }}

## Evaluation

After reviewing the decompiled code at the locations reported in the output, we can conclude that the test fails because:

- JavaScript is enabled on the `WebView` and a native bridge (`SupportBridge`) is registered via `addJavascriptInterface()` (line 85).
- There are three sensitive methods annotated with `@JavascriptInterface` that are reachable from JavaScript running in the page, without any origin restrictions or other mitigations:
    - `getUserProfileJson()` (line 37) reads the user's name, email, and a JWT from `SharedPreferences` and returns them to JavaScript as a JSON string, exposing PII and a credential.
    - `submitSupportMessage(email, message)` (line 58) allows JavaScript to trigger a support-message submission on behalf of the user, affecting integrity.
    - `updateSupportPreference(value)` (line 71) allows JavaScript to write arbitrary values to `SharedPreferences`, affecting app integrity.

Two remediation paths exist for this issue:

- One is to prevent attacker controlled input from being rendered as executable HTML or JavaScript in the `WebView`. This is intentionally left vulnerable in the demo to simulate an XSS style condition, but in a real app the JavaScript execution primitive could come from many other causes, not only obvious reflected input.
- The other is to redesign the native bridge by removing the legacy `addJavascriptInterface()` model and using a message based bridge with explicit origin restrictions, such as `WebViewCompat.addWebMessageListener()` with a narrow `allowedOriginRules` allowlist and sender validation. In this demo, that bridge redesign alone would not visibly stop the exploit because the injected script executes in the same trusted main page origin, so it would still satisfy the origin policy. Even so, it remains an important hardening measure, and it becomes effective when attacker controlled JavaScript runs from a different origin or untrusted frame, which is the broader class of risk associated with legacy JavaScript bridges.
