---
platform: android
title: Native Code Exposed Through WebViews
id: MASTG-DEMO-00de
code: [kotlin]
test: MASTG-TEST-02te
---

### Sample

The following demo demonstrates a `WebView` component that exposes native functionality to JavaScript via the `addJavascriptInterface()` method, compromising the app's integrity and confidentiality.

{{ AndroidManifest.xml }}

### Steps

Let's run our @MASTG-TOOL-0110 rule against the reversed Java code.

{{ ../../../../rules/mastg-android-webview-bridges.yml }}

{{ run.sh }}

### Observation

The rule detected the location of a JavaScript/Native Bridge class (with three methods annotated with `@JavascriptInterface`). The `WebView` had `setJavaScriptEnabled` set to `true`, and the Bridge was passed through the `addJavascriptInterface()` method in that WebView.

{{ output.txt # output2.txt }}

### Evaluation

After reviewing the decompiled code at the location specified in the output (file and line number), we can conclude that the test fails because JavaScript is enabled in this webview, a WebView Bridge is attached, and this Bridge allows reading of sensitive data, specifically a first and a last name (PII) and a JWT via the `@JavascriptInterface` annotated methods.
