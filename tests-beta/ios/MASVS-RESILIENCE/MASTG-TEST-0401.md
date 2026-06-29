---
platform: ios
title: References to Debugging Detection APIs
id: MASTG-TEST-0401
apis: [ptrace, PT_DENY_ATTACH, sysctl, KERN_PROC_PID, P_TRACED, getppid, task_get_exception_ports, EXC_MASK_BREAKPOINT]
type: [static, code, manual]
weakness: MASWE-0101
best-practices: [MASTG-BEST-0029, MASTG-BEST-0074]
profiles: [R]
knowledge: [MASTG-KNOW-0085]
---

## Overview

iOS apps can implement debugging detection using mechanisms such as `ptrace` with `PT_DENY_ATTACH`, `sysctl` checks for `P_TRACED`, parent-process checks through `getppid`, or Mach exception-port checks with `task_get_exception_ports`. If these checks are absent or not applied in security-relevant code paths, an attacker who controls the device or app package can attach a debugger undetected and use it to inspect or modify runtime state, extract sensitive data, or bypass security controls.

See @MASTG-KNOW-0085 for more information on iOS debugging detection techniques and specific APIs and artifacts to look for.

This test checks whether the app references debugging detection mechanisms in its Mach-O binaries. It does not test whether the app is configured as debuggable, which is covered by @MASTG-TEST-0261.

This test is best combined with @MASTG-TEST-0402, which performs dynamic testing to confirm whether the identified debugging detection mechanisms are active at runtime. Use the findings from this test to focus dynamic analysis in @MASTG-TEST-0402 on specific checks.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of debugging detection mechanisms, which can be difficult to assess through static analysis alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0074 for best practices on implementing debugging detection effectively.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations in the app binaries where debugging detection patterns are found.

## Evaluation

The test case fails if the app contains no debugging detection patterns in its main executable or bundled Mach-O libraries. However, note that static analysis may not detect all debugging detection mechanisms, especially if they are obfuscated, dynamically loaded, inlined, or resolved indirectly.

If debugging detection patterns are found, this is a positive sign, but you should still evaluate their runtime behavior using @MASTG-TEST-0402.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0076 to determine whether the detected check is applied correctly:

- Determine whether the check is called in release builds and not only in debug configurations.
- Determine whether the check is executed before or during security-relevant flows, and not only once at startup.
- Determine whether the app takes a security-relevant action when a debugger is detected, such as process termination, feature restriction, reauthentication, or a backend risk signal.

**Expected False Negatives:**

This test may produce false negatives if the app uses debugging detection techniques that are obfuscated, dynamically loaded, implemented with inline system calls, resolved through indirect control flow, or hidden behind a third-party protection library. In such cases, the absence of findings does not guarantee the absence of debugging detection, and additional manual reverse engineering or custom instrumentation may be required.
