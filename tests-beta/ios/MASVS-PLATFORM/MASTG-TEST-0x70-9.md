---
platform: ios
title: Runtime Tracing of Outgoing External Links
id: MASTG-TEST-0070-9
type: [dynamic]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

Applications often execute external actions by passing URLs to the operating system via [`UIApplication.open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-open). If the application constructs this outgoing URL using untrusted data (like query parameters from an inbound Universal Link), dynamic instrumentation can reveal if the app is vulnerable to URI Scheme Hijacking by blindly opening attacker-controlled deep links.

## Steps

1. Install the target application on a jailbroken device or Corellium instance (@MASTG-TECH-0056).
2. Utilize method hooking techniques (@MASTG-TECH-0095) to intercept `-[UIApplication openURL:options:completionHandler:]` and capture outgoing URL requests.
3. Interact with the app or feed it a malicious inbound link to trigger the logic that handles external routing.

## Observation

The output should contain the outgoing URL payloads captured when the application attempts to hand off an external URL scheme to the operating system.

## Evaluation

The test case fails if the application dynamically executes an outgoing link containing an untrusted custom scheme or malicious payload without prior sanitization or explicit user consent.
