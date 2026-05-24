---
platform: android
title: Runtime Use of Debugging Detection APIs
id: MASTG-TEST-0x02
type: [dynamic, hooks, manual]
weakness: MASWE-0101
best-practices: [MASTG-BEST-0007, MASTG-BEST-0029, MASTG-BEST-0x32]
profiles: [R]
knowledge: [MASTG-KNOW-0007, MASTG-KNOW-0028]
---

## Overview

Even if an app references debugging detection APIs, those checks may not execute in security-relevant code paths at runtime. For example, they may only run in debug build variants, fire only once at startup, or be dead code that's never reached. If the app doesn't invoke its debugging detection logic at the right moments, an attacker can attach a debugger without triggering any defensive response. For further information, refer to @MASTG-KNOW-0028.

This test hooks debugging detection APIs at runtime to confirm whether they are invoked during app execution.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of calls to debugging detection APIs observed at runtime, including their return values and backtraces.

## Evaluation

The test case fails if no debugging detection API calls are observed during app execution.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0023, and additionally use @MASTG-TECH-0031 to attach a JDWP or native debugger to verify the app's defensive response:

- Determine whether the checks are called in release builds and not only in debug configurations.
- Determine whether the app changes its behavior when a debugger is attached (for example, issues a warning, restricts access, or terminates).
