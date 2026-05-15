---
platform: ios
title: Sensitive Data Exposure Through Insecure Logging
id: MASTG-TEST-0296
type: [dynamic]
weakness: MASWE-0001
prerequisites:
- identify-sensitive-data
best-practices: [MASTG-BEST-0022]
profiles: [L1, L2]
knowledge: [MASTG-KNOW-0101]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0297.

In this test, we will monitor and capture the device logs and then analyze them for sensitive data.

!!! warning Limitation
    - Linking the logs back to specific locations in the app can be difficult and requires manual analysis of the code. As an alternative you can use dynamic analysis with @MASTG-TOOL-0039.
    - Dynamic analysis works best when you interact extensively with the app. But even then there could be corner cases which are difficult or impossible to execute on every device. The results from this test therefore are likely not exhaustive.

## Steps

1. Install the app on a device (@MASTG-TECH-0056).
2. Monitor the logs with @MASTG-TECH-0060.
3. Open the app.
4. Navigate to the mobile app from which you wish to analyse the log output.
5. Close the app.

## Observation

The output should contain all logged data.

## Evaluation

The test case fails if you can find sensitive data inside the output.
