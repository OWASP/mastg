---
platform: ios
title: URLSessionDelegate Accepting Any Server Certificate
code: [swift]
id: MASTG-DEMO-0154
test: MASTG-TEST-0396
kind: fail
---

## Sample

The code below implements two `URLSessionDelegate` classes that both connect to `expired.badssl.com`. `InsecureURLSessionDelegate` calls `completionHandler(.useCredential, URLCredential(trust: serverTrust))` without first calling `SecTrustEvaluateWithError`, accepting the expired certificate. `SecureURLSessionDelegate` correctly calls `SecTrustEvaluateWithError` and rejects the connection when trust evaluation fails.

{{ MastgTest.swift }}

## Steps

1. Extract the app (@MASTG-TECH-0058) and locate the main binary `./Payload/MASTestApp.app/MASTestApp`.
2. Run @MASTG-TOOL-0073 with the script to identify all URLSession authentication challenge handlers and determine which ones call `SecTrustEvaluateWithError`.

{{ auth_challenge.r2 }}

{{ run.sh }}

## Observation

The output contains five sections followed by separate disassembly files for each handler:

- **Custom authentication-challenge handlers**: lists every function whose signature references `NSURLAuthenticationChallenge`. Both `InsecureURLSessionDelegate` (`0x00004000`) and `SecureURLSessionDelegate` (`0x00004490`) appear here because both implement a custom challenge handler. This is the broad signal that the app has taken over part of the server trust evaluation, regardless of whether it does so correctly.
- **Accessors into the challenge protection space**: the `objc_msgSend$protectionSpace` (`0x000165e0`) and `objc_msgSend$serverTrust` (`0x00016620`) stubs confirm the app reaches into `challenge.protectionSpace.serverTrust`, an indication of manual server trust handling.
- **xrefs to URLSession challenge handler implementations**: `axff` on both ObjC challenge handler methods shows their calls to the underlying Swift implementations. `InsecureURLSessionDelegate`'s ObjC method (`0x41f8`) calls the Swift implementation at `0x00004000`. `SecureURLSessionDelegate`'s ObjC method (`0x4780`) calls its Swift implementation at `0x00004490`.
- **Uses of SecTrustEvaluateWithError**: confirms `SecTrustEvaluateWithError` is imported into the binary (`imp.SecTrustEvaluateWithError` at `0x000161bc`).
- **xrefs to SecTrustEvaluateWithError**: only `SecureURLSessionDelegate`'s Swift implementation (`0x4490`) calls `SecTrustEvaluateWithError`, at offset `0x4638`. `InsecureURLSessionDelegate`'s implementation (`0x4000`) has no entry here.

Reviewing the disassembled code (@MASTG-TECH-0076), the disassembly and AI-reversed Swift below show the insecure handler:

{{ output.txt # InsecureURLSessionDelegate.asm # InsecureURLSessionDelegate_ai_reversed.swift }}

## Evaluation

Both delegates surface in the "Custom authentication-challenge handlers" section, so both have taken control of the server trust evaluation and warrant manual review. The cross-reference to `SecTrustEvaluateWithError` is what distinguishes the secure handler (`0x4490`) from the insecure one (`0x4000`).

The test case fails because `InsecureURLSessionDelegate`'s implementation (`0x00004000`) does not appear in the "xrefs to SecTrustEvaluateWithError" section.

The disassembly confirms this:

- `serverTrust` is obtained at `0x00004064`.
- the only check is a `nil` guard at `0x0000408c` (`cbz x0, 0x4120`).
- `NSURLCredential` is created directly at `0x000040d8` with no call to `SecTrustEvaluateWithError` anywhere in the function.

The AI-reversed Swift makes the pattern explicit: any non-`nil` trust object is accepted unconditionally.

In contrast, `SecureURLSessionDelegate`'s implementation (`0x00004490`) calls `SecTrustEvaluateWithError` at `0x00004638` and only creates a `URLCredential` if that call returns `true` (`tbz w0, 0, 0x46a8` branches to the cancel path on failure):

{{ output.txt # SecureURLSessionDelegate.asm # SecureURLSessionDelegate_ai_reversed.swift }}
