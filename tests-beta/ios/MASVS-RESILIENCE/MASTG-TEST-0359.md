---
platform: ios
title: Implementation Details Exposure in Logs
id: MASTG-TEST-0359
type: [dynamic, logs]
weakness: MASWE-0094
best-practices: [MASTG-BEST-0022]
knowledge: [MASTG-KNOW-0064, MASTG-KNOW-0101]
profiles: [R]
---

## Overview

This test is the dynamic counterpart to @MASTG-TEST-0358.

In this test, device logs are monitored, captured, and analyzed.

!!! warning Limitation
    - Linking the logs back to specific locations in the app can be difficult and requires manual analysis of the code. As an alternative, you can use @MASTG-TECH-0095 to hook relevant logging APIs and capture backtraces.
    - Dynamic analysis works best when you interact extensively with the app. But even then, there could be corner cases that are difficult or impossible to execute on every device. The results from this test therefore are likely not exhaustive.

This test focuses on verbose logging that exposes implementation details. For tests specifically targeting sensitive data in logs, see @MASTG-TEST-0296 and @MASTG-TEST-0297.

## Steps

1. Use @MASTG-TECH-0056 to install the app.
2. Use @MASTG-TECH-0060 to monitor the device logs.
3. Trigger various app functionalities, including error conditions (for example, network failures and invalid inputs).

## Observation

The output should contain the log messages captured during runtime.

## Evaluation

The test case fails if the app produces verbose debug or error messages in production builds and exposes implementation details at runtime.

This determination should be based on capturing logs while exercising relevant application flows and induced error conditions, in order to establish what information is actually emitted during execution and under which circumstances. Dynamic analysis is useful for confirming real runtime exposure, but it is limited to the scenarios triggered during testing and may miss dormant or hard-to-reach logging paths. Static analysis, see @MASTG-TEST-0358, complements this test by identifying additional logging behavior that may not be observed dynamically.

Examples of failing cases include logs that reveal:

- internal function names or code paths
- detailed error information, stack-related details, or diagnostic context
- API endpoints, backend routes, or internal URLs
- internal state, configuration, or feature behavior
- library, framework, or component version details
- developer-oriented debugging messages not intended for production use

Sensitive data exposure through logs (e.g., API keys, passwords, personal data) is covered separately in @MASTG-TEST-0296 and @MASTG-TEST-0297.
