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

If the app constructs an outgoing URL using untrusted data (such as query parameters from an inbound Universal Link) and passes it to [`UIApplication.open(_:options:completionHandler:)`](https://developer.apple.com/documentation/uikit/uiapplication/1648685-open) without sanitization, an attacker can trigger URI Scheme Hijacking by controlling the destination of the outgoing link. This test verifies whether the app blindly opens attacker controlled deep links without prior validation.

## Steps

1. Install the target application on a jailbroken device or Corellium instance (@MASTG-TECH-0056).
2. Use runtime method hooking and trace calls to `-[UIApplication openURL:options:completionHandler:]` while triggering the app's external routing logic with a crafted inbound link to capture outgoing URL payloads.

## Observation

The output should contain the outgoing URL payloads captured when the application attempts to hand off an external URL scheme to the operating system.

## Evaluation

The test case fails if the application dynamically executes an outgoing link containing an untrusted custom scheme or malicious payload without prior sanitization or explicit user consent.
