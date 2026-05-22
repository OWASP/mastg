---
platform: android
title: Non-random Sources Usage
id: MASTG-TEST-0205
type: [static, code, manual]
best-practices: [MASTG-BEST-0001]
prerequisites:
- identify-sensitive-data
- identify-security-relevant-contexts
weakness: MASWE-0027
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0013]
---

## Overview

Android applications sometimes use non-random sources to generate "random" values, leading to potential security vulnerabilities. Common practices include relying on the current time, such as `Date().getTime()`, or accessing `Calendar.MILLISECOND` to produce values that are easily guessable and reproducible.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where non-random sources are used.

## Evaluation

The test case fails if you can find security-relevant values, such as passwords or tokens, generated using non-random sources.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the usage is security-relevant:

- Determine whether the generated values are used for security-relevant purposes, such as generating cryptographic keys, initialization vectors (IVs), nonces, authentication tokens, session identifiers, passwords, or PINs.
