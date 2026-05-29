---
platform: android
title: Runtime Use of Debugging Detection APIs
id: MASTG-TEST-0353
type: [dynamic, hooks, manual]
weakness: MASWE-0101
best-practices: [MASTG-BEST-0007, MASTG-BEST-0029, MASTG-BEST-0047]
profiles: [R]
knowledge: [MASTG-KNOW-0007, MASTG-KNOW-0028]
---

## Overview

Even if an app references debugging detection APIs, those checks may not execute in security-relevant code paths at runtime. For example, they may only run in debug build variants, fire only once at startup, or be dead code that's never reached. If the app doesn't invoke its debugging detection logic at the right moments, an attacker can attach a debugger without triggering any defensive response.

See @MASTG-KNOW-0028 for more information on debugging detection techniques and specific APIs and artifacts to look for.

This test hooks debugging detection APIs at runtime to confirm whether they are invoked during app execution.

This test is best combined with @MASTG-TEST-0352, which checks for the presence of debugging detection logic through static analysis. Obtain a list of potential debugging detection mechanisms from static analysis and then focus your dynamic testing on those specific checks to confirm they are triggered at runtime. Alternatively, you can perform dynamic testing first to identify any debugging detection mechanisms that are active at runtime, and then use static analysis to further investigate their implementation and coverage.

It is recommended to run this test while actively attempting to attach a debugger (or on a debuggable build), to ensure that debugging detection mechanisms are triggered during testing. However, even without attaching a debugger, this test can still surface debugging detection logic if the app runs those checks unconditionally.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of debugging detection mechanisms, which can be very difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0047 for best practices on implementing debugging detection effectively.

In this test we focus on identifying the presence of debugging detection mechanisms at runtime by hooking into common debugging detection APIs and tracing relevant system calls.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Use @MASTG-TECH-0032 to trace the relevant system API calls.
4. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of calls to debugging detection APIs observed at runtime, including their return values and backtraces.

## Evaluation

The test case fails if no debugging detection API calls are observed during app execution. However, results from this test should be interpreted as evidence of the presence of debugging detection logic, not as an assessment of its robustness or effectiveness. See @MASTG-BEST-0047.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0023, and additionally use @MASTG-TECH-0031 to attach a JDWP or native debugger to verify the app's defensive response:

- Determine whether the checks are called in release builds and not only in debug configurations.
- Determine whether the app changes its behavior when a debugger is attached (for example, issues a warning, restricts access, or terminates).

**Expected False Negatives:**

This test may produce false negatives if the app uses debugging detection techniques that are not covered by the hooks or traces used in this test, or if the debugging detection logic is implemented in a way that evades detection (for example, through obfuscation, dynamic code loading, or anti-instrumentation techniques). In such cases, the absence of findings does not guarantee the absence of debugging detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze debugging detection mechanisms.
