---
platform: android
title: Insufficient Obfuscation of Security-Relevant Java/Kotlin Code
id: MASTG-TEST-0368
type: [static, code, manual]
weakness: MASWE-0089
best-practices: [MASTG-BEST-0029]
profiles: [R]
knowledge: [MASTG-KNOW-0033]
---

## Overview

If security-relevant Java or Kotlin code is not sufficiently obfuscated, decompilation of the app's DEX bytecode can expose business logic, device attestation and environment checks, integrity checks, and other implementation details that help an attacker understand the app and model attacks.

This test checks whether the obfuscation techniques applied to the Java or Kotlin layer prevent straightforward identification, correlation, and reverse engineering of security-relevant logic in the decompiled output.

Refer to @MASTG-KNOW-0033 for common obfuscation mechanisms and indicators to look for.

**Example Attack Scenario:**

Suppose a fintech app implements its fraud-detection scoring in Java/Kotlin, relying solely on R8 identifier renaming to protect the logic.

1. An attacker decompiles the APK and, despite the shortened class and method names, locates the fraud-scoring logic within minutes by following the plaintext string constants that remain in the code.
2. The attacker reads the exact detection thresholds and decision criteria directly from the decompiled output.
3. Armed with this knowledge, the attacker crafts transactions that stay just below the detection thresholds.
4. Repeated fraudulent transfers go undetected because the attacker has reverse engineered the exact conditions used to flag them.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.

## Observation

The output should contain the decompiled Java code from the app.

## Evaluation

The test case fails if the Java or Kotlin layer allows an attacker to identify, correlate, and reverse engineer security-relevant logic with reasonable effort.

**Further Validation Required:**

Inspect the decompiled code using @MASTG-TECH-0023. If the decompiled output is incomplete or unreliable, use @MASTG-TECH-0016 to inspect the corresponding Smali code. Refer to @MASTG-KNOW-0033 to determine whether the code shows indicators of obfuscation:

- Determine whether class names, method names, field names, or local variables have been renamed to meaningless identifiers.
- Determine whether string literals (e.g., API endpoints, error messages, or detection artifact names) remain in plaintext and can be used to locate security-relevant logic.
- Determine whether the control flow is structured in a way that still makes the original logic easy to follow (e.g., no obfuscated branches or opaque predicates).

Correlate the findings and determine whether the obfuscation applied to the Java or Kotlin layer still allows security-relevant logic to be identified and reverse engineered with reasonable effort.
