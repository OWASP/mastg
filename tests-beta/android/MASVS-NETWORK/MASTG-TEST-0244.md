---
title: Missing Certificate Pinning in Network Traffic
platform: network
id: MASTG-TEST-0244
type: [dynamic, network]
weakness: MASWE-0028
profiles: [L2]
knowledge: [MASTG-KNOW-0015]
prerequisites:
- identify-first-party-domains
---

## Overview

There are multiple ways an application can implement certificate pinning, including via the Android Network Security Config, custom TrustManager implementations, third-party libraries, and native code. Since some implementations might be difficult to identify through static analysis, especially when obfuscation or dynamic code loading is involved, this test uses network interception techniques to determine if certificate pinning is enforced at runtime.

The goal of this test case is to observe whether a [MITM attack](../../../Document/0x04f-Testing-Network-Communication.md#intercepting-network-traffic-through-mitm) can intercept HTTPS traffic from the app. A successful MITM interception indicates that the app is either not using certificate pinning or implementing it incorrectly.

If the app is properly implementing certificate pinning, the MITM attack should fail because the app rejects certificates issued by an unauthorized CA, even if the CA is trusted by the system.

This test focuses on relevant first-party domains, which are remote endpoints under the developer's control that support the app's core or security-sensitive functionality. Third-party domains outside the developer's control should not be reported only because their traffic can be intercepted.

_Testing Tip:_ While performing the MITM attack, it can be useful to monitor the system logs (see @MASTG-TECH-0009). If a certificate pinning/validation check fails, an event similar to the following log entry might be visible, indicating that the app detected the MITM attack and did not establish a connection.

`I/X509Util: Failed to validate the certificate chain, error: Pin verification failed`

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0011 to set up an interception proxy and to intercept the communication.

## Observation

The output should contain the intercepted traffic capture, including the domains whose HTTPS traffic was successfully intercepted.

## Evaluation

The test case fails if any relevant first-party domain appears in the intercepted traffic capture.

The test case should not fail only because unrelated third-party domains are intercepted.

**Further Validation Required:**

Determining which of the intercepted domains are first-party and security-relevant typically requires information that is not present in the app binary and may require contact with the developers.
