---
platform: android
title: Runtime Use of SDK APIs Known to Handle Sensitive User Data
id: MASTG-TEST-0319
type: [dynamic]
weakness: MASWE-0112
prerequisites:
  - identify-sensitive-data
profiles: [P]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0318.

## Steps

1. Use @MASTG-TECH-0033 to hook SDK methods known to handle sensitive user data.

## Observation

The output should list the locations where SDK methods are called, their stacktrace (call hierarchy leading to the call), and the arguments (values) passed to the SDK method at runtime.

## Evaluation

The test case fails if you can find sensitive user data being passed to these SDK methods in the app code, indicating that the app is sharing sensitive user data with the third-party SDK. If no such data sharing is found, the test case passes.
