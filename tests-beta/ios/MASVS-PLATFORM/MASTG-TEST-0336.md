---
platform: ios
title: Runtime Setting of Relaxed WebView File Origin Policies
id: MASTG-TEST-0336
type: [dynamic]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0033]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0335.

`WKWebView` supports configuration that affects how JavaScript running from `file://` origins can access other resources. In particular, `allowFileAccessFromFileURLs` allows JavaScript running in the context of a `file://` URL to access content from other `file://` URLs, while `allowUniversalAccessFromFileURLs` allows JavaScript running in the context of a `file://` URL to access content from any origin. Both settings are dangerous when enabled because they relax the origin restrictions that normally apply to local content.

This test verifies at runtime whether the application enables either of these settings for a `WKWebView` that loads local `file://` content.

## Steps

1. Deploy the app to a device or simulator as described in @MASTG-TECH-0056.
2. Launch the app with a runtime instrumentation tool such as @MASTG-TOOL-0039.
3. Hook the relevant WebKit APIs to observe whether the app enables relaxed file origin policies and loads local content into a `WKWebView`.
4. Trigger the code paths that create and configure the `WKWebView`.
5. Inspect the captured runtime arguments.

Typical APIs to monitor include:

- `WKPreferences _setAllowFileAccessFromFileURLs:`
- `WKWebViewConfiguration _setAllowUniversalAccessFromFileURLs:`
- `WKPreferences setJavaScriptEnabled:`
- `WKWebView loadFileURL:allowingReadAccessToURL:`
- `WKWebView loadHTMLString:baseURL:` when a `file://` base URL may be used

## Observation

The output should show whether the application enables `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs` at runtime and whether the affected `WKWebView` loads local `file://` content.

## Evaluation

The test case fails if the application enables `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs` for a `WKWebView` that loads local `file://` content.

Inspect each reported call site using @MASTG-TECH-0077.

- Determine whether `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs` is explicitly used and set to `true`.
- Determine which `WKWebView` instance receives the configuration and whether it handles sensitive information or functionality.
- Determine whether that `WKWebView` loads local `file://` content, for example using APIs such as `loadFileURL(_:allowingReadAccessTo:)` or `loadHTMLString(_:baseURL:)` with a `file://` base URL.

Note that some apps may use variables or configuration logic to set these values, which can make them difficult to identify through static analysis alone. Dynamic analysis can help confirm whether the settings are enabled at runtime.

For the identified WebViews, determine whether attacker-controlled JavaScript could execute in the local page context, for example through HTML injection, JavaScript injection, or other untrusted content. Also determine whether the attacker could exfiltrate accessed data, for example by sending it to a remote server using `fetch` or `XMLHttpRequest`, or by embedding it in requests to external resources such as images or iframes.

Even if exploitability cannot be fully confirmed, it is recommended to remove these settings because they weaken the origin isolation normally applied to `file://` content. Enabling them increases the impact of other WebView vulnerabilities, such as content injection or improper handling of untrusted input.
