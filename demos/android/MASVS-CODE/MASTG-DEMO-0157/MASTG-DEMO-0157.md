---
platform: android
title: Uses of WebViewClient URL Loading Handlers with semgrep
id: MASTG-DEMO-0157
code: [kotlin, java]
test: MASTG-TEST-0398
---

## Sample

The following sample demonstrates how a `WebViewClient` is configured to intercept URL loading in a WebView. The `shouldOverrideUrlLoading` method is implemented to handle navigation requests, which overrides the default behavior of opening links in the default browser.

{{ MastgTestWebView.kt # MastgTestWebView_reversed.java }}

## Steps

Let's run our @MASTG-TOOL-0110 rules against the sample code.

{{ ../../../../rules/mastg-android-webview-url-handlers.yml }}

{{ run.sh }}

## Observation

The output shows references to WebViewClient URL loading handlers.

{{ output.txt }}

## Evaluation

The test fails because the WebView can load content from any host the user is directed to. The static rule flags the attack surface: the reported finding points to the `setWebViewClient` call and the custom `WebViewClient` it registers, which spans both overridden handlers. Using a `WebViewClient` is not insecure on its own, so as required by @MASTG-TEST-0398 we inspect each reported handler to confirm whether it restricts navigation to trusted content.

Reviewing the reported code shows that neither handler performs any validation:

1. **`setWebViewClient`**: The WebView is configured with the custom `WebViewClient`.
2. **`shouldOverrideUrlLoading`**: The implementation reads only `request.getUrl().toString()` to log the URL and always returns `false`. It never calls `getHost`, `getScheme`, or `getPath`, nor compares the URL against any allowlist or denylist, so every URL is allowed to load regardless of its host.
3. **`shouldInterceptRequest`**: The implementation likewise only logs `request.getUrl().toString()` and falls back to `super.shouldInterceptRequest`, with no host, scheme, or path inspection.

Because the handlers inspect only the full URL string for logging and make no host-, scheme-, or path-based decision, they impose no restriction on navigation, and the WebView can load content from any host the user is directed to. The runtime counterpart of this conclusion is demonstrated in @MASTG-DEMO-0158.
