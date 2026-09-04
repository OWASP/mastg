---
title: References to WKNavigationDelegate Bypassing Certificate Validation
platform: ios
id: MASTG-TEST-0397
type: [static, code, manual]
weakness: MASWE-0027
best-practices: [MASTG-BEST-0073]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0072]
---

## Overview

`WKWebView` handles server authentication challenges through `WKNavigationDelegate.webView(_:didReceive:completionHandler:)`. When the app provides a navigation delegate that implements this method, it can override the WebView's default server-trust handling. Returning `.performDefaultHandling` preserves the platform's default handling, while explicitly accepting a server trust object without proper evaluation can bypass certificate chain validation and hostname verification.

An insecure implementation calls `completionHandler(.useCredential, URLCredential(trust: serverTrust))` without first successfully evaluating the server's trust object, for example with [`SecTrustEvaluateWithError`](https://developer.apple.com/documentation/security/sectrustevaluatewitherror(_:_:)) or [`SecTrustEvaluateAsyncWithError`](https://developer.apple.com/documentation/security/sectrustevaluateasyncwitherror(_:_:_:)). This can allow certificates that would otherwise fail validation (expired, self-signed, or for the wrong hostname) to be accepted, enabling an attacker to intercept or tamper with WebView traffic via a [Machine-in-the-Middle (MITM)](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) attack.

This test checks whether the app implements `WKNavigationDelegate` in a way that accepts server certificates without successfully evaluating server trust.

More broadly, references to [`URLAuthenticationChallenge`](https://developer.apple.com/documentation/foundation/urlauthenticationchallenge), especially accesses such as `challenge.protectionSpace.serverTrust`, can indicate custom authentication-challenge handling. Checking for the absence of trust-evaluation APIs such as `SecTrustEvaluateWithError` or `SecTrustEvaluateAsyncWithError` is an efficient heuristic to prioritize the most likely bypasses, but it isn't a substitute for reviewing all custom challenge handling: an implementation that does call a trust-evaluation API may still ignore or incorrectly handle its result. Treat every relevant code path that handles a `URLAuthenticationChallenge` as a candidate for manual review.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain:

- All implementations of `webView(_:didReceive:completionHandler:)` found in the binary.
- Any references to `URLAuthenticationChallenge` or server-trust handling (for example, accessing `challenge.protectionSpace.serverTrust`).
- The list of callers of `SecTrustEvaluateWithError` and `SecTrustEvaluateAsyncWithError`, if these functions are imported at all.

## Evaluation

The test case fails if an implementation of `webView(_:didReceive:completionHandler:)` in a `WKNavigationDelegate` accepts a server-trust credential without successful trust evaluation, or accepts the credential regardless of the evaluation result.

The absence of a corresponding cross-reference to `SecTrustEvaluateWithError`, `SecTrustEvaluateAsyncWithError`, or an equivalent trust-evaluation API can be used to identify candidates for further validation, but is not by itself sufficient to determine that the test fails.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to confirm the certificate validation bypass. Look for cases such as:

- **Accepting a credential without trust evaluation:** calling `completionHandler(.useCredential, URLCredential(trust: serverTrust))` without first successfully evaluating `serverTrust`.
- **Ignoring the challenge type:** not checking `challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust` before accepting a server-trust credential.
- **Ignoring the evaluation result:** calling `SecTrustEvaluateWithError`, `SecTrustEvaluateAsyncWithError`, or an equivalent trust-evaluation API but calling `completionHandler(.useCredential, ...)` even when evaluation fails.

> The absence of a trust-evaluation API cross-reference is a heuristic, not a guarantee of a bypass, and its presence is not a guarantee of correct validation. Confirm that `.useCredential` is only reached when server-trust evaluation succeeds. Returning `.performDefaultHandling` does not bypass the platform's default challenge handling.
