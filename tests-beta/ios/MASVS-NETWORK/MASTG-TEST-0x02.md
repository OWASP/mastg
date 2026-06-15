---
title: References to WKNavigationDelegate Bypassing Certificate Validation
platform: ios
id: MASTG-TEST-0x02
type: [static, code, manual]
weakness: MASWE-0052
best-practices: [MASTG-BEST-0x01]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0072]
---

## Overview

`WKWebView` handles server authentication challenges through `WKNavigationDelegate.webView(_:didReceive:completionHandler:)`. When the app provides a navigation delegate that implements this method, the WebView's default certificate validation is replaced by the app's own logic, completely bypassing the default App Transport Security (ATS) checks.

An insecure implementation calls `completionHandler(.useCredential, URLCredential(trust: serverTrust))` without first calling [`SecTrustEvaluateWithError`](https://developer.apple.com/documentation/security/sectrustevaluatewitherror(_:_:)) on the server's trust object. This bypasses certificate chain validation and hostname verification for every HTTPS page loaded in that `WKWebView`. An attacker can use any certificate (expired, self-signed, or for the wrong hostname) to intercept or tamper with WebView traffic via a [Machine-in-the-Middle (MITM)](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) attack.

This test checks whether the app implements `WKNavigationDelegate` in a way that accepts server certificates without calling `SecTrustEvaluateWithError`.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain:

- All implementations of `webView(_:didReceive:completionHandler:)` found in the binary.
- The list of callers of `SecTrustEvaluateWithError`, if the function is imported at all.

## Evaluation

The test case fails if an implementation of `webView(_:didReceive:completionHandler:)` is found in a `WKNavigationDelegate` that has no corresponding cross-reference to `SecTrustEvaluateWithError`.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to confirm the certificate validation bypass. Look for cases such as:

- **Accepting a credential without trust evaluation:** calling `completionHandler(.useCredential, URLCredential(trust: serverTrust))` without first calling `SecTrustEvaluateWithError(serverTrust, &error)` and verifying it returns `true`.
- **Ignoring the challenge type:** not checking `challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust` before accepting a credential.
- **Swallowing evaluation errors:** wrapping `SecTrustEvaluateWithError` in a `do/catch` or ignoring its return value and calling `completionHandler(.useCredential, ...)` regardless of the outcome.
