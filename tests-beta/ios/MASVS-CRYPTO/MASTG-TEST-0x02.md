---
platform: ios
title: Runtime Use of Insecure Random APIs
id: MASTG-TEST-0x02
type: [dynamic]
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

## Observation

The output should contain runtime calls to insecure random APIs, including function names and call locations or traces.

## Evaluation

The test case fails if random values produced by insecure APIs are used in security-relevant contexts, such as key generation, token or session identifier generation, nonce or IV generation, password or PIN generation, or equivalent operations that require unpredictability.