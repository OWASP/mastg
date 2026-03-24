---
platform: ios
title: Runtime Tracing of the Universal Link Receiver Method
id: MASTG-TEST-0070-8
type: [dynamic]
weakness: MASWE-0083
profiles: [L1, L2]
best-practices: [MASTG-BEST-0x70-1]
knowledge: [MASTG-KNOW-0080]
---

## Overview

If the app processes Universal Link payloads without validating the URL components received via [`NSUserActivity.webpageURL`](https://developer.apple.com/documentation/foundation/nsuseractivity/1418086-webpageurl), it may blindly accept untrusted input and route the user or alter application state based on attacker controlled data. This test verifies whether the app sanitizes URL components before acting on them by tracing the method at runtime.

## Steps

1. Install the target application on a jailbroken device (@MASTG-TECH-0056).
2. Use runtime method hooking and trace calls to [`NSUserActivity.webpageURL`](https://developer.apple.com/documentation/foundation/nsuseractivity/1418086-webpageurl) while triggering a Universal Link payload to observe how the app processes the received URL.
3. Trigger a Universal Link payload and monitor the application's runtime behavior in memory.

## Observation

The output should contain the intercepted method calls, displaying the exact raw URL the application extracted from the OS and is actively processing.

## Evaluation

The test case fails if the dynamic instrumentation shows that the application receives an untrusted or malicious Universal Link and pulls the payload into active memory (e.g., extracting tokens or routing paths) without rejecting it or validating the host components.
