---
platform: ios
title: Insecure Random API Usage
id: MASTG-TEST-0311
type: [static, dynamic]
weakness: MASWE-0027
profiles: [L1, L2]
best-practices: [MASTG-BEST-0025]
---

## Overview

iOS apps sometimes use insecure pseudorandom number generators (PRNGs) (@MASTG-KNOW-0070) instead of cryptographically secure ones. This test case focuses on detecting the use of insecure alternatives such as the standard C library functions `rand`, `random`, and the `*rand48` family.

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0073 on the app binary, or use runtime method hooking (see @MASTG-TECH-0095), and look for insecure random APIs.

## Observation

The output should contain a list of locations where insecure random APIs are used, including the function names and code locations where they are called.

## Evaluation

The test case fails if random numbers generated using insecure APIs are used in security-relevant contexts.

For each of the identified API uses, verify the context by decompiling or disassembling the code (see @MASTG-TECH-0076) to determine if the generated random values are used for security-relevant purposes, such as:

- Generating cryptographic keys, initialization vectors (IVs), or nonces
- Creating authentication tokens or session identifiers
- Generating passwords or PINs
- Any other security-relevant operations requiring unpredictability

Other uses of insecure random APIs unrelated to security (e.g., generating random delays, non-security-related identifiers, game mechanics) do not cause the test case to fail.
