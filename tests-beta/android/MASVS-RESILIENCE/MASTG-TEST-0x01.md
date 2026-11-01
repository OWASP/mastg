---
platform: android
title: Behavioral Check of Root Detection Techniques
id: MASTG-TEST-0x01
type: [dynamic, manual]
maswe: [MASWE-0051]
best-practices: [MASTG-BEST-0029, MASTG-BEST-0030]
knowledge: [MASTG-KNOW-0027]
---

## Overview

This test verifies whether an app implements runtime root detection by attempting to run its protected functionality on a rooted device.

Unlike @MASTG-TEST-0325, this test focuses on the behavior of the app when one of its root detection mechanisms triggers and whether such behavior is appropriate.

This test is best combined with:

- @MASTG-TEST-0324: Covers identifying root detection mechanisms with static analysis.
- @MASTG-TEST-0325: Covers identifying root detection mechanisms with dynamic analysis through hooking.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of root detection mechanisms, which can be very difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation.

!!! note "More information about this topic"
    See @MASTG-KNOW-0027 for more information on root detection techniques and specific APIs and artifacts apps can look for.
    See @MASTG-BEST-0030 for best practices on implementing root detection effectively.

## Steps

1. Use @MASTG-TECH-0005 to install the app on a clean, unrooted device.
2. Use @MASTG-TECH-0005 to install the app on a rooted device.
3. Exercise the app extensively on both devices to trigger as many flows as possible and enter sensitive data wherever you can.
4. Use @MASTG-TECH-0009 to capture the app's log output on both devices.

## Observation

The output should contain the app's log output from both devices, including the root indicators found by the app and its reaction to them.

## Evaluation

The test case fails if the app does not react to the root indicators it detects. However, results from this test should be interpreted as evidence of how the app reacts when it detects root, not as an assessment of the robustness or effectiveness of its root detection. See @MASTG-BEST-0030.

**Expected False Negatives:**

This test may produce false negatives if the app's root detection is bypassed or hidden by the rooting solution in use (for example, by using a kernel-based root solution such as KernelSU, @MASTG-TOOL-0x01). In such cases, the absence of a reaction does not guarantee the absence of root detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze root detection mechanisms.
