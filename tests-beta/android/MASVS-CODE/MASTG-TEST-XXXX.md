---
title: Integrity and Authenticity Validation of Local Storage Data
platform: android
id: MASTG-TEST-XXXX
type: [static]
weakness: MASWE-0082
profiles: [L1, L2]
---

## Overview

Data stored in Android's `SharedPreference`s can be tampered with on a rooted device. If an application reads this data without verifying its integrity and authenticity (e.g., with an HMAC signature), it can lead to security vulnerabilities. This test checks if the application properly validates data read from local storage.

## Steps

1. Run @MASTG-TECH-0014 on the code and look for patterns where data is read from `SharedPreferences` without a corresponding integrity check.

## Observation

The output identifies code where `SharedPreferences` data is loaded without an integrity check.

## Evaluation

The test case fails if the application doesn't verify the integrity and authenticity of data loaded from local storage such as `SharedPreferences`.
