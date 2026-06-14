---
platform: android
title: References to Logging APIs
id: MASTG-TEST-0231
apis: [Log, Logger, System.out.print, System.err.print, java.lang.Throwable#printStackTrace, android.util.Log]
type: [static, code]
weakness: MASWE-0001
best-practices: [MASTG-BEST-0002]
profiles: [L1, L2, P]
knowledge: [MASTG-KNOW-0049]
---

## Overview

This test verifies if an app uses logging APIs like `android.util.Log`, `Log`, `Logger`, `System.out.print`, `System.err.print`, and `java.lang.Throwable#printStackTrace`.

## Steps

1. Use @MASTG-TECH-0013 to reverse engineer the app.
2. Use @MASTG-TECH-0014 to look for the relevant APIs.

## Observation

The output should contain a list of locations where logging APIs are used.

## Evaluation

The test case fails if an app logs sensitive information from any of the listed locations.
