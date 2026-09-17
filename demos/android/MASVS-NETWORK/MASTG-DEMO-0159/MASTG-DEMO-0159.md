---
title: GMS Security Provider Not Updated
platform: android
id: MASTG-DEMO-0159
test: MASTG-TEST-0295
code: [kotlin]
kind: fail
---

## Overview

The following sample code demonstrates how an app might establish a network connection without ensuring the GMS Security Provider is up to date, leaving it vulnerable to known SSL/TLS vulnerabilities on older Android devices. It also provides a secure implementation using `ProviderInstaller.installIfNeeded` to hot-patch the security provider.

{{ MastgTest.kt }}

## Steps

1. Reverse engineer the app (@MASTG-TECH-0017) to obtain the decompiled Java code.

{{ MastgTest_reversed.java }}

2. Use @MASTG-TECH-0014 to analyze the reverse-engineered artifact with a static analysis tool (e.g., Semgrep) to identify network connections established without updating the GMS Security Provider.

{{ rule.yml }}
{{ run.sh }}

## Observation

The output lists the relevant locations where network connections are established and where `ProviderInstaller` is utilized. Note that in a fully secure app, this call must occur before any network communication and properly handle `GooglePlayServicesRepairableException` and `GooglePlayServicesNotAvailableException`.

{{ output.txt }}

## Evaluation

The test case fails if the app establishes network connections without first invoking `ProviderInstaller.installIfNeeded` (as seen in `insecureNetworkCall`), or if it fails to appropriately catch and handle the exceptions when the provider installation fails (e.g., displaying a resolution flow).

> **Note:** The demo was validated on Android 13 (API 33) using Android Studio Iguana 2023.2.1 on a Pixel 6 emulator.
