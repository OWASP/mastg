---
platform: ios
title: Verbose Error Logging and Debugging Messages
id: MASTG-TEST-03x2
type: [dynamic]
weakness: MASWE-0094
knowledge: [MASTG-KNOW-0064, MASTG-KNOW-0101]
profiles: [R]
---

## Overview

This test is the dynamic counterpart to MASTG-TEST-03x1.

This test is limited to capturing and reviewing log messages that are actually emitted during execution. It shows what is logged, but not necessarily where in the app the logging originates. Dynamic instrumentation, for example with Frida, can complement this analysis by hooking logging APIs or related code paths to identify the specific classes, methods, or call sites responsible for the observed output.

## Steps

1. Install the app on a device using @MASTG-TECH-0056.
2. Monitor system logs with @MASTG-TECH-0060 while interacting with the app.
3. Trigger various app functionalities including error conditions (e.g., network failures, invalid inputs).

## Observation

The output should contain all log messages captured during runtime.

## Evaluation

The test case fails if dynamic analysis shows that the app produces verbose debug or error messages in production builds and exposes internal implementation details at runtime.

This determination should be based on capturing logs while exercising relevant application flows and induced error conditions, in order to establish what information is actually emitted during execution and under which circumstances. Dynamic analysis is useful for confirming real runtime exposure, but it is limited to the scenarios triggered during testing and may miss dormant or hard to reach logging paths. Static analysis, see @MASTG-TEST-03x1, complements this test by identifying additional logging behavior that may not be observed dynamically.

Examples of failing cases include logs that reveal:

- internal function names or code paths
- detailed error information, stack related details, or diagnostic context
- API endpoints, backend routes, or internal URLs
- internal state, configuration, or feature behavior
- library, framework, or component version details
- developer oriented debugging messages not intended for production use
