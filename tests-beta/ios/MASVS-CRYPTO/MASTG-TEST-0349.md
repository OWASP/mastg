---
platform: ios
title: Runtime Use of Insecure Random APIs
id: MASTG-TEST-0349
type: [dynamic, hooks, manual]
weakness: MASWE-0027
profiles: [L1, L2]
best-practices: [MASTG-BEST-0025]
knowledge: [MASTG-KNOW-0070]
---

## Overview

If the app uses insecure pseudorandom number generators (PRNGs) at runtime, generated values can become predictable. This can lead to weak tokens, nonces, keys, or identifiers when those values are used in security-relevant contexts. This test checks whether the running app calls insecure random APIs, such as `rand`, `random`, and the `*rand48` family, during relevant flows.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain runtime calls to insecure random APIs, including function names and backtraces.

## Evaluation

The test case fails if random values produced by insecure APIs are used in security-relevant contexts.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0076 to determine whether the usage is security-relevant:

- Determine whether the generated random values are used for security-relevant purposes, such as generating cryptographic keys, initialization vectors (IVs), nonces, authentication tokens, session identifiers, passwords, or PINs.
