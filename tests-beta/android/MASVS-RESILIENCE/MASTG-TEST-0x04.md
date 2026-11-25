---
platform: android
title: Behavioral Check of Hook Detection Techniques
id: MASTG-TEST-0x04
type: [dynamic, hooks]
maswe: [MASWE-0058]
best-practices: [MASTG-BEST-0041]
knowledge: [MASTG-KNOW-0030, MASTG-KNOW-0032, MASTG-KNOW-0118]
---

## Overview

This test verifies whether an app implements runtime hooking detection by attempting to run its protected functionality while the application is being hooked.

Unlike @MASTG-TEST-0341, this test focuses on the behavior of the app when one of its hooking detection mechanisms trigger. These may include checks for artifacts typically associated with hooking frameworks like @MASTG-TOOL-0031.

Hooking frameworks modify the runtime integrity of the application. See @MASTG-KNOW-0032 for more information on runtime integrity verification techniques and @MASTG-KNOW-0030 for more information on the artifacts that runtime reverse engineering tools like @MASTG-TOOL-0031 leave on the device or inside the app process.

This test is best combined with:

- @MASTG-TEST-0341: Covers identifying hooking checks with dynamic analysis through hooking.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of hook detection mechanisms, which can be very difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0041 for best practices on implementing hook detection effectively.

## Steps

1. Use @MASTG-TECH-0005 to install the app on a clean, unrooted device.
2. Use @MASTG-TECH-0005 to install the app on a rooted device.
3. In the rooted device use @MASTG-TECH-0043 to instrument the application and hook the `open` function of the `libc` at application startup.
4. Exercise the app extensively on both devices to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a record of the app's behavior on both devices, including any displayed errors or process termination.

## Evaluation

The test case fails if the app does not respond to hook detection in a way appropriate to its risk profile, for example by restricting sensitive functionality or terminating execution.

**Expected False Negatives:**

This test may produce false negatives if the app uses hook detection techniques that do not cover the hooking solution being used (for example, when using a modified version of @MASTG-TOOL-0031). In such cases, the absence of findings does not guarantee the absence of hook detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze hook detection mechanisms.
