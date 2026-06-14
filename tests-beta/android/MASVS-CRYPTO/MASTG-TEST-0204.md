---
platform: android
title: Insecure Random API Usage
id: MASTG-TEST-0204
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

Android apps sometimes use an insecure [pseudorandom number generator (PRNG)](../../../Document/0x05e-Testing-Cryptography.md#random-number-generation), such as [`java.util.Random`](https://developer.android.com/reference/java/util/Random), which is a linear congruential generator and produces a predictable sequence for any given seed value. As a result, `java.util.Random` and `Math.random()` ([the latter](https://franklinta.com/2014/08/31/predicting-the-next-math-random-in-java/) simply calls `nextDouble()` on a static `java.util.Random` instance) generate reproducible sequences across all Java implementations whenever the same seed is used. This predictability makes them unsuitable for cryptographic or other security-sensitive contexts.

In general, if a PRNG is not explicitly documented as being cryptographically secure, it should not be used where randomness must be unpredictable. Refer to the [Android Documentation](https://developer.android.com/privacy-and-security/risks/weak-prng) and the ["random number generation" guide](../../../Document/0x05e-Testing-Cryptography.md#random-number-generation) for further details.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where insecure random APIs are used.

## Evaluation

The test case fails if you can find random numbers generated using those APIs that are used in security-relevant contexts, such as generating passwords or authentication tokens.

**Further Validation Required:**

Inspect each reported code location using @MASTG-TECH-0023 to determine whether the usage is security-relevant:

- Determine whether the generated random values are used for security-relevant purposes, such as generating cryptographic keys, initialization vectors (IVs), nonces, authentication tokens, session identifiers, passwords, or PINs.
