---
title: Runtime Use of Broken Symmetric Encryption Modes
platform: android
id: MASTG-TEST-0x03
type: [dynamic]
weakness: MASWE-0020
best-practices: [MASTG-BEST-0005]
profiles: [L1, L2]
---

## Overview

If the app configures cryptographic operations with broken encryption modes at runtime, sensitive data can be exposed to pattern leakage and other cryptographic weaknesses. This test checks whether the running app sets insecure block modes, such as ECB, in security-relevant cryptographic flows.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.

## Observation

The output should contain runtime calls that configure encryption modes, including function names and call context, and should indicate whether broken modes such as ECB are used.

## Evaluation

The test case fails if you identify broken encryption modes in security-relevant cryptographic operations, such as ECB in Android Keystore configuration or cipher transformations.