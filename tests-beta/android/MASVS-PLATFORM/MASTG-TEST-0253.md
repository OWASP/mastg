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
    - or explicitly hook the setters of the `WebView` settings

## Observation

The output should contain a list of WebView instances and corresponding settings.

## Evaluation

The test case fails if all of the following applies:

- `AllowFileAccess` is `true`.
- `AllowFileAccessFromFileURLs` is `true`.
- `AllowUniversalAccessFromFileURLs` is `true`.

!!! note
    `AllowFileAccess` being `true` does not represent a security vulnerability by itself, but it can be used in combination with other vulnerabilities to escalate the impact of an attack.
