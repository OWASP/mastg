---
platform: ios
title: Runtime Use of Secure Screen Lock Detection APIs
id: MASTG-TEST-0246
apis: [LAContext.canEvaluatePolicy, kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly]
type: [dynamic, hooks]
weakness: MASWE-0008
best-practices: []
profiles: [L2]
knowledge: [MASTG-KNOW-0056]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0248.

In this case we'll hook [`LAContext.canEvaluatePolicy(.deviceOwnerAuthentication)`](https://developer.apple.com/documentation/localauthentication/lacontext/canevaluatepolicy(_:error:)) API or data stored with the [`kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly`](https://developer.apple.com/documentation/security/ksecattraccessiblewhenpasscodesetthisdeviceonly) attribute.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0095 to hook the relevant APIs.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of locations where relevant APIs are used.

## Evaluation

The test case fails if an app doesn't use any API to verify the secure screen lock presence.
