---
platform: android
title: References to Emulator Detection Mechanisms
id: MASTG-TEST-0x03
type: [static, code]
maswe: [MASWE-0053]
best-practices: [MASTG-BEST-0029, MASTG-BEST-0046]
knowledge: [MASTG-KNOW-0031]
---

## Overview

This test checks whether the app implements emulator detection by statically analyzing the app binary for common emulation detection patterns. These may include checks for files and artifacts typically associated with emulated devices, as well as calls to known emulation detection APIs or libraries.

See @MASTG-KNOW-0031 for more information on emulator detection techniques and specific APIs and artifacts to look for.

This test is best combined with @MASTG-TEST-0351, which performs dynamic testing to confirm whether the identified detection mechanisms are active at runtime. This way, you can use static analysis to surface potential detection logic and then focus your dynamic testing on those specific checks to confirm they are triggered at runtime. Alternatively, you can perform dynamic testing first to identify any detection mechanism that is active at runtime, and then use static analysis to further investigate their implementation and coverage.

!!! note "Out of Scope"
    This test does not cover robustness or effectiveness of emulator detection mechanisms, which can be very difficult to assess through static analysis alone and may require manual reverse engineering and custom instrumentation. See @MASTG-BEST-0046 for best practices on implementing emulator detection effectively and understanding its limitations.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where emulator detection checks are implemented, including specific methods and file paths being checked.

## Evaluation

The test case fails if the app does not implement any emulator detection checks.

**Expected False Negatives:**

This test may produce false negatives if the app uses emulator detection mechanisms that are proprietary, obfuscated, or implemented in native code. In such cases, the absence of findings does not guarantee the absence of emulator detection, and additional manual reverse engineering or custom instrumentation may be required to identify and analyze emulator detection mechanisms.
