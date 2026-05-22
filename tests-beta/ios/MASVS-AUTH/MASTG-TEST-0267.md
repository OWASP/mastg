---
platform: ios
title: Runtime Use Of Event-Bound Biometric Authentication
id: MASTG-TEST-0267
apis: [LAContext.evaluatePolicy]
type: [dynamic, hooks]
weakness: MASWE-0044
best-practices: []
profiles: [L2]
knowledge: [MASTG-KNOW-0056]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0266.

In this case we'll hook [`LAContext.evaluatePolicy(...)`](https://developer.apple.com/documentation/localauthentication/lacontext/evaluatepolicy(_:localizedreason:reply:)) and [`SecAccessControlCreateWithFlags`](https://developer.apple.com/documentation/security/secaccesscontrolcreatewithflags(_:_:_:_:)), including all flags.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of locations where the `LAContext.evaluatePolicy` and `SecAccessControlCreateWithFlags` functions are called including all used flags.

## Evaluation

The test case fails if for each sensitive data resource worth protecting:

- `LAContext.evaluatePolicy` is used explicitly.
- There are no calls to `SecAccessControlCreateWithFlags` requiring user presence with [any of the possible flags](https://developer.apple.com/documentation/security/secaccesscontrolcreateflags).
