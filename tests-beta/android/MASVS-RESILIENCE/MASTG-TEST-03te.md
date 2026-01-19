---
platform: android
title: Testing Runtime Hook Detection
id: MASTG-TEST-03te
type: [dynamic]
weakness: MASWE-0107
best-practices: []
profiles: [R]
knowledge: [MASTG-KNOW-0030, MASTG-KNOW-0032, MASTG-KNOW-00kw]
---

## Overview

This test verifies whether the app implements runtime code integrity verification to detect and respond to instrumentation and hooking attempts. There are several ways to harden an app against such attacks (see @MARTG-KNOW-0032, @MARTG-KNOW-0030, @MARTG-KNOW-00kw). With the absence of such protections, attackers can use tools like @MASTG-TOOL-0001 or @MASTG-TOOL-0027 to:

- Alter application logic such as modifying return values to bypass security controls or enable hidden functionality.
- Extract sensitive data from memory.

This test attempts to hook the app in runtime and observe whether the app detects and responds to the instrumentation attempt.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0033 to attempt to hook a security-relevant Java method (e.g. authentication, certificate validation) and/or a native function.
3. Capture the output, including any session termination events or errors.

## Observation

The output should contain one of the following:

- The expected hook callback data (e.g. function arguments, return values).
- Session termination, script errors, empty responses, or absence of expected hook data.

## Evaluation

The test fails if the hook executes successfully and returns the expected data, indicating the app lacks runtime integrity verification.

The test passes if the hooking attempt fails due to the app's defensive response (e.g. session terminates unexpectedly, hook callbacks never execute, or process exits).

