---
platform: android
title: Runtime Use of Emulator Detection Techniques
id: MASTG-TEST-0351
type: [dynamic, hooks]
weakness: MASWE-0099
best-practices: [MASTG-BEST-0046]
profiles: [R]
knowledge: [MASTG-KNOW-0031]
---

## Overview

This test verifies whether an app implements runtime emulator detection by attempting to hook into common emulator detection mechanisms. These may include checks for build properties and artifacts typically associated with emulated devices, as well as calls to known emulator detection APIs.

See @MASTG-KNOW-0031 for more information on emulator detection techniques and specific APIs and artifacts to look for.

It is recommended to run this test on an emulator to ensure that emulator detection mechanisms are triggered during testing. However, some checks may still surface on a physical device if the app runs them unconditionally.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of emulator detection mechanisms, which can be very difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0046 for best practices on implementing emulator detection effectively.

In this test we focus our approach on identifying the presence of emulator detection mechanisms at runtime by hooking into common emulator detection APIs and tracing relevant system calls. But, optionally, you can try to bypass emulator detection checks in the app and observe the results. For example, successful bypassing of certain checks or failed detections may indicate the presence of emulator detection mechanisms.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Use @MASTG-TECH-0032 to trace the relevant system API calls.
4. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain any instances of emulator detection checks, along with the methods or APIs that were hooked.

## Evaluation

The test case fails if no instances of emulator detection checks are observed. However, results from this test should be interpreted as evidence of the presence of emulator detection logic, not as an assessment of its robustness or effectiveness. See @MASTG-BEST-0046.

**Expected False Negatives:**

This test may produce false negatives if the app uses emulator detection techniques that are not covered by the hooks or traces used in this test, or if the emulator detection logic is implemented in a way that evades detection (for example, through obfuscation, dynamic code loading, or anti-instrumentation techniques). In such cases, the absence of findings does not guarantee the absence of emulator detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze emulator detection mechanisms.
