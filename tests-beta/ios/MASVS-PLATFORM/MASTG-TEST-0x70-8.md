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

Once a Universal Link routes to the application, dynamic instrumentation can be used to trace exactly how the app processes the URL payload at runtime. Hooking [`NSUserActivity.webpageURL`](https://developer.apple.com/documentation/foundation/nsuseractivity/1418086-webpageurl) exposes whether the application blindly accepts unvalidated input or properly sanitizes the URL components before routing the user or changing application state.

## Steps

1. Install the target application on a jailbroken device or Corellium instance (@MASTG-TECH-0056).
2. Utilize method hooking techniques (@MASTG-TECH-0095) to intercept the `NSUserActivity` class and capture calls to the `webpageURL` selector.
3. Trigger a Universal Link payload and monitor the application's runtime behavior in memory.

## Observation

The output should contain the intercepted method calls, displaying the exact raw URL the application extracted from the OS and is actively processing.

## Evaluation

The test case fails if the dynamic instrumentation shows that the application receives an untrusted or malicious Universal Link and pulls the payload into active memory (e.g., extracting tokens or routing paths) without rejecting it or validating the host components.
