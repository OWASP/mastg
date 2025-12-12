---
title: Integrity and Authenticity Validation of Local Storage Data
platform: android
id: MASTG-TEST-XXXX
type: [static]
weakness: MASWE-0082
profiles: [L1, L2]
---

## Overview

Data stored in Android's `SharedPreference`s can be tampered with on a rooted device. If an application reads this data without verifying its integrity (e.g., with an HMAC signature), it can lead to security vulnerabilities. This test checks if the application properly validates data read from local storage.

## Steps

1. Run a static analysis tool such as @MASTG-TOOL-0110 on the code and look for patterns where data is read from `SharedPreferences` without a corresponding integrity check.

## Observation

The output identifies code where `SharedPreferences` data is loaded without an integrity check.

## Evaluation

The test fails if the application does not verify the integrity and authenticity of data loaded from local storage such as `SharedPreferences`.
