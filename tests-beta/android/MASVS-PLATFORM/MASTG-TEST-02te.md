---
platform: android
title: Native Code Exposed Through WebViews
id: MASTG-TEST-02te
type: [static]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0011, MASTG-BEST-0012, MASTG-BEST-0013, MASTG-BEST-00be]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0018]
---

## Overview

This test verifies Android apps that use WebViews with WebView-Native bridges do not expose native code to websites loaded inside the WebView.

These bridges can be added via the [`addJavascriptInterface`](https://developer.android.com/reference/kotlin/android/webkit/WebView#addjavascriptinterface) method in the `WebView` class. Their functionality requires that JavaScript is enabled on the WebView with [`WebSettings.setJavaScriptEnabled(true)`](https://developer.android.com/reference/android/webkit/WebSettings#setJavaScriptEnabled(boolean)). On API level 17 and above, the `@JavascriptInterface` annotation is required to expose methods to JavaScript.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0007 to determine the app's target API level.
3. Use @MASTG-TECH-0014 to look for references of:

   - the `setJavaScriptEnabled` method
   - the `addJavascriptInterface` method

### Sub-method 1

1. Use @MASTG-TECH-0014 to look for references to the `@JavascriptInterface` annotation.

### Sub-method 2

1. Install and run the app.
2. Navigate to the WebView you want to analyze.
3. Use @MASTG-TECH-0033 targeting the `@JavascriptInterface` and log their arguments.

## Observation

The output should contain the app's API level and a list of WebView instances, including the following methods and their arguments:

- `setJavaScriptEnabled` and if it's enabled or not
- `addJavascriptInterface` and their associated classes
- `@JavascriptInterface` and their associated methods

## Evaluation

The test fails automatically if all the following are true:

- The application is targeting API level 16 or lower.
- `setJavaScriptEnabled` is `true`.
- `addJavascriptInterface` is used at least once.

The test also fails, after evaluating the `addJavascriptInterface` method(s) and `@JavascriptInterface` annotation(s), if all the following are true:

- Sensitive data can be read through the interface methods.
- Actions that can affect the confidentiality, integrity, or availability of the application can be performed via the interface methods.

The test passes if any of the following are true:

- `setJavaScriptEnabled` is `false` or not used at all.
- `addJavascriptInterface` is not used at all.
