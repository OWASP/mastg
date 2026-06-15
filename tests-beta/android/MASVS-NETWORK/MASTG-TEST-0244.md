---
title: Missing Certificate Pinning in Network Traffic
platform: network
id: MASTG-TEST-0244
type: [dynamic, network]
weakness: MASWE-0047
profiles: [L2]
knowledge: [MASTG-KNOW-0015]
---

## Overview

There are multiple ways an application can implement certificate pinning, including via the Android Network Security Config, custom TrustManager implementations, third-party libraries, and native code. Since some implementations might be difficult to identify through static analysis, especially when obfuscation or dynamic code loading is involved, this test uses network interception techniques to determine if certificate pinning is enforced at runtime.

The goal of this test case is to observe whether a [MITM attack](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) can intercept HTTPS traffic from the app. A successful MITM interception indicates that the app is either not using certificate pinning or implementing it incorrectly.

If the app is properly implementing certificate pinning, the MITM attack should fail because the app rejects certificates issued by an unauthorized CA, even if the CA is trusted by the system.

_Testing Tip:_ While performing the MITM attack, it can be useful to monitor the system logs (see @MASTG-TECH-0009). If a certificate pinning/validation check fails, an event similar to the following log entry might be visible, indicating that the app detected the MITM attack and did not establish a connection.

`I/X509Util: Failed to validate the certificate chain, error: Pin verification failed`

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0011 to set up an interception proxy and to intercept the communication.

## Observation

The output should contain the intercepted traffic capture.

## Evaluation

The test case fails if any relevant domain appears in the intercepted traffic capture.
