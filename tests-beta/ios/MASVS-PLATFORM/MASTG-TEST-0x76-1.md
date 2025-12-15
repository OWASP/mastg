---
platform: ios
title: Deprecated Usage of UIWebView
id: MASTG-TEST-0x76-1
type: [static]
available_since: 8.0
weakness: MASWE-0072
---

## Overview

In this test, we look for references to [`UIWebView`](../../../Document/0x06h-Testing-Platform-Interaction.md/#uiwebview), a deprecated component since iOS 12.0, in favor of `WKWebView`. `UIWebView` presents security and performance risks: it does not allow JavaScript to be fully disabled, lacks process isolation (which `WKWebView` provides), and doesn’t support modern web security features like Content Security Policy (CSP).

## Steps

1. Extract the app as described in @MASTG-TECH-0058.
2. Look for references to `UIWebView` in the app using @MASTG-TECH-0070 on all executables and libraries.

## Observation

The output should contain a list of locations where `UIWebViews` are used.

## Evaluation

The test case fails if there are any references to `UIWebView`.

For iOS apps on iOS 8.0 and above, using [`WKWebView`](../../../Document/0x06h-Testing-Platform-Interaction.md/#wkwebview) is essential. `WKWebView` provides enhanced security and control over web view behavior, including the ability to disable JavaScript by setting `javaScriptEnabled` to `false`, reducing the risk of script-based attacks. Additionally, `WKWebView` supports `hasOnlySecureContent`, which ensures that only secure (HTTPS) resources are loaded, further strengthening the app’s protection against insecure content and mixed content vulnerabilities.
