---
title: Insecure TLS Protocols in Network Traffic
platform: android
id: MASTG-TEST-0218
type: [dynamic, network]
weakness: MASWE-0050
profiles: [L1, L2]
---

## Overview

While static analysis can identify configurations that allow insecure TLS versions, it may not accurately reflect the actual protocol used during live communications. This is because TLS version negotiation occurs between the client (app) and the server at runtime, where they agree on the most secure, mutually supported version.

By capturing and analyzing real network traffic, you can observe the TLS version actually negotiated and in use. This approach provides an accurate view of the protocol's security, accounting for the server's configuration, which may enforce or limit specific TLS versions.

In cases where static analysis is either incomplete or infeasible, examining network traffic can reveal instances where insecure TLS versions (e.g., TLS 1.0 or TLS 1.1) are actively in use.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0010 to capture the app traffic.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain the app traffic.

## Evaluation

The test case fails if any [insecure TLS version](../../../Document/0x04f-Testing-Network-Communication.md#recommended-tls-settings) is used.
