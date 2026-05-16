---
platform: android
title: Native Code Exposed Through WebViews
id: MASTG-TEST-0334
type: [static]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0011, MASTG-BEST-0012, MASTG-BEST-0013, MASTG-BEST-0035]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0018]
prerequisites:
- identify-security-relevant-contexts
---

## Overview

This test verifies Android apps that use WebViews with [legacy WebView-Native bridges](https://developer.android.com/develop/ui/views/layout/webapps/native-api-access-jsbridge#addjavascriptinterface) do not expose native code to websites loaded inside the WebView.

These bridges are created by registering a Java object with the WebView through [`addJavascriptInterface`](https://developer.android.com/reference/kotlin/android/webkit/WebView#addjavascriptinterface). Public methods of that object that are annotated with [`@JavascriptInterface`](https://developer.android.com/reference/android/webkit/JavascriptInterface) become callable from JavaScript running inside the WebView, using the provided `name` as the global JavaScript object.

For this mechanism to work, JavaScript execution must be enabled on the WebView by calling [`WebSettings.setJavaScriptEnabled(true)`](https://developer.android.com/reference/android/webkit/WebSettings#setJavaScriptEnabled(boolean)) (default is `false`), since the exposed interface is invoked from JavaScript code executed within the page.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for references to the relevant WebView APIs.

## Observation

The output should contain any references to the relevant WebView APIs.

## Evaluation

The test case fails if all the following are true:

- `setJavaScriptEnabled` is explicitly set to `true`.
- `addJavascriptInterface` is used at least once.
- At least one method annotated with `@JavascriptInterface` handles sensitive data or actions and is reachable from untrusted content. See below.

**Context Considerations**:

To reduce false positives, make sure you understand the context in which the bridge is being used before reporting the associated code as insecure. Ensure that it is being used in a security-relevant context to protect sensitive data or actions, and that it is reachable from untrusted content. For example, if the WebView can load arbitrary or weakly validated URLs, or if the app does not implement proper origin allowlisting for the bridge.

**Well-known Challenges when testing for WebView-Native bridges**:

- The app may use parametrized or indirect calls to these APIs, for example through utility methods or wrapper classes. Static analysis may not be able to resolve these calls, and dynamic analysis may require specific app states or user interactions to trigger them.
- The app may use several WebViews with different configurations, and it may be difficult to determine which values are set for each WebView instance, especially if they are created dynamically, in different code paths or even across different files.
- The app may use obfuscation, reflection, or dynamic code loading to hide the use of these APIs.
