---
platform: android
title: Runtime Use of Content Provider Access APIs in WebViews
alias: references-to-content-provider-access-in-webviews
id: MASTG-TEST-0251
apis: [WebView, WebSettings, getSettings, ContentProvider, setAllowContentAccess, setAllowUniversalAccessFromFileURLs, setJavaScriptEnabled]
type: [dynamic, hooks, manual]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0011, MASTG-BEST-0012, MASTG-BEST-0013, MASTG-BEST-0049]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0018]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0250.

In this case you can take two approaches when hooking or tracing the relevant APIs:

- enumerate instances of `WebView` in the app and list their configuration values.
- or, explicitly hook the setters of the `WebView` settings.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of WebView setting calls, including the argument values and backtraces of each call.

## Evaluation

The test case fails if all the following applies:

- `JavaScriptEnabled` is `true`.
- `AllowContentAccess` is `true`.
- `AllowUniversalAccessFromFileURLs` is `true`.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0023:

- Determine whether the settings are explicitly used and configured to the identified values.
- Determine which `WebView` instance receives the configuration and whether it handles sensitive information or functionality.
- Determine whether the `WebView` loads content in a context where content provider data could be accessed via `content://` URLs.

For the identified WebViews, determine whether attacker-controlled JavaScript could execute in a context where it can access content providers that handle sensitive data. Also use the list of content providers obtained in @MASTG-TEST-0250 to verify if they handle sensitive data.

!!! note
    `AllowContentAccess` being `true` does not represent a security vulnerability by itself, but it can be used in combination with other vulnerabilities to escalate the impact of an attack.
