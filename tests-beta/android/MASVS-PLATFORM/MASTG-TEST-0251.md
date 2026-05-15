---
platform: android
title: Runtime Use of Content Provider Access APIs in WebViews
alias: references-to-content-provider-access-in-webviews
id: MASTG-TEST-0251
apis: [WebView, WebSettings, getSettings, ContentProvider, setAllowContentAccess, setAllowUniversalAccessFromFileURLs, setJavaScriptEnabled]
type: [dynamic]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0011, MASTG-BEST-0012, MASTG-BEST-0013]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0018]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0250.

## Steps

1. Run a dynamic analysis tool like @MASTG-TOOL-0001 and either:
    - enumerate instances of `WebView` in the app and list their configuration values
    - or explicitly hook the setters of the `WebView` settings

## Observation

The output should contain a list of WebView instances and corresponding settings.

## Evaluation

The test case fails if all of the following applies:

- `JavaScriptEnabled` is `true`.
- `AllowContentAccess` is `true`.
- `AllowUniversalAccessFromFileURLs` is `true`.

You should use the list of content providers obtained in @MASTG-TEST-0250 to verify if they handle sensitive data.

!!! note
    `AllowContentAccess` being `true` does not represent a security vulnerability by itself, but it can be used in combination with other vulnerabilities to escalate the impact of an attack.
