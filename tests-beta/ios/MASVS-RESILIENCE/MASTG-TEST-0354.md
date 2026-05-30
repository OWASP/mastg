---
platform: ios
title: Runtime Use of Hook Detection Techniques
id: MASTG-TEST-0354
type: [dynamic, hooks]
weakness: MASWE-0107
profiles: [R]
knowledge: [MASTG-KNOW-0087]
best-practices: [MASTG-BEST-0048]
---

## Overview

This test verifies whether the app detects and responds to instrumentation and hooking attempts at runtime. For example, if the app does not terminate immediately when the following APIs or functions are hooked:

- Keychain items, session tokens, credentials, and other secrets could be extracted if [`SecItemCopyMatching`](https://developer.apple.com/documentation/security/secitemcopymatching%28_:_:%29), [`SecItemAdd`](https://developer.apple.com/documentation/security/secitemadd%28_:_:%29), or [`SecItemUpdate`](https://developer.apple.com/documentation/security/secitemupdate%28_:_:%29) are hooked.
- Cryptographic keys, signatures, plaintext, or decrypted data could be extracted if [`SecKeyCreateSignature`](https://developer.apple.com/documentation/security/seckeycreatesignature%28_:_:_:_:%29), [`SecKeyCreateDecryptedData`](https://developer.apple.com/documentation/security/seckeycreatedecrypteddata%28_:_:_:_:%29), or [`CCCrypt`](https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man3/CCCrypt.3cc.html) are hooked.
- Authentication could be bypassed if [`LAContext.evaluatePolicy`](https://developer.apple.com/documentation/localauthentication/lacontext/evaluatepolicy%28_:localizedreason:reply:%29) or [`LAContext.evaluateAccessControl`](https://developer.apple.com/documentation/localauthentication/lacontext/evaluateaccesscontrol%28_:operation:localizedreason:reply:%29) are hooked.
- Sensitive network data could be extracted or modified if [`URLSession.dataTask(with:completionHandler:)`](https://developer.apple.com/documentation/foundation/urlsession/datatask%28with:completionhandler:%29-e6xv), [`URLSession.uploadTask(with:from:completionHandler:)`](https://developer.apple.com/documentation/foundation/urlsession/uploadtask%28with:from:completionhandler:%29), [`URLSession.downloadTask(with:completionHandler:)`](https://developer.apple.com/documentation/foundation/urlsession/downloadtask%28with:completionhandler:%29-7cuje), or [`URLSessionTask.resume`](https://developer.apple.com/documentation/foundation/urlsessiontask/resume%28%29) are hooked.
- Any other function that processes or returns sensitive data is hooked.

!!! warning
    This list is just indicative, and each app may have its own defensive response mechanisms.

!!! note "Out of Scope"
    This test does not assess the robustness or bypass-resistance of the hook detection mechanisms. Detection methods and bypass techniques evolve continuously, and determined attackers with sufficient time and resources can circumvent these protections, for example, by using advanced instrumentation mechanisms. These techniques should be part of a defense-in-depth strategy, not a standalone solution. See @MASTG-BEST-0048 for best practices on implementing effective runtime hook detection.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain one of the following:

- The expected hook callback data, (e.g., function arguments, return values, or backtraces).
- Session termination, script errors, empty responses, or absence of expected hook data.

## Evaluation

The test case fails if the hook executes successfully and returns the expected data, indicating the app lacks runtime integrity verification.

**Expected False Negatives:**

This test may produce false negatives if the selected hooks or traces do not cover the app's security-sensitive code paths, if the exercised flows do not trigger operations that process sensitive data, or if the app's runtime hook detection logic is implemented in a way that evades the instrumentation used in this test (for example, through obfuscation, dynamic loading, native code, anti-instrumentation techniques, or checks that run before the hooks are installed). In such cases, the absence of findings does not guarantee that the app has effective runtime hook detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze runtime hook detection mechanisms.
