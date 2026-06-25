---
platform: android
title: Uses of WebViewClient URL Loading Handlers with semgrep
id: MASTG-DEMO-0x02
code: [kotlin, java]
test: MASTG-TEST-0x01
---

## Sample

The following sample demonstrates how a `WebViewClient` is configured to intercept URL loading in a WebView. The `shouldOverrideUrlLoading` method is implemented to handle navigation requests, which overrides the default behavior of opening links in the default browser.

{{ MastgTest.kt # MastgTest_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-webview-url-handlers.yml }}

{{ run.sh }}

## Observation

The output shows references to WebViewClient URL loading handlers.

{{ output.txt }}

## Evaluation

The test case fails because the `WebViewClient` overrides `shouldOverrideUrlLoading` and `shouldInterceptRequest` without validating the requested URL against a trusted allowlist.

Review each reported instance:

1. **`setWebViewClient`**: The WebView is configured with the custom `WebViewClient`.
2. **`shouldOverrideUrlLoading`**: The implementation only logs the URL and always returns `false`, so every URL is allowed to load regardless of its host.
3. **`shouldInterceptRequest`**: The implementation only logs the URL and falls back to the default behavior without checking the host or scheme.

Because neither method restricts navigation to trusted domains, the WebView can load content from any host the user is directed to.
