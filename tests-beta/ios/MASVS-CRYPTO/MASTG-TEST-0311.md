---
platform: ios
title: Insecure Random API Usage
id: MASTG-TEST-0311
type: [static, code, manual]
weakness: MASWE-0027
profiles: [L1, L2]
best-practices: [MASTG-BEST-0025]
knowledge: [MASTG-KNOW-0070]
---

## Overview

iOS apps sometimes use insecure pseudorandom number generators (PRNGs) instead of cryptographically secure ones. This test case focuses on detecting the use of insecure APIs such as the standard C library functions `rand`, `random`, and the `*rand48` family.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should contain a list of locations where insecure random APIs are used, including the function names and code locations where they are called.

## Evaluation

The test case fails if random numbers generated using insecure APIs are used in security-relevant contexts.

**Further Validation Required:**

Since determining whether the usage is security-relevant is context-dependent, inspect each reported code location using @MASTG-TECH-0076:

- Determine whether the generated random values are used for security-relevant purposes, such as generating cryptographic keys, initialization vectors (IVs), nonces, authentication tokens, session identifiers, passwords, or PINs.

Other uses of insecure random APIs unrelated to security (e.g., generating random delays, non-security-related identifiers, game mechanics) do not cause the test case to fail.
