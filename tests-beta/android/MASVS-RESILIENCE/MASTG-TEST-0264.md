---
platform: android
title: Runtime Use of StrictMode APIs
id: MASTG-TEST-0264
type: [dynamic, hooks]
weakness: MASWE-0094
best-practices: []
profiles: [R]
---

## Overview

This test checks whether the app uses `StrictMode` by dynamically analyzing the app's behavior and placing relevant hooks to detect the use of `StrictMode` APIs, such as `StrictMode.setVmPolicy` and `StrictMode.VmPolicy.Builder.penaltyLog`.

While `StrictMode` is useful for developers to log policy violations such as disk I/O or network operations during development, it can expose sensitive implementation details in the logs that could be exploited by attackers.

## Steps

1. Use @MASTG-TECH-0005 to install the app.
2. Use @MASTG-TECH-0043 to hook the relevant API calls.
3. Exercise the app extensively to trigger as many flows as possible and enter sensitive data wherever you can.

## Observation

The output should show the runtime usage of `StrictMode` APIs.

## Evaluation

The test case fails if the output shows the runtime usage of `StrictMode` APIs.
