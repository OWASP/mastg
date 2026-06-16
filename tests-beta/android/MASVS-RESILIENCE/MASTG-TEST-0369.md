---
platform: android
title: Insufficient Obfuscation of Security-Relevant Native Code
id: MASTG-TEST-0369
type: [static, package, manual]
weakness: MASWE-0089
best-practices: [MASTG-BEST-0029]
profiles: [R]
knowledge: [MASTG-KNOW-0033]
---

## Overview

If native libraries that implement security-relevant logic are not obfuscated, reverse engineering of packaged native code can expose business logic, device attestation and environment checks, integrity checks, and other implementation details that help an attacker understand the app and model attacks.

This test checks whether the obfuscation techniques applied to native libraries prevent straightforward identification, correlation, and reverse engineering of security-relevant logic through strings, constants, call structure, or control flow.

Refer to @MASTG-KNOW-0033 for common native obfuscation mechanisms and indicators to look for.

**Example Attack Scenario:**

Suppose a banking app moves its integrity and root checks into a native library, assuming native code is inherently harder to analyze, but does not apply any obfuscation.

1. An attacker extracts the native library from the APK and disassembles it.
2. Plaintext strings in the `.rodata` section immediately reveal every file path and system property the library checks, requiring no further analysis to identify the protection's scope.
3. The exported JNI function name and call structure fully expose the check's logic — the attacker understands it within minutes.
4. The attacker hooks the function at runtime to return a benign result regardless of device state, bypasses the integrity check on a rooted device, and accesses features or data that should have been blocked.

## Steps

1. Use @MASTG-TECH-0157 to extract the native libraries from the app package.

## Observation

The output should contain the extracted native libraries, such as `.so` files.

## Evaluation

The test case fails if the app's native libraries allow an attacker to identify, correlate, and reverse engineer security-relevant logic with reasonable effort.

**Further Validation Required:**

Use @MASTG-TECH-0018 to disassemble the native libraries and inspect it using @MASTG-TECH-0024. Refer to @MASTG-KNOW-0033 to determine whether the native code shows indicators of obfuscation:

- Determine whether native library strings or constants (e.g., monitored file paths, API tokens, or integrity check values) are in plaintext.
- Determine whether the disassembled function structure and call edges still reveal the original security-relevant logic with recognizable patterns.
- Determine whether exported JNI symbols retain descriptive names that can be directly correlated with security-relevant functionality.

Correlate the findings and determine whether the obfuscation applied to the native layer still allows security-relevant logic to be identified and reverse engineered with reasonable effort.
