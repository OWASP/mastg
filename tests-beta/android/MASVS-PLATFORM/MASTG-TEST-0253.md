---
platform: android
title: Runtime Use of Local File Access APIs in WebViews
alias: references-to-local-file-access-in-webviews
id: MASTG-TEST-0253
apis: [WebView, WebSettings, getSettings, setAllowFileAccess, setAllowFileAccessFromFileURLs, setAllowUniversalAccessFromFileURLs]
type: [dynamic]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0010, MASTG-BEST-0011, MASTG-BEST-0012]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0018]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0252.

## Steps

1. Run a dynamic analysis tool like @MASTG-TOOL-0001 and either:
    - enumerate instances of `WebView` in the app and list their configuration values
    - or explicitly hook the setters of the `WebView` settings, including:
        - `setJavaScriptEnabled`
        - `setAllowFileAccess`
        - `setAllowFileAccessFromFileURLs`
        - `setAllowUniversalAccessFromFileURLs`

## Observation

The output should contain a list of WebView instances and corresponding settings.

## Evaluation

**Fail:**

The test case fails if all of the following are true:

- `JavaScriptEnabled` is `true`.
- `AllowFileAccess` is `true`.
- Either `AllowFileAccessFromFileURLs` or `AllowUniversalAccessFromFileURLs` is `true`.

!!! note
    `AllowFileAccess` being `true` does not represent a vulnerability by itself, but it can increase impact when combined with insecure JavaScript and file URL settings.

**Pass:**

The test case passes if any of the following are true:

- `JavaScriptEnabled` is `false`.
- `AllowFileAccess` is `false`.
- Both `AllowFileAccessFromFileURLs` and `AllowUniversalAccessFromFileURLs` are `false`.
