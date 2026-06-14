---
platform: android
title: Runtime Use of Local File Access APIs in WebViews
alias: references-to-local-file-access-in-webviews
id: MASTG-TEST-0253
apis: [WebView, WebSettings, getSettings, setAllowFileAccess, setAllowFileAccessFromFileURLs, setAllowUniversalAccessFromFileURLs]
type: [dynamic, hooks, manual]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0010, MASTG-BEST-0011, MASTG-BEST-0012]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0018]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0252.

In this case you can follow one of these approaches:

- enumerate instances of `WebView` in the app and list their configuration values
- or explicitly hook the setters of the `WebView` settings, including:
    - `setJavaScriptEnabled`
    - `setAllowFileAccess`
    - `setAllowFileAccessFromFileURLs`
    - `setAllowUniversalAccessFromFileURLs`

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of WebView setting calls, including the argument values and backtraces of each call.

## Evaluation

The test case fails if all of the following applies (based on the [API behavior across different Android versions](../../../Document/0x05h-Testing-Platform-Interaction.md#webview-local-file-access-settings)):

- `setJavaScriptEnabled` is explicitly set to `true`.
- `setAllowFileAccess` is explicitly set to `true` (or not used at all when `minSdkVersion` < 30, inheriting the default value, `true`).
- Either `setAllowFileAccessFromFileURLs` or `setAllowUniversalAccessFromFileURLs` is explicitly set to `true` (or not used at all when `minSdkVersion` < 16, inheriting the default value, `true`).

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0023:

- Determine whether the settings are explicitly used and configured to the identified values.
- Determine which `WebView` instance receives the configuration and whether it handles sensitive information or functionality.
- Determine whether that `WebView` loads local `file://` content, for example via `loadUrl("file://...")` or `loadDataWithBaseURL` with a `file://` base URL.

For the identified WebViews, determine whether attacker-controlled JavaScript could execute in the local file context, for example through HTML injection, JavaScript injection, or other untrusted content. Also determine whether the attacker could exfiltrate local files or other sensitive data accessible via `file://` URLs.

!!! note
    `AllowFileAccess` being `true` does not represent a security vulnerability by itself, but it can be used in combination with other vulnerabilities to escalate the impact of an attack.
