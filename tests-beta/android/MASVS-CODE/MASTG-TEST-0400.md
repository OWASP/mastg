---
platform: android
title: Runtime Use of WebViewClient URL Loading Handlers
id: MASTG-TEST-0400
apis: [WebView, WebViewClient, shouldOverrideUrlLoading, shouldInterceptRequest, Uri, getHost, getScheme, getPath]
type: [dynamic, hooks, manual]
weakness: MASWE-0071
best-practices: []
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0018]
---

## Overview

This test dynamically analyzes the runtime behavior of `WebViewClient` URL interception methods to understand how the app handles URL loading in WebViews. By hooking relevant methods at runtime, you can observe:

- Which URLs are being loaded and intercepted.
- How the app validates or filters URLs.
- Whether the app implements allowlist or denylist patterns.
- What decisions the app makes when encountering different URL schemes or domains.

This complements static analysis (@MASTG-TEST-0398) by providing actual runtime evidence of URL handling behavior.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain:

- A list of URLs that were intercepted by `shouldOverrideUrlLoading` and `shouldInterceptRequest`.
- The return values of these methods (indicating whether the URL was allowed or blocked).
- Any URL parsing operations performed using `Uri` methods.

## Evaluation

The test case fails if the runtime analysis shows that a URL is loaded or a resource request is served without the app validating it against trusted content.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0023 to determine whether:

- The app validates the URL's host or scheme before allowing navigation or returning resource data.
- The validation logic reliably restricts navigation to trusted domains (for example, validating the full host rather than relying on a substring match).

Note that intercepting URL loading is not inherently insecure. The test fails only when the implementation does not properly restrict navigation to trusted content.
