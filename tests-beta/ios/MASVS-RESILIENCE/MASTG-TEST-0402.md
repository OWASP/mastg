---
platform: ios
title: Runtime Use of Debugging Detection APIs
id: MASTG-TEST-0402
apis: [ptrace, PT_DENY_ATTACH, sysctl, KERN_PROC_PID, P_TRACED, getppid, task_get_exception_ports, EXC_MASK_BREAKPOINT]
type: [dynamic, hooks, manual]
weakness: MASWE-0101
best-practices: [MASTG-BEST-0029, MASTG-BEST-0074]
profiles: [R]
knowledge: [MASTG-KNOW-0085]
---

## Overview

Even if an iOS app references debugging detection APIs, those checks may not execute in security-relevant code paths at runtime. For example, they may only run in debug builds, fire only once at startup, or be dead code that is never reached. If the app does not invoke its debugging detection logic at the right moments, an attacker who controls the device or app package can attach a debugger without triggering a defensive response.

See @MASTG-KNOW-0085 for more information on iOS debugging detection techniques and specific APIs and artifacts to look for.

This test hooks debugging detection APIs at runtime to confirm whether they are invoked during app execution.

This test is best combined with @MASTG-TEST-0401, which checks for the presence of debugging detection logic through static analysis. Obtain a list of potential debugging detection mechanisms from static analysis and then focus dynamic testing on those specific checks to confirm they are triggered at runtime. Alternatively, you can perform dynamic testing first to identify any debugging detection mechanisms that are active at runtime, and then use static analysis to further investigate their implementation and coverage.

It is recommended to run this test while actively attempting to attach a debugger, where feasible, to ensure that debugging detection mechanisms are triggered during testing. On iOS, this may require a jailbroken device, a development or re-signed build with the required debugging entitlement, or another controlled test setup. Even without attaching a debugger, this test can still surface debugging detection logic if the app runs those checks unconditionally.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of debugging detection mechanisms, which can be difficult to assess through automated testing alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0074 for best practices on implementing debugging detection effectively.

In this test, focus on identifying the presence of debugging detection mechanisms at runtime by hooking common debugging detection APIs and tracing relevant low-level calls.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of calls to debugging detection APIs observed at runtime, including their return values and backtraces.

## Evaluation

The test case fails if no debugging detection API calls are observed during app execution. However, results from this test should be interpreted as evidence of the presence of debugging detection logic, not as an assessment of its robustness or effectiveness. See @MASTG-BEST-0074.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0076, and additionally use @MASTG-TECH-0055 or @MASTG-TECH-0084 to attach a debugger when feasible and verify the app's defensive response:

- Determine whether the checks are called in release builds and not only in debug configurations.
- Determine whether the checks are executed before or during security-relevant flows, and not only once at startup.
- Determine whether the app changes its behavior when a debugger is attached, such as issuing a warning, restricting access, terminating the process, requiring reauthentication, or sending a backend risk signal.

**Expected False Negatives:**

This test may produce false negatives if the app uses debugging detection techniques that are not covered by the hooks or traces used in this test, if the exercised flows do not trigger the relevant code paths, or if the debugging detection logic evades the instrumentation used in this test through obfuscation, dynamic loading, native code, anti-instrumentation techniques, or checks that run before the hooks are installed. In such cases, the absence of findings does not guarantee the absence of debugging detection, and additional manual reverse engineering or custom instrumentation may be required.
