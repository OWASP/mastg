---
title: Properly Validate Server Trust in URLSessionDelegate and WKNavigationDelegate
alias: properly-validate-server-trust-in-delegate
id: MASTG-BEST-0x01
platform: ios
knowledge: [MASTG-KNOW-0072]
---

When an iOS app overrides the default certificate validation by implementing [`URLSessionDelegate.urlSession(_:didReceive:completionHandler:)`](https://developer.apple.com/documentation/foundation/urlsessiondelegate/urlsession(_:didreceive:completionhandler:)) or [`WKNavigationDelegate.webView(_:didReceive:completionHandler:)`](https://developer.apple.com/documentation/webkit/wknavigationdelegate/webview(_:didreceive:completionhandler:)), it takes full control of the server trust evaluation. An incorrect implementation that accepts credentials without calling [`SecTrustEvaluateWithError`](https://developer.apple.com/documentation/security/sectrustevaluatewitherror(_:_:)) bypasses certificate chain validation and hostname verification, leaving connections open to Machine-in-the-Middle (MITM) attacks.

See ["Performing manual server trust authentication"](https://developer.apple.com/documentation/foundation/url_loading_system/handling_an_authentication_challenge/performing_manual_server_trust_authentication) in the Apple Developer Documentation for more information.

## Prefer the Default ATS Trust Evaluation

The safest approach is to **not implement** `urlSession(_:didReceive:completionHandler:)` or `webView(_:didReceive:completionHandler:)` at all. When these methods are absent, the URL Loading System and `WKWebView` perform the full ATS-enforced server trust evaluation automatically. Override these methods only when the app has a specific justified requirement (for example, certificate pinning or connecting to a development server with a self-signed certificate). Still, you should always prefer to use ATS and if necessary, perform server-side fixes to support secure connections rather than implementing custom validation logic in the app.

## Perform Explicit Server Trust Evaluation

If you must handle the challenge, always:

1. Confirm the challenge is of type `NSURLAuthenticationMethodServerTrust`.
2. Obtain the `serverTrust` object from `challenge.protectionSpace.serverTrust`.
3. Call `SecTrustEvaluateWithError` and verify it returns `true`.
4. Call `completionHandler(.useCredential, URLCredential(trust: serverTrust))` only when evaluation succeeds.
5. Call `completionHandler(.cancelAuthenticationChallenge, nil)` on any other challenge type or when evaluation fails.
