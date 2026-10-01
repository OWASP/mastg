---
platform: android
title: Behavioral Check of Emulator Detection Techniques
id: MASTG-TEST-0x02
type: [dynamic, hooks]
maswe: [MASWE-0053]
best-practices: [MASTG-BEST-0046]
knowledge: [MASTG-KNOW-0031]
---

## Overview

This test verifies whether an app implements runtime emulator detection by attempting to run its protected functionality on an emulated device. Unlike @MASTG-TEST-0351, this test focuses on the behavior of the app when one of its emulator detection mechanisms trigger. These may include checks for build properties and artifacts typically associated with emulated devices, as well as calls to known emulator detection APIs.

See @MASTG-KNOW-0031 for more information on emulator detection techniques and specific APIs and artifacts to look for.

This test is best combined with:
- @MASTG-TEST-0x03: Covers identifying anti-emulator checks with static analysis.
- @MASTG-TEST-0351: Covers identifying anti-emulator checks with dynamic analysis through hooking.

It is recommended to run this test on an emulator to ensure that emulator detection mechanisms are triggered during testing. However, some checks may still surface on a physical device if the app runs them unconditionally.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of emulator detection mechanisms, which can be very difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0046 for best practices on implementing emulator detection effectively.

## Steps

1. Use @MASTG-TECH-0005 to install the app on a real, stock device.
2. Use @MASTG-TECH-0005 to install the app on an emulated device.
3. Exercise the app extensively on both devices to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The app should behave differently in both devices, showing errors to the user and potentially stopping execution on the emulated device.

## Evaluation

The test case fails if no differences are observed while using the app in the real and emulated devices. However, results from this test should be interpreted as evidence of the presence of emulator detection logic, not as an assessment of its robustness or effectiveness. See @MASTG-BEST-0046.

**Expected False Negatives:**

This test may produce false negatives if the app uses emulator detection techniques that do not target the emulation framework being used, or if the emulator detection logic is implemented in a way that evades detection (for example, through obfuscation, dynamic code loading, or anti-instrumentation techniques). In such cases, the absence of findings does not guarantee the absence of emulator detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze emulator detection mechanisms.
