---
platform: android
title: Behavioral Check of Root Detection Techniques
id: MASTG-TEST-0x01
type: [dynamic]
maswe: [MASWE-0051]
best-practices: [MASTG-BEST-0029, MASTG-BEST-0030]
knowledge: [MASTG-KNOW-0027]
---

## Overview

This test verifies whether an app implements runtime root detection by attempting to run its protected functionality on a rooted device. Unlike @MASTG-TEST-0325, this test focuses on the behavior of the app when one of its root detection mechanisms trigger. See @MASTG-KNOW-0027 for more information on root detection techniques and specific APIs and artifacts apps can look for.

This test is best combined with: 
- @MASTG-TEST-0324: Covers identifying anti-root checks with static analysis. 
- @MASTG-TEST-0325: Covers identifying anti-root checks with dynamic analysis through hooking.

It is recommended to run this test using a rooted device or emulator to ensure that root detection mechanisms are triggered during testing. However, even on a non-rooted device, this test can still surface root detection logic if the app performs checks that do not require root access (for example, checking for the presence of root-related files or system properties).

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of root detection mechanisms, which can be very difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0030 for best practices on implementing root detection effectively.

## Steps

1. Use @MASTG-TECH-0005 to install the app on a clean, unrooted device.
2. Use @MASTG-TECH-0005 to install the app on a rooted device.
3. Exercise the app extensively on both devices to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The app should behave differently in both devices, showing errors to the user and potentially stopping execution on the rooted device.

## Evaluation

The test case fails if no differences are observed while using the app in the clean and rooted devices. However, results from this test should be interpreted as evidence of the presence of root detection logic, not as an assessment of its robustness or effectiveness. See @MASTG-BEST-0030.

**Expected False Negatives:**

This test may produce false negatives if the app uses root detection techniques that do not cover the rooting solution being used (for example, when using an advanced rooting solution such as @MASTG-TOOL-0x01). In such cases, the absence of findings does not guarantee the absence of root detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze root detection mechanisms.
