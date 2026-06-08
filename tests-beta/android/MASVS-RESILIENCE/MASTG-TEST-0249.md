---
platform: android
title: Runtime Use of Secure Screen Lock Detection APIs
id: MASTG-TEST-0249
apis: [KeyguardManager, BiometricManager#canAuthenticate]
type: [dynamic, hooks]
weakness: MASWE-0008
best-practices: []
profiles: [L2]
knowledge: [MASTG-KNOW-0001]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0247.

In this case, we'll look for uses of `KeyguardManager.isDeviceSecure` and `BiometricManager.canAuthenticate` APIs.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of locations where relevant APIs are used.

## Evaluation

The test case fails if an app doesn't use any API to verify the secure screen lock presence.
