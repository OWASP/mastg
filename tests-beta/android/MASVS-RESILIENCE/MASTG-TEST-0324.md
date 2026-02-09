---
platform: android
title: References to Root Detection Mechanisms
id: MASTG-TEST-0324
type: [static]
weakness: MASWE-0097
best-practices: [MASTG-BEST-0029, MASTG-BEST-0030]
profiles: [R]
knowledge: [MASTG-KNOW-0027]
---

## Overview

This test checks whether the app implements root detection by statically analyzing the app binary for common root detection patterns. These may include checks for files and artifacts typically associated with rooted devices, as well as calls to known root detection APIs or libraries.

See @MASTG-KNOW-0027 for more information on root detection techniques and specific APIs and artifacts to look for.

This test is best combined with @MASTG-TEST-0325, which performs dynamic testing to confirm whether the identified root detection mechanisms are active at runtime. This way, you can use static analysis to surface potential root detection logic and then focus your dynamic testing on those specific checks to confirm they are triggered at runtime. Alternatively, you can perform dynamic testing first to identify any root detection mechanisms that are active at runtime, and then use static analysis to further investigate their implementation and coverage.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of root detection mechanisms, which can be very difficult to assess through static analysis alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0030 for best practices on implementing root detection effectively and understanding its limitations.

## Steps

1. Use @MASTG-TECH-0014 with appropriate patterns to search for root detection APIs and methods in the decompiled code.

## Observation

The output should contain a list of locations where root detection checks are implemented, including specific methods and file paths being checked.

## Evaluation

The test case fails if the app does not implement any root detection checks. However, note that static analysis may not detect all root detection mechanisms, especially if they are proprietary, obfuscated, or implemented in native code.

If root detection checks are found, this is a positive sign, but you should still evaluate their effectiveness. See @MASTG-BEST-0030.
