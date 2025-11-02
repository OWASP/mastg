---
platform: ios
title: Insertion of Sensitive Data into Logs
id: MASTG-TEST-0297
type: [static]
weakness: MASWE-0001
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0022]
profiles: [L1, L2]
---

## Overview

On the iOS platform, logging APIs like `NSLog`, `NSAssert`, `NSCAssert`, `print`, and `printf` can inadvertently lead to the leakage of sensitive information. Log messages are recorded in the console, and you can access them by using @MASTG-TECH-0060. Although other apps on the device cannot read these logs, direct logging is generally discouraged due to its potential for data leakage.

In this test, we will use static analysis to verify whether an app has any logging APIs which take sensitive data.

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0073 on the app binary and look for uses of logging APIs.

## Observation

The output should include the location of all logging functions. Check the decompiled code to verify if they receive sensitive data as input.

## Evaluation

The test case fails if you can find the use of logging APIs logging any sensitive data.
