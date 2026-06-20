---
platform: ios
title: Runtime Use of Virtual Device Detection Techniques
id: MASTG-TEST-0367
type: [dynamic]
weakness: MASWE-0099
best-practices: [MASTG-BEST-0053]
profiles: [R]
knowledge: [MASTG-KNOW-0135]
---

## Overview

This test verifies if the app implements checks to detect the presence of an iOS virtual device (like @MASTG-TOOL-0108) by attempting to hook into common virtual device detection mechanisms.

See @MASTG-KNOW-0135 for a detailed overview about virtual device detection mechanisms and patterns performed by applications.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of these mechanisms, which can be very difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0053 for best practices on implementing virtual device detection effectively.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain any instances of virtual device detection checks, along with the methods or APIs that were hooked.

## Evaluation

The test case fails if no instances of virtual device detection checks are observed.

**Expected False Negatives:**

This test may produce false negatives if the app uses virtual device detection mechanisms that are not covered by the hooks or traces used, or if the virtual device detection logic is implemented in a way that evades detection (for example, through obfuscation, dynamic code loading, or anti-instrumentation techniques). In such cases, the absence of findings does not guarantee the absence of virtual device detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze virtual device detection mechanisms.
