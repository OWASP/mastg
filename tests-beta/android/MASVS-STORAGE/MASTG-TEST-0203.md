---
platform: android
title: Runtime Use of Logging APIs
id: MASTG-TEST-0203
apis: [Log, Logger, System.out.print, System.err.print, java.lang.Throwable#printStackTrace]
type: [dynamic, hooks]
weakness: MASWE-0001
best-practices: [MASTG-BEST-0002]
profiles: [L1, L2, P]
knowledge: [MASTG-KNOW-0049]
---

## Overview

On Android platforms, logging APIs like `Log`, `Logger`, `System.out.print`, `System.err.print`, and `java.lang.Throwable#printStackTrace` can inadvertently lead to the leakage of sensitive information. Log messages are recorded in logcat, a shared memory buffer, accessible since Android 4.1 (API level 16) only to privileged system applications that declare the `READ_LOGS` permission. Nonetheless, the vast ecosystem of Android devices includes pre-loaded apps with the `READ_LOGS` privilege, increasing the risk of sensitive data exposure. Therefore, direct logging to logcat is generally advised against due to its susceptibility to data leaks.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should contain a list of locations where logging APIs are used in the app for the current execution.

## Evaluation

The test case fails if you can find sensitive data being logged using those APIs.
