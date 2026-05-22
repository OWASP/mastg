---
title: Debugging Enabled for WebViews
platform: android
id: MASTG-TEST-0227
type: [static, code]
weakness: MASWE-0067
best-practices: [MASTG-BEST-0008]
profiles: [R]
knowledge: [MASTG-KNOW-0028]
---

## Overview

The `WebView.setWebContentsDebuggingEnabled(true)` API enables debugging for **all** WebViews in the application. This feature can be useful during development, but introduces significant security risks if left enabled in production. When enabled, a connected PC can debug, eavesdrop, or modify communication within any WebView in the application. See the ["Android Documentation"](https://developer.chrome.com/docs/devtools/remote-debugging/webviews/#configure_webviews_for_debugging) for more details.

Note that this flag works independently of the `debuggable` attribute (`ApplicationInfo.FLAG_DEBUGGABLE`) in the `AndroidManifest.xml` (see @MASTG-TEST-0226). Even if the app is not marked as debuggable, the WebViews can still be debugged by calling this API.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should list:

- All locations where `WebView.setWebContentsDebuggingEnabled` is called with `true` at runtime.
- Any references to `ApplicationInfo.FLAG_DEBUGGABLE`.

## Evaluation

The test case fails if `WebView.setWebContentsDebuggingEnabled(true)` is called unconditionally or in contexts where the `ApplicationInfo.FLAG_DEBUGGABLE` flag is not checked.
