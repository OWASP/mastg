---
platform: network
title: Missing Certificate Pinning in Network Traffic
id: MASTG-TEST-0x04
type: [network]
weakness: MASWE-0047
profiles: [L2]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0072]
---

## Overview

There are multiple ways an iOS app can implement certificate pinning, including via ATS `NSPinnedDomains`, manual `URLSessionDelegate` trust evaluation, third-party libraries, and native code. Since some implementations might be difficult to identify through static analysis, especially when obfuscation or dynamic code loading is involved, this test uses network interception techniques to determine whether certificate pinning is enforced at runtime.

The goal of this test is to observe whether a MITM attack can intercept HTTPS traffic from the app. A successful MITM interception indicates that the app either doesn't use certificate pinning or implements it incorrectly.

If the app properly implements certificate pinning, the MITM attack should fail because the app rejects certificates issued by an unauthorized CA, even if that CA is trusted by the system.

_Testing Tip:_ While performing the MITM attack, monitor the system logs (see @MASTG-TECH-0060). If a certificate pinning check fails, log entries indicating a TLS handshake failure or a trust evaluation error may be visible.

## Steps

1. Set up an interception proxy (see @MASTG-TECH-0063).
2. Install the application on a device connected to that proxy, and intercept the communication.
3. Extract all domains for which the interception was successful.

## Observation

The output should contain a list of domains for which the interception was successful.

## Evaluation

The test case fails if any relevant domain was intercepted.
