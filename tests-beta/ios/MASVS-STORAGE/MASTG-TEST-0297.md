---
platform: ios
title: Sensitive Data Exposure Through Logging APIs
id: MASTG-TEST-0297
type: [static, code]
weakness: MASWE-0001
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0022]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0101]
---

## Overview

On the iOS platform, logging APIs like `NSLog`, `NSAssert`, `NSCAssert`, `print`, and `printf` can inadvertently lead to the leakage of sensitive information. Log messages are recorded in the console, and you can access them by using @MASTG-TECH-0060. Although other apps on the device cannot read these logs, direct logging is generally discouraged due to its potential for data leakage.

This test uses static analysis to verify whether an app contains logging APIs that take sensitive data as input.

This test focuses on logged sensitive data. For tests specifically targeting implementation details exposed through logs, see @MASTG-TEST-0358 and @MASTG-TEST-0359.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.

## Observation

The output should include the location of logging functions or other relevant logging references. Check the decompiled code to verify whether they receive sensitive data as input.

## Evaluation

The test case fails if the app contains implemented logging paths that log sensitive data.
