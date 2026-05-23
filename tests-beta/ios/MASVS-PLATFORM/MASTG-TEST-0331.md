---
platform: ios
title: Use of Deprecated WebView APIs
id: MASTG-TEST-0331
type: [static, code]
available_since: 2.0
deprecated_since: 12.0
weakness: MASWE-0072
profiles: [L1, L2]
best-practices: [MASTG-BEST-0032]
knowledge: [MASTG-KNOW-0076]
---

## Overview

In this test, we look for references to `UIWebView` (@MASTG-KNOW-0076), a deprecated component since iOS 12.0, in favor of `WKWebView`. `UIWebView` presents security and performance risks: it does not allow JavaScript to be fully disabled, lacks process isolation (which `WKWebView` provides), and doesn't support modern web security features like Content Security Policy (CSP).

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where `UIWebView`s are used.

## Evaluation

The test case fails if any use of `UIWebView` is found in the app.
