---
platform: android
title: Runtime Verification of Unauthorized Database Access through Content Providers
id: MASTG-TEST-0356
type: [dynamic, filesystem, manual]
weakness: MASWE-0064
profiles: [L1, L2]
best-practices: [MASTG-BEST-0049]
knowledge: [MASTG-KNOW-0020]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0355.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0148 to query the app's exported content providers.
3. Press the Start button to ensure the provider data is populated before testing.

## Observation

The output should contain the content of the database that is available through the content provider.

## Evaluation

The test case fails if sensitive data can be accessed through content providers.
