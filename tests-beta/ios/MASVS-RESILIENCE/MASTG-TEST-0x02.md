---
platform: ios
title: Implementation Details Exposure in Logs
id: MASTG-TEST-0x02
type: [dynamic]
weakness: MASWE-0094
knowledge: [MASTG-KNOW-0064, MASTG-KNOW-0101]
profiles: [R]
---

## Overview

This test is the dynamic counterpart to MASTG-TEST-0x01.

In this test, we will monitor and capture the device logs and then analyze them.

!!! warning Limitation
    - Linking the logs back to specific locations in the app can be difficult and requires manual analysis of the code. As an alternative you can use dynamic analysis with @MASTG-TOOL-0039.
    - Dynamic analysis works best when you interact extensively with the app. But even then there could be corner cases which are difficult or impossible to execute on every device. The results from this test therefore are likely not exhaustive.

This test focuses on verbose logging that exposes implementation details. For tests specifically targeting sensitive data in logs, see @MASTG-TEST-0296 and @MASTG-TEST-0297.

## Steps

1. Install the app on a device using @MASTG-TECH-0056.
2. Monitor system logs with @MASTG-TECH-0060 while interacting with the app.
3. Trigger various app functionalities including error conditions (e.g., network failures, invalid inputs).

## Observation

The output should contain all log messages captured during runtime.

## Evaluation

The test case fails if the app produces verbose debug or error messages in production builds and exposes internal implementation details at runtime.

This determination should be based on capturing logs while exercising relevant application flows and induced error conditions, in order to establish what information is actually emitted during execution and under which circumstances. Dynamic analysis is useful for confirming real runtime exposure, but it is limited to the scenarios triggered during testing and may miss dormant or hard to reach logging paths. Static analysis, see @MASTG-TEST-0x01, complements this test by identifying additional logging behavior that may not be observed dynamically.

Examples of failing cases include logs that reveal:

- internal function names or code paths
- detailed error information, stack related details, or diagnostic context
- API endpoints, backend routes, or internal URLs
- internal state, configuration, or feature behavior
- library, framework, or component version details
- developer oriented debugging messages not intended for production use

It does not fail when logs include sensitive data such as API keys, passwords, user personal information, etc. These assets are treated separately in @MASTG-TEST-0296 and @MASTG-TEST-0297.