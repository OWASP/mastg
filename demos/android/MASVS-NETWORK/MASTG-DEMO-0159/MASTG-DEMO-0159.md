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

1. Reverse engineer the app (@MASTG-TECH-0017).
2. Use @MASTG-TECH-0014 to look for all usages of `ProviderInstaller.installIfNeeded` or `ProviderInstaller.installIfNeededAsync` prior to any network connections.

{{ run.sh }}

## Observation

The output lists the locations where the `ProviderInstaller` is utilized. Note that in a fully secure app, this call must occur before any network communication and properly handle `GooglePlayServicesRepairableException` and `GooglePlayServicesNotAvailableException`.

{{ output.txt }}

## Evaluation

The test case fails if the app establishes network connections without first invoking `ProviderInstaller.installIfNeeded` (as seen in `insecureNetworkCall`), or if it fails to appropriately catch and handle the exceptions when the provider installation fails.
