---
platform: ios
title: Runtime Use Of APIs Allowing Fallback to Non-Biometric Authentication
id: MASTG-TEST-0269
apis: [kSecAccessControlUserPresence, kSecAccessControlDevicePasscode, SecAccessControlCreateWithFlags]
type: [dynamic, hooks]
weakness: MASWE-0045
profiles: [L2]
knowledge: [MASTG-KNOW-0056]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0268.

In this case we'll hook [`SecAccessControlCreateWithFlags`](https://developer.apple.com/documentation/security/secaccesscontrolcreatewithflags(_:_:_:_:)) and its specific flags.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of locations where the `SecAccessControlCreateWithFlags` function is called including all used flags.

## Evaluation

The test case fails if the app uses `SecAccessControlCreateWithFlags` with the `kSecAccessControlUserPresence` or `kSecAccessControlDevicePasscode` flags for any sensitive data resource that needs protection.
