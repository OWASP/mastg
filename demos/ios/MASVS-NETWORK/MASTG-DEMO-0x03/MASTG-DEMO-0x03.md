---
platform: ios
title: Runtime Use of Certificate Pinning APIs
code: [swift]
id: MASTG-DEMO-0x03
test: MASTG-TEST-0x03
kind: fail
status: draft
---

## Sample

The sample below shows an app that implements `URLSessionDelegate` to perform manual server trust evaluation. However, the implementation is flawed: it calls `completionHandler(.useCredential, credential)` unconditionally without comparing the server's certificate against any expected pinned value:

{{ MastgTest.swift }}

## Steps

1. Ensure the device is prepared for dynamic analysis (see @MASTG-TECH-0090).
2. Use @MASTG-TECH-0064 to attempt to bypass certificate pinning and identify which pinning APIs are being hooked. Run the following @MASTG-TOOL-0038 command:

    ```bash
    ios sslpinning disable
    ```

3. Alternatively, use @MASTG-TECH-0086 with @MASTG-TOOL-0031 to trace `URLSessionDelegate` method calls. For example:

    ```bash
    frida-trace -U -f org.owasp.mastestapp \
      -m '-[* URLSession:didReceiveChallenge:completionHandler:]' \
      -m '-[* webView:didReceiveAuthenticationChallenge:completionHandler:]' \
      > output.txt
    ```

## Observation

The output should contain a list of certificate pinning-related delegate method calls observed at runtime, including the class and method names.

## Evaluation

The test fails in this case because the `urlSession(_:didReceive:completionHandler:)` delegate method is called but the implementation accepts any server credential unconditionally without verifying the server certificate against a pinned value.
