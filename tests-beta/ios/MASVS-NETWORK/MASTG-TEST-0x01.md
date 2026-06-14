---
title: Endpoint Identity Verification Not Enforced
platform: ios
id: MASTG-TEST-0x01
type: [static, dynamic]
weakness: MASWE-0052
best-practices: [MASTG-BEST-0x01]
profiles: [L1, L2]
---

## Overview

This test evaluates whether an iOS app properly validates the server's certificate and hostname during TLS connections. Improper or disabled certificate validation allows attackers to perform [Machine-in-the-Middle (MITM)](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) attacks using self-signed or invalid certificates, compromising the confidentiality and integrity of network communications.

iOS apps that use the [URL Loading System](https://developer.apple.com/documentation/foundation/url_loading_system) (typically via `URLSession`) benefit from [App Transport Security (ATS)](https://developer.apple.com/documentation/bundleresources/information_property_list/nsapptransportsecurity) (@MASTG-KNOW-0071), which enforces proper certificate validation by default. However, apps may:

- Disable ATS entirely or for specific domains using configuration exceptions in `Info.plist`.
- Use lower-level networking APIs (e.g., BSD Sockets, `Network` framework) that are not protected by ATS.
- Implement custom certificate validation logic that may be insecure.

**Note:** If custom certificate pinning or custom trust evaluation is implemented but insecurely, it may also fall under @MASTG-TEST-0068. This test focuses on whether basic certificate and hostname validation is performed at all.

## Steps

1. Review the app's `Info.plist` file (@MASTG-TECH-0092) and check for ATS exceptions under the `NSAppTransportSecurity` key:
    - Global exceptions like `NSAllowsArbitraryLoads` which disable ATS for all connections.
    - Domain-specific exceptions such as `NSExceptionAllowsInsecureHTTPLoads` or lowered TLS requirements.
2. Reverse engineer the app (@MASTG-TECH-0058) and run a static analysis tool such as @MASTG-TOOL-0073 on the app binary, or use a dynamic analysis tool like @MASTG-TOOL-0039, and look for:
    - Use of lower-level networking APIs (`CFStream`, BSD Sockets, `Network` framework) that bypass ATS.
    - Custom implementations of `URLSessionDelegate` methods that override certificate validation.
    - Methods like `urlSession(_:didReceive:completionHandler:)` that may accept invalid certificates.
3. Intercept the app's network traffic using @MASTG-TECH-0062:
    - First, try to intercept traffic **without** installing a proxy certificate on the device. If traffic is visible, the app accepts any certificate.
    - If that fails, install the proxy's CA certificate on the device and mark it as trusted. If traffic is now visible, the app validates certificates against the system trust store but does not implement certificate pinning.

## Observation

The output should contain:

- Any ATS exceptions found in the `Info.plist` file.
- Code locations where certificate validation may be bypassed or improperly implemented.
- Network traffic capture results showing whether the app accepts:
    - Untrusted certificates (without installing the proxy CA).
    - Proxy certificates (after installing and trusting the proxy CA).

## Evaluation

The test fails if any of the following conditions are met:

- **ATS is globally disabled** using `NSAllowsArbitraryLoads` without proper justification or specific domain exceptions.
- **Insecure domain exceptions** are configured (e.g., `NSExceptionAllowsInsecureHTTPLoads: true`, `NSExceptionMinimumTLSVersion` set below TLS 1.2, or `NSExceptionRequiresForwardSecrecy: false`) for domains handling sensitive data.
- The app **uses lower-level networking APIs** without implementing proper certificate validation.
- The app **accepts any certificate** without validation, as evidenced by successful traffic interception without installing a trusted CA certificate.
- Custom certificate validation logic **unconditionally accepts** all certificates or hostnames.
- Custom `URLSessionDelegate` methods like `urlSession(_:didReceive:completionHandler:)` **bypass proper certificate checks** by always calling the completion handler with `.useCredential` or `.performDefaultHandling` without validating the server trust.

When evaluating, consider whether the insecure configuration or implementation is limited to development/testing scenarios or affects production connections to sensitive endpoints.
