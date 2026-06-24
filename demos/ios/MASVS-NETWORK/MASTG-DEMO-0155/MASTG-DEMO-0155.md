---
platform: ios
title: WKNavigationDelegate Accepting Any Server Certificate
code: [swift]
id: MASTG-DEMO-0155
test: MASTG-TEST-0397
kind: fail
---

## Sample

The code below implements `WKNavigationDelegate` with a `webView(_:didReceive:completionHandler:)` override that calls `completionHandler(.useCredential, URLCredential(trust: serverTrust))` without first calling `SecTrustEvaluateWithError`. This accepts any certificate the server presents in a `WKWebView`, regardless of whether it is expired, self-signed, or issued for the wrong hostname.

The WebView is used to load `self-signed.badssl.com`, which serves a self-signed certificate that is not trusted by the iOS system trust store. A correctly implemented delegate would cancel this connection.

{{ MastgTest.swift }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the main binary `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to identify the WKNavigationDelegate authentication challenge handler and determine whether `SecTrustEvaluateWithError` is called.

{{ webview_auth_challenge.r2 }}

{{ run.sh }}

## Observation

The output contains five sections followed by the disassembly file for the handler:

- **Custom authentication-challenge handlers**: lists every function whose signature references `NSURLAuthenticationChallenge`. `InsecureWKNavigationDelegate` (`0x00004000`) appears here because it implements a custom challenge handler. This is the broad signal that the app has taken over part of the server trust evaluation, regardless of whether it does so correctly.
- **Accessors into the challenge protection space**: the `objc_msgSend$protectionSpace` (`0x00015980`) and `objc_msgSend$serverTrust` (`0x000159e0`) stubs confirm the app reaches into `challenge.protectionSpace.serverTrust`, an indication of manual server trust handling.
- **xrefs to WKNavigationDelegate challenge handler implementation**: `axff` on the ObjC challenge handler method shows its call to the Swift implementation. `InsecureWKNavigationDelegate`'s ObjC method (`0x41f8`) calls the Swift implementation at `0x00004000`.
- **SecTrustEvaluateWithError calls**: this section is empty. `SecTrustEvaluateWithError` is not imported into the binary, confirming it is never called by any challenge handler.
- **xrefs to SecTrustEvaluateWithError**: empty for the same reason.

Reviewing the disassembled code (@MASTG-TECH-0076), the disassembly and AI-reversed Swift below confirm the insecure handler:

{{ output.txt # InsecureWKNavigationDelegate.asm # InsecureWKNavigationDelegate_ai_reversed.swift }}

## Evaluation

`InsecureWKNavigationDelegate` surfaces in the "Custom authentication-challenge handlers" section, so it has taken control of the server trust evaluation and warrants manual review.

The test case fails because `SecTrustEvaluateWithError` is not imported into the binary at all — the "SecTrustEvaluateWithError calls" section in `output.txt` is empty.

The disassembly confirms this:

- `serverTrust` is obtained at `0x00004064`.
- the only check is a `nil` guard at `0x0000408c` (`cbz x0, 0x4120`).
- `NSURLCredential` is created directly at `0x000040d8` with no call to `SecTrustEvaluateWithError` anywhere in the function.

The AI-reversed Swift makes the pattern explicit: any non-`nil` trust object presented to the `WKWebView` is accepted unconditionally.
