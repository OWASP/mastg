---
platform: ios
title: Runtime Setting of Relaxed WebView File Origin Policies
id: MASTG-TEST-0336
type: [dynamic, hooks, manual]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0033]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0335.

`WKWebView` supports configuration that affects how JavaScript running from `file://` origins can access other resources. In particular, `allowFileAccessFromFileURLs` allows JavaScript running in the context of a `file://` URL to access content from other `file://` URLs, while `allowUniversalAccessFromFileURLs` allows JavaScript running in the context of a `file://` URL to access content from any origin. Both settings are dangerous when enabled because they relax the origin restrictions that normally apply to local content.

This test verifies at runtime whether the application enables either of these settings for a `WKWebView` that loads local `file://` content.

Typical APIs to monitor include:

- `WKPreferences _setAllowFileAccessFromFileURLs:`
- `WKWebViewConfiguration _setAllowUniversalAccessFromFileURLs:`
- `WKPreferences setJavaScriptEnabled:`
- `WKWebView loadFileURL:allowingReadAccessToURL:`
- `WKWebView loadHTMLString:baseURL:` when a `file://` base URL may be used

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should show any uses of functions setting `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs`, loading local `file://` content, as well as the backtraces of each relevant call.

## Evaluation

The test case fails if the application enables `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs` for a `WKWebView` that loads local `file://` content.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0076:

- Determine whether `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs` is explicitly used and set to `true`.
- Determine which `WKWebView` instance receives the configuration and whether it handles sensitive information or functionality.
- Determine whether that `WKWebView` loads local `file://` content, for example using APIs such as `loadFileURL(_:allowingReadAccessTo:)` or `loadHTMLString(_:baseURL:)` with a `file://` base URL.

Note that some apps may use variables or configuration logic to set these values, which can make them difficult to identify through static analysis alone. Dynamic analysis can help confirm whether the settings are enabled at runtime.

For the identified WebViews, determine whether attacker-controlled JavaScript could execute in the local page context, for example through HTML injection, JavaScript injection, or other untrusted content. Also determine whether the attacker could exfiltrate accessed data, for example by sending it to a remote server using `fetch` or `XMLHttpRequest`, or by embedding it in requests to external resources such as images or iframes.

Even if exploitability cannot be fully confirmed, it is recommended to remove these settings because they weaken the origin isolation normally applied to `file://` content. Enabling them increases the impact of other WebView vulnerabilities, such as content injection or improper handling of untrusted input.
