---
platform: android
title: References to WebViewClient URL Loading Handlers
id: MASTG-TEST-0398
apis: [WebView, WebViewClient, shouldOverrideUrlLoading, shouldInterceptRequest, setWebViewClient]
type: [static, code, manual]
weakness: MASWE-0071
best-practices: []
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0018]
---

## Overview

This test checks for references to `WebViewClient` URL interception methods that override the default page navigation behavior in WebViews. The default and safest behavior on Android is to let the default web browser open any link that the user clicks inside the WebView. However, this can be modified by configuring a `WebViewClient` with custom URL handling logic.

The following interception callback methods are relevant:

- [`shouldOverrideUrlLoading`](https://developer.android.com/reference/android/webkit/WebViewClient#shouldOverrideUrlLoading(android.webkit.WebView,%20android.webkit.WebResourceRequest)): allows the application to either abort loading WebViews with suspicious content by returning `true` or allow the WebView to load the URL by returning `false`. Note that this method is not called for POST requests, XmlHttpRequests, iFrames, "src" attributes in HTML, or `<script>` tags.
- [`shouldInterceptRequest`](https://developer.android.com/reference/android/webkit/WebViewClient#shouldInterceptRequest(android.webkit.WebView,%20android.webkit.WebResourceRequest)): allows the application to return custom data from resource requests. This callback is invoked for various URL schemes (e.g., `http(s):`, `data:`, `file:`), but not for `javascript:` or `blob:` URLs, or for assets accessed via `file:///android_asset/` or `file:///android_res/`.

If these methods are implemented without proper URL validation, the app may load content from untrusted sources or navigate users to malicious websites.

See @MASTG-KNOW-0018 for more information on URL loading in WebViews.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where `WebViewClient` URL loading handlers are used, including:

- Classes extending `WebViewClient` with `shouldOverrideUrlLoading` or `shouldInterceptRequest` implementations.
- Calls to `setWebViewClient` on `WebView` instances.

## Evaluation

The test case fails if a `WebViewClient` URL interception method is implemented without properly restricting navigation to trusted content.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023, looking for cases such as:

- **No validation:** the method does not check the URL against an allowlist or denylist before allowing navigation or returning resource data.
- **Weak validation:** the method performs validation that does not reliably prevent navigation to untrusted domains (for example, substring checks instead of validating the host).
- **Missing client implementation:** a `WebViewClient` is assigned to a `WebView` via `setWebViewClient` without overriding any interception method, leaving the default (unrestricted) navigation behavior in place for an app that intended to restrict it.

Note that using a `WebViewClient` is not inherently insecure. The test fails only when the URL handling logic does not properly restrict navigation to trusted content.
