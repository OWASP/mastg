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

Android apps may implement root detection to identify whether the device has been rooted. If the app does not implement root detection, it becomes easier for attackers to perform dynamic analysis, hook into sensitive methods, bypass security controls, or extract sensitive data on rooted devices. Additionally, rooted devices enable attackers to spoof or tamper with device sensors (e.g., GPS, accelerometer) and camera input, which poses a significant risk for identity verification and KYC (Know Your Customer) workflows, allowing fraudsters to manipulate liveness checks, inject prerecorded or synthetic video streams.

This test checks whether the app implements root detection by statically analyzing the app binary for common root detection patterns. These may include checks for:

- Files typically found on rooted devices (e.g., `/system/xbin/su`, `/sbin/su`) (accessed via `java.io.File` or native file APIs)
- Root management apps (e.g., SuperSU, Magisk, KernelSU) (via `PackageManager` queries)
- Running processes associated with root (e.g., `daemonsu`) (via `ActivityManager` or native process APIs)
- System properties indicating custom ROMs or test builds (e.g., `ro.debuggable`, `ro.secure`) (via `android.os.Build` or `Runtime.exec()`)
- Signs of modified system integrity (e.g., SELinux status, system partition mount status)
- Writable system partitions (e.g., checking if `/system` is writable)
- Presence of root-related libraries or APIs (e.g., `com.scottyab.rootbeer.RootBeer`)

See @MASTG-KNOW-0027 for more information on root detection techniques and specific APIs and artifacts to look for.

## Steps

1. Use @MASTG-TECH-0014 with appropriate patterns to search for root detection APIs and methods in the decompiled code.

## Observation

The output should contain a list of locations where root detection checks are implemented, including specific methods and file paths being checked.

## Evaluation

The test fails if the app does not implement any root detection checks. However, note that static analysis may not detect all root detection mechanisms, especially if they are proprietary, obfuscated, or implemented in native code.

If root detection checks are found, this is a positive sign, but you should still evaluate their effectiveness. See @MASTG-BEST-0528.
