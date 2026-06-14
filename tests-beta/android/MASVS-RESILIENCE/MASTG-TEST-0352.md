---
platform: android
title: References to Debugging Detection APIs
id: MASTG-TEST-0352
type: [static, code, manual]
weakness: MASWE-0101
best-practices: [MASTG-BEST-0007, MASTG-BEST-0029, MASTG-BEST-0047]
profiles: [R]
knowledge: [MASTG-KNOW-0007, MASTG-KNOW-0028]
---

## Overview

Apps can implement debugging detection at the Java/Kotlin level using APIs such as [`Debug.isDebuggerConnected()`](https://developer.android.com/reference/android/os/Debug#isDebuggerConnected()), or at the native level using mechanisms such as `ptrace` calls, `TracerPid` checks in `/proc/self/status`, or inlined syscalls. If these checks are absent or not applied in security-relevant code paths, an attacker can attach a debugger undetected and use it to inspect or modify runtime state, extract sensitive data, or bypass security controls.

See @MASTG-KNOW-0028 for more information on debugging detection techniques and specific APIs and artifacts to look for.

This test checks whether the app references JDWP and/or native debugging detection mechanisms in its code.

This test is best combined with @MASTG-TEST-0353, which performs dynamic testing to confirm whether the identified debugging detection mechanisms are active at runtime. Use the findings from this test to focus dynamic analysis in @MASTG-TEST-0353 on specific checks.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of debugging detection mechanisms, which can be very difficult to assess through static analysis alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0047 for best practices on implementing debugging detection effectively.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for Java/Kotlin debugging detection APIs.
3. Use @MASTG-TECH-0157 to extract the native libraries from the app package.
4. Use @MASTG-TECH-0018 to look for native debugging detection patterns in the extracted libraries, such as calls to `ptrace`, reads of `/proc/self/status`, or checks for the `TracerPid` field.

## Observation

The output should contain a list of locations in the Java/Kotlin code and/or native libraries where debugging detection patterns are found.

## Evaluation

The test case fails if the app contains no debugging detection patterns in either its Java/Kotlin code or its native libraries. However, note that static analysis may not detect all debugging detection mechanisms, especially if they are obfuscated or implemented in native code using patterns not covered by the analysis.

If debugging detection patterns are found, this is a positive sign, but you should still evaluate their effectiveness using @MASTG-TEST-0353.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the detected check is applied correctly:

- Determine whether the check is called in release builds and not only in debug configurations.
- Determine whether the app takes a security-relevant action when a debugger is detected (for example, process termination or feature restriction).

**Expected False Negatives:**

This test may produce false negatives if the app uses debugging detection techniques that are obfuscated, dynamically loaded, or implemented in native code using inline syscalls or other patterns not covered by the analysis. In such cases, the absence of findings does not guarantee the absence of debugging detection, and additional manual reverse engineering or custom instrumentation may be required.
