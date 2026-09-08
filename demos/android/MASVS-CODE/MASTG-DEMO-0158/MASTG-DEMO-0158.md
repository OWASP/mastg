---
platform: android
title: Runtime Use of WebViewClient URL Loading Handlers with Frida
id: MASTG-DEMO-0158
code: [kotlin]
test: MASTG-TEST-0400
---

## Sample

This sample demonstrates how to dynamically analyze the runtime behavior of `WebViewClient` URL interception methods using Frida to understand how the app handles URL loading in WebViews.

{{ ../MASTG-DEMO-0157/MastgTestWebView.kt # ../MASTG-DEMO-0157/AndroidManifest.xml }}

The code configures a WebView with a custom `WebViewClient` that intercepts URL loading via `shouldOverrideUrlLoading` and `shouldInterceptRequest` methods. The implementation does not perform proper URL validation, potentially allowing navigation to untrusted content.

## Steps

1. Install the app on a device (@MASTG-TECH-0005).
2. Make sure you have @MASTG-TOOL-0001 installed on your machine and the frida-server running on the device.
3. Run `run.sh` to spawn the app with Frida.
4. Optionally, interact with the app and tap links in the WebView to trigger further navigation.
5. Stop the script by pressing `Ctrl+C` and/or `q` to quit the Frida CLI.

{{ run.sh # script.js }}

The Frida script does three things:

- It hooks `setWebViewClient` to reveal, at launch, the `WebViewClient` implementation the app registers and which URL loading handlers it overrides. This requires no navigation.
- It hooks the `shouldOverrideUrlLoading` and `shouldInterceptRequest` methods to log each URL they receive at runtime and the value they return.
- It hooks the URL inspection accessors `Uri.getHost`, `Uri.getScheme`, and `Uri.getPath` and records any call made _while a handler is executing_. A genuine allowlist check must read the host (and usually the scheme) to decide whether a URL is trusted, so if a handler runs without ever calling these, it performs no host-based validation.

## Observation

The output shows the custom `WebViewClient` registered by the app, the URL loading handlers it overrides, the URLs intercepted at runtime along with their return values, and whether each handler inspected the URL host, scheme, or path.

{{ output.txt }}

## Evaluation

The test case fails because the app registers a custom `WebViewClient` (`MastgTestWebView$mastgTest$2`) that overrides `shouldInterceptRequest` and `shouldOverrideUrlLoading`, but neither handler validates the requested URL:

- Every intercepted request falls through to the default loading behavior, including requests to third-party hosts such as `fonts.googleapis.com` and `cdn.datatables.net`.
- For every handler invocation, the `URL inspection during handler` line reports `NONE`: the handler returned without ever reading the URL's host, scheme, or path. Since an allowlist check must inspect at least the host to decide whether a URL is trusted, this shows the handlers make no host-based validation decision rather than merely failing to block the specific URLs observed.
- When `shouldOverrideUrlLoading` is exercised (by tapping a link), it returns `false`, so every URL the user is directed to is loaded.

Because the handlers perform no host/scheme/path inspection, the WebView loads content from any host, which could allow navigation to untrusted content or open redirect vulnerabilities.

> Note: this technique observes host-, scheme-, and path-based validation, which covers the standard allowlist pattern. It would not detect validation performed purely by matching the raw URL string (for example with `String.contains`). Combine it with the static analysis in @MASTG-DEMO-0157 for full certainty.
