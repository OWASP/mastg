---
platform: ios
title: Insufficient Obfuscation of Security-Relevant Native Code
id: MASTG-TEST-0391
type: [static, package, manual]
weakness: MASWE-0089
best-practices: [MASTG-BEST-0029]
profiles: [R]
knowledge: [MASTG-KNOW-0089]
---

## Overview

If native Mach-O code that implements security-relevant logic is not obfuscated, reverse engineering of packaged iOS binaries can expose business logic, device attestation and environment checks, integrity checks, and other implementation details that help an attacker understand the app and model attacks.

This test checks whether the obfuscation techniques applied to the main executable and bundled Mach-O binaries prevent straightforward identification, correlation, and reverse engineering of security-relevant logic through strings, constants, symbols, runtime metadata, call structure, or control flow.

Refer to @MASTG-KNOW-0089 for common iOS obfuscation mechanisms and indicators to look for.

**Example Attack Scenario:**

Suppose a banking app moves its integrity and jailbreak checks into Swift, Objective-C, or C/C++ code compiled into Mach-O binaries, assuming native code is inherently harder to analyze, but does not apply any obfuscation.

1. An attacker extracts the app package and identifies the main executable and bundled frameworks.
2. Plaintext strings, Objective-C selectors, or Swift symbols immediately reveal every file path, URL, and runtime artifact the app checks, requiring little further analysis to identify the protection's scope.
3. The function names, runtime metadata, and call structure fully expose the check's logic; the attacker understands it within minutes.
4. The attacker patches a branch or hooks the function at runtime on a jailbroken device to return a benign result, bypasses the integrity check, and accesses features or data that should have been blocked.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from the app package.

## Observation

The output should contain the extracted Mach-O binaries, such as the main executable, bundled frameworks, app extensions, or dynamic libraries.

## Evaluation

The test case fails if the app's Mach-O binaries allow an attacker to identify, correlate, and reverse engineer security-relevant logic with reasonable effort.

**Further Validation Required:**

Use @MASTG-TECH-0066 to inspect the app binaries and @MASTG-TECH-0076 to review the disassembled code. Use @MASTG-TECH-0071 to retrieve strings and @MASTG-TECH-0114 to demangle Swift or C++ symbols when needed. Refer to @MASTG-KNOW-0089 to determine whether the code shows indicators of obfuscation:

- Determine whether native strings, constants, selectors, or metadata (e.g., monitored file paths, API tokens, or integrity check values) are in plaintext.
- Determine whether Objective-C metadata, Swift or C++ symbols, exported symbols, or bundled framework names retain descriptive identifiers that can be directly correlated with security-relevant functionality.
- Determine whether the disassembled function structure and call edges still reveal the original security-relevant logic with recognizable patterns.
- Determine whether protected code or data is encrypted, packed, transformed, or decoded only at runtime.

Correlate the findings and determine whether the obfuscation applied to the iOS native layer still allows security-relevant logic to be identified and reverse engineered with reasonable effort.
