---
platform: android
title: References to Debugging Detection APIs
id: MASTG-TEST-0x01
type: [static, code, manual]
weakness: MASWE-0101
best-practices: [MASTG-BEST-0007, MASTG-BEST-0029, MASTG-BEST-0x32]
profiles: [R]
knowledge: [MASTG-KNOW-0007, MASTG-KNOW-0028]
---

## Overview

Apps can implement debugging detection at the Java/Kotlin level using APIs such as [`Debug.isDebuggerConnected()`](https://developer.android.com/reference/android/os/Debug#isDebuggerConnected()), or at the native level using mechanisms such as `ptrace` calls, `TracerPid` checks in `/proc/self/status`, or inlined syscalls. If these checks are absent or not applied in security-relevant code paths, an attacker can attach a debugger undetected and use it to inspect or modify runtime state, extract sensitive data, or bypass security controls. For further information, refer to @MASTG-KNOW-0028.

This test checks whether the app references JDWP and/or native debugging detection mechanisms in its code.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for Java/Kotlin debugging detection APIs.
3. Use @MASTG-TECH-0007 to extract native libraries from the app package.
4. Use @MASTG-TECH-0018 to look for native debugging detection patterns in the extracted libraries, such as calls to `ptrace`, reads of `/proc/self/status`, or checks for the `TracerPid` field.

## Observation

The output should contain a list of locations in the Java/Kotlin code and/or native libraries where debugging detection patterns are found.

## Evaluation

The test case fails if the app contains no debugging detection patterns in either its Java/Kotlin code or its native libraries.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the detected check is applied correctly:

- Determine whether the check is called in release builds and not only in debug configurations.
- Determine whether the app takes a security-relevant action when a debugger is detected (for example, process termination or feature restriction).
