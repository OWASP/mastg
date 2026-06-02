---
platform: android
title: Runtime Use of Root Detection Techniques
id: MASTG-TEST-0325
type: [dynamic, hooks]
weakness: MASWE-0097
best-practices: [MASTG-BEST-0029, MASTG-BEST-0030]
profiles: [R]
knowledge: [MASTG-KNOW-0027]
---

## Overview

This test verifies whether an app implements runtime root detection by attempting to hook into common root detection mechanisms. These may include checks for files and artifacts typically associated with rooted devices, as well as calls to known root detection APIs or libraries.

See @MASTG-KNOW-0027 for more information on root detection techniques and specific APIs and artifacts to look for.

This test is best combined with @MASTG-TEST-0324, which checks for the presence of root detection logic through static analysis. This way, you can obtain a list of potential root detection mechanisms from static analysis and then focus your dynamic testing on those specific checks to confirm they are triggered at runtime. Or you can perform dynamic testing first to identify any root detection mechanisms that are active at runtime, and then use static analysis to further investigate their implementation and coverage.

It is recommended to run this test using a rooted device or emulator to ensure that root detection mechanisms are triggered during testing. However, even on a non-rooted device, this test can still surface root detection logic if the app performs checks that do not require root access (for example, checking for the presence of root-related files or system properties).

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of root detection mechanisms, which can be very difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0030 for best practices on implementing root detection effectively.

In this test we focus our approach on identifying the presence of root detection mechanisms at runtime by hooking into common root detection APIs and tracing relevant system calls. But, optionally, you can use @MASTG-TECH-0144 to try to bypass root detection checks in the app and observe the results. For example, successful bypassing of certain checks or failed detections may indicate the presence of root detection mechanisms.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Use @MASTG-TECH-0032 to trace the relevant system API calls.
4. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain any instances of root detection checks, along with the methods or APIs that were hooked.

## Evaluation

The test case fails if no instances of root detection checks are observed. However, results from this test should be interpreted as evidence of the presence of root detection logic, not as an assessment of its robustness or effectiveness. See @MASTG-BEST-0030.

**Expected False Negatives:**

This test may produce false negatives if the app uses root detection techniques that are not covered by the hooks or traces used in this test, or if the root detection logic is implemented in a way that evades detection (for example, through obfuscation, dynamic code loading, or anti-instrumentation techniques). In such cases, the absence of findings does not guarantee the absence of root detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze root detection mechanisms.
