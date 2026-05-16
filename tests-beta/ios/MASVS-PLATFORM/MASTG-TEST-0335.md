---
platform: ios
title: WebView File Origin Access Relaxed by Configuration
id: MASTG-TEST-0335
type: [static]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0033]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076]
---

## Overview

`WKWebView` supports configuration that affects how JavaScript running from `file://` origins can access other resources. In particular, `allowFileAccessFromFileURLs` allows JavaScript running in the context of a `file://` URL to access content from other `file://` URLs, while `allowUniversalAccessFromFileURLs` allows JavaScript running in the context of a `file://` URL to access content from any origin. Both settings are considered dangerous when enabled because they relax the origin restrictions that normally apply to local content which increases the risk of vulnerabilities such as Cross-Site Scripting (XSS) or Local File Inclusion (LFI) leading to data exfiltration or other malicious actions.

This test checks whether the app enables either of these settings for any `WKWebView` instance. On iOS, these settings are commonly accessed through non-public or unsupported paths, for example by using key value coding on `WKPreferences` or `WKWebViewConfiguration` via `setValue:forKey:` or equivalent Swift calls.

Remember that JavaScript is enabled by default unless the app explicitly sets [`WKWebViewPreferences.setJavaScriptEnabled`](https://developer.apple.com/documentation/webkit/wkpreferences/javascriptenabled) (deprecated since iOS 14) or [`WKWebpagePreferences.allowsContentJavaScript`](https://developer.apple.com/documentation/webkit/wkwebpagepreferences/allowscontentjavascript) to `false`.

This test is related to, but distinct from, @MASTG-TEST-0333, which focuses on the **native file system read scope** granted to the WebView through `loadFileURL(_:allowingReadAccessTo:)`. By contrast, this test focuses on **JavaScript origin restrictions** for content loaded from `file://` URLs. Even if the file read scope is correctly restricted, enabling `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs` can allow JavaScript (e.g. `fetch()`, `XMLHttpRequest`) running in a local page to access additional resources or communicate with remote origins.

## Steps

1. Extract the app as described in @MASTG-TECH-0058.
2. Run a static analysis tool such as @MASTG-TOOL-0073 on the app binary and search for references to the relevant configuration values.

## Observation

The output should identify locations in the binary where the app references or enables the relevant configuration values.

## Evaluation

The test case fails if the app enables `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs` for a `WKWebView` that loads local `file://` content.

Inspect each reported call site using @MASTG-TECH-0076.

- Determine whether `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs` is explicitly used and set to `true`, for example through `setValue:forKey:` or equivalent Swift calls.
- Determine which `WKWebView` instance receives the configuration and whether it handles sensitive information or functionality.
- Determine whether that `WKWebView` loads local `file://` content, for example using APIs such as `loadFileURL(_:allowingReadAccessTo:)` or `loadHTMLString(_:baseURL:)` with a `file://` base URL.

Note that some apps may use variables or configuration logic to set `allowFileAccessFromFileURLs` or `allowUniversalAccessFromFileURLs`, which can make them difficult to identify through static analysis alone. Dynamic analysis can help confirm whether the settings are enabled at runtime.

For the identified WebViews, determine whether attacker-controlled JavaScript could execute in the local page context, for example through HTML injection, JavaScript injection, or other untrusted content. Also determine whether the attacker could exfiltrate accessed data, for example by sending it to a remote server using `fetch` or `XMLHttpRequest`, or by embedding it in requests to external resources such as images or iframes.
