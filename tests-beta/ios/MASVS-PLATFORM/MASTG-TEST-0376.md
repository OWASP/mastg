---
platform: ios
title: References to Native Bridge APIs in WebViews
id: MASTG-TEST-0376
type: [static, code]
weakness: MASWE-0069
best-practices: [MASTG-BEST-0058]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0076]
prerequisites:
- identify-security-relevant-contexts
---

## Overview

iOS apps can establish a bidirectional communication channel between JavaScript and native code through WebView-native bridges. When using `WKWebView`, message handlers are registered on the `WKUserContentController` via [`add(_:name:)`](https://developer.apple.com/documentation/webkit/wkusercontentcontroller/add(_:name:)) (or `addScriptMessageHandler:name:` in Objective-C). This defines a `WKScriptMessageHandler` that handles messages sent from JavaScript using `window.webkit.messageHandlers.<name>.postMessage(...)`.

If the app exposes sensitive native functionality or data through these handlers (for example, returning stored credentials, executing privileged operations, or modifying app state) without adequate input validation, an attacker who can execute JavaScript in the WebView (for example, through XSS, insecure content loading, or other injection vectors) can invoke these native methods and potentially extract data or trigger unauthorized actions.

This test checks whether the app registers native bridge handlers that expose sensitive functionality or data to JavaScript in a `WKWebView`.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations in the binary where `WKUserContentController.add(_:name:)` is called.

## Evaluation

The test case fails if a native bridge is registered and the `WKScriptMessageHandler` implementation exposes sensitive functionality or data to JavaScript without adequate validation.

**Further Validation Required:**

Inspect each reported call site using @MASTG-TECH-0076.

- Identify the registered handler name and its corresponding `WKScriptMessageHandler` class.
- Review the `userContentController(_:didReceive:)` implementation to understand what actions or data are exposed to JavaScript.
- Determine whether the handler processes messages in security-relevant contexts, such as reading stored credentials, executing privileged native operations, or modifying sensitive app state.
- Check whether the handler validates the content of the received message, and whether the WebView can load attacker-controlled content that could enable JavaScript injection.

Note that registering a native bridge handler isn't inherently insecure. The test fails only when the exposed functionality is security-relevant and reachable from untrusted JavaScript without adequate protection.
