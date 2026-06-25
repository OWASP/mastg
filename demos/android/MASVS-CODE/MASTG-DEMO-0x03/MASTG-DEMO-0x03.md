---
platform: android
title: Runtime Use of WebViewClient URL Loading Handlers with Frida
id: MASTG-DEMO-0x03
code: [kotlin]
test: MASTG-TEST-0x03
---

## Sample

This sample demonstrates how to dynamically analyze the runtime behavior of `WebViewClient` URL interception methods using Frida to understand how the app handles URL loading in WebViews.

{{ MastgTest.kt }}

The code configures a WebView with a custom `WebViewClient` that intercepts URL loading via `shouldOverrideUrlLoading` and `shouldInterceptRequest` methods. The implementation does not perform proper URL validation, potentially allowing navigation to untrusted content.

## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Interact with the app to trigger WebView navigation.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ run.sh # script.js }}

The Frida script hooks the `shouldOverrideUrlLoading` and `shouldInterceptRequest` methods to observe:

- Which URLs are being intercepted.
- The return values of these methods (indicating whether the URL was allowed or blocked).
- Any URL parsing operations performed using `Uri` methods.

## Observation

The output shows the URLs that were intercepted by the WebViewClient methods, along with the return values and any URL parsing performed.

{{ output.txt }}

## Evaluation

The test case fails because the implementation does not perform any URL validation:

- URLs are logged but not validated against an allowlist of trusted domains.
- All URLs are allowed to load (return value is `false` for `shouldOverrideUrlLoading`).
- No checks for malicious content.

This could allow navigation to untrusted content or potential open redirect vulnerabilities.
