---
platform: android
title: Root Detection in Code
id: MASTG-TEST-0501
type: [static]
weakness: MASWE-0097
best-practices: [MASTG-BEST-0028]
profiles: [R]
knowledge: [MASTG-KNOW-0027]
---

## Overview

Android apps may implement root detection to identify whether the device has been rooted. If the app does not implement root detection, it becomes easier for attackers to perform dynamic analysis, hook into sensitive methods, bypass security controls, or extract sensitive data on rooted devices.

This test checks whether the app implements root detection by statically analyzing the app binary for common root detection patterns. These may include checks for:

- Files typically found on rooted devices (e.g., `/system/xbin/su`, `/sbin/su`)
- Root management apps (e.g., SuperSU, Magisk)
- Running processes associated with root (e.g., `daemonsu`)
- System properties indicating custom ROMs or test builds
- Writable system partitions

The absence of such checks suggests the app may not adequately protect itself against threats present on rooted devices.

## Steps

1. Use @MASTG-TECH-0014 with appropriate patterns to search for root detection APIs and methods in the decompiled code.

## Observation

The output should contain a list of locations where root detection checks are implemented, including specific methods and file paths being checked.

## Evaluation

The test fails if the app does not implement any root detection checks. However, note that static analysis may not detect all root detection mechanisms, especially if they are obfuscated or implemented in native code. In such cases, @MASTG-TEST-0290 may be more effective.
