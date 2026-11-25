---
platform: android
title: References to Device Security State APIs
id: MASTG-TEST-0x01
type: [static, code]
maswe: [MASWE-0077]
best-practices: [MASTG-BEST-0x01]
knowledge: [MASTG-KNOW-0x01]
apis: [SecurityStateManager, SecurityPatchState, Build.VERSION.SECURITY_PATCH]
---

## Overview

Apps that handle sensitive data may run on devices that lack recent security patches and are therefore exposed to known vulnerabilities. This test checks whether the app verifies the security state of the device using the AndroidX Security State library (@MASTG-KNOW-0x01), for example via `SecurityStateManager.getGlobalSecurityState`, `SecurityPatchState.loadVulnerabilityReport`, or `SecurityPatchState.isDeviceFullyUpdated`, or at least reads the security patch level via `Build.VERSION.SECURITY_PATCH`.

Note that the Security State library evaluates patch compliance only. It does not detect device tampering or verify device authenticity; the Play Integrity API is needed for that.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for uses of the Security State APIs and `Build.VERSION.SECURITY_PATCH`.

## Observation

The output should contain a list of code locations where the app uses the Security State APIs or reads the security patch level.

## Evaluation

The test case fails if the app does not use any of these APIs, or if it uses them but the result is not evaluated before granting access to sensitive functionality (for example, the result is ignored or only logged). Use @MASTG-TECH-0023 to inspect the reported code locations and confirm how the result is used.
