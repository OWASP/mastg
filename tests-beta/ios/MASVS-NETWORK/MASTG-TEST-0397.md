---
title: References to WKNavigationDelegate Bypassing Certificate Validation
platform: ios
id: MASTG-TEST-0397
type: [static, code, manual]
weakness: MASWE-0052
best-practices: [MASTG-BEST-0073]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0072]
---

## Overview

`WKWebView` handles server authentication challenges through `WKNavigationDelegate.webView(_:didReceive:completionHandler:)`. When the app provides a navigation delegate that implements this method, the WebView's default certificate validation is replaced by the app's own logic, completely bypassing the default App Transport Security (ATS) checks.

An insecure implementation calls `completionHandler(.useCredential, URLCredential(trust: serverTrust))` without first calling [`SecTrustEvaluateWithError`](https://developer.apple.com/documentation/security/sectrustevaluatewitherror(_:_:)) on the server's trust object. This bypasses certificate chain validation and hostname verification for every HTTPS page loaded in that `WKWebView`. An attacker can use any certificate (expired, self-signed, or for the wrong hostname) to intercept or tamper with WebView traffic via a [Machine-in-the-Middle (MITM)](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) attack.

This test checks whether the app implements `WKNavigationDelegate` in a way that accepts server certificates without calling `SecTrustEvaluateWithError`.

More broadly, any reference to [`URLAuthenticationChallenge`](https://developer.apple.com/documentation/foundation/urlauthenticationchallenge) only appears when the app implements this authentication-challenge method, which means it has taken over part of the WebView's server trust evaluation. Checking for the absence of `SecTrustEvaluateWithError` is an efficient heuristic to prioritize the most likely bypasses, but it isn't a substitute for reviewing all custom challenge handling: an implementation that does call `SecTrustEvaluateWithError` may still evaluate trust incompletely or incorrectly. Treat every code path that handles a `URLAuthenticationChallenge` as a candidate for manual review.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain:

- All implementations of `webView(_:didReceive:completionHandler:)` found in the binary.
- Any references to `URLAuthenticationChallenge` (for example, accessing `challenge.protectionSpace.serverTrust`), which only appear when the app performs custom authentication-challenge handling.
- The list of callers of `SecTrustEvaluateWithError`, if the function is imported at all.

## Evaluation

The test case fails if an implementation of `webView(_:didReceive:completionHandler:)` is found in a `WKNavigationDelegate` that has no corresponding cross-reference to `SecTrustEvaluateWithError`.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to confirm the certificate validation bypass. Look for cases such as:

- **Accepting a credential without trust evaluation:** calling `completionHandler(.useCredential, URLCredential(trust: serverTrust))` without first calling `SecTrustEvaluateWithError(serverTrust, &error)` and verifying it returns `true`.
- **Ignoring the challenge type:** not checking `challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust` before accepting a credential.
- **Swallowing evaluation errors:** wrapping `SecTrustEvaluateWithError` in a `do/catch` or ignoring its return value and calling `completionHandler(.useCredential, ...)` regardless of the outcome.

> The absence of a `SecTrustEvaluateWithError` cross-reference is a heuristic, not a guarantee of a bypass, and its presence is not a guarantee of correct validation. Treat every implementation that accesses `URLAuthenticationChallenge` as a candidate for manual review, since it has taken control of the server trust evaluation regardless of whether `SecTrustEvaluateWithError` is called.
