---
platform: ios
title: Runtime Use of Certificate Pinning APIs
id: MASTG-TEST-0x03
type: [dynamic]
weakness: MASWE-0047
profiles: [L2]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0072]
---

## Overview

iOS apps can implement certificate pinning using different APIs. At runtime, pinning is typically enforced via:

- The [`URLSessionDelegate`](https://developer.apple.com/documentation/foundation/urlsessiondelegate) method [`urlSession(_:didReceive:completionHandler:)`](https://developer.apple.com/documentation/foundation/urlsessiondelegate/1409308-urlsession) for manual server trust evaluation.
- Third-party libraries such as [TrustKit](https://github.com/datatheorem/TrustKit), [Alamofire](https://github.com/Alamofire/Alamofire), or [AFNetworking](https://github.com/AFNetworking/AFNetworking), which typically wrap `URLSession` delegate methods.
- The `WKNavigationDelegate` method [`webView(_:didReceive:completionHandler:)`](https://developer.apple.com/documentation/webkit/wknavigationdelegate/1455638-webview) for `WKWebView`-based pinning.

This test uses dynamic instrumentation to trace certificate pinning API calls at runtime, which helps to determine whether the app is actually invoking pinning logic during network communication and to identify the specific APIs used. This can be used to supplement static analysis, especially when the app uses obfuscation or dynamically loaded code.

!!! note
    A positive result (pinning APIs being called) doesn't confirm that pinning is correctly implemented. The implementation may still be flawed (for example, accepting any credential unconditionally). Use this test together with @MASTG-TEST-0x04 to verify that pinning is enforced end-to-end.

## Steps

1. Ensure the device is prepared for dynamic analysis (see @MASTG-TECH-0090).
2. Use @MASTG-TECH-0064 to attempt to bypass certificate pinning and identify which pinning APIs are being hooked.
3. Alternatively, use @MASTG-TECH-0086 to method-trace the relevant certificate pinning APIs, such as:
    - `URLSessionDelegate` methods: `-[* URLSession:didReceiveChallenge:completionHandler:]`
    - `SecTrustEvaluateWithError`
    - `SecTrustEvaluate` (deprecated but still in use)
    - `WKNavigationDelegate` methods: `-[* webView:didReceiveAuthenticationChallenge:completionHandler:]`

## Observation

The output should contain a list of certificate pinning API calls observed at runtime, including the methods invoked and the class names that implement them.

## Evaluation

The test case fails if no certificate pinning APIs are called during active network communication with the app's backend endpoints, indicating that the app doesn't implement runtime certificate pinning.

Note that a missing API call could also indicate that:

- The app uses ATS `NSPinnedDomains` (system-level pinning not visible via method tracing).
- The app uses pinning implemented in native code, which requires additional analysis.

In either case, complement this test with static analysis (@MASTG-TEST-0x01) and network interception (@MASTG-TEST-0x04) to get a complete picture.
