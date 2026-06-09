---
platform: ios
title: Implementation Details Exposure Through Logging APIs
id: MASTG-TEST-0358
type: [static, code]
weakness: MASWE-0094
best-practices: [MASTG-BEST-0022]
knowledge: [MASTG-KNOW-0064, MASTG-KNOW-0101]
profiles: [R]
---

## Overview

This test checks for verbose error logging and debugging messages in iOS applications. While logging is useful during development, verbose logging in production builds can expose implementation details such as function names, code paths, internal state information, and error conditions that could be exploited by attackers performing reverse engineering.

Common logging APIs on iOS include `NSLog`, `print`, `dump`, `debugPrint`, and `os_log`. If debug-level logging remains enabled in production builds, or if logged error messages are overly detailed, they can reveal implementation details that increase the app's attack surface.

**Example Attack Scenario:**

Suppose an app logs detailed debug messages and error context in its release build.

1. An attacker captures device logs while exercising the app and intentionally triggering failures (e.g., invalid inputs, offline mode).
2. The attacker uses the revealed module names, function names, endpoints, and stack traces to map internal code paths.
3. The attacker uses this information to focus reverse engineering efforts and target high-value code paths more efficiently.

This test focuses on verbose logging that exposes implementation details. For tests specifically targeting sensitive data in logs, see @MASTG-TEST-0296 and @MASTG-TEST-0297.

## Steps

1. Use @MASTG-TECH-0058 to extract the relevant binaries from app package.
2. Use @MASTG-TECH-0066 to look for the relevant APIs in the app binaries.
3. Use @MASTG-TECH-0071 to look for logging strings.
4. Use @MASTG-TECH-0076 to analyze the relevant code paths and correlate strings and logging API calls where needed.

## Observation

The output should contain a list of logging function calls or other relevant logging references found in the binary.

## Evaluation

The test case fails if the app contains implemented logging paths that produce verbose debug or error messages in production builds and expose implementation details.

This determination should be based on analyzing how logging APIs are used, not merely on the presence of logging functions in the binary. Reverse engineering should be used to inspect the arguments, message strings, and surrounding code paths in order to establish what information is logged and under which conditions.

Static analysis is well suited to identifying logging behavior across the codebase, including paths that may be difficult to reach at runtime, but it can require substantial effort when symbols are stripped, strings are obfuscated, or log messages are constructed indirectly. Dynamic verification, see @MASTG-TEST-0359, can complement this test by confirming which messages are emitted during execution, but it may miss code paths that are not triggered in the tested scenarios.

Examples of failing cases include logs that reveal:

- internal function names or code paths
- detailed error information, stack-related details, or diagnostic context
- API endpoints, backend routes, or internal URLs
- internal state, configuration, or feature behavior
- library, framework, or component version details
- developer-oriented debugging messages not intended for production use

Sensitive data logged through these APIs (e.g., API keys, passwords, personal data) is covered separately in @MASTG-TEST-0296 and @MASTG-TEST-0297.
