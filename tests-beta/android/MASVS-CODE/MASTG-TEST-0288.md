---
title: Use of Local Storage for Input Validation
platform: android
id: MASTG-TEST-0288
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

The test fails as the application does not verify the integrity of data loaded from `SharedPreferences`. Without an integrity check like `HMAC`, an attacker with root access can directly edit the SharedPreferences XML file, modifying values to grant themselves higher privileges. The app then reads and acts on this tampered data, leading to a critical security failure, like privilege escalation.
