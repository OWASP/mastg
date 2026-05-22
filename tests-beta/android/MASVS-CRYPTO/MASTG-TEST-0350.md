---
title: Runtime Use of Broken Symmetric Encryption Modes
platform: android
id: MASTG-TEST-0350
type: [dynamic, hooks, manual]
weakness: MASWE-0020
best-practices: [MASTG-BEST-0005]
profiles: [L1, L2]
---

## Overview

If the app configures cryptographic operations with broken encryption modes at runtime, sensitive data can be exposed to pattern leakage and other cryptographic weaknesses. This test checks whether the running app sets insecure block modes, such as ECB, in security-relevant cryptographic flows.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of calls to encryption configuration APIs, including the transformation string argument and backtraces of each call.

## Evaluation

The test case fails if broken encryption modes are used in security-relevant cryptographic operations.

**Further Validation Required:**

Using the backtraces from the hook output, inspect the code locations using @MASTG-TECH-0023 to determine whether the encryption is applied to sensitive data:

- Determine whether the data being encrypted or decrypted is sensitive (e.g., personal data, authentication tokens, cryptographic keys, or session identifiers).
